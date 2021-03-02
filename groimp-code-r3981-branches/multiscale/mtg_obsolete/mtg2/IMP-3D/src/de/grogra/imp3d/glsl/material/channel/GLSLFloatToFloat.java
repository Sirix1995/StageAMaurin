package de.grogra.imp3d.glsl.material.channel;

import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.xl.lang.FloatToFloat;

/**
 * Simple class that represents GroImps <code>FloatToFloat</code> classes.
 * 
 * @author Konni Hartmann
 */
public abstract class GLSLFloatToFloat {
	public abstract Result process(Result input, FloatToFloat fkt, ShaderConfiguration sc);

	public abstract Class<?> instanceFor();
}
