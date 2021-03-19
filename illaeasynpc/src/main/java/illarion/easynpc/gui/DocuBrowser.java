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
package illarion.easynpc.gui;

import illarion.easynpc.Lang;
import illarion.easynpc.docu.DocuEntry;
import illarion.easynpc.docu.DocuRoot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * This dialog is the help browser used to display the embedded documentation of
 * the editor.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DocuBrowser extends JDialog {
        private final class DocuTreeNode implements TreeNode {
        @Nullable
        private final List<DocuTreeNode> children;
        @NotNull
        private final DocuEntry nodeEntry;

        @Nullable
        private final DocuTreeNode parentNode;
        @Nullable
        private final String title;

        public DocuTreeNode(@NotNull DocuEntry entry) {
            this(entry, null);
        }

        public DocuTreeNode(@NotNull DocuEntry entry, @Nullable DocuTreeNode parent) {
            nodeEntry = entry;
            parentNode = parent;
            title = entry.getTitle();

            if (entry.getChildCount() == 0) {
                children = null;
            } else {
                List<DocuTreeNode> childList = new ArrayList<>();

                for (int i = 0; i < entry.getChildCount(); i++) {
                    childList.add(new DocuTreeNode(entry.getChild(i), this));
                }
                children = Collections.unmodifiableList(childList);
            }
        }

        @NotNull
        @Override
        @Contract(pure = true)
        public Enumeration<DocuTreeNode> children() {
            if (children == null) {
                return Collections.emptyEnumeration();
            }
            return Collections.enumeration(children);
        }

        /**
         * Update the details display for this node.
         */
        public void displayNode() {
            updateDetails(nodeEntry);
        }

        @Override
        @Contract(value = "-> true", pure = true)
        public boolean getAllowsChildren() {
            return true;
        }

        @Nullable
        @Override
        @Contract(pure = true)
        public TreeNode getChildAt(int childIndex) {
            if (children == null) {
                return null;
            }
            if ((childIndex < 0) || (childIndex >= children.size())) {
                return null;
            }
            return children.get(childIndex);
        }

        @Override
        public int getChildCount() {
            if (children == null) {
                return 0;
            }
            return children.size();
        }

        @Override
        @Contract(pure = true)
        public int getIndex(@NotNull TreeNode node) {
            if (children == null) {
                return -1;
            }

            if (node instanceof DocuTreeNode) {
                return getIndex((DocuTreeNode) node);
            }
            return -1;
        }

        @Contract(pure = true)
        public int getIndex(@NotNull DocuTreeNode node) {
            if (children == null) {
                return -1;
            }

            return children.indexOf(node);
        }

        @Override
        @Nullable
        @Contract(pure = true)
        public TreeNode getParent() {
            return parentNode;
        }

        @Override
        @Contract(pure = true)
        public boolean isLeaf() {
            return children == null;
        }

        @NotNull
        @Override
        @Contract(pure = true)
        public String toString() {
            return (title == null) ? "<null>" : title;
        }
    }

    /**
     * The serialization UID of the dialog.
     */
    private static final long serialVersionUID = 1L;

    @NotNull
    private final JTextArea descriptionContent;

    @NotNull
    private final JLabel descriptionTitle;
    @NotNull
    private final JTextArea exampleContent;
    @NotNull
    private final JLabel exampleTitle;

    @NotNull
    private final JTextArea syntaxContent;
    @NotNull
    private final JLabel syntaxTitle;

    @NotNull
    private final JLabel titleLabel;

    /**
     * The default constructor creating this documentation display.
     */
    public DocuBrowser(@NotNull Frame owner) {
        super(owner, Lang.getMsg(DocuBrowser.class, "title"), false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            setIconImage(ImageIO.read(cl.getResourceAsStream("easynpc16.png")));
        } catch (@NotNull IOException ignored) {
        }

        JTree contentTree = new JTree(new DocuTreeNode(DocuRoot.getInstance()));
        JScrollPane contentScroll = new JScrollPane(contentTree);
        contentScroll.setMinimumSize(new Dimension(350, 400));
        contentScroll.setPreferredSize(contentScroll.getMinimumSize());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JScrollPane detailsScroll = new JScrollPane(detailsPanel);
        detailsScroll.setMinimumSize(new Dimension(500, 400));
        detailsScroll.setPreferredSize(detailsScroll.getMinimumSize());

        splitPane.add(contentScroll, JSplitPane.LEFT);
        splitPane.add(detailsScroll, JSplitPane.RIGHT);

        contentTree.addTreeSelectionListener(e -> ((DocuTreeNode) e.getPath().getLastPathComponent()).displayNode());

        getContentPane().add(splitPane);

        titleLabel = new JLabel();
        descriptionTitle = new JLabel(Lang.getMsg(getClass(), "description"));
        descriptionContent = new JTextArea();
        descriptionContent.setFont(titleLabel.getFont());

        syntaxTitle = new JLabel(Lang.getMsg(getClass(), "syntax"));
        syntaxContent = new JTextArea();
        syntaxContent.setFont(titleLabel.getFont());

        exampleTitle = new JLabel(Lang.getMsg(getClass(), "example"));
        exampleContent = new JTextArea();
        exampleContent.setFont(titleLabel.getFont());

        Font headlineFont = titleLabel.getFont().deriveFont(Font.BOLD, 20.f);
        Font subheadlineFont = titleLabel.getFont().deriveFont(Font.BOLD, 18.f);
        Font textsubheadlineFont = titleLabel.getFont();

        titleLabel.setFont(headlineFont);
        descriptionTitle.setFont(subheadlineFont);
        descriptionTitle.setVisible(false);
        descriptionContent.setEditable(false);
        descriptionContent.setFont(textsubheadlineFont);
        descriptionContent.setVisible(false);
        descriptionContent.setLineWrap(true);
        descriptionContent.setWrapStyleWord(true);
        descriptionContent.setBackground(titleLabel.getBackground());
        descriptionContent.setForeground(titleLabel.getForeground());

        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(descriptionTitle, BorderLayout.NORTH);
        descriptionPanel.add(descriptionContent, BorderLayout.CENTER);
        descriptionPanel.add(Box.createRigidArea(new Dimension(20, 1)), BorderLayout.WEST);

        syntaxTitle.setFont(subheadlineFont);
        syntaxTitle.setVisible(false);
        syntaxContent.setEditable(false);
        syntaxContent.setFont(textsubheadlineFont);
        syntaxContent.setVisible(false);
        syntaxContent.setLineWrap(true);
        syntaxContent.setWrapStyleWord(true);
        syntaxContent.setBackground(titleLabel.getBackground());
        syntaxContent.setForeground(titleLabel.getForeground());

        JPanel syntaxPanel = new JPanel(new BorderLayout());
        syntaxPanel.add(syntaxTitle, BorderLayout.NORTH);
        syntaxPanel.add(syntaxContent, BorderLayout.CENTER);
        syntaxPanel.add(Box.createRigidArea(new Dimension(20, 1)), BorderLayout.WEST);

        exampleTitle.setFont(subheadlineFont);
        exampleTitle.setVisible(false);
        exampleContent.setEditable(false);
        exampleContent.setFont(textsubheadlineFont);
        exampleContent.setVisible(false);
        exampleContent.setLineWrap(true);
        exampleContent.setWrapStyleWord(true);
        exampleContent.setBackground(titleLabel.getBackground());
        exampleContent.setForeground(titleLabel.getForeground());

        JPanel examplePanel = new JPanel(new BorderLayout());
        examplePanel.add(exampleTitle, BorderLayout.NORTH);
        examplePanel.add(exampleContent, BorderLayout.CENTER);
        examplePanel.add(Box.createRigidArea(new Dimension(20, 1)), BorderLayout.WEST);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        titlePanel.add(titleLabel);
        detailsPanel.add(titlePanel);
        detailsPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        detailsPanel.add(descriptionPanel);
        detailsPanel.add(syntaxPanel);
        detailsPanel.add(examplePanel);

        validate();
        pack();

        Dimension parentDim = getOwner().getSize();
        Point parentPos = getOwner().getLocation();

        setLocation(((parentDim.width - getWidth()) / 2) + parentPos.x,
                    ((parentDim.height - getHeight()) / 2) + parentPos.y);
    }

    /**
     * Update the details view of the browser. This displays all the details
     * needed for each entry.
     *
     * @param entry the entry the details shall be displayed from
     */
    void updateDetails(@Nullable DocuEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry must not be NULL");
        }

        invalidate();

        String titleText = entry.getTitle();
        titleLabel.setText(titleText);

        String descriptionText = entry.getDescription();
        if (descriptionText != null) {
            descriptionTitle.setVisible(true);
            descriptionContent.setText(descriptionText);
            descriptionContent.setVisible(true);
        } else {
            descriptionTitle.setVisible(false);
            descriptionContent.setVisible(false);
        }

        String syntaxText = entry.getSyntax();
        if (syntaxText != null) {
            syntaxTitle.setVisible(true);
            syntaxContent.setText(syntaxText);
            syntaxContent.setVisible(true);
        } else {
            syntaxTitle.setVisible(false);
            syntaxContent.setVisible(false);
        }

        String exampleText = entry.getExample();
        if (exampleText != null) {
            exampleTitle.setVisible(true);
            exampleContent.setText(exampleText);
            exampleContent.setVisible(true);
        } else {
            exampleTitle.setVisible(false);
            exampleContent.setVisible(false);
        }

        validate();
    }
}
