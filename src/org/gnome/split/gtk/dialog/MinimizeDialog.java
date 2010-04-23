/*
 * MinimizeDialog.java
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

import org.gnome.gdk.Event;
import org.gnome.gtk.Button;
import org.gnome.gtk.ButtonsType;
import org.gnome.gtk.CheckButton;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.MessageDialog;
import org.gnome.gtk.MessageType;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Dialog.Response;
import org.gnome.gtk.Window.DeleteEvent;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.widget.AreaStatusIcon;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to build dialog to ask the user if he want to minimize
 * or quit the program.
 * 
 * @author Guillaume Mazoyer
 */
public final class MinimizeDialog extends MessageDialog implements DeleteEvent, Response
{
    /**
     * The current GNOME Split instance.
     */
    private GnomeSplit app;

    /**
     * Button to check if the user don't want the dialog to pop up another
     * time.
     */
    private CheckButton ask;

    public MinimizeDialog(final GnomeSplit app) {
        super(app.getMainWindow(), false, MessageType.QUESTION, ButtonsType.NONE,
                _("What do you want to do?\nQuit GNOME Split or minimize the window?"));

        // Save the instance
        this.app = app;

        // Add a check button
        ask = new CheckButton(_("Do not ask me again."));
        this.add(ask);

        // Add the minimize button
        final Button minimize = this.addButton(_("_Minimize"), ResponseType.NO);
        minimize.setImage(new Image(Stock.LEAVE_FULLSCREEN, IconSize.BUTTON));

        // Add the quit button
        this.addButton(Stock.QUIT, ResponseType.CLOSE);

        // Connect the signals
        this.connect((DeleteEvent) this);
        this.connect((Response) this);
    }

    @Override
    public void present() {
        this.showAll();
        super.present();
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.DELETE_EVENT);
        return false;
    }

    @Override
    public void onResponse(Dialog source, ResponseType response) {
        if (response == ResponseType.CLOSE) {
            if (ask.getActive()) {
                // Remember the choice
                app.getConfig().CLOSE_BEHAVIOR = 1;
                app.getConfig().savePreferences();
            }

            // Quit the program
            app.quit();
        }

        if (response == ResponseType.NO) {
            if (ask.getActive()) {
                // Remember the choice
                app.getConfig().CLOSE_BEHAVIOR = 2;
                app.getConfig().savePreferences();
            }

            // Minimize the window
            AreaStatusIcon icon = app.getMainWindow().getAreaStatusIcon();
            icon.onActivate(icon);
        }

        // Hide the dialog
        this.hide();
    }
}
