package de.grogra.ext.sunshine.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GL;
import de.grogra.ext.sunshine.SunshineSceneVisitor;




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
	 
	public GenRayKernel(String name, GLAutoDrawable drawable, int tileSize)
	{
		super(name, drawable, tileSize);
		
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
	public void execute(GLAutoDrawable drawable, int px, int py, int sample)
	{
		GL gl = drawable.getGL();
		
		// get location for the uniform variables
		int a0Loc = getUniformLocation("outputImage0", drawable);
		int a1Loc = getUniformLocation("outputImage1", drawable);
		int a2Loc = getUniformLocation("outputImage2", drawable);
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
			gl.glBindTexture(texTarget, inputTextureB[0]);
			setUniformTex(a0Loc, 0, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE1);
			gl.glBindTexture(texTarget, inputTextureB[1]);
			setUniformTex(a1Loc, 1, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE2);
			gl.glBindTexture(texTarget, inputTextureB[2]);
			setUniformTex(a2Loc, 2, drawable);
			
			// draw the quad with the given width and heigth
			drawQuad(drawable, px, py);
		stopProgram(drawable);
	} //execute
	
	public void loadSource(GLAutoDrawable drawable, 
			SunshineSceneVisitor monitor, String intermediates)
	{
		
	}
	

}
