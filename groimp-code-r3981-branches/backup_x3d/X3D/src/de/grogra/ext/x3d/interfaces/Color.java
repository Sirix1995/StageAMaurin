package de.grogra.ext.x3d.interfaces;

import de.grogra.ext.x3d.objects.X3DColor;

/**
 * Interface for nodes with a color node.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public interface Color {

	public X3DColor getColor();
	public void setColor(X3DColor color);
	
}
