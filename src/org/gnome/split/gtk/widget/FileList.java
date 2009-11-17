/*
 * FileList.java
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

import java.io.File;

import org.gnome.gtk.CellRendererPixbuf;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnStock;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.Stock;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreeStore;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;

/**
 * A widget derived from {@link TreeView} used to display files and directory.
 * 
 * @author Guillaume Mazoyer
 */
public class FileList extends TreeView
{
    private TreeStore model;

    private DataColumnStock icon;

    private DataColumnString file;

    public FileList() {
        super();

        // Create the model
        model = new TreeStore(new DataColumn[] {
                icon = new DataColumnStock(), file = new DataColumnString()
        });
        this.setModel(model);

        // Do not interact with the view
        this.setHeadersVisible(false);
        this.setReorderable(false);

        TreeViewColumn column;

        // Add a icon column
        column = this.appendColumn();
        CellRendererPixbuf iconRenderer = new CellRendererPixbuf(column);
        iconRenderer.setStock(icon);

        // Add a file column
        column = this.appendColumn();
        CellRendererText fileRenderer = new CellRendererText(column);
        fileRenderer.setText(file);
    }

    /**
     * Add a file as child after a directory.
     */
    public void addFilename(String path) {
        // Add filename as a child of the directory
        TreeIter row = model.getIterFirst();
        TreeIter child = model.appendChild(row);

        // Set values
        model.setValue(child, icon, Stock.FILE);
        model.setValue(child, file, path);

        // Show it
        this.expandAll();
    }

    /**
     * Add a directory as normal row in the view.
     */
    public void addDirectory(String path) {
        // Add directory as the first row
        TreeIter row = model.appendRow();

        // Set values
        model.setValue(row, icon, Stock.DIRECTORY);
        model.setValue(row, file, path);
    }

    /**
     * Return the first usable value (it uses to be parent + child).
     */
    public String getFirstValue() {
        TreeIter row = model.getIterFirst();

        // Tree is empty
        if (row == null) {
            return null;
        }

        // We are in a TreeStore
        if (model.iterHasChild(row)) {
            // Get the directory path
            String value = model.getValue(row, file) + File.separator;
            TreeIter child = model.iterChildren(row);

            // Append the filename
            value += model.getValue(child, file);

            // And finally
            return value;
        }

        return model.getValue(row, file);
    }

    /**
     * Remove all displayed rows.
     */
    public void clear() {
        model.clear();
    }
}
