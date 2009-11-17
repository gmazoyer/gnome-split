/*
 * ErrorSplittingEvent.java
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
package org.gnome.split.core.event;

import static org.freedesktop.bindings.Internationalization._;

import java.io.IOException;

/**
 * Event thrown when the splitting finishes due to an error.
 * 
 * @author Guillaume Mazoyer
 */
public class ErrorSplittingEvent extends FileSplitEvent
{
    /**
     * The file to split can not be read.
     */
    public static final int NOT_READABLE_FILE = 1;

    /**
     * The file to split does not exist.
     */
    public static final int NOT_EXISTING_FILE = 2;

    /**
     * The file to split is a directory.
     */
    public static final int FILE_IS_DIRECTORY = 3;

    /**
     * The file to split is not found.
     */
    public static final int FILE_NOT_FOUND = 4;

    /**
     * The file to split is empty.
     */
    public static final int FILE_EMPTY = 5;

    /**
     * {@link IOException} while reading the file to split.
     */
    public static final int IOEXCEPTION_READING = 6;

    /**
     * {@link IOException} while writing a chunk.
     */
    public static final int IOEXCEPTION_WRITTING = 7;

    /**
     * File smaller than expected.
     */
    public static final int SPLITFILE_SMALLER = 8;

    /**
     * Splitting has been canceled.
     */
    public static final int SPLITTING_CANCELLED = 9;

    /**
     * Internal error: there are more bytes to read than to write.
     */
    public static final int INTERNAL_ERROR1 = 10;

    /**
     * Internal error: there are less bytes to read than to write.
     */
    public static final int INTERNAL_ERROR2 = 11;

    /**
     * Internal error: {@link InterruptedException}.
     */
    public static final int INTERNAL_ERROR3 = 12;

    private int reason;

    private String filename;

    public ErrorSplittingEvent(int reason) {
        this.reason = reason;
        this.filename = null;
    }

    /**
     * Return the reason associated.
     */
    public int getError() {
        return reason;
    }

    @Override
    public String toString() {
        return reasonToString(reason);
    }

    /**
     * Return the {@link String} error associated to a reason.
     */
    public static String reasonToString(int reason) {
        String text = null;
        switch (reason) {
        case NOT_READABLE_FILE:
            text = _("Cannot read the file to split.");
            break;
        case NOT_EXISTING_FILE:
            text = _("The split file does not exist.");
            break;
        case FILE_IS_DIRECTORY:
            text = _("The file to split is a directory.");
            break;
        case FILE_NOT_FOUND:
            text = _("The file to split has not been found.");
            break;
        case FILE_EMPTY:
            text = _("The file to split is empty.");
            break;
        case IOEXCEPTION_READING:
            text = _("Error while reading the file to split.");
            break;
        case IOEXCEPTION_WRITTING:
            text = _("Error while wrtting a chunk file.");
            break;
        case SPLITFILE_SMALLER:
            text = _("The file to split is smaller than expected.");
            break;
        case SPLITTING_CANCELLED:
            text = _("Splitting has been cancelled.");
            break;
        case INTERNAL_ERROR1:
            text = _("Internal error: there are more bytes to read than to write.");
            break;
        case INTERNAL_ERROR2:
            text = _("Internal error: there are less bytes to read than to write.");
            break;
        case INTERNAL_ERROR3:
            text = _("Internal error: thread interrupted.");
            break;
        default:
            text = _("Unknown error.");
            break;
        }
        return text;
    }

    /**
     * Return the name of the file being splitted. It only applies if this
     * event is launched before an {@link StartSplittingEvent} is launched.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set the name of the file being splitted.
     */
    public void setFilename(String filename) {
        this.filename = new String(filename);
    }
}
