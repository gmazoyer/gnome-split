/*
 * OperationManager.java
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
package org.gnome.split.io;

import java.util.HashMap;
import java.util.Map;

import org.gnome.gtk.TreeIter;
import org.gnome.split.GnomeSplit;

/**
 * A class to manager file operations.
 * 
 * @author Guillaume Mazoyer
 */
public class OperationManager
{
    /**
     * The current GNOME Split instance.
     */
    private GnomeSplit app;

    /**
     * Map containing operations and the corresponding rows in the treeview.
     */
    private Map<FileOperation, TreeIter> operations;

    /**
     * Build an instance of the operation manager.
     * 
     * @param app
     *            a reference to the current program instance.
     */
    public OperationManager(final GnomeSplit app) {
        this.app = app;
        this.operations = new HashMap<FileOperation, TreeIter>();
    }

    /**
     * Add an operation to the interface and to the manager list.
     */
    public void add(FileOperation operation) {
        // Update the interface
        final TreeIter row = app.getMainWindow().getActionsList().newAction(operation);

        // Add it to the manager list
        operations.put(operation, row);
    }

    /**
     * Remove an operation from the interface and from the manager list.
     */
    public void remove(FileOperation operation) {
        // Update the interface
        app.getMainWindow().getActionsList().removeAction(operation);

        // Remove it from the manager list
        operations.remove(operation);
    }

    /**
     * Start the thread which will take care of the operation execution.
     */
    public void start(FileOperation operation) {
        operation.start();
    }

    /**
     * Stop the thread which is taking care of the operation execution. If
     * <code>stop</code> is <code>true</code>, the user will not be allowed to
     * restart the operation.
     */
    public void stop(FileOperation operation, boolean stop) {
        operation.interrupt();

        if (stop)
            operation.requestStop();
    }
}
