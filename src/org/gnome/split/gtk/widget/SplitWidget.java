/*
 * SplitWidget.java
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
package org.gnome.split.gtk.widget;

import java.io.File;

import org.gnome.gtk.Entry;
import org.gnome.gtk.EntryIconPosition;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.FileChooserWidget;
import org.gnome.gtk.Frame;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Label;
import org.gnome.gtk.ProgressBar;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.SpinButton;
import org.gnome.gtk.Stock;
import org.gnome.gtk.TextComboBox;
import org.gnome.gtk.VBox;
import org.gnome.split.GnomeSplit;
import org.gnome.split.core.utils.Algorithm;
import org.gnome.split.core.utils.SizeUnit;

import static org.freedesktop.bindings.Internationalization._;

/**
 * A widget derived from {@link Frame} to allow the user to start a split.
 * 
 * @author Guillaume Mazoyer
 */
public class SplitWidget extends Frame implements ActionWidget
{
    /**
     * Define if the widget is visible or not.
     */
    private boolean visible;

    /**
     * File to split.
     */
    private Entry fileEntry;

    /**
     * Select a file using a {@link FileChooserWidget}.
     */
    private FileChooserButton fileChooser;

    /**
     * Name of the files to create.
     */
    private Entry destinationEntry;

    /**
     * Directory where the chunks will be created.
     */
    private FileChooserButton dirChooser;

    /**
     * Size value of the chunks.
     */
    private SpinButton sizeButton;

    /**
     * Size units of the chunks.
     */
    private TextComboBox sizeUnits;

    /**
     * Algorithm to use to split the file.
     */
    private TextComboBox algoList;

    /**
     * Split progress.
     */
    private ProgressBar progressbar;

