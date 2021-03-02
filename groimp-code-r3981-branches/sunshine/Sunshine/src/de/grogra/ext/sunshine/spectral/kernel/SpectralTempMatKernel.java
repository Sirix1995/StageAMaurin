package de.grogra.ext.sunshine.spectral.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;
import static javax.media.opengl.GL.GL_TEXTURE3;
import static javax.media.opengl.GL.GL_TEXTURE4;
import static javax.media.opengl.GL.GL_TEXTURE5;
import static javax.media.opengl.GL.GL_TEXTURE6;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import de.grogra.ext.sunshine.SunshineSceneVisitor;
import de.grogra.ext.sunshine.kernel.Kernel;

public class SpectralTempMatKernel extends Kernel {
	
	private int[] sceneTexture;
	private int[] texTexture;
	private int[] materialTex;
	private int[] colorRGB2SPDRefTexture;
	private int[] colorRGB2SPDIlluTexture;
	
	public SpectralTempMatKernel(String name, GLAutoDrawable drawable, int[] sceneTexture, int[] texTexture, int[] materialTex, 
			int[] colorRGB2SPDRefTex, int[] colorRGB2SPDIlluTex, int tileSize) 
	{
		super(name, drawable, tileSize);
		
		this.sceneTexture = sceneTexture;
		this.texTexture = texTexture;
		this.materialTex = materialTex;
		this.colorRGB2SPDRefTexture = colorRGB2SPDRefTex;
		this.colorRGB2SPDIlluTexture = colorRGB2SPDIlluTex;
		
	} // constructor
	
	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int i)
	{
		GL gl = drawable.getGL();
		
		int a0Loc	 		= getUniformLocation("a0", drawable);
		int a1Loc 			= getUniformLocation("a1", drawable);
		int sceneLoc		= getUniformLocation("scene", drawable);

		int texLoc			= getUniformLocation("texTexture", drawable);	
		int matLoc			= getUniformLocation("materialTex", drawable);
		int rgb2SPDRefLoc	= getUniformLocation("colorRGB2SPDRefTex", drawable);
		int rgb2SPDIlluLoc	= getUniformLocation("colorRGB2SPDIlluTex", drawable);
		
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
		
		gl.glActiveTexture(GL_TEXTURE3);
		gl.glBindTexture(texTarget, texTexture[0]);
		setUniformTex(texLoc, 3, drawable);
		
		gl.glActiveTexture(GL_TEXTURE4);
		gl.glBindTexture(texTarget, materialTex[0]);
		setUniformTex(matLoc, 4, drawable);
		
		gl.glActiveTexture(GL_TEXTURE5);
		gl.glBindTexture(texTarget, colorRGB2SPDRefTexture[0]);
		setUniformTex(rgb2SPDRefLoc, 5, drawable);
		
		gl.glActiveTexture(GL_TEXTURE6);
		gl.glBindTexture(texTarget, colorRGB2SPDIlluTexture[0]);
		setUniformTex(rgb2SPDIlluLoc, 6, drawable);
		
		drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	}
	
	public void loadSource(GLAutoDrawable drawable, 
			SunshineSceneVisitor monitor, String intermediates)
	{
		
	}
}
