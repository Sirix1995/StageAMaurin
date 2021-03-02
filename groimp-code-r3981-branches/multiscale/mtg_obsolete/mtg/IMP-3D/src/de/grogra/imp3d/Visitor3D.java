
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

import javax.vecmath.*;
import de.grogra.vecmath.*;
import de.grogra.xl.util.ObjectList;
import de.grogra.graph.*;
import de.grogra.imp3d.objects.*;
import de.grogra.imp3d.objects.Attributes;

/**
 * This base implementation of the <code>Visitor</code> interface
 * tracks information about the current coordinate transformation
 * and layer of objects. Invocations of <code>visit</code> methods
 * are forwarded to the abstract methods
 * {@link #visitEnterImpl(Object, boolean, Path)} and
 * {@link #visitLeaveImpl(Object, boolean, Path)}. 
 * 
 * @author Ole Kniemeyer
 */
public abstract class Visitor3D implements Visitor
{
	/**
	 * The graph state in which this visitor runs.
	 */
	protected GraphState state;

	/**
	 * The current transformation from local coordinates to global
	 * coordinates.
	 */
	protected Matrix4d transformation = new Matrix4d ();

	/**
	 * The current layer.
	 */
	protected int layer;

	/**
	 * This stack contains a list of unused <code>Matrix4d</code>s which can
	 * be popped if needed.  
	 */
	private final ObjectList matrixPool = new ObjectList ();

	private final ObjectList transformations = new ObjectList ();

	private EdgePattern pattern;

	private final Matrix4d pre = new Matrix4d ();
	private boolean hasXf;


	/**
	 * Initializes this visitor.
	 * 
	 * @param gs the graph state in which the visitor runs
	 * @param pattern the pattern which is used to extract the scene tree to
	 * visit from the complete graph
	 * @param t the initial transformation from local to global coordinates
	 */
	protected void init (GraphState gs, EdgePattern pattern,
						 Matrix4d t)
	{
		transformations.clear ();
		if (transformation == pre)
		{
			transformation = new Matrix4d ();
		}
		transformation.set (t);
		pre.setIdentity ();
		this.state = gs;
		this.pattern = pattern;
	}


	public GraphState getGraphState ()
	{
		return state;
	}

	
	/**
	 * Returns the current affine transformation from local coordinates
	 * to global coordinates.
	 * 
	 * @return current coordinate transformation
	 */
	public Matrix4d getCurrentTransformation ()
	{
		return transformation;
	}


	public Object visitEnter (Path path, boolean node)
	{
		if (node)
		{
			return visitEnter (path.getObject (-1), true, path);
		}
		else
		{
			if (!GraphUtils.matchesTerminalEdge (pattern, path))
			{
				return STOP;
			}
			return visitEnter (path.getObject (-2), false, path);
		}
	}

	
	protected Object visitEnter (Object object, boolean asNode, Path path)
	{
		Transformation t = (object == null) ? null
			: (Transformation) state.getObjectDefault
			(object, asNode, Attributes.TRANSFORMATION, null);
		int l = layer;
		if (object != null)
		{
			layer = state.getIntDefault (object, asNode, Attributes.LAYER, l);
		}
		Matrix4d m = transformation;
		if (hasXf = (t != null))
		{
			transformations.push (m);
			t.preTransform (object, asNode, m, pre, state);
			transformation = pre;
		}
		else
		{
			transformations.push (null);
		}
		visitEnterImpl (object, asNode, path);
		layer = l;
		if (t != null)
		{
			Matrix4d n;
			if (matrixPool.isEmpty ())
			{
				n = new Matrix4d ();
				n.m33 = 1;
			}
			else
			{
				n = (Matrix4d) matrixPool.pop ();
			}
			t.postTransform (object, asNode, pre, n, m, state);
			transformation = n;
		}
		return null;
	}

	
	private void popTransformation ()
	{
		Matrix4d t = (Matrix4d) transformations.pop ();
		if (t != null)
		{
			matrixPool.push (transformation);
			transformation = t;
		}
	}

	/**
	 * This method has to be implemented by subclasses. It is invoked when
	 * <code>object</code> is entered.
	 * 
	 * @param object the object being entered
	 * @param asNode is <code>object</code> a node or an edge?
	 * @param path the path to <code>object</code> if <code>object</code>
	 * is a node, the path to the node where <code>object</code> points to
	 * if <code>object</code> is an edge
	 */
	protected abstract void visitEnterImpl (Object object, boolean asNode, Path path);


	/**
	 * This method has to be implemented by subclasses. It is invoked when
	 * <code>object</code> is left.
	 * 
	 * @param object the object being left
	 * @param asNode is <code>object</code> a node or an edge?
	 * @param path the path to <code>object</code> if <code>object</code>
	 * is a node, the path to the node where <code>object</code> points to
	 * if <code>object</code> is an edge
	 */
	protected abstract void visitLeaveImpl (Object object, boolean asNode, Path path);


	public boolean visitLeave (Object o, Path path, boolean node)
	{
		if (node)
		{
			visitLeaveImpl (path.getObject (-1), true, path);
			popTransformation ();
		}
		else if (o != STOP)
		{
			visitLeaveImpl (path.getObject (-2), false, path);
			popTransformation ();
		}
		return true;
	}


	public Object visitInstanceEnter ()
	{
		if (hasXf)
		{
			transformations.push (transformation);
			Matrix4d n;
			if (matrixPool.isEmpty ())
			{
				n = new Matrix4d ();
				n.m33 = 1;
			}
			else
			{
				n = (Matrix4d) matrixPool.pop ();
			}
			Math2.setAffine (n, pre);
			transformation = n;
		}
		else
		{
			transformations.push (null);
		}
		return null;
	}


	public boolean visitInstanceLeave (Object o)
	{
		popTransformation ();
		return true;
	}
}
