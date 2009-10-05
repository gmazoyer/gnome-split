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
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gnome.split.GnomeSplit;

/**
 * Action to assembler files into a single file. This class implements the
 * {@link java.lang.Runnable Runnable} interface to make it run in a thread.
 * 
 * @author Guillaume Mazoyer
 */
public class FileAssembly extends FileOperation
{
    /**
     * Names of the files to assemble.
     */
    private String[] filenames;

    /**
     * Construct a runnable assembly action.
     */
    public FileAssembly(final GnomeSplit app, final String filename, final String[] filenames) {
        super(app);

        this.filenames = filenames;
        this.file = new File(filename);
    }

    @Override
    public void run() {
        // Create files objects
        final File[] files = new File[filenames.length];

        // Initialize progress info
        int read = 0;

        // File already exists
        if (file.exists())
            return;

        // Inhibit computer hibernation
        if (app.getConfig().NO_HIBERNATION)
            inhibit.inhibit();

        // Input and output streams
        RandomAccessFile input = null;
        RandomAccessFile output = null;

        // Calculate total size
        for (int i = 0; i < files.length; i++) {
            files[i] = new File(filenames[i]);
            size += files[i].length();
        }

        // Buffer which will contain read data
        final byte[] buffer = new byte[app.getConfig().BUFFER_SIZE];

        try {
            // Create the file
            if (!file.createNewFile())
                return;

            // Open stream to write into the file
            output = new RandomAccessFile(file, "rw");

            // Old value to decide to notify listeners
            double oldProgress = 0;

            for (File part : files) {
                if (!part.exists()) {
                    this.setStatus(OperationStatus.ERROR);
                    return;
                }

                // Open stream to read the file
                input = new RandomAccessFile(part, "r");

                // Read it
                while ((read = input.read(buffer)) >= 0) {
                    // Write it
                    output.write(buffer, 0, read);

                    // Update current state
                    done += read;

                    // Update progress
                    oldProgress = progress;
                    progress = (double) done / (double) size;

                    // Force update the listeners
                    this.fireProgressChanged(oldProgress);
                    this.fireStatusChanged(false);
                }

                // Close read file stream
                input.close();
            }

            // Close the stream
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set status to finished
        done = size;
        this.setStatus(OperationStatus.FINISHED);

        // Uninhibit computer hibernation
        if (app.getConfig().NO_HIBERNATION)
            inhibit.unInhibit();
    }
}
