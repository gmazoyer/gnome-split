/*
 * GetOptions.java
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
 * Class used to parse command line arguments passed to the program.
 * 
 * @author Roland McGrath
 * @author Ulrich Drepper
 * @author Aaron M. Renn
 * @author Guillaume Mazoyer
 */
public class GetOptions
{
    /**
     * REQUIRE_ORDER means don't recognize them as options; stop option
     * processing when the first non-option is seen. This is what Unix does.
     * This mode of operation is selected by either setting the property
     * gnu.posixly_correct, or using `+' as the first character of the list of
     * option characters.
     */
    protected static final int REQUIRE_ORDER = 1;

    /**
     * PERMUTE is the default. We permute the contents of ARGV as we scan, so
     * that eventually all the non-options are at the end. This allows options
     * to be given in any order, even with programs that were not written to
     * expect this.
     */
    protected static final int PERMUTE = 2;

    /**
     * RETURN_IN_ORDER is an option available to programs that were written to
     * expect options and other ARGV-elements in any order and that care about
     * the ordering of the two. We describe each non-option ARGV-element as if
     * it were the argument of an option with character code 1. Using `-' as
     * the first character of the list of option characters selects this mode
     * of operation.
     */
    protected static final int RETURN_IN_ORDER = 3;

    /**
     * Argument when an option that takes an argument is found.
     */
    protected String argument;

    /**
     * Index of the next element to be scanned.
     */
    protected int optionIndex = 0;

    /**
     * Store the value of the invalid option.
     */
    protected int invalid = '?';

    /**
     * The next char to be scanned.
     */
    protected String nextchar;

    /**
     * This is the string describing the valid short options.
     */
    protected String optionString;

    /**
     * This is an array of long options which describe the valid long options.
     */
    protected LongOption[] longOptions;

    /**
     * The index into the long options array of the long option found.
     */
    protected int longOptionIndex;

    /**
     * The flag determines whether or not we operate in strict POSIX
     * compliance.
     */
    protected boolean posixCorrect;

    /**
     * A flag which communicates whether or not checkLongOption() did all
     * necessary processing for the current option
     */
    protected boolean longOptionHandled;

    /**
     * The index of the first non-option.
     */
    protected int firstNonOption = 1;

    /**
     * The index of the last non-option.
     */
    protected int lastNonOption = 1;

    /**
     * Flag to tell getopt to immediately return -1 the next time it is
     * called.
     */
    private boolean end = false;

    /**
     * Saved argument list passed to the program.
     */
    protected String[] args;

    /**
     * Determines whether we permute arguments or not.
     */
    protected int ordering;

    /**
     * Name to print as the program name in error messages. This is necessary
     * since Java does not place the program name in args[0].
     */
    protected String program;

    /**
     * Construct a GetOptions instance with given input data that is capable
     * of parsing long options as well as short.
     */
    public GetOptions(String program, String[] args, String options, LongOption[] longOptions) {
        if (options.length() == 0)
            options = " ";

        // This function is essentially _getopt_initialize from GNU getopt
        this.program = program;
        this.args = args;
        this.optionString = options;
        this.longOptions = longOptions;

        // Check for property "gnu.posixly_correct" to determine whether to
        // strictly follow the POSIX standard.
        if (System.getProperty("gnu.posixly_correct", null) == null) {
            posixCorrect = false;
        } else {
            posixCorrect = true;
        }

        // Determine how to handle the ordering of options and non-options
        if (options.charAt(0) == '-') {
            ordering = RETURN_IN_ORDER;

            if (options.length() > 1) {
                this.optionString = options.substring(1);
            }
        } else if (options.charAt(0) == '+') {
            ordering = REQUIRE_ORDER;

            if (options.length() > 1) {
                this.optionString = options.substring(1);
            }
        } else if (posixCorrect) {
            ordering = REQUIRE_ORDER;
        } else {
            // The normal default case
            ordering = PERMUTE;
        }
    }

