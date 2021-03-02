package de.grogra.gpuflux.jocl.compute;

import java.io.IOException;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4d;

import de.grogra.gpuflux.utils.ByteArray;

public class ComputeByteBuffer {

	private abstract class ComputeByteArray extends ByteArray
	{
		private int written = 0;

		public int size() {
			return written ;
		}
		
		public void reset() {
			super.reset();
			written = 0;
		}
		
		public void allign( int size ) throws IOException
		{
			int offset = ( written % size );
			if( offset != 0 ){
				throw new IOException( "Non-alligned write in JOCL stream" );
			}
		}

		public void write( byte [] b ) throws IOException
		{
			super.write(b);
			written += b.length;
		}
		
		public void write(byte[] b, int off, int len)
		{
			super.write(b,off,len);
			written += b.length;
		}
	   		
		public void write(byte b) throws IOException
		{
			super.write(b);
			written++;
		}
		
		protected byte getByte( int value , int idx ){ return (byte)(value >> (8*idx)); };
		protected int getInt( long value , int idx ){ return (int)(value >> (32*idx)); };
		
		public abstract void writeInt(int v) throws IOException;
		public abstract void writeLong(long v) throws IOException;
		public abstract void writeShort(int v) throws IOException;
	};	
	
	public ByteArray getLittleEndianBuffer()
	{
		return littleEndianBuffer;
	}
	
	public ByteArray getBigEndianBuffer()
	{
		return bigEndianBuffer;
	}
	
	private ComputeByteArray littleEndianBuffer, bigEndianBuffer;
		
	public ComputeByteBuffer( boolean littleEndian, boolean bigEndian )
	{
		if( littleEndian )
		{
			littleEndianBuffer = new ComputeByteArray()
			{
				public void	writeInt(int i) throws IOException
				{
					allign(4);
					write( getByte(i,0) );
					write( getByte(i,1) );
					write( getByte(i,2) );
					write( getByte(i,3) );
				}
				
				public void	writeLong(long l) throws IOException
				{
					allign(8);
					writeInt(getInt(l,0));
					writeInt(getInt(l,1));
				}
				
				public void	writeShort(int s) throws IOException
			    {
					allign(2);
					write( getByte(s,0) );
					write( getByte(s,1) );
			    }
			};
		}
		if( bigEndian )
		{
			bigEndianBuffer = new ComputeByteArray()
			{
				public void	writeInt(int i) throws IOException
				{
					allign(4);
					write( getByte(i,3) );
					write( getByte(i,2) );
					write( getByte(i,1) );
					write( getByte(i,0) );
				}
				
				public void	writeLong(long l) throws IOException
				{
					allign(8);
					writeInt(getInt(l,1));
					writeInt(getInt(l,0));
				}
				
				public void	writeShort(int s) throws IOException
			    {
					allign(2);
					write( getByte(s,1) );
					write( getByte(s,0) );
			    }
			};
		}
	}
	
	public void reset()
	{
		if( littleEndianBuffer != null )
			littleEndianBuffer.reset();
		if( bigEndianBuffer != null )
			bigEndianBuffer.reset();
	}
	
	public void	writeDouble(double v) throws IOException
	{
		long l = Double.doubleToLongBits(v);
		writeLong( l );
	}
   
	public void	writeFloat(float v) throws IOException
    {
    	int i = Float.floatToIntBits(v);
    	writeInt( i );
    }
	
	public void	writeFloat(double d) throws IOException
    {
    	int i = Float.floatToIntBits((float)d);
    	writeInt( i );
    }
	
	public void	writeInt(int i) throws IOException
	{
		if( littleEndianBuffer != null )
			littleEndianBuffer.writeInt(i);
		if( bigEndianBuffer != null )
			bigEndianBuffer.writeInt(i);
	}
	
	public void	writeLong(long l) throws IOException
	{
		if( littleEndianBuffer != null )
			littleEndianBuffer.writeLong(l);
		if( bigEndianBuffer != null )
			bigEndianBuffer.writeLong(l);
	}
	
	public void	writeShort(int s) throws IOException
    {
		if( littleEndianBuffer != null )
			littleEndianBuffer.writeShort(s);
		if( bigEndianBuffer != null )
			bigEndianBuffer.writeShort(s);
    }

	public int size() {
		if( littleEndianBuffer != null )
			return littleEndianBuffer.size();
		if( bigEndianBuffer != null )
			return bigEndianBuffer.size();
		return 0;
	}
	
	public void write( byte [] b ) throws IOException
	{
		if( littleEndianBuffer != null )
			littleEndianBuffer.write(b);
		if( bigEndianBuffer != null )
			bigEndianBuffer.write(b);
	}
	
	public void write(byte[] b, int off, int len) throws IOException
	{
		if( littleEndianBuffer != null )
			littleEndianBuffer.write(b,off,len);
		if( bigEndianBuffer != null )
			bigEndianBuffer.write(b,off,len);
	}
   		
	public void write(byte b) throws IOException
	{
		if( littleEndianBuffer != null )
			littleEndianBuffer.write(b);
		if( bigEndianBuffer != null )
			bigEndianBuffer.write(b);
	}
	
	public void write(Tuple2f v) throws IOException {
		writeFloat( v.x );
		writeFloat( v.y );
	}

	public void write(Tuple3f v) throws IOException {
		writeFloat( v.x );
		writeFloat( v.y );
		writeFloat( v.z );
	}
	
	public void write(Tuple3d v) throws IOException {
		writeFloat( (float) v.x );
		writeFloat( (float) v.y );
		writeFloat( (float) v.z );
	}
	
	public void write(Tuple4d v) throws IOException {
		writeFloat( (float) v.x );
		writeFloat( (float) v.y );
		writeFloat( (float) v.z );
	}
	
	public void write( Matrix3f m ) throws IOException
	{
		writeFloat(m.m00);writeFloat(m.m01);writeFloat(m.m02);
		writeFloat(m.m10);writeFloat(m.m11);writeFloat(m.m12);
		writeFloat(m.m20);writeFloat(m.m21);writeFloat(m.m22);
	}

	public void write(Matrix4f m) throws IOException {
		writeFloat(m.m00);writeFloat(m.m01);writeFloat(m.m02);
		writeFloat(m.m10);writeFloat(m.m11);writeFloat(m.m12);
		writeFloat(m.m20);writeFloat(m.m21);writeFloat(m.m22);
		
		writeFloat(m.m03);
		writeFloat(m.m13);
		writeFloat(m.m23);
	}

	public void writeBoolean(boolean bool) throws IOException {
		writeInt( bool?-1:0 );
	}

	public void write(int[] pixels) throws IOException {
		for( int i = 0 ; i < pixels.length ; i++ )
			writeInt( pixels[i] );
	}

	public ByteArray getBuffer(boolean littleEndian) {
		if(littleEndian)
			return getLittleEndianBuffer();
		else
			return getBigEndianBuffer();
	}
	
}
