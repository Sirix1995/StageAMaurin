package de.grogra.ray2.tracing;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;

import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray2.Scene;
import de.grogra.ray2.light.DefaultLightProcessor;
import de.grogra.ray2.radiosity.GroupListBuilder;
import de.grogra.ray2.radiosity.HemiCube;
import de.grogra.ray2.radiosity.MyMeshVolume;
import de.grogra.ray2.radiosity.OptionReader;
import de.grogra.ray2.radiosity.PatchGroup;
import de.grogra.ray2.radiosity.RadiosityAlgorithm;
import de.grogra.ray2.radiosity.triangulation.TriangulationException;
import de.grogra.ray2.radiosity.triangulation.Triangulizer;
import de.grogra.vecmath.geom.DefaultCellIterator;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.Line;
import de.grogra.vecmath.geom.OctreeUnion;
import de.grogra.vecmath.geom.Volume;

/**
 * This is a Radiosity Processor.
 * It divides the scene into triangle patches and calculates the radiosity color for each.
 * @author Ralf Kopsch
 */
public class Radiosity extends RayProcessorBase {
	private static final int OCTREEMAXDEPTH   = 5;
	private static final int OCTREEMINOBJECTS = 2;
	
	private boolean hemiCubeCreated = false;
	private boolean radiosityCalc = false;
	private Vector<PatchGroup> groups = new Vector<PatchGroup>();
	private Tuple3d sumColor = new Point3d ();
	
	private static OctreeUnion octree = new OctreeUnion();
	private static int groupSize = -1;
	private static int iterations = -1;

	/**
	 * Creates a new Radiosity Processor.
	 */
	public Radiosity() {
		DefaultLightProcessor p = new DefaultLightProcessor();
		setLightProcessor(p);
	}

	@Override
	public void getColorFromRay(Line ray, Spectrum resp, Color4f color, Random random) {
		primaryCount++;
		ilist.clear ();
		color.set (0, 0, 0, 0);
		
		// OctreeUnion
		Radiosity.octree.computeIntersections(ray, Intersection.CLOSEST, ilist, null, null);
		if (ilist.size == 0) {
			return;
		}
		sumColor.set (0, 0, 0);
		
		// calculate color recursively
		float transparency = traceRay (1, ilist.elements[0], resp, sumColor,
			locals.nextReflected (), random);

		sumColor.scale (getBrightness ());
		color.x = (float) sumColor.x;
		color.y = (float) sumColor.y;
		color.z = (float) sumColor.z;
		color.w = (transparency < 1) ? 1 - transparency : 0;
	}

	
	@Override
	public void initialize(PixelwiseRenderer renderer, Scene scene) {
		
		// read Options
		OptionReader reader = new OptionReader(renderer);

		// create a new Hemicube if needed
		if( reader.isHemicubeCalcNeeded() ) {
			HemiCube.init(reader.getCubeWidth());
			this.hemiCubeCreated = true;
		}
		
		// compute root patches if needed
		if (reader.isRadiosityCalcNeeded(scene)) {
			HemiCube.setWorldWide(reader.getHemiWorldWide());
			this.groups.clear();
			Radiosity.octree = new OctreeUnion();
			computePatches(scene);
			RadiosityAlgorithm alg = new RadiosityAlgorithm(renderer);
			alg.calculateScene(this.groups, reader.getSubdivthreshold(), reader.getMaxsubdivdepth(), reader.getThreadCount());
			createSceneOctree();
			this.radiosityCalc = true;
			Radiosity.groupSize = groups.size();
			Radiosity.iterations = alg.getSteps();
		}
		reader.calcFinished(scene);
		super.initialize(renderer, scene);
	}


	private void computePatches(Scene scene) {
		ArrayList<Volume> volumes = scene.getOctree().volumes;
	
		GroupListBuilder builder = new GroupListBuilder();
		for (Volume v: volumes) {
			try {
				Triangulizer.triangulize(builder, scene, v);
			} catch (TriangulationException e) {
				e.printStackTrace();
			}
		}

		Light[] lights = scene.getLights();
		for (int lightIndex = 0; lightIndex < lights.length; lightIndex++) {
			try {
				Triangulizer.triangulize(builder, scene, lightIndex);
			} catch (TriangulationException e) {
				e.printStackTrace();
			}
		}
		this.groups = builder.getGroups();
	}

	private void createSceneOctree() {
		for (PatchGroup pg: this.groups) {
			MyMeshVolume[] mv = pg.createMesh();

			if (mv != null) {
				for (int i = 0; i < mv.length; i++) {
					Radiosity.octree.volumes.add(mv[i]);
				}
			}
		}
		Radiosity.octree.initialize(OCTREEMAXDEPTH, OCTREEMINOBJECTS, new DefaultCellIterator());
	}
		
	
	@Override
	int getEnvironmentType() {
		return 0;
	}

	@Override
	float traceRay(int depth, Intersection desc, Spectrum weight,
			Tuple3d color, Locals loc, Random random) {
		MyMeshVolume mesh;
		
		if (desc.volume instanceof MyMeshVolume)  {
			mesh = (MyMeshVolume) desc.volume;
		} else {
			System.err.println("Radiosity.traceRay(): ERROR! no Mesh found");
			return 0;
		}
		color.set(mesh.getMeshColor().x, mesh.getMeshColor().y, mesh.getMeshColor().z);
		return 0;
	}

	@Override
	protected void appendStatisticsImpl(StringBuffer stats) {
		stats.append("Radiosity Statistics\n");
		stats.append("    HemiCube created        : " + (this.hemiCubeCreated ? "yes" : "no") + "\n");
		stats.append("    Radiosity calculated    : " + (this.radiosityCalc ? "yes" : "no") + "\n");
		stats.append("    Number of PatchGroups   : " + Radiosity.groupSize + "\n");
		stats.append("    Number of Triangles     : " + Radiosity.groupSize * 4 + "\n");
		stats.append("    Number of Iterations    : " + Radiosity.iterations + "\n");
	}
}
