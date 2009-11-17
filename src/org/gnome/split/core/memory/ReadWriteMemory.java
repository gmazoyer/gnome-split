/*
 * ReadWriteMemory.java
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

/**
 * This class allows to create a block of memory where two different processes
 * (threads) can read and write in a synchronized way. To facilitate the
 * communication between the threads this block of memory is an instance of a
 * {@link BufferMemory} interface, allowing any particular implementation.<br>
 * <br>
 * A thread can get a write buffer, that should release later. Another (or the
 * same) thread can get the same buffer to read it, and also it will have to
 * release it.<br>
 * <br>
 * For implementation purposes, two buffers are created, to allow the
 * concurrent read and write.
 * 
 * @author Guillaume Mazoyer
 */
public class ReadWriteMemory
{
    private boolean writing;

    private boolean reading;

    private int freeBuffers;

    private int cursor;

    private BufferMemory[] buffers;

    public ReadWriteMemory(BufferMemory first, BufferMemory second) {
        writing = false;
        reading = false;
        freeBuffers = 2;
        cursor = 0;
        buffers = new BufferMemory[2];
        buffers[0] = first;
        buffers[1] = second;
    }

    /**
     * Gets a buffer to write in. This buffer should be released later. It
     * throws an <code>InterruptedException</code> if another thread has
     * interrupted this one, making unsafe to continue working with the
     * current instance of ReadWriteMemory.
     */
    public synchronized BufferMemory getWriteMemory() throws InterruptedException {
        while (writing) {
            // Do not use 2 buffers at the same time
            this.wait();
        }

        // Set writing
        writing = true;

        while (freeBuffers == 0) {
            // Wait for free buffer
            this.wait();
        }

        return buffers[cursor];
    }

    /**
     * Release a buffer previously got to write in. If there was no buffers
     * previously got, it does nothing.
     */
    public synchronized void releaseWriteMemory() {
        if (writing) {
            freeBuffers--;

            cursor = (cursor + 1) % 2;
            writing = false;

            this.notifyAll();
        }
    }

    /**
     * Gets a buffer to read off. This buffer is supposed to contains valid
     * data (that is, it has been got as WriteMemory previously). This buffer
     * should be released later.It throws an <code>InterruptedException</code>
     * if another thread has interrupted this one, making unsafe to continue
     * working with the current instance of ReadWriteMemory.
     */
    public synchronized BufferMemory getReadMemory() throws InterruptedException {
        while (reading) {
            // Do not use 2 buffers at the same time
            this.wait();
        }

        // Set reading
        reading = true;

        while (freeBuffers == 2) {
            // Wait for a buffer wrote
            this.wait();
        }

        int readCursor = (freeBuffers == 0) ? cursor : ((cursor + 1) % 2);
        return buffers[readCursor];
    }

    /**
     * Release a buffer previously got to read off.If there was no buffers
     * previously got, it does nothing.
     */
    public synchronized void releaseReadMemory() {
        if (reading) {
            freeBuffers++;
            reading = false;
            this.notifyAll();
        }
    }
}
