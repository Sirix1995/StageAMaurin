package de.grogra.imp3d.glsl;

import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

import de.grogra.imp3d.glsl.utility.FrameBufferObject;
import de.grogra.imp3d.glsl.utility.RenderBuffer;

public class JOGL_Test {

	private int dss = DSS_COMBINED;
	
	public int getDepthStencilSetup() {
		return dss;
	}
	
	public void GLTest(GL gl, Logger logger) {
		int maxbuffers[] = new int[1];
		
		String glver = gl.glGetString(GL.GL_VERSION);
		
		StringTokenizer st = new StringTokenizer(glver, ". ");
		int major = Integer.valueOf(st.nextToken());
		int minor = Integer.valueOf(st.nextToken());
		logger.info("OpenGL Version is "+major+"."+minor);
		
		if (major < 2)
		{
			logger.info("Your GPU does not support OpenGL 2.x");
			throw new RuntimeException("OpenGL >=2.0 not found");
		}

		String glslver = gl.glGetString(GL.GL_SHADING_LANGUAGE_VERSION);
		st = new StringTokenizer(glslver, ". ");
		major = Integer.valueOf(st.nextToken());
		minor = Integer.valueOf(st.nextToken());
		logger.info("GLSL Version is: "+major+"."+minor);

		boolean passed = (major > 1 || (major == 1 && minor >= 1)), evaluate = false;
		
		gl.glGetIntegerv (GL.GL_MAX_TEXTURE_SIZE, maxbuffers, 0);
		logger.info("Max Texturesize is: "+maxbuffers[0]);
		passed &= (maxbuffers[0] >= 2048);
		
		gl.glGetIntegerv (GL.GL_DEPTH_BITS, maxbuffers, 0);
		logger.info("Depth is: "+maxbuffers[0]);
		passed &= (maxbuffers[0] >= 24);

		logger.info("Testing GL_EXT_packed_depth_stencil: "+ (evaluate = gl.isExtensionAvailable("GL_EXT_packed_depth_stencil")) );
		passed &= evaluate;

		logger.info("Testing GL_EXT_framebuffer_object: " + (evaluate = gl.isExtensionAvailable("GL_EXT_framebuffer_object")));
		passed &= evaluate;

		logger.info("Testing GL_ARB_draw_buffers: " + (evaluate = gl.isExtensionAvailable("GL_ARB_draw_buffers")));
		passed &= evaluate;

		logger.info("Testing Method glGenRenderbuffersEXT: " + (evaluate = gl.isFunctionAvailable("glGenRenderbuffersEXT")));
		passed &= evaluate;
		
		gl.glGetIntegerv(GL.GL_MAX_COLOR_ATTACHMENTS_EXT, maxbuffers, 0);
		logger.info("Max Colorbuffer is: "+maxbuffers[0]);
		passed &= (maxbuffers[0] >= 4);

		logger.info("Testing GL_ARB_half_float_pixel: " + (evaluate = gl.isExtensionAvailable("GL_ARB_half_float_pixel")));
		passed &= evaluate;

		logger.info("Testing GL_ARB_texture_rectangle: " + (evaluate = gl.isExtensionAvailable("GL_ARB_texture_rectangle")));
		passed &= evaluate;

		logger.info("Testing GL_ARB_texture_cube_map: " + (evaluate = gl.isExtensionAvailable("GL_ARB_texture_cube_map")));
		passed &= evaluate;

		if(!passed) throw new RuntimeException("One or more OpenGL Extensions or Capabilites do not meet the minimum requirements to run Proteus");
		logger.info("All Tests passed!");

		FBOTest(gl, logger);
	}
	
	GLU glu = new GLU();
	private int testGLError(GL gl, Logger logger, String desc) {	
		int error = gl.glGetError();
		if (error != GL.GL_NO_ERROR)
			logger.info(desc + glu.gluErrorString(error));
		else 
			logger.info(desc + "passed");
		return error;
	}
	
	private int testFBOError(GL gl, Logger logger, String desc) {
		int state = gl.glCheckFramebufferStatusEXT(GL.GL_FRAMEBUFFER_EXT);
		String state_desc = "Framebuffer complete";
		switch (state) {
		case GL.GL_FRAMEBUFFER_COMPLETE_EXT:
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
			state_desc = "Framebuffer incomplete format";
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
			state_desc = "Framebuffer incomplete attachment";
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
			state_desc = "Framebuffer incomplete dimensions";
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
			state_desc = "Framebuffer incomplete draw buffer";
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT_EXT:
			state_desc = "Framebuffer incomplete duplicate attachment";
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
			state_desc = "Framebuffer incomplete missing attachment";
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE_EXT:
			state_desc = "Framebuffer incomplete multisample";
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
			state_desc = "Framebuffer incomplete missing read buffer";
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_LAYER_COUNT_EXT:
			state_desc = "Framebuffer incomplete layer count";
			break;
		case GL.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS_EXT:
			state_desc = "Framebuffer incomplete layer targets";
			break;
		case GL.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
			state_desc = "Framebuffer unsupported";
			break;
		default:
			state_desc = "unknown error!";
		}
		
		if(state != GL.GL_FRAMEBUFFER_COMPLETE_EXT)
			logger.info(desc+state_desc);
		else
			logger.info(desc+state_desc);
		return state;
	}

