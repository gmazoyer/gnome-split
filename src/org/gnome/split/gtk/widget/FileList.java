/*
 * FileList.java
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
import org.gnome.split.core.utils.SizeUnit;

/**
 * A widget to display files and directories with their sizes.
 * 
 * @author Guillaume Mazoyer
 */
public class FileList extends TreeView
{
    /**
     * The column which will display an icon.
     */
    private DataColumnStock icon;

    /**
     * The column which will display the name of the file.
     */
    private DataColumnString file;

    /**
     * The column which will display the size of the file.
     */
    private DataColumnString size;

    /**
     * The model to use for this {@link TreeView}.
     */
    private TreeStore model;

    public FileList() {
        super();

        // Create the model
        model = new TreeStore(new DataColumn[] {
                icon = new DataColumnStock(),
                file = new DataColumnString(),
                size = new DataColumnString()
        });
        this.setModel(model);

        TreeViewColumn column;

        // Render the icon
        column = this.appendColumn();
        CellRendererPixbuf iconRenderer = new CellRendererPixbuf(column);
        iconRenderer.setStock(icon);

        // Render the filename
        column = this.appendColumn();
        CellRendererText fileRenderer = new CellRendererText(column);
        fileRenderer.setText(file);

        // Render the size of the file
        column = this.appendColumn();
        CellRendererText sizeRenderer = new CellRendererText(column);
        sizeRenderer.setText(size);

        // Do not show the headers
        this.setHeadersVisible(false);
    }

    /**
     * Add a row representing a directory to this widget.
     */
    public void addDirectory(String fullpath) {
        // Append a new row
        TreeIter row = model.appendRow();

        // Set its values
        model.setValue(row, icon, Stock.DIRECTORY);
        model.setValue(row, file, fullpath);
        model.setValue(row, size, "");
    }

    /**
     * Add a row representing a file to this widget.
     */
    public void addFile(String fullpath) {
        // Create a File object using a full path
        File fileToAdd = new File(fullpath);

        // Append a new row as a child of the first row
        TreeIter row = model.appendChild(model.getIterFirst());

        // Set its values
        model.setValue(row, icon, Stock.FILE);
        model.setValue(row, file, fileToAdd.getName());
        model.setValue(row, size, SizeUnit.formatSize(fileToAdd.length()));

        // Expand all rows
        this.expandAll();
    }

    /**
     * Reset this widget and remove all rows.
     */
    public void clear() {
        model.clear();
    }
}
