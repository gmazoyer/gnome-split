/*
 * AssistantDialog.java
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
package org.gnome.split.gtk.dialog;

import org.gnome.gdk.Event;
import org.gnome.gtk.Assistant;
import org.gnome.gtk.ButtonBoxStyle;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.Label;
import org.gnome.gtk.RadioButton;
import org.gnome.gtk.RadioGroup;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Stock;
import org.gnome.gtk.ToggleButton;
import org.gnome.gtk.VBox;
import org.gnome.gtk.VButtonBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.WindowPosition;
import org.gnome.gtk.Dialog.Response;
import org.gnome.gtk.Window.DeleteEvent;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.widget.MergeAssistant;
import org.gnome.split.gtk.widget.SplitAssistant;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to build a dialog to allow the user to choose what
 * assistant he/she wants to use.
 * 
 * @author Guillaume Mazoyer
 */
public class AssistantDialog extends Dialog implements DeleteEvent, Response
{
    /**
     * The current GNOME Split instance.
     */
    private GnomeSplit app;

    /**
     * The assistant to use.
     */
    private Class<?> assistant;

    public AssistantDialog(final GnomeSplit app) {
        super(_("Assistant chooser"), app.getMainWindow(), false);

        // Save the instance
        this.app = app;

        // Default choice: split assistant
        this.assistant = SplitAssistant.class;

        // Center this dialog using the position of its parent
        this.setPosition(WindowPosition.CENTER_ON_PARENT);

        // Add a border
        this.setBorderWidth(5);

        // Main container
        final VBox container = new VBox(false, 5);
        this.add(container);

        // Add a label to tell the use what to do
        final Label label = new Label(_("What assistant do you want to use?"));
        container.packStart(label, false, false, 0);

        // Add container for the buttons
        final VButtonBox box = new VButtonBox();
        box.setLayout(ButtonBoxStyle.SPREAD);
        container.packStart(box, true, true, 0);

        // Buttons group
        final RadioGroup group = new RadioGroup();

        // Split assistant selector
        final RadioButton split = new RadioButton(group, _("Split assistant"));
        box.packStart(split, true, true, 0);

        // Handle the signal for the split selector
        split.connect(new ToggleButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                if (source.getActive()) {
                    assistant = SplitAssistant.class;
                }
            }
        });

        // Merge assistant selector
        final RadioButton merge = new RadioButton(group, _("Merge assistant"));
        box.packStart(merge, true, true, 0);

        // Handle the signal for the merge selector
        merge.connect(new ToggleButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                if (source.getActive()) {
                    assistant = MergeAssistant.class;
                }
            }
        });

        // Add the buttons
        this.addButton(Stock.CANCEL, ResponseType.CANCEL);
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
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.CLOSE);
        return false;
    }

    @Override
    public void onResponse(Dialog source, ResponseType response) {
        if (response == ResponseType.OK) {
            final Assistant choice;

            // The user wants to popup an assistant
            try {
                choice = (Assistant) assistant.getConstructor(GnomeSplit.class).newInstance(app);

                // Show the assistant
                choice.showAll();
            } catch (Exception e) {
                // We should never go here
                e.printStackTrace();
            }
        }

        // Hide the dialog
        this.hide();
    }
}
