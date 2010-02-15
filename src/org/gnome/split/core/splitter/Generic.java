/*
 * Generic.java
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
package org.gnome.split.core.splitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Algorithm to split files with an algorithm which does not use any headers
 * in the files.
 * 
 * @author Guillaume Mazoyer
 */
public final class Generic extends DefaultSplitEngine
{
    private int parts;

    public Generic(final org.gnome.split.GnomeSplit app, File file, long size, String destination) {
        super(app, file, size, destination);
        parts = (int) Math.ceil((float) file.length() / (float) size);
    }

    @Override
    protected String getChunkName(String destination, int number) {
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
        return (destination + "." + current);
    }

    @Override
    public void split() throws IOException, FileNotFoundException {
        RandomAccessFile toSplit = null;
        try {
            // Open a new file
            toSplit = new RandomAccessFile(file, "r");

            // Define the buffer size
            byte[] buffer;

            for (int i = 1; i <= parts; i++) {
                RandomAccessFile access = null;
                File chunk = null;
                try {
                    // Open the part
                    chunk = new File(this.getChunkName(destination, i));
                    access = new RandomAccessFile(chunk, "rw");
                    int read = 0;

                    // Notify the view from a new part
                    chunks.add(chunk.getAbsolutePath());
                    this.fireEnginePartCreated(chunk.getName());

                    if (i == parts) {
                        // Update size to stop the split correctly
                        size = file.length() - total;
                    }

                    while (read < size) {
                        if (paused) {
                            try {
                                // Pause the current thread
                                mutex.wait();
                            } catch (InterruptedException e) {
                                // Drop this exception
                            }
                        }

                        if (stopped) {
                            // Stop the current thread
                            this.fireEngineStopped();
                            return;
                        }

                        // Define a new buffer size
                        buffer = new byte[(65536 > (size - read) ? (int) (size - read) : 65536)];

                        // Read and write data
                        toSplit.read(buffer);
                        access.write(buffer);

                        // Update read and write status
                        read += buffer.length;
                        total += buffer.length;
                        this.fireEngineDone((double) total, (double) file.length());
                    }

                    // Notify the view from a written part
                    this.fireEnginePartWritten(chunk.getName());
                } catch (FileNotFoundException e) {
                    throw e;
                } catch (IOException e) {
                    throw e;
                } finally {
                    try {
                        // Close the part file
                        access.close();
                    } catch (IOException e) {
                        // Drop the exception
                    }
                }
            }

            // Notify the end of the split
            this.fireEngineEnded();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                // Close the part file
                toSplit.close();
            } catch (IOException e) {
                // Drop the exception
            }
        }
    }
}
