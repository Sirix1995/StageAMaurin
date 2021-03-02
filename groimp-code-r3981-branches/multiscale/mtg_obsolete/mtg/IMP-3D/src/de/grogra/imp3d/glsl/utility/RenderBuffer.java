package de.grogra.imp3d.glsl.utility;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;

public class RenderBuffer implements FBOAttachment {
	int[] glRBNumber = new int[]{0};
	int width = 0, height = 0;
	int internalFormat = 0;
	
	public void attachToFbo(OpenGLState glState,
			int ATTACHMENT_POINT) {
		glState.getGL().glFramebufferRenderbufferEXT(GL.GL_FRAMEBUFFER_EXT,
				ATTACHMENT_POINT, GL.GL_RENDERBUFFER_EXT,
				glRBNumber[0]);
	}

	public void create(OpenGLState glState, int FORMAT) {
		// See if already correct
		if(		(glRBNumber[0] != 0) &&
				(FORMAT == internalFormat) &&
				(glState.height == height) &&
				(glState.width == width))
			return;
		GL gl = glState.getGL();
		
		// if already exists than kill
		if(glRBNumber[0] != 0) {
			delete(glState, false);
		}
		
		gl.glGenRenderbuffersEXT(1, glRBNumber, 0);
		gl.glBindRenderbufferEXT(GL.GL_RENDERBUFFER_EXT, glRBNumber[0]);
		
		// Reserve Space
		gl.glRenderbufferStorageEXT(GL.GL_RENDERBUFFER_EXT,
				FORMAT, glState.width, glState.height);
		width = glState.width;
		height = glState.height;
		internalFormat = FORMAT;
	}

	public void delete(OpenGLState glState, boolean javaonly) {
		if(glRBNumber[0] == 0)
			return;
		if(!javaonly)
			glState.getGL().glDeleteRenderbuffersEXT(1, glRBNumber, 0);
		glRBNumber[0] = 0;
	}

	public void resize(OpenGLState glState) {
		create(glState, internalFormat);
	}
	
	public boolean isOkay(OpenGLState glState) {
		GLSLDisplay.printDebugInfo("Test for RenderBuffer "+glRBNumber[0]+": ");
		boolean ok = glState.testGLError();
		GLSLDisplay.printDebugInfoN("");
		return ok;
	}
	
	@Override
	public String toString() {
		return "RB[I:"+glRBNumber[0]+",F:"+internalFormat+",S:"+width+"x"+height+"]";
	}
	
	private int estimateBPP () {
		switch (internalFormat) {
		case GL.GL_RGBA:
			return 4;
		case GL.GL_LUMINANCE32F_ARB:
			return 4;
		case GL.GL_RGBA16F_ARB:
			return 8;
		case GL.GL_RGBA8:
			return 4;
		case GL.GL_DEPTH_COMPONENT: 
			return 3;
		case GL.GL_RGBA32F_ARB:
			return 16;
		case GL.GL_RGBA16UI_EXT:
			return 8;
		case GL.GL_DEPTH24_STENCIL8_EXT:
			return 4;
		default:
			return 0;
		}
	}
	
	public int estimateSizeInByte()
	{
		if(glRBNumber[0] == 0)
			return 0;
		return width*height*estimateBPP();
	} 
}
