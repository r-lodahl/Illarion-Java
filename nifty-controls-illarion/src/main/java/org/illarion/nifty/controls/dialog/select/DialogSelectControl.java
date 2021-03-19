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
package org.illarion.nifty.controls.dialog.select;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ButtonClickedEvent;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.controls.window.WindowControl;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.tools.SizeValue;
import org.bushe.swing.event.EventTopicSubscriber;
import org.illarion.nifty.controls.DialogSelect;
import org.illarion.nifty.controls.DialogSelectCancelEvent;
import org.illarion.nifty.controls.DialogSelectSelectEvent;
import org.illarion.nifty.controls.SelectListEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This is the control class of the select dialogs. Not meant to direct usage.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 * @deprecated Use {@link DialogSelect}
 */
@Deprecated
public final class DialogSelectControl extends WindowControl implements DialogSelect {
    /**
     * The instance of the Nifty-GUI that is parent to this control.
     */
    private Nifty niftyInstance;

    /**
     * The screen that displays this control.
     */
    private Screen currentScreen;

    /**
     * The ID of this dialog.
     */
    private int dialogId;

    /**
     * Helper variable to prevent double firing close events.
     */
    private boolean alreadyClosed;

    /**
     * The event handler that handles the events on the close button.
     */
    @NotNull
    private final EventTopicSubscriber<ButtonClickedEvent> closeButtonEventHandler;

    /**
     * The event handler that handles clicks on the select button.
     */
    @NotNull
    private final EventTopicSubscriber<ButtonClickedEvent> selectButtonEventHandler;

    public DialogSelectControl() {
        closeButtonEventHandler = (topic, data) -> {
            if (alreadyClosed) {
                return;
            }
            closeWindow();
        };

        selectButtonEventHandler = (topic, data) -> {
            if (alreadyClosed) {
                return;
            }
            selectItem(getSelectedIndex());
        };
    }

    @Override
    public void bind(
            @NotNull Nifty nifty,
            @NotNull Screen screen,
            @NotNull Element element,
            @NotNull Parameters parameter) {
        super.bind(nifty, screen, element, parameter);
        niftyInstance = nifty;
        currentScreen = screen;

        dialogId = Integer.parseInt(parameter.get("dialogId"));
        element.findNiftyControl("#message", Label.class).setText(parameter.getWithDefault("message", ""));
    }

    @Override
    public void onStartScreen() {
        super.onStartScreen();

        Element element = getElement();
        Element parent = element.getParent();

        int x = (parent.getWidth() - element.getWidth()) / 2;
        int y = (parent.getHeight() - element.getHeight()) / 2;

        element.setConstraintX(new SizeValue(x + "px"));
        element.setConstraintY(new SizeValue(y + "px"));

        parent.layoutElements();

        Element closeButton = getElement().findElementById("#cancelButton");
        niftyInstance.subscribe(currentScreen, closeButton.getId(), ButtonClickedEvent.class, closeButtonEventHandler);

        Element selectButton = getElement().findElementById("#selectButton");
        niftyInstance
                .subscribe(currentScreen, selectButton.getId(), ButtonClickedEvent.class, selectButtonEventHandler);
    }

    @Override
    public int getEntryCount() {
        return getList().itemCount();
    }

    @Override
    public SelectListEntry getSelectedItem() {
        List<SelectListEntry> selectedItems = getList().getSelection();
        if (selectedItems.isEmpty()) {
            return null;
        }
        return selectedItems.get(0);
    }

    @Override
    public int getSelectedIndex() {
        List<Integer> selectedIndices = getList().getSelectedIndices();
        if (selectedIndices.isEmpty()) {
            return -1;
        }
        return selectedIndices.get(0);
    }

    @Override
    public void addItem(@NotNull SelectListEntry entry) {
        getList().addItem(entry);
    }

    public void selectItem(int index) {
        if (index == -1) {
            return;
        }
        ListBox<SelectListEntry> list = getList();
        niftyInstance.publishEvent(getId(), new DialogSelectSelectEvent(dialogId, list.getItems().get(index), index));
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private ListBox<SelectListEntry> getList() {
        return getElement().findNiftyControl("#list", ListBox.class);
    }

    @Override
    public void closeWindow() {
        alreadyClosed = true;
        super.closeWindow();
        niftyInstance.publishEvent(getId(), new DialogSelectCancelEvent(dialogId));
    }
}
