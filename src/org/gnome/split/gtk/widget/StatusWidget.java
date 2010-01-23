/*
 * StatusWidget.java
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
package org.gnome.split.gtk.widget;

import org.gnome.gtk.Frame;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.gnome.gtk.Statusbar;
import org.gnome.gtk.Stock;
import org.gnome.gtk.VSeparator;
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
     * Display an icon taken from {@link Stock} with a tooltip to show the
     * speed.
     */
    private Image speed;

    /**
     * Display the information.
     */
    private Label text;

    public StatusWidget() {
        super(null);

        // Add a box to the frame
        final HBox box = new HBox(false, 5);
        this.add(box);

        // Add the icon
        image = new Image(Stock.DIALOG_INFO, IconSize.MENU);
        box.packStart(image, false, false, 0);

        // Add a first separator
        box.packStart(new VSeparator(), false, false, 0);

        // Add the text display
        text = new Label(_("Ready."));
        text.setEllipsize(EllipsizeMode.END);
        box.packStart(text);

        // Add a second separator
        box.packStart(new VSeparator(), false, false, 0);

        // Add the speed display
        speed = new Image(Stock.HARDDISK, IconSize.MENU);
        speed.setTooltipText(_("Unknown speed"));
        box.packStart(speed, false, false, 0);
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

    /**
     * Update the displayed speed.
     */
    public void updateSpeed(String value) {
        String tooltip = (value == null) ? _("Unknown speed") : _("Speed") + " " + value;
        speed.setTooltipText(tooltip);
    }

    /**
     * Reset the widget to its initial status.
     */
    public void reset() {
        this.updateImage(Stock.DIALOG_INFO);
        this.updateText(_("Ready."));
        this.updateSpeed(null);
    }

    /**
     * Update the displayed icon and text.
     */
    public void update(Stock stock, String message) {
        this.updateImage(stock);
        this.updateText(message);
    }

    /**
     * Update the displayed icon, text and speed.
     */
    public void update(Stock stock, String message, String speed) {
        this.updateImage(stock);
        this.updateText(message);
        this.updateSpeed(speed);
    }
}
