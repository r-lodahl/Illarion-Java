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
package illarion.easyquest;

import illarion.easyquest.gui.Editor;
import illarion.easyquest.gui.MainFrame;
import illarion.easyquest.gui.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public final class EditorActions {

    private EditorActions() {
    }

    @Nullable
    public static Editor getEditor(@NotNull ActionEvent e) {
        if (e.getSource() instanceof Component) {
            Component component = (Component) e.getSource();

            while (component != null && !(component instanceof Editor)) {
                component = component.getParent();
            }

            return (Editor) component;
        }

        return null;
    }

    @SuppressWarnings("serial")
    public static class NewAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            MainFrame.getInstance().addNewQuest();
        }
    }

    @SuppressWarnings("serial")
    public static class OpenAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            Utils.selectAndOpenQuest();
        }
    }

    @SuppressWarnings("serial")
    public static class SaveAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            Utils.saveEasyQuest(MainFrame.getInstance().getCurrentQuestEditor());
        }
    }

    @SuppressWarnings("serial")
    public static class UndoAction extends AbstractAction {
        @Override
        public void actionPerformed(@NotNull ActionEvent e) {
            Editor editor = getEditor(e);

            if (editor != null) {
                editor.getUndoManager().undo();
            }
        }
    }

    @SuppressWarnings("serial")
    public static class RedoAction extends AbstractAction {
        @Override
        public void actionPerformed(@NotNull ActionEvent e) {
            Editor editor = getEditor(e);

            if (editor != null) {
                editor.getUndoManager().redo();
            }
        }
    }
}
