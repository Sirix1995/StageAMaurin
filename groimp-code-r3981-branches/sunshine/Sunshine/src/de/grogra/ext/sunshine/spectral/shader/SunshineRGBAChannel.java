
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

package de.grogra.ext.sunshine.spectral.shader;

import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.sunflow.image.RegularSpectralCurve;

import de.grogra.ext.sunshine.SunshineSpectralPT;
import de.grogra.ext.sunshine.spectral.RGB2Spectrum_Illum;
import de.grogra.ext.sunshine.spectral.RGB2Spectrum_Ref;
import de.grogra.ext.sunshine.spectral.SpectralColors;
import de.grogra.ext.sunshine.spectral.SunshineRegularSpectralCurve;
import de.grogra.imp3d.shading.Shader;
import de.grogra.persistence.ManageableType;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Scattering;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.reflect.ClassAdapter;

public final class SunshineRGBAChannel extends Color4f implements SunshineChannel {

	public static final ManageableType $TYPE;
	
	boolean isLight = false;

	static {
		$TYPE = new ManageableType (ClassAdapter.wrap (SunshineRGBAChannel.class),
								   de.grogra.math.Tuple4fType.$TYPE,
								   true)
			{
				@Override
				public Object getRepresentative ()
				{
					return null;
				}

				@Override
				public Object newInstance ()
				{
					return new SunshineRGBAChannel ();
				}
			}.validate ();
	}

	public SunshineRGBAChannel (float red, float green, float blue, float alpha) {
		super (red, green, blue, alpha);
	}

	public SunshineRGBAChannel () {
		this (0.5f, 0.5f, 0.5f, 1);
	}
	
	private static int f2i (float f)
	{
		int i = Math.round (f * 255);
		return (i < 0) ? 0 : (i > 255) ? 255 : i;
	}
	
	public int getAverageColor ()
	{
		return (f2i (x) << 16) + (f2i (y) << 8) + f2i (z) + (f2i (w) << 24);
	}
	
	public int getFlags ()
	{
		return (w < 1) ? IS_NON_OPAQUE | NEEDS_NORMAL : NEEDS_NORMAL;
	}

	public float computeBSDF(Environment env, Vector3f in, Spectrum specIn,
			Vector3f out, boolean adjoint, Spectrum bsdf) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void generateRandomRays(Environment env, Vector3f out,
			Spectrum specOut, RayList rays, boolean adjoint, Random random) {
		// TODO Auto-generated method stub
		
	}

	public void computeMaxRays(Environment env, Vector3f in, Spectrum specIn,
			Ray reflected, Tuple3f refVariance, Ray transmitted,
			Tuple3f transVariance) {
		// TODO Auto-generated method stub
		
	}

	public boolean isTransparent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void shade(Environment env, RayList in, Vector3f out,
			Spectrum specOut, Tuple3d color) {
		// TODO Auto-generated method stub
		
	}
	
	public SunshineRegularSpectralCurve getContent()
	{
		Color3f color = new Color3f(this.x, this.y, this.z);
		
		if(!isLight)
			return SpectralColors.fromRGB(color, 
										RGB2Spectrum_Ref.refrgb2spect_start, 
										RGB2Spectrum_Ref.refrgb2spect_end, 
										!isLight);
		
		else		
			return SpectralColors.fromRGB(color, 
										RGB2Spectrum_Illum.illumrgb2spect_start, 
										RGB2Spectrum_Illum.illumrgb2spect_end, 
										isLight);
	}


}
