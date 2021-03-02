package de.grogra.ext.sunshine.spectral.shader;

import java.util.Random;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.ext.sunshine.spectral.MaterialHandler;
import de.grogra.ext.sunshine.spectral.MaterialHandler.BxDFTypes;
import de.grogra.ext.sunshine.spectral.MaterialHandler.MatElemContainer;
import de.grogra.imp3d.shading.Shader;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.xl.util.ObjectList;

public class CookTorranceShader extends ShareableBase implements SunshineSpectralShader, MaterialCollector {
	
	//enh:sco de.grogra.persistence.SCOType
	
	SunshineChannel diffuse = new SunshineIDChannel();
	//enh:field getter setter
	
	SunshineChannel specular = new SunshineIDChannel();
	//enh:field getter setter

	SunshineChannel roughness = new SunshineIDChannel();
	//enh:field getter setter
	
	SunshineChannel ior = new SunshineIDChannel();
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
		return diffuse.getAverageColor();
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
		channel[0] = diffuse;
		channel[1] = specular;
		channel[2] = roughness;
		channel[3] = ior;
		
		return channel;
	}

	public BxDFTypes getBxDFType() {
		return MaterialHandler.BxDFTypes.COOK_TORRANCE;
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field diffuse$FIELD;
	public static final Type.Field specular$FIELD;
	public static final Type.Field roughness$FIELD;
	public static final Type.Field ior$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (CookTorranceShader representative, de.grogra.persistence.SCOType supertype)
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
					((CookTorranceShader) o).diffuse = (SunshineChannel) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((CookTorranceShader) o).specular = (SunshineChannel) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((CookTorranceShader) o).roughness = (SunshineChannel) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((CookTorranceShader) o).ior = (SunshineChannel) value;
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
					return ((CookTorranceShader) o).getDiffuse ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((CookTorranceShader) o).getSpecular ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((CookTorranceShader) o).getRoughness ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((CookTorranceShader) o).getIor ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new CookTorranceShader ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (CookTorranceShader.class);
		diffuse$FIELD = Type._addManagedField ($TYPE, "diffuse", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SunshineChannel.class), null, Type.SUPER_FIELD_COUNT + 0);
		specular$FIELD = Type._addManagedField ($TYPE, "specular", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SunshineChannel.class), null, Type.SUPER_FIELD_COUNT + 1);
		roughness$FIELD = Type._addManagedField ($TYPE, "roughness", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SunshineChannel.class), null, Type.SUPER_FIELD_COUNT + 2);
		ior$FIELD = Type._addManagedField ($TYPE, "ior", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SunshineChannel.class), null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

	public SunshineChannel getDiffuse ()
	{
		return diffuse;
	}

	public void setDiffuse (SunshineChannel value)
	{
		diffuse$FIELD.setObject (this, value);
	}

	public SunshineChannel getSpecular ()
	{
		return specular;
	}

	public void setSpecular (SunshineChannel value)
	{
		specular$FIELD.setObject (this, value);
	}

	public SunshineChannel getRoughness ()
	{
		return roughness;
	}

	public void setRoughness (SunshineChannel value)
	{
		roughness$FIELD.setObject (this, value);
	}

	public SunshineChannel getIor ()
	{
		return ior;
	}

	public void setIor (SunshineChannel value)
	{
		ior$FIELD.setObject (this, value);
	}

//enh:end
}
