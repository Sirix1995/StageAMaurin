package de.grogra.gpuflux.scene.shading;

import de.grogra.gpuflux.scene.FluxObject;

public abstract class FluxShader extends FluxObject {
	
	protected static final int SHADER_RGBA = 0;
	protected static final int SHADER_PHONG = 1;
	protected static final int SHADER_SWITCH = 2;
	protected static final int SHADER_IOR = 3;
	
}
