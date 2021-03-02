package de.grogra.ray2.radiosity.triangulation;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import de.grogra.ray.physics.Shader;
import de.grogra.ray2.radiosity.GroupListBuilder;
import de.grogra.ray2.radiosity.Vector3d;
import de.grogra.vecmath.geom.TransformableVolume;

/**
 * This class divides a Square into triangle patches.
 * @author Ralf Kopsch
 */
public abstract class Square {
	
	/**
	 * Divides the volume into patches and use the builder to store it.
	 * @param builder saves the created patches.
	 * @param square the volume.
	 * @param shader the material shader.
	 * @param emitted the emitted color.
	 */
	public static void computePatches(GroupListBuilder builder, TransformableVolume square, Shader shader, Color3f emitted) {
		Vector3d[] worldCorners = new Vector3d[4];
		Point3d p = new Point3d();
		Point3d q = new Point3d(); 
		p.set(0, 0, 0);
		square.invTransformPoint(p, q);
		worldCorners[0] = new Vector3d(q);
		p.set(0, 1, 0);
		square.invTransformPoint(p, q);
		worldCorners[1] = new Vector3d(q);
		p.set(1, 0, 0);
		square.invTransformPoint(p, q);
		worldCorners[2] = new Vector3d(q);
		p.set(1, 1, 0);
		square.invTransformPoint(p, q);
		worldCorners[3] = new Vector3d(q);
		
		builder.add(shader, new Vector3d[] {worldCorners[0], worldCorners[2], worldCorners[1]}, emitted);
		builder.add(shader, new Vector3d[] {worldCorners[2], worldCorners[3], worldCorners[1]}, emitted);

		
	}
}
