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

/**
 * A manager to manage actions used by the GTK+ interface.
 * 
 * @author Guillaume Mazoyer
 */
public class ActionManager
{
    /**
     * Regular actions.
     */
    private Map<ActionId, Action> actions;

    /**
     * Actions based on two states.
     */
    private Map<ActionId, ToggleAction> toggles;

    public ActionManager(final GnomeSplit app) {
        actions = new HashMap<ActionId, Action>();
        toggles = new HashMap<ActionId, ToggleAction>();

        // Actions related to the menubar
        actions.put(ActionId.MENU_PROPERTIES, new PropertiesAction(app));
        actions.put(ActionId.MENU_OPEN_DIR, new OpenDirAction(app));
        actions.put(ActionId.MENU_START, new StartAction(app));
        actions.put(ActionId.MENU_PAUSE, new PauseAction(app));
        actions.put(ActionId.MENU_CANCEL, new CancelAction(app));
        actions.put(ActionId.MENU_DELETE, new DeleteAction(app));
        actions.put(ActionId.MENU_CLEAR, new ClearAction(app));
        actions.put(ActionId.MENU_EXIT, new QuitAction(app));
        actions.put(ActionId.MENU_PREFERENCES, new PreferencesAction(app));
        actions.put(ActionId.MENU_HELP, new HelpAction(app));
        actions.put(ActionId.MENU_ABOUT, new AboutAction(app));

        // Actions related to the toolbar
        actions.put(ActionId.TOOL_START, new StartAction(app));
        actions.put(ActionId.TOOL_PAUSE, new PauseAction(app));
        actions.put(ActionId.TOOL_CANCEL, new CancelAction(app));
        actions.put(ActionId.TOOL_CLEAR, new ClearAction(app));
        actions.put(ActionId.TOOL_PROPERTIES, new PropertiesAction(app));

        // Actions related to the select view widget
        toggles.put(ActionId.SELECT_SPLIT, new SelectSplitAction(app));
        toggles.put(ActionId.SELECT_MERGE, new SelectMergeAction(app));

        // Actions related to the tray icon
        toggles.put(ActionId.TRAY_WINDOW, new MainWindowAction(app));
        actions.put(ActionId.TRAY_EXIT, new QuitAction(app));
        actions.put(ActionId.TRAY_ABOUT, new AboutAction(app));
    }

    /**
     * Get an action using its ID.
     */
    public Action getAction(ActionId id) {
        return actions.get(id);
    }

    /**
     * Get a toggle action using its ID.
     */
    public ToggleAction getToggleAction(ActionId id) {
        return toggles.get(id);
    }

    /**
     * Set the actions in the ready state of the interface.
     */
    public void setReadyState() {
        this.getAction(ActionId.MENU_OPEN_DIR).setActive(false);
        this.getAction(ActionId.MENU_PROPERTIES).setActive(false);
        this.getAction(ActionId.MENU_START).setActive(true);
        this.getAction(ActionId.MENU_PAUSE).setActive(false);
        this.getAction(ActionId.MENU_CANCEL).setActive(false);
        this.getAction(ActionId.MENU_DELETE).setActive(false);
        this.getAction(ActionId.MENU_CLEAR).setActive(true);
        this.getAction(ActionId.TOOL_START).setActive(true);
        this.getAction(ActionId.TOOL_PAUSE).setActive(false);
        this.getAction(ActionId.TOOL_CANCEL).setActive(false);
        this.getAction(ActionId.TOOL_CLEAR).setActive(true);
        this.getAction(ActionId.TOOL_PROPERTIES).setActive(false);
    }

    /**
     * Set the actions in the pause state of the interface.
     */
    public void setPauseState() {
        this.getAction(ActionId.MENU_OPEN_DIR).setActive(true);
        this.getAction(ActionId.MENU_PROPERTIES).setActive(false);
        this.getAction(ActionId.MENU_START).setActive(true);
        this.getAction(ActionId.MENU_PAUSE).setActive(false);
        this.getAction(ActionId.MENU_CANCEL).setActive(true);
        this.getAction(ActionId.MENU_DELETE).setActive(true);
        this.getAction(ActionId.MENU_CLEAR).setActive(false);
        this.getAction(ActionId.TOOL_START).setActive(true);
        this.getAction(ActionId.TOOL_PAUSE).setActive(false);
        this.getAction(ActionId.TOOL_CANCEL).setActive(true);
        this.getAction(ActionId.TOOL_CLEAR).setActive(false);
        this.getAction(ActionId.TOOL_PROPERTIES).setActive(false);
    }

    /**
     * Set the actions in the running state of the interface.
     */
    public void setRunningState() {
        this.getAction(ActionId.MENU_OPEN_DIR).setActive(true);
        this.getAction(ActionId.MENU_PROPERTIES).setActive(false);
        this.getAction(ActionId.MENU_START).setActive(false);
        this.getAction(ActionId.MENU_PAUSE).setActive(true);
        this.getAction(ActionId.MENU_CANCEL).setActive(true);
        this.getAction(ActionId.MENU_DELETE).setActive(true);
        this.getAction(ActionId.MENU_CLEAR).setActive(false);
        this.getAction(ActionId.TOOL_START).setActive(false);
        this.getAction(ActionId.TOOL_PAUSE).setActive(true);
        this.getAction(ActionId.TOOL_CANCEL).setActive(true);
        this.getAction(ActionId.TOOL_CLEAR).setActive(false);
        this.getAction(ActionId.TOOL_PROPERTIES).setActive(false);
    }

    /**
     * List of all action IDs.
     * 
     * @author Guillaume Mazoyer
     */
    public enum ActionId
    {
        MENU_OPEN_DIR, MENU_PROPERTIES, MENU_START, MENU_PAUSE, MENU_CANCEL, MENU_DELETE, MENU_CLEAR, MENU_EXIT, MENU_PREFERENCES, MENU_HELP, MENU_ABOUT, TOOL_START, TOOL_PAUSE, TOOL_CANCEL, TOOL_CLEAR, TOOL_PROPERTIES, SELECT_SPLIT, SELECT_MERGE, TRAY_WINDOW, TRAY_EXIT, TRAY_ABOUT;
    }
}
