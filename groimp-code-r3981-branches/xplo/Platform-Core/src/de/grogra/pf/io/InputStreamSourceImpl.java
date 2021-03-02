
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

import java.io.InputStream;

import de.grogra.util.MimeType;
import de.grogra.util.ModifiableMap;

public class InputStreamSourceImpl extends FilterSourceBase
	implements InputStreamSource
{
	private final InputStream in;
	private final String systemId;


	public InputStreamSourceImpl (InputStream in, String systemId,
								  MimeType mimeType,
								  de.grogra.pf.registry.Registry r,
								  ModifiableMap metaData)
	{
		super (new IOFlavor (mimeType, IOFlavor.INPUT_STREAM, null), r, metaData);
		this.in = in;
		this.systemId = systemId;
	}


	public String getSystemId ()
	{
		return systemId;
	}


	public InputStream getInputStream ()
	{
		return in;
	}


	public long length ()
	{
		return -1;
	}

}
