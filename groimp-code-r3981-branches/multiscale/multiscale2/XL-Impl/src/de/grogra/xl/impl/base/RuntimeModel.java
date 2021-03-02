
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

package de.grogra.xl.impl.base;

import java.util.HashMap;

import de.grogra.reflect.Type;
import de.grogra.xl.query.EdgeDirection;
import de.grogra.xl.query.EdgePattern;

public abstract class RuntimeModel implements de.grogra.xl.query.RuntimeModel
{
	/**
	 * The edge bit mask that encodes a successor edge.
	 * <p>
	 * This bit mask represents bit 8, i.e., its value is 256.
	 */
	public static final int SUCCESSOR_EDGE = 0x100;

	/**
	 * The edge bit mask that encodes a branch edge.
	 * <p>
	 * This bit mask represents bit 9, i.e., its value is 512.
	 */
	public static final int BRANCH_EDGE = 0x200;

	/**
	 * The edge bit mask that encodes a containment edge.
	 * <p>
	 * This bit mask represents bit 10, i.e., its value is 1024.
	 */
	public static final int CONTAINMENT_EDGE = 0x400;

	/**
	 * The edge bit mask that encodes an end-of-containment edge.
	 * <p>
	 * This bit mask represents bit 11.
	 */
	public static final int CONTAINMENT_END_EDGE = 0x800;

	/**
	 * The edge bit mask that encodes a refinement edge.
	 * <p>
	 * This bit mask represents bit 12.
	 */
	public static final int REFINEMENT_EDGE = 0x1000;

	/**
	 * The edge bit mask that encodes some general-purpose mark edge.
	 * <p>
	 * This bit mask represents bit 13.
	 */
	public static final int MARK_EDGE = 0x2000;

	/**
	 * The first unused edge bit mask. This and all higher bit masks
	 * can be used freely.
	 * <p>
	 * This bit mask represents bit 14.
	 */
	public static final int MIN_USER_EDGE = 0x4000;


	/**
	 * The edge bit mask that covers the special edge.
	 * <p>
	 * This bit mask represents the bits 0 - 7, i.e., its value is 255.
	 */
	public static final int SPECIAL_MASK = 255;

	public static final int MIN_NORMAL_BIT_INDEX = 8;

	/**
	 * Specifies the base type of nodes of this runtime model. This
	 * should return the same type as
	 * {@link CompiletimeModel#getNodeType}
	 * for the corresponding <code>CompiletimeModel</code> implementation.
	 *  
	 * @return base type of nodes
	 */
	public abstract Class getNodeType ();

	
	/**
	 * Adds a set of edge bits from a <code>source</code> node
	 * to a <code>target</code> node.
	 *  
	 * @param source the source node of the edge
	 * @param target the target node of the edge
	 * @param bits the edge bits that are to be added 
	 */
	public abstract void addEdgeBits (Object source, Object target, int bits);

	
	/**
	 * Return the edge bits between a <code>source</code> node and a
	 * <code>target</code> node. If there is no edge at all, 0 is returned.
	 * 
	 * @param source the source node of the edge
	 * @param target the target node of the edge
	 * @return the edge bits
	 */
	public abstract int getEdgeBits (Object source, Object target);

	
	/**
	 * Creates an iterator over the edges of <code>node</code>. The iterator
	 * is set to the first edge of the node, if any. The direction <code>dir</code>
	 * has to be respected by the iterator.
	 * 
	 * @param node the node whose edges are to be iterated
	 * @param dir direction in which edges are traversed (seen from <code>node</code>)
	 * @return an edge iterator
	 */
	public abstract EdgeIterator createEdgeIterator (Object node, EdgeDirection dir);

	
	public void initialize (String params)
	{
	}

	
	public int getStandardEdgeFor (int edgeType)
	{
		switch (edgeType)
		{
			case EdgePattern.ANY_EDGE:
				return -1;
			case EdgePattern.BRANCH_EDGE:
				return BRANCH_EDGE;
			case EdgePattern.SUCCESSOR_EDGE:
				return SUCCESSOR_EDGE;
			case EdgePattern.REFINEMENT_EDGE:
				return REFINEMENT_EDGE;
			default:
				throw new AssertionError (edgeType);
		}
	}

	
	private static final ThreadLocal<HashMap<Class,Graph>> EXTENTS = new ThreadLocal<HashMap<Class,Graph>> ();
	
