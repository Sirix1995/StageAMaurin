
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
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

package de.grogra.pf.boot;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import de.grogra.pf.io.IO;
import de.grogra.pf.registry.Application;
import de.grogra.pf.registry.Argument;
import de.grogra.pf.registry.Executable;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ItemCriterion;
import de.grogra.pf.registry.MethodDescriptionXMLReader;
import de.grogra.pf.registry.PluginDescriptor;
import de.grogra.pf.registry.PluginPrerequisite;
import de.grogra.pf.registry.Prerequisite;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.registry.expr.Expression;
import de.grogra.util.DelegatingClassLoader;
import de.grogra.util.I18NBundle;
import de.grogra.util.PathListIterator;
import de.grogra.util.ResourceConverter;
import de.grogra.util.SplashScreen;
import de.grogra.util.StringMap;
import de.grogra.util.Utils;
import de.grogra.util.XPropertyResourceBundle;
import de.grogra.vfs.LocalFileSystem;
import de.grogra.xl.util.ObjectList;

public final class Main
{
	private static final String DEBUG = "debug";

	private static final String PROJECT_TREE = "project-tree";

	private static final String BOOT_PATH = "boot";
	private static final String SYSTEM_PLUGIN_PATH = "system-plugins";
	private static final String USER_PLUGIN_PATH = "user-plugins";
	private static final String PLUGIN_PATH = "plugins";

	private static final String DISABLE_PLUGIN = "disable";

	private static final String CONFIGURATION = "config";

	public static final String HEADLESS = "headless";

	private static final String DISABLE_SPLASH = "disable-splash";
	private static final String SPLASH_CLASS = "splash-class";
	private static final String SPLASH_BACKGROUND = "splash-background";
	private static final String SPLASH_FOREGROUND = "splash-foreground";
	private static final String SPLASH_LOGO = "splash-logo";	
	private static final String SPLASH_BAR_LEFT = "splash-bar-left";
	private static final String SPLASH_BAR_RIGHT = "splash-bar-right";
	private static final String SPLASH_TITLE = "splash-title";
	private static final String SPLASH_TEXT_LOCATION = "splash-text-location";
	private static final String SPLASH_TEXT_COLOR = "splash-text-color";
	private static final String SPLASH_BAR_BOUNDS = "splash-bar-bounds";

	private static final String APPLICATION = "application";
	
	public static final Integer SCREEN_PROPERTY_ID = Registry.allocatePropertyId();
	public static final String SCREEN_PROPERTY = "screen";

	private static final String[] OPTIONS = {
		"* " + DEBUG,

		"  " + PROJECT_TREE,

		"* " + BOOT_PATH,
		"* " + SYSTEM_PLUGIN_PATH,
		"*p" + USER_PLUGIN_PATH,
		"* " + PLUGIN_PATH,
		
		"* " + DISABLE_PLUGIN,

		"*c" + CONFIGURATION,

		"  " + HEADLESS,

		"  " + DISABLE_SPLASH,
		"* " + SPLASH_CLASS,
		"* " + SPLASH_BACKGROUND,
		"* " + SPLASH_BAR_LEFT,
		"* " + SPLASH_BAR_RIGHT,
		"* " + SPLASH_BAR_BOUNDS,
		"* " + SPLASH_TITLE,
		"* " + SPLASH_TEXT_LOCATION,
		"* " + SPLASH_TEXT_COLOR,
		
		"* " + SCREEN_PROPERTY,

		"*a" + APPLICATION};

	/**
	 * List of URL's of the GroIMP version file.
	 */
	public static final String[] GROIMP_VERSION_FILE = {
		"http://wwwuser.gwdg.de/~groimp/version.txt",
		"http://ufgb966.forst.uni-goettingen.de/version.txt",
	};

	private final static String DESCRIPTION_FILE = "jel.xml";

	private static Main MAIN;


	public static Main getInstance ()
	{
		if(MAIN==null)
		{
			String[] args = new String[1];
			args[0] = "";
			MAIN = new Main (args);
		}
		return MAIN;
	}


	public static void main (String[] args)
	{
		MAIN = new Main (args);
		URL.setURLStreamHandlerFactory (new URLStreamHandlerFactory ()
			{
				public URLStreamHandler createURLStreamHandler (String protocol)
				{
					return getInstance ().createURLStreamHandler (protocol);
				}
			});
		MAIN.run ();
	}


