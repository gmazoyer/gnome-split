/*
 * ViewToolbarAction.java
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

import org.gnome.gtk.HSeparator;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.widget.SelectView;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to hide and show the main window switcher.
 * 
 * @author Guillaume Mazoyer
 */
final class ViewSwitcherAction extends ToggleAction
{
    protected ViewSwitcherAction(final GnomeSplit app) {
        super(app, "view-switcher-action", _("_View switcher"), app.getConfig().SHOW_SWITCHER);
    }

    @Override
    public void onToggled(org.gnome.gtk.ToggleAction source) {
        // Get the switcher and the separator of the interface
        SelectView switcher = this.getApplication().getMainWindow().getViewSwitcher();
        HSeparator separator = this.getApplication().getMainWindow().getSeparator();

        if (source.getActive()) {
            // Show it
            switcher.showAll();
            separator.show();
        } else {
            // Hide it
            switcher.hide();
            separator.hide();

            // Resize the window if needed
            if (!this.getApplication().getConfig().CUSTOM_WINDOW_SIZE) {
                this.getApplication().getMainWindow().resize();
            }
        }

        // Save config
        this.getApplication().getConfig().SHOW_SWITCHER = source.getActive();
        this.getApplication().getConfig().savePreferences();
    }
}
