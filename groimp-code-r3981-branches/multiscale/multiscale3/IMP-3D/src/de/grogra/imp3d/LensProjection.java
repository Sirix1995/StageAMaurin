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

package de.grogra.imp3d;

import java.util.Random;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;

public class LensProjection extends PerspectiveProjection
{
	//enh:sco

	double focalLength = 0.05;
	//enh:field quantity=LENGTH

	double fStop = 11;
	//enh:field

	double subjectDistance = 3;
	//enh:field quantity=LENGTH

	public LensProjection ()
	{
		super ();
	}

	public LensProjection (float fieldOfView, float aspect)
	{
		super (fieldOfView, aspect);
	}

	@Override
	public void generateRandomOrigins (Environment env, RayList out, Random rnd,
			Matrix4d deviceToView)
	{
		Point3d origin = env.tmpPoint0;
		getRayInViewCoordinates (env.uv.x, env.uv.y, origin, env.tmpVector0,
			deviceToView, null);
		float f = (float) (focalLength / (fStop * 512));
		for (int i = 0; i < out.size (); i++)
		{
			Point3f p = out.rays[i].origin;
			p.set (origin);
			p.z -= focalLength;
			
			// generate a location on the lens which is uniformly distributed
			// over the circular lens surface
			int j = rnd.nextInt ();
			float r = (float) Math.sqrt (j >>> 16) * f;
			char phi = (char) j;
			p.x += Math2.ccos (phi) * r;
			p.y += Math2.csin (phi) * r;

			env.localToGlobal.transform (p);
			out.rays[i].spectrum.setIdentity ();
		}
	}

	@Override
	public void generateRandomRays (Environment env, Vector3f out,
			Spectrum specOut, RayList rays, Random rnd, Matrix4d deviceToView)
	{
		Point3d origin = env.tmpPoint0;
		Vector3d direction = env.tmpVector0;
		getRayInViewCoordinates (env.uv.x, env.uv.y, origin, direction,
			deviceToView, null);
		origin.scaleAdd (-(subjectDistance + origin.z) / direction.z,
			direction, origin);
		env.tmpPoint1.set (env.localPoint);
		direction.sub (origin, env.tmpPoint1);
		direction.normalize ();
		for (int i = 0; i < rays.size (); i++)
		{
			rays.rays[i].direction.set (direction);
			env.localToGlobal.transform (rays.rays[i].direction);
			rays.rays[i].spectrum.set (specOut);
		}
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field focalLength$FIELD;
	public static final Type.Field fStop$FIELD;
	public static final Type.Field subjectDistance$FIELD;

	public static class Type extends PerspectiveProjection.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (LensProjection representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, PerspectiveProjection.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = PerspectiveProjection.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = PerspectiveProjection.Type.FIELD_COUNT + 3;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((LensProjection) o).focalLength = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((LensProjection) o).fStop = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((LensProjection) o).subjectDistance = (double) value;
					return;
			}
			super.setDouble (o, id, value);
		}

		@Override
		protected double getDouble (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((LensProjection) o).focalLength;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((LensProjection) o).fStop;
				case Type.SUPER_FIELD_COUNT + 2:
					return ((LensProjection) o).subjectDistance;
			}
			return super.getDouble (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new LensProjection ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (LensProjection.class);
		focalLength$FIELD = Type._addManagedField ($TYPE, "focalLength", 0 | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 0);
		fStop$FIELD = Type._addManagedField ($TYPE, "fStop", 0 | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 1);
		subjectDistance$FIELD = Type._addManagedField ($TYPE, "subjectDistance", 0 | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 2);
		focalLength$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		subjectDistance$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

//enh:end
}
