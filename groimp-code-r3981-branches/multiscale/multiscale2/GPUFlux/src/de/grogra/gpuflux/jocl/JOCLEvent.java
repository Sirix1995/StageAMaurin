package de.grogra.gpuflux.jocl;

import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_event;

public class JOCLEvent {
	
	public static long getEventTime(cl_event event, int param)
	{
		long time[] = new long[1];
		CL.clGetEventProfilingInfo(
            event, param, 
            Sizeof.cl_ulong, Pointer.to(time), null);
		return time[0];
	}
	
	public static double getQueuedTime(cl_event event)
	{
		return getEventTime(event, CL.CL_PROFILING_COMMAND_QUEUED) / 1000000000.0;
	}
	public static double getSubmitTime(cl_event event)
	{
		return getEventTime(event, CL.CL_PROFILING_COMMAND_SUBMIT) / 1000000000.0;
	}
	public static double getStartTime(cl_event event)
	{
		return getEventTime(event, CL.CL_PROFILING_COMMAND_START) / 1000000000.0;
	}
	public static double getEndTime(cl_event event)
	{
		return getEventTime(event, CL.CL_PROFILING_COMMAND_END) / 1000000000.0;
	}

	
}
