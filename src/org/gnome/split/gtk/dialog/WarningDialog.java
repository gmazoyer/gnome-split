/*
 * WarningDialog.java
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
package org.gnome.split.gtk.dialog;

import org.gnome.gtk.ButtonsType;
import org.gnome.gtk.MessageDialog;
import org.gnome.gtk.MessageType;
import org.gnome.gtk.Window;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to build GTK+ Warning dialog.
 * 
 * @author Guillaume Mazoyer
 */
public final class WarningDialog extends MessageDialog
{
    /**
     * Create an <code>ErrorDialog</code> with a <code>text</code>.
     */
    public WarningDialog(Window parent, String text) {
        super(parent, true, MessageType.WARNING, ButtonsType.OK, text);
        this.setTitle(_("Warning!"));
    }
}
