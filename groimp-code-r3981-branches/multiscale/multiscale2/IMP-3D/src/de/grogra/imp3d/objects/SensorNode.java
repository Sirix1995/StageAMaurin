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

import java.util.Random;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.graph.GraphState;
import de.grogra.imp.PickList;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.ray.RTSky;
import de.grogra.imp3d.ray.RaytracerLeaf;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.imp3d.shading.SunSkyLight;
import de.grogra.math.RGBColor;
import de.grogra.math.Tuple3fType;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Sensor;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;

public class SensorNode extends ColoredNull implements Pickable, Renderable,
		Sensor
{
	protected float radius = 1;
	//enh:field getter setter

	protected float exponent = 0;
	//enh:field getter setter

	protected boolean twoSided = true;

	//enh:field getter setter

	
	public SensorNode ()
	{
		color = RGBColor.WHITE;
	}

	public int getFlags ()
	{
		return NEEDS_NORMAL;
	}

	public int getAverageColor ()
	{
		return Tuple3fType.colorToInt (color);
	}

	public double computeExitance (Environment env, Spectrum exitance)
	{
		exitance.set (color);
		exitance.scale (1 / (Math.PI * radius * radius));
		return 0;
	}

	public float computeBSDF (Environment env, Vector3f in, Spectrum specIn,
			Vector3f out, boolean adjoint, Spectrum bsdf)
	{
		float cos = env.normal.dot (out);
		if (cos <= 0)
		{
			cos = twoSided ? Math.abs (cos) : 0;
		}
		double f = Math.pow (cos, exponent) * (exponent + 1) * Math2.M_1_2PI;
		if (twoSided)
		{
			f *= 0.5;
		}
		bsdf.set (specIn);
		bsdf.scale (f);
		return 0;
	}

	public void generateRandomOrigins (Environment env, RayList out, Random rnd)
	{
		throw new UnsupportedOperationException ();
	}

	public void generateRandomRays (Environment env, Vector3f out,
			Spectrum specOut, RayList rays, boolean adjoint, Random rnd)
	{
		throw new UnsupportedOperationException ();
	}

	public double completeRay (Environment env, Point3d vertex, Ray out)
	{
		throw new UnsupportedOperationException ();
	}
	
	public float[] getUVForVertex(Environment env, Point3d vertex){
		throw new UnsupportedOperationException ();
	}

	public void pick (Object object, boolean asNode, Point3d origin,
			Vector3d direction, Matrix4d t, PickList list)
	{
		Sphere.pick (radius, origin, direction, list);
	}

	public void draw (Object object, boolean asNode, RenderState rs)
	{
		rs.drawSphere (radius, new RGBAShader (color.x, color.y, color.z), RenderState.CURRENT_HIGHLIGHT, null);
	}

	//enh:insert $TYPE.addIdentityAccessor (Attributes.SHAPE);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field radius$FIELD;
	public static final NType.Field exponent$FIELD;
	public static final NType.Field twoSided$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (SensorNode.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 2:
					((SensorNode) o).twoSided = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 2:
					return ((SensorNode) o).isTwoSided ();
			}
			return super.getBoolean (o);
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((SensorNode) o).radius = (float) value;
					return;
				case 1:
					((SensorNode) o).exponent = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 0:
					return ((SensorNode) o).getRadius ();
				case 1:
					return ((SensorNode) o).getExponent ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new SensorNode ());
		$TYPE.addManagedField (radius$FIELD = new _Field ("radius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (exponent$FIELD = new _Field ("exponent", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (twoSided$FIELD = new _Field ("twoSided", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 2));
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
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
		return new SensorNode ();
	}

	public boolean isTwoSided ()
	{
		return twoSided;
	}

	public void setTwoSided (boolean value)
	{
		this.twoSided = (boolean) value;
	}

	public float getRadius ()
	{
		return radius;
	}

	public void setRadius (float value)
	{
		this.radius = (float) value;
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
}
