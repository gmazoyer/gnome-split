/*
 * PreferencesDialog.java
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
package org.gnome.split.gtk.dialog;

import org.gnome.gdk.Event;
import org.gnome.gdk.Pixbuf;
import org.gnome.gtk.Alignment;
import org.gnome.gtk.Button;
import org.gnome.gtk.ButtonBoxStyle;
import org.gnome.gtk.CheckButton;
import org.gnome.gtk.ComboBox;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnPixbuf;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.Frame;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.IconView;
import org.gnome.gtk.Label;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.RadioButton;
import org.gnome.gtk.RadioButtonGroup;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.Stock;
import org.gnome.gtk.TextComboBox;
import org.gnome.gtk.ToggleButton;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreePath;
import org.gnome.gtk.VBox;
import org.gnome.gtk.VButtonBox;
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

public class PreferencesDialog extends Dialog implements DeleteEvent, Response
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
     * The main container of the dialog.
     */
    private VBox container;

    /**
     * All pages that can be displayed.
     */
    private Alignment[] pages;

    /**
     * The current displayed page.
     */
    private Alignment current;

    public PreferencesDialog(final GnomeSplit app) {
        super(_("GNOME Split Preferences"), app.getMainWindow(), false);

        // Get configuration
        this.config = app.getConfig();
        this.app = app;

        // Main container
        container = new VBox(false, 3);
        this.add(container);

        // Add the icon view
        container.packStart(this.createIconView(), false, false, 0);

        // Pages available
        pages = new Alignment[] {
                this.createGeneralPage(),
                this.createSplitPage(),
                this.createMergePage(),
                this.createDesktopPage()
        };

        // Make all pages the same size
        SizeGroup group = new SizeGroup(SizeGroupMode.BOTH);
        for (Alignment page : pages) {
            group.add(page);
        }

        // Display the first page
        this.switchTo(0);

        // Close button (save the configuration and close)
        this.addButton(Stock.CLOSE, ResponseType.CLOSE);

        // Connect classic signals
        this.connect((Window.DeleteEvent) this);
        this.connect((Dialog.Response) this);
    }

    /**
     * Create a {@link IconView} and pack it in a {@link Frame}.
     */
    private Frame createIconView() {
        // Widget which will contain the view
        final Frame container = new Frame(null);

        // Create the icon view
        final IconView view = new IconView();
        container.add(view);

        // Create the needed columns
        final DataColumnPixbuf icon = new DataColumnPixbuf();
        final DataColumnString text = new DataColumnString();

        // Create the model and use it
        final ListStore store = new ListStore(new DataColumn[] {
                icon, text
        });
        view.setModel(store);

        // Set up the columns for the icon view
        view.setPixbufColumn(icon);
        view.setTextColumn(text);
        view.setColumns(4);

        TreeIter row;
        Pixbuf pixbuf;

        // General icon
        row = store.appendRow();
        pixbuf = Gtk.renderIcon(view, Stock.PREFERENCES, IconSize.LARGE_TOOLBAR);
        store.setValue(row, icon, pixbuf);
        store.setValue(row, text, _("General"));

        // Split icon
        row = store.appendRow();
        pixbuf = Gtk.renderIcon(view, Stock.CUT, IconSize.LARGE_TOOLBAR);
        store.setValue(row, icon, pixbuf);
        store.setValue(row, text, _("Split"));

        // Merge icon
        row = store.appendRow();
        pixbuf = Gtk.renderIcon(view, Stock.PASTE, IconSize.LARGE_TOOLBAR);
        store.setValue(row, icon, pixbuf);
        store.setValue(row, text, _("Merge"));

        // Desktop icon
        row = store.appendRow();
        pixbuf = Gtk.renderIcon(view, Stock.FULLSCREEN, IconSize.LARGE_TOOLBAR);
        store.setValue(row, icon, pixbuf);
        store.setValue(row, text, _("Desktop"));

        // Connect the signal to handle change of page
        view.connect(new IconView.SelectionChanged() {
            @Override
            public void onSelectionChanged(IconView source) {
                TreePath[] selections = source.getSelectedItems();
                if ((selections != null) && (selections.length > 0)) {
                    // Get the page ID and change the page
                    int id = selections[0].getIndices()[0];
                    switchTo(id);
                }
            }
        });

        // And finally
        return container;
    }

    /**
     * Create the first page of the dialog (general config).
     */
    private Alignment createGeneralPage() {
        final Alignment page = new Alignment(0.0f, 0.0f, 0.0f, 0.0f);
        page.setPadding(5, 5, 20, 5);

        // Buttons group for default view choice
        final RadioButtonGroup group = new RadioButtonGroup();

        // Restore default view status
        final Label viewLabel = new Label(_("Default view:"));

        final RadioButton split = new RadioButton(group, _("Split"));
        split.setActive(config.DEFAULT_VIEW == 0);
        split.setSensitive(false);
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

        final RadioButton merge = new RadioButton(group, _("Merge"));
        merge.setActive(config.DEFAULT_VIEW == 1);
        merge.setSensitive(false);
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

        // Restore multiple instances status
        final CheckButton instances = new CheckButton(_("_Allow multiple instances."));
        instances.setActive(config.MULTIPLE_INSTANCES);
        instances.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.MULTIPLE_INSTANCES = instances.getActive();
                config.savePreferences();
            }
        });

        // Main container
        final VBox container = new VBox(false, 5);
        page.add(container);

        // Pack default view widgets
        final HBox hbox = new HBox(false, 3);
        container.packStart(hbox);

        // Pack the widgets
        hbox.packStart(viewLabel);
        hbox.packStart(split);
        hbox.packStart(merge);

        // And pack the multiple instances option
        container.packStart(instances);

        // Show all widgets
        page.showAll();

        return page;
    }

    /**
     * Create the second page of the dialog (split config).
     */
    private Alignment createSplitPage() {
        final Alignment page = new Alignment(0.0f, 0.0f, 0.0f, 0.0f);
        page.setPadding(5, 5, 20, 5);

        final CheckButton md5sum = new CheckButton(_("_Calculate the MD5 if possible."));
        md5sum.setActive(config.SAVE_FILE_HASH);
        md5sum.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.SAVE_FILE_HASH = md5sum.getActive();
                config.savePreferences();
            }
        });

        // Algorithm label
        final Label algoLabel = new Label(_("Default algorithm:"));

        // Algorithm list
        final TextComboBox algorithms = new TextComboBox();
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

        // Default directory label
        final Label directoryLabel = new Label(_("Default directory:"));

        // Default directory button
        final FileChooserButton directoryChooser = new FileChooserButton(_("Choose a directory."),
                FileChooserAction.SELECT_FOLDER);
        directoryChooser.setCurrentFolder(config.SPLIT_DIRECTORY);
        directoryChooser.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                // Save preferences
                config.SPLIT_DIRECTORY = new String(source.getCurrentFolder());
                config.savePreferences();
            }
        });

        // Main container
        final VBox container = new VBox(false, 5);
        page.add(container);

        // Add MD5 sum option
        container.packStart(md5sum);

        // Algorithm container
        final HBox algoContainer = new HBox(false, 3);
        container.packStart(algoContainer);

        // Pack algorithm related widgets
        algoContainer.packStart(algoLabel);
        algoContainer.packStart(algorithms);

        // Directory container
        final HBox directoryContainer = new HBox(false, 3);
        container.packStart(directoryContainer);

        // Pack directory related widgets
        directoryContainer.packStart(directoryLabel);
        directoryContainer.packStart(directoryChooser);

        // Size group for labels
        SizeGroup labelGroup = new SizeGroup(SizeGroupMode.BOTH);
        labelGroup.add(algoLabel);
        labelGroup.add(directoryLabel);

        // Size group for option widgets
        SizeGroup optionGroup = new SizeGroup(SizeGroupMode.BOTH);
        optionGroup.add(algorithms);
        optionGroup.add(directoryChooser);

        // Show all widgets
        page.showAll();

        return page;
    }

    /**
     * Create the third page of the dialog (merge config).
     */
    private Alignment createMergePage() {
        final Alignment page = new Alignment(0.0f, 0.0f, 0.0f, 0.0f);
        page.setPadding(5, 5, 20, 5);

        // Restore remove parts status
        final CheckButton remove = new CheckButton(_("_Remove parts if the merge was successful."));
        remove.setActive(config.DELETE_PARTS);
        remove.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.DELETE_PARTS = remove.getActive();
                config.savePreferences();
            }
        });

        // Restore open file status
        final CheckButton open = new CheckButton(
                _("_Open the created file if the merge was successful."));
        open.setActive(config.OPEN_FILE_AT_END);
        open.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.OPEN_FILE_AT_END = open.getActive();
                config.savePreferences();
            }
        });

        // Default directory label
        final Label directoryLabel = new Label(_("Default directory:"));

        // Default directory button
        final FileChooserButton directoryChooser = new FileChooserButton(_("Choose a directory."),
                FileChooserAction.SELECT_FOLDER);
        directoryChooser.setCurrentFolder(config.MERGE_DIRECTORY);
        directoryChooser.connect(new FileChooserButton.FileSet() {
            @Override
            public void onFileSet(FileChooserButton source) {
                // Save preferences
                config.MERGE_DIRECTORY = new String(source.getCurrentFolder());
                config.savePreferences();
            }
        });

        // Main container
        final VBox container = new VBox(false, 5);
        page.add(container);

        // Container to add check buttons
        final VButtonBox buttons = new VButtonBox();
        buttons.setSpacing(5);
        buttons.setLayout(ButtonBoxStyle.SPREAD);
        container.packStart(buttons);

        // Add the buttons
        buttons.packStart(remove);
        buttons.packStart(open);

        // Directory container
        final HBox directoryContainer = new HBox(false, 3);
        container.packStart(directoryContainer);

        // Pack directory related widgets
        directoryContainer.packStart(directoryLabel);
        directoryContainer.packStart(directoryChooser);

        // Show all widgets
        page.showAll();

        return page;
    }

    /**
     * Create the last page of the dialog (desktop config).
     */
    private Alignment createDesktopPage() {
        final Alignment page = new Alignment(0.0f, 0.0f, 0.0f, 0.0f);
        page.setPadding(5, 5, 20, 5);

        // Restore hibernation status
        final CheckButton hibernation = new CheckButton(
                _("Inhibit desktop _hibernation when action is performed."));
        hibernation.setActive(config.NO_HIBERNATION);
        hibernation.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                // Save preferences
                config.NO_HIBERNATION = hibernation.getActive();
                config.savePreferences();
            }
        });

        // Restore tray icon status
        final CheckButton trayIcon = new CheckButton(_("Show _icon in the desktop notification area."));
        trayIcon.setActive(config.SHOW_TRAY_ICON);
        trayIcon.connect(new Button.Clicked() {
            @Override
            public void onClicked(Button source) {
                boolean showTrayIcon = trayIcon.getActive();
                config.SHOW_TRAY_ICON = showTrayIcon;

                // Display icon and save preferences
                app.getMainWindow().getTrayIcon().setVisible(showTrayIcon);
                config.savePreferences();
            }
        });

        // Restore notifications status
        final CheckButton notification = new CheckButton(_("Show desktop _notification."));
        notification.setActive(config.USE_NOTIFICATION);
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

        // Pack buttons in the box
        final VButtonBox vbox = new VButtonBox();
        page.add(vbox);

        // Add every options
        vbox.add(hibernation);
        vbox.add(trayIcon);
        vbox.add(notification);

        // Show all widgets
        page.showAll();

        return page;
    }

    /**
     * Switch the displayed page of the dialog using its ID.
     */
    private void switchTo(int page) {
        // First display
        if (current == null) {
            // Use the first page
            current = pages[0];
        } else {
            // Remove the current page
            container.remove(current);

            // Update the current page with the requested one
            current = pages[page];
        }

        // Display the new page
        container.packStart(current);
    }

    @Override
    public ResponseType run() {
        this.showAll();
        return super.run();
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.CLOSE);
        return false;
    }

    @Override
    public void onResponse(Dialog source, ResponseType response) {
        // Hide the dialog
        this.hide();
    }
}
