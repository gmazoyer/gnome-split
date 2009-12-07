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
package org.gnome.split.core.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gnome.split.core.exception.EngineException;
import org.gnome.split.core.exception.ExceptionMessage;
import org.gnome.split.core.exception.MD5Exception;
import org.gnome.split.core.utils.ByteUtils;
import org.gnome.split.core.utils.MD5Hasher;

/**
 * Algorithm to merge files with the GNOME Split algorithm.
 * 
 * @author Guillaume Mazoyer
 */
public class GnomeSplit extends DefaultMergeEngine
{
    public GnomeSplit(final org.gnome.split.GnomeSplit app, File file, String filename) {
        super(app, file, filename);
    }

    @Override
    protected void loadHeaders() throws IOException, FileNotFoundException {
        RandomAccessFile access = null;
        try {
            // Open the first part to merge
            access = new RandomAccessFile(file, "r");

            // Skip useless header
            access.skipBytes(5);

            // Read filename
            byte[] bytes = new byte[access.read()];
            access.read(bytes);
            access.skipBytes(50 - bytes.length);

            // Update the filename only if it is not specified by the user
            if (filename == null) {
                filename = file.getAbsolutePath().replace(file.getName(), "") + new String(bytes);
            }

            // Read if MD5 is used
            bytes = new byte[1];
            access.read(bytes);
            md5 = ByteUtils.toBoolean(bytes[0]);

            // Read file number
            bytes = new byte[4];
            access.read(bytes);
            parts = (int) ByteUtils.toInt(bytes);

            // Read file length
            bytes = new byte[8];
            access.read(bytes);
            fileLength = ByteUtils.toLong(bytes);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                access.close();
            } catch (IOException e) {
                // Drop the exception
            }
        }
    }

    @Override
    public void merge() throws IOException, FileNotFoundException {
        String part = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 7);
        FileOutputStream out = null;
        File chunk = null;
        boolean md5match = true;
        try {
            // Open the final file
            out = new FileOutputStream(filename);

            // Define the buffer size
            byte[] buffer;

            for (int i = 1; i <= parts; i++) {
                // Get the current extension
                String current;
                if (i >= 100) {
                    current = String.valueOf(i);
                } else if (i >= 10) {
                    current = "0" + i;
                } else {
                    current = "00" + i;
                }

                // Open the current part to merge
                chunk = new File(part + current + ".gsp");
                RandomAccessFile access = new RandomAccessFile(chunk, "r");
                long read = 0;
                long length = access.length();

                // Notify the view from a new part read
                this.fireEnginePartRead(chunk.getName());

                if (i == 1) {
                    // Skip headers if it is the first part
                    access.skipBytes(69);
                    read += 69;
                } else if (md5 && (i == parts)) {
                    // Skip the MD5 sum if it is the last part
                    length -= 32;
                }

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

                if (md5 && (i == parts)) {
                    // Read the MD5 which was calculated during the split
                    buffer = new byte[32];
                    access.read(buffer);
                    md5sum = new String(buffer);

                    // Calculate the MD5 of the new file
                    MD5Hasher hasher = new MD5Hasher();
                    String found = hasher.hashToString(new File(filename));

                    // MD5 are different
                    md5match = md5sum.equals(found);
                }

                // Add the part the full read parts
                chunks.add(chunk.getAbsolutePath());

                // Close the part
                access.close();
            }

            if (app.getConfig().DELETE_PARTS && md5 && md5match) {
                // Delete all parts if and *only if* the MD5 sums are equals
                for (String path : chunks) {
                    new File(path).delete();
                }
            }

            // Notify the end of the merge
            if (md5 && !md5match) {
                EngineException exception = new MD5Exception(ExceptionMessage.MD5_DIFFER);
                this.fireEngineError(exception);
            } else {
                this.fireEngineEnded();
            }
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
