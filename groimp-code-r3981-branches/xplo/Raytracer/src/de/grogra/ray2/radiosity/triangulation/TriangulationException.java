package de.grogra.ray2.radiosity.triangulation;

/**
 * This Exception is thrown, if an Volume can't be subdivided into triangles. 
 * 
 * @author Ralf Kopsch
 */
public class TriangulationException extends Exception {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new TriangulationException 
	 * @param str the error string
	 */
	public TriangulationException(String str) {
		super(str);
	}
}
