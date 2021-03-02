package de.grogra.gpuflux.jocl;

import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clReleaseProgram;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_device_id;
import org.jocl.cl_program;

public class JOCLProgram {
	private cl_program program;
	private String buildLog, name;
	
	private HashMap<String, JOCLKernel> kernelCache = new HashMap<String, JOCLKernel>();
	
	public JOCLProgram( cl_program program, String name, String buildLog )
	{
		this.name = name;
		this.program = program;
		this.buildLog = buildLog;
		
		if( JOCLContext.logResourceManagement )
			System.out.println( "GPUFlux: Create program " + name );
	}
	
	public cl_program getProgram(){ return program; };
	
	public String getBuildLog() { return buildLog; };
	
	public JOCLKernel createKernel( String kernelname )
	{	
		JOCLKernel kernel = (JOCLKernel)kernelCache.get(kernelname);
		
		if( kernel == null )
		{
			// create the kernel
	        kernel = new JOCLKernel( clCreateKernel(program, kernelname, null), kernelname );	
		}
		        
        return kernel;
	}
	
	public String [] getBinaries()
	{
		byte binaryDatas[][] = getBinarieDatas();
		
		// Store the binary data (for NVIDIA, this is the PTX data)
		String [] binaries = new String[binaryDatas.length];
		for (int i=0; i<binaryDatas.length; i++)
		{
			binaries[i] = new String(binaryDatas[i]);
		}
		
		return binaries;
	}
	
	
	public cl_device_id[] getDevices()
	{
		// Obtain the length of the string that will be queried
        long size[] = new long[1];
		CL.clGetProgramInfo(program, CL.CL_PROGRAM_NUM_DEVICES, 0, null, size);
		
		int numDevices = (int)size[0];
		
		cl_device_id devices[] = new cl_device_id[numDevices];
		CL.clGetProgramInfo(program, CL.CL_PROGRAM_DEVICES , numDevices * Sizeof.cl_device_id, Pointer.to(devices), null);
		
		return devices;
	}
	
	public byte [][] getBinarieDatas()
	{
		// Obtain the length of the string that will be queried
        long size[] = new long[1];
		CL.clGetProgramInfo(program, CL.CL_PROGRAM_NUM_DEVICES, 0, null, size);
		
		int numDevices = (int)size[0];
		
		// Obtain the length of the binary data that will be queried, for each device
		long binaryDataSizes[] = new long[numDevices];
		CL.clGetProgramInfo(program, CL.CL_PROGRAM_BINARY_SIZES,
				numDevices * Sizeof.size_t, Pointer.to(binaryDataSizes), null);

		// Allocate arrays that will store the binary data, each
		// with the appropriate size
		byte binaryDatas[][] = new byte[numDevices][];
		for (int i=0; i<numDevices; i++)
		{
			int binaryDataSize = (int)binaryDataSizes[i];
			binaryDatas[i] = new byte[binaryDataSize];
		}
		
		// Create a pointer to an array of pointers which are pointing
		// to the binary data arrays
		Pointer binaryDataPointers[] = new Pointer[numDevices];
		for (int i=0; i<numDevices; i++)
		{
			binaryDataPointers[i] = Pointer.to(binaryDatas[i]);
		}
		
		// Query the binary data
		Pointer pointerToBinaryDataPointers = Pointer.to(binaryDataPointers);
		CL.clGetProgramInfo(program, CL.CL_PROGRAM_BINARIES,
				numDevices * Sizeof.POINTER, pointerToBinaryDataPointers, null);

		return binaryDatas;
	}
	
	public void finalize()  throws Throwable
	{
		if( JOCLContext.logResourceManagement )
			System.out.println( "GPUFlux: Release program " + name );
		
        // release the program
		clReleaseProgram( program );
		super.finalize();
	}

	public String getIdenifier() {
		return getIdenifier(name);
	}
	
	public static String getIdenifier( String name ) {
		String identifier = "" + name;
		return identifier;
	}

	public String getName() {
		return name;
	}
	
}
