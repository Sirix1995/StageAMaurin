
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

public class EdgeDirection
{
	/**
	 * <code>int</code>-code for {@link #FORWARD}.
	 */
	public static final int FORWARD_INT = 0;

	/**
	 * <code>int</code>-code for {@link #BACKWARD}.
	 */
	public static final int BACKWARD_INT = 1;

	/**
	 * <code>int</code>-code for {@link #UNDIRECTED}.
	 */
	public static final int UNDIRECTED_INT = 2;

	/**
	 * <code>int</code>-code for {@link #BOTH}.
	 */
	public static final int BOTH_INT = 3;


	public static final class Undirected extends EdgeDirection
	{
		private Undirected ()
		{
			super (UNDIRECTED_INT);
		}
	}


	public static final class Both extends EdgeDirection
	{
		private Both ()
		{
			super (BOTH_INT);
		}
	}


	public static abstract class Directed extends EdgeDirection
	{
		private Directed (int value)
		{
			super (value);
		}
	}


	public static final class Forward extends Directed
	{
		private Forward ()
		{
			super (FORWARD_INT);
		}
	}


	public static final class Backward extends Directed
	{
		private Backward ()
		{
			super (BACKWARD_INT);
		}
	}


	/**
	 * Forward traversal direction. This
	 * represents a predicate traversal direction from the in-parameter to
	 * the out-parameter. 
	 */
	public static final Forward FORWARD = new Forward ();

	/**
	 * Backward traversal direction. This
	 * represents a predicate traversal direction from the out-parameter to
	 * the in-parameter. 
	 */
	public static final Backward BACKWARD = new Backward ();

	/**
	 * Arbitrary traversal direction. This
	 * represents an arbitrary predicate traversal direction, i.e.,
	 * from the in-parameter to the out-parameter or from the out-parameter
	 * to the in-parameter. 
	 */
	public static final Undirected UNDIRECTED = new Undirected ();

	/**
	 * Arbitrary traversal direction. This
	 * represents an arbitrary predicate traversal direction, i.e.,
	 * from the in-parameter to the out-parameter or from the out-parameter
	 * to the in-parameter. 
	 */
	public static final Both BOTH = new Both ();


	private final int value;

	private EdgeDirection (int value)
	{
		this.value = value;
	}

	public int getCode ()
	{
		return value;
	}

	public boolean contains (EdgeDirection d)
	{
		return (value == d.value) || (d.value == BOTH_INT) || (value == UNDIRECTED_INT);
	}

	@Override
	public String toString ()
	{
		switch (value)
		{
			case FORWARD_INT:
				return "FORWARD";
			case BACKWARD_INT:
				return "BACKWARD";
			case UNDIRECTED_INT:
				return "UNDIRECTED";
			case BOTH_INT:
				return "BOTH";
			default:
				throw new AssertionError ();
		}
	}

}
