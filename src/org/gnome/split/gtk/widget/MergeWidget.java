/*
 * MergeWidget.java
 * 
 * Copyright (c) 2009-2012 Guillaume Mazoyer
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

import java.io.File;

import org.gnome.gtk.Dialog;
import org.gnome.gtk.Entry;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.FileChooserWidget;
import org.gnome.gtk.FileFilter;
import org.gnome.gtk.Frame;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.split.GnomeSplit;
import org.gnome.split.core.merger.DefaultMergeEngine;
import org.gnome.split.core.model.MergeModel;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.gtk.dialog.ErrorDialog;

import static org.freedesktop.bindings.Internationalization._;

/**
 * A widget derived from {@link Frame} to allow the user to start a merge.
 * 
 * @author Guillaume Mazoyer
 */
public class MergeWidget extends VBox implements ActionWidget, MergeModel
{
    /**
     * The GNOME Split application.
     */
    private GnomeSplit app;

    /**
     * Define if the widget is visible or not.
     */
    private boolean visible;

    /**
     * Select a file using a {@link FileChooserWidget}.
     */
    private FileChooserButton fileChooser;

    /**
     * The file to create.
     */
    private Entry destEntry;

    /**
     * The directory where the file will be created.
     */
    private FileChooserButton dirChooser;

    /**
     * The number of parts.
     */
    private Label partsNumber;

    /**
     * The file to create size.
     */
    private Label fileSize;

    /**
     * Display if we will calculate a MD5 sum.
     */
    private Label md5sum;

    /**
     * Size of all the files to merge combined.
     */
    private long size;

