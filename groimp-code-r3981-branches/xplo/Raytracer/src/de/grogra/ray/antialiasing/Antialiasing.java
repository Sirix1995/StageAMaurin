package de.grogra.ray.antialiasing;

import javax.vecmath.Color4f;
import de.grogra.ray.RTCamera;
import de.grogra.ray.tracing.RayProcessor;


/**
 * A implementation of this interface encapsulates a single antialising methode 
 * that is based on prefiltering.
 * 
 * @author Micha
 */
public interface Antialiasing {

	
	/**
	 * Initializes the antialiasing method and sets the camera and 
	 * ray processor object that has to be used.
	 * 
	 * @param camera sets the camera object that has to be used for the 
	 *        antialiasing class.
	 * @param processor sets the ray processor object that has to be used 
	 *        for the antialiasing class.
	 */
	public void initialize(RTCamera camera, RayProcessor processor);
	
	
	/**
	 * This method has to return a color value determined for a given pixel 
	 * frustum. A pixel frustum is a sub frustum of the view frustum that 
	 * encloses exactly a special pixel of the image. The pixel frustum is 
	 * indirectly described by describing the related pixel.
	 * 
	 * @param x Describes the relative x position of the top left corner of 
	 *          the pixel. It can range from -1 to +1.
	 * @param y Describes the relative y position of the top left corner of 
	 *          the pixel. It can range from -1 to +1.
	 * @param width Describes the relative width of the of the pixel. 
	 *              It can range from 0 to +2.
	 * @param height Describes the relative height of the of the pixel. 
	 *               It can range from 0 to +2.
	 * @param color The determined color for the pixel frustum will be
	 *              saved in this parameter. So this parameter will
	 *              be changed!
	 */
	public void getColorFromFrustum(double x, double y, 
			double width, double height, Color4f color); 
	
}
