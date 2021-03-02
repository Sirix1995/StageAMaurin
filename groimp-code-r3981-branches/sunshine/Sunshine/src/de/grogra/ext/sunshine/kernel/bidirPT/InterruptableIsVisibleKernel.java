package de.grogra.ext.sunshine.kernel.bidirPT;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;
import static javax.media.opengl.GL.GL_TEXTURE3;
import static javax.media.opengl.GL.GL_TEXTURE4;
import static javax.media.opengl.GL.GL_TEXTURE5;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.SunshineSceneVisitor;

/**
 * this class calculates the object normals
 * 
 * @author Thomas
 * 
 */
public class InterruptableIsVisibleKernel extends InterruptableKernel
{
	private final static String TEST_SHADOW				= "testShadow.frag";
	private final static String VISIBLE_MAIN			= "visibleTest_main.frag";
	
	private final static String CURRENT_LIGHT_VERTEX	= "lightPathVertex";
	private final static String CURRENT_EYE_VERTEX		= "eyePathVertex";
	
	
	private int[] sceneTexture;
	private int[] texTexture;


	public InterruptableIsVisibleKernel(String name, GLAutoDrawable drawable, int[] scene,
			int tileSize, int[] texTexture, int objects, int steps)
	{
		super(name, drawable, tileSize, objects, steps);

		this.sceneTexture = scene;
		this.texTexture = texTexture;
	} //constructor


	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int currentLightVertex)
	{
		GL gl = drawable.getGL();

		// get location for the uniform variables
		int indexSLoc 		= getUniformLocation(INDEXI, drawable);
		int indexCLoc 		= getUniformLocation(INDEX2, drawable);
		
		int stateLoc		= getUniformLocation(STATE_TEXTURE, drawable);
		int sceneLoc 		= getUniformLocation(SCENE_TEXTURE, drawable);
		int texLoc 			= getUniformLocation(TEXATLAS_TEXTURE, drawable);
		
		int indexLoc		= getUniformLocation(CURRENT_LIGHT_VERTEX, drawable);

		useProgram(drawable);
			setUniformParameters(drawable);
			setUniformInt(indexLoc, currentLightVertex, drawable);
			
			// bind the eye path texture
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, inputTextureA[2]);
			setUniformTex(indexSLoc, 0, drawable);
	
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(texTarget, inputTextureA[3]);
			setUniformTex(indexCLoc, 1, drawable);
			
			//---------------------------------------------\\
			
			gl.glActiveTexture(GL_TEXTURE3);
			gl.glBindTexture(texTarget, inputTextureA[10]);
			setUniformTex(stateLoc, 3, drawable);
			
			gl.glActiveTexture(GL_TEXTURE4);
			gl.glBindTexture(texTarget, sceneTexture[0]);
			setUniformTex(sceneLoc, 4, drawable);
	
			gl.glActiveTexture(GL_TEXTURE5);
			gl.glBindTexture(texTarget, texTexture[0]);
			setUniformTex(texLoc, 5, drawable);

			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	}


	public void loadSource(GLAutoDrawable drawable, SunshineSceneVisitor monitor, 
			String intermediates)
	{
		setSource(drawable, new String[] 
        {
			loadSource(EXTENSIONS),
			SAMPLER + INDEXI			+ ";\n",
			SAMPLER + INDEX2			+ ";\n",
				
			SAMPLER + SCENE_TEXTURE		+ ";\n",
			SAMPLER + STATE_TEXTURE		+ ";\n",
			SAMPLER + TEXATLAS_TEXTURE		+ ";\n",
				
			"uniform int " + CURRENT_LIGHT_VERTEX 	+ ";\n",
			"uniform int " + CURRENT_EYE_VERTEX 	+ ";\n",
			
			intermediates,
			INTERRUPTERS,
			loadSource(STRUCTS),
			loadSource(TEXTURE_LOOKUP),
			loadSource(CALC_NORMALS),
			loadSource(INTERSECT_UTILS),
			monitor.getPhong(),
			loadSource(INTERSECTIONS),
			loadSource(INTERSECT_LOOP),
			loadSource(TEST_SHADOW),
			loadSource(VISIBLE_MAIN),
        });
	}
} // class
