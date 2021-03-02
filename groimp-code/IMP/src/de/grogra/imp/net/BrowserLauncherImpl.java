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

import de.grogra.pf.registry.Item;
import de.grogra.pf.ui.Context;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;

/**
 * Browser launcher class, used to open a web page inside a current default browser.
 * If no browser is open it also will open it.
 * 
 * @author mhenke
 *
 */
public class BrowserLauncherImpl {
	
	/**
	 * The URL of the GroIMP Wiki page
	 */
	private static final String WIKI_PAGE = "https://sourceforge.net/p/groimp/wiki/Home/";

	/**
	 * The URL of the YouTube channel
	 */
	private static final String YOUTUBE_PAGE = "https://www.youtube.com/channel/UC2PsLYZ9vO1vR0ZEsQCvhBg";

	
	/**
	 * The URL of the GroIMP page
	 */
	private static final String GROIMP_PAGE = "http://www.grogra.de/";

	/**
	 * Opens the GroIMP Wiki page.
	 */
	public static void openWikiPage(Item item, Object info, Context ctx) {
		openPage(WIKI_PAGE);
	}

	/**
	 * Opens the GroIMP page.
	 */	
	public static void openGroIMPPage(Item item, Object info, Context ctx) {
		openPage(GROIMP_PAGE);
	}
	
	/**
	 * Opens the GroIMP YouTube channel.
	 */
	public static void openYouTube(Item item, Object info, Context ctx) {
		openPage(YOUTUBE_PAGE);
	}
	
	/**
	 * Opens a web page.
	 * 
	 * @param url
	 */
	public static void openPage(String url) {
		//Step 3: Create an instance of BrowserLauncher.
		BrowserLauncher launcher = null;
		try {
			launcher = new BrowserLauncher();
		} catch (UnsupportedOperatingSystemException e) {
			System.err.println("UnsupportedOperatingSystemException during lunching the web browser");
			e.printStackTrace();
		} catch (BrowserLaunchingInitializingException e) {
			System.err.println("BrowserLaunchingInitializingException during lunching the web browser");
			e.printStackTrace();
		}
		
		// Step 4: Launch a browser with a url.
		if(launcher!=null) {
			launcher.openURLinBrowser(url);
		}
	}

}
