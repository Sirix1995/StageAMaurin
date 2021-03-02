
package de.grogra.openexr;

import java.awt.image.*;
import java.io.IOException;
import java.nio.ByteOrder;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;

/**
 * 
 * @author Reinhard Hemmerling
 *
 */
public class OpenExrImageWriter extends ImageWriter
{
	public OpenExrImageWriter (ImageWriterSpi imageWriterSpi)
	{
		super (imageWriterSpi);
	}

	@Override
	public IIOMetadata getDefaultStreamMetadata (ImageWriteParam param)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIOMetadata getDefaultImageMetadata (ImageTypeSpecifier imageType,
			ImageWriteParam param)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIOMetadata convertStreamMetadata (IIOMetadata inData,
			ImageWriteParam param)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIOMetadata convertImageMetadata (IIOMetadata inData,
			ImageTypeSpecifier imageType, ImageWriteParam param)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void write (IIOMetadata streamMetadata, IIOImage image,
			ImageWriteParam param) throws IOException
	{
		RenderedImage renderedImage = image.getRenderedImage ();
		final int width = renderedImage.getWidth ();
		final int height = renderedImage.getHeight ();

		Raster raster = image.hasRaster () ? image.getRaster () : renderedImage
			.getData ();
		boolean hasAlpha = renderedImage.getColorModel ().hasAlpha ();
		boolean multiply = hasAlpha && !renderedImage.getColorModel ().isAlphaPremultiplied ();
		int numComp = hasAlpha ? 4 : 3;
		SampleModel sampleModel = renderedImage.getSampleModel ();
		DataBuffer data = raster.getDataBuffer ();

		ImageOutputStream stream = (ImageOutputStream) output;
		stream.setByteOrder (ByteOrder.LITTLE_ENDIAN);

		long posStart = stream.getStreamPosition();
		
		// write magic number
		stream.writeByte (0x76);
		stream.writeByte (0x2F);
		stream.writeByte (0x31);
		stream.writeByte (0x01);

		// write version
		stream.writeInt (0x02);

		long posHeader = stream.getStreamPosition();
		
		// write channels attribute
		stream.writeBytes ("channels");
		stream.writeByte (0);
		stream.writeBytes ("chlist");
		stream.writeByte (0);
		stream.writeInt (18 * numComp + 1); // size

		if (hasAlpha)
		{
			// write 18 bytes to declare alpha channel
			stream.writeBytes ("A");
			stream.writeByte (0);
			stream.writeInt (2); // float
			stream.writeByte (0); // linear ?
			stream.writeByte (0);
			stream.writeByte (0);
			stream.writeByte (0);
			stream.writeInt (1); // x-sampling
			stream.writeInt (1); // y-sampling
		}

		// write 18 bytes to declare blue channel
		stream.writeBytes ("B");
		stream.writeByte (0);
		stream.writeInt (2); // float
		stream.writeByte (0); // linear ?
		stream.writeByte (0);
		stream.writeByte (0);
		stream.writeByte (0);
		stream.writeInt (1); // x-sampling
		stream.writeInt (1); // y-sampling

		// write 18 bytes to declare green channel
		stream.writeBytes ("G");
		stream.writeByte (0);
		stream.writeInt (2); // float
		stream.writeByte (0); // linear ?
		stream.writeByte (0);
		stream.writeByte (0);
		stream.writeByte (0);
		stream.writeInt (1); // x-sampling
		stream.writeInt (1); // y-sampling

		// write 18 bytes to declare red channel
		stream.writeBytes ("R");
		stream.writeByte (0);
		stream.writeInt (2); // float
		stream.writeByte (0); // linear ?
		stream.writeByte (0);
		stream.writeByte (0);
		stream.writeByte (0);
		stream.writeInt (1); // x-sampling
		stream.writeInt (1); // y-sampling

		// write terminating null-byte
		stream.writeByte (0);

		// write compression attribute
		stream.writeBytes ("compression");
		stream.writeByte (0);
		stream.writeBytes ("compression");
		stream.writeByte (0);
		stream.writeInt (1); // size
		stream.writeByte (0); // no compression

		// write dataWindow attribute
		stream.writeBytes ("dataWindow");
		stream.writeByte (0);
		stream.writeBytes ("box2i");
		stream.writeByte (0);
		stream.writeInt (16); // size
		stream.writeInt (0); // xMin
		stream.writeInt (0); // yMin
		stream.writeInt (width - 1); // xMax
		stream.writeInt (height - 1); // yMax

		// write displayWindow attribute
		stream.writeBytes ("displayWindow");
		stream.writeByte (0);
		stream.writeBytes ("box2i");
		stream.writeByte (0);
		stream.writeInt (16); // size
		stream.writeInt (0); // xMin
		stream.writeInt (0); // yMin
		stream.writeInt (width - 1); // xMax
		stream.writeInt (height - 1); // yMax

		// write lineOrder attribute
		stream.writeBytes ("lineOrder");
		stream.writeByte (0);
		stream.writeBytes ("lineOrder");
		stream.writeByte (0);
		stream.writeInt (1); // size
		stream.writeByte (0); // INCY

		// write pixelAspectRatio attribute
		stream.writeBytes ("pixelAspectRatio");
		stream.writeByte (0);
		stream.writeBytes ("float");
		stream.writeByte (0);
		stream.writeInt (4); // size
		stream.writeFloat (1.0f);

		// write screenWindowCenter attribute
		stream.writeBytes ("screenWindowCenter");
		stream.writeByte (0);
		stream.writeBytes ("v2f");
		stream.writeByte (0);
		stream.writeInt (8); // size
		stream.writeFloat (0.0f);
		stream.writeFloat (0.0f);

		// write screenWindowWidth attribute
		stream.writeBytes ("screenWindowWidth");
		stream.writeByte (0);
		stream.writeBytes ("float");
		stream.writeByte (0);
		stream.writeInt (4); // size
		stream.writeFloat (1.0f);

		// terminate header
		stream.writeByte (0);

		long posLineOffsetTable = stream.getStreamPosition();

		// write line offset table
		for (int y = 0; y < height; y++)
		{
			long l = 0;
			l += width; // width of image
			l *= 4; // 4 bytes per float
			l *= numComp; // # color components
			l += 8; // row number and pixel data size
			l *= y; // current row
			l += height * 8; // size of line offset table
			l += (posLineOffsetTable - posStart); // start of line offset table
			stream.writeLong (l);
		}

		// write scanlines
		float[] buf = new float[width];
		float[] alphaBuf = new float[width];
		for (int y = 0; y < height; y++)
		{
			stream.writeInt (y);// y
			stream.writeInt (width * 4 * numComp); // sizeof(float) = 4

			if (hasAlpha)
			{
				// write A
				sampleModel.getSamples (0, y, width, 1, 3, alphaBuf, data);
				stream.writeFloats (alphaBuf, 0, width);
			}

			// write B
			sampleModel.getSamples (0, y, width, 1, 2, buf, data);
			if (multiply)
			{
				mul (buf, alphaBuf);
			}
			stream.writeFloats (buf, 0, width);

			// write G
			sampleModel.getSamples (0, y, width, 1, 1, buf, data);
			if (multiply)
			{
				mul (buf, alphaBuf);
			}
			stream.writeFloats (buf, 0, width);

			// write R
			sampleModel.getSamples (0, y, width, 1, 0, buf, data);
			if (multiply)
			{
				mul (buf, alphaBuf);
			}
			stream.writeFloats (buf, 0, width);
		}
	}

	private static void mul (float[] a, float[] factor)
	{
		for (int i = a.length - 1; i >= 0; i--)
		{
			a[i] *= factor[i];
		}
	}
}
