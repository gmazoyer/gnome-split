/*
 * MetaFilenameFilter.java
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
package org.gnome.split.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * This class is an implementation of FilenameFilter which use meta-characters
 * similar as the ones of a basic shell.
 * <ul>
 * <li>'*' represents a string (regexp '.*').</li>
 * <li>'?' represents a character (regexp '.').</li>
 * </ul>
 * The other characters will not be interpreted.
 * 
 * @author Guillaume Mazoyer
 */
public class MetaFilenameFilter implements FilenameFilter
{
    private final Pattern pattern;

    /**
     * This constructor builds a filename filter using a mask.
     * 
     * @param mask
     *            a string which represents the filename mask.
     */
    public MetaFilenameFilter(String mask) {
        // Add \Q and \E around the mask substrings which are not
        // meta-characters
        String regexpPattern = mask.replaceAll("[^\\*\\?]+", "\\\\Q$0\\\\E");
        // Replace every '*' to be able to interpret them
        regexpPattern = regexpPattern.replaceAll("\\*", ".*");
        // Replace every '?' to be able to interpret them
        regexpPattern = regexpPattern.replaceAll("\\?", ".");
        // Create the pattern
        pattern = Pattern.compile(regexpPattern);
    }

    @Override
    public boolean accept(File dir, String name) {
        return pattern.matcher(name).matches();
    }
}
