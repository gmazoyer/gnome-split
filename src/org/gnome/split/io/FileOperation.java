/*
 * FileOperation.java
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
package org.gnome.split.io;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.gnome.notify.Notification;
import org.gnome.split.GnomeSplit;
import org.gnome.split.dbus.DbusInhibit;
import org.gnome.split.io.event.ProgressChangedEvent;
import org.gnome.split.io.event.ProgressListener;
import org.gnome.split.io.event.StatusChangedEvent;
import org.gnome.split.io.event.StatusListener;
import org.gnome.split.utils.SizeUnit;

import static org.freedesktop.bindings.Internationalization._;

/**
 * A class used to define common variables and methods for all operations
 * which can be made on one or more files.
 * 
 * @author Guillaume Mazoyer
 */
public abstract class FileOperation extends Thread
{
    /**
     * The current GNOME Split instance.
     */
    protected GnomeSplit app;

    /**
     * Result file or file on which the operation will be performed.
     */
    protected File file;

    /**
     * Files on which the operation has to be performed.
     */
    protected List<File> waiting;

    /**
     * Files on which the operation has been performed.
     */
    protected List<File> terminated;

    /**
     * Total size to reach.
     */
    protected long size;

    /**
     * Number of bytes already read and write.
     */
    protected long done;

    /**
     * Progress of this action.
     */
    protected double progress;

    /**
     * Status of the current operation (running, paused, finished, error).
     */
    protected OperationStatus status;

    /**
     * Should the operation be stopped?
     */
    protected boolean stop;

    /**
     * Timestamp when the operation has been started.
     */
    protected long timestamp;

    /**
     * Inhibit object to play with computer hibernation through dbus.
     */
    protected DbusInhibit inhibit;

    /**
     * List of listeners to notify when a value changes.
     */
    private EventListenerList listeners;

    /**
     * Prevent listeners to notify change to many times.
     */
    private long lastnotify;

    protected FileOperation(final GnomeSplit app) {
        super();
        this.app = app;
        this.waiting = new ArrayList<File>();
        this.terminated = new ArrayList<File>();
        this.progress = 0.0;
        this.size = 0;
        this.done = 0;
        this.stop = false;
        this.status = OperationStatus.RUNNING;
        this.timestamp = System.currentTimeMillis();
        this.inhibit = new DbusInhibit();
        this.listeners = new EventListenerList();
        this.lastnotify = System.currentTimeMillis();
    }

