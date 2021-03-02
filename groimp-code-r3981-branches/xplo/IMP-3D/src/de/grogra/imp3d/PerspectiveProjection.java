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

package de.grogra.imp3d;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Vector3d;

import de.grogra.ray.physics.Scattering;
import de.grogra.vecmath.Math2;

public class PerspectiveProjection extends Projection
{
	//enh:sco

	protected float fieldOfView;
	//enh:field quantity=ANGLE setmethod=setFieldOfView

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field fieldOfView$FIELD;

	public static class Type extends Projection.Type
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (PerspectiveProjection representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, Projection.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = Projection.Type.FIELD_COUNT;
		protected static final int FIELD_COUNT = Projection.Type.FIELD_COUNT + 1;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((PerspectiveProjection) o).setFieldOfView ((float) value);
					return;
			}
			super.setFloat (o, id, value);
		}

		@Override
		protected float getFloat (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((PerspectiveProjection) o).fieldOfView;
			}
			return super.getFloat (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new PerspectiveProjection ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (PerspectiveProjection.class);
		fieldOfView$FIELD = Type._addManagedField ($TYPE, "fieldOfView", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		fieldOfView$FIELD.setQuantity (de.grogra.util.Quantity.ANGLE);
		$TYPE.validate ();
	}

//enh:end

	public PerspectiveProjection ()
	{
		this ((float) (Math.PI / 3), 1);
	}

	public PerspectiveProjection (float fieldOfView, float aspect)
	{
		super ();
		this.aspect = aspect;
		setFieldOfView (fieldOfView);
	}

	public void setFieldOfView (float fieldOfView)
	{
		this.fieldOfView = fieldOfView;
		update ();
	}

	public static float clampFieldOfView (float fieldOfView)
	{
		return Math.max (Math.min (fieldOfView, 0.49f * (float) Math.PI), 0.001f * (float) Math.PI);
	}

	public float getFieldOfView ()
	{
		return fieldOfView;
	}

	@Override
	protected void update ()
	{
		sx = 1 / (float) Math.tan (0.5 * clampFieldOfView (fieldOfView));
		sy = sx / aspect;
	}

	@Override
	protected void getTransformation (float near, float far, Matrix4d viewToClip, Matrix4d deviceToView)
	{
		viewToClip.setIdentity ();
		viewToClip.m00 = sx;
		viewToClip.m11 = sy;
		viewToClip.m22 = -(far + near) / (far - near);
		viewToClip.m23 = -2 * far * near / (far - near);
		viewToClip.m32 = -1;
		viewToClip.m33 = 0;
		if (deviceToView != null)
		{
			deviceToView.set (viewToClip);
			deviceToView.m20 = deviceToView.m30;
			deviceToView.m21 = deviceToView.m31;
			deviceToView.m22 = deviceToView.m32;
			deviceToView.m23 = deviceToView.m33;
			Math2.invertAffine (deviceToView, deviceToView);
		}
	}

	@Override
	public float getScaleAt (float z)
	{
		return (float) Math.sqrt (Math.abs (sx * sy) / (z * z));
	}

	@Override
	protected void getRayInViewCoordinates (float x, float y, Point3d origin,
			Vector3d direction, Matrix4d deviceToView, Tuple2d densities)
	{
		origin.x = deviceToView.m03;
		origin.y = deviceToView.m13;
		origin.z = deviceToView.m23;
		direction.set (x, y, 1);
		deviceToView.transform (direction);
		origin.scaleAdd (-origin.z / direction.z, direction, origin);
		if (densities != null)
		{
			densities.x = Scattering.DELTA_FACTOR;
			double icos = direction.length () / -direction.z;
			densities.y = 0.25 * icos * icos * icos;
		}
	}

}