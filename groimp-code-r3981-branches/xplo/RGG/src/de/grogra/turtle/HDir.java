
// NOTE: This file was generated automatically.

// ********************************************
// *               DO NOT EDIT!               *
// ********************************************


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

package de.grogra.turtle;

import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

import javax.vecmath.Matrix4d;

import de.grogra.graph.GraphState;
import de.grogra.graph.impl.Node;
import de.grogra.graph.impl.Node.FieldAttributeAccessor;
import de.grogra.imp3d.objects.GlobalTransformation;
import de.grogra.imp3d.objects.Transformation;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.Matrix34d;

/**
 * Set the local z-axis (the turtle's head axis) so that it
 * encompasses the given angle to the world's up axis.
 * This is performed by a rotation in the plane spanned by
 * the world's up axis and the former head direction of the turtle.
 * 
 * @author Reinhard Hemmerling
 */
public class HDir extends Node implements Transformation
{
	/**
	 * The rotation angle in degrees. 
	 */
	public float angle = 0f;
	//enh:field setter getter
	
	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.TRANSFORMATION);
		$TYPE.addDependency (Attributes.ANGLE, Attributes.TRANSFORMATION);
		$TYPE.addAccessor (new FieldAttributeAccessor (Attributes.ANGLE, angle$FIELD, Math.PI / 180));
		$TYPE.setAttribute (angle$FIELD, Attributes.ANGLE);
	}

	//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field angle$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (HDir.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((HDir) o).angle = (float) value;
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
					return ((HDir) o).getAngle ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new HDir ());
		$TYPE.addManagedField (angle$FIELD = new _Field ("angle", _Field.PUBLIC  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
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
		return new HDir ();
	}

	public float getAngle ()
	{
		return angle;
	}

	public void setAngle (float value)
	{
		this.angle = (float) value;
	}

//enh:end

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (HDir.$TYPE, angle$FIELD);
		}

		public static void signature (@In @Out HDir n, float a)
		{
		}
	}


	/**
	 * Creates a new <code>HDir</code> node with an initial
	 * angle of 0 degrees.
	 */
	public HDir ()
	{
		this (0);
	}


	/**
	 * Creates a new <code>HDir</code> node which set's the head
	 * axis of the turtle such that it spans the given <code>angle</code>
	 * in degrees to the world's up-axis.
	 * 
	 * @param angle angle between head and world's up direction in degrees
	 */
	public HDir (float angle)
	{
		setAngle (angle);
	}

	public void preTransform (Object node, boolean asNode, Matrix4d in, Matrix4d out, GraphState gs)
	{
		out.set (in);
	}


	public void postTransform (Object node, boolean asNode, Matrix4d in, Matrix4d out, Matrix4d pre,
							   GraphState gs)
	{
		// find local->global transformation
		Matrix34d m = GlobalTransformation.getParentValue (node, asNode, gs, true);
		
		// calculate angle between head (m02,m12,m22) and up (0,0,1)
		double a = acos(m.m22);
		
		// compute rotation axis
		double x, y, z;
		// check if head direction is straight up
		// TODO maybe need to check for epsilon region around zero
		if (a == 0)
		{
			// randomly select an axis of rotation in the xy-plane
//			double u = 2 * PI * Math.random();
//			x = cos(u);
//			y = sin(u);
//			z = 0;
			// rotate around left axis
			x = m.m00;
			y = m.m10;
			z = m.m20;
		}
		else {
			// calculate rotation axis as cross(up,head)
			x = -m.m12;
			y = m.m02;
			z = 0;
			// normalize axis
			double r = 1.0 / sqrt(x*x + y*y + z*z);
			x *= r;
			y *= r;
			z *= r;
		}
		
		// calculate the amount of rotation needed
		a = toRadians(angle) - a;
		double ca = cos(a);
		double sa = sin(a);
		
		// compute rotation matrix
		// http://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
		// http://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula
		double t = x*m.m00 + y*m.m10 + z*m.m20;
		double m00 = m.m00*ca + (y*m.m20 - z*m.m10)*sa + x*t*(1-ca);
		double m10 = m.m10*ca + (z*m.m00 - x*m.m20)*sa + y*t*(1-ca);
		double m20 = m.m20*ca + (x*m.m10 - y*m.m00)*sa + z*t*(1-ca);
		t = x*m.m01 + y*m.m11 + z*m.m21;
		double m01 = m.m01*ca + (y*m.m21 - z*m.m11)*sa + x*t*(1-ca);
		double m11 = m.m11*ca + (z*m.m01 - x*m.m21)*sa + y*t*(1-ca);
		double m21 = m.m21*ca + (x*m.m11 - y*m.m01)*sa + z*t*(1-ca);
		t = x*m.m02 + y*m.m12 + z*m.m22;
		double m02 = m.m02*ca + (y*m.m22 - z*m.m12)*sa + x*t*(1-ca);
		double m12 = m.m12*ca + (z*m.m02 - x*m.m22)*sa + y*t*(1-ca);
		double m22 = m.m22*ca + (x*m.m12 - y*m.m02)*sa + z*t*(1-ca);
		
		double m03 = m.m03;
		double m13 = m.m13;
		double m23 = m.m23;

		// set out to global->camera transformation
		Math2.invertAffine (m, out);
		Math2.mulAffine (out, in, out);
		
		// apply new local->global transformation to out
		out.m00 = (x = out.m00) * m00 + (y = out.m01) * m10 + (z = out.m02) * m20;
		out.m01 = x * m01 + y * m11 + z * m21;
		out.m02 = x * m02 + y * m12 + z * m22;
		out.m03 += x * m03 + y * m13 + z * m23;
		out.m10 = (x = out.m10) * m00 + (y = out.m11) * m10 + (z = out.m12) * m20;
		out.m11 = x * m01 + y * m11 + z * m21;
		out.m12 = x * m02 + y * m12 + z * m22;
		out.m13 += x * m03 + y * m13 + z * m23;
		out.m20 = (x = out.m20) * m00 + (y = out.m21) * m10 + (z = out.m22) * m20;
		out.m21 = x * m01 + y * m11 + z * m21;
		out.m22 = x * m02 + y * m12 + z * m22;
		out.m23 += x * m03 + y * m13 + z * m23;
	}
	
}
