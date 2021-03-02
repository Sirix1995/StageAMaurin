package de.grogra.imp3d.glsl.renderable;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.Cone;

public class GLSLCone extends GLSLFrustumBase {
	protected float radius = 1;
	boolean asWireframe = false;

	@Override
	public Class<?> instanceFor() {
		return Cone.class;
	}

	@Override
	protected void updateInstanceIndirect(Object state, boolean asNode, GraphState gs) {
		this.radius = gs.getFloat (state, asNode, de.grogra.imp.objects.Attributes.RADIUS);
		this.length = (float)gs.getDouble(state, asNode, de.grogra.imp.objects.Attributes.LENGTH);
		this.asWireframe = gs.getBoolean (state, asNode, de.grogra.imp3d.objects.Attributes.RENDER_AS_WIREFRAME);
		setBool(SCALE_V_MASK, gs.getBoolean(state, asNode, Attributes.SCALE_V));
		setBool(BASE_OPEN_MASK, gs.getBoolean(state, asNode, Attributes.BASE_OPEN));
	}

	@Override
	protected void updateInstanceByInstancing(Renderable reference,
			GraphState gs) {
		super.updateInstanceByInstancing(reference, gs);
		assert (reference instanceof Cone);
		Cone ref = (Cone) reference;
		this.radius = gs.checkFloat (reference, true, de.grogra.imp.objects.Attributes.RADIUS, ref.getRadius());
		this.asWireframe = gs.checkBoolean (reference, true, de.grogra.imp3d.objects.Attributes.RENDER_AS_WIREFRAME, ref.isRenderAsWireframe());
		setBool(BASE_OPEN_MASK, gs.checkBoolean(reference, true, Attributes.BASE_OPEN, ref.isOpen()));		
	}

	@Override
	protected void updateInstanceDirect(Renderable reference) {
		super.updateInstanceDirect(reference);
		assert (reference instanceof Cone);
		Cone ref = (Cone) reference;
		this.radius = ref.getRadius();
		this.asWireframe = ref.isRenderAsWireframe();
		setBool(BASE_OPEN_MASK, ref.isOpen());
	}

	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		drawFrustum (glState, rs, length, radius, 0,
				!getBool(BASE_OPEN_MASK), false,
				getBool(SCALE_V_MASK) ? length : 1, null, RenderState.CURRENT_HIGHLIGHT, asWireframe, null);
	}
	
	@Override
	public GLSLRenderable getInstance() {
		return new GLSLCone();
	}

}
