/*
 * Configuration.java
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
package org.gnome.split.config;

import org.gnome.split.config.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Load the configuration file and interact with it.
 * 
 * @author Guillaume Mazoyer
 */
public final class Configuration
{
    /**
     * Configuration file.
     */
    private File configuration;

    /**
     * Size of the read/write buffer.
     */
    public int BUFFER_SIZE;

    /**
     * Name of the hash file.
     */
    public String HASH_FILENAME;

    /**
     * Hash algorithm to use.
     */
    public String HASH_ALGORITHM;

    /**
     * Size of the file suffix.
     */
    public int SUFFIX_SIZE;

    /**
     * Automatically choose file suffix size.
     */
    public boolean AUTO_SUFFIX_SIZE;

    /**
     * Write a file containing the file hash.
     */
    public boolean SAVE_FILE_HASH;

    /**
     * Delete the parts file after the assembly.
     */
    public boolean DELETE_PARTS;

    /**
     * Disable computer hibernation.
     */
    public boolean NO_HIBERNATION;

    /**
     * Use notification system.
     */
    public boolean USE_NOTIFICATION;

    /**
     * Show icon in the notification zone.
     */
    public boolean SHOW_TRAY_ICON;

    /**
     * Private constructor can't instantiate Configuration in other class.<br>
     * Check for preferences file and load it.
     * 
     * @throws IOException
     *             if an exception occurs while creating the file.
     */
    public Configuration() throws IOException {
        configuration = new File(Constants.CONFIG_FILE);

        // File not found
        if (!configuration.exists()) {
            // Check if path exists and create it if necessary
            File path = new File(Constants.CONFIG_FOLDER);
            if (!path.exists())
                path.mkdirs();

            // Create file and initialize preferences
            configuration.createNewFile();
            resetPreferences();
        }
        load();
    }

    /**
     * This method is used to load the preferences file.
     */
    private void load() {
        try {
            Properties preferences = new Properties();
            InputStream stream = new FileInputStream(configuration);

            preferences.load(stream);
            stream.close();

            BUFFER_SIZE = Integer.parseInt(preferences.getProperty("BufferSize", "1024"));
            HASH_FILENAME = preferences.getProperty("HashFilename", "hash.txt");
            HASH_ALGORITHM = preferences.getProperty("HashAlgorithm", "MD5");
            SUFFIX_SIZE = Integer.parseInt(preferences.getProperty("SuffixSize", "1"));
            AUTO_SUFFIX_SIZE = Boolean.parseBoolean(preferences.getProperty("AutoSuffixSize", "true"));
            SAVE_FILE_HASH = Boolean.parseBoolean(preferences.getProperty("SaveFileHash", "true"));
            DELETE_PARTS = Boolean.parseBoolean(preferences.getProperty("DeleteParts", "false"));
            NO_HIBERNATION = Boolean.parseBoolean(preferences.getProperty("NoHibernation", "true"));
            USE_NOTIFICATION = Boolean.parseBoolean(preferences.getProperty("UseNotification", "false"));
            SHOW_TRAY_ICON = Boolean.parseBoolean(preferences.getProperty("ShowTrayIcon", "false"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This method is used to reset the preferences.
     */
    public void resetPreferences() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(configuration);
            writer.write("BufferSize      = 1024\n");
            writer.write("HashFilename    = hash.txt\n");
            writer.write("HashAlgorithm   = MD5\n");
            writer.write("SuffixSize      = 1\n");
            writer.write("AutoSuffixSize  = true\n");
            writer.write("SaveFileHash    = true\n");
            writer.write("DeleteParts     = false\n");
            writer.write("NoHibernation   = true\n");
            writer.write("UseNotification = false\n");
            writer.write("ShowTrayIcon    = false\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                    load();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to save the preferences by overwriting the file.
     */
    public void savePreferences() {
        FileWriter writer = null;
        try {
            // Prevent empty field
            if ((HASH_FILENAME == null) || HASH_FILENAME.equals(""))
                HASH_FILENAME = "hash.txt";

            if (BUFFER_SIZE == 0)
                BUFFER_SIZE = 1024;

            writer = new FileWriter(configuration);
            writer.write("BufferSize      = " + BUFFER_SIZE + "\n");
            writer.write("HashFilename    = " + HASH_FILENAME + "\n");
            writer.write("HashAlgorithm   = " + HASH_ALGORITHM + "\n");
            writer.write("SuffixSize      = " + SUFFIX_SIZE + "\n");
            writer.write("AutoSuffixSize  = " + AUTO_SUFFIX_SIZE + "\n");
            writer.write("SaveFileHash    = " + SAVE_FILE_HASH + "\n");
            writer.write("DeleteParts     = " + DELETE_PARTS + "\n");
            writer.write("NoHibernation   = " + NO_HIBERNATION + "\n");
            writer.write("UseNotification = " + USE_NOTIFICATION + "\n");
            writer.write("ShowTrayIcon    = " + SHOW_TRAY_ICON + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                    load();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
