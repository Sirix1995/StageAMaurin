package de.grogra.imp3d.glsl.renderpass;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector4d;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.light.ProcessLightingPass;
//import de.grogra.imp3d.glsl.light.RenderToSkyCubePass;
import de.grogra.imp3d.glsl.light.RenderToSkyCubePass;

public class FullQualityRenderPass extends RenderPass {
	
	
	@Override
	protected void epilogue(GLSLDisplay disp, OpenGLState glState, Object data) {
		
		GL gl = glState.getGL();
		
		glState.setFBO(0);
		
		glState.setActiveProgram(0);

		gl.glMatrixMode(GL.GL_TEXTURE);
		resetMatrix(glState, 5);
		gl.glMatrixMode(GL.GL_MODELVIEW);
		
		glState.setState((char) 0x35);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		GLSLDisplay.printDebugInfo("--> End: ");
		glState.testGLError();
		GLSLDisplay.printDebugInfoN("<--");
//		System.err.println("++-++-++-++-++-++-++-++-++");		
	}
	
	public static final int VIEW_TO_CLIP_MATRIX = 0;
	public static final int VIEW_TO_WORLD = 1;
	public static final int CUSTOM_MATRIX_1 = 2;
	public static final int CUSTOM_MATRIX_2 = 3;
	public static final int CLIP_TO_VIEW = 4;
	

	@Override
	protected void prologue(GLSLDisplay disp, OpenGLState glState, Object data) {
//		System.err.println("++*++*++*++*++*++*++*++*++");
		GL gl = glState.getGL();

		GLSLDisplay.printDebugInfoN("---- Rendering Scene "+disp.getView().getGraph().getStamp()+" ----");
		GLSLDisplay.printDebugInfo("--> Begin: ");
		glState.testGLError();
		GLSLDisplay.printDebugInfoN("<--");
		
		glState.csc.setCurrentStamp(disp.getView().getGraph().getStamp());

		glState.disable(OpenGLState.ALPHA_TEST);
		glState.disable(OpenGLState.BLEND);
		
		// only needed to prevent flashing in pip
		glState.floatRT = 0;
		glState.debugDrawn = false;
		glState.renderPass = 0;

		// reset Alpha Tex
		glState.getAlphaFBO().bind(glState);	
		gl.glClearColor(1, 1, 1, 1);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		// Clear HDR-Buffer
		glState.getHDRFBO().drawBuffers(glState, 2);
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		
		// SetupTextureMatrices:
		gl.glMatrixMode(GL.GL_TEXTURE);
		setMatrix(glState, VIEW_TO_CLIP_MATRIX, glState.getViewToClip());
		setMatrix(glState, VIEW_TO_WORLD, glState.getInvWorldToView());
		setMatrix(glState, CLIP_TO_VIEW, glState.getInvViewToClip());
		gl.glMatrixMode(GL.GL_MODELVIEW);		
	}

	private CacheScenePass srp = new CacheScenePass();
	private ToneMappingPass tmp = new ToneMappingPass();
	private PresentScenePass psp = new PresentScenePass();
	private DrawSkyPass dbp = new DrawSkyPass();
	private DrawTranspBackgroundPass dtbp = new DrawTranspBackgroundPass();
	private ToolRenderPass trp = new ToolRenderPass();
	private DepthPeelingPass dpp = new DepthPeelingPass();
	private ProcessLightingPass plp = new ProcessLightingPass();
	private EdgeFilteringPass efp = new EdgeFilteringPass();

	@Override
	protected void render(GLSLDisplay disp, OpenGLState glState, Object data) {
		srp.process(disp, glState, null);

		if(glState.isAssumeTranspMaterials())
			dpp.process(disp, glState, null);
	
		// light the scene
		//DrawLights(disp);
		/* XXX:
		Problem: pingponging
		brauchen pro licht 1 quelle + 1 ziel
		bei letztem licht weiteren pass: 
		blende lichtpass auf result! 1 quelle + 1 ziel
		*/
		plp.process(disp, glState, null);
		
		boolean renderSky = disp.isOptionShowSky() && (glState.BGFound);
		// Add background
		if(renderSky)
			dbp.process(disp, glState, null);
		// Tonemap the scene to get it into a displayable range
		
		if(disp.isOptionEdgeFiltering())
			efp.process(disp, glState, null);
		tmp.process(disp, glState, null);
		

		// Add lightpoints and frustrum
//		trpt.process(disp, glState, null);
		trp.process(disp, glState, null);
		// Present Scene!
		
		if(!renderSky) {
			//XXX Setup parameter for cftp
			dtbp.setImageMode(disp.isOptionShowBGImage(), 
					disp.getBackgroundColorR(), disp.getBackgroundColorG(), 
					disp.getBackgroundColorB(), disp.getBackgroundAlpha());
			dtbp.process(disp, glState, null);
		}

		if(!glState.debugDrawn)
			psp.process(disp, glState, null);
		glState.csc.removeUnusedShaders(glState);
		glState.getShapeManager().removeUnused(glState);
		glState.SM_Manager.removeUnused(glState);
	}

}
