/*
 * DefaultSplitEngine.java
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
package org.gnome.split.core.splitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gnome.split.GnomeSplit;
import org.gnome.split.core.DefaultEngine;
import org.gnome.split.core.Engine;
import org.gnome.split.core.exception.EngineException;
import org.gnome.split.core.exception.ExceptionMessage;

/**
 * Define the model that all split engines should use.
 * 
 * @author Guillaume Mazoyer
 */
public abstract class DefaultSplitEngine extends DefaultEngine
{
    /**
     * The {@link File file} to split.
     */
    protected File file;

    /**
     * The maximum size of parts which will be created.
     */
    protected long size;

    /**
     * A part of the name of the files which will be created.
     */
    protected String destination;

    /**
     * Create a new split {@link Engine engine} using a <code>file</code> to
     * split and a maximum <code>size</code> for each chunk.
     */
    public DefaultSplitEngine(final GnomeSplit app, File file, long size, String destination) {
        super(app);
        this.directory = destination.substring(0, destination.lastIndexOf(File.separator));
        this.file = file;
        this.size = size;
        this.destination = destination;
    }

    @Override
    public void run() {
        synchronized (mutex) {
            try {
                // Invalid size
                if (size == -1) {
                    this.fireEngineError(new EngineException(ExceptionMessage.INVALID_SIZE));
                    return;
                }

                // Split the file
                this.split();
            } catch (Exception e) {
                // Handle the error
                this.fireEngineError(new EngineException(e));
            }
        }
    }

    @Override
    public void stop(boolean clean) {
        super.stop(clean);

        if (clean) {
            // Remove all created parts
            for (String chunk : chunks) {
                new File(chunk).delete();
            }
        }
    }

    /**
     * Get a filename for the current chunk using the file number.
     */
    protected abstract String getChunkName(String destination, int number);

    /**
     * Split a file into smaller parts.
     */
    public abstract void split() throws IOException, FileNotFoundException;

    /**
     * Notify the view that a part has been created.
     */
    protected void fireEnginePartCreated(String filename) {
        app.getEngineListener().enginePartCreated(filename);
    }

    /**
     * Notify the view that the MD5 sum calculation has started.
     */
    protected void fireMD5SumStarted() {
        app.getEngineListener().engineMD5SumStarted();
    }

    /**
     * Notify the view that the MD5 sum calculation has ended.
     */
    protected void fireMD5SumEnded() {
        app.getEngineListener().engineMD5SumEnded();
    }

    /**
     * Notify the view that the engine has finish its work.
     */
    protected void fireEngineEnded() {
        app.getEngineListener().engineEnded();
    }

    /**
     * Notify the view that the engine has been stopped.
     */
    protected void fireEngineStopped() {
        app.getEngineListener().engineStopped();
    }

    /**
     * Notify the view that an error has occurred.
     */
    protected void fireEngineError(EngineException exception) {
        app.getEngineListener().engineError(exception);
    }

    /**
     * Notify the view that a part of the file has been read.
     */
    protected void fireEngineDone(double done, double total) {
        app.getEngineListener().engineDone(done, total);
    }
}
