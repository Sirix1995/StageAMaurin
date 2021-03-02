
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
import javax.vecmath.Vector3f;

import de.grogra.graph.ContextDependent;
import de.grogra.graph.GraphState;
import de.grogra.imp.PickList;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.ray.Raytraceable;
import de.grogra.imp3d.ray.RaytracerLeaf;
import de.grogra.xl.util.ByteList;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

public class Cylinder extends FrustumBase implements Raytraceable, Polygonizable
{
	protected float radius;
	//enh:field attr=Attributes.RADIUS getter setter

	// boolean baseOpen
	//enh:field type=bits(BASE_OPEN_MASK) attr=Attributes.BASE_OPEN getter setter

	// boolean topOpen
	//enh:field type=bits(TOP_OPEN_MASK) attr=Attributes.TOP_OPEN getter setter

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, Cylinder.$TYPE, new NType.Field[] {length$FIELD, radius$FIELD});
		}

		public static void signature (@In @Out Cylinder c, float l, float r)
		{
		}
	}

	public Cylinder ()
	{
		this (1, 1);
	}


	public Cylinder (float length, float radius)
	{
		super ();
		setLength (length);
		this.radius = radius;
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
				pick (length, radius, radius,
					  (bits & BASE_OPEN_MASK) == 0, (bits & TOP_OPEN_MASK) == 0,
					  origin, direction, list);
			}
			else
			{
				float r = gs.checkFloat (this, true, Attributes.RADIUS, radius);
				pick ((float) gs.checkDouble (this, true, Attributes.LENGTH, length),
					  r, r,
					  !gs.checkBoolean (this, true, Attributes.BASE_OPEN, (bits & BASE_OPEN_MASK) != 0),
					  !gs.checkBoolean (this, true, Attributes.TOP_OPEN, (bits & TOP_OPEN_MASK) != 0),
					  origin, direction, list);
			}
		}
		else
		{
			float r = gs.getFloat (object, asNode, Attributes.RADIUS);
			pick ((float) gs.getDouble (object, asNode, Attributes.LENGTH),
				  r, r,
				  !gs.getBoolean (object, asNode, Attributes.BASE_OPEN),
				  !gs.getBoolean (object, asNode, Attributes.TOP_OPEN),
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
				rs.drawFrustum (length, radius, radius,
								(bits & BASE_OPEN_MASK) == 0, (bits & TOP_OPEN_MASK) == 0,
								isScaleV () ? length : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
			}
			else
			{
				float len = (float) gs.checkDouble (this, true, Attributes.LENGTH, length);
				float r = gs.checkFloat (this, true, Attributes.RADIUS, radius);
				rs.drawFrustum (len,
								r, r,
								!gs.checkBoolean (this, true, Attributes.BASE_OPEN, (bits & BASE_OPEN_MASK) != 0),
								!gs.checkBoolean (this, true, Attributes.TOP_OPEN, (bits & TOP_OPEN_MASK) != 0),
								gs.checkBoolean (this, true, Attributes.SCALE_V, isScaleV ()) ? len : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
			}
		}
		else
		{
			float len = (float) gs.getDouble (object, asNode, Attributes.LENGTH);
			float r = gs.getFloat (object, asNode, Attributes.RADIUS);
			rs.drawFrustum (len,
							r, r,
							!gs.getBoolean (object, asNode, Attributes.BASE_OPEN),
							!gs.getBoolean (object, asNode, Attributes.TOP_OPEN),
							gs.getBoolean (object, asNode, Attributes.SCALE_V) ? len : 1, null, RenderState.CURRENT_HIGHLIGHT, null);
		}
	}

	@Override
	public RaytracerLeaf createRaytracerLeaf(Object object, boolean asNode, long pathId, GraphState gs) {
		
		float cur_radius = 1.0f;
		Vector3f cur_axis = new Vector3f ();
		boolean open_top = false;
		boolean open_bottom = false;
		
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				cur_radius  = this.radius;
				cur_axis.z  = this.length;
				open_top    = this.isTopOpen();
				open_bottom = this.isBaseOpen();
			}
			else
			{
				cur_radius  = gs.checkFloat(this,true,Attributes.RADIUS,this.radius);
				cur_axis.z  = (float)gs.checkDouble(this,true,Attributes.LENGTH,this.length);
				open_top    = gs.checkBoolean(this,true,Attributes.TOP_OPEN,this.isTopOpen());
				open_bottom = gs.checkBoolean(this,true,Attributes.BASE_OPEN,this.isBaseOpen());
			}
		}
		else
		{
			cur_radius  = gs.getFloat (object, asNode, Attributes.RADIUS);
			cur_axis.z  = (float)gs.getDouble(object,asNode,Attributes.LENGTH);
			open_top    = gs.getBoolean(object,asNode,Attributes.TOP_OPEN);
			open_bottom = gs.getBoolean(object,asNode,Attributes.BASE_OPEN);
		}
		
		return new de.grogra.imp3d.ray.RTCylinder(object,asNode,pathId,
				cur_radius,cur_axis,open_top,open_bottom);
	}
	
	@Override
	public ContextDependent getPolygonizableSource(GraphState gs) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Polygonization getPolygonization() {
		final class Poly implements Polygonization
		{
			final float radius = Cylinder.this.radius;
			final int bits = Cylinder.this.bits;

			@Override
			public void polygonize (ContextDependent source, GraphState gs, PolygonArray out, int flags, float flatness)
			{
				polygonizeImpl (source, gs, out, flags, flatness);
			}

			@Override
			public boolean equals (Object o)
			{
				if (!(o instanceof Poly))
				{
					return false;
				}
				Poly p = (Poly) o;
				return ((p.radius == radius) && (p.bits == bits));
			}

			@Override
			public int hashCode ()
			{
				return Float.floatToIntBits (radius) ^ bits;
			}
		}

		return new Poly ();
	}

	void polygonizeImpl (ContextDependent source, GraphState gs, PolygonArray out,
			 int flags, float flatness)
	{
		float r;
		float l;
		boolean baseClosed;
		boolean topClosed;
		if (gs.getObjectContext ().getObject () == this)
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				r = this.radius;
				l = this.length;
				baseClosed = (bits & BASE_OPEN_MASK) == 0;
				topClosed = (bits & TOP_OPEN_MASK) == 0;
			}
			else
			{
				r = gs.checkFloat (this, true, Attributes.RADIUS, this.radius);
				l = (float) gs.checkDouble (this, true, Attributes.LENGTH, this.length);
				baseClosed = !gs.checkBoolean (this, true, Attributes.BASE_OPEN, (bits & BASE_OPEN_MASK) != 0);
				topClosed = !gs.checkBoolean (this, true, Attributes.TOP_OPEN, (bits & TOP_OPEN_MASK) != 0);
			}
		}
		else
		{
			r = gs.getFloatDefault (gs.getObjectContext ().getObject (), gs.getObjectContext ().isNode (), Attributes.RADIUS, this.radius);
			l = (float) gs.getDoubleDefault (gs.getObjectContext ().getObject (), gs.getObjectContext ().isNode (), Attributes.LENGTH, this.length);
			baseClosed = !gs.getBooleanDefault (gs.getObjectContext ().getObject( ), gs. getObjectContext ().isNode (), Attributes.BASE_OPEN, (bits & BASE_OPEN_MASK) != 0);
			topClosed = !gs.getBooleanDefault (gs.getObjectContext ().getObject( ), gs. getObjectContext ().isNode (), Attributes.TOP_OPEN, (bits & TOP_OPEN_MASK) != 0);
		}

		out.closed = false;
		out.dimension = 3;
		out.edgeCount = 3;
		out.planar = true;
		
		int uCount = 30;
		
		IntList polygons = new IntList();
		FloatList vertices = new FloatList();
		ByteList normals = new ByteList();
		FloatList uvs = new FloatList();
		
		Vector3f normal = new Vector3f();
		
		// connection from top to bottom
		for (int u = 0; u <= uCount; u++)
		{
			float phi = (float) (2.0f * Math.PI * u / uCount);
			float cosPhi = (float) Math.cos (phi);
			float sinPhi = (float) Math.sin (phi);
			
			vertices.add(r * cosPhi);
			vertices.add(r * sinPhi);
			vertices.add(l);
			
			if (l > 0) {
				normal.x = cosPhi;
				normal.y = sinPhi;
				normal.z = 1 / l;
				normal.normalize();
			}
			else {
				normal.x = 0;
				normal.y = 0;
				normal.z = -1;
			}
			
			normals.add((byte) (normal.x * 127f));
			normals.add((byte) (normal.y * 127f));
			normals.add((byte) (normal.z * 127f));
			
			uvs.add(phi / (float) (2.0f * Math.PI));
			uvs.add(1);
			
			vertices.add(r * cosPhi);
			vertices.add(r * sinPhi);
			vertices.add(0);
			
			normals.add((byte) (normal.x * 127f));
			normals.add((byte) (normal.y * 127f));
			normals.add((byte) (normal.z * 127f));
			
			uvs.add(phi / (float) (2.0f * Math.PI));
			uvs.add(0);
			
			if (u < uCount) {
				polygons.add(2*u);
				polygons.add(2*u+1);
				polygons.add(2*u+2);
	
				polygons.add(2*u+1);
				polygons.add(2*u+3);
				polygons.add(2*u+2);
			}
		}
		
		// top circle
		if (topClosed)
		{
			vertices.add(0);
			vertices.add(0);
			vertices.add(l);
			
			normals.add((byte) 0);
			normals.add((byte) 0);
			normals.add((byte) 127);
			
			uvs.add(0.5f);
			uvs.add(0.5f);
			
			for (int u = 0; u <= uCount; u++)
			{
				float phi = (float) (2.0f * Math.PI * u / uCount);
				float cosPhi = (float) Math.cos (phi);
				float sinPhi = (float) Math.sin (phi);

				vertices.add(r * cosPhi);
				vertices.add(r * sinPhi);
				vertices.add(l);
				
				normals.add((byte) 0);
				normals.add((byte) 0);
				normals.add((byte) 127);

				uvs.add(cosPhi / 2 + 0.5f);
				uvs.add(sinPhi / 2 + 0.5f);

				if (u < uCount) {
					polygons.add(2*(uCount+1));
					polygons.add(2*(uCount+1)+u+1);
					polygons.add(2*(uCount+1)+u+2);
				}
			}
		}
		
		// base circle
		if (baseClosed)
		{
			vertices.add(0);
			vertices.add(0);
			vertices.add(0);
			
			normals.add((byte) 0);
			normals.add((byte) 0);
			normals.add((byte) -127);
			
			uvs.add(0.5f);
			uvs.add(0.5f);
			
			for (int u = 0; u <= uCount; u++)
			{
				float phi = (float) (2.0f * Math.PI * u / uCount);
				float cosPhi = (float) Math.cos (phi);
				float sinPhi = (float) Math.sin (phi);

				vertices.add(r * cosPhi);
				vertices.add(r * sinPhi);
				vertices.add(0);
				
				normals.add((byte) 0);
				normals.add((byte) 0);
				normals.add((byte) -127);

				uvs.add(cosPhi / 2 + 0.5f);
				uvs.add(sinPhi / 2 + 0.5f);

				if (u < uCount) {
					polygons.add(3*(uCount+1)+1);
					polygons.add(3*(uCount+1)+1+u+2);
					polygons.add(3*(uCount+1)+1+u+1);
				}
			}
		}
		
		out.polygons = polygons;
		out.vertices = vertices;
		out.normals = normals;
		out.uv = uvs;
		
		out.visibleSides = Attributes.VISIBLE_SIDES_FRONT;
	}

