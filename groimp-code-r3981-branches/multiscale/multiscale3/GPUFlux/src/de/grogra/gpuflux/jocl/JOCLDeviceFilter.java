package de.grogra.gpuflux.jocl;

import java.util.AbstractList;
import org.jocl.cl_device_id;

public interface JOCLDeviceFilter {

	public abstract void init();
	public abstract void filter( cl_device_id device_id );
	public abstract AbstractList<cl_device_id> getDevices(); 
		
}
