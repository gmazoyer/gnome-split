/*
 * ToggleAction.java
 * 
 * Copyright (c) 2009-2013 Guillaume Mazoyer
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
package org.gnome.split.gtk.action;

/**
 * Abstract class to define a action triggered by a GTK+ toggle widget.
 * 
 * @author Guillaume Mazoyer
 */
public abstract class ToggleAction extends org.gnome.gtk.ToggleAction implements
        org.gnome.gtk.ToggleAction.Toggled
{
    /**
     * Create a new action using a label, a tooltip and a state.
     */
    protected ToggleAction(String name, String label, String tooltip, boolean active) {
        super(name, label, tooltip, null);

        this.setActive(active);
        this.connect((ToggleAction.Toggled) this);
    }

    /**
     * Create a new action using a label and a state.
     */
    protected ToggleAction(String name, String label, boolean active) {
        super(name, label);

        this.setActive(active);
        this.connect((ToggleAction.Toggled) this);
    }
}
