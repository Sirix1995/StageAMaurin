/**
 * 
 */
package de.grogra.ext.sunshine.kernel.bidirPT;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;
import static javax.media.opengl.GL.GL_TEXTURE3;
import static javax.media.opengl.GL.GL_TEXTURE4;
import static javax.media.opengl.GL.GL_TEXTURE5;
import static javax.media.opengl.GL.GL_TEXTURE6;
import static javax.media.opengl.GL.GL_TEXTURE7;
import static javax.media.opengl.GL.GL_TEXTURE8;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.ObjectHandler;
import de.grogra.ext.sunshine.acceleration.SunshineAccelerator;

/**
 * @author mankmil
 *
 */
abstract public class InterruptableTraceRayKernel extends InterruptableKernel
{
	protected static String RAYPROCESSOR 		= "pathRT.frag";
	protected final static String MAIN 			= "bidirect_main.frag";
	protected final static String PROB_UTILS 	= "probUtils.frag";
	
	
	protected final static String TREE_TEXTURE 	= "treeTexture";
	protected final static String PRE_VERTEX	= "preVertexTexture";
	protected final static String PRE_ALPHA		= "preSpecTexture";
	
	protected final String CURRENT_SAMPLE 		= "currentSample";

	protected int[] sceneTexture;
	protected int[] texTexture;
	protected int[] treeTexture;
	
	protected boolean hasImage;
	
	protected SunshineAccelerator tree;
	
	/**
	 * represents the z<sub>i-2</sub> texture
	 */
	protected int z_2;

	public InterruptableTraceRayKernel(String name, GLAutoDrawable drawable,
			int[] sceneTexture, int[] texTexture, int[] treeData, ObjectHandler oh, 
			int tileSize, int steps, SunshineAccelerator tree) 
	{
		super(name, drawable, tileSize, oh.getObjectCount(), steps);
		
		this.sceneTexture 	= sceneTexture;
		this.texTexture 	= texTexture;
		this.treeTexture	= treeData;
		this.tree			= tree;
		
		hasImage = oh.hasImages();
	} // constructor
	
	

	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int sample)
	{
		GL gl = drawable.getGL();
		
		int a0Loc	 	= getUniformLocation("a0", drawable);
		int a1Loc 		= getUniformLocation("a1", drawable);
		int a2Loc 		= getUniformLocation("a2", drawable);
		
		int preVLoc		= getUniformLocation(PRE_VERTEX, drawable);
		int preSLoc		= getUniformLocation(PRE_ALPHA, drawable);
		
		int stateLoc	= getUniformLocation(STATE_TEXTURE, drawable);
		int sceneLoc	= getUniformLocation(SCENE_TEXTURE, drawable);
		int treeLoc		= getUniformLocation(TREE_TEXTURE, drawable);
		
		int pxLoc 		= getUniformLocation(TILE_X, drawable);
		int pyLoc 		= getUniformLocation(TILE_Y, drawable);
		int csLoc 		= getUniformLocation(CURRENT_SAMPLE, drawable);

		int texLoc = 0;
		if(hasImage) 
			texLoc		= getUniformLocation(TEXATLAS_TEXTURE, drawable);

		//activate the kernel
		useProgram(drawable);
			setUniformParameters(drawable);
			setUniformInt(pxLoc, px, drawable);
			setUniformInt(pyLoc, py, drawable);
			setUniformInt(csLoc, sample, drawable);
			
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
			setUniformTex(stateLoc, 3, drawable);
			
			gl.glActiveTexture(GL_TEXTURE4);
			gl.glBindTexture(texTarget, x_1);
			setUniformTex(preVLoc, 4, drawable);
			
			gl.glActiveTexture(GL_TEXTURE5);
			gl.glBindTexture(texTarget, r_1);
			setUniformTex(preSLoc, 5, drawable);

			gl.glActiveTexture(GL_TEXTURE6);
			gl.glBindTexture(texTarget, sceneTexture[0]);
			setUniformTex(sceneLoc, 6, drawable);
			
			gl.glActiveTexture(GL_TEXTURE7);
			gl.glBindTexture(texTarget, treeTexture[0]);
			setUniformTex(treeLoc, 7, drawable);
			
			gl.glActiveTexture(GL_TEXTURE8);
			gl.glBindTexture(texTarget, texTexture[0]);
			if(hasImage)
				setUniformTex(texLoc, 8, drawable);
			
			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
		
	}
	
}
