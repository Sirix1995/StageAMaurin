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

package de.grogra.imp3d.ray2;

import de.grogra.vecmath.geom.Volume;

/**
 * A <code>VolumeListener</code> is informed from a
 * {@link de.grogra.imp3d.ray2.SceneVisitor} about the mappings
 * from graph objects to volumes via invocations of the method
 * <code>volumeCreated</code>.
 * 
 * @author Ole Kniemeyer
 *
 * @see SceneVisitor
 */
public interface VolumeListener
{
	/**
	 * This method is invoked by a {@link SceneVisitor} when a
	 * <code>volume</code> is created as representation of the geometry
	 * of <code>object</code>. By storing the information provided by the
	 * parameters, the link from graph objects (nodes and edges) to volumes
	 * can be established. 
	 * 
	 * @param object an object of the graph
	 * @param asNode is <code>object</code> a node or an edge?
	 * @param volume the volume which has been created as geometrical
	 * representation of <code>object</code>
	 */
	void volumeCreated (Object object, boolean asNode, Volume volume);

	/**
	 * This method is invoked by a {@link SceneVisitor} when subsequent
	 * volumes shall be grouped into a single compound object. The group
	 * extends until the corresponding invocation of {@link #endGroup()}.
	 * These invocations may be nested, i.e., there may be groups within
	 * groups.
	 * <p>
	 * Each group starts at <code>object</code> in the graph. If
	 * <code>object</code> has a geometric representation itself, the
	 * corresponding invocation of {@link #volumeCreated} may be either
	 * immediately before of after <code>beginGroup</code>.
	 * 
	 * @param object the object of the graph which represents the root
	 * of the group
	 * @param asNode is <code>object</code> a node or an edge?
	 * 
	 * @see #endGroup
	 */
	void beginGroup (Object object, boolean asNode);
	
	/**
	 * This method is invoked by a {@link SceneVisitor} when the current group
	 * ends.
	 * 
	 * @see #beginGroup
	 */
	void endGroup ();
}
