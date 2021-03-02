
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

package de.grogra.math;

import javax.vecmath.*;
import java.awt.geom.*;
import java.util.ArrayList;

import de.grogra.xl.util.FloatList;
import de.grogra.graph.*;

public class Pool
{
	private static final int POOL_ID = GraphState.allocatePropertyId ();

	private Pool prev, next;
	private float[][] floatArrays = new float[2][];
	private double[][] doubleArrays = new double[2][];


	public static Pool get (GraphState gs)
	{
		Pool p = (Pool) gs.getUserProperty (POOL_ID);
		if (p == null)
		{
			gs.setUserProperty (POOL_ID, p = new Pool ());
		}
		return p;
	}


	public static Pool push (GraphState gs)
	{
		Pool p = (Pool) gs.getUserProperty (POOL_ID);
		if (p == null)
		{
			p = new Pool ();
		}
		if (p.next == null)
		{
			(p.next = new Pool ()).prev = p;
		}
		gs.setUserProperty (POOL_ID, p = p.next);
		return p;
	}

	
	public Pool ()
	{
	}


	public void pop (GraphState gs)
	{
		gs.setUserProperty (POOL_ID, prev);
	}


	public float[] getFloatArray (int index, int length)
	{
		float[][] a = floatArrays;
		int n;
		if (index >= (n = a.length))
		{
			System.arraycopy (a, 0,
							  floatArrays = a = new float[index + 1][], 0,
							  n);
		}
		return ((a[index] == null) || (length > a[index].length))
			? (a[index] = new float[length]) : a[index];
	}


	public double[] getDoubleArray (int index, int length)
	{
		double[][] a = doubleArrays;
		int n;
		if (index >= (n = a.length))
		{
			System.arraycopy (a, 0,
							  doubleArrays = a = new double[index + 1][], 0,
							  n);
		}
		return ((a[index] == null) || (length > a[index].length))
			? (a[index] = new double[length]) : a[index];
	}



	public ArrayList list = new ArrayList ();


