package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.math.ChannelMap;
import de.grogra.math.Graytone;

/**
 * Implementation of Graytone
 * 
 * @author Konni Hartmann
 */
public class GLSLGraytone extends GLSLChannelMap {

	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration cs,
			GLSLChannelMap inpChan, int channel){
		assert (inp instanceof Graytone);
		Graytone gr = (Graytone) inp;
		return new Result("" + gr.getValue(), Result.ET_FLOAT);
	}

	@Override
	public Class instanceFor() {
		return Graytone.class;
	}

}
