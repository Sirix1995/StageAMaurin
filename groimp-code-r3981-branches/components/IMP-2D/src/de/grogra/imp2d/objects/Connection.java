
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

package de.grogra.imp2d.objects;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector2d;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.imp2d.Connectable;
import de.grogra.math.BSpline;
import de.grogra.math.BSplineCurve;
import de.grogra.math.Pool;
import de.grogra.persistence.SCOType;
import de.grogra.vecmath.Math2;

public class Connection extends ContextDependentBase implements BSplineCurve
{
	//enh:sco SCOType

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Connection representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new Connection ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (Connection.class);
		$TYPE.validate ();
	}

//enh:end


	public int getVertex (float[] out, int index, GraphState gs)
	{
		Object o = gs.getObjectContext ().getObject ();
		Object source = null, target = null;
		Graph g = gs.getGraph ();
		if (gs.getObjectContext ().isNode ())
		{
			for (Object e = g.getFirstEdge (o); e != null;
				 e = g.getNextEdge (e, o))
			{
				Object t = g.getTargetNode (e);
				if (t == o)
				{
					source = g.getSourceNode (e);
				}
				else
				{
					target = t;
				}
			}
		}
		else
		{
			source = g.getSourceNode (o);
			target = g.getTargetNode (o);
		}
		if ((source == null) || (target == null))
		{
			return BSpline.set (out, index, 0);
		}

		Pool p = Pool.push (gs);
		Matrix3d st = GlobalTransformation.get (source, true, gs, false);
		Matrix3d ct = GlobalTransformation.get (o, gs.getObjectContext ().isNode (), gs, false);
		Matrix3d tt = GlobalTransformation.get (target, true, gs, false);
		Object s = gs.getObjectDefault (target, true, Attributes.SHAPE, null);
		Vector2d v = p.v2d0, w = p.v2d1;
		v.set (0, 0);
		if (s instanceof Connectable)
		{
			Math2.transformPoint (st, v);
			Math2.invTransformPoint (tt, v);
			((Connectable) s).getConnectionPoint (target, true, w, source, true, v, p, gs);
			Vector2d tmp = v; v = w; w = tmp;
		}
		Math2.transformPoint (tt, v);
		if (index > 0)
		{
			Math2.invTransformPoint (ct, v);
			p.pop (gs);
			return BSpline.set (out, (float) v.x, (float) v.y);
		}
		Math2.invTransformPoint (st, v);
		s = gs.getObjectDefault (source, true, Attributes.SHAPE, null);
		if (s instanceof Connectable)
		{
			((Connectable) s).getConnectionPoint (source, true, w, target, true, v, p, gs);
		}
		else
		{
			w.set (0, 0);
		}
		Math2.transformPoint (st, w);
		Math2.invTransformPoint (ct, w);
		p.pop (gs);
		return BSpline.set (out, (float) w.x, (float) w.y);
	}


	public int getDegree (GraphState gs)
	{
		return 1;
	}


	public boolean isRational (GraphState gs)
	{
		return false;
	}


	public int getSize (GraphState gs)
	
	{
		return 2;
	}


	public int getDimension (GraphState gs)
	{
		return 2;
	}


	public boolean dependsOnContext ()
	{
		return true;
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return (index > 1) ? 1 : 0;
	}

}
