/*
 * StatusWidget.java
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
package org.gnome.split.gtk.widget;

import org.gnome.gtk.Frame;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.gnome.gtk.Statusbar;
import org.gnome.gtk.Stock;
import org.gnome.pango.EllipsizeMode;

import static org.freedesktop.bindings.Internationalization._;

/**
 * A custom {@link Statusbar} like to show information about an action.
 * 
 * @author Guillaume Mazoyer
 */
public class StatusWidget extends Frame
{
    /**
     * Display an icon taken from {@link Stock}.
     */
    private Image image;

    /**
     * Display the information.
     */
    private Label text;

    public StatusWidget() {
        super(null);

        // Add a box to the frame
        final HBox box = new HBox(false, 2);
        this.add(box);

        // Add the icon
        image = new Image(Stock.DIALOG_INFO, IconSize.MENU);
        box.packStart(image, false, false, 0);

        // Add the text display
        text = new Label(_("Ready."));
        text.setEllipsize(EllipsizeMode.END);
        box.packStart(text);
    }

    /**
     * Reset the widget to its initial status.
     */
    public void reset() {
        image.setImage(Stock.DIALOG_INFO, IconSize.MENU);
        text.setLabel(_("Ready."));
    }

    /**
     * Update the displayed icon.
     */
    public void updateImage(Stock stock) {
        image.setImage(stock, IconSize.MENU);
    }

    /**
     * Update the displayed text.
     */
    public void updateText(String message) {
        text.setLabel(message);
    }
}
