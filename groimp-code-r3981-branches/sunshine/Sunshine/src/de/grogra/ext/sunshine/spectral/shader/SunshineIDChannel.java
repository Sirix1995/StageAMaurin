
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

import org.sunflow.image.RGBSpace;
import org.sunflow.image.SpectralCurve;
import org.sunflow.image.XYZColor;
import org.sunflow.math.MathUtils;

import de.grogra.ext.sunshine.spectral.SpectralColors;
import de.grogra.ext.sunshine.spectral.SunshineRegularSpectralCurve;
import de.grogra.ext.sunshine.spectral.colors.ReadFromURL;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

public class SunshineIDChannel extends ShareableBase implements SunshineChannel {

	//enh:sco de.grogra.persistence.SCOType
	
	int id = 0;
	//enh:field setmethod=setId

	SunshineRegularSpectralCurve curve;
	
	int intColor = -1;
	
	public SunshineIDChannel()
	{
		setId (23);
	}
	
	public SunshineIDChannel(int value)
	{
		setId (value);		
	}
	
	public int getId ()
	{
		return id;
	}

	public SpectralCurve getContent()
	{
		return curve;
	}
	
	public void setId (int value)
	{
		this.id = (int) value;
		curve	= SpectralColors.parseString(ReadFromURL.getNameForID(this.id));
		calculateRGB();
	}
	
	private static int f2i (float f)
	{
		int i = Math.round (f * 255);
		return (i < 0) ? 0 : (i > 255) ? 255 : i;
	}

	private void calculateRGB()
	{
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
		
		intColor =(f2i (color.x) << 16) + (f2i (color.y) << 8) + f2i (color.z) + (f2i (1) << 24);
	}
	
	public int getAverageColor() {		
				
		if(intColor == -1)
			calculateRGB();
		
		return intColor;
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
	
	
    public String toString() {
	    return "ID: " + id;
    }
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field id$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SunshineIDChannel representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SunshineIDChannel) o).setId ((int) value);
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((SunshineIDChannel) o).id;
			}
			return super.getInt (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SunshineIDChannel ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SunshineIDChannel.class);
		id$FIELD = Type._addManagedField ($TYPE, "id", 0 | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end
}
