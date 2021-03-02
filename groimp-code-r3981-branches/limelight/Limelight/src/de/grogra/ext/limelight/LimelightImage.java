package de.grogra.ext.limelight;

import java.awt.Color;
import java.awt.image.BufferedImage;

import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3f;

public class LimelightImage extends BufferedImage {
	public float[][] pixels;

	private Spectrum3f tmpSpec = new Spectrum3f();
	private Spectrum3f tmpSpec2 = new Spectrum3f();
	private Spectrum3f maxSpec = new Spectrum3f();
	int nSamples = 0;

	public LimelightImage(int width, int height) {
		super(width, height, TYPE_INT_RGB);
		pixels = new float[3][width * height];

	}

	//adds a sample to the pixel array and updates maxSpec
	public void addSample(Sample sample, Spectrum3f ls) {

		pixels[0][sample.x + getWidth() * sample.y] += ls.x;
		pixels[1][sample.x + getWidth() * sample.y] += ls.y;
		pixels[2][sample.x + getWidth() * sample.y] += ls.z;
		if (ls.x > maxSpec.x)
			maxSpec.x = ls.x;
		if (ls.y > maxSpec.y)
			maxSpec.y = ls.y;
		if (ls.z > maxSpec.z)
			maxSpec.z = ls.z;
	}

	//write the pixel array to a byte buffer and apply linear tone mapping
	public void createBuffer() {
		for (int j = 1; j < getHeight(); j++) {
			for (int i = 1; i < getWidth(); i++) {
				tmpSpec.set(pixels[0][j * getWidth() + i], pixels[1][j
						* getWidth() + i], pixels[2][j * getWidth() + i]);
				tmpSpec2.set((Spectrum)tmpSpec);
				tmpSpec2.add((Spectrum)new Spectrum3f(1,1,1));
				tmpSpec.div((Spectrum)tmpSpec2);
				setRGB(i, j, toRGB(tmpSpec));

			}
		}

	}

	public int toRGB(Spectrum3f spectrum) {
		if (spectrum.x > 1.f)
			spectrum.x = 1.f;
		if (spectrum.y > 1.f)
			spectrum.y = 1.f;
		if (spectrum.z > 1.f)
			spectrum.z = 1.f;

		Color c = new Color(spectrum.x, spectrum.y, spectrum.z);
		return c.getRGB();
	}

	public void scale(float f) {
		for (int j = 1; j < getHeight(); j++) {
			for (int i = 1; i < getWidth(); i++) {
				pixels[0][j * getWidth() + i] *= f;
				pixels[1][j * getWidth() + i] *= f;
				pixels[2][j * getWidth() + i] *= f;

			}
		}

	}

}
