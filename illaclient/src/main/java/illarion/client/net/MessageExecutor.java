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
package illarion.client.net;

import illarion.client.net.server.ServerReply;
import illarion.client.net.server.ServerReplyResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

/**
 * This class will take care that the messages received from the server are executes properly.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
final class MessageExecutor {
    @NotNull
    private static final Logger log = LogManager.getLogger();

    @NotNull
    private final ExecutorService executorService;

    /**
     * Default constructor for a message executor.
     */
    MessageExecutor() {
        executorService = Executors.newSingleThreadExecutor();
    }

    void scheduleReplyExecution(@NotNull ServerReply reply) {
        log.debug("scheduled" + reply);
        executorService.submit(() -> executeReply(reply));
    }

    private void executeReply(@NotNull ServerReply reply) {
        log.debug("executing" + reply);
        try {
            ServerReplyResult result = reply.execute();
            switch (result) {
                case Success:
                    log.debug("finished with success" + reply);
                    break;
                case Failed:
                    log.error("finished with failure" + reply);
                    break;
                case Reschedule:
                    log.debug("delaying" + reply);
                    scheduleReplyExecution(reply);
            }
        } catch (Exception e) {
            log.error("Error while executing server replay." + e);
        }
    }

    /**
     * Shutdown the sender.
     */
    @NotNull
    public Future<Boolean> saveShutdown() {
        executorService.shutdown();

        return new Future<Boolean>() {
            @Override
            @Contract(value = "_ -> false", pure = true)
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            @Contract(value = "-> false", pure = true)
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return executorService.isTerminated();
            }

            @Override
            @NotNull
            public Boolean get() throws InterruptedException, ExecutionException {
                try {
                    return get(1, TimeUnit.HOURS);
                } catch (TimeoutException e) {
                    throw new ExecutionException(e);
                }
            }

            @Override
            @NotNull
            public Boolean get(long timeout, @NotNull TimeUnit unit)
                    throws InterruptedException, ExecutionException, TimeoutException {
                return executorService.awaitTermination(timeout, unit);
            }
        };
    }
}
