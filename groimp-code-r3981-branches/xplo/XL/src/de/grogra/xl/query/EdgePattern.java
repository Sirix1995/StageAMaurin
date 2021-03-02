
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

package de.grogra.xl.query;

import java.io.Serializable;

import de.grogra.reflect.Type;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.XBitSet;

public final class EdgePattern extends BuiltInPattern
{

	/**
	 * Any edge. This <code>int</code>-value represents
	 * an edge of any type within the context of {@link EdgePattern}.
	 */
	public static final int ANY_EDGE = 0;

	/**
	 * Successor edge. This <code>int</code>-value represents
	 * an edge of type successor within the context of
	 * {@link EdgePattern}.
	 */
	public static final int SUCCESSOR_EDGE = 1;

	/**
	 * Branch edge. This <code>int</code>-value represents
	 * an edge of type branch within the context of
	 * {@link EdgePattern}.
	 */
	public static final int BRANCH_EDGE = 2;

	/**
	 * Refinement edge. This <code>int</code>-value represents
	 * an edge of type refinement within the context of
	 * {@link EdgePattern}.
	 */
	public static final int REFINEMENT_EDGE = 3;


	final Serializable edge;
	final int direction;
	final boolean constEdge;


	public EdgePattern (Type nodeType, Type edgeType, Serializable edge, int direction)
	{
		super ((direction == EdgeDirection.BOTH_INT)
			   ? new Type[] {nodeType, nodeType, edgeType, edgeType}
			   : new Type[] {nodeType, nodeType, edgeType},
			   (direction == EdgeDirection.BOTH_INT) ? 4 : 3);
		if (direction == EdgeDirection.BACKWARD_INT)
		{
			throw new IllegalArgumentException ();
		}
		this.edge = edge;
		this.direction = direction;
		this.constEdge = true;
	}


	public EdgePattern (Type nodeType, Type edgeType, int direction)
	{
		super ((direction == EdgeDirection.BOTH_INT)
			   ? new Type[] {nodeType, nodeType, edgeType, edgeType, edgeType}
			   : new Type[] {nodeType, nodeType, edgeType, edgeType},
			   (direction == EdgeDirection.BOTH_INT) ? 5 : 4);
		if (direction == EdgeDirection.BACKWARD_INT)
		{
			throw new IllegalArgumentException ();
		}
		this.edge = null;
		this.direction = direction;
		this.constEdge = false;
	}


	public void write (BytecodeSerialization out) throws java.io.IOException
	{
		out.beginMethod (Utils.getConstructor (this, constEdge ? 4 : 3));
		out.visitType (getParameterType (0));
		out.visitType (getParameterType (2));
		if (constEdge)
		{
			out.visitObject (edge);
		}
		out.visitInt (direction);
		out.endMethod ();
	}


	@Override
	public Matcher createMatcher (Graph src, XBitSet providedConstants,
								  IntList neededConstantsOut)
	{
		boolean forward;
		if (providedConstants.get (0))
		{
			forward = true;
		}
		else if (providedConstants.get (1))
		{
			forward = false;
		}
		else
		{
			forward = true;
			neededConstantsOut.add (0);
		}
		if (!(constEdge || providedConstants.get (2)))
		{
			neededConstantsOut.add (2);
		}
		final int from = forward ? 0 : 1, to = forward ? 1 : 0;
		final EdgeDirection dir = (direction == EdgeDirection.UNDIRECTED_INT) ? EdgeDirection.UNDIRECTED
			: (direction == EdgeDirection.BOTH_INT) ? EdgeDirection.BOTH
			: forward ? EdgeDirection.FORWARD : EdgeDirection.BACKWARD;
		final Type edgeType = getParameterType (2);
		final int patternIndex = constEdge ? -1 : 2;
		final int matchIndex = constEdge ? 2 : 3;

		if (!src.canEnumerateEdges (dir, constEdge, edge))
		{
			return null;
		}
		return new Matcher (2)
		{
			@Override
			public void findMatches
				(QueryState qs, MatchConsumer consumer, int arg)
			{
				Object n = qs.abound (from);
				if (!qs.model.isNode (n))
				{
					return;
				}
				qs.graph.enumerateEdges
					(n, dir, edgeType, qs, to,
					 patternIndex, edge, matchIndex, consumer, arg);
			}


			@Override
			public void visitMatch (QueryState qs, Producer prod)
			{
				if (qs.isNull (matchIndex))
				{
					return;
				}
				prod.producer$visitEdge (EdgePattern.this);
 			}

		};
	}

	public int getPatternIndex ()
	{
		return constEdge ? -1 : 2;
	}

	public int getMatchIndex ()
	{
		return constEdge ? 2 : 3;
	}
	
	public Serializable getPattern ()
	{
		return edge;
	}

	public boolean needsBothDirections ()
	{
		return direction == EdgeDirection.BOTH_INT;
	}

	@Override
	public int getParameterKind (int index)
	{
		return 0;
	}
	

	@Override
	public boolean isDeleting ()
	{
		return true;
	}

	
	@Override
	public String paramString ()
	{
		return edge + "," + direction;
	}
}
