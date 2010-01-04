/*
 * QuestionDialog.java
 * 
 * Copyright (c) 2010 Guillaume Mazoyer
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

import org.gnome.gtk.QuestionMessageDialog;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Window;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to build an question dialog.
 * 
 * @author Guillaume Mazoyer
 */
public final class QuestionDialog extends QuestionMessageDialog
{
    /**
     * Create an <code>QuestionDialog</code> with a <code>title</code> and
     * <code>text</code>
     */
    public QuestionDialog(Window parent, String title, String text) {
        super(parent, title, text);
        this.setTitle(_("Question"));
        this.setModal(true);
    }

    /**
     * Show the dialog to the user and return <code>true</code> if the user
     * said &quot;yes&quot;.
     */
    public boolean response() {
        ResponseType response = this.run();
        return (response == ResponseType.YES);
    }
}
