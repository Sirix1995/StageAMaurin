
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

package de.grogra.graph.impl;

import java.io.Serializable;

import de.grogra.graph.*;
import de.grogra.persistence.*;
import de.grogra.util.Utils;
import org.xml.sax.SAXException;

public abstract class Edge implements Serializable
{
	private static final int USED_EDGE_MASK = Graph.MIN_UNUSED_EDGE - 1;


	Edge next = null, targetNext = null;
	int edgeBits = 0;

	transient int bitMarks = 0;
	transient Object[] marks = Utils.OBJECT_0;


	Edge ()
	{
		super ();
	}


	public final int getEdgeBits ()
	{
		return edgeBits;
	}


	public final boolean testEdgeBits (int mask)
	{
		return (mask == -1) ? (edgeBits != 0)
			: ((((mask & edgeBits) & ~Graph.SPECIAL_EDGE_MASK) != 0)
			   || (((mask & Graph.SPECIAL_EDGE_MASK) != 0)
				   && (((mask ^ edgeBits) & Graph.SPECIAL_EDGE_MASK) == 0)));
	}


	public final void addEdgeBits (int mask, Transaction xa)
	{
		addEdgeBits (mask, xa, true);
	}

	final void addEdgeBits (int mask, Transaction xa, boolean notify)
	{
		Node s = getSource (), t = getTarget ();
		PersistenceManager ps = s.getPersistenceManager ();
		PersistenceManager pt = t.getPersistenceManager ();
		if (ps != pt)
		{
			if (ps == null)
			{
				pt.makePersistent (s, -1L, xa);
				ps = pt;
			}
			else if (pt == null)
			{
				ps.makePersistent (t, -1L, xa);
			}
			else
			{
				throw new GraphException
					("Can't add edges between nodes of different "
					 + "persistence extents.");
			}
		}
		int e;
		if (s.id < t.id)
		{
			synchronized (s)
			{
				synchronized (t)
				{
					edgeBits = (e = edgeBits) | mask;
				}
			}
		}
		else if (t.id < 0)
		{
			edgeBits = (e = edgeBits) | mask;
		}
		else
		{
			synchronized (t)
			{
				synchronized (s)
				{
					edgeBits = (e = edgeBits) | mask;
				}
			}
		}
		if ((ps != null) && Transaction.isNotApplying (xa))
		{
			((GraphTransaction) xa.makeActive ())
				.logAddEdgeBits (s, t, mask & ~e, mask);
		}
		if (notify)
		{
			s.edgeChanged (this, e, xa);
			t.edgeChanged (this, e, xa);
		}
	}


	public final void setEdgeBits (int bits, Transaction xa)
	{
		int b = edgeBits;
		if ((bits & ~b) != 0)
		{
			addEdgeBits (bits & ~b, xa);
		}
		if ((b & ~bits) != 0)
		{
			removeEdgeBits (b & ~bits, xa);
		}
	}

	
	public final void removeEdgeBits (int mask, Transaction xa)
	{
		Node s = getSource (), t = getTarget ();
		int e;
		if (s.id < t.id)
		{
			synchronized (s)
			{
				synchronized (t)
				{
					edgeBits = (e = edgeBits) & ~mask;
				}
			}
		}
		else if (t.id < 0)
		{
			edgeBits = (e = edgeBits) & ~mask;
		}
		else
		{
			synchronized (t)
			{
				synchronized (s)
				{
					edgeBits = (e = edgeBits) & ~mask;
				}
			}
		}
		if (xa != null)
		{
			PersistenceManager ps = s.getPersistenceManager ();
			if ((ps != null) && (t.getPersistenceManager () == ps)
				&& Transaction.isNotApplying (xa))
			{
				((GraphTransaction) xa.makeActive ())
					.logRemoveEdgeBits (s, t, mask & e, mask);
			}
		}
		/*else*/ if (edgeBits == 0)
		{
			remove (null);
		}
		s.edgeChanged (this, e, xa);
		t.edgeChanged (this, e, xa);
	}


	public final void remove (Transaction xa)
	{
		remove (xa, true);
	}

	final void remove (Transaction xa, boolean notify)
	{
		Node s = getSource (), t = getTarget ();
		int e;
		if (s.id < t.id)
		{
			synchronized (s)
			{
				synchronized (t)
				{
					s.removeEdge (this);
					t.removeEdge (this);
					e = edgeBits;
					edgeBits = 0;
				}
			}
		}
		else if (t.id < 0)
		{
			s.removeEdge (this);
			t.removeEdge (this);
			e = edgeBits;
			edgeBits = 0;
		}
		else
		{
			synchronized (t)
			{
				synchronized (s)
				{
					s.removeEdge (this);
					t.removeEdge (this);
					e = edgeBits;
					edgeBits = 0;
				}
			}
		}
		if (e != 0)
		{
			if (xa != null)
			{
				PersistenceManager ps = s.getPersistenceManager ();
				if ((ps != null) && (t.getPersistenceManager () == ps)
					&& Transaction.isNotApplying (xa))
				{
					((GraphTransaction) xa.makeActive ())
						.logRemoveEdgeBits (s, t, e, -1);
				}
			}
			if (notify)
			{
				s.edgeChanged (this, e, xa);
				t.edgeChanged (this, e, xa);
			}
		}
	}


