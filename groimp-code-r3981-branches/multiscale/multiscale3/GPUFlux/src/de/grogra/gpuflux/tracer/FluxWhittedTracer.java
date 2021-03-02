package de.grogra.gpuflux.tracer;

import static org.jocl.CL.CL_MEM_READ_WRITE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.AbstractList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;

import org.jocl.CL;
import org.jocl.Sizeof;
import org.jocl.cl_event;

import de.grogra.gpuflux.FluxSettings;
import de.grogra.gpuflux.GPUFlux;
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
import de.grogra.ray2.ProgressMonitor;

public class FluxWhittedTracer extends FluxTracer 
{
	private ConcurrentLinkedQueue<DeviceMonitor> openDeviceList;
	private Semaphore available;
	private volatile boolean finnished;
	
	class DeviceMonitor extends Thread
	{
		Semaphore resume = new Semaphore(0);
		Buffer image;
		cl_event event = new cl_event();
		public Device device;
		double passed_time;
		double samplesPerSecond;
		double total_time;
		int smlprun, totalsml;
		String log = "";
		
		public void run()
		{
			//System.out.println( "Start thread" );
			while( true )
			{
				try {
					resume.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if( FluxWhittedTracer.this.finnished )
					break;
				
				// wait for device execution event
				CL.clWaitForEvents(1, new cl_event[]{event});
				
				// compute performance
				passed_time = JOCLEvent.getEndTime(event) - JOCLEvent.getStartTime(event);
				samplesPerSecond = smlprun / passed_time;
				total_time += passed_time;
				
				if( BATCH_LOGGING_ENABLED )
				{
					log += "<i>";
					log += "Sample batch\n";
					log += "    Device:      " + device.getName() + "\n";
					log += "    Batch size: " + smlprun + "\n" ;
					log += "    Render time: " + (passed_time * 1000) + "ms\n";
					log += "    Performance: " + (samplesPerSecond / (1000*1000)) + " MSmpl\n";
					log += "\n</i>";
				}

				// balance work
				totalsml += smlprun;

				// add back to open list
				openDeviceList.add(this);
				
				// release the device 
				available.release();
			}
			//System.out.println( "Stop thread" );
		}
	};
	
