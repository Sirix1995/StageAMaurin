
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

package de.grogra.imp3d.shading;

import javax.vecmath.*;
import de.grogra.math.*;

public class Gradient extends VolumeFunction
{
	final Vector3f direction = new Vector3f (1, 0, 0);
	//enh:field type=Tuple3fType.VECTOR set=set getter setter


	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field direction$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Gradient.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Gradient) o).direction.set ((Vector3f) value);
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Gradient) o).getDirection ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Gradient ());
		$TYPE.addManagedField (direction$FIELD = new _Field ("direction", _Field.FINAL  | _Field.SCO, Tuple3fType.VECTOR, null, 0));
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
		return new Gradient ();
	}

	public Vector3f getDirection ()
	{
		return direction;
	}

	public void setDirection (Vector3f value)
	{
		direction$FIELD.setObject (this, value);
	}

//enh:end


	@Override
	protected float getFloatValue (float x, float y, float z)
	{
		return x * direction.x + y * direction.y + z * direction.z;
	}

	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}
	
}
