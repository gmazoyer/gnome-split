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

import org.gnome.gdk.Keyval;
import org.gnome.gdk.ModifierType;
import org.gnome.gtk.AcceleratorGroup;
import org.gnome.gtk.RadioGroup;
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

    /**
     * Actions based on two states but only one can be active.
     */
    private Map<ActionId, RadioAction> radios;

    /**
     * Group of accelerators for actions.
     */
    private AcceleratorGroup accelerators;

    public ActionManager(final GnomeSplit app) {
        // Create maps of actions
        actions = new HashMap<ActionId, Action>();
        toggles = new HashMap<ActionId, ToggleAction>();
        radios = new HashMap<ActionId, RadioAction>();

        // Create the group of acceperators
        accelerators = new AcceleratorGroup();

        // Actions related to split and merge assistant
        AssistantAction assistant = new AssistantAction(app);
        assistant.setAccelerator(accelerators, Keyval.a, ModifierType.CONTROL_MASK);

        // Action to open the directory
        OpenDirAction directory = new OpenDirAction(app);
        directory.setAccelerator(accelerators, Keyval.o, ModifierType.CONTROL_MASK);

        // Action to start a split/merge
        StartAction start = new StartAction(app);
        start.setAccelerator(accelerators, Keyval.s, ModifierType.CONTROL_MASK);

        // Action to suspend a split/merge
        PauseAction pause = new PauseAction(app);
        pause.setAccelerator(accelerators, Keyval.p, ModifierType.CONTROL_MASK);

        // Action to cancel a split/merge
        CancelAction cancel = new CancelAction(app);
        cancel.setAccelerator(accelerators, Keyval.c, ModifierType.CONTROL_MASK);

        // Action to delete files and cancel a split/merge
        DeleteAction delete = new DeleteAction(app);
        delete.setAccelerator(accelerators, Keyval.Delete, ModifierType.SHIFT_MASK);

        // Add the previously created actions
        actions.put(ActionId.ASSISTANT, assistant);
        actions.put(ActionId.OPEN_DIR, directory);
        actions.put(ActionId.START, start);
        actions.put(ActionId.PAUSE, pause);
        actions.put(ActionId.CANCEL, cancel);
        actions.put(ActionId.DELETE, delete);

        // Action to clear the interface
        ClearAction clear = new ClearAction(app);
        clear.setAccelerator(accelerators, Keyval.c, ModifierType.ALT_MASK);

        // Action to quit the program
        QuitAction quit = new QuitAction(app);
        quit.setAccelerator(accelerators, Keyval.q, ModifierType.CONTROL_MASK);

        // Action to show the preferences
        PreferencesAction preferences = new PreferencesAction(app);

        // Action to open the help
        HelpAction help = new HelpAction(app);
        help.setAccelerator(accelerators, Keyval.F1, ModifierType.NONE);

        // Actions to show the about dialog and to contribute to the project
        OnlineHelpAction online = new OnlineHelpAction(app);
        TranslateAction translate = new TranslateAction(app);
        ReportBugAction report = new ReportBugAction(app);
        AboutAction about = new AboutAction(app);

        // Add the previously created actions
        actions.put(ActionId.CLEAR, clear);
        actions.put(ActionId.EXIT, quit);
        actions.put(ActionId.PREFERENCES, preferences);
        actions.put(ActionId.HELP, help);
        actions.put(ActionId.ONLINE_HELP, online);
        actions.put(ActionId.TRANSLATE, translate);
        actions.put(ActionId.REPORT_BUG, report);
        actions.put(ActionId.ABOUT, about);

        // Other actions related to the interface which have two possible
        // states (active or inactive)
        MainWindowAction window = new MainWindowAction(app);
        ViewToolbarAction toolbar = new ViewToolbarAction(app);
        ViewSwitcherAction switcher = new ViewSwitcherAction(app);
        ViewStatusbarAction status = new ViewStatusbarAction(app);

        // Add the previously created actions
        toggles.put(ActionId.TRAY_WINDOW, window);
        toggles.put(ActionId.TOOLBAR, toolbar);
        toggles.put(ActionId.SWITCHER, switcher);
        toggles.put(ActionId.STATUS, status);

        // Other actions related to the interface which have two possible
        // states (active or inactive) but only one can be active
        RadioGroup views = new RadioGroup();

        // Action to show the split view
        SplitViewAction split = new SplitViewAction(app, views);
        split.setAccelerator(accelerators, Keyval.s, ModifierType.ALT_MASK);

        // Action to show the merge view
        MergeViewAction merge = new MergeViewAction(app, views);
        merge.setAccelerator(accelerators, Keyval.m, ModifierType.ALT_MASK);

        // Add the previously created actions
        radios.put(ActionId.SPLIT, split);
        radios.put(ActionId.MERGE, merge);
    }

    /**
     * Get the group of accelerators associated to the window.
     */
    public AcceleratorGroup getAccelerators() {
        return accelerators;
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
     * Get a radio action using its ID.
     */
    public RadioAction getRadioAction(ActionId id) {
        return radios.get(id);
    }

    /**
     * Emit the signal connected to the {@link Action} associated the
     * {@link ActionId}.
     */
    public void activateAction(ActionId id) {
        this.getAction(id).emitActivate();
    }

    /**
     * Emit the signal connected to the {@link ToggleAction} associated the
     * {@link ActionId}.
     */
    public void activateToggleAction(ActionId id) {
        this.getToggleAction(id).emitActivate();
    }

    /**
     * Emit the signal connected to the {@link RadioAction} associated the
     * {@link ActionId}.
     */
    public void activateRadioAction(ActionId id) {
        this.getRadioAction(id).emitActivate();
    }

    /**
     * List of all action IDs.
     * 
     * @author Guillaume Mazoyer
     */
    public enum ActionId
    {
        ASSISTANT, OPEN_DIR, START, PAUSE, CANCEL, DELETE, CLEAR, EXIT, PREFERENCES, TOOLBAR, SWITCHER, STATUS, SPLIT, MERGE, HELP, ONLINE_HELP, TRANSLATE, REPORT_BUG, ABOUT, TRAY_WINDOW;
    }
}
