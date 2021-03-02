package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.math.TVector3d;
import de.grogra.math.Transform3D;

public class GLSLTVector3d extends GLSLTransform3D {

	@Override
	public Class instanceFor() {
		return TVector3d.class;
	}

	@Override
	public Result process(Result input, Transform3D fkt, ShaderConfiguration sc) {
		assert (fkt instanceof TVector3d);
		TVector3d tv3d = (TVector3d) fkt;
		return new Result("vec3(" + tv3d.x + "," + tv3d.y + "," + tv3d.z + ")"
				+ "+" + input.convert(Result.ET_VEC3), Result.ET_VEC3);
	}

}
