
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
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.ext.sunshine.spectral.MaterialHandler;
import de.grogra.ext.sunshine.spectral.MaterialHandler.BxDFTypes;
import de.grogra.ext.sunshine.spectral.MaterialHandler.MatElemContainer;
import de.grogra.imp3d.shading.ColorMapNode;
import de.grogra.imp3d.shading.ColorMapNodeProperty;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.xl.util.ObjectList;

public class LambertShader extends ShareableBase implements SunshineSpectralShader, MaterialCollector, ColorMapNodeProperty {

	//enh:sco de.grogra.persistence.SCOType
	
	SunshineChannel channel = new SunshineIDChannel();
	//enh:field getter setter
	
	public float computeBSDF(Environment env, Vector3f in, Spectrum specIn,
			Vector3f out, boolean adjoint, Spectrum bsdf) {
		return 0;
	}

	public void generateRandomRays(Environment env, Vector3f out,
			Spectrum specOut, RayList rays, boolean adjoint, Random random) {
	}

	public int getAverageColor() {
		return channel.getAverageColor();
	}

	public int getFlags() {
		return channel.getFlags();
	}

	public void computeMaxRays(Environment env, Vector3f in, Spectrum specIn,
			Ray reflected, Tuple3f refVariance, Ray transmitted,
			Tuple3f transVariance) {
	}

	public boolean isTransparent() {
		return false;
	}

	public void shade(Environment env, RayList in, Vector3f out,
			Spectrum specOut, Tuple3d color) {
	}
	
	public SunshineChannel getDiffuse ()
	{
		return channel;
	}

	public void setDiffuse (SunshineChannel value)
	{
		channel$FIELD.setObject (this, value);
	}
	
	public ColorMapNode getImageChannel() {
		if(channel instanceof ColorMapNode)
			return (ColorMapNode) channel;
		else
			return null;
	}
	
	public SunshineChannel[] collectData() {
		
		SunshineChannel[] channel 	= new SunshineChannel[MaterialHandler.getSize(getBxDFType())];
		
		// .. get all channels content and fill these container with them
		channel[0] = this.channel;
		
		return channel;
		
	}

	public BxDFTypes getBxDFType() {
		return MaterialHandler.BxDFTypes.LAMBERTIAN;
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field channel$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (LambertShader representative, de.grogra.persistence.SCOType supertype)
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
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((LambertShader) o).channel = (SunshineChannel) value;
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
					return ((LambertShader) o).getChannel ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new LambertShader ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (LambertShader.class);
		channel$FIELD = Type._addManagedField ($TYPE, "channel", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SunshineChannel.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public SunshineChannel getChannel ()
	{
		return channel;
	}

	public void setChannel (SunshineChannel value)
	{
		channel$FIELD.setObject (this, value);
	}

//enh:end



}
