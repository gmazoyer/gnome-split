/*
 * SplitDialog.java
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

import org.gnome.gdk.Event;
import org.gnome.gtk.Alignment;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.Entry;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.Label;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.SpinButton;
import org.gnome.gtk.Stock;
import org.gnome.gtk.Table;
import org.gnome.gtk.TextComboBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.split.GnomeSplit;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Dialog which will be used to set the properties of the future split action
 * to do.
 * 
 * @author Guillaume Mazoyer
 */
public class SplitDialog extends Dialog implements Window.DeleteEvent, Dialog.Response
{
    /**
     * Entry which will contain the file to split path.
     */
    private Entry inputEntry;

    /**
     * Length of the future parts of the file.
     */
    private SpinButton size;

    /**
     * Entry which will contain the filename pattern.
     */
    private Entry patternEntry;

    /**
     * List which will contain all size units.
     */
    private TextComboBox units;

    public SplitDialog(final GnomeSplit app) {
        // Set main window and dialog title
        super(_("New split"), app.getMainWindow(), false);

        // Alignment
        final Alignment align = new Alignment(0.0f, 0.0f, 0.0f, 0.0f);
        align.setPadding(5, 5, 5, 5);
        this.add(align);

        // Table (used to place widgets)
        final Table table = new Table(6, 3, false);
        table.setColumnSpacing(5);
        table.setRowSpacing(5);
        align.add(table);

        // File to split label
        final Label inputLabel = new Label(_("File to split"));
        table.attach(inputLabel, 0, 1, 0, 1);

        // Size label
        final Label sizeLabel = new Label(_("Splitted file size"));
        table.attach(sizeLabel, 0, 1, 1, 2);

        // Pattern of name label
        final Label patternLabel = new Label(_("Name pattern"));
        table.attach(patternLabel, 0, 1, 2, 3);

        // Path to the file to split
        inputEntry = new Entry();
        table.attach(inputEntry, 1, 2, 0, 1);

        // Size value chooser
        size = new SpinButton(1.0, 4096.0, 1.0);
        table.attach(size, 1, 2, 1, 2);

        // Pattern of the files
        patternEntry = new Entry();
        table.attach(patternEntry, 1, 2, 2, 3);

        // File to split chooser
        final FileChooserButton inputChooser = new FileChooserButton(_("Choose a file."),
                FileChooserAction.OPEN);
        inputChooser.setCurrentFolder(System.getProperty("user.home"));
        table.attach(inputChooser, 2, 3, 0, 1);
        inputChooser.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                String filename = (source.getFilename() != null) ? source.getFilename() : "";
                String pattern = "";

                // A file has been choosen
                if (!filename.isEmpty()) {
                    // Get its name only
                    int slash = filename.lastIndexOf(File.separator) + 1;
                    pattern = filename.substring(slash, filename.length());
                }

                // Update input entry and pattern
                inputEntry.setText(filename);
                patternEntry.setText(pattern + ".part");
            }
        });

        // Unit chooser (list)
        units = new TextComboBox();
        units.appendText(_("octet"));
        units.appendText(_("kibioctet (Kio)"));
        units.appendText(_("mebioctet (Mio)"));
        units.appendText(_("gibioctet (Gio)"));
        units.appendText(_("parts"));
        units.setActive(0);
        table.attach(units, 2, 3, 1, 2);

        // Output directory chooser
        final FileChooserButton outputChooser = new FileChooserButton(_("Choose a folder."),
                FileChooserAction.SELECT_FOLDER);
        outputChooser.setCurrentFolder(System.getProperty("user.home"));
        table.attach(outputChooser, 2, 3, 2, 3);

        // Add buttons to the dialog
        this.addButton(Stock.CANCEL, ResponseType.CANCEL);
        this.addButton(Stock.ADD, ResponseType.OK);

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
            if (inputEntry.getText().isEmpty() || patternEntry.getText().isEmpty())
                dialog = new ErrorDialog(this, _("Uncompleted fields."),
                        _("Check that all fields are completed."));

            // Display the error dialog
            if (dialog != null) {
                dialog.run();
                dialog.hide();

                // Rerun the parent dialog to make the user able to correct
                // the informations
                this.run();
            }
        }
    }
}