	public void setCurrentGraph (Graph extent)
	{
		synchronized (EXTENTS)
		{
			HashMap<Class,Graph> h = EXTENTS.get ();
			if (h == null)
			{
				h = new HashMap<Class,Graph> ();
				EXTENTS.set (h);
			}
			h.put (getClass (), extent);
		}
	}


	public Graph currentGraph ()
	{
		synchronized (EXTENTS)
		{
			HashMap<Class,Graph> h = EXTENTS.get ();
			if (h == null)
			{
				return null;
			}
			return h.get (getClass ());
		}
	}


	public boolean isNode (Object value)
	{
		return (value != null) && getNodeType ().isInstance (value);
	}


	public static final boolean testEdgeBits (int edgeBits, int mask)
	{
		if (mask == -1)
		{
			return edgeBits != 0;
		}
		if (((mask & edgeBits) & ~SPECIAL_MASK) != 0)
		{
			return true;
		}
		switch (mask & SPECIAL_MASK)
		{
			case 0:
				return false;
			case SPECIAL_MASK:
				return (edgeBits & SPECIAL_MASK) != 0;
			default:
				return ((mask ^ edgeBits) & SPECIAL_MASK) == 0;
		}
	}

	public static final int edgeBitsIntersection (int edgeBits, int mask)
	{
		if (mask == -1)
		{
			return edgeBits;
		}
		switch (mask & SPECIAL_MASK)
		{
			case 0:
			case SPECIAL_MASK:
				return edgeBits & mask;
			default:
				return (((mask ^ edgeBits) & SPECIAL_MASK) == 0) ? edgeBits & mask : (edgeBits & mask & ~SPECIAL_MASK);
		}
	}

	public static final int edgeBitsUnion (int a, int b)
	{
		if (((a ^ b) & SPECIAL_MASK) == 0)
		{
			return a | b;
		}
		else if ((b & SPECIAL_MASK) == 0)
		{
			return a | b;
		}
		else
		{
			return (a & ~SPECIAL_MASK) | b;
		}
	}
	
	//multiscale begin
	public static final int edgeBitsRemove(int a, int toBeRemoved)
	{
		return a & ~toBeRemoved;
	}
	//multiscale end

	protected Neighbors copyOut = new Neighbors (this, -1, -1, 0, BRANCH_EDGE, true);
	protected Neighbors copyIn = new Neighbors (this, -1, -1, 0, 0, false);
	//multiscale begin
	protected Neighbors copyOutNoRefine = new Neighbors (this, -1, -1, 0, BRANCH_EDGE, true,true);
	protected Neighbors copyInNoRefine = new Neighbors (this, -1, -1, 0, 0, false,true);
	//multiscale end
	protected Neighbors branchIn = new Neighbors (this, BRANCH_EDGE | SUCCESSOR_EDGE, 0, BRANCH_EDGE, 0, false);

	public boolean isWrapperFor (Object object, Type type)
	{
		return false;
	}

/*!!
#foreach ($type in $types)
$pp.setType($type)

	public $type unwrap$pp.Type (Object wrapper)
	{
		throw new AbstractMethodError ("Method not implemented");
	}
		
#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public boolean unwrapBoolean (Object wrapper)
	{
		throw new AbstractMethodError ("Method not implemented");
	}
		
// generated
// generated
	public byte unwrapByte (Object wrapper)
	{
		throw new AbstractMethodError ("Method not implemented");
	}
		
// generated
// generated
	public short unwrapShort (Object wrapper)
	{
		throw new AbstractMethodError ("Method not implemented");
	}
		
// generated
// generated
	public char unwrapChar (Object wrapper)
	{
		throw new AbstractMethodError ("Method not implemented");
	}
		
// generated
// generated
	public int unwrapInt (Object wrapper)
	{
		throw new AbstractMethodError ("Method not implemented");
	}
		
// generated
// generated
	public long unwrapLong (Object wrapper)
	{
		throw new AbstractMethodError ("Method not implemented");
	}
		
// generated
// generated
	public float unwrapFloat (Object wrapper)
	{
		throw new AbstractMethodError ("Method not implemented");
	}
		
// generated
// generated
	public double unwrapDouble (Object wrapper)
	{
		throw new AbstractMethodError ("Method not implemented");
	}
		
// generated
// generated
	public Object unwrapObject (Object wrapper)
	{
		throw new AbstractMethodError ("Method not implemented");
	}
		
//!! *# End of generated code

}
