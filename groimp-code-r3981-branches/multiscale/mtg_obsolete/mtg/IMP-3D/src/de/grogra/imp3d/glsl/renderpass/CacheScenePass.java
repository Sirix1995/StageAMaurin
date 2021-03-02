package de.grogra.imp3d.glsl.renderpass;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.GLSLUpdateCache;
import de.grogra.imp3d.glsl.Measures;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.GLDisplay.GLVisitor;
import de.grogra.imp3d.glsl.utility.Drawable;
import de.grogra.imp3d.glsl.utility.GLSLShader;

/**
 * Render scene-graph to deferred shading textures using MaterialShaders (these
 * are activated by GLDisplay per Node)
 * 
 * @author Konni Hartmann
 */
public class CacheScenePass extends FullRenderPass {
	
	@Override
	protected int getID() {
		return 0;
	}
	
	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		gl.glPopAttrib();
		
		gl.glColorMask(true, true, true, true);
	}

	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
		GL gl = glState.getGL();
		glState.getDeferredShadingFBO().drawBuffers(glState, 4);

		gl.glViewport(0, 0, glState.width, glState.height);
		gl.glPushAttrib(GL.GL_VIEWPORT_BIT);

		gl.glClearStencil(0);
		gl.glClearColor(0, 0, 0, 0);

		glState.enable(OpenGLState.CULLING);
		glState.setFaceCullingMode(GL.GL_BACK);

		gl.glClear(GL.GL_DEPTH_BUFFER_BIT | GL.GL_COLOR_BUFFER_BIT
				| GL.GL_STENCIL_BUFFER_BIT);
		
		gl.glDepthFunc(GL.GL_LEQUAL);
		glState.enable(OpenGLState.DEPTH_TEST);
		
		// This should only change 1st bit
		glState.enable(OpenGLState.STENCIL_TEST);
		gl.glStencilFunc(GL.GL_ALWAYS, 0x2, 0x2);
		gl.glStencilOp(GL.GL_KEEP, GL.GL_KEEP, GL.GL_REPLACE);
	}

	int graphStamp = -1;
	boolean first = true;

	@Override
	public void postDrawCallback(Drawable dr, OpenGLState glState,
			GLSLDisplay disp) {
		dr.activateGLSLShader(glState, disp, glState.isAssumeTranspMaterials());
	}
	
//	int[] index = new int[1];
	
	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {

		if (newVisit == null)
			newVisit = new GLSLUpdateCache(disp);
//		if (cachedVisit == null)
//			cachedVisit = new GLSLCachedVisitor(disp);
		
		GLVisitor old = disp.getVisitor();

		GL gl = glState.getGL();
		
		newVisit.init(disp.getRenderGraphState(), glState.getWorldToView(), 0);
		disp.setVisitor(newVisit);

		if(disp.isOptionShowGrid())
			disp.drawGrid(glState.getGL());

		// Will only be called if stamp has changed...
		
		
		/****
		 * Statistically draw full not z-pass
		 ****/
		
//		glState.frustumCullingTester.setupViewFrustum(disp);
		
		if(glState.resetCache || (graphStamp != disp.getView().getGraph().getStamp())) 
		{
			glState.BGFound = false;
			glState.volume.initSceneExtent(disp);
			glState.deferredSolidRenderable.init();
			glState.deferredTranspRenderable.init();
			glState.deferredLabelRenderable.init();
			glState.deferredToolRenderable.init();
			glState.defLight.clear();
			glState.getShapeManager().init();
			GLSLDisplay.printDebugInfoN("**************** ACCEPT *******************");
			disp.getView().getGraph().accept(null, newVisit, null);
			Measures.getInstance().setData(newVisit.getAll(), newVisit.getRend());
			
			GLSLDisplay.printDebugInfoN("***************** END  ********************");

			glState.setupBGShader(null);
			
			glState.deferredSolidRenderable.cleanup();
			glState.deferredLabelRenderable.cleanup();
			glState.deferredToolRenderable.cleanup();
			glState.deferredTranspRenderable.cleanup();
			java.util.Collections.sort(glState.deferredSolidRenderable);
			java.util.Collections.sort(glState.deferredTranspRenderable);
			glState.getShapeManager().cleanup();
			glState.resetCache = false;
			glState.setAssumeTranspMaterials(!glState.deferredTranspRenderable.isEmpty());
			
			if(glState.isAssumeTranspMaterials()) 
				gl.glColorMask(false, false, false, false);
			else 
				gl.glStencilFunc(GL.GL_ALWAYS, 0x1, 0x1);
			
//			gl.glDeleteLists(index[0], 1);
//			index[0] = 0;
			
			renderVector(disp, glState.getWorldToView(), true, glState.deferredSolidRenderable);
			if(!glState.isAssumeTranspMaterials()) {
				glState.volume.finish();
				gl.glColorMask(false, false, false, false);
				gl.glStencilFunc(GL.GL_ALWAYS, 0x2, 0x2);
			}
			renderVector(disp, glState.getWorldToView(), true, glState.deferredToolRenderable);
		} 
		else {
			if(glState.isAssumeTranspMaterials()) 
				gl.glColorMask(false, false, false, false);
			else 
				gl.glStencilFunc(GL.GL_ALWAYS, 0x1, 0x1);
			
//			if(index[0]==0)
//			{
//				index[0] = gl.glGenLists(1);
//				gl.glBegin(GL.GL_COMPILE_AND_EXECUTE);
//				{
				renderAndUpdateVector(disp, glState.getWorldToView(),
					glState.deferredSolidRenderable);
//				}
//				gl.glEnd();
//			}
//			else {
//				gl.glLoadIdentity();
//				glState.deferredSolidRenderable.get(0).activateGLSLShader(glState, disp);
//				gl.glCallList(index[0]);
//			}
			glState.setActiveProgram(0);

			if(!glState.isAssumeTranspMaterials()) {
				glState.volume.finish();
				gl.glColorMask(false, false, false, false);
				gl.glStencilFunc(GL.GL_ALWAYS, 0x2, 0x2);
			}
			
			renderAndUpdateVector(disp, glState.getWorldToView(),
					glState.deferredToolRenderable);
		}
		
		// We need z Information for deferred shading and depth peeling
		glState.getPeelingFarDepthTRT().bindTo(glState, 0);
		gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_DEPTH_COMPONENT24, 0, 0, glState.width, glState.height, 0);
		// This ensures deferred shading gets the correct depth texture
		glState.setDeferredShadingDepthTRT(glState.getPeelingFarDepthTRT());

		disp.setVisitor(old);

		graphStamp = disp.getView().getGraph().getStamp();

	}
}
