/*
 * Generic.java
 * 
 * Copyright (c) 2010 Guillaume Mazoyer
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gnome.split.GnomeSplit;

/**
 * Algorithm to merge files with an algorithm which does not use any headers
 * in the files.
 * 
 * @author Guillaume Mazoyer
 */
public final class Generic extends DefaultMergeEngine
{
    public Generic(final GnomeSplit app, File file, String filename) {
        super(app, file, filename);
    }

    @Override
    protected void loadHeaders() throws IOException, FileNotFoundException {
        // Update the filename only if it is not specified by the user
        if (filename == null) {
            String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
            filename = file.getAbsolutePath().replace(file.getName(), "") + name;
        }

        // We do not use an MD5 sum
        md5 = false;

        // We do not know the number of parts
        parts = -1;

        // We do not know the size of the final file
        fileLength = -1;
    }

    @Override
    protected String getNextChunk(String part, int number) {
        // Get the current extension
        String current;
        if (number >= 100) {
            current = String.valueOf(number);
        } else if (number >= 10) {
            current = "0" + number;
        } else {
            current = "00" + number;
        }

        // Finally
        return (part + current);
    }

    @Override
    public void merge() throws IOException, FileNotFoundException {
        String part = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 3);
        FileOutputStream out = null;
        File chunk = null;

        try {
            // Open the final file
            out = new FileOutputStream(filename);

            // We assume that there is at least one part (which is kinda
            // ridiculous, but still...). We'll do some tricks to find out
            // which files we have to merge
            parts = file.getName().endsWith(".000") ? 0 : 1;

            // Define the buffer size
            byte[] buffer;

            for (int i = parts; i <= parts; i++) {
                // Open the current part to merge
                chunk = new File(this.getNextChunk(part, i));
                RandomAccessFile access = new RandomAccessFile(chunk, "r");
                long read = 0;
                long length = access.length();

                // Notify the view from a new part read
                this.fireEnginePartRead(chunk.getName());

                // Merge the file
                while (read < length) {
                    if (paused) {
                        try {
                            // Pause the current thread
                            mutex.wait();
                        } catch (InterruptedException e) {
                            // Drop the exception
                        }
                    }

                    if (stopped) {
                        // Stop the current thread
                        this.fireEngineStopped();
                        return;
                    }

                    // Define a new buffer size
                    buffer = new byte[(65536 > (length - read) ? (int) (length - read) : 65536)];

                    // Read and write data
                    access.read(buffer);
                    out.write(buffer);

                    // Update read and write status
                    read += buffer.length;
                    total += buffer.length;
                    this.fireEngineDone((double) read, (double) length);
                }

                // Add the part the full read parts
                chunks.add(chunk.getAbsolutePath());

                // Close the part
                access.close();

                // Lets find out if there is one more part
                if (new File(this.getNextChunk(part, (i + 1))).exists()) {
                    parts++;
                }
            }

            if (app.getConfig().OPEN_FILE_AT_END) {
                // Open the created file if requested
                app.openURI("file://" + filename);
            }

            // Notify the end
            this.fireEngineEnded();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                // Close the final file
                out.close();
            } catch (IOException e) {
                // Drop the exception
            }
        }
    }
}
