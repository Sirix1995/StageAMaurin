package de.grogra.rgg;

import de.grogra.gpuflux.scene.experiment.Experiment;
import de.grogra.gpuflux.scene.experiment.Measurement;
import de.grogra.gpuflux.scene.filter.ObjectFilter;
import de.grogra.gpuflux.tracer.FluxLightModelTracer;
import de.grogra.gpuflux.tracer.FluxLightModelTracer.MeasureMode;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.spectral.SpectralCurve;
import de.grogra.persistence.SCOType;
import de.grogra.pf.ui.Workbench;
import de.grogra.ray.physics.Spectrum;

/**
 * <br>
 * The FluxLightModel class provides a spectral light model, used to compute the spectral light distribution in the current graph.
 * The light model is created with one of its constructors.<br> 
 * <br>
 * FluxLightModel lm = new FluxLightModel();<br>
 * <br>
 * The light model computes the light distribution by simulating many photons through the scene. 
 * The total emitted light power is distributed over all photons.
 * The user can specify the maximum number of simulated photons using {@link #setRayCount}.
 * The user can also specify a maximum variance for some given power quantum using {@link #setTargetVariance}. 
 * The simulation will compute the appropriate sample count to achieve the target variance. 
 * Note that this sample count is an overestimation. The light model will select the minimum of both methods as the actual sample count.<br>      
 * <br>
 * Each photon bounces through the scene and any absorbed power is recorded with the objects the photon visits.
 * Besides absorbed power, the simulation also computes sensed irradiance for sensors. 
 * Simulating many sensors can be quite costly and can be disabled using {@link #setEnableSensors}. 
 * The user may specify a maximum path depth to limit the duration of the simulation using {@link #setDepth}.
 * When a simulated photon is almost completely absorbed, further simulation adds little value. 
 * Therefore, the user can specify a cutoff power using {@link #setCutoffPower}; 
 * As soon as the photon power drops below this threshold, the photon is killed off. <br> 
 * <br>
 * The light model supports three different modes of measuring spectral power. 
 * Either it computes regular RGB, a full discretized spectrum or a few weighted integrals over the spectrum.
 * The measure mode is set using {@link #lm.setMeasureMode}. <br>
 * <br>
 * The following code sets the measure mode to RGB.
 * No further settings are required for this mode. RGB is the default measure mode. 
 * The computed measurements are 3 dimensional vectors with channels r,g and b. <br> 
 * <br>
 * lm.setMeasureMode(RGB); <br>
 * <br>
 * The following code sets the measure mode to full discretized spectral measurements.
 * The spectral domain [l,h] is divided into n buckets of equal sized intervals [l + i * (h-l) / n, l + (i + 1) * (h-l) / n]. 
 * The simulation computes the absorbed power in each bucket for each measurement.
 * The dimension of the computed measurements equals the number of buckets. 
 * The user has to specify the spectral domain using {@link #setSpectralDomain} and the discretization resolution using {@link #setSpectralBuckets}.
 * The user may also supply a continuous spectral importance curve. The curve indicates importance and is used for importance sampling over the continuous spectral domain.
 * Regions of high importance will receive more samples and get a more accurate estimate.
 * The spectral importance curve applies to both the discretized measurement mode and the weighted integral mode discussed next. The curve is defined continuously over the whole spectral domain, even though in discrete spectral mode the measured spectra themselves are not continuous.
 * In the following example, the spectral importance curve shows a peak around 500 nm, resulting in improved measuring accuracy around this wavelength.
 * Note that the importance curve does not alter the expected outcome of the simulation, only the distribution of variance over the spectral domain.<br> 
 * <br>
 * lm.setMeasureMode(FULL_SPECTRUM); <br>
 * <br>
 * lm.setSpectralDomain(350,720); <br>
 * lm.setSpectralBuckets(5); <br>
 * <br>
 * SpectralCurve importanceCurve = new IrregularSpectralCurve( {350,400,450,500,550,720} , {2,2,5,8,4,2} ); <br>
 * lm.setSpectralImportanceCurve( importanceCurve ); <br>
 * <br>
 * The following code sets the measurement mode to weighted integration.
 * The simulation computes weighted integrals over the absorbed power for each measurement.
 * The user has to specify the spectral domain using {@link #setSpectralDomain} and the weight functions using {@link #setSpectralWeightCurves}.
 * The user may again specify a spectral importance function. 
 * Note however that in weighted integration mode the light model already uses the weight functions for spectral importance sampling. <br>
 * <br>
 * lm.setMeasureMode(INTEGRATED_SPECTRUM); <br>
 * lm.setSpectralDomain(350,720); <br>
 * <br>
 * SpectralCurve [] weightCurve = { <br> 
 * 	new IrregularSpectralCurve( {350 , 500 , 510 , 520 , 720} , {0,0,1,0,0} ), <br>
 *  new IrregularSpectralCurve( {350 , 600 , 610 , 620 , 720} , {0,0,1,0,0} ) <br>
 * }; <br>
 * <br>
 * lm.setSpectralWeightCurves( weightCurve ); <br>
 * <br>
 * The simulation is executed using {@link #compute}. By default, first the scene is built after which the light distribution is computed. <br>
 * <br>
 * lm.compute(); <br>
 * <br>
 * Rebuilding a large scene can be costly. If the user wishes to simulate the same scene multiple times while only modifying the light sources, the user can instruct the simulation to skip the scene build. <br>
 * <br>
 * // build the scene and compute the distribution <br>
 * lm.compute(true,true); <br>
 * <br>
 * // ... modify light sources here <br>
 * <br>
 * // compute new distribution without rebuilding the scene. <br>
 * lm.compute(true,false); <br>
 * <br>
 * For RGB mode simulations, the simulation results can be obtained for each object trough {@link #getAbsorbedPower3d} and {@link #getSensedIrradiance3d}.
 * The user can also get a handle to the corresponding Experiment objects using {@link #getAbsorbedPower} and {@link #getSensedIrradiance}, which contains a mapping from objects to measurements.  
 * Multiple experiment objects can be aggregated into one experiment. The following code computes the total absorbed power over the course of one day. 
 * All static light sources are simulated only once while the contribution of the sun between 4:00 and 22:00 is integrated using the midpoint rule with time steps of 1 hour.
 * The simulations are aggregated into a single experiment, containing the measured data for one whole day. <br>
 * <br>
 * // ... enable all static lights <br>
 * // ... disable the sun <br>
 * <br>
 * // perform simulation <br> 
 * lm.compute(true,true); <br>
 * Experiment exp = lm.getAbsorbedPower(); <br>
 * <br>
 * exp.mul( 24 ); <br>
 * <br>
 * // ... disable all static lights <br> 
 * // ... enable the sun <br>
 * <br>
 * for( int time = 4 ; time < 22 ; time++ ) <br>
 * { <br>
 * 		// ... move the sun to its position at [time] + 30 minutes. <br>
 *  	<br>
 *  	// perform simulation <br> 
 * 		lm.compute(true,false);<br>
 * 		<br>
 * 		// aggregate experiments <br>
 * 		Experiment sun_exp = lm.getAbsorbedPower(); <br>
 * 		exp.aggregate( sun_exp, 1 ); <br>
 * } <br>
 * <br> 
 * // ... use the final aggregates experiment data in exp <br> 
 * 
 * @author      Dietger van Antwerpen <dietger@xs4all.nl>
 * @version     1.0                                       
 * @since       2011.0824
 */

