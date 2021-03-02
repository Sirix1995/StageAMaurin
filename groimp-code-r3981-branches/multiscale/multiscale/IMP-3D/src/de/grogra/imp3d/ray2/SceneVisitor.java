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

package de.grogra.imp3d.ray2;

import java.util.ArrayList;

import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3d;

import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.Path;
import de.grogra.imp3d.DisplayVisitor;
import de.grogra.imp3d.IMP3D;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.PolygonizationCache;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.ViewConfig3D;
import de.grogra.imp3d.VolumeBuilder;
import de.grogra.imp3d.objects.AreaLight;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.SensorNode;
import de.grogra.imp3d.objects.Sky;
import de.grogra.imp3d.shading.AlgorithmSwitchShader;
import de.grogra.imp3d.shading.Shader;
import de.grogra.imp3d.shading.ShaderRef;
import de.grogra.pf.ui.Workbench;
import de.grogra.ray.physics.Interior;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Sensor;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray2.Options;
import de.grogra.ray2.ProgressMonitor;
import de.grogra.ray2.Scene;
import de.grogra.util.Disposable;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.BoundingBox;
import de.grogra.vecmath.geom.CSGComplement;
import de.grogra.vecmath.geom.CSGDifference;
import de.grogra.vecmath.geom.CSGIntersection;
import de.grogra.vecmath.geom.CSGUnion;
import de.grogra.vecmath.geom.CompoundVolume;
import de.grogra.vecmath.geom.DefaultCellIterator;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;
import de.grogra.vecmath.geom.MeshVolume;
import de.grogra.vecmath.geom.Octree;
import de.grogra.vecmath.geom.OctreeUnion;
import de.grogra.vecmath.geom.SensorDisc;
import de.grogra.vecmath.geom.SkySphere;
import de.grogra.vecmath.geom.Volume;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

/**
 * A <code>SceneVisitor</code> is used to traverse a graph, collect
 * the geometry and lights of this graph and represent them as a
 * <code>Scene</code>.
 * 
 * @author Ole Kniemeyer
 * 
 * @see #SceneVisitor
 */
