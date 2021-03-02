package de.grogra.ext.sunshine.kernel;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import static javax.media.opengl.GL.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.media.opengl.glu.*;

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
	protected String name;
	protected int width;
	protected int height;
	
	/**
	 * GL_TEXTURE_RECTANGLE_ARB
	 */
	protected int texTarget = GL_TEXTURE_RECTANGLE_ARB;

	
	public Kernel(){}
	
	public Kernel(String name, GLAutoDrawable drawable, int tileWidth, int tileHeight)
	{
		GL gl = drawable.getGL();
		
		program = gl.glCreateProgramObjectARB();
		shader 	= gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB); //create the shader program
		width 	= tileWidth; 
		height 	= tileHeight;
		
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
	public abstract void execute(GLAutoDrawable drawable, int px, int py, int i, int[] array);
	
	
	/**
	 * set the string array as the source code for the shader
	 * @param drawable
	 * @param source a string array with the source code
	 */
	public void setSource(GLAutoDrawable drawable, String[] source)
	{					
		GL gl = drawable.getGL();
		gl.glShaderSourceARB(shader, source.length, source, null);
		
			if(name.equals("traceRayKernel")) writeShader(source);
	} //setSource
			
	
	// compile the program
	public void compile(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glCompileShaderARB(shader);
		checkError(gl, "compile status "+ name + ": ", shader, GL_OBJECT_COMPILE_STATUS_ARB);
	}
	
	
	// create the executeable
	public void LinkProgram(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glAttachObjectARB(program, shader);
		
		// Link the shader into a complete GLSL program.
		gl.glLinkProgramARB(program);
		
//		int[] progLinkSuccess = new int[1];
//		int[] prog = new int[1];
//		prog[0] = program;
		
		checkError(gl, "Link status "+ name + ": ", program, GL_OBJECT_LINK_STATUS_ARB);
		
//		//error checking
//		gl.glGetObjectParameterivARB(prog[0], GL.GL_OBJECT_LINK_STATUS_ARB, progLinkSuccess, 0);			
//
//		if (progLinkSuccess[0] == 0)
//		{
//			System.err.println(name + " Shader could not be linked\n");
//			checkError(gl, "Link status "+ name + ": ", program, GL_OBJECT_LINK_STATUS_ARB);
//			checkGLErrors(drawable);
//		} // if
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
		if (msg != null)
			System.err.println(msg + stat[0]);
		
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
	        System.err.println( "OpenGL ERROR: " ); 
	        System.err.println( errStr ); 
	        
	        
	    } 
	    
	}
	
	
	/**
	 * the fragment prozessor
	 * draw a quad with the given width and height
	 * @param drawable
	 * @param width
	 * @param height
	 */
	protected void drawQuad(GLAutoDrawable drawable, int width, int height)
	{
		GL gl = drawable.getGL();
		gl.glBegin(GL_QUADS);
			gl.glTexCoord2f(0, 0);				gl.glVertex3f(-1, -1, -0.5f);
			gl.glTexCoord2f(width, 0);			gl.glVertex3f(1, -1, -0.5f);
			gl.glTexCoord2f(width, height); 	gl.glVertex3f(1, 1, -0.5f);
			gl.glTexCoord2f(0, height); 		gl.glVertex3f(-1, 1, -0.5f);
		gl.glEnd();
	} // drawQuad
		
		
	// debug method, writes the complete shader code to a file
	private void writeShader(String[] s)
	{
		try
		{
			PrintWriter f = new PrintWriter(new BufferedWriter(
					new FileWriter("../Sunshine/Shader/traceRay.c")));
		    
			for(int i = 0; i < s.length; i++)
				f.print(s[i]);
			
			f.close();
			
		} catch (IOException e) {
			System.err.println("Could not create file");
		}
	}

}
