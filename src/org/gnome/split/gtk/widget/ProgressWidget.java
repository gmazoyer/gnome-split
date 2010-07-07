/*
 * ProgressWidget.java
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
package org.gnome.split.gtk.widget;

import org.gnome.gtk.ProgressBar;
import org.gnome.gtk.VBox;

/**
 * Widget derived from {@link ProgressBar} to show the progress of an action.
 * 
 * @author Guillaume Mazoyer
 */
public class ProgressWidget extends VBox
{
    /**
     * Real progress bar.
     */
    private ProgressBar progress;

    public ProgressWidget() {
        super(false, 0);

        // Set the border
        this.setBorderWidth(3);

        // Add the progress bar
        progress = new ProgressBar();
        this.packStart(progress, false, false, 0);
    }

    @Override
    public void show() {
        super.show();
        progress.show();
    }

    @Override
    public void showAll() {
        super.showAll();
        progress.show();
    }

    /**
     * Make the progress bar pulse.
     */
    public void pulse() {
        progress.pulse();
    }

    /**
     * Set the value that the progress bar must display.
     */
    public void setFraction(double value) {
        progress.setFraction(value);
    }

    /**
     * Set the text that the progress bar must display.
     */
    public void setText(String value) {
        progress.setText(value);
    }

    /**
     * Reset the widget to its initial state.
     */
    public void reset() {
        progress.setFraction(0);
        progress.setText("");
    }
}
