/*
 * MainWindowAction.java
 * 
 * Copyright (c) 2009-2011 Guillaume Mazoyer
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

import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.MainWindow;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to hide and show the main window.
 * 
 * @author Guillaume Mazoyer
 */
final class MainWindowAction extends ToggleAction
{
    protected MainWindowAction(final GnomeSplit app) {
        super(app, "main-window-action", _("Show the main _window"), true);
    }

    @Override
    public void onToggled(org.gnome.gtk.ToggleAction source) {
        // Get the main window
        MainWindow window = this.getApplication().getMainWindow();

        if (source.getActive()) {
            // Show the window
            window.show();
        } else {
            // Hide the window
            window.hide();
        }
    }
}
