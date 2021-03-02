package de.grogra.gpuflux.tracer;

import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_READ_WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.AbstractList;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import org.jocl.CL;
import org.jocl.Sizeof;
import org.jocl.cl_event;

import de.grogra.gpuflux.FluxSettings;
import de.grogra.gpuflux.GPUFluxInit;
import de.grogra.gpuflux.jocl.JOCLBuffer;
import de.grogra.gpuflux.jocl.JOCLEvent;
import de.grogra.gpuflux.jocl.compute.Buffer;
import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.jocl.compute.ComputeContext;
import de.grogra.gpuflux.jocl.compute.Device;
import de.grogra.gpuflux.jocl.compute.Kernel;
import de.grogra.gpuflux.jocl.compute.SharedBuffer;
import de.grogra.gpuflux.scene.FluxScene;
import de.grogra.gpuflux.scene.experiment.Experiment;
import de.grogra.gpuflux.scene.experiment.Measurement;
import de.grogra.gpuflux.scene.experiment.MeasuringSetup;
import de.grogra.gpuflux.scene.filter.AllFilter;
import de.grogra.gpuflux.scene.filter.ObjectFilter;
import de.grogra.gpuflux.scene.light.FluxLight;
import de.grogra.gpuflux.scene.shading.FluxSpectrum;
import de.grogra.gpuflux.scene.shading.FluxSpectrum.SpectralDiscretization;
import de.grogra.graph.Attributes;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.spectral.ConstantSpectralCurve;
import de.grogra.imp3d.spectral.SpectralCurve;
import de.grogra.persistence.ManageableType;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.pf.ui.Workbench;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum3d;

/**
 * @author      Dietger van Antwerpen <dietger@xs4all.nl>
 * @version     1.5                                       
 * @since       2011.0722                                 
 *
 * FluxLightModel is a light transport model. For a given list of light sources, it performs experiments on a scene in order to compute the total absorbed power and sensed irradiance by objects and sensors in a scene.
 */

public class FluxLightModelTracer extends ShareableBase implements ProgressMonitor 
{
	private transient double preferredDuration;
	private transient int maximumSampleCount;
	private transient ComputeContext computeContext;
	private transient Kernel kernel;
	private transient Experiment irradiance;
	private transient Experiment power;
	private transient boolean justBuildLights = false;
	
	private transient ConcurrentLinkedQueue<DeviceMonitor> openDeviceList;
	private transient Semaphore available;
	private volatile transient boolean finnished;
	private transient de.grogra.gpuflux.scene.FluxJOCLScene joclScene;
	private transient FluxScene scene;
	private transient boolean useBih;
	private transient de.grogra.gpuflux.scene.FluxSceneSerializer serializer;
	private transient MeasuringSetup measuringSetup;
		
	public enum MeasureMode {
	    FULL_SPECTRUM, INTEGRATED_SPECTRUM, RGB 
	};
	
//enh:sco SCOType
	
	private ObjectFilter measureObjectFilter = null;
	//enh:field getter
	private boolean[] visibleLayers;
	//enh:field getter
	private float flatness = 100;
	//enh:field getter
	private double minPower;
	//enh:field getter
	private double targetVariance;
	//enh:field getter
	private double cutoffPower;
	//enh:field getter
	private int depth;
	//enh:field getter
	private int rayCount;
	//enh:field getter
	private boolean enableSensors = true;
	//enh:field getter
	private MeasureMode measureMode = MeasureMode.RGB;
	//enh:field getter
	private int minLambda = 380, maxLambda = 720;
	//enh:field getter
	private int spectralBuckets = 10;
	//enh:field getter
	private SpectralCurve importanceCurve = null;
	//enh:field getter
	private SpectralCurve[] sensitivityCurves = null;
	//enh:field getter
	private boolean dispersion = false;
	//enh:field getter
	
//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field measureObjectFilter$FIELD;
	public static final Type.Field visibleLayers$FIELD;
	public static final Type.Field flatness$FIELD;
	public static final Type.Field minPower$FIELD;
	public static final Type.Field targetVariance$FIELD;
	public static final Type.Field cutoffPower$FIELD;
	public static final Type.Field depth$FIELD;
	public static final Type.Field rayCount$FIELD;
	public static final Type.Field enableSensors$FIELD;
	public static final Type.Field measureMode$FIELD;
	public static final Type.Field minLambda$FIELD;
	public static final Type.Field spectralBuckets$FIELD;
	public static final Type.Field importanceCurve$FIELD;
	public static final Type.Field sensitivityCurves$FIELD;
	public static final Type.Field dispersion$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (FluxLightModelTracer representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 15;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setBoolean (Object o, int id, boolean value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 8:
					((FluxLightModelTracer) o).enableSensors = (boolean) value;
					return;
				case Type.SUPER_FIELD_COUNT + 14:
					((FluxLightModelTracer) o).dispersion = (boolean) value;
					return;
			}
			super.setBoolean (o, id, value);
		}

