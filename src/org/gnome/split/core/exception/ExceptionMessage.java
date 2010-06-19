/*
 * ExceptionMessage.java
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

import static org.freedesktop.bindings.Internationalization._;

/**
 * Enumeration which contains all error messages.
 * 
 * @author Guillaume Mazoyer
 */
public enum ExceptionMessage
{
    MD5_DIFFER(
            _("MD5 sums are different."),
            _("There is no guarantee that the created file will work. Maybe you should try to merge the chunks again.")), INVALID_SIZE(
            _("Invalid chunk size."),
            _("You must specify a size which is lower than the size of the file to split."));

    /**
     * The message which will be used in the {@link Exception}.
     */
    private String message;

    /**
     * The more detailed message which will be used to be displayed in the
     * interface.
     */
    private String details;

    /**
     * Create an {@link ExceptionMessage} with a <code>message</code> and
     * <code>details</code>.
     */
    private ExceptionMessage(String message, String details) {
        this.message = message;
        this.details = details;
    }

    /**
     * Get the short message of this {@link ExceptionMessage}.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Get the detailed message of this {@link ExceptionMessage}.
     */
    public String getDetails() {
        return details;
    }
}
