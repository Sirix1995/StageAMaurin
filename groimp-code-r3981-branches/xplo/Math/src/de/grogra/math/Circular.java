
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

package de.grogra.math;

import javax.vecmath.Matrix3f;
import javax.vecmath.Tuple2f;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.persistence.SCOType;
import de.grogra.reflect.XClass;
import de.grogra.reflect.XData;
import de.grogra.reflect.XObject;

public abstract class Circular extends ContextDependentBase
	implements BSplineCurve, XObject
{
	//enh:sco SCOType

	int plane;
	//enh:field type=BSpline.SPLINE_PLANE_TYPE getter setter	

	boolean reverse;
	//enh:field getter setter

	float intermediateArcs = 1;
	//enh:field getter setter

	boolean startingAtCenter = true;
	//enh:field getter setter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field plane$FIELD;
	public static final Type.Field reverse$FIELD;
	public static final Type.Field intermediateArcs$FIELD;
	public static final Type.Field startingAtCenter$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Circular representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 4;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((Circular) o).reverse = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((Circular) o).startingAtCenter = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((Circular) o).isReverse ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((Circular) o).isStartingAtCenter ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((Circular) o).plane = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((Circular) o).getPlane ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					((Circular) o).intermediateArcs = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((Circular) o).getIntermediateArcs ();
			}
			return super.getFloat (o, id);
		}
	}

	static
	{
		$TYPE = new Type (Circular.class);
		plane$FIELD = Type._addManagedField ($TYPE, "plane", 0 | Type.Field.SCO, BSpline.SPLINE_PLANE_TYPE, null, Type.SUPER_FIELD_COUNT + 0);
		reverse$FIELD = Type._addManagedField ($TYPE, "reverse", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 1);
		intermediateArcs$FIELD = Type._addManagedField ($TYPE, "intermediateArcs", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		startingAtCenter$FIELD = Type._addManagedField ($TYPE, "startingAtCenter", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

	public boolean isReverse ()
	{
		return reverse;
	}

	public void setReverse (boolean value)
	{
		this.reverse = (boolean) value;
	}

	public boolean isStartingAtCenter ()
	{
		return startingAtCenter;
	}

	public void setStartingAtCenter (boolean value)
	{
		this.startingAtCenter = (boolean) value;
	}

	public int getPlane ()
	{
		return plane;
	}

	public void setPlane (int value)
	{
		this.plane = (int) value;
	}

	public float getIntermediateArcs ()
	{
		return intermediateArcs;
	}

	public void setIntermediateArcs (float value)
	{
		this.intermediateArcs = (float) value;
	}

//enh:end


	public boolean dependsOnContext ()
	{
		return false;
	}


	public boolean isRational (GraphState gs)
	{
		return true;
	}


	public int getDegree (GraphState gs)
	{
		return 2;
	}


	protected abstract int getArcCount ();


	public int getSize (GraphState gs)
	{
		return 2 * getArcCount () + 1; 
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		if (index < 3)
		{
			return 0;
		}
		int n = getArcCount ();
		return (index > 2 * n) ? 1 : (float) ((index - 1) >> 1) / n;
	}


	protected abstract float[] calculateCache (GraphState gs);


	public int getVertex (float[] out, int index, GraphState gs)
	{
		float[] cache = (float[]) gs.getObjectContext ().getValue (this);
		if (cache == null)
		{
			cache = calculateCache (gs);
			gs.getObjectContext ().setValue (this, cache);
		}
		if (getDimension (gs) == 4)
		{
			index *= 4;
			return BSpline.set (out, cache[index], cache[index + 1],
								cache[index + 2], cache[index + 3]);
		}
		else
		{
			index *= 3;
			return BSpline.set (out, cache[index], cache[index + 1],
								cache[index + 2]);
		}
	}


	float[] calculateCache (int arcCount, float height,
							float startAngle, float endAngle,
							float startRadiusX, float endRadiusX,
							float startRadiusY, float endRadiusY,
							GraphState gs)
	{
		int dim = getDimension (null);
		float[] cache = new float[(2 * arcCount + 1) * dim];
		float da = (endAngle - startAngle) / arcCount;
		float drX = (endRadiusX - startRadiusX) / arcCount;
		float drY = (endRadiusY - startRadiusY) / arcCount;
		float dh = height / arcCount;
		float w = (float) Math.cos (0.5 * da);
		Pool pool = Pool.push (gs);
		Matrix3f m = pool.m3f0;
		Tuple2f t = pool.p2f0;
		int ci = 0;
		float rev = reverse ? -1 : 1;
		int xi, yi, zi;
		switch ((dim < 4) ? 0 : plane)
		{
			case 0:
				xi = 0;
				yi = 1;
				zi = 2;
				break;
			case 1:
				xi = 0;
				yi = 2;
				zi = 1;
				break;
			default:
				xi = 1;
				yi = 2;
				zi = 0;
				break;
		}
		float ofsX = 0;
		float ofsY = 0;
		for (int i = 0;; i++)
		{
			float a = startAngle + da * i;
			float rX = startRadiusX + drX * i;
			float rY = startRadiusY + drY * i;
			float h = dh * i;
			float c = (float) Math.cos (a);
			float s = rev * (float) Math.sin (a);
			float c2 = (float) Math.cos (a + da), s2 = rev * (float) Math.sin (a + da);
			float p0x = rX * c;
			float p0y = rY * s;
			float p2x = (rX + drX) * c2;
			float p2y = (rY + drY) * s2;
			if ((i == 0) && !startingAtCenter)
			{
				ofsX = -p0x;
				ofsY = -p0y;
			}
			cache[ci + xi] = p0x + ofsX;
			cache[ci + yi] = p0y + ofsY;
			cache[ci + zi] = h;
			cache[(ci += dim) - 1] = 1;
			if (i == arcCount)
			{
				break;
			}
			m.m00 = -s * rX + c * drX / da;
			m.m10 = c * rY + s * drY / da;
			m.m01 = -s2 * (rX + drX) + c2 * drX / da;
			m.m11 = c2 * (rY + drY) + s2 * drY / da;
			m.m02 = p0x - p2x;
			m.m12 = p0y - p2y;
			t.set (0, 0);
			de.grogra.vecmath.Math2.invTransformPoint (m, t);
			cache[ci + xi] = w * (p0x + ofsX + m.m00 * t.x);
			cache[ci + yi] = w * (p0y + ofsY + m.m10 * t.x);
			cache[ci + zi] = w * (h + 0.5f * dh);
			cache[(ci += dim) - 1] = w;
		}
		pool.pop (gs);
		return cache;
	}

	
	private transient XClass cls;
	private transient XData data;
	
	
	public final void initXClass (XClass cls)
	{
		if (this.cls != null)
		{
			throw new IllegalStateException ();
		}
		this.cls = cls;
		data = new XData ();
		data.init (cls);
	}

	
	public final XClass getXClass ()
	{
		return cls;
	}
	

	public final XData getXData ()
	{
		return data;
	}

}
