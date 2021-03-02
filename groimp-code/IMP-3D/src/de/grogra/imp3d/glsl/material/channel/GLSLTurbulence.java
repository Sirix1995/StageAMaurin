package de.grogra.imp3d.glsl.material.channel;

import javax.vecmath.Vector3f;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.Turbulence;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

/**
 * Implementation of Shader: VolumeTurbulence. Uses class <code>Noise</code> to
 * get the needed noise function.
 * 
 * @author Konni Hartmann
 */
public class GLSLTurbulence extends GLSLChannelMapNode {

	@Override
	public Result generate(ChannelMap inp,
			MaterialConfiguration cs, GLSLChannelMap inpChan, int channel){
		assert (inp instanceof Turbulence);

		// XXX: Cleanup call sequence. Noise should return functionNames
		SimplexNoise.registerdNoiseFunctions(cs);

		Turbulence ch = (Turbulence) inp;
		// ChannelMap cInp = ch.getInput();
		float frequencyRatio = ch.getFrequencyRatio();
		float noiseRatio = ch.getNoiseRatio();
		Vector3f amount = ch.getAmount();

		// do not allow more than 6 iterations
		// (until simplex noise + opt is working)
//		int octaves = Math.max(Math.min(ch.getOctaves(), 10), 1);
		int octaves = Math.max(Math.min(ch.getOctaves(), 6), 1);

		GLSLChannelMap input = GLSLChannelMap.getGLSLObject(ch.getInput());
		Result res = input != null ? input.generate(ch.getInput(), cs, inpChan, channel) : 
									 inpChan.generate(null, cs, null, channel);
		String pos = res.convert(Result.ET_VEC3);

//		float a = 1.0f;
//		if (Math.abs(noiseRatio) > 1) {
//			a /= Math.pow(Math.abs(noiseRatio), octaves - 1);
//		}

		String dturb = SimplexNoise.registerDTurbWithUnroll(cs, octaves);
		return new Result(pos + " + vec3("+amount.x+","+amount.y+","+amount.z+") * "+dturb+"("+ pos + ", "
		+ frequencyRatio + ", " + noiseRatio + ")",
		Result.ET_VEC3);
//		return new Result(pos + " + vec3("+amount.x+","+amount.y+","+amount.z+") * dturbulence("+ pos + ", "
//				+ octaves + ", " + frequencyRatio + ", " + noiseRatio + ")",
//				Result.ET_VEC3);
	}

	/*
		ChannelData in = data.getData (input);
		switch (channel)
		{
			case Channel.X:
			case Channel.Y:
			case Channel.Z:
			case Channel.U:
			case Channel.V:
			case Channel.W:
				Point3f p = data.p3f0, q = data.p3f1;
				in.getTuple3f (p, data, channel & ~3);
				Math2.dTurbulence (q, p.x, p.y, p.z, Math.min (10, octaves),
								   frequencyRatio, noiseRatio);
				p.x += q.x * amount.x;
				p.y += q.y * amount.y;
				p.z += q.z * amount.z;
				data.setTuple3f (channel & ~3, p);
				return data.getValidFloatValue (channel);
			default:
				return data.forwardGetFloatValue (in);
		}
	 */
	
	@Override
	public Class instanceFor() {
		return Turbulence.class;
	}

}
