/**
 * 
 */
package de.grogra.ext.sunshine.acceleration;

import java.nio.ByteBuffer;
import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.kernel.Kernel;

/**
 * @author mankmil
 *
 */
public interface SunshineAccelerator
{
	public Kernel getKernel(GLAutoDrawable drawable, int[] data, int size,
			int objectCount, int steps);

	public abstract ByteBuffer getTreeData();

	public abstract int getSize();
	
	public int getDepth();
	
	public abstract int getMaxVolumeCount();
	
	public abstract String getShaderCode();

}