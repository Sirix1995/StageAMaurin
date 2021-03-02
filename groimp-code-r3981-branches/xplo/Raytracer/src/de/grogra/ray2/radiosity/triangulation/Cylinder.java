package de.grogra.ray2.radiosity.triangulation;

import de.grogra.ray.physics.Shader;
import de.grogra.ray2.radiosity.GroupListBuilder;
import de.grogra.ray2.radiosity.Vector3d;
import de.grogra.vecmath.geom.TransformableVolume;

/**
 * This class divides a Cylinder into triangle patches.
 * @author Ralf Kopsch
 */
public abstract class Cylinder {

	/**
	 * Divides the volume into patches and use the builder to store it.
	 * @param builder saves the created patches.
	 * @param cylinder the volume.
	 * @param shader the material shader.
	 */
	public static void computePatches(GroupListBuilder builder, TransformableVolume cylinder, Shader shader) {
		final int uCount = 20;
		float r = 1;
		Vector3d pos = new Vector3d();
		
		Vector3d[] world = new Vector3d[(uCount * 2) + 2];
		for (int i = 0; i < world.length; i++) {
			world[i] = new Vector3d();
		}

		pos.set (0, 0, -1);
		cylinder.invTransformPoint(pos, world[0]);
		pos.set (0, 0, 1);
		cylinder.invTransformPoint(pos, world[1]);
		int pointIndex = 2;
		for (int u = 0; u < uCount; u++) {
			float phi = (float) (Math.PI * 2 * (float) u / (float) uCount);
			float cosPhi = (float) Math.cos (phi);
			float sinPhi = (float) Math.sin (phi);

			pos.set(cosPhi * r, sinPhi * r, -1);
			cylinder.invTransformPoint(pos, world[pointIndex]);
			pos.set(cosPhi * r, sinPhi * r, 1);
			cylinder.invTransformPoint(pos, world[pointIndex + 1]);
			pointIndex += 2;
		}
		
		for (int u = 0; u < uCount; u++) {
			// bottom face
			builder.add(shader, new Vector3d[] {world[0], world[(u + 1) * 2], 
					world[(u + 1) * 2 + 2 * (uCount - u > 1 ? 1 : 1 - uCount)]});
			// top face
			builder.add(shader, new Vector3d[] {world[1], /*world[(u + 1) * 2 + 1],*/
					world[(u + 1) * 2 + 1 + 2 * (uCount - u > 1 ? 1 : 1 - uCount)], world[(u + 1) * 2 + 1]});
			// mantle face
			builder.add(shader, new Vector3d[] {world[(u + 1) * 2], world[(u + 1) * 2 + 1],
					world[(u + 1) * 2 + 3 + 2 * (uCount - u > 1 ? 0 : 0 - uCount)],
					world[(u + 1) * 2 + 2 + 2 * (uCount - u > 1 ? 0 : 0 - uCount)]});
		}
	}
}
