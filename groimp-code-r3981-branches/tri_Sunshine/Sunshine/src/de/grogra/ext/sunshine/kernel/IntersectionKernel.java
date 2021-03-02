package de.grogra.ext.sunshine.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;
import static javax.media.opengl.GL.GL_TEXTURE3;
import static javax.media.opengl.GL.GL_TEXTURE4;
import static javax.media.opengl.GL.GL_TEXTURE5;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;


public class IntersectionKernel extends Kernel
{
	private int[] sceneTexture;
	private int[] kdTexture;	
	private boolean isShadowTest;
	private int lightCount;

	public IntersectionKernel(String name, GLAutoDrawable drawable,	int[] sceneTexture, int[] kdTexture) 
	{
		super(name, drawable);
		
		this.sceneTexture 	= sceneTexture;
		this.kdTexture 		= kdTexture;
		
		isShadowTest = false;
	} // constructor
	
	
	public void setLightPass(int lightCount)
	{
		this.lightCount = lightCount;
		isShadowTest = true;
	}
	
	public void reset()
	{
		isShadowTest = false;
	}
	
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
		
	/*	int loopStartLoc	= getUniformLocation("loopStart", drawable);	
		int loopStopLoc		= getUniformLocation("loopStop", drawable);	
		int lastCycleLoc	= getUniformLocation("lastCycle", drawable);
		int isSTLoc			= getUniformLocation("isShadowTest", drawable);
		int lightPassLoc	= getUniformLocation("lightPass", drawable);*/
		
		//activate the kernel
		useProgram(drawable);
		
		/*	setUniformInt(loopStartLoc, loopStart, drawable);
			setUniformInt(loopStopLoc, loopStop, drawable);
			setUniformInt(lastCycleLoc, lastCycle ? 1 : 0, drawable);
			setUniformInt(isSTLoc, isShadowTest ? 1 : 0, drawable);
			setUniformInt(lightPassLoc, lightCount, drawable);*/
			
			setUniformParameters(drawable);
			
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
			
			gl.glActiveTexture(GL_TEXTURE5);
			gl.glBindTexture(texTarget, kdTexture[0]);
			setUniformTex(kdLoc, 5, drawable);	
			
			gl.glActiveTexture(GL.GL_TEXTURE6);
			gl.glBindTexture(texTarget, imageTexture[0]);
			setUniformTex(oi0Loc, 6, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE7);
			gl.glBindTexture(texTarget, imageTexture[1]);
			setUniformTex(oi1Loc, 7, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE8);
			gl.glBindTexture(texTarget, imageTexture[2]);
			setUniformTex(oi2Loc, 8, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE9);
			gl.glBindTexture(texTarget, imageTexture[3]);
			setUniformTex(oi3Loc, 9, drawable);
						
			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	}
	


}
