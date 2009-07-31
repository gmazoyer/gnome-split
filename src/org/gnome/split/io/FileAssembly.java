/*
 * FileAssembly.java
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
package org.gnome.split.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gnome.split.GnomeSplit;
import org.gnome.split.dbus.DbusInhibit;

/**
 * Action to assembler files into a single file. This class implements the
 * {@link java.lang.Runnable Runnable} interface to make it run in a thread.
 * 
 * @author Guillaume Mazoyer
 */
public class FileAssembly implements Runnable
{
    /**
     * The current GNOME Split instance.
     */
    private GnomeSplit app;

    /**
     * Name of the file to create.
     */
    private String filename;

    /**
     * Names of the files to assemble.
     */
    private String[] filenames;

    /**
     * Number of bytes already read and write.
     */
    private long done;

    /**
     * Progress of this action.
     */
    private double progress;

    /**
     * Inhibit object to play with computer hibernation through dbus.
     */
    private DbusInhibit inhibit;

    /**
     * Construct a runnable assembly action.
     */
    public FileAssembly(final GnomeSplit app, final String filename, final String[] filenames) {
        this.app = app;
        this.filename = filename;
        this.filenames = filenames;
        this.done = 0;
        this.progress = 0;
        this.inhibit = new DbusInhibit();
    }

    /**
     * Return the progress of the current assembly.
     * 
     * @return the progress.
     */
    public double getProgress() {
        return progress;
    }

    @Override
    public void run() {
        // Create files objects
        final File file = new File(filename);
        final File[] files = new File[filenames.length];

        // Initialize progress info
        int read = 0;
        long size = 0;

        // File does not exist
        if (!file.exists())
            return;

        // Inhibit computer hibernation
        if (app.getConfig().NO_HIBERNATION)
            inhibit.inhibit();

        // Input and output streams
        FileInputStream input = null;
        FileOutputStream output = null;

        // Calculate total size
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(filenames[i]);
            size += files[i].length();
        }

        final byte[] buffer = new byte[app.getConfig().BUFFER_SIZE];

        try {
            // Open stream to read the file
            output = new FileOutputStream(file);

            for (File part : files) {
                if (!part.exists())
                    return;

                // Open stream to read the file
                input = new FileInputStream(part);

                // Read it
                while ((read = input.read(buffer)) >= 0) {
                    output.write(buffer, 0, read);

                    // Update the progress info
                    done += read;
                    progress = size / done;
                }

                // Close read file stream
                input.close();
            }

            // Close the stream
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Uninhibit computer hibernation
        if (app.getConfig().NO_HIBERNATION)
            inhibit.unInhibit();
    }
}
