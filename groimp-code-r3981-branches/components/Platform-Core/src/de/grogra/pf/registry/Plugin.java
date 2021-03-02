
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

package de.grogra.pf.registry;

import java.util.prefs.Preferences;

/**
 * An instance of <code>Plugin</code> is the representation of a plugin and
 * can response to lifetime events of the plugin. It may also opt to disable
 * the plugin when the application boots. This may be used, e.g., if the
 * environment does not provide some features required by the plugin.
 * <p> 
 * Normally, instances of this class <code>Plugin</code> are used to represent
 * a plugin. If a plugin needs a specific implementation of methods of
 * <code>Plugin</code>, it has to provide a specialized subclass and declare
 * this in the root element of the plugin.xml file as in
 * <pre>
 * &lt;plugin id="de.grogra.foo" version="0.9.7"
 *         class="de.grogra.foo.MyPlugin"
 *         xmlns="http://grogra.de/registry"&gt;
 * ...
 * &lt;/plugin&gt;
 * </pre>
 * 
 * @author Ole Kniemeyer
 */
public class Plugin implements RegistryContext
{
	/**
	 * The descriptor of the plugin.
	 */
	PluginDescriptor descriptor;

	
	/**
	 * Initializes the plugin during booting of application.
	 * If this method returns <code>false</code>, the plugin is disabled
	 * throughout the lifetime of the application.
	 * <p>
	 * The default implementation simply returns <code>true</code>.
	 * Subclasses may override this method if they have to check
	 * some preconditions which are required by their plugin.
	 * 
	 * @return <code>true</code> iff plugin shall be enabled
	 */
	public boolean initialize ()
	{
		return true;
	}


	/**
	 * Returns the plugin descriptor of this plugin.
	 * 
	 * @return plugin descriptor
	 */
	public final PluginDescriptor getPluginDescriptor ()
	{
		return descriptor;
	}


	public final Registry getRegistry ()
	{
		return descriptor.getRegistry ();
	}


	/**
	 * Returns the resource bundle which represents the contents of the
	 * plugin.properties file.
	 * 
	 * @return resource bundle of plugin.properties
	 */
	public final de.grogra.util.I18NBundle getI18NBundle ()
	{
		return descriptor.getI18NBundle ();
	}


	/**
	 * This method is invoked when the plugin is activated, i.e., when the
	 * first class defined by the plugin is loaded (except for the Plugin
	 * class itself). The default implementation executes all executable
	 * items in the directory <code>/hooks/startup/<i>name</i></code>, where
	 * <code><i>name</i></code> is the name of the plugin.
	 * <p>
	 * The method {@link #initialize()} has been invoked before. Only if this
	 * invocation has returned <code>true</code>, the plugin may be activated. 
	 */
	public void startup ()
	{
		Executable.runExecutables
			(descriptor.getRegistry (),
			 "/hooks/startup/" + descriptor.getName (), this,
			 new de.grogra.util.StringMap ());
	}

	
	/**
	 * This method is invoked when an active plugin is deactivated, i.e., when
	 * the whole application is terminated.
	 * The default implementation executes all executable
	 * items in the directory <code>/hooks/shutdown/<i>name</i></code>, where
	 * <code><i>name</i></code> is the name of the plugin.
	 */
	public void shutdown ()
	{
		Executable.runExecutables
			(descriptor.getRegistry (),
			 "/hooks/shutdown/" + descriptor.getName (), this,
			 new de.grogra.util.StringMap ());
	}


	/**
	 * This method returns <code>true</code> iff it is has not yet been
	 * invoked before for the same plugin and the same user (extending
	 * over all application invocations). To be more precise, the method sets
	 * a plugin-specific flag in the preferences of the user and checks
	 * whether this flag has not yet been set before. 
	 * 
	 * @return <code>true</code> iff this method is invoked for the first time
	 */
	public boolean checkConfigure ()
	{
		Preferences p = Preferences.userRoot ().node ("/de/grogra/configured");
		String s = p.get (getPluginDescriptor ().getName (), null);
		p.put (getPluginDescriptor ().getName (), "true");
		return s == null;
	}

}
