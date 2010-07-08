/*
 * StatusStyleAction.java
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
import org.gnome.split.gtk.widget.StatusWidget.StatusStyle;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to change the style of the statusbar. Only 2 styles are available.
 * 
 * @author Guillaume Mazoyer
 */
public final class StatusStyleAction extends ToggleAction
{
    public StatusStyleAction(final GnomeSplit app) {
        super(app, "style-statusbar-action", _("_Icons in statusbar"),
                (app.getConfig().STATUS_STYLE == 0));
    }

    @Override
    public void onToggled(org.gnome.gtk.ToggleAction source) {
        // Get the statusbar of the interface
        StatusWidget statusbar = this.getApplication().getMainWindow().getStatusWidget();

        // Set the status bar style
        statusbar.setStyle(source.getActive() ? StatusStyle.ICON : StatusStyle.TEXT);

        // Save config
        this.getApplication().getConfig().STATUS_STYLE = (byte) (source.getActive() ? 0 : 1);
        this.getApplication().getConfig().savePreferences();
    }
}
