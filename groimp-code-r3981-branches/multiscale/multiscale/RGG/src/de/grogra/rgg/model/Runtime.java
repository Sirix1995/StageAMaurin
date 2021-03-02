
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

package de.grogra.rgg.model;

import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.GraphManager;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.imp3d.objects.NURBSSurface;
import de.grogra.pf.ui.Workbench;
import de.grogra.reflect.Type;
import de.grogra.reflect.TypeId;
import de.grogra.rgg.BooleanNode;
import de.grogra.rgg.ByteNode;
import de.grogra.rgg.CharNode;
import de.grogra.rgg.DoubleNode;
import de.grogra.rgg.FloatNode;
import de.grogra.rgg.IntNode;
import de.grogra.rgg.Library;
import de.grogra.rgg.LongNode;
import de.grogra.rgg.ObjectNode;
import de.grogra.rgg.ShortNode;
import de.grogra.util.ThreadContext;
import de.grogra.xl.impl.base.EdgeIterator;
import de.grogra.xl.impl.base.Graph;
import de.grogra.xl.impl.base.RuntimeModel;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.util.ObjectList;

public class Runtime extends RuntimeModel
{
	public static final Runtime INSTANCE = new Runtime ();
	
	private static final int EXTENT = ThreadContext.registerProperty ();



	public Runtime ()
	{
	}

	
	@Override
	public void setCurrentGraph (Graph extent)
	{
		ThreadContext tc = ThreadContext.current ();
		tc.setProperty (EXTENT, extent);
		Workbench w = Workbench.current (tc);
		if (w != null)
		{
			w.setProperty (getClass ().getName (), extent);
		}
	}


	@Override
	public RGGGraph currentGraph ()
	{
		ThreadContext tc = ThreadContext.current ();
		RGGGraph e = (RGGGraph) tc.getProperty (EXTENT);
		Workbench w = Workbench.current (tc);
		if ((e != null) && (w != null) && (e.manager != w.getRegistry ().getProjectGraph ()))
		{
			tc.setProperty (EXTENT, null);
			e = null;
		}
		if ((e == null) && (w != null))
		{
			e = (RGGGraph) w.getProperty (getClass ().getName ());
			tc.setProperty (EXTENT, e);
		}
		return e;
	}

	
	public void setCurrentGraph (GraphManager graph)
	{
		setCurrentGraph (new RGGGraph (this, graph));
	}


	@Override
	public boolean isNode (Object value)
	{
		return value instanceof Node;
	}


	@Override
	public Class getNodeType ()
	{
		return Node.class;
	}


	@Override
	public void addEdgeBits (Object source, Object target, int bits)
	{
		((Node) source).addEdgeBitsTo ((Node) target, bits, null);
	}


	@Override
	public int getEdgeBits (Object source, Object target)
	{
		return ((Node) source).getEdgeBitsTo ((Node) target);
	}


	final ObjectList<Iterator> iterators = new ObjectList<Iterator> ();

	private final class Iterator extends EdgeIterator
	{
		Node node;
		int direction;
		private Edge edge;
		
		void set (Edge e)
		{
			while (true)
			{
				edge = e;
				if (e == null)
				{
					source = null;
					target = null;
					return;
				}
				source = e.getSource ();
				target = e.getTarget ();
				edgeBits = e.getEdgeBits ();
				switch (direction)
				{
					case EdgeDirection.FORWARD_INT:
						if (source == node)
						{
							return;
						}
						break;
					case EdgeDirection.BACKWARD_INT:
						if (target == node)
						{
							return;
						}
						break;
					default:
						return;
				}
				e = e.getNext (node);
			}
		}

		
		
		@Override
		public boolean hasEdge ()
		{
			if (edge != null)
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
		public void moveToNext ()
		{
			set (edge.getNext (node));
		}
		
		
		@Override
		public void dispose ()
		{
			edge = null;
			source = null;
			target = null;
			synchronized (Runtime.this)
			{
				node = null;
				iterators.push (this);
			}
		}
	}


	@Override
	public synchronized EdgeIterator createEdgeIterator (Object node, EdgeDirection dir)
	{
		Iterator i = iterators.isEmpty () ? new Iterator () : iterators.pop ();
		i.node = (Node) node;
		i.direction = dir.getCode ();
		i.set (((Node) node).getFirstEdge ());
		return i;
	}


	public boolean isWrapperFor (Object wrapper, Type type)
	{
		switch (type.getTypeId ())
		{
/*!!
#foreach ($type in $primitives)
$pp.setType($type)
			case TypeId.$pp.TYPE:
				return wrapper instanceof ${pp.Type}Node;
#end
!!*/
//!! #* Start of generated code
// generated
			case TypeId.BOOLEAN:
				return wrapper instanceof BooleanNode;
// generated
			case TypeId.BYTE:
				return wrapper instanceof ByteNode;
// generated
			case TypeId.SHORT:
				return wrapper instanceof ShortNode;
// generated
			case TypeId.CHAR:
				return wrapper instanceof CharNode;
// generated
			case TypeId.INT:
				return wrapper instanceof IntNode;
// generated
			case TypeId.LONG:
				return wrapper instanceof LongNode;
// generated
			case TypeId.FLOAT:
				return wrapper instanceof FloatNode;
// generated
			case TypeId.DOUBLE:
				return wrapper instanceof DoubleNode;
//!! *# End of generated code
			case TypeId.OBJECT:
				return (wrapper instanceof ObjectNode)
					|| (wrapper instanceof NURBSCurve)
					|| (wrapper instanceof NURBSSurface);
			default:
				throw new AssertionError ();
		}
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	public $type unwrap$pp.Type (Object wrapper)
	{
		return Library.${pp.type}Value ((Node) wrapper);
	}

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public boolean unwrapBoolean (Object wrapper)
	{
		return Library.booleanValue ((Node) wrapper);
	}
// generated
// generated
// generated
	public byte unwrapByte (Object wrapper)
	{
		return Library.byteValue ((Node) wrapper);
	}
// generated
// generated
// generated
	public short unwrapShort (Object wrapper)
	{
		return Library.shortValue ((Node) wrapper);
	}
// generated
// generated
// generated
	public char unwrapChar (Object wrapper)
	{
		return Library.charValue ((Node) wrapper);
	}
// generated
// generated
// generated
	public int unwrapInt (Object wrapper)
	{
		return Library.intValue ((Node) wrapper);
	}
// generated
// generated
// generated
	public long unwrapLong (Object wrapper)
	{
		return Library.longValue ((Node) wrapper);
	}
// generated
// generated
// generated
	public float unwrapFloat (Object wrapper)
	{
		return Library.floatValue ((Node) wrapper);
	}
// generated
// generated
// generated
	public double unwrapDouble (Object wrapper)
	{
		return Library.doubleValue ((Node) wrapper);
	}
// generated
// generated
// generated
	public Object unwrapObject (Object wrapper)
	{
		return Library.objectValue ((Node) wrapper);
	}
// generated
//!! *# End of generated code

}
