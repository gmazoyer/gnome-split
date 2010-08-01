/*
 * ViewStatusbarAction.java
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
package org.gnome.split.gtk.action;

import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.widget.StatusWidget;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to hide and show the main window statusbar.
 * 
 * @author Guillaume Mazoyer
 */
public final class ViewStatusbarAction extends ToggleAction
{
    public ViewStatusbarAction(final GnomeSplit app) {
        super(app, "view-statusbar-action", _("_Statusbar"), app.getConfig().SHOW_STATUSBAR);
    }

    @Override
    public void onToggled(org.gnome.gtk.ToggleAction source) {
        // Get the statusbar of the interface
        StatusWidget statusbar = this.getApplication().getMainWindow().getStatusWidget();

        if (source.getActive()) {
            // Show it
            statusbar.show();
        } else {
            // Hide it
            statusbar.hide();

            // Resize the window if needed
            if (!this.getApplication().getConfig().CUSTOM_WINDOW_SIZE) {
                this.getApplication().getMainWindow().resize();
            }
        }

        // Save config
        this.getApplication().getConfig().SHOW_STATUSBAR = source.getActive();
        this.getApplication().getConfig().savePreferences();
    }
}
