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
 * @author Stephan Rogge
 *
 */
public class Collector3d extends Spectrum3d implements Collector, Serializable 
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
	 * Stores the scare-sum of all direction components.
	 * This is important for the calculation of the
	 * variance.
	 */
	protected double squareSumDirectionX = 0;
	protected double squareSumDirectionY = 0;
	protected double squareSumDirectionZ = 0;
	
	/*
	 * Stores the sum of the direction of an ray.
	 */
	protected double sumDirectionX = 0;
	protected double sumDirectionY = 0;
	protected double sumDirectionZ = 0;
	
	/*
	 * Stores the scare-sum of all spectrum components.
	 * This is important for the calculation of the
	 * variance.
	 */
	protected double squareSumSpectrumX = 0;
	protected double squareSumSpectrumY = 0;
	protected double squareSumSpectrumZ = 0;
	
	/*
	 * Stores the sum of the spectrum of an ray.
	 */
	protected double sumSpectrumX = 0;
	protected double sumSpectrumY = 0;
	protected double sumSpectrumZ = 0;
	
	
	/**
	 * Just a new instance of this class.
	 */
	public Collector3d()
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
	public Collector3d(double x, double y, double z)
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
	public Collector3d(Spectrum spectrum)
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
		// This block calculates the arithmetic mean of all directions. In order to save 
		// memory, we don't want to store every direction: So we have to reverse the
		// arithmetic mean (a.m.) calculation by multiplying the current a.m. with the
		// last number of elements (rayCount-1). Then we can add the new value and divide
		// all by the current number of elements (rayCount).
		// We do this separately for every element.			
		rayCount++;

		double scale = spectrum.integrate() * scaleFactor;
		
		normalize(rayOrigin);
		rayOrigin.negate();
		
		sumDirectionX += ((double) (scale * rayOrigin.x));
		sumDirectionY += ((double) (scale * rayOrigin.y));
		sumDirectionZ += ((double) (scale * rayOrigin.z));		
		
		// We square every element and add to the summation. Later we can use the summation
		// to get the variance. But at this point we only need to add everything.
		
		squareSumDirectionX += ((double) rayOrigin.x * rayOrigin.x);
		squareSumDirectionY += ((double) rayOrigin.y * rayOrigin.y);
		squareSumDirectionZ += ((double) rayOrigin.z * rayOrigin.z);
		
		sumSpectrumX += spectrum.x;
		sumSpectrumY += spectrum.y;
		sumSpectrumZ += spectrum.z;
		
		// We square every element and add to the summation. Later we can use the summation
		// to get the variance. But at this point we only need to add everything.
		
		squareSumSpectrumX += spectrum.x * spectrum.x;
		squareSumSpectrumY += spectrum.y * spectrum.y;
		squareSumSpectrumZ += spectrum.z * spectrum.z;	
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
						(squareSumSpectrumX - sumSpectrumX) / rayCount,
						(squareSumSpectrumY - sumSpectrumY) / rayCount,
						(squareSumSpectrumZ - sumSpectrumZ) / rayCount
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
		tuple.x = (squareSumDirectionX - (sumDirectionX * sumDirectionX)) / rayCount;
		tuple.y = (squareSumDirectionY - (sumDirectionY * sumDirectionY)) / rayCount;
		tuple.z = (squareSumDirectionZ - (sumDirectionZ * sumDirectionZ)) / rayCount;
	}	
	
	public void getDirectionVariance(Tuple3f tuple)
	{		
		tuple.x = (float) ((squareSumDirectionX - (sumDirectionX * sumDirectionX)) / rayCount);
		tuple.y = (float) ((squareSumDirectionY - (sumDirectionY * sumDirectionY)) / rayCount);
		tuple.z = (float) ((squareSumDirectionZ - (sumDirectionZ * sumDirectionZ)) / rayCount);
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
		tuple.x = sumDirectionX / rayCount;
		tuple.y = sumDirectionY / rayCount;
		tuple.z = sumDirectionZ / rayCount;
	}
	
	public void getDirectionArithmeticMean(Tuple3f tuple)
	{		
		tuple.x = (float) (sumDirectionX / rayCount);
		tuple.y = (float) (sumDirectionY / rayCount);
		tuple.z = (float) (sumDirectionZ / rayCount);
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
						sumSpectrumX / rayCount, 
						sumSpectrumY / rayCount, 
						sumSpectrumZ / rayCount
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
		
		squareSumDirectionX = 0;
		squareSumDirectionY = 0;
		squareSumDirectionZ = 0;
		
		sumDirectionX = 0;
		sumDirectionY = 0;
		sumDirectionZ = 0;
		
		squareSumSpectrumX = 0;
		squareSumSpectrumY = 0;
		squareSumSpectrumZ = 0;
		
		sumSpectrumX = 0;
		sumSpectrumY = 0;
		sumSpectrumZ = 0;		
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
		
		Point3d sumDir				= new Point3d();
		Point3d sumSquareDir		= new Point3d();
		Point3d sumSpec				= new Point3d();
		Point3d sumSquareSpec		= new Point3d();
		
		col.getCollectedValues(sumDir, sumSquareDir,sumSpec,sumSquareSpec);
		
		this.sumDirectionX 			+= sumDir.x;
		this.sumDirectionY			+= sumDir.y;
		this.sumDirectionZ 			+= sumDir.z;
		
		this.squareSumDirectionX 	+= sumSquareDir.x;
		this.squareSumDirectionY 	+= sumSquareDir.y;
		this.squareSumDirectionZ 	+= sumSquareDir.z;
		
		this.sumSpectrumX 			+= sumSpec.x;
		this.sumSpectrumY 			+= sumSpec.y;
		this.sumSpectrumZ 			+= sumSpec.z;
		
		this.squareSumSpectrumX 	+= sumSquareSpec.x;
		this.squareSumSpectrumY 	+= sumSquareSpec.y;
		this.squareSumSpectrumZ 	+= sumSquareSpec.z;		
	}
	
	
	public void getCollectedValues(Tuple3d sumDir, Tuple3d sumSquareDir, 
			Tuple3d sumSpec, Tuple3d sumSquareSpec)
	{
		sumDir.x 		= this.sumDirectionX;
		sumDir.y 		= this.sumDirectionY;
		sumDir.z 		= this.sumDirectionZ;
		
		sumSquareDir.x	= this.squareSumDirectionX;
		sumSquareDir.y 	= this.squareSumDirectionY;
		sumSquareDir.z 	= this.squareSumDirectionZ;
		
		sumSpec.x 		= this.sumSpectrumX;
		sumSpec.y 		= this.sumSpectrumY;
		sumSpec.z 		= this.sumSpectrumZ;
		
		sumSquareSpec.x	= this.squareSumSpectrumX;
		sumSquareSpec.y = this.squareSumSpectrumY;
		sumSquareSpec.z = this.squareSumSpectrumZ;
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
			
			col.squareSumDirectionX = this.squareSumDirectionX;
			col.squareSumDirectionY = this.squareSumDirectionY;
			col.squareSumDirectionZ = this.squareSumDirectionZ;
			
			col.sumDirectionX 		= this.sumDirectionX;
			col.sumDirectionY		= this.sumDirectionY;
			col.sumDirectionZ 		= this.sumDirectionZ;
			
			col.squareSumSpectrumX 	= this.squareSumSpectrumX;
			col.squareSumSpectrumY 	= this.squareSumSpectrumY;
			col.squareSumSpectrumZ 	= this.squareSumSpectrumZ;
			
			col.sumSpectrumX 		= this.sumSpectrumX;
			col.sumSpectrumY 		= this.sumSpectrumY;
			col.sumSpectrumZ 		= this.sumSpectrumZ;
		}
		
		return col;
	}

	
}