    /**
     * Construct a basic GetOptions instance with the given input data. Note
     * that this handles &quot;short&quot; options only.
     */
    public GetOptions(String program, String[] args, String options) {
        this(program, args, options, null);
    }

    /**
     * Get the index of the scanned option.
     */
    public int getOptionIndex() {
        return optionIndex;
    }

    /**
     * Get the argument of the scanned option that takes an argument.
     */
    public String getArgument() {
        return argument;
    }

    /**
     * Get the value of the invalid option.
     */
    public int getInvalid() {
        return invalid;
    }

    /**
     * Get the index into the array of long options representing the long
     * option that was found.
     */
    public int getLongOptionIndex() {
        return longOptionIndex;
    }

    /**
     * Exchange the shorter segment with the far end of the longer segment.
     * That puts the shorter segment into the right place. It leaves the
     * longer segment in the right place overall, but it consists of two parts
     * that need to be swapped next. This method is used for argument
     * permutation.
     */
    private void exchange(String[] argv) {
        int bottom = firstNonOption;
        int middle = lastNonOption;
        int top = optionIndex;
        String tem;

        while (top > middle && middle > bottom) {
            if (top - middle > middle - bottom) {
                // Bottom segment is the short one.
                int len = middle - bottom;
                int i;

                // Swap it with the top part of the top segment.
                for (i = 0; i < len; i++) {
                    tem = argv[bottom + i];
                    argv[bottom + i] = argv[top - (middle - bottom) + i];
                    argv[top - (middle - bottom) + i] = tem;
                }
                // Exclude the moved bottom segment from further swapping.
                top -= len;
            } else {
                // Top segment is the short one.
                int len = top - middle;
                int i;

                // Swap it with the bottom part of the bottom segment.
                for (i = 0; i < len; i++) {
                    tem = argv[bottom + i];
                    argv[bottom + i] = argv[middle + i];
                    argv[middle + i] = tem;
                }
                // Exclude the moved top segment from further swapping.
                bottom += len;
            }
        }

        // Update records for the slots the non-options now occupy.
        firstNonOption += (optionIndex - lastNonOption);
        lastNonOption = optionIndex;
    }

    /**
     * Check to see if an option is a valid long option. Put in a separate
     * method because this needs to be done twice.
     */
    private int checkLongOption() {
        LongOption pfound = null;
        int nameend;
        boolean ambig;
        boolean exact;

        longOptionHandled = true;
        ambig = false;
        exact = false;
        longOptionIndex = -1;

        nameend = nextchar.indexOf("=");
        if (nameend == -1) {
            nameend = nextchar.length();
        }

        // Test all lnog options for either exact match or abbreviated matches
        for (int i = 0; i < longOptions.length; i++) {
            if (longOptions[i].getName().startsWith(nextchar.substring(0, nameend))) {
                if (longOptions[i].getName().equals(nextchar.substring(0, nameend))) {
                    // Exact match found
                    pfound = longOptions[i];
                    longOptionIndex = i;
                    exact = true;
                    break;
                } else if (pfound == null) {
                    // First nonexact match found
                    pfound = longOptions[i];
                    longOptionIndex = i;
                } else {
                    // Second or later nonexact match found
                    ambig = true;
                }
            }
        }

        // Print out an error if the option specified was ambiguous
        if (ambig && !exact) {
            System.err.println(_("{0}: option {1} is ambiguous", program, args[optionIndex]));

            nextchar = "";
            invalid = 0;
            optionIndex++;

            return '?';
        }

        if (pfound != null) {
            optionIndex++;

            if (nameend != nextchar.length()) {
                if (pfound.getArgType() != LongOption.NO_ARGUMENT) {
                    if (nextchar.substring(nameend).length() > 1) {
                        argument = nextchar.substring(nameend + 1);
                    } else {
                        argument = "";
                    }
                } else {
                    if (args[optionIndex - 1].startsWith("--")) {
                        // -- option
                        System.err.println(_("{0}: option --{1} doesn't allow an argument", program,
                                pfound.getName()));
                    } else {
                        // +option or -option
                        System.err.println(_("{0}: option {1}{2} doesn't allow an argument", program,
                                args[optionIndex - 1].charAt(0), pfound.getName()));
                    }

                    nextchar = "";
                    invalid = pfound.getValue();

                    return '?';
                }
            } else if (pfound.getArgType() == LongOption.REQUIRED_ARGUMENT) {
                if (optionIndex < args.length) {
                    argument = args[optionIndex];
                    ++optionIndex;
                } else {
                    System.err.println(_("{0}: option {1} requires an argument", program,
                            args[optionIndex - 1]));

                    nextchar = "";
                    invalid = pfound.getValue();
                    if (optionString.charAt(0) == ':')
                        return ':';
                    else
                        return '?';
                }
            }

            nextchar = "";
            return pfound.getValue();
        }

        longOptionHandled = false;
        return 0;
    }

