
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

package de.grogra.imp3d.shading;

import java.util.Random;

import javax.vecmath.Matrix3f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.math.Channel;
import de.grogra.math.ChannelData;
import de.grogra.math.ChannelMap;
import de.grogra.math.ColorMap;
import de.grogra.math.Graytone;
import de.grogra.math.RGBColor;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Tests;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;

/**
 * A <code>Phong</code> shader represents a Phong-like reflector.
 * Its bidirectional reflection/transmission distribution functions are
 * as follows:
 * <p>
 * At a given point x, let c<sub>d</sub> be the diffuse color
 * (components R, G, B) at that point, &alpha; the alpha-component of the
 * diffuse color and c<sub>t</sub> the transparency color.
 * For each color component, set
 * <center>
 * c<sup>&alpha;</sup><sub>t</sub> = 1 + &alpha; (c<sub>t</sub> - 1)
 * </center>
 * Let c<sub>s</sub> be the specular color and n
 * the shininess exponent.
 * Let r be the reflection coefficient as computed by
 * {@link Math2#fresnel}.
 * <ul><li>
 * Now if <code>interpolatedTransparency</code> is <code>true</code>,
 * set
 * <center>
 * k<sub>d</sub> = (1 - c<sup>&alpha;</sup><sub>t</sub>) c<sub>d</sub><br>
 * k<sub>s</sub> = (1 - c<sup>&alpha;</sup><sub>t</sub>) c<sub>s</sub>
 * + r c<sup>&alpha;</sup><sub>t</sub><br>
 * k<sub>t</sub> = (1 - r) c<sup>&alpha;</sup><sub>t</sub>
 * </center>
 * <li>
 * Otherwise, set
 * <center>
 * k<sub>d</sub> = c<sub>d</sub><br>
 * k<sub>s</sub> = c<sub>s</sub>
 * + r c<sup>&alpha;</sup><sub>t</sub><br>
 * k<sub>t</sub> = (1 - r) c<sup>&alpha;</sup><sub>t</sub>
 * </center>
 * </ul>
 * The bidirectional reflection distribution function is
 * <center>
 * BRDF(x, &omega;<sub>i</sub>, &omega;<sub>o</sub>)
 * = k<sub>d</sub> / &pi;
 * + k<sub>s</sub> (n + 2) max(cos &beta;, 0)<sup>n</sup> / 2&pi;
 * </center>
 * where &beta; is the angle between &omega;<sub>i</sub> and
 * the direction of ideal reflection of &omega;<sub>o</sub>.
 * <p>
 * The bidirectional transmission distribution function is
 * <center>
 * BTDF(x, &omega;<sub>i</sub>, &omega;<sub>t</sub>)
 * = k<sub>t</sub> (&eta;<sub>t</sub> / &eta;<sub>i</sub>)<sup>2</sup>
 * &delta;<sub>&omega;<sup>+</sup></sub>
 * (&omega;<sub>i</sub> - T(&omega;<sub>t</sub>))
 * </center>
 * where &eta; stands for the index of refraction, T for the
 * direction of transmission according to Fresnel's formulas,
 * and &delta;<sub>&omega;<sup>+</sup></sub> is the &delta;-distribution
 * with respect to projected solid angle &omega;<sup>+</sup>. 
 * 
 * @author Ole Kniemeyer
 */
public class Phong extends Material
{
	public static final ColorMap DEFAULT_DIFFUSE = new Graytone (0.5f);
	public static final ColorMap DEFAULT_TRANSPARENCY = new Graytone (0);
	public static final ColorMap DEFAULT_SPECULAR = new Graytone (0);
	public static final ColorMap DEFAULT_DIFFUSE_TRANSPARENCY = new Graytone (0);
	public static final ColorMap DEFAULT_AMBIENT = new Graytone (0);
	public static final ColorMap DEFAULT_EMISSIVE = new Graytone (0);

	public static final float MAX_SHININESS = (float) (2 * Math.PI * DELTA_FACTOR - 2);

	public static final float DEFAULT_SHININESS = 4;

