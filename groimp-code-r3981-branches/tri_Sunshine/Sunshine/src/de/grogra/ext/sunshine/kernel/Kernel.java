package de.grogra.ext.sunshine.kernel;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import static javax.media.opengl.GL.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.media.opengl.glu.*;

import de.grogra.ext.sunshine.SunshineRaytracer;
import de.grogra.util.StringMap;

/**
 * this class represents the openGL shader as kernels
 * 
 */
public abstract class Kernel
{
	protected int program;
	protected int shader;
	protected int[] status = new int[1];
	protected boolean needSamplers = false;
	public String name;
	private boolean debug = true; 
	
	protected int loopStart	= 0;
	protected int loopStop	= 0;
	protected boolean lastCycle = false;
	
	protected StringMap parameters = new StringMap();
	
	/**
	 * GL_TEXTURE_RECTANGLE_ARB
	 * GL_TEXTURE_RECTANGLE_NV not needed
	 */
	protected int texTarget = GL_TEXTURE_RECTANGLE_ARB;
	
	protected int[] traceTexture;
	protected int[] imageTexture;
	
	public Kernel(){}
	
	public Kernel(String name, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		
		program = gl.glCreateProgramObjectARB();
		shader 	= gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB); //create the shader program
		
		this.name = name;
	} //Constructor
	
	
	/**
	 * the execute method for the kernel
	 * paint a screen sized quad to create fragments 
	 * @param drawable
	 * @param px x index for the tiles
	 * @param py y index for the tiles
	 * @param i the sample for anti aliasing
	 * @param array
	 */
	public abstract void execute(GLAutoDrawable drawable, int px, int py, int i);
	
	
	public void setTextures(int[] traceTex, int[] imageTex)
	{
		traceTexture = traceTex;
		imageTexture = imageTex;
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
		checkError(gl, "compile status "+ name + ": ", shader, GL_OBJECT_COMPILE_STATUS_ARB);
	}
	
	
	// create the executeable
	public void linkProgram(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glAttachObjectARB(program, shader);
		
		// Link the shader into a complete GLSL program.
		gl.glLinkProgramARB(program);
		
		checkError(gl, "Link status "+ name + ": ", program, GL_OBJECT_LINK_STATUS_ARB);
	}
			
	/**
	 * Method to set some uniform parameter at the GLSL-stage. So its necessary that 
	 * <code>paraName</code> has the same name like the uniform variable in the GLSL-code. 
	 * 
	 * @param paraName	Name of the uniform variable in the GLSL-code
	 * @param value		Value for that variable.
	 */
	public void setParameter(String paraName, Object value)
	{
		parameters.put(paraName, value);
	}
	
	protected void setUniformParameters(GLAutoDrawable drawable)
	{
		String[] keys = parameters.getKeys();
		String paraName;
		
		for (int i = 0; i < keys.length; i++)
		{
			paraName = keys[i];
			int loc	= getUniformLocation(paraName, drawable);
			Object value = parameters.get(paraName);
			
			if(value instanceof Boolean)
			{
				int test = ((Boolean) value).booleanValue() ? 1 : 0;
				setUniformInt(loc, ((Boolean) value).booleanValue() ? 1 : 0, drawable);
			} else if(value instanceof Integer)
				setUniformInt(loc, ((Integer) value).intValue(), drawable);
			else if(value instanceof Float)
				setUniformFloat(loc, ((Float) value).floatValue(), drawable);
		}
	}
	
	// set an array of three float values at the given location
	public void setUniformArray(int location, float[] values, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform3fvARB(location, 1, values , 0);
		checkGLErrors(drawable);
	}
			
	
	// set an integer value at the given location
	public void setUniformInt(int location, int value, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform1iARB(location, value);
	}
	
	
	// set afloat value at the given location
	public void setUniformFloat(int location, float value, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform1fARB(location, value);
		checkGLErrors(drawable);
	}
	
	
	// set a texture at the given location
	public void setUniformTex(int location, int textureUnit, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform1iARB(location, textureUnit);
		checkGLErrors(drawable);
	}
	
	
	// get the location for the given uniform variable
	public int getUniformLocation(String s, GLAutoDrawable drawable)
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
	public void useProgram(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUseProgramObjectARB(program);
		checkGLErrors(drawable);
	}
	
	
	// stops the execution of the fragment programm
	public void stopProgram(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUseProgramObjectARB(0);
		checkGLErrors(drawable);
	}
		
		
	// error checking
	private void checkError(GL gl, String msg, int id, int check)
	{
		int[] stat = new int[1];
		gl.glGetObjectParameterivARB(id, check, stat, 0);
//		if (msg != null)
//			System.err.println(msg + stat[0]);
		
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
		
			System.err.println(new String(log, 0, logLength[0]));
		} // if
	} // checkError //setShaders


	public boolean needSamplers()
	{
		return needSamplers;
	}
		
		
	// check gl error, is called after everey gl call
	private void checkGLErrors(GLAutoDrawable drawable) 
	{ 
		GL gl = drawable.getGL();
	    int errCode = 0; 
	    int[] errPos;
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
				* SunshineRaytracer.TILE_WIDTH, SunshineRaytracer.TILE_WIDTH);
		int height = Math.min(drawable.getHeight() - py
				* SunshineRaytracer.TILE_HEIGHT, SunshineRaytracer.TILE_HEIGHT);
		float x0 = -1;
		float y0 = -1;
		float x1 = -1 + (float)width / (SunshineRaytracer.TILE_WIDTH / 2.0f);
		float y1 = -1 + (float)height / (SunshineRaytracer.TILE_HEIGHT / 2.0f);
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
		
		gl.glFlush();
	} // drawQuad
		
	
	public void setDebug()
	{
		debug = true;
	}
	
	// debug method, writes the complete shader code to a file
	private void writeShader(String[] s)
	{
		try
		{
			PrintWriter f = new PrintWriter(new BufferedWriter(
					new FileWriter("../Sunshine/"+name+".c")));
		    
			for(int i = 0; i < s.length; i++)
				f.print(s[i]);
			
			f.close();
			
		} catch (IOException e) {
			System.err.println("Could not create file");
		}
	} //write

}
