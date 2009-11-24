/*
 * Xtremsplit.java
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
package org.gnome.split.core.merger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.gnome.split.core.utils.ByteUtils;

public class Xtremsplit extends DefaultMergeEngine
{
    public Xtremsplit(File file) {
        super(file);
    }

    protected void loadHeaders() throws IOException, FileNotFoundException {
        RandomAccessFile access = null;
        try {
            // Open the first part to merge
            access = new RandomAccessFile(file, "r");

            // Skip useless header
            access.skipBytes(40);

            // Read filename
            byte[] bytes = new byte[access.read()];
            access.read(bytes);
            filename = file.getAbsolutePath().replace(file.getName(), "") + new String(bytes);
            access.skipBytes(50 - bytes.length);

            // Read if MD5 is used
            md5 = ByteUtils.toBoolean(new byte[] {
                (byte) access.read()
            });

            // Read file number
            bytes = new byte[4];
            access.read(bytes);
            parts = (int) ByteUtils.toInt(bytes);

            // Read file length
            bytes = new byte[8];
            access.read(bytes);
            fileLength = (int) ByteUtils.toLong(bytes);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                access.close();
            } catch (IOException e) {
                // Drop the exception
            }
        }
    }

    @Override
    public void merge() throws IOException, FileNotFoundException {
        String part = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 7);
        FileOutputStream out = null;
        try {
            // Open the final file
            out = new FileOutputStream(filename);
            this.fireEnginePartEnded(1);

            for (int i = 1; i <= parts; i++) {
                // Get the current extension
                String current;
                if (i >= 100) {
                    current = String.valueOf(i);
                } else if (i >= 10) {
                    current = "0" + i;
                } else {
                    current = "00" + i;
                }

                // Open the current part to merge
                RandomAccessFile access = new RandomAccessFile(part + current + ".xtm", "r");
                long read = 0;
                long length = access.length();

                if (md5 && (i == parts)) {
                    // Skip the MD5 sum if it is the last part
                    length -= 32;
                }

                if (i == 1) {
                    // Skip headers if it is the first part
                    access.skipBytes(104);
                    this.fireEngineDone(104);
                    read += 104;
                }

                // Merge the file
                while (read < length) {
                    if (paused) {
                        try {
                            // Pause the current thread
                            mutex.wait();
                        } catch (InterruptedException e) {
                            // Drop the exception
                        }
                    }

                    // Define the buffer size
                    byte[] buffer;
                    if (BUFFER > (length - read)) {
                        buffer = new byte[(int) (length - read)];
                    } else {
                        buffer = new byte[BUFFER];
                    }

                    // Read and write data
                    access.read(buffer);
                    out.write(buffer);

                    // Update read and write status
                    read += buffer.length;
                    this.fireEngineDone(buffer.length);
                    Thread.yield();
                }

                // Close the part
                access.close();

                // Notify the end of the part
                this.fireEnginePartEnded(((i + 1) > parts ? -1 : i));
            }

            // Notify the end of the merge
            this.fireEngineEnded();
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                // Close the final file
                out.close();
            } catch (IOException e) {
                // Drop the exception
            }
        }
    }
}
