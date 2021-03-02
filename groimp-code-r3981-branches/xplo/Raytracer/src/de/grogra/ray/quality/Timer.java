package de.grogra.ray.quality;

import java.lang.reflect.Method;

public class Timer {
	
	private static final Method timeMethod;
	static
	{
		Method m = null;
		try
		{
			m = System.class.getMethod ("nanoTime", new Class[0]);
		}
		catch (NoSuchMethodException e)
		{
			m = null;
		}
		timeMethod = m;
	}

	private boolean m_hasStarted = false;
	private long    m_startTime = 0;
	private long    m_lastMesurement = 0;
	
	public Timer() {
		super();
	}
	
	public static long nanoTime ()
	{
		if (timeMethod == null)
		{
			return System.currentTimeMillis () * 1000000;
		}
		try
		{
			return ((Long) timeMethod.invoke (null, (Object[]) null)).longValue ();
		}
		catch (Exception e)
		{
			throw new AssertionError (e);
		}
	}
	
	public void start() {
		m_hasStarted = true;
		m_startTime = nanoTime();
	}
	
	
	public void stop() {
		if (!m_hasStarted) { return; }
		
		long cur_time = nanoTime();
		m_hasStarted = false;
		m_lastMesurement += cur_time - m_startTime;
	}
	
	
	public void reset() {
		m_hasStarted = false;
		m_lastMesurement = 0;
	}
	
	
	public long getLastNanos() {
		return m_lastMesurement;
	}
	
	
	public long getLastMicros() {
		return Math.round(m_lastMesurement/1000.0);
	}
	
	
	public long getLastMillis() {
		return Math.round(m_lastMesurement/1000000.0);
	}
	
	
	

}
