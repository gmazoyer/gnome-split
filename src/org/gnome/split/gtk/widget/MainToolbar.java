/*
 * MainToolbar.java
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
    /**
     * The start button (also resume button).
     */
    private ToolItem start;

    /**
     * The pause button.
     */
    private ToolItem pause;

    /**
     * The cancel button.
     */
    private ToolItem cancel;

    /**
     * The clear button.
     */
    private ToolItem clear;

    /**
     * The properties button.
     */
    private ToolItem properties;

    public MainToolbar(final GnomeSplit app) {
        super();

        // Get the current manager
        final ActionManager actions = app.getActionManager();

        // Add start button
        start = actions.getAction(ActionId.TOOL_START).createToolItem();
        this.insert(start, 0);

        // Add pause button
        pause = actions.getAction(ActionId.TOOL_PAUSE).createToolItem();
        this.insert(pause, 1);

        // Add cancel button
        cancel = actions.getAction(ActionId.TOOL_CANCEL).createToolItem();
        this.insert(cancel, 2);

        // Add clear button
        clear = actions.getAction(ActionId.TOOL_CLEAR).createToolItem();
        this.insert(clear, 3);

        // Add a separator
        this.insert(new SeparatorToolItem(), 4);

        // Add properties button
        properties = actions.getAction(ActionId.TOOL_PROPERTIES).createToolItem();
        this.insert(properties, 5);
    }

    /**
     * Update the &quot;sensitive&quot; state of each button.
     */
    public void setActives(boolean state1, boolean state2, boolean state3, boolean state4, boolean state5) {
        start.setSensitive(state1);
        pause.setSensitive(state2);
        cancel.setSensitive(state3);
        clear.setSensitive(state4);
        properties.setSensitive(state5);
    }
}
