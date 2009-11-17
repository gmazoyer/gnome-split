/*
 * ActionDisplay.java
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
 * This interface is the top-level interface to define how action will show
 * their status.
 * 
 * @author Guillaume Mazoyer
 */
interface ActionDisplay
{
    /**
     * Method to show some informations.
     */
    public void showMessage(String message);

    /**
     * Method to show an error message.
     */
    public void showError(String error);

    /**
     * Method to show the progress done on read (from 0.0 to 1.0).
     */
    public void setReadProgress(double progress);

    /**
     * Method to show the progress done on writing a chunk (from 0.0 to 1.0).
     */
    public void setWriteProgress(double progress);

    /**
     * Method to show how many data have been read.
     */
    public void setReadInfos(long read, long total);

    /**
     * Method to show how many data have been written.
     */
    public void setWriteInfos(long written, long total);
}
