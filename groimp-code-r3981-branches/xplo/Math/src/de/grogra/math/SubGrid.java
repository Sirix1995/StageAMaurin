
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

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.persistence.SCOType;

public class SubGrid extends ContextDependentBase implements VertexGrid
{
	//enh:sco SCOType

	VertexGrid grid;
	//enh:field getter setter

	int offsetU;
	//enh:field getter setter
	
	int offsetV;
	//enh:field getter setter
	
	int widthU = -1;
	//enh:field getter setter
	
	int widthV = -1;
	//enh:field getter setter
	
	int step = 1;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field grid$FIELD;
	public static final Type.Field offsetU$FIELD;
	public static final Type.Field offsetV$FIELD;
	public static final Type.Field widthU$FIELD;
	public static final Type.Field widthV$FIELD;
	public static final Type.Field step$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SubGrid representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 6;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((SubGrid) o).offsetU = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((SubGrid) o).offsetV = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((SubGrid) o).widthU = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((SubGrid) o).widthV = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 5:
					((SubGrid) o).step = (int) value;
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
					return ((SubGrid) o).getOffsetU ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((SubGrid) o).getOffsetV ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((SubGrid) o).getWidthU ();
				case Type.SUPER_FIELD_COUNT + 4:
					return ((SubGrid) o).getWidthV ();
				case Type.SUPER_FIELD_COUNT + 5:
					return ((SubGrid) o).getStep ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SubGrid) o).grid = (VertexGrid) value;
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
					return ((SubGrid) o).getGrid ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SubGrid ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SubGrid.class);
		grid$FIELD = Type._addManagedField ($TYPE, "grid", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (VertexGrid.class), null, Type.SUPER_FIELD_COUNT + 0);
		offsetU$FIELD = Type._addManagedField ($TYPE, "offsetU", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		offsetV$FIELD = Type._addManagedField ($TYPE, "offsetV", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 2);
		widthU$FIELD = Type._addManagedField ($TYPE, "widthU", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 3);
		widthV$FIELD = Type._addManagedField ($TYPE, "widthV", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 4);
		step$FIELD = Type._addManagedField ($TYPE, "step", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 5);
		$TYPE.validate ();
	}

	public int getOffsetU ()
	{
		return offsetU;
	}

	public void setOffsetU (int value)
	{
		this.offsetU = (int) value;
	}

	public int getOffsetV ()
	{
		return offsetV;
	}

	public void setOffsetV (int value)
	{
		this.offsetV = (int) value;
	}

	public int getWidthU ()
	{
		return widthU;
	}

	public void setWidthU (int value)
	{
		this.widthU = (int) value;
	}

	public int getWidthV ()
	{
		return widthV;
	}

	public void setWidthV (int value)
	{
		this.widthV = (int) value;
	}

	public int getStep ()
	{
		return step;
	}

	public void setStep (int value)
	{
		this.step = (int) value;
	}

	public VertexGrid getGrid ()
	{
		return grid;
	}

	public void setGrid (VertexGrid value)
	{
		grid$FIELD.setObject (this, value);
	}

//enh:end


	public SubGrid ()
	{
		super ();
	}


	public int getUSize (GraphState gs)
	{
		if (grid == null)
		{
			return 1;
		}
		int w = grid.getUSize (gs) - offsetU;
		if (widthU >= 1)
		{
			w = Math.min (w, widthU);
		}
		return w / step;
	}


	public int getVSize (GraphState gs)
	{
		if (grid == null)
		{
			return 1;
		}
		int w = grid.getVSize (gs) - offsetV;
		if (widthV >= 1)
		{
			w = Math.min (w, widthV);
		}
		return w / step;
	}


	public int getVertexIndex (int u, int v, GraphState gs)
	{
		return (grid == null) ? 0
			: grid.getVertexIndex (u * step + offsetU, v * step + offsetV, gs);
	}


	public int getDimension (GraphState gs)
	{
		return (grid == null) ? 2 : grid.getDimension (gs);
	}


	public boolean isRational (GraphState gs)
	{
		return (grid != null) && grid.isRational (gs);
	}


	public int getVertex (float[] out, int index, GraphState gs)
	{
		return (grid == null) ? BSpline.set (out, 0, 0)
			: grid.getVertex (out, index, gs);
	}


	public boolean dependsOnContext ()
	{
		return (grid != null) && grid.dependsOnContext ();
	}

}
