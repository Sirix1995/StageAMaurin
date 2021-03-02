
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

import javax.vecmath.SingularMatrixException;

import de.grogra.graph.Cache;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.persistence.SCOType;
import de.grogra.xl.util.FloatList;

public class SkinnedSurface extends ContextDependentBase implements BSplineSurface
{
	//enh:sco SCOType

	BSplineCurveList profiles;
	//enh:field getter setter

	int vDegree = 3;
	//enh:field getter setter

	boolean interpolateProfiles = true;
	//enh:field getter setter

	boolean centripetalParameters = true;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field profiles$FIELD;
	public static final Type.Field vDegree$FIELD;
	public static final Type.Field interpolateProfiles$FIELD;
	public static final Type.Field centripetalParameters$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SkinnedSurface representative, de.grogra.persistence.SCOType supertype)
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
				case Type.SUPER_FIELD_COUNT + 2:
					((SkinnedSurface) o).interpolateProfiles = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((SkinnedSurface) o).centripetalParameters = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((SkinnedSurface) o).isInterpolateProfiles ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((SkinnedSurface) o).isCentripetalParameters ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((SkinnedSurface) o).vDegree = (int) value;
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
					return ((SkinnedSurface) o).getVDegree ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SkinnedSurface) o).profiles = (BSplineCurveList) value;
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
					return ((SkinnedSurface) o).getProfiles ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SkinnedSurface ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SkinnedSurface.class);
		profiles$FIELD = Type._addManagedField ($TYPE, "profiles", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (BSplineCurveList.class), null, Type.SUPER_FIELD_COUNT + 0);
		vDegree$FIELD = Type._addManagedField ($TYPE, "vDegree", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		interpolateProfiles$FIELD = Type._addManagedField ($TYPE, "interpolateProfiles", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		centripetalParameters$FIELD = Type._addManagedField ($TYPE, "centripetalParameters", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

	public boolean isInterpolateProfiles ()
	{
		return interpolateProfiles;
	}

	public void setInterpolateProfiles (boolean value)
	{
		this.interpolateProfiles = (boolean) value;
	}

	public boolean isCentripetalParameters ()
	{
		return centripetalParameters;
	}

	public void setCentripetalParameters (boolean value)
	{
		this.centripetalParameters = (boolean) value;
	}

	public int getVDegree ()
	{
		return vDegree;
	}

	public void setVDegree (int value)
	{
		this.vDegree = (int) value;
	}

	public BSplineCurveList getProfiles ()
	{
		return profiles;
	}

	public void setProfiles (BSplineCurveList value)
	{
		profiles$FIELD.setObject (this, value);
	}

//enh:end


	public SkinnedSurface ()
	{
		this (null);
	}


	public SkinnedSurface (BSplineCurveList profiles)
	{
		super ();
		this.profiles = profiles;
	}


	public boolean dependsOnContext ()
	{
		return profiles.dependsOnContext ();
	}


	@Override
	public void writeStamp (Cache.Entry cache, GraphState gs)
	{
		super.writeStamp (cache, gs);
		profiles.writeStamp (cache, gs);
	}


	protected float[] getCache (GraphState gs)
	{
		float[] cache = (float[]) gs.getObjectContext ().getValue (this);
		if (cache != null)
		{
			return cache;
		}
		int n = 0, p = 0;
		int c = profiles.getSize (gs);
		int vp = Math.min (vDegree, c - 1);
		FloatList out;
		if (c <= 0)
		{
			out = new FloatList (3);
		}
		else
		{
			out = new FloatList
				(8 + c + vp + profiles.getSize (0, gs) * (1 + 4 * c));
			int[] npv = BSpline.makeCompatible (out, profiles, 1e-3f, 4, true, gs);
			if ((npv != null) && (npv[2] > 1))
			{
				n = npv[0];
				p = npv[1];
				c = npv[2];
			}
			else
			{
				c = 0;
			}
		}
		if (c > 0)
		{
			vp = Math.min (vDegree, c - 1);
			float[] params = new float[c];
			KnotVectorImpl knots = new KnotVectorImpl (new float[c + vp + 1]);
			cache = out.elements;
			BSpline.calculateKnotsAndParameters
				(cache, c - 1, n * 4, vp, centripetalParameters,
				 knots, params);
			try
			{
				if ((vp > 1) && interpolateProfiles)
				{
					BSpline.interpolate (cache, c - 1, n * 4, vp, knots, params,
										 new float[c], new float[vp + 1], new float[vp + 1]);
				}
				for (int i = 0; i <= c + vp; i++)
				{
					out.push (knots.getKnot (0, i, null));
				}
			}
			catch (SingularMatrixException e)
			{
				n = 0;
				p = 0;
				c = 0;
			}
		}
		out.push (0);
		out.push (0);
		out.push (0);
		cache = out.elements;
		cache[cache.length - 3] = n;
		cache[cache.length - 2] = p;
		cache[cache.length - 1] = c;
		gs.getObjectContext ().setValue (this, cache);
		return cache;
	}


	public int getUDegree (GraphState gs)
	{
		float[] c = getCache (gs);
		return (int) c[c.length - 2];
	}


	public int getVDegree (GraphState gs)
	{
		return Math.min (vDegree, getVSize (gs) - 1);
	}


	public int getUSize (GraphState gs)
	{
		float[] c = getCache (gs);
		return (int) c[c.length - 3];
	}


	public int getVSize (GraphState gs)
	{
		float[] c = getCache (gs);
		return (int) c[c.length - 1];
	}


	public int getVertexIndex (int u, int v, GraphState gs)
	{
		return v * getUSize (gs) + u;
	}


	public int getDimension (GraphState gs)
	{
		return 4;
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		float[] c = getCache (gs);
		index *= 4;
		int d = (4 > out.length) ? out.length : 4;
		for (int i = 0; i < d; i++)
		{
			out[i] = c[index++];
		}
		return d;
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		float[] c = getCache (gs);
		int us = (int) c[c.length - 3];
		return c[(dim == 0) ? us * getVSize (gs) * 4 + index
				: us * (getVSize (gs) * 4 + 1) + (int) c[c.length - 2]
				  + 1 + index];
	}


	public boolean isRational (GraphState gs)
	{
		return true;
	}

}
