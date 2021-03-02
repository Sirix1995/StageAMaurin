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
import static javax.media.opengl.GL.GL_TEXTURE9;
import static javax.media.opengl.GL.GL_TEXTURE10;
import static javax.media.opengl.GL.GL_TEXTURE11;
import static javax.media.opengl.GL.GL_TEXTURE12;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.ObjectHandler;
import de.grogra.ext.sunshine.SunshineSceneVisitor;
import de.grogra.ext.sunshine.kernel.Kernel;

/**
 * @author Thomas
 *
 */
public class CombineKernel extends Kernel
{
	private final String CURRENT_SAMPLE 		= "currentSample";
	
	private final static String EYE_TEXTURE		= "eyeImage_Texture";
	
	private final static String MAIN			= "combine_main.frag";
	
	private int[] sceneTexture;
	private int[] texTexture;
	private boolean existImages;
	
	private ObjectHandler oh;
	private int imageWidth;
	private int imageHeight;
	
	
	public CombineKernel(String name, GLAutoDrawable drawable, 
			int[] sceneTexture, int[] texTexture, ObjectHandler oh, 
			int tileSize, int imageWidth, int imageHeight)
	{
		super(name, drawable, tileSize);
		
		this.name 			= name;
		this.sceneTexture 	= sceneTexture;
		this.texTexture 	= texTexture;
		existImages 		= oh.hasImages();
		
		this.oh 			= oh;
		this.imageWidth 	= imageWidth;
		this.imageHeight 	= imageHeight;
	} //Constructor
	

	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int currentLightVertices)
	{
		GL gl = drawable.getGL();
		
		int prevIndexSLoc	= getUniformLocation(INDEX1, 			drawable);
		int indexSLoc	 	= getUniformLocation(INDEXI, 			drawable);
		int indexCLoc		= getUniformLocation(INDEX2, 			drawable);
		int prevIndexCLoc	= getUniformLocation(PREV_INDEX2, 		drawable);
		
		int prevIndexSspecLoc= getUniformLocation(INDEX1_SPEC, 		drawable);
		int indexSspecLoc	= getUniformLocation(INDEXI_SPEC,	 	drawable);
		int indexCspecLoc	= getUniformLocation(INDEX2_SPEC,	 	drawable);
		int prevIndexCspecLoc= getUniformLocation(PREV_INDEX2_SPEC, drawable);
		
		int weightLoc		= getUniformLocation(WEIGHT_TEXTURE, 	drawable);
		int eyeImageLoc		= getUniformLocation(EYE_TEXTURE, 		drawable);
		int sceneLoc		= getUniformLocation(SCENE_TEXTURE, 	drawable);
		
		int lightIndexLoc	= getUniformLocation(CURRENT_LIGHT_VERTEX, drawable);
		
		int pxLoc = getUniformLocation(TILE_X, drawable);
		int pyLoc = getUniformLocation(TILE_Y, drawable);
		
		int texLoc = 0;
		if(existImages)
			texLoc			= getUniformLocation(TEXATLAS_TEXTURE, drawable);
		
		//activate the kernel
		useProgram(drawable);
			setUniformParameters(drawable);
			setUniformInt(lightIndexLoc, currentLightVertices, drawable);
			
			setUniformInt(pxLoc, px, drawable);
			setUniformInt(pyLoc, py, drawable);
		
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, inputTextureA[1]);
			setUniformTex(prevIndexSLoc, 0, drawable);
			
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(texTarget, inputTextureA[2]);
			setUniformTex(indexSLoc, 1, drawable);
			
			gl.glActiveTexture(GL_TEXTURE2);
			gl.glBindTexture(texTarget, inputTextureA[3]);
			setUniformTex(indexCLoc, 2, drawable);
			
			gl.glActiveTexture(GL_TEXTURE3);
			gl.glBindTexture(texTarget, inputTextureA[4]);
			setUniformTex(prevIndexCLoc, 3, drawable);
			
			//---------------------------------------------\\
			
			gl.glActiveTexture(GL_TEXTURE4);
			gl.glBindTexture(texTarget, inputTextureA[6]);
			setUniformTex(prevIndexSspecLoc, 4, drawable);
			
			gl.glActiveTexture(GL_TEXTURE5);
			gl.glBindTexture(texTarget, inputTextureA[7]);
			setUniformTex(indexSspecLoc, 5, drawable);
			
			gl.glActiveTexture(GL_TEXTURE6);
			gl.glBindTexture(texTarget, inputTextureA[8]);
			setUniformTex(indexCspecLoc, 6, drawable);
			
			gl.glActiveTexture(GL_TEXTURE7);
			gl.glBindTexture(texTarget, inputTextureA[9]);
			setUniformTex(prevIndexCspecLoc, 7, drawable);
			
			//---------------------------------------------\\
			 
			gl.glActiveTexture(GL_TEXTURE8);
			gl.glBindTexture(texTarget, inputTextureA[10]);
			setUniformTex(weightLoc, 8, drawable);		
			
			gl.glActiveTexture(GL_TEXTURE9);
			gl.glBindTexture(texTarget, inputTextureB[0]);
			setUniformTex(eyeImageLoc, 9, drawable);
			
			gl.glActiveTexture(GL_TEXTURE10);
			gl.glBindTexture(texTarget, sceneTexture[0]);
			setUniformTex(sceneLoc, 10, drawable);
			
			gl.glActiveTexture(GL_TEXTURE11);
			gl.glBindTexture(texTarget, texTexture[0]);
			if(existImages)
				setUniformTex(texLoc, 11, drawable);
			
			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	} //execute
	
	
	public void loadSource(GLAutoDrawable drawable, SunshineSceneVisitor monitor, 
			String intermediates)
	{
		setSource(drawable, new String[]
        {
			loadSource(EXTENSIONS),
			SAMPLER + INDEX1 			+ ";\n",
			SAMPLER + INDEXI 			+ ";\n",
			SAMPLER + INDEX2 			+ ";\n",
			SAMPLER + PREV_INDEX2 		+ ";\n",
				
			SAMPLER + INDEX1_SPEC 		+ ";\n",
			SAMPLER + INDEXI_SPEC		+ ";\n",
			SAMPLER + INDEX2_SPEC 		+ ";\n",
			SAMPLER + PREV_INDEX2_SPEC 	+ ";\n",
				
			SAMPLER + WEIGHT_TEXTURE 	+ ";\n",
			SAMPLER + EYE_TEXTURE 		+ ";\n",
			SAMPLER + SCENE_TEXTURE 	+ ";\n",
			SAMPLER + TEXATLAS_TEXTURE 		+ ";\n",
				
			"uniform int " + CURRENT_LIGHT_VERTEX 	+ ";\n",
			"uniform int " + CURRENT_EYE_VERTEX 	+ ";\n",
			"uniform int " + CURRENT_SAMPLE		 	+ ";\n",
			"uniform int " + LAST_VERTEX 			+ ";\n",
			
			"uniform int " + TILE_X 				+ ";\n",
			"uniform int " + TILE_Y 				+ ";\n",
			
			"vec3 up 			= vec3("+oh.getUpString()	+");\n",
			"vec3 right 		= vec3("+oh.getRightString()+");\n",
			"vec3 dir 			= vec3("+oh.getDirString()	+");\n",
			"const float texWidth 		= " + imageWidth 	+ ".0;\n",
			"const float texHeight 		= " + imageHeight 	+ ".0;\n",
			"const int width = " 	+ tileWidth 	+ ";\n",
			"const int height = " 	+ tileHeight 	+ ";\n",
			
			intermediates,
			loadSource(STRUCTS),
			loadSource(LIGHT_CALC),
			loadSource(TEXTURE_LOOKUP),
			loadSource(CALC_NORMALS),
			loadSource(INTERSECT_UTILS),
			monitor.getPhong(),
			loadSource(COMPUTE_BSDF),
			loadSource(DIRECT_ILLUM),
			loadSource(MAIN)
        });
	}

} //CombineKernel