	public static final float DEFAULT_TRANSPARENCY_SHININESS = MAX_SHININESS;

	ChannelMap diffuse = new RGBColor ();
	//enh:field edge=COLOR getter setter

	ChannelMap specular = null;
	//enh:field edge=SPECULAR getter setter

	ChannelMap shininess = null;
	//enh:field edge=SHININESS getter setter

	ChannelMap transparency = null;
	//enh:field edge=TRANSPARENCY getter setter

	ChannelMap transparencyShininess = null;
	//enh:field edge=TRANSPARENCY_SHININESS getter setter
	
	boolean interpolatedTransparency = true;
	//enh:field getter setter

	ChannelMap diffuseTransparency = null;
	//enh:field edge=DIFFUSE_TRANSPARENCY getter setter

	ChannelMap ambient = null;
	//enh:field edge=AMBIENT getter setter

	ChannelMap emissive = null;
	//enh:field edge=EMISSIVE getter setter

	
	public Phong ()
	{
	}


	public static Phong createPhong ()
	{
		Phong p = new Phong ();
		p.setSpecular (new Graytone (0.5f));
		return p;
	}


	public int getAverageColor ()
	{
		return ((diffuse instanceof ColorMap) ? (ColorMap) diffuse : DEFAULT_DIFFUSE)
			.getAverageColor ();
	}


	public int getFlags ()
	{
		return NEEDS_TRANSFORMATION | NEEDS_POINT | NEEDS_NORMAL
			| NEEDS_TANGENTS | NEEDS_UV | IS_NON_OPAQUE;
	}


	public static float convertShininess (float x)
	{
		x = x * (2 - x);
		if (x <= 0)
		{
			return 0;
		}
		else if (x >= 1)
		{
			return MAX_SHININESS;
		}
		else
		{
			return Math.min (-2 / (float) Math.log (x), MAX_SHININESS);
		}
	}

	private static float shininessPow (float x, float shininess)
	{
		return (x >= 1) ? 1 : (float) Math.pow (x, shininess);
	}