public class FluxLightModel extends LightModelBase {
	
	//enh:sco SCOType
	
	private FluxLightModelTracer processor; 
	//enh:field
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field processor$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (FluxLightModel representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 1;

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
					((FluxLightModel) o).processor = (FluxLightModelTracer) value;
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
					return ((FluxLightModel) o).processor;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new FluxLightModel ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (FluxLightModel.class);
		processor$FIELD = Type._addManagedField ($TYPE, "processor", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (FluxLightModelTracer.class), null, Type.SUPER_FIELD_COUNT + 0);
		$TYPE.validate ();
	}

//enh:end
	
	/**
	 * Stamp of the graph for which the model has been computed.
	 */
	private transient int stamp;
	
	/**
	 * Creates a default light model with 30.000 rays per computation
	 * and a ray depth of 10.
	 */
	public FluxLightModel ()
	{
		this (30000, 10);
	}

	/**
	 * Creates a light model
	 *  
	 * @param rayCount the number of samples per computation
	 * @param depth the maximum ray depth
	 */
	public FluxLightModel (int sampleCount, int depth)
	{
		this (sampleCount, depth, 0.001);
	}

	/**
	 * Creates a light model
	 *  
	 * @param rayCount the number of samples per computation
	 * @param depth the maximum ray depth
	 * @param cutoffPower the maximum neglectable power quantum
	 */
	public FluxLightModel (int sampleCount, int depth, double cutoffPower)
	{
		this (sampleCount, depth, cutoffPower, true);
	}
	
