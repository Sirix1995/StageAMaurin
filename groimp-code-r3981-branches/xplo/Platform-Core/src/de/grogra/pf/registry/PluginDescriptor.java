
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import org.xml.sax.SAXException;

import de.grogra.persistence.PersistenceBindings;
import de.grogra.pf.boot.Main;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.io.InputStreamSource;
import de.grogra.pf.io.InputStreamSourceImpl;
import de.grogra.pf.io.SAXSource;
import de.grogra.pf.io.StreamAdapter;
import de.grogra.util.I18NBundle;
import de.grogra.util.IOWrapException;
import de.grogra.util.MimeType;
import de.grogra.util.WrapException;
import de.grogra.util.XByteArrayOutputStream;
import de.grogra.vfs.FileSystem;
import de.grogra.vfs.LocalFileSystem;
import de.grogra.xl.util.ObjectList;

public final class PluginDescriptor extends Item
{
	public static final int FRESH = 0;
	public static final int DISABLED = 1;
	public static final int ERROR = 2;
	public static final int MISSING_PREREQUISITE = 3;
	public static final int INACTIVE = 4;
	public static final int ACTIVE = 5;

	private int state = FRESH;

	private LibraryClassLoader loader;
	
	private FileSystem fileSystem;
	private Object[] libraryFiles = new Object[0];
	private Object directory;

	private InputStreamSource input;
	private Plugin plugin = null;

	private I18NBundle i18n = null;

	private String version;
	//enh:field

