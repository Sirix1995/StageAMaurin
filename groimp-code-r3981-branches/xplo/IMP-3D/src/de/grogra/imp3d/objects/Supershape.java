/*
 * Copyright (C) 2011 GroIMP Developer Team
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

import static java.lang.Math.max;
import static java.lang.Math.min;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;

/**
 * This class represents a supershape.
 *
 * An implementation of Johan Gielis's Superformula which was published in the
 * American Journal of Botany 90(3): 333–338. 2003.
 * INVITED SPECIAL PAPER A GENERIC GEOMETRIC TRANSFORMATION 
 * THAT UNIFIES A WIDE RANGE OF NATURAL AND ABSTRACT SHAPES
 * 
 * @author MH
 */
public class Supershape extends ShadedNull
	implements Pickable, Renderable
{
	protected static final float A_MIN = 0.1f;
	protected static final float A_MAX = 10;
	protected static final float B_MIN = 0.1f;
	protected static final float B_MAX = 10;
	protected static final float M_MIN = 0;
	protected static final float M_MAX = 32;
	protected static final float N1_MIN = 0;
	protected static final float N1_MAX = 10;
	protected static final float N2_MIN = 0;
	protected static final float N2_MAX = 10;
	protected static final float N3_MIN = 0;
	protected static final float N3_MAX = 10;

	protected float a = 1f;
	//enh:field attr=Attributes.A getter setmethod=setA
	
	protected float b = 1f;
	//enh:field attr=Attributes.B getter setmethod=setB
	
	protected float m1;
	//enh:field attr=Attributes.M1 getter setmethod=setM1
	
	protected float n11;
	//enh:field attr=Attributes.N11 getter setmethod=setN11
	
	protected float n12;
	//enh:field attr=Attributes.N12 getter setmethod=setN12
	
	protected float n13;
	//enh:field attr=Attributes.N13 getter setmethod=setN13

	protected float m2;
	//enh:field attr=Attributes.M2 getter setmethod=setM2
	
	protected float n21; 
	//enh:field attr=Attributes.N21 getter setmethod=setN21
	
	protected float n22;
	//enh:field attr=Attributes.N22 getter setmethod=setN22
	
	protected float n23;
	//enh:field attr=Attributes.N23 getter setmethod=setN23
	
	
	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, Supershape.$TYPE, new NType.Field[] {a$FIELD, b$FIELD, m1$FIELD, n11$FIELD, n12$FIELD, n13$FIELD, m2$FIELD, n21$FIELD, n22$FIELD, n23$FIELD});
		}

		public static void signature (@In @Out Supershape c, float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23)
		{
		}
	}
	

	public Supershape ()
	{
		this (1f, 1f, 10f, 10f, 10f, 10f, 5f, 10f, 10f, 10f);
	}


	public Supershape (float a, float b, float m, float n1, float n2, float n3)
	{
		super ();
		this.a = a;
		this.b = b;
		this.m1 = m;
		this.n11 = n1;
		this.n12 = n2;
		this.n13 = n3;
		this.m2 = m;
		this.n21 = n1;
		this.n22 = n2;
		this.n23 = n3;		
	}

	public Supershape (float a, float b, float m1, float n11, float n12, float n13, float m2, float n21, float n22, float n23)
	{
		super ();
		this.a = a;
		this.b = b;
		this.m1 = m1;
		this.n11 = n11;
		this.n12 = n12;
		this.n13 = n13;
		this.m2 = m2;
		this.n21 = n21;
		this.n22 = n22;
		this.n23 = n23;		
	}
	
	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
			  Matrix4d transformation, de.grogra.imp.PickList list)
	{
		Sphere.pick (1, origin, direction, list);
	}

	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				rs.drawSupershape (a, b, m1, n11, n12, n13, m2, n21, n22, n23, null, RenderState.CURRENT_HIGHLIGHT, null);
			}
			else
			{
				rs.drawSupershape (
						gs.checkFloat (this, true, Attributes.A, a),
						gs.checkFloat (this, true, Attributes.B, b),
						gs.checkFloat (this, true, Attributes.M1, m1),
						gs.checkFloat (this, true, Attributes.N11, n11),
						gs.checkFloat (this, true, Attributes.N12, n12),
						gs.checkFloat (this, true, Attributes.N13, n13),
						gs.checkFloat (this, true, Attributes.M2, m2),
						gs.checkFloat (this, true, Attributes.N21, n21),
						gs.checkFloat (this, true, Attributes.N22, n22),
						gs.checkFloat (this, true, Attributes.N23, n23),
						null, RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
		else
		{
			rs.drawSupershape (
					gs.checkFloat (this, true, Attributes.A, a),
					gs.checkFloat (this, true, Attributes.B, b),
					gs.checkFloat (this, true, Attributes.M1, m1),
					gs.checkFloat (this, true, Attributes.N11, n11),
					gs.checkFloat (this, true, Attributes.N12, n12),
					gs.checkFloat (this, true, Attributes.N13, n13),
					gs.checkFloat (this, true, Attributes.M2, m2),
					gs.checkFloat (this, true, Attributes.N21, n21),
					gs.checkFloat (this, true, Attributes.N22, n22),
					gs.checkFloat (this, true, Attributes.N23, n23),
					null, RenderState.CURRENT_HIGHLIGHT, null);
		}
	}



//	enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field a$FIELD;
	public static final NType.Field b$FIELD;
	public static final NType.Field m1$FIELD;
	public static final NType.Field n11$FIELD;
	public static final NType.Field n12$FIELD;
	public static final NType.Field n13$FIELD;
	public static final NType.Field m2$FIELD;
	public static final NType.Field n21$FIELD;
	public static final NType.Field n22$FIELD;
	public static final NType.Field n23$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Supershape.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Supershape) o).setA ((float) value);
					return;
				case 1:
					((Supershape) o).setB ((float) value);
					return;
				case 2:
					((Supershape) o).setM1 ((float) value);
					return;
				case 3:
					((Supershape) o).setN11 ((float) value);
					return;
				case 4:
					((Supershape) o).setN12 ((float) value);
					return;
				case 5:
					((Supershape) o).setN13 ((float) value);
					return;
				case 6:
					((Supershape) o).setM2 ((float) value);
					return;
				case 7:
					((Supershape) o).setN21 ((float) value);
					return;
				case 8:
					((Supershape) o).setN22 ((float) value);
					return;
				case 9:
					((Supershape) o).setN23 ((float) value);
					return;
			}
			super.setFloat (o, value);
		}

		@Override
		public float getFloat (Object o)
		{
			switch (id)
			{
				case 0:
					return ((Supershape) o).getA ();
				case 1:
					return ((Supershape) o).getB ();
				case 2:
					return ((Supershape) o).getM1 ();
				case 3:
					return ((Supershape) o).getN11 ();
				case 4:
					return ((Supershape) o).getN12 ();
				case 5:
					return ((Supershape) o).getN13 ();
				case 6:
					return ((Supershape) o).getM2 ();
				case 7:
					return ((Supershape) o).getN21 ();
				case 8:
					return ((Supershape) o).getN22 ();
				case 9:
					return ((Supershape) o).getN23 ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Supershape ());
		$TYPE.addManagedField (a$FIELD = new _Field ("a", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (b$FIELD = new _Field ("b", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (m1$FIELD = new _Field ("m1", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (n11$FIELD = new _Field ("n11", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.addManagedField (n12$FIELD = new _Field ("n12", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 4));
		$TYPE.addManagedField (n13$FIELD = new _Field ("n13", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 5));
		$TYPE.addManagedField (m2$FIELD = new _Field ("m2", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 6));
		$TYPE.addManagedField (n21$FIELD = new _Field ("n21", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 7));
		$TYPE.addManagedField (n22$FIELD = new _Field ("n22", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 8));
		$TYPE.addManagedField (n23$FIELD = new _Field ("n23", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 9));
		$TYPE.declareFieldAttribute (a$FIELD, Attributes.A);
		$TYPE.declareFieldAttribute (b$FIELD, Attributes.B);
		$TYPE.declareFieldAttribute (m1$FIELD, Attributes.M1);
		$TYPE.declareFieldAttribute (n11$FIELD, Attributes.N11);
		$TYPE.declareFieldAttribute (n12$FIELD, Attributes.N12);
		$TYPE.declareFieldAttribute (n13$FIELD, Attributes.N13);
		$TYPE.declareFieldAttribute (m2$FIELD, Attributes.M2);
		$TYPE.declareFieldAttribute (n21$FIELD, Attributes.N21);
		$TYPE.declareFieldAttribute (n22$FIELD, Attributes.N22);
		$TYPE.declareFieldAttribute (n23$FIELD, Attributes.N23);
		initType ();
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
		return new Supershape ();
	}

	public float getA ()
	{
		return a;
	}

	public float getB ()
	{
		return b;
	}

	public float getM1 ()
	{
		return m1;
	}

	public float getN11 ()
	{
		return n11;
	}

	public float getN12 ()
	{
		return n12;
	}

	public float getN13 ()
	{
		return n13;
	}

	public float getM2 ()
	{
		return m2;
	}

	public float getN21 ()
	{
		return n21;
	}

	public float getN22 ()
	{
		return n22;
	}

	public float getN23 ()
	{
		return n23;
	}

//enh:end

	public void setA (float value)
	{
		this.a = max(A_MIN, min(A_MAX, value));
	}

	public void setB (float value)
	{
		this.b = max(B_MIN, min(B_MAX, value));
	}

	public void setM1 (float value)
	{
		this.m1 = max(M_MIN, min(M_MAX, value));
	}

	public void setN11 (float value)
	{
		this.n11 = max(N1_MIN, min(N1_MAX, value));
	}

	public void setN12 (float value)
	{
		this.n12 = max(N2_MIN, min(N2_MAX, value));
	}

	public void setN13 (float value)
	{
		this.n13 = max(N3_MIN, min(N3_MAX, value));
	}

	public void setM2 (float value)
	{
		this.m2 = max(M_MIN, min(M_MAX, value));
	}

	public void setN21 (float value)
	{
		this.n21 = max(N1_MIN, min(N1_MAX, value));
	}

	public void setN22 (float value)
	{
		this.n22 = max(N2_MIN, min(N2_MAX, value));
	}

	public void setN23 (float value)
	{
		this.n23 = max(N3_MIN, min(N3_MAX, value));
	}
}
