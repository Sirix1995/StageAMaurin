package de.grogra.gpuflux.utils;

import java.util.concurrent.Semaphore;

public class TaskMonitor extends Semaphore
{
	public TaskMonitor() {
		super(1);
	}
	
	public void startTask()
	{
		reducePermits(1);
	}
	
	public void finishTask()
	{
		release(1);
	}
	
	public void awaitTasks()
	{
		while(true)
		{
			try {
				acquire(1);
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
