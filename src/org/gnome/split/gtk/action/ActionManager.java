/*
 * ActionManager.java
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
        AssistantAction assistant = new AssistantAction(app);
        OpenDirAction directory = new OpenDirAction(app);
        PropertiesAction properties = new PropertiesAction(app);
        StartAction start = new StartAction(app);
        PauseAction pause = new PauseAction(app);
        CancelAction cancel = new CancelAction(app);
        DeleteAction delete = new DeleteAction(app);

        // Add the previously created actions
        actions.put(ActionId.ASSISTANT, assistant);
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
        this.getAction(ActionId.ASSISTANT).setSensitive(true);
        this.getAction(ActionId.OPEN_DIR).setSensitive(false);
        this.getAction(ActionId.PROPERTIES).setSensitive(false);
        this.getAction(ActionId.START).setSensitive(true);
        this.getAction(ActionId.PAUSE).setSensitive(false);
        this.getAction(ActionId.CANCEL).setSensitive(false);
        this.getAction(ActionId.DELETE).setSensitive(false);
        this.getAction(ActionId.CLEAR).setSensitive(true);
    }

    /**
     * Set the actions in the pause state of the interface.
     */
    public void setPauseState() {
        this.getAction(ActionId.ASSISTANT).setSensitive(false);
        this.getAction(ActionId.OPEN_DIR).setSensitive(true);
        this.getAction(ActionId.PROPERTIES).setSensitive(true);
        this.getAction(ActionId.START).setSensitive(true);
        this.getAction(ActionId.PAUSE).setSensitive(false);
        this.getAction(ActionId.CANCEL).setSensitive(true);
        this.getAction(ActionId.DELETE).setSensitive(true);
        this.getAction(ActionId.CLEAR).setSensitive(false);
    }

    /**
     * Set the actions in the running state of the interface.
     */
    public void setRunningState() {
        this.getAction(ActionId.ASSISTANT).setSensitive(false);
        this.getAction(ActionId.OPEN_DIR).setSensitive(true);
        this.getAction(ActionId.PROPERTIES).setSensitive(true);
        this.getAction(ActionId.START).setSensitive(false);
        this.getAction(ActionId.PAUSE).setSensitive(true);
        this.getAction(ActionId.CANCEL).setSensitive(true);
        this.getAction(ActionId.DELETE).setSensitive(true);
        this.getAction(ActionId.CLEAR).setSensitive(false);
    }

    /**
     * List of all action IDs.
     * 
     * @author Guillaume Mazoyer
     */
    public enum ActionId
    {
        ASSISTANT, OPEN_DIR, PROPERTIES, START, PAUSE, CANCEL, DELETE, CLEAR, EXIT, PREFERENCES, HELP, ABOUT, SELECT_SPLIT, SELECT_MERGE, TRAY_WINDOW;
    }
}
