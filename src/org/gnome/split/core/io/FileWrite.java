/*
 * FileWrite.java
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.gnome.split.core.algorithm.NamingAlgorithm;
import org.gnome.split.core.event.ChunkCreatedEvent;
import org.gnome.split.core.event.ErrorSplittingEvent;
import org.gnome.split.core.event.FinishEvent;
import org.gnome.split.core.event.WriteEvent;
import org.gnome.split.core.memory.ReadWriteBufferMemory;
import org.gnome.split.core.memory.ReadWriteMemory;

/**
 * This class allows to write a file (or a portion of it) in a separate thread
 * It launches the following events :
 * <ul>
 * <li><code>ErrorSplittingEvent</code>: when an error is produced. If the
 * error is produced in the beginning, before opening the file to split (or
 * opening it), this event will be thrown without a previous
 * <code>StartSplittingEvent</code>.</li>
 * <li><code>WriteEvent</code>: every n bytes write, this event is thrown. It
 * includes the number of bytes already written, and the written state
 * (finished or not). This event can be controlled, but even if no
 * <code>WriteEvent</code>s have to be launched, when the write finishes, it
 * will be raised an event with the write state on finished</li>
 * </ul>
 * 
 * To use this class, use just the {@link #startWritting(Observer, boolean)
 * startWritting()} and {@link #stopWritting()} methods, don't create a new
 * {@link Thread} explicitly.
 * 
 * @author Guillaume Mazoyer
 */
public class FileWrite extends Observable implements Runnable
{
    private ReadWriteMemory memory;

    private NamingAlgorithm algorithm;

    private long currentChunk;

    private long chunkSize;

    private long write;

    private long thisChunkSize;

    private FileOutputStream file;

    private long chunkWrite;

    private boolean sendEvents;

    private Thread thread;

    public FileWrite(ReadWriteMemory memory, NamingAlgorithm algorithm, long write, long maxChunkSize) {
        this.memory = memory;
        this.algorithm = algorithm;
        this.currentChunk = 1;
        this.chunkSize = maxChunkSize;
        this.write = write;
        this.thisChunkSize = 0;
        this.file = null;
        this.thread = null;
    }

    /**
     * Starts (if not done before) a thread to write the file. It returns
     * immediately to know when the writing has finished.
     */
    public void startWritting(Observer observer, boolean sendEvents) {
        if (thread == null) {
            this.addObserver(observer);
            this.sendEvents = sendEvents;

            // Start the thread
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Stop the writing of the chunks.
     */
    @SuppressWarnings("deprecation")
    public synchronized void stopWritting() {
        if (thread != null) {
            thread.stop();
            this.endEverything();
        }
    }

    /**
     * Destroy any resource still used.
     */
    private synchronized void endEverything() {
        // Close the file
        if (file != null) {
            try {
                file.close();
            } catch (Exception e) {
                // Just drop it
            }
        }

        // Remove the observers
        this.deleteObservers();

        // Free few more things
        file = null;
        memory = null;
        algorithm = null;

        // Destroy the thread
        thread = null;
    }

    @Override
    public void run() {
        ErrorSplittingEvent event = null;
        try {
            while ((event == null) && (write > 0)) {
                // Write the file
                ReadWriteBufferMemory buffer = (ReadWriteBufferMemory) memory.getReadMemory();
                event = this.writeFile(buffer.getBuffer(), buffer.getSize(), buffer.isEnded());
                
                // Release the memory
                memory.releaseReadMemory();
            }
        } catch (InterruptedException e) {
            event = new ErrorSplittingEvent(ErrorSplittingEvent.INTERNAL_ERROR3);
        } catch (IOException e) {
            event = new ErrorSplittingEvent(ErrorSplittingEvent.IOEXCEPTION_WRITTING);
        }

        this.setChanged();

        // Notify the end of the write
        if (event != null) {
            this.notifyObservers(event);
        } else {
            this.notifyObservers(new FinishEvent());
        }

        this.endEverything();
    }

    /**
     * Write the specified number of bytes that are allocated in the buffer.
     */
    private ErrorSplittingEvent writeFile(byte[] buffer, int bytes, boolean eof) throws IOException {
        ErrorSplittingEvent event = null;
        if (bytes > write) {
            event = new ErrorSplittingEvent(ErrorSplittingEvent.INTERNAL_ERROR1);
        } else {
            int start = 0;
            while (start < bytes) {
                // Get name and size of the file
                file = this.getOutputFile();
                int toWrite = bytes - start;

                if (toWrite > chunkWrite) {
                    toWrite = (int) chunkWrite;
                }

                // Write and update info
                file.write(buffer, start, toWrite);
                start += toWrite;
                write -= toWrite;
                chunkWrite -= toWrite;

                // Notify from a write action
                if (sendEvents) {
                    this.setChanged();
                    this.notifyObservers(new WriteEvent(thisChunkSize - chunkWrite, chunkWrite == 0));
                }

                // Close the stream
                if (chunkWrite == 0) {
                    file.close();
                    file = null;
                }
            }

            // EOF reach to early
            if ((write > 0) && eof) {
                event = new ErrorSplittingEvent(ErrorSplittingEvent.INTERNAL_ERROR2);
            }
        }
        return event;
    }

    /**
     * Get the output file. It returns the class variable &quot;file&quot;; if
     * it is null, it opens a new file using the NamingAlg. It also updates
     * the variable chunkWrite, if it is needed.
     */
    private FileOutputStream getOutputFile() throws IOException {
        if (file == null) {
            // Define name and size for the file
            File createFile = algorithm.getOutputFile(currentChunk);
            chunkWrite = chunkSize;

            if (chunkWrite > write) {
                chunkWrite = write;
            }

            thisChunkSize = chunkWrite;

            // Notify from a new chunk creation
            this.setChanged();
            this.notifyObservers(new ChunkCreatedEvent(createFile.getName(), currentChunk++, chunkWrite));

            // Open a stream for the file
            file = new FileOutputStream(createFile);
        }
        
        return file;
    }
}
