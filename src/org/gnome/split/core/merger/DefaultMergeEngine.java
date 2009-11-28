/*
 * DefaultMergeEngine.java
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
package org.gnome.split.core.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gnome.split.GnomeSplit;
import org.gnome.split.core.DefaultEngine;
import org.gnome.split.core.Engine;
import org.gnome.split.core.EngineException;

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
    protected File file = null;

    /**
     * The name of the file to create.
     */
    protected String filename = null;

    /**
     * The total length of the file.
     */
    protected long fileLength = -1;

    /**
     * The number of parts to merge.
     */
    protected int parts = -1;

    /**
     * If the merge should use an MD5 sum or not.
     */
    protected boolean md5 = false;

    /**
     * The MD5 sum if it used.
     */
    protected String md5sum = null;

    /**
     * Create a new merge {@link Engine engine} using a first
     * <code>file</code> to merge.
     */
    public DefaultMergeEngine(final GnomeSplit app, File file) {
        super(app);
        this.file = file;

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
    public static DefaultMergeEngine getInstance(File file) {
        String name = file.getName();

        if (name.endsWith(".001.xtm")) {
            // Use Xtremsplit algorithm
            // return new Xtremsplit(null, file);
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

    /**
     * Load the headers of the files to merge.
     */
    protected abstract void loadHeaders() throws IOException, FileNotFoundException;

    /**
     * Merge files to get a new one.
     */
    public abstract void merge() throws IOException, FileNotFoundException;

    /**
     * Notify the view that a part has been read.
     */
    protected void fireEnginePartEnded(int next) {
        app.getEngineListener().enginePartEnded(next);
    }

    /**
     * Notify the view that the engine has finish its work.
     */
    protected void fireEngineEnded() {
        app.getEngineListener().engineEnded();
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
    protected void fireEngineDone(double progress) {
        app.getEngineListener().engineDone(progress);
    }
}
