package de.grogra.imp3d.glsl.material.channel;

import javax.vecmath.Matrix4d;

import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.math.ComponentTransform;
import de.grogra.math.Transform3D;

public class GLSLComponentTransform extends GLSLTMatrix4d {

	@Override
	public Class instanceFor() {
		return ComponentTransform.class;
	}

	Matrix4d m = new Matrix4d();
	
	@Override
	public Result process(Result input, Transform3D fkt, ShaderConfiguration sc) {
		assert (fkt instanceof ComponentTransform);
		ComponentTransform ct = (ComponentTransform) fkt;
		m.setIdentity();
		ct.transform(m, m);
		return new Result("("+matrixToGLSLMatrix(m) + "*" + input.convert(Result.ET_VEC4)+").xyz", Result.ET_VEC3);
	}

}
