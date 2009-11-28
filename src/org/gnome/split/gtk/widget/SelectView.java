/*
 * SelectView.java
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
package org.gnome.split.gtk.widget;

import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.RadioButton;
import org.gnome.gtk.RadioButtonGroup;
import org.gnome.gtk.Widget;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.action.ActionManager;
import org.gnome.split.gtk.action.ActionManager.ActionId;

import static org.freedesktop.bindings.Internationalization._;

/**
 * A {@link Widget} derived from {@link HBox} to handle views in the main
 * interface. It contains few {@link RadioButton RadioButtons}.
 * 
 * @author Guillaume Mazoyer
 */
public class SelectView extends HBox
{
    public SelectView(final GnomeSplit app) {
        super(false, 10);

        // Set width of the borders
        this.setBorderWidth(5);

        // Actions manager to retrieve select actions
        ActionManager manager = app.getActionManager();

        // Just a label to know what the buttons are used for
        final Label label = new Label(_("View:"));
        this.packStart(label, false, false, 0);

        // Buttons group
        final RadioButtonGroup group = new RadioButtonGroup();

        // Split action - switch to split view
        final RadioButton split = manager.getToggleAction(ActionId.SELECT_SPLIT)
                .createRadioButton(group);
        this.packStart(split, false, false, 0);

        // Merge action - switch to merge view
        final RadioButton merge = manager.getToggleAction(ActionId.SELECT_MERGE)
                .createRadioButton(group);
        this.packStart(merge, false, false, 0);
    }
}