	private float computeEnv (Environment env, Vector3f out,
							  ChannelData sink, Vector3f normal, Vector3f rdir,
							  Vector3f tdir,
							  Tuple3f diff, Tuple3f spec, Tuple3f trans, Tuple3f diffTrans,
							  Point2f shininessPoint,
							  boolean adjoint)
	{
		ChannelData input = getInputData (sink);

		input.getTuple3f (normal, sink, Channel.NX);
		normal.normalize ();

		ChannelData cd = sink.getData
			((transparency != null) ? transparency : DEFAULT_TRANSPARENCY);
		cd.getTuple3f (trans, sink, Channel.R);

		cd = sink.getData
			((diffuse != null) ? diffuse : DEFAULT_DIFFUSE);
		cd.getTuple3f (diff, sink, Channel.R);

		float f = cd.getFloatValue (sink, Channel.A);
		trans.x = 1 + f * (trans.x - 1);
		trans.y = 1 + f * (trans.y - 1);
		trans.z = 1 + f * (trans.z - 1);
		
		if (interpolatedTransparency)
		{
			diff.x *= 1 - trans.x;
			diff.y *= 1 - trans.y;
			diff.z *= 1 - trans.z;
		}

		cd = sink.getData
			((diffuseTransparency != null) ? diffuseTransparency : DEFAULT_DIFFUSE_TRANSPARENCY);
		cd.getTuple3f (diffTrans, sink, Channel.R);

		if (shininess != null)
		{
			cd = sink.getData (shininess);
			shininessPoint.x = convertShininess (cd.getFloatValue (sink, Channel.R));
		}
		else
		{
			shininessPoint.x = DEFAULT_SHININESS;
		}

		if (transparencyShininess != null)
		{
			cd = sink.getData (transparencyShininess);
			shininessPoint.y = convertShininess (cd.getFloatValue (sink, Channel.R));
		}
		else
		{
			shininessPoint.y = DEFAULT_TRANSPARENCY_SHININESS;
		}

		float r = Math2.fresnel
			(normal, out, input.getFloatValue (sink, Channel.IOR), rdir, tdir);

		if (specular != null)
		{
			cd = sink.getData (specular);
			cd.getTuple3f (spec, sink, Channel.R);
			if (interpolatedTransparency)
			{
				spec.x *= 1 - trans.x;
				spec.y *= 1 - trans.y;
				spec.z *= 1 - trans.z;
			}
			spec.scaleAdd (r, trans, spec);
		}
		else
		{
			spec.set (0, 0, 0);
		}

		if (adjoint)
		{
			trans.scale (1 - r);
		}
		else 
		{
			float i = env.iorRatio;
			if (env.normal.dot (out) > 0)
			{
				i = 1 / i;
			}
			trans.scale (i * i * (1 - r));
			diffTrans.scale (i * i);
		}
		if (env.normal.dot (out) < 0)
		{
			normal.negate ();
		}
		return r;
	}
	
	
	public void shade (Environment env, RayList rays, Vector3f out, Spectrum outSpec, Tuple3d outColor)
	{
		ChannelData src = getSource (env);
		ChannelData sink = src.getSink ();

		Vector3f color = sink.v3f0, normal = sink.v3f1,
			tmp = sink.v3f2, rdir = sink.v3f3;
	
		Point3f diff = sink.p3f0, spec = sink.p3f1, trans = sink.p3f2,
			amb = sink.p3f3, diffTrans = sink.q3f0;

		Point2f shininess = sink.p2f0;
		
		computeEnv (env, out, sink, normal, rdir, tmp, diff, spec, trans, diffTrans, shininess, true);

		Tuple3d tmpColor = env.tmpPoint0;
		ChannelData cd = sink.getData
			((emissive != null) ? emissive : DEFAULT_EMISSIVE);
		cd.getTuple3f (tmp, sink, Channel.R);
		outColor.set (tmp);

		cd = sink.getData
			((ambient != null) ? ambient : DEFAULT_AMBIENT);
		cd.getTuple3f (amb, sink, Channel.R);

		for (int i = 0; i < rays.getSize(); i++)
		{
			if (rays.rays[i].ambient)
			{
				color.set (amb);
			}
			else
			{
				Vector3f light = rays.rays[i].direction;
				float f = normal.dot (light);
				if ((f >= 0) == (normal.dot (out) >= 0))
				{
					color.set (diff);
					if (spec.x + spec.y + spec.z > 0)
					{
						float s = rdir.dot (light);
						if (s > 0)
						{
							color.scaleAdd (shininessPow (s, shininess.x), spec, color);
						}
					}
					color.scale ((f < 0) ? -f : f);
				}
				else
				{
					color.set (trans);
					color.scaleAdd ((f < 0) ? -f : f, diffTrans, color);
				}
			}
			rays.rays[i].spectrum.dot (outSpec, tmpColor);
			outColor.x += color.x * tmpColor.x;
			outColor.y += color.y * tmpColor.y;
			outColor.z += color.z * tmpColor.z;
		}
	}


	public void computeMaxRays
		(Environment env, Vector3f out, Spectrum outSpec, Ray reflected, Tuple3f refVariance,
		 Ray transmitted, Tuple3f transVariance)
	{
		ChannelData src = getSource (env);
		ChannelData sink = src.getSink ();

		Vector3f normal = sink.v3f0, spec = sink.v3f2;
		Point3f refColor = sink.p3f0, transColor = sink.p3f1, diffTrans = sink.p3f2;
		Point2f shininess = sink.p2f0;
		
		computeEnv (env, out, sink, normal, reflected.direction, transmitted.direction, refColor, spec, transColor, diffTrans, shininess, true);

		float var = 2 / (shininess.y + 3);
		transVariance.x = var;
		transVariance.y = var;
		transVariance.z = var;
		
		if (spec.x + spec.y + spec.z <= 0)
		{
			reflected.direction.set (normal);

			refVariance.x = LAMBERTIAN_VARIANCE;
			refVariance.y = LAMBERTIAN_VARIANCE;
			refVariance.z = LAMBERTIAN_VARIANCE;
		}
		else
		{
			var = 2 / (shininess.x + 3);
			refColor.set (spec);
			
			refVariance.x = var;
			refVariance.y = var;
			refVariance.z = var;
		}
		reflected.spectrum.set (outSpec);
		reflected.spectrum.mul (refColor);
		transmitted.spectrum.set (outSpec);
		transmitted.spectrum.mul (transColor);
	}

	
	static float dot (Tuple3f a, Tuple3f b, boolean luminance)
	{
		return luminance ? 0.2989f * a.x * b.x + 0.5866f * a.y * b.y + 0.1145f * a.z * b.z : a.x * b.x + a.y * b.y + a.z * b.z;
	}

