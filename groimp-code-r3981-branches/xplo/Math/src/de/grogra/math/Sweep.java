
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

import javax.vecmath.Point4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.persistence.SCOType;

public abstract class Sweep extends ContextDependentBase implements BSplineCurveList
{
	//enh:sco SCOType

	boolean useRail = false;
	//enh:field setter	


	protected abstract BSplineCurve getTrajectory (GraphState gs);


	protected abstract Object[] initCache (GraphState gs);


	protected Object[] getCache (GraphState gs)
	{
		Object[] c = getCacheImpl (gs);
		c[0] = computeTrajectory (c, gs);
		return c;
	}


	protected Object[] getCacheImpl (GraphState gs)
	{
		Object[] cache = (Object[]) gs.getObjectContext ().getValue (this);
		if (cache != null)
		{
			return cache;
		}
		cache = initCache (gs);
		gs.getObjectContext ().setValue (this, cache);
		return cache;
	}


	protected float[] computeTrajectory (Object[] cache, GraphState gs)
	{
		BSplineCurve trajectory = getTrajectory (gs);
		int n = trajectory.getSize (gs) - 1;
		int q = trajectory.getDegree (gs);
		if ((q <= 0) || (n < q))
		{
			n = -1;
		}
		float[] fcache = new float[16 * (n + 1)];
		if (n >= 0)
		{
			Pool pool = Pool.push (gs);
			int d = trajectory.getDimension (gs);
			float s = q * trajectory.getKnot (0, q, gs);
			float q1 = 1f / q;
			float[] ndu = pool.getFloatArray (0, (q + 1) * (q + 1));
			float[] left = pool.getFloatArray (1, Math.max (q + 1, d));
			float[] right = pool.getFloatArray (2, q + 1);
			float[] out = pool.getFloatArray (3, 2 * (q + 1));
			Tuple4f t = pool.p4f0;
			Point4f p = pool.p4f1;
			Vector4f dp = pool.v4f0;
			Vector3f x = pool.v3f0;
			Vector3f z = pool.v3f1;
			Vector3f rail = pool.v3f2;
			boolean r = trajectory.isRational (gs);
			for (int k = 0; k <= n; k++)
			{
				s += trajectory.getKnot (0, Math.min (k + q, n + 1), gs)
					- trajectory.getKnot (0, Math.max (k, q), gs);
				float v = s * q1;
				int span = BSpline.findSpan (n, q, v, trajectory, 0, gs);
				BSpline.calculateDerivatives
					(out, q, trajectory, 0, span, v, 1, gs, left, right, ndu);
				span -= q;
				p.x = p.y = p.z = p.w = dp.x = dp.y = dp.z = dp.w
					= rail.x = rail.y = rail.z = 0;
				float railScale = 0;
				boolean haveRail = false;
				for (int j = 0; j <= q; j++)
				{
					v = out[j];
					int c = trajectory.getVertex (left, span + j, gs);
					BSpline.set (t, left, c, r);
					p.scaleAdd (v, t, p);
					dp.scaleAdd (out[q + 1 + j], t, dp);
					if (r)
					{
						c--;
					}
					if (c > 6)
					{
						rail.x += v * left[3];
						rail.y += v * left[4];
						rail.z += v * left[5];
						railScale += v * left[6];
						haveRail = true;
					}
				}
				v = 1 / p.w;
				t.scale (-dp.w * v * v, p);
				z.x = dp.x * v + t.x;
				z.y = dp.y * v + t.y;
				z.z = dp.z * v + t.z;
				z.normalize ();
				if (k == 0)
				{
					if (!haveRail)
					{
						de.grogra.vecmath.Math2.getOrthogonal (z, x);
						railScale = 1;
					}
					v = p.w * initLocalX (x, z, rail, railScale, cache, gs);
				}
				else
				{
					if (!haveRail)
					{
						rail.set (x);
						railScale = 1;
					}
					v = p.w * calculateLocalX (x, z, rail, railScale, cache, gs);
				}
				x.normalize ();
				span = k << 4;
				fcache[span] = x.x * v;
				fcache[span + 4] = x.y * v;
				fcache[span + 8] = x.z * v;
				fcache[span + 2] = z.x * v;
				fcache[span + 6] = z.y * v;
				fcache[span + 10] = z.z * v;
				z.cross (z, x);
				fcache[span + 1] = z.x * v;
				fcache[span + 5] = z.y * v;
				fcache[span + 9] = z.z * v;
				fcache[span + 3] = p.x;
				fcache[span + 7] = p.y;
				fcache[span + 11] = p.z;
				fcache[span + 15] = p.w;
			}
			pool.pop (gs);
		}
		return fcache;
	}


	protected float initLocalX (Vector3f x, Vector3f z, Vector3f rail,
								float railScale, Object[] cache, GraphState gs)
	{
		if (useRail)
		{
			x.scaleAdd (-rail.dot (z), z, rail);
		}
		else
		{
			de.grogra.vecmath.Math2.getOrthogonal (z, x);
		}
		return railScale;
	}


	protected float calculateLocalX (Vector3f x, Vector3f z, Vector3f rail,
									 float railScale, Object[] cache, GraphState gs)
	{
		if (useRail)
		{
			x.scaleAdd (-rail.dot (z), z, rail);
		}
		else
		{
			x.scaleAdd (-x.dot (z), z, x);
		}
		return railScale;
	}



	public int getSize (GraphState gs)
	{
		return ((float[]) getCache (gs)[0]).length >> 4;
	}


	public int getDimension (int curve, GraphState gs)
	{
		return 4;
	}


	public boolean isRational (int curve, GraphState gs)
	{
		return true;
	}


	public boolean areCurvesCompatible (GraphState gs)
	{
		return true;
	}


	protected abstract int getVertexImpl
		(float[] out, int curve, int index, Object[] cache, GraphState gs);


	public int getVertex (float[] out, int curve, int index, GraphState gs)
	{
		Object[] cache = getCache (gs);
		float[] fcache = (float[]) cache[0];
		int n = getVertexImpl (out, curve, index, cache, gs);
		float w = (n < 0) ? out[n = -n] : 1;
		float x = out[0], y = (n > 1) ? out[1] : 0, z = (n > 2) ? out[2] : 0;
		curve <<= 4;
		return BSpline.set (out, fcache[curve] * x + fcache[curve + 1] * y
							+ fcache[curve + 2] * z + fcache[curve + 3] * w,
							fcache[curve + 4] * x + fcache[curve + 5] * y
							+ fcache[curve + 6] * z + fcache[curve + 7] * w,
							fcache[curve + 8] * x + fcache[curve + 9] * y
							+ fcache[curve + 10] * z + fcache[curve + 11] * w,
							fcache[curve + 15] * w);
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field useRail$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Sweep representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((Sweep) o).useRail = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((Sweep) o).useRail;
			}
			return super.getBoolean (o, id);
		}
	}

	static
	{
		$TYPE = new Type (Sweep.class);
		useRail$FIELD = Type._addManagedField ($TYPE, "useRail", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public void setUseRail (boolean value)
	{
		this.useRail = (boolean) value;
	}

//enh:end

}
