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
import org.gnome.split.io.FileSplit;
import org.gnome.split.utils.SizeUnit;

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
     * The GNOME Split application.
     */
    private GnomeSplit app;

    /**
     * Entry which will contain the file to split path.
     */
    private Entry input;

    /**
     * Length of the future parts of the file.
     */
    private SpinButton size;

    /**
     * Entry which will contain the filename pattern.
     */
    private Entry pattern;

    /**
     * Destination directory. Where the files will be created.
     */
    private FileChooserButton destination;

    /**
     * List which will contain all size units.
     */
    private TextComboBox units;

    public SplitDialog(final GnomeSplit app) {
        // Set main window and dialog title
        super(_("New split"), app.getMainWindow(), false);

        // Save GNOME Split instance
        this.app = app;

        // Let's have a little border
        this.setBorderWidth(5);

        // Table (used to place widgets)
        final Table table = new Table(6, 3, false);
        table.setColumnSpacing(5);
        table.setRowSpacing(5);
        this.add(table);

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
        input = new Entry();
        table.attach(input, 1, 2, 0, 1);

        // Size value chooser
        size = new SpinButton(1.0, 4096.0, 1.0);
        table.attach(size, 1, 2, 1, 2);

        // Pattern of the files
        pattern = new Entry();
        table.attach(pattern, 1, 2, 2, 3);

        // File to split chooser
        final FileChooserButton inputChooser = new FileChooserButton(_("Choose a file."),
                FileChooserAction.OPEN);
        inputChooser.setCurrentFolder(System.getProperty("user.home"));
        table.attach(inputChooser, 2, 3, 0, 1);
        inputChooser.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                String filename = (source.getFilename() != null) ? source.getFilename() : "";
                String name = "";

                // A file has been choosen
                if (!filename.isEmpty()) {
                    // Get its name only
                    int slash = filename.lastIndexOf(File.separator) + 1;
                    name = filename.substring(slash, filename.length());
                }

                // Update input entry and pattern
                input.setText(filename);
                pattern.setText(name + ".part");
            }
        });

        // Unit chooser (list)
        units = new TextComboBox();
        units.appendText(_("byte"));
        units.appendText(_("kilobyte (KB)"));
        units.appendText(_("megabyte (MB)"));
        units.appendText(_("gigabyte (GB)"));
        units.appendText(_("parts"));
        units.setActive(0);
        table.attach(units, 2, 3, 1, 2);

        // Output directory chooser
        destination = new FileChooserButton(_("Choose a folder."), FileChooserAction.SELECT_FOLDER);
        destination.setCurrentFolder(System.getProperty("user.home"));
        table.attach(destination, 2, 3, 2, 3);

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
            if (input.getText().isEmpty() || pattern.getText().isEmpty())
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

            // Find and all values needed to start a split
            final File file = new File(input.getText());
            final String name = destination.getCurrentFolder() + File.separator + pattern.getText();

            // Calculate size for each parts
            final long fileSize;
            switch (units.getActive()) {
            case 1: // Kio
            case 2: // Mio
            case 3: // Gio
            case 4: // Tio
                final double multiplier = SizeUnit.values()[units.getActive() - 1];
                fileSize = (long) (size.getValue() * multiplier);
                break;
            case 5: // Parts
                fileSize = (long) Math.ceil((double) file.length() / size.getValue());
                break;
            default: // octets
                fileSize = (long) size.getValue();
                break;
            }

            // Create the split action
            final FileSplit split = new FileSplit(app, file, name, fileSize);

            // Add listeners to update the interface
            split.addProgressListener(app.getMainWindow().getActionsList());
            split.addStatusListener(app.getMainWindow().getActionsList());

            // Add the operation into the manager
            app.getOperationManager().add(split);

            // A thread will take care of the execution
            app.getOperationManager().start(split);
        }
    }
}
