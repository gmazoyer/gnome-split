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
package org.gnome.split.core.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gnome.split.GnomeSplit;
import org.gnome.split.core.exception.MissingChunkException;

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
    protected void loadHeaders() throws IOException {
        // Update the filename only if it is not specified by the user
        if (filename == null) {
            String name = file.getName().substring(0, file.getName().lastIndexOf('.'));
            filename = file.getAbsolutePath().replace(file.getName(), "") + name;
        }

        // We do not use an MD5 sum
        md5 = false;

        // Setup to found chunks to merge
        String directory = file.getAbsolutePath().replace(file.getName(), "");
        String name = file.getName();

        // Get all the files of the directory
        File[] files = new File(directory).listFiles();

        // Setup default values
        parts = 0;
        fileLength = 0;

        for (File chunk : files) {
            boolean valid = chunk.getName().contains(name.substring(0, name.lastIndexOf('.')));
            if (!chunk.isDirectory() && valid) {
                // Increase the number of chunks
                parts++;

                // Update the size
                fileLength += chunk.length();
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
        return (part + current);
    }

    @Override
    public void merge() throws IOException, FileNotFoundException {
        String part = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 3);
        RandomAccessFile out = null;
        File chunk = null;
        boolean run = true;

        try {
            // Open the final file
            out = new RandomAccessFile(filename, "rw");

            // We assume that there is at least one part (which is kinda
            // ridiculous, but still...). We'll do some tricks to find out
            // which files we have to merge
            parts = file.getName().endsWith(".000") ? 0 : 1;

            for (int i = parts; i <= parts; i++) {
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

                // Merge the file
                run = this.mergeChunk(out, access, 0, access.length());

                // Reading stopped
                if (!run) {
                    return;
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
