/*
 * MergeWidget.java
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
package org.gnome.split.gtk.widget;

import org.gnome.gtk.Frame;

/**
 * A widget derived from {@link Frame} to allow the user to start a merge.
 * 
 * @author Guillaume Mazoyer
 */
public class MergeWidget extends Frame implements ActionWidget
{
    /**
     * Define if the widget is visible or not.
     */
    private boolean visible;

    public MergeWidget() {
        super(null);

        this.showAll();
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean setting) {
        visible = setting;

        if (visible) {
            this.showAll();
        } else {
            this.hide();
        }
    }

    @Override
    public boolean isFullyFilled() {
        return false;
    }

    @Override
    public void disable() {

    }

    @Override
    public void enable() {

    }

    @Override
    public void updateProgress(double progress) {

    }
}
