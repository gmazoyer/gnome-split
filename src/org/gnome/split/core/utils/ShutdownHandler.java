/*
 * ShutdownHandler.java
 * 
 * Copyright (c) 2009-2012 Guillaume Mazoyer
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
package org.gnome.split.core.utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gnome.split.core.io.GFileInputStream;
import org.gnome.split.core.io.GFileWriter;
import org.gnome.split.core.io.GRandomAccessFile;

/**
 * A class to handle cases where kill signals are emitted to terminate the
 * application.
 * 
 * @author Guillaume Mazoyer
 */
public final class ShutdownHandler extends Thread
{
    @Override
    public void run() {
        List<Closeable> io = new ArrayList<Closeable>();

        // Get all opened IO
        io.addAll(GFileInputStream.getInstances());
        io.addAll(GFileWriter.getInstances());
        io.addAll(GRandomAccessFile.getInstances());

        for (Closeable closeable : io) {
            try {
                // Close each opened IO
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
