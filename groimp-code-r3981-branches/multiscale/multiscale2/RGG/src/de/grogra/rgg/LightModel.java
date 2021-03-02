/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.rgg;

import de.grogra.graph.Attributes;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.ray2.SceneVisitor;
import de.grogra.imp3d.ray2.VolumeListener;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.ShareableBase;
import de.grogra.pf.ui.Workbench;
import de.grogra.ray.physics.Collector;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.tracing.LightModelProcessor;
import de.grogra.ray2.tracing.ParallelRadiationModel;
import de.grogra.ray2.tracing.RadiationModel;
import de.grogra.vecmath.geom.Volume;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.LongToIntHashMap;

/**
 * This light model generates light rays from the light sources in the current
 * scene and calculates how much light is received by any object. This
 * basically works like an inverse ray tracer. <br>
 * <br>
 * The total light contribution of the light source to the scene determines
 * how many rays are created for that light source. For instance if there were
 * two light sources with a power of 100W and 10W and a total number of 11.000
 * rays, then the first light would create 10.000 rays and the second light
 * 1.000 rays. <br>
 * 
 * @author Reinhard Hemmerling
 *
 */
public class LightModel extends LightModelBase implements Options,
		VolumeListener
{
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
	 * Next unused group index. Note that we have to start with 1 so that
	 * group index 0 is never used. If nodeToGroup.get(id) returns 0, we know
	 * that there is no group associated with id.
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

	/**
	 * The number of rays that is cast from light sources each time compute()
	 * is called.
	 */
	private int rayCount;
	//enh:field getter

	/**
	 * Maximum recursion depth
	 */
	private int depth;
	//enh:field getter

	/**
	 * Minimum power for a ray to continue recursion
	 */
	private double minPower;
	//enh:field getter

	/**
	 * The seed for the pseudorandom generator.
	 */
	private long seed;
	//enh:field getter setter

	private int threadCount;
	//enh:field getter setter

	/**
	 * The spectrum factory. This defines the type of spectra on which the
	 * light model will perform its computations.
	 */
	private Spectrum spectrumFactory = new Spectrum3d ();
	//enh:field getter setter

	private boolean[] visibleLayers;
	//enh:field

	/**
	 * Create a default light model with 30.000 rays per computation
	 * and a ray depth of 10.
	 */
	public LightModel ()
	{
		this (30000, 10);
	}

	public LightModel (int rayCount, int depth)
	{
		this (rayCount, depth, 0.001);
	}

	public LightModel (int rayCount, int depth, double minPower)
	{
		setRayCount (rayCount);
		setDepth (depth);
		setMinPower (minPower);
		visibleLayers = new boolean[Attributes.LAYER_COUNT];
		for (int i = 0; i < Attributes.LAYER_COUNT; i++)
		{
			visibleLayers[i] = true;
		}
	}
	
	public void compute ()
	{
		compute (spectrumFactory);
	}

	public void compute (Spectrum spectrumFactory)
	{
		compute (spectrumFactory, false);
	}

	public void compute (boolean force)
	{
		compute (spectrumFactory, force);
	}

	/**
	 * (Re-)computes the light distribution in the current graph. This method
	 * has to be invoked at first in order for {@link #getRadiantPowerFor}
	 * to return correct values.
	 * @param force if true forces recomputation of the light distribution
	 */
	public void compute (Spectrum spectrumFactory, boolean force)
	{
		msgCount = 20;
		Workbench w = Library.workbench ();
		if (w == null)
		{
			return;
		}
		GraphManager g = w.getRegistry ().getProjectGraph ();
		if (force || (processor == null) || (stamp != g.getStamp ()))
		{
			// data is out-of-date, recompute the scene
			if (nodeToGroup == null)
			{
				nodeToGroup = new LongToIntHashMap ();
				idToGroup = new IntList ();
			}
			else
			{
				nodeToGroup.clear ();
				idToGroup.clear ();
			}
			grouping = 0;
			nextGroupIndex = 1;
			currentGroupIndex = -1;
			long sceneTime = System.currentTimeMillis ();
			SceneVisitor scene = new SceneVisitor (w, g, 1e-5f, this, null,
				visibleLayers, this, spectrumFactory.clone ());

			long radiationTime = System.currentTimeMillis ();
			sceneTime = radiationTime - sceneTime;

			processor = new ParallelRadiationModel (scene, idToGroup.elements, threadCount);
			scene.setProgress ("Shooting rays...", 0);
			processor.compute (rayCount, seed, scene, depth, minPower);

			radiationTime = System.currentTimeMillis () - radiationTime;

			scene.setProgress ("Done after "
				+ (sceneTime + radiationTime) + " ms",
				ProgressMonitor.DONE_PROGRESS);

			totalSceneTime += sceneTime;
			totalRadiationTime += radiationTime;
			w.logInfo ("LightModel: " + sceneTime + '/' + radiationTime + " ms (total "
				+ totalSceneTime + '/' + totalRadiationTime + " ms), "
				+ (Runtime.getRuntime ().totalMemory () - Runtime.getRuntime ().freeMemory ()) / 1024
				+ " kB used, bounds are " + scene.getBoundingBox ());

			stamp = g.getStamp ();

			// scene no longer needed, release memory resources
			scene.dispose ();
		}
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

	public void setMinPower (double minPower)
	{
		if (!(minPower > 0))
			throw new IllegalArgumentException (
				"minPower must be a positive double but was " + minPower);

		this.minPower = minPower;
	}

	public void setLayerVisible (int layer, boolean visible)
	{
		visibleLayers[layer] = visible;
	}

	private static void print (String msg)
	{
		Workbench w = Library.workbench ();
		if (w != null)
		{
			w.logGUIInfo (msg);
		}
		else
		{
			Library.out.println (msg);
		}
	}

	private int checkVolumeId (Node node)
	{
		if ((nodeToGroup == null) || (processor == null))
		{
			if (msgCount > 0)
			{
				print ("LightModel data has not yet been computed.");
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
	 * Returns the radiant power in Watts which is absorbed by the surface
	 * of the volume of the given <code>node</code>. If the <code>node</code>
	 * does not define a volume, the zero spectrum is returned.
	 * 
	 * @param node a node of the graph
	 * @return the absorbed radiant power of the node
	 */
	public Spectrum getAbsorbedPower (Node node)
	{
		int volumeId = checkVolumeId (node);
		return (volumeId >= 0) ? processor.getAbsorbedPower (volumeId) : spectrumFactory.newInstance ();
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
	public Spectrum getSensedIrradiance (Node node)
	{
		int volumeId = checkVolumeId (node);
		return (volumeId >= 0) ? processor.getSensedIrradiance (volumeId) : spectrumFactory.newInstance ();
	}

	public Collector getSensedIrradianceCollector(Node node)
	{
		int volumeId = checkVolumeId (node);
		return (volumeId >= 0) ? (Collector) processor.getSensedIrradianceCollector (volumeId) : ((Collector) spectrumFactory).newInstance();
	}
	
	public Collector getAbsorbedPowerCollector(Node node)
	{
		int volumeId = checkVolumeId (node);
		return (volumeId >= 0) ? (Collector) processor.getAbsorbedPowerCollector (volumeId) : ((Collector) spectrumFactory).newInstance();
	}
	
	public Object get (String key, Object defaultValue)
	{
		// return options for scene construction and/or concrete light model
		return defaultValue;
	}

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
	
	public void endGroup ()
	{
		if (--grouping < 0)
		{
			throw new IllegalStateException ();
		}
	}
	
	/**
	 * This invalidates the octree of the SceneVisitor and forces a
	 * complete new computation. This method is useful if you want
	 * to recompute the LightModel without any changes of the scene.
	 */
	public void invalidateOctree()
	{
		//nodeToGroup = null;
		stamp = 0;
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field rayCount$FIELD;
	public static final Type.Field depth$FIELD;
	public static final Type.Field minPower$FIELD;
	public static final Type.Field seed$FIELD;
	public static final Type.Field threadCount$FIELD;
	public static final Type.Field spectrumFactory$FIELD;
	public static final Type.Field visibleLayers$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (LightModel representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 7;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setInt (Object o, int id, int value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((LightModel) o).rayCount = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((LightModel) o).depth = (int) value;
					return;
				case Type.SUPER_FIELD_COUNT + 4:
					((LightModel) o).threadCount = (int) value;
					return;
			}
			super.setInt (o, id, value);
		}

		@Override
		protected int getInt (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((LightModel) o).getRayCount ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((LightModel) o).getDepth ();
				case Type.SUPER_FIELD_COUNT + 4:
					return ((LightModel) o).getThreadCount ();
			}
			return super.getInt (o, id);
		}

		@Override
		protected void setLong (Object o, int id, long value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 3:
					((LightModel) o).seed = (long) value;
					return;
			}
			super.setLong (o, id, value);
		}

		@Override
		protected long getLong (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 3:
					return ((LightModel) o).getSeed ();
			}
			return super.getLong (o, id);
		}

		@Override
		protected void setDouble (Object o, int id, double value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					((LightModel) o).minPower = (double) value;
					return;
			}
			super.setDouble (o, id, value);
		}

		@Override
		protected double getDouble (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((LightModel) o).getMinPower ();
			}
			return super.getDouble (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 5:
					((LightModel) o).spectrumFactory = (Spectrum) value;
					return;
				case Type.SUPER_FIELD_COUNT + 6:
					((LightModel) o).visibleLayers = (boolean[]) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 5:
					return ((LightModel) o).getSpectrumFactory ();
				case Type.SUPER_FIELD_COUNT + 6:
					return ((LightModel) o).visibleLayers;
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new LightModel ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (LightModel.class);
		rayCount$FIELD = Type._addManagedField ($TYPE, "rayCount", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 0);
		depth$FIELD = Type._addManagedField ($TYPE, "depth", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 1);
		minPower$FIELD = Type._addManagedField ($TYPE, "minPower", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.DOUBLE, null, Type.SUPER_FIELD_COUNT + 2);
		seed$FIELD = Type._addManagedField ($TYPE, "seed", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.LONG, null, Type.SUPER_FIELD_COUNT + 3);
		threadCount$FIELD = Type._addManagedField ($TYPE, "threadCount", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.Type.INT, null, Type.SUPER_FIELD_COUNT + 4);
		spectrumFactory$FIELD = Type._addManagedField ($TYPE, "spectrumFactory", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Spectrum.class), null, Type.SUPER_FIELD_COUNT + 5);
		visibleLayers$FIELD = Type._addManagedField ($TYPE, "visibleLayers", Type.Field.PRIVATE  | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (boolean[].class), null, Type.SUPER_FIELD_COUNT + 6);
		$TYPE.validate ();
	}

	public int getRayCount ()
	{
		return rayCount;
	}

	public int getDepth ()
	{
		return depth;
	}

	public int getThreadCount ()
	{
		return threadCount;
	}

	public void setThreadCount (int value)
	{
		this.threadCount = (int) value;
	}

	public long getSeed ()
	{
		return seed;
	}

	public void setSeed (long value)
	{
		this.seed = (long) value;
	}

	public double getMinPower ()
	{
		return minPower;
	}

	public Spectrum getSpectrumFactory ()
	{
		return spectrumFactory;
	}

	public void setSpectrumFactory (Spectrum value)
	{
		spectrumFactory$FIELD.setObject (this, value);
	}

//enh:end
}
