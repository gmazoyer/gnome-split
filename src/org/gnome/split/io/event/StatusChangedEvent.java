/*
 * StatusChangedEvent.java
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

public class StatusChangedEvent extends EventObject
{
    private static final long serialVersionUID = 1L;

    /**
     * The new status value.
     */
    private String status;

    public StatusChangedEvent(Object source, String status) {
        super(source);

        // Set the new status value
        this.status = status;
    }

    /**
     * Get the new value of the status.
     * 
     * @return the current status.
     */
    public String getStatus() {
        return status;
    }
}