//	enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field radius$FIELD;
	public static final NType.Field baseOpen$FIELD;
	public static final NType.Field topOpen$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Cylinder.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Cylinder) o).radius = value;
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
					return ((Cylinder) o).getRadius ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Cylinder ());
		$TYPE.addManagedField (radius$FIELD = new _Field ("radius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (baseOpen$FIELD = new NType.BitField ($TYPE, "baseOpen", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, BASE_OPEN_MASK));
		$TYPE.addManagedField (topOpen$FIELD = new NType.BitField ($TYPE, "topOpen", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, TOP_OPEN_MASK));
		$TYPE.declareFieldAttribute (radius$FIELD, Attributes.RADIUS);
		$TYPE.declareFieldAttribute (baseOpen$FIELD, Attributes.BASE_OPEN);
		$TYPE.declareFieldAttribute (topOpen$FIELD, Attributes.TOP_OPEN);
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
		return new Cylinder ();
	}

	public float getRadius ()
	{
		return radius;
	}

	public void setRadius (float value)
	{
		this.radius = value;
	}

	/**
	 * Calculates the area of an object.
	 * Intersection with other object are not considered.The total area will be calculated.
	 * A=2*Pi*r*h
	 * 
	 * @return volume
	 */
	@Override
	public double getSurfaceArea() {
		return 2*Math.PI*radius*length;
	}

	/**
	 * Calculates the volume.
	 * Intersection with other object are not considered.The total volume will be calculated.
	 * V=Pi*r^2*h
	 * 
	 * @return volume
	 */
	@Override
	public double getVolume() {
		return Math.PI*Math.pow(radius, 2)*length;
	}

	public boolean isBaseOpen ()
	{
		return (bits & BASE_OPEN_MASK) != 0;
	}

	public void setBaseOpen (boolean v)
	{
		if (v) bits |= BASE_OPEN_MASK; else bits &= ~BASE_OPEN_MASK;
	}

	public boolean isTopOpen ()
	{
		return (bits & TOP_OPEN_MASK) != 0;
	}

	public void setTopOpen (boolean v)
	{
		if (v) bits |= TOP_OPEN_MASK; else bits &= ~TOP_OPEN_MASK;
	}

//enh:end

}