	final void setNext (Edge next, Node parent)
	{
		if (parent == getSource ())
		{
			this.next = next;
		}
		else
		{
			this.targetNext = next;
		}
	}


	void resetMarks ()
	{
		bitMarks = 0;
		marks = Utils.OBJECT_0;
	}

	
	final boolean getGCMark ()
	{
		return (bitMarks & GraphManager.GC_BITMARK_HANDLE) != 0;
	}


	final synchronized boolean setGCMark (boolean mark)
	{
		if (mark)
		{
			if ((bitMarks & GraphManager.GC_BITMARK_HANDLE) != 0)
			{
				return true;
			}
			bitMarks |= GraphManager.GC_BITMARK_HANDLE;
			return false;
		}
		else
		{
			if ((bitMarks & GraphManager.GC_BITMARK_HANDLE) != 0)
			{
				bitMarks &= ~GraphManager.GC_BITMARK_HANDLE;
				return true;
			}
			return false;
		}
	}


	public final synchronized boolean setBitMark (int handle, boolean mark)
	{
		if (mark)
		{
			getGraph ().bitMarkSet (this, handle);
		}
		if ((handle > 0) || (handle == 1 << 31))
		{
			if (mark)
			{
				if ((bitMarks & handle) != 0)
				{
					return true;
				}
				bitMarks |= handle;
				return false;
			}
			else
			{
				if ((bitMarks & handle) != 0)
				{
					bitMarks &= ~handle;
					return true;
				}
				return false;
			}
		}
		else
		{
			int index = -handle >> 3;
			handle = 1 << (-handle & 7);
			Object[] m = marks;
			byte b = (index >= m.length) ? 0 : (m[index] == null) ? 0
				: ((Byte) m[index]).byteValue ();
			if (mark)
			{
				setObjectMark (index, Byte.valueOf ((byte) (b | handle)));
			}
			else
			{
				byte c = (byte) (b & ~handle);
				setObjectMark (index, (c == 0) ? null : Byte.valueOf (c));
			}
			return (b & handle) != 0;
		}
	}


	public final synchronized boolean getBitMark (int handle)
	{
		if ((handle > 0) || (handle == 1 << 31))
		{
			return (bitMarks & handle) != 0;
		}
		else
		{
			int index = -handle >> 3;
			handle = 1 << (-handle & 7);
			Object[] m = marks;
			Byte o = (index >= m.length) ? null : (Byte) m[index];
			return (o == null) ? false : (o.byteValue () & handle) != 0;
		}
	}


	public final synchronized Object setObjectMark (int handle, Object object)
	{
		if (object != null)
		{
			getGraph ().objectMarkSet (this, handle);
		}
		int l;
		if (handle >= (l = marks.length))
		{
			System.arraycopy (marks, 0, marks = new Object[handle + 4], 0, l);
		}
		Object o = marks[handle];
		marks[handle] = object;
		return o;
	}


