/*
 * AlgorithmsBox.java
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
package org.gnome.split.gtk.widget.base;

import org.gnome.gtk.TextComboBox;
import org.gnome.split.GnomeSplit;
import org.gnome.split.core.utils.Algorithm;

/**
 * A {@link TextComboBox} that lists available algorithms.
 * 
 * @author Guillaume Mazoyer
 */
public class AlgorithmsBox extends TextComboBox
{
    public AlgorithmsBox(final GnomeSplit app) {
        super();

        // Add all algorithms
        for (String algorithm : Algorithm.toStrings()) {
            // Fill the box
            this.appendText(algorithm);
        }

        // Set the default algorithm
        this.setActive(app.getConfig().DEFAULT_ALGORITHM);
    }
}
