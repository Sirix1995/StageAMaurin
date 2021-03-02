
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

import java.awt.*;
import java.awt.geom.*;
import de.grogra.pf.registry.*;
import de.grogra.graph.*;
import de.grogra.imp2d.*;
import de.grogra.imp2d.edit.*;
import de.grogra.math.*;
import de.grogra.xl.util.FloatList;

public class NURBSShape2D extends FillableShape2D implements Editable
{
	Arrow startArrow = new Arrow ();
	//enh:field attr=Attributes.START_ARROW getter setter
	
	Arrow endArrow = new Arrow ();
	//enh:field attr=Attributes.END_ARROW getter setter

	BSplineCurve curve;
	//enh:field attr=Attributes.CURVE getter setter

	float edgeWeight = 1.0f;
	//enh:field attr=Attributes.EDGEWEIGHTED getter setter

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field startArrow$FIELD;
	public static final NType.Field endArrow$FIELD;
	public static final NType.Field curve$FIELD;
	public static final NType.Field edgeWeight$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (NURBSShape2D.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 3:
					((NURBSShape2D) o).edgeWeight = (float) value;
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 3:
					return ((NURBSShape2D) o).getEdgeWeight ();
			}
			return super.getFloat (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((NURBSShape2D) o).startArrow = (Arrow) value;
					return;
				case 1:
					((NURBSShape2D) o).endArrow = (Arrow) value;
					return;
				case 2:
					((NURBSShape2D) o).curve = (BSplineCurve) value;
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
					return ((NURBSShape2D) o).getStartArrow ();
				case 1:
					return ((NURBSShape2D) o).getEndArrow ();
				case 2:
					return ((NURBSShape2D) o).getCurve ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new NURBSShape2D ());
		$TYPE.addManagedField (startArrow$FIELD = new _Field ("startArrow", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Arrow.class), null, 0));
		$TYPE.addManagedField (endArrow$FIELD = new _Field ("endArrow", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Arrow.class), null, 1));
		$TYPE.addManagedField (curve$FIELD = new _Field ("curve", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (BSplineCurve.class), null, 2));
		$TYPE.addManagedField (edgeWeight$FIELD = new _Field ("edgeWeight", 0 | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.declareFieldAttribute (startArrow$FIELD, Attributes.START_ARROW);
		$TYPE.declareFieldAttribute (endArrow$FIELD, Attributes.END_ARROW);
		$TYPE.declareFieldAttribute (curve$FIELD, Attributes.CURVE);
		$TYPE.declareFieldAttribute (edgeWeight$FIELD, Attributes.EDGEWEIGHTED);
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
		return new NURBSShape2D ();
	}

	public float getEdgeWeight ()
	{
		return edgeWeight;
	}

	public void setEdgeWeight (float value)
	{
		this.edgeWeight = (float) value;
	}

	public Arrow getStartArrow ()
	{
		return startArrow;
	}

	public void setStartArrow (Arrow value)
	{
		startArrow$FIELD.setObject (this, value);
	}

	public Arrow getEndArrow ()
	{
		return endArrow;
	}

	public void setEndArrow (Arrow value)
	{
		endArrow$FIELD.setObject (this, value);
	}

	public BSplineCurve getCurve ()
	{
		return curve;
	}

	public void setCurve (BSplineCurve value)
	{
		curve$FIELD.setObject (this, value);
	}

//enh:end


	public NURBSShape2D ()
	{
		this (null);
	}


	public NURBSShape2D (BSplineCurve curve)
	{
		this.curve = curve;
		setFilled (false);
	}


	private static final Arrow DEFAULT_ARROW = new Arrow ();

	@Override
	protected Shape getShape (Object object, boolean asNode, final Pool pool,
							  GraphState gs)
	{
		final Arrow sa, ea;
		BSplineCurve c;
		if (object == this)
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				c = curve;
				sa = startArrow;
				ea = endArrow;
			}
			else
			{
				c = (BSplineCurve) gs.checkObject (this, true, Attributes.CURVE, curve);
				sa = (Arrow) gs.checkObject (this, true, Attributes.START_ARROW, startArrow);
				ea = (Arrow) gs.checkObject (this, true, Attributes.END_ARROW, endArrow);
			}
		}
		else
		{
			c = (BSplineCurve) gs.getObject (object, asNode, Attributes.CURVE);
			sa = (Arrow) gs.getObjectDefault (object, asNode, Attributes.START_ARROW, DEFAULT_ARROW);
			ea = (Arrow) gs.getObjectDefault (object, asNode, Attributes.END_ARROW, DEFAULT_ARROW);
		}

		gs.setObjectContext (object, asNode);
		if (!BSpline.isValid (c, gs))
		{
			return new Rectangle (1, 1);
		}

		final GeneralPath path = pool.path;
		path.reset ();
		path.setWindingRule (PathIterator.WIND_NON_ZERO);

		class Helper extends SubdivisionHelper implements BSpline.BezierSegmentVisitor
		{
			private int count;
			private float currX, currY, prevX, prevY;

			Helper (boolean rational)
			{
				super (rational);
			}

			public void visit (int i, float[] data, int dimension, int degree,
							   float uLeft, float uRight)
			{
				this.dimension = dimension;
				this.degree = degree;
				FloatList v = (pool != null) ? pool.fv : new FloatList ();
				v.clear ();
				v.addAll (data, 0, dimension * (degree + 1));
				addVertex (v.elements, 0);
				subdivideCurve (v, data, 0, 0);
			}

			@Override
			protected void visitFlat (float[] v, int index)
			{
				addVertex (v, index + dimension * degree);
			}

			private void addVertex (float[] v, int i)
			{
				float x, y;
				int d = dimension;
				if (rational)
				{
					float w1 = 1 / v[i + d - 1];
					x = v[i] * w1;
					y = (d > 2) ? v[i + 1] * w1 : 0;
				}
				else
				{
					x = v[i];
					y = (d > 1) ? v[i + 1] : 0;
				}
				prevX = currX;
				prevY = currY;
				currX = x;
				currY = y;
				if (count > 1)
				{
					path.lineTo (prevX, prevY);
				}
				else if (count == 1)
				{
					Arrow.add (sa, path, false, x, y, prevX, prevY);
				}
				count++;
			}

			void setFlatness (float f)
			{
				flatness = f;
			}
			
			void finish ()
			{
				Arrow.add (ea, path, true, prevX, prevY, currX, currY);
			}
		}

		Helper h = new Helper (c.isRational (gs));
		h.setFlatness (0.001f);// * flatness);
		
		if(c instanceof SplineConnection) {
			BSpline.decomposeSplineConnection(h, c, pool, gs);
		} else {
			BSpline.decompose (h, c, false, gs);
		}
		
		h.finish ();
		return path;
	}


	@Override
	protected boolean pickBoundary ()
	{
		return true;
	}

	
	private static LookupForClass EDITABLE_LOOKUP;

	@Override
	protected Editable getEditable ()
	{
		LookupForClass x = EDITABLE_LOOKUP;
		if (x == null)
		{
			EDITABLE_LOOKUP = x = new LookupForClass
				(IMP2D.getInstance ().getRegistry ()
				 .getItem (EditTool.PATH + "/bspline"));
		}
		Object e = x.lookup (curve.getClass ());
		return (e != null) ? (Editable) e : this;
	}

/*
	public void postTransform (INode node, Matrix3d in, Matrix3d out, Matrix3d pre,
							   GraphState gs)
	{
	transform:
		{
			Tuple2d a;
			if (node == this) 
			{
				if ((getName () == null) || !gs.isWithinInstancing ())
				{
					if ((bits & TRANSFORMING_MASK) == 0)
					{
						break transform;
					}
					a = getAxis (null);
				}
				else
				{
					if (!gs.checkBoolean (this, Attributes.TRANSFORMING, (bits & TRANSFORMING_MASK) != 0))
					{
						break transform;
					}
					a = (Tuple2d) gs.checkObject (this, Attributes.AXIS, getAxis (null));
				}
			}
			else
			{
				if (!gs.getBoolean (node, Attributes.TRANSFORMING))
				{
					break transform;
				}
				a = (Tuple2d) gs.getObject (node, null, Attributes.AXIS);
			}
			out.set (in);
			out.m02 += out.m00 * a.x + out.m01 * a.y;
			out.m12 += out.m10 * a.x + out.m11 * a.y;
			return;
		}
		if (out != pre)
		{
			out.set (pre);
		}
	}

*/
}
