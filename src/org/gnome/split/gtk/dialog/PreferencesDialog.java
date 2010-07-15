/*
 * PreferencesDialog.java
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
package org.gnome.split.gtk.dialog;

import org.gnome.gdk.Event;
import org.gnome.gtk.Button;
import org.gnome.gtk.CheckButton;
import org.gnome.gtk.ComboBox;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.HBox;
import org.gnome.gtk.Justification;
import org.gnome.gtk.Label;
import org.gnome.gtk.Notebook;
import org.gnome.gtk.RadioButton;
import org.gnome.gtk.RadioGroup;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.SpinButton;
import org.gnome.gtk.Stock;
import org.gnome.gtk.TextComboBox;
import org.gnome.gtk.ToggleButton;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.Dialog.Response;
import org.gnome.gtk.Window.DeleteEvent;
import org.gnome.notify.Notify;
import org.gnome.split.GnomeSplit;
import org.gnome.split.config.Configuration;
import org.gnome.split.config.Constants;
import org.gnome.split.core.utils.Algorithm;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to build GTK+ Preferences dialog.
 * 
 * @author Guillaume Mazoyer
 */
public final class PreferencesDialog extends Dialog implements DeleteEvent, Response
{
    /**
     * Configuration of the application.
     */
    private final Configuration config;

    /**
     * Current instance of the application.
     */
    private GnomeSplit app;

    /**
     * Directory chooser for the split widget.
     */
    private FileChooserButton splitDirChooser;

    /**
     * Directory chooser for the merge widget.
     */
    private FileChooserButton mergeDirChooser;

    public PreferencesDialog(final GnomeSplit app) {
        super(_("GNOME Split Preferences"), app.getMainWindow(), false);

        // Get configuration
        this.config = app.getConfig();
        this.app = app;

        // Border width
        this.setBorderWidth(12);

        // Add the notebook
        final Notebook notebook = new Notebook();
        notebook.show();
        this.add(notebook);

        // Add all the pages
        notebook.appendPage(this.createGeneralPage(), new Label(_("General")));
        notebook.appendPage(this.createSplitPage(), new Label(_("Split")));
        notebook.appendPage(this.createMergePage(), new Label(_("Merge")));
        notebook.appendPage(this.createDesktopPage(), new Label(_("Desktop")));

        // Close button (save the configuration and close)
        this.addButton(Stock.CLOSE, ResponseType.CLOSE);

        // Connect classic signals
        this.connect((Window.DeleteEvent) this);
        this.connect((Dialog.Response) this);
    }

    /**
     * Just create a label with 4 spaces in it for alignment reason.
     */
    private Label createEmptyLabel() {
        return new Label("    ");
    }

    /**
     * Create a label justified to the left and using bold font.
     */
    private Label createSectionLabel(String text) {
        // Create the label
        final Label label = new Label("<b>" + text + "</b>");

        // Use pango markup language
        label.setUseMarkup(true);

        // Make it goes to the left
        label.setAlignment(0f, 0.5f);
        label.setJustify(Justification.LEFT);

        // Finally
        return label;
    }