	/**
	 * Creates a light model
	 *  
	 * @param rayCount the number of samples per computation
	 * @param depth the maximum ray depth
	 * @param cutoffPower the maximum neglectable power quantum
	 * @param enableSensors sensors are simulated if true
	 */
	public FluxLightModel (int sampleCount, int depth, double cutoffPower, boolean enableSensors)
	{
		processor = new FluxLightModelTracer(sampleCount, depth, cutoffPower, enableSensors);
	}
		
	/**
	 * @return returns true if sensors are enabled
	 */
	public boolean isSensorsEnabled ()
	{
		return processor.isEnableSensors();
	}

	/**
	 * @return returns the maximum rays depth per sample
	 */
	public int getDepth ()
	{
		return processor.getDepth();
	}

	/**
	 * @return returns the random seed
	 */
	public int getRandomSeed ()
	{
		return processor.getRandomseed();
	}
	
	/**
	 * sets the random seed
	 */
	public void setRandomSeed (int value)
	{
		processor.setRandomseed(value);
	}
	
	/**
	 * sets the random seed
	 */
	public void setSeed (int value)
	{
		processor.setRandomseed(value);
	}
	
	/**
	 * @return returns the maximum sample count per compute
	 */
	public int getRayCount ()
	{
		return processor.getRayCount();
	}

	/**
	 * @return returns the lower boundary of the simulated spectrum domain
	 */
	public int getMinLambda ()
	{
		return processor.getMinLambda();
	}
	
	/**
	 * @return returns the upper boundary of the simulated spectrum domain
	 */
	public int getMaxLambda ()
	{
		return processor.getMaxLambda();
	}

	/**
	 * @return returns the spectrum discretization resolution in buckets 
	 */
	public int getSpectralBuckets ()
	{
		return processor.getSpectralBuckets();
	}

	/**
	 * @return returns the maximum neglectable power quantum 
	 */
	public double getCutoffPower ()
	{
		return processor.getSpectralBuckets();
	}

	/**
	 * @return returns the current measure mode 
	 */
	public MeasureMode getMeasureMode ()
	{
		return processor.getMeasureMode();
	}

	/**
	 * @return returns the spectral weight functions  
	 */
	public SpectralCurve[] getSpectralWeightCurve ()
	{
		return processor.getSensitivityCurves();
	}

	/**
	 * @return returns the spectral importance function  
	 */
	public SpectralCurve getSpectralImportanceCurve ()
	{
		return processor.getImportanceCurve();
	}
	
	/**
	 * Enables the simulation of sensor
	 * 
	 * @param enableSensors true enables the sensors  
	 */
	public void setEnableSensors(boolean enableSensors) {
		processor.setEnableSensors(enableSensors);
	}

	/**
	 * Sets the maximum neglectable power quantum 
	 * 
	 * @param minPower maximum neglectable power quantum  
	 */
	public void setCutoffPower (double minPower)
	{
		processor.setCutoffPower(minPower);
	}

	/**
	 * Sets the maximum ray depth per sample 
	 * 
	 * @param depth maximum ray depth per sample 
	 */
	public void setDepth (int depth)
	{
		processor.setDepth(depth);
	}

	/**
	 * Sets the random seed for the random number generator
	 * 
	 * @param seed
	 */
	public void setRandomseed (int seed)
	{
		processor.setRandomseed(seed);
	}

	/**
	 * Sets the maximum sample count per simulation 
	 * 
	 * @param sampleCount maximum sample count
	 */
	public void setRayCount (int sampleCount)
	{
		processor.setRayCount(sampleCount);
	}

