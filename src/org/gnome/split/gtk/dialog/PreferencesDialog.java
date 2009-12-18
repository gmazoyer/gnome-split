/*
 * PreferencesDialog.java
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
package org.gnome.split.gtk.dialog;

import org.gnome.gdk.Event;
import org.gnome.gtk.Alignment;
import org.gnome.gtk.Button;
import org.gnome.gtk.CheckButton;
import org.gnome.gtk.ComboBox;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.Notebook;
import org.gnome.gtk.PositionType;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.gtk.TextComboBox;
import org.gnome.gtk.VBox;
import org.gnome.gtk.VButtonBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.Dialog.Response;
import org.gnome.gtk.Window.DeleteEvent;
import org.gnome.notify.Notify;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Configuration;
import org.gnome.split.config.Constants;
import org.gnome.split.core.utils.Algorithm;

import static org.freedesktop.bindings.Internationalization._;

public class PreferencesDialog extends Dialog implements DeleteEvent, Response
{
    /**
     * Configuration of the application.
     */
    private final Configuration config;

    /**
     * Current instance of the application.
     */
    private GnomeSplit app;

    /**
     * Button to know if we should calculate the MD5 sum.
     */
    private CheckButton md5sum;

    /**
     * Button to know if we should remove the parts files.
     */
    private CheckButton remove;

    /**
     * List to know what should be the default algorithm to use.
     */
    private TextComboBox algorithms;

    /**
     * Button to know if we should inhibit the hibernation during an action.
     */
    private CheckButton hibernation;

    /**
     * Button to know if we should display an icon in the notification area.
     */
    private CheckButton trayIcon;

    /**
     * Button to know if we should display notifications when an action
     * finished.
     */
    private CheckButton notification;

    /**
     * Button to know if we should have to allow multiples instances.
     */
    private CheckButton instances;

    public PreferencesDialog(final GnomeSplit app) {
        super(_("GNOME Split Preferences"), app.getMainWindow(), true);

        // Get configuration
        this.config = app.getConfig();
        this.app = app;

        // Notebook to classify options
        final Notebook notebook = new Notebook();
        notebook.setTabPosition(PositionType.TOP);
        this.add(notebook);

        // Add pages to the notebook
        notebook.appendPage(this.createGeneralPage(), new Label(_("General")));
        notebook.appendPage(this.createDesktopPage(), new Label(_("Desktop")));

        // Close button (save the configuration and close)
        this.addButton(Stock.CLOSE, ResponseType.CLOSE);

        // Connect classic signals
        this.connect((Window.DeleteEvent) this);
        this.connect((Dialog.Response) this);
    }

    /**
     * Create the first page of the {@link Notebook notebook} (general
     * config).
     */
    private Alignment createGeneralPage() {
        final Alignment page = new Alignment(0.0f, 0.0f, 0.0f, 0.0f);
        page.setPadding(5, 5, 20, 5);

        md5sum = new CheckButton(_("_Calculate the MD5 if possible."));
        md5sum.setActive(config.SAVE_FILE_HASH);
        md5sum.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.SAVE_FILE_HASH = md5sum.getActive();
                config.savePreferences();
            }
        });

        remove = new CheckButton(_("_Remove parts if the merge was successful."));
        remove.setActive(config.DELETE_PARTS);
        remove.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.DELETE_PARTS = remove.getActive();
                config.savePreferences();
            }
        });

        // Algorithm label
        final Label algoLabel = new Label(_("Default algorithm:"));

        // Algorithm list
        algorithms = new TextComboBox();
        for (String algorithm : Algorithm.toStrings()) {
            // Fill the list
            algorithms.appendText(algorithm);
        }

        // Set the default algorithm
        algorithms.setActive(config.DEFAULT_ALGORITHM);
        algorithms.connect(new ComboBox.Changed() {
            @Override
            public void onChanged(ComboBox source) {
                // Save preferences
                config.DEFAULT_ALGORITHM = algorithms.getActive();
                config.savePreferences();
            }
        });

        // Main container
        final VBox container = new VBox(false, 0);
        page.add(container);

        // Pack buttons in the box
        final VButtonBox vbox = new VButtonBox();
        container.packStart(vbox);

        // Add every options
        vbox.add(md5sum);
        vbox.add(remove);

        // Pack algorithms list
        final HBox hbox = new HBox(false, 3);
        container.packStart(hbox);

        hbox.packStart(algoLabel);
        hbox.packStart(algorithms);

        return page;
    }

    /**
     * Create the last page of the {@link Notebook notebook} (desktop config).
     */
    private Alignment createDesktopPage() {
        final Alignment page = new Alignment(0.0f, 0.0f, 0.0f, 0.0f);
        page.setPadding(5, 5, 20, 5);

        // Restore hibernation status
        hibernation = new CheckButton(_("Inhibit desktop _hibernation when action is performed."));
        hibernation.setActive(config.NO_HIBERNATION);
        hibernation.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.NO_HIBERNATION = hibernation.getActive();
                config.savePreferences();
            }
        });

        // Restore tray icon status
        trayIcon = new CheckButton(_("Show _icon in the desktop notification area."));
        trayIcon.setActive(config.SHOW_TRAY_ICON);
        trayIcon.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                boolean showTrayIcon = trayIcon.getActive();
                config.SHOW_TRAY_ICON = showTrayIcon;

                // Display icon and save preferences
                app.getMainWindow().getTrayIcon().setVisible(showTrayIcon);
                config.savePreferences();
            }
        });

        // Restore notifications status
        notification = new CheckButton(_("Show desktop _notification."));
        notification.setActive(config.USE_NOTIFICATION);
        notification.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.USE_NOTIFICATION = notification.getActive();
                config.savePreferences();

                if (config.USE_NOTIFICATION)
                    // Load libnotify
                    Notify.init(Constants.PROGRAM_NAME);
                else
                    // Unload libnotify
                    Notify.uninit();
            }
        });

        // Restore multiple instances status
        instances = new CheckButton(_("Allow multiple instances."));
        instances.setActive(config.MULTIPLE_INSTANCES);
        instances.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.MULTIPLE_INSTANCES = instances.getActive();
                config.savePreferences();
            }
        });

        // Pack buttons in the box
        final VButtonBox vbox = new VButtonBox();
        page.add(vbox);

        // Add every options
        vbox.add(hibernation);
        vbox.add(trayIcon);
        vbox.add(notification);
        vbox.add(instances);

        return page;
    }

    @Override
    public ResponseType run() {
        this.showAll();
        return super.run();
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.CANCEL);
        return false;
    }

    @Override
    public void onResponse(Dialog source, ResponseType response) {
        // Hide the dialog
        this.hide();
    }
}
