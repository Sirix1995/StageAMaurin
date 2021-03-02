package de.grogra.ext.sunshine.kernel;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import static javax.media.opengl.GL.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.StreamUtil;
import de.grogra.ext.sunshine.SunshineSceneVisitor;
import de.grogra.util.StringMap;

/**
 * this class represents the openGL shader as kernels
 * 
 */
public abstract class Kernel
{	
	// textures
	protected final static String EYEPATH_TEXTURE 	= "eyePathTexture";
	protected final static String LIGHTPATH_TEXTURE = "lightPathTexture";
	protected final static String IRRAD_TEXTURE 	= "irradianceTexture";
	protected final static String NORMAL_TEXTURE 	= "normalTexture";
	protected final static String SCENE_TEXTURE 	= "sceneTexture";
	protected final static String TEXATLAS_TEXTURE 		= "texTexture";
	protected final static String SPECTRUM_TEXTURE	= "typeTexture";	
	
	// fragment files
	public final static String EXTENSIONS 			= "extension.frag";
	public final static String RANDOM 				= "random.frag";
	public final static String STRUCTS 				= "structs.frag";
	public final static String LIGHT_CALC			= "lightCalculations.frag";
	public final static String INTERSECT_UTILS 		= "intersectUtils.frag";
	public final static String CALC_NORMALS 		= "calc_normals.frag";
	public final static String INTERSECTIONS		= "intersections.frag";
	public final static String INTERSECT_LOOP		= "interruptableIntersectionLoop.frag";
	public final static String INITIALISATION		= "initialisation.frag";
	public final static String DIRECT_ILLUM			= "directIllum.frag";
	public final static String COMPUTE_BSDF			= "computeBSDF.frag";
	public final static String TEST_SHADOW			= "testShadow.frag";
	public final static String TEXTURE_LOOKUP		= "textureLookUps.frag";
	
	protected final static String SAMPLER			= "uniform sampler2DRect ";
	public final static String CURRENT_LIGHT_VERTEX	= "lightPathVertex";
	public final static String CURRENT_EYE_VERTEX	= "eyePathVertex";
	public final static String CURRENT_VERTEX		= "currentVertex";
	public final static String LAST_VERTEX			= "lastVertex";
	
	protected final static String PREV_INDEX1		= "prev_index1_Texture";
	protected final static String INDEX1			= "index1_Texture";
	protected final static String INDEXI			= "indexI_Texture";
	protected final static String INDEX2			= "index2_Texture";
	protected final static String PREV_INDEX2		= "prev_index2_Texture";
	
	
	protected final static String PREV_INDEX1_SPEC	= "prev_index1_spec_Texture";
	protected final static String INDEX1_SPEC		= "index1_spec_Texture";
	protected final static String INDEXI_SPEC		= "indexI_spec_Texture";
	protected final static String INDEX2_SPEC		= "index2_spec_Texture";
	protected final static String PREV_INDEX2_SPEC	= "prev_index2_spec_Texture";
	
	protected final static String WEIGHT_TEXTURE	= "weightTexture";
	
	protected final String TILE_X 					= "currentTileX";
	protected final String TILE_Y 					= "currentTileY";
	
	protected int program;
	protected int shader;
	protected int[] status = new int[1];
	protected boolean needSamplers = false;
	public String name;
	private boolean debug = false;
	
	protected StringMap parameters = new StringMap();
	
	/**
	 * GL_TEXTURE_RECTANGLE_ARB
	 * GL_TEXTURE_RECTANGLE_NV not needed
	 */
	protected int texTarget = GL_TEXTURE_RECTANGLE_ARB;
	protected int tileWidth;
	protected int tileHeight;
	
	protected int[] inputTextureA;
	protected int[] inputTextureB;
	
	/**
	 * represents the x<sub>i-1</sub> texture
	 */
	protected int x_1;
	protected int r_1;
	
	
	protected String PATH;
	
	public Kernel(){}
	
	public Kernel(String name, GLAutoDrawable drawable, int size)
	{
		GL gl = drawable.getGL();
		
		program = gl.glCreateProgramObjectARB();
		shader 	= gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB); //create the shader program
		tileWidth 	= size;
		tileHeight 	= size;
		
		this.name = name;
		