	public static void exit ()
	{
		Registry r = getInstance ().registry;
		PluginDescriptor d = (PluginDescriptor)
			r.getPluginDirectory ().getBranchTail ();
		while (d != null)
		{
			if (d.getPluginState () == PluginDescriptor.ACTIVE)
			{
				try
				{
					d.getPlugin ().shutdown ();
				}
				catch (Exception e)
				{
					logSevere (e);
				}
			}
			d = (PluginDescriptor) d.getPredecessor ();
		}
		System.exit (0);
	}

	
	private final int argStart;
	private final String[] args;
	private final Properties props;
	private final File configurationDirectory;
	private Registry registry;
	private Application application;
	private Logger logger;
	private File rootDirectory = null;
	private boolean usesProjectTree = false;
	private ClassLoader loaderForAll;
	private I18NBundle i18n;
	private Item streamHandlerDir;

	private String version;
	private SplashScreen splash = null;
	private boolean disableSplash = false;
	private volatile long splashWillClose;


	private Main (String[] args)
	{
		Properties config = new Properties ();
		Properties p = new Properties (config);
		int as = args.length;
	parseArgs:
		for (int i = 0; i < args.length; i++)
		{
			String s = args[i];
			if (s.equals ("--"))
			{
				as = i + 1;
				break;
			}
			if (s.startsWith ("--"))
			{
				int e = s.indexOf ('=');
				String n = (e < 0) ? s.substring (2) : s.substring (2, e);
				for (int j = 0; j < OPTIONS.length; j++)
				{
					if (OPTIONS[j].substring (2).equals (n))
					{
						addOption (j, (e >= 0) ? s.substring (e + 1)
								   : null, p, (char) 0);
						continue parseArgs;
					}
				}
				System.err.println ("Unknown option " + s);
			}
			else if (s.startsWith ("-X"))
			{
				if (s.length () == 2)
				{
					System.err.println ("X-Option not specified");
				}
				else
				{
					int e = s.indexOf ('=');
					p.setProperty ((e < 0) ? s.substring (2)
								   : s.substring (2, e),
								   (e < 0) ? "true"
								   : s.substring (e + 1));
				}
			}
			else if (s.startsWith ("-") && !s.equals ("-"))
			{
				char c = s.charAt (1);
				for (int j = 0; j < OPTIONS.length; j++)
				{
					if (OPTIONS[j].charAt (1) == c)
					{
						if (OPTIONS[j].charAt (0) == '*')
						{
							if (s.length () == 2)
							{
								args[i] = null;
								addOption (j, (++i == args.length) ? null
										   : args[i], p, c);
							}
							else
							{
								addOption (j, s.substring (2), p, c);
							}
						}
						else
						{
						parseCluster:
							for (int k = 1; k < s.length (); k++)
							{
								c = s.charAt (k);
								for (j = 0; j < OPTIONS.length; j++)
								{
									if (OPTIONS[j].charAt (1) == c)
									{
										addOption (j, null, p, c);
										continue parseCluster;
									}
								}
								System.err.println ("Unknown option " + c);
							}
						}
						continue parseArgs;
					}
				}
				System.err.println ("Unknown option " + s);
			}
			else
			{
				as = i;
				break;
			}
			args[i] = null;
		}
		props = p;
		this.args = args;
		argStart = as;

		String cd = p.getProperty (CONFIGURATION);
		File f = (cd != null) ? IO.toLocalFile (cd)
			: new File (System.getProperty ("user.home"),
						".grogra.de-platform");
		if ((f != null) && !f.isDirectory () && !f.mkdirs ())
		{
			f = null;
		}
		configurationDirectory = f;

		logger = Logger.getLogger ("de.grogra.pf");
		logger.setLevel (Level.CONFIG);
		logger.setUseParentHandlers (false);
		if (f != null)
		{
			f = new File (f, "log");
			if (f.isDirectory () || f.mkdir ())
			{
				try
				{
					Handler h = new FileHandler
						(f.getAbsolutePath () + "/platform%u-%g.xml", 0, 10, false);
					h.setLevel (Level.ALL);
					logger.addHandler (h);
				}
				catch (IOException e)
				{
					e.printStackTrace ();
				}
			}
		}

		Handler h = new ConsoleHandler ();
		h.setLevel (Level.WARNING);
		if (p.containsKey (DEBUG))
		{
			try
			{
				h.setLevel (Level.parse (p.getProperty (DEBUG)));
			}
			catch (IllegalArgumentException e)
			{
				System.err.println
					("Invalid DEBUG-Level " + p.getProperty (DEBUG));
			}
		}
		logger.addHandler (h);

		logger.config ("configurationDirectory = " + configurationDirectory);
		logProperties ();

		if (p.getProperty (BOOT_PATH) == null)
		{
			URL url = getClass ().getProtectionDomain ().getCodeSource ()
				.getLocation ();
			logger.config ("codeSource = " + url);
			if (!"file".equals (url.getProtocol ()))
			{
				error ("Platform must be loaded from filesystem.");
			}
			f = Utils.urlToFile (url);
			if (f.isFile ())
			{
				if (!f.getName ().toLowerCase ().endsWith (".jar"))
				{
					error ("Boot classes loaded from unknown file format.");
				}
				f = f.getParentFile ();
			}
			else if (!f.isDirectory ())
			{
				error ("Boot classes loaded from unknown file.");
			}
			setProperty (BOOT_PATH, f.getAbsolutePath ());
		}

		File gpf = new File (p.getProperty (BOOT_PATH),
							 "grogra.de-platform.properties");
		if (gpf.isFile ())
		{
			try
			{
				Properties gp = new Properties ();
				gp.load (new FileInputStream (gpf));
				for (Enumeration e = gp.keys (); e.hasMoreElements (); )
				{
					Object k = e.nextElement ();
					if (!p.containsKey (k))
					{
						p.put (k, gp.get (k));
					}
				}
			}
			catch (IOException e)
			{
				logger.log (Level.INFO, "", e);
			}
		}

		rootDirectory = new File (p.getProperty (BOOT_PATH));
		if (p.getProperty (PROJECT_TREE) != null)
		{
			usesProjectTree = true;
			rootDirectory = rootDirectory.getParentFile ().getParentFile ();
		}
		if (!rootDirectory.isDirectory ())
		{
			error ("Cannot find root of installation.");
		}

		logger.config ("rootDirectory = " + rootDirectory);

		if (p.getProperty(DISABLE_SPLASH) != null)
		{
			disableSplash = true;
		}
		
		if (p.getProperty (SYSTEM_PLUGIN_PATH) == null)
		{
			if (p.getProperty (PROJECT_TREE) == null)
			{
				setProperty (SYSTEM_PLUGIN_PATH, new File (rootDirectory, "plugins").toString ());
			}
			else
			{
				File[] projects = rootDirectory.listFiles ();
				StringBuffer b = new StringBuffer ();
				if (projects != null)
				{
					for (int i = 0; i < projects.length; i++)
					{
						if (projects[i].isDirectory ())
						{
							f = new File (projects[i], "build");
							if (f.isDirectory ())
							{
								b.append (f.getAbsolutePath ())
									.append (File.pathSeparatorChar);
							}
						}
					}
				}
				f = new File (rootDirectory, "plugins");
				if (f.isDirectory ())
				{
					b.append (f.getAbsolutePath ());
				}
				setProperty (SYSTEM_PLUGIN_PATH, b.toString ());
			}
		}

		if (p.getProperty (PLUGIN_PATH) == null)
		{
			setProperty (PLUGIN_PATH, p.getProperty (SYSTEM_PLUGIN_PATH)
						 + File.pathSeparatorChar
						 + p.getProperty (USER_PLUGIN_PATH, ""));
		}
	}


