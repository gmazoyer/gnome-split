/*
 * SelectView.java
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

import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.RadioButton;
import org.gnome.gtk.RadioButtonGroup;
import org.gnome.gtk.ToggleButton;
import org.gnome.gtk.Widget;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.MainWindow;

import static org.freedesktop.bindings.Internationalization._;

/**
 * A {@link Widget} derived from {@link HBox} to handle views in the main
 * interface. It contains few {@link RadioButton RadioButtons}.
 * 
 * @author Guillaume Mazoyer
 */
public class SelectView extends HBox
{
    /**
     * Current instance of GNOME Split.
     */
    private GnomeSplit app;

    /**
     * Button used to select the split view.
     */
    private RadioButton split;

    /**
     * Button used to select the merge view.
     */
    private RadioButton merge;

    public SelectView(final GnomeSplit app) {
        super(false, 10);

        // Save instance
        this.app = app;

        // Set width of the borders
        this.setBorderWidth(5);

        // Just a label to know what the buttons are used for
        final Label label = new Label(_("View:"));
        this.packStart(label, false, false, 0);

        // Buttons group
        final RadioButtonGroup group = new RadioButtonGroup();

        // Split action - switch to split view
        this.split = new RadioButton(group, _("Split"));
        this.packStart(this.split, false, false, 0);

        // Connect signal handler for split action
        this.split.connect(new ToggleButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                // Get the main window
                MainWindow window = app.getMainWindow();

                // If the merge widget is shown
                if (window.getActionWidget() instanceof MergeWidget) {
                    if (source.getActive()) {
                        // Switch the view
                        window.switchView();
                    }
                }
            }
        });

        // Merge action - switch to merge view
        this.merge = new RadioButton(group, _("Merge"));
        this.packStart(this.merge, false, false, 0);

        // Connect signal handler for merge action
        this.merge.connect(new ToggleButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                // Get the main window
                MainWindow window = app.getMainWindow();

                // If the split widget is shown
                if (window.getActionWidget() instanceof SplitWidget) {
                    if (source.getActive()) {
                        // Switch the view
                        window.switchView();
                    }
                }
            }
        });
    }

    /**
     * Disable all buttons so the user will not be able to switch the view.
     */
    public void disable() {
        split.setSensitive(false);
        merge.setSensitive(false);
    }

    /**
     * Enable all buttons so the user will be able to switch the view.
     */
    public void enable() {
        split.setSensitive(true);
        merge.setSensitive(true);
    }

    /**
     * Switch the view to the split.
     */
    public void switchToSplit() {
        if (app.getMainWindow().getActionWidget() instanceof MergeWidget) {
            split.activate();
        }
    }

    /**
     * Switch the view to the merge.
     */
    public void switchToMerge() {
        if (app.getMainWindow().getActionWidget() instanceof SplitWidget) {
            merge.activate();
        }
    }
}
