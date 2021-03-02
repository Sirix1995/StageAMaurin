package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.shading.Carpenter;

/**
 * Trigger class. Used to enable Carpenter shader. All used functionalities reside in superclass.
 * @see GLSLSyntheticTexture
 * @author Konni Hartmann
 */
public class GLSLCarpenter extends GLSLSyntheticTexture {

	@Override
	public Class<Carpenter> instanceFor() {
		return Carpenter.class;
	}

}
