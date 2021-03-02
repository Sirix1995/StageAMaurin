package de.grogra.gpuflux.utils;

import java.io.ByteArrayOutputStream;

public class ByteArray extends ByteArrayOutputStream
{
	public ByteArray() {
		super();
	}
	
	public ByteArray(int capacity) {
		super(capacity);
	}

	public byte [] getBuffer() { return buf; };
}

