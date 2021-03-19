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
package illarion.client.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class stores a list of tasks that are supposed to be executed during the next update cycle of the game.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class UpdateTaskManager {
    /**
     * The task queue.
     */
    @NotNull
    private final Queue<UpdateTask> taskQueue;

    /**
     * This value is set {@code true} while the updates are executed.
     */
    private boolean isInUpdateCall;
    /**
     * The delta value of the current update run.
     */
    private int currentDelta;

    /**
     * The thread that is tasked to execute the updates.
     */
    @Nullable
    private Thread currentThread;

    /**
     * The default constructor that prepares the internal structures.
     */
    public UpdateTaskManager() {
        taskQueue = new ConcurrentLinkedQueue<>();
    }

    /**
     * This function is triggered during the update loop of the game and triggers the update tasks.
     *
     * @param delta the time since the last update
     */
    public void onUpdateGame(int delta) {
        currentDelta = delta;
        currentThread = Thread.currentThread();
        isInUpdateCall = true;
        try {
            while (true) {
                @Nullable UpdateTask task = taskQueue.poll();
                if (task == null) {
                    return;
                }

                task.onUpdateGame(delta);
            }
        } finally {
            isInUpdateCall = false;
        }
    }

    /**
     * Add a task to the list of tasks executed during the update loop. In case the update loop is currently
     * executed from the calling thread, the task is executed instantly.
     *
     * @param task the task to execute
     */
    public void addTask(@NotNull UpdateTask task) {
        if (isInUpdateCall && Objects.equals(currentThread, Thread.currentThread())) {
            task.onUpdateGame(currentDelta);
        } else {
            taskQueue.offer(task);
        }
    }

    /**
     * Add a task to the list of tasks executed during the next update.
     *
     * @param task the task to execute
     */
    public void addTaskForLater(@NotNull UpdateTask task) {
        taskQueue.offer(task);
    }
}
