package de.grogra.imp3d.glsl.material.channel;

import javax.vecmath.Vector3f;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.Gradient;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

public class GLSLGradient extends GLSLVolumeFunction {

	@Override
	public Result generateVolumeData(ChannelMap inp,
			MaterialConfiguration cs, GLSLChannelMap inpChan, int channel){
		assert (inp instanceof Gradient);
		Gradient ch = (Gradient) inp;

		Vector3f dirV = ch.getDirection();

		String dir = "vec3(" + dirV.x + "," + dirV.y + "," + dirV.z + ")";
		
		GLSLChannelMap input = GLSLChannelMap.getGLSLObject(ch.getInput());
		Result res = input != null ? input.generate(ch.getInput(), cs, inpChan, Channel.X) : 
									 inpChan.generate(null, cs, null, Channel.X);
		String pos = res.convert(Result.ET_VEC3);
		
		return new Result("dot(" + pos + "," + dir + ")", Result.ET_FLOAT);
	}

	@Override
	public Class instanceFor() {
		return Gradient.class;
	}

}