	private static String getDisableKey (String plugin)
	{
		return DISABLE_PLUGIN + ':' + plugin;
	}

	private static void addOption (int index, String value, Properties p,
								   char c)
	{
		String opt = OPTIONS[index].substring (2);
		String s = (c > 0) ? String.valueOf (c) : opt;
		if (OPTIONS[index].charAt (0) == '*')
		{
			if (value == null)
			{
				System.err.println ("Option " + s
									+ " specified without option value.");
			}
			else
			{
				if (opt.equals (DISABLE_PLUGIN))
				{
					opt = getDisableKey (value);
				}
				p.setProperty (opt, value);
			}
		}
		else
		{
			if (value != null)
			{
				System.err.println ("Option " + s
									+ " specified with option value.");
			}
			else
			{
				p.setProperty (opt, "true");
			}
		}
	}


	public static Logger getLogger ()
	{
		return getInstance ().logger;
	}


	public static void logWarning (Throwable thrown)
	{
		getInstance ().logger.log (Level.WARNING, "", thrown);
	}


	public static void logSevere (Throwable thrown)
	{
		getInstance ().logger.log (Level.SEVERE, "", thrown);
	}


	public void setProperty (String name, String value)
	{
		props.setProperty (name, value);
		logger.config (name + " := " + value);
	}