	public FloatList fv = new FloatList ();

/*!!
#foreach ($t in ["Vector2f", "Vector2d", "Point2f", "Point2d", "Vector3f", "Vector3d", "Point3f", "Point3d", "Vector4f", "Point4f", "Matrix3f", "Matrix3d", "Matrix4f", "Matrix4d", "Line2D.Double", "Rectangle2D.Double", "Ellipse2D.Double", "RoundRectangle2D.Double"])

#if ($t.startsWith("Vector"))
  #set ($p = "v$t.substring(6)")
  #set ($p2 = "w$t.substring(6)")
#elseif ($t.startsWith("Point"))
  #set ($p = "p$t.substring(5)")
  #set ($p2 = "q$t.substring(5)")
#elseif ($t.startsWith("Matrix"))
  #set ($p = "m$t.substring(6)")
  #set ($p2 = "n$t.substring(6)")
#elseif ($t == "Line2D.Double")
  #set ($p = "l")
  #set ($p2 = false)
#elseif ($t == "Rectangle2D.Double")
  #set ($p = "r")
  #set ($p2 = false)
#elseif ($t == "Ellipse2D.Double")
  #set ($p = "e")
  #set ($p2 = false)
#elseif ($t == "RoundRectangle2D.Double")
  #set ($p = "rr")
  #set ($p2 = false)
#end

	public final $t ${p}0 = new $t (), ${p}1 = new $t (),
		${p}2 = new $t (), ${p}3 = new $t ()

#if ($p2)
		, ${p2}0 = new $t (), ${p2}1 = new $t (),
		${p2}2 = new $t (), ${p2}3 = new $t ()
#end
		;

#end
!!*/
//!! #* Start of generated code
// generated
// generated
	public final Vector2f v2f0 = new Vector2f (), v2f1 = new Vector2f (),
		v2f2 = new Vector2f (), v2f3 = new Vector2f ()
// generated
		, w2f0 = new Vector2f (), w2f1 = new Vector2f (),
		w2f2 = new Vector2f (), w2f3 = new Vector2f ()
		;
// generated
// generated
// generated
	public final Vector2d v2d0 = new Vector2d (), v2d1 = new Vector2d (),
		v2d2 = new Vector2d (), v2d3 = new Vector2d ()
// generated
		, w2d0 = new Vector2d (), w2d1 = new Vector2d (),
		w2d2 = new Vector2d (), w2d3 = new Vector2d ()
		;
// generated
// generated
// generated
	public final Point2f p2f0 = new Point2f (), p2f1 = new Point2f (),
		p2f2 = new Point2f (), p2f3 = new Point2f ()
// generated
		, q2f0 = new Point2f (), q2f1 = new Point2f (),
		q2f2 = new Point2f (), q2f3 = new Point2f ()
		;
// generated
// generated
// generated
	public final Point2d p2d0 = new Point2d (), p2d1 = new Point2d (),
		p2d2 = new Point2d (), p2d3 = new Point2d ()
// generated
		, q2d0 = new Point2d (), q2d1 = new Point2d (),
		q2d2 = new Point2d (), q2d3 = new Point2d ()
		;
// generated
// generated
// generated
	public final Vector3f v3f0 = new Vector3f (), v3f1 = new Vector3f (),
		v3f2 = new Vector3f (), v3f3 = new Vector3f ()
// generated
		, w3f0 = new Vector3f (), w3f1 = new Vector3f (),
		w3f2 = new Vector3f (), w3f3 = new Vector3f ()
		;
// generated
// generated
// generated
	public final Vector3d v3d0 = new Vector3d (), v3d1 = new Vector3d (),
		v3d2 = new Vector3d (), v3d3 = new Vector3d ()
// generated
		, w3d0 = new Vector3d (), w3d1 = new Vector3d (),
		w3d2 = new Vector3d (), w3d3 = new Vector3d ()
		;
// generated
// generated
// generated
	public final Point3f p3f0 = new Point3f (), p3f1 = new Point3f (),
		p3f2 = new Point3f (), p3f3 = new Point3f ()
// generated
		, q3f0 = new Point3f (), q3f1 = new Point3f (),
		q3f2 = new Point3f (), q3f3 = new Point3f ()
		;
// generated
// generated
// generated
	public final Point3d p3d0 = new Point3d (), p3d1 = new Point3d (),
		p3d2 = new Point3d (), p3d3 = new Point3d ()
// generated
		, q3d0 = new Point3d (), q3d1 = new Point3d (),
		q3d2 = new Point3d (), q3d3 = new Point3d ()
		;
// generated
// generated
// generated
	public final Vector4f v4f0 = new Vector4f (), v4f1 = new Vector4f (),
		v4f2 = new Vector4f (), v4f3 = new Vector4f ()
// generated
		, w4f0 = new Vector4f (), w4f1 = new Vector4f (),
		w4f2 = new Vector4f (), w4f3 = new Vector4f ()
		;
// generated
// generated
// generated
	public final Point4f p4f0 = new Point4f (), p4f1 = new Point4f (),
		p4f2 = new Point4f (), p4f3 = new Point4f ()
// generated
		, q4f0 = new Point4f (), q4f1 = new Point4f (),
		q4f2 = new Point4f (), q4f3 = new Point4f ()
		;
// generated
// generated
// generated
	public final Matrix3f m3f0 = new Matrix3f (), m3f1 = new Matrix3f (),
		m3f2 = new Matrix3f (), m3f3 = new Matrix3f ()
// generated
		, n3f0 = new Matrix3f (), n3f1 = new Matrix3f (),
		n3f2 = new Matrix3f (), n3f3 = new Matrix3f ()
		;
// generated
// generated
// generated
	public final Matrix3d m3d0 = new Matrix3d (), m3d1 = new Matrix3d (),
		m3d2 = new Matrix3d (), m3d3 = new Matrix3d ()
// generated
		, n3d0 = new Matrix3d (), n3d1 = new Matrix3d (),
		n3d2 = new Matrix3d (), n3d3 = new Matrix3d ()
		;
// generated
// generated
// generated
	public final Matrix4f m4f0 = new Matrix4f (), m4f1 = new Matrix4f (),
		m4f2 = new Matrix4f (), m4f3 = new Matrix4f ()
// generated
		, n4f0 = new Matrix4f (), n4f1 = new Matrix4f (),
		n4f2 = new Matrix4f (), n4f3 = new Matrix4f ()
		;
// generated
// generated
// generated
	public final Matrix4d m4d0 = new Matrix4d (), m4d1 = new Matrix4d (),
		m4d2 = new Matrix4d (), m4d3 = new Matrix4d ()
// generated
		, n4d0 = new Matrix4d (), n4d1 = new Matrix4d (),
		n4d2 = new Matrix4d (), n4d3 = new Matrix4d ()
		;
// generated
// generated
// generated
	public final Line2D.Double l0 = new Line2D.Double (), l1 = new Line2D.Double (),
		l2 = new Line2D.Double (), l3 = new Line2D.Double ()
// generated
		;
// generated
// generated
// generated
	public final Rectangle2D.Double r0 = new Rectangle2D.Double (), r1 = new Rectangle2D.Double (),
		r2 = new Rectangle2D.Double (), r3 = new Rectangle2D.Double ()
// generated
		;
// generated
// generated
// generated
	public final Ellipse2D.Double e0 = new Ellipse2D.Double (), e1 = new Ellipse2D.Double (),
		e2 = new Ellipse2D.Double (), e3 = new Ellipse2D.Double ()
// generated
		;
// generated
// generated
// generated
	public final RoundRectangle2D.Double rr0 = new RoundRectangle2D.Double (), rr1 = new RoundRectangle2D.Double (),
		rr2 = new RoundRectangle2D.Double (), rr3 = new RoundRectangle2D.Double ()
// generated
		;
// generated
//!! *# End of generated code

	public final GeneralPath path = new GeneralPath ();
}
