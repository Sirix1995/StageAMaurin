package de.grogra.imp3d.glsl.renderable;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.renderable.vbo.VBOManager;
import de.grogra.imp3d.objects.Box;

public class GLSLBox extends GLSLAxis {
	protected float width = 1;
	protected float height = 1;

	@Override
	public Class<?> instanceFor() {
		return Box.class;
	}

	@Override
	protected void updateInstanceDirect(Renderable reference) {
		super.updateInstanceDirect(reference);
		assert (reference instanceof Box);
		Box ref = (Box) reference;
		this.width = ref.getWidth();
		this.height = ref.getHeight();
	}

	@Override
	public void updateInstanceIndirect(Object state,
			boolean asNode, GraphState gs) {
		this.width = (float)gs.getDouble(state, asNode, de.grogra.imp.objects.Attributes.WIDTH);
		this.height = (float)gs.getDouble(state, asNode, de.grogra.imp.objects.Attributes.HEIGHT);
		this.length = (float)gs.getDouble(state, asNode, de.grogra.imp.objects.Attributes.LENGTH);
	}

	@Override
	protected void updateInstanceByInstancing(Renderable reference,
			GraphState gs) {
		super.updateInstanceByInstancing(reference, gs);
		assert (reference instanceof Box);
		Box ref = (Box) reference;
		this.width = (float)gs.checkDouble (reference, true, de.grogra.imp.objects.Attributes.WIDTH, ref.getWidth());
		this.height = (float)gs.checkDouble (reference, true, de.grogra.imp.objects.Attributes.HEIGHT, ref.getHeight());
	}

	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		rs.drawBox(width * 0.5f, height * 0.5f, length, null,
				RenderState.CURRENT_HIGHLIGHT, null);
	}

	@Override
	public void drawAlt(OpenGLState glState, GLSLDisplay rs) {
//		rs.drawBox(width * 0.5f, height * 0.5f, length, null,
//				RenderState.CURRENT_HIGHLIGHT, null);
		drawSetup(glState, rs);
	}

	
	private Matrix4d t = null;
	private Matrix4d scale = new Matrix4d();
	
	private void drawSetup(OpenGLState glState, GLSLDisplay rs) {
		// get correct (and probibly derived) shader and transformation
		t = rs.getTransformation (null);

		// get opengl context
		GL gl = glState.getGL();

		// apply transformation
		gl.glPushMatrix ();
		gl.glLoadMatrixd (glState.toGLMatrix4 (t), 0);

		scale.setIdentity();
		scale.m00 = width;
		scale.m11 = height;
		scale.m22 = length;

		gl.glMultMatrixd(glState.toGLMatrix4 (scale), 0);
		
		glState.VBO_Manager.getVBO(VBOManager.BOX, glState).draw(glState);
//		drawBox(gl, rs);

		// restore previous state
		gl.glPopMatrix ();
	}
	
//	private void drawBox(GL gl, GLSLDisplay rs) {
//		if(displayList != 0) {
//			gl.glCallList(displayList);
//		} else {
//			displayList = gl.glGenLists(1);
//			gl.glNewList(displayList, GL.GL_COMPILE_AND_EXECUTE);
//			rs.drawBoxImpl(gl, -.5f, -.5f, 0, 0.5f, 0.5f, 1.0f);
//			gl.glEndList();
//		}
//	}
	
	@Override
	public GLSLRenderable getInstance() {
		return new GLSLBox();
	}
}
