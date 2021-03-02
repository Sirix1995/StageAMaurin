
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
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

package de.grogra.xl.impl.simple;

import de.grogra.xl.impl.base.EdgeIterator;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.util.ObjectList;

public class RuntimeModel extends de.grogra.xl.impl.base.RuntimeModel
{

	@Override
	public Class getNodeType ()
	{
		return Node.class;
	}


	@Override
	public void addEdgeBits (Object source, Object target, int bits)
	{
		((Node) source).addEdgeBitsTo ((Node) target, bits);
	}


	@Override
	public int getEdgeBits (Object source, Object target)
	{
		return ((Node) source).getEdgeBitsTo ((Node) target);
	}


	final ObjectList<Iterator> iterators = new ObjectList<Iterator> ();

	private final class Iterator extends EdgeIterator
	{
		private Node node;
		private int firstOut;
		private int index;
		private ObjectList<Node> neighbors = new ObjectList<Node> ();
		
		void set (Node node, EdgeDirection dir)
		{
			this.node = node;
			if (dir.contains (EdgeDirection.BACKWARD))
			{
				node.getAdjacentNodes (neighbors, false);
			}
			firstOut = neighbors.size ();
			if (dir.contains (EdgeDirection.FORWARD))
			{
				node.getAdjacentNodes (neighbors, true);
			}
			index = -1;
			moveToNext ();
		}

		@Override
		public void moveToNext ()
		{
			if (++index >= neighbors.size ())
			{
				return;
			}
			Node n = neighbors.get (index);
			if (index < firstOut)
			{
				source = n;
				target = node;
				edgeBits = n.getEdgeBitsTo (node);
			}
			else
			{
				source = node;
				target = n;
				edgeBits = node.getEdgeBitsTo (n);
			}
		}
		
		
		@Override
		public boolean hasEdge ()
		{
			if (index < neighbors.size ())
			{
				return true;
			}
			else
			{
				dispose ();
				return false;
			}
		}
		
		
		@Override
		public void dispose ()
		{
			if (node != null)
			{
				iterators.push (this);
				neighbors.clear ();
				source = null;
				target = null;
				node = null;
			}
		}
	}

	@Override
	public EdgeIterator createEdgeIterator (Object node, EdgeDirection dir)
	{
		Iterator i = iterators.isEmpty () ? new Iterator () : (Iterator) iterators.pop ();
		i.set ((Node) node, dir);
		return i;
	}

}
