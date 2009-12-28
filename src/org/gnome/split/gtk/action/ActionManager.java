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

        // Actions related to split and merge processes
        OpenDirAction directory = new OpenDirAction(app);
        PropertiesAction properties = new PropertiesAction(app);
        StartAction start = new StartAction(app);
        PauseAction pause = new PauseAction(app);
        CancelAction cancel = new CancelAction(app);
        DeleteAction delete = new DeleteAction(app);

        // Add the previously created actions
        actions.put(ActionId.OPEN_DIR, directory);
        actions.put(ActionId.PROPERTIES, properties);
        actions.put(ActionId.START, start);
        actions.put(ActionId.PAUSE, pause);
        actions.put(ActionId.CANCEL, cancel);
        actions.put(ActionId.DELETE, delete);

        // Actions related to the interface and program management
        ClearAction clear = new ClearAction(app);
        QuitAction quit = new QuitAction(app);
        PreferencesAction preferences = new PreferencesAction(app);
        HelpAction help = new HelpAction(app);
        AboutAction about = new AboutAction(app);

        // Add the previously created actions
        actions.put(ActionId.CLEAR, clear);
        actions.put(ActionId.EXIT, quit);
        actions.put(ActionId.PREFERENCES, preferences);
        actions.put(ActionId.HELP, help);
        actions.put(ActionId.ABOUT, about);

        // Other actions related to the interface which have two possible
        // states (active or inactive)
        SelectSplitAction split = new SelectSplitAction(app);
        SelectMergeAction merge = new SelectMergeAction(app);
        MainWindowAction window = new MainWindowAction(app);

        // Add the previously created actions
        toggles.put(ActionId.SELECT_SPLIT, split);
        toggles.put(ActionId.SELECT_MERGE, merge);
        toggles.put(ActionId.TRAY_WINDOW, window);
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
        this.getAction(ActionId.OPEN_DIR).setActive(false);
        this.getAction(ActionId.PROPERTIES).setActive(false);
        this.getAction(ActionId.START).setActive(true);
        this.getAction(ActionId.PAUSE).setActive(false);
        this.getAction(ActionId.CANCEL).setActive(false);
        this.getAction(ActionId.DELETE).setActive(false);
        this.getAction(ActionId.CLEAR).setActive(true);
    }

    /**
     * Set the actions in the pause state of the interface.
     */
    public void setPauseState() {
        this.getAction(ActionId.OPEN_DIR).setActive(true);
        this.getAction(ActionId.PROPERTIES).setActive(false);
        this.getAction(ActionId.START).setActive(true);
        this.getAction(ActionId.PAUSE).setActive(false);
        this.getAction(ActionId.CANCEL).setActive(true);
        this.getAction(ActionId.DELETE).setActive(true);
        this.getAction(ActionId.CLEAR).setActive(false);
    }

    /**
     * Set the actions in the running state of the interface.
     */
    public void setRunningState() {
        this.getAction(ActionId.OPEN_DIR).setActive(true);
        this.getAction(ActionId.PROPERTIES).setActive(false);
        this.getAction(ActionId.START).setActive(false);
        this.getAction(ActionId.PAUSE).setActive(true);
        this.getAction(ActionId.CANCEL).setActive(true);
        this.getAction(ActionId.DELETE).setActive(true);
        this.getAction(ActionId.CLEAR).setActive(false);
    }

    /**
     * List of all action IDs.
     * 
     * @author Guillaume Mazoyer
     */
    public enum ActionId
    {
        OPEN_DIR, PROPERTIES, START, PAUSE, CANCEL, DELETE, CLEAR, EXIT, PREFERENCES, HELP, ABOUT, SELECT_SPLIT, SELECT_MERGE, TRAY_WINDOW;
    }
}
