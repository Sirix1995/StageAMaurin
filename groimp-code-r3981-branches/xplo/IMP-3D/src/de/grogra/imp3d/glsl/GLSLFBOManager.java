package de.grogra.imp3d.glsl;

/* 
 * These may be useful for storing a float in 4 bytes
 * 
 * float unpackFloatFromVec4i(const vec4 value)
 * {
 *   const vec4 bitSh = vec4(1.0/(256.0*256.0*256.0), 1.0/(256.0*256.0), 1.0/256.0, 1.0);
 *   return(dot(value, bitSh));
 * }
 * 
 * vec4 packFloatToVec4i(const float value)
 * {
 *   const vec4 bitSh = vec4(256.0*256.0*256.0, 256.0*256.0, 256.0, 1.0);
 *   const vec4 bitMsk = vec4(0.0, 1.0/256.0, 1.0/256.0, 1.0/256.0);
 *   vec4 res = fract(value * bitSh);
 *   res -= res.xxyz * bitMsk;
 *   return res;
 * } 
 */

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.utility.FrameBufferObject;
import de.grogra.imp3d.glsl.utility.RenderBuffer;
import de.grogra.imp3d.glsl.utility.TextureRenderTarget;

/**
 * A container-class managing all FBOs used in oglslview
 * 
 * @author Konni Hartmann
 * @version 0.1.0
 * @date 17.09.2009
 */
public class GLSLFBOManager {
	FrameBufferObject deferredShadingFBO = new FrameBufferObject();
	FrameBufferObject HDRFBO = new FrameBufferObject();
	FrameBufferObject alphaFBO = new FrameBufferObject();
	FrameBufferObject shadowFBO = new FrameBufferObject();
	FrameBufferObject cubeFBO = new FrameBufferObject();

	FrameBufferObject dualDepthFBO = new FrameBufferObject();

	RenderBuffer depthRB = new RenderBuffer();

	TextureRenderTarget peelingFarDepthTRT = new TextureRenderTarget();
	RenderBuffer peelingRB = new RenderBuffer();
	TextureRenderTarget[] textureRenderTargets = null;

	TextureRenderTarget peelingNearDepthTRT = new TextureRenderTarget();

	/**
	 * Setup FBO for the first renderpass. This means depthbuffer plus 4
	 * textures as rendertargets.
	 * @param glState The OpenGLState for the current active GLContext
	 */
	protected void setupFBO(OpenGLState glState) {

		cubeFBO.create(glState);
		
		/********** NORMAL FBO ***********/
		// Create FrameBufferObject
		deferredShadingFBO.create(glState);

		// Create and Bind a Depthbuffer

		// Bind Depthbuffer to FBO
		// Bind Stencilbuffer to FBO
		switch (GLSLDisplay.getDepthStencilSetup()) {
		case JOGL_Test.DSS_COMBINED:
			depthRB.create(glState, GL.GL_DEPTH24_STENCIL8_EXT);
			deferredShadingFBO.attachDepthStencil(glState, depthRB);
			break;
		case JOGL_Test.DSS_SEPERATE:
		case JOGL_Test.DSS_DEPTH_ONLY:
		default:
			depthRB.create(glState, GL.GL_DEPTH_COMPONENT24);
			deferredShadingFBO.attachDepthOnly(glState, depthRB);
		}
	
		glState.setFBO(0);

		// Create Depth Peeling FBO
		peelingFarDepthTRT.create(glState, TextureRenderTarget.DEPTHUB,
				GL.GL_TEXTURE_2D);
		
		dualDepthFBO.create(glState);
		switch (GLSLDisplay.getDepthStencilSetup()) {
		case JOGL_Test.DSS_COMBINED:
			peelingRB.create(glState, GL.GL_DEPTH24_STENCIL8_EXT);
			dualDepthFBO.attachDepthStencil(glState, peelingRB);
			break;
		case JOGL_Test.DSS_SEPERATE:
		case JOGL_Test.DSS_DEPTH_ONLY:
		default:
			peelingRB.create(glState, GL.GL_DEPTH_COMPONENT24);
			dualDepthFBO.attachDepthOnly(glState, peelingRB);
		}	
		
		if (peelingNearDepthTRT == null)
			peelingNearDepthTRT = new TextureRenderTarget();
		peelingNearDepthTRT.create(glState, TextureRenderTarget.DEPTHUB,
				GL.GL_TEXTURE_2D);
		
		dualDepthFBO.isComplete(glState);

		glState.setFBO(0);

		/****** HDR ******/
		// Create FrameBufferObject
		HDRFBO.create(glState);

		// Bind Depthbuffer to FBO
		// Bind Stencilbuffer to FBO
		switch (GLSLDisplay.getDepthStencilSetup()) {
		case JOGL_Test.DSS_COMBINED:
			HDRFBO.attachDepthStencil(glState, depthRB);
			break;
		case JOGL_Test.DSS_SEPERATE:
		case JOGL_Test.DSS_DEPTH_ONLY:
		default:
			HDRFBO.attachDepthOnly(glState, depthRB);
		}	

		/****** ALPHA ******/
		// Create FrameBufferObject
		alphaFBO.create(glState);

		// Bind Depthbuffer to FBO
		// Bind Stencilbuffer to FBO
		switch (GLSLDisplay.getDepthStencilSetup()) {
		case JOGL_Test.DSS_COMBINED:
			alphaFBO.attachDepthStencil(glState, depthRB);
			break;
		case JOGL_Test.DSS_SEPERATE:
		case JOGL_Test.DSS_DEPTH_ONLY:
		default:
			alphaFBO.attachDepthOnly(glState, depthRB);			
		}	

		/****** Shadow ******/
		shadowFBO.create(glState);

		glState.setFBO(0);

		// Create RenderTargetTextures
		/**
		 * 4 RT's for deferred shading + 2 RT's for HDR-ping-pong'ing + 1 RT for
		 * destination alpha
		 */
		if (textureRenderTargets == null)
			textureRenderTargets = new TextureRenderTarget[7];
		for (int i = 0; i < 6; ++i) {
			textureRenderTargets[i] = new TextureRenderTarget();
			textureRenderTargets[i].create(glState,
					i < 4 ? TextureRenderTarget.RGBA16F
							: TextureRenderTarget.RGBA16F);
		}
		textureRenderTargets[6] = new TextureRenderTarget();
		textureRenderTargets[6].create(glState, TextureRenderTarget.RGBA8UB);

		// Attach to FBO
		int i;
		for (i = 0; i < 4; i++)
			deferredShadingFBO.attachDrawBuffer(glState,
					textureRenderTargets[i], i);
		GLSLDisplay.printDebugInfo("DeferredFBO ");
		deferredShadingFBO.isComplete(glState);

		for (i = 0; i < 2; i++)
			HDRFBO.attachDrawBuffer(glState, textureRenderTargets[4 + i], i);
		GLSLDisplay.printDebugInfo("HDRFBO ");
		HDRFBO.isComplete(glState);

		alphaFBO.attachDrawBuffer(glState, textureRenderTargets[6], 0);
		GLSLDisplay.printDebugInfo("AlphaFBO ");
		alphaFBO.isComplete(glState);

		// Reset RenderTarget
		glState.setFBO(0);
	}

