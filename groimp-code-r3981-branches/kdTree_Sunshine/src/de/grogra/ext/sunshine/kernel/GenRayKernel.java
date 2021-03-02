package de.grogra.ext.sunshine.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE_RECTANGLE_NV;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GL;




public class GenRayKernel extends Kernel
{
	
	/**
	 * creates a fragment shader
	 * 
	 * @param name of the shader
	 * @param drawable 
	 * @param tileWidth width of tile
	 * @param tileHeight height of the tile
	 */
	 
	public GenRayKernel(String name, GLAutoDrawable drawable, int tileWidth, int tileHeight)
	{
		GL gl = drawable.getGL();
		
		program = gl.glCreateProgramObjectARB();
		shader 	= gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB);
		width 	= tileWidth;
		height	= tileHeight;
		
		this.name = name;
	} //Constructor
	
	
	/**
	 * 
	 * @param drawable
	 * @param width
	 * @param height
	 * @param px
	 * @param py
	 * @param fbo: render target
	 * @param inputTexture: input texture
	 */
	public void execute(GLAutoDrawable drawable, int px, int py, int sample, int[] inputTexture)
	{
		GL gl = drawable.getGL();
		
		// get location for the uniform variables
		int a0Loc = getUniformLocation("a0", drawable);
		int a2Loc = getUniformLocation("a2", drawable);
		int pxLoc = getUniformLocation("px", drawable);
		int pyLoc = getUniformLocation("py", drawable);
		int ssLoc = getUniformLocation("sample", drawable);
		
		// start the shader programm
		useProgram(drawable);
		
			// set the values for the uniform variables
			setUniformInt(pxLoc, px, drawable);
			setUniformInt(pyLoc, py, drawable);
			setUniformInt(ssLoc, sample, drawable);
		
			// bind the a_0 texture to the viewport size quad
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(GL_TEXTURE_RECTANGLE_NV, inputTexture[0]);
			setUniformTex(a0Loc, 0, drawable);
			
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(texTarget, inputTexture[2]);
			setUniformTex(a2Loc, 1, drawable);
			
			// draw the quad with the given width and heigth
			drawQuad(drawable, width, height);
		stopProgram(drawable);
	} //execute
	

}
