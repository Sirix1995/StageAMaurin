
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

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
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

/**
 * This class implements a directional light. Light rays are cast
 * in the positive direction of the local z-axis.
 * 
 * @author Ole Kniemeyer
 */
public class DirectionalLight extends LightBase implements Raytraceable
{
	//enh:sco
	
	float powerDensity = 10;
	//enh:field quantity=POWER_PER_AREA getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field powerDensity$FIELD;

	public static class Type extends LightBase.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (DirectionalLight representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, LightBase.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = LightBase.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = LightBase.Type.FIELD_COUNT + 1;

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
					((DirectionalLight) o).powerDensity = (float) value;
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
					return ((DirectionalLight) o).getPowerDensity ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new DirectionalLight ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (DirectionalLight.class);
		powerDensity$FIELD = Type._addManagedField ($TYPE, "powerDensity", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		powerDensity$FIELD.setQuantity (de.grogra.util.Quantity.POWER_PER_AREA);
		$TYPE.validate ();
	}

	public float getPowerDensity ()
	{
		return powerDensity;
	}

	public void setPowerDensity (float value)
	{
		this.powerDensity = (float) value;
	}

//enh:end


	@Override
	public int getLightType ()
	{
		return DIRECTIONAL;
	}


	protected static Tuple3f ZERO = new Point3f ();
	private static final int N = 32;

	@Override
	protected void draw (Tuple3f color, RenderState rs)
	{
		Pool pool = rs.getPool ();
		Tuple3f a = pool.v3f0;
		a.set (0, 0, 0);
		a.z = 50 / rs.estimateScaleAt (a);
		rs.drawLine (ZERO, a, color, RenderState.CURRENT_HIGHLIGHT, null);

		//Visualized physical light distribution
		if(visualize) {
			Random rnd = new Random(123456789);
			float radius = 2;
			Tuple3f pi = pool.q3f0, po = pool.q3f1, qi = pool.w3f0, qo = pool.w3f1;
			for (int i = 0; i <= N; i++) {
				float f = i * (float) (2 * Math.PI / N),
					s = (float) Math.sin (f), c = (float) Math.cos (f);
				pi.set (radius * c, radius * s, 0);
				po.set (radius * c, radius * s, 0);
				if (i > 0) {
					rs.drawLine (pi, qi, color, RenderState.CURRENT_HIGHLIGHT, null);
					rs.drawLine (po, qo, color, RenderState.CURRENT_HIGHLIGHT, null);
				}
				if (i < N) {
					rs.drawLine (po, ZERO, color, RenderState.CURRENT_HIGHLIGHT, null);
					rs.drawLine (po, pi, color, RenderState.CURRENT_HIGHLIGHT, null);
				}
				Tuple3f tmp;
				tmp = pi; pi = qi; qi = tmp;
				tmp = po; po = qo; qo = tmp;
			}
			
			float x,y;
			for (int i = numberofrays; i >= 0; i--) {
				// generate a location which is uniformly distributed
				// on the circular area
				int j = rnd.nextInt ();
				double r = Math.sqrt ((j >>> 16) * (1d / 0x10000)) * radius;
				char phi = (char) j;
				x = (float)(Math2.ccos (phi) * r);
				y = (float)(Math2.csin (phi) * r);
				po.set (x,y, raylength);
				a.set(x,y,0);
				rs.drawLine (a, po, color, RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
	}


	@Override
	public double getTotalPower (Environment env)
	{
		return env.boundsRadius * env.boundsRadius * Math.PI * powerDensity;
	}
	
	@Override
	public void generateRandomOrigins (Environment env, RayList out, Random rnd)
	{
		Matrix4f m = env.localToGlobal;
		Vector3d v = env.tmpVector0;
		v.set (m.m02, m.m12, m.m22);
		Matrix3d rot = env.tmpMatrix30;
		Math2.getOrthogonalBasis (v, rot, true);
		double area = env.boundsRadius * env.boundsRadius * Math.PI;
		double areaInv = 1 / area;
		double z = (m.m03 - env.boundsCenter.x) * rot.m02 + (m.m13 - env.boundsCenter.y) * rot.m12
			+ (m.m23 - env.boundsCenter.z) * rot.m22;
		for (int i = 0; i < out.getSize (); i++)
		{
			Ray ray = out.rays[i];

			// generate a location which is uniformly distributed
			// on the circular area
			int j = rnd.nextInt ();
			double r = Math.sqrt ((j >>> 16) * (1d / 0x10000)) * env.boundsRadius;
			char phi = (char) j;
			v.x = Math2.ccos (phi) * r;
			v.y = Math2.csin (phi) * r;
			v.z = z;
			rot.transform (v);
			v.add (env.boundsCenter);
			ray.origin.set (v);
			if (i == 0)
			{
				ray.spectrum.set (color);
				ray.spectrum.scale (powerDensity * area / ray.spectrum.integrate ());
			}
			else
			{
				ray.spectrum.set (out.rays[0].spectrum);
			}
			ray.originDensity = (float) areaInv;
		}
	}


	@Override
	public double computeExitance (Environment env, Spectrum exitance)
	{
		exitance.set (color);
		exitance.scale (powerDensity / (color.x + color.y + color.z));
		return 1 / (env.boundsRadius * env.boundsRadius * Math.PI);
	}


	@Override
	public void generateRandomRays (Environment env, Vector3f out, Spectrum specOut, RayList rays,
									boolean adjoint, Random rnd)
	{
		Matrix4f m = env.localToGlobal;
		Vector3f v = env.userVector;
		v.set (m.m02, m.m12, m.m22);
		v.normalize ();
		for (int i = rays.getSize() - 1; i >= 0; i--)
		{
			Ray r = rays.rays[i];
			r.direction.set (v);
			r.spectrum.set (specOut);
			r.directionDensity = DELTA_FACTOR;
		}
	}


	@Override
	public float computeBSDF (Environment env,
							  Vector3f in, Spectrum specIn, Vector3f out, boolean adjoint,
							  Spectrum bsdf)
	{
		bsdf.setZero ();
		return 0;
	}

	@Override
	public RaytracerLeaf createRaytracerLeaf(Object object, boolean asNode, long pathId, GraphState gs) {
		Light light = (Light) gs.
			getObjectDefault(object, asNode, Attributes.LIGHT, null);
		if (light==null) { System.err.println("PointLight::createRaytracerLeaf - light is null"); }
		if (light.getLightType()!=Light.DIRECTIONAL) { System.err.println("DirectionalLight::createRaytracerLeaf - unexpected light type"); }
	
		return new de.grogra.imp3d.ray.RTDirectionalLight(object, asNode, pathId, light);
	}

	@Override
	public void accept(LightVisitor visitor) {
		visitor.visit( this );
	}

}
