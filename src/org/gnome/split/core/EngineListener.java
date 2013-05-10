/*
 * EngineListener.java
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
package org.gnome.split.core;

import java.util.List;

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
     * Used when the speed of the action has changed.
     */
    public void engineSpeedChanged(long speed);

    /**
     * Used when a part has been created.
     */
    public void enginePartCreated(String filename);

    /**
     * Used when a part has been written.
     */
    public void enginePartWritten(String filename);

    /**
     * Used when a part is currently being read.
     */
    public void enginePartRead(String filename);

    /**
     * Used when the MD5 sum calculation is started.
     */
    public void engineMD5SumStarted();

    /**
     * Used when the MD5 sum calculation has ended.
     */
    public void engineMD5SumEnded();

    /**
     * Used when the engine is ready to do something.
     */
    public void engineReady();

    /**
     * Used when the engine is running.
     */
    public void engineRunning();

    /**
     * Used when the engine is suspended.
     */
    public void engineSuspended();

    /**
     * Used when the action has finished.
     */
    public void engineEnded();

    /**
     * Used when the action is stopped.
     */
    public void engineStopped();

    /**
     * Used when an {@link Exception error} has occurred.
     */
    public void engineError(Exception exception);

    /**
     * Used when a read has been done.
     */
    public void engineDone(long done, long total);

    /**
     * Used when the engine has finished and wants to give a list of files.
     */
    public void engineFilesList(List<String> list);

    /**
     * Get the current list of files that the last engine provided.
     */
    public List<String> getFilesList();
}
