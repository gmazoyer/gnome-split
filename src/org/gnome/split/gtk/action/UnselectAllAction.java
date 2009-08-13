/*
 * UnselectAllAction.java
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
import org.gnome.gtk.TreeSelection;
import org.gnome.split.GnomeSplit;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to unselect every rows of the list.
 * 
 * @author Guillaume Mazoyer
 */
public final class UnselectAllAction extends Action
{
    public UnselectAllAction(final GnomeSplit app) {
        super(app, _("_Unselect all"));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        final TreeSelection selection = this.getApplication()
                .getMainWindow()
                .getMainTreeView()
                .getSelection();
        selection.setMode(SelectionMode.SINGLE);
        selection.unselectAll();
    }
}
