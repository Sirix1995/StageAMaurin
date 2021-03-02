
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

package de.grogra.imp;

import de.grogra.graph.*;
import de.grogra.imp.*;

/**
 * A <code>FilterDescriptor</code> can be used as base class for
 * {@link de.grogra.imp.GraphDescriptor}s
 * of {@link de.grogra.graph.GraphFilter}s. This class contains
 * a source descriptor which represents the source
 * graph of the filter. 
 *
 * @author Ole Kniemeyer
 */
public abstract class FilterDescriptor extends GraphDescriptor
{
	//enh:sco
	
	/**
	 * Descriptor for the source graph of the filter.
	 */
	protected GraphDescriptor source = new ProjectGraphDescriptor ();
	//enh:field


	@Override
	public void substituteSelection (GraphState[] gs, Object[] object,
									 boolean asNode[], int index)
	{
		// substitute the selection such that it represents data
		// from the source graph
		gs[index] = GraphFilter.getSourceState (gs[index]);
		// forward substitution to source
		source.substituteSelection (gs, object, asNode, index);
	}


//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field source$FIELD;

	public static class Type extends GraphDescriptor.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (FilterDescriptor representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, GraphDescriptor.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = GraphDescriptor.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = GraphDescriptor.Type.FIELD_COUNT + 1;

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
					((FilterDescriptor) o).source = (GraphDescriptor) value;
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
					return ((FilterDescriptor) o).source;
			}
			return super.getObject (o, id);
		}
	}

	static
	{
		$TYPE = new Type (FilterDescriptor.class);
		source$FIELD = Type._addManagedField ($TYPE, "source", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (GraphDescriptor.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end

}
