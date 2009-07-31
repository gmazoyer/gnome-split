/*
 * FileHash.java
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.gnome.split.GnomeSplit;

/**
 * This class is used to get an hash from a file.
 * 
 * @author Guillaume Mazoyer
 */
public class FileHash
{
    /**
     * The current GNOME Split instance.
     */
    private GnomeSplit app;

    /**
     * SHA-1 algorithm.
     */
    public static final String SHA1 = "SHA-1";

    /**
     * SHA-256 algorithm.
     */
    public static final String SHA256 = "SHA-256";

    /**
     * SHA-384 algorithm.
     */
    public static final String SHA384 = "SHA-384";

    /**
     * SHA-512 algorithm.
     */
    public static final String SHA512 = "SHA-512";

    /**
     * MD2 algorithm.
     */
    public static final String MD2 = "MD2";

    /**
     * MD5 algorithm.
     */
    public static final String MD5 = "MD5";

    /**
     * Used algorithm to calculate the hash.
     */
    private MessageDigest algorithm;

    /**
     * Create a Hasher that will uses the specified algorithm.
     * 
     * @param algorithm
     *            the algorithm to use.
     */
    public FileHash(final GnomeSplit app, final String algorithm) {
        try {
            this.app = app;
            this.algorithm = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Hashes the specified input stream.
     * 
     * @param input
     *            the input stream to hash.
     * @return the hash as a bytes array.
     */
    private byte[] hash(InputStream input) {
        final BufferedInputStream buffer = new BufferedInputStream(input);
        final byte[] data = new byte[app.getConfig().BUFFER_SIZE];

        // Reset to clean other calculation
        algorithm.reset();

        int read = 0;
        try {
            // Read data from the stream
            while ((read = buffer.read(data)) > 0)
                algorithm.update(data, 0, read);
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
     * Hashes the specified file to a bytes array.
     * 
     * @param file
     *            the file to hash.
     * @return the hash as a bytes array.
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
                if (input != null)
                    input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return hash;
    }

    /**
     * Returns the specified bytes array as an hexadecimal String.
     * 
     * @param hash
     *            the data as a bytes array.
     * @return the hash as an hexadecimal String.
     */
    private String buildHexaString(byte[] hash) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < hash.length; i++) {
            int current = hash[i] & 0xFF;

            if (current < 16)
                builder.append("0");

            builder.append(Integer.toString(current, 16).toUpperCase());
        }

        return builder.toString();
    }

    /**
     * Hashes the specified file to a String.
     * 
     * @param file
     *            the file to hash.
     * @return the hash as a String.
     */
    public String hashToString(File file) {
        // Start progress informations
        // this.emitStatusText(_("Calculating file hash..."));
        // this.emitIndeterminate(true, _("Calculation of {0} hash.",
        // file.getName()));

        // Calculate hash
        final String hash = this.buildHexaString(this.hash(file));

        // Stop update progress informations
        // this.emitIndeterminate(false, "");
        // this.emitCloseProgress();
        // this.emitStatusText(_("Ready."));

        return hash;
    }
}
