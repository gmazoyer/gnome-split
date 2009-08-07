/*
 * ActionManager.java
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
package org.gnome.split.gtk.action;

import java.util.HashMap;
import java.util.Map;

import org.gnome.split.GnomeSplit;

public class ActionManager
{
    private Map<ActionId, Action> actions;

    public ActionManager(final GnomeSplit app) {
        actions = new HashMap<ActionId, Action>();

        // Actions related to the menubar
        actions.put(ActionId.MENU_NEW, new NewAction(app));
        actions.put(ActionId.MENU_PROPERTIES, new PropertiesAction(app));
        actions.put(ActionId.MENU_OPEN_DIR, new OpenDirAction(app));
        actions.put(ActionId.MENU_START, new StartAction(app));
        actions.put(ActionId.MENU_PAUSE, new PauseAction(app));
        actions.put(ActionId.MENU_REMOVE, new RemoveAction(app));
        actions.put(ActionId.MENU_DELETE, new DeleteAction(app));
        actions.put(ActionId.MENU_START_ALL, new StartAllAction(app));
        actions.put(ActionId.MENU_PAUSE_ALL, new PauseAllAction(app));
        actions.put(ActionId.MENU_EXIT, new QuitAction(app));
        actions.put(ActionId.MENU_SELECT_ALL, new SelectAllAction(app));
        actions.put(ActionId.MENU_UNSELECT_ALL, new UnselectAllAction(app));
        actions.put(ActionId.MENU_PREFERENCES, new PreferencesAction(app));
        actions.put(ActionId.MENU_HELP, new HelpAction(app));
        actions.put(ActionId.MENU_ABOUT, new AboutAction(app));

        // Actions related to the toolbar
        actions.put(ActionId.TOOL_NEW, new NewAction(app));
        actions.put(ActionId.TOOL_START, new StartAction(app));
        actions.put(ActionId.TOOL_PAUSE, new PauseAction(app));
        actions.put(ActionId.TOOL_REMOVE, new RemoveAction(app));
        actions.put(ActionId.TOOL_PROPERTIES, new PropertiesAction(app));

        // Actions related to the tray icon
        actions.put(ActionId.TRAY_START_ALL, new StartAllAction(app));
        actions.put(ActionId.TRAY_PAUSE_ALL, new PauseAllAction(app));
        actions.put(ActionId.TRAY_EXIT, new QuitAction(app));
        actions.put(ActionId.TRAY_ABOUT, new AboutAction(app));
    }

    /**
     * Get an action using its ID.
     * 
     * @param id
     *            the action ID.
     * @return the action.
     */
    public Action getAction(ActionId id) {
        return actions.get(id);
    }

    /**
     * List of all action IDs.
     * 
     * @author Guillaume Mazoyer
     */
    public enum ActionId
    {
        MENU_NEW, MENU_PROPERTIES, MENU_OPEN_DIR, MENU_START, MENU_PAUSE, MENU_REMOVE, MENU_DELETE, MENU_START_ALL, MENU_PAUSE_ALL, MENU_EXIT, MENU_SELECT_ALL, MENU_UNSELECT_ALL, MENU_PREFERENCES, MENU_HELP, MENU_ABOUT, TOOL_NEW, TOOL_START, TOOL_PAUSE, TOOL_REMOVE, TOOL_PROPERTIES, TRAY_START_ALL, TRAY_PAUSE_ALL, TRAY_EXIT, TRAY_ABOUT;
    }
}
