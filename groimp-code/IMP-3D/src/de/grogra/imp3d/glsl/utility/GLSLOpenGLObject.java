package de.grogra.imp3d.glsl.utility;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.OpenGLState;

public interface GLSLOpenGLObject {
	public void cleanup(OpenGLState glState, boolean javaOnly);
}
