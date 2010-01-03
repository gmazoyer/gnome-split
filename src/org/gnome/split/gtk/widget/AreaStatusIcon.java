/*
 * AreaStatusIcon.java
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
package org.gnome.split.gtk.widget;

import org.gnome.gtk.Menu;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.StatusIcon;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Constants;
import org.gnome.split.gtk.action.ActionManager;
import org.gnome.split.gtk.action.ToggleAction;
import org.gnome.split.gtk.action.ActionManager.ActionId;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to build GTK+ notification icon.
 * 
 * @author Guillaume Mazoyer
 */
public class AreaStatusIcon extends StatusIcon implements StatusIcon.Activate, StatusIcon.PopupMenu
{
    /**
     * The GNOME Split application.
     */
    private GnomeSplit app;

    /**
     * State of the tray icon.
     */
    private boolean activated;

    /**
     * Menu attached to this status icon.
     */
    private Menu menu;

    /**
     * Build the tray icon to show it into the notification zone.
     */
    public AreaStatusIcon(final GnomeSplit app) {
        super(Constants.PROGRAM_LOGO);

        this.app = app;
        this.activated = false;

        // Create icon menu
        this.createIconMenu();

        // Set up visibility and icon tooltip
        this.setTooltip("GNOME Split - " + _("version") + " " + Constants.PROGRAM_VERSION);
        this.setVisible(app.getConfig().SHOW_STATUS_ICON);

        // Connect interaction signals
        this.connect((StatusIcon.Activate) this);
        this.connect((StatusIcon.PopupMenu) this);
    }

    /**
     * Create the tray icon menu.
     */
    private void createIconMenu() {
        ActionManager actions = app.getActionManager();
        menu = new Menu();

        menu.append(actions.getToggleAction(ActionId.TRAY_WINDOW).createCheckMenuItem());
        menu.append(new SeparatorMenuItem());
        menu.append(actions.getAction(ActionId.ABOUT).createMenuItem());
        menu.append(actions.getAction(ActionId.EXIT).createMenuItem());
    }

    @Override
    public void onActivate(StatusIcon source) {
        ToggleAction action = app.getActionManager().getToggleAction(ActionId.TRAY_WINDOW);
        action.actionPerformed(null, activated);
    }

    @Override
    public void onPopupMenu(StatusIcon source, int button, int activateTime) {
        menu.popup();
        menu.showAll();
    }

    /**
     * Set the current activated state.
     */
    public void setActivated(boolean setting) {
        activated = setting;
    }
}
