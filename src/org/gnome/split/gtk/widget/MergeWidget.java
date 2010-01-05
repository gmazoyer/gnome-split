/*
 * MergeWidget.java
 * 
 * Copyright (c) 2009-2010 Guillaume Mazoyer
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
import org.gnome.gtk.EntryIconPosition;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.FileChooserWidget;
import org.gnome.gtk.Frame;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.ProgressBar;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.Stock;
import org.gnome.gtk.VBox;
import org.gnome.split.GnomeSplit;
import org.gnome.split.core.merger.DefaultMergeEngine;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.gtk.dialog.ErrorDialog;

import static org.freedesktop.bindings.Internationalization._;

/**
 * A widget derived from {@link Frame} to allow the user to start a merge.
 * 
 * @author Guillaume Mazoyer
 */
public class MergeWidget extends Frame implements ActionWidget
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
     * The first file to merge.
     */
    private Entry fileEntry;

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
     * Merge progress.
     */
    private ProgressBar progressbar;

    public MergeWidget(final GnomeSplit app) {
        super(null);

        // Save instance
        this.app = app;

        // At first, it is invisible
        visible = false;

        // Main container
        final VBox container = new VBox(false, 12);
        this.add(container);

        // Secondary vertical box
        final VBox secondary = new VBox(false, 5);
        container.packStart(secondary);

        // First chunk row
        final HBox chunkRow = new HBox(false, 5);
        secondary.packStart(chunkRow);

        final Label fileLabel = new Label(_("First chunk:"));
        chunkRow.packStart(fileLabel, false, false, 0);

        fileEntry = new Entry();
        fileEntry.setIconFromStock(EntryIconPosition.PRIMARY, Stock.FILE);
        fileEntry.setIconActivatable(EntryIconPosition.PRIMARY, false);
        chunkRow.packStart(fileEntry);

        fileChooser = new FileChooserButton(_("Choose a file."), FileChooserAction.OPEN);
        fileChooser.setCurrentFolder(app.getConfig().MERGE_DIRECTORY);
        fileChooser.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                boolean load = false;
                String file = source.getFilename();

                // Load the file
                load = loadFile(new File(file));

                // If the load succeeded, update the view
                if (load) {
                    fileEntry.setText(source.getFilename());
                }
            }
        });
        // table.attach(fileChooser, 2, 3, 0, 1);
        chunkRow.packStart(fileChooser, false, false, 0);

        // Destination row
        final HBox destinationRow = new HBox(false, 5);
        secondary.packStart(destinationRow);

        final Label destinationLabel = new Label(_("Destination:"));
        destinationRow.packStart(destinationLabel, false, false, 0);

        destEntry = new Entry();
        destEntry.setIconFromStock(EntryIconPosition.PRIMARY, Stock.FILE);
        destEntry.setIconActivatable(EntryIconPosition.PRIMARY, false);
        destinationRow.packStart(destEntry);

        dirChooser = new FileChooserButton(_("Choose a directory."), FileChooserAction.SELECT_FOLDER);
        dirChooser.setCurrentFolder(app.getConfig().MERGE_DIRECTORY);
        destinationRow.packStart(dirChooser, false, false, 0);

        // Parts info row
        final HBox partsRow = new HBox(false, 5);
        secondary.packStart(partsRow);

        final Label partsLabel = new Label(_("Chunks:"));
        partsRow.packStart(partsLabel, false, false, 0);

        partsNumber = new Label();
        partsRow.packStart(partsNumber);

        // Size info row
        final HBox infoRow = new HBox(false, 5);
        secondary.packStart(infoRow);

        final Label sizeLabel = new Label(_("Total size:"));
        infoRow.packStart(sizeLabel, false, false, 0);

        fileSize = new Label();
        infoRow.packStart(fileSize);

        // MD5 sum info row
        final HBox md5Row = new HBox(false, 5);
        secondary.packStart(md5Row);

        final Label md5Label = new Label(_("MD5 sum:"));
        md5Row.packStart(md5Label, false, false, 0);

        md5sum = new Label();
        md5Row.packStart(md5sum);

        // Make all labels the same size
        SizeGroup labels = new SizeGroup(SizeGroupMode.HORIZONTAL);
        labels.add(fileLabel);
        labels.add(destinationLabel);
        labels.add(partsLabel);
        labels.add(sizeLabel);
        labels.add(md5Label);

        // Make all entries the same size
        SizeGroup entries = new SizeGroup(SizeGroupMode.HORIZONTAL);
        entries.add(fileEntry);
        entries.add(destEntry);

        // Make all choosers the same size
        SizeGroup choosers = new SizeGroup(SizeGroupMode.BOTH);
        choosers.add(fileChooser);
        choosers.add(dirChooser);

        // Pack the progress bar
        progressbar = new ProgressBar();
        container.packStart(progressbar, false, false, 0);
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
        int number = engine.getParts();

        // Update the widgets
        destEntry.setText(filename);
        dirChooser.setCurrentFolder(directory);
        partsNumber.setLabel((number == -1) ? _("Unknown") : String.valueOf(number));
        fileSize.setLabel(SizeUnit.formatSize(engine.getFileLength()));
        md5sum.setLabel(engine.useMD5() ? _("MD5 sum will be use.") : _("MD5 sum will not be use."));

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
        return (!fileEntry.getText().isEmpty() && !destEntry.getText().isEmpty());
    }

    @Override
    public void disable() {
        fileEntry.setSensitive(false);
        fileChooser.setSensitive(false);
        destEntry.setSensitive(false);
        dirChooser.setSensitive(false);
    }

    @Override
    public void enable() {
        fileEntry.setSensitive(true);
        fileChooser.setSensitive(true);
        destEntry.setSensitive(true);
        dirChooser.setSensitive(true);
    }

    @Override
    public void reset() {
        fileEntry.setText("");
        fileChooser.setCurrentFolder(app.getConfig().MERGE_DIRECTORY);
        destEntry.setText("");
        dirChooser.setCurrentFolder(app.getConfig().MERGE_DIRECTORY);
        partsNumber.setLabel("");
        fileSize.setLabel("");
        md5sum.setLabel("");
        progressbar.setFraction(0);
        progressbar.setText("");
    }

    @Override
    public void updateProgress(double progress, String text, boolean sure) {
        if (sure) {
            // Known progress
            progressbar.setFraction(progress);
        } else {
            // Unknown progress
            progressbar.pulse();
        }
        progressbar.setText(text);
    }

    /**
     * Get the name of the file to create.
     */
    public String getDestination() {
        StringBuilder builder = new StringBuilder();

        // Add directory + name
        builder.append(dirChooser.getCurrentFolder());
        builder.append(File.separator);
        builder.append(destEntry.getText());

        return builder.toString();
    }

    /**
     * Get the first chunk to merge.
     */
    public File getFirstFile() {
        return new File(fileEntry.getText());
    }
}
