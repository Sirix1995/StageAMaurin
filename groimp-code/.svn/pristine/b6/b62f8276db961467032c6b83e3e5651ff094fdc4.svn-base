
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
import de.grogra.graph.*;
import de.grogra.graph.impl.*;
import de.grogra.vecmath.*;
import de.grogra.imp.*;

public class PickRayVisitor extends Visitor3D
{
	protected PickList list;
	protected final Point3d origin = new Point3d ();
	protected final Vector3d direction = new Vector3d ();

	final Point3d origin0 = new Point3d ();
	final Vector3d direction0 = new Vector3d ();

	private final LineArray lines = new LineArray ();

	private boolean checkLayer;

	private final ArrayPath placeInPath = new ArrayPath ((Graph) null);

	public void pick (View3D view, int x, int y,
					  Point3d origin, Vector3d direction, PickList list)
	{
		list.reset (view, x, y);
		origin0.set (origin);
		direction0.set (direction);
		direction0.normalize ();
		this.list = list;
		placeInPath.clear (view.getGraph ());
		init (view.getWorkbenchGraphState (), view.getGraph ().getTreePattern (),
			  de.grogra.imp.objects.Matrix4dAttribute.IDENTITY);
		this.checkLayer = true;
		view.getGraph ().accept (null, this, placeInPath);
		de.grogra.imp.edit.Tool tool = view.getActiveTool ();
		if (tool != null)
		{
			list.beginNewGroup ();
			init (GraphManager.STATIC_STATE, EdgePatternImpl.TREE,
				  de.grogra.imp.objects.Matrix4dAttribute.IDENTITY);
			placeInPath.clear (GraphManager.STATIC);
			this.checkLayer = false;
			for (int i = 0; i < tool.getToolCount (); i++)
			{
				GraphManager.acceptGraph (tool.getRoot (i), this, placeInPath);
			}
		}
	}


	@Override
	protected void visitEnterImpl (Object object, boolean asNode, Path path)
	{
		Object p = state.getObjectDefault
			(object, asNode, de.grogra.imp3d.objects.Attributes.SHAPE, null);
		if (p != null)
		{
			if (checkLayer && !list.getView ().isInVisibleLayer (object, asNode, state))
			{
				return;
			}
			if (p instanceof Pickable)
			{
				origin.set (origin0);
				direction.set (direction0);
				Math2.invTransformPointAndVector (transformation, origin, direction);
				list.begin (path, !asNode);
				((Pickable) p)
					.pick (object, asNode, origin, direction, transformation, list);
				list.end ();
			}
			else if (p instanceof LineSegmentizable)
			{
				origin.set (origin0);
				direction.set (direction0);
				Math2.invTransformPointAndVector (transformation, origin, direction);
				list.begin (path, !asNode);
				state.setObjectContext (object, asNode);
				LineSegmentizable ls = (LineSegmentizable) p;
				ls.segmentize (ls.getSegmentizableSource (state), state, lines, 1);
				pickLines (lines, origin, direction, transformation, list, 4);
				list.end ();
			}
		}
	}

	@Override
	protected void visitLeaveImpl (Object object, boolean asNode, Path path)
	{
	}

	public static void pickLines (LineArray lines,
								  Point3d origin, Vector3d direction,
								  Matrix4d transformation, PickList list,
								  int tolerance)
	{
		int[] vertices = lines.lines.elements;
		float[] data = lines.vertices.elements;
		int dim = lines.dimension;
		int n = lines.lines.size;
		int i = 0;
		boolean line = false;
		Point3d p = list.p3d0, q = list.p3d1;
		Vector3d d = list.v3d0;
		while (i < n)
		{
			int v = vertices[i++];
			if (v >= 0)
			{
				v *= dim;
				q.x = data[v];
				q.y = (dim > 1) ? data[v + 1] : 0;
				q.z = (dim > 2) ? data[v + 2] : 0;
				if (line)
				{
					d.sub (q, p);
					pickLine (p, d, origin, direction, transformation, list, tolerance);
				}
				Point3d t = p; p = q; q = t;
				line = true;
			}
			else
			{
				line = false;
			}
		}
	}


	public static void pickLine (Point3d basis, Vector3d axis,
								 Point3d origin, Vector3d direction,
								 Matrix4d transformation, PickList list,
								 int tolerance)
	{
		double[] lambda = list.getDoubleArray (0, 2);
		Math2.shortestConnection
			(basis, axis, origin, direction, lambda);
		if ((lambda[0] >= 0) && (lambda[0] <= 1) && (lambda[1] > 0))
		{
			Point3d p = list.q3d0;
			Tuple2f a = list.q2f0;
			p.scaleAdd (lambda[0], axis, basis);
			transformation.transform (p);
			if (((View3D) list.getView ()).getCanvasCamera ()
				.projectWorld (p, a) > 0)
			{
				if ((Math.abs (a.x - list.getViewX ()) < tolerance)
					&& (Math.abs (a.y - list.getViewY ()) < tolerance))
				{
					list.add (lambda[1]);
				}
			}
		}
	}


	public static void pickPoint (Point3d origin, Vector3d direction,
								  Matrix4d transformation, PickList list,
								  int tolerance)
	{
		Point3d b = list.p3d0;
		Tuple2f p = list.p2f0;
		b.set (0, 0, 0);
		transformation.transform (b);
		if (((View3D) list.getView ()).getCanvasCamera ()
			.projectWorld (b, p) > 0)
		{
			if ((Math.abs (p.x - list.getViewX ()) < tolerance)
				&& (Math.abs (p.y - list.getViewY ()) < tolerance))
			{
				b.set (0, 0, 0);
				list.add (Math2.closestConnection (origin, direction, b));
			}
		}
	}

	public static void pickPoint (Point3d origin, Vector3d direction, Point3d point,
								  Matrix4d transformation, PickList list,
								  int tolerance)
	{
		Point3d b = list.p3d0;
		Tuple2f p = list.p2f0;
		b.set (point);
		transformation.transform (b);
		if (((View3D) list.getView ()).getCanvasCamera ()
			.projectWorld (b, p) > 0)
		{
			if ((Math.abs (p.x - list.getViewX ()) < tolerance)
				&& (Math.abs (p.y - list.getViewY ()) < tolerance))
			{
				b.set (0, 0, 0);
				list.add (Math2.closestConnection (origin, direction, b));
			}
		}
	}

}

