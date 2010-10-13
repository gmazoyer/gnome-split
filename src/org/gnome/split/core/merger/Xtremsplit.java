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
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gnome.split.GnomeSplit;
import org.gnome.split.core.exception.EngineException;
import org.gnome.split.core.exception.MD5Exception;
import org.gnome.split.core.exception.MissingChunkException;
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

    private boolean extractable;

    public Xtremsplit(final GnomeSplit app, File file, String filename) {
        super(app, file, filename);
    }

    /**
     * Used to find all the MD5 sums of the files
     */
    private void loadMD5sums(String lastFilename) throws IOException {
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
    protected void loadHeaders() throws IOException {
        RandomAccessFile access = null;
        try {
            // Open the first part to merge
            access = new RandomAccessFile(file, "r");

            if (file.getName().endsWith(".001.exe")) {
                // Skip useless header and .exe header
                access.skipBytes(305704);
                extractable = true;
            } else {
                // Skip useless header
                access.skipBytes(40);
                extractable = false;
            }

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
        return (part + current + ((extractable && (number == 1)) ? ".exe" : ".xtm"));
    }

    @Override
    public void merge() throws IOException, EngineException {
        String part = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 7);
        RandomAccessFile out = null;
        File chunk = null;
        boolean run = true;
        boolean success = true;

        try {
            // Open the final file
            out = new RandomAccessFile(filename, "rw");

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
                // Next chunk
                chunk = new File(this.getNextChunk(part, i));
                if (!chunk.exists()) {
                    // Check if the chunk really exists
                    throw new MissingChunkException();
                }

                // Open the chunk to read it
                RandomAccessFile access = new RandomAccessFile(chunk, "r");

                // Notify the view from a new part read
                this.fireEnginePartRead(chunk.getName());

                long read = 0;
                long length = access.length();
                if (i == 1) {
                    // Size of the header
                    if (extractable) {
                        read += 305664;

                        // Skip the last 25 bytes (due to .exe format)
                        length -= 24;
                    }

                    // Skip header if it is the first part
                    read += 104;
                    access.skipBytes((int) read);
                } else if (md5 && (i == parts)) {
                    // Skip the MD5 sum if it is the last part
                    length -= (parts * 32);
                }

                // Merge the file
                run = this.mergeChunk(out, access, read, length);

                // Reading stopped
                if (!run) {
                    return;
                }

                if (app.getConfig().CHECK_FILE_HASH && md5) {
                    // Notify the view
                    this.fireMD5SumStarted();

                    if (i == parts) {
                        // Calculate the MD5 sum without including the MD5
                        // sums at the end of the last file
                        md5sum = md5hasher.hashToString(chunk, length);
                    } else {
                        if (extractable && (i == 1)) {
                            // Calculate the MD5 sum skipping .exe header
                            md5sum = md5hasher.hashToString(chunk, 305664, length);
                        } else {
                            // Calculate the MD5 sum normally
                            md5sum = md5hasher.hashToString(chunk);
                        }
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
                // Notify the error. It's just a warning so we don't throw it.
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
