/*
 * StartAction.java
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
package org.gnome.split.gtk.action;

import java.io.File;
import java.lang.reflect.Constructor;

import org.gnome.gtk.Dialog;
import org.gnome.gtk.Stock;
import org.gnome.split.core.Engine;
import org.gnome.split.core.merger.DefaultMergeEngine;
import org.gnome.split.core.splitter.Generic;
import org.gnome.split.core.splitter.GnomeSplit;
import org.gnome.split.core.splitter.Xtremsplit;
import org.gnome.split.gtk.dialog.ErrorDialog;
import org.gnome.split.gtk.widget.ActionWidget;
import org.gnome.split.gtk.widget.MergeWidget;
import org.gnome.split.gtk.widget.SplitWidget;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to start a split/merge.
 * 
 * @author Guillaume Mazoyer
 */
public final class StartAction extends Action
{
    public StartAction(final org.gnome.split.GnomeSplit app) {
        super(app, "start-action", _("_Start"), _("Start this action."), Stock.MEDIA_PLAY);
    }

    @Override
    public void onActivate(org.gnome.gtk.Action source) {
        // Get current instance and current widget
        org.gnome.split.GnomeSplit app = this.getApplication();
        Engine engine = app.getEngineListener().getEngine();

        // Action already started and paused
        if ((engine != null) && engine.paused()) {
            // Then resume it
            engine.resume();
            app.getActionManager().setRunningState();
        } else {
            // Get current widget
            ActionWidget widget = app.getMainWindow().getActionWidget();
            Engine run = null;

            if (!widget.isFullyFilled()) {
                // The user did not fill all the fields
                Dialog dialog = new ErrorDialog(app.getMainWindow(), _("Incompleted fields."),
                        _("You must fill all fields to start an action."));
                dialog.run();
                dialog.hide();
                return;
            }

            // These classes are our splitter classes
            Class<?>[] splitters = new Class[] {
                    GnomeSplit.class, Xtremsplit.class, Generic.class
            };

            // A split is performed
            if (widget instanceof SplitWidget) {
                // Widget related info
                SplitWidget split = (SplitWidget) widget;

                // Split related info
                File file = new File(split.getFilename());
                long size = split.getMaxSize();
                String dest = split.getDestination();
                int algorithm = split.getAlgorithm();

                Constructor<?> constructor;
                try {
                    // Get the class constructor
                    constructor = splitters[algorithm].getConstructor(org.gnome.split.GnomeSplit.class,
                            File.class, long.class, String.class);

                    // Create the runnable object
                    run = (Engine) constructor.newInstance(app, file, size, dest);

                    // Finally, start the thread
                    new Thread(run, "Split - " + file.getName()).start();
                } catch (Exception e) {
                    // Should *never* happen
                    e.printStackTrace();
                }
            } else if (widget instanceof MergeWidget) {
                // Widget related info
                MergeWidget merge = (MergeWidget) widget;

                // Split related info
                File file = merge.getFirstFile();
                String dest = merge.getDestination();

                // Create the new process and start it
                run = DefaultMergeEngine.getInstance(app, file, dest);
                new Thread(run, "Merge - " + file.getName()).start();
            }

            // Update the interface state
            if (run != null) {
                app.getEngineListener().setEngine(run);
                app.getActionManager().setRunningState();
            }
        }
    }
}
