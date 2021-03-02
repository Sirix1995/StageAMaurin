package de.grogra.ray2.radiosity;

import de.grogra.ray2.Scene;
import de.grogra.ray2.tracing.PixelwiseRenderer;

/**
 * Reads all Radiosity Options from the registry and checks if values where changed.
 * @author Ralf Kopsch
 */
public class OptionReader {
	private static final String CUBEWIDTH = "radiosity/hemisize";
	private static final String HEMICUBEWIDE = "radiosity/hemicubeworldwide";
	private static final String SUBDIVTHRESHOLD = "radiosity/subdivthreshold";
	private static final String MAXSUBDIVDEPTH = "radiosity/maxsubdivdepth";
	
	private static int oldCubeWidth = -1;
	private static float oldHemiWorldWide = -1;
	private static float oldSubdivthreshold = -1;
	private static int oldMaxsubdivdepth = -1;
	private static String oldUniqueName = null;
	private static int oldStamp = -1;

	private int cubeWidth;
	private float hemiWorldWide;
	private float subdivthreshold;
	private int maxsubdivdepth;
	private int threadcount;
	
	/**
	 * This Constructor reads all Radiosity parameter from the registry.
	 * @param renderer the PixelwiseRenderer
	 */
	public OptionReader(PixelwiseRenderer renderer) {
		this.cubeWidth = renderer.getNumericOption(CUBEWIDTH, 100).intValue();
		assert this.cubeWidth > 0;
		this.hemiWorldWide = renderer.getNumericOption(HEMICUBEWIDE, 1.0f).floatValue();
		assert this.hemiWorldWide > 0;
		this.subdivthreshold = renderer.getNumericOption(SUBDIVTHRESHOLD, 5.0f).floatValue();
		assert this.subdivthreshold > 0;
		this.maxsubdivdepth = renderer.getNumericOption(MAXSUBDIVDEPTH, 7).intValue();
		assert this.maxsubdivdepth > 0;
		this.threadcount = renderer.getNumericOption(PixelwiseRenderer.THREAD_COUNT, 0).intValue();
		if (this.threadcount == 0) {
			this.threadcount = Runtime.getRuntime().availableProcessors();
		}
	}
	
	/**
	 * Returns true, if a new Hemicube must be created.
	 * @return true, if a new Hemicube must be created.
	 */
	public boolean isHemicubeCalcNeeded() {
		return (this.cubeWidth != OptionReader.oldCubeWidth); 
	}
	
	/**
	 * Returns true, if all Radiosity values must be calculated.
	 * @param scene the Scene.
	 * @return true, if all Radiosity values must be calculated.
	 */
	public boolean isRadiosityCalcNeeded(Scene scene) {
		return ((scene.getStamp()      != OptionReader.oldStamp) ||
				(scene.getUniqueName() != OptionReader.oldUniqueName) ||
				(this.subdivthreshold  != OptionReader.oldSubdivthreshold) ||
				(this.maxsubdivdepth   != OptionReader.oldMaxsubdivdepth) ||
				(this.hemiWorldWide    != OptionReader.oldHemiWorldWide) ||
				isHemicubeCalcNeeded());
	}

	/**
	 * Call this after the rendering to update the values.
	 * @param scene the scene object.
	 */
	public void calcFinished(Scene scene) {
		OptionReader.oldCubeWidth = this.cubeWidth;
		OptionReader.oldSubdivthreshold = this.subdivthreshold;
		OptionReader.oldMaxsubdivdepth = this.maxsubdivdepth;
		OptionReader.oldHemiWorldWide = this.hemiWorldWide;
		OptionReader.oldUniqueName = scene.getUniqueName();
		OptionReader.oldStamp = scene.getStamp();
	}
	
	/**
	 * Return the cube pixel size.
	 * @return Return the cube pixel size.
	 */
	public int getCubeWidth() {
		return cubeWidth;
	}

	/**
	 * Returns the cube size in world coordinates.
	 * @return Returns the cube size in world coordinates.
	 */
	public float getHemiWorldWide() {
		return hemiWorldWide;
	}

	/**
	 * Returns the max subdivision size.
	 * @return Returns the max subdivision size.
	 */
	public int getMaxsubdivdepth() {
		return maxsubdivdepth;
	}

	/**
	 * Returns the subdivision threshold.
	 * @return Returns the subdivision threshold.
	 */
	public float getSubdivthreshold() {
		return subdivthreshold;
	}
	
	/**
	 * Returns the thread count.
	 * @return Returns the thread count.
	 */
	public int getThreadCount() {
		return this.threadcount;
	}
}
