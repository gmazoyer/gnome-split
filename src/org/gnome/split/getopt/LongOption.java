/*
 * LongOption.java
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
package org.gnome.split.getopt;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Class that defines a long option.
 * 
 * @author Guillaume Mazoyer
 */
public class LongOption
{
    /**
     * Constant value used to indicate that the option takes no argument.
     */
    public static final int NO_ARGUMENT = 0;

    /**
     * Constant value used to indicate that the options an argument that is
     * required.
     */
    public static final int REQUIRED_ARGUMENT = 1;

    /**
     * Constant value used to indicate that the options an argument that is
     * optional.
     */
    public static final int OPTIONAL_ARGUMENT = 2;

    /**
     * Name of the long option.
     */
    private String name;

    /**
     * Type of argument that takes the long option.
     */
    private int arg;

    /**
     * Value of the long option.
     */
    private int value;

    /**
     * Create a long option using a <var>name</var>, a type of
     * <var>arg<var>ument and a <var>value</var> which is equal to the short
     * option.
     */
    public LongOption(String name, int arg, int value) throws IllegalArgumentException {
        // Invalid argument type
        if ((arg != NO_ARGUMENT) && (arg != REQUIRED_ARGUMENT) && (arg != OPTIONAL_ARGUMENT)) {
            throw new IllegalArgumentException(_("Invalid value {0} for paramater 'arg'.", arg));
        }

        // Save values
        this.name = name;
        this.arg = arg;
        this.value = value;
    }

    /**
     * Get the name of the long option.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the type of argument of the long option.
     */
    public int getArgType() {
        return arg;
    }

    /**
     * Get the value of the long option.
     */
    public int getValue() {
        return value;
    }
}
