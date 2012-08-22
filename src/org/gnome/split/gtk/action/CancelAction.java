/*
 * CancelAction.java
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

import org.gnome.gtk.Stock;
import org.gnome.split.core.Engine;

/**
 * Action to cancel a split/merge.
 * 
 * @author Guillaume Mazoyer
 */
final class CancelAction extends Action
{
    protected CancelAction() {
        super("cancel-action", null, _("Cancel this action."), Stock.CANCEL);
    }

    @Override
    public void onActivate(org.gnome.gtk.Action source) {
        Engine real = engine.getEngine();

        if (real != null) {
            real.stop(false);
        }
    }
}
