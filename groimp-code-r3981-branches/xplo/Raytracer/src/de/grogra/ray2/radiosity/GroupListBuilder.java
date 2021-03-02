package de.grogra.ray2.radiosity;

import java.util.Vector;

import javax.vecmath.Color3f;

import de.grogra.ray.physics.Shader;

/**
 * This Class creates a List of PatchGroups.
 * Every object in the scene must be converted into patch groups.
 * 
 * @author Ralf Kopsch
 */
public class GroupListBuilder {
	
	private static final int maskR = 0xFF0000;
	private static final int shiftR = 16;
	private static final int maskG = 0xFF00;
	private static final int shiftG = 8;
	private static final int maskB = 0xFF;
	private static final int shiftB = 0;
	
	private Vector<PatchGroup> groups = new Vector<PatchGroup>();
	private int id = 0;
	
	/**
	 * Converts a list of vertices into a patch group. 
	 * @param shader a surface shader. 
	 * @param vertices a list of vertices.
	 * @param emitted the emitted color. 
	 */
	public void add(Shader shader, Vector3d[] vertices, Color3f emitted) {
		int col = shader.getAverageColor();
		float r = (float) ((col & maskR) >> shiftR) / 255;
		float g = (float) ((col & maskG) >> shiftG) / 255;
		float b = (float) ((col & maskB) >> shiftB) / 255;
		this.groups.add(new PatchGroup(vertices, id, emitted, new Color3f(r, g, b), new Color3f(), true));
		this.id++;
	}
	
	/**
	 * Converts a list of vertices into a patch group.
	 * Sets the emitted color to black.
	 * @param shader a surface shader.
	 * @param vertices a list of vertices.
	 */
	public void add(Shader shader, Vector3d[] vertices) {
		int col = shader.getAverageColor();
		float r = (float) ((col & maskR) >> shiftR) / 255;
		float g = (float) ((col & maskG) >> shiftG) / 255;
		float b = (float) ((col & maskB) >> shiftB) / 255;
		this.groups.add(new PatchGroup(vertices, id, new Color3f(), new Color3f(r, g, b), new Color3f(), true));
		this.id++;
	}	

	/**
	 * Converts a list of vertices into a patch group.
	 * @param vertices a list of vertices.
	 * @param emitted the emitted color. 
	 * @param visible if false, the patch group is not visible
	 */
	public void add(Vector3d[] vertices, Color3f emitted, boolean visible) {
		this.groups.add(new PatchGroup(vertices, id, emitted, new Color3f(), new Color3f(), visible));
		this.id++;
	}
	
	/**
	 * Returns a list of patch groups. 
	 * @return Returns a list of patch groups.
	 */
	public Vector<PatchGroup> getGroups() {
		return this.groups;
	}
}
