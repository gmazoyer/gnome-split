/*
 * MainWindow.java
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
package org.gnome.split.gtk;

import org.gnome.gdk.Cursor;
import org.gnome.gdk.Event;
import org.gnome.gtk.AcceleratorGroup;
import org.gnome.gtk.Frame;
import org.gnome.gtk.HSeparator;
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.SeparatorMenuItem;
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
import org.gnome.split.gtk.dialog.MinimizeDialog;
import org.gnome.split.gtk.widget.ActionWidget;
import org.gnome.split.gtk.widget.AreaStatusIcon;
import org.gnome.split.gtk.widget.InfoWidget;
import org.gnome.split.gtk.widget.MainToolbar;
import org.gnome.split.gtk.widget.MergeWidget;
import org.gnome.split.gtk.widget.ProgressWidget;
import org.gnome.split.gtk.widget.SelectView;
import org.gnome.split.gtk.widget.SplitWidget;
import org.gnome.split.gtk.widget.StatusWidget;

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
     * Group of accelerators for actions.
     */
    private AcceleratorGroup accelerators;

    /**
     * Icon in the notification area associated to this window.
     */
    private AreaStatusIcon statusIcon;

    /**
     * Toolbar.
     */
    private MainToolbar toolbar;

    /**
     * Views selector.
     */
    private SelectView views;

    /**
     * Main container of the {@link Window}.
     */
    private VBox mainContainer;

    /**
     * Separators just to perfect the interface.
     */
    private HSeparator[] separators;

    /**
     * Widget to display when the split view is selected.
     */
    private SplitWidget split;

    /**
     * Widget to display when the merge view is selected.
     */
    private MergeWidget merge;

    /**
     * Widget to display the progress.
     */
    private ProgressWidget progress;

    /**
     * Widget derived from {@link Frame} to display the status.
     */
    private StatusWidget status;

    /**
     * Widget to display events instead of showing a dialog.
     */
    private InfoWidget info;

    /**
     * Build the main window of GNOME Split.
     */
    public MainWindow(final GnomeSplit app) {
        super();

        // Save program instance
        this.app = app;

        // Create the accelerators group
        this.accelerators = app.getActionManager().getAccelerators();
        this.addAcceleratorGroup(this.accelerators);

        // Create the separators array
        this.separators = new HSeparator[2];

        // Place the window in the middle of the screen
        this.setPosition(WindowPosition.CENTER);

        // Create the notification zone icon
        this.statusIcon = new AreaStatusIcon(app);
        this.statusIcon.setVisible(app.getConfig().SHOW_STATUS_ICON);

        // Main container
        this.mainContainer = new VBox(false, 0);
        this.mainContainer.show();
        this.add(this.mainContainer);

        // Add the menu bar
        final MenuBar menubar = this.createMenu();
        menubar.showAll();
        this.mainContainer.packStart(menubar, false, false, 0);

        // Add the info bar
        this.info = new InfoWidget(app);
        this.mainContainer.packStart(info, false, false, 0);

        // Add the tool bar
        this.toolbar = new MainToolbar(app);
        this.mainContainer.packStart(this.toolbar, false, false, 0);

        // Show the toolbar if needed
        if (app.getConfig().SHOW_TOOLBAR) {
            this.toolbar.showAll();
        }

        // Add the views selector
        this.views = new SelectView(app);
        this.mainContainer.packStart(views, false, false, 0);

        // Add a separator
        this.separators[0] = new HSeparator();
        mainContainer.packStart(this.separators[0], false, false, 0);

        // Show the switcher if need
        if (app.getConfig().SHOW_SWITCHER) {
            this.views.showAll();
            this.separators[0].show();
        }

        // Create the two main widgets
        this.split = new SplitWidget(app);
        this.merge = new MergeWidget(app);

        // Make sure they have the same size
        final SizeGroup group = new SizeGroup(SizeGroupMode.BOTH);
        group.add(this.split);
        group.add(this.merge);

        // Add the main widgets
        this.mainContainer.packStart(this.split, true, true, 0);
        this.mainContainer.packStart(this.merge, true, true, 0);

        // Show the split widget first
        this.split.setVisible(true);

        // Show the progress bar
        this.progress = new ProgressWidget();
        this.progress.show();
        this.mainContainer.packStart(this.progress, false, false, 0);

        // Add a separator
        this.separators[1] = new HSeparator();
        mainContainer.packStart(this.separators[1], false, false, 0);

        // Add status widget
        this.status = new StatusWidget();
        this.mainContainer.packStart(this.status, false, false, 0);

        // Show the status widget if needed
        if (app.getConfig().SHOW_STATUSBAR) {
            this.status.showAll();
            this.separators[1].show();
        }

        // Connect delete event handler
        this.connect((Window.DeleteEvent) this);

        // Restore the window size
        if (app.getConfig().CUSTOM_WINDOW_SIZE) {
            this.setDefaultSize(app.getConfig().WINDOW_SIZE_X, app.getConfig().WINDOW_SIZE_Y);
        }
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
        fileMenu.setAcceleratorGroup(accelerators);

        // Create menu items
        MenuItem[] items;

        // Create menu items
        items = new MenuItem[8];
        items[0] = actions.getAction(ActionId.ASSISTANT).createMenuItem();
        items[1] = actions.getAction(ActionId.OPEN_DIR).createMenuItem();
        items[2] = actions.getAction(ActionId.START).createMenuItem();
        items[3] = actions.getAction(ActionId.PAUSE).createMenuItem();
        items[4] = actions.getAction(ActionId.CANCEL).createMenuItem();
        items[5] = actions.getAction(ActionId.DELETE).createMenuItem();
        items[6] = actions.getAction(ActionId.EXIT).createMenuItem();

        // Add menu items to the menu
        fileItem.setSubmenu(fileMenu);
        fileMenu.append(items[0]);
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(items[1]);
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(items[2]);
        fileMenu.append(items[3]);
        fileMenu.append(items[4]);
        fileMenu.append(items[5]);
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(items[6]);
        menubar.append(fileItem);

        // Edit menu item
        final MenuItem editItem = new MenuItem(_("_Edit"));
        final Menu editMenu = new Menu();
        editMenu.setAcceleratorGroup(accelerators);

        // Create menu items
        items = new MenuItem[1];
        items[0] = actions.getAction(ActionId.PREFERENCES).createMenuItem();

        // Add menu items to the menu
        editItem.setSubmenu(editMenu);
        editMenu.append(items[0]);
        menubar.append(editItem);

        // View menu item
        final MenuItem viewItem = new MenuItem(_("_View"));
        final Menu viewMenu = new Menu();
        viewMenu.setAcceleratorGroup(accelerators);

        // Create menu items
        items = new MenuItem[6];
        items[0] = actions.getAction(ActionId.CLEAR).createMenuItem();
        items[1] = actions.getToggleAction(ActionId.TOOLBAR).createMenuItem();
        items[2] = actions.getToggleAction(ActionId.SWITCHER).createMenuItem();
        items[3] = actions.getToggleAction(ActionId.STATUS).createMenuItem();
        items[4] = actions.getRadioAction(ActionId.SPLIT).createMenuItem();
        items[5] = actions.getRadioAction(ActionId.MERGE).createMenuItem();

        // Add menu items to the menu
        viewItem.setSubmenu(viewMenu);
        viewMenu.append(items[0]);
        viewMenu.append(new SeparatorMenuItem());
        viewMenu.append(items[1]);
        viewMenu.append(items[2]);
        viewMenu.append(items[3]);
        viewMenu.append(new SeparatorMenuItem());
        viewMenu.append(items[4]);
        viewMenu.append(items[5]);
        menubar.append(viewItem);

        // Help menu item
        final MenuItem helpItem = new MenuItem(_("_Help"));
        final Menu helpMenu = new Menu();
        helpMenu.setAcceleratorGroup(accelerators);

        // Create menu items
        items = new MenuItem[5];
        items[0] = actions.getAction(ActionId.HELP).createMenuItem();
        items[1] = actions.getAction(ActionId.ONLINE_HELP).createMenuItem();
        items[2] = actions.getAction(ActionId.TRANSLATE).createMenuItem();
        items[3] = actions.getAction(ActionId.REPORT_BUG).createMenuItem();
        items[4] = actions.getAction(ActionId.ABOUT).createMenuItem();

        // Add menu items to the menu
        helpItem.setSubmenu(helpMenu);
        helpMenu.append(items[0]);
        helpMenu.append(new SeparatorMenuItem());
        helpMenu.append(items[1]);
        helpMenu.append(items[2]);
        helpMenu.append(items[3]);
        helpMenu.append(new SeparatorMenuItem());
        helpMenu.append(items[4]);
        menubar.append(helpItem);

        return menubar;
    }

    /**
     * Select the default view of the interface.
     */
    public void selectDefaultView() {
        // Get the default view
        int view = app.getConfig().DEFAULT_VIEW;

        // The merge view is the default one
        if (view == 1) {
            // Switch the view
            app.getActionManager().getRadioAction(ActionId.MERGE).emitActivate();
        }
    }

    /**
     * Show the split widget.
     */
    public void switchToSplitView() {
        // Hide the merge widget
        merge.setVisible(false);

        // Show the split widget
        split.setVisible(true);
    }

    /**
     * Show the merge widget.
     */
    public void switchToMergeView() {
        // Hide the split widget
        split.setVisible(false);

        // Show the merge widget
        merge.setVisible(true);
    }

    /**
     * Set the state of the cursor. It can be {@link Cursor.WORKING} or
     * {@link Cursor.NORMAL}.
     */
    public void setCursorWorkingState(boolean working) {
        this.getWindow().setCursor(working ? Cursor.WORKING : Cursor.NORMAL);
    }

    /**
     * Resize the window according to the user configuration.
     */
    public void resize() {
        if (!app.getConfig().CUSTOM_WINDOW_SIZE) {
            // Use the optimal size
            this.resize(1, 1);
        } else {
            // Get the user defined size of the window
            int x = app.getConfig().WINDOW_SIZE_X;
            int y = app.getConfig().WINDOW_SIZE_Y;

            // Resize the window
            this.resize(x, y);
        }
    }

    /**
     * Get the separators of the interface.
     */
    public HSeparator[] getSeparators() {
        return separators;
    }

    /**
     * Get the toolbar of the window.
     */
    public MainToolbar getToolbar() {
        return toolbar;
    }

    /**
     * Get the select view widget.
     */
    public SelectView getViewSwitcher() {
        return views;
    }

    /**
     * Get the split widget which is used.
     */
    public SplitWidget getSplitWidget() {
        return split;
    }

    /**
     * Get the merge widget which is used.
     */
    public MergeWidget getMergeWidget() {
        return merge;
    }

    /**
     * Get the current displayed widget.
     */
    public ActionWidget getActionWidget() {
        return (split.isVisible() ? split : merge);
    }

    /**
     * Get the progress bar attached to this window.
     */
    public ProgressWidget getProgressBar() {
        return progress;
    }

    /**
     * Get the widget that displays the status.
     */
    public StatusWidget getStatusWidget() {
        return status;
    }

    /**
     * Get the widget that displays events.
     */
    public InfoWidget getInfoBar() {
        return info;
    }

    /**
     * Get the notification area icon associated to this window.
     */
    public AreaStatusIcon getAreaStatusIcon() {
        return statusIcon;
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        if (!app.getConfig().SHOW_STATUS_ICON) {
            // Close the program
            app.quit();
        } else {
            switch (app.getConfig().CLOSE_BEHAVIOR) {
            case 0:
                // Show the dialog
                final MinimizeDialog dialog = new MinimizeDialog(app);
                dialog.present();
                break;

            case 1:
                // Close the program
                app.quit();
                break;

            case 2:
                // Hide the window
                statusIcon.onActivate(statusIcon);
                break;
            }
        }

        return true;
    }
}