	@Override
	public void trace() throws IOException
	{
		StringBuffer stats = new StringBuffer("<html><pre>");
		stats.append( "<B>GPUFlux Whitted Tracer</B>\n\n" );
	
		long sceneConstructionTime = 0, sceneSerializationTime = 0;
		long totalRenderTime = 0;
		long totalsamples = 0;
		
		DeviceMonitor deviceMonitors [] = null;
		
		try
		{
			long time;
			
			setProgress ("Build scene", ProgressMonitor.INDETERMINATE_PROGRESS);
			sceneConstructionTime = System.currentTimeMillis ();
			
			// constuct flux scene
			FluxScene scene = new FluxScene();
			
			// build scene from scene graph
			scene.buildSceneFromGraph( view.getGraph(), (View3D)view, new NoneFilter(), false, this, true, FluxSettings.getModelFlatness() );
			
			sceneConstructionTime = System.currentTimeMillis () - sceneConstructionTime;
			
			stats.append( scene.getLog() );
			stats.append( scene.getSceneStats() );
						
			setProgress ("Init compute context", ProgressMonitor.INDETERMINATE_PROGRESS);
			
			// init compute context
			ComputeContext computeContext = GPUFluxInit.initComputeContext(true,null);
			stats.append( computeContext.aquireLog() );
			if( !computeContext.valid() )
				return;
			
			// get primary compute devices
			AbstractList<Device> deviceList = computeContext.getDeviceList();
			//Device device = computeContext.getPrimaryDevice();
			
			setProgress ("Serialize scene", ProgressMonitor.INDETERMINATE_PROGRESS);
			sceneSerializationTime = System.currentTimeMillis ();
			
			// construct flux scene serializer
			de.grogra.gpuflux.scene.FluxSceneSerializer serializer = new de.grogra.gpuflux.scene.FluxSceneSerializer();
			
			// construct OCL flux scene
			de.grogra.gpuflux.scene.FluxJOCLScene joclScene = new de.grogra.gpuflux.scene.FluxJOCLScene( serializer, computeContext ); 
			
			//  serialize flux scene
			serializer.serializeScene(scene);
			
			// setup OCL scene, camera and lights
			joclScene.setupOCLScene(useBih);
			joclScene.setupOCLCamera(width, height);
			joclScene.setupOCLLights();
			
			sceneSerializationTime = System.currentTimeMillis () - sceneSerializationTime;
			
			// load kernel
			setProgress ("Load kernel", ProgressMonitor.INDETERMINATE_PROGRESS);
			Kernel kernel = computeContext.createKernel("kernel/whitted_kernel.cl", "computeImage", getKernelCompilationArguments(false,false));
			
			stats.append( computeContext.getLog() );
			
			stats.append("<B>Settings</B>\n");	
			stats.append(FluxSettings.getTracerLog());
			stats.append("        Image size:   " + width + " x " + height + "\n");
			stats.append("        Total pixels: " + width * height + "\n");
			stats.append("\n");
						
			// get render settings
			int depth = FluxSettings.getRenderDepth();
			double preferredDuration = FluxSettings.getOCLPreferredDuration();
			float minPower = FluxSettings.getRenderMinPower();
			int initialSampleCount = FluxSettings.getOCLInitialSampleCount();
			
			int initialSamples = initialSampleCount / scene.getSampleCount();
			int smlprun = Math.min(initialSamples, width*height);
			int total_pixels = width*height*1;
			int pixel = 0;
			
			deviceMonitors = new DeviceMonitor[deviceList.size()];
			openDeviceList = new ConcurrentLinkedQueue<DeviceMonitor>();
			available = new Semaphore(deviceList.size());
						
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
				
				openDeviceList.add(monitor);
				
				// start the thread
				monitor.start();
			}
			
			stats.append("<B>Render Profile</B>\n");	
			
			long startDisplayTime = System.currentTimeMillis ();
			long startRenderTime = startDisplayTime;
			
			// sample light paths
			int smpl = 0, sampleCount = total_pixels;
			finnished = false;
			while( smpl < sampleCount )
			{
				// set progress
				setProgress ("Execute", (float)smpl / (float)sampleCount);
				
				try {
					available.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
				
				DeviceMonitor monitor = openDeviceList.remove();
				
				int npixels = Math.min( monitor.smlprun, sampleCount - smpl);
				// set last sample count
				monitor.smlprun = npixels; 
				
				// set image
				monitor.device.setKernelArgMemBuffer(kernel, 1, monitor.image);
				monitor.device.setKernelArgInt(kernel, 2, width);
				monitor.device.setKernelArgInt(kernel, 3, height);
				
				monitor.device.setKernelArgInt(kernel, 4, npixels);
				monitor.device.setKernelArgInt(kernel, 5, pixel);
				
				// set scene
				joclScene.setKernelArgScene(monitor.device, kernel, 6);
		
				// set camera
				joclScene.setKernelArgCamera(monitor.device, kernel, 19);
				
				// set depth
				monitor.device.setKernelArgInt(kernel, 20, depth);
				
				// set minimum power
				monitor.device.setKernelArgFloat(kernel, 21, minPower);
						
				// execute kernel
				monitor.device.executeKernel(kernel , npixels, monitor.event);
					
				// resume monitor thread
				monitor.resume.release();
				
				// account for enqueued samples
				smpl += npixels;
				pixel += npixels;
				totalsamples += npixels;
				
				long timeSinceDisplay = System.currentTimeMillis () - startDisplayTime;
				if( timeSinceDisplay / 1000.0 > preferredDuration )
				{
					displayImage( deviceMonitors );
					
					startDisplayTime = System.currentTimeMillis ();
				}
			}
			
			// wait until all devices are finished
			computeContext.finish();
			
			// rendering has finished, all threads must stop 
			finnished = true;
			
			// release all monitors
			for( DeviceMonitor monitor : deviceMonitors ) {
				monitor.resume.release();
			}
			
			time = System.currentTimeMillis () - startRenderTime;
			totalRenderTime += time;
			
			// display the intermediate image
			displayImage( deviceMonitors );
			
			stats.append( "\n" );
			
			for(DeviceMonitor monitor : deviceMonitors )
			{
				stats.append( "Device: " + monitor.device.getName() + "\n" );
				stats.append( "\tTotal samples:     " + monitor.totalsml + "\n");
				stats.append( "\tSamples per batch: " + monitor.smlprun + "\n" );
				stats.append( "\tTotal trace time:  " + (int)(monitor.total_time * 1000) + " ms\n" );
				stats.append( "\tSamples per second: " + (monitor.totalsml / (1000.0*1000.0)) / monitor.total_time + " MSmpl/s\n" );
			}
			
			stats.append( "\n" );
			
			for( DeviceMonitor monitor : deviceMonitors ) {
				stats.append( monitor.log + "\n" );
			}
		}
		finally
		{
			stats.append("\n<B>Profile Summary</B>\n");
			stats.append("    Construction time: " + sceneConstructionTime + " ms\n");
			stats.append("    Serialize time:    " + sceneSerializationTime + " ms\n");
			stats.append("    Render time:       " + totalRenderTime + " ms\n");
			
			if( deviceMonitors != null )
			{
		        int total = 0;
				for(DeviceMonitor monitor : deviceMonitors )
					total += monitor.totalsml;
				
				if( total > 0 )
				{
					for( DeviceMonitor monitor : deviceMonitors ) {
						stats.append( "        " + ((monitor.totalsml * 100) / total) + "%: \t" + monitor.device.getName() + "\n" );
					}
				}
			}
			
	        stats.append("    Device Memory:     " + (JOCLBuffer.getMemoryUsage() / 1024) + " KB\n");
		    	        
	        setProgress ("Done", ProgressMonitor.DONE_PROGRESS);
	        
			// display statistics
			view.getWorkbench().logGUIInfo ( stats.append ("</pre></html>").toString() );
			
			//  release data 
			available = null;
			openDeviceList = null;
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

