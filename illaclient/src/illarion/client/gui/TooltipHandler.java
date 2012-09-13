/*
 * This file is part of the Illarion Client.
 *
 * Copyright © 2012 - Illarion e.V.
 *
 * The Illarion Client is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Illarion Client is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Illarion Client.  If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.client.gui;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.net.server.events.AbstractItemLookAtEvent;
import illarion.common.util.Rectangle;
import org.illarion.nifty.controls.tooltip.builder.ToolTipBuilder;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This tooltip handler takes care of showing and hiding the item tooltips on the screen.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class TooltipHandler implements ScreenController, UpdatableHandler {
    /**
     * The queue that contains the tasks for the tooltips that need to be executed upon the next update.
     */
    private final Queue<Runnable> toolTipTasks;

    /**
     * The Nifty that is the parent to this handler.
     */
    private Nifty parentNifty;

    /**
     * The screen that is the parent to this handler.
     */
    private Screen parentScreen;

    /**
     * The layer-element that contains all the tooltips.
     */
    private Element toolTipLayer;

    /**
     * The area the mouse as the remain inside to keep the tooltip active.
     */
    private Rectangle activeTooltipArea;

    /**
     * The task that will clean all opened tooltip.
     */
    private Runnable cleanToolTips = new Runnable() {
        @Override
        public void run() {
            for (final Element element : toolTipLayer.getElements()) {
                element.hide();
                activeTooltipArea = null;
            }
        }
    };

    /**
     * The default constructor.
     */
    public TooltipHandler() {
        toolTipTasks = new ConcurrentLinkedQueue<Runnable>();
    }

    @Override
    public void bind(final Nifty nifty, final Screen screen) {
        parentNifty = nifty;
        parentScreen = screen;

        toolTipLayer = screen.findElementByName("tooltipLayer");
    }

    @Override
    public void onStartScreen() {
        // nothing
    }

    @Override
    public void onEndScreen() {
        cleanToolTips.run();
    }

    @Override
    public void update(final GameContainer container, final int delta) {
        while (true) {
            final Runnable task = toolTipTasks.poll();
            if (task == null) {
                break;
            }

            task.run();
        }

        final Input input = container.getInput();
        if (activeTooltipArea != null) {
            if (!activeTooltipArea.isInside(input.getMouseX(), input.getMouseY())) {
                hideToolTip();
            }
        }
    }

    /**
     * Hide all current tooltips.
     */
    public void hideToolTip() {
        toolTipTasks.offer(cleanToolTips);
    }

    /**
     * Create a new tooltip.
     *
     * @param location the tooltip should be place around, the area of this rectangle won't be hidden by the tooltip.
     *                 Also the mouse is required to remain inside this area to keep the tooltip active
     * @param event    the event that contains the data of the tooltip
     */
    public void showToolTip(final Rectangle location, final AbstractItemLookAtEvent event) {
        toolTipTasks.offer(new Runnable() {
            @Override
            public void run() {
                showToolTipImpl(location, event);
                activeTooltipArea = location;
            }
        });
    }

    /**
     * Create a new tooltip. This is the internal implementation that is only called from the update loop.
     *
     * @param location the tooltip should be place around, the area of this rectangle won't be hidden by the tooltip
     * @param event    the event that contains the data of the tooltip
     */
    private void showToolTipImpl(final Rectangle location, final AbstractItemLookAtEvent event) {
        final ToolTipBuilder builder = new ToolTipBuilder();
        builder.title(event.getName());
        builder.titleColor(Color.WHITE);
        builder.producer(event.getName());
        builder.worth(event.getWorth().getTotalCopper());
        builder.quality(event.getQualityText());
        builder.durability(event.getDurabilityText());
        builder.amethystLevel(event.getAmethystLevel());
        builder.blackStoneLevel(event.getBlackStoneLevel());
        builder.blueStoneLevel(event.getBlueStoneLevel());
        builder.diamondLevel(event.getDiamondLevel());
        builder.emeraldLevel(event.getEmeraldLevel());
        builder.rubyLevel(event.getRubyLevel());
        builder.topazLevel(event.getTopazLevel());

        final Element toolTip = builder.build(parentNifty, parentScreen, toolTipLayer);
        toolTip.getParent().layoutElements();

        final int screenWidth = parentScreen.getRootElement().getWidth();
        final int screenHeight = parentScreen.getRootElement().getHeight();

        final int toolTipWidth = toolTip.getWidth();
        final int toolTipHeight = toolTip.getHeight();

        final boolean topSide = (location.getTop() - toolTipHeight) > 0;
        final boolean leftSide = (location.getRight() - toolTipWidth) < 0;

        if (topSide) {
            toolTip.setConstraintY(SizeValue.px(location.getTop() - toolTip.getHeight()));
        } else {
            toolTip.setConstraintY(SizeValue.px(location.getBottom()));
        }

        if (leftSide) {
            toolTip.setConstraintX(SizeValue.px(location.getLeft() - toolTip.getWidth()));
        } else {
            toolTip.setConstraintX(SizeValue.px(location.getRight()));
        }

        toolTip.getParent().layoutElements();
    }
}