package de.grogra.ray2.radiosity;

/**
 * This class represents one Pixel on the HemiCube.
 * @author Ralf Kopsch 
 */
public class ZPixel {
	private SubPatch patch;
	private double zValue;
	private boolean isBacksideFront;	
	
	/**
	 * Creates a new Hemicube pixel.
	 */
	public ZPixel() {
		this.patch = null;
		this.zValue = Double.MAX_VALUE;
		this.isBacksideFront = true;
	}
	
	/**
	 * Adds a patch to this pixel.
	 * If the pixel contains all ready a patch the 2 z values will be compared
	 * and the patch with the lesser value will be inserted.
	 * @param patch
	 * @param z
	 * @param back
	 */
	public void add(SubPatch patch, double z, boolean back) {
		if( zValue > z ) {
			this.zValue = z;
			this.patch = patch;
			this.isBacksideFront = back;
		}		
	}
	
	/**
	 * Returns the patch assigned to this pixel.
	 * @return Returns the patch assigned to this pixel.
	 */
	public SubPatch getPatch() {
		return this.patch;
	}
	
	/**
	 * Returns true, if the back side of a patch is in front. 
	 * @return Returns true, if the back side of a patch is in front.
	 */
	public boolean isBack() {
		return this.isBacksideFront;
	}	
	
	@Override
	public String toString() {
		return this.patch.toString();
	}
}
