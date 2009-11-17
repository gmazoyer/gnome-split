/*
 * StartSplittingEvent.java
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
package org.gnome.split.core.event;

/**
 * Event thrown when the splitting starts.
 * 
 * @author Guillaume Mazoyer
 */
public class StartSplittingEvent extends FileSplitEvent
{
    private String filename;

    private long number;

    private long fileSize;

    private long chunkSize;

    public StartSplittingEvent(String filename, long fileSize, long number, long chunkSize) {
        this.filename = new String(filename);
        this.number = number;
        this.fileSize = fileSize;
        this.chunkSize = chunkSize;
    }

    /**
     * Return the name of the file being splitted.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Return the number of chunks that will be created.
     */
    public long getNumberOfChunks() {
        return number;
    }

    /**
     * Return the size of the file being splitted.
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Return the size of each chunk (the last one can be smaller).
     */
    public long getChunkSize() {
        return chunkSize;
    }
}
