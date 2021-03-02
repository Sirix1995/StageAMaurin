package de.grogra.gpuflux.jocl.compute;

import static org.jocl.CL.*;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.Vector;
import java.util.ArrayList;

import org.jocl.cl_device_id;
import org.jocl.cl_platform_id;

import de.grogra.gpuflux.jocl.JOCLCPP;
import de.grogra.gpuflux.jocl.JOCLDeviceFilter;
import de.grogra.gpuflux.jocl.JOCLBuffer;
import de.grogra.gpuflux.jocl.JOCLContext;
import de.grogra.gpuflux.jocl.JOCLDevice;
import de.grogra.gpuflux.jocl.JOCLKernel;
import de.grogra.gpuflux.jocl.JOCLPlatform;
import de.grogra.gpuflux.jocl.JOCLPlatform.JOCLPlatformVersion;
import de.grogra.gpuflux.jocl.JOCLProgram;
import de.grogra.gpuflux.jocl.JOCLSource;
import de.grogra.pf.ui.Workbench;

public class ComputeContext {

	private Vector<Device> devices = new Vector<Device>();
	private Vector<JOCLContext> contexts = new Vector<JOCLContext>();
	private JOCLDeviceFilter filter;
	String log = "";
	private boolean hasLittleEndian = false;
	private boolean hasBigEndian = false;
	
	public String getLog()
	{
		return log;
	}
	
	public ComputeContext( JOCLDeviceFilter filter )
	{
		this.filter = filter;
		
		 log += "<B>Compute Context</B>\n";
		
		// Enable exceptions and subsequently omit error checks in this sample
        setExceptionsEnabled(true);
        
        // Obtain the number of platforms
        int numPlatforms[] = new int[1];
        clGetPlatformIDs(0, null, numPlatforms);
        
		// get all platforms
		cl_platform_id platforms[] = new cl_platform_id[numPlatforms[0]];
        clGetPlatformIDs(platforms.length, platforms, null);
		
        Vector<JOCLPlatform> plaforms = new Vector<JOCLPlatform>();
        
        // include only the latest version of each platform to prevent double booking a device
        for(cl_platform_id platform_id : platforms )
        {
        	JOCLPlatform newPlatform = new JOCLPlatform( platform_id );
        	
        	JOCLPlatformVersion version = newPlatform.getVersion();
        	
        	Iterator<JOCLPlatform> itr = plaforms.iterator();
        	while(itr.hasNext())
        	{
        		JOCLPlatform platform = itr.next();
        		
        		if( platform.getName() == newPlatform.getName() )
        		{
	        		if( version.compare(version, platform.getVersion()) < 0 )
	        		{
	        			newPlatform = null;
	        			break;
	        		}
	        		else
	        		{
	        			// the new platform has a higher version
	        			// the old platform is ignored
	        			itr.remove();
	        		}
        		}
        	}
        	
        	if( newPlatform != null )
        		plaforms.add( newPlatform );
        }
        
        log += "    <B>Available Compute Platforms:</B>\n";
        
        // create a context for each device
        filter.init();
        for(JOCLPlatform platform : plaforms )
        {
        	log += "    Platform: " + platform.getName() + "\n";
        	log += "    Vendor:   " + platform.getVendor() + "\n";
        	log += "    Version:  " + platform.getVersion() + "\n\n";
        	
        	for( cl_device_id device_id : platform.getDevices() )
        	{
        		filter.filter(device_id);
        	}
        }
        
        AbstractList<cl_device_id> device_ids = filter.getDevices();
        
        log += "    <B>Selected Compute Devices:</B>\n";
        
        if( device_ids.size() == 0 )
        {
        	log += "    No suitable devices where found\n";
        }
        
		for( cl_device_id device_id : device_ids )
		{
			AbstractList<cl_device_id> device_id_list = new ArrayList<cl_device_id>(1);
			device_id_list.add(device_id);
						
			JOCLPlatform platform = new JOCLPlatform(JOCLDevice.getPlatform(device_id));
			
			JOCLContext context = platform.createContext(device_id_list);
			
			log += context.getLog();
			
			Device device = new Device( contexts.size() , context.getDevices().get(0) , this );
			devices.add(device);
			contexts.add( context );
			
			hasLittleEndian |= context.hasLittleEndian();
       		hasBigEndian |= context.hasBigEndian();
		}
		
		log += "\n";
	}
		
	public Kernel createKernel( String filename, String kernelname, String options) throws IOException 
	{
		JOCLSource source = new JOCLSource(filename);
		
		String log = "<html><pre><i>Load kernel:</i> " + kernelname + "\n";
		
		Workbench w = Workbench.current ();
				
		Kernel kernel = new Kernel(contexts.size(), this);
		for( int i = 0 ; i < contexts.size(); i++ )
		{
			String key = filename + options;
						
			JOCLProgram program = contexts.get(i).getProgram(key);
			if( program == null )
			{
				long startTime = System.currentTimeMillis ();
								
				log += "Device: \n" + contexts.get(i).getDeviceNames();

				// Program Setup
				try {
					program = contexts.get(i).loadProgram(key, source, options);
				} catch (IOException e) {
					throw new IOException( filename + " " + options + "\n\n" +	source.dereferenceError(e.getMessage()) );
				}
				
				long buildTime = System.currentTimeMillis () - startTime;
				
				log += "Load time: " + buildTime + " ms\n";
				log += "Build log: " + source.dereferenceError(program.getBuildLog());
			}
			
			JOCLKernel joclKernel = program.createKernel(kernelname);
			kernel.setContextKernel(i, joclKernel);
		}
		
		if( w != null )
			w.logGUIInfo(log + "</pre></html>");
		
		return kernel;
	}

	public SharedBuffer createSharedBuffer( ComputeByteBuffer computeByteBuffer, long flags )
	{
		SharedBuffer buffer = new SharedBuffer(contexts.size(), this);
		for( int i = 0 ; i < contexts.size(); i++ )
		{
			JOCLBuffer joclLittleEndianBuffer=null, joclBigEndianBuffer=null;
			if( contexts.get(i).hasLittleEndian() )
				joclLittleEndianBuffer = contexts.get(i).createBufferFromByteArray( computeByteBuffer.getLittleEndianBuffer(), flags );
			if( contexts.get(i).hasBigEndian() )
				joclBigEndianBuffer = contexts.get(i).createBufferFromByteArray( computeByteBuffer.getBigEndianBuffer(), flags );
			buffer.setContextBuffer(i, joclLittleEndianBuffer, joclBigEndianBuffer);
		}
		return buffer;
	}
	
	public AbstractList<Device> getDeviceList()
	{
		return devices;
	}
	
	protected JOCLContext getContext( int id )
	{
		return contexts.get(id);
	}

	public JOCLDeviceFilter getFilter() {
		return filter;
	}
	
	public boolean hasLittleEndian() {
		return hasLittleEndian;
	}

	public boolean hasBigEndian() {
		return hasBigEndian;
	}

	public Device getPrimaryDevice() {
		return devices.get(0);
	}

	public boolean valid() {
		return (devices.size() != 0);
	}
	
	public void finish() {
		for( Device device : devices )
			device.finish();
	}
	
	public ComputeByteBuffer createByteBuffer()
	{
		return new ComputeByteBuffer(hasLittleEndian() , hasBigEndian());
	}

	public Object aquireLog() {
		String _log = log;
		log = "";
		return _log;
	}
}
