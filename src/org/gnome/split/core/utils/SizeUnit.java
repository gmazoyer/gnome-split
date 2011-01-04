/*
 * SizeUnit.java
 * 
 * Copyright (c) 2009-2011 Guillaume Mazoyer
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

import org.gnome.glib.Glib;

import static org.freedesktop.bindings.Internationalization._;

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
    public static final double KiB = 1024;

    /**
     * One megabyte.
     */
    public static final double MiB = 1024 * 1024;

    /**
     * One gigabyte.
     */
    public static final double GiB = 1024 * 1024 * 1024;

    /**
     * One CD-ROM.
     */
    public static final double CDR = 700 * 1000000;

    /**
     * One DVD-ROM.
     */
    public static final double DVDR = 4.7 * 1000000000;

    /**
     * Get all the values using an array.
     */
    private static final double[] values = {
            KiB, MiB, GiB, CDR, DVDR
    };

    /**
     * Return all units into a array.
     * 
     * @return all units.
     */
    public static double[] values() {
        return values;
    }

    /**
     * Format a size using the best unit for it.
     */
    public static String formatSize(long size) {
        return Glib.formatSizeForDisplay(size);
    }

    /**
     * Format a speed using the best unit for it.
     */
    public static String formatSpeed(long speed) {
        String format = Glib.formatSizeForDisplay(speed);
        return (format += "/s");
    }

    /**
     * Get a {@link String} representation of all units.
     */
    public static String[] toStrings() {
        return new String[] {
                _("chunks"), _("bytes (B)"), _("kibibytes (KiB)"), _("mebibytes (MiB)"),
                _("gibibytes (GiB)"), _("CD-R (700 MB)"), _("DVD-R (4.7 GB)")
        };
    }
}
