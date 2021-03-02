
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

import javax.vecmath.*;

public class SphericalHeightFieldMapping extends HeightFieldMapping
{
	//enh:sco

	float longitude;
	//enh:field quantity=ANGLE setmethod=setLongitude getter

	float latitude = (float) (Math.PI / -2);
	//enh:field quantity=ANGLE setmethod=setLatitude getter

	float longWidth = (float) (2 * Math.PI);
	//enh:field quantity=ANGLE setmethod=setLongWidth getter

	float latWidth = (float) Math.PI;
	//enh:field quantity=ANGLE setmethod=setLatWidth getter

	double radius = 1;
	//enh:field setmethod=setRadius getter

	boolean horizontal;
	//enh:field setmethod=setHorizontal getter


	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field longitude$FIELD;
	public static final Type.Field latitude$FIELD;
	public static final Type.Field longWidth$FIELD;
	public static final Type.Field latWidth$FIELD;
	public static final Type.Field radius$FIELD;
	public static final Type.Field horizontal$FIELD;

	public static class Type extends HeightFieldMapping.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SphericalHeightFieldMapping representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, HeightFieldMapping.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = HeightFieldMapping.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = HeightFieldMapping.Type.FIELD_COUNT + 6;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 5:
					((SphericalHeightFieldMapping) o).setHorizontal ((boolean) value);
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 5:
					return ((SphericalHeightFieldMapping) o).isHorizontal ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SphericalHeightFieldMapping) o).setLongitude ((float) value);
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((SphericalHeightFieldMapping) o).setLatitude ((float) value);
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((SphericalHeightFieldMapping) o).setLongWidth ((float) value);
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((SphericalHeightFieldMapping) o).setLatWidth ((float) value);
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
					return ((SphericalHeightFieldMapping) o).getLongitude ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((SphericalHeightFieldMapping) o).getLatitude ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((SphericalHeightFieldMapping) o).getLongWidth ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((SphericalHeightFieldMapping) o).getLatWidth ();
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 4:
					((SphericalHeightFieldMapping) o).setRadius ((double) value);
					return;
			}
			super.setDouble (o, id, value);
		}

		@Override
		protected double getDouble (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 4:
					return ((SphericalHeightFieldMapping) o).getRadius ();
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SphericalHeightFieldMapping ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SphericalHeightFieldMapping.class);
		longitude$FIELD = Type._addManagedField ($TYPE, "longitude", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		latitude$FIELD = Type._addManagedField ($TYPE, "latitude", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		longWidth$FIELD = Type._addManagedField ($TYPE, "longWidth", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		latWidth$FIELD = Type._addManagedField ($TYPE, "latWidth", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 3);
		radius$FIELD = Type._addManagedField ($TYPE, "radius", 0 | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 4);
		horizontal$FIELD = Type._addManagedField ($TYPE, "horizontal", 0 | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 5);
		longitude$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		latitude$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		longWidth$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		latWidth$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		$TYPE.validate ();
	}

	public boolean isHorizontal ()
	{
		return horizontal;
	}

	public float getLongitude ()
	{
		return longitude;
	}

	public float getLatitude ()
	{
		return latitude;
	}

	public float getLongWidth ()
	{
		return longWidth;
	}

	public float getLatWidth ()
	{
		return latWidth;
	}

	public double getRadius ()
	{
		return radius;
	}

//enh:end

	private transient boolean valid = false;

	private transient int csx, csy;
	private final transient Matrix4d transform = new Matrix4d ();
	private transient double[] cost, sint, cosp, sinp;

	@Override
	public synchronized void map (int x, int y, int sx, int sy, float height,
								  Tuple3f out)
	{
		float longResolution = longWidth / (sx - 1);
		float latResolution = latWidth / (sy - 1);
		Matrix4d t = transform;
		double r;
		if (!valid || (csx != sx) || (csy != sy))
		{
			valid = true;
			csx = sx;
			csy = sy;
			double a = Math.PI / 2 - (latitude + (sy - 1) * latResolution / 2);
			double ct = Math.cos (a), st = Math.sin (a);
			a = longitude + (sx - 1) * longResolution / 2;
			double cp = Math.cos (a), sp = Math.sin (a);
			r = radius;
			t.setIdentity ();
			if (horizontal)
			{
				t.m00 = cp * ct;
				t.m10 = -sp;
				t.m20 = cp * st;
				t.m01 = sp * ct;
				t.m11 = cp;
				t.m21 = sp * st;
				t.m02 = -st;
				t.m22 = ct;
				t.m23 = -r;
			}
			else
			{
				t.m03 = -r * st * cp;
				t.m13 = -r * st * sp;
				t.m23 = -r * ct;
			}
			cost = new double[sy];
			sint = new double[sy];
			for (int i = 0; i < sy; i++)
			{
				a = Math.PI / 2 - (latitude + i * latResolution);
				cost[i] = Math.cos (a);
				sint[i] = Math.sin (a);
			}
			cosp = new double[sx];
			sinp = new double[sx];
			for (int i = 0; i < sx; i++)
			{
				a = longitude + i * longResolution;
				cosp[i] = Math.cos (a);
				sinp[i] = Math.sin (a);
			}
		}
		r = (water && (height <= zeroLevel)) ? radius
			: radius + height - zeroLevel;
		double px = r * sint[y] * cosp[x]; 
		double py = r * sint[y] * sinp[x]; 
		double pz = r * cost[y];
		out.x = scale * (float) (t.m00 * px + t.m01 * py + t.m02 * pz + t.m03); 
		out.y = scale * (float) (t.m10 * px + t.m11 * py + t.m12 * pz + t.m13); 
		out.z = scale * (float) (t.m20 * px + t.m21 * py + t.m22 * pz + t.m23); 
	}


	public synchronized void setLongitude (float f)
	{
		valid = false;
		longitude = f;
	}


	public synchronized void setLatitude (float f)
	{
		valid = false;
		latitude = f;
	}


	public synchronized void setLongWidth (float f)
	{
		valid = false;
		longWidth = f;
	}


	public synchronized void setLatWidth (float f)
	{
		valid = false;
		latWidth = f;
	}


	public synchronized void setRadius (double d)
	{
		valid = false;
		radius = d;
	}


	public synchronized void setHorizontal (boolean r)
	{
		valid = false;
		horizontal = r;
	}

}
