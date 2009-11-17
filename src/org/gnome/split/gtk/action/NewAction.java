/*
 * NewAction.java
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
package org.gnome.split.gtk.action;

import org.gnome.gtk.Dialog;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.Stock;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.dialog.SplitDialog;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to add a split
 * 
 * @author Guillaume Mazoyer
 */
public final class NewAction extends Action
{
    public NewAction(final GnomeSplit app) {
        super(app, Stock.NEW);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Menu to popup
        final Menu menu = new Menu();

        // Item to display a new split dialog
        final ImageMenuItem split = new ImageMenuItem(new Image(Stock.CUT, IconSize.MENU), _("Split"));
        split.connect(new MenuItem.Activate() {
            @Override
            public void onActivate(MenuItem source) {
                // Display the dialog
                Dialog dialog = new SplitDialog(getApplication());
                dialog.run();
                dialog.hide();
            }
        });

        // Add the item to the menu
        menu.append(split);

        // Display everything and popup
        menu.showAll();
        menu.popup();
    }
}
