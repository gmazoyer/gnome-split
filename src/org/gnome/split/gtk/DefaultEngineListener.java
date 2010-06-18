/*
 * DefaultEngineListener.java
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
package org.gnome.split.gtk;

import java.util.Timer;
import java.util.TimerTask;

import org.gnome.gtk.Dialog;
import org.gnome.gtk.Stock;
import org.gnome.notify.Notification;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Constants;
import org.gnome.split.core.Engine;
import org.gnome.split.core.EngineListener;
import org.gnome.split.core.exception.EngineException;
import org.gnome.split.core.exception.ExceptionMessage;
import org.gnome.split.core.exception.InvalidSizeException;
import org.gnome.split.core.exception.MD5Exception;
import org.gnome.split.core.splitter.DefaultSplitEngine;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.dbus.DbusInhibit;
import org.gnome.split.gtk.action.ActionManager;
import org.gnome.split.gtk.action.ActionManager.ActionId;
import org.gnome.split.gtk.dialog.ErrorDialog;
import org.gnome.split.gtk.dialog.InfoDialog;
import org.gnome.split.gtk.dialog.WarningDialog;

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
     * Create a new implementation of the {@link EngineListener engine
     * listener}.
     */
    public DefaultEngineListener(final GnomeSplit app) {
        this.app = app;
        this.inhibit = null;
        this.gtk = app.getMainWindow();
        this.engine = null;
        this.timer = null;

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

            // Reset the status icon tooltip
            gtk.getAreaStatusIcon().updateText(null);

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
        gtk.getStatusWidget().update(Stock.REFRESH, _("Writting {0}.", filename));
    }

    @Override
    public void enginePartWritten(String filename) {

    }

    @Override
    public void enginePartRead(String filename) {
        // Update the status widget
        gtk.getStatusWidget().update(Stock.REFRESH, _("Reading {0}.", filename));
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

        actions.getAction(ActionId.DUMMY_ASSISTANT).setSensitive(true);
        actions.getAction(ActionId.ASSISTANT).setSensitive(true);
        actions.getAction(ActionId.OPEN_DIR).setSensitive(false);
        actions.getAction(ActionId.START).setSensitive(true);
        actions.getAction(ActionId.PAUSE).setSensitive(false);
        actions.getAction(ActionId.CANCEL).setSensitive(false);
        actions.getAction(ActionId.DELETE).setSensitive(false);
        actions.getAction(ActionId.CLEAR).setSensitive(true);
        actions.getRadioAction(ActionId.SPLIT).setSensitive(true);
        actions.getRadioAction(ActionId.MERGE).setSensitive(true);
    }

    @Override
    public void engineRunning() {
        ActionManager actions = app.getActionManager();

        actions.getAction(ActionId.DUMMY_ASSISTANT).setSensitive(false);
        actions.getAction(ActionId.ASSISTANT).setSensitive(false);
        actions.getAction(ActionId.OPEN_DIR).setSensitive(true);
        actions.getAction(ActionId.START).setSensitive(false);
        actions.getAction(ActionId.PAUSE).setSensitive(true);
        actions.getAction(ActionId.CANCEL).setSensitive(true);
        actions.getAction(ActionId.DELETE).setSensitive(true);
        actions.getAction(ActionId.CLEAR).setSensitive(false);
        actions.getRadioAction(ActionId.SPLIT).setSensitive(false);
        actions.getRadioAction(ActionId.MERGE).setSensitive(false);
    }

    @Override
    public void engineSuspended() {
        ActionManager actions = app.getActionManager();

        actions.getAction(ActionId.DUMMY_ASSISTANT).setSensitive(false);
        actions.getAction(ActionId.ASSISTANT).setSensitive(false);
        actions.getAction(ActionId.OPEN_DIR).setSensitive(true);
        actions.getAction(ActionId.START).setSensitive(true);
        actions.getAction(ActionId.PAUSE).setSensitive(false);
        actions.getAction(ActionId.CANCEL).setSensitive(true);
        actions.getAction(ActionId.DELETE).setSensitive(true);
        actions.getAction(ActionId.CLEAR).setSensitive(false);
        actions.getRadioAction(ActionId.SPLIT).setSensitive(false);
        actions.getRadioAction(ActionId.MERGE).setSensitive(false);
    }

    @Override
    public void engineEnded() {
        // Title and body of the message to display
        String title;
        String body;
        if (engine instanceof DefaultSplitEngine) {
            title = _("Split finished.");
            body = _("The file was succesfully splitted.");
        } else {
            title = _("Merge finished.");
            body = _("The files were successfully merged.");
        }

        // Update the status widget
        gtk.getStatusWidget().update(Stock.YES, title, null);

        if (app.getConfig().USE_NOTIFICATION) {
            // Use notification
            Notification notify = new Notification(title, body, null, gtk.getAreaStatusIcon());
            notify.setIcon(Constants.PROGRAM_LOGO);
            notify.show();
        } else {
            // Use simple dialog
            Dialog dialog = new InfoDialog(gtk, title, body);
            dialog.run();
            dialog.hide();
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
        gtk.getStatusWidget().update(Stock.CANCEL, text, null);

        // Update engine
        this.setEngine(null);
    }

    @Override
    public void engineError(EngineException exception) {
        Stock item;
        Dialog dialog;

        // First print the stacktrace
        exception.printStackTrace();

        if (exception instanceof MD5Exception) {
            // MD5 exception - warning only (file *may* work)
            item = Stock.DIALOG_WARNING;
            dialog = new WarningDialog(gtk, exception.getExceptionMessage().getDetails());
        } else if (exception instanceof InvalidSizeException) {
            // Invalid size exception
            ExceptionMessage message = exception.getExceptionMessage();
            item = Stock.DIALOG_ERROR;
            dialog = new ErrorDialog(gtk, message.getMessage(), message.getDetails());
        } else {
            // Other exception - error (file is supposed broken)
            item = Stock.DIALOG_ERROR;
            dialog = new ErrorDialog(
                    gtk,
                    _("Unhandled exception."),
                    _("An exception occurs. You can report it to the developers and tell them how to reproduce it.\n\nSee the details for more information."),
                    exception);
        }

        // Update the status widget
        gtk.getStatusWidget().update(item, exception.getMessage(), null);

        // Display the dialog
        dialog.run();
        dialog.hide();

        // Update engine
        this.setEngine(null);
    }

    @Override
    public void engineDone(long done, long total) {
        // Format the sizes to display them in the widget
        String text = SizeUnit.formatSize(done) + " / " + SizeUnit.formatSize(total);
        double value = done / total;

        // Now update the widgets
        gtk.getActionWidget().updateProgress(value, text, true);
    }

    /**
     * A class to use with a timer to make the progress bar of the user
     * interface pulse.
     * 
     * @author Guillaume Mazoyer
     */
    class PulseProgress extends TimerTask
    {
        @Override
        public void run() {
            gtk.getActionWidget().updateProgress(1, "", false);
        }
    }
}
