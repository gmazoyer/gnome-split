/*
 * AboutSoftDialog.java
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
import org.gnome.gtk.AboutDialog;
import org.gnome.gtk.Dialog;
import org.gnome.gtk.ResponseType;
import org.gnome.gtk.Widget;
import org.gnome.gtk.Window;
import org.gnome.split.config.Constants;

import static org.freedesktop.bindings.Internationalization.N_;
import static org.freedesktop.bindings.Internationalization._;

/**
 * This class is used to build the GTK+ About dialog.<br>
 * This dialog is conventionally used in all GNOME programs.
 * 
 * @author Guillaume Mazoyer
 */
public class AboutSoftDialog extends AboutDialog implements Window.DeleteEvent, Dialog.Response
{
    private static String developer = N_("Head developer:");

    private static String packager = N_("Packager:");

    private static String expackager = N_("Retired packager:");

    /**
     * Build the about dialog.
     */
    public AboutSoftDialog() {
        super();

        // Define all properties
        this.setIcon(Constants.PROGRAM_LOGO);
        this.setProgramName(Constants.PROGRAM_NAME);
        this.setVersion(Constants.PROGRAM_VERSION);
        this.setLogo(Constants.PROGRAM_LOGO);
        this.setComments(_("Split and merge your files easily."));
        this.setCopyright("Copyright \u00A9 2009 " + _("the GNOME Split project"));
        this.setWebsite("http://www.respawner.fr/gnome-split/");

        // Program authors
        this.setAuthors(new String[] {
                _(developer),
                "  Guillaume Mazoyer <respawneral@gmail.com>\n",
                _(packager),
                "  Olivier Bitsch <olivier.b@iabsis.com>\n",
                _(expackager),
                "  J\u00E9r\u00F4me <eclipse.magick@gmail.com>"
        });

        // Program documenters
        this.setDocumenters(new String[] {
            "Guillaume Mazoyer <respawneral@gmail.com>"
        });

        // Program artists
        this.setArtists(new String[] {
            "Skatershi"
        });

        // Program translators
        this.setTranslatorCredits(_("translator-credits"));

        // Program license
        this.setLicense(_("This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3, as published by the Free Software Foundation.\n\nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranties of MERCHANTABILITY, SATISFACTORY QUALITY, or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.\n\nYou should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>."));
        this.setWrapLicense(true);

        // When cross button is clicked
        this.connect((Window.DeleteEvent) this);

        // When close response is emitted
        this.connect((Dialog.Response) this);
    }

    @Override
    public boolean onDeleteEvent(Widget source, Event event) {
        this.emitResponse(ResponseType.CLOSE);
        return false;
    }

    @Override
    public void onResponse(Dialog source, ResponseType response) {
        this.hide();
    }
}
