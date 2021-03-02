package de.grogra.ray.physics;

import java.io.Serializable;

import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;


/**
 * This class stores some useful information about incoming rays of an
 * corresponding object, which has received a certain ray. This class extended
 * all properties of {@link Spectrum3d}. It means that this Collector is
 * also a {@link Spectrum}.
 * 
 * This is an implementation for calculation statics of the West Algorithm.
 * For more information see {@link http://doi.acm.org/10.1145/359146.359152}.
 *  
 * To calculate the elements of statistic:
 * 
 * Q = X - M<br>
 * R = Q/rayCount<br>
 * M = M + R<br>
 * T = T +(rayCount - 1) * Q * R
 *  
 * @author Stephan Rogge
 *
 */
public class CollectorW3d extends Spectrum3d implements Collector, Serializable 
{	
	/*
	 * Stores the number of rays which hit the object of
	 * this Collector-instance.
	 */
	protected long rayCount;
	
	/*
	 * This is a flag for marking this instance of the 
	 * collector as a normal spectrum or as the collector. 
	 * This mean, that every operation here is executed as 
	 * operation of the interface {@link: #Spectrum}, when
	 * the value is false.
	 */
	protected boolean asCollector		= false;
	
	/*
	 * Stores the T-sum of all direction components.
	 * This is important for the calculation of the
	 * variance.
	 */
	protected double TDirectionX = 0;
	protected double TDirectionY = 0;
	protected double TDirectionZ = 0;
	
	/*
	 * Stores the M-sum of the direction of an ray.
	 */
	protected double MDirectionX = 0;
	protected double MDirectionY = 0;
	protected double MDirectionZ = 0;
	
	/*
	 * Stores the T-sum of all spectrum components.
	 * This is important for the calculation of the
	 * variance.
	 */
	protected double TSpectrumX = 0;
	protected double TSpectrumY = 0;
	protected double TSpectrumZ = 0;
	
	/*
	 * Stores the M-sum of the spectrum of an ray.
	 */
	protected double MSpectrumX = 0;
	protected double MSpectrumY = 0;
	protected double MSpectrumZ = 0;
	
	
	private double tempQX, tempQY, tempQZ;
	private double tempRX, tempRY, tempRZ;
	
	
	/**
	 * Just a new instance of this class.
	 */
	public CollectorW3d()
	{
		
	}
	
	/**
	 * This constructor creates a Collector-instance with a certain
	 * {@link Spectrum}.
	 * 
	 * @param x Red-component of {@link Spectrum3d}
	 * @param y Green-component of {@link Spectrum3d}
	 * @param z Blue-component of {@link Spectrum3d}
	 */
	public CollectorW3d(double x, double y, double z)
	{
		super ( x, y, z );
		this.add((Spectrum) this );
	}
	
	/**
	 * This constructor creates a Collector-instance with a certain
	 * {@link Spectrum}.
	 * 
	 * @param spectrum Spectrum, which is used by the LightModel
	 */
	public CollectorW3d(Spectrum spectrum)
	{
		this.add((Spectrum) spectrum);
	}

	/**
	 * Returns a new instance of this class.
	 */	
	public Collector3d newInstance ()
	{
		return new Collector3d ();
	}
	
	/**
	 * The initial value of <code>asCollector</code> is false. It means
	 * that the collector works like a {@link Spectrum}. Thats useful
	 * when this Collector-instance is used for the calculations in
	 * {@link LightModel}.
	 */
	public void setAsCollector()
	{
		asCollector = true;
	}
	
	/**
	 * If the collector should works like a Spectrum, this method
	 * returns false, otherwise true.
	 */
	public boolean isCollector()
	{
		return asCollector;
	}
	
	/**
	 * Add the newSpectrum to the spectrum of this Collector-instance
	 * and update the statistics.
	 * 
	 * @param newSpectrum New to added spectrum.
	 */
	public void add(Spectrum spectrum)
	{
		if(!(spectrum instanceof Collector) || !((Collector) spectrum).isCollector())
		{			
			super.add(spectrum);		
						
		} else {
			
			add((Collector) spectrum);
		}
	}
	
		
	public void addToStatistic(Tuple3d rayOrigin, Spectrum spectrum, double scaleFactor, boolean isPrimary)
	{		
		addToStatistic(rayOrigin, (Spectrum3d) spectrum, scaleFactor, isPrimary);
	}
	
