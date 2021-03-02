package de.grogra.imp3d.objects;

import java.util.Random;

import javax.vecmath.Point2d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.shading.Light;
import de.grogra.math.Pool;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;

public class PhysicalLight extends PointLight
{
//enh:sco
	
	private static final int PHYSICAL_RESOLUTION = 0;
		
	LightDistributionIF distribution = new LightDistribution();
	//enh:field getter setter
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field distribution$FIELD;

	public static class Type extends PointLight.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (PhysicalLight representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, PointLight.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = PointLight.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = PointLight.Type.FIELD_COUNT + 1;

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
					((PhysicalLight) o).distribution = (LightDistributionIF) value;
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
					return ((PhysicalLight) o).getDistribution ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new PhysicalLight ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (PhysicalLight.class);
		distribution$FIELD = Type._addManagedField ($TYPE, "distribution", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (LightDistribution.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

	public LightDistributionIF getDistribution ()
	{
		return distribution;
	}

	public void setDistribution (LightDistributionIF value)
	{
		distribution$FIELD.setObject (this, value);
	}

//enh:end
	
	/**
	 * physical light constructor 
	 *  
	 **/
	public PhysicalLight()
	{
		super();
	}
	
	/**
	 * physical light constructor 
	 *  
	 *  @param distribution is the light emission distribution over the unit sphere
	 **/
	public PhysicalLight(LightDistributionIF distribution)
	{
		super();
		setDistribution(distribution);
	}
	
	/**
	 * physical light constructor 
	 *  
	 *  @param distribution is the 2D discretized light emission distribution over the unit sphere
	 **/
	public PhysicalLight(double [][] distribution)
	{
		this( new LightDistribution( distribution ) );
	}
	
	/**
	 * sets the light emission dsitribution 
	 *  
	 *  @param distribution is the 2D discretized light emission distribution over the unit sphere
	 **/
	public void setDistribution (double [][] distribution)
	{
		setDistribution( new LightDistribution(distribution) );
	}

	/**
	 * sets the light emission dsitribution by discretizing the emission distribution of an existing light source
	 *  
	 *  @param light is used to construct a discretized emission distribution
	 **/
	public void setDistribution (Light light)
	{
		Environment env = new Environment();
		env.point.set(0,0,0);
		env.localPoint.set(0,0,0);
		env.normal.set(0,0,1);
		env.dpdu.set(1,0,0);
		env.dpdv.set(0,1,0);
		env.solid = false;
		
		Vector3f in = new Vector3f(0,0,-1);
		Vector3f out = new Vector3f(0,0,1);
		Spectrum3d spectrum = new Spectrum3d();
		
		double [][] pdf = new double[PHYSICAL_RESOLUTION][PHYSICAL_RESOLUTION];
		
		Spectrum3d bsdf = new Spectrum3d();
		Spectrum3d avr_bsdf = new Spectrum3d();
		
		for( int x = 0 ; x < PHYSICAL_RESOLUTION ; x++ )
			for( int y = 0 ; y < PHYSICAL_RESOLUTION ; y++ )
			{
				float u = x / (float)PHYSICAL_RESOLUTION;
				float v = y / (float)PHYSICAL_RESOLUTION;
				
				env.uv.set(u,v);
				
				Point2d inPoint = new Point2d(u,v);
				
				double density = distribution.map2direction(out, inPoint);
				in.negate(out);
				
				light.computeExitance(env , spectrum);
				light.computeBSDF(env, in, spectrum, out, false, bsdf);
				
				pdf[y][x] = bsdf.integrate() / 3.f;
				
				bsdf.scale(density);
				avr_bsdf.add((Spectrum)bsdf);
			}
		
		avr_bsdf.scale(1.0 / (4*Math.PI));

		getColor().set( avr_bsdf );
		setDistribution( pdf );
	}


	@Override
	protected void draw (Tuple3f color, RenderState rs)
	{
		Pool pool = rs.getPool ();
		Tuple3f o = pool.q3f3;
		o.set (0, 0, 0);

		//Visualized physical light distribution
		if(visualize) {
			if( distribution != null ) {
				Random rnd = new Random(123456789);

				for (int i = numberofrays; i >= 0; i--) {
					int j = rnd.nextInt ();
					float r1 = (1f / 0x10000) * (j >>> 16);
					float r2 = (1f / 0x10000) * (j & 0x10000);
				
					Point2d inPoint = new Point2d(r1,r2);

					Ray r = new Ray();
					float sampleDensity = (float) distribution.map2direction(r.direction, inPoint);
					r.direction.normalize ();
					if(sampleDensity>0.4) sampleDensity = 0.4f;
					r.direction.scale(sampleDensity*2*raylength);
					rs.drawLine (o, r.direction, color, RenderState.CURRENT_HIGHLIGHT, null);
				}
			}
		}
	}

	@Override
	 public void generateRandomRays(Environment env, Vector3f out,
			 Spectrum specOut, RayList rays, boolean adjoint, Random rnd) {
		
		if( distribution == null )
		{
			super.generateRandomRays(env, out, specOut, rays, adjoint, rnd);
		}
		else
		{
			for (int i = rays.getSize () - 1; i >= 0; i--)
			{
			    int j = rnd.nextInt ();
			    float r1 = (1f / 0x10000) * (j >>> 16);
			    float r2 = (1f / 0x10000) * (j & 0x10000);
			    
			    Point2d inPoint = new Point2d(r1,r2);
			    
			    Ray r = rays.rays[i];
			    float sampleDensity = (float) distribution.map2direction(r.direction, inPoint);
			    float powerDensity = getDensityAt (r.direction);
			    env.localToGlobal.transform (r.direction);
			    r.direction.normalize ();
			    r.spectrum.set (specOut);
			    r.spectrum.scale (powerDensity / sampleDensity);
			    r.directionDensity = sampleDensity;
			}
		}
	}
	
	@Override
	protected float getDensityAt (Vector3f direction)
	{
		if( distribution == null )
		{
			return super.getDensityAt(direction);
		}
		else
		{
			// return distribution
			return (float) distribution.getDensityAt(direction);
		}
	}
	
}
