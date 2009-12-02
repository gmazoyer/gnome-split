/*
 * CancelAction.java
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
import org.gnome.split.GnomeSplit;
import org.gnome.split.core.Engine;

/**
 * Action to cancel a split/merge.
 * 
 * @author Guillaume Mazoyer
 */
public final class CancelAction extends Action
{
    public CancelAction(final GnomeSplit app) {
        super(app, Stock.CANCEL);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Engine engine = this.getApplication().getEngineListener().getEngine();
        if (engine != null) {
            engine.stop(false);
        }
    }
}
