package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.XYZTransformation;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

public class GLSLXYZTransformation extends GLSLChannelMapNode {

	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration sc,
			GLSLChannelMap inpChan, int channel){
		assert (inp instanceof XYZTransformation);
		XYZTransformation xyzT = (XYZTransformation) inp;
		Result res = generateResultWithChannelDefault(xyzT.getInput(), sc,
				inpChan, xyzT.isUseGlobal() ? Channel.PX : Channel.X);		
		
		return Transform3DCollection.transform(res, xyzT
				.getTransform(), sc);
	}

	@Override
	public Class instanceFor() {
		return XYZTransformation.class;
	}

}
