/*
 * ActionAssistant.java
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
package org.gnome.split.gtk.widget.assistant;

import java.io.File;

import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Assistant;
import org.gnome.gtk.AssistantPageType;
import org.gnome.gtk.Button;
import org.gnome.gtk.ButtonBoxStyle;
import org.gnome.gtk.CheckButton;
import org.gnome.gtk.ComboBox;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.FileFilter;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.Label;
import org.gnome.gtk.RadioButton;
import org.gnome.gtk.RadioGroup;
import org.gnome.gtk.SpinButton;
import org.gnome.gtk.Stock;
import org.gnome.gtk.ToggleButton;
import org.gnome.gtk.VBox;
import org.gnome.gtk.VButtonBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.WindowPosition;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Constants;
import org.gnome.split.core.utils.Algorithm;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.gtk.action.ActionManager.ActionId;
import org.gnome.split.gtk.widget.base.AlgorithmsBox;
import org.gnome.split.gtk.widget.base.UnitsBox;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This assistant is used to help the user to create a split or a merge.
 * 
 * @author Guillaume Mazoyer
 */
public class ActionAssistant extends Assistant implements Assistant.Prepare, Assistant.Close,
        Assistant.Cancel, Assistant.Apply, Assistant.ForwardPage
{
    /**
     * The current instance of GNOME Split.
     */
    private GnomeSplit app;

    /**
     * Type of assistant we're going to use.
     */
    private byte type;

    /**
     * Final page of the assistant.
     */
    private FinalPage conclusion;

    /**
     * The logo of this assistant.
     */
    private Pixbuf logo;

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

    public ActionAssistant(final GnomeSplit app) {
        super();

        // Save the instance
        this.app = app;

        // Set this assistant in the center of the screen
        this.setPosition(WindowPosition.CENTER);

        // Set the title
        this.setTitle(_("Assistant"));

        // Set the type
        this.type = 0;

        // Set the default values
        this.filename = null;
        this.size = 1;
        this.unit = 0;
        this.algorithm = 0;

        // Add introduction
        this.createGeneralIntroduction();

        // Add merge assistant page
        this.createMergeFileSelection();

        // Add split assistant pages
        this.createSplitFileSelection();
        this.createSplitSizeSelection();
        this.createSplitAlgoSelection();

        // Add conclusion
        this.createSummary();

        // Connect a signal to handle pages
        this.setForwardPageCallback((ForwardPage) this);

        // Connect signal handlers
        this.connect((Assistant.Prepare) this);
        this.connect((Assistant.Close) this);
        this.connect((Assistant.Cancel) this);
        this.connect((Assistant.Apply) this);
    }

    /**
     * Create a simple label which is aligned to the left and which can use
     * markups.
     */
    static Label createLeftAlignedLabel(String text) {
        final Label label = new Label(text);

        label.setUseMarkup(true);
        label.setLineWrap(true);
        label.setAlignment(0.0f, 0.5f);

        return label;
    }

    /**
     * Method to update the right widget using the previously gathered data.
     */
    private void updateWidget() {
        if (type == 0) {
            // Switch to the split view if needed
            app.getActionManager().activateRadioAction(ActionId.SPLIT);

            // Update the widget using the info
            app.getMainWindow().getSplitWidget().setSplit(filename, size, unit, algorithm);
        } else {
            // Switch to the merge view if needed
            app.getActionManager().activateRadioAction(ActionId.MERGE);

            // Update the widget using the filename
            app.getMainWindow().getMergeWidget().setFile(filename);
        }
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
     * Create a &quot;page&quot;. It is actually just a {@link VBox} with 5
     * pixels as border.
     * 
     * @return
     */
    private VBox createPage() {
        final VBox page = new VBox(false, 3);

        page.setBorderWidth(5);

        return page;
    }

    /**
     * Create the main introduction of the assistant.
     */
    private void createGeneralIntroduction() {
        final VBox page = this.createPage();

        // The text to display
        final String data = _("What do you want to do?");

        // Add the label
        page.packStart(createLeftAlignedLabel(data), false, false, 0);

        // Create a box to pack the two choices
        final VButtonBox box = new VButtonBox();
        box.setLayout(ButtonBoxStyle.SPREAD);
        page.packStart(box, false, false, 0);

        // Create the two choices
        final RadioGroup group = new RadioGroup();
        final RadioButton split = new RadioButton(group, _("Split a file"));
        final RadioButton merge = new RadioButton(group, _("Merge several files"));

        // Add them to the page
        box.packStart(split, false, false, 0);
        box.packStart(merge, false, false, 0);

        // Connect signals to the buttons
        split.connect(new ToggleButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                if (source.getActive()) {
                    type = 0;
                }
            }
        });

        merge.connect(new ToggleButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                if (source.getActive()) {
                    type = 1;
                }
            }
        });

        // Set the type according to the current view
        type = (byte) (app.getMainWindow().getSplitWidget().isVisible() ? 0 : 1);
        split.setActive(type == 0);
        merge.setActive(type == 1);

        // Add a button to turn on/off the assistant on start
        final CheckButton assistant = new CheckButton(_("_Show the assistant on start"));
        assistant.setActive(app.getConfig().ASSISTANT_ON_START);
        page.packStart(assistant, false, false, 0);

        // Connect check button signal
        assistant.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                app.getConfig().ASSISTANT_ON_START = assistant.getActive();
                app.getConfig().savePreferences();
            }
        });

        // Setup the page in the assistant
        this.appendPage(page);
        this.setPageType(page, AssistantPageType.INTRO);
        this.setPageTitle(page, _("Action selection"));
        this.setPageHeaderImage(page, Constants.PROGRAM_LOGO);
        this.setPageComplete(page, true);
    }

    /**
     * Create a page to sum up all info and validate the action.
     */
    private void createSummary() {
        // The text to display
        final String data = _("You can verify that all the data that have been collected are correct. If they are not, you can go back to a previous step to change them.");

        // Create the final page
        conclusion = new FinalPage(data);

        // Setup the page in the assistant
        this.appendPage(conclusion);
        this.setPageType(conclusion, AssistantPageType.CONFIRM);
        this.setPageTitle(conclusion, _("Confirmation"));
        this.setPageComplete(conclusion, true);
    }

    /**
     * Create a page to select the file to split.
     */
    private void createSplitFileSelection() {
        final VBox page = this.createPage();

        // The text to display
        final String data = _("Select the file to split.");

        // Add the label
        page.packStart(createLeftAlignedLabel(data), false, false, 0);

        // Create a box to pack widgets to select a file
        final HBox box = new HBox(false, 3);
        page.packStart(box, false, false, 0);

        // Add a label to it
        final Label label = new Label(_("File to split:"));
        box.packStart(label, false, false, 0);

        // Add a chooser button to it
        final FileChooserButton button = new FileChooserButton(_("Select a file."),
                FileChooserAction.OPEN);
        button.setCurrentFolder(System.getProperty("user.home"));
        box.packStart(button, true, true, 0);

        // Connect chooser handler to change the filename
        button.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                // Update the filename
                filename = source.getFilename();

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
    private void createSplitSizeSelection() {
        final VBox page = this.createPage();

        // The text to display
        final String data = _("Select the maximal size for each chunk. You can let GNOME Split calculate the size by giving the number of chunks to create.");

        // Add the label
        page.packStart(createLeftAlignedLabel(data), false, false, 0);

        // Create a box to pack widgets to select a file size
        final HBox box = new HBox(false, 3);
        page.packStart(box, false, false, 0);

        // Create the spin button
        final SpinButton button = new SpinButton(1, 4096, 1);
        button.setValue(2);
        box.packStart(button, true, true, 0);

        // Create the list of units
        final UnitsBox units = new UnitsBox(app);
        box.packStart(units, true, true, 0);

        // Add a last label to see if the size is valid
        final Image valid = new Image(Stock.YES, IconSize.BUTTON);
        box.packStart(valid, false, false, 0);

        // Init some variables
        size = button.getValue();
        unit = units.getActive();

        // Handle the signal from the spin button
        button.connect(new SpinButton.ValueChanged() {
            @Override
            public void onValueChanged(SpinButton source) {
                size = source.getValue();

                // Calculate the size
                long value = calculateSize(size, unit);

                // Change the image and its tooltip
                if (value > -1) {
                    valid.setImage(Stock.YES, IconSize.BUTTON);
                    valid.setTooltipMarkup("");
                    setPageComplete(page, true);
                } else {
                    valid.setImage(Stock.DIALOG_WARNING, IconSize.BUTTON);
                    valid.setTooltipMarkup(_("Invalid chunk size. The size must be lower than the size of the file to split."));
                    valid.show();
                    setPageComplete(page, false);
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
        this.setPageComplete(page, true);
    }

    /**
     * Create a page to select the algorithm.
     */
    private void createSplitAlgoSelection() {
        final VBox page = this.createPage();

        // Setup the default algorithm
        algorithm = app.getConfig().DEFAULT_ALGORITHM;

        // The text to display
        final String data = _("The algorithm defines the way how the file will be split.");

        // Add the label
        page.packStart(createLeftAlignedLabel(data), false, false, 0);

        // Create a box to pack widgets to select a file
        final HBox box = new HBox(false, 3);
        page.packStart(box, false, false, 0);

        // Add a label to it
        final Label label = new Label(_("Algorithm to use:"));
        box.packStart(label, false, false, 0);

        // Add a list containing the algorithm
        final AlgorithmsBox list = new AlgorithmsBox(app);
        box.packStart(list, true, true, 0);

        // Add an icon which will contain a quick description of the selected
        // algorithm as a tooltip
        final Image info = new Image(Stock.DIALOG_QUESTION, IconSize.BUTTON);
        info.setTooltipMarkup(Algorithm.getDescriptions()[algorithm]);
        box.packStart(info, false, false, 0);

        // Connect the signal handler for the list
        list.connect(new ComboBox.Changed() {
            @Override
            public void onChanged(ComboBox source) {
                algorithm = source.getActive();
                info.setTooltipMarkup(Algorithm.getDescriptions()[algorithm]);
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
     * Create a page to select the first file to merge.
     */
    private void createMergeFileSelection() {
        final VBox page = this.createPage();

        // The text to display
        final String data = _("Select the first file to merge.");

        // Add the label
        page.packStart(createLeftAlignedLabel(data), false, false, 0);

        // Create a box to pack widgets to select a file
        final HBox box = new HBox(false, 3);
        page.packStart(box, false, false, 0);

        // Add a label to it
        final Label label = new Label(_("File to merge:"));
        box.packStart(label, false, false, 0);

        // File filter to help choosing a valid chunk
        final FileFilter chk = new FileFilter(_("Valid chunks"));
        chk.addPattern("*.001.gsp");
        chk.addPattern("*.001.xtm");
        chk.addPattern("*.000");
        chk.addPattern("*.001");

        // Add a chooser button to it
        final FileChooserButton button = new FileChooserButton(_("Select a file."),
                FileChooserAction.OPEN);
        button.addFilter(chk);
        button.setCurrentFolder(System.getProperty("user.home"));
        box.packStart(button, true, true, 0);

        // Connect chooser handler to change the filename
        button.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                // Update the filename
                filename = source.getFilename();

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

    @Override
    public void onApply(Assistant source) {
        if (source.getCurrentPage() != 0) {
            // Update the widget
            this.updateWidget();

            // Start the split/merge if requested
            app.getActionManager().activateAction(ActionId.START);

            // Then hide the assistant
            source.hide();
        }
    }

    @Override
    public void onCancel(Assistant source) {
        source.hide();
    }

    @Override
    public void onClose(Assistant source) {
        source.hide();
    }

    @Override
    public void onPrepare(Assistant source, Widget widget) {
        switch (source.getCurrentPage()) {
        case 1:
            if (type == 1) {
                // Update the logo to use
                logo = Gtk.renderIcon(this, Stock.PASTE, IconSize.DIALOG);
            } else {
                // Update the logo to use
                logo = Gtk.renderIcon(this, Stock.CUT, IconSize.DIALOG);
            }
            this.setPageHeaderImage(conclusion, logo);
            break;

        case 5:
            if (type == 1) {
                conclusion.setFields(new String[] {
                    _("First file to merge:")
                }, new String[] {
                    new File(filename).getName()
                });
            } else {
                conclusion.setFields(
                        new String[] {
                                _("File to split:"), _("Maximum size of a chunk:"),
                                _("Algorithm of split:")
                        },
                        new String[] {
                                new File(filename).getName(),
                                SizeUnit.formatSize(this.calculateSize(size, unit)),
                                Algorithm.toStrings()[algorithm]
                        });
            }
            break;
        }
    }

    @Override
    public int onForward(Assistant source, int currentPage) {
        // The next page to display
        int next = 0;

        if (type == 1) {
            // Only merge related pages
            switch (currentPage) {
            case 0:
                next = 1;
                break;
            case 1:
                next = 5;
                break;
            }
        } else {
            // Only split related pages
            switch (currentPage) {
            case 0:
                next = 2;
                break;
            case 2:
                next = 3;
                break;
            case 3:
                next = 4;
                break;
            case 4:
                next = 5;
                break;
            }
        }

        // Finally
        return next;
    }
}
