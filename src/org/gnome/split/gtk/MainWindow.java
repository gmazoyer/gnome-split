/*
 * MainWindow.java
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
package org.gnome.split.gtk;

import org.gnome.gdk.Event;
import org.gnome.gtk.Frame;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.SeparatorToolItem;
import org.gnome.gtk.SizeGroup;
import org.gnome.gtk.SizeGroupMode;
import org.gnome.gtk.Toolbar;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.gtk.WindowPosition;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.action.ActionManager;
import org.gnome.split.gtk.action.ActionManager.ActionId;
import org.gnome.split.gtk.dialog.AboutSoftDialog;
import org.gnome.split.gtk.dialog.PreferencesDialog;
import org.gnome.split.gtk.widget.ActionWidget;
import org.gnome.split.gtk.widget.MergeWidget;
import org.gnome.split.gtk.widget.SelectView;
import org.gnome.split.gtk.widget.SplitWidget;
import org.gnome.split.gtk.widget.StatusWidget;
import org.gnome.split.gtk.widget.TrayIcon;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Main window of the interface. It will be used to do everything GNOME Split
 * can thanks to the {@link MenuBar}, the {@link Toolbar} or the
 * {@link ActionWidget}.
 * 
 * @author Guillaume Mazoyer
 */
public class MainWindow extends Window implements Window.DeleteEvent
{
    /**
     * Current GNOME Split instance.
     */
    private GnomeSplit app;

    /**
     * Icon in the notification area associated to this window.
     */
    private TrayIcon trayIcon;

    /**
     * Views selector.
     */
    private SelectView views;

    /**
     * Main container of the {@link Window}.
     */
    private VBox mainContainer;

    /**
     * Widget to display when the split view is selected.
     */
    private SplitWidget split;

    /**
     * Widget to display when the merge view is selected.
     */
    private MergeWidget merge;

    /**
     * Widget derived from {@link Frame} to display the status.
     */
    private StatusWidget status;

    /**
     * Classic preferences dialog associated to this window.
     */
    private PreferencesDialog preferences;

    /**
     * Classic about dialog associated to this window.
     */
    private AboutSoftDialog about;

    /**
     * Build the main window of GNOME Split.
     */
    public MainWindow(final GnomeSplit app) {
        super();

        // Save program instance
        this.app = app;

        // Place the window in the middle of the screen
        this.setPosition(WindowPosition.CENTER);

        // Create the notification zone icon
        this.trayIcon = new TrayIcon(app);
        this.trayIcon.setVisible(app.getConfig().SHOW_TRAY_ICON);

        // Create classic preferences dialog
        this.preferences = new PreferencesDialog(app);

        // Create classic about dialog
        this.about = new AboutSoftDialog();

        // Main container
        this.mainContainer = new VBox(false, 0);
        this.add(this.mainContainer);

        // Add the menu bar
        this.mainContainer.packStart(this.createMenu(), false, false, 0);

        // Add the tool bar
        this.mainContainer.packStart(this.createToolbar(), false, false, 0);

        // Add the views selector
        this.views = new SelectView(app);
        this.mainContainer.packStart(views, false, false, 0);

        // Create the two main widgets
        this.split = new SplitWidget();
        this.merge = new MergeWidget();

        // Make sure they have the same size
        final SizeGroup group = new SizeGroup(SizeGroupMode.BOTH);
        group.add(this.split);
        group.add(this.merge);

        // Add the main widget
        this.split.setVisible(true);
        this.mainContainer.packStart(this.split);

        // Add status widget
        this.status = new StatusWidget();
        this.mainContainer.packStart(this.status, false, false, 0);

        // Connect delete event handler
        this.connect((Window.DeleteEvent) this);

        // Show everything
        this.showAll();
    }

    /**
     * Create the menubar to use.
     */
    private MenuBar createMenu() {
        final MenuBar menubar = new MenuBar();
        final ActionManager actions = app.getActionManager();

        // File menu item
        final MenuItem fileItem = new MenuItem(_("_File"));
        final Menu fileMenu = new Menu();

        fileItem.setSubmenu(fileMenu);
        fileMenu.append(actions.getAction(ActionId.MENU_OPEN_DIR).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_PROPERTIES).createMenuItem());
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_START).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_PAUSE).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_CANCEL).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_DELETE).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_CLEAR).createMenuItem());
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_EXIT).createMenuItem());
        menubar.append(fileItem);

        // Edit menu item
        final MenuItem editItem = new MenuItem(_("_Edit"));
        final Menu editMenu = new Menu();

        editItem.setSubmenu(editMenu);
        editMenu.append(actions.getAction(ActionId.MENU_PREFERENCES).createMenuItem());
        menubar.append(editItem);

        // Help menu item
        final MenuItem helpItem = new MenuItem(_("_Help"));
        final Menu helpMenu = new Menu();

        helpItem.setSubmenu(helpMenu);
        helpMenu.append(actions.getAction(ActionId.MENU_HELP).createMenuItem());
        helpMenu.append(actions.getAction(ActionId.MENU_ABOUT).createMenuItem());
        menubar.append(helpItem);

        return menubar;
    }

    /**
     * Create the toolbar to use.
     */
    private Toolbar createToolbar() {
        final Toolbar toolbar = new Toolbar();
        final ActionManager actions = app.getActionManager();

        toolbar.insert(actions.getAction(ActionId.TOOL_START).createToolItem(), 0);
        toolbar.insert(actions.getAction(ActionId.TOOL_PAUSE).createToolItem(), 1);
        toolbar.insert(actions.getAction(ActionId.TOOL_CANCEL).createToolItem(), 2);
        toolbar.insert(actions.getAction(ActionId.TOOL_CLEAR).createToolItem(), 3);
        toolbar.insert(new SeparatorToolItem(), 4);
        toolbar.insert(actions.getAction(ActionId.TOOL_PROPERTIES).createToolItem(), 5);

        return toolbar;
    }

    /**
     * Switch between widget to display.
     */
    public void switchView() {
        // First we must remove the status widget
        mainContainer.remove(status);

        if (split.isVisible()) {
            // Remove the split widget
            mainContainer.remove(split);
            split.setVisible(false);

            // Add the merge widget
            mainContainer.packStart(merge);
            merge.setVisible(true);
        } else {
            // Remove the merge widget
            mainContainer.remove(merge);
            merge.setVisible(false);

            // Add the split widget
            mainContainer.packStart(split);
            split.setVisible(true);
        }

        // Finally we re-add the status widget
        mainContainer.packStart(status);
    }

    /**
     * Get the current displayed widget.
     */
    public ActionWidget getActionWidget() {
        if (split.isVisible()) {
            return split;
        } else {
            return merge;
        }
    }

    /**
     * Get the preferences dialog.
     */
    public PreferencesDialog getPreferencesDialog() {
        return preferences;
    }

    /**
     * Get the about dialog.
     */
    public AboutSoftDialog getAboutDialog() {
        return about;
    }

    /**
     * Get the tray icon associated to this window.
     */
    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        app.quit();
        return false;
    }
}
