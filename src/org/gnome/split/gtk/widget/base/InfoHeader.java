/*
 * InfoWidget.java
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
package org.gnome.split.gtk.widget.base;

import org.gnome.gtk.InfoBar;
import org.gnome.gtk.Justification;
import org.gnome.gtk.Label;
import org.gnome.gtk.MessageType;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.split.GnomeSplit;

/**
 * A widget derived from {@link InfoBar} to display information in the
 * interface without showing an entire dialog.
 * 
 * @author Guillaume Mazoyer
 */
public class InfoHeader extends InfoBar implements InfoBar.Close, InfoBar.Response
{
    /**
     * Current GNOME Split instance.
     */
    private GnomeSplit app;

    /**
     * Widget to display text.
     */
    private Label label;

    public InfoHeader(final GnomeSplit app) {
        super();

        // Save instance
        this.app = app;

        // Add a close button
        this.addButton(Stock.CLOSE, ResponseType.CLOSE);

        // Create the label to display text
        this.label = new Label();
        this.label.setLineWrap(true);
        this.label.setUseMarkup(true);
        this.label.setJustify(Justification.LEFT);

        // Add the label
        this.add(this.label);

        // Connect signals
        this.connect((InfoBar.Close) this);
        this.connect((InfoBar.Response) this);
    }

    @Override
    public void hide() {
        super.hide();

        // Resize the window
        app.getMainWindow().resize();
    }

    /**
     * Show a message to inform the people from something normal.
     */
    public void showInfo(String title, String body) {
        // Set the message type
        this.setMessageType(MessageType.INFO);

        // Set the default response
        this.setDefaultResponse(ResponseType.CLOSE);

        // Set the text of the label
        label.setLabel("<b>" + title + "</b>\n" + body);

        // Show the info bar
        this.showAll();
    }

    /**
     * Show a message to inform the people from a warning.
     */
    public void showWarning(String title, String body) {
        // Set the message type
        this.setMessageType(MessageType.WARNING);

        // Set the default response
        this.setDefaultResponse(ResponseType.CLOSE);

        // Set the text of the label
        label.setLabel("<b>" + title + "</b>\n" + body);

        // Show the info bar
        this.showAll();
    }

    @Override
    public void onClose(InfoBar source) {
        // Just hide the info bar
        this.hide();
    }

    @Override
    public void onResponse(InfoBar source, ResponseType response) {
        if (response == ResponseType.CLOSE) {
            // Just hide the info bar
            this.hide();
        }
    }
}
