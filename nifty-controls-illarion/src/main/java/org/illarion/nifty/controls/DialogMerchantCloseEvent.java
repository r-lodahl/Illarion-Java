/*
 * This file is part of the Illarion project.
 *
 * Copyright © 2014 - Illarion e.V.
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
package org.illarion.nifty.controls;

/**
 * This event is fired in case the player closes the merchant dialog.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public final class DialogMerchantCloseEvent extends DialogEvent {
    /**
     * Create a new instance of this event and set the ID of the dialog that was closed.
     *
     * @param id the ID of the dialog
     */
    public DialogMerchantCloseEvent(int id) {
        super(id);
    }
}
