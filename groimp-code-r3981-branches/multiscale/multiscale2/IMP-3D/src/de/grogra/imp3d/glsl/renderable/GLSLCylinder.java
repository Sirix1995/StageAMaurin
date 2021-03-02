package de.grogra.imp3d.glsl.renderable;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.Cylinder;

public class GLSLCylinder extends GLSLFrustumBase {
	protected float radius = 1;

	@Override
	public Class<?> instanceFor() {
		return Cylinder.class;
	}

	@Override
	protected void updateInstanceIndirect(Object state, boolean asNode, GraphState gs) {
		this.radius = gs.getFloat (state, asNode, de.grogra.imp.objects.Attributes.RADIUS);
		this.length = (float)gs.getDouble(state, asNode, de.grogra.imp.objects.Attributes.LENGTH);
		setBool(SCALE_V_MASK, gs.getBoolean(state, asNode, Attributes.SCALE_V));
		setBool(BASE_OPEN_MASK, gs.getBoolean(state, asNode, Attributes.BASE_OPEN));	
		setBool(TOP_OPEN_MASK, gs.getBoolean(state, asNode, Attributes.TOP_OPEN));	
	}

	@Override
	protected void updateInstanceByInstancing(Renderable reference,
			GraphState gs) {
		super.updateInstanceByInstancing(reference, gs);
		assert (reference instanceof Cylinder);
		Cylinder ref = (Cylinder) reference;
		this.radius = gs.checkFloat (reference, true, de.grogra.imp.objects.Attributes.RADIUS, ref.getRadius());
		setBool(BASE_OPEN_MASK, gs.checkBoolean(reference, true, Attributes.BASE_OPEN, ref.isBaseOpen()));	
		setBool(TOP_OPEN_MASK, gs.checkBoolean(reference, true, Attributes.TOP_OPEN, ref.isTopOpen()));	
	}

	@Override
	protected void updateInstanceDirect(Renderable reference) {
		super.updateInstanceDirect(reference);
		assert (reference instanceof Cylinder);
		Cylinder ref = (Cylinder) reference;
		this.radius = ref.getRadius();
		setBool(BASE_OPEN_MASK, ref.isBaseOpen());	
		setBool(TOP_OPEN_MASK, ref.isTopOpen());	
	}

	@Override
	public void draw(OpenGLState glState, RenderState rs) {
		drawFrustum (glState, rs, length, radius, radius,
				!getBool(BASE_OPEN_MASK), !getBool(TOP_OPEN_MASK),
				getBool(SCALE_V_MASK) ? length : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
	}
	
	@Override
	public GLSLRenderable getInstance() {
		return new GLSLCylinder();
	}

}
