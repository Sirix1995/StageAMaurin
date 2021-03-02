
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

package de.grogra.imp2d.objects;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

import javax.vecmath.Matrix3d;
import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import de.grogra.graph.GraphState;
import de.grogra.imp2d.AWTCanvas2DIF;
import de.grogra.imp2d.DragEvent2D;
import de.grogra.imp2d.edit.EditTool;
import de.grogra.imp2d.edit.Editable;
import de.grogra.math.Pool;
import de.grogra.math.Transform2D;
import de.grogra.persistence.ManageableType;
import de.grogra.pf.ui.event.EditEvent;
import de.grogra.vecmath.Math2;

public class Shape2D extends FillableShape2D implements Editable
{
	Shape shape = new Rectangle2D.Float (-0.5f, -0.3f, 1, 0.6f);
	//enh:field attr=Attributes.SHAPE_2D getter setter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field shape$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Shape2D.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Shape2D) o).shape = (Shape) value;
					return;
			}
			super.setObjectImpl (o, value);
		}

		@Override
		public Object getObject (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Shape2D) o).getShape ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Shape2D ());
		$TYPE.addManagedField (shape$FIELD = new _Field ("shape", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Shape.class), null, 0));
		$TYPE.declareFieldAttribute (shape$FIELD, Attributes.SHAPE_2D);
		$TYPE.validate ();
	}

	@Override
	protected NType getNTypeImpl ()
	{
		return $TYPE;
	}

	@Override
	protected de.grogra.graph.impl.Node newInstance ()
	{
		return new Shape2D ();
	}

	public Shape getShape ()
	{
		return shape;
	}

	public void setShape (Shape value)
	{
		shape$FIELD.setObject (this, value);
	}

//enh:end


	@Override
	protected Editable getEditable ()
	{
		return this;
	}


	@Override
	protected Shape getShape (Object o, boolean asNode, Pool pool, GraphState gs)
	{
		if (o == this)
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				return shape;
			}
			else
			{
				return gs.checkObject (this, true, Attributes.SHAPE_2D, shape);
			}
		}
		else
		{
			return (Shape) gs.getObject (o, asNode, Attributes.SHAPE_2D);
		}
	}


	private static final int HANDLE_COUNT = 8;
	private static final float[] W_FACTORS = {-0.5f, 0, 0.5f, 0.5f, 0.5f, 0, -0.5f, -0.5f};
	private static final float[] H_FACTORS = {-0.5f, -0.5f, -0.5f, 0, 0.5f, 0.5f, 0.5f, 0};


	@Override
	public void pickTool (Point2d point,
						  Matrix3d transformation, de.grogra.imp.PickList list,
						  EditTool tool)
	{
		Shape s = getShape (tool.getToolTarget (), tool.isTargetNode (), list, list.getGraphState ());
		if (s instanceof Editable)
		{
			((Editable) s).pickTool (point, transformation, list, tool);
			if (tool.getPickId () != EditTool.PICK_NOTHING)
			{
				return;
			}
		}
		else if ((s instanceof RectangularShape)
				 && Attributes.SHAPE_2D.isWritable
				 (tool.getToolTarget (), tool.isTargetNode (), list.getGraphState ()))
		{
			RectangularShape rs = (RectangularShape) s;
			for (int i = 0; i < HANDLE_COUNT; i++)
			{
				if (tool.pickHandle (transformation, list,
									 rs.getCenterX () + rs.getWidth () * W_FACTORS[i],
									 rs.getCenterY () + rs.getHeight () * H_FACTORS[i], i))
				{
					return;
				}
			}
		}
		pick (tool.getToolTarget (), tool.isTargetNode (), point, transformation, list);
		if (list.containsCurrent ())
		{
			tool.setPickId (EditTool.PICK_CENTER);
		}
	}


	@Override
	public void drawTool (AWTCanvas2DIF canvas,
						  Matrix3d transformation, EditTool tool)
	{
		Shape s = getShape (tool.getToolTarget (), tool.isTargetNode (), canvas.getPool(), canvas.getRenderGraphState ());
		if (s instanceof Editable)
		{
			((Editable) s).drawTool (canvas, transformation, tool);
		}
		else if ((s instanceof RectangularShape)
				 && Attributes.SHAPE_2D.isWritable
				 (tool.getToolTarget (), tool.isTargetNode (), canvas.getRenderGraphState ()))
		{
			RectangularShape rs = (RectangularShape) s;
			for (int i = 0; i < HANDLE_COUNT; i++)
			{
				EditTool.drawHandle (canvas, transformation,
										  rs.getCenterX () + rs.getWidth () * W_FACTORS[i],
										  rs.getCenterY () + rs.getHeight () * H_FACTORS[i]);
			}
		}
	}


	@Override
	public void toolEventOccured (EditEvent e, EditTool tool)
	{
		int id = tool.getPickId ();
		if (id == EditTool.PICK_NOTHING)
		{
			return;
		}
		if (id == EditTool.PICK_CENTER)
		{
			super.toolEventOccured (e, tool);
			return;
		}

		Shape s = getShape (tool.getToolTarget (), tool.isTargetNode (), tool.pool, tool.getGraphState ());
		if (s instanceof Editable)
		{
			((Editable) s).toolEventOccured (e, tool);
		}
		else
		{
			RectangularShape rs;
			double width, height;
			if (s instanceof RectangularShape)
			{
				rs = (RectangularShape) s;
				width = rs.getWidth ();
				height = rs.getHeight ();
			}
			else
			{
				return;
			}
			if (e instanceof DragEvent2D)
			{
				DragEvent2D d = (DragEvent2D) e;
				if (d.draggingStarted ())
				{
					if ((id >= 0) && (id < HANDLE_COUNT))
					{
						Point2d p = new Point2d (d.point);
						Math2.invTransformPoint
							(tool.getTargetTransformation (), p);
						p.x -= width * W_FACTORS[id];
						p.y -= height * H_FACTORS[id];
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
					p.x -= width * W_FACTORS[id];
					p.y -= height * H_FACTORS[id];
					p.sub (q);
					Matrix3d t = tool.pool.m3d1;
					Transform2D x = (Transform2D) tool.getTool ()
						.getObjectOfObject (Attributes.TRANSFORM, null);
					if (x != null)
					{
						tool.pool.m3d0.setIdentity ();
						x.transform (tool.pool.m3d0, t);
					}
					else
					{
						t.setIdentity ();
					}

					if ((id >= 0) && (id < HANDLE_COUNT))
					{
						ManageableType mt = ManageableType.forClass (rs.getClass ());
						if (mt == null)
						{
							return;
						}
						Vector2d delta = tool.pool.v2d0;
						delta.set (Math.abs (W_FACTORS[id]) * p.x, Math.abs (H_FACTORS[id]) * p.y);
						if (delta.lengthSquared () > 0)
						{
							double w = width + 2 * W_FACTORS[id] * p.x,
								h = height + 2 * H_FACTORS[id] * p.y;
							Math2.transformVector (t, delta);
							t.m02 += delta.x;
							t.m12 += delta.y;
							tool.setTransform (t);
							if (h != height)
							{
								tool.getTool ().setSubfield
									(Attributes.SHAPE_2D, mt.getManagedField ("height"),
									 null, new Double (h));
							}
							if (w != width)
							{
								tool.getTool ().setSubfield
									(Attributes.SHAPE_2D, mt.getManagedField ("width"),
									 null, new Double (w));
							}
						}
					}
				}
			}
		}
	}

}
