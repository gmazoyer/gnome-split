/*
 * TranslateAction.java
 * 
 * Copyright (c) 2009-2011 Guillaume Mazoyer
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

import org.gnome.split.GnomeSplit;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to open the online translation.
 * 
 * @author Guillaume Mazoyer
 */
final class TranslateAction extends Action
{
    protected TranslateAction(final GnomeSplit app) {
        super(app, "translate-action", _("_Translate This Application..."));
    }

    @Override
    public void onActivate(org.gnome.gtk.Action source) {
        this.getApplication().openURI("https://translations.launchpad.net/gnome-split");
    }
}
