package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.math.Transform3D;
import de.grogra.math.UniformScale;

public class GLSLUniformScale extends GLSLTransform3D {

	@Override
	public Class instanceFor() {
		return UniformScale.class;
	}

	@Override
	public Result process(Result input, Transform3D fkt, ShaderConfiguration sc) {
		assert (fkt instanceof UniformScale);
		UniformScale us = (UniformScale) fkt;
		return new Result(us.getScale() + "*" + input, input.getReturnType());
	}

}
