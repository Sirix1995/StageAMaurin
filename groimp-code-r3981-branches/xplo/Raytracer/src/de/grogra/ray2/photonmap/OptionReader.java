package de.grogra.ray2.photonmap;

import de.grogra.ray2.Scene;
import de.grogra.ray2.tracing.PixelwiseRenderer;

/**
 * Reads all Photon Map parameter from the registry and checks if values where changed.
 * @author Ralf Kopsch
 */
public class OptionReader {
	private static final String GLOBALPHOTONCOUNT = "photonmapping/globalcount";
	private static final String CAUSTICPHOTONCOUNT = "photonmapping/causticcount";
	private static final String PHOTONAREA  = "photonmapping/area";
	private static final String PHOTONDEPTH = "photonmapping/depth";
	
	private static int oldGlobalPhotonCount = -1;
	private static int oldCausticPhotonCount = -1;
//	private static double oldPhotonArea = -1;
	private static String oldUniqueName = null;
	private static int oldStamp = -1;
	private static int oldDepth = -1;

	private int globalPhotonCount;
	private int causticPhotonCount;
	private int photonDepth;
	private double photonArea;

	/**
	 * This Constructor reads all Photon Map parameter from the registry.
	 * @param renderer the PixelwiseRenderer
	 */	
	public OptionReader(PixelwiseRenderer renderer) {
		this.globalPhotonCount = renderer.getNumericOption(GLOBALPHOTONCOUNT, 200000).intValue();
		assert this.globalPhotonCount >= 0;
		this.causticPhotonCount = renderer.getNumericOption(CAUSTICPHOTONCOUNT, 100000).intValue();
		assert this.causticPhotonCount >= 0;
		this.photonArea = renderer.getNumericOption(PHOTONAREA, 0.08d).doubleValue();
		assert this.photonArea > 0;
		this.photonDepth = renderer.getNumericOption(PHOTONDEPTH, 5).intValue();
		assert this.photonDepth > 0;
	
//		this.threadcount = renderer.getNumericOption(PixelwiseRenderer.THREAD_COUNT, 0).intValue();
//		if (this.threadcount == 0) {
//			this.threadcount = Runtime.getRuntime().availableProcessors();
//		}
	}
	
	/**
	 * Returns true, if new Photon map must be calculated.
	 * @param scene the Scene.
	 * @return true, if new Photon map must be calculated.
	 */
	public boolean isPhotonMapCalcNeeded(Scene scene) {
		return ((scene.getStamp()        != OptionReader.oldStamp) ||
				(scene.getUniqueName()   != OptionReader.oldUniqueName) ||
				(this.globalPhotonCount  != OptionReader.oldGlobalPhotonCount) ||
				(this.causticPhotonCount != OptionReader.oldCausticPhotonCount) ||
//				(this.photonArea         != OptionReader.oldPhotonArea) ||
				(this.photonDepth        != OptionReader.oldDepth));
	}

	/**
	 * Call this after the the photon map was created to update the values.
	 * @param scene the scene object.
	 */
	public void calcFinished(Scene scene) {
		OptionReader.oldGlobalPhotonCount  = this.globalPhotonCount;
		OptionReader.oldCausticPhotonCount = this.causticPhotonCount;
//		OptionReader.oldPhotonArea         = this.photonArea;
		OptionReader.oldUniqueName         = scene.getUniqueName();
		OptionReader.oldStamp              = scene.getStamp();
		OptionReader.oldDepth              = this.photonDepth;
	}
	
	/**
	 * Returns the global photon count.
	 * @return Returns the global photon count.
	 */
	public int getGlobalPhotonCount() {
		return this.globalPhotonCount;
	}
	
	/**
	 * Returns the caustic photon count.
	 * @return Returns the caustic photon count.
	 */
	public int getCausticPhotonCount() {
		return this.causticPhotonCount;
	}

	/**
	 * Returns the photon scan area.
	 * @return Returns the photon scan area.
	 */
	public double getPhotonArea() {
		return this.photonArea;
	}
	
	/**
	 * Returns the recursion depth.
	 * @return Returns the recursion depth.
	 */
	public int getPhotonDepth() {
		return this.photonDepth;
	}

//	public int getThreadCount() {
//		return this.threadcount;
//	}
}
