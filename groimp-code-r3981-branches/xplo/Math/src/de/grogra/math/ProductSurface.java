
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

import de.grogra.graph.Cache;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.persistence.SCOType;

public abstract class ProductSurface extends ContextDependentBase implements BSplineSurface
{
	//enh:sco SCOType

	BSplineCurve profile;
	//enh:field getter setter

	BSplineCurve trajectory;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field profile$FIELD;
	public static final Type.Field trajectory$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ProductSurface representative, de.grogra.persistence.SCOType supertype)
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
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((ProductSurface) o).profile = (BSplineCurve) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((ProductSurface) o).trajectory = (BSplineCurve) value;
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
					return ((ProductSurface) o).getProfile ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((ProductSurface) o).getTrajectory ();
			}
			return super.getObject (o, id);
		}
	}

	static
	{
		$TYPE = new Type (ProductSurface.class);
		profile$FIELD = Type._addManagedField ($TYPE, "profile", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (BSplineCurve.class), null, Type.SUPER_FIELD_COUNT + 0);
		trajectory$FIELD = Type._addManagedField ($TYPE, "trajectory", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (BSplineCurve.class), null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

	public BSplineCurve getProfile ()
	{
		return profile;
	}

	public void setProfile (BSplineCurve value)
	{
		profile$FIELD.setObject (this, value);
	}

	public BSplineCurve getTrajectory ()
	{
		return trajectory;
	}

	public void setTrajectory (BSplineCurve value)
	{
		trajectory$FIELD.setObject (this, value);
	}

//enh:end


	protected ProductSurface ()
	{
		super ();
	}


	public ProductSurface (BSplineCurve profile, BSplineCurve trajectory)
	{
		this.profile = profile;
		this.trajectory = trajectory;
	}


	public boolean dependsOnContext ()
	{
		return profile.dependsOnContext () || trajectory.dependsOnContext ();
	}


	@Override
	public void writeStamp (Cache.Entry cache, GraphState gs)
	{
		super.writeStamp (cache, gs);
		profile.writeStamp (cache, gs);
		trajectory.writeStamp (cache, gs);
	}


	public int getUDegree (GraphState gs)
	{
		return trajectory.getDegree (gs);
	}


	public int getVDegree (GraphState gs)
	{
		return profile.getDegree (gs);
	}


	public int getUSize (GraphState gs)
	{
		return trajectory.getSize (gs);
	}


	public int getVSize (GraphState gs)
	{
		return profile.getSize (gs);
	}


	public int getVertexIndex (int u, int v, GraphState gs)
	{
		return u * profile.getSize (gs) + v;
	}


	public int getDimension (GraphState gs)
	{
		return 4;
	}


	public float getKnot (int dim, int index, GraphState gs)
	{
		return ((dim == 0) ? trajectory : profile).getKnot (0, index, gs);
	}


	public boolean isRational (GraphState gs)
	{
		return true;
	}

}
