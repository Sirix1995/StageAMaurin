
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

import java.io.*;
import java.util.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.imageio.metadata.IIOMetadata;

import de.grogra.xl.util.IntList;

public class HGTImageReader extends ImageReader
{
	private static List imageType;
	private int width = -1;
	private int height = -1;


	public HGTImageReader (HGTImageReaderSpi spi)
	{
		super (spi);
		if (imageType == null)
		{
			ArrayList list = new ArrayList ();
			BufferedImage i
				= new BufferedImage (1, 1, BufferedImage.TYPE_USHORT_GRAY);
			list.add (new ImageTypeSpecifier (i));
			imageType = list;
		}
	}


	public static String getDescription ()
	{
		return "HGTFILTER";//Utils.I18N.msg ("ppmimagereader.description");
	}


	@Override
	public int getHeight (int imageIndex) throws IOException
	{
		return height;
	}


	@Override
	public int getWidth (int imageIndex) throws IOException
	{
		return width;
	}


	@Override
	public int getNumImages (boolean allowSearch) throws IOException
	{
		return 1;
	}


	@Override
	public Iterator getImageTypes (int imageIndex)
	{
		return imageType.iterator ();
	}


	@Override
	public IIOMetadata getStreamMetadata ()
	{
		return null;
	}


	@Override
	public IIOMetadata getImageMetadata (int imageIndex)
	{
		return null;
	}


	private IIOByteBuffer buf = new IIOByteBuffer (new byte[0x4000], 0, 0x4000);
	private int bufPos = 0, bufEnd = 0;

	private int readByte () throws IOException
	{
		if (bufPos == bufEnd)
		{
			buf.setOffset (0);
			buf.setLength (((ImageInputStream) input).read (buf.getData (), 0, 0x4000));
			bufEnd = (bufPos = buf.getOffset ()) + buf.getLength ();
		}
		return (bufPos >= bufEnd) ? -1 : buf.getData ()[bufPos++] & 255;
	}


	@Override
	public BufferedImage read (int imageIndex, ImageReadParam param)
		throws IOException
	{
		short[] values = new short[1201 * 1201];
		int n = 0;
		IntList invalid = new IntList ();
		while (true)
		{
			int a = readByte ();
			if (a < 0)
			{
				break;
			}
			short h = (short) readByte ();
			if (h < 0)
			{
				// TODO
			}
			if (n == values.length)
			{
				System.arraycopy (values, 0,
								  values = new short[2 * n], 0,
								  n);
			}
			h += a << 8;
			if (h == Short.MIN_VALUE)
			{
				invalid.add (n);
			}
			values[n++] = h;
		}
		
		width = (int) Math.sqrt (n);
		height = n / width;

		int invSize = invalid.size ();
		while (!invalid.isEmpty ())
		{
			int index = invalid.pop ();
			int y = index / width, x = index - y * width;
			int d = 1;
		searchValid:
			while (true)
			{
				int ax = Math.max (x - d, 0);
				int bx = Math.min (x + d, width - 1);
				int ay = Math.max (y - d, 0);
				int by = Math.min (y + d, height - 1);
				for (int i = ay; i <= by; i++)
				{
					for (int j = ax; j <= bx; j++)
					{
						short c;
						if ((c = values[i * width + j]) != Short.MIN_VALUE)
						{
							values[index] = c;
							break searchValid;
						}
					}
				}
				d++;
			}
		}

		BufferedImage image = new BufferedImage
			(width, height, BufferedImage.TYPE_USHORT_GRAY);
		int[] line = new int[width];
		for (int y = 0; y < height; y++)
		{
			for (int x = width - 1; x >= 0; x--)
			{
				line[x] = values[y * width + x];
			}
			image.getRaster ().setPixels (0, y, width, 1, line);
		}
		return image;
	}

}
