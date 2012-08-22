/*
 * ClearAction.java
 * 
 * Copyright (c) 2009-2012 Guillaume Mazoyer
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

import static org.freedesktop.bindings.Internationalization._;
import static org.gnome.split.GnomeSplit.engine;
import static org.gnome.split.GnomeSplit.ui;

import org.gnome.gtk.Stock;

/**
 * Action to clear the action widgets.
 * 
 * @author Guillaume Mazoyer
 */
final class ClearAction extends Action
{
    protected ClearAction() {
        super("clear-action", null, _("Clear this view."), Stock.CLEAR);
    }

    @Override
    public void onActivate(org.gnome.gtk.Action source) {
        // No action is performed, allow the widget clean up
        if (engine.getEngine() == null) {
            ui.getActionWidget().reset();
            ui.getStatusWidget().reset();
            engine.engineFilesList(null);
        }
    }
}
