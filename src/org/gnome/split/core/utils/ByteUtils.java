/*
 * ByteUtils.java
 * 
 * Copyright (c) 2009-2012 Guillaume Mazoyer
 * 
 * This file is part of GNOME Split.
 * 
 * GNOME Split is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * GNOME Split is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with GNOME Split.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gnome.split.core.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Give some methods to convert bytes or other types to bytes.
 * 
 * @author Guillaume Mazoyer
 */
public final class ByteUtils
{
    /**
     * Convert an array of {@link Byte bytes} using little endian coding to a
     * {@link Integer} value.
     */
    public static int littleEndianToInt(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);

        // Order as little endian
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Convert and return the value
        return buffer.getInt();
    }

    /**
     * Convert the value using the little endian coding.
     */
    public static byte[] toLittleEndian(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);

        // Order as little endian and convert the value
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(value);

        return buffer.array();
    }

    /**
     * Convert an array of {@link Byte bytes} using little endian coding to a
     * {@link Long} value.
     */
    public static long littleEndianToLong(byte[] array) {
        ByteBuffer buffer = ByteBuffer.wrap(array);

        // Order as little endian
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // Convert and return the value
        return buffer.getLong();
    }

    /**
     * Convert the value using the little endian coding.
     */
    public static byte[] toLittleEndian(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);

        // Order as little endian and convert the value
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(value);

        return buffer.array();
    }

    /**
     * Convert the value using the little endian coding and return only the 4
     * interesting bytes.
     */
    public static byte[] toLittleEndian(double value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);

        // Order as little endian and convert the value
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putDouble(value);

        // Keep only the 4 needed bytes
        byte[] result = buffer.array();
        result = new byte[] {
            result[4],
            result[5],
            result[6],
            result[7]
        };

        return result;
    }
}
