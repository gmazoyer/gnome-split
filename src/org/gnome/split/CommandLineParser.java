/*
 * CommandLineParser.java
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
package org.gnome.split;

import java.io.File;

import org.gnome.split.core.utils.Algorithm;
import org.gnome.split.gtk.MainWindow;

/**
 * A class to parse the command line and the arguments the user gave.
 * 
 * @author Guillaume Mazoyer
 */
class CommandLineParser
{
    /**
     * A reference to the GTK+ interface to update.
     */
    private MainWindow window;

    CommandLineParser(final MainWindow window) {
        this.window = window;
    }

    /**
     * Open the file with GNOME Split.
     * 
     * <p>
     * The first argument must be <code>--split</code> or <code>--merge</code>
     * and the second argument must be a valid file.
     */
    void parseCommandLine(String[] args) {
        // Check if the file exists
        File file = new File(args[1]);
        if (!file.exists()) {
            return;
        }

        // The user wants to split the file
        if (args[0].equals("-s") || args[0].equals("--split")) {
            // Load the file to split
            window.getSplitWidget().setFile(args[1]);

            // Show the split widget
            window.getViewSwitcher().switchToSplit();
        } else if (args[0].equals("-m") || args[0].equals("--merge")) {
            // Update the merge widget
            window.getMergeWidget().setFirstFile(args[1]);
            // Show the merge widget
            window.getViewSwitcher().switchToMerge();
        }

    }

    /**
     * Open the file with GNOME Split.
     * 
     * <p>
     * If the file is a valid that can be merged, open the interface using the
     * merge widget and update it. In the other case, open the interface using
     * the split widget and update it.
     */
    void useCommandLineFile(String filename) {
        // The file is a chunk that can be merged
        if (Algorithm.isValidExtension(filename)) {
            this.parseCommandLine(new String[] {
                    "--merge", filename
            });
        } else {
            this.parseCommandLine(new String[] {
                    "--split", filename
            });
        }
    }
}