	/**
	 * Add the Vector of the origin of a certain ray, which hits the object for this
	 * corresponding Collector-instance. Notice: The direction and the weight of the
	 * direction doesn't depends on the absorbed power but on the intensity of the
	 * incoming ray.
	 * 
	 * @param rayOrigin Where the light comes from.
	 * @param scale The factor for weighting this direction (usually intensity of incoming light)
	 */
	public void addToStatistic(Tuple3d rayOrigin, Spectrum3d spectrum, double scaleFactor, boolean isPrimary)
	{		
		rayCount++;

		double scale = spectrum.integrate() * scaleFactor;
		
		normalize(rayOrigin);
		rayOrigin.negate();
		
		// At first we calculate statistics for directions of light.
		
		// Calculating Q for directions
		tempQX		= ((double) (scale * rayOrigin.x)) - MDirectionX;
		tempQY		= ((double) (scale * rayOrigin.y)) - MDirectionY;
		tempQZ		= ((double) (scale * rayOrigin.z)) - MDirectionZ;
		
		// Calculating R for directions
		tempRX		= tempQX / rayCount;
		tempRY		= tempQY / rayCount;
		tempRZ		= tempQZ / rayCount;
		
		// Calculating M to obtain later the arithmetic mean of all directions of light
		MDirectionX += tempRX;
		MDirectionY += tempRY;
		MDirectionZ += tempRZ;		
		
		// Calculating T to obtain later the variance of all directions of light		
		TDirectionX += ( rayCount - 1 ) * tempQX * tempRX;
		TDirectionY += ( rayCount - 1 ) * tempQY * tempRY;
		TDirectionZ += ( rayCount - 1 ) * tempQZ * tempRZ;
		
		// Directions DONE! ////////////
		
		
		// Now we calculate statistics for the spectra.
		
		// Calculating Q for spectra
		tempQX		= spectrum.x - MSpectrumX;
		tempQY		= spectrum.y - MSpectrumY;;
		tempQZ		= spectrum.z - MSpectrumZ;;
		
		// Calculating R for spectra.
		tempRX		= tempQX / rayCount;
		tempRY		= tempQY / rayCount;
		tempRZ		= tempQZ / rayCount;
		
		// Calculating M to obtain later the arithmetic mean of all received spectra.
		MSpectrumX += tempRX;
		MSpectrumY += tempRY;
		MSpectrumZ += tempRZ;
		
		// We square every element and add to the summation. Later we can use the summation
		// to get the variance. But at this point we only need to add everything.
		
		TSpectrumX += ( rayCount - 1 ) * tempQX * tempRX;
		TSpectrumY += ( rayCount - 1 ) * tempQY * tempRY;
		TSpectrumZ += ( rayCount - 1 ) * tempQZ * tempRZ;	
	}
	

	/**
	 * This methods returns the variance of the spectrums. The
	 * calculation for this is:
	 * 
	 * sigma^2 = 1/N * ((sum of all spectra^2) - (N * x^2))
	 * 
	 * N ... The number of rays
	 * x ... The arithmetic mean of all direction 
	 * 
	 * To get the standard deviation, just extract the root of the 
	 * variance. 
	 * For more information see {@link: http://en.wikipedia.org/wiki/Standard_deviation}.
	 */	
	public void getSpectrumVariance(Spectrum spectrum)
	{
		spectrum.set( 
				new Vector3d(
						TSpectrumX / ( rayCount - 1 ),
						TSpectrumY / ( rayCount - 1 ),
						TSpectrumZ / ( rayCount - 1 )
				)
		);		
	}
	
	/**
	 * This methods returns the variance of the light-direction (not normalized). The
	 * calculation for this is:
	 * 
	 * sigma^2 = 1/N * ((sum of all directions^2) - (N * x^2))
	 * 
	 * N ... The number of rays
	 * x ... The arithmetic mean of all direction 
	 * 
	 * To get the standard deviation, just extract the root of the 
	 * variance.
	 * For more information see {@link: http://en.wikipedia.org/wiki/Standard_deviation}.
	 */
	public void getDirectionVariance(Tuple3d tuple)
	{		
		tuple.x = TDirectionX / ( rayCount - 1 );
		tuple.y = TDirectionY / ( rayCount - 1 );
		tuple.z = TDirectionZ / ( rayCount - 1 );
	}	
	
	public void getDirectionVariance(Tuple3f tuple)
	{		
		tuple.x = (float) ( TDirectionX / ( rayCount - 1 ));
		tuple.y = (float) ( TDirectionY / ( rayCount - 1 ));
		tuple.z = (float) ( TDirectionZ / ( rayCount - 1 ));
	}	
	
	/**
	 * Set the arithmetic mean of all directions (not normalized) to <code>vector</code>.
	 * The parameter <code>vector</code> should be initialized before.
	 * 
	 * @param vector 	Returns the arithmetic mean of all direction 
	 * 					for this Collector-instance.
	 */
	public void getDirectionArithmeticMean(Tuple3d tuple)
	{		
		tuple.x = MDirectionX;
		tuple.y = MDirectionY;
		tuple.z = MDirectionZ;
	}
	
	public void getDirectionArithmeticMean(Tuple3f tuple)
	{		
		tuple.x = (float) MDirectionX;
		tuple.y = (float) MDirectionY;
		tuple.z = (float) MDirectionZ;
	}
	
	/**
	 * Set the arithmetic mean of all spectra to <code>spectrum</code>. 
	 * The parameter <code>spectrum</code> should be initialized before.
	 * 
	 * @param spectrum 	Returns the arithmetic mean of all spectra 
	 * 					for this Collector-instance.
	 */
	public void getSpectrumArithmeticMean(Spectrum spectrum)
	{		
		spectrum.set( 
				new Vector3d(
						MSpectrumX, 
						MSpectrumY, 
						MSpectrumZ
				)
		);
	}
	
