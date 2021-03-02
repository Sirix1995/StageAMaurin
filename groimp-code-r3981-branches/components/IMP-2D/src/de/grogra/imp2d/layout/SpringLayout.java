
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

package de.grogra.imp2d.layout;

import java.util.Random;

import javax.vecmath.*;

public class SpringLayout extends ForceBasedLayout
{
	//enh:sco

	float attraction = 1;
	//enh:field

	float repulsion = 1.0f;
	//enh:field


	@Override
	protected void setRandomPosition(Node nodeTemp, Random rnd) {
		nodeTemp.x = rnd.nextFloat();
		nodeTemp.y = rnd.nextFloat();
		
	}
	
	@Override
	protected void computeForce (Edge e, Vector2f force)
	{
		force.sub (e.target, e.source);
		float d = force.length ();
		if (d < attraction * 1e-6f)
		{
			force.set (attraction, 0);
		}
		else
		{
			force.scale ((attraction / e.weight - d) / d);
		}
	}


	@Override
	protected void computeForce (Node s, Node t, Vector2f force)
	{
		force.sub (t, s);
		float d = force.length ();
		if (d < attraction * 1e-6f)
		{
			force.set (attraction, 0);
		}
		else
		{
			force.scale (repulsion / (d * d * d));
		}
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field attraction$FIELD;
	public static final Type.Field repulsion$FIELD;

	public static class Type extends ForceBasedLayout.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SpringLayout representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, ForceBasedLayout.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = ForceBasedLayout.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = ForceBasedLayout.Type.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SpringLayout) o).attraction = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((SpringLayout) o).repulsion = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((SpringLayout) o).attraction;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((SpringLayout) o).repulsion;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SpringLayout ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SpringLayout.class);
		attraction$FIELD = Type._addManagedField ($TYPE, "attraction", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		repulsion$FIELD = Type._addManagedField ($TYPE, "repulsion", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end

}
