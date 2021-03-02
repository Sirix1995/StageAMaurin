package de.grogra.imp3d.glsl.renderpass.nostencil;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.light.ProcessLightingPass;
import de.grogra.imp3d.glsl.renderpass.RenderPass;

public class DepthPeelingPass extends RenderPass {

	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
	}

	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		if(glState.occlusionQuery == null) {
			glState.occlusionQuery = new int[1];
			gl.glGenQueries(1, glState.occlusionQuery, 0);
		}
		gl.glDisable(GL.GL_CULL_FACE);
	}

	private PrepareAlphaPass pap = new PrepareAlphaPass();
	private ExtractLayerPass elp = new ExtractLayerPass();
	private ExtractSucessiveLayerPass eslp = new ExtractSucessiveLayerPass();
	private CachedRenderPass crp = new CachedRenderPass();
	private ProcessLightingPass plp = new ProcessLightingPass();

	int queryResult[] = new int[1];

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		
		GL gl = glState.getGL();
		
		// Draw Transp-Elements, if there are any
		int i = 0;

		
		while ((i < disp.getMaxDepth()) && !glState.deferredTranspRenderable.isEmpty()){

			if(i==0) {
				elp.process(disp, glState, new Integer(0));
				// This ensures lighting gets the right depth texture
				glState.setDeferredShadingDepthTRT(glState.getPeelingNearDepthTRT());
				glState.volume.finish();
			}
			else {
				gl.glGetQueryObjectiv(glState.occlusionQuery[0], GL.GL_QUERY_RESULT,
						queryResult, 0);
				if (queryResult[0] <= 10)
					break;
				eslp.process(disp, glState, new Integer(i%2));
			}
			crp.process(disp, glState, glState.deferredTranspRenderable);
			
			copyDepthBuffer(disp, glState);
			
			// light the scene
			plp.process(disp, glState, null);
			pap.process(disp, glState, null);
			//XXX Setup parameter for cftp
			i++;
		}

		gl.glEnable(GL.GL_CULL_FACE);
		
		if(i != 0) {
			// This ensures lighting gets the right depth texture
			glState.setDeferredShadingDepthTRT(glState.getPeelingFarDepthTRT());
			glState.getDeferredShadingFBO().attachDepthStencil(glState, glState.getDepthRB());
			glState.getHDRFBO().attachDepthStencil(glState, glState.getDepthRB());
			glState.getAlphaFBO().attachDepthStencil(glState, glState.getDepthRB());	
		} else
			glState.volume.finish();
		
		crp.process(disp, glState, glState.deferredSolidRenderable);
	}

	private void copyDepthBuffer(GLSLDisplay disp, OpenGLState glState) {
		GL gl = glState.getGL();
		glState.getDualDepthFBO().bind(glState);
		glState.getPeelingNearDepthTRT().bindTo(glState, 0);
		gl.glDrawBuffer(GL.GL_NONE);
		gl.glReadBuffer(GL.GL_NONE);
		// glState.dualDepthFBO.isComplete(glState);
//		gl.glCopyTexImage2D(GL.GL_TEXTURE_RECTANGLE_ARB, 0, GL.GL_DEPTH_COMPONENT24, 0, 0,
//				glState.width, glState.height, 0);		
		gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_DEPTH_COMPONENT24, 0, 0, glState.width, glState.height, 0);
	}
}
