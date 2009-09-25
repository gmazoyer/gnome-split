/*
 * RemoveAction.java
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
package org.gnome.split.gtk.action;

import org.gnome.gtk.Stock;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreeModel;
import org.gnome.gtk.TreeSelection;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.widget.ActionsListWidget;
import org.gnome.split.io.FileOperation;
import org.gnome.split.io.OperationManager;

/**
 * Action to remove a split/assembly/check from the actions list.
 * 
 * @author Guillaume Mazoyer
 */
public final class RemoveAction extends Action
{
    public RemoveAction(final GnomeSplit app) {
        super(app, Stock.REMOVE);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Get the necessary widget
        final ActionsListWidget actions = this.getApplication().getMainWindow().getActionsList();
        final TreeModel model = this.getApplication().getMainWindow().getActionsList().getModel();

        // Get the selection
        final TreeSelection selection = actions.getSelection();

        if (selection != null) {
            // Get the selected row
            final TreeIter selected = selection.getSelected();
            if (selected != null) {
                // Get the manager and the selected operation
                final OperationManager manager = this.getApplication().getOperationManager();
                final FileOperation operation = (FileOperation) model.getValue(selected,
                        actions.reference);

                // Stop the operation
                manager.stop(operation, true);

                // Remove the operation
                manager.remove(operation);
            }
        }
    }
}
