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
package illarion.common.bug;

import illarion.common.util.MessageSource;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This is the dialog implementation for Swing. It will display a dialog that
 * contains the error problem description and the error message and allows the
 * user to choose what to do.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class ReportDialogSwing implements ReportDialog {
    /**
     * Button listener helper class. This class is assigned to the buttons of
     * the dialog.
     *
     * @author Martin Karing &lt;nitram@illarion.org&gt;
     */
    private final class ButtonListener implements ActionListener {
        /**
         * The dialog that is closed upon calling this listener.
         */
        @NotNull
        private final JDialog closingDialog;

        /**
         * The result value that is set in case the button is pressed.
         */
        private final int resultValue;

        /**
         * Public constructor so the parent class is able to create a instance.
         * Also this sets the result value that is put in place in case the
         * button this listener is assigned to is clicked.
         *
         * @param dialog the dialog that is closed upon calling this listener
         * @param setResult the result value that is supposed to be set
         */
        public ButtonListener(@NotNull JDialog dialog, int setResult) {
            resultValue = setResult;
            closingDialog = dialog;
        }

        /**
         * The action performed when the button is pressed.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            setResult(resultValue);
            closingDialog.setVisible(false);
        }
    }

    /**
     * The newline string that is used in the dialog.
     */
    private static final String NL = "\n";

    /**
     * The data about the crash.
     */
    @Nullable
    private CrashData crashData;

    /**
     * The source of the messages displayed in the dialog.
     */
    @Nullable
    private MessageSource messages;

    /**
     * The result received from the displayed dialog.
     */
    private int result;

    /**
     * Get the result of the dialog.
     */
    @Override
    @Contract(pure = true)
    public int getResult() {
        return result;
    }

    /**
     * Set the crash data that is displayed in this dialog.
     */
    @Override
    public void setCrashData(@Nullable CrashData data) {
        crashData = data;
    }

    /**
     * Set the source of the messages displayed in this dialog.
     */
    @Override
    public void setMessageSource(MessageSource source) {
        messages = source;
    }

    /**
     * Create and show the dialog. This method blocks until the dialog is
     * closed.
     */
    @Override
    public void showDialog() {
        if ((messages == null) || (crashData == null)) {
            throw new IllegalStateException("The message source and the crash data needs to be set.");
        }
        JDialog dialog = new JDialog();

        dialog.setModalityType(ModalityType.APPLICATION_MODAL);
        dialog.setTitle(messages.getMessage("illarion.common.bug.Title"));
        dialog.setAlwaysOnTop(true);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        dialog.getContentPane().add(mainPanel);

        JTextArea introText = new JTextArea(messages.getMessage(crashData.getDescription()));
        introText.setMargin(new Insets(10, 10, 10, 10));
        introText.setEditable(false);
        introText.setCursor(null);
        introText.setOpaque(false);
        introText.setFocusable(false);
        mainPanel.add(introText, BorderLayout.NORTH);

        JTextArea detailsText = new JTextArea(messages.getMessage("illarion.common.bug.details.Intro") + NL + NL +
                                                            messages.getMessage(
                                                                    "illarion.common.bug.details.Application") + ' ' +
                                                            crashData.getApplicationIdentifier().getApplicationName() +
                                                            NL +
                                                            messages.getMessage("illarion.common.bug.details.Version") +
                                                            ' ' + crashData.getApplicationIdentifier()
                .getApplicationVersion() + NL + messages.getMessage("illarion.common.bug.details.OS") + ' ' +
                                                            CrashData.getOSName() + NL +
                                                            messages.getMessage("illarion.common.bug.details.Thread") +
                                                            ' ' + crashData.getThreadName() + NL +
                                                            messages.getMessage("illarion.common.bug.details.Stack") +
                                                            NL + crashData.getStackBacktrace());
        detailsText.setEditable(false);
        detailsText.setCursor(null);
        detailsText.setOpaque(false);
        detailsText.setFocusable(false);

        JScrollPane detailsScroll = new JScrollPane(detailsText);
        mainPanel.add(detailsScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JButton alwaysButton = new JButton(messages.getMessage("illarion.common.bug.buttons.always"));
        JButton onceButton = new JButton(messages.getMessage("illarion.common.bug.buttons.once"));
        JButton notButton = new JButton(messages.getMessage("illarion.common.bug.buttons.not"));
        JButton neverButton = new JButton(messages.getMessage("illarion.common.bug.buttons.never"));

        alwaysButton.addActionListener(new ButtonListener(dialog, SEND_ALWAYS));
        onceButton.addActionListener(new ButtonListener(dialog, SEND_ONCE));
        notButton.addActionListener(new ButtonListener(dialog, SEND_NOT));
        neverButton.addActionListener(new ButtonListener(dialog, SEND_NEVER));

        buttonPanel.add(alwaysButton);
        buttonPanel.add(onceButton);
        buttonPanel.add(notButton);
        buttonPanel.add(neverButton);

        dialog.setPreferredSize(new Dimension(550, 300));

        dialog.validate();
        dialog.pack();

        dialog.setLocationRelativeTo(null);

        setResult(SEND_NOT);

        dialog.setVisible(true);
        dialog.dispose();
    }

    /**
     * Set the result value. This is used instead of a synthetic accessor.
     *
     * @param newResult the new result value
     */
    void setResult(int newResult) {
        result = newResult;
    }
}
