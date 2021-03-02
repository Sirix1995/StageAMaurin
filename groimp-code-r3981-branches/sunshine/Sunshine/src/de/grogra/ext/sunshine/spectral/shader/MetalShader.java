package de.grogra.ext.sunshine.spectral.shader;

import java.util.Random;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.ext.sunshine.spectral.MaterialHandler;
import de.grogra.ext.sunshine.spectral.MaterialHandler.BxDFTypes;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

public class MetalShader extends ShareableBase implements SunshineSpectralShader, MaterialCollector {
	
	//enh:sco de.grogra.persistence.SCOType
	
	SunshineChannel uRoughness = new SunshineIDChannel();
	//enh:field getter setter
	
	SunshineChannel vRoughness = new SunshineIDChannel();
	//enh:field getter setter

	SunshineChannel ior = new SunshineIDChannel();
	//enh:field getter setter
	
	SunshineChannel absorption = new SunshineIDChannel();
	//enh:field getter setter
	

	public float computeBSDF(Environment env, Vector3f in, Spectrum specIn,
			Vector3f out, boolean adjoint, Spectrum bsdf) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void generateRandomRays(Environment env, Vector3f out,
			Spectrum specOut, RayList rays, boolean adjoint, Random random) {
		// TODO Auto-generated method stub
		
	}

	public int getAverageColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getFlags() {
		// TODO Auto-generated method stub
		return 0;
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
	
	public SunshineChannel[] collectData() 
	{
		SunshineChannel[] channel 	= new SunshineChannel[MaterialHandler.getSize(getBxDFType())];
		
		// .. get all channels content and fill these container with them
		channel[0] 	= uRoughness;
		channel[1] 	= vRoughness;
		channel[2] 	= ior;
		channel[3] 	= absorption;
		
		return channel;
	}

	public BxDFTypes getBxDFType() {
		return MaterialHandler.BxDFTypes.METAL;
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field uRoughness$FIELD;
	public static final Type.Field vRoughness$FIELD;
	public static final Type.Field ior$FIELD;
	public static final Type.Field absorption$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (MetalShader representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT + 4;

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
					((MetalShader) o).uRoughness = (SunshineChannel) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((MetalShader) o).vRoughness = (SunshineChannel) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((MetalShader) o).ior = (SunshineChannel) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((MetalShader) o).absorption = (SunshineChannel) value;
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
					return ((MetalShader) o).getURoughness ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((MetalShader) o).getVRoughness ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((MetalShader) o).getIor ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((MetalShader) o).getAbsorption ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new MetalShader ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (MetalShader.class);
		uRoughness$FIELD = Type._addManagedField ($TYPE, "uRoughness", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SunshineChannel.class), null, Type.SUPER_FIELD_COUNT + 0);
		vRoughness$FIELD = Type._addManagedField ($TYPE, "vRoughness", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SunshineChannel.class), null, Type.SUPER_FIELD_COUNT + 1);
		ior$FIELD = Type._addManagedField ($TYPE, "ior", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SunshineChannel.class), null, Type.SUPER_FIELD_COUNT + 2);
		absorption$FIELD = Type._addManagedField ($TYPE, "absorption", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SunshineChannel.class), null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

	public SunshineChannel getURoughness ()
	{
		return uRoughness;
	}

	public void setURoughness (SunshineChannel value)
	{
		uRoughness$FIELD.setObject (this, value);
	}

	public SunshineChannel getVRoughness ()
	{
		return vRoughness;
	}

	public void setVRoughness (SunshineChannel value)
	{
		vRoughness$FIELD.setObject (this, value);
	}

	public SunshineChannel getIor ()
	{
		return ior;
	}

	public void setIor (SunshineChannel value)
	{
		ior$FIELD.setObject (this, value);
	}

	public SunshineChannel getAbsorption ()
	{
		return absorption;
	}

	public void setAbsorption (SunshineChannel value)
	{
		absorption$FIELD.setObject (this, value);
	}

//enh:end
}
