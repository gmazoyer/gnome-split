/*
 * Configuration.java
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
     * Default view to display
     */
    public byte DEFAULT_VIEW;

    /**
     * Allow multiple instances
     */
    public boolean MULTIPLE_INSTANCES;

    /**
     * Write a file containing the file hash.
     */
    public boolean SAVE_FILE_HASH;

    /**
     * The ID of the default algorithm to use to split files.
     */
    public int DEFAULT_ALGORITHM;

    /**
     * The default directory to select files to split.
     */
    public String SPLIT_DIRECTORY;

    /**
     * Delete the parts file after the assembly.
     */
    public boolean DELETE_PARTS;

    /**
     * Open the file after a successful merge.
     */
    public boolean OPEN_FILE_AT_END;

    /**
     * The default directory to select files to merge.
     */
    public String MERGE_DIRECTORY;

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
    public boolean SHOW_STATUS_ICON;

    /**
     * Private constructor can't instantiate Configuration in other class.<br>
     * Check for preferences file and load it.
     */
    public Configuration() throws IOException {
        configuration = new File(Constants.CONFIG_FILE);

        // File not found
        if (!configuration.exists()) {
            // Check if path exists and create it if necessary
            File path = new File(Constants.CONFIG_FOLDER);
            if (!path.exists()) {
                path.mkdirs();
            }

            // Create file and initialize preferences
            configuration.createNewFile();
            this.createPreferences();
        }
        this.load();
    }

    /**
     * This method is used to reset the preferences.
     */
    private void createPreferences() {
        FileWriter writer = null;
        try {
            // Open the file writer
            writer = new FileWriter(configuration);

            // Write general config
            writer.write("DefaultView       = 0\n");
            writer.write("MultipleInstances = false\n");

            // Write split config
            writer.write("SaveFileHash      = true\n");
            writer.write("DefaultAlgo       = 0\n");
            writer.write("SplitDirectory    = " + System.getProperty("user.home") + "\n");

            // Write merge config
            writer.write("DeleteParts       = false\n");
            writer.write("OpenFile          = false\n");
            writer.write("MergeDirectory    = " + System.getProperty("user.home") + "\n");

            // Write desktop config
            writer.write("NoHibernation     = true\n");
            writer.write("UseNotification   = true\n");
            writer.write("ShowStatusIcon    = false\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                    this.load();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is used to load the preferences file.
     */
    private void load() {
        try {
            Properties preferences = new Properties();
            InputStream stream = new FileInputStream(configuration);

            // Load the properties
            preferences.load(stream);
            stream.close();

            // Load general config
            DEFAULT_VIEW = Byte.parseByte(preferences.getProperty("DefaultView", "0"));
            MULTIPLE_INSTANCES = Boolean.parseBoolean(preferences.getProperty("MultipleInstances",
                    "false"));

            // Load split config
            SAVE_FILE_HASH = Boolean.parseBoolean(preferences.getProperty("SaveFileHash", "true"));
            DEFAULT_ALGORITHM = Integer.parseInt(preferences.getProperty("DefaultAlgo", "0"));
            SPLIT_DIRECTORY = preferences.getProperty("SplitDirectory", System.getProperty("user.home"));

            // Load merge config
            DELETE_PARTS = Boolean.parseBoolean(preferences.getProperty("DeleteParts", "false"));
            OPEN_FILE_AT_END = Boolean.parseBoolean(preferences.getProperty("OpenFile", "false"));
            MERGE_DIRECTORY = preferences.getProperty("MergeDirectory", System.getProperty("user.home"));

            // Load desktop config
            NO_HIBERNATION = Boolean.parseBoolean(preferences.getProperty("NoHibernation", "true"));
            USE_NOTIFICATION = Boolean.parseBoolean(preferences.getProperty("UseNotification", "false"));
            SHOW_STATUS_ICON = Boolean.parseBoolean(preferences.getProperty("ShowStatusIcon", "false"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * This method is used to save the preferences by overwriting the file.
     */
    public void savePreferences() {
        FileWriter writer = null;
        try {
            // Open the file writer
            writer = new FileWriter(configuration);

            // Write general config
            writer.write("DefaultView       = " + DEFAULT_VIEW + "\n");
            writer.write("MultipleInstances = " + MULTIPLE_INSTANCES + "\n");

            // Write split config
            writer.write("SaveFileHash      = " + SAVE_FILE_HASH + "\n");
            writer.write("DefaultAlgo       = " + DEFAULT_ALGORITHM + "\n");
            writer.write("SplitDirectory    = " + SPLIT_DIRECTORY + "\n");

            // Write merge config
            writer.write("DeleteParts       = " + DELETE_PARTS + "\n");
            writer.write("OpenFile          = " + OPEN_FILE_AT_END + "\n");
            writer.write("MergeDirectory    = " + MERGE_DIRECTORY + "\n");

            // Write desktop config
            writer.write("NoHibernation     = " + NO_HIBERNATION + "\n");
            writer.write("UseNotification   = " + USE_NOTIFICATION + "\n");
            writer.write("ShowStatusIcon    = " + SHOW_STATUS_ICON + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                    this.load();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