	public void generateRandomRays
		(Environment env, Vector3f out, Spectrum specOut, RayList rays, boolean adjoint, Random rnd)
	{
		ChannelData src = getSource (env);
		ChannelData sink = src.getSink ();

		Vector3f normal = sink.v3f0, rdir = sink.v3f2, tdir = sink.v3f3;
		Point3f diff = sink.p3f0, spec = sink.p3f1, trans = sink.p3f2, diffTrans = sink.p3f3;
		Vector3f col = sink.w3f0;
		Point2f shininessPoint = sink.p2f0;

		computeEnv (env, out, sink, normal, rdir, tdir, diff, spec, trans, diffTrans, shininessPoint, adjoint);

		float ocos = Math.abs (out.dot (normal));

		specOut.get (col);

		boolean lum = env.type != Environment.RADIATION_MODEL;
		float pd = dot (col, diff, lum);
		float ps = dot (col, spec, lum);
		float pdt = dot (col, diffTrans, lum);
		float pst = dot (col, trans, lum);
		float p = pd + ps + pdt + pst;

		if (p < 1e-7)
		{
			p = 0;
		}
		else
		{
			p = 1 / p;
		}

		pd *= p; // pd: diffusely reflected fraction
		ps *= p; // ps: specularly reflected fraction
		pdt *= p; // pt: diffusely transmitted fraction
		pst *= p; // pt: specularly transmitted fraction
		// pd, ps, pdt and pst sum up to 1, or all are 0 if ray is completely absorbed
		
		Matrix3f diffBasis = null, specBasis = null, transBasis = null;

		for (int i = rays.getSize () - 1; i >= 0; i--)
		{
			Vector3f in = rays.rays[i].direction;
			rays.rays[i].valid = true;
			
			// determine randomly if the ray is diffusely or specularly reflected
			// or transmitted (according to probabilities pd, ps, pdt, pst)
			float z = (2 * (rnd.nextInt () >>> 8) + 1) * (1f / (1 << 25));
			// z is uniformly distributed between 0 and 1

			Matrix3f m;
			float sint, cost;
			if (p == 0)
			{
				// completely absorbed
				col.set (0, 0, 0);
				rays.rays[i].direction.set (1, 0, 0);
				rays.rays[i].directionDensity = DELTA_FACTOR;
				rays.rays[i].valid = false;
//				System.err.println("Phong: generateRandomRays: ABSORBED!");
			}
			else
			{
				boolean transmitted;
				boolean specular;
				float shininess;
				if (z <= pst)
				{
					// this is a specularly transmitted ray
					transmitted = true;
					specular = true;
					shininess = shininessPoint.y;
					if ((m = transBasis) == null)
					{
						// compute a local orthogonal basis with its z-axis
						// pointing in the direction of ideal refraction
						m = transBasis = env.userMatrix3;
						Math2.getOrthogonalBasis (tdir, transBasis, true);
					}
				}
				else if (z <= pst + ps)
				{
					// this is a specularly reflected ray
					transmitted = false;
					specular = true;
					shininess = shininessPoint.x;
					if ((m = specBasis) == null)
					{
						// compute a local orthogonal basis with its z-axis
						// pointing in the direction of ideal reflection
						m = specBasis = env.userMatrix2;
						Math2.getOrthogonalBasis (rdir, specBasis, true);
					}
				}
				else
				{
					// this is a diffusely transmitted or reflected ray
					transmitted = z <= pst + ps + pdt;
					specular = false;
					shininess = 0;
					if ((m = diffBasis) == null)
					{
						// compute a local orthogonal basis with its z-axis
						// pointing in the direction of normal
						m = diffBasis = env.userMatrix;
						Math2.getOrthogonalBasis (normal, diffBasis, true);
					}
				}
				int j = rnd.nextInt ();
				float cosr;
				if (specular)
				{
					// choose theta randomly according to the density
					// (n+1) / 2PI * cos(theta)^n 
					double t = (1d / 0x20000) * (2 * (j >>> 16) + 1);
					cost = (float) Math.pow (t, 1 / (shininess + 1));
					sint = (float) Math.sqrt (1 - cost * cost);
					cosr = cost;
				}
				else
				{
					// choose theta randomly according to the density cos(theta)/PI
					z = (2 * (j >>> 16)) * (1f / 0x20000);
					cost = (float) Math.sqrt (z);
					sint = (float) Math.sqrt (1 - z);
					if (transmitted)
					{
						cost = -cost;
					}
					cosr = 0;
				}
				// choose phi randomly between 0 and 2 PI
				char phi = (char) j;

				// set in to the direction vector described by theta and phi
				// in local coordinates
				in.set (Math2.ccos (phi) * sint,
						Math2.csin (phi) * sint, cost);

				// transform in to coordinates of the environment
				m.transform (in);

				cost = in.dot (normal);
				if (transmitted)
				{
					cost = -cost;
				}
				if (cost > 0)
				{
					// OK, randomly chosen direction points to the side
					// of the normal
					
					col.scale (Math2.M_1_PI, transmitted ? diffTrans : diff);
					
					// z: probability densitity of choosing in as diffusely
					// reflected direction
					z = (transmitted ? pdt : pd) * cost * Math2.M_1_PI;

					if (!specular)
					{
						cosr = in.dot (transmitted ? tdir : rdir);
					}
					float prob = transmitted ? pst : ps;
					if ((cosr > 0) && (prob > 0))
					{
						// angle between in and ideally reflected/refracted direction
						// is less than 90 degress. Thus, this ray could
						// have been chosen as a specularly reflected/refracted ray, too
						cosr = shininessPow (cosr, shininess);
						col.scaleAdd ((shininess + 2) * cosr * Math2.M_1_2PI / Math.max (cost, ocos), transmitted ? trans : spec, col);
						z += prob * (shininess + 1) * cosr * Math2.M_1_2PI;
					}
					col.scale (cost / z);
					rays.rays[i].directionDensity = z;
				}
				else
				{
					// direction points to the back-side, reset ray.
					// This can only happen for Phong shaders. It reflects
					// the fact that the total reflectivity of Phong's
					// reflection model depends on the angle between out
					// and normal: For non-perpendicular rays, an additional
					// fraction is absorbed.
					col.set (0, 0, 0);
					rays.rays[i].directionDensity = DELTA_FACTOR;
				}
				rays.rays[i].reflected = !transmitted;
			}
			rays.rays[i].spectrum.set (specOut);
			rays.rays[i].spectrum.mul (col);
		}
	}


