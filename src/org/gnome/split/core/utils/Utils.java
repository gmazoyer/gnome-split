/*
 * Utils.java
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

import java.util.GregorianCalendar;

/**
 * Give some methods that can be useful.
 * 
 * @author Guillaume Mazoyer
 */
public final class Utils
{
    /**
     * Return the number of days between now and the 30th day of December
     * 1899.
     */
    public static double datetimeFromNow() {
        // Actually, Java can't go back further than 1900/01/30 so this is
        // just for a cosmetic purpose. It is the date on which Delphi's
        // TDatetime type is based.
        GregorianCalendar base = new GregorianCalendar(1899, 12, 30);
        GregorianCalendar time = new GregorianCalendar();

        // Start a 30 because there is one month of difference between
        // TDatetime and Java minimal time.
        double difference = 30;

        // The 2 times are not equals
        while (base.compareTo(time) <= 0) {
            difference++;

            // Increment date too
            base.add(GregorianCalendar.DATE, 1);
        }

        // Finally
        return difference;
    }
}
