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

package de.grogra.imp3d.objects;

import javax.vecmath.Matrix4d;
import de.grogra.graph.*;

/**
 * This interface represents affine 3D coordinate transformations for an object
 * in a graph. The <code>preTransform</code> is applied to the post-transformation
 * of the object's parent in the scene hierarchy in order to obtain the
 * transformation of the object. The <code>postTransform</code> is applied to this
 * transformation in order to obtain the object's post-transformation.
 * 
 * @author Ole Kniemeyer
 */
public interface Transformation
{
	/**
	 * Implements the change from the parent's post-transformation <code>in</code>
	 * to the object's transformation <code>out</code>.
	 * 
	 * @param object the object for which the transformation is computed
	 * @param asNode is <code>object</code> a node or an edge?
	 * @param in the post-transformation of the object's parent
	 * @param out the tranformation of the object to be computed
	 * @param gs the graph state in which the computation has to be done
	 */
	void preTransform (Object object, boolean asNode, Matrix4d in,
			Matrix4d out, GraphState gs);

	/**
	 * Implements the change from object's transformation <code>in</code>
	 * to the object's post-transformation <code>out</code>.
	 * 
	 * @param object the object for which the transformation is computed
	 * @param asNode is <code>object</code> a node or an edge?
	 * @param in the transformation of the object
	 * @param out the post-tranformation of the object to be computed
	 * @param parent the post-transformation of the object's parent
	 * @param gs the graph state in which the computation has to be done
	 */
	void postTransform (Object object, boolean asNode, Matrix4d in,
			Matrix4d out, Matrix4d parent, GraphState gs);
}
