package de.grogra.gpuflux.tracer;

import static org.jocl.CL.CL_MEM_READ_WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.AbstractList;

import org.jocl.Sizeof;
import org.jocl.cl_event;

import de.grogra.gpuflux.FluxSettings;
import de.grogra.gpuflux.GPUFluxInit;
import de.grogra.gpuflux.jocl.JOCLBuffer;
import de.grogra.gpuflux.jocl.JOCLEvent;
import de.grogra.gpuflux.jocl.compute.Buffer;
import de.grogra.gpuflux.jocl.compute.ComputeContext;
import de.grogra.gpuflux.jocl.compute.Device;
import de.grogra.gpuflux.jocl.compute.Kernel;
import de.grogra.gpuflux.scene.FluxScene;
import de.grogra.gpuflux.scene.filter.NoneFilter;
import de.grogra.imp3d.View3D;
import de.grogra.pf.io.ProgressMonitor;

public class FluxLightTracer extends FluxTracer { 
	
	boolean terminate;
	
	class DeviceMonitor
	{
		Buffer image;
		cl_event event = new cl_event();
		int smlprun;
		long totalsml;
		double total_time;
		public Device device;
	};
	
	@Override
	public void trace() throws IOException {
		StringBuffer stats = new StringBuffer("<html><pre>");
		stats.append( "<B>GPUFlux Light Tracer</B>\n\n" );
		
		long sceneConstructionTime = 0, sceneSerializationTime = 0;
		long totalRenderTime = 0;
		long totalsamples = 0;
		DeviceMonitor deviceMonitors [] = null;
		
		try
		{
			long startTime, time;
			
			setProgress ("Build scene", ProgressMonitor.INDETERMINATE_PROGRESS);
			sceneConstructionTime = System.currentTimeMillis ();
			
			// constuct flux scene
			FluxScene scene = new FluxScene();
			
			// build scene from scene graph
			scene.buildSceneFromGraph( view.getGraph(), (View3D)view, new NoneFilter(), false, this, false, FluxSettings.getModelFlatness() );
			
			sceneConstructionTime = System.currentTimeMillis () - sceneConstructionTime;
			
			stats.append( scene.getLog() );
			stats.append( scene.getSceneStats() );
						
			setProgress ("Init compute context", ProgressMonitor.INDETERMINATE_PROGRESS);
			
			// init compute context
			ComputeContext computeContext = GPUFluxInit.initComputeContext(true,null);
			stats.append( computeContext.aquireLog() );
			
			if( !computeContext.valid() )
				return;
			
			// get compute devices
			AbstractList<Device> deviceList = computeContext.getDeviceList();
			
			setProgress ("Serialize scene", ProgressMonitor.INDETERMINATE_PROGRESS);
			sceneSerializationTime = System.currentTimeMillis ();
			
			// construct flux scene serializer
			de.grogra.gpuflux.scene.FluxSceneSerializer serializer = new de.grogra.gpuflux.scene.FluxSceneSerializer();
			
			// construct OCL flux scene
			de.grogra.gpuflux.scene.FluxJOCLScene joclScene = new de.grogra.gpuflux.scene.FluxJOCLScene( serializer, computeContext ); 
			
			//  serialize flux scene
			serializer.serializeScene(scene);
			
			// setup OCL scene, camera and lights
			joclScene.setupOCLBounds();
			joclScene.setupOCLScene(useBih);
			joclScene.setupOCLCamera(width, height);
			joclScene.setupOCLLights();
			
			sceneSerializationTime = System.currentTimeMillis () - sceneSerializationTime;
			
			// load kernel
			setProgress ("Load kernel", ProgressMonitor.INDETERMINATE_PROGRESS);
			Kernel kernel = computeContext.createKernel("kernel/lt_kernel.cl", "computeImage", getKernelCompilationArguments(true,true));
			
			int depth = FluxSettings.getRenderDepth();
			int randomseed = FluxSettings.getRandomSeed();
			double preferredDuration = FluxSettings.getOCLPreferredDuration();
			float minPower = FluxSettings.getRenderMinPower();
			int initialSamples = FluxSettings.getOCLInitialSampleCount();
			int maximumSampleCount = FluxSettings.getOCLMaximumSampleCount();
			
			int maxsmpl = maximumSampleCount;
			int smlprun =  Math.min(initialSamples, width*height);
			
			deviceMonitors = new DeviceMonitor[deviceList.size()];
			for(int i = 0 ; i < deviceList.size(); i++ )
			{
				Device device = deviceList.get(i);
				DeviceMonitor monitor = new DeviceMonitor();
							
				monitor.smlprun = smlprun;
				monitor.totalsml = 0;
				monitor.device = device;
				monitor.total_time = 0;
								
				monitor.image = device.createBuffer(Sizeof.cl_float4*width*height, CL_MEM_READ_WRITE);
				monitor.image.clear();
							
				deviceMonitors[i] = monitor;
			}
			
			stats.append( computeContext.getLog() );
			
			stats.append("<B>Settings</B>\n");	
			stats.append(FluxSettings.getTracerLog());
			stats.append("        Image size:   " + width + " x " + height + "\n");
			stats.append("        Total pixels: " + width * height + "\n");
			stats.append("\n");
			
			// start termination dialog
			terminate = false;
			
			Thread t = new Thread()
			{
				@Override
				public void run() {
		             new MsgBox
		             (null , "Progressive rendering in progress...");
		        	 terminate = true;
		         }
			};
			t.start();
			
			stats.append("<B>Render Profile</B>\n");
			
			long smpl = 0;
			while( !terminate )
			{
				setProgress ("Execute kernels", ProgressMonitor.INDETERMINATE_PROGRESS);
				
				if( BATCH_LOGGING_ENABLED )
					stats.append( "<i><b>Itteration</b></i>\n" );
				
				startTime = System.currentTimeMillis ();
				
				// execute the kernel on each device
				for(DeviceMonitor monitor : deviceMonitors )
				{
					// set image
					monitor.device.setKernelArgMemBuffer(kernel, 1, monitor.image);
					monitor.device.setKernelArgInt(kernel, 2, monitor.smlprun);
					monitor.device.setKernelArgInt(kernel, 3, (int)smpl);
					
					// set scene
					joclScene.setKernelArgScene(monitor.device, kernel, 4);
			
					// set camera
					joclScene.setKernelArgCamera(monitor.device, kernel, 17);
					
					// set bounds
					joclScene.setKernelArgBounds(monitor.device, kernel, 18);
					
					// set seed
					monitor.device.setKernelArgInt(kernel, 19, randomseed);
					
					// set depth
					monitor.device.setKernelArgInt(kernel, 20, depth);
					
					// set minimum power
					monitor.device.setKernelArgFloat(kernel, 21, minPower);
					
					// execute kernel
					monitor.device.executeKernel(kernel, monitor.smlprun, monitor.event);
					
					// account for enqueued samples
					smpl += monitor.smlprun;
				}
						
				// wait until all devices are finished
				computeContext.finish();
				
				time = System.currentTimeMillis () - startTime;
				totalRenderTime += time;
				
				// balance the work between the devices
				for(DeviceMonitor monitor : deviceMonitors )
				{
					double passed_time = JOCLEvent.getEndTime(monitor.event) - JOCLEvent.getStartTime(monitor.event);
					double samplesPerSecond = 0;
					
					monitor.total_time += passed_time;
					
					if( passed_time > 0 )
						samplesPerSecond = monitor.smlprun / passed_time;
					
					if( BATCH_LOGGING_ENABLED )
					{
						stats.append( "<i>" );
						stats.append( "Sample batch\n" );
						stats.append( "    Device:      " + monitor.device.getName() + "\n");
						stats.append( "    Batch size:  " + monitor.smlprun + "\n" );
						stats.append( "    Render time: " + (passed_time * 1000) + "ms\n");
						stats.append( "    Performance: " + (samplesPerSecond / (1000*1000)) + " MSmpl\n");
						stats.append( "\n" );
						stats.append( "</i>" );
					}
					
					monitor.totalsml += monitor.smlprun;
					totalsamples += monitor.smlprun;
					int newSmlPerRun = Math.min( maxsmpl , (int) (samplesPerSecond * preferredDuration) );
					monitor.smlprun = (int)(BATCH_BALANCE_SMOOTH * newSmlPerRun + (1.0-BATCH_BALANCE_SMOOTH) * monitor.smlprun);
				}
					
				// display the intermediate image
				displayImage( deviceMonitors );
			}
			
			for(DeviceMonitor monitor : deviceMonitors )
			{
				stats.append( "Device: " + monitor.device.getName() + "\n" );
				stats.append( "\tTotal samples:     " + monitor.totalsml + "\n");
				stats.append( "\tSamples per batch: " + monitor.smlprun + "\n" );
				stats.append( "\tTotal trace time:  " + (int)(monitor.total_time * 1000) + " ms\n" );
				stats.append( "\tSamples per second: " + (monitor.totalsml / (1000.0*1000.0)) / monitor.total_time + " MSmpl/s\n" );
			}
		}
		finally
		{
			stats.append("\n<B>Profile Summary</B>\n");
			stats.append("    Construction time: " + sceneConstructionTime + " ms\n");
			stats.append("    Serialize time:    " + sceneSerializationTime + " ms\n");
			stats.append("    Render time:       " + totalRenderTime + " ms\n");
			if( totalRenderTime > 0 )
				stats.append("    Performance:       " + ((totalsamples / totalRenderTime) / 1000.0) + " MSmpl/s\n");
	        		    
	        if( deviceMonitors != null )
	        {
		        long total = 0;
				for(DeviceMonitor monitor : deviceMonitors )
					if( monitor != null )
						total += monitor.totalsml;
				
				if( total > 0 )
				{
					for(DeviceMonitor monitor : deviceMonitors )
						if( monitor != null )
							stats.append( "        " + ((monitor.totalsml * 100) / total) + "%: \t" + monitor.device.getName() + "\n" );
				}
	        }
	        
	        stats.append("    Device Memory:     " + (JOCLBuffer.getMemoryUsage() / 1024) + " KB\n");
	        
	        setProgress ("Done", ProgressMonitor.DONE_PROGRESS);
	        
			// display statistics
			view.getWorkbench().logGUIInfo ( stats.append ("</pre></html>").toString() );
		}
	}
		
	protected void displayImage(DeviceMonitor deviceMonitors [])
	{
		setProgress ("Display image", ProgressMonitor.INDETERMINATE_PROGRESS);
		
		byte imagedata[] = new byte[width*height*Sizeof.cl_float4];
		FloatBuffer imageBuffer = (ByteBuffer.wrap(imagedata)).asFloatBuffer();
		
		
		for(DeviceMonitor monitor : deviceMonitors )
		{
			// load the hdr image data
			byte image[] = new byte[width*height*Sizeof.cl_float4];
			monitor.image.readBuffer(image);
            
			ByteBuffer accData = ByteBuffer.wrap(image);
			accData.order(monitor.device.getByteOrder());
			
			FloatBuffer accBuffer = accData.asFloatBuffer();
			
			for( int i = 0 ; i < width*height*4; i++ )
				imageBuffer.put(i, imageBuffer.get(i) + accBuffer.get(i) );
		}
        
        displayImage( imageBuffer );
	}
}
