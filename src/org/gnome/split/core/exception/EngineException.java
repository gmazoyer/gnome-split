/*
 * EngineException.java
 * 
 * Copyright (c) 2009-2010 Guillaume Mazoyer
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
 * Define a new {@link Exception} to manager exceptions thrown by
 * {@link Engine} classes.
 * 
 * @author Guillaume Mazoyer
 */
public class EngineException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * The {@link ExceptionMessage message} of this {@link Exception
     * exception}.
     */
    protected ExceptionMessage message;

    /**
     * If this exception is just used as a warning or not.
     */
    protected boolean warning;

    /**
     * Create a new exception with a <code>message</code>.
     */
    public EngineException(String message) {
        super(message);
        this.warning = false;
    }

    /**
     * Create a new exception with an <code>error</code> (a {@link Throwable}
     * object).
     */
    public EngineException(Throwable error) {
        super(error);
        this.warning = false;
    }

    /**
     * Create a new exception with a <code>message</code> and an
     * <code>error</code> (a {@link Throwable} object).
     */
    public EngineException(String message, Throwable error) {
        super(message, error);
        this.warning = false;
    }

    /**
     * Create an {@link Exception} with an {@link ExceptionMessage} .
     */
    public EngineException(ExceptionMessage message) {
        this(message.getMessage());
        this.message = message;
        this.warning = false;
    }

    /**
     * Get the {@link ExceptionMessage} of this {@link Exception exception}.
     */
    public ExceptionMessage getExceptionMessage() {
        return message;
    }

    /**
     * Check if this exception is just a warning.
     */
    public boolean isWarning() {
        return warning;
    }
}
