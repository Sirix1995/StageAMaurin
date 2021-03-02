package de.grogra.imp3d.glsl.light;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import de.grogra.imp3d.Camera;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.light.shadow.GLSLShadowMap;
import de.grogra.imp3d.glsl.renderpass.FullQualityRenderPass;
import de.grogra.imp3d.glsl.renderpass.FullRenderPass;
import de.grogra.imp3d.objects.Parallelogram;
import de.grogra.imp3d.objects.Sky;
import de.grogra.imp3d.shading.SunSkyLight;
import de.grogra.ray.physics.Light;

/**
 * Process deferred shading textures and render results into float-Textures
 * (ping-pong'ing)
 * 
 * @author Konni Hartmann
 */
public class LightingProcessPass extends FullRenderPass {
	@Override
	protected int getID() {
		return 2;
	}

	private ShadowMapGenerationPass smgp = new ShadowMapGenerationPass();

	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof LightPos);
		GL gl = glState.getGL();
		LightPos l = (LightPos) data;
		
		if(l.getLight() instanceof Sky) {
			if (!glState.BGFound || glState.getBGShader() == null) {
//				System.err.println("cancelling light");
				return;
			}
		}
		
		gl.glPopAttrib();
		ViewPerspective(glState);

		glState.disable(OpenGLState.STENCIL_TEST);
		
		glState.setDepthMask(true);
		glState.enable(OpenGLState.DEPTH_TEST);

		// deactivate z Pos
		gl.glActiveTexture(GL.GL_TEXTURE8);
		gl.glBindTexture(GL.GL_TEXTURE_2D, 0);
		
		boolean shadows = (!l.getLight().isShadowless())
				& disp.isOptionShowShadows()
				& !(l.getLight() instanceof Sky)
				& !(l.getLight().getLightType() == Light.AREA);

		if (shadows) {
			gl.glActiveTexture(GL.GL_TEXTURE7);
			gl.glBindTexture(GL.GL_TEXTURE_RECTANGLE_ARB, 0);
			gl.glMatrixMode(GL.GL_TEXTURE);
			gl.glActiveTexture(GL.GL_TEXTURE0 + FullQualityRenderPass.CUSTOM_MATRIX_1);
			gl.glLoadIdentity();
			gl.glMatrixMode(GL.GL_MODELVIEW);
		}

		if (l.getLight() instanceof Sky) {
			gl.glActiveTexture(GL.GL_TEXTURE7);
			gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, 0);
			gl.glActiveTexture(GL.GL_TEXTURE6);
			gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, 0);
		}

		deactivateTextures(gl, 6, GL.GL_TEXTURE_RECTANGLE_ARB);
	}

	LightShaderConfiguration lsc = new LightShaderConfiguration();
	private RenderToSkyCubePass rtsc = new RenderToSkyCubePass();

	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		assert (data instanceof LightPos);
		GL gl = glState.getGL();
		LightPos light = (LightPos) data;

		if(light.getLight() instanceof Sky) {
			if (!glState.BGFound || glState.getBGShader() == null) {
//				System.err.println("cancelling light");
				return;
			}
			rtsc.process(disp, glState, null);
		}
		
		boolean shadows = (!light.getLight().isShadowless())
				& disp.isOptionShowShadows()
				& !(light.getLight() instanceof Sky)
				& !(light.getLight().getLightType() == Light.AREA);

		// XXX: optimize ... by adding another processing pass... problems with
		// floatRT!
		if (shadows) {
			// get a shadowmap cached for the current light and type
			GLSLShadowMap sMap = glState.SM_Manager.getDefaultCachedMap(light
					.getLight());
			// if it was a new texture it is not initialized
			sMap.create(gl);
			// set current transformations (this should be changed to setPos and
			// setDir
			sMap.setLightTransf(light);

			// process the shadowmap (smgp is responsible to set bounding
			// conditions like
			// binding sMap as a rendertarget etc.
			smgp.process(disp, glState, sMap);

			Matrix4d ViewToWorld = new Matrix4d();
			ViewToWorld.set(glState.getInvWorldToView());
			sMap.setupTextureMatrices(glState, ViewToWorld, light);

			// Now sMap should be filled correctly put it back into the cache
//			glState.SM_Manager.updateCache(light.getLight(), sMap);
		}

		// gl.glBindFramebufferEXT(GL.GL_FRAMEBUFFER_EXT, glState.fboHDR);
		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);
		gl.glViewport(0, 0, glState.width, glState.height);
		// Set stencil test to only work on drawn fragments
		glState.switchFloatRT();
		GLSLDisplay.printDebugInfoN("Render Light to: "
				+ (4 + glState.getFloatRT()));
		glState.getHDRFBO().drawBuffer(glState, glState.getFloatRT());

		// XXX: Will trigger shaderchanged or currentShader in
		// CachedShaderCollection
		GLSLLightShader cs = (GLSLLightShader) glState.csc.getCacheEntry(
				glState, disp, lsc, light.getLight());
		if (cs == null) {
			GLSLDisplay.printDebugInfoN("! " + light.getLight().toString()
					+ " : not known");
			return;
		}

		ViewOrtho(glState);
		glState.disable(OpenGLState.DEPTH_TEST);

		// Disable Write to Depth-Buffer
		glState.setDepthMask(false);

		glState.getDeferredShadingFBO().bindAllAttachmentsAsTextures(glState);
		// Add read input for HDR-Pass
		glState.getHDRFBO().bindAttachmentAsTexture(glState, glState
				.getFloatRTLast(), 4);
		glState.getAlphaFBO().bindAttachmentAsTexture(glState, 0, 5);
		
		// Setup depth texture
		glState.getDeferredShadingDepthTRT().bindTo(glState, 8);
		
