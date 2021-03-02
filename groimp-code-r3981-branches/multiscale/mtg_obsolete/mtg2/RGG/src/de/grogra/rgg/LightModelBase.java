package de.grogra.rgg;

import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.NType;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3d;

public abstract class LightModelBase extends ShareableBase {
		
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;


	static
	{
		$TYPE = new NType (LightModelBase.class);
		$TYPE.validate ();
	}

//enh:end
	
	/**
	 * (Re-)computes the light distribution in the current graph. 

	 */
	public abstract void compute ();
	
	/**
	 * (Re-)computes the light distribution in the current graph. 
	 * @param spectrumFactory factrory for spectrum objects
	 */
	public abstract void compute (Spectrum spectrumFactory);
	
	/**
	 * (Re-)computes the light distribution in the current graph. 
	 * @param force if true forces recomputation of the light distribution
	 */
	public abstract void compute (boolean force);
	
	/**
	 * (Re-)computes the light distribution in the current graph. This method
	 * has to be invoked at first in order for {@link #getRadiantPowerFor}, {@link #getAbsorbedPower} and {@link #getSensedIrradiance}
	 * to return correct values.
	 * @param spectrumFactory factrory for spectrum objects
	 * @param force if true forces recomputation of the light distribution
	 */
	public abstract void compute (Spectrum spectrumFactory, boolean force);
		
	
	/**
	 * Deprecated, use getAbsorbedPower(node) instead.
	 * @param node
	 * @return
	 */
	@Deprecated
	public Spectrum getRadiantPowerFor (Node node)
	{
		return getAbsorbedPower (node);
	}

	/**
	 * Deprecated, use getAbsorbedPower3d(node) instead.
	 * @param node
	 * @return
	 */
	@Deprecated
	public Spectrum3d getRadiantPower3dFor (Node node)
	{
		return getAbsorbedPower3d (node);
	}
	
	/**
	 * Returns the radiant power in Watts which is absorbed by the surface
	 * of the volume of the given <code>node</code>. If the <code>node</code>
	 * does not define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the absorbed radiant power of the node
	 */
	public abstract Spectrum getAbsorbedPower (Node node);
	
	
	/**
	 * Returns the radiant power in Watts which is absorbed by the surface
	 * of the volume of the given <code>node</code>, represented as a
	 * <code>Spectrum3d</code>. If the <code>node</code>
	 * does not define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the absorbed radiant power of the node
	 */
	public Spectrum3d getAbsorbedPower3d (Node node)
	{
		Spectrum s = getAbsorbedPower (node);
		if (s instanceof Spectrum3d)
		{
			return (Spectrum3d) s;
		}
		Spectrum3d t = new Spectrum3d ();
		s.get (t);
		return t;
	}
	
	/**
	 * Returns the irradiance in Watts per square meter which is sensed by the
	 * sensor attached to the volume of the given <code>node</code>.
	 * If the <code>node</code> does not define a volume with a sensor,
	 * the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the sensed irradiance of the node
	 */
	public abstract Spectrum getSensedIrradiance (Node node);
	
	public Spectrum3d getSensedIrradiance3d (Node node)
	{
		Spectrum s = getSensedIrradiance (node);
		if (s instanceof Spectrum3d)
		{
			return (Spectrum3d) s;
		}
		Spectrum3d t = new Spectrum3d ();
		s.get (t);
		return t;
	}
	
	/**
	 * sets the visibility of a layer
	 * */
	public abstract void setLayerVisible (int layer, boolean visible);
	
}
