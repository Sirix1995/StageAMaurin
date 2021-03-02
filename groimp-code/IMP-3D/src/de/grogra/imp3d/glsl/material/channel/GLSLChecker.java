package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.Checker;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;
import de.grogra.math.Graytone;
import de.grogra.persistence.ManageableType;
import de.grogra.reflect.Field;

/**
 * Implementation of the surface-shader: Checker. Will return 'higher' return
 * type of both associated colors. Also implements antialiasing.
 * 
 * @author Konni Hartmann
 */
public class GLSLChecker extends GLSLChannelMapNode {

	@Override
	public Result generate(ChannelMap channelMap, MaterialConfiguration config,
			GLSLChannelMap inputChannelMap, int channel) {
		assert (channelMap instanceof Checker);
		Checker ch = (Checker) channelMap;

		Result colo1 = generateResult((ChannelMap) Checker.color1$FIELD
				.getObject(ch), config, inputChannelMap, channel, ch.getColor1());
		Result colo2 = generateResult((ChannelMap) Checker.color2$FIELD
				.getObject(ch), config, inputChannelMap, channel, ch.getColor2());

		String UV =
			config.registerNewTmpVar(Result.ET_VEC2, generateResultWithChannelDefault(ch.getInput(), config,
				inputChannelMap, Channel.U).convert(Result.ET_VEC2));

		int rstType = getMaxResultType(colo1, colo2);

		if (!config.isShaderAntialiasing()) {

			String boolvar = "mod(floor(2.0*(" + UV + ").s) + floor(2.0*(" + UV
							+ ").t),2.0) == 1.0";
			return new Result("(" + boolvar + " ? " + colo1.convert(rstType)
					+ " : " + colo2.convert(rstType) + ")", rstType);
		}

		String colorType = Result.getTypeString(rstType);
		config
				.registerFunc(
						colorType + " filteredChecker(vec2 inp, " + colorType
								+ " col1, " + colorType + " col2)",
						colorType
								+ " tmp;\n"
								+ colorType
								+ " average = (col1 + col2) * 0.5;"
								+ "vec2 fuzz = fwidth(inp)*2.0;\n"
								+ "float fuzzMax = max(fuzz.s, fuzz.t);\n"
								+ "vec2 check = fract(inp+fuzz*0.5);\n"
								+ "if(fuzzMax < .75) {\n"
								+ "	vec2 p = smoothstep(vec2(0.5), vec2(0.5)+fuzz, check)+\n"
								+ "		(1.0 - smoothstep(vec2(0.0), fuzz, check));\n"
								+ "	tmp = mix((col1), (col2), p.x*p.y + (1.0-p.x)*(1.0-p.y));\n"
								+ "	tmp = mix(tmp, (average), smoothstep(0.5, 0.75, fuzzMax));\n"
								+ "} else\n" + "	tmp = average;\n"
								+ "return tmp;\n");
		return new Result("filteredChecker(" + UV + ","
				+ colo1.convert(rstType) + "," + colo2.convert(rstType) + ")",
				rstType);
	}

	@Override
	public Class instanceFor() {
		return Checker.class;
	}

	// XXX: Implement may discard function!
	@Override
	public boolean mayDiscard() {
		return true;
	}

}
