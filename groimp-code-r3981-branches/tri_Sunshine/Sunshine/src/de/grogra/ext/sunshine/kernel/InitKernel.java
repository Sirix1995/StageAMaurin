package de.grogra.ext.sunshine.kernel;

import javax.media.opengl.GLAutoDrawable;

/**
 * this class initialize the textures
 * @author Mankmil
 *
 */
public class InitKernel extends Kernel
{

	public InitKernel(String name, GLAutoDrawable drawable)
	{
		super(name, drawable);
	} //Constructor
	
	
	public void execute(GLAutoDrawable drawable, int px, int py, int i)
	{
		useProgram(drawable);
		drawQuad(drawable, px, py);
		stopProgram(drawable);
	}
	
	
} //class
