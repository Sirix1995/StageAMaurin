package de.grogra.gpuflux.tracer;

import static org.jocl.CL.CL_MEM_READ_WRITE;

import java.io.IOException;
import java.util.Random;

import org.jocl.Sizeof;

import de.grogra.gpuflux.FluxSettings;
import de.grogra.gpuflux.GPUFlux;
import de.grogra.gpuflux.GPUFluxInit;
import de.grogra.gpuflux.jocl.JOCLBuffer;
import de.grogra.gpuflux.jocl.compute.Buffer;
import de.grogra.gpuflux.jocl.compute.ComputeContext;
import de.grogra.gpuflux.jocl.compute.Device;
import de.grogra.gpuflux.jocl.compute.Kernel;
import de.grogra.gpuflux.scene.FluxScene;
import de.grogra.gpuflux.scene.filter.NoneFilter;
import de.grogra.imp3d.View3D;
import de.grogra.pf.io.ProgressMonitor;

public class FluxPathTracer extends FluxTracer {
		
	boolean terminate;  
	
	@Override
	public void trace() throws IOException {
		StringBuffer stats = new StringBuffer("<html><pre>");
		stats.append( "<B>GPUFlux path Tracer</B>\n\n" );
		
		long sceneConstructionTime = 0, sceneSerializationTime = 0;
		long totalRenderTime = 0;
		long totalsamples = 0;
		
		try
		{
			long startTime, time;
			
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
			sceneSerializationTime = System.currentTimeMillis ();
			
			// init compute context
			ComputeContext computeContext = GPUFluxInit.initComputeContext(false,null);
			stats.append( computeContext.aquireLog() );
			
			if( !computeContext.valid() )
				return;
			
			// get primary compute device
			Device device = computeContext.getPrimaryDevice();
			stats.append("Primary Device: \n" + device + "\n");
			
			setProgress ("Serialize scene", ProgressMonitor.INDETERMINATE_PROGRESS);
			
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
			Kernel kernel = computeContext.createKernel("kernel/pt_kernel.cl", "computeImage", getKernelCompilationArguments(true,false));
			
			// allocate image
			Buffer imageBuffer = device.createBuffer(Sizeof.cl_float4*width*height, CL_MEM_READ_WRITE);
			imageBuffer.clear();
			
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
				public void run() {
		             new MsgBox
		             (null , "Progressive rendering in progress...");
		        	 terminate = true;
		         }
			};
			t.start();
			
			Random rnd = new Random(0);
	
			int depth = FluxSettings.getRenderDepth();
			double preferredDuration = FluxSettings.getOCLPreferredDuration();
			float minPower = FluxSettings.getRenderMinPower();
			int initialSampleCount = FluxSettings.getOCLInitialSampleCount();
			int maximumSampleCount = FluxSettings.getOCLMaximumSampleCount();
			
			int initialSamples = initialSampleCount / scene.getSampleCount();
			
	        long startDisplayTime = System.currentTimeMillis ();
	        
			int maxsmpl = maximumSampleCount  / scene.getSampleCount();
			int smlprun = Math.min(initialSamples, width*height);
			
			stats.append("<B>Render Profile</B>\n");
			
			while( !terminate )
			{
				int runsPerScreen = (int)Math.ceil((double)(width*height) / (double)smlprun);
	
				if( BATCH_LOGGING_ENABLED )
				{
					stats.append("\n<i>Iteration</i>\n");
					stats.append("Samples per execution: " + smlprun + "\n\n");
				}
				
				double minSamplesPerSecond = Double.MAX_VALUE;
				
				int pixelsPerRun = (int)Math.ceil((double)(width*height) / (double)runsPerScreen);
				int pixel = 0;
				
				for( int i = 0 ; i < runsPerScreen ; i++ )
				{
					setProgress ("Execute kernel", ProgressMonitor.INDETERMINATE_PROGRESS);
					
					int samples = Math.min(pixelsPerRun, width*height - pixel);
					
					startTime = System.currentTimeMillis ();
				
					// set image
					device.setKernelArgMemBuffer(kernel, 1, imageBuffer);
					device.setKernelArgInt(kernel,2, width);
					device.setKernelArgInt(kernel,3, height);
					
					// set pass
					device.setKernelArgInt(kernel,4, samples);
					device.setKernelArgInt(kernel,5, pixel);
							
					// set scene
					joclScene.setKernelArgScene(device, kernel, 6);
			
					// set camera
					joclScene.setKernelArgCamera(device, kernel, 19);
					
					// set seed
					device.setKernelArgInt(kernel,20, rnd.nextInt());
					
					// set depth
					device.setKernelArgInt(kernel,21, depth);
					
					// set minimum power
					device.setKernelArgFloat(kernel,22, minPower);
					
					device.finish();
					startTime = System.currentTimeMillis ();
					
					// execute kernel
					device.executeKernel(kernel , samples);
					
					pixel += samples;
					totalsamples += samples;
					
					device.finish();
					time = System.currentTimeMillis () - startTime;
					totalRenderTime += time;
					
					if( BATCH_LOGGING_ENABLED )
					{
						stats.append("<i>Sample batch</i>\n");
						stats.append("    Batch size:  " + samples + " samples\n");
						stats.append("    Render time: " + time + " ms\n");
						if( time > 0 )
							stats.append("    Performance: " + ((samples / time) / 1000.0) + " MSmpl\n");
					}
		
					double samplesPerSecond = 1000.0 * (double)(samples) / (double)Math.max(time, 1);
					
					minSamplesPerSecond = Math.min( samplesPerSecond, minSamplesPerSecond);
					long timeSinceDisplay = System.currentTimeMillis () - startDisplayTime;
					if( timeSinceDisplay / 1000.0 > preferredDuration )
					{
						displayImage( imageBuffer, device.getByteOrder() );
				        
				        startDisplayTime = System.currentTimeMillis ();
					}
				}
				
				// aim for 500ms per iteration
				int newSmlPerRun = Math.min( maxsmpl , (int) (minSamplesPerSecond * preferredDuration) );
				smlprun = (int) (BATCH_BALANCE_SMOOTH * newSmlPerRun + (1.0-BATCH_BALANCE_SMOOTH) * smlprun);
			}
			
			stats.append( "Device: " + device.getName() + "\n" );
			stats.append( "\tTotal samples:     " + totalsamples + "\n");
			stats.append( "\tSamples per batch: " + smlprun + "\n" );
			stats.append( "\tTotal trace time:  " + (int)(totalRenderTime * 1000) + " ms\n" );
			stats.append( "\tSamples per second: " + (totalsamples / (1000.0*1000.0)) / totalRenderTime + " MSmpl/s\n" );
			
			displayImage( imageBuffer, device.getByteOrder() );
			
		}
		finally
		{
			stats.append("\n<B>Profile Summary</B>\n");
			stats.append("    Construction time: " + sceneConstructionTime + " ms\n");
			stats.append("    Serialize time:    " + sceneSerializationTime + " ms\n");
			stats.append("    Render time:       " + totalRenderTime + " ms\n");
			if( totalRenderTime > 0 )
				stats.append("    Performance:       " + ((totalsamples / totalRenderTime) / 1000.0) + " MSmpl/s\n");
	        stats.append("    Device Memory:     " + (JOCLBuffer.getMemoryUsage() / 1024) + " KB\n");
		    
	        setProgress ("Done", ProgressMonitor.DONE_PROGRESS);
	        
			// display statistics
			view.getWorkbench().logGUIInfo ( stats.append ("</pre></html>").toString() );
		}
	}
}
