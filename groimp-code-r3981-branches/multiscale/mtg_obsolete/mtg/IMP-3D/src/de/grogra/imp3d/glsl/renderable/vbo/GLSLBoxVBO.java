package de.grogra.imp3d.glsl.renderable.vbo;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.OpenGLState;
import de.grogra.imp3d.glsl.utility.VertexBufferObject;

public class GLSLBoxVBO extends VertexBufferObject {

	public GLSLBoxVBO(OpenGLState glState) {
		super(glState);
	}

	private static final float[] data = {
		0, 1f/3,		-1, 0, 0,	-.5f, -.5f, 0,
		.25f, 1f/3,		-1, 0, 0,	-.5f, -.5f, 1,
		.25f, 2f/3,		-1, 0, 0,	-.5f,  .5f, 1,
		0, 2f/3,		-1, 0, 0,	-.5f,  .5f, 0,
		
		.75f, 1f/3,		 1, 0, 0,	 .5f, -.5f, 0,
		.75f, 2f/3,		 1, 0, 0,	 .5f,  .5f, 0,
		.5f, 2f/3,		 1, 0, 0,	 .5f,  .5f, 1,
		.5f, 1f/3,		 1, 0, 0,	 .5f, -.5f, 1,
		
		.25f, 0,		0, -1, 0,	-.5f, -.5f, 0,
		.5f, 0,			0, -1, 0,	 .5f, -.5f, 0,
		.5f, 1f/3,		0, -1, 0,	 .5f, -.5f, 1,
		.25f, 1f/3,		0, -1, 0,	-.5f, -.5f, 1,

		.25f, 1,		0,  1, 0,	-.5f,  .5f, 0,
		.25f, 2f/3,		0,  1, 0,	-.5f,  .5f, 1,
		.5f, 2f/3,		0,  1, 0,	 .5f,  .5f, 1,
		.5f, 1,			0,  1, 0,	 .5f,  .5f, 0,

		1, 1f/3,		0, 0, -1,	-.5f, -.5f, 0,
		1, 2f/3,		0, 0, -1,	-.5f,  .5f, 0,
		.75f, 2f/3,		0, 0, -1,	 .5f,  .5f, 0,
		.75f, 1f/3,		0, 0, -1,	 .5f, -.5f, 0,
		
		.25f, 1f/3,		0, 0,  1,	-.5f, -.5f, 1,
		.5f, 1f/3,		0, 0,  1,	 .5f, -.5f, 1,
		.5f, 2f/3,		0, 0,  1,	 .5f,  .5f, 1,
		.25f, 2f/3,		0, 0,  1,	-.5f,  .5f, 1,		
	};
	
	public int getVertexFormat() { return GL.GL_T2F_N3F_V3F; }
	public int entryPerVertex() { return 8; }
	public int getVertexDrawType() { return GL.GL_QUADS; }

	@Override
	public float[] getVertices() {
		return data;
	}

}
