package de.grogra.imp3d.glsl.renderable;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.Sphere;

public class GLSLSphere extends GLSLInstanceableRenderable {
	protected float radius = 1;
	boolean asWireframe = false;

	@Override
	public Class<?> instanceFor() {
		return Sphere.class;
	}

	@Override
	public void updateInstanceIndirect(Object state, boolean asNode, GraphState gs) {
		this.radius = gs.getFloat (state, asNode, de.grogra.imp.objects.Attributes.RADIUS);
		this.asWireframe = gs.getBoolean (state, asNode, de.grogra.imp3d.objects.Attributes.RENDER_AS_WIREFRAME);
	}

	@Override
	protected void updateInstanceByInstancing(Renderable reference, GraphState gs) {
		assert (reference instanceof Sphere);
		Sphere ref = (Sphere) reference;
		this.radius = gs.checkFloat (reference, true, de.grogra.imp.objects.Attributes.RADIUS, ref.getRadius());
		this.asWireframe = gs.checkBoolean (reference, true, de.grogra.imp3d.objects.Attributes.RENDER_AS_WIREFRAME, ref.isRenderAsWireframe());
	}

	@Override
	protected void updateInstanceDirect(Renderable reference) {
		assert (reference instanceof Sphere);
		Sphere ref = (Sphere) reference;
		this.radius = ref.getRadius();
		this.asWireframe = ref.isRenderAsWireframe();
	}

	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		rs.drawSphere (radius, null, RenderState.CURRENT_HIGHLIGHT, asWireframe, null);
	}
	
	@Override
	public GLSLRenderable getInstance() {
		return new GLSLSphere();
	}

}
