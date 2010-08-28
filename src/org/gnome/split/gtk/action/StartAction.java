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

import org.gnome.gtk.Dialog;
import org.gnome.gtk.Stock;
import org.gnome.split.core.Engine;
import org.gnome.split.core.EngineFactory;
import org.gnome.split.core.utils.SizeUnit;
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
final class StartAction extends Action
{
    protected StartAction(final org.gnome.split.GnomeSplit app) {
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

            long free = widget.checkFreeSpace();
            if (free != -1) {
                // There is not enough space
                Dialog dialog = new ErrorDialog(app.getMainWindow(), _("Not enough space."), _(
                        "There is not enough available space ({0}) in the folder that you selected.",
                        SizeUnit.formatSize(free)));
                dialog.run();
                dialog.hide();
                return;
            }

            byte access = widget.checkFileSystemPermission();
            if (access != 0) {
                // We do not have all the permissions on the file system
                String[] messages = new String[2];

                switch (access) {
                // Can't read
                case 1:
                    messages[0] = _("Can't read on the file system.");
                    messages[1] = _("Can't read the file. Please check the permissions before doing anything.");
                    break;

                // Can't write
                case 2:
                    messages[0] = _("Can't write on the file system.");
                    messages[1] = _("Can't write the file. Please check the permissions before doing anything.");
                    break;

                // Can't read and write
                case 3:
                    messages[0] = _("Can't read and write on the file system.");
                    messages[1] = _("Can't read and write the files. Please check the permissions before doing anything.");
                    break;
                }

                // Show the error dialog
                Dialog dialog = new ErrorDialog(app.getMainWindow(), messages[0], messages[1]);
                dialog.run();
                dialog.hide();
                return;
            }

            // A split is performed
            if (widget instanceof SplitWidget) {
                // Widget related info
                SplitWidget split = (SplitWidget) widget;

                // Create the new process and start it
                run = EngineFactory.createSplitEngine(app, split);
                new Thread(run, "Split - " + split.getFile().getName()).start();
            } else if (widget instanceof MergeWidget) {
                // Widget related info
                MergeWidget merge = (MergeWidget) widget;

                // Create the new process and start it
                run = EngineFactory.createMergeEngine(app, merge);
                new Thread(run, "Merge - " + merge.getFile().getName()).start();
            }

            // Update the interface state
            if (run != null) {
                app.getEngineListener().setEngine(run);
            }
        }
    }
}
