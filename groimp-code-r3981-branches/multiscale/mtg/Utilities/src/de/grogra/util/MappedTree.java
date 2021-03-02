
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

package de.grogra.util;

public class MappedTree extends MutableTree implements MappedTreeModel
{
	public static class Node extends MutableTree.Node
	{
		final Object source;


		protected Node (Object source)
		{
			this.source = source;
		}


		public Object getSourceNode ()
		{
			return source;
		}


		@Override
		public String toString ()
		{
			return "MappedTree.Node[" + source + ']';
		}
	}


	public MappedTree ()
	{
		super (null);
	}


	public Object createNode (Object sourceNode, Object targetParent)
	{
		return new Node (sourceNode);
	}


	public void disposeNode (Object node)
	{
	}


	public boolean isImage (Object sourceNode, Object targetNode)
	{
		return ((Node) targetNode).source.equals (sourceNode);
	}


	public void treeChanged (Object parent)
	{
	}

}
