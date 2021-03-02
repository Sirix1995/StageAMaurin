
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

import java.io.File;
import de.grogra.util.*;
import de.grogra.pf.registry.*;

public abstract class FileTypeItem extends Item
{
	private MimeType mimeType;
	//enh:field

	private transient Filter filter;

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field mimeType$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (FileTypeItem.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((FileTypeItem) o).mimeType = (MimeType) value;
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
					return ((FileTypeItem) o).mimeType;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (FileTypeItem.class);
		$TYPE.addManagedField (mimeType$FIELD = new _Field ("mimeType", _Field.PRIVATE  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (MimeType.class), null, 0));
		$TYPE.validate ();
	}

//enh:end


	public final class Filter extends MimeTypeFileFilter
	{
		Filter ()
		{
		}


		@Override
		public boolean accept (File file)
		{
			return file.isDirectory () || matches (file.getName ());
		}


		@Override
		public String getDescription ()
		{
			return getFilterDescription ();
		}


		public FileTypeItem getItem ()
		{
			return FileTypeItem.this;
		}


		@Override
		public MimeType getMimeType (File file)
		{
			return FileTypeItem.this.getMimeType ();
		}
	}


	public static class Map implements java.net.FileNameMap, ItemCriterion
	{
		private final Registry registry;


		public Map (Registry registry)
		{
			this.registry = registry;
		}


		public String getContentTypeFor (String fileName)
		{
			FileTypeItem i
				= (FileTypeItem) registry.findFirst (this, fileName, true);
			return (i != null) ? i.getMimeType ().toString () : null;
		}


		public boolean isFulfilled (Item item, Object info)
		{
			return (item instanceof FileTypeItem)
				&& ((FileTypeItem) item).matches ((String) info);
		}


		public String getRootDirectory ()
		{
			return "/io/filetypes";
		}
	}


	public FileTypeItem (String key, MimeType mimeType)
	{
		super (key);
		this.mimeType = mimeType;
	}


	public MimeType getMimeType ()
	{
		return mimeType;
	}


	public abstract boolean matches (String name);


	public abstract String match (String name);


	public Filter getFilter ()
	{
		if (filter == null)
		{
			filter = new Filter ();
		}
		return filter;
	}


	@Override
	protected Object getDefaultDescription (String type)
	{
		Item i = MimeTypeItem.get (this, mimeType);
		return (i != null) ? i.getDescription (type)
			: super.getDefaultDescription (type);
	}


	protected String getFilterDescription ()
	{
		return String.valueOf (getDescription (NAME));
	}


	private static final ItemCriterion MIME_MATCH = new Map (null);

	public static FileTypeItem get (RegistryContext ctx, String fileName)
	{
		return (FileTypeItem) ctx.getRegistry ().getRootRegistry ()
			.findFirst (MIME_MATCH, fileName, true);
	}

}