    public SplitWidget(final GnomeSplit app) {
        super(null);

        // At first, it is invisible
        visible = false;

        // Main container
        final VBox container = new VBox(false, 12);
        this.add(container);

        final HBox firstRow = new HBox(false, 5);
        container.packStart(firstRow);

        // Pack all Labels in the same box
        final VBox labelColumn = new VBox(false, 5);
        firstRow.packStart(labelColumn);

        final Label fileLabel = new Label(_("File:"));
        labelColumn.packStart(fileLabel);

        final Label destinationLabel = new Label(_("Destination:"));
        labelColumn.packStart(destinationLabel);

        // Pack all Entrys in the same box
        final VBox entryColumn = new VBox(false, 5);
        firstRow.packStart(entryColumn);

        fileEntry = new Entry();
        fileEntry.setIconFromStock(EntryIconPosition.PRIMARY, Stock.FILE);
        fileEntry.setIconActivatable(EntryIconPosition.PRIMARY, false);
        entryColumn.packStart(fileEntry);

        destinationEntry = new Entry();
        destinationEntry.setIconFromStock(EntryIconPosition.PRIMARY, Stock.FILE);
        destinationEntry.setIconActivatable(EntryIconPosition.PRIMARY, false);
        entryColumn.packStart(destinationEntry);

        // Pack all choosers in the same box
        final VBox chooserColumn = new VBox(false, 5);
        firstRow.packStart(chooserColumn);

        fileChooser = new FileChooserButton(_("Choose a file."), FileChooserAction.OPEN);
        fileChooser.setCurrentFolder(System.getProperty("user.home"));
        fileChooser.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                String filename = source.getFilename();
                int separator = filename.lastIndexOf(File.separator) + 1;
                String file = filename.substring(separator, filename.length());

                // Update entries
                fileEntry.setText(filename);
                destinationEntry.setText(file);
            }
        });
        chooserColumn.packStart(fileChooser);

        dirChooser = new FileChooserButton(_("Choose a directory."), FileChooserAction.SELECT_FOLDER);
        dirChooser.setCurrentFolder(System.getProperty("user.home"));
        chooserColumn.packStart(dirChooser);

        final HBox thirdRow = new HBox(false, 5);
        container.packStart(thirdRow);

        // Pack size related widgets
        final VBox firstColumn = new VBox(false, 5);
        thirdRow.packStart(firstColumn);

        final Label sizeLabel = new Label(_("Split in:"));
        firstColumn.packStart(sizeLabel);

        final HBox splitSize = new HBox(false, 3);
        firstColumn.packStart(splitSize);

        sizeButton = new SpinButton(1, 4096, 1);
        splitSize.packStart(sizeButton);

        sizeUnits = new TextComboBox();
        for (String unit : SizeUnit.toStrings()) {
            // Fill the box
            sizeUnits.appendText(unit);
        }
        sizeUnits.setActive(0);
        splitSize.packStart(sizeUnits);

        // Pack algorithm related widgets
        final VBox secondColumn = new VBox(false, 5);
        thirdRow.packStart(secondColumn);

        final Label algoLabel = new Label(_("Algorithm:"));
        secondColumn.packStart(algoLabel);

        algoList = new TextComboBox();
        for (String algorithm : Algorithm.toStrings()) {
            // Fill the box
            algoList.appendText(algorithm);
        }
        algoList.setActive(app.getConfig().DEFAULT_ALGORITHM);
        secondColumn.packStart(algoList);

        // Make the sizes of size and algorithm boxes equal
        final SizeGroup group = new SizeGroup(SizeGroupMode.BOTH);
        group.add(firstColumn);
        group.add(secondColumn);

        // Pack the progress bar
        progressbar = new ProgressBar();
        container.packStart(progressbar, false, false, 0);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean setting) {
        visible = setting;

        if (visible) {
            this.showAll();
        } else {
            this.hide();
        }
    }

    @Override
    public boolean isFullyFilled() {
        return (!fileEntry.getText().isEmpty() && !destinationEntry.getText().isEmpty());
    }

    @Override
    public void disable() {
        fileEntry.setSensitive(false);
        fileChooser.setSensitive(false);
        destinationEntry.setSensitive(false);
        dirChooser.setSensitive(false);
        sizeButton.setSensitive(false);
        sizeUnits.setSensitive(false);
        algoList.setSensitive(false);
    }

    @Override
    public void enable() {
        fileEntry.setSensitive(true);
        fileChooser.setSensitive(true);
        destinationEntry.setSensitive(true);
        dirChooser.setSensitive(true);
        sizeButton.setSensitive(true);
        sizeUnits.setSensitive(true);
        algoList.setSensitive(true);
    }

    @Override
    public void reset() {
        fileEntry.setText("");
        destinationEntry.setText("");
        dirChooser.setCurrentFolder(System.getProperty("user.home"));
        sizeButton.setValue(1);
        sizeUnits.setActive(0);
        algoList.setActive(0);
        progressbar.setFraction(0);
        progressbar.setText("");
    }

    @Override
    public void updateProgress(double progress, String text) {
        progressbar.setFraction(progress);
        progressbar.setText(text);
    }

    /**
     * Get the name of the file to split.
     */
    public String getFilename() {
        return fileEntry.getText();
    }

    /**
     * Get the names of the files to create.
     */
    public String getDestination() {
        StringBuilder builder = new StringBuilder();

        // Add directory + name
        builder.append(dirChooser.getCurrentFolder());
        builder.append(File.separator);
        builder.append(destinationEntry.getText());

        return builder.toString();
    }

    /**
     * Get the maximum size of each chunk.
     */
    public long getMaxSize() {
        int unit = sizeUnits.getActive();
        long input = new File(this.getFilename()).length();
        long result;

        if (unit == 0) {
            result = (long) (input / sizeButton.getValue());
        } else {
            unit -= 2;
            double multiplicator = (unit == -1) ? 1 : SizeUnit.values()[unit];
            result = (long) (sizeButton.getValue() * multiplicator);
        }

        // Size not valid (bigger than the length of the file to split)
        if (result >= input) {
            return -1;
        }
        return result;
    }

    /**
     * Get the ID of the {@link Algorithm algorithm} to use.
     */
    public int getAlgorithm() {
        return algoList.getActive();
    }

    /**
     * Update the {@link ProgressBar} to display the right
     * <code>progress</code>.
     */
    public void setProgress(double progress) {
        progressbar.setFraction(progress);
    }
}
