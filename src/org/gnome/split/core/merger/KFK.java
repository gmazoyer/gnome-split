/*
 * KFK.java
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gnome.split.GnomeSplit;

/**
 * Algorithm to merge files with the KFK algorithm.
 * 
 * @author Guillaume Mazoyer
 */
public final class KFK extends DefaultMergeEngine
{
    public KFK(final GnomeSplit app, File file, String filename) {
        super(app, file, filename);
    }

    @Override
    protected void loadHeaders() throws IOException, FileNotFoundException {
        String name = file.getName();
        File[] files = new File(file.getAbsolutePath().replace(name, "")).listFiles();

        // Get the common part of the name of each chunk
        name = name.substring(0, name.lastIndexOf("."));

        // Update the filename only if it is not specified by the user
        if (filename == null) {
            filename = file.getAbsolutePath().replace(file.getName(), "") + name;
        }

        // Default values
        parts = 0;
        fileLength = 0;

        // Calculate the number of chunks and the length :
        for (File chunk : files) {
            if (chunk.getName().contains(name + ".kk")) {
                // Increase the number of chunks
                parts++;

                // Update the size
                fileLength += chunk.length();
            }
        }
    }

    @Override
    protected String getNextChunk(String part, int number) {
        return (part + ".kk" + number);
    }

    @Override
    public void merge() throws IOException, FileNotFoundException {
        String part = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4);
        FileOutputStream out = null;
        File chunk = null;

        try {
            // Open the final file
            out = new FileOutputStream(filename);

            // Define the buffer size
            byte[] buffer;

            for (int i = 0; i < parts; i++) {
                // Open the current part to merge
                chunk = new File(this.getNextChunk(part, i));
                RandomAccessFile access = new RandomAccessFile(chunk, "r");

                // Setup size infos
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
                    this.fireEngineDone((double) total, (double) fileLength);
                }

                // Add the part the full read parts
                chunks.add(chunk.getAbsolutePath());

                // Close the part
                access.close();
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
