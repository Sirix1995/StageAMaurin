
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

package de.grogra.pf.io;

import de.grogra.util.MimeType;

public class ExtensionItem extends FileTypeItem
{
	private String[] extensions;
	//enh:field

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field extensions$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (ExtensionItem.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((ExtensionItem) o).extensions = (String[]) value;
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
					return ((ExtensionItem) o).extensions;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new ExtensionItem ());
		$TYPE.addManagedField (extensions$FIELD = new _Field ("extensions", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String[].class), null, 0));
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
		return new ExtensionItem ();
	}

//enh:end

	private ExtensionItem ()
	{
		this (null, null, null);
	}


	public ExtensionItem (String key, MimeType mimeType, String[] extensions)
	{
		super (key, mimeType);
		this.extensions = extensions;
	}


	@Override
	public boolean matches (String name)
	{
		String[] e = extensions;
		for (int i = 0; i < e.length; i++)
		{
			int s = e[i].length ();
			if ((name.length () > s)
				&& name.regionMatches (true, name.length () - s, e[i], 0, s))
			{
				return true;
			}
		}
		return false;
	}


	@Override
	public String match (String name)
	{
		return matches (name) ? name : name + extensions[0];
	}


	@Override
	protected String getFilterDescription ()
	{
		StringBuffer b = new StringBuffer ((String) getDescription (NAME));
		b.append (" (");
		String[] e = extensions;
		for (int i = 0; i < e.length; i++)
		{
			if (i > 0)
			{
				b.append (", ");
			}
			b.append ('*').append (e[i]);
		}
		return b.append (')').toString ();
	}

}
