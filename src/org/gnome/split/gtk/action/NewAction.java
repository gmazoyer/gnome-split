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
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.dialog.AssemblyDialog;
import org.gnome.split.gtk.dialog.CheckDialog;
import org.gnome.split.gtk.dialog.ChoiceDialog;
import org.gnome.split.gtk.dialog.SplitDialog;

/**
 * Action to add a split/assembly/check to the current actions list.
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
        final GnomeSplit app = this.getApplication();
        final ChoiceDialog dialog = new ChoiceDialog(this.getApplication());
        final ResponseType response = dialog.run();

        final Dialog action;
        if (response == ResponseType.OK) {
            switch (dialog.getTextComboBox().getActive()) {
            case 0: // Go split
                action = new SplitDialog(app);
                action.run();
                action.hide();
                break;
            case 1: // Go assemble
                action = new AssemblyDialog(app);
                action.run();
                action.hide();
                break;
            case 2: // Go check
                action = new CheckDialog(app);
                action.run();
                action.hide();
                break;
            }

        }
        dialog.hide();
    }
}
