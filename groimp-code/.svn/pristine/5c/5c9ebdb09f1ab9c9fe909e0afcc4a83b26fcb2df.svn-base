
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

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.imp.PickList;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;

public class SphereSegmentSolid extends ShadedNull
	implements Pickable, Renderable
{
	private static final long serialVersionUID = 686382261193185103L;

	protected float radius = 1f;
	//enh:field attr=Attributes.RADIUS getter setter
	protected float theta1 = 1;
	//enh:field attr=Attributes.THETA1 getter setter
	protected float theta2 = -1;
	//enh:field attr=Attributes.THETA2 getter setter
	protected float phi = (float) (Math.PI / 4f);
	//enh:field attr=Attributes.PHI getter setter


	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
	}


	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, SphereSegmentSolid.$TYPE, new NType.Field[] {radius$FIELD, theta1$FIELD, theta2$FIELD, phi$FIELD});
		}

		public static void signature (@In @Out SphereSegmentSolid s, float r, float t1, float t2, float p)
		{
		}
	}


	public SphereSegmentSolid ()
	{
		this (1f);
	}


	public SphereSegmentSolid (float radius)
	{
		this(radius, 1, -1, (float) (Math.PI / 4f));
	}
	
	public SphereSegmentSolid (float radius, float theta1, float theta2, float phi)
	{
		super ();
		setRadius(radius);
		setTheta1(theta1);
		setTheta2(theta2);
		setPhi(phi);
	}


	public static void pick (float radius, Point3d origin, Vector3d direction,
							 PickList list)
	{
		double bx, by, bz, s, t;
		bx = -origin.x;
		by = -origin.y;
		bz = -origin.z;
		s = bx * direction.x + by * direction.y + bz * direction.z;
		t = direction.lengthSquared ();
		bx = s * s + t * (radius * radius - bx * bx - by * by - bz * bz);
		if (bx < 0)
		{
			return;
		}
		bx = (bx <= 0d) ? 0d : Math.sqrt (bx);
		if (s >= bx)
		{
			list.add ((s - bx) / t);
		}
		else if (s + bx >= 0d)
		{
			list.add ((s + bx) / t);
		}
	}


	@Override
	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d t, PickList list)
	{
		GraphState gs = list.getGraphState ();
		if (object == this)
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				pick (radius, origin, direction, list);
			}
			else
			{
				pick (gs.checkFloat (this, true, Attributes.RADIUS, radius),
					  origin, direction, list);
			}
		}
		else
		{
			pick (gs.getFloat (object, asNode, Attributes.RADIUS),
				  origin, direction, list);
		}
	}


	@Override
	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				rs.drawSphereSegmentSolid (radius, theta1, theta2, phi,
						null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
			else
			{
				rs.drawSphereSegmentSolid (
						gs.checkFloat (this, true, Attributes.RADIUS, radius),
						gs.checkFloat (this, true, Attributes.THETA1, theta1),
						gs.checkFloat (this, true, Attributes.THETA2, theta2),
						gs.checkFloat (this, true, Attributes.PHI, phi),
						null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
		}
		else
		{
			rs.drawSphereSegmentSolid (
					gs.getFloat (object, asNode, Attributes.RADIUS),
					gs.getFloat (object, asNode, Attributes.THETA1),
					gs.getFloat (object, asNode, Attributes.THETA2),
					gs.getFloat (object, asNode, Attributes.PHI),
					null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
		}
	}

	

//	enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field radius$FIELD;
	public static final NType.Field theta1$FIELD;
	public static final NType.Field theta2$FIELD;
	public static final NType.Field phi$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (SphereSegmentSolid.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((SphereSegmentSolid) o).setRadius (value);
					return;
				case 1:
					((SphereSegmentSolid) o).setTheta1 (value);
					return;
				case 2:
					((SphereSegmentSolid) o).setTheta2 (value);
					return;
				case 3:
					((SphereSegmentSolid) o).setPhi (value);
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
					return ((SphereSegmentSolid) o).getRadius ();
				case 1:
					return ((SphereSegmentSolid) o).getTheta1 ();
				case 2:
					return ((SphereSegmentSolid) o).getTheta2 ();
				case 3:
					return ((SphereSegmentSolid) o).getPhi ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new SphereSegmentSolid ());
		$TYPE.addManagedField (radius$FIELD = new _Field ("radius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (theta1$FIELD = new _Field ("theta1", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (theta2$FIELD = new _Field ("theta2", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 2));
		$TYPE.addManagedField (phi$FIELD = new _Field ("phi", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 3));
		$TYPE.declareFieldAttribute (radius$FIELD, Attributes.RADIUS);
		$TYPE.declareFieldAttribute (theta1$FIELD, Attributes.THETA1);
		$TYPE.declareFieldAttribute (theta2$FIELD, Attributes.THETA2);
		$TYPE.declareFieldAttribute (phi$FIELD, Attributes.PHI);
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
		return new SphereSegmentSolid ();
	}

	public float getRadius ()
	{
		return radius;
	}

	public void setRadius (float value)
	{
		if(value<0) value=0;
		radius = value;
	}
	
	public float getTheta1 ()
	{
		return theta1;
	}

	public void setTheta1 (float value)
	{
		if(value<theta2) {
			value = theta2+0.01f;
		}
		if(value>Math.PI/2f) {
			value = (float)Math.PI/2f;
		}
		if(value<-Math.PI/2f) {
			value = (float)-Math.PI/2f;
		}
		theta1 = value;
		if(theta1==theta2 && theta1<Math.PI/2f) {
			theta1 = theta1+0.01f;
		}
		if(theta1==theta2 && theta1==Math.PI/2f) {
			theta2 = theta2-0.01f;
		}	
		if(theta1==theta2 && theta1==-Math.PI/2f) {
			theta1 = theta1+0.01f;
		}
	}
	
	public float getTheta2 ()
	{
		return theta2;
	}

	public void setTheta2 (float value)
	{
		if(value>theta1) {
			value = theta1-0.01f;
		}
		if(value>Math.PI/2f) {
			value = (float)Math.PI/2f;
		}
		if(value<-Math.PI/2f) {
			value = (float)-Math.PI/2f;
		}
		theta2 = value;
		if(theta1==theta2 && theta1<Math.PI/2f) {
			theta1 = theta1+0.01f;
		}
		if(theta1==theta2 && theta1==Math.PI/2f) {
			theta2 = theta2-0.01f;
		}	
		if(theta1==theta2 && theta1==-Math.PI/2f) {
			theta1 = theta1+0.01f;
		}
	}
	
	public float getPhi ()
	{
		return phi;
	}

	public void setPhi (float value)
	{
		if(value>Math.PI) {
			value = (float)Math.PI;
		}
		if(value<0) {
			value = 0;
		}
		phi = value;
	}

//enh:end


	/**
	 * Calculates the area of an object.
	 * Intersection with other object are not considered.The total area will be calculated.
	 * A=tbd
	 * 
	 * @return area
	 */
	@Override
	public double getSurfaceArea() {
		return -1;
	}

	/**
	 * Calculates the volume.
	 * Intersection with other object are not considered.The total volume will be calculated.
	 * V=tbd
	 * 
	 * @return volume
	 */
	@Override
	public double getVolume() {
		return -1;
	}
	
}
