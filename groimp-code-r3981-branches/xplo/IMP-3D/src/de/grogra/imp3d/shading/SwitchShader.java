
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

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

/**
 * This abstract base class defines a shader which switches between a set of
 * actual shaders based on the shading environment and ray direction. This
 * can be used, e.g., to use different shaders for front and back side
 * (see the subclass {@link SideSwitchShader}), or to use different shaders
 * depending on the algorithm (raytracer or light model).
 * 
 * @author Ole Kniemeyer
 */
public abstract class SwitchShader extends ShareableBase implements Shader
{
	//enh:sco de.grogra.persistence.SCOType

	/**
	 * This method has to be implemented by subclasses and defines the
	 * actual shader which shall be used depending on the environment
	 * and the ray direction.
	 * 
	 * @param env environment for which shading computations are to be performed
	 * @param in given ray direction 
	 * @return actual shader to use for shading computations
	 */
	protected abstract Shader getShaderFor (Environment env, Vector3f in);


	public void shade (Environment env, RayList in, Vector3f out, Spectrum specOut, Tuple3d color)
	{
		transformEnvironment (env);
		getShaderFor (env, out).shade (env, in, out, specOut, color);
	}


	public void computeMaxRays
		(Environment env, Vector3f in, Spectrum specIn,
		 Ray reflected, Tuple3f refVariance,
		 Ray transmitted, Tuple3f transVariance)
	{
		transformEnvironment (env);
		getShaderFor (env, in).computeMaxRays (env, in, specIn, reflected, refVariance, transmitted, transVariance);
	}


	public void generateRandomRays 
		(Environment env, Vector3f out, Spectrum specOut, RayList rays, boolean adjoint, Random rnd)
	{
		transformEnvironment (env);
		getShaderFor (env, out).generateRandomRays (env, out, specOut, rays, adjoint, rnd);
	}


	public float computeBSDF
		(Environment env, Vector3f in, Spectrum specIn, Vector3f out, boolean adjoint, Spectrum bsdf)
	{
		transformEnvironment (env);
		return getShaderFor (env, in).computeBSDF (env, in, specIn, out, adjoint, bsdf);
	}


	protected void transformEnvironment (Environment e)
	{
	}


	public boolean isTransparent() {
		return false;
	}
	

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SwitchShader representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}
	}

	static
	{
		$TYPE = new Type (SwitchShader.class);
		$TYPE.validate ();
	}

//enh:end

}
