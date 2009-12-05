/*
 * InfoDialog.java
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

import static org.freedesktop.bindings.Internationalization._;

import org.gnome.gtk.InfoMessageDialog;
import org.gnome.gtk.Window;

/**
 * This class is used to build an info dialog.
 * 
 * @author Guillaume Mazoyer
 */
public final class InfoDialog extends InfoMessageDialog
{
    /**
     * Create an <code>InfoDialog</code> with a <code>title</code> and
     * <code>text</code>
     */
    public InfoDialog(Window parent, String title, String text) {
        super(parent, title, text);
        this.setTitle(_("Information"));
    }
}
