package de.grogra.gpuflux.jocl;

import de.grogra.gpuflux.utils.ByteArray;

import org.jocl.*;

import static org.jocl.CL.*;

public class JOCLKernel {

	private cl_kernel kernel;
	private String name;

	public JOCLKernel( cl_kernel kernel, String name )
	{
		if( JOCLContext.logResourceManagement )
			System.out.println( "GPUFlux: Create kernel " + name );
		
		this.name = name;
		this.kernel = kernel;
	}
	
	public cl_kernel getKernel(){ return kernel; };
	
	public void finalize()  throws Throwable
	{
		if( JOCLContext.logResourceManagement )
			System.out.println( "GPUFlux: Release kernel " + name );
		
		clReleaseKernel( kernel );
		super.finalize();
	}
	
	public void setKernelArg( int arg_idx , int arg_size , Pointer p )
	{
		clSetKernelArg(kernel, arg_idx, arg_size, p);
	}
	
	public void setKernelArgInt( int arg_idx , int i )
	{
		clSetKernelArg(kernel, arg_idx, Sizeof.cl_int, Pointer.to(new int[]{i}));
	}
	
	public void setKernelArgFloat( int arg_idx , float f )
	{
		clSetKernelArg(kernel, arg_idx, Sizeof.cl_float, Pointer.to(new float[]{f}));
	}
	
	public void setKernelArgMemBuffer( int arg_idx , JOCLBuffer buf )
	{
		clSetKernelArg(kernel, arg_idx, Sizeof.cl_mem, Pointer.to(buf.getBuffer()));
	}

	public void setKernelArgBuffer(int arg_idx, ByteArray cameraBuffer) {
		clSetKernelArg(kernel, arg_idx, Sizeof.cl_char * cameraBuffer.size() , Pointer.to(cameraBuffer.getBuffer()));
	}

}
