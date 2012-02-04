/*
 * MainToolbar.java
 * 
 * Copyright (c) 2009-2011 Guillaume Mazoyer
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
import org.gnome.gtk.StyleClass;
import org.gnome.gtk.StyleContext;
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

        // Add a button to show the assistant
        final ToolItem assistant = actions.getAction(ActionId.ASSISTANT).createToolItem();
        this.insert(assistant, 0);

        // Add a separator
        this.insert(new SeparatorToolItem(), 1);

        // Add start button
        final ToolItem start = actions.getAction(ActionId.START).createToolItem();
        this.insert(start, 2);

        // Add pause button
        final ToolItem pause = actions.getAction(ActionId.PAUSE).createToolItem();
        this.insert(pause, 3);

        // Add cancel button
        final ToolItem cancel = actions.getAction(ActionId.CANCEL).createToolItem();
        this.insert(cancel, 4);

        // Add a separator
        this.insert(new SeparatorToolItem(), 5);

        // Add clear button
        final ToolItem clear = actions.getAction(ActionId.CLEAR).createToolItem();
        this.insert(clear, 6);

        // Make this toolbar primary for theming purpose
        final StyleContext style = this.getStyleContext();
        style.addClass(StyleClass.PRIMARY_TOOLBAR);
        style.save();
    }
}
