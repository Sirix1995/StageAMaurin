
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

package de.grogra.imp3d.objects;

import java.util.ArrayList;

import javax.vecmath.Matrix4d;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.imp3d.IMP3D;
import de.grogra.math.Pool;
import de.grogra.persistence.SCOType;
import de.grogra.util.EnumerationType;

public abstract class Sequence extends ContextDependentBase
{
	public static final int DOWNWARD = 0;
	public static final int DOWNWARD_AXIS = 1;
	public static final int UPWARD_AXIS = 2;
	public static final int UPWARD_BRANCH = 3;

	public static final EnumerationType PATH_TYPE = new EnumerationType ("pathType", IMP3D.I18N, 4);

	//enh:sco SCOType

	String name;
	//enh:field getter setter
	
	int path = DOWNWARD;
	//enh:field type=PATH_TYPE getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field name$FIELD;
	public static final Type.Field path$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Sequence representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((Sequence) o).path = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((Sequence) o).getPath ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((Sequence) o).name = (String) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((Sequence) o).getName ();
			}
			return super.getObject (o, id);
		}
	}

	static
	{
		$TYPE = new Type (Sequence.class);
		name$FIELD = Type._addManagedField ($TYPE, "name", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, Type.SUPER_FIELD_COUNT + 0);
		path$FIELD = Type._addManagedField ($TYPE, "path", 0 | Type.Field.SCO, PATH_TYPE, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

	public int getPath ()
	{
		return path;
	}

	public void setPath (int value)
	{
		this.path = (int) value;
	}

	public String getName ()
	{
		return name;
	}

	public void setName (String value)
	{
		name$FIELD.setObject (this, value);
	}

//enh:end


	protected Object getCache (GraphState gs)
	{
		Object cache = gs.getObjectContext ().getValue (this);
		if (cache != null)
		{
			return cache;
		}
		cache = calculateCache (gs, null);
		gs.getObjectContext ().setValue (this, cache);
		return cache;
	}


	protected Object calculateCache (GraphState gs, Object info)
	{
		Pool pool = Pool.push (gs);
		ArrayList list = pool.list;
		list.clear ();
		Graph g = gs.getGraph ();
		Object node = gs.getObjectContext ().getObject ();
		if (!gs.getObjectContext ().isNode ())
		{
			node = g.getSourceNode (node);
		}
		Matrix4d m = pool.m4d0;
		GlobalTransformation.get (node, true, gs, false).get (m);
		de.grogra.vecmath.Math2.invertAffine (m, m);
		int index = gs.getInstancingPathIndex (), i = index;
		boolean up = path > DOWNWARD_AXIS;
		int mask;
		switch (path)
		{
			case DOWNWARD:
				mask = Graph.BRANCH_EDGE | Graph.SUCCESSOR_EDGE;
				break;
			case DOWNWARD_AXIS:
			case UPWARD_AXIS:
				mask = Graph.SUCCESSOR_EDGE;
				break;
			case UPWARD_BRANCH:
				mask = Graph.BRANCH_EDGE;
				break;
			default:
				throw new AssertionError ();
		}
	collect:
		while (true)
		{
			Object o = gs.getObjectDefault (node, true, Attributes.SHAPE, null);
			if (o != null)
			{
				visitNode (node, o, list, gs);
				String sn;
				if ((o instanceof Mark)
					&& (((sn = getName ()) == null) || sn.equals (g.getName (node, true))))
				{
					break;
				}
			}
			if (i > 0)
			{
				if (up)
				{
					break;
				}
				node = gs.getInstancingPath ().getObject (i -= 2);
				gs.moveToPreviousInstance ();
				gs.moveToPreviousInstance ();
			}
			else
			{
				for (Object e = g.getFirstEdge (node); e != null;
					 e = g.getNextEdge (e, node))
				{
					int b = g.getEdgeBits (e);
					if ((b & mask) != 0)
					{
						Object n = up ? g.getTargetNode (e) : g.getSourceNode (e);
						if (n != node)
						{
							if (path == UPWARD_BRANCH)
							{
								mask = Graph.SUCCESSOR_EDGE;
							}
							node = n;
							continue collect;
						}
					}
				}
				break;
			}
		}
		if (index > 0)
		{
			gs.moveToInstance (index);
		}
		Object c = calculateCache (m, list, gs, info);
		pool.pop (gs);
		return c;
	}


	protected abstract void visitNode
		(Object node, Object shape, ArrayList list, GraphState gs);


	protected abstract Object calculateCache
		(Matrix4d inv, ArrayList list, GraphState gs, Object info);

}
