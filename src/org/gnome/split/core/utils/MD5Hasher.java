/*
 * MD5Hasher.java
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
package org.gnome.split.core.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class which can get the MD5 sum of a {@link File}.
 * 
 * @author Guillaume Mazoyer
 */
public class MD5Hasher
{
    /**
     * Used algorithm to calculate the hash.
     */
    private MessageDigest algorithm;

    public MD5Hasher() {
        try {
            algorithm = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // Should not happened
        }
    }

    /**
     * Hashes the specified input stream.
     */
    private byte[] hash(InputStream input) {
        BufferedInputStream buffer = new BufferedInputStream(input);
        byte[] data = new byte[65536];

        // Reset to clean other calculation
        algorithm.reset();

        int read = 0;
        try {
            // Read data from the stream
            while ((read = buffer.read(data)) > 0) {
                algorithm.update(data, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close stream
                buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Finalize calculation
        return algorithm.digest();
    }

    /**
     * Hash the specified file to a bytes array.
     */
    private byte[] hash(File file) {
        FileInputStream input = null;
        byte[] hash = null;

        try {
            // Open file and calculate the hash
            input = new FileInputStream(file);
            hash = this.hash(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the file
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return hash;
    }

    /**
     * Return the specified bytes array as an hexadecimal {@link String}.
     */
    private String buildHexaString(byte[] hash) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < hash.length; i++) {
            int current = hash[i] & 0xFF;
            if (current < 16) {
                builder.append("0");
            }

            builder.append(Integer.toString(current, 16).toUpperCase());
        }

        return builder.toString();
    }

    /**
     * Hash the specified file to a {@link String}.
     */
    public String hashToString(File file) {
        return this.buildHexaString(this.hash(file));
    }
}
