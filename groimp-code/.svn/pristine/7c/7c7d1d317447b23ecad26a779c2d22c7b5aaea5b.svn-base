
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
import java.io.Writer;

import de.grogra.pf.registry.Registry;
import de.grogra.util.Map;
import de.grogra.util.MimeType;
import de.grogra.util.ModifiableMap;

public class ObjectSourceImpl extends FilterSourceBase implements ObjectSource, WriterSource
{
	private final Object object;
	private final String systemId;


	public ObjectSourceImpl (Object object, String systemId, IOFlavor flavor,
							 Registry r, ModifiableMap metaData)
	{
		super (flavor, r, metaData);
		this.systemId = systemId;
		this.object = object;
	}


	public ObjectSourceImpl (Object object, String systemId, MimeType mimeType,
							 Registry r, ModifiableMap metaData)
	{
		this (object, systemId,
			  new IOFlavor (mimeType, IOFlavor.OBJECT, object.getClass ()),
			  r, metaData);
	}


	public String getSystemId ()
	{
		return systemId;
	}


	public Object getObject ()
	{
		return object;
	}


	public void write (Writer out) throws IOException
	{
		out.write (object.toString ());
	}

}
