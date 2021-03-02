package de.grogra.imp3d.objects;

import java.util.Random;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.shading.LightVisitor;
import de.grogra.imp3d.shading.SPDIF;
import de.grogra.imp3d.spectral.SpectralCurve;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

public class SpectralLight extends ShareableBase implements Light
{
	//enh:sco SCOType

	SpectralLightMap spectrum = new SpectralLightMapNode();
	//enh:field getter setter

	float power = 100;
	//enh:field quantity=POWER getter setter

	Light light = new PointLight();
	//enh:field

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field spectrum$FIELD;
	public static final Type.Field power$FIELD;
	public static final Type.Field light$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (SpectralLight representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 3;

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
					((SpectralLight) o).power = value;
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
					return ((SpectralLight) o).getPower ();
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((SpectralLight) o).spectrum = (SpectralLightMap) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((SpectralLight) o).light = (Light) value;
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
					return ((SpectralLight) o).getSpectrum ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((SpectralLight) o).light;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new SpectralLight ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (SpectralLight.class);
		spectrum$FIELD = Type._addManagedField ($TYPE, "spectrum", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SpectralLightMap.class), null, Type.SUPER_FIELD_COUNT + 0);
		power$FIELD = Type._addManagedField ($TYPE, "power", 0 | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		light$FIELD = Type._addManagedField ($TYPE, "light", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Light.class), null, Type.SUPER_FIELD_COUNT + 2);
		power$FIELD.setQuantity (de.grogra.util.Quantity.POWER);
		$TYPE.validate ();
	}

	public float getPower ()
	{
		return power;
	}

	public void setPower (float value)
	{
		this.power = value;
		if(value!=100 && ((PointLight)light).getPower()==100) {
			((PointLight)light).setPower(value);
		}
	}

	public SpectralLightMap getSpectrum ()
	{
		return spectrum;
	}

	public void setSpectrum (SpectralLightMap value)
	{
		spectrum$FIELD.setObject (this, value);
	}

//enh:end

	/**
	 * spectral light constructor 
	 **/
	public SpectralLight()
	{
		super();
	}
	
	/**
	 * spectral light constructor 
	 *  
	 *  @param curve the spectral curve, used by the lightsouce
	 **/
	public SpectralLight(SpectralCurve curve)
	{
		super();
		setSpectrum(curve);
	}

	/**
	 * spectral light constructor 
	 *  
	 *  @param spd the resource containing then spectral curve, used by the lightsouce
	 **/
	public SpectralLight(SPDIF spd)
	{
		super();
		setSpectrum(spd.getSpectralDistribution());
	}

	public SpectralLight(Light light, SPDIF spd, float power)
	{
		super();
		this.light = light;
		setSpectrum(spd.getSpectralDistribution());
		this.power = power;
		if(light instanceof PointLight) ((PointLight)this.light).setPower(power);
	}

	public SpectralLight(Light light, SpectralCurve curve, float power)
	{
		super();
		this.light = light;
		setSpectrum(curve);
		this.power = power;
		if(light instanceof PointLight) ((PointLight)this.light).setPower(power);
	}

	@Override
	public Light getLight() {
		return light;
	}

	
	/**
	 * Sets the directional emission distribution of the spectal light. 
	 * The directional emission distribution of the input lights is used as emission distribution  
	 *  
	 *  @param light input light
	 **/
	public void setLight (Light light)
	{
		if(light instanceof PointLight) {
			if(getPower()!=100 && ((PointLight)light).getPower()==100) {
				((PointLight)light).setPower(getPower());
			}
		}
		this.light = light;
	}
	
	/**
	 * sets the spectral distribution
	 *  
	 *  @param curve is the spectral distribution
	 **/
	public void setSpectrum (SpectralCurve curve)
	{
		setSpectrum( new SpectralLightMapNode(curve) );
	}
	
	/**
	 * sets the spectral distribution
	 *  
	 *  @param curve is the spectral distribution
	 **/
	public void setSpectrum (SPDIF spd)
	{
		setSpectrum( new SpectralLightMapNode(spd.getSpectralDistribution()) );
	}
	
	@Override
	public void accept(LightVisitor visitor) {
		visitor.visit( this );
	}

	@Override
	public void generateRandomRays(Environment env, Vector3f out,
			Spectrum specOut, RayList rays, boolean adjoint, Random random) {
		light.generateRandomRays(env, out, specOut, rays, adjoint, random);
	}

	@Override
	public float computeBSDF(Environment env, Vector3f in, Spectrum specIn,
			Vector3f out, boolean adjoint, Spectrum bsdf) {
		float density = light.computeBSDF(env, in, specIn, out, adjoint, bsdf);
		return density; 
	}

	@Override
	public int getLightType() {
		return light.getLightType();
	}

	@Override
	public double getTotalPower(Environment env) {
		int RGB = light.getAverageColor();
		double power = light.getTotalPower(env);
		double color_power = (((RGB >> 0) & 0xFF) + ((RGB >> 8) & 0xFF) + ((RGB >> 16) & 0xFF)) / (256.f);
			
		// compensate for the color power as it is not used
		return (power / color_power) * power;
	}

	@Override
	public void generateRandomOrigins(Environment env, RayList out,
			Random random) {
		light.generateRandomOrigins(env, out, random);
	}

	@Override
	public double computeExitance(Environment env, Spectrum exitance) {
		return light.computeExitance(env, exitance);
	}

	@Override
	public int getFlags() {
		return light.getFlags();
	}

	@Override
	public int getAverageColor() {
		return light.getAverageColor();
	}

	@Override
	public boolean isShadowless() {
		return light.isShadowless();
	}

	@Override
	public boolean isIgnoredWhenHit() {
		return light.isIgnoredWhenHit();
	}

	@Override
	public double completeRay(Environment env, Point3d vertex, Ray out) {
		return light.completeRay(env, vertex, out);
	}

}
