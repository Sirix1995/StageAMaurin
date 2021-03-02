package de.grogra.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;

import de.grogra.persistence.*;
import de.grogra.vecmath.Math2;
import de.grogra.xl.lang.ConversionConstructor;

public class ComponentTransform extends ShareableBase implements Transform3D {
	
	//enh:sco SCOType
	
	Vector3d translation;
	//enh:field getter setter

	Vector3d rotation;
	//enh:field getter setter

	Vector3d scale;
	//enh:field getter setter
	
	Vector3d shear;
	//enh:field getter setter

	Matrix4d tmp1 = new Matrix4d();
	Matrix4d tmp2 = new Matrix4d();
	
	public ComponentTransform() {
		translation = new Vector3d(0, 0, 0);
		rotation = new Vector3d(0, 0, 0);
		scale = new Vector3d(1, 1, 1);
		shear = new Vector3d(0, 0, 0);
	}
	
	@ConversionConstructor
	public ComponentTransform(Tuple3d t) {
		this();
		this.translation.set(t);
	}
	
	@ConversionConstructor
	public ComponentTransform(Matrix3d m) {
		this.setRotationScaleShear(m);		
	}
	
	@ConversionConstructor
	public ComponentTransform(Matrix4d m) {
		this();
		Matrix3d m1 = new Matrix3d();
		Vector3d t1 = new Vector3d();
		m.get(t1);
		m.getRotationScale(m1);
		this.translation.set(t1);
		this.setRotationScaleShear(m1);
	}
	
	private void setRotationScaleShear(Matrix3d m) {
		Matrix3d r = new Matrix3d(m);
		Matrix3d q = new Matrix3d();
		Math2.decomposeQR(r, q);
		
		// set rotation
		rotation.y = -Math.asin(q.m20);
		rotation.x = Math.atan2(q.m21, q.m22);
		rotation.z = Math.atan2(q.m10, q.m00);

		// set scale
		this.scale.set(r.m00, r.m11, r.m22);
		
		// set shear
		this.shear.set(r.m01 / r.m00, r.m02 / r.m00, r.m12 / r.m11);
	}

	public void transform(Matrix4d in, Matrix4d out) {
		tmp1.setIdentity();
		
		// translation
		tmp1.setTranslation(translation);

		// rotation
		tmp2.setIdentity();
		tmp2.rotZ(rotation.z);
		tmp1.mul(tmp2);
		tmp2.rotY(rotation.y);
		tmp1.mul(tmp2);
		tmp2.rotX(rotation.x);
		tmp1.mul(tmp2);
		
		// scale
		tmp2.setIdentity();
		tmp2.setElement(0, 0, scale.x);
		tmp2.setElement(1, 1, scale.y);
		tmp2.setElement(2, 2, scale.z);
		tmp1.mul(tmp2);
		
		// shear
		tmp2.setIdentity();
		tmp2.m01 = shear.x;
		tmp2.m02 = shear.y;
		tmp2.m12 = shear.z;
		tmp1.mul(tmp2);	
		
		Math2.mulAffine (out, in, tmp1);
	}
	
	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field translation$FIELD;
	public static final Type.Field rotation$FIELD;
	public static final Type.Field scale$FIELD;
	public static final Type.Field shear$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (ComponentTransform representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, SCOType.$TYPE);
		}

		private static final int SUPER_FIELD_COUNT = SCOType.FIELD_COUNT;
		protected static final int FIELD_COUNT = SCOType.FIELD_COUNT + 4;

		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((ComponentTransform) o).translation = (Vector3d) value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((ComponentTransform) o).rotation = (Vector3d) value;
					return;
				case Type.SUPER_FIELD_COUNT + 2:
					((ComponentTransform) o).scale = (Vector3d) value;
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((ComponentTransform) o).shear = (Vector3d) value;
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					return ((ComponentTransform) o).getTranslation ();
				case Type.SUPER_FIELD_COUNT + 1:
					return ((ComponentTransform) o).getRotation ();
				case Type.SUPER_FIELD_COUNT + 2:
					return ((ComponentTransform) o).getScale ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((ComponentTransform) o).getShear ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new ComponentTransform ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (ComponentTransform.class);
		translation$FIELD = Type._addManagedField ($TYPE, "translation", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Vector3d.class), null, Type.SUPER_FIELD_COUNT + 0);
		rotation$FIELD = Type._addManagedField ($TYPE, "rotation", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Vector3d.class), null, Type.SUPER_FIELD_COUNT + 1);
		scale$FIELD = Type._addManagedField ($TYPE, "scale", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Vector3d.class), null, Type.SUPER_FIELD_COUNT + 2);
		shear$FIELD = Type._addManagedField ($TYPE, "shear", 0 | Type.Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Vector3d.class), null, Type.SUPER_FIELD_COUNT + 3);
		$TYPE.validate ();
	}

	public Vector3d getTranslation ()
	{
		return translation;
	}

	public void setTranslation (Vector3d value)
	{
		translation$FIELD.setObject (this, value);
	}

	public Vector3d getRotation ()
	{
		return rotation;
	}

	public void setRotation (Vector3d value)
	{
		rotation$FIELD.setObject (this, value);
	}

	public Vector3d getScale ()
	{
		return scale;
	}

	public void setScale (Vector3d value)
	{
		scale$FIELD.setObject (this, value);
	}

	public Vector3d getShear ()
	{
		return shear;
	}

	public void setShear (Vector3d value)
	{
		shear$FIELD.setObject (this, value);
	}

//enh:end

}
