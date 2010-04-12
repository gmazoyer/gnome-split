/*
 * GnomeSplit.java
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
package org.gnome.split;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.freedesktop.bindings.Internationalization;
import org.gnome.glib.Glib;
import org.gnome.gtk.Gtk;
import org.gnome.notify.Notify;
import org.gnome.split.config.Configuration;
import org.gnome.split.config.Constants;
import org.gnome.split.core.EngineListener;
import org.gnome.split.core.utils.UncaughtExceptionLogger;
import org.gnome.split.gtk.DefaultEngineListener;
import org.gnome.split.gtk.MainWindow;
import org.gnome.split.gtk.action.ActionManager;
import org.gnome.split.gtk.dialog.ErrorDialog;
import org.gnome.split.gtk.dialog.QuestionDialog;
import org.gnome.unique.Application;
import org.gnome.unique.Command;
import org.gnome.unique.MessageData;
import org.gnome.unique.Response;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class contains the GNOME Split application entry point.
 * 
 * @author Guillaume Mazoyer
 */
public final class GnomeSplit
{
    /**
     * Used to check that only one instance of this application is running.
     */
    private Application application;

    /**
     * Configuration for the application.
     */
    private Configuration config;

    /**
     * Application actions manager.
     */
    private ActionManager actions;

    /**
     * Application main window.
     */
    private MainWindow window;

    /**
     * Engine listener to update the view.
     */
    private EngineListener engine;

    /**
     * Create an instance of the application.
     */
    public GnomeSplit(String[] args) {
        // Initialize uncaught exception handler
        new UncaughtExceptionLogger();

        // Load program name
        Glib.setProgramName(Constants.PROGRAM_NAME);

        // Load GTK
        Gtk.init(args);

        try {
            // Load constants and preferences
            Constants.load();
            config = new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Initialize unique application check
        application = new Application("org.gnome.GnomeSplit", null);

        // Signal to handle message from other instances
        application.connect(new Application.MessageReceived() {
            @Override
            public Response onMessageReceived(Application source, Command cmd, MessageData data, int time) {
                ErrorDialog dialog = new ErrorDialog(
                        getMainWindow(),
                        _("More than one instance."),
                        _("Only one instance of GNOME Split can be executed at a time. If you want to run multiple instances, edit the preferences. Remember that it is never safe to run more than one instance of GNOME Split."));
                dialog.run();
                dialog.hide();
                return Response.OK;
            }
        });

        // Already running, quit this application
        if (application.isRunning() && !config.MULTIPLE_INSTANCES) {
            // Send the message
            application.sendMessage(Command.CLOSE, new MessageData());

            // Quit the current app
            System.exit(1);
        }

        // Load logo
        Gtk.setDefaultIcon(Constants.PROGRAM_LOGO);

        // Load translations
        Internationalization.init("gnome-split", "share/locale/");

        // Load libnotify
        if (config.USE_NOTIFICATION) {
            Notify.init(Constants.PROGRAM_NAME);
        }

        // Load actions manager
        actions = new ActionManager(this);

        // Start the user interface
        window = new MainWindow(this);
        window.selectDefaultView();
        window.show();

        // Load engine listener
        engine = new DefaultEngineListener(this);

        // If there are some arguments
        if (args.length > 0) {
            if (args.length == 1) {
                CommandLineParser.useCommandLineFile(this, window, args[0]);
            } else {
                CommandLineParser.parseCommandLine(this, window, args);
            }
        }

        // Start GTK main loop (blocker method)
        Gtk.main();
    }

    /**
     * Return the configuration object of the app.
     */
    public Configuration getConfig() {
        return config;
    }

    /**
     * Return the actions manager of the app.
     */
    public ActionManager getActionManager() {
        return actions;
    }

    /**
     * Return the main window of the app.
     */
    public MainWindow getMainWindow() {
        return window;
    }

    /**
     * Return the engine listener of the app.
     */
    public EngineListener getEngineListener() {
        return engine;
    }

    /**
     * Open the the URI with the default program.
     */
    public void openURI(String uri) {
        try {
            Gtk.showURI(new URI(uri));
        } catch (URISyntaxException e) {
            // Can be dropped, should *never* happen
        }
    }

    /**
     * This will cause the program to be ended.
     */
    public void quit() {
        boolean quit = true;

        // An action is running
        if (!config.DO_NOT_ASK_QUIT && (engine.getEngine() != null)) {
            // Show a question to the user
            QuestionDialog dialog = new QuestionDialog(this, window, _("Quit GNOME Split."),
                    _("An action is currently being perfomed. Do you really want to quit GNOME Split?"));

            // Get his response and hide the dialog
            quit = dialog.response();
            dialog.hide();
        }

        // The user really wants to quit
        if (quit) {
            // Hide the window immediately
            window.hide();

            // Uninitialize the libnotify
            if (Notify.isInitialized()) {
                Notify.uninit();
            }

            // Quit the GTK main loop (cause the app end)
            Gtk.mainQuit();

            // Forcing Garbage Collector
            System.gc();

            // Ending program
            System.exit(0);
        }
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        new GnomeSplit(args);
    }
}
