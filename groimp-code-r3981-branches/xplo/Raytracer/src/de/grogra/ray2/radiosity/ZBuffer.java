package de.grogra.ray2.radiosity;

import java.util.Map;

import javax.vecmath.Point3d;

/**
 * This class implements a Z-buffer for one side of the hemicube. 
 * @author Ralf Kopsch
 */
public class ZBuffer {
	private ZPixel viewPlane[][] = null;

	private int planePixWide; 
	private int planePixHigh;
	private float planeStartX; 
	private float planeEndX; 
	private float planeStartY;
	private float planeEndY;
	
	/**
	 * Creates a new z-buffer.
	 * @param isTop true, if this is the top side of a hemicube.
	 */
	public ZBuffer(boolean isTop) {
		int hemiCubeWide = HemiCube.getPixelsWide();
		float hemiCubeWorldWide = HemiCube.getWorldWide();
		float halfWorldWide = hemiCubeWorldWide / 2.0f;
				
		if (isTop) {
			this.planePixWide =  hemiCubeWide;
			this.planePixHigh =  hemiCubeWide;
			this.planeStartX  = -halfWorldWide;
			this.planeEndX    =  halfWorldWide;
			this.planeStartY  = -halfWorldWide;
			this.planeEndY    =  halfWorldWide;
		} else {
			this.planePixWide =  hemiCubeWide;
			this.planePixHigh =  hemiCubeWide >> 1;
			this.planeStartX  = -halfWorldWide;
			this.planeEndX    =  halfWorldWide;
			this.planeStartY  =  0;
			this.planeEndY    =  halfWorldWide;
		}
		this.viewPlane = new ZPixel[planePixWide][planePixHigh];

		for (int x = 0; x < planePixWide; x++) {
			for (int y = 0; y < planePixHigh; y++) {
				this.viewPlane[x][y] = new ZPixel();
			}
		}
	}
	
	/**
	 * Inserts a new patch into the buffer.
	 * @param p a rotated patch
	 * @param originalPatch the unmodified patch
	 */
	public void insert(SubPatch p, SubPatch originalPatch){
		Vector3d vert[] = new Vector3d[3];
		vert[0] = (Vector3d) p.getVertices()[0].clone();
		vert[1] = (Vector3d) p.getVertices()[1].clone();
		vert[2] = (Vector3d) p.getVertices()[2].clone();
		
		boolean isBack = false;
		Vector3d patchNorm = p.getNormal();
		Vector3d planeNorm = new Vector3d(0,0,1);
		// patch facing away from view plane
		if (patchNorm.dot(planeNorm) > 0) {
			isBack = true;
		}
		
		boolean areZValsBehind = calcProjectedCoords(vert);
		if( !areZValsBehind ) {
			int[] maxRowX = new int[planePixHigh];
			int[] minRowX = new int[planePixHigh];
			double[] zOfMaxRowX = new double[planePixHigh];
			double[] zOfMinRowX = new double[planePixHigh];

			for( int i=0; i<planePixHigh; i++) {
				maxRowX[i] = -planePixWide;
				minRowX[i] = planePixWide;
				zOfMaxRowX[i] = 1;
				zOfMinRowX[i] = 1;
			}
			computeRowValues(vert, minRowX, maxRowX, zOfMinRowX, zOfMaxRowX);
			clipValues(minRowX, maxRowX, zOfMinRowX, zOfMaxRowX);
			fillZBuffer(originalPatch, isBack, minRowX, maxRowX, zOfMinRowX, zOfMaxRowX);
		}
	}
	
