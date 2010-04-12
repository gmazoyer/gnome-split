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
package org.gnome.split.core.splitter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gnome.split.GnomeSplit;
import org.gnome.split.core.utils.MD5Hasher;

/**
 * Algorithm to split a file with the YoyoCut algorithm.
 * 
 * @author Guillaume Mazoyer
 */
public final class YoyoCut extends DefaultSplitEngine
{
    private int parts;

    public YoyoCut(final GnomeSplit app, File file, long size, String destination) {
        super(app, file, size, destination);
        parts = (int) Math.ceil((float) file.length() / (float) size);
    }

    /**
     * Write the GNOME Split header at the beginning of a file.
     */
    private void writeHeaders(RandomAccessFile access) throws IOException {
        // Write the extension
        String extension = file.getName().substring(file.getName().lastIndexOf('.') + 1) + " ";
        access.write(extension.getBytes());

        // Format the number of chunks
        String number;
        if (parts >= 100) {
            number = String.valueOf(parts);
        } else if (parts >= 10) {
            number = "0" + parts;
        } else {
            number = "00" + parts;
        }

        // Write the number of chunks
        access.write(number.getBytes());

        // Should we save MD5 sum?
        if (app.getConfig().SAVE_FILE_HASH) {
            // Notify the view
            this.fireMD5SumStarted();

            // Get file MD5 sum
            MD5Hasher hasher = new MD5Hasher();
            String md5sum = hasher.hashToString(file);

            // Write it a the end of the header
            access.write("MD5:".getBytes());
            access.write(md5sum.getBytes());

            // Notify the view again
            this.fireMD5SumEnded();
        }
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

        // Remove the extension
        String removed = destination.substring(0, destination.lastIndexOf('.'));

        // Finally
        return (removed + "." + current + ".yct");
    }

    @Override
    public void split() throws IOException {
        RandomAccessFile toSplit = null;
        boolean run = true;
        try {
            // Open a new file
            toSplit = new RandomAccessFile(file, "r");

            for (int i = 1; i <= parts; i++) {
                RandomAccessFile access = null;
                File chunk = null;
                try {
                    // Open the part
                    chunk = new File(this.getChunkName(destination, i));
                    access = new RandomAccessFile(chunk, "rw");

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

                    // Write the chunk
                    run = this.writeChunk(toSplit, access);

                    // Writing stopped
                    if (!run) {
                        return;
                    }

                    // Notify the view from a written part
                    this.fireEnginePartWritten(chunk.getName());
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
