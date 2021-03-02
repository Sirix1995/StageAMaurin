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
import static javax.media.opengl.GL.GL_TEXTURE13;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.ObjectHandler;
import de.grogra.ext.sunshine.SunshineSceneVisitor;
import de.grogra.ext.sunshine.kernel.Kernel;

public class ComplementKernel extends Kernel
{
	private final static String MAIN = "traceComplement_main.frag";
	
	private final static String INFO_TEXTUREI = "infoTexture_I";
	
	private int[] sceneTexture;
	private int[] texTexture;
	private boolean existImages;
	
	private int heuristicExp;
	private ObjectHandler oh;
	

	public ComplementKernel(String name, GLAutoDrawable drawable, int size,
			int[] scene, int[] textures, ObjectHandler oh, int heuristicExp)
	{
		super(name, drawable, size);
		
		this.sceneTexture 	= scene;
		this.texTexture 	= textures;
		existImages 		= oh.hasImages();
		
		this.heuristicExp 	= heuristicExp;
		this.oh 			= oh;
	}


	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int currentSample)
	{
		GL gl = drawable.getGL();
		
		int prevIndex1Loc 		= getUniformLocation(PREV_INDEX1,	drawable);
		int index1Loc 			= getUniformLocation(INDEX1, 		drawable);
		int indexILoc 			= getUniformLocation(INDEXI,	 	drawable);
		int index2Loc			= getUniformLocation(INDEX2,	 	drawable);
		int prevIndex2Loc 		= getUniformLocation(PREV_INDEX2,	drawable);
		
		int prevIndex1specLoc	= getUniformLocation(PREV_INDEX1_SPEC, 	drawable);
		int index1specLoc		= getUniformLocation(INDEX1_SPEC,	 	drawable);
		int indexIspecLoc		= getUniformLocation(INDEXI_SPEC,	 	drawable);
		int index2specLoc		= getUniformLocation(INDEX2_SPEC,	 	drawable);
		int prevIndex2specLoc	= getUniformLocation(PREV_INDEX2_SPEC, 	drawable);
		
		int weightLoc			= getUniformLocation(WEIGHT_TEXTURE,	drawable);
		int sceneLoc			= getUniformLocation(SCENE_TEXTURE, 	drawable);
		
		int infoLoc				= getUniformLocation(INFO_TEXTUREI, 	drawable);
		
		int texLoc = 0;
		if(existImages)
			texLoc				= getUniformLocation(TEXATLAS_TEXTURE, 	drawable);
		
		//activate the kernel
		useProgram(drawable);
		
			setUniformParameters(drawable);
			
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, inputTextureA[0]);
			setUniformTex(prevIndex1Loc, 0, drawable);
			
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(texTarget, inputTextureA[1]);
			setUniformTex(index1Loc, 1, drawable);
			
			gl.glActiveTexture(GL_TEXTURE2);
			gl.glBindTexture(texTarget, inputTextureA[2]);
			setUniformTex(indexILoc, 2, drawable);
			
			gl.glActiveTexture(GL_TEXTURE3);
			gl.glBindTexture(texTarget, inputTextureA[3]);
			setUniformTex(index2Loc, 3, drawable);
			
			gl.glActiveTexture(GL_TEXTURE4);
			gl.glBindTexture(texTarget, inputTextureA[4]);
			setUniformTex(prevIndex2Loc, 4, drawable);
			
			//-----------------------------------------\\
			
			gl.glActiveTexture(GL_TEXTURE5);
			gl.glBindTexture(texTarget, inputTextureA[5]);
			setUniformTex(prevIndex1specLoc, 5, drawable);
			
			gl.glActiveTexture(GL_TEXTURE6);
			gl.glBindTexture(texTarget, inputTextureA[6]);
			setUniformTex(index1specLoc, 6, drawable);
			
			gl.glActiveTexture(GL_TEXTURE7);
			gl.glBindTexture(texTarget, inputTextureA[7]);
			setUniformTex(indexIspecLoc, 7, drawable);
			
			gl.glActiveTexture(GL_TEXTURE8);
			gl.glBindTexture(texTarget, inputTextureA[8]);
			setUniformTex(index2specLoc, 8, drawable);
			
			gl.glActiveTexture(GL_TEXTURE9);
			gl.glBindTexture(texTarget, inputTextureA[9]);
			setUniformTex(prevIndex2specLoc, 9, drawable);
			
			//-----------------------------------------\\
			
			gl.glActiveTexture(GL_TEXTURE10);
			gl.glBindTexture(texTarget, inputTextureA[10]);
			setUniformTex(weightLoc, 10, drawable);
			
			//-----------------------------------------\\
			
			gl.glActiveTexture(GL_TEXTURE11);
			gl.glBindTexture(texTarget, inputTextureA[11]);
			setUniformTex(infoLoc, 11, drawable);
			
			//-----------------------------------------\\
			
			gl.glActiveTexture(GL_TEXTURE12);
			gl.glBindTexture(texTarget, sceneTexture[0]);
			setUniformTex(sceneLoc, 12, drawable);
			
			
			gl.glActiveTexture(GL_TEXTURE13);
			gl.glBindTexture(texTarget, texTexture[0]);
			if(existImages)
				setUniformTex(texLoc, 13, drawable);
			
			drawQuad(drawable, px, py);
		// disable the kernel
		stopProgram(drawable);
	}


	@Override
	public void loadSource(GLAutoDrawable drawable,
			SunshineSceneVisitor monitor, String intermediates)
	{
		setSource(drawable, new String[]
		{
			loadSource(EXTENSIONS),
			SAMPLER + PREV_INDEX1 		+ ";\n",
			SAMPLER + INDEX1 			+ ";\n",
			SAMPLER + INDEXI 			+ ";\n",
			SAMPLER + INDEX2 			+ ";\n",
			SAMPLER + PREV_INDEX2 		+ ";\n",
				
			SAMPLER + PREV_INDEX1_SPEC 	+ ";\n",
			SAMPLER + INDEX1_SPEC 		+ ";\n",
			SAMPLER + INDEXI_SPEC 		+ ";\n",
			SAMPLER + INDEX2_SPEC 		+ ";\n",
			SAMPLER + PREV_INDEX2_SPEC 	+ ";\n",
			
			SAMPLER + SCENE_TEXTURE		+ ";\n",
			SAMPLER + TEXATLAS_TEXTURE 	+ ";\n",
			SAMPLER + WEIGHT_TEXTURE	+ ";\n",
			
			SAMPLER + INFO_TEXTUREI		+ ";\n",
			
			"uniform int " + CURRENT_EYE_VERTEX 	+ ";\n",
			"uniform int " + LAST_VERTEX 			+ ";\n",
			"uniform int " + CURRENT_VERTEX			+ ";\n", 

			"uniform int DIRECTION;\n",
			"float heuristicExponent = " + heuristicExp + ".0;\n",
			
			"vec3 camDir = vec3("+oh.getDirString()	+");\n",
			
			intermediates,
			loadSource(STRUCTS),
			loadSource(LIGHT_CALC),
			loadSource(TEXTURE_LOOKUP),
			loadSource(CALC_NORMALS),
			loadSource(INTERSECT_UTILS),
			monitor.getPhong(),
			loadSource(COMPUTE_BSDF),
			loadSource(MAIN)
		});
	}

}
