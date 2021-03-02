
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

import de.grogra.graph.impl.*;

public class NodeReference extends LazyObjectItem
{
	long ref;
	//enh:field


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field ref$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (NodeReference.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setLong (Object o, long value)
		{
			switch (id)
			{
				case 0:
					((NodeReference) o).ref = (long) value;
					return;
			}
			super.setLong (o, value);
		}

		@Override
		public long getLong (Object o)
		{
			switch (id)
			{
				case 0:
					return ((NodeReference) o).ref;
			}
			return super.getLong (o);
		}
	}

	static
	{
		$TYPE = new NType (new NodeReference ());
		$TYPE.addManagedField (ref$FIELD = new _Field ("ref", 0 | _Field.SCO, de.grogra.reflect.Type.LONG, null, 0));
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
		return new NodeReference ();
	}

//enh:end


	private NodeReference ()
	{
		this (null, null);
	}


	public NodeReference (String key, Node ref)
	{
		super (key, false);
		if (ref != null)
		{
			this.ref = ref.getId ();
			setBaseObject (ref);
		}
	}


	public NodeReference (Node ref)
	{
		this (null, ref);
	}

	
	@Override
	protected boolean hasNullValue ()
	{
		return false;
	}


	@Override
	protected Object fetchBaseObject ()
	{
		return getRegistry ().getProjectGraph ().getObject (ref);
	}


	@Override
	public boolean validate ()
	{
		return getNode () != null;
	}


	public final Node getNode ()
	{
		return (Node) getBaseObjectImpl ();
	}


	@Override
	protected boolean getTypeFromObject ()
	{
		return true;
	}


	private static final ItemCriterion CRITERION = new ItemCriterion ()
	{
		public boolean isFulfilled (Item item, Object info)
		{
			return (item instanceof NodeReference)
				&& (((NodeReference) item).ref == ((Node) info).getId ());
		}

		public String getRootDirectory ()
		{
			return null;
		}
	};


	public static NodeReference get
		(RegistryContext ctx, String dir, Node s)
	{
		return (NodeReference)
			findFirst (ctx, dir, CRITERION, s, true);
	}

}
