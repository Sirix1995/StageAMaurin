package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.VolumeTurbulence;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

/**
 * Implementation of Shader: VolumeTurbulence. Uses class <code>Noise</code> to
 * get the needed noise function.
 * 
 * @author Konni Hartmann
 */
public class GLSLVolumeTurbulence extends GLSLVolumeFunction {

	@Override
	public Result generateVolumeData(ChannelMap inp,
			MaterialConfiguration cs, GLSLChannelMap inpChan, int channel){
		assert (inp instanceof VolumeTurbulence);

		// XXX: Cleanup call sequence. Noise should return functionNames
		SimplexNoise.registerNoiseFunctions(cs);

		VolumeTurbulence ch = (VolumeTurbulence) inp;
		// ChannelMap cInp = ch.getInput();
		float frequencyRatio = ch.getFrequencyRatio();
		float noiseRatio = ch.getNoiseRatio();
		float size = ch.getSize();
		// do not allow more than 6 iterations
		// (until simplex noise + opt is working)
//		int octaves = Math.max(Math.min(ch.getOctaves(), 11), 1);
		int octaves = Math.max(Math.min(ch.getOctaves(), 6), 1);

		GLSLChannelMap input = GLSLChannelMap.getGLSLObject(ch.getInput());
		Result res = input != null ? input.generate(ch.getInput(), cs, inpChan, Channel.X) : 
									 inpChan.generate(null, cs, null, Channel.X);
		String pos = res.convert(Result.ET_VEC3);

		float a = 1.0f;
		if (Math.abs(noiseRatio) > 1) {
			a /= Math.pow(Math.abs(noiseRatio), octaves - 1);
		}

		String turb = SimplexNoise.registerTurbWithUnroll(cs, octaves);
		return new Result(a + "*"+turb+"(" + size + "*" + pos + ", "
				+ frequencyRatio + ", " + noiseRatio + ")",
				Result.ET_FLOAT);
	}

	@Override
	public Class instanceFor() {
		return VolumeTurbulence.class;
	}

}
