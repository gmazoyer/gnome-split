/*
 * AssemblyDialog.java
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

import java.io.File;
import java.util.ArrayList;

import org.gnome.gdk.Event;
import org.gnome.gdk.EventButton;
import org.gnome.gdk.MouseButton;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.Entry;
import org.gnome.gtk.EntryIconPosition;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.FileChooserDialog;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.Label;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.Stock;
import org.gnome.gtk.Table;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreeSelection;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.Dialog.Response;
import org.gnome.gtk.Window.DeleteEvent;
import org.gnome.split.GnomeSplit;
import org.gnome.split.io.FileAssembly;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Dialog which will be used to set the properties of the future assembly
 * action to do.
 * 
 * @author Guillaume Mazoyer
 */
public class AssemblyDialog extends Dialog implements DeleteEvent, Response
{
    /**
     * The GNOME Split application.
     */
    private GnomeSplit app;

    /**
     * Entry which will contain the name of the file to create.
     */
    private Entry output;

    /**
     * Directory where the file will be created.
     */
    private FileChooserButton directory;

    /**
     * List of the files to put together.
     */
    private TreeView inputs;

    /**
     * TreeModel used by the files list.
     */
    private ListStore store;

    /**
     * Column to display filenames in the list.
     */
    private DataColumnString file;

