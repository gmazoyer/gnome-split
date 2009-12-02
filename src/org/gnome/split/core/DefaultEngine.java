/*
 * DefaultEngine.java
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
package org.gnome.split.core;

import org.gnome.split.GnomeSplit;

public abstract class DefaultEngine implements Engine
{
    /**
     * Default size of a buffer.
     */
    protected static final int BUFFER = 65536;

    /**
     * To manage synchronization of thread.
     */
    protected final Object mutex = new Object();

    /**
     * Current instance of GNOME Split.
     */
    protected GnomeSplit app = null;

    /**
     * Total of bytes read.
     */
    protected long total;

    /**
     * The directory where the file(s) is/are created.
     */
    protected String directory;

    /**
     * To manage pause and resume actions.
     */
    protected boolean paused;

    /**
     * To stop an action
     */
    protected boolean stopped;

    public DefaultEngine(final GnomeSplit app) {
        this.app = app;
        this.total = 0;
        this.directory = null;
        this.paused = false;
        this.stopped = false;
    }

    @Override
    public abstract void run();

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        synchronized (mutex) {
            paused = false;
            mutex.notify();
        }
    }

    @Override
    public void stop(boolean clean) {
        if (paused) {
            this.resume();
        }
        stopped = true;
    }

    @Override
    public boolean paused() {
        return paused;
    }

    /**
     * Get the directory where the current action is performed.
     */
    public String getDirectory() {
        return directory;
    }
}
