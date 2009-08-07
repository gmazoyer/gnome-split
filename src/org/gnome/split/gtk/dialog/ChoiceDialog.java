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
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.CellRendererToggle;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnBoolean;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.Label;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.gtk.TextComboBox;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;
import org.gnome.gtk.VBox;
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
    private TextComboBox box;

    public DataColumnBoolean active;

    public DataColumnString action;

    private ListStore model;

    private TreeView treeview;

    public ChoiceDialog(final GnomeSplit app) {
        // Set main window and dialog title
        super(_("Action choice"), app.getMainWindow(), false);

        // Main container
        final VBox vbox = new VBox(false, 3);
        this.add(vbox);

        // Introduction label
        final Label label = new Label(_("Choose an action to perform."));
        vbox.packStart(label);

        // Build a simple treeview
        TreeViewColumn vertical = null;
        model = new ListStore(new DataColumn[] {
                active = new DataColumnBoolean(), action = new DataColumnString()
        });
        treeview = new TreeView(model);
        treeview.setHeadersVisible(false);
        vbox.add(treeview);

        vertical = treeview.appendColumn();
        CellRendererToggle toggle = new CellRendererToggle(vertical);
        toggle.setActive(active);
        toggle.setActivatable(true);
        toggle.setRadio(true);

        vertical = treeview.appendColumn();
        CellRendererText text = new CellRendererText(vertical);
        text.setText(action);

        TreeIter row;
        for (byte b = 0; b < 3; b++) {
            row = model.appendRow();
            switch (b) {
            case 0:
                model.setValue(row, active, true);
                model.setValue(row, action, _("Split"));
                break;
            case 1:
                model.setValue(row, active, false);
                model.setValue(row, action, _("Assembly"));
                break;
            case 2:
                model.setValue(row, active, false);
                model.setValue(row, action, _("Check"));
                break;
            }
        }

        // Build the text combo box
        box = new TextComboBox();
        box.appendText(_("Split"));
        box.appendText(_("Assembly"));
        box.appendText(_("Check"));
        box.setActive(0);
        vbox.add(box);

        // Add buttons
        this.addButton(Stock.CANCEL, ResponseType.CANCEL);
        this.addButton(Stock.OK, ResponseType.OK);

        // Connect delete signal
        this.connect((Window.DeleteEvent) this);

        // Show everything
        this.showAll();
    }

    /**
     * Get the text combo box of this dialog.
     * 
     * @return the text combo box object.
     */
    public TextComboBox getTextComboBox() {
        return box;
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.CANCEL);
        return false;
    }
}
