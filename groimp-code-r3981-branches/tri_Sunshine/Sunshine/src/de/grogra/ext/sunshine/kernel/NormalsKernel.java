package de.grogra.ext.sunshine.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;
import static javax.media.opengl.GL.GL_TEXTURE3;
import static javax.media.opengl.GL.GL_TEXTURE4;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;


public class NormalsKernel extends Kernel
{
	private int[] sceneTexture;
	private int[] texTexture;
	private boolean hasImage;

	public NormalsKernel(String name, GLAutoDrawable drawable,
			int[] sceneTexture, int[] texTexture, boolean img) 
	{
		super(name, drawable);

		this.sceneTexture = sceneTexture;
		this.texTexture = texTexture;
		hasImage = img;
	} // constructor
	
	
	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int i)
	{
		GL gl = drawable.getGL();
		
		int a0Loc	 	= getUniformLocation("a0", drawable);
		int a1Loc 		= getUniformLocation("a1", drawable);
		int a2Loc 		= getUniformLocation("a2", drawable);
		int a3Loc 		= getUniformLocation("a3", drawable);
		int sceneLoc	= getUniformLocation("scene", drawable);	

		int texLoc = 0;
		if(hasImage)
			texLoc		= getUniformLocation("tex", drawable);

		//activate the kernel
		useProgram(drawable);
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, traceTexture[0]);
			setUniformTex(a0Loc, 0, drawable);
			
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(texTarget, traceTexture[1]);
			setUniformTex(a1Loc, 1, drawable);
			
			gl.glActiveTexture(GL_TEXTURE2);
			gl.glBindTexture(texTarget, traceTexture[2]);
			setUniformTex(a2Loc, 2, drawable);
			
			gl.glActiveTexture(GL_TEXTURE3);
			gl.glBindTexture(texTarget, traceTexture[3]);
			setUniformTex(a3Loc, 3, drawable);

			
			gl.glActiveTexture(GL_TEXTURE4);
			gl.glBindTexture(texTarget, sceneTexture[0]);
			setUniformTex(sceneLoc, 4, drawable);
			
			
			gl.glActiveTexture(GL.GL_TEXTURE5);
			gl.glBindTexture(texTarget, texTexture[0]);
			if(hasImage)
				setUniformTex(texLoc, 5, drawable);

						
			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	}

}
