
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

package de.grogra.imp2d.edit;

import javax.vecmath.*;
import de.grogra.math.*;
import de.grogra.vecmath.*;
import de.grogra.pf.ui.event.EditEvent;
import de.grogra.imp2d.*;
import de.grogra.imp2d.objects.*;
import de.grogra.graph.GraphState;
import de.grogra.persistence.*;

public class BezierCurveEdit implements Editable
{
	private static final IndirectField FIELD
		= BezierCurve.data$FIELD.getArrayChain (1);


	private static BezierCurve getCurve (EditTool tool)
	{
		Object c = tool.getTool ().getObjectOfObject (Attributes.CURVE, null);
		if (c instanceof BezierCurve)
		{
			tool.getGraphState ().setObjectContext (tool.getToolTarget (), tool.isTargetNode ());
			return (BezierCurve) c;
		}
		else
		{
			return null;
		}
	}


	public void drawTool (AWTCanvas2DIF canvas,
						  Matrix3d transformation, EditTool tool)
	{
		BezierCurve c = getCurve (tool);
		if (c == null)
		{
			return;
		}
		float[] a = canvas.getPool().getFloatArray (0, 2);
		GraphState gs = canvas.getRenderGraphState ();
		for (int i = 0; i < c.getSize (gs); i++)
		{
			c.getVertex (a, i, gs);
			EditTool.drawHandle (canvas, transformation, a[0], a[1]);
		}
	}

	
	public void pickTool (Point2d point,
						  Matrix3d transformation, de.grogra.imp.PickList list,
						  EditTool tool)
	{
		BezierCurve c = getCurve (tool);
		if (c == null)
		{
			return;
		}
		float[] a = list.getFloatArray (0, 2);
		GraphState gs = list.getGraphState ();
		for (int i = 0; i < c.getSize (gs); i++)
		{
			c.getVertex (a, i, gs);
			if (tool.pickHandle (transformation, list, a[0], a[1], i))
			{
				return;
			}
		}
		Object o = tool.getToolTarget ();
		((Shape2DBase) ((o instanceof Shape2DBase) ? o
						: NURBSShape2D.$TYPE.getRepresentative ()))
			.pick (o, tool.isTargetNode (), point, transformation, list);
		if (list.containsCurrent ())
		{
			tool.setPickId (EditTool.PICK_CENTER);
		}
	}

	
	public void toolEventOccured (EditEvent e, EditTool tool)
	{
		BezierCurve c = getCurve (tool);
		if (c == null)
		{
			return;
		}
		if (e instanceof DragEvent2D)
		{
			DragEvent2D d = (DragEvent2D) e;
			int id = tool.getPickId ();
			float[] a = tool.pool.getFloatArray (0, 2);
			GraphState gs = tool.getGraphState ();
			if (d.draggingStarted ())
			{
				if ((id == EditTool.PICK_CENTER)
					|| ((id >= 0) && (id < c.getSize (gs))))
				{
					Point2d p = new Point2d (d.point);
					Math2.invTransformPoint
						(tool.getTargetTransformation (), p);
					if (id != EditTool.PICK_CENTER)
					{
						c.getVertex (a, id, gs);
						p.x -= a[0];
						p.y -= a[1];
					}
					tool.setObject (p);
				}
				else
				{
					tool.setObject (null);
				}
			}
			else if (d.draggingContinued ())
			{
				Point2d p = tool.pool.p2d0, q = (Point2d) tool.getObject ();
				if (q == null)
				{
					return;
				}
				p.set (d.point);
				Math2.invTransformPoint
					(tool.getTargetTransformation (), p);
				p.sub (q);
				if (id == EditTool.PICK_CENTER)
				{
					Matrix3d t = tool.pool.m3d1;
					Transform2D tx = (Transform2D) tool.getTool ().getObjectOfObject
						(Attributes.TRANSFORM, null);
					if (tx != null)
					{
						tool.pool.m3d0.setIdentity ();
						tx.transform (tool.pool.m3d0, t);
					}
					else
					{
						t.setIdentity ();
					}
					Math2.transformVector (t, p);
					t.m02 += p.x;
					t.m12 += p.y;
					tool.setTransform (t);
				}
				else if ((id >= 0) && (id < c.getSize (gs)))
				{
					int[] i = new int[] {2 * id};
					tool.getTool ().setSubfield
						(Attributes.CURVE, FIELD, i, new Float (p.x));
					i[0] = 2 * id + 1;
					tool.getTool ().setSubfield
						(Attributes.CURVE, FIELD, i, new Float (p.y));
				}
			}
		}
	}

}
