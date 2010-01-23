/*
 * SizeUnit.java
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

import java.text.DecimalFormat;

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
    public static final double KB = 1024;

    /**
     * One megabyte.
     */
    public static final double MB = 1024 * 1024;

    /**
     * One gigabyte.
     */
    public static final double GB = 1024 * 1024 * 1024;

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

    /**
     * Get the best divider for a size to make it readable for human being.
     */
    public static double getDivider(double size) {
        if (size < KB) {
            // Use bytes only
            return 0;
        } else if (size < MB) {
            // Use kilobytes
            return KB;
        } else if (size < GB) {
            // Use megabytes
            return MB;
        } else {
            // Use gigabytes
            return GB;
        }
    }

    /**
     * Format a size into a {@link String} to make it readable for human
     * being.
     */
    public static String formatSize(double size, double divider) {
        final StringBuilder builder = new StringBuilder();
        final DecimalFormat format = new DecimalFormat();

        // Can have 0 figure after comma
        format.setMinimumFractionDigits(0);

        // Can have (a maximum of) 1 figure after comma
        format.setMaximumFractionDigits(1);

        if (size == -1) {
            builder.append(_("Unknown"));
        } else if (divider == 0) {
            // Use bytes only
            builder.append(format.format(size));
            builder.append(" ");
            builder.append(_("bytes"));
        } else {
            double displayed;

            if (divider == KB) {
                // Use kilobytes
                displayed = (double) size / KB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("KB"));
            } else if (divider == MB) {
                // Use megabytes
                displayed = (double) size / MB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("MB"));
            } else {
                // Use gigabytes
                displayed = (double) size / GB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("GB"));
            }
        }

        // Finally
        return builder.toString();
    }

    /**
     * Format a speed into a {@link String} to make it readable for human
     * being.
     */
    public static String formatSpeed(double speed, double divider) {
        final StringBuilder builder = new StringBuilder();
        final DecimalFormat format = new DecimalFormat();

        // Can have 0 figure after comma
        format.setMinimumFractionDigits(0);

        // Can have (a maximum of) 1 figure after comma
        format.setMaximumFractionDigits(1);

        // Append a first string
        builder.append("@ ");

        if (speed <= 0) {
            builder.append(_("Unknown speed"));
        } else if (divider == 0) {
            // Use bytes only
            builder.append(format.format(speed));
            builder.append(" ");
            builder.append(_("b/s"));
        } else {
            double displayed;

            if (divider == KB) {
                // Use kilobytes
                displayed = (double) speed / KB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("KB/s"));
            } else if (divider == MB) {
                // Use megabytes
                displayed = (double) speed / MB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("MB/s"));
            } else {
                // Use gigabytes
                displayed = (double) speed / GB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("GB/s"));
            }
        }

        // Finally
        return builder.toString();
    }

    /**
     * Format a size using the best unit for it.
     */
    public static String formatSize(double size) {
        return formatSize(size, getDivider(size));
    }

    /**
     * Format a speed using the best unit for it.
     */
    public static String formatSpeed(double speed) {
        return formatSpeed(speed, getDivider(speed));
    }

    /**
     * Get a {@link String} representation of all units.
     */
    public static String[] toStrings() {
        return new String[] {
                _("chunks"),
                _("bytes (B)"),
                _("kilobytes (KB)"),
                _("megabytes (MB)"),
                _("gigbaytes (GB)")
        };
    }
}
