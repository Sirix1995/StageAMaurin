package de.grogra.gpuflux.scene.experiment;

import java.util.Vector;

import de.grogra.graph.impl.Node;
import de.grogra.xl.util.LongToIntHashMap;

/**
 * @author      Dietger van Antwerpen <dietger@xs4all.nl>
 * @version     1.0                                       
 * @since       2011.0824                                 
 *
 * The Experiment class gives a mapping from objects to measurements. 
 * An experiment represents a set of measurements for a collection of object groups.
 * The measurement dimensions and units are undefined and depend on the context.
 * All measurements are assumed to have the same dimensions and units.
 */

public class Experiment {

	private transient LongToIntHashMap nodeToGroup;
	private transient Vector<? extends Measurement> measurements;
	private transient Measurement zero = new Measurement();
	
	/**
	 * Constructor
	 * 
	 * @param nodeToGroup node to group mapping
	 * @param measurements vector of measurements for each group
	 * @param zero default zero measurement
	 */
	public Experiment(LongToIntHashMap nodeToGroup , Vector<? extends Measurement> measurements, Measurement zero )
	{
		this.nodeToGroup = nodeToGroup;
		this.measurements = measurements;
		this.zero = zero;
	}
	
	/**
	 * Maps an input node to a measurement
	 * 
	 * @param node input node
	 * @return returns the measurement corresponding to the input node 
	 */
	public Measurement getMeasurement (Node node)
	{
		int volumeId = checkGroupId (node);
		return (volumeId >= 0) ? measurements.get(volumeId) : (Measurement)zero.clone();
	}
	
	/**
	 * Returns a zero measurement corresponding to this experiment 
	 * 
	 * @return returns the zero measurement 
	 */
	public Measurement getZero()
	{
		return (Measurement) zero.clone();
	}
	
	/**
	 * Maps an input node to a group index in the measurement vector
	 * 
	 * @param node
	 * @return returns the corresponding group index, or -1 if not found
	 */
	public int checkGroupId (Node node)
	{
		if ((nodeToGroup == null) )
		{
			return -1;
		}
		int groupId = nodeToGroup.get (node.getId (), -1);
		return groupId;
	}
	
	/**
	 * Scales all measurements in the experiment by a single scale factor
	 * 
	 * @param scale scale factor
	 */
	public void mul(float scale)
	{
		for(Measurement m: measurements)
			m.mul( scale );
	}
	
	/**
	 * Aggregate two experiments by scaling the input experiment and adding it to this experiment.
	 * All corresponding measurements in both experiments are aggregated pairwise.
	 * The experiments are assumed to apply to the same node to group mapping.
	 * 
	 * @param map
	 * @param scale
	 */
	public void aggregate(Experiment map, float scale)
	{
		if( map.nodeToGroup != this.nodeToGroup )
			throw new IllegalArgumentException( "Node to group mappings don't match" );
			
		for( int index = 0 ; index < measurements.size() ; index++ )
			measurements.elementAt(index).mad( map.measurements.elementAt(index), scale );
	}
	
}
