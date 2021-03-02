package de.grogra.imp3d.glsl.utility;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.OpenGLState;

public class TextureRenderTarget extends GLSLTexture implements FBOAttachment {

	public void attachToFbo(OpenGLState glState, 
			int ATTACHMENT_POINT) {
		glState.getGL().glFramebufferTexture2DEXT(GL.GL_FRAMEBUFFER_EXT,
				ATTACHMENT_POINT, getTexType(), index, 0);
	}
	
	public final static int RGBA16F = 0;
	public final static int RGBA8UB = 1;
	public final static int DEPTHUB = 2;
	public final static int RGBA32F = 3;
	public final static int RGBA16US = 1;
	
	int[][] formats = {
			{GL.GL_RGBA16F_ARB, GL.GL_BGRA, GL.GL_HALF_FLOAT_ARB},
			{GL.GL_RGBA8, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE},
			{GL.GL_DEPTH_COMPONENT24, GL.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE},
			{GL.GL_RGBA32F_ARB, GL.GL_BGRA, GL.GL_FLOAT},
			{GL.GL_RGBA16UI_EXT, GL.GL_BGRA, GL.GL_UNSIGNED_SHORT}
	}; 

	int formatType = -1;

	public void create(OpenGLState glState, int FORMAT) {
		create(glState, FORMAT, GL.GL_TEXTURE_RECTANGLE_ARB);
	}

	public void create(OpenGLState glState, int FORMAT, int type) {
		GL gl = glState.getGL();
		if(	create(gl, glState.width, glState.height, GL.GL_NEAREST, formats[FORMAT][0], formats[FORMAT][1], formats[FORMAT][2], type) )
			System.err.println("Error!!!");
		formatType = FORMAT;
	}
	
	public void delete(OpenGLState glState, boolean javaonly) {
		delete(glState.getGL(), javaonly);
		formatType = -1;
	}

	public void resize(OpenGLState glState) {
		GL gl = glState.getGL();
		create(gl, glState.width, glState.height, GL.GL_NEAREST, formats[formatType][0], formats[formatType][1], formats[formatType][2], texType);		
	}

	public void bindTo(OpenGLState glState, int targetTexNo) {
		bindTo(glState.getGL(), GL.GL_TEXTURE0 + targetTexNo);
	}	

	// Not useful since create already detects all errors and deletes them
//	public boolean isOkay(OpenGLState glState) {
//		GLSLDisplay.printDebugInfo("Test for TextureRenderTarget "+index+": ");
//		boolean ok = glState.testGLError();
//		GLSLDisplay.printDebugInfoN("");
//		return ok;
//	}
	
	@Override
	public String toString() {
		return "TRT[I:"+index+",F:"+internalFormat+",S:"+width+"x"+height+",T:"+texType+"]";
	}
}
