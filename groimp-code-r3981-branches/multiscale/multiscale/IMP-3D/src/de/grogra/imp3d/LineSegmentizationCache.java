
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

package de.grogra.imp3d;

import de.grogra.graph.*;

/**
 * This class can be used to cache the result of segmentization
 * of {@link de.grogra.imp3d.LineSegmentizable} shapes.
 * 
 * @author Ole Kniemeyer
 */
public class LineSegmentizationCache extends Cache
{
	final float flatness;


	/**
	 * Constructs a new cache. The parameters will be passed
	 * to {@link LineSegmentizable#segmentize(ContextDependent, GraphState, LineArray, float)}
	 * when a cache entry is to be computed.
	 * 
	 * @param gs the graph state within which the cache will be used
	 * @param flatness the flatness to pass to <code>segmentize</code>
	 */
	public LineSegmentizationCache (GraphState gs, float flatness)
	{
		super (gs);
		this.flatness = flatness;
	}


	/**
	 * Returns the <code>LineArray</code> of a <code>LineSegmentizable</code>
	 * in the given object context.
	 * 
	 * @param object the context
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param ls the segmentizable
	 * @return a <code>LineArray</code>, computed by <code>ls</code>
	 */
	public LineArray get (Object object, boolean asNode, LineSegmentizable ls)
	{
		gs.setObjectContext (object, asNode);
		ContextDependent cd = ls.getSegmentizableSource (gs);
		if (cd == null)
		{
			return null;
		}
		return (LineArray) getValue (object, asNode, cd, ls);
	}


	@Override
	protected Entry createEntry (Object obj, boolean node,
								 ContextDependent dep, Object strategy)
	{
		return new Entry (obj, node, dep, strategy, gs)
		{
			@Override
			protected Object computeValue
				(Object oldValue, ContextDependent dependent, Object ls, GraphState s)
			{
				LineArray out = (oldValue != null) ? (LineArray) oldValue
					: new LineArray ();
				((LineSegmentizable) ls).segmentize (dependent, s, out, flatness);
				return out;
			}
		};
	}

}
