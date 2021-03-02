package de.grogra.gpuflux.scene.experiment;

import java.util.Vector;

import de.grogra.gpuflux.FluxSettings;
import de.grogra.gpuflux.scene.FluxScene;
import de.grogra.gpuflux.scene.volume.FluxPrimitive;
import de.grogra.gpuflux.scene.volume.FluxSensor;
import de.grogra.vecmath.BoundingBox3d;
import de.grogra.vecmath.geom.Variables;

public class MeasuringSetup {

	/**
	 * Minimum total number of measurements used to represent all detectors.
	 */
	public static final int MIN_MEASUREMENTS = 1024*256;

	public static final double EPSILON = 0.00001;
	
	public class FluxDetector {
		
		public FluxDetector(int offset, int measurements)
		{
			this.offset = offset;
			this.measurements = measurements;
		}
		
		public int getOffset(){ return offset; };
		public int getMeasurements(){ return measurements; };
		
		private int offset;
		private int measurements;
	}
	
	private Vector<FluxDetector> detectors = new Vector<FluxDetector>();
	private int numMeasurements;
	private int numMeasurementsBits;
	private int measureDimensions;

	public MeasuringSetup()
	{}
	
	public int getDimensions()
	{
		return measureDimensions;
	}
	
	public void setDimensions(int measureDimensions)
	{
		this.measureDimensions = measureDimensions;
	}
		
	// shuffle the measurement locations
	public int shuffleMeasurement( int value , int bits)
	{
		// mirror the lower binary bits in value 
		value = ((value & 0xAAAAAAAA) >>>  1) | ((value & 0x55555555) <<  1);
		value = ((value & 0xCCCCCCCC) >>>  2) | ((value & 0x33333333) <<  2);
		value = ((value & 0xF0F0F0F0) >>>  4) | ((value & 0x0F0F0F0F) <<  4);
		value = ((value & 0xFF00FF00) >>>  8) | ((value & 0x00FF00FF) <<  8);
		value = ((value & 0xFFFF0000) >>> 16) | ((value & 0x0000FFFF) << 16);
		return value >>> (32-bits);
	}
	
	// shuffle the measurement locations
	public int unshuffleMeasurement( int value , int bits)
	{
		// reverse the shifting
		value <<= (32-bits);
		// mirror the lower binary bits in value 
		value = ((value & 0xAAAAAAAA) >>>  1) | ((value & 0x55555555) <<  1);
		value = ((value & 0xCCCCCCCC) >>>  2) | ((value & 0x33333333) <<  2);
		value = ((value & 0xF0F0F0F0) >>>  4) | ((value & 0x0F0F0F0F) <<  4);
		value = ((value & 0xFF00FF00) >>>  8) | ((value & 0x00FF00FF) <<  8);
		value = ((value & 0xFFFF0000) >>> 16) | ((value & 0x0000FFFF) << 16);
		return value;
	}
	
	public interface MeasurementAggregater<T>
	{
		public T zero();
		public void aggregate( T out , T a );
		public void scale( T out, double scale);
	}
	
	private int log2( int i )
	{
		int v = i;
		int r = 0; // r will be lg(v)

		while (v != 0) // unroll for more speed...
		{
		  r++;
		  v >>= 1;
		};

		return r;
	}
	
	public Vector<Measurement> LoadMeasuredData(float[] data, double scale) {

        Vector<Measurement> measurements = new Vector<Measurement>();
        
        for( int i = 0 ; i < getNumMeasurement() ; i++)
        {
        	Measurement measurement = createMeasurement();
        	
        	measurement.data = new double[getDimensions()];
        	
        	for( int j = 0 ; j < getDimensions(); j++ )
        		measurement.data[j] = data[i*(getDimensions())+j];
        	        	
        	measurements.add( measurement );
        }
        
        return aggregateMeasurements(measurements, scale);
	}
	
	public Vector<Measurement> aggregateMeasurements( Vector<Measurement> measurements, double scale )
	{
		Vector<Measurement> aggMeasurements = new Vector<Measurement>();
		
		int bits = getNumMeasurementBits();
		
		for( int i = 0 ; i < detectors.size() ; i++ )
		{
			FluxDetector d = detectors.get(i);
			
			Measurement agg = createMeasurement();
			for( int j = 0 ; j < d.getMeasurements(); j++ )
			{
				int idx = d.getOffset() + j;
				int shuffleIdx = shuffleMeasurement( idx, bits );
				
				agg.add(measurements.get(shuffleIdx));
			}
			agg.mul(scale);
			
			aggMeasurements.add(agg);
		}
		
		return aggMeasurements;
	}
		
	public int getNumMeasurement()
	{ return numMeasurements; }
	public int getNumMeasurementBits()
	{ return numMeasurementsBits; }
	
	public void buildSetup( int min_measurements, FluxScene scene )
	{
		min_measurements = Math.max(FluxSettings.getOCLMinMeasurements(),min_measurements);
		buildDetectors( min_measurements , scene );
	}
		
	// build a list of detectors, one for each group
	// assign measurements according to measure likelihood with a minimum of 1
	private void buildDetectors( int min_measurements, FluxScene scene ) 
	{
		float sah[] = getGroupSAH(scene);
		
		measureDimensions = sah.length;
		numMeasurements = Math.max( min_measurements, sah.length );
		
		int bits = log2(numMeasurements-1); 
		numMeasurements = (1 << bits);
		numMeasurementsBits = bits;
		
		// compute total sah
		float total = 0;
		for( int i = 0 ; i < sah.length ; i++ )
		{
			total += sah[i];
		}
		
		// assign all measurements
		int idx = 0;
		for( int i = 0 ; i < sah.length ; i++ )
		{
			// number of measurements for this group
			int count = (int) ((sah[i] / total) * (numMeasurements - sah.length)) + 1;
			
			detectors.add( new FluxDetector( idx, count ) );
			idx += count;
		}
	}
	
	// get the relative likelihood for a random ray to hit each group
	private float [] getGroupSAH(FluxScene scene)
	{
		float sah [] = new float[scene.getGroupCount()];
		
		Variables tmp = new Variables();
		BoundingBox3d bb = new BoundingBox3d(); 
		
		// compute the area heuristic for all groups
		
		// iterate over all infinite primitives
		for( int i = 0 ; i < scene.getInfPrimitives().size() ; i++ )
		{
			FluxPrimitive p = scene.getInfPrimitives().get(i);
			
			if( p.getGroupIndex() != -1 )
			{
				// add bounds of entire scene for each infinite object
				sah[p.getGroupIndex() ] += Math.max(EPSILON, scene.getBounds().area());
			}
		}
		
		// iterate over all finite primitives
		for( int i = 0 ; i < scene.getPrimitives().size() ; i++ )
		{
			FluxPrimitive p = scene.getPrimitives().get(i);
			if( p.getGroupIndex() != -1 )
			{
				p.getExtent(bb, tmp);
				sah[p.getGroupIndex() ] += Math.max(EPSILON, bb.area());
			}
		}
		
		// iterate over all sensors
		for( int i = 0 ; i < scene.getSensors().size() ; i++ )
		{
			FluxSensor p = scene.getSensors().get(i);
			if( p.getGroupIndex() != -1 )
			{
				p.getExtent(bb, tmp);
				sah[p.getGroupIndex() ] += Math.max(EPSILON, bb.area());
			}
		}
		
		return sah;
	}

	public Vector<FluxDetector> getDetectors() {
		return detectors;
	}

	public Measurement createMeasurement() {
		return new Measurement(measureDimensions);
	}
	
}