	public static final int DSS_NONE = 0;
	public static final int DSS_DEPTH_ONLY = DSS_NONE + 1;
	public static final int DSS_SEPERATE = DSS_DEPTH_ONLY + 1;
	public static final int DSS_COMBINED = DSS_SEPERATE + 1;
	private static final String DSS_DESCRIPTION[] = {"None", "depth only", "seperate", "combined"};
	
	private void FBOTest(GL gl, Logger logger) {
		int error = GL.GL_NO_ERROR;
		dss = DSS_COMBINED;
	
		OpenGLState glState = new OpenGLState();
		glState.initSize(1, 1);
		glState.setGL(gl);
		
		// --- Test RenderBuffer creation
		RenderBuffer depthRB = new RenderBuffer();
		depthRB.create(glState, GL.GL_DEPTH_COMPONENT24_ARB);
		error = testGLError(gl, logger, "GL_DEPTH_COMPONENT24_ARB: ");

		if(error != GL.GL_NO_ERROR)
			throw new RuntimeException("Creating DepthBuffer failed");
		
		// test for combined depth/stencil buffer
		RenderBuffer depthStencilRB = new RenderBuffer();
		depthStencilRB.create(glState, GL.GL_DEPTH24_STENCIL8_EXT);
		error = testGLError(gl, logger, "GL_DEPTH24_STENCIL8: ");
		
		RenderBuffer stencilRB = new RenderBuffer();
		if (error != GL.GL_NO_ERROR) {
			// test for stencil buffer
			stencilRB.create(glState, GL.GL_STENCIL_INDEX8_EXT);
			error = testGLError(gl, logger, "GL_STENCIL_INDEX8_EXT: ");
			
			if(error != GL.GL_NO_ERROR) {
				dss = DSS_DEPTH_ONLY;
				logger.info("OpenGL(Proteus) was not able to create a valid Stencil-Buffer. " +
						" Fallback to simplified output"); 
			}
			else {
				dss = DSS_SEPERATE;
			}			
		}
		
		// --- Test FBO combinations
		FrameBufferObject testFBO = null;
		while(dss > DSS_NONE) {
			if(testFBO == null)
				testFBO = new FrameBufferObject();	
			else
				testFBO.delete(glState, false);

			testFBO.create(glState);			

			if(testGLError(gl, logger, "FBO creation: ") != GL.GL_NO_ERROR)
				throw new RuntimeException("Failed creating empty Frame-Buffer-Object");
						
			// Test FBO with color, depth and stencil attachment
			switch (dss) {
			case DSS_COMBINED:
				testFBO.attachDepthStencil(glState, depthStencilRB);
				break;
			case DSS_SEPERATE:
				testFBO.attachStencilOnly(glState, stencilRB);
			case DSS_DEPTH_ONLY:
				testFBO.attachDepthOnly(glState, depthRB);
				break;
			}
			
			gl.glDrawBuffer(GL.GL_NONE);
			gl.glReadBuffer(GL.GL_NONE);

			// setting read buffer to GL_NONE results in 
			// invalid enumerant on some systems
			gl.glGetError();

			error = testFBOError(gl, logger, "FBO-Setup: Depth/Stencil["+DSS_DESCRIPTION[dss]+"]: ");
			if(error == GL.GL_FRAMEBUFFER_COMPLETE_EXT)
				break;
			
			dss--;
		}
		
		if(dss == DSS_NONE)
			throw new RuntimeException("Not able to create depth/stencil FBO");

		logger.info("Will use \""+DSS_DESCRIPTION[dss]+"\"-depth/stencil buffer");
		
		// Test FBO with depth-Texture only	
		
		// --- Remove all created buffers
		depthStencilRB.delete(glState, false);
		stencilRB.delete(glState, false);
		depthRB.delete(glState, false);
		
		testFBO.delete(glState, false);
		
		// clean error flag
		// since this context will be deleted
		// all textures and rbs will be removed
		// thus errors occuring while cleanup
		// may be ignored
		gl.glGetError();
		return;
		
		/*
		// test for seperate depth, stencil buffer
		
		// --- Test Texture creation
		TextureRenderTarget depthText = new TextureRenderTarget();
		TextureRenderTarget depthCube = new TextureRenderTarget();
		TextureRenderTarget floatTex = new TextureRenderTarget();
		TextureRenderTarget intTex = new TextureRenderTarget();
		*/
	}
}
