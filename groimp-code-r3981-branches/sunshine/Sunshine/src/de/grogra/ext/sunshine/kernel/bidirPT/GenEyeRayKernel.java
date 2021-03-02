package de.grogra.ext.sunshine.kernel.bidirPT;

import static javax.media.opengl.GL.GL_TEXTURE0;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GL;
import de.grogra.ext.sunshine.ObjectHandler;
import de.grogra.ext.sunshine.SunshineSceneVisitor;
import de.grogra.ext.sunshine.kernel.Kernel;




public class GenEyeRayKernel extends Kernel
{
	private final String MAIN = "genCamRay_main.frag";
	
	protected final String CURRENT_SAMPLE 	= "currentSample";
	
	protected ObjectHandler oh;
	protected int imageWidth;
	protected int imageHeight;
	private int grid;
	
	 
	public GenEyeRayKernel(String name, GLAutoDrawable drawable, int tileSize,
			ObjectHandler oh, int imageWidth, int imageHeight, int grid)
	{
		super(name, drawable, tileSize);

		this.oh 			= oh;
		this.grid 			= grid;
		this.imageWidth 	= imageWidth;
		this.imageHeight 	= imageHeight;
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
		int a1Loc = getUniformLocation("a1", drawable);
		int pxLoc = getUniformLocation(TILE_X, drawable);
		int pyLoc = getUniformLocation(TILE_Y, drawable);
		int csLoc = getUniformLocation(CURRENT_SAMPLE, drawable);
		
		// start the shader programm
		useProgram(drawable);
		
			// set the values for the uniform variables
			setUniformInt(pxLoc, px, drawable);
			setUniformInt(pyLoc, py, drawable);
			setUniformInt(csLoc, sample, drawable);
		
			// bind the a_1 texture to retrieve the random number
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, inputTextureA[1]);
			setUniformTex(a1Loc, 0, drawable);
			
			// draw the quad with the given width and heigth
			drawQuad(drawable, px, py);
		stopProgram(drawable);
	} //execute
	
	public void loadSource(GLAutoDrawable drawable, SunshineSceneVisitor monitor, String intermediates)
	{
		setSource(drawable, new String[]
		{
			loadSource(Kernel.EXTENSIONS),
			"vec3 rayOrigin 	= vec3("+oh.getPosString()	+");\n", // the camera parameter
			"vec3 up 			= vec3("+oh.getUpString()	+");\n",
			"vec3 right 		= vec3("+oh.getRightString()+");\n",
			"vec3 dir 			= vec3("+oh.getDirString()	+");\n",
			"const float texWidth 		= " + imageWidth 	+ ".0;\n",
			"const float texHeight 		= " + imageHeight 	+ ".0;\n",
			"const int gridSize 		= " + grid 			+ ";\n",
			"const float width			= " + tileWidth 	+ ".0;\n",
			"const float height			= " + tileHeight 	+ ".0;\n",
			
			SAMPLER + "a1;\n",
			
			"uniform int " + TILE_X + ";\n",
			"uniform int " + TILE_Y + ";\n",
			"uniform int " + CURRENT_SAMPLE + ";\n",
			loadSource(Kernel.RANDOM),
			loadSource(MAIN)
		});
	}

}
