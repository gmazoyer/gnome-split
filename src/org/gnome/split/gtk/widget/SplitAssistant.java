/*
 * SplitAssistant.java
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
package org.gnome.split.gtk.widget;

import java.io.File;

import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Assistant;
import org.gnome.gtk.AssistantPageType;
import org.gnome.gtk.Button;
import org.gnome.gtk.CheckButton;
import org.gnome.gtk.ComboBox;
import org.gnome.gtk.Editable;
import org.gnome.gtk.Entry;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Justification;
import org.gnome.gtk.Label;
import org.gnome.gtk.SpinButton;
import org.gnome.gtk.Stock;
import org.gnome.gtk.TextComboBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.WindowPosition;
import org.gnome.gtk.Assistant.Apply;
import org.gnome.gtk.Assistant.Cancel;
import org.gnome.gtk.Assistant.Close;
import org.gnome.gtk.Assistant.Prepare;
import org.gnome.split.GnomeSplit;
import org.gnome.split.core.utils.Algorithm;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.gtk.action.ActionManager.ActionId;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This assistant is used to help the user to create a split.
 * 
 * @author Guillaume Mazoyer
 */
public class SplitAssistant extends Assistant implements ActionAssistant, Prepare, Close, Cancel, Apply
{
    /**
     * The current instance of GNOME Split.
     */
    private GnomeSplit app;

    /**
     * The logo of this assistant.
     */
    private Pixbuf logo;

    /**
     * The label to update to show the summary.
     */
    private Label label;

    /**
     * The name of the file to split.
     */
    private String filename;

    /**
     * The size of the file without the multiplication by the unit.
     */
    private double size;

    /**
     * The unit of the size.
     */
    private int unit;

    /**
     * The ID of the algorithm to use.
     */
    private int algorithm;

    /**
     * To know if we should start the action.
     */
    private boolean start;

    public SplitAssistant(final GnomeSplit app) {
        super();

        // Save the instance
        this.app = app;

        // Set this assistant in the center of the screen
        this.setPosition(WindowPosition.CENTER);

        // Set the title
        this.setTitle(_("Split assistant"));

        // Setup the label to update
        this.label = new Label();

        // Setup the default values
        this.filename = null;
        this.size = 1;
        this.unit = 0;
        this.algorithm = 0;
        this.start = false;

        // Create a logo for the assistant
        logo = Gtk.renderIcon(this, Stock.CUT, IconSize.DIALOG);

        // Setup the introduction
        this.createIntroduction();

        // Setup the file selection
        this.createFileSelection();

        // Setup the size selection
        this.createSizeSelection();

        // Setup the algo selection
        this.createAlgoSelection();

        // Setup the confirmation
        this.createSummary();

        // Connect signal handlers
        this.connect((Prepare) this);
        this.connect((Close) this);
        this.connect((Cancel) this);
        this.connect((Apply) this);
    }

    /**
     * Calculate the maximum size that will be used to create a chunk.
     */
    private long calculateSize(double value, int unit) {
        long input = new File(filename).length();
        long result;

        if (unit == 0) {
            result = (long) (input / value);
        } else {
            // Split by size
            unit -= 2;
            double multiplicator = (unit == -1) ? 1 : SizeUnit.values()[unit];
            result = (long) (value * multiplicator);
        }

        // If size is not valid (bigger than the length of the file to split),
        // just return -1, else return the result
        return ((result >= input) ? -1 : result);
    }

