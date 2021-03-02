
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

public class AttributeDependencies
{
	private final Int2ObjectMap graph;
	private Int2ObjectMap dependent;


	public AttributeDependencies ()
	{
		graph = new Int2ObjectMap ();
	}


	public AttributeDependencies (AttributeDependencies superDep)
	{
		graph = superDep.graph.dup ();
		for (int i = graph.size () - 1; i >= 0; i--)
		{
			graph.setValueAt
				(i, ((Int2ObjectMap) graph.getValueAt (i)).dup ());
		}
	}


	public void add (Attribute src, Attribute dest)
	{
		Int2ObjectMap m = (Int2ObjectMap) graph.get (src.getId ());
		if (m == null)
		{
			graph.put (src.getId (), m = new Int2ObjectMap ());
		}
		m.put (src.getId (), src);
		m.put (dest.getId (), dest);
	}


	public void validate ()
	{
		dependent = new Int2ObjectMap (graph.size ());
		Int2ObjectMap list = new Int2ObjectMap ();
		for (int i = graph.size () - 1; i >= 0; i--)
		{
			list.clear ();
			int id = graph.getKeyAt (i);
			buildDep (id, list);
			dependent.put (id, list.getValues (new Attribute[list.size ()]));
		}
	}


	private void buildDep (int id, Int2ObjectMap list)
	{
		Int2ObjectMap m = (Int2ObjectMap) graph.get (id);
		if (m != null)
		{
			for (id = m.size () - 1; id >= 0; id--)
			{
				if (list.put (m.getKeyAt (id), m.getValueAt (id)) == null)
				{
					buildDep (m.getKeyAt (id), list);
				}
			}
		}
	}


	public Attribute[] getDependent (Attribute a)
	{
		Attribute[] as = (Attribute[]) dependent.get (a.getId ());
		return (as != null) ? as : a.toArray ();
	}

}
