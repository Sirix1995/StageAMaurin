package de.grogra.ray2.radiosity;

import java.util.Map;

/**
 * This class describes a viewcube.
 * A viewcube is a hemicube for exact one patch.
 * 
 * @author Ralf Kopsch
 */
public class ViewCube {
	private static float M_05PI = (float) (Math.PI / 2f);
	
	private ZBuffer top;
	private ZBuffer[] side = new ZBuffer[4]; 
	private Vector3d normalVector;
	private Vector3d deltaCenter = new Vector3d();
	private double thetaX;
	private double thetaY;

	/**
	 * Creates a new viewcube.
	 * @param center the center of the viewcube.
	 * @param normal the normal vector of the viewcube.
	 */
	public ViewCube(Vector3d center, Vector3d normal) {
		this.normalVector = normal;
		Vector3d cubeCenter = new Vector3d(0,0,0);
		this.deltaCenter.sub(cubeCenter, center);
		
		// calculate rotation needed to line normal up with Z+
		Vector3d.Theta T = normalVector.calcRotationToZ();
		this.thetaX = T.thetaX;
		this.thetaY = T.thetaY;

		// init ZBuffer
		this.top = new ZBuffer(true);
		for (int j = 0; j < 4; j++) {
			this.side[j] = new ZBuffer(false);
		}
	}

 
	/**
	 * Add patch to the viewcube.
	 * @param pIn the patch to add.
	 */
	public void projPatchOnCube(SubPatch pIn) {
		SubPatch p = new SubPatch(pIn);

		// move patch so that cube center is at (0,0,0)
		p.move(this.deltaCenter);

		// Now rotate patch so that cube normal lines up with Z+
		p.rotateX(thetaX);	
		p.rotateY(thetaY);
		this.top.insert(p, pIn);

		// now rotate patch and cube so that side[0] normal points along Z+
		// rotate clockwise 90 degrees around y axis
		float delTheta = ViewCube.M_05PI;
		p.rotateY(-delTheta);
		p.rotateZ(-delTheta);
		this.side[0].insert(p, pIn);
		for( int s=1; s<4; s++) {
			p.rotateY(-delTheta);
			this.side[s].insert(p, pIn);		
		}
	}
	
	/**
	 * Computes the form factor map.
	 * @param ffMap the map to fill. 
	 */
	public void computeFormFactorMap(Map<SubPatch, FormFactor> ffMap) {
		this.top.fillFormFactors(ffMap, 0);
		for( int s = 0; s < 4; s++) {
			this.side[s].fillFormFactors(ffMap, 1);
		}	
	}
}
