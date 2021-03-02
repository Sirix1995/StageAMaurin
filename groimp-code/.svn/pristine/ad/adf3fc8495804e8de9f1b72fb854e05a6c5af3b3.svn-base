package de.grogra.imp3d.glsl.renderable;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.Frustum;

public class GLSLFrustum extends GLSLFrustumBase {
	protected float top_radius = 1;
	protected float base_radius = 1;
	boolean asWireframe = false;

	@Override
	public Class<?> instanceFor() {
		return Frustum.class;
	}

	@Override
	protected void updateInstanceIndirect(Object state, boolean asNode, GraphState gs) {
		this.length = (float)gs.getDouble(state, asNode, de.grogra.imp.objects.Attributes.LENGTH);
		this.base_radius = gs.getFloat (state, asNode, de.grogra.imp.objects.Attributes.BASE_RADIUS);
		this.top_radius = gs.getFloat (state, asNode, de.grogra.imp.objects.Attributes.TOP_RADIUS);
		this.asWireframe = gs.getBoolean (state, asNode, de.grogra.imp3d.objects.Attributes.RENDER_AS_WIREFRAME);
		setBool(SCALE_V_MASK, gs.getBoolean(state, asNode, Attributes.SCALE_V));
		setBool(BASE_OPEN_MASK, gs.getBoolean(state, asNode, Attributes.BASE_OPEN));	
		setBool(TOP_OPEN_MASK, gs.getBoolean(state, asNode, Attributes.TOP_OPEN));	
	}

	@Override
	protected void updateInstanceByInstancing(Renderable reference,
			GraphState gs) {
		super.updateInstanceByInstancing(reference, gs);
		assert (reference instanceof Frustum);
		Frustum ref = (Frustum) reference;
		this.base_radius = gs.checkFloat (reference, true, de.grogra.imp.objects.Attributes.BASE_RADIUS, ref.getBaseRadius());
		this.top_radius = gs.checkFloat (reference, true, de.grogra.imp.objects.Attributes.TOP_RADIUS, ref.getTopRadius());
		this.asWireframe = gs.checkBoolean (reference, true, de.grogra.imp3d.objects.Attributes.RENDER_AS_WIREFRAME, ref.isRenderAsWireframe());
		setBool(BASE_OPEN_MASK, gs.checkBoolean(reference, true, Attributes.BASE_OPEN, ref.isBaseOpen()));	
		setBool(TOP_OPEN_MASK, gs.checkBoolean(reference, true, Attributes.TOP_OPEN, ref.isTopOpen()));	
	}

	@Override
	protected void updateInstanceDirect(Renderable reference) {
		super.updateInstanceDirect(reference);
		assert (reference instanceof Frustum);
		Frustum ref = (Frustum) reference;
		this.base_radius = ref.getBaseRadius();
		this.top_radius = ref.getTopRadius();
		this.asWireframe = ref.isRenderAsWireframe();
		setBool(BASE_OPEN_MASK, ref.isBaseOpen());	
		setBool(TOP_OPEN_MASK, ref.isTopOpen());	
	}

	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		drawFrustum (glState, rs, length, base_radius, top_radius,
				!getBool(BASE_OPEN_MASK), !getBool(TOP_OPEN_MASK),
				getBool(SCALE_V_MASK) ? length : 1, null, RenderState.CURRENT_HIGHLIGHT, asWireframe, null);
	}
	
	@Override
	public GLSLRenderable getInstance() {
		return new GLSLFrustum();
	}

}
