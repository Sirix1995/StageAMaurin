
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

/**
 * This class implements a spot light. Light rays are cast
 * from the local origin, the central direction of the spot light is
 * the local z-axis. Light rays with an angle &alpha; to the z-axis less than
 * {@link #innerAngle} have a maximal intensity, the intensity falls
 * off to zero when the angle reaches {@link #outerAngle}. The fall-off
 * is governed by a cubic spline: Let
 * <center>
 * t = (cos &alpha; - cos <code>outerAngle</code>)
 * / (cos <code>innerAngle</code> - cos <code>outerAngle</code>) 
 * </center>
 * then the interpolation factor is
 * <center>
 * (3 - 2 t) t<sup>2</sup>
 * </center>
 * 
 * @author Ole Kniemeyer
 */
public class SpotLight extends PointLight implements Raytraceable
{
	//enh:sco

	float innerAngle = (float) (Math.PI / 8);
	//enh:field quantity=ANGLE getter setter

	float outerAngle = (float) (Math.PI / 6);
	//enh:field quantity=ANGLE getter setter

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field innerAngle$FIELD;
	public static final Type.Field outerAngle$FIELD;

	public static class Type extends PointLight.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SpotLight representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, PointLight.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = PointLight.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = PointLight.Type.FIELD_COUNT + 2;

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
					((SpotLight) o).innerAngle = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((SpotLight) o).outerAngle = (float) value;
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
					return ((SpotLight) o).getInnerAngle ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((SpotLight) o).getOuterAngle ();
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SpotLight ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SpotLight.class);
		innerAngle$FIELD = Type._addManagedField ($TYPE, "innerAngle", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		outerAngle$FIELD = Type._addManagedField ($TYPE, "outerAngle", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		innerAngle$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		outerAngle$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		$TYPE.validate ();
	}

	public float getInnerAngle ()
	{
		return innerAngle;
	}

	public void setInnerAngle (float value)
	{
		this.innerAngle = (float) value;
	}

	public float getOuterAngle ()
	{
		return outerAngle;
	}

	public void setOuterAngle (float value)
	{
		this.outerAngle = (float) value;
	}

//enh:end


	private static final int N = 32;

	@Override
	protected void draw (Tuple3f color, RenderState rs)
	{
		Pool pool = rs.getPool ();
		Tuple3f pi = pool.q3f0, po = pool.q3f1, o = pool.q3f3,
			qi = pool.w3f0, qo = pool.w3f1;

		o.set (0, 0, 0);

		float d = getAttenuationDistance ();
		float zi = d * (float) Math.cos (innerAngle),
			zo = d * (float) Math.cos (outerAngle);

		float ir = (float) Math.sin (innerAngle) * d,
			or = (float) Math.sin (outerAngle) * d;

		for (int i = 0; i <= N; i++)
		{
			float f = i * (float) (2 * Math.PI / N),
				s = (float) Math.sin (f), c = (float) Math.cos (f);
			pi.set (ir * c, ir * s, zi);
			po.set (or * c, or * s, zo);
			if (i > 0)
			{
				rs.drawLine (pi, qi, color, RenderState.CURRENT_HIGHLIGHT, null);
				rs.drawLine (po, qo, color, RenderState.CURRENT_HIGHLIGHT, null);
			}
			if ((i < N) && ((i & 7) == 0))
			{
				rs.drawLine (po, o, color, RenderState.CURRENT_HIGHLIGHT, null);
				rs.drawLine (po, pi, color, RenderState.CURRENT_HIGHLIGHT, null);
			}
			Tuple3f tmp;
			tmp = pi; pi = qi; qi = tmp;
			tmp = po; po = qo; qo = tmp;
		}

		
		//Visualized physical light distribution
		if(visualize) {
			float outer = (float) Math.cos (outerAngle);
			float inner = (float) Math.cos (innerAngle);
			double dd = 1 - 0.5 * (inner + outer);
			Random rnd = new Random(123456789);
			for (int i = numberofrays; i >= 0; i--) {
				int j = rnd.nextInt ();
				double x = (dd / 0x10000) * ((j >>> 16) + 1);
				float cost;
				if (2 * x > inner - outer) {
					cost = (float) (x + 0.5 * (inner + outer));
				}
				else {
					x *= 2 / (inner - outer);
					// solution of 2 t^3 - t^4 = x
					double t = Math.pow ((x > 0.3) ? x : 0.5 * x, 1d / 3);
					while (true) {
						double t2 = t * t;
						double delta = x - t2 * (2 * t - t2);
						if ((-1e-8 < delta) && (delta < 1e-8))
						{
							cost = (float) ((inner - outer) * t + outer);
							break;
						}
						t += delta / (6 * t2 - 4 * t * t2); 
					}
				}
				float sint = (float) Math.sqrt (1 - cost * cost);
				char phi = (char) j;
				po.set (raylength*Math2.ccos (phi) * sint, raylength*Math2.csin (phi) * sint, raylength*cost);
				rs.drawLine (o, po, color, RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
	}


	@Override
	public void generateRandomRays (Environment env, Vector3f out, Spectrum specOut, RayList rays,
									boolean photon, Random rnd)
	{
		float outer = (float) Math.cos (outerAngle);
		float inner = (float) Math.cos (innerAngle);
		double d = 1 - 0.5 * (inner + outer);
		for (int i = rays.getSize() - 1; i >= 0; i--)
		{
			Ray r = rays.rays[i];
			int j = rnd.nextInt ();
			double x = (d / 0x10000) * ((j >>> 16) + 1);
			float cost;
			if (2 * x > inner - outer)
			{
				cost = (float) (x + 0.5 * (inner + outer));
				r.directionDensity = 1 / (float) (Math2.M_2PI * d);
			}
			else
			{
				x *= 2 / (inner - outer);

				// solution of 2 t^3 - t^4 = x
				double t = Math.pow ((x > 0.3) ? x : 0.5 * x, 1d / 3);
				while (true)
				{
					double t2 = t * t;
					double delta = x - t2 * (2 * t - t2);
					if ((-1e-8 < delta) && (delta < 1e-8))
					{
						cost = (float) ((inner - outer) * t + outer);
						r.directionDensity = (float) ((3 - 2 * t) * t2 / (2 * Math.PI * d));
						break;
					}
					t += delta / (6 * t2 - 4 * t * t2); 
				}
			}
			float sint = (float) Math.sqrt (1 - cost * cost);
			char phi = (char) j;
			r.direction.set (Math2.ccos (phi) * sint,
							 Math2.csin (phi) * sint, cost);
			env.localToGlobal.transform (r.direction);
			r.direction.normalize ();
			r.spectrum.set (specOut);
		}
	}


	@Override
	protected float getDensityAt (Vector3f direction)
	{
		float cos = direction.z;
		float outer = (float) Math.cos (outerAngle);
		if (cos <= outer)
		{
			return 0;
		}
		float inner = (float) Math.cos (innerAngle);
		float d = Math2.M_2PI * (1 - 0.5f * (inner + outer));
		if (cos >= inner)
		{
			return 1 / d;
		}
		cos = (cos - outer) / (inner - outer);
		return (3 - 2 * cos) * cos * cos / d;
	}
	
	
	@Override
	public RaytracerLeaf createRaytracerLeaf(Object object, boolean asNode, long pathId, GraphState gs) {
		Light light = (Light) gs.getObjectDefault
			(object, asNode, Attributes.LIGHT, null);
		if (light==null) { System.err.println("PointLight::createRaytracerLeaf - light is null"); }
		if (light.getLightType()!=Light.POINT) { System.err.println("PointLight::createRaytracerLeaf - unexpected light type"); }
		
		return new de.grogra.imp3d.ray.RTSpotLight(object, asNode, pathId, light);
	}

	@Override
	public void accept(LightVisitor visitor) {
		visitor.visit( this );
	}

	
}
