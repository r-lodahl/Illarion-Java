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
package illarion.client.gui.controller;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import illarion.client.IllaClient;
import illarion.client.gui.*;
import illarion.client.gui.controller.game.*;
import illarion.client.world.World;
import org.illarion.engine.input.Input;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is the global accessor to the GUI of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class GameScreenController implements GameGui, ScreenController {
    /**
     * ALL child ScreenControllers, such as Skills, Inventory, etc.
     */
    @NotNull
    private final Collection<ScreenController> childControllers;
    /**
     * The child ScreenControllers that are updatable
     */
    @NotNull
    private final Collection<UpdatableHandler> childUpdateControllers;

    /**
     * These handlers are all of the GUIs that can be displayed during the game
     * Any new In-Game GUIs must be declared here
     */
    @NotNull
    private final BookHandler bookHandler;
    @NotNull
    private final DialogHandler dialogHandler;
    @NotNull
    private final SkillsHandler skillsHandler;
    @NotNull
    private final InformHandler informHandler;
    @NotNull
    private final GUIChatHandler chatHandler;
    @NotNull
    private final GUIInventoryHandler inventoryHandler;
    @NotNull
    private final ContainerHandler containerHandler;
    @NotNull
    private final GameMapHandler gameMapHandler;
    @NotNull
    private final QuestHandler questHandler;
    @NotNull
    private final DocumentationHandler documentationHandler;
    @NotNull
    private final GameMiniMapHandler gameMiniMapHandler;
    @NotNull
    private final CharStatusHandler charStatusHandler;
    @NotNull
    private final CloseGameHandler closeGameHandler;

    /**
     * Indicates that the screen has been setup by calling bind(Nifty, Screen)
     */
    private boolean ready;

    /**
     * Initializes all of the child handlers, adds them to appropriate collections
     *
     * Any new In-Game GUIs need to be added to this method
     *
     * @param input The Engine's input system
     */
    public GameScreenController(@NotNull Input input) {
        NumberSelectPopupHandler numberPopupHandler = new NumberSelectPopupHandler();
        TooltipHandler tooltipHandler = new TooltipHandler();

        childControllers = new ArrayList<>();
        childUpdateControllers = new ArrayList<>();

        chatHandler = new GUIChatHandler();
        bookHandler = new BookHandler();
        dialogHandler = new DialogHandler(input, numberPopupHandler, tooltipHandler);
        skillsHandler = new SkillsHandler();
        informHandler = new InformHandler();
        inventoryHandler = new GUIInventoryHandler(input, numberPopupHandler, tooltipHandler);
        containerHandler = new ContainerHandler(input, numberPopupHandler, tooltipHandler);
        gameMapHandler = new GameMapHandler(input, numberPopupHandler, tooltipHandler);
        gameMiniMapHandler = new GameMiniMapHandler();
        questHandler = new QuestHandler();
        documentationHandler = new DocumentationHandler();
        charStatusHandler = new CharStatusHandler();
        closeGameHandler = new CloseGameHandler();

        addHandler(numberPopupHandler);
        addHandler(tooltipHandler);
        addHandler(chatHandler);
        addHandler(bookHandler);
        addHandler(inventoryHandler);
        addHandler(dialogHandler);
        addHandler(containerHandler);
        addHandler(closeGameHandler);
        addHandler(new DisconnectHandler());
        addHandler(charStatusHandler);
        addHandler(skillsHandler);
        addHandler(questHandler);
        addHandler(documentationHandler);

        addHandler(gameMapHandler);
        addHandler(gameMiniMapHandler);

        addHandler(informHandler);
    }

    /**
     * Adds the handler to either the childControllers and childUpdatableHandlers if appropriate
     * @param handler   the ScreenController to be added
     */
    private void addHandler(ScreenController handler) {
        childControllers.add(handler);
        if (handler instanceof UpdatableHandler) {
            childUpdateControllers.add((UpdatableHandler) handler);
        }
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public BookGui getBookGui() {
        return bookHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public ChatGui getChatGui() {
        return chatHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public ContainerGui getContainerGui() {
        return containerHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public DialogGui getDialogGui() {
        return dialogHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public DialogCraftingGui getDialogCraftingGui() {
        return dialogHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public DialogMerchantGui getDialogMerchantGui() {
        return dialogHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public DialogInputGui getDialogInputGui() {
        return dialogHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public DialogMessageGui getDialogMessageGui() {
        return dialogHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public DialogSelectionGui getDialogSelectionGui() {
        return dialogHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public GameMapGui getGameMapGui() {
        return gameMapHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public InformGui getInformGui() {
        return informHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public InventoryGui getInventoryGui() {
        return inventoryHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public PlayerStatusGui getPlayerStatusGui() {
        return charStatusHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public QuestGui getQuestGui() {
        return questHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public DocumentationGui getDocumentationGui() {
        return documentationHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public ScreenController getScreenController() {
        return this;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public SkillGui getSkillGui() {
        return skillsHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public MiniMapGui getMiniMapGui() {
        return gameMiniMapHandler;
    }

    @NotNull
    @Override
    @Contract(pure = true)
    public CloseGameGui getCloseGameGui() {
        return closeGameHandler;
    }

    @Override
    @Contract(pure = true)
    public boolean isReady() {
        return ready;
    }

    /**
     * Calls onEndScreen() for all child ScreenControllers
     * Cleans up the world
     * Saves the current configuration state
     */
    @Override
    public void onEndScreen() {
        childControllers.forEach(ScreenController::onEndScreen);
        World.cleanEnvironment();
        IllaClient.getConfig().save();
    }

    /**
     * Starts up all child ScreenControllers
     */
    @Override
    public void onStartScreen() {
        childControllers.forEach(ScreenController::onStartScreen);
    }

    /**
     * This function is called once inside the game loop with the delta value of the current update loop. Inside this
     * functions changes to the actual representation of the GUI should be done.
     *
     * @param delta the time since the last update call
     */
    @Override
    public void onUpdateGame(int delta) {
        for (UpdatableHandler childController : childUpdateControllers) {
            childController.update(delta);
        }
    }

    /**
     * Calls bind() for all child ScreenControllers with the given arguments
     * Sets ready to {@code true} once all children are ready
     * @param nifty     The Nifty object for this instance of the game
     * @param screen    The Screen for this instance of the game
     */
    @Override
    public void bind(@NotNull Nifty nifty, @NotNull Screen screen) {
        for (ScreenController childController : childControllers) {
            childController.bind(nifty, screen);
        }
        ready = true;
    }
}
