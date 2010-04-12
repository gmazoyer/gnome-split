/*
 * EngineFactory.java
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
package org.gnome.split.core;

import java.io.File;
import java.lang.reflect.Constructor;

import org.gnome.split.GnomeSplit;
import org.gnome.split.core.merger.DefaultMergeEngine;
import org.gnome.split.core.model.MergeModel;
import org.gnome.split.core.model.SplitModel;
import org.gnome.split.core.splitter.DefaultSplitEngine;
import org.gnome.split.core.utils.Algorithm;

/**
 * A class used to create an {@link Engine engine}.
 * 
 * @author Guillaume Mazoyer
 */
public class EngineFactory
{
    /**
     * List of the splitters that can be used.
     */
    private static final Class<?>[] splitters = new Class[] {
            org.gnome.split.core.splitter.Generic.class, org.gnome.split.core.splitter.GnomeSplit.class,
            org.gnome.split.core.splitter.Xtremsplit.class, org.gnome.split.core.splitter.KFK.class,
            org.gnome.split.core.splitter.YoyoCut.class
    };

    /**
     * List of the mergers that can be used.
     */
    private static final Class<?>[] mergers = new Class[] {
            org.gnome.split.core.merger.Generic.class, org.gnome.split.core.merger.GnomeSplit.class,
            org.gnome.split.core.merger.Xtremsplit.class, org.gnome.split.core.merger.KFK.class,
            org.gnome.split.core.merger.YoyoCut.class
    };

    /**
     * Create a split engine to split a file.
     */
    public static DefaultSplitEngine createSplitEngine(final GnomeSplit app, SplitModel model) {
        DefaultSplitEngine engine = null;

        // Split related info
        File file = model.getFile();
        long size = model.getMaxSize();
        String dest = model.getDestination();
        int algorithm = model.getAlgorithm();

        Constructor<?> constructor;
        try {
            // Get the class constructor
            constructor = splitters[algorithm].getConstructor(GnomeSplit.class, File.class, long.class,
                    String.class);

            // Create the runnable object
            engine = (DefaultSplitEngine) constructor.newInstance(app, file, size, dest);
        } catch (Exception e) {
            // Should *never* happen
            e.printStackTrace();
        }

        // Finally
        return engine;
    }

    /**
     * Create a merge engine to merge files.
     */
    public static DefaultMergeEngine createMergeEngine(final GnomeSplit app, MergeModel model) {
        DefaultMergeEngine engine = null;

        // Merge related info
        File file = model.getFile();
        String dest = model.getDestination();

        // To check the extension of the file
        String name = file.getName();
        String[] extensions = Algorithm.getExtensions();

        // ID of the merger to select
        byte index = -1;

        if (name.endsWith(extensions[0]) || name.endsWith(extensions[1])) {
            // Use Generic algorithm
            index = 0;
        } else if (name.endsWith(extensions[2])) {
            // Use GNOME Split algorithm
            index = 1;
        } else if (name.endsWith(extensions[3])) {
            // Use Xtremsplit algorithm
            index = 2;
        } else if (name.endsWith(extensions[4])) {
            // Use KFK algorithm
            index = 3;
        } else if (name.endsWith(extensions[5])) {
            // Use YoyoCut algorithm
            index = 4;
        }

        if (index != -1) {
            Constructor<?> constructor;
            try {
                // Get the class constructor
                constructor = mergers[index].getConstructor(GnomeSplit.class, File.class, String.class);

                // Create the runnable object
                engine = (DefaultMergeEngine) constructor.newInstance(app, file, dest);
            } catch (Exception e) {
                // Should *never* happen
                e.printStackTrace();
            }
        }

        // Finally
        return engine;
    }
}
