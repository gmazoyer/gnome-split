/*
 * DefaultEngineListener.java
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
package org.gnome.split.gtk;

import org.gnome.gtk.Dialog;
import org.gnome.gtk.Stock;
import org.gnome.notify.Notification;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Constants;
import org.gnome.split.core.Engine;
import org.gnome.split.core.EngineException;
import org.gnome.split.core.EngineListener;
import org.gnome.split.core.splitter.DefaultSplitEngine;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.dbus.DbusInhibit;
import org.gnome.split.gtk.dialog.InfoDialog;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Manage the view update of the application.
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
     * GTK+ interface of the application (<code>null</code> if the command
     * line interface is used).
     */
    private MainWindow gtk;

    /**
     * The current engine (action).
     */
    private Engine engine;

    /**
     * Create a new implementation of the {@link EngineListener engine
     * listener}.
     */
    public DefaultEngineListener(final GnomeSplit app) {
        this.app = app;
        this.inhibit = new DbusInhibit();
        this.gtk = app.getMainWindow();
        this.engine = null;
    }

    @Override
    public void engineDone(double done, double total) {
        // Format the sizes to display them in the widget
        double divider = SizeUnit.getSizeDivider(total);
        String text = SizeUnit.formatSize(done, divider) + " / " + SizeUnit.formatSize(total, divider);

        // Now update the widget
        gtk.getActionWidget().updateProgress((done / total), text);
    }

    @Override
    public void engineEnded() {
        // Update engine
        engine = null;

        // Enable user interaction (only in action widget)
        gtk.getActionWidget().enable();

        // Title and body of the message to display
        String title;
        String body;
        if (engine instanceof DefaultSplitEngine) {
            title = _("Split terminated.");
            body = _("The split has been terminated successfully without any errors.");
        } else {
            title = _("Merge terminated.");
            body = _("The merge has been terminated successfully without any errors.");
        }

        // Update the status widget
        gtk.getStatusWidget().update(Stock.YES, title);

        if (app.getConfig().USE_NOTIFICATION) {
            // Use notification
            Notification notify = new Notification(title, body, null, gtk.getTrayIcon());
            notify.setIcon(Constants.PROGRAM_LOGO);
            notify.show();
        } else {
            // Use simple dialog
            Dialog dialog = new InfoDialog(gtk, title, body);
            dialog.run();
            dialog.hide();
        }

        // Update the interface state
        app.getActionManager().setReadyState();

        // Finally, uninhibit computer hibernation if needed
        if (inhibit.hasInhibit()) {
            inhibit.unInhibit();
        }
    }

    @Override
    public void engineError(EngineException exception) {
        // Update engine
        engine = null;

        // Enable user interaction (only in action widget)
        gtk.getActionWidget().enable();

        // Update the status widget
        gtk.getStatusWidget().update(Stock.DIALOG_ERROR, exception.getMessage());

        // Finally, uninhibit computer hibernation if needed
        if (inhibit.hasInhibit()) {
            inhibit.unInhibit();
        }
    }

    @Override
    public void enginePartCreated(String filename) {
        gtk.getStatusWidget().update(Stock.REFRESH, _("Writting {0}.", filename));
    }

    @Override
    public void enginePartRead(String filename) {
        gtk.getStatusWidget().update(Stock.REFRESH, _("Reading {0}.", filename));
    }

    @Override
    public void setEngine(Engine engine) {
        // Update engine
        this.engine = engine;

        // Disable user interaction (only in action widget)
        gtk.getActionWidget().disable();

        // Inhibit hibernation if requested
        if (app.getConfig().NO_HIBERNATION) {
            inhibit.inhibit();
        }
    }

    @Override
    public Engine getEngine() {
        return engine;
    }
}