    /**
     * This method returns a char that is the current option that has been
     * parsed from the command line. If the option takes an argument, then the
     * internal variable <var>argument</var> is set which is a String
     * representing the the value of the argument. This value can be retrieved
     * by the caller using the {@link #getArgument()} method. If an invalid
     * option is found, an error message is printed and a '?' is returned. The
     * name of the invalid option character can be retrieved by calling the
     * {@link #getInvalid()} method. When there are no more options to be
     * scanned, this method returns -1. The index of first non-option element
     * in <var>args</var> can be retrieved with the {@link #getOptionIndex()}
     * method.
     */
    public int getOption() {
        argument = null;

        if (end == true) {
            return -1;
        }

        if ((nextchar == null) || (nextchar.equals(""))) {
            // If we have just processed some options following some
            // non-options,
            // exchange them so that the options come first.
            if (lastNonOption > optionIndex) {
                lastNonOption = optionIndex;
            }

            if (firstNonOption > optionIndex) {
                firstNonOption = optionIndex;
            }

            if (ordering == PERMUTE) {
                // If we have just processed some options following some
                // non-options,
                // exchange them so that the options come first.
                if ((firstNonOption != lastNonOption) && (lastNonOption != optionIndex)) {
                    exchange(args);
                } else if (lastNonOption != optionIndex) {
                    firstNonOption = optionIndex;
                }

                // Skip any additional non-options
                // and extend the range of non-options previously skipped.
                while ((optionIndex < args.length)
                        && (args[optionIndex].equals("") || (args[optionIndex].charAt(0) != '-') || args[optionIndex].equals("-"))) {
                    optionIndex++;
                }

                lastNonOption = optionIndex;
            }

            // The special ARGV-element `--' means premature end of options.
            // Skip it like a null option,
            // then exchange with previous non-options as if it were an
            // option,
            // then skip everything else like a non-option.
            if ((optionIndex != args.length) && args[optionIndex].equals("--")) {
                optionIndex++;

                if ((firstNonOption != lastNonOption) && (lastNonOption != optionIndex)) {
                    exchange(args);
                } else if (firstNonOption == lastNonOption) {
                    firstNonOption = optionIndex;
                }

                lastNonOption = args.length;
                optionIndex = args.length;
            }

            // If we have done all the ARGV-elements, stop the scan
            // and back over any non-options that we skipped and permuted.
            if (optionIndex == args.length) {
                // Set the next-arg-index to point at the non-options
                // that we previously skipped, so the caller will digest them.
                if (firstNonOption != lastNonOption) {
                    optionIndex = firstNonOption;
                }

                return -1;
            }

            // If we have come to a non-option and did not permute it,
            // either stop the scan or describe it to the caller and pass it
            // by.
            if (args[optionIndex].equals("") || (args[optionIndex].charAt(0) != '-')
                    || args[optionIndex].equals("-")) {
                if (ordering == REQUIRE_ORDER) {
                    return -1;
                }

                argument = args[optionIndex++];
                return 1;
            }

            // We have found another option-ARGV-element.
            // Skip the initial punctuation.
            if (args[optionIndex].startsWith("--")) {
                nextchar = args[optionIndex].substring(2);
            } else {
                nextchar = args[optionIndex].substring(1);
            }
        }

        // Check whether the args element is a long option.
        if ((longOptions != null)
                && (args[optionIndex].startsWith("--") || (optionString.indexOf(args[optionIndex].charAt(1)) == -1))) {
            int c = checkLongOption();

            if (longOptionHandled) {
                return c;
            }

            // Can't find it as a long option.
            if (args[optionIndex].startsWith("--") || (optionString.indexOf(nextchar.charAt(0)) == -1)) {
                if (args[optionIndex].startsWith("--")) {
                    System.err.println(_("{0}: unrecognized option --{1}", program, nextchar));
                } else {
                    System.err.println(_("{0}: unrecognized option {1}{2}", program,
                            args[optionIndex].charAt(0), nextchar));
                }

                nextchar = "";
                optionIndex++;
                invalid = 0;

                return '?';
            }
        }

        // Look at and handle the next short option-character
        int c = nextchar.charAt(0);
        if (nextchar.length() > 1) {
            nextchar = nextchar.substring(1);
        } else {
            nextchar = "";
        }

        String temp = null;
        if (optionString.indexOf(c) != -1) {
            temp = optionString.substring(optionString.indexOf(c));
        }

        if (nextchar.equals("")) {
            optionIndex++;
        }

        if ((temp == null) || (c == ':')) {
            if (posixCorrect) {
                System.err.println(_("{0}: illegal option --{1}", program, (char) c));
            } else {
                System.err.println(_("{0}: invalid option --{1}", program, (char) c));
            }

            invalid = c;

            return '?';
        }

        // Convenience. Treat POSIX -W foo same as long option --foo
        if ((temp.charAt(0) == 'W') && (temp.length() > 1) && (temp.charAt(1) == ';')) {
            if (!nextchar.equals("")) {
                argument = nextchar;
            }
            // No further cars in this args element and no more args elements
            else if (optionIndex == args.length) {
                System.err.println(_("{0}: option requires an argument -- {1}", program, (char) c));

                invalid = c;
                if (optionString.charAt(0) == ':') {
                    return ':';
                } else {
                    return '?';
                }
            } else {
                // We already incremented `optionIndex' once; increment it
                // again.
                nextchar = args[optionIndex];
                argument = args[optionIndex];
            }

            c = checkLongOption();

            if (longOptionHandled) {
                return c;
            } else {
                nextchar = null;
                optionIndex++;
                return 'W';
            }
        }

        if ((temp.length() > 1) && (temp.charAt(1) == ':')) {
            // This is an option that accepts and argument optionally
            if ((temp.length() > 2) && (temp.charAt(2) == ':')) {
                if (!nextchar.equals("")) {
                    argument = nextchar;
                    optionIndex++;
                } else {
                    argument = null;
                }

                nextchar = null;
            } else {
                if (!nextchar.equals("")) {
                    argument = nextchar;
                    optionIndex++;
                } else if (optionIndex == args.length) {
                    System.err.println(_("{0}: option requires an argument --{1}", program, (char) c));

                    invalid = c;

                    if (optionString.charAt(0) == ':') {
                        return ':';
                    } else {
                        return '?';
                    }
                } else {
                    argument = args[optionIndex];
                    optionIndex++;

                    if ((posixCorrect) && argument.equals("--")) {
                        // If end of argv, error out
                        if (optionIndex == args.length) {
                            System.err.println(_("{0}: option requires an argument --{1}", program,
                                    (char) c));

                            invalid = c;

                            if (optionString.charAt(0) == ':') {
                                return ':';
                            } else {
                                return '?';
                            }
                        }

                        // Set new optarg and set to end
                        // Don't permute as we do on -- up above since we know
                        // we aren't in permute mode because of Posix.
                        argument = args[optionIndex];
                        optionIndex++;
                        firstNonOption = optionIndex;
                        lastNonOption = args.length;
                        end = true;
                    }
                }

                nextchar = null;
            }
        }

        return c;
    }
}
