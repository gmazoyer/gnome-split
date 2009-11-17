/*
 * SplitterDisplay.java
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
package org.gnome.split.core.display;

/**
 * This is the interface that the splitting class will use to show its status.
 * 
 * @author Guillaume Mazoyer
 */
public interface SplitterDisplay extends ActionDisplay
{
    /**
     * This is not a showing information method, but the way the splitting
     * class will use to say that a chunk has been created.
     */
    public void chunkCreated(String filename);

    /**
     * This is not a showing information method, but the way the splitting
     * class will use to say that has finished its task.
     */
    public void splittingFinished(boolean error);
}
