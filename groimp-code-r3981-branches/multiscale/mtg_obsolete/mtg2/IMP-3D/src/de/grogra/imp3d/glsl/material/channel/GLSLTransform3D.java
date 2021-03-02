package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.math.Transform3D;

public abstract class GLSLTransform3D {
	public abstract Result process(Result input, Transform3D fkt, ShaderConfiguration sc);

	public abstract Class instanceFor();
}
