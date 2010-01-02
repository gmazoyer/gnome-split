/*
 * DefaultMergeEngine.java
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
package org.gnome.split.core.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gnome.split.core.DefaultEngine;
import org.gnome.split.core.Engine;
import org.gnome.split.core.exception.EngineException;

/**
 * Define the model that all merge engines should use.
 * 
 * @author Guillaume Mazoyer
 */
public abstract class DefaultMergeEngine extends DefaultEngine
{
    /**
     * The first part to merge.
     */
    protected File file;

    /**
     * The name of the file to create.
     */
    protected String filename;

    /**
     * The total length of the file.
     */
    protected long fileLength;

    /**
     * The number of parts to merge.
     */
    protected int parts;

    /**
     * If the merge should use an MD5 sum or not.
     */
    protected boolean md5;

    /**
     * The MD5 sum if it used.
     */
    protected String md5sum;

    /**
     * Create a new merge {@link Engine engine} using a first
     * <code>file</code> to merge.
     */
    public DefaultMergeEngine(final org.gnome.split.GnomeSplit app, File file, String filename) {
        super(app);
        this.filename = filename;
        this.file = file;

        if (filename != null) {
            this.directory = filename.substring(0, filename.lastIndexOf(File.separator));
        }

        try {
            // Load headers
            this.loadHeaders();
        } catch (Exception e) {
            // Handle the error
            this.fireEngineError(new EngineException(e));
        }
    }

    /**
     * Return the right merger to merge files with right algorithm.
     */
    public static final DefaultMergeEngine getInstance(final org.gnome.split.GnomeSplit app, File file,
            String filename) {
        String name = file.getName();

        if (name.endsWith(".001.gsp")) {
            // Use GNOME Split algorithm
            return new GnomeSplit(app, file, filename);
        }

        if (name.endsWith(".001.xtm")) {
            // Use Xtremsplit algorithm
            return new Xtremsplit(app, file, filename);
        }

        if (name.endsWith(".001")) {
            // Use Simple algorithm
            return new Simple(app, file, filename);
        }

        // Can't find the right algorithm
        return null;
    }

    @Override
    public void run() {
        synchronized (mutex) {
            try {
                // Merge files
                this.merge();
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
            // Remove the created file
            new File(filename).delete();
        }
    }

    /**
     * Load the headers of the files to merge.
     */
    protected abstract void loadHeaders() throws IOException, FileNotFoundException;

    /**
     * Get the next name of the file to merge.
     */
    protected abstract String getNextChunk(String part, int number);

    /**
     * Merge files to get a new one.
     */
    public abstract void merge() throws IOException, FileNotFoundException;

    /**
     * Notify the view that a part is being read.
     */
    protected void fireEnginePartRead(String filename) {
        app.getEngineListener().enginePartRead(filename);
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

    /**
     * Get the name of the file to create.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get the length of the file to create.
     */
    public long getFileLength() {
        return fileLength;
    }

    /**
     * Get the number of parts to merge.
     */
    public int getParts() {
        return parts;
    }

    /**
     * Tell if whether or not the merge will use a MD5 sum to control the file
     * integrity.
     */
    public boolean useMD5() {
        return md5;
    }
}
