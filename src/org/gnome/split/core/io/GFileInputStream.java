/*
 * GFileInputStream.java
 * 
 * Copyright (c) 2009-2013 Guillaume Mazoyer
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
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Subclass of {@link FileInputStream} to be able to close all opened streams
 * when needed.
 * 
 * @author Guillaume Mazoyer
 */
public final class GFileInputStream extends FileInputStream
{
    /**
     * Instances of {@link GFileInputStream} that have been opened.
     */
    private static final List<GFileInputStream> instances;

    static {
        instances = new ArrayList<GFileInputStream>();
    }

    public GFileInputStream(String name) throws FileNotFoundException {
        super(name);
        instances.add(this);
    }

    public GFileInputStream(File file) throws FileNotFoundException {
        super(file);
        instances.add(this);
    }

    public GFileInputStream(FileDescriptor descriptor) {
        super(descriptor);
        instances.add(this);
    }

    /**
     * Return all instances of {@link GFileInputStream} that have been opened.
     */
    public static List<GFileInputStream> getInstances() {
        return instances;
    }

    @Override
    public void close() throws IOException {
        super.close();
        instances.remove(this);
    }
}
