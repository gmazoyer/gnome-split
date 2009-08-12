/*
 * ChoiceDialog.java
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
package org.gnome.split.gtk.dialog;

import org.gnome.gdk.Event;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.Label;
import org.gnome.gtk.RadioButton;
import org.gnome.gtk.RadioButtonGroup;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.gtk.ToggleButton;
import org.gnome.gtk.VBox;
import org.gnome.gtk.VButtonBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.split.GnomeSplit;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Dialog to make the user choose the action to perform.
 * 
 * <p>
 * This will not perform the action. This will actually open a second dialog
 * to choose more precise properties.
 * 
 * @author Guillaume Mazoyer
 */
public class ChoiceDialog extends Dialog implements Window.DeleteEvent
{
    private byte choice;

    public ChoiceDialog(final GnomeSplit app) {
        // Set main window and dialog title
        super(_("Action choice"), app.getMainWindow(), false);

        // Main container
        final VBox vbox = new VBox(false, 3);
        this.add(vbox);

        // Introduction label
        final Label label = new Label(_("Choose an action to perform."));
        vbox.packStart(label);

        // Buttons container
        final VButtonBox buttons = new VButtonBox();
        vbox.add(buttons);

        // Buttons group
        final RadioButtonGroup group = new RadioButtonGroup();

        // Split choice
        final RadioButton split = new RadioButton(group, _("Split"));
        split.setActive(true);
        buttons.add(split);
        split.connect(new ToggleButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                if (source.getActive())
                    choice = 0;
            }
        });

        // Assembly choice
        final RadioButton assembly = new RadioButton(group, _("Assembly"));
        assembly.setActive(false);
        buttons.add(assembly);
        assembly.connect(new ToggleButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                if (source.getActive())
                    choice = 1;
            }
        });

        // Check choice
        final RadioButton check = new RadioButton(group, _("Check"));
        buttons.add(check);
        check.setActive(false);
        check.connect(new ToggleButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                if (source.getActive())
                    choice = 2;
            }
        });

        // Add buttons
        this.addButton(Stock.CANCEL, ResponseType.CANCEL);
        this.addButton(Stock.OK, ResponseType.OK);

        // Connect delete signal
        this.connect((Window.DeleteEvent) this);

        // Show everything
        this.showAll();
    }

    /**
     * Get the choice the user made.
     * 
     * @return the user choice.
     */
    public byte getChoice() {
        return choice;
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.CANCEL);
        return false;
    }
}
