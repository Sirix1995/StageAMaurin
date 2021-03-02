/**
 * 
 */
package de.grogra.ext.sunshine.kernel.bidirPT;

import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.SunshineSceneVisitor;
import de.grogra.ext.sunshine.kernel.InitKernel;

/**
 * @author Thomas
 *
 */
public class CombineInitKernel extends InitKernel
{
	public CombineInitKernel(String name, GLAutoDrawable drawable, int size)
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
				"void main(void)\n",
				"{\n",
				"	gl_FragData[0] = vec4(0.0);\n",
				"	gl_FragData[1] = vec4(0.0);\n",
				"}\n"
        });
	}
}