	public final synchronized Object getObjectMark (int handle)
	{
		Object[] m = marks;
		return (handle >= m.length) ? null : m[handle];
	}
	
	
	public SpecialEdgeDescriptor getSpecialEdgeDescriptor ()
	{
		if ((edgeBits & Graph.SPECIAL_EDGE_MASK) == 0)
		{
			throw new RuntimeException ();
		}
		return (((edgeBits & Graph.SPECIAL_EDGE_OF_SOURCE_BIT) != 0) ? getSource () : getTarget ())
			.getNType ().getSpecialEdgeDescriptor (edgeBits & Graph.SPECIAL_EDGE_MASK);
	}

	
	public static int parseEdgeKeys (String keys, Node source, Node target) throws SAXException
	{
		int bits = 0;
		int i = 0, len = keys.length ();
		while (i <= len)
		{
			int c = keys.indexOf (',', i);
			char ch;
			if (c < 0)
			{
				c = len;
			}
			if (c == i)
			{
				throw new SAXException (keys);
			}
			else if (c == i + 1)
			{
				switch (keys.charAt (i))
				{
					case '-':
					case '>':
						bits |= Graph.SUCCESSOR_EDGE;
						break;
					case '+':
						bits |= Graph.BRANCH_EDGE;
						break;
					case '{':
						bits |= Graph.CONTAINMENT_EDGE;
						break;
					case '}':
						bits |= Graph.CONTAINMENT_END_EDGE;
						break;
					case '/':
						bits |= Graph.REFINEMENT_EDGE;
						break;
					case '#':
						bits |= Graph.MARK_EDGE;
						break;
					case '@':
						bits |= Graph.NOTIFIES_EDGE;
						break;
					case 'A':
						bits |= Graph.STD_EDGE_5;
						break;
					case 'B':
						bits |= Graph.STD_EDGE_6;
						break;
					case '0':
						bits |= 1 << Graph.MIN_UNUSED_EDGE_BIT;
						break;
					case '1':
						bits |= 2 << Graph.MIN_UNUSED_EDGE_BIT;
						break;
					case '2':
						bits |= 4 << Graph.MIN_UNUSED_EDGE_BIT;
						break;
					case '3':
						bits |= 8 << Graph.MIN_UNUSED_EDGE_BIT;
						break;
					case '4':
						bits |= 16 << Graph.MIN_UNUSED_EDGE_BIT;
						break;
					case '5':
						bits |= 32 << Graph.MIN_UNUSED_EDGE_BIT;
						break;
					case '6':
						bits |= 64 << Graph.MIN_UNUSED_EDGE_BIT;
						break;
					case '7':
						bits |= 128 << Graph.MIN_UNUSED_EDGE_BIT;
						break;
					case '8':
						bits |= 256 << Graph.MIN_UNUSED_EDGE_BIT;
						break;
					case '9':
						bits |= 512 << Graph.MIN_UNUSED_EDGE_BIT;
						break;
					default:
						throw new SAXException (keys);
				}
			}
			else if (((ch = keys.charAt (i)) == '0')
					 && (keys.charAt (i + 1) == 'x'))
			{
				bits = (bits & ~Graph.SPECIAL_EDGE_MASK) | Integer.parseInt (keys.substring (i + 2, c), 16);
			}
			else if ((ch >= '0') && (ch <= '9'))
			{
				bits |= Integer.parseInt (keys.substring (i, c)) << Graph.MIN_UNUSED_EDGE_BIT;
			}
			else
			{
				SpecialEdgeDescriptor sd = (ch == '*')
					? target.getNType ().getSpecialEdgeDescriptor (keys.substring (i + 1, c))
					: source.getNType ().getSpecialEdgeDescriptor (keys.substring (i, c));
				if (sd == null)
				{
				//	throw new SAXException (keys);
				}
				else
				{
					bits = (bits & ~Graph.SPECIAL_EDGE_MASK) | sd.getBits ();
				}
			}
			i = c + 1;
		}
		return bits;
	}

	
	void getEdgeKeys (StringBuffer out, boolean successorAsMinus, boolean useDescription)
	{
		int b = edgeBits;
		boolean comma = false;
		if ((b & Graph.SPECIAL_EDGE_MASK) != 0)
		{
			SpecialEdgeDescriptor d = getSpecialEdgeDescriptor ();
			if (d != null)
			{
				if (useDescription)
				{
					out.append (d.getDescription (de.grogra.util.Described.NAME));
				}
				else
				{
					(d.isDeclaredBySource () ? out : out.append ('*'))
						.append (d.getKey ());
				}
			}
			else
			{
				out.append ("0x").append (Integer.toHexString (b & Graph.SPECIAL_EDGE_MASK));
			}
			comma = true;
		}
		b &= ~Graph.SPECIAL_EDGE_MASK;
		for (int i = Graph.MIN_NORMAL_BIT_INDEX; (b != 0) && (i < 32); i++)
		{
			int mask = 1 << i;
			if ((b & mask) != 0)
			{
				b &= ~mask;
				if (comma)
				{
					out.append (',');
				}
				else
				{
					comma = true;
				}
				switch (mask)
				{
					case Graph.SUCCESSOR_EDGE:
						out.append (successorAsMinus ? '-' : '>');
						break;
					case Graph.BRANCH_EDGE:
						out.append ('+');
						break;
					case Graph.CONTAINMENT_EDGE:
						out.append ('{');
						break;
					case Graph.CONTAINMENT_END_EDGE:
						out.append ('}');
						break;
					case Graph.REFINEMENT_EDGE:
						out.append ('/');
						break;
					case Graph.MARK_EDGE:
						out.append ('#');
						break;
					case Graph.NOTIFIES_EDGE:
						out.append ('@');
						break;
					case Graph.STD_EDGE_5:
						out.append ('A');
						break;
					case Graph.STD_EDGE_6:
						out.append ('B');
						break;
					default:
						if ((mask & USED_EDGE_MASK) != 0)
						{
							throw new RuntimeException ();
						}
						out.append (i - Graph.MIN_UNUSED_EDGE_BIT);
						break;
				}
			}
		}
	}


	abstract GraphManager getGraph ();

	public abstract Node getSource ();

	public abstract Node getTarget ();

	public abstract boolean isSource (Node node);

	public abstract boolean isTarget (Node node);

	public abstract boolean isDirection (Node source, Node target);

	public abstract Node getNeighbor (Node start);

	public abstract Edge getNext (Node parent);

}
