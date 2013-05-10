/*
 * DefaultEngineListener.java
 * 
 * Copyright (c) 2009-2013 Guillaume Mazoyer
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
package org.gnome.split.gtk;

import static org.freedesktop.bindings.Internationalization._;
import static org.gnome.split.GnomeSplit.actions;
import static org.gnome.split.GnomeSplit.config;
import static org.gnome.split.GnomeSplit.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.gnome.glib.Glib;
import org.gnome.glib.Handler;
import org.gnome.gtk.Dialog;
import org.gnome.notify.Notification;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Constants;
import org.gnome.split.core.Engine;
import org.gnome.split.core.EngineListener;
import org.gnome.split.core.exception.EngineException;
import org.gnome.split.core.exception.ExceptionMessage;
import org.gnome.split.core.splitter.DefaultSplitEngine;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.gtk.action.ActionManager.ActionId;
import org.gnome.split.gtk.dialog.ErrorDialog;

/**
 * Manage the view update of the application. We *must not* set the engine
 * without using the {@link #setEngine(Engine) setEngine()} method.
 * 
 * @author Guillaume Mazoyer
 */
public class DefaultEngineListener implements EngineListener
{
    /**
     * The current engine (action).
     */
    private Engine engine;

    /**
     * A timer to schedule an action.
     */
    private Timer timer;

    /**
     * List of files that have been created.
     */
    private List<String> files;

    /**
     * Create a new implementation of the {@link EngineListener engine
     * listener}.
     */
    public DefaultEngineListener() {
        this.engine = null;
        this.timer = null;
        this.files = new ArrayList<String>();
    }

