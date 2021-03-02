package de.grogra.ray2.radiosity;

/**
 * This class creates a hemicube and calculates the delta form factors.
 * @author Ralf Kopsch
 */
public final class HemiCube {
	private static boolean isInit = false;
	private static int pixelsWide;
	private static float worldWide = -1;
	private static float[][] topPixels;
	private static float[][] sidePixels;
	
	/**
	 * Initializes the hemicube with the given cube width.  
	 * @param cubeWidth the cube width.
	 */
	public static void init(int cubeWidth) {
		HemiCube.pixelsWide = cubeWidth;
		
		int regionSize = pixelsWide / 2;
		
		// init arrays
		topPixels = new float[pixelsWide][pixelsWide];
		
		// all sides have the same ff-values so we only need to store one side
		sidePixels = new float[pixelsWide][regionSize];
		
		// calculate values
		for( int topY = regionSize - 1; topY >= 0; topY--) {
			for( int topX = 0; topX < regionSize; topX++) {
				float x = topX - regionSize - 0.5f;
				float y = topY + 0.5f;
				float temp = x * x + y * y + regionSize * regionSize;
				temp = temp * temp;
				float value = (regionSize * regionSize) / ((float) Math.PI * temp);
				topPixels[topX][topY] = value;
			}
		}
		for( int topY = regionSize - 1; topY >= 0; topY--) {
			for( int topX = 0; topX < regionSize; topX++) {
				float x = topX - regionSize - 0.5f;
				float y = topY + 0.5f;
				float temp = x * x + regionSize * regionSize;
				float temp2 = temp + y * y;
				temp2 = temp2 * temp2;
				temp = (float) Math.sqrt(temp);
				float value = (y * temp) / ((float) Math.PI * temp2);
				sidePixels[topX][topY] = value;
			}
		}
		
		// copy into rest of top, and into sides
		for(int y = 0; y < regionSize; y++) {
			for( int x = 0; x < regionSize; x++) {
				topPixels[pixelsWide - x - 1][y] = topPixels[x][y];
				sidePixels[pixelsWide - x - 1][y] = sidePixels[x][y];
			}
			for( int x = 0; x < pixelsWide; x++) {
				topPixels[x][pixelsWide - y - 1] = topPixels[x][y];
			}
		}
		
		
		isInit = true;
	}
	
	/**
	 * Returns the form factor for one hemicube pixel. 
	 * @param side the cube side
	 * @param pixX the x-coordinate.
	 * @param pixY the y-coordinate.
	 * @return Returns the form factor for one hemicube pixel.
	 */
	public static float getFormFactor(int side, int pixX, int pixY) {
		if (!isInit) {
			System.err.println("ERROR: Hemicube not initialized");
			System.exit(1);
		}
		if( side == 0) {
			return topPixels[pixX][pixY];
		}
		if( side > 0) {
			return sidePixels[pixX][pixY];
		}
		return 0;
	}
	
	/**
	 * Returns the hemicube size in world coordinates.
	 * @return Returns the hemicube size in world coordinates.
	 */
	public static float getWorldWide() {
		if (HemiCube.worldWide == -1) {
			System.err.println("ERROR: Hemicube not initialized");
			System.exit(1);
		}
		return HemiCube.worldWide;
	}
	
	/**
	 * Sets the hemicube size in world coordinates.
	 * @param worldWide the hemicube size to set.
	 */
	public static void setWorldWide(float worldWide) {
		HemiCube.worldWide = worldWide;
	}
	
	/**
	 * Return the number of hemicube pixels.
	 * @return Return the number of hemicube pixels.
	 */
	public static int getPixelsWide() {
		return HemiCube.pixelsWide;
	}
	
	
	/**
	 * Prints out the hemicube delta form factor for debugging.  
	 */
	public static void print() {
	    System.out.println("hemi cube top content:");
	    for (int pixX = 0; pixX < pixelsWide; pixX++) {
	        for (int pixY = 0; pixY < pixelsWide; pixY++) {
	            System.out.print(String.format("%f ", getFormFactor(0, pixX, pixY)));
	            if (((pixY+1) % 5) == 0) {
	                System.out.println();
	            }
	        }
	        System.out.println();
	    }

	    System.out.println("hemi cube side 2 content:");
	    for (int pixX = 0; pixX < pixelsWide; pixX++) {
	        for (int pixY = 0; pixY < pixelsWide/2; pixY++) {
	            System.out.print(String.format("%f ", getFormFactor(2, pixX, pixY)));
	            if (((pixY+1) % 5) == 0) {
	                System.out.println();
	            }
	        }
	        System.out.println();
	    }
	}
}
