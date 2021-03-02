
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

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.ray.Raytraceable;
import de.grogra.imp3d.ray.RaytracerLeaf;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.shading.LightVisitor;
import de.grogra.math.Pool;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;

public class PointLight extends LightBase implements Raytraceable
{
	//enh:sco
	
	float power = 100;
	//enh:field quantity=POWER getter setter

	float attenuationExponent = 0;
	//enh:field getter setter

	float attenuationDistance = 1;
	//enh:field quantity=LENGTH getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field power$FIELD;
	public static final Type.Field attenuationExponent$FIELD;
	public static final Type.Field attenuationDistance$FIELD;

	public static class Type extends LightBase.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (PointLight representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, LightBase.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = LightBase.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = LightBase.Type.FIELD_COUNT + 3;

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
					((PointLight) o).power = value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((PointLight) o).attenuationExponent = value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((PointLight) o).attenuationDistance = value;
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
					return ((PointLight) o).getPower ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((PointLight) o).getAttenuationExponent ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((PointLight) o).getAttenuationDistance ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new PointLight ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (PointLight.class);
		power$FIELD = Type._addManagedField ($TYPE, "power", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		attenuationExponent$FIELD = Type._addManagedField ($TYPE, "attenuationExponent", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		attenuationDistance$FIELD = Type._addManagedField ($TYPE, "attenuationDistance", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		power$FIELD.setQuantity (de.grogra.util.Quantity.POWER);
		attenuationDistance$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public float getPower ()
	{
		return power;
	}

	public void setPower (float value)
	{
		this.power = value;
	}

	public float getAttenuationExponent ()
	{
		return attenuationExponent;
	}

	public void setAttenuationExponent (float value)
	{
		this.attenuationExponent = value;
	}

	public float getAttenuationDistance ()
	{
		return attenuationDistance;
	}

	public void setAttenuationDistance (float value)
	{
		this.attenuationDistance = value;
	}

//enh:end
	
	@Override
	public int getLightType ()
	{
		return POINT;
	}


	protected float getDensityAt (Vector3f direction)
	{
		return Math2.M_1_2PI / 2;
	}


	@Override
	public double getTotalPower (Environment env)
	{
		return power;
	}

	@Override
	public void generateRandomOrigins (Environment env, RayList out, Random rnd)
	{
		Matrix4f m = env.localToGlobal;
		for (int i = 0; i < out.getSize(); i++)
		{
			Ray r = out.rays[i];
			r.origin.set (m.m03, m.m13, m.m23);
			if (i == 0)
			{
				r.spectrum.set (color);
				r.spectrum.scale (power / r.spectrum.integrate ());
			}
			else
			{
				r.spectrum.set (out.rays[0].spectrum);
			}
			r.originDensity = DELTA_FACTOR;
		}
	}


	@Override
	public double computeExitance (Environment env, Spectrum exitance)
	{
		exitance.setZero ();
		return 0;
	}


	@Override
	protected void draw (Tuple3f color, RenderState rs)
	{
		Pool pool = rs.getPool ();
		Tuple3f o = pool.q3f3, po = pool.q3f1;
		o.set (0, 0, 0);

		//Visualized physical light distribution
		if(visualize) {
			Random rnd = new Random(123456789);

			for (int i = numberofrays; i >= 0; i--) {
				int j = rnd.nextInt ();
				float cost = 1 - (1f / 0x10000) * (2 * (j >>> 16) + 1),
					sint = (float) Math.sqrt (1 - cost * cost);
				char phi = (char) j;
				po.set (raylength*Math2.ccos (phi) * sint, raylength*Math2.csin (phi) * sint, raylength*cost);
				rs.drawLine (o, po, color, RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
	}

	@Override
	public void generateRandomRays (Environment env, Vector3f out, Spectrum specOut, RayList rays,
									boolean adjoint, Random rnd)
	{
		for (int i = rays.getSize () - 1; i >= 0; i--)
		{
			int j = rnd.nextInt ();
			float cost = 1 - (1f / 0x10000) * (2 * (j >>> 16) + 1),
				sint = (float) Math.sqrt (1 - cost * cost);
			char phi = (char) j;
			Ray r = rays.rays[i];
			r.direction.set (Math2.ccos (phi) * sint,
							 Math2.csin (phi) * sint, cost);
			env.localToGlobal.transform (r.direction);
			r.direction.normalize ();
			r.spectrum.set (specOut);
			r.directionDensity = Math2.M_1_2PI / 2;
		}
	}


	@Override
	public float computeBSDF (Environment env,
							  Vector3f in, Spectrum specIn, Vector3f out, boolean adjoint,
							  Spectrum bsdf)
	{
		env.globalToLocal.transform (out, env.userVector);
		env.userVector.normalize ();
		float d = getDensityAt (env.userVector);
		bsdf.set (specIn);
		bsdf.scale (d);
		return d;
	}

	
	@Override
	public RaytracerLeaf createRaytracerLeaf(Object object, boolean asNode, long pathId, GraphState gs) {
		Light light = (Light) gs.getObjectDefault
			(object, asNode, Attributes.LIGHT, null);
		if (light==null) { System.err.println("PointLight::createRaytracerLeaf - light is null"); }
		if (light.getLightType()!=Light.POINT) { System.err.println("PointLight::createRaytracerLeaf - unexpected light type"); }
		
		return new de.grogra.imp3d.ray.RTPointLight(object, asNode, pathId, light);
	}

	@Override
	public void accept(LightVisitor visitor) {
		visitor.visit( this );
	}

}
