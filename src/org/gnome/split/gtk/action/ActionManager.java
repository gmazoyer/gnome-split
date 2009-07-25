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

        actions.put(ActionId.MENU_EXIT, new QuitAction(app));
        actions.put(ActionId.MENU_PREFERENCES, new PreferencesAction(app));
        actions.put(ActionId.MENU_HELP, new HelpAction(app));
        actions.put(ActionId.MENU_ABOUT, new AboutAction(app));
        actions.put(ActionId.TRAY_EXIT, new QuitAction(app));
        actions.put(ActionId.TRAY_ABOUT, new AboutAction(app));
    }

    public Action getAction(ActionId id) {
        return actions.get(id);
    }

    public enum ActionId
    {
        MENU_EXIT, MENU_PREFERENCES, MENU_HELP, MENU_ABOUT, TRAY_EXIT, TRAY_ABOUT;
    }
}