	/**
	 * Returns the number of rays which hit the corresponding 
	 * object of this instance.
	 */
	public long getRayCount()
	{
		return rayCount;
	}
	
	/**
	 * Sets all values the instance to zero. Useful when 
	 * the instance is still used as container for information/
	 * results of the {@link LightModel} computation.
	 */
	public void setZero()
	{
		rayCount = 0;
		super.setZero();
		
		TDirectionX = 0;
		TDirectionY = 0;
		TDirectionZ = 0;
		
		MDirectionX = 0;
		MDirectionY = 0;
		MDirectionZ = 0;
		
		TSpectrumX = 0;
		TSpectrumY = 0;
		TSpectrumZ = 0;
		
		MSpectrumX = 0;
		MSpectrumY = 0;
		MSpectrumZ = 0;		
	}
	
	public static void normalize(Tuple3d t)
	{
		
		double length = Math.sqrt(
								(t.x * t.x) + 
								(t.y * t.y) + 
								(t.z * t.z)
						);
		
		t.scale(1/length);		
	}
	
	public static void normalize(Tuple3f t)
	{
		
		float length = (float) Math.sqrt(
								(t.x * t.x) + 
								(t.y * t.y) + 
								(t.z * t.z)
						);
		
		t.scale(1/length);		
	}
	
	/**
	 * To combine two Collector-instances. All values of <code>col</code> 
	 * added to the values of this instance. 
	 * 
	 * @param col To added Collector-instance.
	 */
	public void add(Collector col)
	{				
		super.add(col);
		
		this.rayCount 				+= col.getRayCount();
		
		Point3d sumDir			= new Point3d();
		Point3d sumSquareDir	= new Point3d();
		Point3d sumSpec			= new Point3d();
		Point3d sumSquareSpec	= new Point3d();
		
		col.getCollectedValues(sumDir, sumSquareDir,sumSpec,sumSquareSpec);
		
		this.MDirectionX 		+= sumDir.x;
		this.MDirectionY		+= sumDir.y;
		this.MDirectionZ 		+= sumDir.z;
		
		this.TDirectionX 		+= sumSquareDir.x;
		this.TDirectionY 		+= sumSquareDir.y;
		this.TDirectionZ 		+= sumSquareDir.z;
		
		this.MSpectrumX 		+= sumSpec.x;
		this.MSpectrumY 		+= sumSpec.y;
		this.MSpectrumZ 		+= sumSpec.z;
		
		this.TSpectrumX 		+= sumSquareSpec.x;
		this.TSpectrumY 		+= sumSquareSpec.y;
		this.TSpectrumZ 		+= sumSquareSpec.z;		
	}
	
	
	public void getCollectedValues(Tuple3d sumDir, Tuple3d sumSquareDir, 
			Tuple3d sumSpec, Tuple3d sumSquareSpec)
	{
		sumDir.x 		= this.MDirectionX;
		sumDir.y 		= this.MDirectionY;
		sumDir.z 		= this.MDirectionZ;
		
		sumSquareDir.x	= this.TDirectionX;
		sumSquareDir.y 	= this.TDirectionY;
		sumSquareDir.z 	= this.TDirectionZ;
		
		sumSpec.x 		= this.MSpectrumX;
		sumSpec.y 		= this.MSpectrumY;
		sumSpec.z 		= this.MSpectrumZ;
		
		sumSquareSpec.x	= this.TSpectrumX;
		sumSquareSpec.y = this.TSpectrumY;
		sumSquareSpec.z = this.TSpectrumZ;
	}	
	
	/**
	 * Creates a copy of the Spectrum or a deep-copy of this instance.
	 * 
	 * @return Copy of this instance.
	 */
	public Collector3d clone()
	{
		Collector3d col 			= new Collector3d();		
		
		/*
		 * If this instance works like a Spectrum, just add the
		 * Spectrum of this instance to the cloned Collector.
		 * Otherwise create a deep-copy of all values of this
		 * instance.
		 */ 
		if(!asCollector)	
		{
			
			col.add((Spectrum) this);
		}
		else 
		{		
			col.x					= super.x;
			col.y					= super.y;
			col.z					= super.z;		
			
			col.rayCount 			= this.rayCount;
			
			col.squareSumDirectionX = this.TDirectionX;
			col.squareSumDirectionY = this.TDirectionY;
			col.squareSumDirectionZ = this.TDirectionZ;
			
			col.sumDirectionX 		= this.MDirectionX;
			col.sumDirectionY		= this.MDirectionY;
			col.sumDirectionZ 		= this.MDirectionZ;
			
			col.squareSumSpectrumX 	= this.TSpectrumX;
			col.squareSumSpectrumY 	= this.TSpectrumY;
			col.squareSumSpectrumZ 	= this.TSpectrumZ;
			
			col.sumSpectrumX 		= this.MSpectrumX;
			col.sumSpectrumY 		= this.MSpectrumY;
			col.sumSpectrumZ 		= this.MSpectrumZ;
		}
		
		return col;
	}

	
}

