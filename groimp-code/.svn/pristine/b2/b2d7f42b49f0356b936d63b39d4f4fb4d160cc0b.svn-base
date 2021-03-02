package de.grogra.rgg;

import de.grogra.graph.Attributes;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.ray2.SceneVisitor;
import de.grogra.imp3d.ray2.VolumeListener;
import de.grogra.pf.ui.Workbench;
import de.grogra.ray.physics.Collector;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.tracing.ParallelRadiationModelD;
import de.grogra.ray2.tracing.RadiationModel;
import de.grogra.vecmath.geom.Volume;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongToIntHashMap;
import de.grogra.xl.util.ObjectList;

/**
 * Provides the same functionality as LightModelD but in addition can deliver
 * results for every recursion depth.
 * 
 * @author MH
 *
 */
public class LightModelD extends LightModel implements Options, VolumeListener {
//enh:sco SCOType

	/**
	 * Stamp of the graph for which the model has been computed.
	 */
	private transient int stamp;

	/**
	 * Map from node ids to group indices.
	 */
	private transient LongToIntHashMap nodeToGroup;

	/**
	 * Map from volume ids to group indices.
	 */
	private transient IntList idToGroup;

	/**
	 * Grouping counter. If positive, we are within a group.
	 */
	private transient int grouping;

	/**
	 * Next unused group index. Note that we have to start with 1 so that group
	 * index 0 is never used. If nodeToGroup.get(id) returns 0, we know that there
	 * is no group associated with id.
	 */
	private transient int nextGroupIndex;

	/**
	 * Group index to use within {@link #volumeCreated}.
	 */
	private transient int currentGroupIndex;

	private transient int msgCount = 20;

	private transient RadiationModel processor;

	private transient long totalSceneTime;

	private transient long totalRadiationTime;

	private final Spectrum[] BLACK;

	/**
	 * Create a default light model with 30.000 rays per computation and a ray depth
	 * of 10.
	 */
	public LightModelD() {
		this(30000, 10);
	}

	public LightModelD(int rayCount, int depth) {
		this(rayCount, depth, 0.001);
	}

	public LightModelD(int rayCount, int depth, double minPower) {
		setRayCount(rayCount);
		setDepth(depth);
		setMinPower(minPower);
		visibleLayers = new boolean[Attributes.LAYER_COUNT];
		for (int i = 0; i < Attributes.LAYER_COUNT; i++) {
			visibleLayers[i] = true;
		}
		BLACK = new Spectrum[depth];
		for(int i=0;i<depth; i++) BLACK[i] = spectrumFactory.newInstance();
		
	}

