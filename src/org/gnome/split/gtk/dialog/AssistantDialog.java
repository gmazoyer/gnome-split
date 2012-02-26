/*
 * AssistantDialog.java
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

import org.gnome.gdk.Event;
import org.gnome.gtk.Assistant;
import org.gnome.gtk.Button;
import org.gnome.gtk.ButtonBoxStyle;
import org.gnome.gtk.CheckButton;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.Label;
import org.gnome.gtk.RadioButton;
import org.gnome.gtk.RadioGroup;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.gtk.VButtonBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.widget.assistant.MergeAssistant;
import org.gnome.split.gtk.widget.assistant.SplitAssistant;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to build the dialog to choose the assistant to use.
 * 
 * @author Guillaume Mazoyer
 */
public class AssistantDialog extends Dialog implements Window.DeleteEvent, Dialog.Response
{
    /**
     * Current instance of the application.
     */
    private GnomeSplit app;

    /**
     * Button to popup the split assistant.
     */
    private RadioButton split;

    /**
     * Button to popup the merge assistant.
     */
    private RadioButton merge;

    public AssistantDialog(final GnomeSplit app) {
        super(_("Assistant"), app.getMainWindow(), false);

        this.app = app;

        // This dialog should be modal
        this.setModal(true);

        // Border width
        this.setBorderWidth(12);

        // Add the label
        final Label label = new Label(_("What do you want to do?"));
        label.setUseMarkup(true);
        label.setLineWrap(true);
        label.setAlignment(0.0f, 0.5f);
        this.add(label);

        // Create a box to pack the two choices
        final VButtonBox box = new VButtonBox();
        box.setBorderWidth(12);
        box.setLayout(ButtonBoxStyle.SPREAD);
        this.add(box);

        // Create the two choices
        final RadioGroup group = new RadioGroup();
        split = new RadioButton(group, _("Split a file"));
        merge = new RadioButton(group, _("Merge several files"));

        // Add them to the page
        box.packStart(split, false, false, 0);
        box.packStart(merge, false, false, 0);

        // Add a button to turn on/off the assistant on start
        final CheckButton assistant = new CheckButton(_("_Show the assistant on start"));
        assistant.setActive(app.getConfig().ASSISTANT_ON_START);
        this.add(assistant);

        // Connect check button signal
        assistant.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                app.getConfig().ASSISTANT_ON_START = assistant.getActive();
                app.getConfig().savePreferences();
            }
        });

        // Close button (save the configuration and close)
        this.addButton(Stock.CLOSE, ResponseType.CLOSE);
        this.addButton(Stock.OK, ResponseType.OK);

        // Connect classic signals
        this.connect((Window.DeleteEvent) this);
        this.connect((Dialog.Response) this);
    }

    @Override
    public void present() {
        this.showAll();
        super.present();
    }

    @Override
    public void onResponse(Dialog source, ResponseType response) {
        if (response == ResponseType.OK) {
            final Assistant assistant;

            if (split.getActive()) {
                assistant = new SplitAssistant(app);
            } else {
                assistant = new MergeAssistant(app);
            }

            // Focus on the assistant
            assistant.showAll();
        }

        // Hide the dialog
        this.hide();
        this.destroy();
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.CLOSE);
        return false;
    }
}
