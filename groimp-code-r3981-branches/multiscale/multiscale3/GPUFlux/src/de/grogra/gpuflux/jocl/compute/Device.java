package de.grogra.gpuflux.jocl.compute;

import java.nio.ByteOrder;

import org.jocl.Pointer;
import org.jocl.cl_event;

import de.grogra.gpuflux.jocl.JOCLBuffer;
import de.grogra.gpuflux.jocl.JOCLDevice;
import de.grogra.gpuflux.jocl.JOCLKernel;
import de.grogra.gpuflux.utils.ByteArray;

public class Device {

	private ComputeContext context;
	private int contextID;
	private JOCLDevice device;
	
	protected Device(int contextID, JOCLDevice device, ComputeContext context) {
		this.contextID = contextID;
		this.device = device;
		this.context = context;
	}

	public Buffer createBuffer( int size, long flags )
	{
		return new Buffer( context.getContext(contextID).createBuffer( size , flags ) , this );
	}

	public ComputeContext getContext()
	{
		return context;
	}
	
	private JOCLKernel getJOCLKernel( Kernel kernel )
	{
		assert kernel.getContext() == context;
		return kernel.getKernel( contextID );
	}
	
	private JOCLBuffer getJOCLBuffer( Buffer buffer )
	{
		assert buffer.getDevice() == this;
		return buffer.getBuffer();
	}
	
	private JOCLBuffer getJOCLBuffer(SharedBuffer buf) {
		assert buf.getContext() == context;
		return buf.getBuffer( contextID, device.isLittleEndian() );
	}
	
	public void setKernelArg( Kernel kernel, int arg_idx , int arg_size , Pointer p )
	{
		getJOCLKernel(kernel).setKernelArg(arg_idx, arg_size, p);
	}
	
	public void setKernelArgInt( Kernel kernel, int arg_idx , int i )
	{
		getJOCLKernel(kernel).setKernelArgInt(arg_idx, i);
	}
	
	public void setKernelArgFloat( Kernel kernel, int arg_idx , float f )
	{
		getJOCLKernel(kernel).setKernelArgFloat(arg_idx, f);
	}
	
	public void setKernelArgMemBuffer( Kernel kernel, int arg_idx , Buffer buf )
	{
		getJOCLKernel(kernel).setKernelArgMemBuffer(arg_idx, getJOCLBuffer(buf) );
	}
	
	public void setKernelArgMemBuffer( Kernel kernel, int arg_idx , SharedBuffer buf )
	{
		getJOCLKernel(kernel).setKernelArgMemBuffer(arg_idx, getJOCLBuffer(buf) );
	}

	public void setKernelArgBuffer( Kernel kernel, int arg_idx, ByteArray buffer) {
		getJOCLKernel(kernel).setKernelArgBuffer(arg_idx, buffer);
	}
	
	public void executeKernel( Kernel kernel, int stream_size )
	{
		device.executeKernel( getJOCLKernel( kernel ), stream_size );
	}
	
	public void finish() {
		device.finish();
	}
	
	protected int getContextID() {
		return contextID;
	}
	
	protected JOCLDevice getDevice() {
		return device;
	}

	public String getName() {
		return device.getName();
	}
	
	public String toString() {
		return device.toString();
	}

	public void executeKernel(Kernel kernel, int stream_size, cl_event event) {
		device.executeKernel( getJOCLKernel( kernel ), stream_size, event );
	}
	
	public ComputeByteBuffer createByteBuffer()
	{
		boolean littleEndian = device.isLittleEndian();
		return new ComputeByteBuffer(littleEndian , !littleEndian);
	}

	public boolean isLittleEndian() {
		return device.isLittleEndian();
	}

	public ByteOrder getByteOrder() {
		return device.getByteOrder();
	}
}
