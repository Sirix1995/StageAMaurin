
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

import javax.vecmath.Color3f;

import de.grogra.imp3d.shading.Shader;
import de.grogra.math.Tuple3fType;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Spectrum;

public class AreaLight extends ShareableBase
{
	//enh:sco SCOType

	float power = 100;
	//enh:field quantity=POWER getter setter

	float exponent = 4;
	//enh:field getter setter

	boolean shadowless = false;
	//enh:field getter setter

	boolean ignoredWhenHit = false;
	//enh:field getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field power$FIELD;
	public static final Type.Field exponent$FIELD;
	public static final Type.Field shadowless$FIELD;
	public static final Type.Field ignoredWhenHit$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (AreaLight representative, de.grogra.persistence.SCOType supertype)
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
					((AreaLight) o).shadowless = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((AreaLight) o).ignoredWhenHit = (boolean) value;
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
					return ((AreaLight) o).isShadowless ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((AreaLight) o).isIgnoredWhenHit ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((AreaLight) o).power = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((AreaLight) o).exponent = (float) value;
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
					return ((AreaLight) o).getPower ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((AreaLight) o).getExponent ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new AreaLight ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (AreaLight.class);
		power$FIELD = Type._addManagedField ($TYPE, "power", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		exponent$FIELD = Type._addManagedField ($TYPE, "exponent", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		shadowless$FIELD = Type._addManagedField ($TYPE, "shadowless", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 2);
		ignoredWhenHit$FIELD = Type._addManagedField ($TYPE, "ignoredWhenHit", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 3);
		power$FIELD.setQuantity (de.grogra.util.Quantity.POWER);
		$TYPE.validate ();
	}

	public boolean isShadowless ()
	{
		return shadowless;
	}

	public void setShadowless (boolean value)
	{
		this.shadowless = (boolean) value;
	}

	public boolean isIgnoredWhenHit ()
	{
		return ignoredWhenHit;
	}

	public void setIgnoredWhenHit (boolean value)
	{
		this.ignoredWhenHit = (boolean) value;
	}

	public float getPower ()
	{
		return power;
	}

	public void setPower (float value)
	{
		this.power = (float) value;
	}

	public float getExponent ()
	{
		return exponent;
	}

	public void setExponent (float value)
	{
		this.exponent = (float) value;
	}

//enh:end
	
	void computeExitance (Shader sh, float area, Spectrum exitance)
	{
		Color3f c = new Color3f ();
		Tuple3fType.setColor (c, (sh != null) ? sh.getAverageColor () : -1);
		exitance.set (c);
		exitance.scale (power / (area * exitance.integrate ()));
	}

}
