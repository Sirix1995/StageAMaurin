package de.grogra.imp3d.shading;

/**
 * This interface is helper to mark other Shaders instead of Phong as an owner
 * of a ColorMapNode. Its necessary for displaying different textures. 
 * 
 * @author adgen
 *
 */
public interface ColorMapNodeProperty {
	
	ColorMapNode getImageChannel();

}
