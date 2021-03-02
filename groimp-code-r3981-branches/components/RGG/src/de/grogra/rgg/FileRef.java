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

package de.grogra.rgg;

import java.io.*;

import de.grogra.pf.io.FileSource;
import de.grogra.pf.io.IOFlavor;
import de.grogra.pf.registry.ItemReference;
import de.grogra.pf.registry.Registry;
import de.grogra.pf.ui.registry.SourceFile;
import de.grogra.util.MimeType;

public final class FileRef extends ItemReference
{
	//enh:sco
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends ItemReference.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (FileRef representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ItemReference.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new FileRef ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (FileRef.class);
		$TYPE.validate ();
	}

//enh:end

	FileRef ()
	{
		super (null);
	}

	public FileRef (String name)
	{
		super (name);
	}

	public synchronized FileSource resolve ()
	{
		SourceFile sf = (SourceFile) (itemResolved ? item : resolveItem (
			"/objects/files", Registry.current ()));
		return (sf == null) ? null : sf.toFileSource ();
	}

	public Reader getReader () throws IOException
	{
		return resolve ().getReader ();
	}

	public Writer getWriter (boolean append) throws IOException
	{
		return resolve ().getWriter (append);
	}

	public InputStream getInputStream () throws IOException
	{
		return resolve ().getInputStream ();
	}

	public OutputStream getOutputStream (boolean append) throws IOException
	{
		return resolve ().getOutputStream (append);
	}

	public IOFlavor getFlavor ()
	{
		return resolve ().getFlavor ();
	}

	public MimeType getMimeType ()
	{
		return resolve ().getFlavor ().getMimeType ();
	}

}
