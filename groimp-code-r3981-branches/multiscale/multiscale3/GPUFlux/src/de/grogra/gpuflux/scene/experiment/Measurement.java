package de.grogra.gpuflux.scene.experiment;

/**
 * @author      Dietger van Antwerpen <dietger@xs4all.nl>
 * @version     1.0                                       
 * @since       2011.0824                                 
 *
 * The Measurement class contains a vector of measurements applying to a single object or group of objects.
 * The dimension and units for the measurements are not defined and depend on the context.
 */

public class Measurement {
	
	/**
	 * Constructor
	 * 
	 * @param length the dimension of the measurement vector
	 * */
	public Measurement(int length) {
		data = new double[length];
	}

	/**
	 * Constructor
	 * */
	public Measurement() {
	}

	/**
	 * Constructor
	 * 
	 * @param data the measurement vector data
	 * */
	public Measurement(double[] data) {
		this.data = data;
	}

	/**
	 * Adds a measurement vector to this measurement. 
	 * The measurements are assumed to have the same dimensions and units. 
	 * 
	 * @param m measurement to add 
	 * */
	public void add(Measurement m) {
		for( int i = 0 ; i < data.length ; i++ )
			data[i] += m.data[i];
	}

	/**
	 * Scales a measurement vector by a single factor. 
	 * 
	 * @param scale the scaling factor 
	 * */
	public void mul(double scale) {
		for( int i = 0 ; i < data.length ; i++ )
			data[i] *= scale;
	}
	
	/**
	 * Scaled a measurement vector and adds it to this measurement. 
	 * The measurements are assumed to have the same dimensions and units. 
	 * 
	 * @param m measurement to scall and add 
	 * @param scale scale factor
	 * */
	public void mad(Measurement m, double scale) {
		for( int i = 0 ; i < data.length ; i++ )
			data[i] += m.data[i] * scale;
	}
	
	/**
	 * Returns the sum of the measurement vector elements.
	 * All dimensions are assumed to be of the same unit. 
	 * 
	 * @return sum of all vector elements
	 * */
	public double integrate()
	{	
		double ret = 0.0;
		for( int i = 0 ; i < data.length ; i++ )
			ret += data[i];
		return ret;
	}
	
	public String toString()
	{
		String str = "[ ";
		for( int i = 0 ; i < data.length - 1 ; i++ )
			str += data[i] + " , ";
		str += data[data.length - 1] + " ]";
		return str;
	}
	
	/**
	 * @return true if this measurement contains no data 
	 * */
	public boolean isEmpty()
	{
		return data == null || data.length == 0;
	}
	
	public Object clone()
	{
		return new Measurement( data.clone() );
	}
	
	/**
	 * Measurement vector data 
	 * */
	public double [] data;
}
