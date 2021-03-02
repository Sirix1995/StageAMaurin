package de.grogra.ext.sunshine;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Window;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;




public class ExtensionCheck extends Window implements GLEventListener
{
	private boolean executable = false;
	private boolean check = false;

    public ExtensionCheck(Frame f) 
    {
        super(f);

        GLCapabilities caps = new GLCapabilities();
        
        GLCanvas canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);

        add(canvas, BorderLayout.CENTER);
    }
    
    
    public void init(GLAutoDrawable drawable) 
    {
    	check(drawable);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) 
    {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) 
    {
    }

    public void display(GLAutoDrawable drawable) 
    {
    	
    }
    
    
	private void check(GLAutoDrawable drawable)
	{
		check = true;
		GL gl = drawable.getGL();
		
		int[] maxTextureUnits = new int[1];
		gl.glGetIntegerv(gl.GL_MAX_TEXTURE_UNITS, maxTextureUnits, 0);
		int[] Size = new int[1];
		gl.glGetIntegerv(gl.GL_MAX_TEXTURE_SIZE, Size, 0);
//		System.out.println("Max texture Size available = " + Size[0]);
//		System.out.println("Max texture Units available = "+ maxTextureUnits[0]);
		

	
		// Need to check whether support OpenGL Shading Language
		if(gl.isExtensionAvailable("GL_ARB_fragment_shader")
//				&& gl.isExtensionAvailable("GL_ARB_shader_objects")
//				&& gl.isExtensionAvailable("GL_ARB_shading_language_120")
				&& gl.isExtensionAvailable("GL_ARB_draw_buffers")
				&& gl.isExtensionAvailable("GL_ARB_texture_rectangle")
			)
		{
			executable = true;
		} // if

	} // check
	
	public boolean isExecutable()
	{
		while(!check){}
			
		return executable;
	}
   
}