package de.grogra.ext.limelight;

import javax.vecmath.Vector2d;
import javax.vecmath.Vector2f;

public class Sample {

	public int x;
	public int y;
	private int maxX;
	private int maxY;
	private float posX;
	private float posY;
	private float subX;
	private float subY;
	private int sampleCount;
	
	//take samples from a sub-pixel grid of size gridSize^2
	private int gridSize;
	
	private int posInPixel=0;

	public Sample(int width, int height, int max) {
		maxX = width;
		maxY = height;
		x = 0;
		y = 0;
		gridSize = max;
		sampleCount = 0;
		posInPixel=0;

	}

	public double GetNextSample(MersenneTwister rand) {

		if (sampleCount >= gridSize*gridSize) {
			sampleCount=0;
			posInPixel=0;
			// move to new pixel
			if (x < maxX - 1)
				x++;

			else if (y < maxY - 1) {
				y++;
				System.out.println("Line " + y);
				x = 0;

			} else
				return 0.0;
		}
		
		// get random value in pixel
		
		subX=(float)(posInPixel%gridSize)/(float)gridSize;
		subY=(float)(posInPixel/gridSize)/(float)gridSize;
				
		posX = (float) rand.nextFloat()/gridSize + subX + x;
		posY = (float) rand.nextFloat()/gridSize + subY + y;
		sampleCount++;
		posInPixel++;
		return 1.0/(maxX*maxY*gridSize*gridSize);

	}

	public Vector2f toWorld() {
		return new Vector2f(posX * 2.f / maxX - 1, posY * 2.f / maxY - 1);
	}


}
