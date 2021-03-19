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
package illarion.mapedit.gui.util;

import illarion.mapedit.data.MapIO;
import illarion.mapedit.events.menu.MapOpenEvent;
import illarion.mapedit.resource.loaders.ImageLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bushe.swing.event.EventBus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pushingpixels.flamingo.api.common.icon.ResizableIcon;

import javax.swing.*;
import javax.swing.SwingWorker.StateValue;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * @author Fredrik K
 */
public class FileTree extends JTree {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Color SELECTED_COLOR = new Color(115, 164, 209);

    private class FileTreeMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(@NotNull MouseEvent e) {
            int selRow = getClosestRowForLocation(e.getX(), e.getY());
            if (selRow == -1) {
                return;
            }
            Rectangle bounds = getRowBounds(selRow);
            boolean outside = (e.getX() < bounds.getX()) || (e.getX() > (bounds.getX() + bounds.getWidth()));

            boolean onRow = (e.getY() >= bounds.getY()) && (e.getY() < (bounds.getY() + bounds.getHeight()));
            if (onRow) {
                if (outside) {
                    setSelectionRow(selRow);
                    if (e.getClickCount() == 2) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                        if (node == null) {
                            return;
                        }
                        if (isCollapsed(selRow)) {
                            expandRow(selRow);
                        } else {
                            collapseRow(selRow);
                        }
                    }
                }
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
                    if (node == null) {
                        return;
                    }
                    if (node.isLeaf()) {
                        EventBus.publish(node.getUserObject());
                    }
                }
            }
        }
    }

    public static class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        private static final int ICON_SIZE = 16;

        public MyTreeCellRenderer() {
            setBackgroundNonSelectionColor(null);
            ResizableIcon iconOpen = ImageLoader.getResizableIcon("fileopen");
            iconOpen.setDimension(new Dimension(ICON_SIZE, ICON_SIZE));
            ResizableIcon iconMap = ImageLoader.getResizableIcon("mapedit64");
            iconMap.setDimension(new Dimension(ICON_SIZE, ICON_SIZE));
            setLeafIcon(iconMap);
            setClosedIcon(iconOpen);
            setOpenIcon(iconOpen);
            setBorderSelectionColor(null);
            setBackgroundSelectionColor(null);
        }

        @Override
        @Nullable
        public Color getBackground() {
            return null;
        }
    }

    public FileTree() {
        setCellRenderer(new MyTreeCellRenderer());
        setRootVisible(false);
        setShowsRootHandles(true);
        setEditable(false);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setModel(null);

        addMouseListener(new FileTreeMouseAdapter());
    }

    public static boolean isMapFile(@NotNull File node) {
        return node.isDirectory() || node.getName().endsWith(MapIO.EXT_TILE);
    }

    @Nullable
    private static MutableTreeNode scan(@NotNull Path node) {
        if (Files.isDirectory(node)) {
            return getDirectoryTreeNode(node);
        }
        String fileName = node.getFileName().toString();
        MapOpenEvent leaf = new MapOpenEvent(node.getParent(),
                                                   fileName.substring(0, fileName.length() - MapIO.EXT_TILE.length()));
        return new DefaultMutableTreeNode(leaf);
    }

    private static final Filter<Path> MAP_FILE_FILTER = entry -> {
        if (Files.isDirectory(entry)) {
            return true;
        }
        return entry.toString().endsWith(MapIO.EXT_TILE);
    };

    @Nullable
    private static MutableTreeNode getDirectoryTreeNode(@NotNull Path node) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(node, MAP_FILE_FILTER)) {
            String dirName = node.getFileName().toString();
            DefaultMutableTreeNode ret = new DefaultMutableTreeNode(dirName);

            for (Path child : stream) {
                MutableTreeNode childNode = scan(child);
                if (childNode != null) {
                    ret.add(childNode);
                }
            }
            if (ret.getChildCount() == 0) {
                return null;
            }
            return ret;
        } catch (IOException e) {
            LOGGER.error("Error while reading directory: {}", e.getMessage());
        }
        return null;
    }

    private static void sortFiles(@NotNull File... files) {
        Arrays.sort(files, (o1, o2) -> {
            if (o1.isDirectory() ^ o2.isDirectory()) {
                if (o1.isDirectory()) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
    }

    @Nullable
    private SwingWorker<MutableTreeNode, Object> currentWorker;

    public void setDirectory(@NotNull Path file) {
        if (Files.isDirectory(file)) {
            SwingWorker<MutableTreeNode, Object> localCurrentWorker = currentWorker;
            if (localCurrentWorker != null && localCurrentWorker.getState() != StateValue.DONE) {
                localCurrentWorker.cancel(true);
            }
            setModel(null);
            currentWorker = new SwingWorker<MutableTreeNode, Object>() {
                @Override
                @Nullable
                protected MutableTreeNode doInBackground() {
                    return scan(file);
                }

                @Override
                protected void done() {
                    try {
                        setModel(new DefaultTreeModel(get()));
                    } catch (Exception ignore) {
                    }
                    currentWorker = null;
                }
            };
            currentWorker.execute();
        }
    }

    @Override
    protected void paintComponent(@NotNull Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());

        int fromRow = getRowForPath(getSelectionPath());
        if (fromRow != -1) {
            Rectangle fromBounds = getRowBounds(fromRow);
            if (fromBounds != null) {
                g.setColor(SELECTED_COLOR);
                g.fillRect(0, fromBounds.y, getWidth(), fromBounds.height);
            }
        }
        setOpaque(false);
        super.paintComponent(g);
        setOpaque(false);
    }
}