		PATH = "../shaderCode/";
	} //Constructor
	
	
	/**
	 * the execute method for the kernel
	 * paint a screen sized quad to create fragments 
	 * @param drawable
	 * @param px x index for the tiles
	 * @param py y index for the tiles
	 * @param sample for anti aliasing
	 * @param array
	 */
	public abstract void execute(GLAutoDrawable drawable, int px, int py, int sample);
	
	public abstract void loadSource(GLAutoDrawable drawable, 
			SunshineSceneVisitor monitor, String intermediates);
	
	
	public void setInputTextures(int[] inputTex, int[] imageTex)
	{
		inputTextureA = inputTex;
		inputTextureB = imageTex;
	}
	
	public void setTexture(int iPointTexture, int radianceTexture)
	{
		x_1 = iPointTexture;
		r_1 = radianceTexture;
	}
	
	
	/**
	 * set the string array as the source code for the shader
	 * @param drawable
	 * @param source a string array with the source code
	 */
	public void setSource(GLAutoDrawable drawable, String[] source)
	{					
		GL gl = drawable.getGL();
		gl.glShaderSourceARB(shader, source.length, source, null);
		
		
		if(debug) 
			writeShader(source);
	} //setSource
			
	
	// compile the program
	public void compile(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glCompileShaderARB(shader);
		checkError(gl, shader, GL_OBJECT_COMPILE_STATUS_ARB);
	}
	
	
	// create the executeable
	public void linkProgram(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glAttachObjectARB(program, shader);
		
		// Link the shader into a complete GLSL program.
		gl.glLinkProgramARB(program);
		
		checkError(gl, program, GL_OBJECT_LINK_STATUS_ARB);
	}
	
	
	/**
	 * Method to set some uniform parameter at the GLSL-stage. So its necessary that 
	 * <code>paraName</code> has the same name like the uniform variable in the GLSL-code. 
	 * 
	 * @param paraName	Name of the uniform variable in the GLSL-code
	 * @param value		Value for that variable.
	 */
	public void setUniform(String paraName, Object value)
	{
		parameters.put(paraName, value);
	}
	
	
	protected void setUniformParameters(GLAutoDrawable drawable)
	{		
		String[] paraNames = parameters.getKeys();
		
		for(int i = 0; i < parameters.size(); i++)
		{
			Object value = parameters.get(paraNames[i]);
			int loc	= getUniformLocation(paraNames[i], drawable);
			
			if(value instanceof Boolean)
			{
				setUniformInt(loc, ((Boolean) value).booleanValue() ? 1 : 0, drawable);
			} else if(value instanceof Integer)
				setUniformInt(loc, ((Integer) value).intValue(), drawable);
			else if(value instanceof Float)
				setUniformFloat(loc, ((Float) value).floatValue(), drawable);
			
		}
	}
		
	
	// set an array of three float values at the given location
	protected void setUniformArray(int location, float[] values, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform3fvARB(location, 1, values , 0);
		checkGLErrors(drawable);
	}
			
	
	// set an integer value at the given location
	protected void setUniformInt(int location, int value, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform1iARB(location, value);
	}
	
	
	// set afloat value at the given location
	protected void setUniformFloat(int location, float value, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform1fARB(location, value);
		checkGLErrors(drawable);
	}
	
	
	// set a texture at the given location
	protected void setUniformTex(int location, int textureUnit, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform1iARB(location, textureUnit);
		checkGLErrors(drawable);
	}
	
	
	// get the location for the given uniform variable
	protected int getUniformLocation(String s, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		// Get location of the sampler uniform variables
		int location = gl.glGetUniformLocationARB(program, s);
		
		if (location == -1)
		{
//			System.err.println(name + " can't GetUniformLocationARB for " + s);

			checkGLErrors(drawable);
		} // if	
		
		return location;
	} //getUniformLocation
		
		
	// start the execution of the fragment program
	protected void useProgram(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUseProgramObjectARB(program);
		checkGLErrors(drawable);
	}
	
	
	// stops the execution of the fragment programm
	protected void stopProgram(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUseProgramObjectARB(0);
		checkGLErrors(drawable);
	}
		
		
	// error checking
	private void checkError(GL gl, int id, int check)
	{
		int[] stat = new int[1];
		gl.glGetObjectParameterivARB(id, check, stat, 0);
		
		if(stat[0] != GL_TRUE);
		{
			
			// print error message
			int[] logLength = new int[1]; 
			//get the length of the error log
			gl.glGetObjectParameterivARB
			(
				id, GL_OBJECT_INFO_LOG_LENGTH_ARB, logLength, 0
			);
			
			byte[] log = new byte[logLength[0]];
			gl.glGetInfoLogARB(id, log.length, logLength, 0, log, 0);
		
			if(logLength[0] > 0) 
			System.err.println(name + " " + new String(log, 0, logLength[0]));
		} // if
	} // checkError //setShaders
		
		
	// check gl error, is called after everey gl call
	private void checkGLErrors(GLAutoDrawable drawable) 
	{ 
		GL gl = drawable.getGL();
	    int errCode = 0; 
	    String errStr = new String();
	    GLU glu = new GLU();
	    if ( ( errCode = gl.glGetError( ) ) != GL_NO_ERROR ) 
	    { 
	        errStr = glu.gluErrorString( errCode );  
	        
	        if(!errStr.equals(new String("invalid operation")))
	        {
	        	System.err.println( "OpenGL ERROR: " ); 
	        	System.err.println( errStr ); 
	        }
	    } 
	    
	}
	
	
	/**
	 * the fragment processor
	 * draw a quad with the given width and height
	 * @param drawable
	 * @param width
	 * @param height
	 * 
	 * TODO explain what texture coordinates are used for when drawing the quad
	 */
	protected void drawQuad(GLAutoDrawable drawable, int px, int py)
	{
		int width = Math.min(drawable.getWidth() - px
				* tileWidth, tileWidth);
		int height = Math.min(drawable.getHeight() - py
				* tileHeight, tileHeight);
		float x0 = -1;
		float y0 = -1;
		float x1 = -1 + (float)width / (tileWidth/2);
		float y1 = -1 + (float)height / (tileHeight/2);
//		width *= (x1 - x0) / 2;
//		height *= (y1 - y0) / 2;
		
		GL gl = drawable.getGL();
		gl.glBegin(GL_QUADS);
//		gl.glTexCoord2f(0, 0);				gl.glVertex3f(x0, y0, -0.5f);
//		gl.glTexCoord2f(width, 0);			gl.glVertex3f(x1, y0, -0.5f);
//		gl.glTexCoord2f(width, height); 	gl.glVertex3f(x1, y1, -0.5f);
//		gl.glTexCoord2f(0, height); 		gl.glVertex3f(x0, y1, -0.5f);
		gl.glTexCoord2f(0, 0);				gl.glVertex3f(x0, y0, -0.5f);
		gl.glTexCoord2f(width, 0);			gl.glVertex3f(x1, y0, -0.5f);
		gl.glTexCoord2f(width, height); 	gl.glVertex3f(x1, y1, -0.5f);
		gl.glTexCoord2f(0, height); 		gl.glVertex3f(x0, y1, -0.5f);
		gl.glEnd();
	} // drawQuad
	
	
	// load a String out of the file given by the path s
	protected String loadSource(String s)
	{
		return loadSource(s, PATH);
	} //loadShader
	
	
	protected String loadSource(String s, String path)
	{
		s = path + s;
		String source = null;
		try {
			source = new String(StreamUtil.readAll(this.getClass().getResourceAsStream(s)));
		} catch (Exception e)
		{
			System.out.println(s);
			System.out.println(e);
		}		
		return source;
	}
	
	public void setDebug()
	{
		debug = true;
	}
	
	public void setDebug(boolean b)
	{
		debug = b;
	}
		
	// debug method, writes the complete shader code to a file
	private void writeShader(String[] s)
	{
		try
		{
			File file = new File("../Sunshine/tmp/"+name+".c");
			file.getParentFile().mkdirs();
			PrintWriter f = new PrintWriter(new BufferedWriter(
					new FileWriter("../Sunshine/tmp/"+name+".c")));
		    
			for(int i = 0; i < s.length; i++)
				f.print(s[i]);
			
			f.close();
			
		} catch (IOException e) {
			System.err.println("Could not create file");
		}
	} //write

}
