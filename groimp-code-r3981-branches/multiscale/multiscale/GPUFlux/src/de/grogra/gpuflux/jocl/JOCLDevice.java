package de.grogra.gpuflux.jocl;

import static org.jocl.CL.CL_DEVICE_ENDIAN_LITTLE;
import static org.jocl.CL.CL_MEM_READ_WRITE;
import static org.jocl.CL.CL_TRUE;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadBuffer;
import static org.jocl.CL.clFinish;
import static org.jocl.CL.clGetDeviceInfo;
import static org.jocl.CL.clReleaseCommandQueue;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Vector;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_device_id;
import org.jocl.cl_event;
import org.jocl.cl_platform_id;

public class JOCLDevice {

	private static final boolean DEBUG = false;
	
	// allocate debug buffer
	private String name;
	private String extensions;
	private String [] extensionList;
	
	private JOCLBuffer defaultDebugBuffer = null;
	private JOCLContext context;
	
	private cl_command_queue commandQueue;

	private boolean littleEndian;

	private cl_device_id device;
	
	JOCLDevice(JOCLContext context, cl_device_id device)
	{
		this.device = device;
		this.context = context;
		// Create a command-queue
		commandQueue = clCreateCommandQueue(context.getClContext(), device, CL.CL_QUEUE_PROFILING_ENABLE, null);
		
		littleEndian = getBool(device, CL_DEVICE_ENDIAN_LITTLE);
		
		 // allocate a default debug buffer
        defaultDebugBuffer = context.createBuffer(Sizeof.cl_int4*1, CL_MEM_READ_WRITE);
        
        name = getString(device, CL.CL_DEVICE_NAME);
        
        extensions = getString(device, CL.CL_DEVICE_EXTENSIONS);
    	extensionList = extensions.split(" ");
    	for( int i = 0 ; i < extensionList.length ; i++ )
    	{
    		extensionList[i] = extensionList[i].trim().toUpperCase();
    	}
	}
	
	public boolean isLittleEndian(){ return littleEndian; };
		
	public void readBuffer( JOCLBuffer buffer , int [] out ){
		clEnqueueReadBuffer( commandQueue, buffer.getBuffer(), CL_TRUE, 0,
				Math.min(out.length * 4,buffer.getSize()), Pointer.to(out), 0, null, null);
	}
	
	public void readBuffer(JOCLBuffer buffer, byte[] out) {
		clEnqueueReadBuffer( commandQueue, buffer.getBuffer(), CL_TRUE, 0,
        		Math.min(out.length, buffer.getSize()), Pointer.to(out), 0, null, null);
	}
	
	public void finalize() throws Throwable
	{
		clReleaseCommandQueue(commandQueue);
		super.finalize();
	}
	
	public void finish() {
		clFinish(commandQueue);
	}
	
	public void executeKernel(JOCLKernel joclKernel, int stream_size) {
		executeKernel( joclKernel, stream_size, null );
	}
	
	public void executeKernel( JOCLKernel kernel , int n , cl_event event )
	{
		if( n > 0 )
		{
			// allocate debug buffer
			JOCLBuffer debugBuffer = defaultDebugBuffer;
			
			if( DEBUG ){
				System.out.printf("WARNING: Allocate debug buffer");
				debugBuffer = context.createBuffer(Sizeof.cl_int4*n, CL_MEM_READ_WRITE);
			}
			
			// set debug buffer
			kernel.setKernelArgMemBuffer(0, debugBuffer);
			
			/* Enqueue a kernel run call */
			long workGroupSize = 64;
			long [] globalThreads = new long[1];
			globalThreads[0] = n;
			if (globalThreads[0] % workGroupSize != 0)
				globalThreads[0] = (globalThreads[0] / workGroupSize + 1) * workGroupSize;
			long [] localThreads = new long[1];
			localThreads[0] = workGroupSize;
			
			clEnqueueNDRangeKernel(commandQueue, kernel.getKernel(), 1, null,
					globalThreads, localThreads, 0, null, event);
			
			if( DEBUG ){
				System.out.printf("WARNING: Write debug buffer to file");
				byte [] debugData = new byte[Sizeof.cl_int4*n];
				
				// Read debug data
		        readBuffer( debugBuffer, debugData );
		        
		        // Write debug data
		        writeDebugFile( "debug.txt" , debugData );
			}
		}
	}
		
	public String getName()
	{
		return name;
	}
	
	public static boolean isLittleEndian(cl_device_id device)
	{
		return getBool(device, CL_DEVICE_ENDIAN_LITTLE);
	}
	
	public static long getDeviceType(cl_device_id device)
	{
		return getLong(device, CL.CL_DEVICE_TYPE );
	}

	  /**
     * Returns the value of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @return The value
     */
    private static boolean getBool(cl_device_id device, int paramName)
    {
         return getInt(device, paramName) != 0;
    }
	
	  /**
     * Returns the value of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @return The value
     */
    private static int getInt(cl_device_id device, int paramName)
    {
        return getInts(device, paramName, 1)[0];
    }

    /**
     * Returns the values of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @param numValues The number of values
     * @return The value
     */
    private static int[] getInts(cl_device_id device, int paramName, int numValues)
    {
        int values[] = new int[numValues];
        clGetDeviceInfo(device, paramName, Sizeof.cl_int * numValues, Pointer.to(values), null);
        return values;
    }

