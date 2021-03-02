
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

import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.graph.GraphState;
import de.grogra.graph.ObjectAttribute;
import de.grogra.graph.impl.Node.NType;
import de.grogra.imp.PickList;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.ray.Raytraceable;
import de.grogra.imp3d.ray.RaytracerLeaf;
import de.grogra.imp3d.shading.Light;
import de.grogra.imp3d.shading.LightVisitor;
import de.grogra.math.Tuple3fType;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.Math2;

public class Parallelogram extends Axis
	implements Pickable, Renderable, Light, Raytraceable
{
	final Vector3f axis = new Vector3f (1, 0, 0);
	//enh:field type=Tuple3fType.VECTOR set=set attr=Attributes.AXIS getter setter
	
	protected AreaLight light;
	//enh:field attr=Attributes.AREA_LIGHT getter setter

	private static void initType ()
	{
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
		$TYPE.addAccessor (new AccessorBridge (Attributes.LIGHT));
	}

//enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field axis$FIELD;
	public static final NType.Field light$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Parallelogram.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Parallelogram) o).axis.set ((Vector3f) value);
					return;
				case 1:
					((Parallelogram) o).light = (AreaLight) value;
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
					return ((Parallelogram) o).getAxis ();
				case 1:
					return ((Parallelogram) o).getLight ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Parallelogram ());
		$TYPE.addManagedField (axis$FIELD = new _Field ("axis", _Field.FINAL  | _Field.SCO, Tuple3fType.VECTOR, null, 0));
		$TYPE.addManagedField (light$FIELD = new _Field ("light", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (AreaLight.class), null, 1));
		$TYPE.declareFieldAttribute (axis$FIELD, Attributes.AXIS);
		$TYPE.declareFieldAttribute (light$FIELD, Attributes.AREA_LIGHT);
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
		return new Parallelogram ();
	}

	public Vector3f getAxis ()
	{
		return axis;
	}

	public void setAxis (Vector3f value)
	{
		axis$FIELD.setObject (this, value);
	}

	public AreaLight getLight ()
	{
		return light;
	}

	public void setLight (AreaLight value)
	{
		light$FIELD.setObject (this, value);
	}