		@Override
		protected boolean getBoolean (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 8:
					return ((FluxLightModelTracer) o).isEnableSensors ();
				case Type.SUPER_FIELD_COUNT + 14:
					return ((FluxLightModelTracer) o).isDispersion ();
			}
			return super.getBoolean (o, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 6:
					((FluxLightModelTracer) o).depth = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 7:
					((FluxLightModelTracer) o).rayCount = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 10:
					((FluxLightModelTracer) o).minLambda = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 11:
					((FluxLightModelTracer) o).spectralBuckets = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 6:
					return ((FluxLightModelTracer) o).getDepth ();
				case Type.SUPER_FIELD_COUNT + 7:
					return ((FluxLightModelTracer) o).getRayCount ();
				case Type.SUPER_FIELD_COUNT + 10:
					return ((FluxLightModelTracer) o).getMinLambda ();
				case Type.SUPER_FIELD_COUNT + 11:
					return ((FluxLightModelTracer) o).getSpectralBuckets ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					((FluxLightModelTracer) o).flatness = (float) value;
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((FluxLightModelTracer) o).getFlatness ();
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 3:
					((FluxLightModelTracer) o).minPower = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((FluxLightModelTracer) o).targetVariance = (double) value;
					return;
				case Type.SUPER_FIELD_COUNT + 5:
					((FluxLightModelTracer) o).cutoffPower = (double) value;
					return;
			}
			super.setDouble (o, id, value);
		}

		@Override
		protected double getDouble (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 3:
					return ((FluxLightModelTracer) o).getMinPower ();
				case Type.SUPER_FIELD_COUNT + 4:
					return ((FluxLightModelTracer) o).getTargetVariance ();
				case Type.SUPER_FIELD_COUNT + 5:
					return ((FluxLightModelTracer) o).getCutoffPower ();
			}
			return super.getDouble (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((FluxLightModelTracer) o).measureObjectFilter = (ObjectFilter) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((FluxLightModelTracer) o).visibleLayers = (boolean[]) value;
					return;
				case Type.SUPER_FIELD_COUNT + 9:
					((FluxLightModelTracer) o).measureMode = (MeasureMode) value;
					return;
				case Type.SUPER_FIELD_COUNT + 12:
					((FluxLightModelTracer) o).importanceCurve = (SpectralCurve) value;
					return;
				case Type.SUPER_FIELD_COUNT + 13:
					((FluxLightModelTracer) o).sensitivityCurves = (SpectralCurve[]) value;
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
					return ((FluxLightModelTracer) o).getMeasureObjectFilter ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((FluxLightModelTracer) o).getVisibleLayers ();
				case Type.SUPER_FIELD_COUNT + 9:
					return ((FluxLightModelTracer) o).getMeasureMode ();
				case Type.SUPER_FIELD_COUNT + 12:
					return ((FluxLightModelTracer) o).getImportanceCurve ();
				case Type.SUPER_FIELD_COUNT + 13:
					return ((FluxLightModelTracer) o).getSensitivityCurves ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new FluxLightModelTracer ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (FluxLightModelTracer.class);
		measureObjectFilter$FIELD = Type._addManagedField ($TYPE, "measureObjectFilter", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (ObjectFilter.class), null, Type.SUPER_FIELD_COUNT + 0);
		visibleLayers$FIELD = Type._addManagedField ($TYPE, "visibleLayers", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (boolean[].class), null, Type.SUPER_FIELD_COUNT + 1);
		flatness$FIELD = Type._addManagedField ($TYPE, "flatness", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 2);
		minPower$FIELD = Type._addManagedField ($TYPE, "minPower", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 3);
		targetVariance$FIELD = Type._addManagedField ($TYPE, "targetVariance", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 4);
		cutoffPower$FIELD = Type._addManagedField ($TYPE, "cutoffPower", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 5);
		depth$FIELD = Type._addManagedField ($TYPE, "depth", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 6);
		rayCount$FIELD = Type._addManagedField ($TYPE, "rayCount", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 7);
		enableSensors$FIELD = Type._addManagedField ($TYPE, "enableSensors", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 8);
		measureMode$FIELD = Type._addManagedField ($TYPE, "measureMode", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (MeasureMode.class), null, Type.SUPER_FIELD_COUNT + 9);
		minLambda$FIELD = Type._addManagedField ($TYPE, "minLambda", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 10);
		spectralBuckets$FIELD = Type._addManagedField ($TYPE, "spectralBuckets", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 11);
		importanceCurve$FIELD = Type._addManagedField ($TYPE, "importanceCurve", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SpectralCurve.class), null, Type.SUPER_FIELD_COUNT + 12);
		sensitivityCurves$FIELD = Type._addManagedField ($TYPE, "sensitivityCurves", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (SpectralCurve[].class), null, Type.SUPER_FIELD_COUNT + 13);
		dispersion$FIELD = Type._addManagedField ($TYPE, "dispersion", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, Type.SUPER_FIELD_COUNT + 14);
		$TYPE.validate ();
	}

	public boolean isEnableSensors ()
	{
		return enableSensors;
	}

	public boolean isDispersion ()
	{
		return dispersion;
	}

	public int getDepth ()
	{
		return depth;
	}

	public int getRayCount ()
	{
		return rayCount;
	}

	public int getMinLambda ()
	{
		return minLambda;
	}

	public int getSpectralBuckets ()
	{
		return spectralBuckets;
	}

	public float getFlatness ()
	{
		return flatness;
	}

	public double getMinPower ()
	{
		return minPower;
	}

	public double getTargetVariance ()
	{
		return targetVariance;
	}

	public double getCutoffPower ()
	{
		return cutoffPower;
	}

	public ObjectFilter getMeasureObjectFilter ()
	{
		return measureObjectFilter;
	}

	public boolean[] getVisibleLayers ()
	{
		return visibleLayers;
	}

	public MeasureMode getMeasureMode ()
	{
		return measureMode;
	}

	public SpectralCurve getImportanceCurve ()
	{
		return importanceCurve;
	}

	public SpectralCurve[] getSensitivityCurves ()
	{
		return sensitivityCurves;
	}

