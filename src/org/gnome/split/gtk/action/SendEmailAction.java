/*
 * SendEmailAction.java
 * 
 * Copyright (c) 2009-2013 Guillaume Mazoyer
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
import static org.gnome.split.GnomeSplit.openURI;

import java.util.List;

import org.freedesktop.icons.ActionIcon;

/**
 * Action to send the created files by email.
 * 
 * @author Guillaume Mazoyer
 */
final class SendEmailAction extends Action
{
    protected SendEmailAction() {
        super("send-email-action", _("Send by _email"), _("Send one or several files by email"),
                ActionIcon.MAIL_SEND);
    }

    @Override
    public void onActivate(org.gnome.gtk.Action source) {
        List<String> list = engine.getFilesList();
        StringBuilder uri = new StringBuilder("mailto:?");

        // Build the URI
        for (int i = 0; i < list.size(); i++) {
            uri.append((i == 0) ? "attach=" : "&attach=");
            uri.append(list.get(i));
        }

        // Pass a URI which means that it sends an email
        openURI(uri.toString());
    }
}
