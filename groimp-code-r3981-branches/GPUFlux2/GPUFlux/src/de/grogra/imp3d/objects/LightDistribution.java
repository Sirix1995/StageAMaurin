package de.grogra.imp3d.objects;

import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;
import de.grogra.vecmath.Math2;

/**
 * 
 * @author Dietger van Antwerpen
 * 
 * This object holds a discretized 2D light distribution over unit hemisphere
 * It is used for computing the cumulative probability distribution for importance sampling
 * 
 * The object uses the following mapping from 2D (u,v) coordinates in the discetized unit square [0,1]^2 to spherical coordinates (theta,phi)
 *
 * theta = u * PI
 * phi = v * 2 * PI - PI
 * 
 **/

public class LightDistribution extends ShareableBase implements LightDistributionIF
{
// enh:sco SCOType
	
	private static final double CONSERVATIVE_IMPORTANCE = 0.1; // 10% conservative importance
	
	private int width = 1;
	//enh:field
	private int height = 1;
	//enh:field
	private double[][] lipdf = {{1}};
	//enh:field
	private double[] licdf = {1};
	//enh:field
	
	// enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field width$FIELD;
	public static final Type.Field height$FIELD;
	public static final Type.Field lipdf$FIELD;
	public static final Type.Field licdf$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (LightDistribution representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 4;

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
					((LightDistribution) o).width = value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((LightDistribution) o).height = value;
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
					return ((LightDistribution) o).width;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((LightDistribution) o).height;
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					((LightDistribution) o).lipdf = (double[][]) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((LightDistribution) o).licdf = (double[]) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((LightDistribution) o).lipdf;
				case Type.SUPER_FIELD_COUNT + 3:
					return ((LightDistribution) o).licdf;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new LightDistribution ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (LightDistribution.class);
		width$FIELD = Type._addManagedField ($TYPE, "width", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 0);
		height$FIELD = Type._addManagedField ($TYPE, "height", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		lipdf$FIELD = Type._addManagedField ($TYPE, "lipdf", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (double[][].class), null, Type.SUPER_FIELD_COUNT + 2);
		licdf$FIELD = Type._addManagedField ($TYPE, "licdf", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (double[].class), null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

//enh:end
	
	@Override
	public int getWidth() { return width; };
	@Override
	public int getHeight() { return height; };
	@Override
	public double[][] getDistribution() { return lipdf; };
	@Override
	public double[] getLinearCDF() { return licdf; };
		
	public LightDistribution()
	{
		super();
	}
	
	public LightDistribution( double [] [] lipdf ) {
		super();
		setDistribution( lipdf );
	}
	
	private double power;
	
	// normalize distribution, making it integrate to 1 over the unit sphere per solid angle
	// make conservative cumulative distribution
	//	- use maximum filter
	//	- distribute 10% uniform
	@Override
	public void setDistribution( double [] [] lipdf )
	{
		// set distribution
		this.lipdf = lipdf;
		
		// get dimensions
		height = lipdf.length;
		width = lipdf[0].length;
		
		// compute the maximum emitted power on each patches
		double max_pdf [][] = new double[height][width];
		
		// compute the area of all patches
		double patch_area [][] = new double[height][width];
		
		// compute non_zero power area
		double emitting_area = 0;
		
		// compute power
		double max_power = 0;
		
		for( int y = 0 ; y < height; y++ )
			for( int x = 0 ; x < width; x++)
			{
				// get maximum power for patch
				double max = 0;
				for( int dy = 0 ; dy < 2; dy++ )
					for( int dx = 0 ; dx < 2; dx++)
					{
						int nx = Math.min((x + dx),width-1);
						int ny = Math.min((y + dy),height-1);
						
						max = Math.max(max, lipdf[ny][nx]);
					}
				max_pdf[y][x] = max;
				
				// compute area of patches
				float theta_0 = (x / (float)width) * Math2.M_PI;
				float phi_0 = (y / (float)height) * 2 * Math2.M_PI - Math2.M_PI;
				float theta_1 = ((x+1) / (float)width) * Math2.M_PI;
				float phi_1 = ((y+1) / (float)height) * 2 * Math2.M_PI - Math2.M_PI;
				
				double area = (phi_1 - phi_0) * (Math.cos(theta_0) - Math.cos(theta_1));
				
				patch_area[y][x] = area; 
				
				// compute non_zero power area
				if( max_pdf[y][x] > 0 )
					emitting_area += area;

				// integrate interpolated power over patch
				int x1 = Math.min(x+1, width-1);
				int y1 = Math.min(y+1, height-1);
				
				double a = (float) (0.5f * (lipdf[y][x] + lipdf[y1][x]));
				double b = (float) (0.5f * (lipdf[y][x1] + lipdf[y1][x1]));
				double c = theta_0;
				double d = theta_1;	
				
				// Integrate[(x*b + (1 - x)*a)*Sin[x*d + (1 - x)*c], x] ==
				double i0 = (-((c - d)*(a*(-1 + 0) - b*0)*Math.cos(c - c*0 + d*0)) + (-a + b)*Math.sin(c - c*0 + d*0))/((c - d)*(c - d));
				double i1 = (-((c - d)*(a*(-1 + 1) - b*1)*Math.cos(c - c*1 + d*1)) + (-a + b)*Math.sin(c - c*1 + d*1))/((c - d)*(c - d));
				double i = i1 - i0;
				
				// Integrate[Sin[x*d + (1 - x)*c], x] ==
				double n0 = Math.cos(c - c*0 + d*0)/(c - d);
				double n1 = Math.cos(c - c*1 + d*1)/(c - d);
				double n = n1 - n0;
						
				double interpolated_power = i / n;
				
				// compute power
				power += area * interpolated_power;
				max_power += area * max_pdf[y][x];
			}
		
		// allocate linear cdf buffer
		licdf = new double[width*height];
		
		// compute the relative importance of each patch 
		for( int y = 0 ; y < height; y++ )
			for( int x = 0 ; x < width; x++)
			{
				licdf[y*width + x] = 0;
				      
				// add conservative importance 
				if(  max_pdf[y][x] > 0 )
				{
					licdf[y*width + x] = CONSERVATIVE_IMPORTANCE * max_power * (patch_area[y][x] / emitting_area);
				
					// add maximum power importance
					licdf[y*width + x] += (1 - CONSERVATIVE_IMPORTANCE) * max_pdf[y][x] * patch_area[y][x];
				}
			}
		
		// compute cumulative distribution
		double sum = 0;
		for( int y = 0 ; y < height; y++ )
			for( int x = 0 ; x < width; x++)
			{
				sum += licdf[y*width + x];
				licdf[y*width + x] = sum;
			}
		
		// normalize pdf and cdf
		// pdf is normalized to integrate to 1 over unit sphere
		// cdf is normalized to domain [0,1]
		for( int y = 0 ; y < height; y++ )
			for( int x = 0 ; x < width; x++)
			{
				lipdf[y][x] /= power;
				licdf[y*width + x] /= sum;
			}
	}
	
	// normalize distribution, making it integrate to 1 over the unit sphere per solid angle
	// compute blurred cumulative distribution
	@Override
	public void setDistributionEx( double [] [] lipdf )
	{
		// set distribution
		this.lipdf = lipdf;
		
		// get dimensions
		height = lipdf.length;
		width = lipdf[0].length;
		
		// allocate linear cdf buffer
		double power_pdf [][] = new double[height][width];
		licdf = new double[width*height];
		
		// compute area corrected power over unit sphere per unit surface area
		float total_area = 0.f;
		power = 0.f;
		for( int y = 0 ; y < height; y++ )
			for( int x = 0 ; x < width; x++)
			{
				float theta_0 = (x / (float)width) * Math2.M_PI;
				float phi_0 = (y / (float)height) * 2 * Math2.M_PI - Math2.M_PI;
				float theta_1 = ((x+1) / (float)width) * Math2.M_PI;
				float phi_1 = ((y+1) / (float)height) * 2 * Math2.M_PI - Math2.M_PI;
				
				double area = (phi_1 - phi_0) * (Math.cos(theta_0) - Math.cos(theta_1));
				//double area_power = area * lipdf[y][x];
				total_area += area;
				
				// integrate interpolated power over patch
				int x1 = Math.min(x+1, width-1);
				int y1 = Math.min(y+1, height-1);
				
				double a = (float) (0.5f * (lipdf[y][x] + lipdf[y1][x]));
				double b = (float) (0.5f * (lipdf[y][x1] + lipdf[y1][x1]));
				double c = theta_0;
				double d = theta_1;	
				
				// Integrate[(x*b + (1 - x)*a)*Sin[x*d + (1 - x)*c], x] ==
				double i0 = (-((c - d)*(a*(-1 + 0) - b*0)*Math.cos(c - c*0 + d*0)) + (-a + b)*Math.sin(c - c*0 + d*0))/((c - d)*(c - d));
				double i1 = (-((c - d)*(a*(-1 + 1) - b*1)*Math.cos(c - c*1 + d*1)) + (-a + b)*Math.sin(c - c*1 + d*1))/((c - d)*(c - d));
				double i = i1 - i0;
				
				// Integrate[Sin[x*d + (1 - x)*c], x] ==
				double n0 = Math.cos(c - c*0 + d*0)/(c - d);
				double n1 = Math.cos(c - c*1 + d*1)/(c - d);
				double n = n1 - n0;
						
				double interpolated_power = i / n;
				
				double area_power = area * interpolated_power;
				
				power += area_power;
				power_pdf[y][x] = area_power; 
			}

		
		
		// blur the area corrected power 
		for( int y = 0 ; y < height; y++ )
			for( int x = 0 ; x < width; x++)
			{
				double sum = 0;
				for( int dy = 0 ; dy < 2; dy++ )
					for( int dx = 0 ; dx < 2; dx++)
					{
						int nx = Math.min((x + dx),width-1);
						int ny = Math.min((y + dy),height-1);
						
						sum += power_pdf[ny][nx];
					}
				licdf[y*width + x] = sum / 4.0;
			}
		
		// compute cumulative distribution
		double sum = 0;
		for( int y = 0 ; y < height; y++ )
			for( int x = 0 ; x < width; x++)
			{
				sum += licdf[y*width + x];
				licdf[y*width + x] = sum;
			}
	
		// normalize pdf and cdf
		// pdf is normalized to integrate to 1 over unit sphere
		// cdf is normalized to domain [0,1]
		for( int y = 0 ; y < height; y++ )
			for( int x = 0 ; x < width; x++)
			{
				lipdf[y][x] /= power;
				licdf[y*width + x] /= sum;
			}
	}
	
	
	static double interpolate(double d00, double d01, double d10, double d11, double d, double e)
	{
		return d00*(1-d)*(1-e) + d01*(1-d)*e + d10*d*(1-e) + d11*d*e;
	}
	
	// get total power of the power distribution
	@Override
	public double getPower() { return power; }
	
	// get total power of the power distribution
	@Override
	public void setPower(double power) { this.power = power; }
	
	// get normalized power density for specified direction
	@Override
	public double getDensityAt(Vector3f direction) {
		double result = 0;
		
		// calculate spherical coordinates
		Vector2f spherical = cartesian2map( direction );
		
		// calculate indices into discrete buffer
		float x = spherical.x * width;
		float y = spherical.y * height;
		int theta0 = Math.max(0, Math.min(width-1, (int) x));
		int theta1 = Math.min(width-1, theta0+1);
		int phi0 = Math.max(0, Math.min(height-1, (int) y));
		int phi1 = Math.min(height-1, phi0+1);
						
		// calculate bilinear interpolated density for given direction
		double d00 = lipdf[phi0][theta0];
		double d10 = lipdf[phi0][theta1];
		double d01 = lipdf[phi1][theta0];
		double d11 = lipdf[phi1][theta1];
		result = interpolate(d00, d01, d10, d11, x - Math.floor(x), y - Math.floor(y));
		//result = d00;
				
		return result;
	} // end density
	
	// transform 2d map coordinates to a 3d direction with density proportional to power density
	// return the sample density per unit solid angle
	@Override
	public double map2direction(Vector3f outDirection, Tuple2d inPoint) {
		// sample point in cdf
		double r = inPoint.x; 
		int low = 0, high = width*height;
		r *= licdf[high - 1];
		while( low + 1 < high )
		{
			int middle = (low + high) >> 1;
			double cumtotal = licdf[middle - 1];
			if( cumtotal >= r )
				high = middle;
			else
				low = middle;
		};
		
		int idx = low;
		
		// get the cdf interval
		double low_cdf = idx==0?0:licdf[idx - 1];
		double high_cdf = licdf[idx];
		
		// compute pdf for interval
		double pdf_interval = high_cdf - low_cdf;
		
		// compute the position within the interval
		double r1 = ((inPoint).x - low_cdf) / pdf_interval;
		double r2 = (inPoint).y;
		
		// calculate position into discrete buffer
		int ix = (idx % width);
		int iy = (idx / width);
		
		// compute area of patches
		float theta_0 = (ix / (float)width) * Math2.M_PI;
		float theta_1 = ((ix+1) / (float)width) * Math2.M_PI;
		
		float d_phi = (1.f / (float)height) * 2 * Math2.M_PI;
		
		// sample theta uniformly per unit solid angle (proportional to sin theta)
		float cos_theta_0 = (float) Math.cos(theta_0);
		float cos_theta_1 = (float) Math.cos(theta_1);

		float area = d_phi * (cos_theta_0 - cos_theta_1);
		
		// calculate position into discrete buffer
		float x = (idx % width) + (float)r1;
		float y = (idx / width) + (float)r2;
		
		// compute the position within the 2d distribution
		float u = x / width;
		float v = y / height;
		
		// map to spherical coordinates
		float theta = (float) Math.acos( (1.f - r1) * cos_theta_0 + r1 * cos_theta_1 );
		float phi = (float) (((iy + r2) / (float)height) * 2 * Math.PI - Math.PI);
		
		// set out direction
		float sint = (float) Math.sin(theta);
		float cost = (float) Math.cos(theta);
		
		outDirection.set (
			(float)Math.cos (phi) * sint,
	    	(float)Math.sin (phi) * sint, 
	    	cost );
		
		outDirection.set( map2cartesian(u,v) );
		
		// return relative mapping density
		return (pdf_interval) * ((area < 0.001f) ? (0.f) : (1/area));
	}
	
	public static Vector3f map2cartesian( float r1, float r2 )
	{
		// set out direction
		float theta = r1 * Math2.M_PI;
		float phi = r2 * 2 * Math2.M_PI - Math2.M_PI;
		
		float sint = (float) Math.sin(theta);
		float cost = (float) Math.cos(theta);
		
		return new Vector3f (
			(float)Math.cos (phi) * sint,
	    	(float)Math.sin (phi) * sint, 
	    	cost );
	}
	
	public static void map2cartesian( Tuple3f out, float r1, float r2 )
	{
		// set out direction
		float theta = r1 * Math2.M_PI;
		float phi = r2 * 2 * Math2.M_PI - Math2.M_PI;
		
		float sint = (float) Math.sin(theta);
		float cost = (float) Math.cos(theta);
		
		out.set(
			(float)Math.cos (phi) * sint,
	    	(float)Math.sin (phi) * sint, 
	    	cost );
	}
	
	public static Vector2f cartesian2map( Vector3f direction )
	{
		// calculate spherical coordinates
		float theta = (float) Math.atan2(Math.sqrt(direction.x*direction.x + direction.y*direction.y), direction.z);
		float phi = (float) Math.atan2(direction.y, direction.x);

		// map to 2D distribution
		float u = (theta * Math2.M_1_PI);
		float v = (phi+Math2.M_PI) / Math2.M_2PI;
				
		return new Vector2f(u,v);
	}
	
	
}
