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
package illarion.common.net;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.CharacterCodingException;

/**
 * This interface offers the possibility to write on a connection handled by the network communication class of
 * Illarion.
 *
 * @author Martin Karing &lt;nitram@illarion.org&gt;
 */
public interface NetCommWriter {
    /**
     * Write 1 byte as signed value to the network.
     *
     * @param value the signed byte that shall be send to the server
     */
    void writeByte(byte value);

    /**
     * Write 4 byte as signed value to the network.
     *
     * @param value the signed integer that shall be send to the server
     */
    void writeInt(int value);

    /**
     * Write 2 byte signed value to the network.
     *
     * @param value the value to write to the server
     */
    void writeShort(short value);

    /**
     * Write a string to the network.
     *
     * @param value the string that shall be send to the server
     */
    void writeString(@NotNull String value) throws CharacterCodingException;

    /**
     * Write 1 byte as unsigned value to the network.
     *
     * @param value the value that shall be send as unsigned byte
     */
    void writeUByte(short value);

    /**
     * Write 2 byte as unsigned value to the network.
     *
     * @param value the value that shall be send as unsigned short
     */
    void writeUShort(int value);
}
