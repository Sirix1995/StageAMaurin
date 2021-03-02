/**
 * 
 */
package de.grogra.ext.sunshine.kernel.bidirPT;

import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.SunshineSceneVisitor;
import de.grogra.ext.sunshine.kernel.InitKernel;

/**
 * @author mankmil
 *
 */
public class IsVisibleInitKernel extends InitKernel
{

	/**
	 * @param name
	 * @param drawable
	 * @param size
	 */
	public IsVisibleInitKernel(String name, GLAutoDrawable drawable, int size)
	{
		super(name, drawable, size);
	}
	
	@Override
	public void loadSource(GLAutoDrawable drawable,
			SunshineSceneVisitor monitor, String intermediates)
	{
		setSource(drawable, new String[]
        {
			loadSource(EXTENSIONS),
			"void main()\n",
			"{\n",
			"	gl_FragData[0] = vec4(1.0, 1.0, 0.0, 1.0);\n",
			"}"
        });
	}
}