    public MergeWidget(final GnomeSplit app) {
        super(false, 12);

        // Save instance
        this.app = app;

        // At first, it is invisible
        visible = false;

        // Set the border of the widget
        this.setBorderWidth(5);

        // Container vertical box
        final VBox container = new VBox(false, 5);
        this.packStart(container, true, true, 0);

        // First chunk row
        final HBox chunkRow = new HBox(false, 5);
        container.packStart(chunkRow, false, false, 0);

        final Label fileLabel = new Label(_("First chunk:"));
        fileLabel.setAlignment(0.0f, 0.5f);
        chunkRow.packStart(fileLabel, false, false, 0);

        // Filter for the file chooser to limit the file choice
        final FileFilter all = new FileFilter(_("All files"));
        all.addPattern("*");
        final FileFilter chk = new FileFilter(_("Valid chunks"));
        chk.addPattern("*.001.gsp");
        chk.addPattern("*.001.xtm");
        chk.addPattern("*.000");
        chk.addPattern("*.001");

        fileChooser = new FileChooserButton(_("Choose a file."), FileChooserAction.OPEN);
        fileChooser.setCurrentFolder(app.getConfig().MERGE_DIRECTORY);

        // Add filters to the file chooser
        fileChooser.addFilter(all);
        fileChooser.addFilter(chk);

        fileChooser.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                // Update the widget
                setFile(source.getFilename());
            }
        });
        chunkRow.packStart(fileChooser, true, true, 0);

        // Destination row
        final HBox destinationRow = new HBox(false, 5);
        container.packStart(destinationRow, false, false, 0);

        final Label destinationLabel = new Label(_("Destination:"));
        destinationLabel.setAlignment(0.0f, 0.5f);
        destinationRow.packStart(destinationLabel, false, false, 0);

        destEntry = new Entry();
        destinationRow.packStart(destEntry, true, true, 0);

        dirChooser = new FileChooserButton(_("Choose a directory."), FileChooserAction.SELECT_FOLDER);
        dirChooser.setCurrentFolder(app.getConfig().MERGE_DIRECTORY);
        destinationRow.packStart(dirChooser, true, true, 0);

        // Parts info row
        final HBox partsRow = new HBox(false, 5);
        container.packStart(partsRow, false, false, 0);

        final Label partsLabel = new Label(_("Chunks:"));
        partsLabel.setAlignment(0.0f, 0.5f);
        partsRow.packStart(partsLabel, false, false, 0);

        partsNumber = new Label(_("Unknown"));
        partsNumber.setAlignment(0.0f, 0.5f);
        partsRow.packStart(partsNumber, true, true, 0);

        // Size info row
        final HBox infoRow = new HBox(false, 5);
        container.packStart(infoRow, false, false, 0);

        final Label sizeLabel = new Label(_("Total size:"));
        sizeLabel.setAlignment(0.0f, 0.5f);
        infoRow.packStart(sizeLabel, false, false, 0);

        fileSize = new Label(_("Unknown"));
        fileSize.setAlignment(0.0f, 0.5f);
        infoRow.packStart(fileSize, true, true, 0);

        // MD5 sum info row
        final HBox md5Row = new HBox(false, 5);
        container.packStart(md5Row, false, false, 0);

        final Label md5Label = new Label(_("MD5 sum:"));
        md5Label.setAlignment(0.0f, 0.5f);
        md5Row.packStart(md5Label, false, false, 0);

        md5sum = new Label(_("Unknown"));
        md5sum.setAlignment(0.0f, 0.5f);
        md5Row.packStart(md5sum, true, true, 0);

        // Make all labels the same size
        labels.add(fileLabel);
        labels.add(destinationLabel);
        labels.add(partsLabel);
        labels.add(sizeLabel);
        labels.add(md5Label);

        // Make all choosers the same size
        choosers.add(fileChooser);
    }

    /**
     * Load the first file to merge. It is used to find informations about the
     * future merge.
     */
    private boolean loadFile(File file) {
        // Load the file
        DefaultMergeEngine engine = DefaultMergeEngine.getInstance(app, file, null);
        if (engine == null) {
            Dialog dialog = new ErrorDialog(
                    app.getMainWindow(),
                    _("Cannot merge."),
                    _("You will not be able to merge the files because this file format is unknown. You are welcome to fill a bug about that."));
            dialog.run();
            dialog.hide();
            return false;
        }

        // Get the full path
        String fullpath = engine.getFilename();

        // Get the last separator
        int lastSeparator = fullpath.lastIndexOf(File.separator);

        // Find the real filename and the directory
        String filename = fullpath.substring((lastSeparator + 1), fullpath.length());
        String directory = fullpath.substring(0, lastSeparator);

        // Get the number of parts
        int number = engine.getChunksNumber();

        // Get the size
        size = engine.getFileLength();

        // Update the widgets
        destEntry.setText(filename);
        dirChooser.setCurrentFolder(directory);
        partsNumber.setLabel((number == -1) ? _("Unknown") : String.valueOf(number));
        fileSize.setLabel(SizeUnit.formatSize(size));
        md5sum.setLabel(engine.useMD5() && app.getConfig().CHECK_FILE_HASH ? _("A MD5 sum will be calculated.")
                : _("A MD5 sum will not be calculated."));

        return true;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean setting) {
        visible = setting;

        if (visible) {
            this.showAll();
        } else {
            this.hide();
        }
    }

    @Override
    public boolean isFullyFilled() {
        return ((fileChooser.getFilename() != null) && !destEntry.getText().isEmpty());
    }

    @Override
    public long checkFreeSpace() {
        File dire = new File(dirChooser.getCurrentFolder());
        long free = dire.getFreeSpace();

        return ((free >= size) ? -1 : free);
    }

    @Override
    public byte checkFileSystemPermission() {
        // Check permission
        boolean read = new File(fileChooser.getFilename()).canRead();
        boolean write = new File(dirChooser.getCurrentFolder()).canWrite();

        // Consider we can do everything
        byte result = 0;

        if (!read) {
            // Can't read
            result += 1;
        }

        if (!write) {
            // Can't write
            result += 2;
        }

        return result;
    }

    @Override
    public void disable() {
        // Get all widgets
        Widget[] widgets = this.getChildren();
        for (Widget widget : widgets) {
            // Make them non-sensitive
            widget.setSensitive(false);
        }
    }

    @Override
    public void enable() {
        // Get all widgets
        Widget[] widgets = this.getChildren();
        for (Widget widget : widgets) {
            // Make them sensitive
            widget.setSensitive(true);
        }
    }

    @Override
    public void reset() {
        fileChooser.setCurrentFolder(app.getConfig().MERGE_DIRECTORY);
        destEntry.setText("");
        dirChooser.setCurrentFolder(app.getConfig().MERGE_DIRECTORY);
        partsNumber.setLabel(_("Unknown"));
        fileSize.setLabel(_("Unknown"));
        md5sum.setLabel(_("Unknown"));
        app.getMainWindow().getProgressBar().reset();
    }

    @Override
    public void updateProgress(double progress, String text, boolean sure) {
        if (!sure) {
            // Unknown progress
            app.getMainWindow().getProgressBar().pulse();
        } else {
            // Known progress
            app.getMainWindow().getProgressBar().setFraction(progress);

            if (!text.isEmpty()) {
                app.getMainWindow().getProgressBar().setText(text);
            }
        }
    }

    @Override
    public File getFile() {
        return new File(fileChooser.getFilename());
    }

    @Override
    public File getDirectory() {
        return new File(dirChooser.getCurrentFolder());
    }

    @Override
    public String getDestination() {
        StringBuilder builder = new StringBuilder();

        // Add directory + name
        builder.append(dirChooser.getCurrentFolder());
        builder.append(File.separator);
        builder.append(destEntry.getText());

        return builder.toString();
    }

    /**
     * Set the first file to merge and update the widget.
     */
    public void setFile(String filename) {
        boolean load = false;

        // Load the file
        load = loadFile(new File(filename));

        // If the load succeeded, update the view
        if (!load) {
            this.reset();
        } else {
            fileChooser.setFilename(filename);
        }
    }
}
