
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

import java.awt.geom.RectangularShape;

import javax.swing.JButton;
import javax.vecmath.Matrix3d;

import de.grogra.graph.Graph;
import de.grogra.graph.impl.Node;
import de.grogra.imp.IMPJobManager;
import de.grogra.imp.edit.ViewSelection;
import de.grogra.imp2d.objects.Connection;
import de.grogra.imp2d.objects.NURBSShape2D;
import de.grogra.pf.registry.Plugin;
import de.grogra.pf.ui.Context;
import de.grogra.pf.ui.event.ClickEvent;
import de.grogra.pf.ui.event.DragEvent;
import de.grogra.pf.ui.registry.CommandItem;
import de.grogra.pf.ui.registry.CommandPlugin;
import de.grogra.util.I18NBundle;

public final class IMP2D extends Plugin implements CommandPlugin
{
	public static final I18NBundle I18N = I18NBundle.getInstance (IMP2D.class);

	private static IMP2D PLUGIN;


	public static IMP2D getInstance ()
	{
		return PLUGIN;
	}


	public IMP2D ()
	{
		assert PLUGIN == null;
		PLUGIN = this;
	}

	@Override
	public void run (Object info, Context ctx, CommandItem item)
	{
		String n = item.getName ();
		// only for the ComponentView2D-buttons (up, down, haspart, uses, send) 
		if(info instanceof ClickEvent) {
			ClickEvent e = (ClickEvent) info;
			View2DIF view = (View2DIF) e.getPanel ();
			if(view instanceof ComponentView2D) {
				if ("up".equals (n))
				{
					((ComponentView2D)view).goUp();
				}
				else if ("down".equals (n))
				{
					((ComponentView2D)view).goDown();
				}
				else if ("haspart".equals (n))
				{
					((ComponentView2D)view).addHasPartEdge((JButton)e.getSource ());
				}
				else if ("uses".equals (n))
				{
					((ComponentView2D)view).addUsesEdge((JButton)e.getSource ());
				}
				else if ("slot".equals (n))
				{
					((ComponentView2D)view).addSlotEdge((JButton)e.getSource ());
				}
				else if ("send".equals (n))
				{
					((ComponentView2D)view).addSendEdge((JButton)e.getSource ());
				}
			}
		} else
		// move, zoom, ... buttons 
		if(info instanceof DragEvent) {
			DragEvent e = (DragEvent) info;
			if (e.draggingContinued ())
			{
				if ("move".equals (n))
				{
					move ((View2DIF) e.getPanel (), e.getDeltaX (), e.getDeltaY ());
				}
				else if ("zoom".equals (n))
				{
					zoom ((View2DIF) e.getPanel (), e.getDeltaX (), e.getDeltaY ());
				}
				else if ("rotate".equals (n))
				{
					rotate ((View2DIF) e.getPanel (), e.getDeltaX (), e.getDeltaY ());
				}
			}
		}
		if ("addconnection".equals (n))
		{
			ViewSelection selection = ViewSelection.get (ctx);
			ViewSelection.Entry[] er = selection.getAll (ViewSelection.SELECTED);
			if (er.length >= 2) 
			{
				Node start = (Node) er[0].getPath ().getObject (-1); 
				if ((start instanceof NURBSShape2D)
					&& (((NURBSShape2D) start).getCurve () instanceof Connection))
				{
					return;
				}
				Node end = (Node) er[1].getPath ().getObject (-1);
				if ((end instanceof NURBSShape2D)
					&& (((NURBSShape2D) end).getCurve () instanceof Connection))
				{
					return;
				}
				Node cx = new NURBSShape2D (new Connection ());
				IMPJobManager jm
					= (IMPJobManager) ctx.getWorkbench ().getJobManager ();
				start.addEdgeBitsTo (cx, Graph.BRANCH_EDGE | Graph.EDGENODE_IN_EDGE, null);
				cx.addEdgeBitsTo (end, Graph.EDGENODE_OUT_EDGE, null);
			}
		}
	}


	static void zoom (View2DIF view, int dx, int dy)
	{
		Matrix3d t = new Matrix3d (view.getTransformation ());
		double f = Math.exp (0.01 * (dx + dy));
		t.m00 *= f;
		t.m10 *= f;
		t.m01 *= f;
		t.m11 *= f;
		t.m02 *= f;
		t.m12 *= f;
		view.setTransformation (t);
	}


	static void rotate (View2DIF view, int dx, int dy)
	{
//		Vector2d v = new Vector2d (cx, cy);
//		Math2.invTransformPoint (view.getTransformation (), v);
		Matrix3d t = new Matrix3d ();
		t.rotZ ((dx - dy) * 0.005); 
		t.mul (view.getTransformation ());
//		Math2.transformVector (t, v);
//		t.m02 = cx - v.x;
//		t.m12 = cy - v.y;
		view.setTransformation (t);
	}


	static void move (View2DIF view, int dx, int dy)
	{
		Matrix3d t = new Matrix3d (view.getTransformation ());
		t.m02 += dx;
		t.m12 += dy;
		view.setTransformation (t);
	}

	
	public static void copySize (RectangularShape r, Object old)
	{
		if (old instanceof RectangularShape)
		{
			RectangularShape o = (RectangularShape) old;
			double w = o.getWidth (), h = o.getHeight ();
			r.setFrame (-0.5f * w, -0.5f * h, w, h);
		}
	}

}
