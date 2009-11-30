/*
 * StartAction.java
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

import java.io.File;

import org.gnome.gtk.Dialog;
import org.gnome.gtk.Stock;
import org.gnome.split.GnomeSplit;
import org.gnome.split.core.Engine;
import org.gnome.split.core.splitter.Xtremsplit;
import org.gnome.split.core.utils.Algorithm;
import org.gnome.split.gtk.dialog.ErrorDialog;
import org.gnome.split.gtk.widget.ActionWidget;
import org.gnome.split.gtk.widget.SplitWidget;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Action to start a split/merge.
 * 
 * @author Guillaume Mazoyer
 */
public final class StartAction extends Action
{
    public StartAction(final GnomeSplit app) {
        super(app, Stock.MEDIA_PLAY, _("Start"));
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        // Get current instance and current widget
        GnomeSplit app = this.getApplication();
        Engine engine = app.getEngineListener().getEngine();

        // Action already started and paused
        if ((engine != null) && engine.paused()) {
            // Then resume it
            engine.resume();
        } else {
            // Get current widget
            ActionWidget widget = app.getMainWindow().getActionWidget();

            if (!widget.isFullyFilled()) {
                // The user did not fill all the fields
                Dialog dialog = new ErrorDialog(app.getMainWindow(), _("Incompleted fields."),
                        _("You must fill all fields to start a split."));
                dialog.run();
                dialog.hide();
                return;
            }

            // A split is performed
            if (widget instanceof SplitWidget) {
                // Widget related info
                SplitWidget split = (SplitWidget) widget;
                int algorithm = split.getAlgorithm();

                // Split related info
                File file = new File(split.getFilename());
                long size = split.getMaxSize();
                String dest = split.getDestination();

                switch (algorithm) {
                case Algorithm.XTREMSPLIT:
                    // Create the new process and start it
                    Engine run = new Xtremsplit(app, file, size, dest);
                    app.getEngineListener().setEngine(run);
                    new Thread(run, "Split - " + file.getName()).start();
                    break;
                default:
                    break;
                }
            }
        }
    }
}
