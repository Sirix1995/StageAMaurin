
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

import de.grogra.graph.Cache;
import de.grogra.graph.GraphState;
import de.grogra.math.BSpline;
import de.grogra.math.BSplineCurve;
import de.grogra.math.BSplineCurveList;
import de.grogra.math.Pool;
import de.grogra.vecmath.Matrix34d;

public class CurveSequence extends Sequence implements BSplineCurveList
{
	//enh:sco

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends Sequence.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (CurveSequence representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Sequence.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new CurveSequence ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (CurveSequence.class);
		$TYPE.validate ();
	}

//enh:end

	public CurveSequence ()
	{	
	}


	public CurveSequence (String name)
	{
		this.name = name;
	}


	public boolean dependsOnContext ()
	{
		return true;
	}


	@Override
	public void writeStamp (Cache.Entry cache, GraphState gs)
	{
		super.writeStamp (cache, gs);
		Object[] c = (Object[]) getCache (gs);
		int n = c.length;
		cache.write (n);
		GraphState.ObjectContext os = gs.getObjectContext ();
		while ((n -= 3) >= 0)
		{
			gs.setObjectContext ((GraphState.ObjectContext) c[n + 1]);
			((BSplineCurve) c[n]).writeStamp (cache, gs);
			cache.write ((float[]) c[n + 2]);
		}
		gs.setObjectContext (os);
	}


	@Override
	protected void visitNode
		(Object node, Object shape, ArrayList list, GraphState gs)
	{
		if (shape instanceof NURBSCurve)
		{
			list.add (gs.createObjectState (node, true));
			list.add (gs.getObject (node, true, null, Attributes.CURVE));
			list.add (GlobalTransformation.get (node, true, gs, false));
		}
	}


	@Override
	protected Object calculateCache
		(Matrix4d inv, ArrayList list, GraphState gs, Object info)
	{
		int i = list.size ();
		Object[] cache = new Object[i];
		int ci = -1;
		Matrix4d a = Pool.get (gs).m4d1;
		while (i > 0)
		{
			((Matrix34d) list.get (--i)).get (a);
			a.mul (inv, a);
			cache[++ci] = (BSplineCurve) list.get (--i);
			cache[++ci] = (GraphState.ObjectContext) list.get (--i);
			cache[++ci] = new float[] {(float) a.m00, (float) a.m01,
									   (float) a.m02, (float) a.m03,
									   (float) a.m10, (float) a.m11,
									   (float) a.m12, (float) a.m13,
									   (float) a.m20, (float) a.m21,
									   (float) a.m22, (float) a.m23};
		}
		return cache;
	}


	public int getSize (GraphState gs)
	{
		Object[] cache = (Object[]) getCache (gs);
		return cache.length / 3;
	}


	public int getVertex (float[] out, int curve, int index, GraphState gs)
	{
		Object[] cache = (Object[]) getCache (gs);
		BSplineCurve c = (BSplineCurve) cache[curve *= 3];
		GraphState.ObjectContext os = gs.getObjectContext ();
		gs.setObjectContext ((GraphState.ObjectContext) cache[curve + 1]);
		int n = c.getVertex (out, index, gs);
		float w = c.isRational (gs) ? out[--n] : 1;
		float x = out[0], y = (n > 1) ? out[1] : 0, z = (n > 2) ? out[2] : 0;
		float[] a = (float[]) cache[curve + 2];
		gs.setObjectContext (os);
		return BSpline.set (out, a[0] * x + a[1] * y + a[2] * z + a[3] * w,
							a[4] * x + a[5] * y + a[6] * z + a[7] * w,
							a[8] * x + a[9] * y + a[10] * z + a[11] * w, w);
	}


	public float getKnot (int curve, int index, GraphState gs)
	{
		Object[] cache = (Object[]) getCache (gs);
		GraphState.ObjectContext os = gs.getObjectContext ();
		gs.setObjectContext ((GraphState.ObjectContext) cache[(curve *= 3) + 1]);
		float v = ((BSplineCurve) cache[curve]).getKnot (0, index, gs);
		gs.setObjectContext (os);
		return v;
	}


	public int getSize (int curve, GraphState gs)
	{
		Object[] cache = (Object[]) getCache (gs);
		GraphState.ObjectContext os = gs.getObjectContext ();
		gs.setObjectContext ((GraphState.ObjectContext) cache[(curve *= 3) + 1]);
		int v = ((BSplineCurve) cache[curve]).getSize (gs);
		gs.setObjectContext (os);
		return v;
	}


	public int getDimension (int curve, GraphState gs)
	{
		return 4;
	}


	public int getDegree (int curve, GraphState gs)
	{
		Object[] cache = (Object[]) getCache (gs);
		GraphState.ObjectContext os = gs.getObjectContext ();
		gs.setObjectContext ((GraphState.ObjectContext) cache[(curve *= 3) + 1]);
		int v = ((BSplineCurve) cache[curve]).getDegree (gs);
		gs.setObjectContext (os);
		return v;
	}


	public boolean isRational (int curve, GraphState gs)
	{
		Object[] cache = (Object[]) getCache (gs);
		GraphState.ObjectContext os = gs.getObjectContext ();
		gs.setObjectContext ((GraphState.ObjectContext) cache[(curve *= 3) + 1]);
		boolean v = ((BSplineCurve) cache[curve]).isRational (gs);
		gs.setObjectContext (os);
		return v;
	}


	public boolean areCurvesCompatible (GraphState gs)
	{
		return false;
	}

}
