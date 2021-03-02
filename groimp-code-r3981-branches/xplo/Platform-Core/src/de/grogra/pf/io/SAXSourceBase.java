
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

import org.xml.sax.SAXNotRecognizedException;

import de.grogra.pf.registry.Registry;
import de.grogra.util.ModifiableMap;

public abstract class SAXSourceBase extends FilterSourceBase
	implements SAXSource
{
	protected boolean provideNamespaces = true;
	protected boolean providePrefixes = false;


	public SAXSourceBase (IOFlavor flavor, Registry reg,
						  ModifiableMap metaData)
	{
		super (flavor, reg, metaData);
	}


	public boolean getFeature (String name)
		throws SAXNotRecognizedException
	{
		if (NAMESPACES.equals (name))
		{
			return provideNamespaces;
		}
		else if (NAMESPACE_PREFIXES.equals (name))
		{
			return providePrefixes;
		}
		else
		{
			throw new SAXNotRecognizedException (name);
		}
	}


	public void setFeature (String name, boolean value)
		throws SAXNotRecognizedException
	{
		if (NAMESPACES.equals (name))
		{
			provideNamespaces = value;
		}
		else if (NAMESPACE_PREFIXES.equals (name))
		{
			providePrefixes = value;
		}
		else
		{
			throw new SAXNotRecognizedException (name);
		}
	}

}
