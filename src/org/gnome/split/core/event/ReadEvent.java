/*
 * ReadEvent.java
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
 * Event thrown periodically when a number of bytes of the splitting file are
 * read.
 * 
 * @author Guillaume Mazoyer
 */
public class ReadEvent extends FileEvent
{
    private boolean fileRead;

    private long bytesRead;

    public ReadEvent(long bytesRead, boolean fileRead) {
        this.bytesRead = bytesRead;
        this.fileRead = fileRead;
    }

    /**
     * Return the number of bytes already read.
     */
    public long getBytesRead() {
        return bytesRead;
    }

    /**
     * Return whether or not file read has finished.
     */
    public boolean isFileRead() {
        return fileRead;
    }
}