	public float computeBSDF
		(Environment env, Vector3f in, Spectrum specIn, Vector3f out, boolean adjoint, Spectrum bsdf)
	{
		float cos = out.dot (env.normal);
		float icos = in.dot (env.normal);
		boolean transmitted = (icos > 0) != (cos > 0);
		if (cos < 0)
		{
			cos = -cos;
		}

		ChannelData src = getSource (env);
		ChannelData sink = src.getSink ();

		Point2f shPoint = sink.p2f0;

		Vector3f normal = sink.v3f1, tdir = sink.v3f2, rdir = sink.v3f3;
		Point3f diff = sink.p3f0, spec = sink.p3f1, trans = sink.p3f2, diffTrans = sink.p3f3;

		Vector3f col = sink.w3f0;

		computeEnv (env, out, sink, normal, rdir, tdir, diff, spec, trans, diffTrans, shPoint, adjoint);

		cos = out.dot (normal);
		icos = in.dot (normal);
		if (cos < 0)
		{
			cos = -cos;
		}

		float shininess;
		if (transmitted)
		{
			Point3f t = diffTrans; diffTrans = diff; diff = t;
			t = trans; trans = spec; spec = t;
			shininess = shPoint.y;
			rdir = tdir;
		}
		else
		{
			shininess = shPoint.x;
		}
		
		float kd = diff.x + diff.y + diff.z,
			ks = spec.x + spec.y + spec.z,
			kt = trans.x + trans.y + trans.z;
		
		float k = kd + ks + kt;
		if (k > 0)
		{
			k = 1 / k;
		}

		float c = rdir.epsilonEquals (in, 1e-6f) ? 1 : rdir.dot (in);
		float p = kd * k * cos * Math2.M_1_PI;
		col.scale (Math2.M_1_PI * cos, diff);
		if ((c > 0) && (ks > 0))
		{
			c = shininessPow (c, shininess);
			icos = Math.abs (icos);
			col.scaleAdd ((shininess + 2) * c * Math2.M_1_2PI * ((icos > cos) ? cos / icos : 1), spec, col);
			p += ks * k * (shininess + 1) * c * Math2.M_1_2PI;
		}
		bsdf.set (specIn);
		bsdf.mul (col);
		return p;
	}

