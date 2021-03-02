
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

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.Instance3D;
import de.grogra.pf.registry.Item;
import de.grogra.pf.registry.ObjectItem;
import de.grogra.pf.registry.Registry;

public class Instance extends Instance3D
{
	protected String object;
	//enh:field getter setter

	private transient Reference ref;
	private transient Node resolvedObject;
	
	public Instance ()
	{
		super ();
	}


	public Instance (String object)
	{
		this (object, null);
	}


	Instance (String object, Reference ref)
	{
		super ();
		this.object = object;
		this.ref = ref;
	}


	@Override
	protected Node getInstanceRootSetAttributes (GraphState gs)
	{
		if (object != null)
		{
			if (resolvedObject == null)
			{
				if (ref != null)
				{
					resolvedObject = ref.resolveNode ();
				}
				else
				{
					Item i = Registry.current ().getItem
						("/project/objects/objects/" + object);
					if (!(i instanceof ObjectItem))
					{
						return null;
					}
					resolvedObject = (Node) ((ObjectItem) i).getObject ();
				}
			}
			return resolvedObject;
		}
		else
		{
			return super.getInstanceRootSetAttributes (gs);
		}
	}


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field object$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Instance.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Instance) o).object = (String) value;
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
					return ((Instance) o).getObject ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Instance ());
		$TYPE.addManagedField (object$FIELD = new _Field ("object", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, 0));
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
		return new Instance ();
	}

	public String getObject ()
	{
		return object;
	}

	public void setObject (String value)
	{
		object$FIELD.setObject (this, value);
	}

//enh:end

}