	public static String getProperty (String name)
	{
		return getInstance ().props.getProperty (name);
	}


	public static String getProperty (String name, String defaultValue)
	{
		return getInstance ().props.getProperty (name, defaultValue);
	}


	public static int getArgCount ()
	{
		Main m = getInstance ();
		return m.args.length - m.argStart;
	}


	public static String getArg (int index)
	{
		Main m = getInstance ();
		return m.args[index + m.argStart];
	}

	
	public static String getVersion ()
	{
		return getInstance ().version;
	}

	public static boolean usesProjectTree ()
	{
		return getInstance ().usesProjectTree;
	}


	public static Registry getRegistry ()
	{
		return getInstance ().registry;
	}

	
	public static boolean isInitialized ()
	{
		return getInstance () != null;
	}


	public static I18NBundle getI18NBundle (String pluginId)
	{
		return getInstance ().registry.getPluginDescriptor (pluginId)
			.getI18NBundle ();
	}


	public static I18NBundle getI18NBundle ()
	{
		return getInstance ().i18n;
	}


	public static Object getFromResource (String key)
	{
		return getApplication ().getFromResource (key);
	}


	public static Application getApplication ()
	{
		return getInstance ().application;
	}


	public static ClassLoader getLoaderForAll ()
	{
		return getInstance ().loaderForAll;
	}


	public static File getConfigurationDirectory ()
	{
		return getInstance ().configurationDirectory;
	}

	
	public static void closeSplashScreen ()
	{
		Main i = getInstance ();
		SplashScreen s = i.splash;
		if (s != null)
		{
			i.setProgress (1, i.i18n.msg ("splash.running"));
		}
		i.splashWillClose = System.currentTimeMillis () + 1500;
	}


	public static void error (String msg)
	{
		closeSplashScreen ();
		showMessage (msg, true);
	}


	public static void error (Throwable exception)
	{
		exception.printStackTrace ();
		error ("Unexpected exception " + exception);
	}


	private void logProperties ()
	{
		if (!logger.isLoggable (Level.CONFIG))
		{
			return;
		}
		StringBuffer b = new StringBuffer ("Properties:\n");
		for (Enumeration e = props.keys (); e.hasMoreElements (); )
		{
			Object k = e.nextElement ();
			b.append (k).append (" = ").append (props.get (k)).append ('\n');
		}
		logger.config (b.toString ());
	}

	
	private void setProgress (float p, String msg)
	{
		if (splash != null)
		{
			splash.setInitializationProgress
				(p, i18n.msg ("splash.progress", msg, version));
		}
	}

	
	private URL toURL (String prop)
	{
		String url = props.getProperty (prop);
		URL u = null;
		if (url != null)
		{
			try
			{
				u = new URL (Utils.fileToURL (new File (props.getProperty (BOOT_PATH))),
							 url);
			}
			catch (MalformedURLException e)
			{
				logger.log (Level.INFO, "", e);
			}
		}
		return u;
	}

	/**
	 * Searches for the preferences entry "/de/grogra/options/ui/options/languages" and
	 * returns the associated Locale object.
	 * To add a new language see de.grogra.pf.ui.Languages
	 * 
	 * @return
	 */
	private Locale getCurrentLocale() {
		Preferences prefs0 = Preferences.userRoot().node(this.getClass().getName());
		if(prefs0==null) return Locale.getDefault();
		Preferences  prefs1 = prefs0.node("/de/grogra/options/ui/options");
		if(prefs1==null) return Locale.getDefault();
		
		try {
			switch(Integer.parseInt(prefs1.get("languages", null))) {
				case 0: return new Locale("en", "GB");
				case 1: return new Locale("zh", "CN");
//				case 0: return new Locale("de", "DE");
			}	
		} catch (NumberFormatException ex) {}
		return Locale.getDefault();
	}
	
