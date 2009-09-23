/*
 * ProgressChangedEvent.java
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
package org.gnome.split.io.event;

import java.util.EventObject;

/**
 * Class that represents a event created when an operation progress has
 * changed.
 * 
 * @author Guillaume Mazoyer
 */
public class ProgressChangedEvent extends EventObject
{
    private static final long serialVersionUID = 1L;

    /**
     * The new progress value.
     */
    private double progress;

    public ProgressChangedEvent(Object source, double progress) {
        super(source);

        // Set the new progress value
        this.progress = progress;
    }

    /**
     * Get the new value of the progress.
     * 
     * @return the current progress.
     */
    public double getProgress() {
        return progress;
    }
}
