package de.grogra.imp3d.glsl.material.channel;

import java.util.HashMap;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

/**
 * Base for all ChannelMaps. Also holds a list of all available GLSLChannelMaps.
 * 
 * @author Konni Hartmann
 */
public abstract class GLSLChannelMap {

	private static ChannelCollection chanCol = new ChannelCollection();

	public static GLSLChannelMap getGLSLObject(ChannelMap inp) {
		return chanCol.getGLSLObject(inp);
	}

	public abstract Class instanceFor();
	
	public static int getMaxResultType(Result a, Result b) {
		return a.getReturnType() < b.getReturnType() ? b.getReturnType() : a.getReturnType();
	}
	
	public static Result generateResultWithChannelDefault(ChannelMap inp, MaterialConfiguration sc, GLSLChannelMap inpChan, int requestedChannel) {
		GLSLChannelMap glslCM = getGLSLObject(inp);
		Result res = glslCM != null ? glslCM.generate(inp, sc, inpChan, requestedChannel) : inpChan.generate(null, sc, null, requestedChannel);
		return res;
	}


	public static Result generateResult(ChannelMap inp, MaterialConfiguration sc, GLSLChannelMap inpChan, int requestedChannel, ChannelMap defaultInp) {
		inp = inp != null ? inp : defaultInp;
		GLSLChannelMap glslCM = getGLSLObject(inp);
		Result res = glslCM != null ? glslCM.generate(inp, sc, inpChan, requestedChannel) : null;
		return res;
	}

	
	public static Result generateResult(ChannelMap inp, MaterialConfiguration sc, GLSLChannelMap inpChan, int requestedChannel) {
		GLSLChannelMap glslCM = getGLSLObject(inp);
		Result res = glslCM != null ? glslCM.generate(inp, sc, inpChan, requestedChannel) : null;
		return res;
	}
	
	/**
	 * This should be used instead of the other one 
	 * @param inp
	 * @param cs
	 * @param inpChan
	 * @param Channel
	 * @return
	 */
	public abstract Result generate(ChannelMap inp, MaterialConfiguration cs,
			GLSLChannelMap inpChan, int Channel);
	
//	public abstract Result generate(ChannelMap inp, ShaderConfiguration cs);

	public boolean mayDiscard() {
		return false;
	}

	public static void initMap() {
		chanCol.initMap();
	}
}
