/*
 * ToggleAction.java
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

import org.gnome.gtk.CheckMenuItem;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.RadioButton;
import org.gnome.gtk.RadioButtonGroup;
import org.gnome.gtk.ToggleButton;
import org.gnome.gtk.Widget;
import org.gnome.split.GnomeSplit;

/**
 * Abstract class to define a action triggered by a GTK+ toggle/radio/check
 * widget.
 * 
 * @author Guillaume Mazoyer
 */
public abstract class ToggleAction
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
     * The state of the action.
     */
    private boolean active;

    /**
     * Create a new action using a label, a tooltip and a state.
     */
    public ToggleAction(GnomeSplit app, String label, String tooltip, boolean active) {
        this.app = app;
        this.label = label;
        this.tooltip = tooltip;
        this.active = active;
    }

    /**
     * Create a new action using a label and a state.
     */
    public ToggleAction(GnomeSplit app, String label, boolean active) {
        this(app, label, null, active);
    }

    /**
     * Used when a widget related to this action is used.
     */
    public abstract void actionPerformed(ToggleActionEvent event, boolean active);

    /**
     * Create a new {@link MenuItem} related to this action.
     */
    public CheckMenuItem createCheckMenuItem() {
        CheckMenuItem item = new CheckMenuItem(label);

        // Set tooltip if there is one and active state
        if (tooltip != null) {
            item.setTooltipText(tooltip);
        }

        // Set the state of the widget using the state of the action
        item.setActive(active);

        // Connect signal and event use
        item.connect(new CheckMenuItem.Toggled() {
            @Override
            public void onToggled(CheckMenuItem source) {
                ToggleActionEvent event = new ToggleActionEvent(source);
                actionPerformed(event, active);
            }
        });

        return item;
    }

    /**
     * Create a new {@link RadioButton} related to this action.
     */
    public RadioButton createRadioButton(RadioButtonGroup group) {
        RadioButton button = new RadioButton(group, label);

        // Set tooltip if there is one and active state
        if (tooltip != null) {
            button.setTooltipText(tooltip);
        }

        // Set the state of the widget using the state of the action
        button.setActive(active);

        // Connect signal and event use
        button.connect(new RadioButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                ToggleActionEvent event = new ToggleActionEvent(source);
                actionPerformed(event, active);
            }
        });

        return button;
    }

    /**
     * Get the current program instance.
     */
    protected GnomeSplit getApplication() {
        return app;
    }

    /**
     * Change the state of this action.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Event generated when the associated action is triggered.
     * 
     * @author Guillaume Mazoyer
     */
    public class ToggleActionEvent
    {
        /**
         * The widget which created the event.
         */
        private Widget item;

        /**
         * Create a new event using a {@link Widget widget}.
         */
        public ToggleActionEvent(Widget item) {
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