//enh:end
	
	private int getMeasureDimensions()
	{
		switch( measureMode )
		{
		case RGB:
			return 3;
		case FULL_SPECTRUM:
			return spectralBuckets;
		case INTEGRATED_SPECTRUM:
			return sensitivityCurves.length;
		}
		return 0;
	}
	
	/**
	 * Creates a default light model with 30.000 rays per computation
	 * and a ray depth of 10.
	 */
	public FluxLightModelTracer ()
	{
		this (30000, 10, 0.001, true);
	}
	
	/**
	 * Creates a light model
	 *  
	 * @param rayCount the number of samples per computation
	 * @param depth the maximum ray depth
	 * @param cutoffPower the maximum neglectable power quantum
	 * @param enableSensors sensors are simulated if true
	 */
	public FluxLightModelTracer (int rayCount, int depth, double cutoffPower, boolean enableSensors)
	{
		setEnableSensors (enableSensors);
		setRayCount (rayCount);
		setDepth (depth);
		setCutoffPower (cutoffPower);
		measureObjectFilter = new AllFilter();
		
		visibleLayers = new boolean[Attributes.LAYER_COUNT];
		for (int i = 0; i < Attributes.LAYER_COUNT; i++)
		{
			visibleLayers[i] = true;
		}
	}

	private void setupJOCL()
	{
		if( computeContext == null )
		{
			Workbench w = Workbench.current ();
			
			w.beginStatus (this);
			
			setProgress ("Init OpenCL", ProgressMonitor.INDETERMINATE_PROGRESS);
			
			computeContext = GPUFluxInit.initComputeContext(true, new String[] {"CL_KHR_GLOBAL_INT32_BASE_ATOMICS"});
			
			setProgress ("", ProgressMonitor.DONE_PROGRESS);
		}
	}
	
	private void loadKernel( String options )
	{

		Workbench w = Workbench.current ();
		
		w.beginStatus (this);
		
		setProgress ("Load kernel", ProgressMonitor.INDETERMINATE_PROGRESS);
		
		// load kernel
		try {
			
			boolean spectral = false;
			switch( measureMode )
			{
			case RGB:
				break;
			case FULL_SPECTRUM:
				spectral = true;
				options += " -D MEASURE_FULL_SPECTRUM";
				options += " -D MEASURE_MIN_LAMBDA=" + minLambda;
				options += " -D MEASURE_MAX_LAMBDA=" + maxLambda;
				options += " -D MEASURE_SPECTRUM_BINS=" + spectralBuckets;
				break;
			case INTEGRATED_SPECTRUM:
				spectral = true;
				options += " -D NUM_SENSITIVITYSPDS=" + sensitivityCurves.length;
				break;
			}
			
			if( spectral )
			{
				options += " -D SPECTRAL";
				if( dispersion )
					options += " -D SPECTRUM_DISPERSION";
			}
			
			if( enableSensors )
			{
				options += " -D ENABLE_SENSORS";
			}
			
			SpectralDiscretization discrete = FluxSpectrum.getDiscretization();
			
			options += " -D SPECTRAL_WAVELENGTH_MIN=" + discrete.getLambdaMin();
			options += " -D SPECTRAL_WAVELENGTH_MAX=" + discrete.getLambdaMax();
			options += " -D SPECTRAL_WAVELENGTH_BINS=" + discrete.getLambdaBins();
						
			// load kernel
			kernel = computeContext.createKernel("kernel/lightmodel_kernel.cl", "compute", options);
						
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		setProgress ("", ProgressMonitor.DONE_PROGRESS);
	}
	
	public void setEnableSensors(boolean enableSensors) {
		this.enableSensors = enableSensors;
	}

	public void setCutoffPower (double minPower)
	{
		if (!(minPower > 0))
			throw new IllegalArgumentException (
				"cutoffPower must be a positive double but was " + minPower);

		this.cutoffPower = minPower;
	}

	public void setDepth (int depth)
	{
		// check if depth is invalid, if so throw an exception
		if (depth <= 0)
			throw new IllegalArgumentException (
				"depth must be a positive integer but was " + depth);

		this.depth = depth;
	}

	public void setRayCount (int rayCount)
	{
		// check if rayCount is invalid, if so throw an exception
		if (rayCount <= 0)
			throw new IllegalArgumentException (
				"rayCount must be a positive integer but was " + rayCount);

		this.rayCount = rayCount;
	}

	public void setSensitivityCurves( SpectralCurve [] sensitivityCurves )
	{
		this.sensitivityCurves = sensitivityCurves;
	}
	
	public void setImportanceCurve( SpectralCurve importanceCurve )
	{
		this.importanceCurve = importanceCurve;
	}
	
	public void setSpectralBuckets( int spectralBuckets )
	{
		if( spectralBuckets <= 0 )
			throw new IllegalArgumentException (
				"Number of spectral buckets must be strictly positive");
		this.spectralBuckets = spectralBuckets;
	}
	
	public void setMeasureMode( MeasureMode measureMode )
	{
		this.measureMode = measureMode;
	}
	
	public void setSpectralRange( int minLambda, int maxLambda )
	{
		if( minLambda >= maxLambda )
			throw new IllegalArgumentException (
				"range must be non-empty: (" + minLambda + "," + maxLambda + ")");
		
		this.minLambda = minLambda;
		this.maxLambda = maxLambda;
	}
	
	private SharedBuffer serializeSensitivityCurves()
	{
		ComputeByteBuffer buffer = computeContext.createByteBuffer();
		
		try {
			if( sensitivityCurves != null )
			{
				for(SpectralCurve curve : sensitivityCurves )
				{
					FluxSpectrum.serialize(buffer,curve);
				}
			}
		
			buffer.writeInt(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// create light power buffer
		SharedBuffer curveBuffer = computeContext.createSharedBuffer( buffer, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR );
		
		return curveBuffer;
	}

	class DeviceMonitor extends Thread
	{
		Semaphore resume = new Semaphore(0);
		String log = "";
		Buffer powerBuffer, irradianceBuffer;
		cl_event event = new cl_event();
		int smlprun;
		long totalsml;
		Device device;
		double passed_time;
		double samplesPerSecond;
		double total_time;
			
		public void run()
		{
			//System.out.println( "Start thread" );
			while( true )
			{
				try {
					resume.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if( FluxLightModelTracer.this.finnished )
					break;
				
				// wait for device execution event
				CL.clWaitForEvents(1, new cl_event[]{event});
				
				// compute performance
				passed_time = JOCLEvent.getEndTime(event) - JOCLEvent.getStartTime(event);
				total_time += passed_time;
				samplesPerSecond = smlprun / passed_time;
				
				if( FluxTracer.BATCH_LOGGING_ENABLED )
				{
					log += "<i>";
					log += "Sample batch\n";
					log += "    Device:      " + device.getName() + "\n";
					log += "    Batch size: " + smlprun + "\n" ;
					log += "    Render time: " + (passed_time * 1000) + "ms\n";
					log += "    Performance: " + (samplesPerSecond / (1000*1000)) + " MSmpl\n";
					log += "\n</i>";
				}

				// balance work
				totalsml += smlprun;
				int newSmlPerRun = Math.min( FluxLightModelTracer.this.maximumSampleCount , (int) (samplesPerSecond * FluxLightModelTracer.this.preferredDuration) );
				smlprun = (int) (FluxTracer.BATCH_BALANCE_SMOOTH * newSmlPerRun + (1.0-FluxTracer.BATCH_BALANCE_SMOOTH) * smlprun);
				
				// add back to open list
				openDeviceList.add(this);
				
				// release the device 
				available.release();
			}
			//System.out.println( "Stop thread" );
		}
	};
	
	/**
	 * (Re-)builds the light sources from the current graph. These lightsources is than used in {@link #conpute}
	 */
	private void buildlights() {
		Workbench w = Workbench.current ();
		if (w == null)
		{
			return;
		}
		
		GraphManager g = w.getRegistry ().getProjectGraph ();

		Graph graph = GraphState.current (g).getGraph();
	
		// build scene from scene graph
		scene.buildLightsFromGraph( graph , null, measureObjectFilter, enableSensors, this, false );
		
		justBuildLights = true;
	}

	
	/**
	 * (Re-)builds a scene from the current graph. This scene is than used in {@link #conpute}
	 */
	public void build ()
	{
		Workbench w = Workbench.current ();
		if (w == null)
		{
			return;
		}
				
		StringBuffer stats = new StringBuffer("<html><pre>");
		
		stats.append( "<B>GPUFlux Light Model</B>\n\n" );
		
		w.beginStatus (this);
		
		// set spectral discretization
		int spectralLambdaStep = FluxSettings.getModelSpectralLambdaStep();
		FluxSpectrum.setDiscretization( new FluxSpectrum.SpectralDiscretization(minLambda, maxLambda, spectralLambdaStep));
		
		useBih = false;
		
		if( importanceCurve == null )
			importanceCurve = new ConstantSpectralCurve(1.f);
		
		// set spectral sampling importance
		switch( measureMode )
		{
		case INTEGRATED_SPECTRUM:
			// the importance is proportional to the spectral importance curve and the envelope of the spectral sensitivity functions
			FluxSpectrum.setImportance(new SpectralCurve()
				{
					public float sample( float lambda )
					{
						float maximum = 0.f;
						if( sensitivityCurves != null )
						{
							for(SpectralCurve curve : sensitivityCurves )
							{
								maximum = Math.max(maximum,curve.sample(lambda));
							}
						}
						return importanceCurve.sample(lambda) * maximum;
					}

					public ManageableType getManageableType() {
						// TODO Auto-generated method stub
						return null;
					}
				}
			);
			break;
		default:
			// the importance is constant over the entire spectrum
			FluxSpectrum.setImportance(importanceCurve);
			break;
		}
		
		long sceneConstructionTime = 0, sceneSerializationTime = 0;
		
		try
		{
			GraphManager g = w.getRegistry ().getProjectGraph ();

			Graph graph = GraphState.current (g).getGraph();
			
			setProgress ("Build scene", ProgressMonitor.INDETERMINATE_PROGRESS);
				
			// build scene 
			setProgress ("Build scene", ProgressMonitor.INDETERMINATE_PROGRESS);
			
			sceneConstructionTime = System.currentTimeMillis ();
			
			// constuct flux scene
			scene = new FluxScene();
			
			// build scene from scene graph
			scene.buildSceneFromGraph( graph , null, measureObjectFilter, enableSensors, this, false, flatness );
			
			sceneConstructionTime = System.currentTimeMillis () - sceneConstructionTime;
			stats.append( scene.getLog() );
			
			setupJOCL();
			stats.append( computeContext.getLog() );
			
			if( !computeContext.valid() )
				return;
			
			setProgress ("Serialize scene", ProgressMonitor.INDETERMINATE_PROGRESS);
			sceneSerializationTime = System.currentTimeMillis ();
			
			// serialize scene
			serializer = new de.grogra.gpuflux.scene.FluxSceneSerializer();
			
			// construct OCL flux scene
			joclScene = new de.grogra.gpuflux.scene.FluxJOCLScene( serializer, computeContext ); 

			measuringSetup = new MeasuringSetup();
			measuringSetup.buildSetup(2*scene.getGroupCount(), scene);
			
			//  serialize flux scene
			serializer.serializeScene(scene);
			serializer.serializeMeasureSetup(measuringSetup);
			
			// setup OCL scene, camera, sensors and detectors
			joclScene.setupOCLBounds();
			joclScene.setupOCLScene(useBih);
			joclScene.setupOCLDetectors();
			joclScene.setupOCLSensors();

			sceneSerializationTime = System.currentTimeMillis () - sceneSerializationTime;
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			justBuildLights = true;
			
			stats.append("\n<B>Profile Summary</B>\n");
			stats.append("    Construction time: " + sceneConstructionTime + " ms\n");
			stats.append("    Serialize time:    " + sceneSerializationTime + " ms\n");
	        stats.append("    Device Memory:     " + (JOCLBuffer.getMemoryUsage() / 1024) + " KB\n");
	        
			setProgress ("Done", ProgressMonitor.DONE_PROGRESS);
			
			// display statistics
			w.logGUIInfo ( stats.append ("</pre></html>").toString() );
		}
	}
	
	public void compute ()
	{
		compute( true, true );
	}
	
	/**
	 * (Re-)computes the light distribution in the current graph. This method
	 * has to be invoked at first in order for {@link #getSensedIrradiance} and {@link #getAbsorbedPower}
	 * to return correct values.
	 * @param forceRebuild true forces rebuilding of the scene, otherwise the last build is reused
	 * @param forceRebuild2 
	 * @param lights list of lights for which the light distribution is computed. If null, the light distribution is computed for all lights in the scene.
	 */
	public void compute (boolean forceCompute, boolean forceRebuild)
	{
		if( forceRebuild || scene == null )
		{
			build();
		}
		else if( !justBuildLights )
		{
			buildlights();
		}
		
		justBuildLights = false;
		
		if( !computeContext.valid() )
			return;
		
		//if( lights == null )
		//	lights = scene.getDefaultLights();
		
		Workbench w = Workbench.current ();
		if (w == null)
		{
			return;
		}
				
		StringBuffer stats = new StringBuffer("<html><pre>");
		
		stats.append( "<B>GPUFluxLightModel</B>\n\n" );
		
		w.beginStatus (this);
		
		DeviceMonitor deviceMonitors [] = null;
		long sceneConstructionTime = 0, totalRenderTime = 0, loadResultTime = 0;
		long totalsamples = 0;
				
		try
		{
			SharedBuffer curveBuffer;
			
			setProgress ("Build scene", ProgressMonitor.INDETERMINATE_PROGRESS);
				
			sceneConstructionTime = System.currentTimeMillis ();
			
			// build scene 
			setProgress ("Build scene", ProgressMonitor.INDETERMINATE_PROGRESS);
			
			// create curve buffer
			curveBuffer = serializeSensitivityCurves();
			
			sceneConstructionTime = System.currentTimeMillis () - sceneConstructionTime;
			stats.append( scene.getSceneStats() );
						
			if( scene.getLightCount() == 0 )
			{
				stats.append( "Scene has no light sources!\n\n" );
			}
			else
			{
				irradiance = null; // release last experiments
				power = null; // release last experiments
				
				setProgress ("Serialize scene", ProgressMonitor.INDETERMINATE_PROGRESS);
				
				joclScene.setupOCLLights();
				
				setProgress ("Load kernel", ProgressMonitor.INDETERMINATE_PROGRESS);
				
				// load jocl kernel
				loadKernel( useBih?"-D BIH ":"-D BVH " );
				
				// get samples per batch
				int initialSampleCount = FluxSettings.getOCLInitialSampleCount();
				maximumSampleCount = FluxSettings.getOCLMaximumSampleCount();
				preferredDuration = FluxSettings.getOCLPreferredDuration();
				
				AbstractList<Device> deviceList = computeContext.getDeviceList();
				deviceMonitors = new DeviceMonitor[deviceList.size()];
				
				openDeviceList = new ConcurrentLinkedQueue<DeviceMonitor>();
				available = new Semaphore(deviceList.size());
				
				measuringSetup.setDimensions(getMeasureDimensions());
				for(int i11 = 0 ; i11 < deviceList.size(); i11++ )
				{
					Device device = deviceList.get(i11);
					DeviceMonitor monitor = new DeviceMonitor();
					
					monitor.smlprun = initialSampleCount;
					monitor.totalsml = 0;
					monitor.device = device;
					monitor.total_time = 0;
					
					// create output buffers
					monitor.powerBuffer = device.createBuffer(Sizeof.cl_float*measuringSetup.getDimensions()*measuringSetup.getNumMeasurement(), CL_MEM_READ_WRITE);
					monitor.powerBuffer.clear();

					monitor.irradianceBuffer = device.createBuffer(Sizeof.cl_float*measuringSetup.getDimensions()*measuringSetup.getNumMeasurement(), CL_MEM_READ_WRITE);
					monitor.irradianceBuffer.clear();
								
					deviceMonitors[i11] = monitor;
					
					openDeviceList.add(monitor);
					
					// start the thread
					monitor.start();
				}				
				
				setProgress ("Execute", ProgressMonitor.INDETERMINATE_PROGRESS);
		
				stats.append("<B>Settings</B>\n");	
				// compute sample count
								
				stats.append(FluxSettings.getLightModelSettingsLog());
				stats.append(FluxSettings.getOpenCLSettingsLog());
				stats.append("    <b>LightModel settings</b>\n");

				// get light list
				Vector<Light> lights = new Vector<Light>();
				for( FluxLight fluxLight : scene.getLights() )
				{
					lights.add(	fluxLight.getLight() );
				}
				
				int sampleCount = varianceToSampleCount( lights, minPower, targetVariance );
				stats.append( "        Target sample count: " + sampleCount + "\n");
				sampleCount = Math.min(sampleCount,rayCount);
				stats.append( "        Cutoff sample count: " + sampleCount + "\n");

				stats.append("        Depth: " + depth + "\n");
				stats.append("        Minimum Power: " + minPower + "\n");
				stats.append("        Spectral: " + ((measureMode == MeasureMode.RGB)?"RGB":((measureMode == MeasureMode.INTEGRATED_SPECTRUM)?"INTEGRATED SPECTRUM":"FULL SPECTRUM")) + "\n");
				stats.append("        Dispersian: " + (dispersion?"Enabled":"Disabled") + "\n");
				stats.append("        Lambda range: [" + minLambda + "," + maxLambda + "]\n");
				stats.append("        Spectral buckets: " + spectralBuckets + "\n");
				stats.append("        Sensors: " + (enableSensors?"Enabled":"Disabled") + "\n");
				stats.append("\n");
				
				stats.append("<B>Render Profile</B>\n");
				
				totalRenderTime = System.currentTimeMillis ();
				
				// sample light paths
				int smpl = 0;
				finnished = false;
				while( smpl < sampleCount )
				{
					try {
						available.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
					
					DeviceMonitor monitor = openDeviceList.remove();
					
					int nsmpl = Math.min( monitor.smlprun, sampleCount - smpl);
					// set last sample count
					monitor.smlprun = nsmpl; 
					
					// set thread count
					monitor.device.setKernelArgInt(kernel, 1, nsmpl);
					// set thread offset
					monitor.device.setKernelArgInt(kernel, 2, smpl);
					// set total sample count
					monitor.device.setKernelArgInt(kernel, 3, sampleCount);
					
					// set output buffers
					monitor.device.setKernelArgMemBuffer(kernel, 4, monitor.powerBuffer);
					monitor.device.setKernelArgMemBuffer(kernel, 5, monitor.irradianceBuffer);
					
					// set detectors
					joclScene.setKernelArgDetectors(monitor.device, kernel, 6);
					monitor.device.setKernelArgInt(kernel, 7, measuringSetup.getNumMeasurementBits());
					
					// set scene
					joclScene.setKernelArgScene(monitor.device, kernel, 8);
					// set sensors
					joclScene.setKernelArgSensors(monitor.device, kernel, 21);
					
					// set maximum depth
					monitor.device.setKernelArgInt(kernel, 25, depth);
					// set minimum power
					monitor.device.setKernelArgFloat(kernel, 26, (float) cutoffPower);
					
					// set bounds
					joclScene.setKernelArgBounds(monitor.device, kernel, 27);
					
					// set curve buffer
					monitor.device.setKernelArgMemBuffer(kernel, 28, curveBuffer);
											
					// set seed
					monitor.device.setKernelArgInt(kernel, 29, 0);
					
					// execute kernel
					monitor.device.executeKernel(kernel , nsmpl, monitor.event);
						
					// resume monitor thread
					monitor.resume.release();
					
					// set progress
					setProgress ("Execute", (float)smpl / (float)sampleCount);
											
					// account for enqueued samples
					smpl += nsmpl;
					totalsamples += nsmpl;
				}
								
				// set to finnished
				finnished = true;
				// resume all monitors
				for( DeviceMonitor monitor : deviceMonitors ) 
					monitor.resume.release();
				
				computeContext.finish();
				totalRenderTime = System.currentTimeMillis () - totalRenderTime;
				
				for(DeviceMonitor monitor : deviceMonitors )
				{
					stats.append( "Device: " + monitor.device.getName() + "\n" );
					stats.append( "\tTotal samples:     " + monitor.totalsml + "\n");
					stats.append( "\tSamples per batch: " + monitor.smlprun + "\n" );
					stats.append( "\tTotal trace time:  " + (int)(monitor.total_time * 1000) + " ms\n" );
					stats.append( "\tSamples per second: " + (monitor.totalsml / (1000.0*1000.0)) / monitor.total_time + " MSmpl/s\n" );
				}
				
				setProgress ("Load results", ProgressMonitor.INDETERMINATE_PROGRESS);
				
				loadResultTime = System.currentTimeMillis ();
				
				// accumulate the measurement for all devices
		        float power [] = new float[(measuringSetup.getDimensions())*measuringSetup.getNumMeasurement()];
		        for( DeviceMonitor monitor : deviceMonitors ) {
		        	byte powerData[] = new byte[Sizeof.cl_float*measuringSetup.getDimensions()*measuringSetup.getNumMeasurement()];
					monitor.powerBuffer.readBuffer(powerData);
					monitor.powerBuffer = null; // release memory
					
					ByteBuffer powerByteBuffer = ByteBuffer.wrap(powerData);
					powerByteBuffer.order(monitor.device.getByteOrder());
			        FloatBuffer powerBuffer = powerByteBuffer.asFloatBuffer();
			        
			        for(int i11 = 0; i11 < (measuringSetup.getDimensions()) * measuringSetup.getNumMeasurement(); i11++ )
			        {
			        	power[i11] += powerBuffer.get(i11);
			        }
		        }
				this.power = loadMeasuredData( power );
				power = null; // release memory
				
		        float irradiance [] = new float[(measuringSetup.getDimensions())*measuringSetup.getNumMeasurement()];
		        for( DeviceMonitor monitor : deviceMonitors ) {
					byte irradianceData[] = new byte[Sizeof.cl_float*measuringSetup.getDimensions()*measuringSetup.getNumMeasurement()];
					monitor.irradianceBuffer.readBuffer(irradianceData);
					monitor.irradianceBuffer = null; // release memory
					
			        ByteBuffer irradianceByteBuffer = ByteBuffer.wrap(irradianceData);
					irradianceByteBuffer.order(monitor.device.getByteOrder());
			        FloatBuffer irradianceBuffer = irradianceByteBuffer.asFloatBuffer();
			        
			        for(int i11 = 0; i11 < (measuringSetup.getDimensions()) * measuringSetup.getNumMeasurement(); i11++ )
			        {
			        	irradiance[i11] += irradianceBuffer.get(i11);
			        }
		        }
				this.irradiance = loadMeasuredData( irradiance );
				irradiance = null; // release memory
				
				loadResultTime = System.currentTimeMillis () - loadResultTime;
				
				stats.append( "\n" );
				
				for( DeviceMonitor monitor : deviceMonitors ) {
					stats.append( monitor.log + "\n" );
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
			stats.append("<B>Profile Summary</B>\n");
			stats.append("    Build time: " + sceneConstructionTime + " ms\n");
			stats.append("    Render time:       " + totalRenderTime + " ms\n");
			stats.append("    Load Result time:  " + loadResultTime + " ms\n");
			if( totalRenderTime > 0 )
				stats.append("    Performance:       " + ((totalsamples / totalRenderTime) / 1000.0) + " MSmpl/s\n");
			
			if( deviceMonitors != null )
			{
		        long total = 0;
				for(DeviceMonitor monitor : deviceMonitors )
					total += monitor.totalsml;
				
				if( total > 0 )
				{
					for( DeviceMonitor monitor : deviceMonitors ) {
						stats.append( "        " + ((monitor.totalsml * 100) / total) + "%: \t" + monitor.device.getName() + "\n" );
					}
				}
			}
			
			stats.append("    Device Memory usage:  " + (JOCLBuffer.getMemoryUsage() / 1024) + " KB\n");
			
			setProgress ("Done", ProgressMonitor.DONE_PROGRESS);
			
			// display statistics
			w.logGUIInfo ( stats.append ("</pre></html>").toString() );
			
			//  release data 
			available = null;
			openDeviceList = null;
		}
	}
		

	private Experiment loadMeasuredData(float[] data) {

		double invRayCount = 1.0 / rayCount;
		return new Experiment(scene.getNodeToGroup(),measuringSetup.LoadMeasuredData(data, invRayCount), new Measurement( getMeasureDimensions() ));
	}
	
	@Deprecated 
	/**
	 * Returns the irradiance sensed by a node during the last call to {@link #compute}
	 * Available for compatibility with LightModel
	 *  
	 * @param node node for which the absorbed power is returned
	 * @return sensed irradiance 
	 */
	public Spectrum3d getSensedIrradiance (Node node)
	{
		Measurement m = getSensedIrradianceMeasurement(node);
		return new Spectrum3d( m.data[0], m.data[1], m.data[2] );
	}
	
	@Deprecated 
	/**
	 * Returns the power absorbed by a node during the last call to {@link #compute}
	 * Available for compatibility with LightModel
	 *  
	 * @param node node for which the absorbed power is returned
	 * @return absorbed power 
	 */
	public Spectrum3d getAbsorbedPower (Node node)
	{
		Measurement m = getAbsorbedPowerMeasurement(node);
		return new Spectrum3d( m.data[0], m.data[1], m.data[2] );
	}
	
	/**
	 * Returns the experiment data on sensed irradiance, computed during the last call to {@link #compute}
	 * @return sensed irradiance data 
	 */
	public Experiment getSensedIrradiance ()
	{
		return irradiance;
	}
	
	/**
	 * Returns the experiment data on absorbed power, computed during the last call to {@link #compute}
	 * @return absorbed power data
	 */
	public Experiment getAbsorbedPower ()
	{
		return power;
	}
	
	/**
	 * Returns the irradiance sensed by a node during the last call to {@link #compute}
	 *  
	 * @param node node for which the absorbed power is returned
	 * @return sensed irradiance 
	 */
	public Measurement getSensedIrradianceMeasurement (Node node)
	{
		if( !enableSensors ) throw new UnsupportedOperationException( "Sensors are currently disabled" );
		return irradiance.getMeasurement(node);
	}
	
	/**
	 * Returns the power absorbed by a node during the last call to {@link #compute}
	 *  
	 * @param node node for which the absorbed power is returned
	 * @return absorbed power 
	 */
	public Measurement getAbsorbedPowerMeasurement (Node node)
	{
		if( power == null ) return new Measurement();
		return power.getMeasurement(node);
	}

	public void setProgress(String text, float progress) {
		Workbench w = Workbench.current ();
		w.setStatus (this, text);
		if (progress < 0)
		{
			w.setIndeterminateProgress (this);
		}
		else if (progress == DONE_PROGRESS)
		{
			w.clearProgress (this);
		}
		else
		{
			w.setProgress (this, progress);
		}
	}
	
	public void showMessage (String message)
	{
		Workbench w = Workbench.current ();
		w.logGUIInfo(message);
	}

	public void setMeasureObjectFilter(ObjectFilter measureObjectFilter) {
		this.measureObjectFilter = measureObjectFilter;
	}

	/**
	 * Returns the estimated sample count, required to achieve a given target variance for one computation 
	 *  
	 * @param lights the list of light sources
	 * @param minPower the smallest measurable power quantum
	 * @param targetVariance the target variance on the smallest measurable power quantum
	 * @return the estimated number of required samples
	 */
	public int varianceToSampleCount(AbstractList<Light> lights, double minPower, double targetVariance) {
		double totalPower = estimateTotalPower(lights, scene.getEnvironment());
		return varianceToSampleCount(totalPower, minPower, targetVariance);
	}
	
	/**
	 * Returns the variance for a given sample count for one computation 
	 *  
	 * @param lights the list of light sources
	 * @param minPower the smallest measurable power quantum
	 * @param sampleCount the number of samples
	 * @return the variance on the smallest measurable power quantum
	 */
	public double sampleCountToVariance(AbstractList<Light> lights, double minPower, int sampleCount) {
		double totalPower = estimateTotalPower(lights, scene.getEnvironment());
		return sampleCountToVariance(totalPower, minPower, sampleCount);
	}
	
	private static int varianceToSampleCount(double totalPower, double minPower, double variance) {
		
		/*
		 * The simulation generates n photons of totalPower/n power each
		 * How large should n be, to reach the expected target variance for some minimal measured power
		 * We assume that all samples are sampled independently
		 * Than, for an area having minPower power, the probability of a sample reaching the area equals p = minPower/totalPower
		 * For n samples, the contribution of each sample equals c=totalPower/n
		 * The estimated variance for a scaled binomial distribution equals variance = c^2*(n-1)*p*(1-p)= totalPower^2/n^2*(n)*p*(1-p)
		 * 
		 *  variance = totalPower^2/n^2*n*minPower/totalPower*(1-minPower/totalPower) =  totalPower * minPower * (1-minPower/totalPower) * 1 / n
		 * 
		 * Hence, n = totalPower * minPower * (1-minPower/totalPower) / variance
		 * */
		if( totalPower == 0 )
			return 0;
		double prob = Math.min(1, minPower / totalPower);
		if( variance == 0 )
			return Integer.MAX_VALUE;
		int n = (int)Math.ceil(totalPower * minPower * (1-prob) / variance);
		return n;
	}
	
	private static double sampleCountToVariance(double totalPower, double minPower, int sampleCount) {
		double prob = Math.min(1, minPower / totalPower);
		double var = ((totalPower / totalPower) * (sampleCount * sampleCount)) * sampleCount * prob * (1-prob);
		return var;
	}
	
	private static double estimateTotalPower(AbstractList<Light> lights, Environment env) {

		double power = 0.f;
		
		for( Light light: lights )
			power += light.getTotalPower(env);
		
		return power;
	}

	public void setTargetVariance(double minPower, double targetVariance) {
		this.minPower = minPower;
		this.targetVariance = targetVariance;
	}

	public void setDispersion(boolean dispersion) {
		this.dispersion = dispersion;
	}
	
	public void setFlatness( float flatness ) {
		this.flatness = flatness;
	}

	public int getMaxLambda() {
		return maxLambda;
	}
	
	public void setLayerVisible (int layer, boolean visible)
	{
		visibleLayers[layer] = visible;
	}
	
}
