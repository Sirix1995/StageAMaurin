
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

public class CSGNode extends ShadedNull
{
	int operation;
	//enh:field attr=Attributes.CSG_OPERATION type=Attributes.CSG_OPERATION_TYPE getter setter	

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field operation$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (CSGNode.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((CSGNode) o).operation = (int) value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 0:
					return ((CSGNode) o).getOperation ();
			}
			return super.getInt (o);
		}
	}

	static
	{
		$TYPE = new NType (new CSGNode ());
		$TYPE.addManagedField (operation$FIELD = new _Field ("operation", 0 | _Field.SCO, Attributes.CSG_OPERATION_TYPE, null, 0));
		$TYPE.declareFieldAttribute (operation$FIELD, Attributes.CSG_OPERATION);
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new CSGNode ();
	}

	public int getOperation ()
	{
		return operation;
	}

	public void setOperation (int value)
	{
		this.operation = (int) value;
	}

//enh:end

	public CSGNode ()
	{
		this (Attributes.CSG_UNION);
	}


	public CSGNode (int operation)
	{
		setOperation (operation);
	}
}