	private void run ()
	{
		Locale.setDefault(getCurrentLocale());
		i18n = I18NBundle.getInstance (getClass ());
		I18NBundle.addResourceConverter (ResourceConverter.CAT);
		I18NBundle.addResourceConverter (ResourceConverter.LINK);
		version = i18n.getString ("pluginVersion");

		// check for updates
		boolean newVersion = false;		
		try	{
			newVersion = checkForUpdates(version);
		}
		catch (Exception e1) {}

		URL u = toURL (SPLASH_BACKGROUND);
		if (u != null)
		{
			String splashClass = props.getProperty
				(SPLASH_CLASS,
				 (props.getProperty (HEADLESS) != null)
				 ? "de.grogra.util.ConsoleSplashScreen"
				 : "de.grogra.util.AWTSplashScreen");
			try
			{
				splash = (SplashScreen)
					Class.forName (splashClass).newInstance ();
				splash.init (props.getProperty (SPLASH_TITLE, "grogra.de"),
						 u, toURL(SPLASH_FOREGROUND), toURL(SPLASH_LOGO), 
						 Utils.parseRectangle (props.getProperty (SPLASH_BAR_BOUNDS)),
						 toURL (SPLASH_BAR_LEFT), toURL (SPLASH_BAR_RIGHT),
						 Utils.parsePoint (props.getProperty (SPLASH_TEXT_LOCATION, "+10+20")),
						 new Font ("SansSerif", Font.PLAIN, 12),
						 Utils.parseColor (props.getProperty (SPLASH_TEXT_COLOR, "0 0 0")),
						 newVersion, getInstance ().i18n);
			}
			catch (Exception e)
			{
				logger.log (Level.INFO, "", e);
				splash = null;
			}
			if ((splash != null) && !disableSplash)
			{
				setProgress (0.125f, i18n.msg ("splash.initializing"));
				splash.show ();
			}
		}

		long splashShown = System.currentTimeMillis ();
		splashWillClose = splashShown + 20000;

		int disabledCount = 0;

		ObjectList plugins = new ObjectList ();
		final HashMap<String,PluginDescriptor> pluginMap = new HashMap<String, PluginDescriptor> ();
		for (PathListIterator pathList
			 = new PathListIterator (props.getProperty (PLUGIN_PATH));
			 pathList.hasNext (); )
		{
			File pluginPath = pathList.nextPath ();
			logger.config ("Checking plugin-path " + pluginPath);
			if (pluginPath.isDirectory ())
			{
				File[] a = pluginPath.listFiles ();
				if (a != null)
				{
					for (int i = 0; i < a.length; i++)
					{
						File dir = null;
						if (a[i].isDirectory ())
						{
							dir = a[i];
						}
						else if (a[i].isFile ())
						{
							if (a[i].getName ().equalsIgnoreCase ("plugin.xml"))
							{
								dir = pluginPath;
							}
						}
						if (dir != null)
						{
							PluginDescriptor pd = loadPluginFromDirectory (dir);
							if (pd != null)
							{
								pluginMap.put (pd.getName (), pd);
								plugins.add (pd);
								if (props.getProperty (getDisableKey (pd.getName ())) != null)
								{
									pd.setPluginState (PluginDescriptor.DISABLED);
									logger.config ("Plugin " + pd.getName () + " was disabled by command line option");
									disabledCount++;
								}
							}
						}
					}
				}
			}
		}

		boolean pluginDisabled;
		StringBuffer msg = new StringBuffer ();
		
		do
		{
			pluginDisabled = false;
			for (int i = 0; i < plugins.size (); i++)		 
			{
				PluginDescriptor pd = (PluginDescriptor) plugins.get (i);
				if (pd.getPluginState () != PluginDescriptor.FRESH)
				{
					continue;
				}
				Item[] missing = pd.findAll (new ItemCriterion ()
				{
					public boolean isFulfilled (Item item, Object info)
					{
						if (item instanceof PluginPrerequisite)
						{
							PluginDescriptor pd = pluginMap.get (((PluginPrerequisite) item).getName ());
							return (pd == null) || (pd.getPluginState () != PluginDescriptor.FRESH);
						}
						else
						{
							return (item instanceof Prerequisite) && !((Prerequisite) item).isFulfilled ();
						}
					}

					public String getRootDirectory ()
					{
						return null;
					}
				}, null, false);   
				if (missing.length > 0)
				{
					pd.setPluginState (PluginDescriptor.MISSING_PREREQUISITE);
					disabledCount++;
					pluginDisabled = true;
					for (int j = 0; j < missing.length; j++)
					{
						((Prerequisite) missing[j]).addMessage
							(msg, pd.getPluginName ());
						msg.append (System.getProperty ("line.separator"));
					}
				}
			}
		} while (pluginDisabled);

		Registry r = Registry.create (null);
		
		File ext = new File (rootDirectory, "ext");
		if (!ext.isDirectory ())
		{
			ext = null;
		}
		logger.config ("extensionDirectory = " + ext);
		PluginDescriptor core = PluginDescriptor.createCoreDescriptor (ext);
		
		ObjectList<ClassLoader> loaders = new ObjectList<ClassLoader> ();
		boolean pluginAdded;
		int activatedCount = 0;
		Item dir = r.getDirectory ("/plugins", null);
		dir.add (core);
		do
		{
			pluginAdded = false;
			for (int i = 0; i < plugins.size (); i++)		 
			{
				PluginDescriptor pd = (PluginDescriptor) plugins.get (i); 
				if ((pd.getPluginState () == PluginDescriptor.FRESH)
					&& (pd.findFirst (new ItemCriterion ()
					{
						public boolean isFulfilled (Item item, Object info)
						{
							if (item instanceof PluginPrerequisite)
							{
								PluginDescriptor pd = pluginMap.get (((PluginPrerequisite) item).getName ());
								return (pd == null) || (pd.getPluginState () < PluginDescriptor.INACTIVE);
							}
							else
							{
								return false;
							}
						}

						public String getRootDirectory ()
						{
							return null;
						}
					}, null, false) == null))
				{
					setProgress
						(0.25f + 0.65f * activatedCount / (plugins.size () - disabledCount),
						 i18n.msg ("splash.loading-plugin", pd.getPluginName ()));
					logger.config ("Loading " + pd);
					if (pd.loadPlugin (r, core.getClassLoader ()))
					{
						pluginAdded = true;
						Registry.PLUGIN_FILE_SYSTEMS.addFileSystem
							(pd.getFileSystem (), pd.getName (), pd.getPluginDirectory ());
						loaders.add (pd.getPluginClassLoader ());
						dir.add (pd);
					}
					activatedCount++;
				}
			}
		} while (pluginAdded);

		setProgress (0.95f, i18n.msg ("splash.launching"));

		for (int i = 0; i < plugins.size (); i++)
		{
			PluginDescriptor pd = (PluginDescriptor) plugins.get (i); 
			if (pd.getPluginState () == PluginDescriptor.FRESH)
			{
				System.err.println ("Cycle in " + pd);
			}
		}

		if (loaders.isEmpty ())
		{
			loaderForAll = getClass ().getClassLoader ();
		}
		else
		{
			loaderForAll = new DelegatingClassLoader (loaders.toArray (new ClassLoader[loaders.size ()]));
		}

		registry = r;

		// loads the XML description of (Library.java) methods (for list and help command)
		String tmp = props.getProperty(BOOT_PATH);
		if(tmp==null || tmp.length ()==0) {
			error ("Error in Main: Could not load XML method description!");
		} else {
			try {
				registry.setMethodDescription(
					MethodDescriptionXMLReader.readXML(new FileInputStream(
						new File(tmp).getParent () + File.separatorChar + DESCRIPTION_FILE)
					)
				);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				error ("Error in Main: Could not load XML method description!");
			}
		}

		Executable.runExecutables (r, "/hooks/postboot", r, new StringMap ());

		streamHandlerDir = r.getItem ("/io/streamhandlers");
		r.startup ();
//		Utils.dumpTree (r);

		dir = r.getItem ("/applications");
		if (dir == null)
		{
			msg.append (i18n.msg ("boot.no-application"));
			error (msg.toString ());
		}
		String app = props.getProperty (APPLICATION);
		for (dir = (Item) dir.getBranch (); dir != null;
			 dir = (Item) dir.getSuccessor ())
		{
			if ((dir instanceof Application)
				&& ((app == null) || dir.hasName (app)))
			{
				for (Item arg = (Item) dir.getBranch (); arg != null;
					 arg = (Item) arg.getSuccessor ())
				{
					if (arg instanceof Argument)
					{
						if ((props.getProperty (arg.getName ()) == null)
							|| ((Argument) arg).isOverride ())
						{
							props.setProperty (arg.getName (),
											   ((Argument) arg).getValue ()); 
						}
					}
				}
				// set screen property
				String s = props.getProperty (SCREEN_PROPERTY);
				if (s != null) {
					try 
					{
						Integer i = Integer.valueOf(s);
						r.setUserProperty(SCREEN_PROPERTY_ID, i);
					}
					catch (NumberFormatException e)
					{
						msg.append (i18n.msg ("commandline.invalid", SCREEN_PROPERTY + "=" + s));
						error (msg.toString ());
					}
				}
				
				application = (Application) dir;
				logger.config ("Starting application " + application);
				logProperties ();
				new Thread (application, "Application").start ();
				break;
			}
		}

		if (dir == null)
		{
			msg.append ((app != null)
						? i18n.msg ("boot.application-not-found", app)
						: i18n.msg ("boot.no-application"));
			error (msg.toString ());
		}

		if (msg.length () > 0)
		{
			showMessage (msg.toString (), false);
		}

		if (splash != null)
		{
			try
			{
				long s;
				while ((s = 3000 + splashShown - System.currentTimeMillis ())
					   > 0)
				{
					Thread.sleep (s);
				}
				while (splashWillClose > System.currentTimeMillis ())
				{
					Thread.sleep (100);
				}
			}
			catch (InterruptedException e)
			{
			}
			splash.close ();
		}
	}

