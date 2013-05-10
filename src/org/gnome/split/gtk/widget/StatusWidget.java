/*
 * StatusWidget.java
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
package org.gnome.split.gtk.widget;

import java.util.Timer;
import java.util.TimerTask;

import org.gnome.glib.Glib;
import org.gnome.glib.Handler;
import org.gnome.gtk.Frame;
import org.gnome.gtk.Label;
import org.gnome.gtk.ShadowType;
import org.gnome.gtk.Statusbar;

/**
 * A {@link Statusbar} like to show information about an action.
 * 
 * @author Guillaume Mazoyer
 */
public class StatusWidget extends Statusbar
{
    /**
     * Display the information.
     */
    private Label speed;

    public StatusWidget() {
        super();

        // Just a little space
        this.setBorderWidth(2);

        // Add a frame to pack the widgets
        final Frame frame = new Frame(null);
        frame.setShadowType(ShadowType.NONE);
        this.packStart(frame, false, false, 0);

        // Add the speed label
        this.speed = new Label();
        frame.add(speed);

        // Show the container
        frame.showAll();
    }

    /**
     * Update the displayed text.
     */
    public void updateText(String message) {
        this.setMessage((message == null) ? "" : message);
    }

    /**
     * Update the displayed speed.
     */
    public void updateSpeed(String value) {
        if (value == null) {
            speed.hide();
            speed.setLabel("");
        } else {
            speed.setLabel(value);
            speed.show();
        }
    }

    /**
     * Schedule the message disappearing in the statusbar using a timer.
     */
    public void scheduleTimeout(int seconds) {
        // Create a timer
        final Timer timer = new Timer();

        // Schedule the text disappearing
        timer.schedule(new TimeoutTask(timer), (seconds * 1000));
    }

    /**
     * Reset the widget to its initial status.
     */
    public void reset() {
        this.updateText(null);
        this.updateSpeed(null);
    }

    /**
     * A class to schedule the message disappearing in the statusbar using a
     * timer.
     * 
     * @author Guillaume Mazoyer
     */
    private class TimeoutTask extends TimerTask
    {
        private Timer timer;

        TimeoutTask(Timer timer) {
            this.timer = timer;
        }

        @Override
        public void run() {
            Glib.idleAdd(new Handler() {
                @Override
                public boolean run() {
                    // Erase the text
                    updateText(null);
                    return false;
                }
            });

            // Clear the timer object
            timer.cancel();
            timer.purge();
        }
    }
}
