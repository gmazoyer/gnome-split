/*
 * MergeAssistant.java
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
import static org.gnome.split.GnomeSplit.ui;

import java.io.File;

import org.gnome.gtk.Assistant;
import org.gnome.gtk.AssistantPageType;
import org.gnome.gtk.FileChooserAction;
import org.gnome.gtk.FileChooserButton;
import org.gnome.gtk.FileFilter;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.split.config.Constants;
import org.gnome.split.gtk.action.ActionManager.ActionId;

/**
 * This assistant is used to help the user to create a merge action.
 * 
 * @author Guillaume Mazoyer
 */
public class MergeAssistant extends BasicAssistant
{
    /**
     * Final page of the assistant.
     */
    private FinalPage conclusion;

    /**
     * The name of the file to split.
     */
    private String filename;

    public MergeAssistant() {
        super(_("Merge assistant"));

        // Set the default values
        this.filename = null;

        // Add introduction
        this.createIntroduction();

        // Add conclusion
        this.createConclusion();
    }

    @Override
    protected void createIntroduction() {
        final VBox page = createPage();

        // The text to display
        final String data = _("Select the first file to merge.");

        // Add the label
        page.packStart(createLeftAlignedLabel(data), false, false, 0);

        // File filter to help choosing a valid chunk
        final FileFilter chunks = new FileFilter(_("Valid chunks"));
        chunks.addPattern("*.001.gsp");
        chunks.addPattern("*.001.xtm");
        chunks.addPattern("*.000");
        chunks.addPattern("*.001");

        // Add a chooser button to it
        final FileChooserButton button = new FileChooserButton(_("Select a file."),
                FileChooserAction.OPEN);
        button.addFilter(chunks);
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
        this.setPageType(page, AssistantPageType.CONTENT);
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
        // Switch to the merge view if needed
        actions.activateRadioAction(ActionId.MERGE);

        // Update the widget using the filename
        ui.getMergeWidget().setFile(filename);
    }

    @Override
    public void onPrepare(Assistant source, Widget widget) {
        if (source.getCurrentPage() == 1) {
            conclusion.setFields(new String[] {
                _("First file to merge:")
            }, new String[] {
                new File(filename).getName()
            });
        }
    }
}
