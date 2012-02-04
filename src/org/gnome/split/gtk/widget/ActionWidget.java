/*
 * ActionWidget.java
 * 
 * Copyright (c) 2009-2012 Guillaume Mazoyer
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

import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;

/**
 * Interface which defines a model for action views.
 * 
 * @author Guillaume Mazoyer
 */
public interface ActionWidget
{
    /**
     * Group to make all labels of the interface identical.
     */
    final static SizeGroup labels = new SizeGroup(SizeGroupMode.BOTH);

    /**
     * Group to make all choosers of the interface identical.
     */
    final static SizeGroup choosers = new SizeGroup(SizeGroupMode.BOTH);

    /**
     * Return whether or not the widget is visible.
     */
    public boolean isVisible();

    /**
     * Change the visible state of the widget.
     */
    public void setVisible(boolean setting);

    /**
     * Return whether or not the user can start an action.
     */
    public boolean isFullyFilled();

    /**
     * Return whether or not there is enough free space on the partition for
     * our split/merge. If there is not enough space -1 is returned.
     */
    public long checkFreeSpace();

    /**
     * Return whether or not we can read/write on the file system. 0 means OK,
     * 1 means can't read, 2 means can't write, 3 means can't read and can't
     * write.
     */
    public byte checkFileSystemPermission();

    /**
     * Disable the widget causing all sub-widgets to be non-sensitive.
     */
    public void disable();

    /**
     * Enable the widget causing all sub-widgets to be sensitive.
     */
    public void enable();

    /**
     * Reset the widget to its basic state.
     */
    public void reset();

    /**
     * Update the progress which is displayed by the widget.
     */
    public void updateProgress(double progress, String text, boolean sure);
}
