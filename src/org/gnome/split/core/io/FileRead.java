/*
 * FileRead.java
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
package org.gnome.split.core.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.gnome.split.core.event.ErrorSplittingEvent;
import org.gnome.split.core.event.ReadEvent;
import org.gnome.split.core.memory.ReadWriteBufferMemory;
import org.gnome.split.core.memory.ReadWriteMemory;

/**
 * This class allows to read a file (or a portion of it) in a separate thread
 * It launches the following events :
 * <ul>
 * <li><code>ErrorSplittingEvent</code>: when an error is produced. If the
 * error is produced in the beginning, before opening the file to split (or
 * opening it), this event will be thrown without a previous
 * <code>StartSplittingEvent</code>.</li>
 * <li><code>ReadEvent</code>: every n bytes read, this event is thrown. It
 * includes the number of bytes already read, and the read state (finished or
 * not). This event can be controlled, but even if no <code>ReadEvent</code>s
 * have to be launched, when the read finishes, it will be raised an event
 * with the read state on finished</li>
 * </ul>
 * 
 * To use this class, use just the {@link #startReading(Observer, boolean)
 * startReading()} and {@link #stopReading()} methods, don't create a new
 * {@link Thread} explicitly.
 * 
 * @author Guillaume Mazoyer
 */
public class FileRead extends Observable implements Runnable
{
    private FileInputStream file;

    private ReadWriteMemory memory;

    private long read;

    private long constRead;

    private boolean sendEvents;

    private Thread thread;

    public FileRead(FileInputStream fis, long bytes, ReadWriteMemory memory) {
        this.file = fis;
        this.memory = memory;
        this.read = bytes;
        this.constRead = this.read;
        this.thread = null;
    }

    /**
     * Start (if not done before) a thread to read the file. It returns
     * immediately to know when the reading has finished, it is needed to
     * verify the {@link ReadEvent}s.
     */
    public void startReading(Observer observer, boolean sendEvents) {
        if (thread == null) {
            this.addObserver(observer);
            this.sendEvents = sendEvents;

            // Start the thread
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Stop the reading of the file.
     */
    @SuppressWarnings("deprecation")
    public synchronized void stopReading() {
        if (thread != null) {
            thread.stop();
            this.endEverything();
        }
    }

    /**
     * Destroy any resource still used.
     */
    private synchronized void endEverything() {
        // Destroy the thread
        thread = null;

        // Remove the observers
        this.deleteObservers();

        // Final cleanup
        file = null;
        memory = null;
    }

    @Override
    public void run() {
        ErrorSplittingEvent event = null;
        try {
            while ((event == null) && (read > 0)) {
                // Allocate a memory buffer
                ReadWriteBufferMemory buffer = (ReadWriteBufferMemory) memory.getWriteMemory();

                // Get the size of the buffer
                int toRead = buffer.getMaxSize();
                if (toRead > read) {
                    toRead = (int) read;
                }

                if (file.read(buffer.getBuffer(), 0, toRead) != toRead) {
                    event = new ErrorSplittingEvent(ErrorSplittingEvent.SPLITFILE_SMALLER);
                } else {
                    buffer.setSize(toRead);

                    read -= toRead;
                    boolean finished = (read == 0);

                    if (finished || sendEvents) {
                        this.setChanged();
                        this.notifyObservers(new ReadEvent((constRead - read), finished));

                        if (finished) {
                            buffer.setEnded();
                        }
                    }
                    memory.releaseWriteMemory();
                }
            }
        } catch (InterruptedException e) {
            event = new ErrorSplittingEvent(ErrorSplittingEvent.INTERNAL_ERROR3);
        } catch (IOException e) {
            event = new ErrorSplittingEvent(ErrorSplittingEvent.IOEXCEPTION_READING);
        }

        this.setChanged();

        if (event != null) {
            this.notifyObservers(event);
        }

        this.endEverything();
    }
}
