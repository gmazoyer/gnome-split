/*
 * SizeUnit.java
 * 
 * Copyright (c) 2009 Guillaume Mazoyer
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
package org.gnome.split.utils;

/**
 * This defines constants to know file size a little bit comfortably.
 * 
 * @author Guillaume Mazoyer
 */
public final class SizeUnit
{
    /**
     * One kilobyte.
     */
    public static final double KB = 1024.0;

    /**
     * One megabyte.
     */
    public static final double MB = 1024.0 * 1024.0;

    /**
     * One gigabyte.
     */
    public static final double GB = 1024.0 * 1024.0 * 1024.0;

    private static final double[] values = {
            KB, MB, GB
    };

    /**
     * Return all units into a array.
     * 
     * @return all units.
     */
    public static double[] values() {
        return values;
    }
}
