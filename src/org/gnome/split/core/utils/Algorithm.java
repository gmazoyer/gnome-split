package org.gnome.split.core.utils;

public final class Algorithm
{
    /**
     * Our own algorithm.
     */
    public static final int GNOME_SPLIT = 0;

    /**
     * Algorithm used by Xtremsplit.
     */
    public static final int XTREMSPLIT = 1;

    /**
     * Get a {@link String} representation of all algorithms.
     */
    public static String[] toStrings() {
        return new String[] {
                "GNOME Split", "Xtremsplit"
        };
    }
}
