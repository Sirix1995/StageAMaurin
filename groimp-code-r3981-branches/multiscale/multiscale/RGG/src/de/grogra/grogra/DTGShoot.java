
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

package de.grogra.grogra;

import de.grogra.graph.FloatAttribute;
import de.grogra.graph.GraphState;
import de.grogra.graph.IntAttribute;
import de.grogra.turtle.Attributes;
import de.grogra.turtle.F;

public class DTGShoot extends F
{
	
	public int internodeCount;
	//enh:field

	public float heartwood = -1.0f;
	//enh:field
	
	public int order = -1;
	//enh:field
	
	public int generativeDistance = -1;
	//enh:field
	
	public float tropismStrength = -1;
	//enh:field
	
	public float parameter = 0.0f;
	//enh:field
	
	public float carbon = 0.0f;
	//enh:field
	
	public float relPosition = 0.0f;
	//enh:field
	
	public int scale = 0;
	//enh:field
	
	public int noOfYear = -1;
	//enh:field
	
	protected int getInt (IntAttribute a, GraphState gs)
	{
		return (a == Attributes.INTERNODE_COUNT)
			? internodeCount
			: (a == Attributes.ORDER)
			? order
			: (a == Attributes.GENERATIVE_DISTANCE)
			? generativeDistance
			: (a == Attributes.LOCAL_SCALE)
			? scale
			: (a == Attributes.NO_OF_YEAR)
			? noOfYear
			: super.getInt (a, gs);
	}

	protected float getFloat (FloatAttribute a, GraphState gs)
	{
		return (a == Attributes.HEARTWOOD)
			? heartwood
			: (a == Attributes.TROPISM_STRENGTH)
			? tropismStrength
			: (a == Attributes.PARAMETER)
			? parameter
			: (a == Attributes.CARBON)
			? carbon
			: (a == Attributes.REL_POSITION)
			? relPosition
			: super.getFloat (a, gs);
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field internodeCount$FIELD;
	public static final NType.Field heartwood$FIELD;
	public static final NType.Field order$FIELD;
	public static final NType.Field generativeDistance$FIELD;
	public static final NType.Field tropismStrength$FIELD;
	public static final NType.Field parameter$FIELD;
	public static final NType.Field carbon$FIELD;
	public static final NType.Field relPosition$FIELD;
	public static final NType.Field scale$FIELD;
	public static final NType.Field noOfYear$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (DTGShoot.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 0:
					((DTGShoot) o).internodeCount = (int) value;
					return;
				case 2:
					((DTGShoot) o).order = (int) value;
					return;
				case 3:
					((DTGShoot) o).generativeDistance = (int) value;
					return;
				case 8:
					((DTGShoot) o).scale = (int) value;
					return;
				case 9:
					((DTGShoot) o).noOfYear = (int) value;
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
					return ((DTGShoot) o).internodeCount;
				case 2:
					return ((DTGShoot) o).order;
				case 3:
					return ((DTGShoot) o).generativeDistance;
				case 8:
					return ((DTGShoot) o).scale;
				case 9:
					return ((DTGShoot) o).noOfYear;
			}
			return super.getInt (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 1:
					((DTGShoot) o).heartwood = (float) value;
					return;
				case 4:
					((DTGShoot) o).tropismStrength = (float) value;
					return;
				case 5:
					((DTGShoot) o).parameter = (float) value;
					return;
				case 6:
					((DTGShoot) o).carbon = (float) value;
					return;
				case 7:
					((DTGShoot) o).relPosition = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 1:
					return ((DTGShoot) o).heartwood;
				case 4:
					return ((DTGShoot) o).tropismStrength;
				case 5:
					return ((DTGShoot) o).parameter;
				case 6:
					return ((DTGShoot) o).carbon;
				case 7:
					return ((DTGShoot) o).relPosition;
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new DTGShoot ());
		$TYPE.addManagedField (internodeCount$FIELD = new _Field ("internodeCount", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 0));
		$TYPE.addManagedField (heartwood$FIELD = new _Field ("heartwood", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (order$FIELD = new _Field ("order", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 2));
		$TYPE.addManagedField (generativeDistance$FIELD = new _Field ("generativeDistance", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 3));
		$TYPE.addManagedField (tropismStrength$FIELD = new _Field ("tropismStrength", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 4));
		$TYPE.addManagedField (parameter$FIELD = new _Field ("parameter", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 5));
		$TYPE.addManagedField (carbon$FIELD = new _Field ("carbon", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 6));
		$TYPE.addManagedField (relPosition$FIELD = new _Field ("relPosition", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 7));
		$TYPE.addManagedField (scale$FIELD = new _Field ("scale", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 8));
		$TYPE.addManagedField (noOfYear$FIELD = new _Field ("noOfYear", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.INT, null, 9));
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
		return new DTGShoot ();
	}

//enh:end

}
