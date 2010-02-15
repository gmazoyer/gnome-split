/*
 * Algorithm.java
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

import static org.freedesktop.bindings.Internationalization._;

/**
 * A class which contains all algorithms references.
 * 
 * @author Guillaume Mazoyer
 */
public final class Algorithm
{
    /**
     * Algorithm used for files without headers.
     */
    public static final int GENERIC = 0;

    /**
     * Our own algorithm.
     */
    public static final int GNOME_SPLIT = 1;

    /**
     * Algorithm used by Xtremsplit.
     */
    public static final int XTREMSPLIT = 2;

    /**
     * Get a {@link String} representation of all algorithms.
     */
    public static String[] toStrings() {
        return new String[] {
                _("Generic"), "GNOME Split", "Xtremsplit"
        };
    }

    /**
     * Get the extensions for the algorithms.
     */
    public static String[] getExtensions() {
        return new String[] {
                ".001.gsp", ".001.xtm", ".000", ".001"
        };
    }

    /**
     * Check if the extension is valid.
     */
    public static boolean isValidExtension(String extension) {
        // GNOME Split file format
        if (extension.endsWith(".001.gsp")) {
            return true;
        }

        // Xtremsplit file format
        if (extension.endsWith(".001.xtm")) {
            return true;
        }

        // Generic file format
        if (extension.endsWith(".000")) {
            return true;
        }

        // Generic file format
        if (extension.endsWith(".001")) {
            return true;
        }

        // Unknown
        return false;
    }
}
