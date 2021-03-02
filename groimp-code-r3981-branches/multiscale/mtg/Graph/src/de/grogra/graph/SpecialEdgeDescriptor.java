
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

package de.grogra.graph;

import de.grogra.util.*;

public class SpecialEdgeDescriptor implements Described
{
	private final I18NBundle bundle;
	private final String key;
	private final int bits;
	private final Class nodeClass;

	
	public SpecialEdgeDescriptor (I18NBundle bundle, String key, int bits,
								  Class nodeClass)
	{
		if ((bits & Graph.SPECIAL_EDGE_MASK) == 0)
		{
			throw new IllegalArgumentException
				("Invalid bits " + bits + " for special edge " + key);
		}
		this.bundle = bundle;
		this.key = key;
		this.bits = bits;
		this.nodeClass = nodeClass;
	}

	
	public Object getDescription (String type)
	{
		return Utils.get (bundle, key, type, null);
	}

	
	public int getBits ()
	{
		return bits;
	}
	
	
	public String getKey ()
	{
		return key;
	}
	
	
	public Class getNodeClass ()
	{
		return nodeClass;
	}

	
	public boolean isDeclaredBySource ()
	{
		return (bits & Graph.SPECIAL_EDGE_OF_SOURCE_BIT) != 0;
	}

}
