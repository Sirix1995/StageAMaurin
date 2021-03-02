package de.grogra.ext.x3d.interfaces;

import de.grogra.ext.x3d.objects.X3DTextureCoordinate;

/**
 * Interface for nodes with a texture coordinate node.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public interface TextureCoordinate {
	
	public X3DTextureCoordinate getTexCoord();
	public void setTexCoord(X3DTextureCoordinate texCoord);
	
}
