
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

import java.io.Reader;

import de.grogra.pf.registry.Registry;
import de.grogra.util.MimeType;
import de.grogra.util.ModifiableMap;

/**
 * A simple implementation of {@link ReaderSource} which wraps an existing
 * <code>Reader</code>.
 * 
 * @author Ole Kniemeyer
 */
public class ReaderSourceImpl extends FilterSourceBase
	implements ReaderSource
{
	private final Reader in;
	private final String systemId;


	/**
	 * Creates a new instance.
	 * 
	 * @param in the reader to be wrapped by the new instance 
	 * @param systemId the system id for the reader
	 * @param mimeType the MIME type of the data
	 * @param r registry which defines the context within which the new instance will be used 
	 * @param metaData some meta data, may be <code>null</code>
	 */
	public ReaderSourceImpl (Reader in, String systemId,
							 MimeType mimeType,
							 Registry r,
							 ModifiableMap metaData)
	{
		super (new IOFlavor (mimeType, IOFlavor.READER, null), r, metaData);
		this.in = in;
		this.systemId = systemId;
	}


	public String getSystemId ()
	{
		return systemId;
	}


	public Reader getReader ()
	{
		return in;
	}

}
