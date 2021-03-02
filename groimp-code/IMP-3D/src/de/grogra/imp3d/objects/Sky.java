
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
import de.grogra.imp3d.ray.Raytraceable;
import de.grogra.imp3d.ray.RaytracerLeaf;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.shading.LightVisitor;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;

public class Sky extends ShadedNull implements Pickable, Renderable, Raytraceable, Light
{
	float powerDensity = 50;
	//enh:field quantity=POWER_PER_AREA getter setter

	@Override
	public int getFlags ()
	{
		return (getShader () != null) ? getShader ().getFlags () | NEEDS_NORMAL
			: NEEDS_NORMAL | RANDOM_RAYS_GENERATE_ORIGINS;
	}

	@Override
	public int getAverageColor ()
	{
		return -1;
	}

	@Override
	public boolean isShadowless ()
	{
		return false;
	}

	@Override
	public boolean isIgnoredWhenHit ()
	{
		return false;
	}

	@Override
	public double getTotalPower (Environment env)
	{
		Shader s = getShader ();
		if (s instanceof Light)
		{
			return ((Light) s).getTotalPower (env);
		}
		else
		{
			if( s == null )
				s = RGBAShader.WHITE;
			RGBAShader rgbaShader = new RGBAShader( s.getAverageColor() );
			return env.boundsRadius * env.boundsRadius * 2 * Math.PI * powerDensity * (rgbaShader.x + rgbaShader.y + rgbaShader.z);
		}
	}

	@Override
	public double computeExitance (Environment env, Spectrum exitance)
	{
		Shader s = getShader ();
		if (s instanceof Light)
		{
			return ((Light) s).computeExitance (env, exitance);
		}
		exitance.setIdentity ();
		exitance.scale (powerDensity * (1f / 3));
		return 1 / (env.boundsRadius * env.boundsRadius * Math.PI);
	}

	@Override
	public float computeBSDF (Environment env, Vector3f in, Spectrum specIn, Vector3f out, boolean adjoint, Spectrum bsdf)
	{
		Shader s = getShader ();
		if (s == null)
		{
			s = RGBAShader.WHITE;
		}
		float f = s.computeBSDF (env, env.normal, specIn, out, adjoint, bsdf);
		if (s instanceof Light)
		{
			return f;
		}
		bsdf.scale (0.25);
		return Math2.M_1_2PI / 2;
	}

	@Override
	public int getLightType ()
	{
		return (getShader () instanceof Light) ? SKY : NO_LIGHT;
	}

	@Override
	public void generateRandomOrigins (Environment env, RayList out, Random rnd)
	{
		Shader s = getShader ();
		if (s instanceof Light)
		{
			((Light) s).generateRandomOrigins (env, out, rnd);
			return;
		}
		throw new UnsupportedOperationException ();
	}

	@Override
	public void generateRandomRays (Environment env, Vector3f out, Spectrum specOut, RayList rays, boolean adjoint, Random rnd)
	{
		Shader s = getShader ();
		if (s instanceof Light)
		{
			((Light) s).generateRandomRays (env, out, specOut, rays, adjoint, rnd);
			return;
		}
		throw new UnsupportedOperationException ();
	}

	@Override
	public double completeRay (Environment env, Point3d vertex, Ray out)
	{
		throw new UnsupportedOperationException ();
	}

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
		$TYPE.addIdentityAccessor (Attributes.LIGHT);
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field powerDensity$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Sky.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Sky) o).powerDensity = value;
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
					return ((Sky) o).getPowerDensity ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Sky ());
		$TYPE.addManagedField (powerDensity$FIELD = new _Field ("powerDensity", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		powerDensity$FIELD.setQuantity (de.grogra.util.Quantity.POWER_PER_AREA);
		initType ();
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
		return new Sky ();
	}

	public float getPowerDensity ()
	{
		return powerDensity;
	}

	public void setPowerDensity (float value)
	{
		this.powerDensity = value;
	}

//enh:end


	@Override
	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d t, PickList list)
	{
		Sphere.pick (0.2f, origin, direction, list);
	}


	@Override
	public void draw (Object object, boolean asNode, RenderState rs)
	{
		rs.drawSphere (0.2f, null, RenderState.CURRENT_HIGHLIGHT, false, null);
	}

	@Override
	public RaytracerLeaf createRaytracerLeaf(Object object, boolean asNode, long pathId, GraphState gs) {
		return new RTSky(object, asNode, pathId);
	}

	@Override
	public void accept(LightVisitor visitor) {
		visitor.visit( this );
	}

	@Override
	public Light resolveLight() {
		return this;
	}

}
