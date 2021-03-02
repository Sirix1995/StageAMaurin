package de.grogra.ray2.radiosity.triangulation;

import javax.vecmath.Point3d;

import de.grogra.ray.physics.Shader;
import de.grogra.ray2.radiosity.GroupListBuilder;
import de.grogra.ray2.radiosity.Vector3d;
import de.grogra.vecmath.geom.TransformableVolume;

/**
 * This class divides a Cube into triangle patches.
 * @author Ralf Kopsch
 */
public abstract class Cube {
	
	private static final Point3d[] CORNERS = new Point3d[] {
		new Point3d (-1, -1, -1),  // Left, Front, Bottom 
		new Point3d (1, -1, -1),   // Rigth, Front, Bottom
		new Point3d (1, -1, 1),    // Right, Front, Top
		new Point3d (1, 1, 1),     // Right, Back, Top
		new Point3d (-1, 1, 1),    // Left, Back, Top
		new Point3d (-1, 1, -1),   // Left, Back, Bottom
		new Point3d (-1, -1, 1),   // Left, Front, Top
		new Point3d (1, 1, -1)};   // Right, Back, Bottom
	
	/**
	 * Divides the volume into patches and use the builder to store it.
	 * @param builder saves the created patches.
	 * @param cube the volume.
	 * @param shader the material shader.
	 */
	public static void computePatches(GroupListBuilder builder, TransformableVolume cube, Shader shader) {
		Vector3d[] worldCorners = new Vector3d[CORNERS.length];
		
		// calculate world coordinates
		for (int i = 0; i < CORNERS.length; i++) {
			Vector3d p = new Vector3d();
			cube.invTransformPoint(CORNERS[i], p);
			worldCorners[i] = p;
		}
		
		builder.add(shader, new Vector3d[] {worldCorners[0], worldCorners[2], worldCorners[1]}); // Front, Bottom
		builder.add(shader, new Vector3d[] {worldCorners[0], worldCorners[6], worldCorners[2]}); // Front, Top
		builder.add(shader, new Vector3d[] {worldCorners[1], worldCorners[2], worldCorners[7]}); // Right, Bottom
		builder.add(shader, new Vector3d[] {worldCorners[7], worldCorners[2], worldCorners[3]}); // Rigth, Top
		builder.add(shader, new Vector3d[] {worldCorners[6], worldCorners[4], worldCorners[2]}); // Top, Front 
		builder.add(shader, new Vector3d[] {worldCorners[2], worldCorners[4], worldCorners[3]}); // Top, Back
		builder.add(shader, new Vector3d[] {worldCorners[0], worldCorners[1], worldCorners[5]}); // Bottom, Front
		builder.add(shader, new Vector3d[] {worldCorners[1], worldCorners[7], worldCorners[5]}); // Bottom, Back 
		builder.add(shader, new Vector3d[] {worldCorners[5], worldCorners[4], worldCorners[6]}); // Left, Top 
		builder.add(shader, new Vector3d[] {worldCorners[5], worldCorners[6], worldCorners[0]}); // Left, Bottom
		builder.add(shader, new Vector3d[] {worldCorners[7], worldCorners[4], worldCorners[5]}); // Back, Bottom
		builder.add(shader, new Vector3d[] {worldCorners[7], worldCorners[3], worldCorners[4]}); // Back, Top
	}
}
