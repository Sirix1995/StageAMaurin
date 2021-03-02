package de.grogra.ray.physics;

import javax.vecmath.Tuple3d;

/**
 * This interface is used to store more information during the computation
 * of the {@link LightModel}. Its an extension of the {@link Spectrum} interface,
 * which means, that every Collector - instance can handled as a {@link Spectrum}
 * instance.
 * 
 * Every implementation of this interface is also a certain specialized 
 * {@link Spectrum}.
 * 
 * @author Stephan Rogge
 *
 */
public interface Collector extends Spectrum {
	
	/**
	 * Returns a clone of this collector.
	 * 
	 * @return clone of this collector
	 */
	Collector clone ();

	/**
	 * Returns a new instance of the class of this collector. It is
	 * initialized to the zero function.
	 * 
	 * @return new instance of same class
	 */
	Collector newInstance ();
		
	/**
	 * Returns the number of rays, which hits the corresponding object.
	 * 
	 * @return Number of rays.
	 */
	long getRayCount();
	
	/**
	 * This method combines to collectors to one single collector
	 * by add every corresponding value. 
	 * 
	 * @param collector which has to be added to the corresponding instance.
	 */
	void add(Collector collector);
		
	/**
	 * The collector works like a {@link Spectrum} after initializing. 
	 * Thats useful when this Collector-instance is used for the calculations 
	 * in {@link LightModel}.
	 */
	void setAsCollector();
	
	/**
	 * Return the state if this instance working as Collector or just as an
	 * {@link Spectrum}.
	 * 
	 * @return
	 */
	boolean isCollector();	
		
	/**
	 * Here the information of {@link LightModel} computation are stored.
	 * 
	 * @param tuple May this contains a certain vector (direction for example).
	 * @param spectrum To added {@link Spectrum}.
	 * @param scaleFactor For the added {@link Spectrum}.
	 * @param isPrimary To distinguish from primary or secondary rays.
	 */
	void addToStatistic(Tuple3d tuple, Spectrum spectrum, double scaleFactor, boolean isPrimary);
	
	/**
	 * Set the arithmetic mean of all spectra to <code>vector</code>.
	 * The parameter <code>vector</code> should be initialized before.
	 * 
	 * @param vector 	Returns the arithmetic mean of all spectra 
	 * 					for this Collector-instance.
	 */
	void getSpectrumArithmeticMean(Spectrum spectrum);
	
	/**
	 * This methods returns the variance of the spectra. The
	 * calculation for this is:
	 * 
	 * sigma^2 = 1/N * ((sum of all spectra^2) - (N * x^2))
	 * 
	 * N ... The number of rays
	 * x ... The arithmetic mean of all spectra 
	 * 
	 * To get the standard deviation, just extract the root of the 
	 * variance. 
	 * For more information see {@link: http://en.wikipedia.org/wiki/Standard_deviation}.
	 */	
	void getSpectrumVariance(Spectrum spectrum);
	
	/**
	 * Set the arithmetic mean of all directions (not normalized) to 
	 * <code>vector</code>. The parameter <code>vector</code> should 
	 * be initialized before.
	 * 
	 * @param vector 	Returns the arithmetic mean of all direction 
	 * 					for this Collector-instance.
	 */
	void getDirectionArithmeticMean(Tuple3d tuple);
	
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
	void getDirectionVariance(Tuple3d tuple);
	
	void getCollectedValues(Tuple3d sumDir, Tuple3d sumSquareDir, Tuple3d sumSpec, Tuple3d sumSquareSpec);
	
}
