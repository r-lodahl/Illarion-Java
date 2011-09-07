/*
 * This file is part of the Illarion Input Engine.
 *
 * Copyright © 2011 - Illarion e.V.
 *
 * The Illarion Input Engine is free software: you can redistribute i and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * The Illarion Input Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * the Illarion Input Interface. If not, see <http://www.gnu.org/licenses/>.
 */
package illarion.input.receiver;

/**
 * This is the interface to receive primitive keyboard events.
 * 
 * @author Martin Karing
 * @since 1.22
 * @version 1.22
 */
public interface KeyboardEventReceiverPrimitive extends KeyboardEventReceiver {
    /**
     * The function to receive the keyboard event.
     * 
     * @param key the ID of the key
     * @param character the character of the key
     * @param down <code>true</code> in case the key got pressed down
     * @return <code>true</code> in case this event got consumed
     */
    boolean handleKeyboardEvent(int key, char character, boolean down);
}
