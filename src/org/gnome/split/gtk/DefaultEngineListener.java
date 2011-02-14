/*
 * DefaultEngineListener.java
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
package org.gnome.split.gtk;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.gnome.gtk.Dialog;
import org.gnome.notify.Notification;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Constants;
import org.gnome.split.core.Engine;
import org.gnome.split.core.EngineListener;
import org.gnome.split.core.exception.EngineException;
import org.gnome.split.core.exception.ExceptionMessage;
import org.gnome.split.core.splitter.DefaultSplitEngine;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.dbus.DbusInhibit;
import org.gnome.split.gtk.action.ActionManager;
import org.gnome.split.gtk.action.ActionManager.ActionId;
import org.gnome.split.gtk.dialog.ErrorDialog;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Manage the view update of the application. We *must not* set the engine
 * without using the {@link #setEngine(Engine) setEngine()} method.
 * 
 * @author Guillaume Mazoyer
 */
public class DefaultEngineListener implements EngineListener
{
    /**
     * The current instance of the application.
     */
    private GnomeSplit app;

    /**
     * Object to inhibit and uninhibit computer hibernation.
     */
    private DbusInhibit inhibit;

    /**
     * GTK+ interface of the application.
     */
    private MainWindow gtk;

    /**
     * The current engine (action).
     */
    private Engine engine;

    /**
     * A timer to schedule an action.
     */
    private Timer timer;

    /**
     * List of files that have been created.
     */
    private List<String> files;

    /**
     * Create a new implementation of the {@link EngineListener engine
     * listener}.
     */
    public DefaultEngineListener(final GnomeSplit app) {
        this.app = app;
        this.inhibit = null;
        this.gtk = app.getMainWindow();
        this.engine = null;
        this.timer = null;
        this.files = new ArrayList<String>();

        // Set the state of the interface
        this.engineReady();
    }

    @Override
    public void setEngine(Engine engine) {
        // Update engine
        this.engine = engine;

        if (engine != null) {
            // Inhibit hibernation if requested
            if (app.getConfig().NO_HIBERNATION) {
                inhibit = new DbusInhibit();
                inhibit.inhibit();
            }

            // Disable user interaction (only in action widget)
            gtk.getActionWidget().disable();
            gtk.getViewSwitcher().disable();

            // Update the status icon tooltip
            gtk.getAreaStatusIcon().updateText(engine.toString());

            // Update the interface state
            this.engineRunning();
        } else {
            // Uninhibit hibernation if requested
            if ((inhibit != null) && inhibit.isInhibited()) {
                inhibit.unInhibit();
                inhibit = null;
            }

            // Enable user interaction (only in action widget)
            gtk.getActionWidget().enable();
            gtk.getViewSwitcher().enable();

            // Reset the window's title
            gtk.setTitle(null);

            // Reset the status bar message
            gtk.getAreaStatusIcon().updateText(null);

            // Reset the status bar speed indicator
            gtk.getStatusWidget().updateSpeed(null);

            // Update the interface state
            this.engineReady();
        }
    }

    @Override
    public Engine getEngine() {
        return engine;
    }

    @Override
    public void engineSpeedChanged(String speed) {
        // Update the status widget
        gtk.getStatusWidget().updateSpeed(speed);
    }

    @Override
    public void enginePartCreated(String filename) {
        // Update the status widget
        gtk.getStatusWidget().updateText(_("Writing {0}.", filename));
    }

    @Override
    public void enginePartWritten(String filename) {

    }

    @Override
    public void enginePartRead(String filename) {
        // Update the status widget
        gtk.getStatusWidget().updateText(_("Reading {0}.", filename));
    }

    @Override
    public void engineMD5SumStarted() {
        if (timer == null) {
            // Start the timer
            timer = new Timer();
            timer.scheduleAtFixedRate(new PulseProgress(), 0, 250);

            // Update the status widget
            gtk.getStatusWidget().updateText(_("MD5 sum calculation."));
        }
    }

