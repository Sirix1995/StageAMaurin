package de.grogra.ext.sunshine.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;
import static javax.media.opengl.GL.GL_TEXTURE3;
import static javax.media.opengl.GL.GL_TEXTURE4;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.SunshineSceneVisitor;


public class ShadingKernel extends Kernel
{
	private int[] sceneTexture;
	private int[] texTexture;
	private int[] kdTexture;
	private int[] tempMatTexture;
	
	private boolean hasImage;	

	public ShadingKernel(String name, GLAutoDrawable drawable,
			int[] sceneTexture, int[] texTexture, int[] kdTexture, boolean img,
			int tileSize, int[] tempMatTexture) 
	{
		super(name, drawable, tileSize);

		this.sceneTexture = sceneTexture;
		this.texTexture = texTexture;
		this.kdTexture = kdTexture;
		
		this.tempMatTexture = tempMatTexture;
		
		hasImage = img;
		
	} // constructor
		
	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int i)
	{
		GL gl = drawable.getGL();
		
		int a0Loc	 		= getUniformLocation("a0", drawable);
		int a1Loc 			= getUniformLocation("a1", drawable);
		int a2Loc 			= getUniformLocation("a2", drawable);
		int a3Loc 			= getUniformLocation("a3", drawable);
		int sceneLoc		= getUniformLocation("scene", drawable);
		int kdLoc			= getUniformLocation("kd_tree", drawable);		
		
		int oi0Loc	 		= getUniformLocation("outputImage0", drawable);
		int oi1Loc 			= getUniformLocation("outputImage1", drawable);
		int oi2Loc 			= getUniformLocation("outputImage2", drawable);
		int oi3Loc 			= getUniformLocation("outputImage3", drawable);
		
		int uvLOC			= getUniformLocation("tempMatTex", drawable);
				
		int texLoc = 0;
		if(hasImage)
			texLoc		= getUniformLocation("texTexture", drawable);

		//activate the kernel
		useProgram(drawable);		
		/*	setUniformInt(loopStartLoc, loopStart, drawable);
			setUniformInt(loopStopLoc, loopStop, drawable);
			setUniformInt(lastCycleLoc, lastCycle ? 1 : 0, drawable);*/
					
			setUniformParameters(drawable);
		
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, inputTextureA[0]);
			setUniformTex(a0Loc, 0, drawable);
			
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(texTarget, inputTextureA[1]);
			setUniformTex(a1Loc, 1, drawable);
			
			gl.glActiveTexture(GL_TEXTURE2);
			gl.glBindTexture(texTarget, inputTextureA[2]);
			setUniformTex(a2Loc, 2, drawable);
			
			gl.glActiveTexture(GL_TEXTURE3);
			gl.glBindTexture(texTarget, inputTextureA[3]);
			setUniformTex(a3Loc, 3, drawable);

			
			gl.glActiveTexture(GL_TEXTURE4);
			gl.glBindTexture(texTarget, sceneTexture[0]);
			setUniformTex(sceneLoc, 4, drawable);
			
			
			gl.glActiveTexture(GL.GL_TEXTURE5);
			gl.glBindTexture(texTarget, texTexture[0]);
			if(hasImage)
				setUniformTex(texLoc, 5, drawable);
			
			
			gl.glActiveTexture(GL.GL_TEXTURE6);
			gl.glBindTexture(texTarget, kdTexture[0]);
			setUniformTex(kdLoc, 6, drawable);
			
			
			gl.glActiveTexture(GL.GL_TEXTURE7);
			gl.glBindTexture(texTarget, inputTextureB[0]);
			setUniformTex(oi0Loc, 7, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE8);
			gl.glBindTexture(texTarget, inputTextureB[1]);
			setUniformTex(oi1Loc, 8, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE9);
			gl.glBindTexture(texTarget, inputTextureB[2]);
			setUniformTex(oi2Loc, 9, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE10);
			gl.glBindTexture(texTarget, inputTextureB[3]);
			setUniformTex(oi3Loc, 10, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE11);
			gl.glBindTexture(texTarget, tempMatTexture[0]);
			setUniformTex(uvLOC, 11, drawable);
			
						
			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	}
	
	
	public void loadSource(GLAutoDrawable drawable, 
			SunshineSceneVisitor monitor, String intermediates)
	{
		
	}

}
