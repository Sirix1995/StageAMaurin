/**
 * 
 */
package de.grogra.ext.sunshine.kernel;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;
import static javax.media.opengl.GL.GL_TEXTURE3;
import static javax.media.opengl.GL.GL_TEXTURE4;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

/**
 * @author Thomas
 *
 */
public class CombineKernel extends Kernel
{
	private int[] sceneTexture;
	private int[] texTexture;
	private int[] kdTexture;
	private boolean hasImage;
	
	
	public CombineKernel(String name, GLAutoDrawable drawable, 
			int[] sceneTexture, int[] texTexture, int[] kdTexture, boolean img)
	{
		GL gl = drawable.getGL();
		
		program = gl.glCreateProgramObjectARB();
		shader 	= gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB);
		
		this.name = name;
		this.sceneTexture = sceneTexture;
		this.texTexture = texTexture;
		this.kdTexture = kdTexture;
		hasImage = img;
	} //Constructor
	

	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int i)
	{
		GL gl = drawable.getGL();
		
		int eyePathLoc	 	= getUniformLocation("eyePath", drawable);
		int lightPathLoc 	= getUniformLocation("lightPath", drawable);
		int sceneLoc		= getUniformLocation("scene", drawable);
		int colorLoc		= getUniformLocation("color", drawable);
		int normalTarget	= getUniformLocation("normals", drawable);
		
		//activate the kernel
		useProgram(drawable);
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, traceTexture[0]);
			setUniformTex(eyePathLoc, 0, drawable);
			
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(texTarget, traceTexture[1]);
			setUniformTex(lightPathLoc, 1, drawable);
			
			gl.glActiveTexture(GL_TEXTURE2);
			gl.glBindTexture(texTarget, traceTexture[2]);
			setUniformTex(normalTarget, 2, drawable);
			
			gl.glActiveTexture(GL_TEXTURE3);
			gl.glBindTexture(texTarget, traceTexture[3]);
			setUniformTex(colorLoc, 3, drawable);
			
			gl.glActiveTexture(GL_TEXTURE4);
			gl.glBindTexture(texTarget, sceneTexture[0]);
			setUniformTex(sceneLoc, 4, drawable);
			
			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	} //execute

} //CombineKernel
