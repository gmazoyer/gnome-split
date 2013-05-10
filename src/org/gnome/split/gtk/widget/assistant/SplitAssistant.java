/*
 * SplitAssistant.java
 * 
 * Copyright (c) 2009-2013 Guillaume Mazoyer
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

import static org.freedesktop.bindings.Internationalization._;
import static org.gnome.split.GnomeSplit.actions;
import static org.gnome.split.GnomeSplit.config;
import static org.gnome.split.GnomeSplit.ui;

import java.io.File;

import org.gnome.gtk.Assistant;
import org.gnome.gtk.AssistantPageType;
import org.gnome.gtk.ComboBox;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Image;
import org.gnome.gtk.SpinButton;
import org.gnome.gtk.Stock;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.split.config.Constants;
import org.gnome.split.core.utils.Algorithm;
import org.gnome.split.core.utils.SizeUnit;
import org.gnome.split.gtk.action.ActionManager.ActionId;
import org.gnome.split.gtk.widget.base.AlgorithmsBox;
import org.gnome.split.gtk.widget.base.UnitsBox;

/**
 * This assistant is used to help the user to create a split action.
 * 
 * @author Guillaume Mazoyer
 */
public class SplitAssistant extends BasicAssistant
{
    /**
     * Final page of the assistant.
     */
    private FinalPage conclusion;

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

    public SplitAssistant() {
        super(_("Split assistant"));

        // Set the default values
        this.filename = null;
        this.size = 1;
        this.unit = 0;
        this.algorithm = 0;

        // Add introduction
        this.createIntroduction();

        // Add split pages
        this.createSplitSizeSelection();
        this.createSplitAlgoSelection();

        // Add conclusion
        this.createConclusion();
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
     * Create a page to select the size.
     */
    private void createSplitSizeSelection() {
        final VBox page = createPage();

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
        final UnitsBox units = new UnitsBox();
        box.packStart(units, true, true, 0);

        // Add a last label to see if the size is valid
        final Image valid = new Image(Stock.YES, IconSize.BUTTON);
        box.packStart(valid, false, false, 0);

        // Initialize some variables
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

                // Calculate the size
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
        this.setPageHeaderImage(page, Constants.PROGRAM_LOGO);
        this.setPageComplete(page, true);
    }

    /**
     * Create a page to select the algorithm.
     */
    private void createSplitAlgoSelection() {
        final VBox page = createPage();

        // Setup the default algorithm
        algorithm = config.DEFAULT_ALGORITHM;

        // The text to display
        final String data = _("The algorithm defines the way how the file will be split.");

        // Add the label
        page.packStart(createLeftAlignedLabel(data), false, false, 0);

        // Create a box to pack widgets to select a file
        final HBox box = new HBox(false, 3);
        page.packStart(box, false, false, 0);

        // Add a list containing the algorithm
        final AlgorithmsBox list = new AlgorithmsBox();
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
        this.setPageHeaderImage(page, Constants.PROGRAM_LOGO);
        this.setPageComplete(page, true);
    }

    @Override
    protected void createIntroduction() {
        final VBox page = createPage();

        // The text to display
        final String data = _("Select the file to split.");

        // Add the label
        page.packStart(createLeftAlignedLabel(data), false, false, 0);

        // Add a chooser button to it
        final FileChooserButton button = new FileChooserButton(_("Select a file."),
                FileChooserAction.OPEN);
        button.setCurrentFolder(System.getProperty("user.home"));
        page.packStart(button, false, false, 0);

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
        this.setPageType(page, AssistantPageType.INTRO);
        this.setPageTitle(page, _("File selection"));
        this.setPageHeaderImage(page, Constants.PROGRAM_LOGO);
        this.setPageComplete(page, false);
    }

    @Override
    protected void createConclusion() {
        // The text to display
        final String data = _("You can verify that all the data that have been collected are correct. If they are not, you can go back to a previous step to change them.");

        // Create the final page
        conclusion = new FinalPage(data);

        // Setup the page in the assistant
        this.appendPage(conclusion);
        this.setPageType(conclusion, AssistantPageType.CONFIRM);
        this.setPageTitle(conclusion, _("Confirmation"));
        this.setPageHeaderImage(conclusion, Constants.PROGRAM_LOGO);
        this.setPageComplete(conclusion, true);
    }

    @Override
    protected void updateInterface() {
        // Switch to the split view if needed
        actions.activateRadioAction(ActionId.SPLIT);

        // Update the widget using the info
        ui.getSplitWidget().setSplit(filename, size, unit, algorithm);
    }

    @Override
    public void onPrepare(Assistant source, Widget widget) {
        if (source.getCurrentPage() == 3) {
            conclusion.setFields(new String[] {
                _("File to split:"),
                _("Maximum size of a chunk:"),
                _("Algorithm of split:")
            }, new String[] {
                new File(filename).getName(),
                SizeUnit.formatSize(this.calculateSize(size, unit)),
                Algorithm.toStrings()[algorithm]
            });
        }
    }
}
