
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

package de.grogra.imp2d;

import javax.vecmath.*;
import de.grogra.graph.*;
import de.grogra.graph.impl.Node;
import de.grogra.xl.util.*;
import de.grogra.imp2d.objects.*;
import de.grogra.imp2d.objects.Attributes;

public abstract class Visitor2D extends ObjectList implements Visitor
{
	private EdgePattern pattern;
	protected GraphState state;

	protected Matrix3d transformation = new Matrix3d ();
	protected int layer;

	private Matrix3d pre = new Matrix3d ();
	private boolean hasXf;


	protected void init (GraphState gs, EdgePattern pattern,
						 Matrix3d t)
	{
		size = 0;
		transformation.set (t);
		pre.setIdentity ();
		this.state = gs;
		this.pattern = pattern;
	}


	public GraphState getGraphState ()
	{
		return state;
	}


	protected final void pushTransformation (Matrix3d t)
	{
		Object o;
		if ((elements.length == size)
			|| ((o = elements[size]) == null))
		{
			push (new Matrix3d (t));
		}
		else
		{
			((Matrix3d) o).set (t);
			size++;
		}
	}


	protected final Matrix3d popTransformation (Matrix3d t)
	{
		Matrix3d m = (Matrix3d) elements[--size];
		elements[size] = t;
		return m;
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
		Object result = null;
		pushTransformation (transformation);
		Transformation t = (object == null) ? null
			: (Transformation) state.getObjectDefault
			(object, asNode, Attributes.TRANSFORMATION, null);
		int l = layer;
		if (object != null)
		{
			layer = state.getIntDefault (object, asNode, Attributes.LAYER, l);
		}
		Matrix3d m = transformation;
		if (hasXf = (t != null))
		{
			t.preTransform (object, asNode, m, pre, state);
			transformation = pre;
		}
		try
		{
			visitImpl (object, asNode, path);
		}
		finally
		{
			transformation = m;
			layer = l;
		}
		if (t != null)
		{
			t.postTransform (object, asNode, pre, m, m, state);
		}
		return result;
	}


	protected abstract void visitImpl (Object object, boolean asNode, Path path);


	public boolean visitLeave (Object o, Path path, boolean node)
	{
		if (node)
		{
			transformation = popTransformation (transformation);
			return true;
		}
		else if (o != STOP)
		{
			transformation = popTransformation (transformation);
		}
		return true;
	}


	public Object visitInstanceEnter ()
	{
		pushTransformation (transformation);
		if (hasXf)
		{
			transformation.set (pre);
		}
		return null;
	}


	public boolean visitInstanceLeave (Object o)
	{
		transformation = popTransformation (transformation);
		return true;
	}
}
