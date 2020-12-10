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
package illarion.client.gui.controller.game;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;
import de.lessvoid.nifty.builder.ElementBuilder.Align;
import de.lessvoid.nifty.controls.*;
import de.lessvoid.nifty.controls.label.builder.LabelBuilder;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import illarion.client.IllaClient;
import illarion.client.graphics.FontLoader;
import illarion.client.gui.QuestGui;
import illarion.client.world.World;
import illarion.common.types.ServerCoordinate;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class takes care for managing the quest log.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class QuestHandler implements QuestGui, ScreenController {
    /**
     * This is a single quest that is shown in the quest log.
     */
    private static final class QuestEntry implements Comparable<QuestEntry> {
        /**
         * The ID of the quest.
         */
        private final int questId;

        /**
         * The displayed name of the quest.
         */
        @Nonnull
        private final String name;

        /**
         * The description of the current quest state.
         */
        @Nonnull
        private final String description;

        /**
         * The flag if this quest is now finished or not.
         */
        @Nonnull
        private final boolean finished;

        /**
         * The list of valid target locations for this quest.
         */
        @Nonnull
        private final List<ServerCoordinate> targetLocations;

        /**
         * The constructor of the quest.
         *
         * @param questId the ID of the quest
         * @param name the name of the quest
         * @param description the description of the quest state
         * @param finished {@code true} in case the quest is finished
         * @param locations the valid target locations
         */
        QuestEntry(int questId, @Nonnull String name, @Nonnull String description, boolean finished,
                   @Nonnull @Flow(targetIsContainer = true, sourceIsContainer = true) List<ServerCoordinate> locations) {
            this.questId = questId;
            this.name = name;
            this.description = description;
            this.finished = finished;
            //noinspection AssignmentToCollectionOrArrayFieldFromParameter
            targetLocations = locations;
        }

        @Override
        public int compareTo(@Nonnull QuestEntry o) {
            if (o.finished && !finished) {
                return -1;
            }
            if (!o.finished && finished) {
                return 1;
            }

            return name.compareTo(o.name);
        }

        @Override
        @Contract(value = "null -> false", pure = true)
        public boolean equals(Object obj) {
            return super.equals(obj) || ((obj instanceof QuestEntry) && (questId == ((QuestEntry) obj).questId));
        }

        @Override
        @Contract(pure = true)
        public int hashCode() {
            return questId;
        }

        @Nonnull
        @Override
        @Contract(pure = true)
        public String toString() {
            return name;
        }

        /**
         * Get the description of the quest.
         *
         * @return the quest description
         */
        @Nonnull
        @Contract(pure = true)
        public String getDescription() {
            return description;
        }

        /**
         * Get the name of the quest.
         *
         * @return the name of the quest
         */
        @Nonnull
        @Contract(pure = true)
        public String getName() {
            return name;
        }

        /**
         * Get the ID of the quest.
         *
         * @return the ID of the quest
         */
        @Contract(pure = true)
        public int getQuestId() {
            return questId;
        }

        /**
         * Check if the quest is finished.
         *
         * @return {@code true} in case the quest is finished
         */
        @Contract(pure = true)
        public boolean isFinished() {
            return finished;
        }

        /**
         * Get the amount of target locations assigned to this quest entry.
         *
         * @return the target locations
         */
        @Contract(pure = true)
        public int getTargetLocationCount() {
            return targetLocations.size();
        }

        /**
         * Get the target location that is assigned to the index.
         *
         * @param index the index of the target location
         * @return the assigned target location
         * @throws ArrayIndexOutOfBoundsException in case index is {@code < 0} or greater then or equal to the count
         */
        @Nonnull
        @Contract(pure = true)
        public ServerCoordinate getTargetLocation(int index) {
            return targetLocations.get(index);
        }
    }

    /**
     * The logging instance of this class.
     */
    @Nonnull
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestHandler.class);

    /**
     * The window that shows the quest log.
     */
    @Nullable
    private Window questWindow;

    /**
     * This is the list of quests that are currently not shown in the GUI.
     */
    @Nonnull
    private final List<QuestEntry> hiddenList;

    /**
     * In case this is {@code true} even the finished quests are shown.
     */
    private boolean showFinishedQuests;

    /**
     * The reference to the Nifty instance this handler is bound to.
     */
    @Nullable
    private Nifty nifty;

    /**
     * The reference to the screen this handler is bound to.
     */
    @Nullable
    private Screen screen;

    /**
     * This value is turned true once the login sequence is done.
     */
    private boolean loginDone;

    /**
     * Default constructor.
     */
    public QuestHandler() {
        hiddenList = new ArrayList<>();
    }

    @Nullable
    private Window getQuestWindow() {
        if (screen == null) {
            LOGGER.warn("Can't fetch the quest window as long as the quest handler is not bound to a screen.");
            return null;
        }
        if (questWindow == null) {
            questWindow = screen.findNiftyControl("questLog", Window.class);
        }
        if (questWindow == null) {
            LOGGER.error("Fetching the quest window failed. Seems its not yet created.");
        }
        return questWindow;
    }

    @Nullable
    private Element getQuestWindowElement() {
        Window questWindow = getQuestWindow();
        if (questWindow == null) {
            return null;
        }
        return questWindow.getElement();
    }

    @Override
    public boolean isQuestLogVisible() {
        Element questWindow = getQuestWindowElement();
        return (questWindow != null) && questWindow.isVisible();
    }

    @Override
    public void hideQuestLog() {
        Window questWindow = getQuestWindow();
        if (questWindow != null) {
            questWindow.closeWindow();
        }
    }

    @Override
    public void showQuestLog() {
        Element questWindowElement = getQuestWindowElement();
        if (questWindowElement == null) {
            LOGGER.error("Showing the quest log failed. The required GUI element can't be located.");
        } else {
            questWindowElement.show(() -> {
                Window questWindow1 = getQuestWindow();
                if (questWindow1 != null) {
                    questWindow1.moveToFront();
                }
            });
        }
    }

    /**
     * The event subscriber for click events on the quest button.
     *
     * @param topic the event topic
     * @param data the event data
     */
    @NiftyEventSubscriber(id = "openQuestBtn")
    public void onQuestLogButtonClicked(String topic, ButtonClickedEvent data) {
        if (isQuestLogVisible()) {
            hideQuestLog();
        } else {
            showQuestLog();
        }
    }

    /**
     * Event Handler for change events of the selection int he quest list box.
     *
     * @param topic the event topic
     * @param event the event data
     */
    @NiftyEventSubscriber(id = "questLog#questList")
    public void onSelectedQuestChanged(
            @Nonnull String topic, @Nonnull ListBoxSelectionChangedEvent<QuestEntry> event) {
        Element descriptionArea = getDescriptionArea();
        if (descriptionArea != null) {
            descriptionArea.hide(this::updateDisplayedQuest);
        }
    }

    @Override
    public void updateAllQuests() {
        World.getUpdateTaskManager().addTask((delta) -> updateAllQuestsInternal());
    }

    private void updateAllQuestsInternal() {
        ListBox<QuestEntry> questList = getQuestList();
        if (questList == null) {
            return;
        }
        List<QuestEntry> selectedEntries = questList.getItems();
        selectedEntries.forEach(QuestHandler::updateQuest);
    }

    private static void updateQuest(@Nonnull QuestEntry quest) {
        Collection<ServerCoordinate> locationList = new ArrayList<>(quest.getTargetLocationCount());
        for (int i = 0; i < quest.getTargetLocationCount(); i++) {
            ServerCoordinate target = quest.getTargetLocation(i);
            locationList.add(target);
        }
        World.getMap().removeQuestMarkers(locationList);
        World.getMap().applyQuestTargetLocations(locationList);
    }

    /**
     * Update the quest that is currently displayed in the dialog.
     */
    private void updateDisplayedQuest() {
        if ((nifty == null) || (screen == null)) {
            LOGGER.error("Can't update the quest display as long as the handler is not bound.");
            return;
        }
        Element descriptionArea = getDescriptionArea();
        if (descriptionArea == null) {
            LOGGER.error("Can't update displayed quest. Description area not found.");
            return;
        }
        descriptionArea.getChildren().forEach(Element::markForRemoval);

        QuestEntry selectedEntry = getSelectedQuest();
        if (selectedEntry == null) {
            return;
        }

        LabelBuilder titleLabel = new LabelBuilder();
        titleLabel.label(selectedEntry.getName());
        titleLabel.font(FontLoader.MENU_FONT);
        titleLabel.marginLeft("5px");
        titleLabel.marginRight("5px");
        titleLabel.marginBottom("10px");
        titleLabel.width((descriptionArea.getWidth() - 10) + "px");
        titleLabel.wrap(true);
        titleLabel.build(nifty, screen, descriptionArea);

        if (!selectedEntry.getDescription().isEmpty()) {
            LabelBuilder descriptionLabel = new LabelBuilder();
            descriptionLabel.label(selectedEntry.getDescription());
            descriptionLabel.font(FontLoader.TEXT_FONT);
            descriptionLabel.marginLeft("5px");
            descriptionLabel.marginRight("5px");
            descriptionLabel.width((descriptionArea.getWidth() - 10) + "px");
            descriptionLabel.textHAlign(Align.Left);
            descriptionLabel.wrap(true);
            descriptionLabel.build(nifty, screen, descriptionArea);
        }

        if (selectedEntry.isFinished()) {
            LabelBuilder finishedLabel = new LabelBuilder();
            finishedLabel.label("${gamescreen-bundle.questFinished}");
            finishedLabel.font(FontLoader.TEXT_FONT);
            finishedLabel.marginLeft("5px");
            finishedLabel.marginRight("5px");
            finishedLabel.marginTop("15px");
            finishedLabel.width((descriptionArea.getWidth() - 10) + "px");
            finishedLabel.textHAlign(Align.Center);
            finishedLabel.wrap(true);
            finishedLabel.build(nifty, screen, descriptionArea);
        }

        updateQuest(selectedEntry);

        descriptionArea.show();
    }

    /**
     * Get the GUI area that contains the description.
     *
     * @return the element of the description area
     */
    @Nullable
    private Element getDescriptionArea() {
        Element questWindow = getQuestWindowElement();
        if (questWindow == null) {
            return null;
        }
        return questWindow.findElementById("#questDescription");
    }

    /**
     * Fetch the quest that is currently selected.
     *
     * @return the currently selected quest or {@code null} in case no quest is selected
     */
    @Nullable
    private QuestEntry getSelectedQuest() {
        ListBox<QuestEntry> questList = getQuestList();
        if (questList == null) {
            return null;
        }
        List<QuestEntry> selectedEntries = questList.getSelection();
        if (selectedEntries.isEmpty()) {
            return null;
        }

        return selectedEntries.get(0);
    }

    /**
     * Event Handler for change events of the "Show finished quests" checkbox.
     *
     * @param topic the event topic
     * @param event the event data
     */
    @NiftyEventSubscriber(id = "questLog#showFinishedCheckbox")
    public void onShowFinishedChange(@Nonnull String topic, @Nonnull CheckBoxStateChangedEvent event) {
        IllaClient.getConfig().set("questShowFinished", event.isChecked());
        showFinishedQuests = event.isChecked();

        if (showFinishedQuests) {
            hiddenList.forEach(this::insertToGuiList);
            hiddenList.clear();
        } else {
            ListBox<QuestEntry> questList = getQuestList();
            if (questList != null) {
                questList.getItems().stream().filter(visibleEntry -> visibleEntry.isFinished() && !hiddenList.contains(visibleEntry)).forEach(hiddenList::add);
            }
            getQuestList().removeAllItems(hiddenList);
        }
    }

    /**
     * This function is used to insert a quest into the GUI list. This takes  are to apply the required order to the
     * quest.
     *
     * @param entry the entry to add
     */
    private void insertToGuiList(@Nonnull QuestEntry entry) {
        ListBox<QuestEntry> guiList = getQuestList();
        if (guiList == null) {
            LOGGER.error("Updating the GUI list failed. GUI element not located.");
            return;
        }
        List<QuestEntry> questEntries = guiList.getItems();
        int currentStart = 0;
        int currentEnd = questEntries.size() - 1;

        while (currentStart <= currentEnd) {
            int middle = currentStart + ((currentEnd - currentStart) >> 1);
            QuestEntry foundItem = questEntries.get(middle);
            int compareResult = foundItem.compareTo(entry);

            if (compareResult < 0) {
                currentStart = middle + 1;
            } else if (compareResult > 0) {
                currentEnd = middle - 1;
            } else {
                guiList.insertItem(entry, middle);
                return;
            }
        }
        guiList.insertItem(entry, currentStart);
        updateQuest(entry);
    }

    /**
     * Fetch the reference to the quest list.
     *
     * @return the quest list
     */
    @Nullable
    private ListBox<QuestEntry> getQuestList() {
        Element questWindow = getQuestWindowElement();
        if (questWindow == null) {
            return null;
        }
        //noinspection unchecked
        return (ListBox<QuestEntry>) questWindow.findNiftyControl("#questList", ListBox.class);
    }

    @Override
    public void bind(@Nonnull Nifty nifty, @Nonnull Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onEndScreen() {
        if (nifty != null) {
            nifty.unsubscribeAnnotations(this);
        }

        Element questWindow = getQuestWindowElement();
        if (questWindow != null) {
            IllaClient.getConfig().set("questWindowPosX", questWindow.getX() + "px");
            IllaClient.getConfig().set("questWindowPosY", questWindow.getY() + "px");
        }
        hideQuestLog();

        ListBox<QuestEntry> questList = getQuestList();
        if (questList != null) {
            questList.clear();
        }
    }

    @Override
    public void onStartScreen() {
        if ((nifty == null) || (screen == null)) {
            LOGGER.error("Quest handler is not bound. Can't properly launch.");
        }
        nifty.subscribeAnnotations(this);

        Element questWindowElement = getQuestWindowElement();
        if (questWindowElement != null) {
            questWindowElement.setConstraintX(new SizeValue(IllaClient.getConfig().getString("questWindowPosX")));
            questWindowElement.setConstraintY(new SizeValue(IllaClient.getConfig().getString("questWindowPosY")));

            CheckBox showFinished = questWindowElement.findNiftyControl("#showFinishedCheckbox", CheckBox.class);
            if (showFinished != null) {
                showFinished.setChecked(IllaClient.getConfig().getBoolean("questShowFinished"));
                showFinishedQuests = showFinished.isChecked();
            }
        }
    }

    @Override
    public void removeQuest(int questId) {
        World.getUpdateTaskManager().addTask((delta) -> removeQuestInternal(questId));
    }

    /**
     * Remove a quest from the quest list.
     *
     * @param questId the ID of the quest
     */
    private void removeQuestInternal(int questId) {
        for (QuestEntry entry : hiddenList) {
            if (entry.getQuestId() == questId) {
                hiddenList.remove(entry);
                return;
            }
        }
        ListBox<QuestEntry> questList = getQuestList();
        if (questList != null) {
            for (QuestEntry entry : questList.getItems()) {
                if (entry.getQuestId() == questId) {
                    questList.removeItem(entry);
                    return;
                }
            }
        }
    }

    @Override
    public void setDisplayedQuest(int questId) {
        ListBox<QuestEntry> guiList = getQuestList();
        if (guiList != null) {
            guiList.getItems().stream().filter(guiListEntry -> guiListEntry.getQuestId() == questId).forEach(guiList::selectItem);
        }
    }

    @Override
    public void setQuest(
            int questId,
            @Nonnull String name,
            @Nonnull String description,
            boolean finished,
            @Nonnull List<ServerCoordinate> locations) {
        World.getUpdateTaskManager().addTask((delta) -> setQuestInternal(questId, name, description, finished, locations));
    }

    @Override
    public void toggleQuestLog() {
        if (isQuestLogVisible()) {
            hideQuestLog();
        } else {
            showQuestLog();
        }
    }

    /**
     * The internal method to set the quest. This needs to be called during the update call before Nifty itself is
     * updated.
     *
     * @param questId the ID of the quest
     * @param name the name of the quest
     * @param description the current description of the quest
     * @param finished {@code true} if the quest is finished
     * @param locations the valid target locations
     */
    private void setQuestInternal(
            int questId,
            @Nonnull String name,
            @Nonnull String description,
            boolean finished,
            @Nonnull List<ServerCoordinate> locations) {
        QuestEntry oldEntry = findQuest(questId);
        if (finished && (oldEntry != null)) {
            Collection<ServerCoordinate> locationList = new ArrayList<>(oldEntry.getTargetLocationCount());
            for (int i = 0; i < oldEntry.getTargetLocationCount(); i++) {
                ServerCoordinate target = oldEntry.getTargetLocation(i);
                locationList.add(target);
            }
            World.getMap().removeQuestMarkers(locationList);
        }
        if (oldEntry != null) {
            ListBox<QuestEntry> questList = getQuestList();
            if (questList != null) {
                questList.removeItem(oldEntry);
            }
        }

        QuestEntry newEntry = new QuestEntry(questId, name, description, finished, locations);
        if (!finished || showFinishedQuests) {
            insertToGuiList(newEntry);
            pulseQuestButton();
        } else {
            hiddenList.add(newEntry);
        }

        QuestEntry selectedEntry = getSelectedQuest();
        if (selectedEntry == null) {
            return;
        }
        if (selectedEntry.getQuestId() == questId) {
            updateDisplayedQuest();
        }
    }

    /**
     * Find a existing instance of the quest object with the ID supplied. This function searches both the GUI and the
     * hidden list.
     *
     * @param questId the ID of the quest to search
     * @return the quest instance or {@code null} in case its not found
     */
    @Nullable
    private QuestEntry findQuest(int questId) {
        for (QuestEntry hiddenListEntry : hiddenList) {
            if (hiddenListEntry.getQuestId() == questId) {
                return hiddenListEntry;
            }
        }

        ListBox<QuestEntry> questList = getQuestList();
        if (questList != null) {
            for (QuestEntry guiListEntry : questList.getItems()) {
                if (guiListEntry.getQuestId() == questId) {
                    return guiListEntry;
                }
            }
        }

        return null;
    }

    /**
     * Show the pulsing animation of the quest button.
     */
    private void pulseQuestButton() {
        if (loginDone && (screen != null)) {
            @Nullable Element questBtn = screen.findElementById("openQuestBtn");
            if (questBtn != null) {
                questBtn.startEffect(EffectEventId.onCustom, null, "pulse");
            }
        }
    }
}
