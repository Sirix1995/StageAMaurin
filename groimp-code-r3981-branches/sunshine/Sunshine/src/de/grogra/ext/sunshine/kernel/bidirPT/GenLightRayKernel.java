package de.grogra.ext.sunshine.kernel.bidirPT;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GL;
import de.grogra.ext.sunshine.SunshineLightHandler;
import de.grogra.ext.sunshine.SunshineSceneVisitor;
import de.grogra.ext.sunshine.kernel.Kernel;


public class GenLightRayKernel extends Kernel
{
	private final String SAMPLE 	= "currentSample";
	
	private int[] sceneTexture;
	private int lightCount;
	
	
	/**
	 * creates a fragment shader
	 * 
	 * @param name of the shader
	 * @param drawable 
	 * @param tileWidth width of tile
	 * @param tileHeight height of the tile
	 */
	 
	public GenLightRayKernel(String name, GLAutoDrawable drawable, 
			int[] scene, int lights, int tileSize)
	{
		super(name, drawable, tileSize);
		
		sceneTexture 	= scene;
		lightCount 		= lights;	 
	} //Constructor
	
	
	/**
	 * 
	 * @param drawable
	 * @param width
	 * @param height
	 * @param px
	 * @param py
	 * @param fbo: render target
	 * @param inputTexture: input texture
	 */
	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int sample)
	{
		GL gl = drawable.getGL();
		
		// get location for the uniform variables
		int a1Loc 		= getUniformLocation("a1", drawable);
		int sceneLoc	= getUniformLocation(SCENE_TEXTURE, drawable);
		int csLoc 		= getUniformLocation(SAMPLE, drawable);
		
		// start the shader programm
		useProgram(drawable);
		
			setUniformInt(csLoc, sample, drawable);
		
			// bind the a_1 texture to retrieve the random number
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, inputTextureA[1]);
			setUniformTex(a1Loc, 0, drawable);
			
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(texTarget, sceneTexture[0]);
			setUniformTex(sceneLoc, 1, drawable);
			
			// draw the quad with the given width and heigth
			drawQuad(drawable, px, py);
		stopProgram(drawable);
	} //execute
	
	
	
	public void loadSource(GLAutoDrawable drawable, SunshineSceneVisitor monitor, 
			String intermediates)
	{
		SunshineLightHandler lh = new SunshineLightHandler(lightCount);
		
		setSource(drawable, new String[]
	    {
				loadSource(EXTENSIONS),
				SAMPLER + "a1;\n",
				SAMPLER + SCENE_TEXTURE + ";\n",
				
				"uniform int " + SAMPLE + ";\n",
				
				intermediates,
				loadSource(RANDOM),
				loadSource(STRUCTS),
				loadSource(TEXTURE_LOOKUP),
				loadSource(CALC_NORMALS),
				loadSource(INTERSECT_UTILS),
				loadSource("randomValues.frag"),
				lh.retrieveLight(),
				loadSource("genLightRay_main.frag")		
	    });
	}

} //class GenLightRay
