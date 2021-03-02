
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

package de.grogra.imp3d.objects;

import de.grogra.graph.*;
import de.grogra.imp3d.*;
import de.grogra.math.*;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class NURBSCurve extends ColoredNull implements LineSegmentizable
{
	protected BSplineCurve curve;
	//enh:field attr=Attributes.CURVE getter setter

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
		$TYPE.declareAlias ("radius", curve$FIELD.concat (Circle.radius$FIELD));
	}


	public NURBSCurve ()
	{
		this (null);
	}


	public NURBSCurve (BSplineCurve curve)
	{
		this.curve = curve;
		setLayer (2);
	}


	public ContextDependent getSegmentizableSource (GraphState gs)
	{
		return curve;
	}


	public void segmentize (ContextDependent source, GraphState gs, LineArray out, float flatness)
	{
		out.init (3);
		final Pool pool = Pool.get (gs);

		class Helper extends NURBSSubdivisionHelper implements BSpline.BezierSegmentVisitor
		{
			private final IntList vertexIndices;

			Helper (LineArray out, boolean rational)
			{
				super (out.vertices, rational);
				this.vertexIndices = out.lines;
			}

			public void visit (int i, float[] data, int dimension, int degree,
							   float uLeft, float uRight)
			{
				this.dimension = dimension;
				this.degree = degree;
				FloatList v = pool.fv;
				v.clear ();
				v.addAll (data, 0, dimension * (degree + 1));
				vertexIndices.add (addVertex (v.elements, 0));
				subdivideCurve (v, data, 0, 0);
				vertexIndices.add (-1);
			}

			@Override
			protected void visitFlat (float[] v, int index)
			{
				vertexIndices.add (addVertex (v, index + dimension * degree));
			}
		}

		if (BSpline.isValid (curve, gs))
		{
			Helper h = new Helper (out, curve.isRational (gs));
			h.setFlatness (0.001f * flatness);
			BSpline.decompose (h, curve, false, gs);
		}
	}
//	enh:insert initType();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field curve$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (NURBSCurve.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((NURBSCurve) o).curve = (BSplineCurve) value;
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
					return ((NURBSCurve) o).getCurve ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new NURBSCurve ());
		$TYPE.addManagedField (curve$FIELD = new _Field ("curve", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (BSplineCurve.class), null, 0));
		$TYPE.declareFieldAttribute (curve$FIELD, Attributes.CURVE);
		initType();
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
		return new NURBSCurve ();
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


}
