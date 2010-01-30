/*
 * PropertiesDialog.java
 * 
 * Copyright (c) 2010 Guillaume Mazoyer
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
import org.gnome.gtk.Frame;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ProgressBar;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.Stock;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.WindowPosition;
import org.gnome.gtk.Dialog.Response;
import org.gnome.gtk.Window.DeleteEvent;
import org.gnome.pango.EllipsizeMode;
import org.gnome.split.GnomeSplit;
import org.gnome.split.core.Engine;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.gtk.widget.FileList;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to build a properties dialog where detailed information
 * about an action will be displayed.
 * 
 * @author Guillaume Mazoyer
 */
public class PropertiesDialog extends Dialog implements DeleteEvent, Response
{
    /**
     * {@link Label} displaying the directory of the file.
     */
    private Label directory;

    /**
     * {@link Label} displaying the filename of the file.
     */
    private Label filename;

    /**
     * {@link Label} displaying the size of the file.
     */
    private Label size;

    /**
     * {@link FileList} listing all manipulated files.
     */
    private FileList list;

    /**
     * {@link ProgressBar} indicating the progress of the action.
     */
    private ProgressBar progress;

    public PropertiesDialog(final GnomeSplit app) {
        super(_("Properties of the action"), app.getMainWindow(), false);

        // Make the dialog the same size of the main window
        this.setDefaultSize(app.getMainWindow().getWidth(), app.getMainWindow().getHeight());

        // Center this dialog using the position of its parent
        this.setPosition(WindowPosition.CENTER_ON_PARENT);

        // Add a border
        this.setBorderWidth(5);

        // Main container
        final VBox container = new VBox(false, 5);
        this.add(container);

        // Frame containing information about the file
        final Frame fileFrame = new Frame(_("Information about the file"));
        container.packStart(fileFrame, false, false, 0);

        // Add a first container into the frame
        final VBox fileRows = new VBox(true, 3);
        fileFrame.add(fileRows);

        // Information about file directory
        final HBox firstRow = new HBox(false, 3);
        fileRows.packStart(firstRow, false, false, 0);

        final Label directoryLabel = new Label(_("Directory:"));
        firstRow.packStart(directoryLabel, false, false, 0);

        directory = new Label(_("Unknown"));
        directory.setEllipsize(EllipsizeMode.MIDDLE);
        firstRow.packStart(directory);

        // Information about filename
        final HBox secondRow = new HBox(false, 3);
        fileRows.packStart(secondRow, false, false, 0);

        final Label filenameLabel = new Label(_("Filename:"));
        secondRow.packStart(filenameLabel, false, false, 0);

        filename = new Label(_("Unknown"));
        secondRow.packStart(filename);

        // Information about the size of the file
        final HBox thirdRow = new HBox(false, 3);
        fileRows.packStart(thirdRow, false, false, 0);

        final Label sizeLabel = new Label(_("Size of the file:"));
        thirdRow.packStart(sizeLabel, false, false, 0);

        size = new Label(_("Unknown"));
        thirdRow.packStart(size);

        // Make all labels the same size
        final SizeGroup fileLabels = new SizeGroup(SizeGroupMode.BOTH);
        fileLabels.add(directoryLabel);
        fileLabels.add(filenameLabel);
        fileLabels.add(sizeLabel);

        // Frame containing information about the progress
        final Frame progFrame = new Frame(_("Progress of the action"));
        container.packStart(progFrame);

        // Add a first container into the frame
        final VBox progRows = new VBox(false, 3);
        progFrame.add(progRows);

        // Add the files list
        list = new FileList();

        final ScrolledWindow scroll = new ScrolledWindow();
        scroll.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
        scroll.add(list);
        progRows.packStart(scroll);

        // Add the progress bar
        progress = new ProgressBar();
        progRows.packStart(progress, false, false, 0);

        // Close button (save the configuration and close)
        this.addButton(Stock.CLOSE, ResponseType.CLOSE);

        // Connect classic signals
        this.connect((Window.DeleteEvent) this);
        this.connect((Dialog.Response) this);
    }

    @Override
    public void present() {
        this.showAll();
        super.present();
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.CLOSE);
        return false;
    }

    @Override
    public void onResponse(Dialog source, ResponseType response) {
        // Hide the dialog
        this.hide();
    }

    /**
     * Reset the dialog to its basic state.
     */
    public void reset() {
        directory.setLabel(_("Unknown"));
        filename.setLabel(_("Unknown"));
        size.setLabel(_("Unknown"));
        list.clear();
        progress.setFraction(0);
        progress.setText("");
    }

    /**
     * Update the progress bar.
     */
    public void updateProgress(double value, String text, boolean sure) {
        if (!sure) {
            // Unknown progress
            progress.pulse();
        } else {
            // Known progress
            progress.setFraction(value);

            if (!text.isEmpty()) {
                progress.setText(text);
            }
        }
    }

    /**
     * Update the directory label of the dialog.
     */
    public void updateDirectory(String path) {
        directory.setLabel(path);
    }

    /**
     * Update the filename label of the dialog.
     */
    public void updateFilename(String name) {
        filename.setLabel(name);
    }

    /**
     * Update the size label of the dialog.
     */
    public void updateFileSize(long length) {
        size.setLabel(SizeUnit.formatSize(length));
    }

    /**
     * Update the directory to display in the file list.
     */
    public void updateListDirectory(String path) {
        list.addDirectory(path);
    }

    /**
     * Update the file to display in the file list.
     */
    public void updateListFile(String path) {
        list.addFile(path);
    }

    /**
     * Update the properties dialog using the current engine which is running.
     */
    public void update(Engine engine) {
        // Find directory and filename
        int separator = engine.getFilename().lastIndexOf(File.separator);
        String directory = engine.getFilename().substring(0, separator);
        String filename = engine.getFilename().substring((separator + 1));

        // Update the widgets
        this.updateDirectory(directory);
        this.updateFilename(filename);
        this.updateFileSize(engine.getFileLength());
        this.updateListDirectory(engine.getDirectory());
    }
}
