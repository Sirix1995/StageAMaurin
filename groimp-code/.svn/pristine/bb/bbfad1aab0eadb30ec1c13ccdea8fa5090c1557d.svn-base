package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.VolumeFunction;
import de.grogra.math.ChannelMap;

/**
 * Base class for all volume shaders. Will add FloatToFloat transformation.
 * 
 * @author Konni Hartmann
 */
public abstract class GLSLVolumeFunction extends GLSLChannelMapNode {

	public abstract Result generateVolumeData(ChannelMap inp,
			MaterialConfiguration cs, GLSLChannelMap inpChan, int channel);

	// static DecimalFormat ff = new DecimalFormat("#,0");
	public static String floatToString(float f) {
		// return ff.format(f);
		return "" + f;
	}

	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration cs,
			GLSLChannelMap inpChan, int channel){
		assert (inp instanceof VolumeFunction);
		VolumeFunction vf = (VolumeFunction) inp;
		Result res = generateVolumeData(inp, cs, inpChan, channel);

		String add = ((vf.getFrequency() == 1.0) & (vf.getPhase() == 0.0)) ? ""
				: "*" + floatToString(vf.getFrequency()) + "+"
						+ floatToString(vf.getPhase());

		res = new Result(res + add, res.getReturnType());
		return FloatToFloatCollection.addFloatToFloat(res, vf.getWaveForm(), cs);
	}

}
