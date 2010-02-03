/*
 * ActionAssistant.java
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

import org.gnome.gtk.Alignment;
import org.gnome.gtk.Assistant;
import org.gnome.gtk.VBox;

/**
 * Interface which defines a model for action assistant.
 * 
 * @author Guillaume Mazoyer
 */
interface ActionAssistant
{
    /**
     * Method to create the first page of the assistant.
     */
    void createIntroduction();

    /**
     * Method to update the right widget using the previously gathered data.
     */
    void updateWidget();

    /**
     * A custom widget which will be used as a page for an {@link Assistant}.
     * 
     * @author Guillaume Mazoyer
     */
    class Page extends Alignment
    {
        /**
         * The main container.
         */
        VBox container;

        Page() {
            super(0f, 0f, 0f, 0f);

            // Set the margins
            this.setPadding(12, 12, 12, 12);

            // Add the main container
            container = new VBox(false, 5);
            this.add(container);
        }
    }
}
