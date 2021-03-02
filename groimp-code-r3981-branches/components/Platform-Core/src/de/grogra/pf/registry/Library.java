
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

import java.io.File;
import java.util.Arrays;

import de.grogra.graph.impl.Node.NType;
import de.grogra.pf.boot.Main;
import de.grogra.vfs.FileSystem;

public class Library extends Item
{
	private String[] files;
	//enh:field

	private String[] prefixes;
	//enh:field
	
	private transient Object[] libraryFiles;

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field files$FIELD;
	public static final NType.Field prefixes$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Library.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Library) o).files = (String[]) value;
					return;
				case 1:
					((Library) o).prefixes = (String[]) value;
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
					return ((Library) o).files;
				case 1:
					return ((Library) o).prefixes;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Library ());
		$TYPE.addManagedField (files$FIELD = new _Field ("files", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String[].class), null, 0));
		$TYPE.addManagedField (prefixes$FIELD = new _Field ("prefixes", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String[].class), null, 1));
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
		return new Library ();
	}

//enh:end


	public Library ()
	{
		super (null);
	}

	
	public Object[] getLibraryFiles ()
	{
		return libraryFiles;
	}


	LibraryClassLoader createLoader (Object directory, PluginClassLoader loader)
	{
		Object project = null;
		Object lib = directory;
		FileSystem fs = loader.getPluginDescriptor ().getFileSystem ();
		if (Main.usesProjectTree () && fs.getName (directory).equalsIgnoreCase ("build"))
		{
			project = fs.getParent (directory);
			lib = fs.getFile (project, "lib");
		}
		libraryFiles = new Object[files.length];
		for (int i = 0; i < files.length; i++)
		{
			Object f = fs.getFile (lib, files[i]);
			if (f == null)
			{
				if (!Main.usesProjectTree ())
				{
					Main.error ("Library " + f + " does not exist");
				}
				String libName = files[i].substring (0, files[i].lastIndexOf ('.'));
				if (libName.equalsIgnoreCase (fs.getName (project)))
				{
					f = directory;
				}
				else
				{
					Main.error ("Don't know how to find library " + files[i]);
				}
			}
			libraryFiles[i] = f;
		}
		String[] p;
		if (prefixes == null)
		{
			p = new String[] {""};
		}
		else
		{
			p = new String[prefixes.length];
			for (int i = 0; i < p.length; i++)
			{
				p[i] = prefixes[i] + '.';
			}
		}
		Main.getLogger ().config
			(loader.descriptor.getName () + ": Adding LibraryClassLoader for "
			 + Arrays.toString (libraryFiles));
		return new LibraryClassLoader (libraryFiles, loader, p);
	}


	@Override
	protected boolean readAttribute (String uri, String name, String value)
		throws org.xml.sax.SAXException
	{
		if ("".equals (uri) && "file".equals (name))
		{
			files = new String[] {value};
			return true;
		}
		return super.readAttribute (uri, name, value);
	}

}