	/**
	 * (Re-)computes the light distribution in the current graph. This method has to
	 * be invoked at first in order for {@link #getRadiantPowerFor} to return
	 * correct values.
	 * 
	 * @param force             if true forces recomputation of the light
	 *                          distribution
	 * @param collectTracedRays if true the traced rays are collected and can be
	 *                          obtained by {}. This can be used for debugging
	 *                          purposes.
	 */
	@Override
	public void compute(Spectrum spectrumFactory, boolean force, boolean collectTracedRays) {
		msgCount = 20;
		Workbench w = Library.workbench();
		if (w == null) {
			return;
		}
		GraphManager g = w.getRegistry().getProjectGraph();
		if (force || (processor == null) || (stamp != g.getStamp())) {
			// data is out-of-date, recompute the scene
			if (nodeToGroup == null) {
				nodeToGroup = new LongToIntHashMap();
				idToGroup = new IntList();
			} else {
				nodeToGroup.clear();
				idToGroup.clear();
			}
			grouping = 0;
			nextGroupIndex = 1;
			currentGroupIndex = -1;
			long sceneTime = System.currentTimeMillis();
			SceneVisitor scene = new SceneVisitor(w, g, 1e-5f, this, null, visibleLayers, this, spectrumFactory.clone());

			long radiationTime = System.currentTimeMillis();
			sceneTime = radiationTime - sceneTime;

			processor = new ParallelRadiationModelD(scene, idToGroup.elements, threadCount,
					collectTracedRays ? new ObjectList<ObjectList<RadiationModel.RayPoint>>() : null);
			scene.setProgress("Shooting rays...", 0);
			processor.compute(rayCount, seed, scene, depth, minPower);

			radiationTime = System.currentTimeMillis() - radiationTime;

			scene.setProgress("Done after " + (sceneTime + radiationTime) + " ms", ProgressMonitor.DONE_PROGRESS);

			totalSceneTime += sceneTime;
			totalRadiationTime += radiationTime;
			w.logInfo("LightModelD: " + sceneTime + '/' + radiationTime + " ms (total " + totalSceneTime + '/'
					+ totalRadiationTime + " ms), "
					+ (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024
					+ " kB used, bounds are " + scene.getBoundingBox());

			stamp = g.getStamp();

			// scene no longer needed, release memory resources
			scene.dispose();
		}
	}


	@Override
	protected int checkVolumeId (Node node)
	{
		if ((nodeToGroup == null) || (processor == null))
		{
			if (msgCount > 0)
			{
				print ("LightModelD data has not yet been computed.");
				msgCount--;
			}
			return -1;
		}
		int volumeId = nodeToGroup.get (node.getId (), -1);
		if (volumeId < 0)
		{
			if (msgCount > 0)
			{
				print (node + " is invisible, no LightModel data available.");
				msgCount--;
			}
		}
		return volumeId;
	}

	/**
	 * Returns the number of rays which is hit the surface of the volume of the
	 * given <code>node</code>. If the <code>node</code> does not define a volume,
	 * the zero is returned.
	 * 
	 * @param node a node of the graph
	 * @return the number of rays which is hit the node
	 */
	@Override
	public int getHitCount(Node node) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			int[] specA = processor.getHitCountD(volumeId);
			int sum = 0;
			for(int i=0;i<specA.length; i++) {
				sum += specA[i];
			}
			return sum;
		}
		return 0;
	}

	/**
	 * Returns the radiant power in Watts which is transmitted by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the transmitted radiant power of the node
	 */
	@Override
	public Spectrum getTransmittedPower(Node node) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			Spectrum[] specA = processor.getTransmittedPowerD(volumeId);
			Spectrum sum = spectrumFactory.newInstance();
			for(int i=0;i<specA.length; i++) {
				if(specA[i]!=null) {
					sum.add(specA[i]);
				}
			}
			return sum;
		}
		return spectrumFactory.newInstance();
	}

	/**
	 * Returns the radiant power in Watts which is reflected by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the reflected radiant power of the node
	 */
	@Override
	public Spectrum getReflectedPower(Node node) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			Spectrum[] specA = processor.getReflectedPowerD(volumeId);
			Spectrum sum = spectrumFactory.newInstance();
			for(int i=0;i<specA.length; i++) {
				if(specA[i]!=null) {
					sum.add(specA[i]);
				}
			}
			return sum;
		}
		return spectrumFactory.newInstance();
	}

	/**
	 * Returns the radiant power in Watts which is received by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the received radiant power of the node
	 */
	@Override
	public Spectrum getReceivedPower(Node node) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			Spectrum[] specA = processor.getReceivedPowerD(volumeId);
			Spectrum sum = spectrumFactory.newInstance();
			for(int i=0;i<specA.length; i++) {
				if(specA[i]!=null) {
					sum.add(specA[i]);
				}
			}
			return sum;
		}
		return spectrumFactory.newInstance();
	}

	/**
	 * Returns the radiant power in Watts which is absorbed by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the absorbed radiant power of the node
	 */
	@Override
	public Spectrum getAbsorbedPower(Node node) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			Spectrum[] specA = processor.getAbsorbedPowerD(volumeId);
			Spectrum sum = spectrumFactory.newInstance();
			for(int i=0;i<specA.length; i++) {
				if(specA[i]!=null) {
					sum.add(specA[i]);
				}
			}
			return sum;
		}
		return spectrumFactory.newInstance();
	}

	/**
	 * Returns the irradiance in Watts per square meter which is sensed by the
	 * sensor attached to the volume of the given <code>node</code>. If the
	 * <code>node</code> does not define a volume with a sensor, the zero spectrum
	 * is returned.
	 * 
	 * @param node a node of the graph
	 * @return the sensed irradiance of the node
	 */
	@Override
	public Spectrum getSensedIrradiance(Node node) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			Spectrum[] specA = processor.getSensedIrradianceD(volumeId);
			Spectrum sum = spectrumFactory.newInstance();
			for(int i=0;i<specA.length; i++) {
				if(specA[i]!=null) {
					sum.add(specA[i]);
				}
			}
			return sum;
		}
		return spectrumFactory.newInstance();
	}

	
	/**
	 * Returns the number of rays which is hit the surface of the volume of the
	 * given <code>node</code>. If the <code>node</code> does not define a volume,
	 * the zero is returned.
	 * 
	 * @param node a node of the graph
	 * @return the number of rays which is hit the node
	 */
	public int getHitCount(Node node, int idx) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			int[] intA = processor.getHitCountD(volumeId);
			if(idx<0||idx>=intA.length) return 0;
			return intA[idx];
		}
		return 0;
	}

	/**
	 * Returns the radiant power in Watts which is transmitted by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the transmitted radiant power of the node
	 */
	public Spectrum getTransmittedPower(Node node, int idx) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			Spectrum[] specA = processor.getTransmittedPowerD(volumeId);
			if(idx<0||idx>=specA.length) return spectrumFactory.newInstance();
			return (specA[idx]!=null)?specA[idx]:spectrumFactory.newInstance();
		}
		return spectrumFactory.newInstance();
	}

	/**
	 * Returns the radiant power in Watts which is reflected by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the reflected radiant power of the node
	 */
	public Spectrum getReflectedPower(Node node, int idx) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			Spectrum[] specA = processor.getReflectedPowerD(volumeId);
			if(idx<0||idx>=specA.length) return spectrumFactory.newInstance();
			return (specA[idx]!=null)?specA[idx]:spectrumFactory.newInstance();
		}
		return spectrumFactory.newInstance();
	}

	/**
	 * Returns the radiant power in Watts which is received by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the received radiant power of the node
	 */
	public Spectrum getReceivedPower(Node node, int idx) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			Spectrum[] specA = processor.getReceivedPowerD(volumeId);
			if(idx<0||idx>=specA.length) return spectrumFactory.newInstance();
			return (specA[idx]!=null)?specA[idx]:spectrumFactory.newInstance();
		}
		return spectrumFactory.newInstance();
	}

	/**
	 * Returns the radiant power in Watts which is absorbed by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the absorbed radiant power of the node
	 */
	public Spectrum getAbsorbedPower(Node node, int idx) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			Spectrum[] specA = processor.getAbsorbedPowerD(volumeId);
			if(idx<0||idx>=specA.length) return spectrumFactory.newInstance();
			return (specA[idx]!=null)?specA[idx]:spectrumFactory.newInstance();
		}
		return spectrumFactory.newInstance();
	}

	/**
	 * Returns the irradiance in Watts per square meter which is sensed by the
	 * sensor attached to the volume of the given <code>node</code>. If the
	 * <code>node</code> does not define a volume with a sensor, the zero spectrum
	 * is returned.
	 * 
	 * @param node a node of the graph
	 * @return the sensed irradiance of the node
	 */
	public Spectrum getSensedIrradiance(Node node, int idx) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0) { 
			Spectrum[] specA = processor.getSensedIrradianceD(volumeId);
			if(idx<0||idx>=specA.length) return spectrumFactory.newInstance();
			return (specA[idx]!=null)?specA[idx]:spectrumFactory.newInstance();
		}
		return spectrumFactory.newInstance();
	}

	
	/**
	 * Returns the number of rays which is hit the surface of the volume of the
	 * given <code>node</code>. If the <code>node</code> does not define a volume,
	 * the zero is returned.
	 * 
	 * @param node a node of the graph
	 * @return the number of rays which is hit the node
	 */
	public int[] getHitCountD(Node node) {
		int volumeId = checkVolumeId(node);
		if (volumeId >= 0)
			return processor.getHitCountD(volumeId);
		int[] ZEROS = new int[depth];
		for(int i=0;i<depth; i++) ZEROS[i] = 0;
		return ZEROS;
	}

	/**
	 * Returns the radiant power in Watts which is transmitted by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the transmitted radiant power of the node
	 */
	public Spectrum[] getTransmittedPowerD(Node node) {
		int volumeId = checkVolumeId(node);
		return (volumeId >= 0) ? processor.getTransmittedPowerD(volumeId) : BLACK;
	}

	/**
	 * Returns the radiant power in Watts which is reflected by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the reflected radiant power of the node
	 */
	public Spectrum[] getReflectedPowerD(Node node) {
		int volumeId = checkVolumeId(node);
		return (volumeId >= 0) ? processor.getReflectedPowerD(volumeId) : BLACK;
	}

	/**
	 * Returns the radiant power in Watts which is received by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the received radiant power of the node
	 */
	public Spectrum[] getReceivedPowerD(Node node) {
		int volumeId = checkVolumeId(node);
		return (volumeId >= 0) ? processor.getReceivedPowerD(volumeId) : BLACK;
	}

	/**
	 * Returns the radiant power in Watts which is absorbed by the surface of the
	 * volume of the given <code>node</code>. If the <code>node</code> does not
	 * define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the absorbed radiant power of the node
	 */
	public Spectrum[] getAbsorbedPowerD(Node node) {
		int volumeId = checkVolumeId(node);
		return (volumeId >= 0) ? processor.getAbsorbedPowerD(volumeId) : BLACK;
	}

	/**
	 * Returns the irradiance in Watts per square meter which is sensed by the
	 * sensor attached to the volume of the given <code>node</code>. If the
	 * <code>node</code> does not define a volume with a sensor, the zero spectrum
	 * is returned.
	 * 
	 * @param node a node of the graph
	 * @return the sensed irradiance of the node
	 */
	public Spectrum[] getSensedIrradianceD(Node node) {
		int volumeId = checkVolumeId(node);
		return (volumeId >= 0) ? processor.getSensedIrradianceD(volumeId) : BLACK;
	}

	
	@Override
	public Collector getSensedIrradianceCollector(Node node) {
		int volumeId = checkVolumeId(node);
		return (volumeId >= 0) ? (Collector) processor.getSensedIrradianceCollector(volumeId)
				: ((Collector) spectrumFactory).newInstance();
	}

	@Override
	public Collector getAbsorbedPowerCollector(Node node) {
		int volumeId = checkVolumeId(node);
		return (volumeId >= 0) ? (Collector) processor.getAbsorbedPowerCollector(volumeId)
				: ((Collector) spectrumFactory).newInstance();
	}
	
	
	@Override
	public void volumeCreated (Object object, boolean asNode, Volume volume)
	{
		// The project graph of the current workbench is used as graph. Its
		// edges have no attributes, so they cannot have an associated volume.
		assert asNode;

		if (grouping == 0)
		{
			currentGroupIndex = nextGroupIndex++;
			nodeToGroup.put (((Node) object).getId (), currentGroupIndex);
		}
		idToGroup.set (volume.getId (), currentGroupIndex);
	}

	@Override
	public void beginGroup (Object object, boolean asNode)
	{
		assert asNode;

		if (grouping == 0)
		{
			currentGroupIndex = nodeToGroup.get (((Node) object).getId ());
			if (currentGroupIndex == 0)
			{
				currentGroupIndex = nextGroupIndex++;
				nodeToGroup.put (((Node) object).getId (), currentGroupIndex);
			}
		}
		grouping++;
	}
	
	@Override
	public void endGroup ()
	{
		if (--grouping < 0)
		{
			throw new IllegalStateException ();
		}
	}

//enh:insert
//enh:begin
//NOTE: The following lines up to enh:end were generated automatically

//enh:end
}