    @Override
    public void engineMD5SumEnded() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        // Now update the widgets
        gtk.getActionWidget().updateProgress(1, "", true);
    }

    @Override
    public void engineReady() {
        ActionManager actions = app.getActionManager();

        // Update the actions
        actions.getAction(ActionId.ASSISTANT).setSensitive(true);
        actions.getAction(ActionId.SEND_EMAIL).setSensitive(!files.isEmpty());
        actions.getAction(ActionId.START).setSensitive(true);
        actions.getAction(ActionId.PAUSE).setSensitive(false);
        actions.getAction(ActionId.CANCEL).setSensitive(false);
        actions.getAction(ActionId.DELETE).setSensitive(false);
        actions.getAction(ActionId.CLEAR).setSensitive(true);
        actions.getRadioAction(ActionId.SPLIT).setSensitive(true);
        actions.getRadioAction(ActionId.MERGE).setSensitive(true);

        // Update the cursor
        gtk.setCursorWorkingState(false);
    }

    @Override
    public void engineRunning() {
        ActionManager actions = app.getActionManager();

        // Update the actions
        actions.getAction(ActionId.ASSISTANT).setSensitive(false);
        actions.getAction(ActionId.SEND_EMAIL).setSensitive(false);
        actions.getAction(ActionId.START).setSensitive(false);
        actions.getAction(ActionId.PAUSE).setSensitive(true);
        actions.getAction(ActionId.CANCEL).setSensitive(true);
        actions.getAction(ActionId.DELETE).setSensitive(true);
        actions.getAction(ActionId.CLEAR).setSensitive(false);
        actions.getRadioAction(ActionId.SPLIT).setSensitive(false);
        actions.getRadioAction(ActionId.MERGE).setSensitive(false);

        // Update the cursor
        gtk.setCursorWorkingState(true);
    }

    @Override
    public void engineSuspended() {
        ActionManager actions = app.getActionManager();

        // Update the actions
        actions.getAction(ActionId.ASSISTANT).setSensitive(false);
        actions.getAction(ActionId.SEND_EMAIL).setSensitive(false);
        actions.getAction(ActionId.START).setSensitive(true);
        actions.getAction(ActionId.PAUSE).setSensitive(false);
        actions.getAction(ActionId.CANCEL).setSensitive(true);
        actions.getAction(ActionId.DELETE).setSensitive(true);
        actions.getAction(ActionId.CLEAR).setSensitive(false);
        actions.getRadioAction(ActionId.SPLIT).setSensitive(false);
        actions.getRadioAction(ActionId.MERGE).setSensitive(false);

        // Update the cursor
        gtk.setCursorWorkingState(false);
    }

    @Override
    public void engineEnded() {
        // Title and body of the message to display
        String title;
        String body;
        if (engine instanceof DefaultSplitEngine) {
            title = _("Split finished.");
            body = _("The file was succesfully split.");
        } else {
            title = _("Merge finished.");
            body = _("The files were successfully merged.");
        }

        // Update the status widget
        gtk.getStatusWidget().updateText(title);
        gtk.getStatusWidget().scheduleTimeout(5);

        if (!app.getConfig().USE_NOTIFICATION) {
            // Use simple info bar
            gtk.getInfoBar().showInfo(title, body);
        } else {
            // Use notification
            Notification notify = new Notification(title, body, null);
            notify.setIcon(Constants.PROGRAM_LOGO);
            notify.show();
        }

        // Update engine
        this.setEngine(null);
    }

    @Override
    public void engineStopped() {
        // Use the correct text
        String text;
        if (engine instanceof DefaultSplitEngine) {
            text = _("Split stopped.");
        } else {
            text = _("Merge stopped.");
        }

        // Update the status widget
        gtk.getStatusWidget().updateText(text);
        gtk.getStatusWidget().scheduleTimeout(5);

        // Update engine
        this.setEngine(null);
    }

    @Override
    public void engineError(Exception exception) {
        Dialog dialog = null;

        if (exception instanceof EngineException) {
            EngineException error = (EngineException) exception;
            ExceptionMessage message = error.getExceptionMessage();

            if (error.isWarning()) {
                // Warning only (file *may* work)
                gtk.getInfoBar().showWarning(message.getMessage(), message.getDetails());
            } else {
                // Invalid size exception
                dialog = new ErrorDialog(gtk, message.getMessage(), message.getDetails());
            }
        } else {
            // First print the stacktrace
            exception.printStackTrace();

            // Other exception - error (file is supposed broken)
            dialog = new ErrorDialog(
                    gtk,
                    _("Unhandled exception."),
                    _("An exception occurs. You can report it to the developers and tell them how to reproduce it.\n\nSee the details for more information."),
                    exception);
        }

        // Update the status widget
        gtk.getStatusWidget().updateText(exception.getMessage());
        gtk.getStatusWidget().scheduleTimeout(8);

        if (dialog != null) {
            // Display the dialog
            dialog.run();
            dialog.hide();
        }

        // Update engine
        this.setEngine(null);
    }

    @Override
    public void engineDone(long done, long total) {
        // Format the sizes to display them in the widget
        String text = SizeUnit.formatSize(done) + " / " + SizeUnit.formatSize(total);
        double value = (double) done / (double) total;

        // Now update the widgets
        gtk.setTitle(String.valueOf((int) (value * 100)) + "%");
        gtk.getActionWidget().updateProgress(value, text, true);
    }

    @Override
    public void engineFilesList(List<String> list) {
        // First, clear the list
        files.clear();

        // Set action sensitiveness
        app.getActionManager().getAction(ActionId.SEND_EMAIL).setSensitive(list != null);

        if (list != null) {
            // Copy all elements from the first list to the new one
            files.addAll(list);
        }
    }

    /**
     * Get the list of files which have been created previously.
     */
    public List<String> getFilesList() {
        return files;
    }

    /**
     * A class to use with a timer to make the progress bar of the user
     * interface pulse.
     * 
     * @author Guillaume Mazoyer
     */
    private class PulseProgress extends TimerTask
    {
        @Override
        public void run() {
            gtk.getActionWidget().updateProgress(1, "", false);
        }
    }
}
