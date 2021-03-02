package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.Julia;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

public class GLSLJulia extends GLSLVolumeFunction {

	final static String juliaFuncSig = "float julia(vec3 pos, vec2 c, float iterations)";
	final static String juliaFunc = "float x = ((pos.x > 0.0) ? pos.x - floor(pos.x) : pos.x + (1.0 + floor(-pos.x)) ) * 4.0 - 2.0;\n"
			+ "float y = ((pos.y > 0.0) ? pos.y - floor(pos.y) : pos.y + (1.0 + floor(-pos.y)) ) * 4.0 - 2.0;\n"
			+ "float a = x;\n"
			+ "float b = y;\n"
			+ "float a2 = a * a;\n"
			+ "float b2 = b * b;\n"
			+ "float dist2;\n"
			+ "float i;\n"
			+ "for (i = 0.0; i < iterations; i+=1.0)\n"
			+ "{\n"
			+ "b = 2.0 * a * b + c.y;\n"
			+ "a = a2 - b2 + c.x;\n"
			+ "a2 = a * a;\n"
			+ "b2 = b * b;\n"
			+ "dist2 = a2 + b2;\n"
			+ "if (dist2 > 4.0)\n" + "{\n" + "break;\n" + "}\n" +

			"}\n" + "return (2.0 * i - iterations) / iterations;\n";

	@Override
	public Class instanceFor() {
		return Julia.class;
	}

	@Override
	public Result generateVolumeData(ChannelMap inp,
			MaterialConfiguration cs, GLSLChannelMap inpChan, int channel){
		assert (inp instanceof Julia);
		// XXX: Cleanup call sequence. atm this is quite confusing!
		OldNoise.regLookUpTexture(cs);

		Julia ch = (Julia) inp;

		int iterations = Math.max(Math.min(ch.getIterations(), 200), 1);
		cs.registerFunc(juliaFuncSig, juliaFunc);
		
		GLSLChannelMap input = GLSLChannelMap.getGLSLObject(ch.getInput());
		Result res = input != null ? input.generate(ch.getInput(), cs, inpChan, Channel.X) : 
									 inpChan.generate(null, cs, null, Channel.X);
		String pos = res.convert(Result.ET_VEC3);
		
		return new Result("julia(" + pos + ",vec2(" + ch.getCx() + ","
		+ ch.getCy() + ")," + (float) iterations + ")", Result.ET_FLOAT);
	}

}
