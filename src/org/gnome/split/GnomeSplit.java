/*
 * GnomeSplit.java
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
package org.gnome.split;

import java.net.URI;
import java.net.URISyntaxException;

import org.freedesktop.bindings.Internationalization;
import org.gnome.glib.Glib;
import org.gnome.gtk.Gtk;
import org.gnome.notify.Notify;
import org.gnome.split.config.Configuration;
import org.gnome.split.config.Constants;
import org.gnome.split.gtk.MainWindow;
import org.gnome.split.utils.UncaughtExceptionLogger;

/**
 * This class contains the GNOME Split application entry point.
 * 
 * @author Guillaume Mazoyer
 */
public final class GnomeSplit
{
    /**
     * Application main window.
     */
    public MainWindow window = null;

    /**
     * Create an instance of the application.
     * 
     * @param args
     *            the command line arguments.
     */
    public GnomeSplit(String[] args) {
        // Initialize uncaught exception handler
        new UncaughtExceptionLogger();

        // Load GTK
        Gtk.init(args);

        // Load constants and preferences
        Configuration.newInstance();

        // Load logo and program name
        Glib.setProgramName(Constants.PROGRAM_NAME);
        Gtk.setDefaultIcon(Constants.PROGRAM_LOGO);

        // Load translations
        Internationalization.init("gnome-split", "share/locale/");

        // Load libnotify
        if (Configuration.USE_NOTIFICATION)
            Notify.init(Constants.PROGRAM_NAME);

        // Start the user interface
        window = new MainWindow(this);
        window.showAll();

        // Start GTK main loop (blocker method)
        Gtk.main();

        // Forcing Garbage Collector
        System.gc();

        // Ending program
        System.exit(0);
    }

    /**
     * Return the main window of the app.
     * 
     * @return the application main window.
     */
    public MainWindow getMainWindow() {
        return window;
    }

    /**
     * Open the web browser to show the documentation.
     */
    public void openDocumentation() {
        try {
            Gtk.showURI(new URI("http://www.respawner.fr/projects/gnome-split/"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * This will cause the program to be ended.
     */
    public void quit() {
        // Hide the window immediately
        window.hide();

        // Quit the GTK main loop (cause the app end)
        Gtk.mainQuit();
    }

    /**
     * Application entry point.
     * 
     * @param args
     *            the arguments given in the CLI.
     */
    public static void main(String[] args) {
        new GnomeSplit(args);
    }
}