	private boolean checkForUpdates (String thisVersion) throws Exception {
		Preferences prefs0 = Preferences.userRoot().node(this.getClass().getName());
		if(prefs0==null) return false;
		Preferences  prefs1 = prefs0.node("/de/grogra/options/ui/options");
		if(prefs1==null) return false;
		
		if (Boolean.parseBoolean (prefs1.get("auto_update_check", "false"))) {
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
			if(compare(thisVersion, currentVersion)<0) return true;
		}
		return false;
	}

	/**
	 * compares two strings containing version numbers and returns the result.
	 * 0 .. v1 equals v2
	 * <0 .. v2 newer then v1
	 * >0 .. v2 older then v1 
	 * 
	 * @param v1 this version number (version of the installed application)
	 * @param v2 version to check (probably new version)
	 * @return
	 */
	public static int compare(String v1, String v2) {
		String s1 = normalisedVersion(v1);
		String s2 = normalisedVersion(v2);
		return s1.compareTo(s2);
	}

	private static String normalisedVersion(String version) {
		return normalisedVersion(version, ".", 4);
	}

	private static String normalisedVersion(String version, String sep, int maxWidth) {
		String[] split = Pattern.compile(sep, Pattern.LITERAL).split(version);
		StringBuilder sb = new StringBuilder();
		for (String s : split) {
			sb.append(String.format("%" + maxWidth + 's', s));
		}
		return sb.toString();
	}