//		if (light.getLight().getLightType() == Light.AREA) {
//			gl.glActiveTexture(GL.GL_TEXTURE0
//					+ FullQualityRenderPass.CUSTOM_MATRIX_2);
//			gl.glMatrixMode(GL.GL_TEXTURE);
//			Matrix4d invLightTransform = new Matrix4d();
//			Matrix4d eyeToLight = new Matrix4d();
//			invLightTransform.set(light.getLightTransform());
//			eyeToLight.set(glState.getInvWorldToView());
//			eyeToLight.mul(invLightTransform);
//			glState.loadMatrixd(eyeToLight);
//			gl.glMatrixMode(GL.GL_MODELVIEW);
//		}
		
		if (light.getLight() instanceof Sky) {
			Matrix4d ViewToWorld = new Matrix4d();
			ViewToWorld.set(glState.getWorldToView());
			ViewToWorld.m30 = 0;
			ViewToWorld.m31 = 0;
			ViewToWorld.m32 = 0;
			ViewToWorld.m03 = 0;
			ViewToWorld.m13 = 0;
			ViewToWorld.m23 = 0;
			ViewToWorld.invert();
			
			gl.glActiveTexture(GL.GL_TEXTURE0 + FullQualityRenderPass.CUSTOM_MATRIX_1);
			gl.glMatrixMode(GL.GL_TEXTURE);
			glState.loadMatrixd(ViewToWorld);
			gl.glMatrixMode(GL.GL_MODELVIEW);

			glState.skyCube.bindTo(gl, GL.GL_TEXTURE6);
			glState.skyDiffuseCube.bindTo(gl, GL.GL_TEXTURE7);

			GLSLDisplay.printDebugInfoN("Bound Env-Map");
		}

		glState.enable(OpenGLState.STENCIL_TEST);
		gl.glStencilFunc(GL.GL_EQUAL, 0x1, 0x1);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_KEEP);

		cs.activateShader(glState, disp, light);
		GLSLDisplay.printDebugInfoN("Rendering Light with Shader: " + cs);

	}

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		Camera c = disp.getView3D().getCamera();
		drawPrjQuad(glState, c);
	}

	@Override
	public void process(GLSLDisplay disp, OpenGLState glState, Object data) {
		super.process(disp, glState, data);
	}
}
