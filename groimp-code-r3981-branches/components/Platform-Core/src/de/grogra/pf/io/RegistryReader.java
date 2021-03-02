
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

import java.io.*;

import de.grogra.util.IOWrapException;
import de.grogra.pf.registry.*;

public class RegistryReader extends FilterBase
	implements ObjectSource, RegistryLoader
{
	public RegistryReader (FilterItem item, FilterSource source)
	{
		super (item, source);
		setFlavor (IOFlavor.REGISTRY_LOADER);
	}


	public Object getObject () throws IOException
	{
		return this;
	}


	public void loadRegistry (Registry r) throws IOException
	{
		try
		{
			((SAXSource) source).parse (r.createXMLReader (),
										null, null, null, null);
		}
		catch (org.xml.sax.SAXException e)
		{
			throw new IOWrapException (e);
		}
	}

}
