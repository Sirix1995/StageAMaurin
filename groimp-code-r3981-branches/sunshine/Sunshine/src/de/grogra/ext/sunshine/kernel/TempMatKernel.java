package de.grogra.ext.sunshine.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import de.grogra.ext.sunshine.SunshineSceneVisitor;

public class TempMatKernel extends Kernel {
	
	private int[] sceneTexture;
	
	public TempMatKernel(String name, GLAutoDrawable drawable, int[] sceneTexture, int tileSize) 
	{
		super(name, drawable, tileSize);
		
		this.sceneTexture = sceneTexture;
		
	} // constructor
		
	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int i)
	{
		GL gl = drawable.getGL();
		
		int a0Loc	 		= getUniformLocation("a0", drawable);
		int a1Loc 			= getUniformLocation("a1", drawable);
		int sceneLoc		= getUniformLocation("scene", drawable);

		//activate the kernel
		useProgram(drawable);		
			setUniformParameters(drawable);

			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, inputTextureA[0]);
			setUniformTex(a0Loc, 0, drawable);
			
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(texTarget, inputTextureA[1]);
			setUniformTex(a1Loc, 1, drawable);
			
			gl.glActiveTexture(GL_TEXTURE2);
			gl.glBindTexture(texTarget, sceneTexture[0]);
			setUniformTex(sceneLoc, 2, drawable);
							
			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	}
	
	public void loadSource(GLAutoDrawable drawable, 
			SunshineSceneVisitor monitor, String intermediates)
	{
		
	}
}