	/**
	 * Sets the spectral weight functions  
	 * 
	 * @param weightCurves array of spectral weight functions  
	 */
	public void setSpectralWeightCurves( SpectralCurve [] weightCurves )
	{
		processor.setSensitivityCurves(weightCurves);
	}
	
	/**
	 * Sets the spectral importance function, used for spectral importance sampling.
	 * The spectral importance function indicates regions of interest within the spectral domain.
	 * Setting an importance function does not alter the expected outcome, but may influence the distribution of variance in the spectral domain.  
	 * 
	 * @param importanceCurve spectral importance functions  
	 */
	public void setSpectralImportanceCurve( SpectralCurve importanceCurve )
	{
		processor.setImportanceCurve(importanceCurve);
	}
	
	/**
	 * Sets the spectrum discretization resolution in buckets.
	 * Measured discretized spectral distributions are represented by buckets, dividing the spectral domain in equal sized intervals
	 * 
	 * @param spectralBuckets bucket resolution  
	 */
	public void setSpectralBuckets( int spectralBuckets )
	{
		processor.setSpectralBuckets(spectralBuckets);
	}
	
	/**
	 * Sets the current measurement mode.
	 * There are three possible measurement modi.
	 * 	  	FULL_SPECTRUM: Measure the full spectral range, specified using {@link #setSpectralDomain}. The spectral resolution is specified using {@linkg #setSpectralBuckets}
	 * 		INTEGRATED_SPECTRUM: Measure weighted spectral integrals, specified using {@link #setSpectralWeightCurves}
	 * 		RGB: Measure RGB values  
	 * Spectral dispersion only applies to FULL_SPECTRUM and INTEGRATED_SPECTRUM and may be enabled using {@link #setDispersion}
	 * 
	 * @param measureMode measurement mode 
	 */
	public void setMeasureMode( MeasureMode measureMode )
	{
		processor.setMeasureMode(measureMode);
	}
	
	/**
	 * Sets the simulated spectral domain. The domain is assumed to be non-empty
	 * 
	 * @param minLambda lower boundary wavelength
	 * @param maxLambda upper boundary wavelength 
	 */
	public void setSpectralDomain( int minLambda, int maxLambda )
	{		
		processor.setSpectralRange(minLambda, maxLambda);
	}
	
	/**
	 * Sets the required maximum target variance for some minimum measurable power quantum.
	 * When calling {@link #compute}, the simulation computes the minimum number of samples required 
	 * to achieve the maximum target variance #targetVariance on any measurement of at least #minPower for the given scene. 
	 * 
	 * @param minPower minimal measurable power quantum
	 * @param targetVariance maximum target variance  
	 */
	public void setTargetVariance(double minPower, double targetVariance) {
		processor.setTargetVariance(minPower, targetVariance);
	}
	
	/**
	 * Enables dispersion
	 * 
	 * @param dispersion true enables dispersion
	 */
	public void setDispersion(boolean dispersion) {
		processor.setDispersion(dispersion);
	}
	
	/**
	 * Sets the tessellation flatness for polygonizable objects 
	 * 
	 * @param flatness positive flatness parameter with 0 being infinite tessellation. 
	 */
	public void setFlatness( float flatness ) {
		processor.setFlatness(flatness);
	}
	
	/**
	 * Sets the object filter for filtering measurable objects.
	 * All objects will participate in the simulation but the absorbed power is only computed for the filtered objects 
	 * 
	 * @param measureObjectFilter Object filter 
	 */
	public void setMeasureObjectFilter(ObjectFilter measureObjectFilter) {
		processor.setMeasureObjectFilter(measureObjectFilter);
	}
	
	/**
	 * Rebuild the scene 
	 */
	public void build ()
	{
		processor.build();	
	}
	
	/**
	 * Compute the light distribution 
	 */
	@Override
	public void compute ()
	{
		compute(true, true);	
	}
	
	/**
	 * Compute the light distribution
	 * 
	 *  @param forceCompute if true forces recomputation of the light distribution
	 */
	@Override
	public void compute (boolean forceCompute)
	{
		compute(forceCompute, true);	
	}
	
