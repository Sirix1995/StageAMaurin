
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

import java.util.Random;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.math.TMatrix4d;
import de.grogra.persistence.Manageable;
import de.grogra.persistence.PersistenceField;
import de.grogra.persistence.SCOType;
import de.grogra.persistence.Shareable;
import de.grogra.persistence.SharedObjectProvider;
import de.grogra.persistence.SharedObjectReference;
import de.grogra.persistence.Transaction;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Sensor;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;
import de.grogra.xl.util.ObjectList;

public class Camera extends CameraBase implements Shareable, Manageable, Sensor
{
	//enh:sco SCOType

	protected float minZ = 0.1f;
	//enh:field quantity=LENGTH

	protected float maxZ = 2000;
	//enh:field quantity=LENGTH

	protected Projection projection;
	//enh:field type=Projection.$TYPE getter setter

	protected final Matrix4d transformation = new TMatrix4d ();
	//enh:field type=TMatrix4d.$TYPE set=set getter setter

	private transient int stamp = 0;

//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;

	public static final Type.Field minZ$FIELD;
	public static final Type.Field maxZ$FIELD;
	public static final Type.Field projection$FIELD;
	public static final Type.Field transformation$FIELD;

	public static class Type extends SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (Camera representative, de.grogra.persistence.SCOType supertype)
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
		protected void setFloat (Object o, int id, float value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 0:
					((Camera) o).minZ = value;
					return;
				case Type.SUPER_FIELD_COUNT + 1:
					((Camera) o).maxZ = value;
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
					return ((Camera) o).minZ;
				case Type.SUPER_FIELD_COUNT + 1:
					return ((Camera) o).maxZ;
			}
			return super.getFloat (o, id);
		}

		@Override
		protected void setObject (Object o, int id, Object value)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					((Camera) o).projection = (Projection) Projection.$TYPE.setObject (((Camera) o).projection, value);
					return;
				case Type.SUPER_FIELD_COUNT + 3:
					((Camera) o).transformation.set ((Matrix4d) value);
					return;
			}
			super.setObject (o, id, value);
		}

		@Override
		protected Object getObject (Object o, int id)
		{
			switch (id)
			{
				case Type.SUPER_FIELD_COUNT + 2:
					return ((Camera) o).getProjection ();
				case Type.SUPER_FIELD_COUNT + 3:
					return ((Camera) o).getTransformation ();
			}
			return super.getObject (o, id);
		}

		@Override
		public Object newInstance ()
		{
			return new Camera ();
		}

	}

	@Override
	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (Camera.class);
		minZ$FIELD = Type._addManagedField ($TYPE, "minZ", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 0);
		maxZ$FIELD = Type._addManagedField ($TYPE, "maxZ", Type.Field.PROTECTED  | Type.Field.SCO, de.grogra.reflect.Type.FLOAT, null, Type.SUPER_FIELD_COUNT + 1);
		projection$FIELD = Type._addManagedField ($TYPE, "projection", Type.Field.PROTECTED  | Type.Field.SCO, Projection.$TYPE, null, Type.SUPER_FIELD_COUNT + 2);
		transformation$FIELD = Type._addManagedField ($TYPE, "transformation", Type.Field.PROTECTED | Type.Field.FINAL  | Type.Field.SCO, TMatrix4d.$TYPE, null, Type.SUPER_FIELD_COUNT + 3);
		minZ$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		maxZ$FIELD.setQuantity (de.grogra.util.Quantity.LENGTH);
		$TYPE.validate ();
	}

	public Projection getProjection ()
	{
		return projection;
	}

	public void setProjection (Projection value)
	{
		projection$FIELD.setObject (this, value);
	}

	public Matrix4d getTransformation ()
	{
		return transformation;
	}

	public void setTransformation (Matrix4d value)
	{
		transformation$FIELD.setObject (this, value);
	}

