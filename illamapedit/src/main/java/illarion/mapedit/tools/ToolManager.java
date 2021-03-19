/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2015 - Illarion e.V.
 *
 * Illarion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Illarion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package illarion.mapedit.tools;

import illarion.mapedit.data.Map;
import illarion.mapedit.data.MapTile;
import illarion.mapedit.events.*;
import illarion.mapedit.events.map.MapClickedEvent;
import illarion.mapedit.events.map.MapDragFinishedEvent;
import illarion.mapedit.events.map.MapDraggedEvent;
import illarion.mapedit.events.map.RepaintRequestEvent;
import illarion.mapedit.gui.GuiController;
import illarion.mapedit.history.HistoryManager;
import illarion.mapedit.resource.ItemImg;
import illarion.mapedit.resource.TileImg;
import illarion.mapedit.util.Disposable;
import illarion.mapedit.util.MouseButton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * @author Tim
 */
public final class ToolManager implements Disposable {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int TOOL_RADIUS = 10000;
    public static final int ICON_SIZE = 16;

    private final GuiController controller;

    @Nullable
    private AbstractTool actualTool;
    private TileImg selectedTile;
    private ItemImg selectedItem;
    private boolean doPaste;
    private int currentX = Integer.MIN_VALUE;
    private int currentY = Integer.MIN_VALUE;

    public ToolManager(GuiController controller) {
        this.controller = controller;
        AnnotationProcessor.process(this);
        setTool(new TileBrushTool());
    }

    @Nullable
    public MapTile getActiveTile() {
        Map currentMap = controller.getSelected();
        if (currentMap == null) {
            return null;
        }
        return currentMap.getActiveTile();
    }

    public void setTool(@Nullable AbstractTool tool) {
        if (tool != null) {
            tool.registerManager(this);
        }
        actualTool = tool;
        EventBus.publish(new RepaintRequestEvent());
    }

    TileImg getSelectedTile() {
        return selectedTile;
    }

    ItemImg getSelectedItem() {
        return selectedItem;
    }

    @Override
    public void dispose() {
        AnnotationProcessor.unprocess(this);
    }

    @EventSubscriber
    public void clickedAt(@NotNull MapClickedEvent e) {
        if (e.getButton() != MouseButton.LeftButton) {
            return;
        }
        if (isAnnotated(e)) {
            return;
        }
        if (doPaste) {
            EventBus.publish(new PasteEvent(e.getX(), e.getY()));
            doPaste = false;
        } else if ((actualTool != null) && isFillAction(e)) {
            actualTool.fillSelected(e.getMap());
            EventBus.publish(new RepaintRequestEvent());
            e.getMap().setActiveTile(e.getX(), e.getY());
            controller.setSaved(false);
        } else if ((actualTool != null) && !actualTool.isFillSelected()) {
            actualTool.clickedAt(e.getX(), e.getY(), e.getMap());
            EventBus.publish(new RepaintRequestEvent());
            e.getMap().setActiveTile(e.getX(), e.getY());
            controller.setSaved(false);
        }
    }

    private boolean isAnnotated(@NotNull MapClickedEvent e) {
        if ((actualTool == null) || !actualTool.isWarnAnnotated()) {
            return false;
        }
        if (isFillAction(e)) {
            return controller.getAnnotationChecker().isAnnotatedFill(e.getMap());
        }
        return controller.getAnnotationChecker().isAnnotated(e.getX(), e.getY(), e.getMap());
    }

    private boolean isFillAction(@NotNull MapClickedEvent e) {
        return (actualTool != null) && actualTool.isFillSelected() && e.getMap().isSelected(e.getX(), e.getY());
    }

    @EventSubscriber
    public void onMapDragged(@NotNull MapDraggedEvent e) {
        if ((actualTool != null) && (e.getButton() == MouseButton.LeftButton)) {
            if (actualTool.isFillAreaAction()) {
                e.getMap().setFillingArea(e.getX(), e.getY(), e.getStartX(), e.getStartY());
            } else if (currentX != e.getX() || currentY != e.getY()) {
                currentX = e.getX();
                currentY = e.getY();
                actualTool.clickedAt(e.getX(), e.getY(), e.getMap());
                EventBus.publish(new RepaintRequestEvent());
                e.getMap().setActiveTile(e.getX(), e.getY());
                controller.setSaved(false);
            }
        }
    }

    @EventSubscriber
    public void onMapDragFinished(@NotNull MapDragFinishedEvent e) {
        if ((actualTool != null) && !actualTool.isFillSelected()) {
            currentX = Integer.MIN_VALUE;
            currentY = Integer.MIN_VALUE;
            e.getMap().setFillDragging(false);
            actualTool.fillArea(e.getStartX(), e.getStartY(), e.getEndX(), e.getEndY(), e.getMap());
            EventBus.publish(new RepaintRequestEvent());
            e.getMap().setActiveTile(e.getEndX(), e.getEndY());
            controller.setSaved(false);
        }
    }

    @EventSubscriber
    public void onTileSelected(@NotNull TileSelectedEvent e) {
        selectedTile = e.getTileImg();
    }

    @EventSubscriber
    public void onItemSelected(@NotNull ItemSelectedEvent e) {
        selectedItem = e.getItemImg();
    }

    @EventSubscriber
    public void onSelectTool(@NotNull ToolSelectedEvent e) {
        setTool(e.getTool());
    }

    @NotNull
    public HistoryManager getHistory() {
        return controller.getHistoryManager();
    }

    @EventSubscriber
    public void onPasteClipboard(@NotNull ClipboardPasteEvent e) {
        doPaste = true;
    }
}
