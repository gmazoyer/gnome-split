/*
 * ReadWriteBufferMemory.java
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
package org.gnome.split.core.memory;

import org.gnome.split.core.io.FileRead;
import org.gnome.split.core.io.FileWrite;

/**
 * This class implements the {@link BufferMemory} to allow the communication
 * between the read and write threads ({@link FileRead} / {@link FileWrite}).<br>
 * <br>
 * It is a block of bytes, with a fixed size, that allows to communicate:
 * <ul>
 * <li>
 * the real data length in the block</li>
 * <li>
 * if the write operations have finished (that is, if there is nothing else to
 * read)</li>
 * </ul>
 * 
 * @author Guillaume Mazoyer
 */
public class ReadWriteBufferMemory implements BufferMemory
{
    private byte[] buffer;

    private int maximum;

    private int size;

    private boolean end;

    public ReadWriteBufferMemory(int size) {
        this.buffer = new byte[size];
        this.maximum = size;
        this.size = size;
        this.end = false;
    }

    /**
     * Return the allocated buffer.
     */
    public byte[] getBuffer() {
        return buffer;
    }

    /**
     * Return the size of the buffer. This value won't change during the
     * ReadWriteBufferMemory lifetime.
     */
    public int getMaxSize() {
        return maximum;
    }

    /**
     * Set the size of the data stored in the buffer.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Return the size of the data stored in the buffer.
     */
    public int getSize() {
        return size;
    }

    /**
     * Set the End Of Operation flag.
     */
    public void setEnded() {
        end = true;
    }

    /**
     * Return the End Of Operation flag.
     */
    public boolean isEnded() {
        return end;
    }
}