	/**
	 * Resizes FBO. If View is resized associated FBO use wrong sizes. To fix this
	 * the FBOs are deleted and recreated.
	 * 
	 * @param disp Display object to get new resolution
	 * @param glState New GL context
	 */
	public void resize(GLSLDisplay disp, OpenGLState glState) {
		// if(gl != this.gl)
		// System.err.println("Resize called with wrong Context!");
		GLSLDisplay.printDebugInfoN("Resizing FBO:");

		glState.initSize(disp.getView().getSize().width, disp.getView().getSize().height);

		peelingFarDepthTRT.resize(glState);
		peelingNearDepthTRT.resize(glState);

		depthRB.resize(glState);
		peelingRB.resize(glState);

		// dualDepthFBO.resizeAttachments(glState);
		switch (GLSLDisplay.getDepthStencilSetup()) {
		case JOGL_Test.DSS_COMBINED:
			dualDepthFBO.attachDepthStencil(glState, peelingRB);
			break;
		case JOGL_Test.DSS_SEPERATE:
		case JOGL_Test.DSS_DEPTH_ONLY:
		default:
			dualDepthFBO.attachDepthOnly(glState, peelingRB);
		}	
		dualDepthFBO.isComplete(glState);

		deferredShadingFBO.resizeAttachments(glState);
		switch (GLSLDisplay.getDepthStencilSetup()) {
		case JOGL_Test.DSS_COMBINED:
			deferredShadingFBO.attachDepthStencil(glState, depthRB);
			break;
		case JOGL_Test.DSS_SEPERATE:
		case JOGL_Test.DSS_DEPTH_ONLY:
		default:
			deferredShadingFBO.attachDepthOnly(glState, depthRB);
		}	
		deferredShadingFBO.isComplete(glState);
		
		HDRFBO.resizeAttachments(glState);
		switch (GLSLDisplay.getDepthStencilSetup()) {
		case JOGL_Test.DSS_COMBINED:
			HDRFBO.attachDepthStencil(glState, depthRB);
			break;
		case JOGL_Test.DSS_SEPERATE:
		case JOGL_Test.DSS_DEPTH_ONLY:
		default:
			HDRFBO.attachDepthOnly(glState, depthRB);
		}	
		HDRFBO.isComplete(glState);
		
		alphaFBO.resizeAttachments(glState);
		switch (GLSLDisplay.getDepthStencilSetup()) {
		case JOGL_Test.DSS_COMBINED:
			alphaFBO.attachDepthStencil(glState, depthRB);
			break;
		case JOGL_Test.DSS_SEPERATE:
		case JOGL_Test.DSS_DEPTH_ONLY:
		default:
			alphaFBO.attachDepthOnly(glState, depthRB);
		}	
		alphaFBO.isComplete(glState);

	}

	/**
	 * Deletes all associated FBOs and Renderbuffers.
	 * @param glState Current OpenGLState.
	 * @param javaonly true, if only Java-Side of OpenGL-Objects 
	 * should be deleted.
	 */
	public void deleteAll(OpenGLState glState, boolean javaonly) {
		GLSLDisplay.printDebugInfoN("Deleting FBO:");
		deferredShadingFBO.deleteAttachments(glState, javaonly);
		deferredShadingFBO.delete(glState, javaonly);
		HDRFBO.deleteAttachments(glState, javaonly);
		HDRFBO.delete(glState, javaonly);
		alphaFBO.deleteAttachments(glState, javaonly);
		alphaFBO.delete(glState, javaonly);
		shadowFBO.deleteAttachments(glState, javaonly);
		shadowFBO.delete(glState, javaonly);

		dualDepthFBO.deleteAttachments(glState, javaonly);
		dualDepthFBO.delete(glState, javaonly);

		peelingNearDepthTRT.delete(glState, javaonly);
		peelingFarDepthTRT.delete(glState, javaonly);

		peelingRB.delete(glState, javaonly);
		depthRB.delete(glState, javaonly);
		
		cubeFBO.delete(glState, javaonly);
	}
}
