package de.grogra.gpuflux.jocl;

import java.util.AbstractList;
import java.util.ArrayList;

import org.jocl.CL;
import org.jocl.cl_device_id;

import de.grogra.gpuflux.FluxSettings;

public class JOCLSingleDeviceFilter implements JOCLDeviceFilter {

	private boolean GPU;
	private cl_device_id device_id;
	
	public JOCLSingleDeviceFilter()
	{
		this.GPU  = FluxSettings.getOCLGPU();
	}
	
	public void init() {
		device_id = null;
	}

	public void filter(cl_device_id device_id) {
		
		if( device_id == null )
			return;
		
		long new_type = JOCLDevice.getDeviceType(device_id);
						
		// filter gpus when disabled
		if( (new_type & (CL.CL_DEVICE_TYPE_GPU | CL.CL_DEVICE_TYPE_ACCELERATOR)) != 0 && GPU == false )
			return;
		
		// select at least one device
		if( this.device_id == null )
		{
			this.device_id = device_id;
			return;
		}
		
		long curr_type = JOCLDevice.getDeviceType(this.device_id);
		
		// gpus have precedence over cpus
		if( (new_type & (CL.CL_DEVICE_TYPE_GPU | CL.CL_DEVICE_TYPE_ACCELERATOR)) != 0 )
		{
			if( (curr_type & CL.CL_DEVICE_TYPE_CPU) != 0 )
			{
				this.device_id = device_id;
				return;
			}
			
			// default gpus have precedence over non-default gpus
			if( (new_type & CL.CL_DEVICE_TYPE_DEFAULT) != 0 )
			{
				this.device_id = device_id;
				return;
			}
		}
				
		return;
	}

	public AbstractList<cl_device_id> getDevices() {
		if( device_id == null )
			return null; 
			
		ArrayList<cl_device_id> deviceList = new ArrayList<cl_device_id>(1);
		deviceList.add(device_id); 
		return deviceList;
	}

	@Override 
	public boolean equals( Object filter )
	{
		if( !(filter instanceof JOCLSingleDeviceFilter) )
			return false;
		
		JOCLSingleDeviceFilter singleFilter = (JOCLSingleDeviceFilter)filter;
		
		return (singleFilter.GPU == GPU);
	}
	
}
