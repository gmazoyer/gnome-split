/*
 * DotNumberAlgorithm.java
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
package org.gnome.split.core.algorithm;

import java.io.File;

import org.gnome.split.core.event.ErrorSplittingEvent;

/**
 * Algorithm to add a &quot;.&quot; and the sequence value at the end of each
 * chunk filename.
 * 
 * @author Guillaume Mazoyer
 */
public class DotNumberAlgorithm implements NamingAlgorithm
{
    private String path;

    public DotNumberAlgorithm(String path) {
        this.path = new String(path + ".");
    }

    @Override
    public File getOutputFile(long sequence) {
        return new File(path + String.valueOf(sequence));
    }

    @Override
    public ErrorSplittingEvent init(File input, long chunks) {
        return null;
    }
}
