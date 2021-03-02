
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

import javax.vecmath.Color3f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.shading.LightVisitor;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.RayList;

/**
 * This class implements an ambient light.
 * 
 * @author Ole Kniemeyer
 */
public class AmbientLight extends LightBase
{
	//enh:sco

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends LightBase.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (AmbientLight representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, LightBase.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new AmbientLight ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (AmbientLight.class);
		$TYPE.validate ();
	}

//enh:end


	public int getLightType ()
	{
		return AMBIENT;
	}


	@Override
	protected void draw (Tuple3f color, RenderState rs)
	{
	}
	

	public double getTotalPower (Environment env)
	{
		throw new UnsupportedOperationException ("Not supported for ambient lights");
	}
	
	public void generateRandomOrigins (Environment env, RayList out, Random rnd)
	{
		throw new UnsupportedOperationException ("Not supported for ambient lights");
	}


	public double computeExitance (Environment env, Spectrum exitance)
	{
		throw new UnsupportedOperationException ("Not supported for ambient lights");
	}


	public void generateRandomRays (Environment env, Vector3f out, Spectrum specOut, RayList rays,
									boolean adjoint, Random rnd)
	{
		throw new UnsupportedOperationException ("Not supported for ambient lights");
	}


	public float computeBSDF (Environment env,
							  Vector3f in, Spectrum specIn, Vector3f out, boolean adjoint,
							  Spectrum bsdf)
	{
		throw new UnsupportedOperationException ("Not supported for ambient lights");
	}
	
	public void accept(LightVisitor visitor) {
		visitor.visit( this );
	}


}
