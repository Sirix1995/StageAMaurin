/**
 * 
 */
package de.grogra.ext.sunshine.kernel.bidirPT;

import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.kernel.Kernel;

/**
 * @author mankmil
 *
 */
abstract public class InterruptableKernel extends Kernel implements Interruptable 
{
	protected final static String STATE_TEXTURE = "stateTexture";
	
	protected static final String LOOPSTART 	= "loopStart";
	protected static final String LOOPSTOP		= "loopStop";
	protected static final String LASTCYCLE		= "lastCycle";
	protected static final String COMPONENT		= "component";
	protected static final String INTERRUPTERS	= "uniform int loopStart;\nuniform int loopStop;\n";

	private int loopStart;
	private int loopStop;
	private int loopSteps;
	private int objectCount;

	public InterruptableKernel(String name, GLAutoDrawable drawable, 
			int tileSize, int objects, int steps)
	{
		super(name, drawable, tileSize);
		
		objectCount = objects;
		loopSteps	= steps;
	} //constructor
		
	
	public void setLoopParameter(int count, int steps)
	{
		objectCount	= count;
		loopSteps	= steps;
	}
	
	public void reset()
	{
		loopStart 	= 0;
		loopStop 	= Math.min(loopSteps, objectCount);
	}
	
	public int getLoopStart()
	{
		return loopStart;
	}
	
	public int getLoopStop()
	{
		return Math.min(loopStop, objectCount + 1);
	}
	
	public boolean resume()
	{
		setUniform(LOOPSTART, loopStart);
		setUniform(LOOPSTOP, getLoopStop());
		
		if(loopStop < objectCount)
		{					
			loopStart = loopStop;
			loopStop += loopSteps;
			
			return false;
		} 
		else 
		{			
			reset();
			
			return true;
		}
	}
	
	public boolean resume(int component)
	{
		setUniform(COMPONENT, component);
		
		return resume();
	}

}
