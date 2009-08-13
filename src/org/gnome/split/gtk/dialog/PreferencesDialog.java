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
import org.gnome.gtk.Dialog;
import org.gnome.gtk.Label;
import org.gnome.gtk.Notebook;
import org.gnome.gtk.PositionType;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.gtk.VButtonBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.Dialog.Response;
import org.gnome.gtk.Window.DeleteEvent;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Configuration;

import static org.freedesktop.bindings.Internationalization._;

public class PreferencesDialog extends Dialog implements DeleteEvent, Response
{
    private final Configuration config;
    
    private GnomeSplit app;

    private CheckButton hibernation;

    private CheckButton trayIcon;

    private CheckButton notification;

    public PreferencesDialog(final GnomeSplit app) {
        super(_("GNOME Split Preferences"), app.getMainWindow(), true);

        // Get configuration
        this.config = app.getConfig();
        this.app = app;

        // Notebook to classify options
        final Notebook notebook = new Notebook();
        notebook.setTabPosition(PositionType.TOP);
        this.add(notebook);

        // Add page to the notebook
        notebook.appendPage(this.createDesktopTab(), new Label(_("Desktop")));

        // Close button (save the configuration and close)
        this.addButton(Stock.CLOSE, ResponseType.CLOSE);

        // Connect classic signals
        this.connect((Window.DeleteEvent) this);
        this.connect((Dialog.Response) this);
    }
    
    private Alignment createDesktopTab() {
        final Alignment desktopTab = new Alignment(0.0f, 0.0f, 0.0f, 0.0f);
        desktopTab.setPadding(5, 5, 20, 5);

        // Restore hibernation status
        hibernation = new CheckButton(_("Inhibit desktop _hibernation when action is performed"));
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
        trayIcon = new CheckButton(_("Show _icon in the desktop notification area"));
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
        notification = new CheckButton(_("Show desktop _notification"));
        notification.setActive(config.USE_NOTIFICATION);
        notification.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.USE_NOTIFICATION = notification.getActive();
                config.savePreferences();
            }
        });

        // Pack buttons in the box
        final VButtonBox vbox = new VButtonBox();
        desktopTab.add(vbox);

        // Add every options
        vbox.add(hibernation);
        vbox.add(trayIcon);
        vbox.add(notification);
        
        return desktopTab;
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