    /**
     * Format a size into a string to make it readable for human being.
     * 
     * @param size
     *            the size format.
     * @param divider
     *            the divider.
     * @return a formatted string.
     */
    private String formatSize(long size, double divider) {
        final StringBuilder builder = new StringBuilder();
        final DecimalFormat format = new DecimalFormat();

        // Can have 0 figure after comma
        format.setMinimumFractionDigits(0);

        // Can have (a maximum of) 1 figure after comma
        format.setMaximumFractionDigits(1);

        if (divider == 0) {
            // Use bytes only
            builder.append(format.format(size));
            builder.append(" ");
            builder.append(_("bytes"));
        } else {
            double displayed;

            if (divider == SizeUnit.KB) {
                // Use kilobytes
                displayed = (double) size / SizeUnit.KB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("KB"));
            } else if (divider == SizeUnit.MB) {
                // Use megabytes
                displayed = (double) size / SizeUnit.MB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("MB"));
            } else {
                // Use gigabytes
                displayed = (double) size / SizeUnit.GB;
                builder.append(format.format(displayed));
                builder.append(" ");
                builder.append(_("GB"));
            }
        }

        // Finally
        return builder.toString();
    }

    /**
     * Return a string representation of the current and total sizes of an
     * operation.
     * 
     * @return a String.
     */
    private String formatProgress() {
        final StringBuilder builder = new StringBuilder();
        final double divider = this.getSizeDivider(size);

        builder.append(this.formatSize(done, divider));
        builder.append(" / ");
        builder.append(this.formatSize(size, divider));

        return builder.toString();
    }

    /**
     * Get the best divider for a size to make it human readable.
     * 
     * @param size
     *            the size to format
     * @return the divider.
     */
    protected double getSizeDivider(long size) {
        if (size < SizeUnit.KB)
            // Use bytes only
            return 0.0;
        else {
            if (size < SizeUnit.MB)
                // Use kilobytes
                return SizeUnit.KB;
            else if (size < SizeUnit.GB)
                // Use megabytes
                return SizeUnit.MB;
            else
                // Use gigabytes
                return SizeUnit.GB;
        }
    }

    /**
     * Notify all progress listeners that the progress of the operation has
     * changed.
     */
    protected void fireProgressChanged() {
        // Get all listeners
        final ProgressListener[] list = (ProgressListener[]) listeners.getListeners(ProgressListener.class);

        // Notify all listeners
        for (ProgressListener listener : list) {
            final ProgressChangedEvent event = new ProgressChangedEvent(this, progress);
            listener.progressChanged(event);
        }
    }

    /**
     * Notify all progress listeners that the progress of the operation has
     * changed.
     */
    protected void fireProgressChanged(double old) {
        final int older = (int) (old * 100);
        final int newer = (int) (progress * 100);

        if (older < newer)
            this.fireProgressChanged();
    }

    /**
     * Notify all status listeners that the status of the operation has
     * changed.
     */
    protected void fireStatusChanged(boolean force) {
        final long time = System.currentTimeMillis();

        // Allow update only each second
        if (force || ((time - lastnotify) >= 1000)) {
            // Get all listeners
            final StatusListener[] list = (StatusListener[]) listeners.getListeners(StatusListener.class);

            // Notify all listeners
            for (StatusListener listener : list) {
                final StatusChangedEvent event = new StatusChangedEvent(this, this.getStatusString());
                listener.statusChanged(event);
            }

            // Refresh last notification time
            lastnotify = time;
        }
    }

    @Override
    public abstract void run();

    /**
     * Get the list of the waiting files.
     * 
     * @return a files list.
     */
    public List<File> getWaitingFiles() {
        return waiting;
    }

    /**
     * Get the list of the terminated files.
     * 
     * @return a files list.
     */
    public List<File> getTerminatedFiles() {
        return terminated;
    }

    /**
     * Return the progress of the current operation.
     * 
     * @return the progress.
     */
    public double getProgress() {
        return progress;
    }

    /**
     * Return a string to know the current operation status.
     * 
     * @return status.
     */
    public String getStatusString() {
        final StringBuilder builder = new StringBuilder();

        // First, append the filename
        builder.append("<b>");
        builder.append(file.getName());
        builder.append("</b>\n");

        // Then, append the number of data read/write and the number of total
        // data
        builder.append(this.formatProgress());
        builder.append("\n");

        // Then, append the current action status
        switch (status) {
        case RUNNING: // Action is in progress
            if (this instanceof FileSplit)
                builder.append(_("Splitting..."));
            else
                builder.append(_("Assembling..."));
            break;
        case VERIFYING: // Action is being verified
            if (this instanceof FileSplit)
                builder.append(_("Calculating file hash..."));
            else
                builder.append(_("Verifying file integrity..."));
            break;
        case PAUSED: // Action is paused
            if (this instanceof FileSplit)
                builder.append(_("Split paused."));
            else
                builder.append(_("Assembly paused."));
            break;
        case FINISHED: // Action is finished
            if (this instanceof FileSplit)
                builder.append(_("Split finished."));
            else
                builder.append(_("Assembly finished."));
            break;
        case ERROR: // An error has occurred
            if (this instanceof FileSplit)
                builder.append(_("Error during the split."));
            else
                builder.append(_("Error during the assembly."));
            break;
        default:
            break;
        }

        // Finally, return the formatted string
        return builder.toString();
    }

    /**
     * Request the operation to be stopped and not paused.
     */
    public void requestStop() {
        stop = true;
    }

    /**
     * Get the time when the action has been started in milliseconds.
     * 
     * @return the time in ms.
     */
    public long getStartTime() {
        return timestamp;
    }

    /**
     * Add a progress listener to the operation.
     */
    public void addProgressListener(ProgressListener listener) {
        listeners.add(ProgressListener.class, listener);
    }

    /**
     * Add a status listener to the operation.
     */
    public void addStatusListener(StatusListener listener) {
        listeners.add(StatusListener.class, listener);
    }

    /**
     * Remove a progress listener to the operation.
     */
    public void removeProgressListener(ProgressListener listener) {
        listeners.remove(ProgressListener.class, listener);
    }

    /**
     * Remove a status listener to the operation.
     */
    public void removeStatusListener(StatusListener listener) {
        listeners.remove(StatusListener.class, listener);
    }

    /**
     * Set the status of the action to another one.
     * 
     * @param status
     *            the new status.
     */
    public void setStatus(OperationStatus status) {
        // Change the status
        this.status = status;

        // Force the view to update the displayed status
        this.fireStatusChanged(true);

        // Use notification to notify the user
        if (app.getConfig().USE_NOTIFICATION) {
            Notification notification = null;

            if (status == OperationStatus.FINISHED) {
                // Operation successfully done
                if (this instanceof FileSplit)
                    notification = new Notification(_("Split terminated."), _(
                            "The split of {0} has been terminated succesfully.", file.getName()),
                            "dialog-info", app.getMainWindow().getTrayIcon());
                else
                    notification = new Notification(_("Assembly terminated."), _(
                            "The assembly of {0} has been terminated succesfully.", file.getName()),
                            "dialog-info", app.getMainWindow().getTrayIcon());
            } else if (status == OperationStatus.ERROR) {
                // Operation with an error
                if (this instanceof FileSplit)
                    notification = new Notification(
                            _("Split error."),
                            _(
                                    "An error has occurred during the split of {0}. Try to split this file again.",
                                    file.getName()), "dialog-info", app.getMainWindow().getTrayIcon());
                else
                    notification = new Notification(
                            _("Assembly error."),
                            _(
                                    "An error has occurred during  the assembly of {0}. Try to assemble the files again.",
                                    file.getName()), "dialog-error", app.getMainWindow().getTrayIcon());
            }

            if (notification != null)
                notification.show();
        }
    }

    /**
     * Status in which an action can be.
     * 
     * @author Guillaume Mazoyer
     */
    public enum OperationStatus
    {
        RUNNING, VERIFYING, PAUSED, FINISHED, ERROR;
    }
}