//enh:end


	protected transient SharedObjectProvider sop;
	private transient ObjectList refs = null;


	public Camera ()
	{
		this (new PerspectiveProjection ());
	}


	public Camera (Projection p)
	{
		this.projection = p;
		transformation.rotX (-0.25 * Math.PI);
		transformation.setColumn (3, 0, 0, -8, 1);
		update ();
	}


	public static Camera createPerspective ()
	{
		return new Camera (new PerspectiveProjection ());
	}


	public static Camera createParallel ()
	{
		return new Camera (new ParallelProjection ());
	}


	public static Camera createTopView ()
	{
		Camera c = new Camera (new ParallelProjection ());
		Matrix4d m = c.transformation;
		m.setIdentity ();
		m.m23 = -10;
		c.update ();
		return c;
	}


	public static Camera createBottomView ()
	{
		Camera c = new Camera (new ParallelProjection ());
		Matrix4d m = c.transformation;
		m.setIdentity ();
		m.m11 = -1;
		m.m22 = -1;
		m.m23 = -10;
		c.update ();
		return c;
	}


	public static Camera createLeftView ()
	{
		Camera c = new Camera (new ParallelProjection ());
		Matrix4d m = c.transformation;
		m.setIdentity ();
		m.m00 = 0;
		m.m01 = -1;
		m.m11 = 0;
		m.m12 = 1;
		m.m20 = -1;
		m.m22 = 0;
		m.m23 = -10;
		c.update ();
		return c;
	}


	public static Camera createRightView ()
	{
		Camera c = new Camera (new ParallelProjection ());
		Matrix4d m = c.transformation;
		m.setIdentity ();
		m.m00 = 0;
		m.m01 = 1;
		m.m11 = 0;
		m.m12 = 1;
		m.m20 = 1;
		m.m22 = 0;
		m.m23 = -10;
		c.update ();
		return c;
	}


	public static Camera createFrontView ()
	{
		Camera c = new Camera (new ParallelProjection ());
		Matrix4d m = c.transformation;
		m.setIdentity ();
		m.m11 = 0;
		m.m22 = 0;
		m.m12 = 1;
		m.m21 = -1;
		m.m23 = -10;
		c.update ();
		return c;
	}


	public static Camera createBackView ()
	{
		Camera c = new Camera (new ParallelProjection ());
		Matrix4d m = c.transformation;
		m.setIdentity ();
		m.m00 = -1;
		m.m11 = 0;
		m.m12 = 1;
		m.m21 = 1;
		m.m22 = 0;
		m.m23 = -10;
		c.update ();
		return c;
	}


	@Override
	public void initProvider (SharedObjectProvider provider)
	{
		if (sop != null)
		{
			throw new IllegalStateException ();
		}
		sop = provider;
	}


	@Override
	public SharedObjectProvider getProvider ()
	{
		return sop;
	}


	@Override
	public synchronized void addReference (SharedObjectReference ref)
	{
		if (refs == null)
		{
			refs = new ObjectList (4, false);
		}
		refs.add (ref);
	}

	
	@Override
	public synchronized void removeReference (SharedObjectReference ref)
	{
		if (refs != null)
		{
			refs.remove (ref);
		}
	}

	
	@Override
	public synchronized void appendReferencesTo (java.util.List out)
	{
		if (refs != null)
		{
			out.addAll (refs);
		}
	}


	@Override
	public void fieldModified (PersistenceField field, int[] indices, Transaction t)
	{
		stamp++;
		update ();
		if ((t != null) && (sop != null))
		{
			t.fireSharedObjectModified (this);
		}
	}
	
	
	@Override
	public int getStamp ()
	{
		return stamp;
	}


	public void update ()
	{
		projection.getTransformation (minZ, maxZ, viewToClip, deviceToView);
		worldToClip.mul (viewToClip, transformation);
		Math2.invertAffine (transformation, viewToWorld);
	}

	
	@Override
	public Manageable manageableReadResolve ()
	{
		update ();
		return this;
	}

	@Override
	public Object manageableWriteReplace ()
	{
		return this;
	}

	private transient Matrix4d worldToClip = new Matrix4d ();
	private transient Matrix4d viewToClip = new Matrix4d ();
	private transient Matrix4d deviceToView = new Matrix4d ();
	private transient Matrix4d viewToWorld = new Matrix4d ();


	@Override
	public float getZNear ()
	{
		return minZ;
	}


	@Override
	public float getZFar ()
	{
		return maxZ;
	}


	public void setZNear (float minZ)
	{
		this.minZ = minZ;
	}


	public void setZFar (float maxZ)
	{
		this.maxZ = maxZ;
	}


	@Override
	public Matrix4d getWorldToViewTransformation ()
	{
		return transformation;
	}


	/**
	 * Computes the transformation from view
	 * coordinates (= camera coordinates, not world coordinates!)
	 * to clip coordinates. 
	 * 
	 * @param m the transformation is placed in here
	 */
	public void getViewToClipTransformation (Matrix4d m)
	{
		projection.getTransformation (minZ, maxZ, m, null);
	}


	public boolean projectWorld (double px, double py, double pz, Tuple2f point2D)
	{
		Matrix4d m = worldToClip;
		float w = 1 / (float) (m.m30 * px + m.m31 * py + m.m32 * pz + m.m33);
		float z = w * (float) (m.m20 * px + m.m21 * py + m.m22 * pz + m.m23);
		if ((z > 1) || (z < -1))
		{
			return false;
		}
		float x = w * (float) (m.m00 * px + m.m01 * py + m.m02 * pz + m.m03);
		float y = w * (float) (m.m10 * px + m.m11 * py + m.m12 * pz + m.m13);
		point2D.x = x;
		point2D.y = y;
		return true;
	}


	public boolean projectView (float px, float py, float pz, Tuple2f point2D,
							boolean checkClip)
	{
		Matrix4d m = viewToClip;
		float w = 1 / (float) (m.m30 * px + m.m31 * py + m.m32 * pz + m.m33);
		if (checkClip)
		{
			float z = w * (float) (m.m20 * px + m.m21 * py + m.m22 * pz + m.m23);
			if ((z > 1) || (z < -1))
			{
				return false;
			}
		}
		float x = w * (float) (m.m00 * px + m.m01 * py + m.m02 * pz + m.m03);
		float y = w * (float) (m.m10 * px + m.m11 * py + m.m12 * pz + m.m13);
		point2D.x = x;
		point2D.y = y;
		return true;
	}


	@Override
	public float getScaleAt (double x, double y, double z)
	{
		Matrix4d m = transformation;
		return (float) m.getScale () * projection.getScaleAt
			((float) (m.m20 * x + m.m21 * y + m.m22 * z + m.m23));
	}


	@Override
	public float getScaleAt (float z)
	{
		return projection.getScaleAt (z);
	}


	@Override
	public void getRay (float x, float y, Point3d origin, Vector3d direction)
	{
		projection.getRayInViewCoordinates (x, y, origin, direction, deviceToView, null);
		viewToWorld.transform (origin);
		viewToWorld.transform (direction);
		direction.normalize ();
	}


	@Override
	public double computeExitance (Environment env, Spectrum exitance)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void generateRandomOrigins (Environment env, RayList out, Random rnd)
	{
		projection.generateRandomOrigins (env, out, rnd, deviceToView);
	}


	@Override
	public int getFlags ()
	{
		return NEEDS_POINT | NEEDS_TRANSFORMATION | NEEDS_UV;
	}


	@Override
	public int getAverageColor ()
	{
		return 0;
	}


	@Override
	public void generateRandomRays (Environment env, Vector3f out, Spectrum specOut, RayList rays, boolean adjoint, Random rnd)
	{
		projection.generateRandomRays (env, out, specOut, rays, rnd, deviceToView);
	}


	@Override
	public float computeBSDF (Environment env, Vector3f in, Spectrum specIn, Vector3f out, boolean adjoint, Spectrum bsdf)
	{
		return projection.computeBSDF (env, specIn, out, bsdf);
	}
	
	@Override
	public double completeRay (Environment env, Point3d vertex, Ray out)
	{
		return projection.completeRay (env, vertex, out, viewToClip, deviceToView);
	}
	
	@Override
	public float[] getUVForVertex(Environment env, Point3d vertex){
		return projection.getUVForVertex(env,vertex, viewToClip,deviceToView);
	}

}
