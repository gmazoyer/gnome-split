/*
 * SelectAllAction.java
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

import org.gnome.gtk.SelectionMode;
import org.gnome.gtk.Stock;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreeSelection;
import org.gnome.gtk.TreeView;
import org.gnome.split.GnomeSplit;

/**
 * Action to select every rows of the list.
 * 
 * @author Guillaume Mazoyer
 */
public final class SelectAllAction extends Action
{
    public SelectAllAction(final GnomeSplit app) {
        super(app, Stock.SELECT_ALL);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final TreeView treeview = this.getApplication().getMainWindow().getMainTreeView();
        final TreeSelection selection = treeview.getSelection();
        final TreeIter row = treeview.getModel().getIterFirst();

        // Make us be able to select more than one row
        selection.setMode(SelectionMode.MULTIPLE);
        do {
            // Select row one by one
            selection.selectRow(row);
        } while (row.iterNext());
    }
}
