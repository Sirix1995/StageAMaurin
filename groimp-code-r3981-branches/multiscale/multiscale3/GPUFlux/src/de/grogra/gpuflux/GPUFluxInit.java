package de.grogra.gpuflux;

import org.jocl.cl_device_id;

import de.grogra.gpuflux.jocl.JOCLDevice;
import de.grogra.gpuflux.jocl.JOCLDeviceFilter;
import de.grogra.gpuflux.jocl.JOCLMultiDeviceFilter;
import de.grogra.gpuflux.jocl.JOCLSingleDeviceFilter;
import de.grogra.gpuflux.jocl.compute.ComputeContext;

public class GPUFluxInit {
	static ComputeContext computeContext = null;
	
	public static ComputeContext initComputeContext( boolean multi, final String [] extensions )
	{
		boolean precompile = FluxSettings.getOCLPrecompile();
		multi &= FluxSettings.getOCLMultiGPU();
		
		JOCLDeviceFilter filter = null;
		
		// filter out all big-endian devices
		if(multi)
		{
			filter = new JOCLMultiDeviceFilter()
			{
				public void filter(cl_device_id device_id)
				{
					//if( JOCLDevice.isLittleEndian(device_id) )
					if( extensions == null || JOCLDevice.supportsExtensions(device_id, extensions) )
						super.filter(device_id);
				}
			};
		}
		else
		{
			filter = new JOCLSingleDeviceFilter()
			{
				public void filter(cl_device_id device_id)
				{
					//if( JOCLDevice.isLittleEndian(device_id) )
					if( extensions == null || JOCLDevice.supportsExtensions(device_id, extensions) )
						super.filter(device_id);
				}
			};
		}
				
		if( computeContext == null || precompile == false || !computeContext.getFilter().equals(filter) )
		{
			computeContext = new ComputeContext(filter);
		}
		
		return computeContext;
	}
}
