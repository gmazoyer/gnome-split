/*
 * ByteUtils.java
 * 
 * Copyright (c) 2009-2010 Guillaume Mazoyer
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

/**
 * Give some methods to convert bytes or other types to bytes.
 * 
 * @author Guillaume Mazoyer
 */
public final class ByteUtils
{
    /**
     * Convert an array of {@link Byte bytes} to a {@link Integer} value.
     */
    public static int toInt(byte[] b) {
        int value = 0;
        int exp = 0;
        for (int i = 0; i < b.length; i++) {
            value += b[i] << exp;
            exp += 8;
        }
        return value;
    }

    /**
     * Convert an {@link Integer} value to an array of {@link Byte bytes}.
     */
    public static byte[] toBytes(int num) {
        byte[] b = new byte[4];
        int exp = 3 * 8;
        for (int i = b.length - 1; i >= 0; i--) {
            b[i] = (byte) (num << exp);
            exp -= 8;
        }
        return b;
    }

    /**
     * Convert a {@link Long} value to an array of {@link Byte bytes}.
     */
    public static long toLong(byte[] b) {
        long value = 0;
        int exp = 0;
        for (int i = 0; i < b.length; i++) {
            value += (((long) b[i]) & 0xFF) << exp;
            exp += 8;
        }
        return value;
    }

    /**
     * Convert an array of {@link Byte bytes} to a {@link Long} value.
     */
    public static byte[] toBytes(long num) {
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) num;
            num >>>= 8;
        }
        return b;
    }
}
