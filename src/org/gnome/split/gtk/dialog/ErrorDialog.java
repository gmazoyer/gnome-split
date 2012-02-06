/*
 * ErrorDialog.java
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
package org.gnome.split.gtk.dialog;

import org.gnome.gtk.ErrorMessageDialog;
import org.gnome.gtk.Expander;
import org.gnome.gtk.Frame;
import org.gnome.gtk.TextBuffer;
import org.gnome.gtk.TextView;
import org.gnome.gtk.Window;
import org.gnome.gtk.WrapMode;
import org.gnome.split.core.utils.UncaughtExceptionLogger;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to build GTK+ Error dialog.
 * 
 * @author Guillaume Mazoyer
 */
public final class ErrorDialog extends ErrorMessageDialog
{
    /**
     * Create an <code>ErrorDialog</code> with a <code>title</code> and
     * <code>text</code>.
     */
    public ErrorDialog(Window parent, String title, String text) {
        super(parent, title, text);
    }

    /**
     * Create an <code>ErrorDialog</code> with a <code>title</code>, a
     * <code>text</code> and an <code>exception</code>.
     */
    public ErrorDialog(Window parent, String title, String text, Throwable exception) {
        super(parent, title, text);

        // Add details to the dialog
        final Expander expander = new Expander(_("Details"));
        this.add(expander);

        // Add a frame into the expander
        final Frame frame = new Frame(null);
        expander.add(frame);

        // Add a text view with the exception stacktrace
        final TextBuffer buffer = new TextBuffer();
        final TextView view = new TextView(buffer);

        // The view is not editable
        view.setEditable(false);
        view.setWrapMode(WrapMode.WORD);

        // Insert the exception stacktrace
        if (exception.getMessage() != null) {
            buffer.insert(buffer.getIterStart(), exception.getMessage() + "\n");
        }
        buffer.insert(buffer.getIterEnd(), UncaughtExceptionLogger.getStackTrace(exception));

        // Add the textview inside the frame
        frame.add(view);

        // Show everything
        this.showAll();
    }

    @Override
    public void hide() {
        super.hide();
        this.destroy();
    }
}
