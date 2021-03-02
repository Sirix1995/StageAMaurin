package de.grogra.ext.sunshine.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.SunshineSceneVisitor;

/**
 * this class initialize the textures
 * @author Thomas
 *
 */
public class InitKernel extends Kernel
{	
	protected int seedID;
	private int size;
	
	public InitKernel(String name, GLAutoDrawable drawable, int size)
	{
		super(name, drawable, size);
		this.size = size;
	} //Constructor
	
	public void setSeedTex(int[] seedID)
	{
		this.seedID = seedID[0];
	}
	
	@Override	
	public void execute(GLAutoDrawable drawable, int px, int py, int sample)
	{
		GL gl = drawable.getGL();
		
		// get location for the uniform variables
		int rndLoc 	= getUniformLocation("RND_TEX", drawable);

		int pxLoc 	= getUniformLocation("px", drawable);
		int pyLoc 	= getUniformLocation("py", drawable);
		int wLoc	= getUniformLocation("width", drawable);
		
		useProgram(drawable);
				
			setUniformInt(pxLoc, px, drawable);
			setUniformInt(pyLoc, py, drawable);
			setUniformInt(wLoc, size, drawable);
		
			// bind the seed texture to retrieve the random number
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, seedID);
			setUniformTex(rndLoc, 0, drawable);
		
			drawQuad(drawable, px, py);
		stopProgram(drawable);
	}
	
	
	public void loadSource(GLAutoDrawable drawable, SunshineSceneVisitor monitor, 
			String intermediates)
	{
	}
	
	
} //class
