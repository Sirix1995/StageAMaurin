
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package de.grogra.util;

import java.io.IOException;
import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

public class PPMImageReaderSpi extends ImageReaderSpi
{
	public static final String MIME_TYPE = "image/x-portable-pixmap";

	public PPMImageReaderSpi ()
	{
		super ("grogra.de", "1.0", new String[] {"ppm"},
			   new String[] {"ppm"}, new String[] {MIME_TYPE},
			   "de.grogra.util.PPMImageReader",
			   STANDARD_INPUT_TYPE,
			   null, false, null, null, null, null, false, null, null, null, null);
	}


	@Override
	public String getDescription (Locale locale)
	{
		return Utils.I18N.msg ("ppmimagereader.description");
	}


	@Override
	public boolean canDecodeInput (Object input) throws IOException
	{
		if (!(input instanceof ImageInputStream))
		{
			return false;
		}
		ImageInputStream iis = (ImageInputStream) input;
		iis.mark ();
		try
		{
			return (iis.read () == 'P') && (iis.read () == '6');
		}
		finally
		{
			iis.reset ();
		}
	}
	

	@Override
	public ImageReader createReaderInstance (Object extension)
	{
		return new PPMImageReader (1000000);
	}

}
