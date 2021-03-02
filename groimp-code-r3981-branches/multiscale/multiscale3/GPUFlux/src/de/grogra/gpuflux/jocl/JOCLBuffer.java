package de.grogra.gpuflux.jocl;

import org.jocl.*;
import static org.jocl.CL.*;

public class JOCLBuffer {

	static int memoryUsage = 0;
	
	private static void AllocateMemory( int size ){
		memoryUsage += size;
	}
	private static void ReleaseMemory( int size ){
		memoryUsage -= size;
	}
	
	public static int getMemoryUsage() {
		return memoryUsage;
	}
	
	private cl_mem buffer;
	private int size;

	public JOCLBuffer( cl_mem buffer , int size )
	{
		this.buffer = buffer;
		this.size = size;
		
		AllocateMemory( size );
		
		if( JOCLContext.logResourceManagement )
		{
			System.out.println( "GPUFlux: Create Buffer of " + size + " bytes" );
			System.out.println( "GPUFlux: \tTotal memory usage: " + memoryUsage + " bytes" );
		}
	}
	
	public int getSize() {return size; };
	public cl_mem getBuffer(){ return buffer; };
	
	public void finalize() throws Throwable
	{
		ReleaseMemory( size );
		clReleaseMemObject( buffer );
		
		if( JOCLContext.logResourceManagement )
		{
			System.out.println( "GPUFlux: Release Buffer of " + size + " bytes" );
			System.out.println( "GPUFlux: \tTotal memory usage: " + memoryUsage + " bytes" );
		}
		
		super.finalize();
	}
}
