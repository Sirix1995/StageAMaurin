/*
 * Copyright (C) 2012 GroIMP Developer Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */


package de.grogra.imp.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.prefs.Preferences;

import de.grogra.pf.boot.Main;
import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.Window;
import de.grogra.pf.ui.Workbench;
import de.grogra.util.I18NBundle;

/**
 * Opens the GroIMP version file and compares it with the version of this copy.
 * Finally a message window will show the result.
 * 
 * @author mhenke
 *
 */
public class UpdateCheck {
	
	/**
	 * Opens the GroIMP version file and compares it with the version of this copy.
	 * Finally a message window will show the result.
	 */
	public static void check(Item item, Object info, Context ctx) throws Exception {
		check(false, ctx);
	}

	/**
	 * Opens the GroIMP version file and compares it with the version of this copy.
	 * Finally a message window will show the result.
	 *
	 * @param quiet turns the message window in the negative (if there is no new version available) case on and off
	 */
	public static void check(boolean quiet, Context ctx) {
		String currentVersion = "";
		int i = 0;
		while(currentVersion.length ()==0) {
			try {
				URL fileURL = new URL(Main.GROIMP_VERSION_FILE[i]);
				URLConnection yc = fileURL.openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
				currentVersion = in.readLine();
				in.close();
				i++;
			} catch (IOException e) {}
		}

		I18NBundle thisI18NBundle = ctx.getWorkbench().getRegistry().getPluginDescriptor("de.grogra.imp").getI18NBundle();

		String thisVersion = thisI18NBundle.getString("app.version.Name");
		if(Main.compare(thisVersion, currentVersion)<0) {
			// there is a newer version
			Workbench.current().getWindow().showDialog(
				thisI18NBundle.getString ("updatedialog.title"),
				thisI18NBundle.getString ("updatedialog.new.version.message"),
				Window.INFORMATION_MESSAGE);
		} else {
			if(!quiet ) {
				// this version is the latest one
				Workbench.current().getWindow().showDialog(
					thisI18NBundle.getString ("updatedialog.title"),
					thisI18NBundle.getString ("updatedialog.same.version.message"),
					Window.INFORMATION_MESSAGE);
			}
		}

		Preferences prefs0 = Preferences.userRoot().node(Main.getInstance ().getClass().getName());
		if(prefs0!=null) {
			Preferences  prefs1 = prefs0.node("/de/grogra/options/ui/options");
			if(prefs1!=null) {
				if (!Boolean.parseBoolean (prefs1.get("auto_update_check", "true"))) {
					int x = Workbench.current().getWindow().showChoiceDialog(
						thisI18NBundle.getString ("checkforupdatesautomatically.title"), thisI18NBundle,
						"", new String [] {thisI18NBundle.getString ("checkforupdatesautomaticallyYes.title"), thisI18NBundle.getString ("checkforupdatesautomaticallyNo.title")});
					if(x==0) {
						prefs1.put ("auto_update_check", "true");
					}
				}
			}
		}
	}

}