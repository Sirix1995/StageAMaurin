package de.grogra.imp3d.glsl.utility;

import de.grogra.imp3d.glsl.OpenGLState;

public interface FBOAttachment {
	void attachToFbo(OpenGLState glState, int where);
	public void create(OpenGLState glState, int FORMAT);
	void resize(OpenGLState glState);
	void delete(OpenGLState glState, boolean javaonly);

	public int estimateSizeInByte ();
}
