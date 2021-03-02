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
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.sunflow.image.IrregularSpectralCurve;
import org.sunflow.image.RGBSpace;
import org.sunflow.image.XYZColor;
import org.sunflow.math.MathUtils;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.ext.sunshine.spectral.SpectralColors;
import de.grogra.ext.sunshine.spectral.SunshineRegularSpectralCurve;
import de.grogra.persistence.ShareableBase;

public class SunshineSPDChannel extends ShareableBase implements SunshineChannel {

	SunshineRegularSpectralCurve curve;
	
	boolean recreate = true;
	
	private int intColor = -1;
	
	//enh:sco de.grogra.persistence.SCOType
	
	String wavelengths;
	//enh:field setmethod=setWavelengths
	
	String intensities;
	//enh:field setmethod=setIntensities

	public SunshineSPDChannel() {
		
	}
	
	public String getWavelengths() {
		return wavelengths;
	}

	public void setWavelengths(String wavelengths) {
		recreate = true;
		this.wavelengths = wavelengths;
	}

	public String getIntensities() {
		return intensities;
	}

	public void setIntensities(String intensities) {
		recreate = true;
		this.intensities = intensities;
	}
	
	private SunshineRegularSpectralCurve generateCurve() {
		
		if(!recreate && curve != null)
			return curve;
		
		recreate			= false;
		
		String[] ws_array	= wavelengths.replaceAll(" ", "").replaceAll(";", ",").split(",");
		String[] is_array	= intensities.replaceAll(" ", "").replaceAll(";", ",").split(",");
		
		if(ws_array.length < 1 || is_array.length != ws_array.length)
			return curve;
		
		float[] w_array		= new float[ws_array.length];
		float[] i_array		= new float[is_array.length];
		
		for(int i = 0; i < w_array.length; i++)
			w_array[i]		= Float.valueOf(ws_array[i]);
		
		for(int i = 0; i < i_array.length; i++)
			i_array[i]		= Float.valueOf(is_array[i]);
		
		IrregularSpectralCurve irr_curve = new IrregularSpectralCurve( w_array, i_array);
		
		curve = SpectralColors.irreg2RegSpecCurve(irr_curve, w_array[0], w_array[w_array.length - 1]);
		
//		for(int i = 0; i < w_array.length; i++)
//			System.out.println(w_array[i] + ": " + curve.sample(w_array[i]));
		
		return curve;
	}
	
	public Object getContent() {
		recreate = true;
		return generateCurve();
	}

	public int getFlags() {
		// TODO Auto-generated method stub
		return 0;
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
	
	private static int f2i (float f)
	{
		int i = Math.round (f * 255);
		return (i < 0) ? 0 : (i > 255) ? 255 : i;
	}
	
	private void calcColor()
	{
		generateCurve();
		
		if(curve == null)
		{
			intColor = (f2i (0) << 16) + (f2i (0) << 8) + f2i (0) + (f2i (1) << 24);
			return;
		}
		
		Color3f color 	= new Color3f();
		XYZColor XYZcol	= curve.toXYZBB();
		
		XYZcol.mul(  SpectralColors.getK(SpectralColors.DISPLAY_WHITE_POINT_BBTEMP) );
		
		RGBSpace.SRGB.convertXYZtoRGB (XYZcol, color);		
		
		float m = -MathUtils.min (color.x, color.y, color.z);
		if (m > 0)
		{
			color.x += m;
			color.y += m;
			color.z += m;
		}
		
		color.scale( (1.f / 3.f));
		
		intColor = (f2i (color.x) << 16) + (f2i (color.y) << 8) + f2i (color.z) + (f2i (1) << 24);
		
	}
	
	public int getAverageColor() 
	{
		if(intColor < 0)
			calcColor();
		
		return intColor;		
	}


	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field wavelengths$FIELD;
	public static final Type.Field intensities$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SunshineSPDChannel representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT + 2;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SunshineSPDChannel) o).setWavelengths ((String) value);
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((SunshineSPDChannel) o).setIntensities ((String) value);
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((SunshineSPDChannel) o).wavelengths;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((SunshineSPDChannel) o).intensities;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SunshineSPDChannel ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SunshineSPDChannel.class);
		wavelengths$FIELD = Type._addManagedField ($TYPE, "wavelengths", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, Type.SUPER_FIELD_COUNT + 0);
		intensities$FIELD = Type._addManagedField ($TYPE, "intensities", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (String.class), null, Type.SUPER_FIELD_COUNT + 1);
		$TYPE.validate ();
	}

//enh:end
	
	
}
