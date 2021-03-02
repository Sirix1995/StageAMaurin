/**
 * 
 */
package de.grogra.ext.sunshine.kernel.bidirPT;

import javax.media.opengl.GLAutoDrawable;
import de.grogra.ext.sunshine.ObjectHandler;
import de.grogra.ext.sunshine.SunshineSceneVisitor;
import de.grogra.ext.sunshine.acceleration.SunshineAccelerator;

/**
 * @author Thomas
 *
 */
public class TraceEyeRayKernel extends InterruptableTraceRayKernel
{
	private String INFOS 	= "getEyeVertexInfos.frag";
	
	/**
	 * @param name
	 * @param drawable
	 * @param sceneTexture
	 * @param texTexture
	 * @param img
	 * @param tileSize
	 * @param objects
	 * @param steps
	 */
	public TraceEyeRayKernel(String name, GLAutoDrawable drawable,
			int[] sceneTexture, int[] texTexture, int[] treeData, ObjectHandler oh,
			int tileSize, int steps, SunshineAccelerator tree,	boolean accelerate)
	{
		super(name, drawable, sceneTexture, texTexture, treeData, oh, tileSize, 
				steps, tree);
		
		if(accelerate)
		{
//			MAIN = "bidirect_tree_main.frag";
//			MAIN = "bidirect_intrusiv.frag";
		}
	}
	
	@Override
	public void execute(GLAutoDrawable drawable, int px, int py, int i)
	{
		super.execute(drawable, px, py, i);
	}
	
	public void loadSource(GLAutoDrawable drawable,
			SunshineSceneVisitor monitor, String intermediates)
	{
		setSource(drawable, new String[] 
        {
				loadSource(EXTENSIONS),
				SAMPLER + "a0" 			+ ";\n",
				SAMPLER + "a1" 			+ ";\n",
				SAMPLER + "a2" 			+ ";\n",
				
				SAMPLER + PRE_VERTEX	+ ";\n",
				SAMPLER + PRE_ALPHA		+ ";\n",
				
				SAMPLER + STATE_TEXTURE	+ ";\n",
				SAMPLER + SCENE_TEXTURE	+ ";\n",
				SAMPLER + TEXATLAS_TEXTURE 	+ ";\n",
				
				"uniform int " + CURRENT_VERTEX 	+ ";\n",

				loadSource(RANDOM),
				intermediates,
				"int maxVolumeCount = " + tree.getMaxVolumeCount() + ";\n",
				"int treeDepth 		= " + tree.getDepth() + ";\n",
				INTERRUPTERS,
				loadSource(STRUCTS),
				loadSource(LIGHT_CALC),
				loadSource(TEXTURE_LOOKUP),
				loadSource(CALC_NORMALS),
				loadSource(INTERSECT_UTILS),
				loadSource(INTERSECTIONS),
				monitor.getPhong(),
				loadSource(RAYPROCESSOR),
				loadSource(INTERSECT_LOOP),
				loadSource(COMPUTE_BSDF),
				loadSource(PROB_UTILS),
				loadSource(INFOS),
				loadSource(INITIALISATION),
				loadSource(MAIN)
        });
	} //loadSource
	
	protected String loadSource(String s, String path)
	{
		return super.loadSource(s, path);
	}
}
