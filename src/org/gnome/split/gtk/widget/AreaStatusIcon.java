/*
 * AreaStatusIcon.java
 * 
 * Copyright (c) 2009-2013 Guillaume Mazoyer
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
package org.gnome.split.gtk.widget;

import static org.freedesktop.bindings.Internationalization._;
import static org.gnome.split.GnomeSplit.actions;
import static org.gnome.split.GnomeSplit.config;

import org.gnome.gtk.Menu;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.StatusIcon;
import org.gnome.split.config.Constants;
import org.gnome.split.gtk.action.ActionManager.ActionId;

/**
 * This class is used to build GTK+ notification icon.
 * 
 * @author Guillaume Mazoyer
 */
public class AreaStatusIcon extends StatusIcon implements StatusIcon.Activate, StatusIcon.PopupMenu
{
    /**
     * Menu attached to this status icon.
     */
    private Menu menu;

    /**
     * Build the tray icon to show it into the notification zone.
     */
    public AreaStatusIcon() {
        super(Constants.PROGRAM_LOGO);

        // Create icon menu
        this.createIconMenu();

        // Set up visibility and icon tooltip
        this.setVisible(config.SHOW_STATUS_ICON);
        this.updateText(null);

        // Connect interaction signals
        this.connect((StatusIcon.Activate) this);
        this.connect((StatusIcon.PopupMenu) this);
    }

    /**
     * Create the tray icon menu.
     */
    private void createIconMenu() {
        menu = new Menu();

        menu.append(actions.getToggleAction(ActionId.TRAY_WINDOW).createMenuItem());
        menu.append(new SeparatorMenuItem());
        menu.append(actions.getAction(ActionId.START).createMenuItem());
        menu.append(actions.getAction(ActionId.PAUSE).createMenuItem());
        menu.append(new SeparatorMenuItem());
        menu.append(actions.getAction(ActionId.ABOUT).createMenuItem());
        menu.append(actions.getAction(ActionId.EXIT).createMenuItem());
        menu.showAll();
    }

    @Override
    public void onActivate(StatusIcon source) {
        actions.activateToggleAction(ActionId.TRAY_WINDOW);
    }

    @Override
    public void onPopupMenu(StatusIcon source, int button, int activateTime) {
        menu.popup(source);
    }

    /**
     * Update the tooltip of the status icon. A string showing the program
     * name and version will always be present.
     */
    public void updateText(String text) {
        StringBuilder builder = new StringBuilder();

        // Constant text - always here
        builder.append(Constants.PROGRAM_NAME);
        builder.append(" - ");
        builder.append(_("version"));
        builder.append(" ");
        builder.append(Constants.PROGRAM_VERSION);

        // Text to append
        if (text != null) {
            builder.append("\n");
            builder.append(text);
        }

        // Update the icon tooltip
        this.setTooltipText(builder.toString());
    }
}
