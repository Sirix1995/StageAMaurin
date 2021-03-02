
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
import java.util.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.imageio.event.*;
import javax.imageio.stream.*;
import javax.imageio.metadata.IIOMetadata;

public class PPMImageReader extends ImageReader
{
	private final PPMReader reader;
	private static List imageType;


	public PPMImageReader (int minLines)
	{
		super (null);
		if (imageType == null)
		{
			ArrayList list = new ArrayList ();
			BufferedImage i
				= new BufferedImage (1, 1, BufferedImage.TYPE_INT_RGB);
			list.add (new ImageTypeSpecifier (i));
			imageType = list;
		}
		reader = new PPMReader (null, minLines)
		{
			@Override
			protected void readBytes (IIOByteBuffer buf, int len) throws IOException
			{
				((ImageInputStream) PPMImageReader.this.input).readBytes (buf, len);
			}


			@Override
			protected void scanlinesRead (int minY, int h)
			{
				if (updateListeners != null)
				{
					for (Iterator i = updateListeners.iterator (); i.hasNext (); )
					{
						IIOReadUpdateListener l = (IIOReadUpdateListener) i.next ();
						l.imageUpdate (PPMImageReader.this, image,
									   0, minY, width, h, 1, 1, null);
					}	
				}
			}
		};
	}


	@Override
	public int getHeight (int imageIndex) throws IOException
	{
		return reader.height;
	}


	@Override
	public int getWidth (int imageIndex) throws IOException
	{
		return reader.width;
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


	@Override
	public BufferedImage read (int imageIndex, ImageReadParam param)
		throws IOException
	{
		return reader.read ();
	}

}
