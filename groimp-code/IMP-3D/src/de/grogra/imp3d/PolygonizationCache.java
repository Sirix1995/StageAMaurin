
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
 * This class can be used to cache the result of polygonization
 * of {@link de.grogra.imp3d.Polygonizable} shapes.
 * 
 * @author Ole Kniemeyer
 */
public class PolygonizationCache extends Cache
{
	final int flags;
	final float flatness;
	final PolygonArray sharedLists;


	/**
	 * Constructs a new cache. The parameters will be passed
	 * to {@link Polygonization#polygonize}
	 * when a cache entry is to be computed.
	 * 
	 * @param gs the graph state within which the cache will be used
	 * @param flags the flags to pass to <code>polygonize</code>
	 * @param flatness the flatness to pass to <code>polygonize</code>
	 * @param shareLists shall the returned <code>PolygonArray</code>s share their lists
	 * (<code>vertices</code>, <code>normals</code> etc.)? This is useful where the
	 * returned data is copied into another representation and no longer needed
	 */
	public PolygonizationCache (GraphState gs, int flags, float flatness, boolean shareLists)
	{
		super (gs);
		this.flags = flags;
		this.flatness = flatness;
		this.sharedLists = shareLists ? new PolygonArray () : null;
	}


	/**
	 * Returns the <code>PolygonArray</code> of a <code>Polygonizable</code>
	 * in the given object context.
	 * 
	 * @param object the context
	 * @param asNode <code>true</code> if <code>object</code> is a node,
	 * <code>false</code> if <code>object</code> is an edge
	 * @param p the polygonizable
	 * @return a <code>PolygonArray</code>, computed by <code>p</code>
	 */
	public PolygonArray get (Object object, boolean asNode, Polygonizable p)
	{
		gs.setObjectContext (object, asNode);
		ContextDependent cd = p.getPolygonizableSource (gs);
		if (cd == null)
		{
			return null;
		}
		return (PolygonArray) getValue (object, asNode, cd, p.getPolygonization ());
	}


	@Override
	protected Entry createEntry (Object obj, boolean node,
								 ContextDependent dep, Object strategy)
	{
		return new Entry (obj, node, dep, strategy, gs)
		{
			@Override
			protected Object computeValue
				(Object oldValue, ContextDependent dependent, Object p, GraphState s)
			{
				PolygonArray out;
				if (oldValue != null)
				{
					out = (PolygonArray) oldValue;
				}
				else
				{
					out = new PolygonArray ();
					if (sharedLists != null)
					{
						out.vertices = sharedLists.vertices;
						out.normals = sharedLists.normals;
						out.uv = sharedLists.uv;
						out.polygons = sharedLists.polygons;
					}
				}
				((Polygonization) p).polygonize (dependent, s, out, flags, flatness);
				return out;
			}
		};
	}

}
