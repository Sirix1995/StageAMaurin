
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.*;
import de.grogra.util.MimeType;
import de.grogra.util.StringMap;

public class GZipFilter extends FilterBase implements OutputStreamSource
{
	public GZipFilter (FilterItem item, FilterSource source)
	{
		super (item, source);
		setSystemId (source.getSystemId () + ".gz");
		StringMap m = new de.grogra.util.StringMap ()
			.putObject (MimeType.WRAPPED_TYPE_PARAM,
						source.getFlavor ().getMimeType ().toString ());
		setFlavor (new IOFlavor (new MimeType (MimeType.GZIP.getMediaType (),
								 m), IOFlavor.OUTPUT_STREAM, null));
	}


	public void write (OutputStream out) throws IOException
	{
		((OutputStreamSource) source).write (new GZIPOutputStream (out));
	}

}
