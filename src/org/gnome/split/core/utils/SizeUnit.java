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
    public static final double CDROM = 700 * MiB;

    /**
     * One DVD-ROM
     */
    public static final double DVDROM = 4.7 * GiB;

    private static final double[] values = {
            KiB, MiB, GiB, CDROM, DVDROM
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
        if (size < KiB) {
            // Use bytes only
            return 0;
        } else if (size < MiB) {
            // Use kilobytes
            return KiB;
        } else if (size < GiB) {
            // Use megabytes
            return MiB;
        } else {
            // Use gigabytes
            return GiB;
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

            if (divider == KiB) {
                // Use kilobytes
                displayed = (double) size / KiB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("KiB"));
            } else if (divider == MiB) {
                // Use megabytes
                displayed = (double) size / MiB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("MiB"));
            } else {
                // Use gigabytes
                displayed = (double) size / GiB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("GiB"));
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
            builder.append(_("B/s"));
        } else {
            double displayed;

            if (divider == KiB) {
                // Use kilobytes
                displayed = (double) speed / KiB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("KiB/s"));
            } else if (divider == MiB) {
                // Use megabytes
                displayed = (double) speed / MiB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("MiB/s"));
            } else {
                // Use gigabytes
                displayed = (double) speed / GiB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("GiB/s"));
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
                _("chunks"), _("bytes (B)"), _("kibibytes (KiB)"), _("mebibytes (MiB)"),
                _("gibibytes (GiB)"), _("CD-ROM (700 MiB)"), _("DVD-ROM (4.7 GiB)")
        };
    }
}
