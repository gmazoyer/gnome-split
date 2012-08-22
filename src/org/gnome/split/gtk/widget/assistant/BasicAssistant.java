/*
 * BasicAssistant.java
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
package org.gnome.split.gtk.widget.assistant;

import static org.gnome.split.GnomeSplit.actions;

import org.gnome.gtk.Assistant;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.Label;
import org.gnome.gtk.VBox;
import org.gnome.gtk.WindowPosition;
import org.gnome.split.gtk.action.ActionManager.ActionId;

/**
 * This class is the base for all used assistants.
 * 
 * @author Guillaume Mazoyer
 */
abstract class BasicAssistant extends Assistant implements Assistant.Close, Assistant.Cancel,
        Assistant.Apply, Assistant.Prepare
{
    protected BasicAssistant(String title) {
        super();

        // Set this assistant in the center of the screen
        this.setPosition(WindowPosition.CENTER);

        // Set the title
        this.setTitle(title);

        // Connect signal handlers
        this.connect((Assistant.Close) this);
        this.connect((Assistant.Cancel) this);
        this.connect((Assistant.Apply) this);
        this.connect((Assistant.Prepare) this);
    }

    /**
     * Create a simple label which is aligned to the left and which can use
     * markups.
     */
    protected static Label createLeftAlignedLabel(String text) {
        final Label label = new Label(text);

        label.setUseMarkup(true);
        label.setLineWrap(true);
        label.setAlignment(0.0f, 0.5f);

        return label;
    }

    /**
     * Create a &quot;page&quot;. It is actually just a {@link VBox} with 5
     * pixels as border.
     */
    protected static VBox createPage() {
        final VBox page = new VBox(false, 3);

        page.setBorderWidth(5);

        return page;
    }

    /**
     * Create the introduction of the assistant.
     */
    protected abstract void createIntroduction();

    /**
     * Create the conclusion of the assistant.
     */
    protected abstract void createConclusion();

    /**
     * Update the main interface.
     */
    protected abstract void updateInterface();

    @Override
    public void onCancel(Assistant source) {
        source.hide();
        source.destroy();
    }

    @Override
    public void onClose(Assistant source) {
        source.hide();
        source.destroy();
    }

    @Override
    public void onApply(Assistant source) {
        if (source.getCurrentPage() != 0) {
            // Update the widget
            this.updateInterface();

            // Wait for the interface to be fully updated
            while (Gtk.eventsPending()) {
                Gtk.mainIterationDo(false);
            }

            // Start the split/merge if requested
            actions.activateAction(ActionId.START);

            // Then hide the assistant
            source.hide();
            source.destroy();
        }
    }
}