    /**
     * Create a page to select the file to split.
     */
    private void createFileSelection() {
        final Page page = new Page();

        // The text to display
        final String data = _("Here, you have to select the file to split. After that, you can go to the\nnext step of this assistant. The file that you will choose should be displayed\nusing its full path in the text entry.");

        // Create the label
        final Label text = new Label();
        text.setLabel(data);
        text.setUseMarkup(true);
        text.setJustify(Justification.LEFT);

        // Add the label
        page.container.packStart(text, false, false, 0);

        // Create a box to pack widgets to select a file
        final HBox box = new HBox(false, 3);
        page.container.packStart(box, false, false, 0);

        // Add a label to it
        final Label label = new Label(_("File to split:"));
        box.packStart(label, false, false, 0);

        // Add an entry to it
        final Entry entry = new Entry();
        box.packStart(entry);

        // Add a chooser button to it
        final FileChooserButton button = new FileChooserButton(_("Select a file."),
                FileChooserAction.OPEN);
        button.setCurrentFolder(System.getProperty("user.home"));
        box.packStart(button, false, false, 0);

        // Add a last label to see if the file actually exists
        final Label exist = new Label();
        exist.setUseMarkup(true);
        page.container.packStart(exist, false, false, 0);

        // Connect entry handler to change the filename
        entry.connect(new Entry.Changed() {
            @Override
            public void onChanged(Editable source) {
                // Update the filename
                filename = entry.getText();

                // Tell if we can go to the next step
                boolean complete = true;

                // Text to display
                String text = "";

                // No filename
                if (filename.isEmpty()) {
                    complete = false;
                    text = "";
                } else {
                    File file = new File(filename);

                    // The file exists
                    if (file.exists()) {
                        // The file is actually a directory
                        if (file.isDirectory()) {
                            complete = false;
                            text = _("This file is a directory!");
                        } else {
                            complete = true;
                            text = "";
                        }
                    } else {
                        // The file does not exist
                        complete = false;
                        text = _("This file does not exist!");
                    }
                }

                // Set the state of the page
                setPageComplete(page, complete);

                // Update label
                exist.setLabel("<b><span foreground=\"red\">" + text + "</span></b>");
            }
        });

        // Connect chooser handler to change the filename
        button.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                String file = source.getFilename();

                // Update the entry
                entry.setText(file);

                // Update the filename
                filename = file;

                // Set the state of the page
                setPageComplete(page, true);
            }
        });

        // Setup the page in the assistant
        this.appendPage(page);
        this.setPageType(page, AssistantPageType.CONTENT);
        this.setPageTitle(page, _("File selection"));
        this.setPageHeaderImage(page, logo);
        this.setPageComplete(page, false);
    }

    /**
     * Create a page to select the size.
     */
    private void createSizeSelection() {
        final Page page = new Page();

        // The text to display
        final String data = _("Now, you have to select the maximal size for each chunks. You can choose this\nsize by using an already defined size (like CD-ROM or DVD-ROM), you can also\nselect the size by using a unit (B, KB, MB, GB), and finally, you can let GNOME\nSplit calculate the size just by giving the number of chunks to create.");

        // Create the label
        final Label text = new Label();
        text.setLabel(data);
        text.setUseMarkup(true);
        text.setJustify(Justification.LEFT);

        // Add the label
        page.container.packStart(text, false, false, 0);

        // Create a box to pack widgets to select a file size
        final HBox box = new HBox(false, 3);
        page.container.packStart(box, false, false, 0);

        // Create a label
        final Label label = new Label(_("Split in:"));
        box.packStart(label, false, false, 0);

        // Create the spin button
        final SpinButton button = new SpinButton(1, 4096, 1);
        box.packStart(button);

        // Create the list of units
        final TextComboBox units = new TextComboBox();
        for (String unit : SizeUnit.toStrings()) {
            // Fill the box
            units.appendText(unit);
        }
        units.setActive(0);
        box.packStart(units);

        // Add a last label to see if the size is valid
        final Label valid = new Label();
        valid.setJustify(Justification.CENTER);
        valid.setUseMarkup(true);
        valid.setLabel("<b><span foreground=\"red\">"
                + _("Invalid chunk size. The size must be lower than the size of the file to split.")
                + "</span></b>");
        page.container.packStart(valid, false, false, 0);

        // Handle the signal from the spin button
        button.connect(new SpinButton.ValueChanged() {
            @Override
            public void onValueChanged(SpinButton source) {
                size = source.getValue();

                // Calulate the size
                long value = calculateSize(size, unit);

                // Show or hide the label
                if (value == -1) {
                    valid.show();
                    setPageComplete(page, false);
                } else {
                    valid.hide();
                    setPageComplete(page, true);
                }
            }
        });

        // Handle the signal from the list of units
        units.connect(new ComboBox.Changed() {
            @Override
            public void onChanged(ComboBox source) {
                unit = source.getActive();

                // Calulate the size
                long value = calculateSize(size, unit);

                // Show or hide the label
                if (value == -1) {
                    valid.show();
                    setPageComplete(page, false);
                } else {
                    valid.hide();
                    setPageComplete(page, true);
                }
            }
        });

        // Setup the page in the assistant
        this.appendPage(page);
        this.setPageType(page, AssistantPageType.CONTENT);
        this.setPageTitle(page, _("Size selection"));
        this.setPageHeaderImage(page, logo);
        this.setPageComplete(page, false);
    }

    /**
     * Create a page to select the algorithm.
     */
    private void createAlgoSelection() {
        final Page page = new Page();

        // Setup the default algorithm
        algorithm = app.getConfig().DEFAULT_ALGORITHM;

        // The text to display
        final String data = _("The algorithm of split defines the way the file will be splitted.\n\nIf you want to split the file and then merge the created chunks with this\nprogram, you can use the <b>GNOME Split</b> algorithm. You can also use the\n<b>Xtremsplit</b> algorithm. Its advantage is that the chunks could be merged with the\nXtremsplit software available on <s>Windows</s>. And finally, you may want to\nuse the <b>Simple</b> algorithm. It will allow you to merge the chunks using a\ncommand line with a tool like `cat`.");

        // Create the label
        final Label text = new Label();
        text.setLabel(data);
        text.setUseMarkup(true);
        text.setJustify(Justification.LEFT);

        // Add the label
        page.container.packStart(text, false, false, 0);

        // Create a box to pack widgets to select a file
        final HBox box = new HBox(false, 3);
        page.container.packStart(box, false, false, 0);

        // Add a label to it
        final Label label = new Label(_("Algorithm to use:"));
        box.packStart(label, false, false, 0);

        // Add a list containing the algorithm
        final TextComboBox list = new TextComboBox();
        for (String algorithm : Algorithm.toStrings()) {
            // Fill the box
            list.appendText(algorithm);
        }
        list.setActive(algorithm);
        box.packStart(list, false, false, 0);

        // Connect the signal handler for the list
        list.connect(new ComboBox.Changed() {
            @Override
            public void onChanged(ComboBox source) {
                algorithm = source.getActive();
            }
        });

        // Setup the page in the assistant
        this.appendPage(page);
        this.setPageType(page, AssistantPageType.CONTENT);
        this.setPageTitle(page, _("Algorithm selection"));
        this.setPageHeaderImage(page, logo);
        this.setPageComplete(page, true);
    }

    /**
     * Create a page to sum up all info and validate the merge.
     */
    private void createSummary() {
        final Page page = new Page();

        // The text to display
        final String data = _("Now, we are able to start to split the file you want. But please take a look to\nthe summary. If something is wrong, go back to the a previous step to change it\nbefore confirming and starting the split.");

        // Create the label
        final Label text = new Label();
        text.setLabel(data);
        text.setUseMarkup(true);
        text.setJustify(Justification.LEFT);

        // Add the label
        page.container.packStart(text, false, false, 0);

        // Add the labels
        label.setUseMarkup(true);
        page.container.packStart(label, false, false, 0);

        // Last label and last question
        final Label last = new Label(
                _("Just a last question before you confirm the split to do.\nIf you check the following box, the split will start after your confirmation."));
        last.setJustify(Justification.LEFT);
        page.container.packStart(last, false, false, 0);

        // Last question
        final CheckButton startCheck = new CheckButton(_("Start the split after the confirmation."));
        page.container.packStart(startCheck, false, false, 0);

        // Connect the signal handler of the question
        startCheck.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                start = startCheck.getActive();
            }
        });

        // Setup the page in the assistant
        this.appendPage(page);
        this.setPageType(page, AssistantPageType.CONFIRM);
        this.setPageTitle(page, _("Confirmation"));
        this.setPageHeaderImage(page, logo);
        this.setPageComplete(page, true);
    }

    @Override
    public void createIntroduction() {
        final Page page = new Page();

        // The text to display
        final String data = _("This assistant will help you to create a <b>split</b>.\n\nSplitting a file will create smaller files that you will be able to store on a\ndevice or online. During this assistant, 3 questions will be asked to you.\n\nFirst, you will have to select the file to split. After that, you will have to\nchoose the maximal size for each chunks that will be created. And finally, you\nwill have to choose the algorithm to use.\n\nAfter those 3 questions, the assistant will show you a summary of what GNOME\nSplit will do. And to finish, after your confirmation, the split will be ready.\n\nYou can also modify the split configuration to define in details how you want\nto have the split done by taking a look at <i>Edit > Preferences > Split</i>.");

        // Create the label
        final Label text = new Label();
        text.setLabel(data);
        text.setUseMarkup(true);
        text.setJustify(Justification.LEFT);

        // Add the label
        page.container.packStart(text, false, false, 0);

        // Setup the page in the assistant
        this.appendPage(page);
        this.setPageType(page, AssistantPageType.INTRO);
        this.setPageTitle(page, _("Introduction"));
        this.setPageHeaderImage(page, logo);
        this.setPageComplete(page, true);
    }

    @Override
    public void updateWidget() {
        // Switch the view if needed
        app.getMainWindow().getViewSwitcher().switchToSplit();

        // Update the widget using the info
        app.getMainWindow().getSplitWidget().setSplit(filename, size, unit, algorithm);
    }

    @Override
    public void onPrepare(Assistant source, Widget page) {
        // Prepare the last page
        if (source.getCurrentPage() == 4) {
            // Setup the label
            label.setLabel(_(
                    "The file to split is <b>{0}</b>.\n\nThe maximum size of each chunk will be <b>{1}</b>.\n\nThe file will be splitted using the <b>{2}</b> algorithm.",
                    new File(filename).getName(), SizeUnit.formatSize(this.calculateSize(size, unit)),
                    Algorithm.toStrings()[algorithm]));
        }
    }

    @Override
    public void onClose(Assistant source) {
        source.hide();
    }

    @Override
    public void onCancel(Assistant source) {
        source.hide();
    }

    @Override
    public void onApply(Assistant source) {
        // Update the widget
        this.updateWidget();

        if (start) {
            // Start the merge if requested
            app.getActionManager().getAction(ActionId.START).emitActivate();
        }

        // Then hide the assistant
        source.hide();
    }
}
