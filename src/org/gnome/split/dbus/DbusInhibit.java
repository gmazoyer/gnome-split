/*
 * DbusInhibit.java
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
package org.gnome.split.dbus;

import org.freedesktop.dbus.DBusConnection;
import org.freedesktop.dbus.UInt32;
import org.freedesktop.dbus.exceptions.DBusException;
import org.gnome.SessionManager;
import org.gnome.split.config.Constants;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Try to inhibit and uninhibit the computer hibernation using dbus and GNOME
 * Power Manager.
 * 
 * @author Guillaume Mazoyer
 */
public class DbusInhibit
{
    /**
     * Connection to dbus.
     */
    private DBusConnection connection;

    /**
     * Inhibit dbus object.
     */
    private SessionManager inhibit;

    /**
     * Inhibit cookie.
     */
    private UInt32 cookie;

    /**
     * To know if we have inhibit hibernation before.
     */
    private boolean hasInhibit;

    /**
     * Inhibit the computer hibernation.
     */
    public void inhibit() {
        try {
            // Get dbus connection
            connection = DBusConnection.getConnection(DBusConnection.SESSION);

            // Get inhibit object
            inhibit = connection.getRemoteObject("org.gnome.SessionManager",
                    "/org/gnome/SessionManager", SessionManager.class);

            // Inhibit hibernation and get inhibit cookie
            cookie = inhibit.Inhibit(Constants.PROGRAM_NAME, new UInt32(0), _("GNOME Split activity"),
                    new UInt32(1 + 2 + 4 + 8));
            hasInhibit = true;
        } catch (DBusException e) {
            e.printStackTrace();
        }
    }

    /**
     * Uninhibit the computer hibernation.
     */
    public void unInhibit() {
        // Uninhibit hibernation
        inhibit.Uninhibit(cookie);
        hasInhibit = false;

        // Close dbus connection
        connection.disconnect();
    }

    /**
     * Used to know if we has inhibit before.
     */
    public boolean hasInhibit() {
        return hasInhibit;
    }
}
