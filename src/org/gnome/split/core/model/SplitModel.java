/*
 * SplitModel.java
 * 
 * Copyright (c) 2009-2013 Guillaume Mazoyer
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
package org.gnome.split.core.model;

import java.io.File;

import org.gnome.split.core.utils.Algorithm;

/**
 * An interface that all classes which want to create a split engine must
 * implement.
 * 
 * @author Guillaume Mazoyer
 */
public interface SplitModel
{
    /**
     * Get the name of the file to split.
     */
    public File getFile();

    /**
     * Get the directory where the split will be done.
     */
    public File getDirectory();

    /**
     * Get the names of the files to create.
     */
    public String getDestination();

    /**
     * Get the maximum size of each chunk.
     */
    public long getMaxSize();

    /**
     * Get the ID of the {@link Algorithm algorithm} to use.
     */
    public int getAlgorithm();
}
