package de.grogra.ext.sunshine.spectral.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;
import static javax.media.opengl.GL.GL_TEXTURE3;
import static javax.media.opengl.GL.GL_TEXTURE4;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import de.grogra.ext.sunshine.kernel.Kernel;
import de.grogra.ext.sunshine.SunshineSceneVisitor;


public class SpectralShadingKernel extends Kernel
{
	private int[] sceneTexture;
	private int[] texTexture;
	private int[] kdTexture;
	private int[] tempMapTexture;
	private int[] materialTex;
	private int[] colorRGB2SPDRefTexture;
	private int[] colorRGB2SPDIlluTexture;
	
	private boolean hasImage		= false;	
	private boolean extraMatFetch 	= false;;

	public SpectralShadingKernel(String name, GLAutoDrawable drawable,
			int[] sceneTexture, int[] texTexture, int[] kdTexture, boolean img,
			int[] materialTex, int[] colorRGB2SPDRefTex, int[] colorRGB2SPDIlluTex, int tileSize, int[] tempMapTexture) 
	{
		super(name, drawable, tileSize);

		this.sceneTexture = sceneTexture;
		this.texTexture = texTexture;
		this.kdTexture = kdTexture;
		this.tempMapTexture = tempMapTexture;
		this.materialTex = materialTex;
		this.colorRGB2SPDRefTexture = colorRGB2SPDRefTex;
		this.colorRGB2SPDIlluTexture = colorRGB2SPDIlluTex;
		hasImage = img;
		
	} // constructor
		
	public void setExtraMaterialFetch(boolean value)
	{
		extraMatFetch = value;
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
		int tempMat0Loc		= getUniformLocation("tempMatTex0", drawable);	
		int tempMat1Loc		= getUniformLocation("tempMatTex1", drawable);
		int matLoc			= getUniformLocation("materialTex", drawable);
		int rgb2SPDRefLoc	= getUniformLocation("colorRGB2SPDRefTex", drawable);
		int rgb2SPDIlluLoc	= getUniformLocation("colorRGB2SPDIlluTex", drawable);
		
		int oi0Loc	 		= getUniformLocation("outputImage0", drawable);
		int oi1Loc 			= getUniformLocation("outputImage1", drawable);
		int oi2Loc 			= getUniformLocation("outputImage2", drawable);
		int oi3Loc 			= getUniformLocation("outputImage3", drawable);
				
		int texLoc = 0;
		if(hasImage)
			texLoc		= getUniformLocation("texTexture", drawable);

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
			gl.glBindTexture(texTarget, materialTex[0]);
			setUniformTex(matLoc, 11, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE12);
			gl.glBindTexture(texTarget, colorRGB2SPDRefTexture[0]);
			setUniformTex(rgb2SPDRefLoc, 12, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE13);
			gl.glBindTexture(texTarget, colorRGB2SPDIlluTexture[0]);
			setUniformTex(rgb2SPDIlluLoc, 13, drawable);
			
			if(extraMatFetch)
			{
				gl.glActiveTexture(GL.GL_TEXTURE14);
				gl.glBindTexture(texTarget, tempMapTexture[0]);
				setUniformTex(tempMat0Loc, 14, drawable);
				
				gl.glActiveTexture(GL.GL_TEXTURE15);
				gl.glBindTexture(texTarget, tempMapTexture[1]);
				setUniformTex(tempMat1Loc, 15, drawable);
			}
									
			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	}
	
	
	public void loadSource(GLAutoDrawable drawable, 
			SunshineSceneVisitor monitor, String intermediates)
	{
		
	}

}

