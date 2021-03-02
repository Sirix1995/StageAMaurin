package de.grogra.imp3d.shading;

import java.util.Random;

import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.shading.ShaderVisitor;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

public class IORShader extends ShareableBase implements Shader {

	//enh:sco de.grogra.persistence.SCOType

	Shader inputshader = new RGBAShader(0.5f,0.5f,0.5f);
	//enh:field getter setter
	
	float iorA = 1.f; // in micrometer
	//enh:field getter setter
	
	float iorB = 0.f; // in micrometer
	//enh:field getter setter
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field inputshader$FIELD;
	public static final Type.Field iorA$FIELD;
	public static final Type.Field iorB$FIELD;

	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (IORShader representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = de.grogra.persistence.SCOType.FIELD_COUNT + 3;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					((IORShader) o).iorA = (float) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((IORShader) o).iorB = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 1:
					return ((IORShader) o).getIorA ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((IORShader) o).getIorB ();
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((IORShader) o).inputshader = (Shader) value;
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
					return ((IORShader) o).getInputshader ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new IORShader ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (IORShader.class);
		inputshader$FIELD = Type._addManagedField ($TYPE, "inputshader", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Shader.class), null, Type.SUPER_FIELD_COUNT + 0);
		iorA$FIELD = Type._addManagedField ($TYPE, "iorA", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		iorB$FIELD = Type._addManagedField ($TYPE, "iorB", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		$TYPE.validate ();
	}

	public float getIorA ()
	{
		return iorA;
	}

	public void setIorA (float value)
	{
		this.iorA = (float) value;
	}

	public float getIorB ()
	{
		return iorB;
	}

	public void setIorB (float value)
	{
		this.iorB = (float) value;
	}

	public Shader getInputshader ()
	{
		return inputshader;
	}

	public void setInputshader (Shader value)
	{
		inputshader$FIELD.setObject (this, value);
	}

//enh:end
	
	public IORShader()
	{
		super();
	}
	
	/**
	 * Constructor  
	 *  
	 *  @param iorA first term in Cauchy's equation
	 *  @param iorB second term in Cauchy's equation
	 *  @param inputShader input shader
	 **/
	public IORShader( float iorA, float iorB, Shader inputShader )
	{
		super();
		setIorA( iorA );
		setIorB( iorB );
		setInputshader( inputShader );
	}
	
	public int getFlags() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAverageColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void generateRandomRays(Environment env, Vector3f out,
			Spectrum specOut, RayList rays, boolean adjoint, Random random) {
		// TODO Auto-generated method stub

	}

	public float computeBSDF(Environment env, Vector3f in, Spectrum specIn,
			Vector3f out, boolean adjoint, Spectrum bsdf) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void shade(Environment env, RayList in, Vector3f out,
			Spectrum specOut, Tuple3d color) {
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

	public void accept(ShaderVisitor visitor) {
		visitor.visit(this);
	}

}
