/*
 * ChunkCreatedEvent.java
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
 * Event thrown when an new chunk file is created.
 * 
 * @author Guillaume Mazoyer
 */
public class ChunkCreatedEvent extends FileSplitEvent
{
    private String filename;

    private long number;

    private long size;

    public ChunkCreatedEvent(String filename, long number, long size) {
        this.filename = new String(filename);
        this.number = number;
        this.size = size;
    }

    /**
     * Return the name of the file being splitted.
     */
    public String getFileName() {
        return filename;
    }

    /**
     * Return the number of this chunk in the sequence of chunks being
     * created. The first is the &quot;1&quot;.
     */
    public long getSequenceNumber() {
        return number;
    }

    /**
     * Return the size of the chunk being created.
     */
    public long getFileSize() {
        return size;
    }
}
