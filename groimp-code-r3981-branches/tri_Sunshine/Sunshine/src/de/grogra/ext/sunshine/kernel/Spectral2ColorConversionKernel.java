package de.grogra.ext.sunshine.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

public class Spectral2ColorConversionKernel  extends Kernel
{
	int[] colorMatchTex;

	public Spectral2ColorConversionKernel(String name, GLAutoDrawable drawable, int[] texture)
	{
		super(name, drawable);
		
		colorMatchTex = texture;
	} //Constructor
	
	
	public void execute(GLAutoDrawable drawable, int px, int py, int i)
	{
		
		GL gl = drawable.getGL();
		
		int colMatLoc = getUniformLocation("matchTable", drawable);
		
		useProgram(drawable);
		
			// set the values for the uniform variables
			setUniformParameters(drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE4);
			gl.glBindTexture(texTarget, colorMatchTex[0]);
			setUniformTex(colMatLoc, 4, drawable);
			
			drawQuad(drawable, px, py);
		stopProgram(drawable);
	}
	
	
} //class
