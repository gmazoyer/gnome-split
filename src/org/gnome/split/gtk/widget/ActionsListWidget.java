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

import org.gnome.gtk.CellRendererPixbuf;
import org.gnome.gtk.CellRendererProgress;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnInteger;
import org.gnome.gtk.DataColumnPixbuf;
import org.gnome.gtk.DataColumnReference;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.SelectionMode;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Constants;
import org.gnome.split.io.FileOperation;
import org.gnome.split.io.event.ProgressChangedEvent;
import org.gnome.split.io.event.ProgressListener;
import org.gnome.split.io.event.StatusChangedEvent;
import org.gnome.split.io.event.StatusListener;

/**
 * This class is used to create a widget specialized to display an action in
 * progress.
 * 
 * @author Guillaume Mazoyer
 */
public class ActionsListWidget extends TreeView implements StatusListener, ProgressListener
{
    /**
     * Column to contain reference to the operation.
     */
    public DataColumnReference reference;

    /**
     * Column to show the file mime-type
     */
    public DataColumnPixbuf image;

    /**
     * Column to show infos about the action.
     */
    public DataColumnString infos;

    /**
     * Column to show the progress.
     */
    public DataColumnInteger progress;

    /**
     * The model of the TreeView.
     */
    private ListStore model;

    /**
     * Build a TreeView to display actions and their progress.
     * 
     * @param app
     *            the GNOME Split current instance.
     */
    public ActionsListWidget(final GnomeSplit app) {
        super();

        // Create all columns
        reference = new DataColumnReference();
        image = new DataColumnPixbuf();
        infos = new DataColumnString();
        progress = new DataColumnInteger();

        // Create the model
        model = new ListStore(new DataColumn[] {
                reference, image, infos, progress
        });

        // Set the model
        this.setModel(model);

        // Do not display headers
        this.setHeadersVisible(false);

        // Request a default size
        this.setSizeRequest(400, 200);

        // Renderer for all cells
        TreeViewColumn column;
        final CellRendererPixbuf pixbufRenderer;
        final CellRendererText textRenderer;
        final CellRendererProgress progressRenderer;

        // Render the image cell
        column = this.appendColumn();
        column.setTitle("mime-type");
        pixbufRenderer = new CellRendererPixbuf(column);
        pixbufRenderer.setPixbuf(image);

        // Render the text cell
        column = this.appendColumn();
        column.setTitle("infos");
        textRenderer = new CellRendererText(column);
        textRenderer.setMarkup(infos);

        // Render the progress cell
        column = this.appendColumn();
        column.setTitle("progress");
        progressRenderer = new CellRendererProgress(column);
        progressRenderer.setValue(progress);

        // Allow multiple selections
        this.getSelection().setMode(SelectionMode.MULTIPLE);
    }

    /**
     * Get the row containing an operation.
     * 
     * @param operation
     *            the operation to look for.
     * @return the row containing the operation or <code>null</code> if not
     *         found.
     */
    private TreeIter getRowFromOperation(FileOperation operation) {
        // Get the first row in the view
        final TreeIter row = model.getIterFirst();

        do {
            // Get the object reference
            final FileOperation tmp = (FileOperation) model.getValue(row, reference);

            // Did we find the right row?
            if (tmp == operation)
                return row;
        } while (row.iterNext());

        // Row not found
        return null;
    }

    @Override
    public void statusChanged(StatusChangedEvent event) {
        final TreeIter row = this.getRowFromOperation((FileOperation) event.getSource());

        // Change the status
        model.setValue(row, infos, event.getStatus());
    }

    @Override
    public void progressChanged(ProgressChangedEvent event) {
        final TreeIter row = this.getRowFromOperation((FileOperation) event.getSource());

        // Change the progress
        model.setValue(row, progress, (int) (event.getProgress() * 100));
    }

    /**
     * Add an action, this will result in appending a new row to display the
     * stuffs related to the new action.
     */
    public TreeIter newAction(FileOperation operation) {
        // Append a new row
        final TreeIter row = model.appendRow();

        // Set the reference
        model.setValue(row, reference, operation);

        // Set the correct image
        model.setValue(row, image, Constants.PROGRAM_LOGO);

        // Set the correct action infos
        model.setValue(row, infos, operation.getStatusString());

        // Set the current progress
        model.setValue(row, progress, (int) (operation.getProgress() * 100));

        // Finally, return the reference to the action in the view
        return row;
    }

    /**
     * Remove an action from the view.
     */
    public void removeAction(FileOperation operation) {
        model.removeRow(this.getRowFromOperation(operation));
    }
}
