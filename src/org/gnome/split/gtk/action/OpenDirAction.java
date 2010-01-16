/*
 * OpenDirAction.java
 * 
 * Copyright (c) 2009-2010 Guillaume Mazoyer
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
import org.gnome.split.core.DefaultEngine;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to open the directory containing files.
 * 
 * @author Guillaume Mazoyer
 */
public final class OpenDirAction extends Action
{
    public OpenDirAction(final GnomeSplit app) {
        super(app, "opendir-action", _("_Open folder"), null, Stock.OPEN);
    }

    @Override
    public void onActivate(org.gnome.gtk.Action source) {
        DefaultEngine engine = (DefaultEngine) this.getApplication().getEngineListener().getEngine();
        if (engine != null) {
            String directory = engine.getDirectory();
            if (directory != null) {
                // Open the directory with the default program
                this.getApplication().openURI("file://" + directory);
            }
        }
    }
}
