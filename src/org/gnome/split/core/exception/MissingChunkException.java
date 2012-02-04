/*
 * MissingChunkException.java
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
package org.gnome.split.core.exception;

/**
 * Define a new {@link Exception} to manage exceptions due to a missing chunk.
 * 
 * @author Guillaume Mazoyer
 */
public final class MissingChunkException extends EngineException
{
    private static final long serialVersionUID = 1L;

    /**
     * Create an {@link Exception} with an {@link ExceptionMessage} .
     */
    public MissingChunkException() {
        super(ExceptionMessage.MISSING_CHUNK, false);
    }
}
