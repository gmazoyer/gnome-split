/*
 * WriteEvent.java
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
 * Event thrown periodically when a number of bytes of the chunk are wrote.
 * 
 * @author Guillaume Mazoyer
 */
public class WriteEvent extends FileEvent
{
    private boolean fileWritten;

    private long bytesWritten;

    public WriteEvent(long bytesWritten, boolean fileWritten) {
        this.bytesWritten = bytesWritten;
        this.fileWritten = fileWritten;
    }

    /**
     * Return the number of bytes already written.
     */
    public long getBytesWritten() {
        return bytesWritten;
    }

    /**
     * Return whether or not the file write has finished.
     */
    public boolean isFileWritten() {
        return fileWritten;
    }
}
