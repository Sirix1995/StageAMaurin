
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

import java.util.*;

public class ObjectMapImpl<V> extends ObjectMap<V>
{
	private final HashMap<Object,V> nodeMap = new HashMap<Object,V> ();
	private final HashMap<Object,V> edgeMap = new HashMap<Object,V> ();


	@Override
	public synchronized V putObject (Object object, boolean asNode, V value)
	{
		return (asNode ? nodeMap : edgeMap).put (object, value);
	}


	@Override
	public synchronized V getObject (Object object, boolean asNode)
	{
		return (asNode ? nodeMap : edgeMap).get (object);
	}
	

	public synchronized void dispose ()
	{
		nodeMap.clear ();
		edgeMap.clear ();
	}
	
	
	public HashMap getNodeMap ()
	{
		return nodeMap;
	}

	
	public HashMap getEdgeMap ()
	{
		return edgeMap;
	}

}
