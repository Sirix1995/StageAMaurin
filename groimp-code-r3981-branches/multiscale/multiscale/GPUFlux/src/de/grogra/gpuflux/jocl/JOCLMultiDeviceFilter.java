package de.grogra.gpuflux.jocl;

import java.util.AbstractList;
import java.util.Vector;

import org.jocl.CL;
import org.jocl.cl_device_id;

import de.grogra.gpuflux.FluxSettings;

public class JOCLMultiDeviceFilter implements JOCLDeviceFilter {

	private boolean GPU;
	private Vector<cl_device_id> devices;
	private boolean hasCPUDevice;
	
	public JOCLMultiDeviceFilter()
	{
		this.GPU  = FluxSettings.getOCLGPU();
	}
	
	public void init() {
		devices = new Vector<cl_device_id>();
		hasCPUDevice = false;
	}
	
	public void filter(cl_device_id device_id) {
		
		long deviceType = JOCLDevice.getDeviceType(device_id);
		
		if( (deviceType & (CL.CL_DEVICE_TYPE_GPU | CL.CL_DEVICE_TYPE_ACCELERATOR)) != 0 && GPU == false )
			return;
		
		// allow only a single endianess
		if( devices.size() != 0 && JOCLDevice.isLittleEndian(devices.get(0)) != JOCLDevice.isLittleEndian(device_id) )
			return;

		// allow only a single cpu device
		if( (deviceType & (CL.CL_DEVICE_TYPE_CPU)) != 0 ) 
		{
			if( hasCPUDevice )
			{
				return;
			}
			else
			{
				hasCPUDevice = true;
			}
		}
		
		devices.add(device_id);
	}

	public AbstractList<cl_device_id> getDevices() {
		return devices;
	}
	
	public boolean equals( Object filter )
	{
		if( !(filter instanceof JOCLMultiDeviceFilter) )
			return false;
		
		JOCLMultiDeviceFilter singleFilter = (JOCLMultiDeviceFilter)filter;
		
		return (singleFilter.GPU == GPU);
	}
}
