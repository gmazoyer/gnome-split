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
import org.gnome.gtk.Menu;
import org.gnome.gtk.MenuBar;
import org.gnome.gtk.MenuItem;
import org.gnome.gtk.PolicyType;
import org.gnome.gtk.ScrolledWindow;
import org.gnome.gtk.SeparatorMenuItem;
import org.gnome.gtk.SeparatorToolItem;
import org.gnome.gtk.Toolbar;
import org.gnome.gtk.VBox;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.split.GnomeSplit;
import org.gnome.split.gtk.action.ActionManager;
import org.gnome.split.gtk.action.ActionManager.ActionId;
import org.gnome.split.gtk.dialog.AboutSoftDialog;
import org.gnome.split.gtk.dialog.PreferencesDialog;
import org.gnome.split.gtk.widget.MainList;
import org.gnome.split.gtk.widget.TrayIcon;

import static org.freedesktop.bindings.Internationalization._;

/**
 * Main window of the interface. It will be used to do everything GNOME Split
 * can thanks to the menubar, the toolbar or the treeview.
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
     * Classic preferences dialog associated to this window.
     */
    private PreferencesDialog preferences;

    /**
     * Classic about dialog associated to this window.
     */
    private AboutSoftDialog about;

    /**
     * Main treeview, main widget of this window.
     */
    private MainList mainView;

    /**
     * Build the main window of GNOME Split.
     * 
     * @param app
     *            the instance of GNOME Split.
     */
    public MainWindow(final GnomeSplit app) {
        super();

        this.app = app;

        // Create the notification zone icon
        this.trayIcon = new TrayIcon(app);
        this.trayIcon.setVisible(app.getConfig().SHOW_TRAY_ICON);

        // Create classic preferences dialog
        this.preferences = new PreferencesDialog(app);

        // Create classic about dialog
        this.about = new AboutSoftDialog();

        // Main container
        final VBox mainContainer = new VBox(false, 0);
        this.add(mainContainer);

        // Add the menu bar
        mainContainer.packStart(this.createMenu());

        // Add the tool bar
        mainContainer.add(this.createToolbar());

        // Add the main widgets (action list)
        mainContainer.add(this.createMainTreeView());

        // Connect delete event handler
        this.connect((Window.DeleteEvent) this);

        // Show everything
        this.showAll();
    }

    /**
     * Create the menubar to use.
     * 
     * @return the menubar.
     */
    private MenuBar createMenu() {
        final MenuBar menubar = new MenuBar();
        final ActionManager actions = app.getActionManager();

        // File menu item
        final MenuItem fileItem = new MenuItem(_("_File"));
        final Menu fileMenu = new Menu();

        fileItem.setSubmenu(fileMenu);
        fileMenu.append(actions.getAction(ActionId.MENU_NEW).createMenuItem());
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_PROPERTIES).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_OPEN_DIR).createMenuItem());
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_START).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_PAUSE).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_REMOVE).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_DELETE).createMenuItem());
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_START_ALL).createMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_PAUSE_ALL).createMenuItem());
        fileMenu.append(new SeparatorMenuItem());
        fileMenu.append(actions.getAction(ActionId.MENU_EXIT).createMenuItem());
        menubar.append(fileItem);

        // Edit menu item
        final MenuItem editItem = new MenuItem(_("_Edit"));
        final Menu editMenu = new Menu();

        editItem.setSubmenu(editMenu);
        editMenu.append(actions.getAction(ActionId.MENU_SELECT_ALL).createMenuItem());
        editMenu.append(actions.getAction(ActionId.MENU_UNSELECT_ALL).createMenuItem());
        editMenu.append(new SeparatorMenuItem());
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
     * 
     * @return the toolbar.
     */
    private Toolbar createToolbar() {
        final Toolbar toolbar = new Toolbar();
        final ActionManager actions = app.getActionManager();

        toolbar.insert(actions.getAction(ActionId.TOOL_NEW).createToolItem(), 0);
        toolbar.insert(actions.getAction(ActionId.TOOL_START).createToolItem(), 1);
        toolbar.insert(actions.getAction(ActionId.TOOL_PAUSE).createToolItem(), 2);
        toolbar.insert(actions.getAction(ActionId.TOOL_REMOVE).createToolItem(), 3);
        toolbar.insert(new SeparatorToolItem(), 4);
        toolbar.insert(actions.getAction(ActionId.TOOL_PROPERTIES).createToolItem(), 5);

        return toolbar;
    }

    /**
     * Pack the main treeview into a window which allows us to scroll.
     * 
     * @return the scrolled window.
     */
    private ScrolledWindow createMainTreeView() {
        final ScrolledWindow scroll = new ScrolledWindow();
        mainView = new MainList();

        mainView.setSizeRequest(457, 275);
        scroll.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
        scroll.add(mainView);

        return scroll;
    }

    /**
     * Get the preferences dialog.
     * 
     * @return the dialog.
     */
    public PreferencesDialog getPreferencesDialog() {
        return preferences;
    }

    /**
     * Get the about dialog.
     * 
     * @return the dialog.
     */
    public AboutSoftDialog getAboutDialog() {
        return about;
    }

    /**
     * Get the tray icon associated to this window.
     * 
     * @return the tray icon.
     */
    public TrayIcon getTrayIcon() {
        return trayIcon;
    }

    /**
     * Get the main treeview.
     * 
     * @return the treeview.
     */
    public MainList getMainTreeView() {
        return mainView;
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        app.quit();
        return false;
    }
}
