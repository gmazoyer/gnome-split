/*
 * ActionWidget.java
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

import org.gnome.gtk.Alignment;
import org.gnome.gtk.AttachOptions;
import org.gnome.gtk.Frame;
import org.gnome.gtk.Label;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ProgressBar;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.Stock;
import org.gnome.gtk.Table;
import org.gnome.gtk.TreeView;
import org.gnome.notify.Notification;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Constants;
import org.gnome.split.core.Splitter;
import org.gnome.split.core.action.properties.ActionProperties;
import org.gnome.split.core.action.properties.SplitProperties;
import org.gnome.split.core.display.SplitterDisplay;
import org.gnome.split.core.util.SizeUnit;
import org.gnome.split.dbus.DbusInhibit;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to create a widget specialized to display an action in
 * progress.
 * 
 * @author Guillaume Mazoyer
 */
public class ActionWidget extends Frame implements SplitterDisplay
{
    private GnomeSplit app;

    private FileList input;

    private FileList output;

    private Label size;

    private StatusWidget status;

    private ProgressBar readProgress;

    private ProgressBar writeProgress;

    private Splitter worker;

    private boolean running;

    private ActionProperties properties;

    private DbusInhibit inhibit;

    /**
     * Build a widget to display actions and their progress.
     * 
     * @param app
     *            the GNOME Split current instance.
     */
    public ActionWidget(final GnomeSplit app) {
        super(null);

        // Save GNOME Split instance
        this.app = app;

        // Table to organize widgets
        final Table table = new Table(8, 6, false);
        table.setColumnSpacing(3);
        table.setRowSpacing(3);
        this.add(table);

        // First rows (input file(s))
        final Label inputLabel = new Label(_("Input"));
        table.attach(inputLabel, 0, 1, 0, 1, AttachOptions.SHRINK, AttachOptions.SHRINK, 0, 0);
        input = new FileList();
        table.attach(this.packFileList(input), 1, 6, 0, 2, AttachOptions.FILL, AttachOptions.FILL, 0, 0);

        // Second rows (output file(s))
        final Label outputLabel = new Label(_("Output"));
        table.attach(outputLabel, 0, 1, 2, 3, AttachOptions.SHRINK, AttachOptions.SHRINK, 0, 0);
        output = new FileList();
        table.attach(this.packFileList(output), 1, 6, 2, 4, AttachOptions.FILL, AttachOptions.FILL, 0, 0);

        // Third row (size)
        final Label sizeLabel = new Label(_("Chunk size"));
        table.attach(sizeLabel, 0, 1, 4, 5);
        size = new Label();
        table.attach(size, 1, 6, 4, 5);

        // Fourth row (read)
        final Label readLabel = new Label(_("Read"));
        table.attach(readLabel, 0, 1, 5, 6);
        readProgress = new ProgressBar();
        table.attach(readProgress, 1, 6, 5, 6);

        // Fifth row (read)
        final Label writeLabel = new Label(_("Write"));
        table.attach(writeLabel, 0, 1, 6, 7);
        writeProgress = new ProgressBar();
        table.attach(writeProgress, 1, 6, 6, 7);

        // Sixth row (status)
        status = new StatusWidget();
        table.attach(status, 0, 6, 7, 8);

        // Inhibition query
        inhibit = new DbusInhibit();
    }

    @Override
    public void chunkCreated(String filename) {
        output.addFilename(filename);
    }

    @Override
    public void splittingFinished(boolean error) {
        worker = null;
        this.setRunning(false);

        if (error) {
            this.showError(_("An error has occurred."));
        } else {
            this.showMessage(_("Splitting successful."));
            status.updateImage(Stock.YES);
        }

        if (app.getConfig().NO_HIBERNATION) {
            // Uninhibit computer hibernation
            inhibit.unInhibit();
        }

        if (app.getConfig().USE_NOTIFICATION) {
            Notification notify;

            if (error) {
                // Notify the error
                notify = new Notification(
                        _("Error while splitting."),
                        _("An error has occurred while splitting the file. Please try to split it again."),
                        null, app.getMainWindow().getTrayIcon());
            } else {
                // Notify the end of action
                notify = new Notification(_("Split successful."),
                        _("The split of the file has ended successfully."), null, app.getMainWindow()
                                .getTrayIcon());
            }

            // Set the notification icon
            notify.setIcon(Constants.PROGRAM_LOGO);

            // Show the notification
            notify.show();
        }
    }

