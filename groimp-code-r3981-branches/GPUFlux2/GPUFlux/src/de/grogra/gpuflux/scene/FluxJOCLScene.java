package de.grogra.gpuflux.scene;

import static org.jocl.CL.CL_MEM_COPY_HOST_PTR;
import static org.jocl.CL.CL_MEM_READ_ONLY;

import java.io.IOException;

import de.grogra.gpuflux.jocl.compute.ComputeByteBuffer;
import de.grogra.gpuflux.jocl.compute.ComputeContext;
import de.grogra.gpuflux.jocl.compute.Device;
import de.grogra.gpuflux.jocl.compute.Kernel;
import de.grogra.gpuflux.jocl.compute.SharedBuffer;

public class FluxJOCLScene {
	private FluxSceneSerializer serializer;
	private ComputeContext computeContext;
	
	private SharedBuffer primBuffer;
	private SharedBuffer offsetBuffer;
	private SharedBuffer bvhBuffer;
	private SharedBuffer shaderBuffer;
	private SharedBuffer channelBuffer;
	private SharedBuffer sensorBuffer;
	private SharedBuffer sensorBVHBuffer;
	
	private SharedBuffer lightPowerBuffer;
	private SharedBuffer lightBuffer;
	private SharedBuffer lightOffsetBuffer;
	private SharedBuffer cameraBuffer;
	private SharedBuffer detectorBuffer;
	
	private ComputeByteBuffer boundsBuffer;
	private ComputeByteBuffer computeByteBuffer; 
		
	public FluxJOCLScene(FluxSceneSerializer serializer , ComputeContext computeContext)
	{
		this.serializer = serializer ;
		this.computeContext = computeContext;
		
		computeByteBuffer = computeContext.createByteBuffer();
	}

	public void setupOCLBounds() throws IOException
	{
		boundsBuffer = computeContext.createByteBuffer();
		serializer.serializeBoundingSphere(boundsBuffer);
	}
	
	public void setupOCLDetectors() throws IOException
	{
		computeByteBuffer.reset();
		serializer.serializeDetectors(computeByteBuffer);
		detectorBuffer = computeContext.createSharedBuffer(computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR);
	}
	
	public void setupOCLCamera(int width , int height) throws IOException
	{
		computeByteBuffer.reset();
		serializer.serializeCamera(computeByteBuffer, width, height);
		cameraBuffer = computeContext.createSharedBuffer(computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR);
	}
	
	public void setupOCLSensors() throws IOException
	{
		computeByteBuffer.reset();
		serializer.serializeSensors(computeByteBuffer);
		sensorBuffer = computeContext.createSharedBuffer( computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR);
		
		computeByteBuffer.reset();
		serializer.serializeSensorBVH(computeByteBuffer, false);
		sensorBVHBuffer = computeContext.createSharedBuffer( computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR );
	}
	
	public void setupOCLScene(boolean useBIH) throws IOException
	{
		computeByteBuffer.reset();
		serializer.serializeChannels(computeByteBuffer);
		channelBuffer = computeContext.createSharedBuffer( computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR );
		
		computeByteBuffer.reset();
		serializer.serializeShaders(computeByteBuffer);
		shaderBuffer = computeContext.createSharedBuffer( computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR );
		
		computeByteBuffer.reset();
		serializer.serializePrimitives(computeByteBuffer);
		primBuffer = computeContext.createSharedBuffer( computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR );
		
		computeByteBuffer.reset();
		serializer.serializePrimitiveOffsets(computeByteBuffer);
		offsetBuffer = computeContext.createSharedBuffer( computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR );
		
		computeByteBuffer.reset();
		serializer.serializeBVH(computeByteBuffer,useBIH);
		bvhBuffer = computeContext.createSharedBuffer( computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR );
	}
	
	public void setupOCLLights() throws IOException
	{
		computeByteBuffer.reset();
		serializer.serializeLights(computeByteBuffer);
		lightBuffer = computeContext.createSharedBuffer( computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR );
		
		computeByteBuffer.reset();
		serializer.serializeLightOffsets(computeByteBuffer);
		lightOffsetBuffer = computeContext.createSharedBuffer( computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR );
		
		computeByteBuffer.reset();
		serializer.serializeCumulativeLightPowerDistribution(computeByteBuffer);
		lightPowerBuffer = computeContext.createSharedBuffer( computeByteBuffer , CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR );
	}
	
	public void setKernelArgDetectors( Device device, Kernel kernel, int offset )
	{
		// set detectors
		device.setKernelArgMemBuffer(kernel, offset, detectorBuffer);
	}
	
	public void setKernelArgCamera( Device device, Kernel kernel, int offset )
	{
		// set camera
		device.setKernelArgMemBuffer(kernel, offset, cameraBuffer);
	}
	
	public void setKernelArgBounds( Device device, Kernel kernel, int offset )
	{
		// set bounds
		device.setKernelArgBuffer(kernel, offset, boundsBuffer.getBuffer(device.isLittleEndian()));
	}
	
	public void setKernelArgSensors( Device device, Kernel kernel, int offset )
	{
		device.setKernelArgInt(kernel, offset+0, serializer.getScene().getSensorCount() );
		device.setKernelArgMemBuffer(kernel, offset+1, sensorBuffer);
		device.setKernelArgInt(kernel, offset+2, serializer.getScene().getSensorBVHRoot() );
		device.setKernelArgMemBuffer(kernel, offset+3, sensorBVHBuffer);
	}
	
	public void setKernelArgScene( Device device, Kernel kernel, int offset )
	{
		// set scene
		device.setKernelArgInt(kernel, offset+0, serializer.getScene().getPrimitiveCount());
		device.setKernelArgInt(kernel, offset+1, serializer.getScene().getInfPrimitiveCount());
		device.setKernelArgMemBuffer(kernel, offset+2, primBuffer);
		device.setKernelArgMemBuffer(kernel, offset+3, offsetBuffer);
		device.setKernelArgInt(kernel, offset+4, serializer.getScene().getBVHRoot() );
		device.setKernelArgMemBuffer(kernel, offset+5, bvhBuffer);
		device.setKernelArgMemBuffer(kernel, offset+6, shaderBuffer);
		device.setKernelArgMemBuffer(kernel, offset+7, channelBuffer);
		device.setKernelArgInt(kernel, offset+8, serializer.getScene().getLightCount());
		device.setKernelArgMemBuffer(kernel, offset+9, lightBuffer);
		device.setKernelArgMemBuffer(kernel, offset+10, lightOffsetBuffer);
		device.setKernelArgMemBuffer(kernel, offset+11, lightPowerBuffer);
		device.setKernelArgInt(kernel, offset+12, serializer.getSkyOffset());
	}
}
