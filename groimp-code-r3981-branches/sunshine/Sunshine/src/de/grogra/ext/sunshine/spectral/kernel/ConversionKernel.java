package de.grogra.ext.sunshine.spectral.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;
import static javax.media.opengl.GL.GL_TEXTURE3;
import static javax.media.opengl.GL.GL_TEXTURE4;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

import de.grogra.ext.sunshine.SunshineSceneVisitor;
import de.grogra.ext.sunshine.kernel.Kernel;


public class ConversionKernel extends Kernel
{
	public ConversionKernel(String name, GLAutoDrawable drawable, int tileWidth) 
	{
		super(name, drawable, tileWidth);
		
	} // constructor
		
	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int i)
	{
		GL gl = drawable.getGL();
		
		int oi0Loc	 		= getUniformLocation("outputImage0", drawable);
		int oi1Loc 			= getUniformLocation("outputImage1", drawable);
		int oi2Loc 			= getUniformLocation("outputImage2", drawable);
		int oi3Loc 			= getUniformLocation("outputImage3", drawable);
		
		//activate the kernel
		useProgram(drawable);		
					
			setUniformParameters(drawable);		
			
			gl.glActiveTexture(GL.GL_TEXTURE0);
			gl.glBindTexture(texTarget, inputTextureB[0]);
			setUniformTex(oi0Loc, 0, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE1);
			gl.glBindTexture(texTarget, inputTextureB[1]);
			setUniformTex(oi1Loc, 1, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE2);
			gl.glBindTexture(texTarget, inputTextureB[2]);
			setUniformTex(oi2Loc, 2, drawable);
			
			gl.glActiveTexture(GL.GL_TEXTURE3);
			gl.glBindTexture(texTarget, inputTextureB[3]);
			setUniformTex(oi3Loc, 3, drawable);		
			
			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	}

	@Override
	public void loadSource(GLAutoDrawable drawable,
			SunshineSceneVisitor monitor, String intermediates) {
		// TODO Auto-generated method stub
		
	}

}
