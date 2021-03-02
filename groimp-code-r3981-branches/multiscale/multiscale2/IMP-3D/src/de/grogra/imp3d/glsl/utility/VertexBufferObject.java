package de.grogra.imp3d.glsl.utility;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

import de.grogra.imp3d.glsl.OpenGLState;

public abstract class VertexBufferObject implements GLSLOpenGLObject {

	private int index_vertex[] = {0};
	private int vertexCount;
	private int dataCount;
	
	public VertexBufferObject(OpenGLState glState) {
		glState.staticObjects.add(this);
	}
		
	public abstract float[] getVertices();
	public int getVertexFormat() { return GL.GL_T2F_N3F_V3F; }
	public int entryPerVertex() { return 8; }
	public int getVertexDrawType() { return GL.GL_QUADS; }
	
	private void fill(OpenGLState glState){
		GL gl = glState.getGL();
		gl.glGenBuffersARB(1, index_vertex, 0);

		float[] vertices = getVertices();
		dataCount = vertices.length;
		vertexCount = dataCount / entryPerVertex();
		
		FloatBuffer vertBuf = BufferUtil.newFloatBuffer(dataCount);
		
		vertBuf.put(vertices);
		vertBuf.flip();
		
		glState.getGL().glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, index_vertex[0]);		
		gl.glBufferDataARB(
				GL.GL_ARRAY_BUFFER_ARB, dataCount*BufferUtil.SIZEOF_FLOAT, vertBuf, GL.GL_STATIC_DRAW_ARB);
	}
	
	public void draw(OpenGLState glState) {
		GL gl = glState.getGL();
		if(index_vertex[0] == 0)
			fill(glState);
		else
			gl.glBindBuffer(GL.GL_ARRAY_BUFFER_ARB, index_vertex[0]);
//		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);             // activate vertex coords array
//		gl.glEnableClientState(GL.GL_NORMAL_ARRAY);             // activate vertex coords array
//		gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);             // activate vertex coords array
		// do same as vertex array except pointer
//		gl.glVertexPointer(3, GL_FLOAT, 0, 0);               // last param is offset, not ptr
		
//		System.out.println(vertexCount + "/" + entryPerVertex());
		gl.glInterleavedArrays(getVertexFormat(), entryPerVertex()*BufferUtil.SIZEOF_FLOAT, 0);
		gl.glDrawArrays(getVertexDrawType(), 0, vertexCount);
		gl.glDisableClientState(GL.GL_VERTEX_ARRAY);             // activate vertex coords array
		gl.glDisableClientState(GL.GL_NORMAL_ARRAY);             // activate vertex coords array
		gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);             // activate vertex coords array
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
	}
	
	public void cleanup(OpenGLState glState, boolean javaOnly) {
		index_vertex[0] = 0;
		if(!javaOnly)
			glState.getGL().glDeleteBuffers(1, index_vertex, 0);
	}

}
