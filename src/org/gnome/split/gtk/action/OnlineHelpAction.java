/*
 * OnlineHelpAction.java
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
import static org.gnome.split.GnomeSplit.openURI;

/**
 * Action to open the online help.
 * 
 * @author Guillaume Mazoyer
 */
final class OnlineHelpAction extends Action
{
    protected OnlineHelpAction() {
        super("online-help-action", _("Get Help _Online..."));
    }

    @Override
    public void onActivate(org.gnome.gtk.Action source) {
        openURI("https://answers.launchpad.net/gnome-split");
    }
}
