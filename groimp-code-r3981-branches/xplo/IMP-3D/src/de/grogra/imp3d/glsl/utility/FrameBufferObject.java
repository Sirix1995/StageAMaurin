package de.grogra.imp3d.glsl.utility;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;

public class FrameBufferObject {
	int[] glFBONumber = new int[] { 0 };

	/**
	 * first 4 are DrawBuffers next is DepthBuffer last is StencilBuffer
	 */
	FBOAttachment[] attachments = new FBOAttachment[6];
	private final int[] nothingActive = {-1, -1, -1, -1};
	int[] activeRenderTargets = new int[4];
	int activeRenderTarget = -1;
	
	public FBOAttachment getAttachment(int i) {
		return attachments[i];
	}
	
	public void bind(OpenGLState glState) {
		glState.setFBO(glFBONumber[0]);
	}

	public void delete(OpenGLState glState, boolean javaonly) {
		glFBONumber[0] = 0;
		for(int i = 0; i < 6; i++)
			attachments[i] = null;
		
		if(!javaonly)
			glState.getGL().glDeleteFramebuffersEXT(1, glFBONumber, 0);
			
	}

	public void create(OpenGLState glState) {
		GL gl = glState.getGL();
		gl.glGenFramebuffersEXT(1, glFBONumber, 0);
	}
	
	public void attachDrawBuffer(OpenGLState glState, FBOAttachment att, int where) {
		bind(glState);
		att.attachToFbo(glState, GL.GL_COLOR_ATTACHMENT0_EXT + where);
		GLSLDisplay.printDebugInfoN("Attached "+att+" to "+glFBONumber[0]+" at "+where);
		attachments[where] = att;
	}

	public void attachDepthOnly(OpenGLState glState, FBOAttachment att) {
		bind(glState);
		attachments[4] = att;
		att.attachToFbo(glState, GL.GL_DEPTH_ATTACHMENT_EXT);
	}

	public void attachStencilOnly(OpenGLState glState, FBOAttachment att) {
		bind(glState);
		attachments[5] = att;
		att.attachToFbo(glState, GL.GL_STENCIL_ATTACHMENT_EXT);
	}
	
	public void attachDepthStencil(OpenGLState glState, FBOAttachment att) {
		bind(glState);
		attachments[4] = att;
		attachments[5] = att;
		att.attachToFbo(glState, GL.GL_DEPTH_ATTACHMENT_EXT);
		att.attachToFbo(glState, GL.GL_STENCIL_ATTACHMENT_EXT);
	}

	/**
	 * Sets this FBO as active and sets Attachment <code>which</which> as current drawBuffer
	 * @param glState
	 * @param which
	 */
	public void drawBuffer(OpenGLState glState, int which) {
		// Multible Targets active!
//		if(activeRenderTarget < 0) {
//			activeRenderTargets[0] = nothingActive[0];
//			activeRenderTargets[1] = nothingActive[1];
//			activeRenderTargets[2] = nothingActive[2];
//			activeRenderTargets[3] = nothingActive[3];
//		}
		bind(glState);
		glState.getGL().glDrawBuffer(GL.GL_COLOR_ATTACHMENT0_EXT + which);
		activeRenderTarget = which;
	}

	/**
	 * Array that enumerates the first 4 OpenGL color attachments 
	 */
	final static int[] all = new int[] {
			GL.GL_COLOR_ATTACHMENT0_EXT, 
			GL.GL_COLOR_ATTACHMENT1_EXT, 
			GL.GL_COLOR_ATTACHMENT2_EXT, 
			GL.GL_COLOR_ATTACHMENT3_EXT}; 

	/**
	 * Sets this FBO as active and sets Attachments 0 up to <code>upTo</which> as current drawBuffers
	 * @see #drawBuffer(OpenGLState, int)
	 * @param glState
	 * @param upTo
	 * @param offset 
	 */
	public void drawBuffers(OpenGLState glState, int upTo, int offset) {
		// Multible Targets active!
		bind(glState);
		glState.getGL().glDrawBuffers(upTo, all, offset);
		activeRenderTarget = -1;
	}

