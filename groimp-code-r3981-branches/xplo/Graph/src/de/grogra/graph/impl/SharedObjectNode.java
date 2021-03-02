
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

package de.grogra.graph.impl;

import java.beans.PropertyChangeEvent;

import de.grogra.persistence.*;
import de.grogra.reflect.ClassAdapter;
import de.grogra.reflect.Type;

public final class SharedObjectNode extends Node implements SharedObjectProvider
{
	public static final int REF = Node.MIN_UNUSED_SPECIAL_OF_SOURCE;
	public static final int MIN_UNUSED_SPECIAL_OF_SOURCE = REF + 1;

	Shareable object;
	//enh:field definesshared setmethod=setObject edge=REF

	private static void initType ()
	{
		$TYPE.declareSpecialEdge (REF, "edge.sharedobject", new Shareable[0]);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field object$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (SharedObjectNode.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((SharedObjectNode) o).setObject ((Shareable) value);
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
					return ((SharedObjectNode) o).object;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new SharedObjectNode ());
		$TYPE.addManagedField (object$FIELD = new _Field ("object", 0 | _Field.FCO | _Field.DEFINES_SHARED, de.grogra.reflect.ClassAdapter.wrap (Shareable.class), null, 0));
		$TYPE.setSpecialEdgeField (object$FIELD, REF);
		initType ();
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
		return new SharedObjectNode ();
	}

//enh:end

	private SharedObjectNode ()
	{
		super ();
	}


	public SharedObjectNode (Shareable object)
	{
		this ();
		object$FIELD.setObject (this, null, object, null);
	}


	public static ManageableType.Field getObjectField ()
	{
		return object$FIELD;
	}


	public String getProviderName ()
	{
		return GraphManager.META_GRAPH;
	}


	public ResolvableReference readReference (PersistenceInput in)
		throws java.io.IOException
	{
		return manager.readReference (in);
	}


	public void writeObject (Shareable object, PersistenceOutput out)
		throws java.io.IOException
	{
		if (object == this.object)
		{
			out.writePersistentObjectReference (this);
		}
		else
		{
			manager.writeObject (object, out);
		}
	}


	public Shareable getSharedObject ()
	{
		return object;
	}


	public Object getObject ()
	{
		return object;
	}


	void setObject (Shareable object)
	{
		this.object = object;
		object.initProvider (this);
	}


	public Type getObjectType ()
	{
		return (object instanceof Manageable) ? ((Manageable) object).getManageableType ()
			: (Type) ClassAdapter.wrap (object.getClass ());
	}


	@Override
	protected void specialEdgeRefModified (Node ref, NType.Field edgeField,
										   PersistenceField field, int[] indices,
										   Transaction t)
	{
		if (manager != null)
		{
			assert ref == object;
			manager.sharedObjectModified
				(new PropertyChangeEvent
				 (ref, field.getSubfield (0).getSimpleName (),
				  null, field.getSubfield (0).get (ref, null)));
		}
	}


	@Override
	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
		super.fieldModified (field, indices, t);
		if ((field.length () > 1) && (manager != null))
		{
			manager.sharedObjectModified
				(new PropertyChangeEvent
				 (object, field.getSubfield (1).getSimpleName (),
				  null, field.getSubfield (1).get (object, null)));
		}
	}

}