    @Override
    public void setReadProgress(double progress) {
        readProgress.setFraction(progress);
    }

    @Override
    public void setWriteProgress(double progress) {
        writeProgress.setFraction(progress);
    }

    @Override
    public void setReadInfos(long read, long total) {
        double divider = SizeUnit.getSizeDivider(total);
        readProgress.setText(SizeUnit.formatSize(read, divider) + " / "
                + SizeUnit.formatSize(total, divider));
    }

    @Override
    public void setWriteInfos(long written, long total) {
        double divider = SizeUnit.getSizeDivider(total);
        writeProgress.setText(SizeUnit.formatSize(written, divider) + " / "
                + SizeUnit.formatSize(total, divider));
    }

    @Override
    public void showError(String error) {
        status.updateImage(Stock.DIALOG_ERROR);
        status.updateText(error);
    }

    @Override
    public void showMessage(String message) {
        status.updateImage(Stock.DIALOG_INFO);
        status.updateText(message);
    }

    /**
     * Pack a {@link FileList} widget (derived from {@link TreeView}) into a
     * {@link ScrolledWindow}.
     */
    private Alignment packFileList(FileList list) {
        // Pack everything in an Alignment
        Alignment align = new Alignment(0.0f, 0.0f, 0.0f, 0.0f);

        // Create the scroll area
        Frame frame = new Frame(null);
        ScrolledWindow scroll = new ScrolledWindow();

        // Set a proper size
        list.setSizeRequest(300, 50);

        // Show scrollbars only if needed
        scroll.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
        scroll.add(list);
        frame.add(scroll);

        // And finally
        align.add(frame);
        return align;
    }

    private String getInput() {
        return input.getFirstValue();
    }

    private void clear() {
        // Clear files lists
        input.clear();
        output.clear();

        // Clear size info
        size.setLabel("");

        // Clear status
        status.reset();

        // Clear progress info
        readProgress.setFraction(0);
        readProgress.setText("");
        writeProgress.setFraction(0);
        writeProgress.setText("");
    }

    private void setRunning(boolean state) {
        running = state;

        // Force to run the GC
        System.gc();
    }

    private void initAction(String filename, String targetPath, long maxChunkSize) {
        if (worker == null) {
            // Start the worker thread
            this.setRunning(true);
            worker = new Splitter(this, filename, targetPath, maxChunkSize);
        }
    }

    private void cancelAction() {
        if (worker != null) {
            // Stop the worker thread
            worker.stopSplitting();
        }
    }

    public void updateView(SplitProperties properties) {
        // Update properties object
        this.properties = properties;

        // Update input tree
        int last = properties.filename.lastIndexOf('/');
        String directory = properties.filename.substring(0, last);
        String filename = properties.filename.substring((last + 1), properties.filename.length());

        input.addDirectory(directory);
        input.addFilename(filename);

        // Update output tree
        output.addDirectory(properties.directory);

        // Update size label
        size.setLabel(SizeUnit.formatSize(properties.chunkSize) + " " + _("maximum for each chunk."));

        // Start the action if user wants to
        if (app.getConfig().AUTO_START) {
            this.run();
        }
    }

    public void run() {
        if (!running && (properties != null)) {
            if (app.getConfig().NO_HIBERNATION) {
                // Inhibit computer hibernation
                inhibit.inhibit();
            }

            // Get properties
            SplitProperties split = (SplitProperties) properties;
            long size = split.chunkSize;

            if (size <= 0) {
                this.showError(_("Invalid chunk size."));
            } else {
                // Initialize the splitting
                this.initAction(this.getInput(), split.targetPattern, size);
            }
        }
    }

    public void cancel() {
        if (running) {
            // Reset properties
            properties = null;

            // Cancel action
            this.cancelAction();
        }

        // Clear the view
        this.clear();
    }

    public void delete() {
        if (running) {
            // Remove chunks
            worker.removeChunks();
        }
    }

    public ActionProperties getActionProperties() {
        return properties;
    }
}
