/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus This
 * program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */

package de.grogra.imp.io;

import java.io.*;
import java.awt.image.*;
import java.util.*;
import javax.imageio.spi.*;
import javax.imageio.stream.*;
import de.grogra.util.*;
import de.grogra.pf.registry.*;
import de.grogra.pf.io.*;

public class ImageWriter extends FilterBase implements OutputStreamSource
{
	public static final IOFlavor RENDERED_IMAGE_FLAVOR = IOFlavor
		.valueOf (RenderedImage.class);

	public ImageWriter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (item.getOutputFlavor ());
	}

	public void write (OutputStream out) throws IOException
	{
		RenderedImage img = (RenderedImage) ((ObjectSource) source)
			.getObject ();
		while (true)
		{
			Iterator<javax.imageio.ImageWriter> i = javax.imageio.ImageIO
				.getImageWritersByMIMEType (getFlavor ().getMimeType ()
					.getMediaType ());
			if (!i.hasNext ())
			{
				throw new IOException ("Unsupported image MIME-type "
					+ getFlavor ().getMimeType ());
			}
			while (i.hasNext ())
			{
				javax.imageio.ImageWriter w = i.next ();
				if (w.getOriginatingProvider ().canEncodeImage (img))
				{
					ImageOutputStream iout = javax.imageio.ImageIO
						.createImageOutputStream (out);
					w.setOutput (iout);
					w.write (img);
					iout.flush ();
					w.dispose ();
					return;
				}
			}
			if (!(img instanceof BufferedImage))
			{
				break;
			}
			BufferedImage b = (BufferedImage) img;
			int type = ((b.getType () != BufferedImage.TYPE_INT_ARGB) && b
				.getColorModel ().hasAlpha ()) ? BufferedImage.TYPE_INT_ARGB
					: BufferedImage.TYPE_INT_RGB;
			if (b.getType () == type)
			{
				break;
			}
			BufferedImage b2 = new BufferedImage (b.getWidth (),
				b.getHeight (), type);
			b2.getGraphics ().drawImage (b, 0, 0, null);
			img = b2;
		}
		throw new IOException ("Image type not supported by image writer.");
	}
}
