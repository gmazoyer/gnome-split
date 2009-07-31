/*
 * MainList.java
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

import org.gnome.gtk.CellRendererPixbuf;
import org.gnome.gtk.CellRendererProgress;
import org.gnome.gtk.CellRendererText;
import org.gnome.gtk.DataColumn;
import org.gnome.gtk.DataColumnInteger;
import org.gnome.gtk.DataColumnStock;
import org.gnome.gtk.DataColumnString;
import org.gnome.gtk.ListStore;
import org.gnome.gtk.Stock;
import org.gnome.gtk.TreeIter;
import org.gnome.gtk.TreeView;
import org.gnome.gtk.TreeViewColumn;

import static org.freedesktop.bindings.Internationalization._;

public class MainList extends TreeView
{
    public DataColumnStock type;

    public final DataColumnString file;

    public final DataColumnStock state;

    public final DataColumnInteger progress;

    public final DataColumnString time;

    private final ListStore model;

    public MainList() {
        super();

        model = new ListStore(new DataColumn[] {
                type = new DataColumnStock(),
                file = new DataColumnString(),
                state = new DataColumnStock(),
                progress = new DataColumnInteger(),
                time = new DataColumnString()
        });

        this.setModel(model);

        TreeViewColumn vertical = null;
        CellRendererPixbuf iconRenderer = null;
        CellRendererText textRenderer = null;
        CellRendererProgress progressRenderer = null;

        vertical = this.appendColumn();
        vertical.setTitle(_("Type"));
        iconRenderer = new CellRendererPixbuf(vertical);
        iconRenderer.setStock(type);

        vertical = this.appendColumn();
        vertical.setTitle(_("File"));
        textRenderer = new CellRendererText(vertical);
        textRenderer.setText(file);

        vertical = this.appendColumn();
        vertical.setTitle(_("State"));
        iconRenderer = new CellRendererPixbuf(vertical);
        iconRenderer.setStock(state);

        vertical = this.appendColumn();
        vertical.setTitle(_("Progress"));
        progressRenderer = new CellRendererProgress(vertical);
        progressRenderer.setValue(progress);

        vertical = this.appendColumn();
        vertical.setTitle(_("Time"));
        textRenderer = new CellRendererText(vertical);
        textRenderer.setText(time);
        
        this.addTestAction();
        this.addTestAction();
    }

    @Override
    public ListStore getModel() {
        return model;
    }
    
    public void addTestAction() {
        final TreeIter row = model.appendRow();
        model.setValue(row, type, Stock.PASTE);
        model.setValue(row, file, "Test file");
        model.setValue(row, state, Stock.MEDIA_PAUSE);
        model.setValue(row, progress, 45);
        model.setValue(row, time, "Test time");
    }
}