	private PluginDescriptor loadPluginFromDirectory (File dir)
	{
		StringBuffer sb = new StringBuffer ("Looking for plugin in ")
			.append (dir).append ('\n');
		dir = dir.getAbsoluteFile ();
		PluginDescriptor pd = null;
		File pf = new File (dir, "plugin.xml");
		IOException ioe = null;
		if (pf.isFile ())
		{
			sb.append ("plugin.xml found in ").append (dir).append ('\n');
			InputStream in = null;
			try
			{
				in = new FileInputStream (pf);
				pd = PluginDescriptor.read (pf.getAbsolutePath (),
					new BufferedInputStream (in), new LocalFileSystem ("local", dir.getParentFile ()), dir);
			}
			catch (IOException e)
			{
				ioe = e;
			}
			if (in != null)
			{
				try
				{
					in.close ();
				}
				catch (IOException e)
				{
					e.printStackTrace ();
				}
			}
			if (pd == null)
			{
				sb.append ("Failed reading plugin.xml");
			}
			else
			{
				sb.append ("Found plugin ").append (pd.getName ())
					.append (' ').append (pd.getPluginVersion ()).append ('\n');
				File[] a = dir.listFiles ();
				Locale l = Locale.getDefault ();
				String[] s = {"plugin", l.getLanguage (), l.getCountry (),
							  l.getVariant ()};
				for (int i = 1; i < 4; i++)
				{
					if (s[i] != null)
					{
						s[i] = s[i - 1] + '_' + s[i];
					}
				}
				XPropertyResourceBundle[] p = new XPropertyResourceBundle[4];
				for (int f = 0; f < a.length; f++)
				{
					String n = a[f].getName ();
					if (n.endsWith (".properties"))
					{
						n = n.substring (0, n.length () - 11);
						for (int i = 0; i < 4; i++)
						{
							if (n.equals (s[i]))
							{
								in = null;
								try
								{
									in = new FileInputStream (a[f]);
									p[i] = new XPropertyResourceBundle (in);
								}
								catch (Exception e)
								{
									e.printStackTrace ();
								}
								if (in != null)
								{
									try
									{
										in.close ();
									}
									catch (IOException e)
									{
									}
								}
							}
						}
					}
				}
				XPropertyResourceBundle b = null;
				for (int i = 0; i < 4; i++)
				{
					if (p[i] != null)
					{
						p[i].setParent (b);
						b = p[i];
					}
				}
				pd.setI18NBundle (new I18NBundle (b, pd.getName ()));
			}
		}
		else
		{
			sb.append ("plugin.xml not found in ").append (dir);
		}
		logger.config (sb.toString ());
		if (ioe != null)
		{
			logger.log (Level.WARNING, "Failure is due to", ioe);
		}
		return pd;
	}


