
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

import de.grogra.util.*;
import de.grogra.persistence.*;
import de.grogra.graph.*;
import javax.swing.ListModel;

public final class EdgeBits extends ShareableBase
{

	//enh:sco SCOType

	EnumValueImpl edge; 
	//enh:field setmethod=setEdge

	int bits;
	//enh:field setmethod=setBits
	
	boolean successor;
	//enh:field setmethod=setSuccessor
	
	boolean branch;
	//enh:field setmethod=setBranch

	ListModel specialEdges;


	EdgeBits ()
	{
		this (null);
	}
	

	EdgeBits (ListModel specialEdges)
	{
		this.specialEdges = specialEdges;
	}


	void setEdge (EnumValueImpl edge)
	{
		this.edge = edge;
		if (edge.getValue () instanceof SpecialEdgeDescriptor)
		{
			SpecialEdgeDescriptor d = (SpecialEdgeDescriptor) edge.getValue ();
			setBits ((bits & ~Graph.SPECIAL_EDGE_MASK) | d.getBits ());
		}
		else
		{
			setBits (bits & ~Graph.SPECIAL_EDGE_MASK);
		}
	}
	
	
	void setBits (int bits)
	{
		if (bits == 0)
		{
			return;
		}
		this.bits = bits;
		successor = (bits & Graph.SUCCESSOR_EDGE) != 0;
		branch = (bits & Graph.BRANCH_EDGE) != 0;
		if ((specialEdges != null) && (bits & Graph.SPECIAL_EDGE_MASK) != 0)
		{
			for (int i = 1; i < specialEdges.getSize (); i++)
			{
				EnumValueImpl v = (EnumValueImpl) specialEdges.getElementAt (i);
				if (((SpecialEdgeDescriptor) v.getValue ()).getBits ()
					== (bits & Graph.SPECIAL_EDGE_MASK))
				{
					edge = v;
					return;
				}
			}
		}
		edge = (EnumValueImpl) specialEdges.getElementAt (0);
	}
	
	
	void setSuccessor (boolean b)
	{
		setBits (b ? bits | Graph.SUCCESSOR_EDGE : bits & ~Graph.SUCCESSOR_EDGE);
	}
	
	
	void setBranch (boolean b)
	{
		setBits (b ? bits | Graph.BRANCH_EDGE : bits & ~Graph.BRANCH_EDGE);
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field edge$FIELD;
	public static final Type.Field bits$FIELD;
	public static final Type.Field successor$FIELD;
	public static final Type.Field branch$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (EdgeBits representative, de.grogra.persistence.SCOType supertype)
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
					((EdgeBits) o).setSuccessor ((boolean) value);
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((EdgeBits) o).setBranch ((boolean) value);
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
					return ((EdgeBits) o).successor;
				case Type.SUPER_FIELD_COUNT + 3:
					return ((EdgeBits) o).branch;
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((EdgeBits) o).setBits ((int) value);
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
					return ((EdgeBits) o).bits;
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((EdgeBits) o).setEdge ((EnumValueImpl) value);
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
					return ((EdgeBits) o).edge;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new EdgeBits ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (EdgeBits.class);
		edge$FIELD = Type._addManagedField ($TYPE, "edge", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (EnumValueImpl.class), null, Type.SUPER_FIELD_COUNT + 0);
		bits$FIELD = Type._addManagedField ($TYPE, "bits", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		successor$FIELD = Type._addManagedField ($TYPE, "successor", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		branch$FIELD = Type._addManagedField ($TYPE, "branch", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

//enh:end

	public static final ObjectAttribute ATTRIBUTE = Attributes.init
		(new ObjectAttribute ($TYPE, false, null), "edgebits",
		 de.grogra.imp2d.IMP2D.I18N);

}
