
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
import de.grogra.imp.edit.Tool;
import de.grogra.imp2d.graphs.ObjectData;
import de.grogra.vecmath.Math2;

public class PickAllVisitor extends Visitor2D {
	private static final long serialVersionUID = 134764858322L;
	protected PickList list;
	protected final Point2d point = new Point2d ();
	private Tool tool;


	private final ArrayPath placeInPath = new ArrayPath ((Graph) null);

	public void pick (View2DIF view, PickList list) {
		list.reset ();
		this.list = list;
		init (view.getWorkbenchGraphState (),
			  view.getGraph ().getTreePattern (),
			  de.grogra.imp.objects.Matrix3dAttribute.IDENTITY);
		placeInPath.clear (view.getGraph ());
		view.getGraph ().accept (null, this, placeInPath);
		tool = view.getActiveTool ();
		if (tool != null) {
			list.beginNewGroup ();
			init (GraphManager.STATIC_STATE, EdgePatternImpl.TREE,
				  de.grogra.imp.objects.Matrix3dAttribute.IDENTITY);
			placeInPath.clear (GraphManager.STATIC);
			for (int i = 0; i < tool.getToolCount (); i++) {
				GraphManager.acceptGraph (tool.getRoot (i), this, placeInPath);
			}
		}
	}
	
	@Override
	protected void visitImpl (Object object, boolean asNode, Path path) {
		String tmp = object.getClass ().getSimpleName ();
		if(!(tmp.equals ("InputSlot") || tmp.equals ("OutputSlot"))) return;
		Object p = state.getObjectDefault(object, asNode, de.grogra.imp2d.objects.Attributes.SHAPE, null);
		ObjectData od = (ObjectData)state.getObjectDefault(object, true, de.grogra.imp2d.objects.Attributes.TRANSFORM, null);
		if(p==null || od==null) return;
		point.set (od.x,od.y);
		if (p instanceof Pickable) {
			Math2.invTransformPoint (transformation, point);
			list.begin (path, !asNode);
			((Pickable) p).pick (object, asNode, point, transformation, list);
			list.end ();
		}
	}

}

