/*
 * ViewSizeDetails.java
 * 
 * Copyright (c) 2012-2013 Guillaume Mazoyer
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

final class ViewSizeDetails extends ToggleAction
{
    protected ViewSizeDetails() {
        super("view-size-details-action", _("Size _details"), config.SHOW_SIZE_DETAILS);
    }

    @Override
    public void onToggled(org.gnome.gtk.ToggleAction source) {
        // Update the user interface
        ui.getProgressBar().showSizeDetails(source.getActive());
        ui.resize();

        // Save config
        config.SHOW_SIZE_DETAILS = source.getActive();
        config.savePreferences();
    }
}