public class SceneVisitor extends DisplayVisitor implements Scene,
		ProgressMonitor, Cloneable, Disposable
{
	private ObjectList<Interior> interiorStack = new ObjectList<Interior> ();
	private Interior interior;

	private OctreeUnion sceneVolume;
	private Octree sceneOctree;
	private Octree.State sceneVolumeState;
	private ObjectList<ArrayList<Volume>> volumeStack = new ObjectList<ArrayList<Volume>> ();

	private BoundingBox bounds;

	private ArrayList<Volume> volumes;
	private ArrayList<Volume> infiniteVolumes;
	private ArrayList<Volume> nonCSGVolumes;

	private ObjectList<Light> lights = new ObjectList<Light> ();
	private Light[] lightsArray;

	private ObjectList<Sensor> sensors = new ObjectList<Sensor> ();
	private Sensor[] sensorsArray;

	private ObjectList<Matrix4d> lightTransformations = new ObjectList<Matrix4d> ();
	private Matrix4d[] lightTransformationsArray;
	private Matrix4d[] inverseLightTransformationsArray;

	private ObjectList<Matrix4d> sensorTransformations = new ObjectList<Matrix4d> ();
	private Matrix4d[] sensorTransformationsArray;
	private Matrix4d[] inverseSensorTransformationsArray;

	private ObjectList<Shader> shaders = new ObjectList<Shader> ();
	private ObjectList<Interior> interiors = new ObjectList<Interior> ();
	private ObjectList<Matrix4d> transforms = new ObjectList<Matrix4d> ();
	private IntList volumeLights = new IntList ();
	private IntList volumeSensors = new IntList ();

	private VolumeListener mapping;

	private ViewConfig3D view;
	private boolean[] visibleLayers;
	private Workbench workbench;
	private final Spectrum spectrumFactory;

	int volumeCount = 0;
	int csgCount = 0;
	int polyCount = 0;

	private Volume addedVolume;
	private boolean infinite;
	private boolean haveRealLight;

	private final long startTime;
	private long time;

	private VolumeBuilder builder;

	void addVolume (Volume v, Matrix4d t, Shader s)
	{
		int id = addVolume (v);
		if (s == null)
		{
			s = getCurrentShader ();
		}
		shaders.set (id, s);
		Matrix4d m = new Matrix4d ();
		m.m33 = 1;
		Math2.invertAffine (t, m);
		transforms.set (id, m);
	}

	private long nextProgressTime = 0;

	private int addVolume (Volume v)
	{
		int id = volumeCount++;
		v.setId (id);
		(infinite ? infiniteVolumes : volumes).add (v);
		interiors.set (id, interior);
		if ((volumeCount % 50) == 0)
		{
			long t = System.currentTimeMillis ();
			if (t >= nextProgressTime)
			{
				nextProgressTime = t + 200;
				setProgress (IMP3D.I18N.msg ("ray.constructing-geometry",
					volumeCount),
					ProgressMonitor.INDETERMINATE_PROGRESS);
			}
		}
		addedVolume = v;
		if (v instanceof MeshVolume)
		{
			polyCount += ((MeshVolume) v).getPolygonCount ();
		}
		return id;
	}

	public OctreeUnion getOctree ()
	{
		return sceneVolume;
	}

	public Scene dup ()
	{
		try
		{
			SceneVisitor v = (SceneVisitor) clone ();
			v.sceneVolumeState = sceneOctree.createState ();
			return v;
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError (e);
		}
	}

	public de.grogra.ray.physics.Shader getShader (Volume v)
	{
		return shaders.get (v.getId ());
	}

	public Interior getInterior (Volume v)
	{
		return interiors.get (v.getId ());
	}

	public int getLight (Volume v)
	{
		return volumeLights.get (v.getId ()) - 1;
	}

	public int getSensor (Volume v)
	{
		return volumeSensors.get (v.getId ()) - 1;
	}

	public void transform (Volume v, Tuple3d global, Tuple3d localOut)
	{
		Math2.transformPoint (transforms.get (v.getId ()), global, localOut);
	}

	@Override
	protected boolean isInVisibleLayer (Object o, boolean asNode)
	{
		if (visibleLayers == null)
		{
			return super.isInVisibleLayer (o, asNode);
		}
		int layer = state.getIntDefault (o, asNode, Attributes.LAYER, 0);
		return (layer < 0) || (layer >= visibleLayers.length)
			|| visibleLayers[layer];
	}

	/**
	 * Constructs a new <code>SceneVisitor</code> which traverses the given
	 * <code>graph</code> to obtain the complete geometry and light
	 * information and represent it as a {@link Scene}. Note that the traversal
	 * is already part of the constructor, so no additional method invocation
	 * is required to obtain the geometry. 
	 * 
	 * @param wb the workbench to use
	 * @param graph x
	 * @param epsilon only objects whose magnitude is larger than this value
	 * are considered 
	 * @param opts options for the construction of geometry
	 * @param view if the scene is used to render a 3D view, this has to be
	 * specified in this parameter.
	 * Otherwise <code>view</code> is <code>null</code>, this is interpreted
	 * such that a radiation model is to be computed
	 * @param visibleLayers layers which are visible for the scene visitor, or
	 * <code>null</code> if <code>view</code> shall be used to determine the
	 * visibility
	 * @param mapping if not <code>null</code>, mappings from graph objects to
	 * volumes are reported to this parameter
	 * @param spectrumFactory instance of spectrum to be used as factory
	 * (i.e., new spectra are allocated using <code>spectrumFactory.newInstance()</code>)
	 */
	public SceneVisitor (Workbench wb, Graph graph, float epsilon,
			Options opts, ViewConfig3D view, boolean[] visibleLayers,
			VolumeListener mapping, Spectrum spectrumFactory)
	{
		workbench = wb;
		this.spectrumFactory = spectrumFactory;
		this.view = view;
		this.visibleLayers = visibleLayers;
		this.mapping = mapping;
		startTime = System.currentTimeMillis ();
		wb.beginStatus (this);
		setProgress (IMP3D.I18N.msg ("ray.constructing-geometry", 0),
			ProgressMonitor.INDETERMINATE_PROGRESS);
		Matrix4d m = new Matrix4d ();
		m.setIdentity ();
		init (GraphState.current (graph), m, view, view != null);
		builder = new VolumeBuilder (
			new PolygonizationCache (state, Polygonization.COMPUTE_NORMALS
				| Polygonization.COMPUTE_UV, ((Number) opts.get ("flatness",
				new Float (1))).floatValue (), true), epsilon)
		{
			@Override
			protected void addVolume (Volume v, Matrix4d t, Shader s)
			{
				SceneVisitor.this.addVolume (v, t, s);
			}

			@Override
			protected Matrix4d getCurrentTransformation ()
			{
				return SceneVisitor.this.getCurrentTransformation ();
			}

			public Shader getCurrentShader ()
			{
				return SceneVisitor.this.getCurrentShader ();
			}

			public GraphState getRenderGraphState ()
			{
				return SceneVisitor.this.getGraphState ();
			}

		};
		nonCSGVolumes = volumes = new ArrayList<Volume> ();
		infiniteVolumes = new ArrayList<Volume> ();
		state.getGraph ().accept (null, this, null);
		volumeStack.clear ();
		volumeStack = null;
		interiorStack.clear ();
		interiorStack = null;
		setProgress (IMP3D.I18N.msg ("ray.constructing-octree"),
			ProgressMonitor.INDETERMINATE_PROGRESS);
		OctreeUnion v = new OctreeUnion ();
		v.volumes.addAll (volumes);
		volumes.clear ();
		volumes = null;
		v.initialize (Octree.suggestDepth (v.volumes.size ()), MIN_OBJ, new DefaultCellIterator ());
		for (int i = 0; i < infiniteVolumes.size (); i++)
		{
			v.addInfiniteVolume (infiniteVolumes.get (i));
		}
		sceneVolume = v;
		sceneOctree = v.getOctree ();
		sceneVolumeState = sceneOctree.createState ();
		bounds = new BoundingBox (sceneOctree.getMin (), sceneOctree.getMax ());
		if (!haveRealLight && (view != null))
		{
			lights.add (view.getDefaultLight (m));
			lightTransformations.add (m);
		}
		time = System.currentTimeMillis () - startTime;
		lights.toArray (lightsArray = new Light[lights.size ()]);
		lights = null;
		sensors.toArray (sensorsArray = new Sensor[sensors.size ()]);
		sensors = null;
		lightTransformations
			.toArray (lightTransformationsArray = new Matrix4d[lightTransformations
				.size ()]);
		inverseLightTransformationsArray = new Matrix4d[lightTransformationsArray.length];
		lightTransformations = null;
		sensorTransformations
			.toArray (sensorTransformationsArray = new Matrix4d[sensorTransformations
				.size ()]);
		inverseSensorTransformationsArray = new Matrix4d[sensorTransformationsArray.length];
		sensorTransformations = null;
		setProgress ("", ProgressMonitor.DONE_PROGRESS);
	}

	@Override
	protected Shader resolveShader (Shader shader)
	{
		return (shader instanceof AlgorithmSwitchShader) ? ((view != null) ? ((AlgorithmSwitchShader) shader)
			.getRaytracerShader ()
				: ((AlgorithmSwitchShader) shader).getRadiationShader ())
				: (shader instanceof ShaderRef) ? ((ShaderRef) shader)
					.resolve () : shader;
	}

	@Override
	protected void visitImpl (Object object, boolean asNode, Shader s, Path path)
	{
		Interior i = (Interior) state.getObjectDefault (object, asNode,
			Attributes.INTERIOR, interior);
		if (i != null)
		{
			interior = i;
		}
		else
		{
			i = interior;
		}
		CompoundVolume csg;
		boolean compl = false;
		switch (state.getIntDefault (object, asNode, Attributes.CSG_OPERATION,
			Attributes.CSG_NONE))
		{
			case Attributes.CSG_UNION:
				csg = new CSGUnion ();
				break;
			case Attributes.CSG_INTERSECTION:
				csg = new CSGIntersection ();
				break;
			case Attributes.CSG_DIFFERENCE:
				csg = new CSGDifference ();
				break;
			case Attributes.CSG_COMPLEMENT:
				compl = true;
				csg = new CSGUnion ();
				break;
			default:
				csg = null;
				break;
		}
		boolean notInCSG = volumes == nonCSGVolumes;
		addedVolume = null;
		infinite = state.getBooleanDefault (object, asNode,
			Attributes.TREATED_AS_INFINITE, false);
		Object shape;
		
	createVolume:
		if (csg != null)
		{
			addVolume (compl ? new CSGComplement (csg) : csg, getCurrentTransformation (), s);
			volumes = csg.volumes;
			csgCount++;
		}
		else if ((shape = state.getObjectDefault (object, asNode,
			Attributes.SHAPE, null)) instanceof Sky)
		{
			Matrix4d t = getCurrentTransformation ();
			SkySphere v = new SkySphere ();
			builder.setInvTransformation (v, t, 0);
			addVolume (v, t, s);
		}
		else if (shape instanceof SensorNode)
		{
			if (view != null)
			{
				break createVolume;
			}
			double radius = ((SensorNode) shape).getRadius ();
			if (Math.abs (radius) < builder.epsilon)
			{
				break createVolume;
			}
			Matrix4d t = getCurrentTransformation ();
			SensorDisc v = new SensorDisc ();
			builder.setInvTransformation (v, t, 0);
			radius = 1 / radius;
			v.scale (radius, radius, radius);
			addVolume (v, t, s);
			// SensorNode has no shader: rays will not be modified
			// when they go through the SensorDisc
			shaders.set (v.getId (), null);
			int index = sensors.size ();
			sensors.add ((SensorNode) shape);
			sensorTransformations.add (new Matrix4d (t));
			volumeSensors.set (v.getId (), index + 1);
		}
		else if (shape instanceof Renderable)
		{
			((Renderable) shape).draw (object, asNode, builder);
		}
		Light light = (Light) state.getObjectDefault (object, asNode,
			Attributes.LIGHT, null);
		if (light != null)
		{
			int index = lights.size ();
			lights.add (light);
			lightTransformations
				.add (new Matrix4d (getCurrentTransformation ()));
			if (addedVolume != null)
			{
				volumeLights.set (addedVolume.getId (), index + 1);
			}
			if (light.getLightType () != Light.NO_LIGHT)
			{
				haveRealLight = true;
			}
		}
		if (notInCSG && (mapping != null) && (addedVolume != null))
		{
			mapping.volumeCreated (object, asNode, addedVolume);
		}
	}

	@Override
	public Object visitInstanceEnter ()
	{
		if (mapping != null)
		{
			mapping.beginGroup (lastEntered, lastEnteredIsNode);
		}
		return super.visitInstanceEnter ();
	}

	@Override
	public boolean visitInstanceLeave (Object o)
	{
		if (mapping != null)
		{
			mapping.endGroup ();
		}
		return super.visitInstanceLeave (o);
	}

	@Override
	protected void visitEnterImpl (Object object, boolean asNode, Path path)
	{
		interiorStack.push (interior);
		volumeStack.push (volumes);
		super.visitEnterImpl (object, asNode, path);
	}

	@Override
	protected void visitLeaveImpl (Object object, boolean asNode, Path path)
	{
		super.visitLeaveImpl (object, asNode, path);
		volumes = volumeStack.pop ();
		interior = interiorStack.pop ();
	}


	public static int MIN_OBJ = 2;

	public Light[] getLights ()
	{
		return lightsArray;
	}

	public Sensor[] getSensors ()
	{
		return sensorsArray;
	}

	public int getStamp ()
	{
		return getGraphState ().getGraph ().getStamp ();
	}
	
	public String getUniqueName() 
	{
		return workbench.getRegistry().getFileSystemName();
	}

	public Object getGraph ()
	{
		return getGraphState ().getGraph ();
	}

	public Matrix4d getLightTransformation (int light)
	{
		return lightTransformationsArray[light];
	}

	public Matrix4d getInverseLightTransformation (int light)
	{
		synchronized (inverseLightTransformationsArray)
		{
			Matrix4d m = inverseLightTransformationsArray[light];
			if (m == null)
			{
				m = new Matrix4d ();
				m.m33 = 1;
				Math2.invertAffine (lightTransformationsArray[light], m);
				inverseLightTransformationsArray[light] = m;
			}
			return m;
		}
	}

	public Matrix4d getSensorTransformation (int sensor)
	{
		return sensorTransformationsArray[sensor];
	}

	public Matrix4d getInverseSensorTransformation (int sensor)
	{
		synchronized (inverseSensorTransformationsArray)
		{
			Matrix4d m = inverseSensorTransformationsArray[sensor];
			if (m == null)
			{
				m = new Matrix4d ();
				m.m33 = 1;
				Math2.invertAffine (sensorTransformationsArray[sensor], m);
				inverseSensorTransformationsArray[sensor] = m;
			}
			return m;
		}
	}

	public boolean computeIntersections (Line ray, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		return sceneOctree.computeIntersections (ray, which, list,
			excludeStart, excludeEnd, sceneVolumeState);
	}

	public BoundingBox getBoundingBox ()
	{
		return bounds;
	}

	public void appendStatistics (StringBuffer stats)
	{
		stats.append (IMP3D.I18N.msg ("ray.scene-statistics", new Object[] {
				(int) (time / 60000),
				(time % 60000) * 0.001f,
				volumeCount - csgCount, csgCount,
				polyCount, getLights ().length,
				sceneOctree.getDepth (),
				sceneOctree.getCellCount ()}));
	}

	public void dispose ()
	{
		lightsArray = null;
		lightTransformationsArray = null;
		inverseLightTransformationsArray = null;
		sensorsArray = null;
		sensorTransformationsArray = null;
		inverseSensorTransformationsArray = null;
		sceneOctree = null;
		sceneVolume = null;
		sceneVolumeState = null;
		shaders = null;
		interiors = null;
		transforms = null;
		volumeLights = null;
		volumeSensors = null;
		workbench = null;
		mapping = null;
	}

	public void setProgress (String text, float progress)
	{
		workbench.setStatus (this, text);
		if (progress < 0)
		{
			workbench.setIndeterminateProgress (this);
		}
		else if (progress == DONE_PROGRESS)
		{
			workbench.clearProgress (this);
		}
		else
		{
			workbench.setProgress (this, progress);
		}
	}

	public void showMessage (String message)
	{
		workbench.logGUIInfo (message);
	}

	public Spectrum createSpectrum ()
	{
		return spectrumFactory.newInstance ();
	}
}
