package de.grogra.imp3d.gl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

class CacheData
{
	int polygonSize;
	IntBuffer ib;
	FloatBuffer vb;
	ByteBuffer nb;
	FloatBuffer uvb;

	// id for VBO
	int[] id;
	int vsize;
	int nsize;
	int tsize;
}
