/*
 * Inhibit.java
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
package org.freedesktop.PowerManagement;

import java.util.List;

import org.freedesktop.dbus.DBusInterface;
import org.freedesktop.dbus.DBusSignal;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;

/**
 * Interface to access to the GNOME power manager.
 * 
 * @author Guillaume Mazoyer
 */
public interface Inhibit extends DBusInterface
{
    /**
     * A DBus signal representation to know if the inhibit state has changed.
     */
    public static class HasInhibitChanged extends DBusSignal
    {
        public final boolean hasInhibit;

        public HasInhibitChanged(String path, boolean hasInhibit) throws DBusException {
            super(path, hasInhibit);
            this.hasInhibit = hasInhibit;
        }
    }

    /**
     * Method used to get all active requests.
     * 
     * @return a list which contains all requests.
     */
    public List<String> GetRequests();

    /**
     * Method used to know if the computer has inhibit its hibernation.
     * 
     * @return true if it has inhibit.
     */
    public boolean HasInhibit();

    /**
     * Method used to cancel a previous computer hibernation inhibition.
     * 
     * @param cookie
     *            the cookie which identify the inhibition to cancel.
     */
    public void UnInhibit(UInt32 cookie);

    /**
     * Method used to inhibit computer hibernation.
     * 
     * @param application
     *            the application name which requires the inhibition.
     * @param reason
     *            the reason to require the inhibition.
     * @return a cookie to identify the inhibition to cancel it later.
     */
    public UInt32 Inhibit(String application, String reason);
}
