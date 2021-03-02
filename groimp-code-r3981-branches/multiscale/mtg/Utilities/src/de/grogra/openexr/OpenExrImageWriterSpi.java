
package de.grogra.openexr;

import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;

/**
 * 
 * @author Reinhard Hemmerling
 *
 */
public class OpenExrImageWriterSpi extends ImageWriterSpi
{
	private static final String vendorName = "nmi";

	private static final String version = "1.0";

	private static final String[] names = {"exr"};

	private static final String[] suffixes = {"exr"};

	private static final String[] MIMETypes = {"image/exr", "image/x-exr"};

	private static final String writerClassName = "de.grogra.openexr.OpenExrImageWriter";

	public OpenExrImageWriterSpi ()
	{
		super (
			vendorName, 
			version, 
			names, 
			suffixes, 
			MIMETypes,
			writerClassName, 
			STANDARD_OUTPUT_TYPE, 
			null,//readerSpiNames, 
			false, null,
			null, null, null, false,
			null, 
			null, 
			//PNGMetadata.nativeMetadataFormatName,
			//"com.sun.imageio.plugins.png.PNGMetadataFormat", 
			null, null);
	}

	@Override
	public boolean canEncodeImage (ImageTypeSpecifier type)
	{
		SampleModel sampleModel = type.getSampleModel();
		boolean result = sampleModel.getNumBands() >= 3 && sampleModel.getDataType() == DataBuffer.TYPE_FLOAT;
		return  result;
	}

	@Override
	public ImageWriter createWriterInstance (Object extension)
			throws IOException
	{
		return new OpenExrImageWriter(this);
	}

	@Override
	public String getDescription (Locale locale)
	{
		return "OpenEXR Image Writer";
	}

}
