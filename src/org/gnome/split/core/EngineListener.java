/*
 * EngineListener.java
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
package org.gnome.split.core;

import org.gnome.split.core.exception.EngineException;

/**
 * Define the way that an {@link Engine} class will notify the
 * &quot;view&quot;.
 * 
 * @author Guillaume Mazoyer
 */
public interface EngineListener
{
    /**
     * Update the current {@link Engine engine}.
     */
    public void setEngine(Engine engine);

    /**
     * Get the current used {@link Engine engine}.
     */
    public Engine getEngine();

    /**
     * Used when a part has been created.
     */
    public void enginePartCreated(String filename);

    /**
     * Used when a part is currently being read.
     */
    public void enginePartRead(String filename);

    /**
     * Used when the action has finished.
     */
    public void engineEnded();

    /**
     * Used when the action is stopped.
     */
    public void engineStopped();

    /**
     * Used when an {@link EngineException error} has occurred.
     */
    public void engineError(EngineException exception);

    /**
     * Used when a read has been done.
     */
    public void engineDone(double done, double total);
}
