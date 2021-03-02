
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

import javax.vecmath.Point2d;

import de.grogra.graph.ArrayPath;
import de.grogra.graph.EdgePatternImpl;
import de.grogra.graph.Graph;
import de.grogra.graph.Path;
import de.grogra.graph.impl.GraphManager;
import de.grogra.imp.PickList;
import de.grogra.vecmath.Math2;

public class PickRayVisitor extends Visitor2D
{
	protected PickList list;
	protected final Point2d point = new Point2d ();

	final Point2d point0 = new Point2d ();


	private final ArrayPath placeInPath = new ArrayPath ((Graph) null);

	public void pick (View2DIF view, int x, int y, PickList list)
	{
		list.reset (view, x, y);
		point0.set (x, y);
		Math2.invTransformPoint (view.getCanvasTransformation (), point0);
		this.list = list;
		init (view.getWorkbenchGraphState (),
			  view.getGraph ().getTreePattern (),
			  de.grogra.imp.objects.Matrix3dAttribute.IDENTITY);
		placeInPath.clear (view.getGraph ());
		view.getGraph ().accept (null, this, placeInPath);
		de.grogra.imp.edit.Tool tool = view.getActiveTool ();
		if (tool != null)
		{
			list.beginNewGroup ();
			init (GraphManager.STATIC_STATE, EdgePatternImpl.TREE,
				  de.grogra.imp.objects.Matrix3dAttribute.IDENTITY);
			placeInPath.clear (GraphManager.STATIC);
			for (int i = 0; i < tool.getToolCount (); i++)
			{
				GraphManager.acceptGraph (tool.getRoot (i), this, placeInPath);
			}
		}
	}


	@Override
	protected void visitImpl (Object object, boolean asNode, Path path)
	{
		Object p = state.getObjectDefault
			(object, asNode, de.grogra.imp2d.objects.Attributes.SHAPE, null);
		if (p instanceof Pickable)
		{
			if (!list.getView ().isInVisibleLayer (object, asNode, state))
			{
				return;
			}
			point.set (point0);
			Math2.invTransformPoint (transformation, point);
			list.begin (path, !asNode);
			((Pickable) p).pick (object, asNode, point, transformation, list);
			list.end ();
		}
	}

}