//enh:end

	public Parallelogram ()
	{
	}


	public Parallelogram (float length, float width)
	{
		setLength (length);
		setAxis (width * 0.5f, 0, 0);
	}


	public void setAxis (float x, float y, float z)
	{
		axis.set (x, y, z);
	}


	public static void pick (float height, Vector3f axis,
							 Point3d origin, Vector3d direction, PickList list)
	{
		Matrix3f m = list.n3f0;
		m.setColumn (0, axis);
		m.setColumn (1, 0, 0, height);
		Vector3f v = list.w3f0;
		v.set (direction);
		m.setColumn (2, v);
		v.set (origin);
		try
		{
			m.invert ();
			m.transform (v);
			if ((-1 <= v.x) && (v.x <= 1) && (0 <= v.y) && (v.y <= 1)
				&& (v.z < 0))
			{
				list.add (-v.z);
			}
		}
		catch (RuntimeException e)
		{
		}
	}


	public void pick (Object object, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d t, PickList list)
	{
		GraphState gs = list.getGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				pick (length, axis, origin, direction, list);
			}
			else
			{
				pick ((float) gs.checkDouble (this, true, Attributes.LENGTH, length),
					  (Vector3f) gs.checkObject (this, true, Attributes.AXIS, axis),
					  origin, direction, list);
			}
		}
		else
		{
			pick ((float) gs.getDouble (object, asNode, Attributes.LENGTH),
				  (Vector3f) gs.getObject (object, asNode, list.v3f1, Attributes.AXIS),
				  origin, direction, list);
		}
	}


	public void draw (Object object, boolean asNode, RenderState rs)
	{
		GraphState gs = rs.getRenderGraphState ();
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				rs.drawParallelogram (length, axis,
									  1, isScaleV () ? length : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
			}
			else
			{
				float len = (float) gs.checkDouble (this, true, Attributes.LENGTH, length);
				rs.drawParallelogram ((float) gs.checkDouble (this, true, Attributes.LENGTH, length),
									  (Vector3f) gs.checkObject (this, true, Attributes.AXIS, axis),	
									  1, gs.checkBoolean (this, true, Attributes.SCALE_V, isScaleV ()) ? len : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
		else
		{
			float len = (float) gs.getDouble (object, asNode, Attributes.LENGTH);
			rs.drawParallelogram ((float) gs.getDouble (object, asNode, Attributes.LENGTH),
								  (Vector3f) gs.getObject (object, asNode, rs.getPool ().v3f1, Attributes.AXIS),
								  1, gs.getBoolean (object, asNode, Attributes.SCALE_V) ? len : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
		}
	}


	@Override
	protected Object getObject (ObjectAttribute a, Object placeIn, GraphState gs)
	{
		if (a == Attributes.LIGHT)
		{
			return (light != null) ? this : null;
		}
		else
		{
			return super.getObject (a, placeIn, gs);
		}
	}


	public int getLightType ()
	{
		return AREA;
	}


	public boolean isShadowless ()
	{
		return light.isShadowless ();
	}

	public boolean isIgnoredWhenHit ()
	{
		return light.isIgnoredWhenHit ();
	}

	public int getAverageColor ()
	{
		return 0;
	}

	
	public int getFlags ()
	{
		return NEEDS_TRANSFORMATION;
	}

	
	public float getArea ()
	{
		return 2 * axis.length () * length;
	}

	public void generateRandomOrigins (Environment env, RayList out, Random rnd)
	{
		Vector3f v = env.userVector, w = env.userVector2, q = env.userVector3;
		v.set (0, 0, length);
		env.localToGlobal.transform (v, v);
		env.localToGlobal.transform (axis, w);
		q.cross (v, w);
		float area = 2 * q.length ();
		Spectrum s = out.rays[0].spectrum;
		light.computeExitance (getShader (), area, s);
		s.scale (area);
		for (int i = out.getSize() - 1; i >= 0; i--)
		{
			Point3f p = out.rays[i].origin;
			int j = rnd.nextInt ();
			p.scale ((j >>> 16) * (1f / 0x10000), v);
			p.scaleAdd ((char) j * (2f / 0x10000) - 1, w, p);
			p.x += env.localToGlobal.m03;
			p.y += env.localToGlobal.m13;
			p.z += env.localToGlobal.m23;
			out.rays[i].spectrum.set (s);
			out.rays[i].originDensity = 1 / area;
		}
	}


	public double computeExitance (Environment env, Spectrum exitance)
	{
		Vector3f v = env.userVector, w = env.userVector2, q = env.userVector3;
		v.set (0, 0, length);
		env.localToGlobal.transform (v, v);
		env.localToGlobal.transform (axis, w);
		q.cross (v, w);
		float area = 2 * q.length ();
		light.computeExitance (getShader (), area, exitance);
		return 1 / area;
	}


	public void generateRandomRays (Environment env, Vector3f out, Spectrum specOut, RayList rays,
									boolean photon, Random rnd)
	{
		Vector3f v = env.userVector, w = env.userVector2, q = env.userVector3;
		v.set (0, 0, length);
		env.localToGlobal.transform (v, v);
		env.localToGlobal.transform (axis, w);
		q.cross (v, w);
		Matrix3f basis = env.userMatrix;
		Math2.getOrthogonalBasis (q, basis, true);

		for (int i = rays.getSize() - 1; i >= 0; i--)
		{
			int j = rnd.nextInt ();
			double t = (1d / 0x10000) * ((j >>> 16) + 1);
			rays.rays[i].directionDensity = (light.exponent + 2)
				* (float) Math.pow (t, (light.exponent + 1) / (light.exponent + 2))
				* (float) (1 / (2 * Math.PI));
			float cost = (float) Math.pow (t, 1 / (light.exponent + 2)),
				sint = (float) Math.sqrt (1 - cost * cost);
			char phi = (char) j;
			q.set (Math2.ccos (phi) * sint,
					Math2.csin (phi) * sint, cost);
			basis.transform (q, rays.rays[i].direction);
			rays.rays[i].spectrum.set (specOut);
		}
	}


	public float computeBSDF (Environment env, Vector3f in, Spectrum specIn, Vector3f out,
							  boolean photon, Spectrum bsdf)
	{
		Vector3f v = env.userVector, w = env.userVector2, q = env.userVector3;
		v.set (0, 0, length);
		env.localToGlobal.transform (v, v);
		env.localToGlobal.transform (axis, w);
		q.cross (v, w);
		float b = q.dot (out);
		if (b <= 0)
		{
			bsdf.setZero ();
			return 0;
		}
		b = (light.exponent + 2) * (float) Math.pow (b / q.length (), light.exponent + 1)
			* (float) (1 / (2 * Math.PI));
		bsdf.set (specIn);
		bsdf.scale (b);
		return b;
	}

	public double getTotalPower (Environment env)
	{
		return light.power;
	}

	public double completeRay (Environment env, Point3d vertex, Ray out)
	{
		throw new UnsupportedOperationException ();
	}

	public RaytracerLeaf createRaytracerLeaf(Object object, boolean asNode, long pathId, GraphState gs) {
				
		// ... if this is a visible parallelogram
		Vector3f axis1 = new Vector3f ();
		Vector3f axis2 = null;
		//float start1   = 0.0f;
		//float end1     = 0.0f;

		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				axis1.z= this.length;
				axis2  = this.axis;
				//start1 = this.startPosition;
				//end1   = this.endPosition;
			}
			else
			{
				axis1.z= (float)gs.checkDouble(this,true,Attributes.LENGTH,this.length);
				axis2  = (Vector3f)gs.checkObject(this,true,Attributes.AXIS,this.axis);
				//start1 = gs.checkFloat(this,true,Attributes.START_POSITION,this.startPosition);
				//end1   = gs.checkFloat(this,true,Attributes.END_POSITION,this.endPosition);
			}
		}
		else
		{
			axis1.z= (float)gs.getDouble(object,asNode,Attributes.LENGTH);
			axis2  = (Vector3f)gs.getObject(object,asNode,Attributes.AXIS);
			//start1 = gs.getFloat(object,asNode,Attributes.START_POSITION);
			//end1   = gs.getFloat(object,asNode,Attributes.END_POSITION);
		}				
		
		if (this.light!=null) { 
			// if this is interpreted as area light
			return new de.grogra.imp3d.ray.RTAreaLight(object, asNode, pathId, 
					this, axis1, axis2);
		} else {
			return new de.grogra.imp3d.ray.RTParallelogram(object, asNode, pathId, 
					axis1, axis2);
		}
	}
	
	public void accept(LightVisitor visitor) {
		visitor.visit( this );
	}


}
