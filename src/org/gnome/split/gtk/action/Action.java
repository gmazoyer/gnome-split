/*
 * Action.java
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
package org.gnome.split.gtk.action;

import java.util.ArrayList;
import java.util.List;

import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.ImageMenuItem;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.Stock;
import org.gnome.gtk.ToolButton;
import org.gnome.gtk.ToolItem;
import org.gnome.gtk.Widget;
import org.gnome.split.GnomeSplit;

/**
 * Abstract class to define a action triggered by a GTK+ widget.
 * 
 * @author Guillaume Mazoyer
 */
public abstract class Action
{
    /**
     * The current instance of GNOME Split.
     */
    private GnomeSplit app;

    /**
     * The label of the action.
     */
    private String label;

    /**
     * The tooltip of the action.
     */
    private String tooltip;

    /**
     * The {@link Stock} item attached to the action.
     */
    private Stock stock;

    /**
     * The widgets attached to the action.
     */
    private List<Widget> widgets;

    /**
     * Create a new action using a label, a tooltip and a {@link Stock} item.
     */
    public Action(GnomeSplit app, String label, String tooltip, Stock stock) {
        this.app = app;
        this.label = label;
        this.tooltip = tooltip;
        this.stock = stock;
        this.widgets = new ArrayList<Widget>();
    }

    /**
     * Create a new action using a {@link Stock} item and a label.
     */
    public Action(GnomeSplit app, Stock stock, String label) {
        this(app, label, null, stock);
    }

    /**
     * Create a new action using a {@link Stock} item.
     */
    public Action(GnomeSplit app, Stock stock) {
        this(app, null, null, stock);
    }

    /**
     * Get the current program instance.
     */
    protected final GnomeSplit getApplication() {
        return app;
    }

    /**
     * Used when a widget related to this action is used.
     */
    public abstract void actionPerformed(ActionEvent event);

    /**
     * Create a new {@link MenuItem} related to this action.
     */
    public MenuItem createMenuItem() {
        MenuItem item = null;

        if ((stock != null) && (label != null)) {
            // Create a menu item with a label and an image
            Image image = new Image(stock, IconSize.MENU);
            item = new ImageMenuItem(image, label);
        } else if (stock != null) {
            // Create a menu item with an image
            item = new ImageMenuItem(stock);
        } else if (label != null) {
            // Create a menu item with a label
            item = new MenuItem(label);
        }

        // Set menu item tooltip
        if (tooltip != null) {
            item.setTooltipText(tooltip);
        }

        // Connect the activate handler
        item.connect(new MenuItem.Activate() {
            @Override
            public void onActivate(MenuItem source) {
                ActionEvent event = new ActionEvent(source);
                actionPerformed(event);
            }
        });

        // Register the widget
        widgets.add(item);

        return item;
    }

    /**
     * Create a new {@link ToolItem} related to this action.
     */
    public ToolItem createToolItem() {
        // Cannot build a tool button without stock item
        if (stock == null) {
            throw new NullPointerException(
                    "It is not possible to create a GtkToolButton without any GtkStock item.");
        }

        // Create a tool item with a Stock
        ToolItem item = new ToolButton(stock);

        // Set a label to the item
        if (label != null) {
            ((ToolButton) item).setLabel(label);
        }

        // Set menu item tooltip
        if (tooltip != null) {
            item.setTooltipText(tooltip);
        }

        // Connect the activate handler
        ((ToolButton) item).connect(new ToolButton.Clicked() {
            @Override
            public void onClicked(ToolButton source) {
                ActionEvent event = new ActionEvent(source);
                actionPerformed(event);
            }
        });

        // Register the widget
        widgets.add(item);

        return item;
    }

    /**
     * Change the sensitive state of the widgets related to this action.
     */
    public void setActive(boolean setting) {
        for (Widget widget : widgets) {
            widget.setSensitive(setting);
        }
    }

    /**
     * Event generated when the associated action is triggered.
     * 
     * @author Guillaume Mazoyer
     */
    public class ActionEvent
    {
        /**
         * The widget which created the event.
         */
        private Widget item;

        /**
         * Create a new event using a {@link Widget widget}.
         */
        public ActionEvent(Widget item) {
            this.item = item;
        }

        /**
         * Get the current widget.
         */
        public Widget getWidget() {
            return item;
        }
    }
}
