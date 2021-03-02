package de.grogra.ray2.radiosity.triangulation;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import de.grogra.ray2.radiosity.GroupListBuilder;
import de.grogra.ray2.radiosity.Vector3d;

/**
 * This class divides a PointLight into triangle patches.
 * @author Ralf Kopsch
 */
public class PointLight {
//	private static double sidelength = 0.001d;
	private static double sidelength = 0.2d;
	
	private static final Point3d[] CORNERS = new Point3d[] {
		new Point3d (-sidelength, -sidelength, -sidelength),  // 0 Left, Front, Bottom 
		new Point3d (sidelength, -sidelength, -sidelength),   // 1 Rigth, Front, Bottom
		new Point3d (sidelength, -sidelength, sidelength),    // 2 Right, Front, Top
		new Point3d (sidelength, sidelength, sidelength),     // 3 Right, Back, Top
		new Point3d (-sidelength, sidelength, sidelength),    // 4 Left, Back, Top
		new Point3d (-sidelength, sidelength, -sidelength),   // 5 Left, Back, Bottom
		new Point3d (-sidelength, -sidelength, sidelength),   // 6 Left, Front, Top
		new Point3d (sidelength, sidelength, -sidelength)};   // 7 Right, Back, Bottom
	
	
	/**
	 * Divides the volume into patches and use the builder to store it.
	 * @param builder saves the created patches.
	 * @param middle the center of the point light.
	 * @param color the color of the point light.
	 */
	public static void computePatches(GroupListBuilder builder, Point3d middle, Color3f color) {
		Vector3d[] worldCorners = new Vector3d[CORNERS.length];
		
		for (int i = 0; i < CORNERS.length; i++) {
			Vector3d p = new Vector3d(middle);
			p.add(CORNERS[i]);
			worldCorners[i] = p;
		}
		Color3f emitted = new Color3f(color);
		
		// create Patchgroups
		builder.add(new Vector3d[] {worldCorners[0], worldCorners[2], worldCorners[1]}, emitted, false); // Front, Bottom
		builder.add(new Vector3d[] {worldCorners[0], worldCorners[6], worldCorners[2]}, emitted, false); // Front, Top
		builder.add(new Vector3d[] {worldCorners[1], worldCorners[2], worldCorners[7]}, emitted, false); // Right, Bottom
		builder.add(new Vector3d[] {worldCorners[7], worldCorners[2], worldCorners[3]}, emitted, false); // Rigth, Top
		builder.add(new Vector3d[] {worldCorners[6], worldCorners[4], worldCorners[2]}, emitted, false); // Top, Front 
		builder.add(new Vector3d[] {worldCorners[2], worldCorners[4], worldCorners[3]}, emitted, false); // Top, Back
		builder.add(new Vector3d[] {worldCorners[0], worldCorners[1], worldCorners[5]}, emitted, false); // Bottom, Front
		builder.add(new Vector3d[] {worldCorners[1], worldCorners[7], worldCorners[5]}, emitted, false); // Bottom, Back 
		builder.add(new Vector3d[] {worldCorners[5], worldCorners[4], worldCorners[6]}, emitted, false); // Left, Top 
		builder.add(new Vector3d[] {worldCorners[5], worldCorners[6], worldCorners[0]}, emitted, false); // Left, Bottom
		builder.add(new Vector3d[] {worldCorners[7], worldCorners[4], worldCorners[5]}, emitted, false); // Back, Bottom
		builder.add(new Vector3d[] {worldCorners[7], worldCorners[3], worldCorners[4]}, emitted, false); // Back, Top
	}
	
}
