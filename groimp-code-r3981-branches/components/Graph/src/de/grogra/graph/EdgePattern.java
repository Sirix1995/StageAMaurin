
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

/**
 * An <code>EdgePattern</code> is a <code>boolean</code> function which
 * can be applied to edges in the context of graph traversal.
 * It is used as a pattern to filter the edges which shall be traversed.
 * 
 * @author Ole Kniemeyer
 */
public interface EdgePattern
{
	/**
	 * Tests whether the given edge matches this pattern.
	 * 
	 * @param source the source node of the edge
	 * @param target the target node of the edge
	 * @param edges the edge bits of the edge
	 * @param toTarget <code>true</code> iff the edge is traversed
	 * from <code>source</code> to <code>target</code>
	 * @return <code>true</code> iff edge matches pattern
	 */
	boolean matches (Object source, Object target, int edges, boolean toTarget);
}
