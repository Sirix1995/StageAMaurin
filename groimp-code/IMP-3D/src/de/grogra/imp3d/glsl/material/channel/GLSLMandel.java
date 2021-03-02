package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.Mandel;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

public class GLSLMandel extends GLSLVolumeFunction {

	final static String mandelFuncSig = "float mandel(vec3 pos, float iterations)";
	final static String mandelFunc = "float x = ((pos.x > 0.0) ? pos.x - floor(pos.x) : pos.x + (1.0 + floor(-pos.x)) ) * 4.0 - 2.0;\n"
			+ "float y = ((pos.y > 0.0) ? pos.y - floor(pos.y) : pos.y + (1.0 + floor(-pos.y)) ) * 4.0 - 2.0;\n"
			+ "float a = x;\n"
			+ "float b = y;\n"
			+ "float a2 = a * a;\n"
			+ "float b2 = b * b;\n"
			+ "float dist2;\n"
			+ "float i;\n"
			+ "for (i = 0.0; i < iterations; i+=1.0)\n"
			+ "{\n"
			+ "b = 2.0 * a * b + y;\n"
			+ "a = a2 - b2 + x;\n"
			+ "a2 = a * a;\n"
			+ "b2 = b * b;\n"
			+ "dist2 = a2 + b2;\n"
			+ "if (dist2 > 4.0)\n"
			+ "{\n"
			+ "break;\n"
			+ "}\n"
			+ "}\n"
			+ "return (2.0 * i - iterations) / iterations;";

	@Override
	public Class instanceFor() {
		return Mandel.class;
	}

	@Override
	public Result generateVolumeData(ChannelMap inp,
			MaterialConfiguration cs, GLSLChannelMap inpChan, int channel){
		assert (inp instanceof Mandel);

		Mandel ch = (Mandel) inp;
		
		GLSLChannelMap input = GLSLChannelMap.getGLSLObject(ch.getInput());
		Result res = input != null ? input.generate(ch.getInput(), cs, inpChan, Channel.X) : 
									 inpChan.generate(null, cs, null, Channel.X);
		

		int iterations = Math.max(Math.min(ch.getIterations(), 200), 1);
		cs.registerFunc(mandelFuncSig, mandelFunc);
		String pos = res.convert(Result.ET_VEC3);
		return new Result("mandel(" + pos + "," + (float) iterations + ")",
				Result.ET_FLOAT);
	}

}
