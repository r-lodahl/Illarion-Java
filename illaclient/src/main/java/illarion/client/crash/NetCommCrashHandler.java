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
package illarion.client.crash;

import illarion.client.IllaClient;
import illarion.client.util.Lang;
import org.jetbrains.annotations.NotNull;


/**
 * The crash handler that takes care for crashes of the network interface. It
 * will cause a disconnect of the client and restart the connection.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class NetCommCrashHandler extends AbstractCrashHandler {
    /**
     * The singleton instance of this crash handler to avoid to many instances
     * of this one.
     */
    private static final NetCommCrashHandler INSTANCE = new NetCommCrashHandler();

    /**
     * The private constructor that is used to avoid the creation of any other
     * instances but the singleton instance.
     */
    private NetCommCrashHandler() {
    }

    /**
     * Get the singleton instance of this class.
     *
     * @return the singleton instance of this class
     */
    @NotNull
    public static NetCommCrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * Get the message that describes the problem human readable.
     *
     * @return the error message
     */
    @NotNull
    @Override
    protected String getCrashMessage(@NotNull Thread t, @NotNull Throwable e) {
        return Lang.INSTANCE.getMessagesResourceBundle().getLocalizedString("crash.netcomm")
                + '\n' + e.getLocalizedMessage();
    }

    /**
     * Prepare everything for a proper restart of the map processor.
     */
    @Override
    protected void restart(@NotNull Thread t, @NotNull Throwable e) {
        IllaClient.performLogout();
    }
}