	private boolean calcProjectedCoords(Vector3d verts[]) {
		float d = this.planeEndY;
		double minDist = 0.001d * d;	// near clipping plane
		boolean areZValsBehind = false;				
	
		for(int i = 0; i < 3; i++) {
			if( verts[i].z < minDist ) {
				int nextInd = 1;
				double maxZ = -1;
				int indOfMaxZ = 0;

				for( nextInd = 1; nextInd < 3; nextInd++ ) {
					double thisZ = verts[(i+nextInd)%3].z;
					if( thisZ > maxZ ) {
						maxZ = thisZ; 
						indOfMaxZ = nextInd;
					}
				}

				nextInd = indOfMaxZ;	
				if( maxZ < minDist ) {
					areZValsBehind = true;
				}
				if(!areZValsBehind) {
					Point3d delta = new Point3d();
					delta.sub(verts[(i + nextInd) % 3], verts[i]);
					double invDeltaZ = 1d / delta.z;
					double slopeX = delta.x * invDeltaZ;
					double slopeY = delta.y * invDeltaZ;
					double deltaToZero = minDist - verts[i].z;
					
					verts[i].set(deltaToZero * slopeX + verts[i].x, 
							deltaToZero * slopeY + verts[i].y, minDist);
				}
			}

			double invZ = 1d / verts[i].z;
			double newX = (verts[i].x * d) * invZ;
			double newY = (verts[i].y * d) * invZ;
			double newZ = -invZ;
			verts[i].set(newX, newY, newZ);
		}
		return areZValsBehind;
	}

	private void computeRowValues(Vector3d vert[], int[] minRowX, int[] maxRowX, double[] zOfMinRowX, double[] zOfMaxRowX) {
		float worldToPixUnit = planePixWide/(planeEndX - planeStartX);
		int planeStartXInPix = (int)(worldToPixUnit * planeStartX);
		int planeEndXInPix = planeStartXInPix + planePixWide -1;
		int planeStartYInPix = (int)(worldToPixUnit * planeStartY);
		int planeEndYInPix = planeStartYInPix + planePixHigh -1;
		int yPixArrayOffset = 0 - planeStartYInPix;
		
		for( int i=0; i<3; i++) {
			int startXInPix = (int)(vert[i].x * worldToPixUnit);
			int startYInPix = (int)(vert[i].y * worldToPixUnit);
			int endXInPix = (int)(vert[(i+1)%3].x * worldToPixUnit);
			int endYInPix = (int)(vert[(i+1)%3].y * worldToPixUnit);
			double startZ = vert[i].z;
			double endZ = vert[(i+1)%3].z;

			if( startYInPix > endYInPix) {
				int temp = startYInPix;
				startYInPix = endYInPix;
				endYInPix = temp;
				temp = startXInPix;
				startXInPix = endXInPix;
				endXInPix = temp;
				double tempf = startZ;
				startZ = endZ;
				endZ = tempf;
			}

			if( (endYInPix > planeStartYInPix) && (startYInPix < planeEndYInPix) ) {
				// find max and min x contributions from this edge	
				double deltaX = endXInPix - startXInPix;
				double deltaY = endYInPix - startYInPix;
				double deltaZ = endZ - startZ;
				double invDeltaY = 1/deltaY;
				double slopeX = deltaX * invDeltaY;
				double slopeZ = deltaZ * invDeltaY;
				int realStartY = startYInPix;
				int realEndY = endYInPix;
				if( startYInPix < planeStartYInPix )  realStartY = planeStartYInPix;	
				if( endYInPix > planeEndXInPix )  realEndY = planeEndYInPix;
				int currentY = realStartY;
				double currentX = slopeX * (realStartY - startYInPix) + startXInPix;
				double currentZ = slopeZ * (realStartY - startYInPix) + startZ;

				while( currentY <= realEndY ) {
					int arrayIndex = currentY + yPixArrayOffset;
					// compare x against max for this row
					if( maxRowX[ arrayIndex ] < currentX ) {
						maxRowX[ arrayIndex ] = (int)(currentX);
						zOfMaxRowX[ arrayIndex ] = currentZ;
					}
					// compare x against min for this row
					if( minRowX[ arrayIndex ] > currentX ) {
						minRowX[ arrayIndex ] = (int)(currentX);
						zOfMinRowX[ arrayIndex ] = currentZ;
					}
					currentY++;
					currentZ += slopeZ;
					currentX += slopeX;
				}	// end while
			}	// end if in y range
		}	// end for all sides of poly
	}
	
