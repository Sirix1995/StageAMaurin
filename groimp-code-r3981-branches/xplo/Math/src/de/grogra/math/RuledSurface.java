
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

public class RuledSurface extends ContextDependentBase implements BSplineCurveList
{
	//enh:sco SCOType

	BSplineCurve firstProfile;
	//enh:field getter setter

	BSplineCurve secondProfile;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field firstProfile$FIELD;
	public static final Type.Field secondProfile$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (RuledSurface representative, de.grogra.persistence.SCOType supertype)
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
					((RuledSurface) o).firstProfile = (BSplineCurve) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((RuledSurface) o).secondProfile = (BSplineCurve) value;
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
					return ((RuledSurface) o).getFirstProfile ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((RuledSurface) o).getSecondProfile ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new RuledSurface ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (RuledSurface.class);
		firstProfile$FIELD = Type._addManagedField ($TYPE, "firstProfile", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (BSplineCurve.class), null, Type.SUPER_FIELD_COUNT + 0);
		secondProfile$FIELD = Type._addManagedField ($TYPE, "secondProfile", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (BSplineCurve.class), null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

	public BSplineCurve getFirstProfile ()
	{
		return firstProfile;
	}

	public void setFirstProfile (BSplineCurve value)
	{
		firstProfile$FIELD.setObject (this, value);
	}

	public BSplineCurve getSecondProfile ()
	{
		return secondProfile;
	}

	public void setSecondProfile (BSplineCurve value)
	{
		secondProfile$FIELD.setObject (this, value);
	}

//enh:end


	public boolean dependsOnContext ()
	{
		return firstProfile.dependsOnContext ()
			|| secondProfile.dependsOnContext ();
	}


	@Override
	public void writeStamp (Cache.Entry cache, GraphState gs)
	{
		super.writeStamp (cache, gs);
		firstProfile.writeStamp (cache, gs);
		secondProfile.writeStamp (cache, gs);
	}


	public int getSize (GraphState gs)
	{
		return ((firstProfile != null) && (secondProfile != null)) ? 2 : 0;
	}


	public int getVertex (float[] out, int curve, int index, GraphState gs)
	{
		return (curve == 0 ? firstProfile : secondProfile)
			.getVertex (out, index, gs);
	}


	public float getKnot (int curve, int index, GraphState gs)
	{
		return (curve == 0 ? firstProfile : secondProfile)
			.getKnot (0, index, gs);
	}


	public int getSize (int curve, GraphState gs)
	{
		return (curve == 0 ? firstProfile : secondProfile).getSize (gs);
	}


	public int getDimension (int curve, GraphState gs)
	{
		return (curve == 0 ? firstProfile : secondProfile).getDimension (gs);
	}


	public int getDegree (int curve, GraphState gs)
	{
		return (curve == 0 ? firstProfile : secondProfile).getDegree (gs);
	}


	public boolean isRational (int curve, GraphState gs)
	{
		return (curve == 0 ? firstProfile : secondProfile).isRational (gs);
	}


	public boolean areCurvesCompatible (GraphState gs)
	{
		return false;
	}

}
