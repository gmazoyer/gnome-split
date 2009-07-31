/*
 * FileSplit.java
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
package org.gnome.split.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.gnome.split.GnomeSplit;
import org.gnome.split.dbus.DbusInhibit;

/**
 * Action to split a file into smaller files. This class implements the
 * {@link java.lang.Runnable Runnable} interface to make it run in a thread.
 * 
 * @author Guillaume Mazoyer
 */
public class FileSplit implements Runnable
{
    /**
     * The current GNOME Split instance.
     */
    private GnomeSplit app;

    /**
     * File to split in smaller files.
     */
    private File file;

    /**
     * Name template that each filename should use.
     */
    private String name;

    /**
     * Size that each file should weight.
     */
    private long size;

    /**
     * Number of bytes already read and write.
     */
    private long done;

    /**
     * Progress of this action.
     */
    private double progress;

    /**
     * Inhibit object to play with computer hibernation through dbus.
     */
    private DbusInhibit inhibit;

    /**
     * Construct a runnable split action.
     */
    public FileSplit(final GnomeSplit app, final File file, final String name, final long size) {
        this.app = app;
        this.file = file;
        this.name = name;
        this.size = size;
        this.done = 0;
        this.progress = 0;
        this.inhibit = new DbusInhibit();
    }

    /**
     * Return a suffix size using a file number.
     * 
     * @param nbFiles
     *            the file number.
     * @return the suffix size.
     */
    private int getSuffixSize(int nbFiles) {
        return ((app.getConfig().AUTO_SUFFIX_SIZE) ? app.getConfig().SUFFIX_SIZE : String.valueOf(
                nbFiles).length());
    }

    /**
     * Generate a filename using the file number, a suffix size and a name
     * template.
     * 
     * @param index
     *            the file number.
     * @param suffixSize
     *            the suffix size.
     * @param template
     *            the name template.
     * @return the generate filename.
     */
    private String generateName(int index, int suffixSize, String template) {
        final StringBuilder name = new StringBuilder();
        String suffix = Integer.toString(index + 1);
        final int length = suffix.length();

        // Format the suffix
        if (length < suffixSize) {
            final char[] buffer = new char[suffixSize];
            Arrays.fill(buffer, 0, (suffixSize - length), '0');
            suffix.getChars(0, length, buffer, (suffixSize - length));
            suffix = new String(buffer);
        }

        // Create the name
        name.append(template);
        name.append(suffix);

        return name.toString();
    }

    /**
     * Return the progress of the current split.
     * 
     * @return the progress.
     */
    public double getProgress() {
        return progress;
    }

    @Override
    public void run() {
        // File does not exist
        if (!file.exists())
            return;

        // Inhibit computer hibernation
        if (app.getConfig().NO_HIBERNATION)
            inhibit.inhibit();

        // Input and output streams
        FileInputStream input = null;
        FileOutputStream output = null;

        // Generate filenames and number
        final byte[] buffer = new byte[app.getConfig().BUFFER_SIZE];
        final int filesNumber = (int) Math.ceil((float) file.length() / (float) size);
        final File[] files = new File[filesNumber];

        try {
            // Open input stream
            input = new FileInputStream(file);

            for (int i = 0; i < files.length; i++) {
                // Create file object
                final String filename = this.generateName(i, this.getSuffixSize(filesNumber), name);
                files[i] = new File(filename);

                int read = 0;
                long current = 0;

                // File already exists
                if (files[i].exists())
                    return;

                // Cannot create file
                if (!files[i].createNewFile())
                    return;

                // Open output stream
                output = new FileOutputStream(files[i]);

                while (current < size) {
                    // Read a number of bytes
                    read = input.read(buffer);

                    // Nothing to read
                    if (read < 0)
                        break;

                    // Write read data
                    output.write(buffer, 0, read);

                    // Update current state
                    current += read;
                    done += read;

                    // Update progress
                    progress = done / file.length();
                }

                // Close output stream
                output.close();
            }

            // Close input stream
            input.close();

            // Save file hash if user wants to
            if (app.getConfig().SAVE_FILE_HASH) {
                // Find filename for hash file and create or update it if it
                // already exists.
                final String hash = new FileHash(app, app.getConfig().HASH_ALGORITHM).hashToString(file);
                final int slash = name.lastIndexOf(File.separator) + 1;
                final String filename = name.subSequence(0, slash) + app.getConfig().HASH_FILENAME;
                final FileWriter writer = new FileWriter(filename, true);

                // Write info into the hash file
                writer.write(app.getConfig().HASH_ALGORITHM + " " + hash + " " + file.getName() + "\n");
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Uninhibit computer hibernation
        if (app.getConfig().NO_HIBERNATION)
            inhibit.unInhibit();
    }
}
