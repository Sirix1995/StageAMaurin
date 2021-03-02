package de.grogra.ext.sunshine.kernel;


import static javax.media.opengl.GL.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.*;


/**
 * @author Thoams
 *
 */
public class MyKernel {

	private int program;
	private int shader;
	private int[] status;
	private String name;
	private boolean needSamplers = false;
	

	
	/**
	 * creates a fragment shader with the given name
	 * @param name
	 * @param gl
	 */
	public MyKernel(String name, GLAutoDrawable drawable, boolean needSamplers)
	{
		this.needSamplers = needSamplers;
		GL gl = drawable.getGL();		
		program = gl.glCreateProgramObjectARB();
		shader 	= gl.glCreateShaderObjectARB(GL.GL_FRAGMENT_SHADER_ARB);
		
		status = new int[1];
		this.name = name;
	} //Constructor
	
	
		
	public void setSource(GLAutoDrawable drawable, String[] source)
	{					
		GL gl = drawable.getGL();
		gl.glShaderSourceARB(shader, source.length, source, null);
		
//		if(name.equals("traceRayKernel")) writeShader(source);
	} //setSource
		
	
	public void compile(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glCompileShaderARB(shader);
		checkError(gl, "compile status "+ name + ": ", shader, GL_OBJECT_COMPILE_STATUS_ARB);
	}
	
	
	public void LinkProgram(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glAttachObjectARB(program, shader);
		
		// Link the shader into a complete GLSL program.
		gl.glLinkProgramARB(program);
		
		int[] progLinkSuccess = new int[1];
		int[] prog = new int[1];
		prog[0] = program;
		
		gl.glGetObjectParameterivARB(prog[0], GL.GL_OBJECT_LINK_STATUS_ARB, progLinkSuccess, 0);			

		if (progLinkSuccess[0] == 0)
		{
			System.out.println(name + " Shader could not be linked\n");
			
			checkGLErrors(drawable);
		} // if
	}
	
	
	public void setUniformArray(int location, float[] values, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform3fvARB(location, 1, values , 0);
	}
			
	
	public void setUniformInt(int location, int value, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform1iARB(location, value);
	}
	
	
	public void setUniformFloat(int location, float value, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform1fARB(location, value);
	}
	
	
	public void setUniformTex(int location, int textureUnit, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUniform1iARB(location, textureUnit);
	}
	
	
	public int getUniformLocation(String s, GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		int location = gl.glGetUniformLocationARB(program, s);
		
		if (location == -1)
		{
			System.out.println(name + " can't GetUniformLocationARB for " + s);

			checkGLErrors(drawable);
		} // if	
		
		return location;
	} //getUniformLocation
	
	
	public void useProgram(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUseProgramObjectARB(program);
	}
	
	
	public void stopProgram(GLAutoDrawable drawable)
	{
		GL gl = drawable.getGL();
		gl.glUseProgramObjectARB(0);
	}
	
	
	private void checkError(GL gl, String msg, int id, int check)
	{
		gl.glGetObjectParameterivARB(id, check, status, 0);
		if (msg != null)
			System.out.println(msg + status[0]);
		
		if(status[0] != GL_TRUE);
		{
			// print error message
			int[] logLength = new int[1];
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
	
	
	private void checkGLErrors(GLAutoDrawable drawable) 
	{ 
		GL gl = drawable.getGL();
	    int errCode = 0; 
	    String errStr = new String();
	    GLU glu = new GLU();
	    if ( ( errCode = gl.glGetError( ) ) != GL_NO_ERROR ) 
	    { 
	        errStr = glu.gluErrorString( errCode ); 
	        System.err.println( "OpenGL ERROR: " ); 
	        System.err.println( errStr ); 
	    } 
	    
	}
	
	
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
	
	
	private void writeShader(String[] s)
	{
		try
		{
			PrintWriter f = new PrintWriter(new BufferedWriter(
					new FileWriter("../Sunshine/Shader/traceRay.txt")));
		    
			for(int i = 0; i < s.length; i++)
				f.print(s[i]);
			
			f.close();
			
		} catch (IOException e) {
			System.out.println("Fehler beim Erstellen der Datei");
		}
	}

} //class