    public AssemblyDialog(final GnomeSplit app) {
        // Set main window and dialog title
        super(_("New assembly"), app.getMainWindow(), false);

        // Save GNOME Split instance
        this.app = app;

        // Let's have a little border
        this.setBorderWidth(5);

        // Table (used to place widgets)
        final Table table = new Table(7, 3, false);
        table.setColumnSpacing(5);
        table.setRowSpacing(5);
        this.add(table);

        // Output file label
        final Label outputLabel = new Label(_("Assembled file"));
        table.attach(outputLabel, 0, 1, 0, 1);

        // Output directory label
        final Label directoryLabel = new Label(_("Directory"));
        table.attach(directoryLabel, 0, 1, 1, 2);

        // Entry output file
        output = new Entry();
        table.attach(output, 1, 3, 0, 1);

        // Clear entry
        output.setIconFromStock(EntryIconPosition.SECONDARY, Stock.CLEAR);
        output.connect(new Entry.IconPress() {
            @Override
            public void onIconPress(Entry source, EntryIconPosition position, Event event) {
                source.setText("");
            }
        });

        // Directory chooser
        directory = new FileChooserButton(_("Choose a directory"), FileChooserAction.SELECT_FOLDER);
        directory.setCurrentFolder(System.getProperty("user.home"));
        table.attach(directory, 1, 3, 1, 2);

        // Inputs label
        final Label inputsLabel = new Label(_("Files to assemble"));
        table.attach(inputsLabel, 0, 3, 2, 3);

        // Widget to make user be able to scroll in the view
        final ScrolledWindow scroll = new ScrolledWindow();
        scroll.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
        table.attach(scroll, 0, 3, 3, 7);

        // Create the TreeModel
        store = new ListStore(new DataColumn[] {
            file = new DataColumnString()
        });

        // Create the view using the prebuilt model
        inputs = new TreeView(store);
        inputs.setHeadersVisible(false);
        inputs.setSizeRequest(200, 150);
        scroll.add(inputs);

        // Create the column
        final TreeViewColumn vertical = inputs.appendColumn();
        vertical.setTitle(_("Filename"));

        // Create the cell renderer
        CellRendererText text = new CellRendererText(vertical);
        text.setText(file);

        inputs.connect(new Widget.ButtonPressEvent() {
            @Override
            public boolean onButtonPressEvent(Widget source, EventButton event) {
                // Not a right click
                if (event.getButton() != MouseButton.RIGHT)
                    return false;

                // Create and popup the menu
                final Menu popup = popupInputsMenu();
                popup.showAll();
                popup.popup();

                return false;
            }
        });

        // Add buttons to the dialog
        this.addButton(Stock.CANCEL, ResponseType.CANCEL);
        this.addButton(Stock.NEW, ResponseType.OK);

        // Connect signals
        this.connect((Window.DeleteEvent) this);
        this.connect((Dialog.Response) this);

        // Show everything
        this.showAll();
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.CANCEL);
        return false;
    }

    @Override
    public void onResponse(Dialog source, ResponseType response) {
        if (response == ResponseType.OK) {
            Dialog dialog = null;

            // Missing informations
            if (output.getText().isEmpty() || (store.getIterFirst() == null))
                dialog = new ErrorDialog(this, _("Uncompleted fields."),
                        _("Check that all fields are completed."));

            // Display the error dialog
            if (dialog != null) {
                dialog.run();
                dialog.hide();

                // Rerun the parent dialog to make the user able to correct
                // the informations
                this.run();
                return;
            }

            // Get all needed values to start an assembly
            final String[] filenames = this.getInputsAsArray();
            final String destination = directory.getCurrentFolder();
            final String filename = destination + File.separator + output.getText();

            // Create the assembly action
            final FileAssembly assembly = new FileAssembly(app, filename, filenames);

            // Add listeners to update the interface
            assembly.addProgressListener(app.getMainWindow().getActionsList());
            assembly.addStatusListener(app.getMainWindow().getActionsList());

            // Add the operation into the manager
            app.getOperationManager().add(assembly);

            // A thread will take care of the execution
            app.getOperationManager().start(assembly);
        }
    }

    private String[] getInputsAsArray() {
        // Create an array which will contain filenames
        final ArrayList<String> filenames = new ArrayList<String>();
        final TreeIter row = store.getIterFirst();

        do {
            // Get all values
            final String value = store.getValue(row, file);
            filenames.add(value);
        } while (row.iterNext());

        // Return a simple array
        return (String[]) filenames.toArray(new String[filenames.size()]);
    }

    private void changeRowPosition(boolean up) {
        // Get the current selection
        final TreeSelection selection = inputs.getSelection();

        // Nothing selected
        if (selection == null)
            return;

        // Get selected row
        final TreeIter row = selection.getSelected();

        // Get the first row
        final TreeIter tmp = store.getIterFirst();

        // Position of the row and total number of rows
        int position = 0;
        int total = 0;

        // Nothing selected or tree empty
        if ((row == null) || (tmp == null))
            return;

        // Get the selected row value
        final String value = store.getValue(row, file);

        do {
            final String current = store.getValue(tmp, file);

            // Find row position
            if (value.equals(current))
                position = total;

            // Increment total number of rows
            total++;
        } while (tmp.iterNext());

        // Row cannot be moved
        if ((up && (position == 0)) || (!up && (position + 1) == total))
            return;

        // Remove the current row
        store.removeRow(row);

        // Make a new one and add it
        final TreeIter newRow = up ? store.insertRow(position - 1) : store.insertRow(position + 1);
        store.setValue(newRow, file, value);
    }

    private boolean alreadyAdded(String filename) {
        // Get the first row
        final TreeIter row = store.getIterFirst();
        String value = null;

        // View is empty
        if (row == null)
            return false;

        do {
            // Get the current row value
            value = store.getValue(row, file);

            // Row already added
            if (filename.equals(value))
                return true;
        } while (row.iterNext());

        // Row not in the current view
        return false;
    }

    private Menu popupInputsMenu() {
        // All menu items
        final ImageMenuItem add = new ImageMenuItem(Stock.ADD);
        final ImageMenuItem remove = new ImageMenuItem(Stock.REMOVE);
        final ImageMenuItem clear = new ImageMenuItem(Stock.CLEAR);
        final ImageMenuItem up = new ImageMenuItem(Stock.GO_UP);
        final ImageMenuItem down = new ImageMenuItem(Stock.GO_DOWN);

        // Create the menu
        final Menu menu = new Menu();

        menu.append(add);
        add.connect(new MenuItem.Activate() {
            @Override
            public void onActivate(MenuItem source) {
                // Open a file chooser dialog
                final FileChooserDialog chooser = new FileChooserDialog(_("Choose a file."),
                        AssemblyDialog.this, FileChooserAction.OPEN);
                final ResponseType response = chooser.run();
                chooser.hide();

                // File is selected
                if (response == ResponseType.OK) {
                    final String filename = chooser.getFilename();

                    // Try to add it
                    if (!alreadyAdded(filename)) {
                        final TreeIter row = store.appendRow();
                        store.setValue(row, file, filename);
                    }
                }
            }
        });

        menu.add(remove);
        remove.connect(new MenuItem.Activate() {
            @Override
            public void onActivate(MenuItem source) {
                // Get treeview selection
                final TreeSelection selection = inputs.getSelection();

                if (selection != null) {
                    // Get the corresponding row
                    final TreeIter row = selection.getSelected();
                    if (row != null)
                        // Remove the row
                        store.removeRow(row);
                }
            }
        });

        menu.add(clear);
        clear.connect(new MenuItem.Activate() {
            @Override
            public void onActivate(MenuItem source) {
                store.clear();
            }
        });

        // Add a little separator to make a difference between to type of
        // action
        menu.add(new SeparatorMenuItem());

        menu.add(up);
        up.connect(new MenuItem.Activate() {
            @Override
            public void onActivate(MenuItem source) {
                changeRowPosition(true);
            }
        });

        menu.add(down);
        down.connect(new MenuItem.Activate() {
            @Override
            public void onActivate(MenuItem source) {
                changeRowPosition(false);
            }
        });

        return menu;
    }
}
