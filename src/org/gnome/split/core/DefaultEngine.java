/*
 * DefaultEngine.java
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
package org.gnome.split.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.gnome.split.GnomeSplit;
import org.gnome.split.core.utils.SizeUnit;

/**
 * A class giving a model and an initial behavior for all merger and splitter
 * classes.
 * 
 * @author Guillaume Mazoyer
 */
public abstract class DefaultEngine implements Engine
{
    /**
     * To manage synchronization of thread.
     */
    protected final Object mutex = new Object();

    /**
     * Current instance of GNOME Split.
     */
    protected GnomeSplit app;

    /**
     * Total of bytes read.
     */
    protected long total;

    /**
     * The directory where the file(s) is/are created.
     */
    protected String directory;

    /**
     * {@link List} of all chunks which had been created/read.
     */
    protected List<String> chunks;

    /**
     * To manage pause and resume actions.
     */
    protected boolean paused;

    /**
     * To stop an action
     */
    protected boolean stopped;

    /**
     * A timer to calculate the speed.
     */
    private Timer speed;

    public DefaultEngine(final GnomeSplit app) {
        this.app = app;
        this.total = 0;
        this.directory = null;
        this.chunks = new ArrayList<String>();
        this.paused = false;
        this.stopped = false;
        this.speed = null;
    }

    @Override
    public abstract void run();

    @Override
    public abstract String getFilename();

    @Override
    public abstract long getFileLength();

    @Override
    public void pause() {
        paused = true;
        app.getEngineListener().engineSuspended();
    }

    @Override
    public void resume() {
        synchronized (mutex) {
            paused = false;
            mutex.notify();
            app.getEngineListener().engineRunning();
        }
    }

    @Override
    public void stop(boolean clean) {
        if (paused) {
            // If suspended, resume the action
            this.resume();
        }

        if (speed != null) {
            // Stop the speed calculator
            this.stopSpeedCalculator();
        }

        // Stop the action
        stopped = true;
    }

    @Override
    public boolean paused() {
        return paused;
    }

    @Override
    public String getDirectory() {
        return directory;
    }

    /**
     * Notify the view from a speed that has changed.
     */
    private void fireEngineSpeedChanged(String speed) {
        app.getEngineListener().engineSpeedChanged(speed);
    }

    /**
     * Start the speed calculator which should notify the view from the speed
     * of the action.
     */
    protected void startSpeedCalculator() {
        // Create a new timer and start its task
        speed = new Timer("Speed calculator");
        speed.scheduleAtFixedRate(new SpeedCalculatorTask(total), 1, 2000);
    }

    /**
     * Stop the speed calculator.
     */
    protected void stopSpeedCalculator() {
        // Stop the timer
        if (speed != null) {
            speed.cancel();
            speed = null;
        }

        // Make displayed speed to unknown
        this.fireEngineSpeedChanged(null);
    }

    /**
     * A class which calculate the speed of the action.
     * 
     * @author Guillaume Mazoyer
     */
    private class SpeedCalculatorTask extends TimerTask
    {
        private long oldTotal;

        private SpeedCalculatorTask(long oldTotal) {
            this.oldTotal = oldTotal;
        }

        @Override
        public void run() {
            // The speed is calculated every 2 seconds
            long speed = (total - oldTotal) / 2;

            // Update the old total
            oldTotal = total;

            // Notify the view
            if (speed == 0) {
                fireEngineSpeedChanged(null);
            } else {
                String value = SizeUnit.formatSpeed(speed);
                fireEngineSpeedChanged(value);
            }
        }

    }
}
