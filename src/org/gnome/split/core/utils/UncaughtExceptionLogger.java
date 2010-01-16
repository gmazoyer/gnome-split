/*
 * UncaughtExceptionLogger.java
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
package org.gnome.split.core.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class will register itself as the default uncaught exception handler.
 * 
 * @author Guillaume Mazoyer
 */
public class UncaughtExceptionLogger implements Thread.UncaughtExceptionHandler
{
    /**
     * Register this class as the default uncaught exception handler.
     */
    public UncaughtExceptionLogger() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * This method is used to write the stacktrace exception in a
     * {@link String}.
     */
    private String getStackTrace(Throwable exception) {
        StringWriter swriter = new StringWriter();
        PrintWriter pwriter = new PrintWriter(swriter, true);

        // Get the stacktrace
        exception.printStackTrace(pwriter);
        pwriter.flush();
        swriter.flush();

        return swriter.toString();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        StringBuilder report = new StringBuilder();

        report.append("UncaughtException in thread: " + thread.getName() + "\n");
        report.append("  Thread ID      = " + thread.getId() + "\n");
        report.append("  Thread prority = " + thread.getPriority() + "\n");
        report.append("Exception stacktrace:\n");
        report.append(this.getStackTrace(exception));

        System.err.println(report.toString());
    }
}
