/*
 * Constants.java
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

import java.io.FileNotFoundException;

import org.gnome.gdk.Pixbuf;

/**
 * This class is used to define and load the global constants variables.
 * 
 * @author Guillaume Mazoyer
 */
public final class Constants
{
    /**
     * Name of the program.
     */
    public static String PROGRAM_NAME = "GNOME Split";

    /**
     * Version of the program.
     */
    public static String PROGRAM_VERSION = "0.4";

    /**
     * Website of the program.
     */
    public static String PROGRAM_WEBSITE = "http://www.respawner.fr/gnome-split/";

    /**
     * Logo of the program (as a {@link Pixbuf}).
     */
    public static Pixbuf PROGRAM_LOGO = null;

    /**
     * List of the program authors.
     */
    public static String[] PROGRAM_AUTHORS = null;

    /**
     * Path to the configuration folder.
     */
    public static String CONFIG_FOLDER = null;

    /**
     * Path to the configuration file.
     */
    public static String CONFIG_FILE = null;

    /**
     * Load all the constants file.
     */
    public static void load() {
        try {
            PROGRAM_LOGO = new Pixbuf("share/pixmaps/gnome-split.png");
            CONFIG_FOLDER = System.getProperty("user.home") + "/.config/gnome-split/";
            CONFIG_FILE = CONFIG_FOLDER + "config";
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }
}
