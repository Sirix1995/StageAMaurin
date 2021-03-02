
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

package de.grogra.pf.io;

import de.grogra.util.MimeType;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.*;

public class GUnzipFilter extends FilterBase implements InputStreamSource
{
	public GUnzipFilter (FilterItem item, FilterSource source)
	{
		super (item, source);
		String s = source.getSystemId ();
		String t = source.getFlavor ().getMimeType ()
			.getParameter (MimeType.WRAPPED_TYPE_PARAM);
		MimeType m = (t == null) ? null : new MimeType (t);
		if (s.endsWith (".gz"))
		{
			s = s.substring (0, s.length () - 3);
			if (m == null)
			{
				m = IO.getMimeType (s);
			}
		}
		setFlavor (new IOFlavor ((m == null) ? MimeType.OCTET_STREAM : m,
								 IOFlavor.INPUT_STREAM, null));
		setSystemId (s);
	}


	public InputStream getInputStream () throws IOException
	{
		return new GZIPInputStream (((InputStreamSource) source)
									.getInputStream ());
	}


	public long length ()
	{
		return -1;
	}

}
