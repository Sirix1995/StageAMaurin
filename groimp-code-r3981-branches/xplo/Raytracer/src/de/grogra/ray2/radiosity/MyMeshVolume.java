package de.grogra.ray2.radiosity;

import javax.vecmath.Color3f;

import de.grogra.vecmath.geom.MeshVolume;

/**
 * This is an extension of MeshVolume.
 * It adds a color to a mesh volume.
 * @author Ralf Kopsch
 */
public class MyMeshVolume extends MeshVolume {
	/** the mesh color **/
	private Color3f meshColor;

	/**
	 * Returns the Mesh color.
	 * @return the Mesh color.
	 */
	public Color3f getMeshColor() {
		return meshColor;
	}

	/**
	 * Sets the Color for this Mesh.
	 * @param color the color to set.
	 */
	public void setMeshColor(Color3f color) {
		this.meshColor = color;
	}
}
