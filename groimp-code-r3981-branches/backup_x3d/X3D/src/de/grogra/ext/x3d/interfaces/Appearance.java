package de.grogra.ext.x3d.interfaces;

import de.grogra.ext.x3d.objects.X3DAppearance;

/**
 * Interface for nodes with an extra to handle appearance.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public interface Appearance {
	
	public X3DAppearance getAppearance();
	public void setAppearance(X3DAppearance appearance);
	
}