	public void drawBuffers(OpenGLState glState, int upTo) {
		drawBuffers(glState, upTo, 0);
	}

	/**
	 * Binds an Attachments as Texture to Unit <code>where</code>
	 * @param glState
	 * @param which No of Attachment that should be bound
	 * @param where Which Texture Unit to bind to
	 */
	public void bindAttachmentAsTexture(OpenGLState glState, int which, int where) {
		assert(attachments[which] instanceof TextureRenderTarget);
		((TextureRenderTarget)attachments[which]).bindTo(glState.getGL(), GL.GL_TEXTURE0 + where);
	}
	
	/**
	 * Binds all 4 DrawBuffer-Attachments as Textures to Units 0 to 3
	 * only works if at least 4 Textures are attached.
	 * @param glState
	 */
	public void bindAllAttachmentsAsTextures(OpenGLState glState, int upTo, int offset) {
		for(int i = 0; i<upTo; i++)
			((TextureRenderTarget)attachments[i+offset]).bindTo(glState.getGL(), GL.GL_TEXTURE0 + i);
	}
	
	public void bindAllAttachmentsAsTextures(OpenGLState glState, int upTo) {
		bindAllAttachmentsAsTextures(glState, upTo, 0);
	}
	
	public void bindAllAttachmentsAsTextures(OpenGLState glState) {
		bindAllAttachmentsAsTextures(glState, 4, 0);
	}

	/**
	 * Only resizes color attachments
	 * @param glState
	 */
	public void resizeAttachments(OpenGLState glState) {
		for(int i = 0; i < 4; i++)
			if(attachments[i] != null) {
				attachments[i].resize(glState);
				attachDrawBuffer(glState, attachments[i], i);
			}
	}

	public void deleteAttachments(OpenGLState glState, boolean javaonly) {
		for(int i = 0; i < 4; i++)
			if(attachments[i] != null) {
				attachments[i].delete(glState, javaonly);
				attachments[i] = null;
			}
	}

	public boolean isComplete(OpenGLState glState) {
		boolean ok = false;
		GLSLDisplay.printDebugInfo(this + ":");
		switch (glState.getGL().glCheckFramebufferStatusEXT(
				GL.GL_FRAMEBUFFER_EXT)) {
		case GL.GL_FRAMEBUFFER_COMPLETE_EXT:
			GLSLDisplay.printDebugInfoN("Framebuffer complete");
			ok = true;
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
			GLSLDisplay.printDebugInfoN("Framebuffer incomplete format");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
			GLSLDisplay.printDebugInfoN("Framebuffer incomplete attachment");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
			GLSLDisplay.printDebugInfoN("Framebuffer incomplete dimensions");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
			GLSLDisplay.printDebugInfoN("Framebuffer incomplete draw buffer");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT:
			GLSLDisplay
					.printDebugInfoN("Framebuffer incomplete duplicate attachment");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
			GLSLDisplay
					.printDebugInfoN("Framebuffer incomplete missing attachment");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE_EXT:
			GLSLDisplay.printDebugInfoN("Framebuffer incomplete multisample");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
			GLSLDisplay
					.printDebugInfoN("Framebuffer incomplete missing attachment");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_LAYER_COUNT_EXT:
			GLSLDisplay.printDebugInfoN("Framebuffer incomplete layer count");
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS_EXT:
			GLSLDisplay.printDebugInfoN("Framebuffer incomplete layer targets");
			break;
		case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
			GLSLDisplay.printDebugInfoN("Framebuffer unsupported");
			break;
		default:
			GLSLDisplay.printDebugInfoN("ERROR!!!!");
		}
		return ok;
	}

	@Override
	public String toString() {
		String s =  "FBO[I:"+glFBONumber[0]+",ATT:[";
		for(int i = 0; i<6; i++)
			if(attachments[i]!=null)
				s+=attachments[i]+",";
		s+="]]";
		return s;
	}
	
	public int estimateSizeInByteForColor() {
		int memory = 0;
		for(int i = 0; i<4; i++)
			if(attachments[i]!=null)
				memory += attachments[i].estimateSizeInByte();
		return memory;
	}
}
