package de.grogra.imp3d.glsl.renderable;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.Axis;

public abstract class GLSLAxis extends GLSLBitCache {

	protected float length = 1;
	//enh:field attr=Attributes.LENGTH getter setter

	protected float startPosition = 0;
	//enh:field attr=Attributes.START_POSITION getter setter

	protected float endPosition = 1;
	//enh:field attr=Attributes.END_POSITION getter setter

	protected static final int SCALE_V_MASK = 1 << GLSLBitCache.USED_BITS;
	public static final int USED_BITS = GLSLBitCache.USED_BITS + 1;

	@Override
	protected void updateInstanceDirect(Renderable reference) {
		assert (reference instanceof Axis);
		Axis axis = (Axis) reference;
		this.length = axis.getLength();
		this.startPosition = axis.getStartPosition();
		this.endPosition = axis.getEndPosition();
		setBool(SCALE_V_MASK, axis.isScaleV());	
	}
	
	@Override
	protected void updateInstanceIndirect(Object state, boolean asNode, GraphState gs) {
		this.length = (float)gs.getDouble(state, asNode, de.grogra.imp.objects.Attributes.LENGTH);
		this.startPosition = gs.getFloat(state, asNode, Attributes.START_POSITION);
		this.endPosition = gs.getFloat(state, asNode, Attributes.END_POSITION);
		setBool(SCALE_V_MASK, gs.getBoolean(state, asNode, Attributes.SCALE_V));	
	}


	@Override
	protected void updateInstanceByInstancing(Renderable reference,
			GraphState gs) {
		assert(reference instanceof Axis);
		Axis axis = (Axis) reference;
		this.length = (float)gs.checkDouble(this, true, de.grogra.imp.objects.Attributes.LENGTH, axis.getLength());
		this.startPosition = gs.checkFloat(this, true, Attributes.START_POSITION, axis.getStartPosition());
		this.endPosition = gs.checkFloat(this, true, Attributes.END_POSITION, axis.getStartPosition());
		setBool(SCALE_V_MASK, gs.checkBoolean(this, true, Attributes.SCALE_V, axis.isScaleV()));	
	}
}
