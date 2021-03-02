
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

import javax.vecmath.*;
import java.awt.*;
import java.util.Vector;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import de.grogra.math.*;
import de.grogra.vecmath.*;
import de.grogra.graph.*;
import de.grogra.imp2d.*;
import de.grogra.imp2d.edit.*;
import de.grogra.pf.ui.event.EditEvent;

public class QuadraticLine extends FillableShape2D implements Editable
{
	private static final int HANDLE_COUNT = 7;
	
	public final Vector2d axis = new Vector2d (100, 100);
	//enh:field type=Tuple2dType.VECTOR set=set
	
	protected double fLength = 0;
	//enh:field attr=Attributes.LENGTH
	
	private Vector2d control0 = new Vector2d(0,0);
	private Vector2d control1 = new Vector2d(50,0);
	private Vector  fPoints = new Vector();
	
	
	

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field axis$FIELD;
	public static final NType.Field fLength$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (QuadraticLine.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setDouble (Object o, double value)
		{
			switch (id)
			{
				case 1:
					((QuadraticLine) o).fLength = (double) value;
					return;
			}
			super.setDouble (o, value);
		}

		@Override
		public double getDouble (Object o)
		{
			switch (id)
			{
				case 1:
					return ((QuadraticLine) o).fLength;
			}
			return super.getDouble (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((QuadraticLine) o).axis.set ((Vector2d) value);
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
					return ((QuadraticLine) o).axis;
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new QuadraticLine ());
		$TYPE.addManagedField (axis$FIELD = new _Field ("axis", _Field.PUBLIC | _Field.FINAL  | _Field.SCO, Tuple2dType.VECTOR, null, 0));
		$TYPE.addManagedField (fLength$FIELD = new _Field ("fLength", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.DOUBLE, null, 1));
		$TYPE.declareFieldAttribute (fLength$FIELD, Attributes.LENGTH);
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
		return new QuadraticLine ();
	}

//enh:end

    public QuadraticLine ()
    {
		super ();
		axis.set(100,100);
		setTransforming (false);
		addVector();
    }


    public QuadraticLine  (double dx, double dy)
    {
		this ();
		axis.set (dx, dy);
    }


    public QuadraticLine  (double dx, double dy, boolean transforming)
    {
		this ();
		axis.set (dx, dy);
		setTransforming (transforming);
    }


    public QuadraticLine  (double x, double y,  double dx, double dy)
    {
		this ();
		transform = new TVector2d (x, y);
		axis.set (dx, dy);
    }

    public void addVector()
    {
    	fPoints.add(control0);
    	fPoints.add(control1);
    	fPoints.add(axis);
    }
	
    @Override
	public void drawTool (AWTCanvas2DIF canvas, Matrix3d transformation, EditTool tool)
    {
        // vorher: attribute.getObject (object, graphstate)
        // jetzt : graphstate.getObject (object, attribute)
    	Vector2d axis = this.axis;
    	
    	double width= axis.x , height = axis.y;
    	for (int i = 0; i  < fPoints.size();i++)
    	{
    		EditTool.drawHandle (canvas, transformation, 
    								((Vector2d)fPoints.get(i)).x, 
    								((Vector2d)fPoints.get(i)).y);
    	}
    }
    
	@Override
	public Shape getShape( Object object, boolean asNode, de.grogra.math.Pool pool, GraphState gs) 
	{
		Shape shape_start = null;
		Shape shape_end = null;
		Point2D.Double tStart = new Point2D.Double(control0.x,control0.y);
		Point2D.Double tEnd = new Point2D.Double(axis.x,axis.y);
		Point2D.Double pEnd = new Point2D.Double(axis.x,axis.y);
		
		GeneralPath path  = pool.path;
		path.reset();
/*		if (startshapestyle != Line.ARROW_NO) 
			shape_start = createLineEnd((int)(startsize*scaley),startshapestyle,
										 new Point2D.Double(((Vector2d)fPoints.get(1)).x,
										 					((Vector2d)fPoints.get(1)).y),
										tStart);
		
		if (endshapestyle != Line.ARROW_NO)
			shape_end = createLineEnd( (int)(endsize*scaley),endshapestyle,
										new Point2D.Double(((Vector2d)fPoints.get(1)).x,
														   ((Vector2d)fPoints.get(1)).y),
										pEnd);		
*/						
		path.moveTo((float)tStart.x,(float)tStart.y);
		
		path.quadTo((float)(control1.x),(float)(control1.y),
					 (float)pEnd.x,(float)pEnd.y);
		
/*		
		if (shape_end != null)
		{
			if (isendfill) { 
				canvas.getDrawGraphics ().setColor (eColor);
				canvas.getDrawGraphics ().fill(shape_end);
				canvas.getDrawGraphics ().setPaint (awtColor);
			}
			path.append(shape_end,false);
		}
		
		if (shape_start != null) 
		{
			if (isstartfill)
			{
				canvas.getDrawGraphics ().setColor(sColor);
				canvas.getDrawGraphics ().fill(shape_start);
			}
			path.append(shape_start,false);
		}
		*/
		return path;
	}

    
    public void pickTool (Object object, Point2d point,
			  Matrix3d transformation, de.grogra.imp.PickList list,
			  EditTool tool)
    {
    	if (	!(tool.pickHandle (transformation, list, 0, 0, 0)
    			|| tool.pickHandle (transformation, list, control1.x, control1.y, 1)
				|| tool.pickHandle (transformation, list, axis.x, axis.y, 2)))
    	{
    		pick (object, point, transformation, list);
    		if (list.containsCurrent ())
    		{
    			tool.setPickId (3);
    		}
		}
    }
    
    public void pick (Object node, Point2d point,
			  Matrix3d transformation, de.grogra.imp.PickList list)
    {
    	for (int i = 0; i < fPoints.size()-1; i++ ) {
    		pick ((float)((Vector2d)fPoints.get(i)).x,
    			  (float)((Vector2d)fPoints.get(i)).y,
    			  (float)((Vector2d)fPoints.get(i+1)).x,
    			  (float)((Vector2d)fPoints.get(i+1)).y,
    			  point, transformation, list);
		
   		}
    }


	public static void pick (float x1, float y1, float x2, float y2, Point2d point,
					  Matrix3d transformation, de.grogra.imp.PickList list)
	{
		if (list.getView () != null)
		{
			Matrix3d t = ((View2D) list.getView ()).getCanvasTransformation ();
			Point2d p = list.q2d0;
			p.set (x1, y1);
			Math2.transformPoint (transformation, p);
			Math2.transformPoint (t, p);
			Vector2d v = list.w2d0;
			v.set (x2 - x1, y2 - y1);
			Math2.transformVector (transformation, v);
			Math2.transformVector (t, v);
			Point2d q = list.q2d1, r = list.q2d2;
			q.add (p, v);
			r.set (list.getViewX (), list.getViewY ());
			if ((r.distanceSquared (p) < 25) || (r.distanceSquared (q) < 25))
			{
				list.add ();
				return;
			}
			Vector2d w = list.w2d1;
			w.sub (r, p);
			double lambda = w.dot (v) / v.lengthSquared ();
			if ((lambda < 0) || (lambda > 1))
			{
				return;
			}
			p.scaleAdd (lambda, v, p);
			if (r.distanceSquared (p) < 25)
			{
				list.add ();
			}
		}
	}


//    public void pick (IGraphObject object, Point2d point,
//			  Matrix3d transformation, de.grogra.imp.PickList list)
//    {
//    	RectangularShape r = (RectangularShape)getLine (list);
//    	r.setFrame (0, 0, width, height);
//    	if (r.contains (point.x, point.y))
//    	{
//    		list.add (layer);
//    	}
//    }
    
    
    
    @Override
	public void toolEventOccured (EditEvent e,
					  EditTool tool)
    {
		if (e instanceof DragEvent2D)
		{
			DragEvent2D d = (DragEvent2D) e;
			int id = tool.getPickId ();
			if (d.draggingStarted ())
			{
				if ((id >= 0) && (id <= 3))
				{
					Point2d p = new Point2d (d.point);
					Math2.invTransformPoint
						(tool.getTargetTransformation (), p);

					if (id >= 0 && id <= 2)
					{
						p.sub ((Vector2d)fPoints.get(id));
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
				Point2d p = tool.pool.p2d0, 
						q = (Point2d) tool.getObject ();
				if (q == null)
				{
					return;
				}
				p.set (d.point);
				Math2.invTransformPoint
					(tool.getTargetTransformation (), p);
							

				if (id > 0 && id <= 2)
				{
					p.sub ((Vector2d)fPoints.get(id));
				}
				p.sub (q);
				Matrix3d t = tool.pool.m3d1;
				if (transform != null)
				{
					tool.pool.m3d0.setIdentity ();
					transform.transform (tool.pool.m3d0, t);
				}
				else
				{
					t.setIdentity ();
				}

				if (id == 3)
				{
					Math2.transformVector (t, p);
					t.m02 += p.x;
					t.m12 += p.y;
					tool.setTransform (t);
				}
				else if ((id >= 0) && (id < 3))
				{
					Vector2d delta = tool.pool.v2d0;
					delta.x = 0;
					delta.y = 0;
					double w = ((Vector2d)fPoints.get(id)).x, 
						   h = ((Vector2d)fPoints.get(id)).y;
					
					
					if (id == 0)
					{
						delta.x = p.x;
						delta.y = p.y;
					}
					if (id > 0 && id <= 2)
					{
						w += p.x;
						h += p.y;
					}
										
					if (delta.lengthSquared () > 0)
					{
						Math2.transformVector (t, delta);
						t.m02 += delta.x;
						t.m12 += delta.y;
						tool.setTransform (t);
						for (int i = 1 ; i < fPoints.size(); i++)
						{
							double w1 = ((Vector2d)fPoints.get(i)).x;
							double h1 = ((Vector2d)fPoints.get(i)).y;
							w1 -= delta.x;
							h1 -= delta.y;
							((Vector2d)fPoints.get(i)).set(w1,h1);
							tool.getTool ().setDoubleOfObject
								(Attributes.LENGTH, fLength);
						}
					}
					if ((h != ((Vector2d)fPoints.get(id)).y) 
						 || (w != ((Vector2d)fPoints.get(id)).x))
					{
						((Vector2d)fPoints.get(id)).set(w,h);
						tool.getTool ().setDoubleOfObject
							(Attributes.LENGTH, fLength);
					}
					
				}
				
			}
		}
    }

    public void setPath(GeneralPath path,
			Point2D.Double tStart,Point2D.Double pEnd)
    {
    	path.moveTo((float)tStart.x,(float)tStart.y);
    	path.lineTo((float)pEnd.x,(float)pEnd.y);
	
    }
    
    
    
	public void postTransform (Matrix3d in, Matrix3d out,
							   Matrix3d pre, GraphState gc)
	{
		if ((bits & TRANSFORMING_MASK) != 0)
		{
			if (in != out)
			{
				out.set (in);
			}
			double e = 1;
			if (e != 0)
			{
				Vector2d a = axis;
				out.m02 += e * (out.m00 * a.x + out.m01 * a.y);
				out.m12 += e * (out.m10 * a.x + out.m11 * a.y);
			}
		}
		else
		{
			out.set (pre);
		}
	}


	@Override
	protected Editable getEditable ()
	{
		return this;
	}

}