	private void clipValues(int[] minRowX, int[] maxRowX, double[] zOfMinRowX, double[] zOfMaxRowX) {
		float worldToPixUnit = planePixWide/(planeEndX - planeStartX);
		int planeStartXInPix = (int)(worldToPixUnit * planeStartX);
		int planeEndXInPix = planeStartXInPix + planePixWide -1;
		
		// clip max and min values for each row 
		for( int y=0; y<planePixHigh; y++ ) {
			double startZ = zOfMinRowX[y];
			double endZ = zOfMaxRowX[y];
			double deltaZ = endZ - startZ;
			int deltaPix = maxRowX[y] - minRowX[y];
			double slopeZ = deltaZ/deltaPix;

			if( maxRowX[y] > planeEndXInPix ) {
				float deltaX = (float)(maxRowX[y] - planeEndXInPix);
				zOfMaxRowX[y] = zOfMaxRowX[y] - deltaX * slopeZ;
				maxRowX[y] = planeEndXInPix;
			}
			if( minRowX[y] < planeStartXInPix ) {
				float deltaX = (float)(minRowX[y] - planeStartXInPix);
				zOfMinRowX[y] = zOfMinRowX[y] - deltaX * slopeZ;
				minRowX[y] = planeStartXInPix;
			}
		}
	}
	
	private void fillZBuffer(SubPatch originalPatch, boolean isBack, int[] minRowX, int[] maxRowX, double[] zOfMinRowX, double[] zOfMaxRowX) {
		float worldToPixUnit = planePixWide/(planeEndX - planeStartX);
		int planeStartXInPix = (int)(worldToPixUnit * planeStartX);
		int planeEndXInPix = planeStartXInPix + planePixWide -1;
		int xPixArrayOffset = 0 - planeStartXInPix;	
		
		for( int y = 0; y < planePixHigh; y++) {
			if( (maxRowX[y] >= planeStartXInPix) && (minRowX[y] <= planeEndXInPix)) {
				double startZ = zOfMinRowX[y];
				double deltaZ = zOfMaxRowX[y] - startZ;
				int deltaPix = maxRowX[y] - minRowX[y];
				double slopeZ = deltaZ/deltaPix;
				int startX = minRowX[y] + xPixArrayOffset;
				int endX = maxRowX[y] + xPixArrayOffset;
				double currentZ = startZ;
				for( int x=startX; x<=endX; x++) {
					viewPlane[x][y].add( originalPatch, currentZ, isBack);
					currentZ += slopeZ;
				} 
			}
		}					
	}
	
	/**
	 * Returns a pixel from this buffer.
	 * @param x the x-coordinate.
	 * @param y the y-coordinate.
	 * @return Returns a pixel from this buffer.
	 */
	public ZPixel getZPixel(int x, int y){
		return this.viewPlane[x][y];
	}
	
	/**
	 * Fills the given form factor map with form factors.
	 * @param formFactors the form factor array (in/out parameter)
	 * @param side the side of the cube.
	 */
	public void fillFormFactors(Map<SubPatch, FormFactor> formFactors, int side) {
		for( int px = 0; px < this.viewPlane.length; px++) {
			for( int py = 0; py < this.viewPlane[0].length; py++) {
				SubPatch patch = this.viewPlane[px][py].getPatch();
				if( (patch != null) && !(this.viewPlane[px][py].isBack()) ) {
					if (formFactors.get(patch) == null) {
						formFactors.put(patch, new FormFactor(HemiCube.getFormFactor(0, px, py)));
					} else {
						formFactors.get(patch).add(HemiCube.getFormFactor(0, px, py));
					}
				}
			}
		}
	}
	
}
