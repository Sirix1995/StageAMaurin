package de.grogra.gpuflux.jocl;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.clCreateContextFromType;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clGetPlatformInfo;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.StringTokenizer;

import org.jocl.CL;
import org.jocl.CLException;
import org.jocl.Pointer;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

public class JOCLPlatform {

	public class JOCLPlatformVersion implements Comparator<JOCLPlatformVersion>
	{
		private String info;
		private int major, minor;

		public JOCLPlatformVersion(int major, int minor, String info) {
			this.setMajor(major);
			this.setMinor(minor);
			this.setInfo(info);
		}

		private void setInfo(String info) {
			this.info = info;
		}

		public String getInfo() {
			return info;
		}

		private void setMajor(int major) {
			this.major = major;
		}

		public int getMajor() {
			return major;
		}

		private void setMinor(int minor) {
			this.minor = minor;
		}

		public int getMinor() {
			return minor;
		}

		@Override
		public int compare(JOCLPlatformVersion o1, JOCLPlatformVersion o2) {
			if( o1.major < o2.major )
				return -1;
			if( o1.major > o2.major )
				return 1;
			
			if( o1.minor < o2.minor )
				return -1;
			if( o1.minor > o2.minor )
				return 1;
			
			return 0;
		}
		
		@Override
		public String toString()
		{
			String str = "";
			str += major + "." + minor + " " + info;
			return str;
		}
	};
	
	private final cl_platform_id platform_id;

	public JOCLPlatform( cl_platform_id plaform_id )
	{
		this.platform_id = plaform_id;
	}
	
	public String getProfile(){
		return getString(platform_id, CL.CL_PLATFORM_PROFILE);
	}
	
	public JOCLPlatformVersion getVersion(){
		String version = getString(platform_id, CL.CL_PLATFORM_VERSION);
		
		StringTokenizer tokenizer = new StringTokenizer( version , " ." );
		
		tokenizer.nextToken();
		int major = Integer.parseInt(tokenizer.nextToken());
		int minor = Integer.parseInt(tokenizer.nextToken());
	
		String info = "";
		if(tokenizer.hasMoreTokens()) info = tokenizer.nextToken();
		
		return new JOCLPlatformVersion(major,minor,info);
	}
	
	public String getName(){
		return getString(platform_id, CL.CL_PLATFORM_NAME);
	}
	
	public String getVendor(){
		return getString(platform_id, CL.CL_PLATFORM_VENDOR);
	}
	
	public String get_EXTENSIONS(){
		return getString(platform_id, CL.CL_PLATFORM_EXTENSIONS);
	}
	
	@Override
	public String toString()
	{
		String info = "";
		info += "    Platform: " + getName() + "\n";
		info += "    Vendor: " + getVendor() + "\n";
		info += "    Version: " + getVersion() + "\n";
		return info;
	}
	
		
	public JOCLContext createContext( AbstractList<cl_device_id> device_ids )
	{
		cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform_id);
        
        try{
        	// 	Create an OpenCL context on a GPU device
  			cl_context clContext = clCreateContextFromType(
  				contextProperties, CL_DEVICE_TYPE_ALL, null, null, null);
  	
  			return new JOCLContext( clContext, device_ids );
  		}
  		catch(CLException ex1){
	  		return null;
  		}
	}
	
	private static String getString(cl_platform_id platform, int paramName)
    {
        // Obtain the length of the string that will be queried
        long size[] = new long[1];
        clGetPlatformInfo(platform, paramName, 0, null, size);

        // Create a buffer of the appropriate size and fill it with the info
        byte buffer[] = new byte[(int)size[0]];
        clGetPlatformInfo(platform, paramName, buffer.length, Pointer.to(buffer), null);

        // Create a string from the buffer (excluding the trailing \0 byte)
        return new String(buffer, 0, buffer.length-1);
    }

	public cl_device_id [] getDevices() {
		return JOCLDevice.getDevices(platform_id);
	}
		
	public static cl_platform_id[] getPlatforms()
	{
		// Obtain the number of platforms
	    int numPlatforms[] = new int[1];
	    clGetPlatformIDs(0, null, numPlatforms);
	    
		// get all platforms
		cl_platform_id platforms[] = new cl_platform_id[numPlatforms[0]];
	    clGetPlatformIDs(platforms.length, platforms, null);
	    
	    return platforms;
	}
	
}
