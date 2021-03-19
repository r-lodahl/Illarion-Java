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
package illarion.common.gui;

import illarion.common.util.PoolThreadFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * This class is a helper that enables to GUI to handle things like double clicks.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public abstract class AbstractMultiActionHelper {
    /**
     * The executor service that takes care for handling the multi action events.
     */
    @NotNull
    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1,
            new PoolThreadFactory("MultiActionHelper", true));

    /**
     * Amount of actions that were registered since the events were last fired.
     */
    private int actionCount;

    /**
     * The amount of clicks that are allowed at the maximum.
     * <p/>
     * {@code -1} means that there is no limit
     */
    private final int countLimit;

    /**
     * The timeout for this task.
     */
    private final long timeout;

    /**
     * The time unit for the timeout.
     */
    @NotNull
    private final TimeUnit timeoutUnits;

    /**
     * The task that is scheduled for the execution for this action.
     */
    @Nullable
    private ScheduledFuture<Void> task;

    /**
     * Create a instance of this class and set the timeout that should be used to group events.
     *
     * @param timeoutInMs the timeout value in milliseconds
     */
    protected AbstractMultiActionHelper(long timeoutInMs) {
        this(timeoutInMs, -1);
    }

    /**
     * Create a instance of this class and set the timeout that should be used to group events.
     *
     * @param timeoutInMs the timeout value in milliseconds
     * @param limit the amount of clicks allowed at the maximum
     */
    protected AbstractMultiActionHelper(long timeoutInMs, int limit) {
        this(timeoutInMs, TimeUnit.MILLISECONDS, limit);
    }

    /**
     * Create a instance of this class and set the timeout that should be used to group events.
     *
     * @param timeout the timeout value
     * @param unit    the unit of the timeout
     * @param limit   the amount of clicks allowed at the maximum
     */
    protected AbstractMultiActionHelper(long timeout, @NotNull TimeUnit unit, int limit) {
        this.timeout = timeout;
        timeoutUnits = unit;
        countLimit = limit;
        task = null;
        reset();
    }

    /**
     * Reset the helper.
     */
    public final void reset() {
        resetPendingTask();
        actionCount = 0;
    }

    /**
     * Send one action pulse to the helper.
     */
    public final void pulse() {
        actionCount++;
        if ((actionCount < countLimit) || (countLimit == -1)) {
            resetPendingTask();
            task = executorService.schedule(() -> {
                task = null;
                execute();
                return null;
            }, timeout, timeoutUnits);
        } else {
            execute();
        }
    }

    private void resetPendingTask() {
        ScheduledFuture<Void> pendingTask = task;
        task = null;
        if (pendingTask != null) {
            pendingTask.cancel(false);
        }
    }

    public final void execute() {
        int count = actionCount;
        reset();
        executeAction(count);
    }

    /**
     * This function is called with the amount of registered actions as parameter once the timer times out.
     *
     * @param count the amount of actions since the last timeout. This value is 1 or larger.
     */
    public abstract void executeAction(int count);
}
