package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.VolumeChecker;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;
import de.grogra.math.Graytone;

/**
 * Implementation of Shader: VolumeChecker
 * 
 * @author Konni Hartmann
 */
public class GLSLVolumeChecker extends GLSLChannelMapNode {

	@Override
	// note: result is independent of requestedChannel
	public Result generate(ChannelMap data, MaterialConfiguration sc,
			GLSLChannelMap inputChannnel, int requestedChannel){
		assert (data instanceof VolumeChecker);
		VolumeChecker ch = (VolumeChecker) data;
		
		// generate shader code for both colors
		Result color1 = generateResult(ch.getColor1() != null ? ch.getColor1() : new Graytone(1),
				sc, inputChannnel, requestedChannel);
		Result color2 = generateResult(ch.getColor2() != null ? ch.getColor2() : new Graytone(0),
				sc, inputChannnel, requestedChannel);

		// check result (should be replaced by throw....)
		if(color1 == null | color2 == null)
			return null;
		
		// Get input values (by default worldspace-coordinate of the fragment)
		GLSLChannelMap input = GLSLChannelMap.getGLSLObject(ch.getInput());
		Result res = input != null ? input.generate(ch.getInput(), sc, inputChannnel, Channel.X) : 
									 inputChannnel.generate(null, sc, null, Channel.X);
		
		// Input may be something other than a vec3 so convert it
		String resS = res.convert(Result.ET_VEC3);

		// define 3d-coordinates for readability
		String X = "(" + resS + ").x";
		String Y = "(" + resS + ").y";
		String Z = "(" + resS + ").z";

		// register boolean variable that saves in which tile of the checkboard the coordinates lay
		String boolvar = sc.registerNewTmpVar(Result.ET_BOOL, "mod(floor("
				+ X + ") + floor(" + Y + ") + floor(" + Z + "),2.0) == 1.0");

		// color1 and color2 may have different result types. Convert to maximum of both 
		int rstType = Result.ET_FLOAT;

		int rt1 = color1.getReturnType();
		int rt2 = color2.getReturnType();

		if (rt1 == rt2)
			rstType = rt1;
		else
			rstType = rt1 < rt2 ? rt2 : rt1;

		String cl1Str = rt1 < rt2 ? color1.convert(rt2) : color1.toString();
		String cl2Str = rt1 > rt2 ? color2.convert(rt1) : color2.toString();

		// return Result containing the generated String and type
		return new Result(
				"(" + boolvar + " ? " + cl1Str + " : " + cl2Str + ")", rstType);
	}

	@Override
	public Class instanceFor() {
		return VolumeChecker.class;
	}

}
