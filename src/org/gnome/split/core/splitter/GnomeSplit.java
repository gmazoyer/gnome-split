/*
 * GnomeSplit.java
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
import java.io.RandomAccessFile;

import org.gnome.split.config.Constants;
import org.gnome.split.core.utils.ByteUtils;
import org.gnome.split.core.utils.MD5Hasher;

/**
 * Algorithm to split a file with the GNOME Split algorithm.
 * 
 * @author Guillaume Mazoyer
 */
public class GnomeSplit extends DefaultSplitEngine
{
    private int parts;

    public GnomeSplit(final org.gnome.split.GnomeSplit app, File file, long size, String destination) {
        super(app, file, size, destination);
        parts = (int) Math.ceil((float) file.length() / (float) size);
    }

    /**
     * Write the GNOME Split header at the beginning of a file.
     */
    private void writeHeaders(RandomAccessFile access) throws IOException {
        // Write program version
        access.writeByte(Constants.PROGRAM_VERSION.length());
        access.write(Constants.PROGRAM_VERSION.getBytes());
        for (int i = Constants.PROGRAM_VERSION.length(); i < 4; i++) {
            access.write(0);
        }

        // Write original filename
        access.writeByte(file.getName().length());
        if (file.getName().length() > 50) {
            access.write(file.getName().substring(0, 50).getBytes());
        } else {
            access.write(file.getName().getBytes());
            for (int i = file.getName().length(); i < 50; i++) {
                access.write(0);
            }
        }

        // Write if using MD5
        access.writeBoolean(app.getConfig().SAVE_FILE_HASH);

        // Write number of files
        access.write(ByteUtils.toBytes(parts));

        // Write file size
        access.write(ByteUtils.toBytes(file.length()));
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
                    // Get the current extension
                    String current;
                    if (i >= 100) {
                        current = String.valueOf(i);
                    } else if (i >= 10) {
                        current = "0" + i;
                    } else {
                        current = "00" + i;
                    }

                    // Open the part
                    chunk = new File(destination + "." + current + ".gsp");
                    access = new RandomAccessFile(chunk, "rw");
                    int read = 0;

                    // Notify the view from a new part
                    chunks.add(chunk.getAbsolutePath());
                    this.fireEnginePartCreated(chunk.getName());

                    if (i == 1) {
                        // Write header on the first part
                        this.writeHeaders(access);
                    } else if (i == parts) {
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

                    // Should we save MD5 sum?
                    if (app.getConfig().SAVE_FILE_HASH && (i == parts)) {
                        // Get file MD5 sum
                        MD5Hasher hasher = new MD5Hasher();
                        String md5sum = hasher.hashToString(file);

                        // Write it a the end of the file
                        access.write(md5sum.getBytes());
                    }
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
