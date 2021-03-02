/**
 * 
 */
package de.grogra.ext.sunshine.kernel.acceleration;

import static javax.media.opengl.GL.GL_TEXTURE0;
import static javax.media.opengl.GL.GL_TEXTURE1;
import static javax.media.opengl.GL.GL_TEXTURE2;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.SunshineSceneVisitor;

/**
 * @author mankmil
 *
 */
public class OctreeInitKernel extends AccelerationKernel
{
	private static final String MAIN = "octreeInit.frag"; 

	
	/**
	 * @param name
	 * @param drawable
	 * @param size
	 */
	public OctreeInitKernel(String name, GLAutoDrawable drawable, int[] data, 
			int size)
	{
		super(name, drawable, size, 0, 0, data);
	}
	
	@Override	
	public void execute(GLAutoDrawable drawable, int px, int py, int sample)
	{
		GL gl = drawable.getGL();
		
		// get location for the uniform variables
		int a0Loc 	= getUniformLocation("a1", drawable);
		int a1Loc 	= getUniformLocation("a1", drawable);
		int treeLoc	= getUniformLocation(TREE_TEXTURE, drawable);

		useProgram(drawable);
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(texTarget, inputTextureA[0]);
			setUniformTex(a0Loc, 0, drawable);
		
			gl.glActiveTexture(GL_TEXTURE1);
			gl.glBindTexture(texTarget, inputTextureA[1]);
			setUniformTex(a1Loc, 1, drawable);
			
			gl.glActiveTexture(GL_TEXTURE2);
			gl.glBindTexture(texTarget, treeTexture[0]);
			setUniformTex(treeLoc, 2, drawable);
		
			drawQuad(drawable, px, py);
		stopProgram(drawable);
	}
	
	@Override
	public void loadSource(GLAutoDrawable drawable,
			SunshineSceneVisitor monitor, String intermediates)
	{
		setSource(drawable, new String[]
        {
			super.loadSource(EXTENSIONS),
			SAMPLER 	+ 	"a0;\n",
			SAMPLER 	+ 	"a1;\n",
			SAMPLER 	+ 	"sceneTexture;\n",

			intermediates,
			super.loadSource(STRUCTS),
			loadSource(TEXTURE_LOOKUP),
			loadSource(INTERSECT_UTILS),
			loadSource(INTERSECTION),
			"float random;",
			super.loadSource(INITIALISATION),
			loadSource(MAIN)
        });
	}
	
	protected String loadSource(String s)
	{
		return super.loadSource(s, PATH);
	}

}
