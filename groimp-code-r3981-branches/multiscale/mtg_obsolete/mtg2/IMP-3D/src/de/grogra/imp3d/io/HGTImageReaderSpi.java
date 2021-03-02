
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

package de.grogra.imp3d.io;

import java.util.Locale;
import javax.imageio.*;
import javax.imageio.spi.ImageReaderSpi;

public class HGTImageReaderSpi extends ImageReaderSpi
{
	public static final String MIME_TYPE = "model/x-srtm-hgt";

	public HGTImageReaderSpi ()
	{
		super ("grogra.de", "1.0", new String[] {"hgt"},
			   new String[] {"hgt"}, new String[] {MIME_TYPE},
			   "de.grogra.imp3d.io.HGTImageReader",
			   STANDARD_INPUT_TYPE,
			   null, false, null, null, null, null, false, null, null, null, null);
	}


	@Override
	public String getDescription (Locale locale)
	{
		return "HGT heightfield reader";
	}


	@Override
	public boolean canDecodeInput (Object input)
	{
		return false;
	}
	

	@Override
	public ImageReader createReaderInstance (Object extension)
	{
		return new HGTImageReader (this);
	}

}
