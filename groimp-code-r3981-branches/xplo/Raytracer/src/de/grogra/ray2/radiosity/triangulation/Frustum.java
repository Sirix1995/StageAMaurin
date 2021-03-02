package de.grogra.ray2.radiosity.triangulation;

import de.grogra.ray.physics.Shader;
import de.grogra.ray2.radiosity.GroupListBuilder;
import de.grogra.ray2.radiosity.Vector3d;
import de.grogra.vecmath.geom.TransformableVolume;

/**
 * This class divides a Frustum into triangle patches.
 * @author Ralf Kopsch
 */
public abstract class Frustum {

	/**
	 * Divides the volume into patches and use the builder to store it.
	 * @param builder saves the created patches.
	 * @param frustrum the volume.
	 * @param shader the material shader.
	 */
	public static void computePatches(GroupListBuilder builder, TransformableVolume frustrum, Shader shader) {
		final int uCount = 20;	
		float br = 1;
		float tr = 2;
		
		Vector3d pos = new Vector3d();
		Vector3d[] world = new Vector3d[(uCount * 2) + 2];
		for (int i = 0; i < world.length; i++) {
			world[i] = new Vector3d();
		}
		
		pos.set (0, 0, 1);
		frustrum.invTransformPoint(pos, world[0]);
		pos.set (0, 0, 2);
		frustrum.invTransformPoint(pos, world[1]);
		
		int pointIndex = 2;	
		for (int u = 0; u < uCount; u++) {
			float phi = (float) (Math.PI * 2 * (float) u / (float) uCount);
			float cosPhi = (float) Math.cos (phi);
			float sinPhi = (float) Math.sin (phi);

			pos.set(br * cosPhi, br * sinPhi, 1);
			frustrum.invTransformPoint(pos, world[pointIndex]);
			
			pos.set(tr * cosPhi, tr * sinPhi, 2);
			frustrum.invTransformPoint(pos, world[pointIndex + 1]);
			pointIndex += 2;
		}
		
		for (int u = 0; u < uCount; u++) {
			// top face
			builder.add(shader, new Vector3d[] {world[0], world[(u + 1) * 2], 
					world[(u + 1) * 2 + 2 * (uCount - u > 1 ? 1 : 1 - uCount)]});
			
			// bottom face
			builder.add(shader, new Vector3d[] {world[1], /*world[(u + 1) * 2 + 1],*/
					world[(u + 1) * 2 + 1 + 2 * (uCount - u > 1 ? 1 : 1 - uCount)], world[(u + 1) * 2 + 1]});
			
			// mantle face
			builder.add(shader, new Vector3d[] {world[(u + 1) * 2], world[(u + 1) * 2 + 1],
					world[(u + 1) * 2 + 3 + 2 * (uCount - u > 1 ? 0 : 0 - uCount)],
					world[(u + 1) * 2 + 2 + 2 * (uCount - u > 1 ? 0 : 0 - uCount)]});
		}
	}
}
