
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

import java.io.*;
import java.awt.image.*;
import javax.imageio.stream.*;

public class PPMReader
{
	public static final MimeType MIME_TYPE
		= new MimeType (PPMImageReaderSpi.MIME_TYPE, null);

	int width = -1, height;
	BufferedImage image;

	private final int minLines;

	private boolean use16bit;
	private int factor, linesRead = 0, buffer = -2;

	private IIOByteBuffer buf = new IIOByteBuffer (new byte[2048], 0, 2048);
	private int bufPos = 0, bufEnd = 0;

	private final InputStream input;


	public PPMReader (InputStream input, int minLines)
	{
		this.input = input;
		this.minLines = minLines;
	}


	public static String getDescription ()
	{
		return Utils.I18N.msg ("ppmimagereader.description");
	}


	private int readByte () throws IOException
	{
		int b;
		if (buffer != -2)
		{
			b = buffer;
			buffer = -2;
		}
		else
		{
			if (bufPos == bufEnd)
			{
				readBytes (buf, 2048);
				bufEnd = (bufPos = buf.getOffset ()) + buf.getLength ();
			}
			b = (bufPos >= bufEnd) ? -1 : buf.getData ()[bufPos++] & 255;
		}
		if (b < 0)
		{
			throw new IOException ();
		}
		return b;
	}


	protected void readBytes (IIOByteBuffer buf, int len) throws IOException
	{
		buf.setOffset (0);
		buf.setLength (input.read (buf.getData (), 0, len));
	}


	private int read (boolean skipWhitespace) throws IOException
	{
		while (true)
		{
			int b = readByte ();
			if (skipWhitespace)
			{
				if (b == '#')
				{
					while ((b != '\n') && (b != '\r'))
					{
						b = readByte ();
					}
					continue;
				}
				else if (Character.isWhitespace ((char) b))
				{
					continue;
				}
			}
			return b;
		}
	}


	private int readInt () throws IOException
	{
		int b = read (true), i = 0;
		while (!Character.isWhitespace ((char) b))
		{
			if ((b < '0') || (b > '9'))
			{
				throw new IOException ();
			}
			i = i * 10 + (b - '0');
			b = read (false);
		}
		return i;
	}


	private int readPixel () throws IOException
	{
		int b, i, p = 255;
		for (i = 0; i < 3; i++)
		{
			b = read (false);
			if (b < 0)
			{
				throw new IOException ();
			}
			if (use16bit)
			{
				int lsb = read (false);
				if (lsb < 0)
				{
					throw new IOException ();
				}
				b = (b << 8) + lsb;
			}
			p = (p << 8) + (b * factor >>> 8);
		}
		return p;
	}


	private void scanlinesRead (int h)
	{
		scanlinesRead (linesRead, h - linesRead);
		linesRead = h;
	}


	protected void scanlinesRead (int minY, int height)
	{
	}


	public BufferedImage read () throws IOException
	{
		if ((read (true) != 'P') || (read (false) != '6'))
		{
			throw new IOException ();
		}
		width = readInt ();
		height = readInt ();
		image = new BufferedImage (width, height, BufferedImage.TYPE_INT_RGB);
		int maxValue = readInt ();
		use16bit = maxValue > 255;
		factor = 0x10000 / maxValue;
		int[] data = new int[width];
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				data[x] = readPixel ();
			}
			image.setRGB (0, y, width, 1, data, 0, 1);
			if (y + 1 - linesRead >= minLines)
			{
				scanlinesRead (y + 1);
			}
		}
		if (linesRead < height)
		{
			scanlinesRead (height);
		}
		return image;
	}

}
