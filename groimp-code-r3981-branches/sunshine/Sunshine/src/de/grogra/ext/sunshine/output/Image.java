package de.grogra.ext.sunshine.output;


import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferFloat;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import javax.vecmath.Color4f;

import com.sun.opengl.util.ImageUtil;

import java.awt.Color;
import java.awt.Transparency;


/**
 * This class manages the creating, storing and converting of images
 * 
 * @author Thomas
 *
 */
public class Image
{
	public static final int OUTPUT_RAW 		= 0;
	public static final int OUTPUT_CLAMP	= 1;
	public static final int OUTPUT_LINEAR	= 2;
	public static final int OUTPUT_LOG		= 3;
	
	private int outputMode = OUTPUT_RAW;
	private SunshineOutputStrategy output;
	
	private float scale = 1.0f;
	
	BufferedImage image;
	float[][] hdrPixels;
	
	public Image(int width, int height)
	{
//		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		this (width, height, OUTPUT_RAW);
	}
	
	public Image(int width, int height, int mode)
	{		
		createImage(width, height);
		setOutputMode(mode);
	}
	
	public void setScale(float scale)
	{
		this.scale = scale;
	}
	
	public void setOutputMode(int mode)
	{
		outputMode = mode;
		
		switch(outputMode)
		{			
			case OUTPUT_CLAMP:
			{
				output = new ClampOutput();
				break;
			}
			
			case OUTPUT_LINEAR:
			{
				output = new LinearOutput();
				break;
			}
			
			case OUTPUT_LOG:
			{
				output = new LogarithmicOutput();
				break;
			}
			default:
			{
				output = new SunshineOutputStrategy() 
				{
//					@Override
					public void applyValues(float[] values, float scale) {}

//					@Override
					public void setMinMax(float min, float max) {}

					public void gatherValues(float[] values) {	}

					public boolean needPrePass() { return false; }
					
				};
				break;
			}
		}
	}
	
	// convertes the given byte buffer to a buffered image
	public void createImage(ByteBuffer bb)
	{
		bb.rewind();
		try {
			int offset 			= 0;
			final int width 	= image.getWidth();
			final int height 	= image.getHeight();
			
			float[] values		= new float[4]; 
			
			if(output.needPrePass())
			{
				for(int y = 0; y < height ; y++)
				{
					for (int x = 0; x < width; x++) 
					{
						values[0] = bb.getFloat();
						values[1] = bb.getFloat();
						values[2] = bb.getFloat();
						values[3] = bb.getFloat();
			
						output.gatherValues(values);

						offset++;
					}
				} //for
				
				bb.rewind();
			}

			offset = 0;
			
			for(int y = 0; y < height ; y++)
			{
				for (int x = 0; x < width; x++) 
				{
//					image.setRGB(x, y, getScaledIntColor( getPixelColor(bb) ));

					values[0] = bb.getFloat();
					values[1] = bb.getFloat();
					values[2] = bb.getFloat();
					values[3] = bb.getFloat();
										
					output.applyValues(values, scale);

					hdrPixels[0][offset] = values[0];
					hdrPixels[1][offset] = values[1];
					hdrPixels[2][offset] = values[2];
					hdrPixels[3][offset] = values[3];
					offset++;
				}
			} //for
			
			ImageUtil.flipImageVertically(image);
			
		} catch (BufferUnderflowException e)
		{
			System.err.println("Error: " + e);
		}
	} //createImage
	
	
	public BufferedImage getImage()
	{
		return image;
	} //getImage
	
	int i = 0;
	
	/**
	 * convert the pixel values to a color
	 * @param bb 
	 * @return color4f
	 */
	private Color4f getPixelColor(ByteBuffer bb)
	{
		return new Color4f(bb.getFloat(), bb.getFloat(), bb.getFloat(), bb.getFloat());
	} //readFromTexture


	/**
	 * scale the color to an integer value
	 * 
	 * @param color
	 * @return the scaled value
	 */
	private int getScaledIntColor(Color4f color) {
		float m_brightnessScaleFactor = 1.5f;
		
		Color4f scaled_color = new Color4f(color);
		
		// scale color
		if (color.w>0) {
			scaled_color.x *= m_brightnessScaleFactor;
			scaled_color.y *= m_brightnessScaleFactor;
			scaled_color.z *= m_brightnessScaleFactor;
		}
		
		// clamp colors
		scaled_color.clampMax(1.0f);
		scaled_color.clampMin(0.0f);
	
		if(i == 1)
		{
			i++;
		}
		
		// convert to int
		Color rgba_color = scaled_color.get();
		return (rgba_color.getAlpha() << 24 ) +
			   (rgba_color.getRed() << 16) + 
			   (rgba_color.getGreen() << 8) + 
			   (rgba_color.getBlue());
		
				
	} //getScaledIntColor
	
	
	
	
	private BufferedImage createImage(int width, int height)
	{
		hdrPixels = new float[4][width * height];
		DataBufferFloat buffer = new DataBufferFloat (hdrPixels, hdrPixels[0].length);
		BandedSampleModel sampleModel = new BandedSampleModel (buffer.getDataType (), width, height, 4);
		WritableRaster raster = Raster.createWritableRaster (sampleModel, buffer, null);
		ColorSpace cs = ColorSpace.getInstance (ColorSpace.CS_sRGB);
		ComponentColorModel cm = new ComponentColorModel (cs, true, false, Transparency.TRANSLUCENT, buffer.getDataType ());
		image = new BufferedImage (cm, raster, false, null);
		
		return image;
	}
	
} //class