	private String cls;
	//enh:field


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field version$FIELD;
	public static final NType.Field cls$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (PluginDescriptor.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((PluginDescriptor) o).version = (String) value;
					return;
				case 1:
					((PluginDescriptor) o).cls = (String) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((PluginDescriptor) o).version;
				case 1:
					return ((PluginDescriptor) o).cls;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new PluginDescriptor ());
		$TYPE.addManagedField (version$FIELD = new _Field ("version", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
		$TYPE.addManagedField (cls$FIELD = new _Field ("cls", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 1));
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new PluginDescriptor ();
	}

//enh:end


	PluginDescriptor ()
	{
		super (null);
	}

	
	String getPluginClass ()
	{
		return cls;
	}

	public static PluginDescriptor createCoreDescriptor (File libDir)
	{
		PluginDescriptor d = new PluginDescriptor ();
		d.setName ("de.grogra");
		d.setI18NBundle (Main.getI18NBundle ());
		d.version = String.valueOf (d.getFromResource ("pluginVersion"));
		ObjectList exts = new ObjectList ();
		if (libDir != null)
		{
			File[] f = libDir.listFiles ();
			for (int i = 0; i < f.length; i++)
			{
				if (!f[i].isDirectory ()
					&& f[i].getName ().toLowerCase ().endsWith (".jar"))
				{
					Main.getLogger ().config ("jar extension " + f[i]);
					exts.add (f[i]);
				}
			}
		}
		d.fileSystem = LocalFileSystem.FILE_ADAPTER;
		d.libraryFiles = exts.toArray (new File[exts.size ()]);
		d.loader = new LibraryClassLoader (d.libraryFiles, libDir, d);
		return d;
	}


	public String getPluginVersion ()
	{
		return version;
	}


	public String getPluginName ()
	{
		return String.valueOf (getFromResource ("pluginName"));
	}


	public String getPluginProvider ()
	{
		return String.valueOf (getFromResource ("provider"));
	}


	@Override
	public Object get (Object key, Object defaultValue)
	{
		if ("pluginId".equals (key))
		{
			return getName ();
		}
		else if ("pluginName".equals (key))
		{
			return getPluginName ();
		}
		else if ("provider".equals (key))
		{
			return getPluginProvider ();
		}
		else
		{
			return super.get (key, defaultValue);
		}
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		if ("".equals (uri))
		{
			if ("id".equals (name))
			{
				setName (value);
				return true;		
			}
			if ("class".equals (name))
			{
				cls = value;
				return true;		
			}
		}
		return super.readAttribute (uri, name, value);
	}


	public static PluginDescriptor read (String systemId, InputStream in,
										 FileSystem fs, Object dir)
		throws IOException
	{
		XByteArrayOutputStream out = new XByteArrayOutputStream (0x4000);
		out.read (in);
		ByteArrayInputStream bin = out.createInputStream (); 
		InputStreamSourceImpl input = new InputStreamSourceImpl
			(bin, systemId, MimeType.TEXT_XML, null, null);
		SAXSource xml = new StreamAdapter (input, IOFlavor.XML_FLAVOR);
		XMLRegistryReader r = new XMLRegistryReader (null, null, null);
		try
		{
			xml.parse (r, null, null, null, null);
		}
		catch (SAXException e)
		{
			throw new IOWrapException
				(e, "Reading of plugin " + systemId + " failed.");
		}
		bin.reset (); 
		PluginDescriptor pd = (PluginDescriptor) r.getRoot ();
		pd.fileSystem = fs;
		pd.directory = dir;
		pd.input = input;
		return pd;
	}


	public boolean loadPlugin (Registry registry, ClassLoader parentLoader)
	{
		state = INACTIVE;
		setRegistry (registry);
		ObjectList v = new ObjectList (10);
		for (Item i = (Item) getBranch (); i != null;
			 i = (Item) i.getSuccessor ())
		{
			if (i instanceof PluginPrerequisite)
			{
				v.add (registry.getPluginDescriptor
					   (((PluginPrerequisite) i).getName ())
					   .getPluginClassLoader ());
			}
		}
		LibraryClassLoader[] a = new LibraryClassLoader[v.size ()];
		v.toArray (a);
		PluginClassLoader l = new ImportsClassLoader (parentLoader, a, this);
		for (Item i = (Item) getBranch (); i != null;
			 i = (Item) i.getSuccessor ())
		{
			if (i instanceof Library)
			{
				l = ((Library) i).createLoader (directory, l);
			}
		}
		loader = new LibraryClassLoader (new Object[] {directory}, l, new String[0]);
		i18n.initClassLoader (loader);
		SAXSource xml = new StreamAdapter (input, IOFlavor.XML_FLAVOR);
		XMLRegistryReader r = new XMLRegistryReader
			(registry, this, new PersistenceBindings (loader, registry));
		try
		{
			instantiatePlugin ();
			if (plugin.initialize ())
			{
				xml.parse (r, null, null, null, null);
				return true;
			}
			else
			{
				state = DISABLED;
				Main.getLogger ().config ("Plugin " + this + " disabled itself.");
				return false;
			}
		}
		catch (Exception e)
		{
			Main.getLogger ().log (Level.CONFIG,
				"Error loading plugin " + getPluginName (),
				e);
			System.err.println ("Error loading plugin " + getPluginName ());
			e.printStackTrace ();
			state = ERROR;
			return false;
		}
	}

	
	public Object getPluginDirectory ()
	{
		return directory;
	}

	
	public FileSystem getFileSystem ()
	{
		return fileSystem;
	}

	
	public synchronized void setPluginState (int state)
	{
		if (this.state != FRESH)
		{
			throw new IllegalStateException ();
		}
		this.state = state;
	}

	public synchronized int getPluginState ()
	{
		return state;
	}


	public Plugin getPlugin ()
	{
		return plugin;
	}


	private boolean instantiating = false;
	private boolean instantiated = false;

	private synchronized void instantiatePlugin () throws Exception
	{
		if (instantiated || (plugin != null))
		{
			return;
		}
		instantiated = true;
		instantiating = true;
		try
		{
			plugin = (cls == null) ? new Plugin ()
				: (Plugin) Class.forName (cls, true, loader).newInstance ();
			plugin.descriptor = this;
		}
		finally
		{
			instantiating = false;
		}
	}

	synchronized void activatePlugin ()
	{
		if (state != INACTIVE)
		{
			return;
		}
		if (instantiating)
		{
			return;
		}
		state = ACTIVE;
		Main.getLogger ().log
			(java.util.logging.Level.CONFIG,
			 "Activating plugin " + getName ());
		try
		{
			instantiatePlugin ();
		}
		catch (Exception e)
		{
			Main.error (e);
		}
		plugin.startup ();
	}


	@Override
	public String toString ()
	{
		return "Plugin[" + getName () + ", " + getPluginName ()
			+ ", provider=" + getPluginProvider () + ", version="
			+ version + "]"; 
	}


	@Override
	public I18NBundle getI18NBundle ()
	{
		return i18n;
	}


	public void setI18NBundle (I18NBundle bundle)
	{
		this.i18n = bundle;
	}

	
	public Object[] getLibraryFiles ()
	{
		return libraryFiles;
	}


	@Override
	public ClassLoader getClassLoader ()
	{
		return loader;
	}


	public PluginClassLoader getPluginClassLoader ()
	{
		return loader;
	}


	public URL getURLForResource (String name)
	{
		try
		{
			return new URL ("plugin", null, -1, getName () + '/' + name);
		}
		catch (MalformedURLException e)
		{
			throw new WrapException (e);
		}
	}


	public static PluginDescriptor getInstance (String pluginId)
	{
		return Main.getRegistry ().getPluginDescriptor (pluginId);
	}


	public static PluginDescriptor getInstance (Class cls)
	{
		return getInstance (cls.getClassLoader ());
	}


	public static PluginDescriptor getInstance (ClassLoader loader)
	{
		while (loader != null)
		{
			if (loader instanceof PluginClassLoader)
			{
				return ((PluginClassLoader) loader).descriptor;
			}
			loader = loader.getParent ();
		}
		return null;
	}


	public File getConfigurationDirectory ()
	{
		File f = Main.getConfigurationDirectory ();
		if (f == null)
		{
			return null;
		}
		f = new File (f, getName ());
		return (f.isDirectory () || f.mkdir ()) ? f : null;
	}

}
