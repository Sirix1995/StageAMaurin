package de.grogra.ray2.radiosity.triangulation;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;

import net.goui.util.MTRandom;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.Scene;
import de.grogra.ray2.radiosity.GroupListBuilder;
import de.grogra.vecmath.geom.TransformableVolume;
import de.grogra.vecmath.geom.Volume;

/**
 * This class divides a volume into patches.
 * @author Ralf Kopsch
 */
public class Triangulizer {
	
	/**
	 * This method divides a volume into a list of patches.  
	 * @param builder the global builder list.
	 * @param scene the scene.
	 * @param v a volume.
	 * @throws TriangulationException thrown if, the given volume can not be divided.
	 */
	public static void triangulize(GroupListBuilder builder, Scene scene, Volume v) throws TriangulationException {
		if (v instanceof de.grogra.vecmath.geom.Cube) {
			Cube.computePatches(builder, (TransformableVolume) v, scene.getShader(v));
			return;
		}
		if (v instanceof de.grogra.vecmath.geom.Sphere) {
			Sphere.computePatches(builder, (TransformableVolume) v, scene.getShader(v));
			return;
		}
		if (v instanceof de.grogra.vecmath.geom.Cylinder) {
			Cylinder.computePatches(builder, (TransformableVolume) v, scene.getShader(v));
			return;
		}
		if (v instanceof de.grogra.vecmath.geom.Frustum) {
			Frustum.computePatches(builder, (TransformableVolume) v, scene.getShader(v));
			return;
		}
		if (v instanceof de.grogra.vecmath.geom.Cone) {
			Cone.computePatches(builder, (TransformableVolume) v, scene.getShader(v));
			return;
		}
		if (v instanceof de.grogra.vecmath.geom.Square) {
			Color3f emitted = new Color3f(); 
			// check if Square is an Arealight
			int lightindex = scene.getLight(v);
			if (lightindex != -1 ) {
				Light light = scene.getLights()[lightindex];
				// complute color
				RayList list = new RayList(1);
				Environment lightEnvironments = new Environment (scene.getBoundingBox (), scene.createSpectrum(), 0);
				lightEnvironments.localToGlobal.set(scene.getLightTransformation(lightindex));
				light.generateRandomOrigins(lightEnvironments, list, new MTRandom());
				emitted.x = list.rays[0].color.x;
				emitted.y = list.rays[0].color.y;
				emitted.z = list.rays[0].color.z;
			}
			Square.computePatches(builder, (TransformableVolume) v, scene.getShader(v), emitted);
			return;
		}
		throw new TriangulationException("Triangulation not possible for this object: " + v);
	}
	
	/**
	 * This method divides a Light into patches.
	 * @param builder the global builder list.
	 * @param scene the scene
	 * @param lightIndex the light index.
	 * @throws TriangulationException thrown if, a volume can not be divided.
	 */
	public static void triangulize(GroupListBuilder builder, Scene scene, int lightIndex) throws TriangulationException {
		Light light = scene.getLights()[lightIndex];
		if ("de.grogra.imp3d.objects.Parallelogram".equals(light.getClass().getName())) {
			// do nothing, because a Parallelogram is also a Square Volume
			return;
		}
		if ("de.grogra.imp3d.objects.PointLight".equals(light.getClass().getName())) {
			Matrix4d localToGlobal = scene.getLightTransformation(lightIndex);
			Point3d origin = new Point3d(localToGlobal.m03, localToGlobal.m13, localToGlobal.m23);
			
			Environment lightEnvironments = new Environment (scene.getBoundingBox (), scene.createSpectrum(), 0);
			lightEnvironments.localToGlobal.set(localToGlobal);
			RayList list = new RayList(1);
			light.generateRandomOrigins(lightEnvironments, list, new MTRandom());
			
			PointLight.computePatches(builder, origin, list.rays[0].color);
			return;
		}
		throw new TriangulationException("Triangulation not possible for this light: " + light);
	}
}
