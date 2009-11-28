/*
 * Xtremsplit.java
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

import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Constants;
import org.gnome.split.core.utils.ByteUtils;
import org.gnome.split.core.utils.MD5Hasher;

public class Xtremsplit extends DefaultSplitEngine
{
    public Xtremsplit(final GnomeSplit app, File file, int parts, String destination) {
        super(app, file, parts, destination);
    }

    /**
     * Write the xtm header at the beginning of a file.
     */
    private void writeHeaders(RandomAccessFile access) throws IOException {
        // Write program name
        access.writeByte(Constants.PROGRAM_NAME.length());
        access.write(Constants.PROGRAM_NAME.getBytes());
        for (int i = Constants.PROGRAM_NAME.length(); i < 20; i++) {
            access.write(0);
        }

        // Write program version
        access.writeByte(Constants.PROGRAM_VERSION.length());
        access.write(Constants.PROGRAM_VERSION.getBytes());
        for (int i = Constants.PROGRAM_VERSION.length(); i < 14; i++) {
            access.write(0);
        }

        // Write date : supposed to be 4 bytes
        access.writeInt(0);

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
            int length = (int) (file.length() / parts);

            for (int i = 1; i <= parts; i++) {
                RandomAccessFile access = null;
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
                    access = new RandomAccessFile(destination + "." + current + ".xtm", "rw");
                    int read = 0;

                    if (i == 1) {
                        // Write header on the first part
                        this.writeHeaders(access);

                        // Update useful variables
                        read += 104;
                        total += 104;
                        this.fireEngineDone((double) 104 / (double) file.length());
                    }

                    while (read < length) {
                        if (paused) {
                            try {
                                // Pause the current thread
                                mutex.wait();
                            } catch (InterruptedException e) {
                                // Drop this exception
                            }
                        }

                        // Define the buffer size
                        byte[] buffer;
                        if (BUFFER > (length - read)) {
                            buffer = new byte[length - read];
                        } else {
                            buffer = new byte[BUFFER];
                        }

                        // Read and write data
                        toSplit.read(buffer);
                        access.write(buffer);

                        // Update read and write status
                        read += buffer.length;
                        total += buffer.length;
                        this.fireEngineDone((double) total / (double) file.length());
                    }

                    // Should we save MD5 sum?
                    if (app.getConfig().SAVE_FILE_HASH && (i == parts)) {
                        // Get file MD5 sum
                        MD5Hasher hasher = new MD5Hasher();
                        String md5sum = hasher.hashToString(file);

                        // Write it a the end of the file
                        access.writeByte(md5sum.length());
                        access.write(md5sum.getBytes());
                    }

                    // Notify the end of the part
                    this.fireEnginePartEnded(((i + 1) > parts) ? -1 : i);
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
