package de.grogra.imp3d.glsl.renderable;

public abstract class GLSLBitCache extends GLSLInstanceableRenderable {

	protected int BITMASK = 0;
	
	public static final int USED_BITS = 0;
	
	protected boolean getBool(int MASK) {
		return (BITMASK & MASK) > 0;
	}
	
	protected void setBool(int MASK, boolean value) {
		if(value)
			this.BITMASK |= MASK; 
		else
			this.BITMASK &= ~MASK;
	}

}
