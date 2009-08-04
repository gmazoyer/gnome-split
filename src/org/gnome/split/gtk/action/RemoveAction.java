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
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.widget.MainList;

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
        final MainList view = this.getApplication().getMainWindow().getMainTreeView();
        final TreeIter selected = view.getSelection().getSelected();

        if (selected != null)
            view.getModel().removeRow(selected);
    }
}
