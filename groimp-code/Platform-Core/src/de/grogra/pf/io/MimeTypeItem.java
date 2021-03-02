
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
import de.grogra.pf.registry.*;

public final class MimeTypeItem extends Item
{
	private static final int EDITABLE_MASK = 1 << Item.USED_BITS;
	private static final int VIEWABLE_MASK = 1 << Item.USED_BITS + 1;
	public static final int USED_BITS = Item.USED_BITS + 2;

	// boolean editable
	//enh:field type=bits(EDITABLE_MASK) getter

	// boolean viewable
	//enh:field type=bits(VIEWABLE_MASK) getter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field editable$FIELD;
	public static final NType.Field viewable$FIELD;

	static
	{
		$TYPE = new NType (new MimeTypeItem ());
		$TYPE.addManagedField (editable$FIELD = new NType.BitField ($TYPE, "editable", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, EDITABLE_MASK));
		$TYPE.addManagedField (viewable$FIELD = new NType.BitField ($TYPE, "viewable", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, VIEWABLE_MASK));
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
		return new MimeTypeItem ();
	}

	public boolean isEditable ()
	{
		return (bits & EDITABLE_MASK) != 0;
	}

	public boolean isViewable ()
	{
		return (bits & VIEWABLE_MASK) != 0;
	}

//enh:end


	private MimeTypeItem ()
	{
		this (null);
	}


	public MimeTypeItem (String mimeType)
	{
		super (mimeType);
		setDirectory ();
	}


	public static MimeTypeItem get (RegistryContext ctx, MimeType mimeType)
	{
		return get (ctx, mimeType.getMediaType ());
	}


	public static MimeTypeItem get (RegistryContext ctx, String mimeType)
	{
		Item i = resolveItem (ctx, "/io/mimetypes");
		if (i == null)
		{
			return null;
		}
		return (MimeTypeItem) i.getItem (mimeType);
	}

}
