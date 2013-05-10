/*
 * ViewToolbarAction.java
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
package org.gnome.split.gtk.action;

import static org.freedesktop.bindings.Internationalization._;
import static org.gnome.split.GnomeSplit.config;
import static org.gnome.split.GnomeSplit.ui;

import org.gnome.split.gtk.widget.MainToolbar;

/**
 * Action to hide and show the main window toolbar.
 * 
 * @author Guillaume Mazoyer
 */
final class ViewToolbarAction extends ToggleAction
{
    protected ViewToolbarAction() {
        super("view-toolbar-action", _("_Toolbar"), config.SHOW_TOOLBAR);
    }

    @Override
    public void onToggled(org.gnome.gtk.ToggleAction source) {
        // Get the toolbar of the interface
        MainToolbar toolbar = ui.getToolbar();

        if (source.getActive()) {
            // Show it
            toolbar.showAll();
        } else {
            // Hide it
            toolbar.hide();

            // Resize the window if needed
            if (!config.CUSTOM_WINDOW_SIZE) {
                ui.resize();
            }
        }

        // Save config
        config.SHOW_TOOLBAR = source.getActive();
        config.savePreferences();
    }
}