    /**
     * Create the first page of the dialog (general config).
     */
    private VBox createGeneralPage() {
        final VBox page = new VBox(false, 18);
        page.setBorderWidth(12);

        // Buttons group for default view choice
        final RadioGroup group = new RadioGroup();

        // First options
        final VBox first = new VBox(false, 6);
        page.packStart(first, false, false, 0);

        // Add the label
        first.packStart(this.createSectionLabel(_("Assistant")), false, false, 0);

        // Add the row of options
        final HBox firstRow = new HBox(false, 0);
        first.packStart(firstRow, false, false, 0);

        // Add an empty label
        firstRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Add the option
        final CheckButton assistant = new CheckButton(_("_Show the assistant on start"));
        assistant.setActive(config.ASSISTANT_ON_START);
        firstRow.packStart(assistant, false, false, 0);
        assistant.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.ASSISTANT_ON_START = assistant.getActive();
                config.savePreferences();
            }
        });

        // First options
        final VBox second = new VBox(false, 6);
        page.packStart(second, false, false, 0);

        // Add the label
        second.packStart(this.createSectionLabel(_("Default view")), false, false, 0);

        // Add the row of options
        final HBox secondRow = new HBox(false, 0);
        second.packStart(secondRow, false, false, 0);

        // Add an empty label
        secondRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Pack the options
        final VBox boxes = new VBox(false, 6);
        secondRow.packStart(boxes, false, false, 0);

        // Split choice
        final RadioButton split = new RadioButton(group, _("Split"));
        split.setActive(config.DEFAULT_VIEW == 0);
        boxes.packStart(split, false, false, 0);
        split.connect(new RadioButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                if (source.getActive()) {
                    // Save preferences
                    config.DEFAULT_VIEW = 0;
                    config.savePreferences();
                }
            }
        });

        // Merge choice
        final RadioButton merge = new RadioButton(group, _("Merge"));
        merge.setActive(config.DEFAULT_VIEW == 1);
        boxes.packStart(merge, false, false, 0);
        merge.connect(new RadioButton.Toggled() {
            @Override
            public void onToggled(ToggleButton source) {
                if (source.getActive()) {
                    // Save preferences
                    config.DEFAULT_VIEW = 1;
                    config.savePreferences();
                }
            }
        });

        // Second option
        final VBox third = new VBox(false, 6);
        page.packStart(third, false, false, 0);

        // Add the label
        third.packStart(this.createSectionLabel(_("Program run")), false, false, 0);

        // Add the row of options
        final HBox thirdRow = new HBox(false, 0);
        third.packStart(thirdRow, false, false, 0);

        // Add an empty label
        thirdRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Restore multiple instances status
        final CheckButton instances = new CheckButton(_("_Allow multiple instances."));
        instances.setActive(config.MULTIPLE_INSTANCES);
        thirdRow.packStart(instances, false, false, 0);
        instances.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.MULTIPLE_INSTANCES = instances.getActive();
                config.savePreferences();
            }
        });

        // Third option
        final VBox fourth = new VBox(false, 6);
        page.packStart(fourth, false, false, 0);

        // Add the label
        fourth.packStart(this.createSectionLabel(_("Size of the main window")), false, false, 0);

        // Add the row of options
        final HBox fourthRow = new HBox(false, 0);
        fourth.packStart(fourthRow, false, false, 0);

        // Add an empty label
        fourthRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Add a box to pack widgets to change the size
        final VBox sizeBox = new VBox(false, 6);
        fourthRow.packStart(sizeBox, false, false, 0);

        // Create some needed widgets
        final SpinButton width = new SpinButton(1, 2048, 1);
        final SpinButton height = new SpinButton(1, 2048, 1);
        final Button useCurrent = new Button(_("Use the _current size"));
        final Button apply = new Button(Stock.APPLY);

        // Restore window size status
        final CheckButton customSize = new CheckButton(_("_Use a custom size."));
        customSize.setActive(config.CUSTOM_WINDOW_SIZE);
        sizeBox.packStart(customSize, false, false, 0);
        customSize.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                boolean active = customSize.getActive();

                // Enable the needed widgets
                width.setSensitive(active);
                height.setSensitive(active);
                useCurrent.setSensitive(active);
                apply.setSensitive(active);

                // Save preferences
                config.CUSTOM_WINDOW_SIZE = active;
                config.savePreferences();
            }
        });

        // Pack spin buttons in a box
        final HBox firstLine = new HBox(false, 3);
        sizeBox.packStart(firstLine, false, false, 0);

        // Width value
        width.setSensitive(config.CUSTOM_WINDOW_SIZE);
        width.setValue(config.WINDOW_SIZE_X);
        firstLine.packStart(width, true, true, 0);
        width.connect(new SpinButton.ValueChanged() {
            @Override
            public void onValueChanged(SpinButton source) {
                // Save preferences
                config.WINDOW_SIZE_X = (int) source.getValue();
                config.savePreferences();
            }
        });

        // Just for design
        firstLine.packStart(new Label("x"), true, true, 0);

        // Height value
        height.setSensitive(config.CUSTOM_WINDOW_SIZE);
        height.setValue(config.WINDOW_SIZE_Y);
        firstLine.packStart(height, true, true, 0);
        height.connect(new SpinButton.ValueChanged() {
            @Override
            public void onValueChanged(SpinButton source) {
                // Save preferences
                config.WINDOW_SIZE_Y = (int) source.getValue();
                config.savePreferences();
            }
        });

        // Pack buttons in a box
        final HBox secondLine = new HBox(false, 3);
        sizeBox.packStart(secondLine, false, false, 0);

        // Button to use the current size of the window
        useCurrent.setSensitive(config.CUSTOM_WINDOW_SIZE);
        secondLine.packStart(useCurrent, false, false, 0);
        useCurrent.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                double x = (double) app.getMainWindow().getWidth();
                double y = (double) app.getMainWindow().getHeight();

                // Update the widgets
                width.setValue(x);
                height.setValue(y);
            }
        });

        // Button to apply the defined size
        apply.setSensitive(config.CUSTOM_WINDOW_SIZE);
        secondLine.packStart(apply, false, false, 0);
        apply.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Get the size
                int width = config.WINDOW_SIZE_X;
                int height = config.WINDOW_SIZE_Y;

                // Resize the window
                app.getMainWindow().resize(width, height);
            }
        });

        // Make the buttons the same size
        final SizeGroup spins = new SizeGroup(SizeGroupMode.HORIZONTAL);
        spins.add(width);
        spins.add(height);

        // Make the other buttons the same size
        final SizeGroup buttons = new SizeGroup(SizeGroupMode.HORIZONTAL);
        buttons.add(useCurrent);
        buttons.add(apply);

        // Show all widgets
        page.showAll();

        return page;
    }

    /**
     * Create the second page of the dialog (split config).
     */
    private VBox createSplitPage() {
        final VBox page = new VBox(false, 18);
        page.setBorderWidth(12);

        // First option
        final VBox first = new VBox(false, 6);
        page.packStart(first, false, false, 0);

        // Add the label
        first.packStart(this.createSectionLabel(_("During a split")), false, false, 0);

        // Add the row of option
        final HBox firstRow = new HBox(false, 0);
        first.packStart(firstRow, false, false, 0);

        // Add an empty label
        firstRow.packStart(this.createEmptyLabel(), false, false, 0);

        final CheckButton md5sum = new CheckButton(_("_Calculate the MD5 sum if possible."));
        md5sum.setActive(config.SAVE_FILE_HASH);
        firstRow.packStart(md5sum, false, false, 0);
        md5sum.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.SAVE_FILE_HASH = md5sum.getActive();
                config.savePreferences();
            }
        });

        // Second option
        final VBox second = new VBox(false, 6);
        page.packStart(second, false, false, 0);

        // Add the label
        second.packStart(this.createSectionLabel(_("Default algorithm")), false, false, 0);

        // Add the row of option
        final HBox secondRow = new HBox(false, 0);
        second.packStart(secondRow, false, false, 0);

        // Add an empty label
        secondRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Algorithm list
        final TextComboBox algorithms = new TextComboBox();
        secondRow.packStart(algorithms, false, false, 0);
        for (String algorithm : Algorithm.toStrings()) {
            // Fill the list
            algorithms.appendText(algorithm);
        }

        // Set the default algorithm
        algorithms.setActive(config.DEFAULT_ALGORITHM);
        algorithms.connect(new ComboBox.Changed() {
            @Override
            public void onChanged(ComboBox source) {
                // Save preferences
                config.DEFAULT_ALGORITHM = algorithms.getActive();
                config.savePreferences();
            }
        });

        // Third option
        final VBox third = new VBox(false, 6);
        page.packStart(third, false, false, 0);

        // Add the label
        third.packStart(this.createSectionLabel(_("Default directory")), false, false, 0);

        // Add the row of option
        final HBox thirdRow = new HBox(false, 0);
        third.packStart(thirdRow, false, false, 0);

        // Add an empty label
        thirdRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Default directory button
        splitDirChooser = new FileChooserButton(_("Choose a directory."),
                FileChooserAction.SELECT_FOLDER);
        splitDirChooser.setCurrentFolder(config.SPLIT_DIRECTORY);
        thirdRow.packStart(splitDirChooser, false, false, 0);

        // Size group for option widgets
        SizeGroup optionGroup = new SizeGroup(SizeGroupMode.BOTH);
        optionGroup.add(algorithms);
        optionGroup.add(splitDirChooser);

        // Show all widgets
        page.showAll();

        return page;
    }

    /**
     * Create the third page of the dialog (merge config).
     */
    private VBox createMergePage() {
        final VBox page = new VBox(false, 18);
        page.setBorderWidth(12);

        // First option
        final VBox first = new VBox(false, 6);
        page.packStart(first, false, false, 0);

        // Add the label
        first.packStart(this.createSectionLabel(_("During a merge")), false, false, 0);

        // Add the row of options
        final HBox firstRow = new HBox(false, 0);
        first.packStart(firstRow, false, false, 0);

        // Add an empty label
        firstRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Box for check buttons
        final VBox button = new VBox(false, 6);
        firstRow.packStart(button, false, false, 0);

        // Depends on the next option
        final CheckButton remove = new CheckButton(_("_Remove the chunks."));

        // Restore check hash file status
        final CheckButton check = new CheckButton(_("_Check the MD5 sum if possible."));
        check.setActive(config.CHECK_FILE_HASH);
        button.packStart(check, false, false, 0);
        check.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                boolean active = check.getActive();

                // Set the sensitivity of the remove chunks option
                remove.setSensitive(active);

                // Save preferences
                config.CHECK_FILE_HASH = check.getActive();
                config.savePreferences();
            }
        });

        // Second options
        final VBox second = new VBox(false, 6);
        page.packStart(second, false, false, 0);

        // Add the label
        second.packStart(this.createSectionLabel(_("After a merge")), false, false, 0);

        // Add the row of options
        final HBox secondRow = new HBox(false, 0);
        second.packStart(secondRow, false, false, 0);

        // Add an empty label
        secondRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Box for check buttons
        final VBox buttons = new VBox(false, 6);
        secondRow.packStart(buttons, false, false, 0);

        // Restore remove parts status
        remove.setActive(config.DELETE_PARTS);
        remove.setSensitive(config.CHECK_FILE_HASH);
        buttons.packStart(remove, false, false, 0);
        remove.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.DELETE_PARTS = remove.getActive();
                config.savePreferences();
            }
        });

        // Restore open file status
        final CheckButton open = new CheckButton(_("_Open the created file."));
        open.setActive(config.OPEN_FILE_AT_END);
        buttons.packStart(open, false, false, 0);
        open.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.OPEN_FILE_AT_END = open.getActive();
                config.savePreferences();
            }
        });

        // Third option
        final VBox third = new VBox(false, 6);
        page.packStart(third, false, false, 0);

        // Add the label
        third.packStart(this.createSectionLabel(_("Default directory")), false, false, 0);

        // Add the row of option
        final HBox thirdRow = new HBox(false, 0);
        third.packStart(thirdRow, false, false, 0);

        // Add an empty label
        thirdRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Default directory button
        mergeDirChooser = new FileChooserButton(_("Choose a directory."),
                FileChooserAction.SELECT_FOLDER);
        mergeDirChooser.setCurrentFolder(config.MERGE_DIRECTORY);
        thirdRow.packStart(mergeDirChooser, false, false, 0);

        // Show all widgets
        page.showAll();

        return page;
    }

    /**
     * Create the last page of the dialog (desktop config).
     */
    private VBox createDesktopPage() {
        final VBox page = new VBox(false, 18);
        page.setBorderWidth(12);

        // First option
        final VBox first = new VBox(false, 6);
        page.packStart(first, false, false, 0);

        // Add the label
        first.packStart(this.createSectionLabel(_("Power management")), false, false, 0);

        // Add the row of options
        final HBox firstRow = new HBox(false, 0);
        first.packStart(firstRow, false, false, 0);

        // Add an empty label
        firstRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Restore hibernation status
        final CheckButton hibernation = new CheckButton(
                _("Inhibit desktop _hibernation when an action is performed."));
        hibernation.setActive(config.NO_HIBERNATION);
        firstRow.packStart(hibernation, false, false, 0);
        hibernation.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.NO_HIBERNATION = hibernation.getActive();
                config.savePreferences();
            }
        });

        // Second options
        final VBox second = new VBox(false, 6);
        page.packStart(second, false, false, 0);

        // Add the label
        second.packStart(this.createSectionLabel(_("Notification")), false, false, 0);

        // Add the row of options
        final HBox secondRow = new HBox(false, 0);
        second.packStart(secondRow, false, false, 0);

        // Add an empty label
        secondRow.packStart(this.createEmptyLabel(), false, false, 0);

        // Box to pack buttons
        final VBox checks = new VBox(false, 6);
        secondRow.packStart(checks, false, false, 0);

        // Restore tray icon status
        final CheckButton statusIcon = new CheckButton(_("Show _icon in the desktop notification area."));
        statusIcon.setActive(config.SHOW_STATUS_ICON);
        checks.packStart(statusIcon, false, false, 0);
        statusIcon.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                boolean showStatusIcon = statusIcon.getActive();
                config.SHOW_STATUS_ICON = showStatusIcon;

                // Display icon and save preferences
                app.getMainWindow().getAreaStatusIcon().setVisible(showStatusIcon);
                config.savePreferences();
            }
        });

        // Restore notifications status
        final CheckButton notification = new CheckButton(_("Show desktop _notification."));
        notification.setActive(config.USE_NOTIFICATION);
        checks.packStart(notification, false, false, 0);
        notification.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.USE_NOTIFICATION = notification.getActive();
                config.savePreferences();

                if (config.USE_NOTIFICATION) {
                    // Load libnotify
                    Notify.init(Constants.PROGRAM_NAME);
                } else {
                    // Unload libnotify
                    Notify.uninit();
                }
            }
        });

        // Show all widgets
        page.showAll();

        return page;
    }

    @Override
    public void present() {
        this.show();
        super.present();
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.CLOSE);
        return false;
    }

    @Override
    public void onResponse(Dialog source, ResponseType response) {
        // Save preferences that cannot be saved using signals
        config.SPLIT_DIRECTORY = splitDirChooser.getCurrentFolder();
        config.MERGE_DIRECTORY = mergeDirChooser.getCurrentFolder();
        config.savePreferences();

        // Hide the dialog
        this.hide();
    }
}
