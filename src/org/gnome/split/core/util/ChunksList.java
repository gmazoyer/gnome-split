/*
 * ChuncksList.java
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
package org.gnome.split.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.gnome.split.core.action.FileSplitter;
import org.gnome.split.core.event.ChunkCreatedEvent;
import org.gnome.split.core.event.ErrorSplittingEvent;
import org.gnome.split.core.event.StopSplittingEvent;

/**
 * Container for chunks created while splitting a file.
 * 
 * @author Guillaume Mazoyer
 */
public class ChunksList implements Observer
{
    /**
     * The list which will contain the filename of each chunk.
     */
    private List<String> chunks;

    /**
     * The object which will actually split the file into chunks.
     */
    private FileSplitter splitter;

    /**
     * Create a new list of chunks and set a {@link FileSplitter} as an
     * {@link Observer} (it cannot be <code>null</code>).
     */
    public ChunksList(FileSplitter splitter) {
        this.chunks = new ArrayList<String>();
        this.splitter = splitter;
        this.splitter.addObserver(this);
    }

    /**
     * Return the number of chunks created.
     */
    public int getNumberOfChunks() {
        return chunks.size();
    }

    /**
     * Return an {@link Enumeration} with the chunks created.
     */
    public Enumeration<String> getChunks() {
        return (Enumeration<String>) Collections.enumeration(chunks);
    }

    @Override
    public void update(Observable observable, Object object) {
        if (object instanceof ChunkCreatedEvent) {
            // A chunk is created, get its filename.
            chunks.add(new String(((ChunkCreatedEvent) object).getFileName()));
        } else if ((object instanceof ErrorSplittingEvent) || (object instanceof StopSplittingEvent)) {
            // The splitter finished its work (with or without errors).
            splitter.deleteObserver(this);
            splitter = null;
        }
    }
}