	/**
	 (Re-)computes the light distribution in the current graph.
	 * 
	 *  @param forceCompute if true forces recomputation of the light distribution
	 *  @param forceBuild if true forces rebuilding of the scene
	 */
	public void compute (boolean forceCompute, boolean forceBuild )
	{
		Workbench w = Workbench.current ();
		if (w == null)
		{
			return;
		}
		GraphManager g = w.getRegistry ().getProjectGraph ();
		if (forceCompute || (stamp != g.getStamp ()))
		{
			processor.compute(forceCompute, forceBuild);
			
			stamp = g.getStamp ();
		}
	}

	/**
	 * (Re-)computes the light distribution in the current graph. 
	 * @param spectrumFactory factrory for spectrum objects, not used in Flux light model
	 */
	@Override
	public void compute(Spectrum spectrumFactory) {
		processor.compute();
	}

	/**
	 * (Re-)computes the light distribution in the current graph.
	 * @param spectrumFactory factrory for spectrum objects, not used in Flux light model
	 * @param force if true forces recomputation of the light distribution
	 */
	@Override
	public void compute(Spectrum spectrumFactory, boolean force) {
		processor.compute();
	}

	@Override
	public Spectrum getAbsorbedPower(Node node) {
		return processor.getAbsorbedPower(node);
	}

	@Override
	public Spectrum getSensedIrradiance(Node node) {
		return processor.getSensedIrradiance(node);
	}
	
	/**
	 * Returns the experiment data on sensed irradiance, computed during the last call to {@link #compute}
	 * @return sensed irradiance data 
	 */
	public Experiment getSensedIrradiance ()
	{
		return processor.getSensedIrradiance();
	}
	
	/**
	 * Returns the experiment data on absorbed power, computed during the last call to {@link #compute}
	 * @return absorbed power data
	 */
	public Experiment getAbsorbedPower ()
	{
		return processor.getAbsorbedPower();
	}
	
	/**
	 * Returns the experiment data on reflected power, computed during the last call to {@link #compute}
	 * @return absorbed power data
	 */
	public Experiment getReflectedPower ()
	{
		return processor.getReflectedPower();
	}
	
	/**
	 * Returns the experiment data on transmitted power, computed during the last call to {@link #compute}
	 * @return absorbed power data
	 */
	public Experiment getTransmittedPower ()
	{
		return processor.getTransmittedPower();
	}
	
	/**
	 * Returns the experiment data on sensed power, computed during the last call to {@link #compute}
	 * @return absorbed power data
	 */
	public Experiment getSensedPower ()
	{
		return processor.getSensedPower();
	}
	
	/**
	 * Returns the irradiance sensed by a node during the last call to {@link #compute}
	 *  
	 * @param node node for which the absorbed power is returned
	 * @return sensed irradiance 
	 */
	public Measurement getSensedIrradianceMeasurement (Node node)
	{
		return processor.getSensedIrradianceMeasurement(node);
	}
	
	/**
	 * Returns the power absorbed by a node during the last call to {@link #compute}
	 *  
	 * @param node node for which the absorbed power is returned
	 * @return absorbed power 
	 */
	public Measurement getAbsorbedPowerMeasurement (Node node)
	{
		return processor.getAbsorbedPowerMeasurement(node);
	}
	
	/**
	 * Returns the power reflected by a node during the last call to {@link #compute}
	 *  
	 * @param node node for which the reflected power is returned
	 * @return reflected power 
	 */
	public Measurement getReflectedPowerMeasurement (Node node)
	{
		return processor.getReflectedPowerMeasurement(node);
	}
	
	/**
	 * Returns the power transmitted by a node during the last call to {@link #compute}
	 *  
	 * @param node node for which the transmitted power is returned
	 * @return transmitted power 
	 */
	public Measurement getTransmittedPowerMeasurement (Node node)
	{
		return processor.getTransmittedPowerMeasurement(node);
	}
	
	/**
	 * Returns the power sensed by a node during the last call to {@link #compute}
	 *  
	 * @param node node for which the sensed power is returned
	 * @return sensed power 
	 */
	public Measurement getSensedPowerMeasurement (Node node)
	{
		return processor.getSensedPowerMeasurement(node);
	}

	@Override
	public void setLayerVisible(int layer, boolean visible) {
		processor.setLayerVisible(layer, visible);
		
	}
}
