/*
 * MainToolbar.java
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

import org.gnome.gtk.SeparatorToolItem;
import org.gnome.gtk.ToolItem;
import org.gnome.gtk.Toolbar;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.action.ActionManager;
import org.gnome.split.gtk.action.ActionManager.ActionId;

/**
 * Widget derived from {@link Toolbar} which is automatically filled up.
 * 
 * @author Guillaume Mazoyer
 */
public class MainToolbar extends Toolbar
{
    public MainToolbar(final GnomeSplit app) {
        super();

        // Get the current manager
        final ActionManager actions = app.getActionManager();

        // Add start button
        final ToolItem start = actions.getAction(ActionId.START).createToolItem();
        this.insert(start, 0);

        // Add pause button
        final ToolItem pause = actions.getAction(ActionId.PAUSE).createToolItem();
        this.insert(pause, 1);

        // Add cancel button
        final ToolItem cancel = actions.getAction(ActionId.CANCEL).createToolItem();
        this.insert(cancel, 2);

        // Add clear button
        final ToolItem clear = actions.getAction(ActionId.CLEAR).createToolItem();
        this.insert(clear, 3);

        // Add a separator
        this.insert(new SeparatorToolItem(), 4);

        // Add properties button
        final ToolItem properties = actions.getAction(ActionId.PROPERTIES).createToolItem();
        this.insert(properties, 5);
    }
}
