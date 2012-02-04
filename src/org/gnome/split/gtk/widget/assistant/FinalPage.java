/*
 * FinalPage.java
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

import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.VBox;

/**
 * Custom {@link VBox} to create the last page of the assistant.
 * 
 * @author Guillaume Mazoyer
 */
final class FinalPage extends VBox
{
    /**
     * A widget to display info using {@link Label} labels.
     */
    private ConclusionWidget widget;

    FinalPage(String data) {
        super(false, 3);

        // Set a border
        this.setBorderWidth(5);

        // Add the label
        this.packStart(ActionAssistant.createLeftAlignedLabel(data), false, false, 0);
    }

    /**
     * Set fields names and fields values to display.
     */
    void setFields(String[] fields, String[] values) {
        // Remove the previous widget
        if (widget != null) {
            this.remove(widget);
        }

        // Add the new one
        widget = new ConclusionWidget();
        this.packStart(widget, false, false, 0);

        // Set fields
        widget.setFieldsNames(fields, values);

        // Show the page
        this.showAll();
    }

    /**
     * This is a widget that display info in rows using a name and a value.
     * 
     * @author Guillaume Mazoyer
     */
    private class ConclusionWidget extends VBox
    {
        /**
         * A group to use the same size for several widgets.
         */
        private SizeGroup group;

        private ConclusionWidget() {
            super(false, 3);
            this.group = new SizeGroup(SizeGroupMode.BOTH);
        }

        /**
         * Set fields names and fields values to display.
         */
        private void setFieldsNames(String[] fields, String[] values) {
            for (byte b = 0; b < fields.length; b++) {
                // Pack a box
                final HBox box = new HBox(false, 3);
                this.packStart(box, false, false, 0);

                // Add a field name
                final Label label = ActionAssistant.createLeftAlignedLabel(fields[b]);
                box.packStart(label, false, false, 0);
                group.add(label);

                // Add a field value
                box.packStart(ActionAssistant.createLeftAlignedLabel(values[b]), false, false, 0);
            }
        }
    }
}
