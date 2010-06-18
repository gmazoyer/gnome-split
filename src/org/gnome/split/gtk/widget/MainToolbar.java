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

import org.gnome.gtk.Activatable;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuToolButton;
import org.gnome.gtk.SeparatorToolItem;
import org.gnome.gtk.Stock;
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

        // Add a button to select an assistant
        final MenuToolButton assistants = new MenuToolButton(Stock.NEW);
        this.insert(assistants, 0);

        // Make this button a proxy of the assistant action
        ((Activatable) assistants).setRelatedAction(actions.getAction(ActionId.ASSISTANT));

        // Attach a menu to this button
        final Menu menu = new Menu();
        assistants.setMenu(menu);

        // And finally, attache menu items to the menu
        menu.append(actions.getAction(ActionId.SPLIT_ASSISTANT).createMenuItem());
        menu.append(actions.getAction(ActionId.MERGE_ASSISTANT).createMenuItem());

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
    }
}
