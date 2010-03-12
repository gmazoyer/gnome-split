/*
 * Xtremsplit.java
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
import org.gnome.split.core.exception.EngineException;
import org.gnome.split.core.exception.ExceptionMessage;
import org.gnome.split.core.exception.MD5Exception;
import org.gnome.split.core.utils.ByteUtils;
import org.gnome.split.core.utils.MD5Hasher;

/**
 * Algorithm to merge files with the Xtremsplit algorithm.
 * 
 * @author Guillaume Mazoyer
 */
public final class Xtremsplit extends DefaultMergeEngine
{
    private String[] md5sums;

    public Xtremsplit(final GnomeSplit app, File file, String filename) {
        super(app, file, filename);
    }

    /**
     * Used to find all the MD5 sums of the files
     */
    private void loadMD5sums(String lastFilename) throws IOException, FileNotFoundException {
        // Find the last file to read
        File lastFile = new File(lastFilename);
        RandomAccessFile access = new RandomAccessFile(lastFile, "r");

        // Find the position of the MD5 sums
        long position = access.length() - (parts * 32);
        access.seek(position);

        byte[] read = new byte[32];
        for (int i = 0; i < parts; i++) {
            // Read a MD5 sum
            access.read(read);

            // Convert it to a String
            md5sums[i] = new String(read);
        }

        // Close the file access
        access.close();
    }

    @Override
    protected void loadHeaders() throws IOException, FileNotFoundException {
        RandomAccessFile access = null;
        try {
            // Open the first part to merge
            access = new RandomAccessFile(file, "r");

            // Skip useless header
            access.skipBytes(40);

            // Read filename
            byte[] bytes = new byte[access.read()];
            access.read(bytes);
            access.skipBytes(50 - bytes.length);

            // Update the filename only if it is not specified by the user
            if (filename == null) {
                filename = file.getAbsolutePath().replace(file.getName(), "") + new String(bytes);
            }

            // Read if MD5 is used
            md5 = access.readBoolean();

            // Read file number
            bytes = new byte[4];
            access.read(bytes);
            parts = (int) ByteUtils.littleEndianToInt(bytes);

            // Read file length
            bytes = new byte[8];
            access.read(bytes);
            fileLength = ByteUtils.littleEndianToLong(bytes);
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
        return (part + current + ".xtm");
    }

    @Override
    public void merge() throws IOException, FileNotFoundException {
        String part = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 7);
        FileOutputStream out = null;
        File chunk = null;
        boolean success = true;

        try {
            // Open the final file
            out = new FileOutputStream(filename);

            // Define the buffer size
            byte[] buffer;

            // Load all the MD5 sums
            md5sums = new String[parts];
            this.loadMD5sums(this.getNextChunk(part, parts));

            // Use for MD5 calculation
            MD5Hasher md5hasher = null;

            // Use it only if we should calculate the MD5
            if (md5) {
                md5hasher = new MD5Hasher();
            }

            for (int i = 1; i <= parts; i++) {
                // Open the current part to merge
                chunk = new File(this.getNextChunk(part, i));
                RandomAccessFile access = new RandomAccessFile(chunk, "r");
                long read = 0;
                long length = access.length();

                // Notify the view from a new part read
                this.fireEnginePartRead(chunk.getName());

                if (i == 1) {
                    // Skip headers if it is the first part
                    access.skipBytes(104);
                    read += 104;
                } else if (md5 && (i == parts)) {
                    // Skip the MD5 sum if it is the last part
                    length -= (parts * 32);
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

                if (md5) {
                    // Notify the view
                    this.fireMD5SumStarted();

                    if (i != parts) {
                        // Calculate the MD5 sum normally
                        md5sum = md5hasher.hashToString(chunk);
                    } else {
                        // Calculate the MD5 sum without including the MD5
                        // sums at the end of the last file
                        long max = access.length() - (parts * 32);
                        md5sum = md5hasher.hashToString(chunk, max);
                    }

                    // Notify the view
                    this.fireMD5SumEnded();

                    // MD5 sums are different
                    if (!md5sum.equals(md5sums[i - 1])) {
                        success = false;
                    }
                }

                // Add the part the full read parts
                chunks.add(chunk.getAbsolutePath());

                // Close the part
                access.close();
            }

            if (!success && md5) {
                // Notify the error
                EngineException exception = new MD5Exception(ExceptionMessage.MD5_DIFFER);
                this.fireEngineError(exception);
            } else if (success) {
                if (app.getConfig().DELETE_PARTS && md5) {
                    // Delete all parts if and *only if* the MD5 sums are
                    // equals
                    for (String path : chunks) {
                        new File(path).delete();
                    }
                }

                if (app.getConfig().OPEN_FILE_AT_END) {
                    // Open the created file if requested
                    app.openURI("file://" + filename);
                }

                // Notify the end
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
