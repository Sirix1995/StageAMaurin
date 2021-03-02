package de.grogra.imp3d.glsl.utility;

import javax.media.opengl.GL;

import de.grogra.imp3d.glsl.GLSLDisplay;

public class GLSLQueuedFloatTexture extends GLSLQueuedTexture {
	float[] data;

	public void setData(int width, int height, float[] data) {
		this.width = width;
		this.height = height;
		this.data = data;
	}
	
	// XXX: Too simple!!
	// this should be done by the Texture Manager
	
	/**
	 * @return the index
	 */
	@Override
	public int getIndex(GL gl) {
		if(this.index == 0) {
			if(!createFloat(gl, width, height, data)) {
				GLSLDisplay.printDebugInfoN("! Could not create Texture");
			} else 
				GLSLDisplay.printDebugInfoN("Created queued Texture");
		}
		return this.index;
	}
	
}