    /**
     * Returns the value of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @return The value
     */
    private static long getLong(cl_device_id device, int paramName)
    {
        return getLongs(device, paramName, 1)[0];
    }

    /**
     * Returns the values of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @param numValues The number of values
     * @return The value
     */
    private static long[] getLongs(cl_device_id device, int paramName, int numValues)
    {
        long values[] = new long[numValues];
        clGetDeviceInfo(device, paramName, Sizeof.cl_long * numValues, Pointer.to(values), null);
        return values;
    }
	
	  /**
     * Returns the value of the device info parameter with the given name
     *
     * @param device The device
     * @param paramName The parameter name
     * @return The value
     */
    private static String getString(cl_device_id device, int paramName)
    {
        // Obtain the length of the string that will be queried
        long size[] = new long[1];
        clGetDeviceInfo(device, paramName, 0, null, size);

        // Create a buffer of the appropriate size and fill it with the info
        byte buffer[] = new byte[(int)size[0]];
        clGetDeviceInfo(device, paramName, buffer.length, Pointer.to(buffer), null);

        // Create a string from the buffer (excluding the trailing \0 byte)
        return new String(buffer, 0, buffer.length-1);
    }
    
    public ByteOrder getByteOrder() {
		if( isLittleEndian() )
			return ByteOrder.LITTLE_ENDIAN;
		return ByteOrder.BIG_ENDIAN;
	}
    
    private void writeDebugFile(String fileName, byte [] debugData)
	{
		ByteBuffer debugBuffer = ByteBuffer.wrap(debugData);
		if( littleEndian )
			debugBuffer.order(getByteOrder());
		int length = debugData.length / (4*4);
		
		IntBuffer intBuffer = debugBuffer.asIntBuffer();
		FloatBuffer floatBuffer = debugBuffer.asFloatBuffer();

		FileWriter outFile;
		try {
			outFile = new FileWriter(fileName);
			
			PrintWriter out = new PrintWriter(outFile);
			
			for( int i = 0 ; i < length ; i++ )
			{
				out.println(i);
				out.print( "\t" );
				for( int j = 0 ; j < 4 ; j++ )
					out.print( "" + intBuffer.get(i*4+j) + " " );
				out.print( "\n\t" );
				for( int j = 0 ; j < 4 ; j++ )
					out.print( "" + floatBuffer.get(i*4+j) + " " );
				out.print( "\n\n" );
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public cl_device_id getClDevice() {
		return device;
	}

	public String getExtensions() {
		return extensions;
	}
	
	public String[] getExtensionList() {
		return extensionList;
	}
	
	public String getVendor() {
		return getString(device,CL.CL_DEVICE_VENDOR);
	}

	public String getVersion() {
		return getString(device,CL.CL_DEVICE_VERSION);
	}
	
	public String toString() {
		String info = "";
		info += "    Name:       " + getName() + "\n";
		info += "    Vendor:     " + getVendor() + "\n";
		info += "    Version:    " + getVersion() + "\n";
		info += "    Type:       " + deviceTypeToString(getDeviceType()) + "\n";
		info += "    Extensions: " + getExtensions() + "\n";
		return info;
	}

	public String deviceTypeToString( long type ) {
		if( type == CL.CL_DEVICE_TYPE_CPU )
			return "CPU";
		if( type == CL.CL_DEVICE_TYPE_GPU )
			return "GPU";
		if( type == CL.CL_DEVICE_TYPE_ACCELERATOR )
			return "ACCELERATOR";
		return "UNKNOWN";
	}
	
	public long getDeviceType() {
		return getDeviceType(device);
	}
	
	public static cl_platform_id getPlatform(cl_device_id device_id) {
		cl_platform_id platform[] = new cl_platform_id[1];
        clGetDeviceInfo(device_id, CL.CL_DEVICE_PLATFORM, Sizeof.cl_platform_id, Pointer.to(platform), null);
        return platform[0];
	}
	
	public static cl_device_id[] getDevices(cl_platform_id platform_id) {
		// Obtain the number of platforms
	    int numDevices[] = new int[1];
	    CL.clGetDeviceIDs(platform_id, CL.CL_DEVICE_TYPE_ALL, 0, null, numDevices);
	    
		// get all platforms
		cl_device_id devices[] = new cl_device_id[numDevices[0]];
		CL.clGetDeviceIDs(platform_id, CL.CL_DEVICE_TYPE_ALL,devices.length, devices, null);
	    
	    return devices;
	}

	public static boolean supportsExtensions(cl_device_id device_id,
			String[] extensions) {
		String extensionstr = " " + getString(device_id, CL.CL_DEVICE_EXTENSIONS).toUpperCase() + " ";
		
		for( String extension : extensions )
		{
			if( !extensionstr.contains(" " + extension + " ") )
				return false;
		}
		
		return true;
	}

	public String getIdentifier() {
		String identifier = getVendor() + " " + getName() + " " + getVersion();
		return identifier;
	}

	public static String getIdentifier(cl_device_id program_device) {
		String identifier = getVendor(program_device) + " " + getName(program_device) + " " + getVersion(program_device);
		return identifier;
	}

	private static String getVersion(cl_device_id program_device) {
		return getString(program_device,CL.CL_DEVICE_VERSION);
	}

	private static String getName(cl_device_id program_device) {
		return getString(program_device,CL.CL_DEVICE_NAME);
	}

	private static String getVendor(cl_device_id program_device) {
		return getString(program_device,CL.CL_DEVICE_VENDOR);
	}

}
