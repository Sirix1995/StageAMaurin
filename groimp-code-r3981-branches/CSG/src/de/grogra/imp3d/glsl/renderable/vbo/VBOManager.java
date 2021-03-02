package de.grogra.imp3d.glsl.renderable.vbo;

import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.utility.VertexBufferObject;

public class VBOManager {
	public static final int BOX = 0;
	
	private static final int size = 1;

	VertexBufferObject box = null;
	
	public VertexBufferObject getVBO(int index, OpenGLState glState) {
		if(box == null)
			box = new GLSLBoxVBO(glState);
		return box;
	}
	
}
