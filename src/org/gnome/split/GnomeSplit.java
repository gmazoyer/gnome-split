/*
 * GnomeSplit.java
 * 
 * Copyright (c) 2009-2012 Guillaume Mazoyer
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

import static java.lang.System.exit;
import static org.freedesktop.bindings.Internationalization._;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.freedesktop.bindings.Internationalization;
import org.gnome.glib.ApplicationCommandLine;
import org.gnome.glib.ApplicationFlags;
import org.gnome.glib.Glib;
import org.gnome.gtk.Application;
import org.gnome.gtk.Gtk;
import org.gnome.notify.Notify;
import org.gnome.split.config.Configuration;
import org.gnome.split.config.Constants;
import org.gnome.split.core.EngineListener;
import org.gnome.split.core.utils.ShutdownHandler;
import org.gnome.split.core.utils.UncaughtExceptionLogger;
import org.gnome.split.gtk.DefaultEngineListener;
import org.gnome.split.gtk.MainWindow;
import org.gnome.split.gtk.action.ActionManager;
import org.gnome.split.gtk.action.ActionManager.ActionId;
import org.gnome.split.gtk.dialog.QuestionDialog;

/**
 * This class contains the GNOME Split application entry point.
 * 
 * @author Guillaume Mazoyer
 */
public final class GnomeSplit
{
    /**
     * Application instance to run.
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

    private GnomeSplit() {
        /*
         * No instantiation from outside.
         */
    }

    /**
     * Load the configuration and preferences.
     */
    private void loadConfig() {
        try {
            // Load constants and preferences
            Constants.load();
            config = new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
        }
    }

    /**
     * Build the GTK+ user interface.
     */
    private void buildUserInterface() {
        // Load actions manager
        actions = new ActionManager(this);

        // Start the user interface
        window = new MainWindow(this);
        window.selectDefaultView();

        // Add the window to the underlining application model
        application.addWindow(window);

        // Load engine listener
        engine = new DefaultEngineListener(this);
    }

    /**
     * Change the view of the window, and update it using a filename. If no
     * filename is given (filename == null), the view will not be updated. 0
     * means split view and other means merge view.
     */
    private void selectView(byte view, String filename) {
        // Choose split
        if (view == 0) {
            if (filename != null) {
                // Update the merge widget
                window.getSplitWidget().setFile(filename);
            }

            // Show the merge widget
            actions.activateRadioAction(ActionId.SPLIT);
        } else {
            if (filename != null) {
                // Load the file to split
                window.getMergeWidget().setFile(filename);
            }

            // Show the split widget
            actions.activateRadioAction(ActionId.MERGE);
        }
    }

    private int run(String[] args) {
        int status;

        // Initialize uncaught exception handler
        new UncaughtExceptionLogger();

        // Start kill signals handler
        Runtime.getRuntime().addShutdownHook(new ShutdownHandler());

        // Load GTK
        Gtk.init(args);

        // Load config
        this.loadConfig();

        // Load program name
        Glib.setProgramName(Constants.PROGRAM_NAME);

        // Load logo
        Gtk.setDefaultIcon(Constants.PROGRAM_LOGO);

        // Load translations
        Internationalization.init("gnome-split", "share/locale/");

        // Load libnotify
        if (config.USE_NOTIFICATION) {
            Notify.init(Constants.PROGRAM_NAME);
        }

        // Build the application object
        application = new Application("org.gnome.split.GnomeSplit",
                ApplicationFlags.HANDLES_COMMAND_LINE);

        // Build the GUI on startup
        application.connect(new Application.Startup() {
            @Override
            public void onStartup(Application application) {
                // Build the user interface
                buildUserInterface();
            }
        });

        // Display the GUI when activated
        application.connect(new Application.Activate() {
            @Override
            public void onActivate(Application app) {
                window.show();

                // Show the assistant on start if requested
                actions.activateAction(ActionId.ASSISTANT);
            }
        });

        // Handle command line arguments
        application.connect(new Application.CommandLine() {
            @Override
            public int onCommandLine(Application app, ApplicationCommandLine remote) {
                String[] args = remote.getArguments();

                if (args.length > 1) {
                    // Change the view
                    selectView((byte) ((args[1].equals("-s") || args[1].equals("--split")) ? 0 : 1),
                            (args.length > 2) ? args[1] : null);
                }

                // Trigger the Application.Activate signal
                application.activate();

                // Indicate that the remote instance that sent us the command
                // line arguments should terminate as soon as possible
                remote.exit();

                return 0;
            }
        });

        // Fire the main loop (blocker)
        status = application.run(args);

        return status;
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
            // Should *never* happen
            e.printStackTrace();
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
                    _("An action is currently being performed. Do you really want to quit GNOME Split?"));

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

            // Quit the GTK application.
            application.removeWindow(window);
            application.quit();
        }
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        GnomeSplit application = new GnomeSplit();

        int status = application.run(args);

        exit(status);
    }
}