	private URLStreamHandler createURLStreamHandler (String protocol)
	{
		if ("plugin".equals (protocol))
		{
			return new URLStreamHandler ()
			{
				@Override
				protected URLConnection openConnection (URL u) throws IOException
				{
					String path = u.getPath ();
					if (path.charAt (0) == '/')
					{
						throw new IOException ("Invalid format of URL " + u);
					}
					int i = path.indexOf ('/');
					if (i < 0)
					{
						throw new IOException ("Invalid format of URL " + u);
					}
					ClassLoader c;
					if (i == 1)
					{
						c = getClass ().getClassLoader ();
					}
					else
					{
						PluginDescriptor p = getRegistry ().getPluginDescriptor
							(path.substring (0, i));
						if (p == null)
						{
							throw new IOException ("Plugin of " + u + " does not exist");
						}
						c = p.getClassLoader ();
					}
					URL u2 = c.getResource (path.substring (i + 1));
					if (u2 == null)
					{
						throw new IOException ("Resource " + u + " does not exist");
					}
					return u2.openConnection ();
				}
			};
		}
		else if ("project".equals (protocol))
		{
			return Registry.ALL_FILE_SYSTEMS.getURLStreamHandler ();
		}
		if (streamHandlerDir != null)
		{
			Item h = streamHandlerDir.getItem (protocol);
			if (h instanceof Expression)
			{
				return (URLStreamHandler) ((Expression) h)
					.evaluate (registry, new StringMap ());
			}
		}
		return null;
	}


	public static void showMessage (String msg, final boolean exitOnClose)
	{
		System.err.println (msg);
		I18NBundle i18n = getInstance ().i18n;
		final Frame f = new Frame (i18n.msg ("boot.messages"));

		class Listener extends WindowAdapter implements ActionListener
		{
			@Override
			public void windowClosing (WindowEvent e)
			{
				actionPerformed (null);
			}

			public void actionPerformed (ActionEvent e)
			{
				f.dispose ();
				if (exitOnClose)
				{
					System.exit (1);
				}
			}
		}

		Listener l = new Listener ();
		f.addWindowListener (l);
		TextArea t = new TextArea (msg, 20, 80);
		f.add (t, BorderLayout.CENTER);
		Button b = new Button (i18n.msg ("boot.close-messages"));
		b.addActionListener (l);
		f.add (b, BorderLayout.SOUTH);
		f.pack ();
		f.setVisible (true);
		if (exitOnClose)
		{
			while (true)
			{
				try
				{
					Thread.sleep (1000);
				}
				catch (InterruptedException e)
				{
				}
			}
		}
	}

}
