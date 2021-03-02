package de.grogra.ext.x3d.interfaces;

/**
 * Interface for nodes which need an extra call of their setValues() method.
 * 
 * @author Udo Bischof, Uwe Mannl
 *
 */
public interface Value {

	/**
	 * This method is called after reading the whole x3d file.
	 * Appearance/material to the groimp node is also already set.
	 */
	public void setValues();
	
}
