package de.grogra.ray2.radiosity.triangulation;

import de.grogra.ray.physics.Shader;
import de.grogra.ray2.radiosity.GroupListBuilder;
import de.grogra.ray2.radiosity.Vector3d;
import de.grogra.vecmath.geom.TransformableVolume;

/**
 * This class divides a Sphere into triangle patches.
 * @author Ralf Kopsch
 */
public abstract class Sphere {

	/**
	 * Divides the volume into patches and use the builder to store it.
	 * @param builder saves the created patches.
	 * @param sphere the volume.
	 * @param shader the material shader.
	 */
	public static void computePatches(GroupListBuilder builder, TransformableVolume sphere, Shader shader) {
		final int uCount = 20;
		final int vCount = 10;
		float r = 1;
		Vector3d pos = new Vector3d();
		Vector3d[] world = new Vector3d[(uCount * (vCount - 1)) + 2];
		for (int i = 0; i < world.length; i++) {
			world[i] = new Vector3d();
		}
		
		pos.set(0, 0, -r);
		sphere.invTransformPoint(pos, world[0]);
		int pointIndex = 1;
		for (int v = 1; v < vCount; v++) {
			float theta = (float) (Math.PI * ((float) v / (float) vCount - 0.5f));
			for (int u = 0; u < uCount; u++) {
				float phi = (float) (Math.PI * 2 * (float) u / (float) uCount);
				float cosPhi = (float) Math.cos (phi);
				float sinPhi = (float) Math.sin (phi);
				float cosTheta = (float) Math.cos (theta);
				float sinTheta = (float) Math.sin (theta);
				float x = r * cosPhi * cosTheta;
				float y = r * sinPhi * cosTheta;
				float z = r * sinTheta;
				pos.set(x, y, z);
				sphere.invTransformPoint(pos, world[pointIndex]);
				pointIndex++;
			}
		}
		pos.set(0, 0, r);
		sphere.invTransformPoint(pos, world[pointIndex]);

		pointIndex = 1;
		for (int v = 1; v < vCount; v++) {
			for (int u = 0; u < uCount; u++) {
				if (v == 1) {
					// top face
					builder.add(shader, new Vector3d[] {world[(vCount - 2) * uCount + 1 + u], world[(vCount - 1) * uCount + 1], 
							world[(vCount - 2) * uCount + 1 + (uCount - u > 1 ? u+1 : 0)]});

					// bottom face
					builder.add(shader, new Vector3d[] {world[0], world[u + 1], 
							world[u + 1 + (uCount - u > 1 ? 1 : 1 - uCount)]});
				} else {
					// mantle face
					builder.add(shader, new Vector3d[] {world[pointIndex - uCount], world[pointIndex], 
							world[pointIndex + (uCount - u > 1 ? 1 : 1 - uCount)], 
							world[pointIndex - uCount + (uCount - u > 1 ? 1 : 1 - uCount)]});					
				}
				pointIndex++;
			}
		}
	}
}
