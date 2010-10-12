/*
 * YoyoCut.java
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
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.gnome.split.GnomeSplit;
import org.gnome.split.core.exception.MD5Exception;
import org.gnome.split.core.exception.MissingChunkException;
import org.gnome.split.core.utils.MD5Hasher;

/**
 * Algorithm to merge files with the YoyoCut algorithm.
 * 
 * @author Guillaume Mazoyer
 */
public final class YoyoCut extends DefaultMergeEngine
{
    /**
     * The length of the header.
     */
    private int header;

    public YoyoCut(final GnomeSplit app, File file, String filename) {
        super(app, file, filename);
    }

    @Override
    protected void loadHeaders() throws IOException {
        RandomAccessFile access = null;
        try {
            // Open the first part to merge
            access = new RandomAccessFile(file, "r");

            // Set the length of the header to 0
            header = 0;

            // An array of bytes which will be useful
            byte[] bytes;

            // Read the file extension
            ArrayList<Byte> buffer = new ArrayList<Byte>();
            bytes = new byte[1];

            // The separator is an space character
            while (bytes[0] != 32) {
                access.read(bytes);
                buffer.add(bytes[0]);
            }

            // Update the header length
            header += buffer.size();

            // Read the number of chunks
            bytes = new byte[3];
            access.read(bytes);

            // Parse and update the length of the header
            parts = Integer.parseInt(new String(bytes));
            header += 3;

            // Check if there is a MD5 sum
            bytes = new byte[4];
            access.read(bytes);

            // Update the length of the header
            if (new String(bytes).equals("MD5:")) {
                md5 = true;
                header += 36;

                // Read the MD5 sum
                bytes = new byte[32];
                access.read(bytes);

                // Convert from bytes to string
                md5sum = new String(bytes).toUpperCase();
            }

            // Get the common part of the name of each chunk
            String part = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 7);

            // Update the filename only if it is not specified by the user
            if (filename == null) {
                // Parse the extension (don't get the space character)
                byte[] extension = new byte[buffer.size() - 1];
                for (byte b = 0; b < extension.length; b++) {
                    extension[b] = buffer.get(b);
                }

                // Update the name
                filename = part + new String(extension);
            }

            // Calculate the length of the final file
            for (int i = 1; i <= parts; i++) {
                fileLength += new File(this.getNextChunk(part, i)).length();
            }
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
        return (part + current + ".yct");
    }

    @Override
    public void merge() throws IOException, FileNotFoundException {
        String part = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 7);
        RandomAccessFile out = null;
        File chunk = null;
        boolean run = true;
        boolean success = true;

        try {
            // Open the final file
            out = new RandomAccessFile(filename, "rw");

            for (int i = 1; i <= parts; i++) {
                // Next chunk
                chunk = new File(this.getNextChunk(part, i));
                if (!chunk.exists()) {
                    // Check if the chunk really exists
                    this.fireEngineError(new MissingChunkException());
                    return;
                }

                // Open the chunk to read it
                RandomAccessFile access = new RandomAccessFile(chunk, "r");

                // Notify the view from a new part read
                this.fireEnginePartRead(chunk.getName());

                long read = 0;
                long length = access.length();
                if (i == 1) {
                    // Update the read bytes count
                    access.skipBytes(header);
                    read += header;
                }

                // Merge the file
                run = this.mergeChunk(out, access, read, length);

                // Reading stopped
                if (!run) {
                    return;
                }

                if (app.getConfig().CHECK_FILE_HASH && md5 && (i == parts)) {
                    // Notify the view
                    this.fireMD5SumStarted();

                    // Calculate the MD5 of the new file
                    MD5Hasher hasher = new MD5Hasher();
                    String found = hasher.hashToString(new File(filename));

                    // MD5 are different
                    success = md5sum.equals(found);

                    // Notify the view again
                    this.fireMD5SumEnded();
                }

                // Add the part the full read parts
                chunks.add(chunk.getAbsolutePath());

                // Close the part
                access.close();
            }

            if (!success && md5) {
                // Notify the error
                this.fireEngineError(new MD5Exception());
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