    @Override
    public void setEngine(final Engine another) {
        // Update engine
        this.engine = another;

        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                if (engine != null) {
                    // Inhibit hibernation if requested
                    if (config.NO_HIBERNATION) {
                        GnomeSplit.inhibit();
                    }

                    // Disable user interaction (only in action widget)
                    ui.getActionWidget().disable();
                    ui.getViewSwitcher().disable();

                    // Update the status icon tooltip
                    ui.getAreaStatusIcon().updateText(engine.toString());

                    // Update the interface state
                    engineRunning();
                } else {
                    // Uninhibit hibernation if requested
                    if (GnomeSplit.isInhibited()) {
                        GnomeSplit.unInhibit();
                    }

                    // Enable user interaction (only in action widget)
                    ui.getActionWidget().enable();
                    ui.getViewSwitcher().enable();

                    // Reset the window's title
                    ui.setTitle(null);

                    // Reset the status bar message
                    ui.getAreaStatusIcon().updateText(null);

                    // Reset the status bar speed indicator
                    ui.getStatusWidget().updateSpeed(null);

                    // Update the interface state
                    engineReady();
                }

                return false;
            }
        });
    }

    @Override
    public Engine getEngine() {
        return engine;
    }

    @Override
    public void engineSpeedChanged(final long speed) {
        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Update the status widget
                ui.getStatusWidget().updateSpeed((speed == -1) ? null : SizeUnit.formatSpeed(speed));
                return false;
            }
        });
    }

    @Override
    public void enginePartCreated(final String filename) {
        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Update the status widget
                ui.getStatusWidget().updateText(_("Writing {0}.", filename));
                return false;
            }
        });
    }

    @Override
    public void enginePartWritten(final String filename) {

    }

    @Override
    public void enginePartRead(final String filename) {
        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Update the status widget
                ui.getStatusWidget().updateText(_("Reading {0}.", filename));
                return false;
            }
        });
    }

    @Override
    public void engineMD5SumStarted() {
        if (timer == null) {
            // Start the timer
            timer = new Timer();
            timer.scheduleAtFixedRate(new PulseProgress(), 0, 250);

            Glib.idleAdd(new Handler() {
                @Override
                public boolean run() {
                    // Update the status widget
                    ui.getStatusWidget().updateText(_("MD5 sum calculation."));
                    return false;
                }
            });
        }
    }

    @Override
    public void engineMD5SumEnded() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Now update the widgets
                ui.getActionWidget().updateProgress(1, "", true);
                return false;
            }
        });
    }

    @Override
    public void engineReady() {
        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Update the actions
                actions.getAction(ActionId.ASSISTANT).setSensitive(true);
                actions.getAction(ActionId.SEND_EMAIL).setSensitive(!files.isEmpty());
                actions.getAction(ActionId.START).setSensitive(true);
                actions.getAction(ActionId.PAUSE).setSensitive(false);
                actions.getAction(ActionId.CANCEL).setSensitive(false);
                actions.getAction(ActionId.DELETE).setSensitive(false);
                actions.getAction(ActionId.CLEAR).setSensitive(true);
                actions.getRadioAction(ActionId.SPLIT).setSensitive(true);
                actions.getRadioAction(ActionId.MERGE).setSensitive(true);

                // Update the cursor
                ui.setCursorWorkingState(false);

                return false;
            }
        });
    }

    @Override
    public void engineRunning() {
        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Update the actions
                actions.getAction(ActionId.ASSISTANT).setSensitive(false);
                actions.getAction(ActionId.SEND_EMAIL).setSensitive(false);
                actions.getAction(ActionId.START).setSensitive(false);
                actions.getAction(ActionId.PAUSE).setSensitive(true);
                actions.getAction(ActionId.CANCEL).setSensitive(true);
                actions.getAction(ActionId.DELETE).setSensitive(true);
                actions.getAction(ActionId.CLEAR).setSensitive(false);
                actions.getRadioAction(ActionId.SPLIT).setSensitive(false);
                actions.getRadioAction(ActionId.MERGE).setSensitive(false);

                // Update the cursor
                ui.setCursorWorkingState(true);

                return false;
            }
        });
    }

    @Override
    public void engineSuspended() {
        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Update the actions
                actions.getAction(ActionId.ASSISTANT).setSensitive(false);
                actions.getAction(ActionId.SEND_EMAIL).setSensitive(false);
                actions.getAction(ActionId.START).setSensitive(true);
                actions.getAction(ActionId.PAUSE).setSensitive(false);
                actions.getAction(ActionId.CANCEL).setSensitive(true);
                actions.getAction(ActionId.DELETE).setSensitive(true);
                actions.getAction(ActionId.CLEAR).setSensitive(false);
                actions.getRadioAction(ActionId.SPLIT).setSensitive(false);
                actions.getRadioAction(ActionId.MERGE).setSensitive(false);

                // Update the cursor
                ui.setCursorWorkingState(false);

                return false;
            }
        });
    }

    @Override
    public void engineEnded() {
        // Title and body of the message to display
        final String title;
        final String body;
        if (engine instanceof DefaultSplitEngine) {
            title = _("Split finished.");
            body = _("The file was successfully split.");
        } else {
            title = _("Merge finished.");
            body = _("The files were successfully merged.");
        }

        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Update the status widget
                ui.getStatusWidget().updateText(title);
                ui.getStatusWidget().scheduleTimeout(5);

                if (!config.USE_NOTIFICATION) {
                    // Use simple info bar
                    ui.getInfoBar().showInfo(title, body);
                } else {
                    // Use notification
                    Notification notify = new Notification(title, body, null);
                    notify.setIcon(Constants.PROGRAM_LOGO);
                    notify.show();
                }

                return false;
            }
        });

        // Update engine
        this.setEngine(null);
    }

    @Override
    public void engineStopped() {
        // Use the correct text
        final String text;
        if (engine instanceof DefaultSplitEngine) {
            text = _("Split stopped.");
        } else {
            text = _("Merge stopped.");
        }

        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Update the status widget
                ui.getStatusWidget().updateText(text);
                ui.getStatusWidget().scheduleTimeout(5);

                return false;
            }
        });

        // Update engine
        this.setEngine(null);
    }

    @Override
    public void engineError(final Exception exception) {
        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                Dialog dialog = null;

                if (exception instanceof EngineException) {
                    EngineException error = (EngineException) exception;
                    ExceptionMessage message = error.getExceptionMessage();

                    if (error.isWarning()) {
                        // Warning only (file *may* work)
                        ui.getInfoBar().showWarning(message.getMessage(), message.getDetails());
                    } else {
                        // Invalid size exception
                        dialog = new ErrorDialog(ui, message.getMessage(), message.getDetails());
                    }
                } else {
                    // First print the stacktrace
                    exception.printStackTrace();

                    // Other exception - error (file is supposed broken)
                    dialog = new ErrorDialog(
                            ui,
                            _("Unhandled exception."),
                            _("An exception occurs. You can report it to the developers and tell them how to reproduce it.\n\nSee the details for more information."),
                            exception);
                }

                // Update the status widget
                ui.getStatusWidget().updateText(exception.getMessage());
                ui.getStatusWidget().scheduleTimeout(8);

                if (dialog != null) {
                    // Display the dialog
                    dialog.run();
                    dialog.hide();
                }

                return false;
            }
        });

        // Update engine
        this.setEngine(null);
    }

    @Override
    public void engineDone(final long done, final long total) {
        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Format the sizes to display them in the widget
                String text = SizeUnit.formatSize(done) + " / " + SizeUnit.formatSize(total);
                double value = (double) done / (double) total;

                // Now update the widgets
                ui.setTitle(String.valueOf((int) (value * 100)) + "%");
                ui.getActionWidget().updateProgress(value, text, true);

                return false;
            }
        });
    }

    @Override
    public void engineFilesList(final List<String> list) {
        // First, clear the list
        files.clear();

        Glib.idleAdd(new Handler() {
            @Override
            public boolean run() {
                // Set action sensitiveness
                actions.getAction(ActionId.SEND_EMAIL).setSensitive(list != null);
                return false;
            }
        });

        if (list != null) {
            // Copy all elements from the first list to the new one
            files.addAll(list);
        }
    }

    /**
     * Get the list of files which have been created previously.
     */
    public List<String> getFilesList() {
        return files;
    }

    /**
     * A class to use with a timer to make the progress bar of the user
     * interface pulse.
     * 
     * @author Guillaume Mazoyer
     */
    private class PulseProgress extends TimerTask
    {
        @Override
        public void run() {
            Glib.idleAdd(new Handler() {
                @Override
                public boolean run() {
                    ui.getActionWidget().updateProgress(1, "", false);
                    return false;
                }
            });
        }
    }
}
