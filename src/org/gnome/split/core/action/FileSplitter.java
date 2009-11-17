/*
 * FileSplitter.java
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
package org.gnome.split.core.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.gnome.split.core.algorithm.NamingAlgorithm;
import org.gnome.split.core.event.ErrorSplittingEvent;
import org.gnome.split.core.event.FinishEvent;
import org.gnome.split.core.event.StartSplittingEvent;
import org.gnome.split.core.event.StopSplittingEvent;
import org.gnome.split.core.io.FileRead;
import org.gnome.split.core.io.FileWrite;
import org.gnome.split.core.memory.ReadWriteBufferMemory;
import org.gnome.split.core.memory.ReadWriteMemory;

/**
 * Make the splitting of a file.
 * 
 * <p>
 * It gets a filename, a chunk size, and will create as many chunk files as
 * needed, each of them with a maximum size equal to the specified chunk size.
 * To generate the chunk filename it uses a {@link NamingAlgorithm}, that must
 * be also specified.
 * 
 * <p>
 * To allow any object the observation of the splitting procedure, it extends
 * the class <code>Observable</code>, and throws the following events (all of
 * them are specializations of the <code>SplitFileEvent</code> class):
 * <ul>
 * <li><code>StartSplittingEvent</code>: when the splitting starts. This is
 * always the first event, (except if an error is produced before) and
 * includes some information about the splitting operation: the filename, its
 * length, the number of chunks to create and their size.</li>
 * <li><code>StopSplittingEvent</code>: when the splitting finishes with no
 * errors.</li>
 * <li><code>ErrorSplittingEvent</code>: when an error occurred. If the error
 * is produced in the beginning, before opening the file to split (or opening
 * it), this event will be thrown without a previous
 * <code>StartSplittingEvent</code></li>
 * <li><code>ChunkCreatedEvent</code>: when a new chunk file is created. It
 * includes the name of the file created, its sequence number, and its length.
 * </li>
 * <li><code>ReadEvent</code>: every n bytes read, this event is thrown. It
 * includes the number of bytes already read, and the read state (finished or
 * not).</li>
 * <li><code>WriteEvent</code>: every n bytes written, this event is thrown.
 * It includes the number of bytes already written (in the current chunk), and
 * the write state (finished or not).</li> The throwing of events can be
 * controlled.
 * 
 * @author Guillaume Mazoyer
 */
public class FileSplitter extends Observable implements Observer
{
    private final int MAX_BLOCK_LENGTH = 65536;

    private FileInputStream input;

    private long size;

    private long number;

    private ReadWriteMemory memory;

    private FileRead read;

    private FileWrite write;

    private boolean error;

    public void split(String filename, long chunkSize, NamingAlgorithm algorithm, boolean receiveEvents) {
        ErrorSplittingEvent event = this.initFileSplitter(filename, chunkSize, algorithm);
        if (event != null) {
            // An exception has been produced, do not start the split
            event.setFilename(filename);
            this.setChanged();
            this.notifyObservers(event);
        } else {
            // Start an new split event
            this.setChanged();
            this.notifyObservers(new StartSplittingEvent(filename, size, number, chunkSize));

            // Init read and write processes
            read = new FileRead(input, size, memory);
            write = new FileWrite(memory, algorithm, size, chunkSize);

            // Start read and write processes
            read.startReading(this, receiveEvents);
            write.startWritting(this, receiveEvents);

            // Wait for the processes to finish
            this.waitToFinish();
        }
    }

    /**
     * Stops the current splitting. An event {@link ErrorSplittingEvent} will
     * be emitted.
     */
    public void stopSplitting() {
        if (memory != null) {
            // Stop read/write
            read.stopReading();
            write.stopWritting();

            // Change observable status
            this.setChanged();
            this.notifyObservers(new ErrorSplittingEvent(ErrorSplittingEvent.SPLITTING_CANCELLED));

            // Inform for an error
            error = true;

            // Finish it
            this.finishWait();
        }
    }

    /**
     * Initialize some variables used in the <code>FileSplitter</code>: the
     * input file, its length, the {@link ReadWriteBufferMemory}, the
     * {@link NamingAlgorithm}, and the number of chunks. An
     * {@link ErrorSplittingEvent} is thrown if an exception is produced, or
     * <code>null</code> if no exceptions at all are produced.
     */
    private ErrorSplittingEvent initFileSplitter(String filename, long chunkSize,
            NamingAlgorithm algorithm) {
        ErrorSplittingEvent event = null;
        File file = new File(filename);

        input = null;
        size = 0;
        memory = null;
        read = null;
        write = null;
        error = false;

        if (!file.exists()) {
            event = new ErrorSplittingEvent(ErrorSplittingEvent.NOT_EXISTING_FILE);
        } else if (!file.canRead()) {
            event = new ErrorSplittingEvent(ErrorSplittingEvent.NOT_READABLE_FILE);
        } else if (file.isDirectory()) {
            event = new ErrorSplittingEvent(ErrorSplittingEvent.FILE_IS_DIRECTORY);
        } else {
            try {
                input = new FileInputStream(file);

                size = file.length();
                if (size == 0) {
                    try {
                        input.close();
                    } catch (Exception e) {
                        // Just drop it
                    }

                    input = null;
                    event = new ErrorSplittingEvent(ErrorSplittingEvent.FILE_EMPTY);
                } else {
                    number = size / chunkSize;

                    if ((size % chunkSize) != 0) {
                        number++;
                    }

                    int blockSize = (size > MAX_BLOCK_LENGTH) ? MAX_BLOCK_LENGTH : (int) size;
                    memory = new ReadWriteMemory(new ReadWriteBufferMemory(blockSize),
                            new ReadWriteBufferMemory(blockSize));
                    event = algorithm.init(file, number);
                }
            } catch (FileNotFoundException e) {
                event = new ErrorSplittingEvent(ErrorSplittingEvent.FILE_NOT_FOUND);
            }
        }
        return event;
    }

    @Override
    public void update(Observable observable, Object object) {
        if (object instanceof FinishEvent) {
            // The splitting has finished
            this.finishWait();
        } else {
            this.setChanged();
            this.notifyObservers(object);

            if (object instanceof ErrorSplittingEvent) {
                // Set the error flag
                error = true;

                // Finish the splitting
                this.finishWait();
            }
        }
    }

    /**
     * Wait until the operation finishes.
     */
    private synchronized void waitToFinish() {
        try {
            // Block until finishWait() is called
            this.wait();
        } catch (InterruptedException e) {
            error = true;
            this.setChanged();
            this.notifyObservers(new ErrorSplittingEvent(ErrorSplittingEvent.INTERNAL_ERROR3));
        }

        if (error) {
            // Stop the threads (can be active)
            read.stopReading();
            write.stopWritting();
        }

        try {
            // Close the stream
            input.close();

            if (!error) {
                // Notify the end of splitting
                this.setChanged();
                this.notifyObservers(new StopSplittingEvent());
            }
        } catch (IOException e) {
            // Error closing the file
            this.setChanged();
            this.notifyObservers(new ErrorSplittingEvent(ErrorSplittingEvent.IOEXCEPTION_READING));
        }

        // Cleaning
        input = null;
        memory = null;
        read = null;
        write = null;
    }

    /**
     * Finish the operation, making {@link #waitToFinish()} to run out.
     */
    private synchronized void finishWait() {
        // Wake up the process to finish the wait
        this.notify();
    }
}
