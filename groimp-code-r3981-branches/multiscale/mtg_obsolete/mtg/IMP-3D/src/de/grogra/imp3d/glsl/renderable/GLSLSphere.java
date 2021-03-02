package de.grogra.imp3d.glsl.renderable;

import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.Sphere;

import de.grogra.imp3d.objects.Attributes;
import de.grogra.graph.GraphState;

public class GLSLSphere extends GLSLInstanceableRenderable {
	protected float radius = 1;

	@Override
	public Class<?> instanceFor() {
		return Sphere.class;
	}

	@Override
	public void updateInstanceIndirect(Object state, boolean asNode, GraphState gs) {
		this.radius = gs.getFloat (state, asNode, de.grogra.imp.objects.Attributes.RADIUS);		
	}

	@Override
	protected void updateInstanceByInstancing(Renderable reference,
			GraphState gs) {
		assert (reference instanceof Sphere);
		Sphere ref = (Sphere) reference;
		this.radius = gs.checkFloat (reference, true, de.grogra.imp.objects.Attributes.RADIUS, ref.getRadius());
	}

	@Override
	protected void updateInstanceDirect(Renderable reference) {
		assert (reference instanceof Sphere);
		Sphere ref = (Sphere) reference;
		this.radius = ref.getRadius();
	}

	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		rs.drawSphere (radius, null, RenderState.CURRENT_HIGHLIGHT, null);
	}
	
	@Override
	public GLSLRenderable getInstance() {
		return new GLSLSphere();
	}

}
