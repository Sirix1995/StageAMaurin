
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

package de.grogra.imp2d.graphs;

import javax.vecmath.Color3f;

import de.grogra.graph.Graph;
import de.grogra.imp.FilterDescriptor;
import de.grogra.imp.View;
import de.grogra.math.Tuple3fType;

public class HighlightFilterDescriptor extends FilterDescriptor
{
	//enh:sco
	
	String xmlFile = "../IMP-2D/src/de/grogra/imp2d/graphs/HighlightFilter_Specification.xml";
	//enh:field

	@Override
	public Graph getGraph (View view)
	{
		return new HighlightFilter (source.getGraph (view), this);
	}


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field xmlFile$FIELD;

	public static class Type extends FilterDescriptor.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (HighlightFilterDescriptor representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, FilterDescriptor.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = FilterDescriptor.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = FilterDescriptor.Type.FIELD_COUNT + 1;

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
					((HighlightFilterDescriptor) o).xmlFile = (String) value;
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
					return ((HighlightFilterDescriptor) o).xmlFile;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new HighlightFilterDescriptor ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (HighlightFilterDescriptor.class);
		xmlFile$FIELD = Type._addManagedField ($TYPE, "xmlFile", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end

}
