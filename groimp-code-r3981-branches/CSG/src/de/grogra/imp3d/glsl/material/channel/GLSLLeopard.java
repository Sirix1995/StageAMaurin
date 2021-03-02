package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.Leopard;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

public class GLSLLeopard extends GLSLVolumeFunction {

	@Override
	public Result generateVolumeData(ChannelMap inp,
			MaterialConfiguration cs, GLSLChannelMap inpChan, int channel){

		assert (inp instanceof Leopard);
		Leopard ld = (Leopard)inp;
		GLSLChannelMap input = GLSLChannelMap.getGLSLObject(ld.getInput());
		Result res = input != null ? input.generate(ld.getInput(), cs, inpChan, Channel.X) : 
									 inpChan.generate(null, cs, null, Channel.X);
		String pos = res.convert(Result.ET_VEC3);
		
		String x = cs.registerNewTmpVar(Result.ET_FLOAT, "dot(sin(" + pos
				+ "),vec3(1.0/3.0))");
		return new Result(x + "*" + x, Result.ET_FLOAT);
	}

	@Override
	public Class instanceFor() {
		return Leopard.class;
	}

}
