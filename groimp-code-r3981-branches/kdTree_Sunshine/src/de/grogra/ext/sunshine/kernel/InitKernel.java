package de.grogra.ext.sunshine.kernel;

import javax.media.opengl.GLAutoDrawable;

/**
 * this class initialize the textures
 * @author Mankmil
 *
 */
public class InitKernel extends Kernel
{

	public InitKernel(String name, GLAutoDrawable drawable, int tileWidth, int tileHeight)
	{
		super(name, drawable, tileWidth, tileHeight);
	} //Constructor
	
	
	public void execute(GLAutoDrawable drawable, int px, int py, int i, int[] array)
	{
		useProgram(drawable);
		drawQuad(drawable, width, height);
		stopProgram(drawable);
	}
	
	
} //class
