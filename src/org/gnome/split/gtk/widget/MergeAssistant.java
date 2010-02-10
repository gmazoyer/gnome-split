/*
 * MergeAssistant.java
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
import org.gnome.gtk.Editable;
import org.gnome.gtk.Entry;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.Gtk;
import org.gnome.gtk.HBox;
import org.gnome.gtk.IconSize;
import org.gnome.gtk.Justification;
import org.gnome.gtk.Label;
import org.gnome.gtk.Stock;
import org.gnome.gtk.Widget;
import org.gnome.gtk.WindowPosition;
import org.gnome.gtk.Assistant.Apply;
import org.gnome.gtk.Assistant.Cancel;
import org.gnome.gtk.Assistant.Close;
import org.gnome.gtk.Assistant.Prepare;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.action.ActionManager.ActionId;

import static org.freedesktop.bindings.Internationalization._;

/**
 * This assistant is used to help the user to create a merge.
 * 
 * @author Guillaume Mazoyer
 */
public class MergeAssistant extends Assistant implements ActionAssistant, Prepare, Close, Cancel, Apply
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
     * Labels to update.
     */
    private Label label;

    /**
     * The name of the first file to merge.
     */
    private String filename;

    /**
     * To know if we should start the action.
     */
    private boolean start;

    public MergeAssistant(final GnomeSplit app) {
        super();

        // Save the instance
        this.app = app;

        // Set this assistant in the center of the screen
        this.setPosition(WindowPosition.CENTER);

        // Set the title
        this.setTitle(_("Merge assistant"));

        // Setup the default values
        this.label = new Label();
        this.filename = null;
        this.start = false;

        // Create a logo for the assistant
        logo = Gtk.renderIcon(this, Stock.PASTE, IconSize.DIALOG);
        // Setup the introduction
        this.createIntroduction();

        // Setup the file selection
        this.createFileSelection();

        // Setup the confirmation
        this.createSummary();

        // Connect signal handlers
        this.connect((Prepare) this);
        this.connect((Close) this);
        this.connect((Cancel) this);
        this.connect((Apply) this);
    }

    /**
     * Create a page to select the first file to merge.
     */
    private void createFileSelection() {
        final Page page = new Page();

        // The text to display
        final String data = _("Here, you have to select the first file to merge. After that, you can go to the\nnext step of this assistant. The file that you will choose should be displayed\nusing its full path in the text entry.");

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
        final Label label = new Label(_("File to merge:"));
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
     * Create a page to sum up all info and validate the merge.
     */
    private void createSummary() {
        final Page page = new Page();

        // The text to display
        final String data = _("The first file to merge is now selected. But you can still check that you pick\nup the right file. If it is not the first file to merge, go back to the\nprevious step to choose another one.");

        // Create the label
        final Label text = new Label();
        text.setLabel(data);
        text.setUseMarkup(true);
        text.setJustify(Justification.LEFT);

        // Add the label
        page.container.packStart(text, false, false, 0);

        // Create a box to show the directory information
        final HBox dirBox = new HBox(false, 3);
        page.container.packStart(dirBox, false, false, 0);

        // Create a box to show the file information
        final HBox fileBox = new HBox(false, 3);
        page.container.packStart(fileBox, false, false, 0);

        // Add a label
        final Label fileLabel = new Label(_("Filename:"));
        fileBox.packStart(fileLabel, false, false, 0);

        // Add another label but empty
        fileBox.packStart(label, false, false, 0);

        // Last label and last question
        final Label last = new Label(
                _("Just a last question before you confirm the merge to do.\nIf you check the following box, the merge will start after your confirmation."));
        last.setJustify(Justification.LEFT);
        page.container.packStart(last, false, false, 0);

        // Last question
        final CheckButton startCheck = new CheckButton(_("Start the merge after the confirmation."));
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
        final String data = _("This assistant will help you to create a <b>merge</b>.\n\nActually, merging several files is easy. You just have to select the first file\nto merge and GNOME Split will do the everything else for you.\n\nSo the first and only thing that we will need is to select the first file to merge.\n\nYou can also modify the merge configuration to define in details how you want\nto have the merge done by taking a look at <i>Edit > Preferences > Merge</i>.");

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
        // Update the widget using the filename
        ((MergeWidget) app.getMainWindow().getActionWidget()).setFirstFile(filename);
    }

    @Override
    public void onPrepare(Assistant source, Widget page) {
        // Prepare the last page
        if (source.getCurrentPage() == 2) {
            // Setup the label value
            label.setLabel(new File(filename).getName());

            // Switch the view if needed
            app.getMainWindow().getViewSwitcher().switchToMerge();
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
