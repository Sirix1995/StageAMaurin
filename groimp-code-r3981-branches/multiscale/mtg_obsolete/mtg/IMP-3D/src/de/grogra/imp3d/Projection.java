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
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Scattering;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;

public abstract class Projection extends ShareableBase
{
	//enh:sco de.grogra.persistence.SCOType

	protected float aspect = 1;
	//enh:field setmethod=setAspect

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field aspect$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Projection representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT + 1;

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
					((Projection) o).setAspect ((float) value);
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
					return ((Projection) o).aspect;
			}
			return super.getFloat (o, id);
		}
	}

	static
	{
		$TYPE = new Type (Projection.class);
		aspect$FIELD = Type._addManagedField ($TYPE, "aspect", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end

	protected transient float sx, sy;

	void setAspect (float aspect)
	{
		this.aspect = aspect;
		update ();
	}

	public float getAspect ()
	{
		return aspect;
	}

	protected abstract void update ();

	public float getScaleX ()
	{
		return sx;
	}

	public float getScaleY ()
	{
		return sy;
	}

	protected abstract void getTransformation (float near, float far,
			Matrix4d viewToClip, Matrix4d deviceToView);

	protected abstract void getRayInViewCoordinates (float x, float y,
			Point3d origin, Vector3d direction, Matrix4d deviceToView, Tuple2d densities);

	public abstract float getScaleAt (float z);

	
	public void generateRandomOrigins (Environment env, RayList out, Random rnd, Matrix4d deviceToView)
	{
		Point3d origin = env.tmpPoint0;
		Tuple2d d = env.tmpPoint2d0;
		getRayInViewCoordinates (env.uv.x, env.uv.y, origin, env.tmpVector0, deviceToView, d);
		for (int i = 0; i < out.size (); i++)
		{
			out.rays[i].origin.set (origin);
			env.localToGlobal.transform (out.rays[i].origin);
			out.rays[i].spectrum.setIdentity ();
			if (this instanceof ParallelProjection)
			{
				out.rays[i].spectrum.scale (1 / d.x);
			}
			out.rays[i].originDensity = (float) d.x;
		}
	}

	public void generateRandomRays (Environment env, Vector3f out, Spectrum specOut, RayList rays, Random rnd, Matrix4d deviceToView)
	{
		Point3d origin = env.tmpPoint0;
		Vector3d direction = env.tmpVector0;
		Tuple2d d = env.tmpPoint2d0;
		getRayInViewCoordinates (env.uv.x, env.uv.y, origin, direction, deviceToView, d);
		direction.normalize ();
		for (int i = 0; i < rays.size (); i++)
		{
			rays.rays[i].direction.set (direction);
			env.localToGlobal.transform (rays.rays[i].direction);
			rays.rays[i].spectrum.set (specOut);
			rays.rays[i].directionDensity = (float) d.y;
		}
	}

	public float computeBSDF (Environment env, Spectrum specIn, Vector3f out, Spectrum bsdf)
	{
		throw new UnsupportedOperationException ();
	}

	protected double completeRay (Environment env, Point3d vertex, Ray out, Matrix4d viewToClip, Matrix4d deviceToView)
	{
		Vector3f p = env.userVector;
		p.set (vertex);
		Math2.transformPoint (env.globalToLocal, p);
		if (p.z < 0)
		{
			double w = 1 / (viewToClip.m30 * p.x + viewToClip.m31 * p.y + viewToClip.m32 * p.z + viewToClip.m33);
			float u = (float) (w * (viewToClip.m00 * p.x + viewToClip.m01 * p.y + viewToClip.m02 * p.z + viewToClip.m03));
			if ((-1 <= u) && (u <= 1))
			{
				float v = (float) (w * (viewToClip.m10 * p.x + viewToClip.m11 * p.y + viewToClip.m12 * p.z + viewToClip.m13));
				if ((-1 <= v) && (v <= 1))
				{
					Tuple2d d = env.tmpPoint2d0;
					env.uv.set (u, v);
					getRayInViewCoordinates (u, v, env.tmpPoint0, env.tmpVector0, deviceToView, d);
					out.origin.set (env.tmpPoint0);
					out.spectrum.setIdentity ();
					if (this instanceof ParallelProjection)
					{
						out.originDensity = Scattering.DELTA_FACTOR;
					}
					else
					{
						out.originDensity = (float) d.x;
						out.spectrum.scale (out.directionDensity / p.lengthSquared ());
					}
					out.directionDensity = (float) d.y;
					return d.x;
				}
			}
		}
		out.originDensity = 0;
		out.directionDensity = 0;
		out.spectrum.setZero ();
		return 0;
	}

	
	float[] getUVForVertex(Environment env, Point3d vertex, Matrix4d viewToClip, Matrix4d deviceToView){
		
		float[] ret= {-10,-10};
		Vector3f p = env.userVector;
		p.set (vertex);
		Math2.transformPoint (env.globalToLocal, p);
		if (p.z < 0)
		{
			double w = 1 / (viewToClip.m30 * p.x + viewToClip.m31 * p.y + viewToClip.m32 * p.z + viewToClip.m33);
			float u = (float) (w * (viewToClip.m00 * p.x + viewToClip.m01 * p.y + viewToClip.m02 * p.z + viewToClip.m03));
			if ((-1 <= u) && (u <= 1))
			{
				float v = (float) (w * (viewToClip.m10 * p.x + viewToClip.m11 * p.y + viewToClip.m12 * p.z + viewToClip.m13));
				if ((-1 <= v) && (v <= 1))
				{
					ret[0]=u;
					ret[1] =v;
				}
			}
		}	
		return ret;
	}
	
	
	
}