	public boolean isTransparent() {
		if (transparency==null) return false;
		return true;
	}

	public static void main (String[] args)
	{
		Phong phong = new Phong ();
		phong.diffuse = new Graytone (0);
		phong.specular = new Graytone (1);
		phong.transparency = new Graytone (0);
		phong.diffuseTransparency = new Graytone (0);
		float s = 0.02f;
		System.out.println ("# " + convertShininess (s));
		phong.shininess = new Graytone (s);
		Tests.computeAlbedo (phong, 100, 400);
	}
	
	public Phong clone() {
		Phong p = new Phong();
		p.setAmbient(this.getAmbient());
		p.setDiffuse(this.getDiffuse());
		p.setDiffuseTransparency(this.getDiffuseTransparency());
		p.setEmissive(this.getEmissive());
		p.setShininess(this.getShininess());
		p.setSpecular(this.getSpecular());
		p.setTransparency(this.getTransparency());
		p.setTransparencyShininess(this.getTransparencyShininess());
		return p;
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field diffuse$FIELD;
	public static final NType.Field specular$FIELD;
	public static final NType.Field shininess$FIELD;
	public static final NType.Field transparency$FIELD;
	public static final NType.Field transparencyShininess$FIELD;
	public static final NType.Field interpolatedTransparency$FIELD;
	public static final NType.Field diffuseTransparency$FIELD;
	public static final NType.Field ambient$FIELD;
	public static final NType.Field emissive$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Phong.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 5:
					((Phong) o).interpolatedTransparency = (boolean) value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 5:
					return ((Phong) o).isInterpolatedTransparency ();
			}
			return super.getBoolean (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Phong) o).diffuse = (ChannelMap) value;
					return;
				case 1:
					((Phong) o).specular = (ChannelMap) value;
					return;
				case 2:
					((Phong) o).shininess = (ChannelMap) value;
					return;
				case 3:
					((Phong) o).transparency = (ChannelMap) value;
					return;
				case 4:
					((Phong) o).transparencyShininess = (ChannelMap) value;
					return;
				case 6:
					((Phong) o).diffuseTransparency = (ChannelMap) value;
					return;
				case 7:
					((Phong) o).ambient = (ChannelMap) value;
					return;
				case 8:
					((Phong) o).emissive = (ChannelMap) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Phong) o).getDiffuse ();
				case 1:
					return ((Phong) o).getSpecular ();
				case 2:
					return ((Phong) o).getShininess ();
				case 3:
					return ((Phong) o).getTransparency ();
				case 4:
					return ((Phong) o).getTransparencyShininess ();
				case 6:
					return ((Phong) o).getDiffuseTransparency ();
				case 7:
					return ((Phong) o).getAmbient ();
				case 8:
					return ((Phong) o).getEmissive ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Phong ());
		$TYPE.addManagedField (diffuse$FIELD = new _Field ("diffuse", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 0));
		$TYPE.addManagedField (specular$FIELD = new _Field ("specular", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 1));
		$TYPE.addManagedField (shininess$FIELD = new _Field ("shininess", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 2));
		$TYPE.addManagedField (transparency$FIELD = new _Field ("transparency", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 3));
		$TYPE.addManagedField (transparencyShininess$FIELD = new _Field ("transparencyShininess", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 4));
		$TYPE.addManagedField (interpolatedTransparency$FIELD = new _Field ("interpolatedTransparency", 0 | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 5));
		$TYPE.addManagedField (diffuseTransparency$FIELD = new _Field ("diffuseTransparency", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 6));
		$TYPE.addManagedField (ambient$FIELD = new _Field ("ambient", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 7));
		$TYPE.addManagedField (emissive$FIELD = new _Field ("emissive", 0 | _Field.FCO, de.grogra.reflect.ClassAdapter.wrap (ChannelMap.class), null, 8));
		$TYPE.setSpecialEdgeField (diffuse$FIELD, COLOR);
		$TYPE.setSpecialEdgeField (specular$FIELD, SPECULAR);
		$TYPE.setSpecialEdgeField (shininess$FIELD, SHININESS);
		$TYPE.setSpecialEdgeField (transparency$FIELD, TRANSPARENCY);
		$TYPE.setSpecialEdgeField (transparencyShininess$FIELD, TRANSPARENCY_SHININESS);
		$TYPE.setSpecialEdgeField (diffuseTransparency$FIELD, DIFFUSE_TRANSPARENCY);
		$TYPE.setSpecialEdgeField (ambient$FIELD, AMBIENT);
		$TYPE.setSpecialEdgeField (emissive$FIELD, EMISSIVE);
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
		return new Phong ();
	}

	public boolean isInterpolatedTransparency ()
	{
		return interpolatedTransparency;
	}

	public void setInterpolatedTransparency (boolean value)
	{
		this.interpolatedTransparency = (boolean) value;
	}

	public ChannelMap getDiffuse ()
	{
		return diffuse;
	}

	public void setDiffuse (ChannelMap value)
	{
		diffuse$FIELD.setObject (this, value);
	}

	public ChannelMap getSpecular ()
	{
		return specular;
	}

	public void setSpecular (ChannelMap value)
	{
		specular$FIELD.setObject (this, value);
	}

	public ChannelMap getShininess ()
	{
		return shininess;
	}

	public void setShininess (ChannelMap value)
	{
		shininess$FIELD.setObject (this, value);
	}

	public ChannelMap getTransparency ()
	{
		return transparency;
	}

	public void setTransparency (ChannelMap value)
	{
		transparency$FIELD.setObject (this, value);
	}

	public ChannelMap getTransparencyShininess ()
	{
		return transparencyShininess;
	}

	public void setTransparencyShininess (ChannelMap value)
	{
		transparencyShininess$FIELD.setObject (this, value);
	}

	public ChannelMap getDiffuseTransparency ()
	{
		return diffuseTransparency;
	}

	public void setDiffuseTransparency (ChannelMap value)
	{
		diffuseTransparency$FIELD.setObject (this, value);
	}

	public ChannelMap getAmbient ()
	{
		return ambient;
	}

	public void setAmbient (ChannelMap value)
	{
		ambient$FIELD.setObject (this, value);
	}

	public ChannelMap getEmissive ()
	{
		return emissive;
	}

	public void setEmissive (ChannelMap value)
	{
		emissive$FIELD.setObject (this, value);
	}

//enh:end
	
	public void accept(ShaderVisitor visitor) {
		visitor.visit( this );
	}
	
	@Override
	public void accept(ChannelMapNodeVisitor visitor) {
		visitor.visit( this );
	}


}
