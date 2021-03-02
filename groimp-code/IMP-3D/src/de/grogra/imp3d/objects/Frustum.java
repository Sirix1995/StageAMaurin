
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


public class Frustum extends FrustumBase implements Raytraceable, Polygonizable
{
	protected float baseRadius;
	//enh:field attr=Attributes.BASE_RADIUS getter setter

	protected float topRadius;
	//enh:field attr=Attributes.TOP_RADIUS getter setter

	// boolean baseOpen
	//enh:field type=bits(BASE_OPEN_MASK) attr=Attributes.BASE_OPEN getter setter

	// boolean topOpen
	//enh:field type=bits(TOP_OPEN_MASK) attr=Attributes.TOP_OPEN getter setter



	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern
	{
		public Pattern ()
		{
			super (null, Frustum.$TYPE, new NType.Field[] {length$FIELD, baseRadius$FIELD, topRadius$FIELD});
		}

		public static void signature (@In @Out Frustum f, float l, float b, float t)
		{
		}
	}


	public Frustum ()
	{
		this (1, 1, 0.5f);
	}


	public Frustum (float length, float baseRadius, float topRadius)
	{
		super ();
		setLength (length);
		this.baseRadius = baseRadius;
		this.topRadius = topRadius;
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
				pick (length, baseRadius, topRadius,
					  (bits & BASE_OPEN_MASK) == 0, (bits & TOP_OPEN_MASK) == 0,
					  origin, direction, list);
			}
			else
			{
				pick ((float) gs.checkDouble (this, true, Attributes.LENGTH, length),
					  gs.checkFloat (this, true, Attributes.BASE_RADIUS, baseRadius),
					  gs.checkFloat (this, true, Attributes.TOP_RADIUS, topRadius),
					  !gs.checkBoolean (this, true, Attributes.BASE_OPEN, (bits & BASE_OPEN_MASK) != 0),
					  !gs.checkBoolean (this, true, Attributes.TOP_OPEN, (bits & TOP_OPEN_MASK) != 0),
					  origin, direction, list);
			}
		}
		else
		{
			pick ((float) gs.getDouble (object, asNode, Attributes.LENGTH),
				  gs.getFloat (object, asNode, Attributes.BASE_RADIUS),
				  gs.getFloat (object, asNode, Attributes.TOP_RADIUS),
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
				rs.drawFrustum (length, baseRadius, topRadius,
								(bits & BASE_OPEN_MASK) == 0, (bits & TOP_OPEN_MASK) == 0,
								isScaleV () ? length : 1, null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
			else
			{
				float len = (float) gs.checkDouble (this, true, Attributes.LENGTH, length);
				rs.drawFrustum (len,
								gs.checkFloat (this, true, Attributes.BASE_RADIUS, baseRadius),
								gs.checkFloat (this, true, Attributes.TOP_RADIUS, topRadius),
								!gs.checkBoolean (this, true, Attributes.BASE_OPEN, (bits & BASE_OPEN_MASK) != 0),
								!gs.checkBoolean (this, true, Attributes.TOP_OPEN, (bits & TOP_OPEN_MASK) != 0),
								gs.checkBoolean (this, true, Attributes.SCALE_V, isScaleV ()) ? len : 1, null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
		}
		else
		{
			float len = (float) gs.getDouble (object, asNode, Attributes.LENGTH);
			rs.drawFrustum (len,
							gs.getFloat (object, asNode, Attributes.BASE_RADIUS),
							gs.getFloat (object, asNode, Attributes.TOP_RADIUS),
							!gs.getBoolean (object, asNode, Attributes.BASE_OPEN),
							!gs.getBoolean (object, asNode, Attributes.TOP_OPEN),
							gs.getBoolean (object, asNode, Attributes.SCALE_V) ? len : 1, null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
		}
	}

	@Override
	public RaytracerLeaf createRaytracerLeaf(Object object, boolean asNode, long pathId, GraphState gs) {
		
		float radius_bottom = 1.0f;
		float radius_top    = 1.0f;
		Vector3f cur_axis = new Vector3f ();
		boolean open_top = false;
		boolean open_bottom = false;
		
		if (object == this) 
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				radius_top    = this.topRadius;
				radius_bottom = this.baseRadius;
				cur_axis.z    = this.length;
				open_top      = this.isTopOpen();
				open_bottom   = this.isBaseOpen();
			}
			else
			{
				radius_top    = gs.checkFloat(this,true,Attributes.TOP_RADIUS,this.topRadius);
				radius_bottom = gs.checkFloat(this,true,Attributes.BASE_RADIUS,this.baseRadius);
				cur_axis.z    = (float)gs.checkDouble(this,true,Attributes.LENGTH,this.length);
				open_top      = gs.checkBoolean(this,true,Attributes.TOP_OPEN,this.isTopOpen());
				open_bottom   = gs.checkBoolean(this,true,Attributes.BASE_OPEN,this.isBaseOpen());
			}
		}
		else
		{
			radius_top    = gs.getFloat (object, asNode, Attributes.TOP_RADIUS);
			radius_bottom = gs.getFloat (object, asNode, Attributes.BASE_RADIUS);
			cur_axis.z    = (float)gs.getDouble(object,asNode,Attributes.LENGTH);
			open_top      = gs.getBoolean(object,asNode,Attributes.TOP_OPEN);
			open_bottom   = gs.getBoolean(object,asNode,Attributes.BASE_OPEN);
		}
		
		return new de.grogra.imp3d.ray.RTFrustum(object,asNode,pathId,
				radius_top,radius_bottom,cur_axis,open_top,open_bottom);
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
			final float baseRadius = Frustum.this.baseRadius;
			final float topRadius = Frustum.this.topRadius;
			final int bits = Frustum.this.bits;

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
				return ((p.baseRadius == baseRadius) && (p.topRadius == topRadius) && (p.bits == bits));
			}

			@Override
			public int hashCode ()
			{
				return Float.floatToIntBits (baseRadius) ^ Float.floatToIntBits (topRadius) ^ bits;
			}
		}

		return new Poly ();
	}
	
	void polygonizeImpl (ContextDependent source, GraphState gs, PolygonArray out,
			 int flags, float flatness)
	{
		float br;
		float tr;
		float l;
		boolean baseClosed;
		boolean topClosed;
		if (gs.getObjectContext ().getObject () == this)
		{
			if (gs.getInstancingPathIndex () <= 0)
			{
				br = this.baseRadius;
				tr = this.topRadius;
				l = this.length;
				baseClosed = (bits & BASE_OPEN_MASK) == 0;
				topClosed = (bits & TOP_OPEN_MASK) == 0;
			}
			else
			{
				br = gs.checkFloat (this, true, Attributes.BASE_RADIUS, this.baseRadius);
				tr = gs.checkFloat (this, true, Attributes.TOP_RADIUS, this.topRadius);
				l = (float) gs.checkDouble (this, true, Attributes.LENGTH, this.length);
				baseClosed = !gs.checkBoolean (this, true, Attributes.BASE_OPEN, (bits & BASE_OPEN_MASK) != 0);
				topClosed = !gs.checkBoolean (this, true, Attributes.TOP_OPEN, (bits & TOP_OPEN_MASK) != 0);
			}
		}
		else
		{
			br = gs.getFloatDefault (gs.getObjectContext ().getObject (), gs.getObjectContext ().isNode (), Attributes.BASE_RADIUS, this.baseRadius);
			tr = gs.getFloatDefault (gs.getObjectContext ().getObject (), gs.getObjectContext ().isNode (), Attributes.TOP_RADIUS, this.topRadius);
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
			
			vertices.add(tr * cosPhi);
			vertices.add(tr * sinPhi);
			vertices.add(l);
			
			if (l > 0) {
				normal.x = cosPhi;
				normal.y = sinPhi;
				normal.z = (br-tr) / l;
				normal.normalize();
			}
			else {
				normal.x = 0;
				normal.y = 0;
				normal.z = (br-tr) > 0 ? 1 : -1;
			}
			
			normals.add((byte) (normal.x * 127f));
			normals.add((byte) (normal.y * 127f));
			normals.add((byte) (normal.z * 127f));
			
			uvs.add(phi / (float) (2.0f * Math.PI));
			uvs.add(1);
			
			vertices.add(br * cosPhi);
			vertices.add(br * sinPhi);
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

				vertices.add(tr * cosPhi);
				vertices.add(tr * sinPhi);
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

				vertices.add(br * cosPhi);
				vertices.add(br * sinPhi);
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

	public static final NType.Field baseRadius$FIELD;
	public static final NType.Field topRadius$FIELD;
	public static final NType.Field baseOpen$FIELD;
	public static final NType.Field topOpen$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Frustum.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setFloat (Object o, float value)
		{
			switch (id)
			{
				case 0:
					((Frustum) o).baseRadius = value;
					return;
				case 1:
					((Frustum) o).topRadius = value;
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
					return ((Frustum) o).getBaseRadius ();
				case 1:
					return ((Frustum) o).getTopRadius ();
			}
			return super.getFloat (o);
		}
	}

	static
	{
		$TYPE = new NType (new Frustum ());
		$TYPE.addManagedField (baseRadius$FIELD = new _Field ("baseRadius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 0));
		$TYPE.addManagedField (topRadius$FIELD = new _Field ("topRadius", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.FLOAT, null, 1));
		$TYPE.addManagedField (baseOpen$FIELD = new NType.BitField ($TYPE, "baseOpen", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, BASE_OPEN_MASK));
		$TYPE.addManagedField (topOpen$FIELD = new NType.BitField ($TYPE, "topOpen", 0 | NType.BitField.SCO, de.grogra.reflect.Type.BOOLEAN, TOP_OPEN_MASK));
		$TYPE.declareFieldAttribute (baseRadius$FIELD, Attributes.BASE_RADIUS);
		$TYPE.declareFieldAttribute (topRadius$FIELD, Attributes.TOP_RADIUS);
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
		return new Frustum ();
	}

	public float getBaseRadius ()
	{
		return baseRadius;
	}

	public void setBaseRadius (float value)
	{
		this.baseRadius = value;
	}

	public float getTopRadius ()
	{
		return topRadius;
	}

	public void setTopRadius (float value)
	{
		this.topRadius = value;
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


	/**
	 * Calculates the area of an object.
	 * Intersection with other object are not considered.The total area will be calculated.
	 * A=M+G+D; M=(r1+r2)*pi*m; m=sqrt((r1-r2)^2+h^2); D=Pi*r2^2; G=Pi*r1^2
	 * 
	 * @return area
	 */
	@Override
	public double getSurfaceArea() {
		double m = Math.sqrt(Math.pow(baseRadius-topRadius,2)+length*length);
		double M = (baseRadius+topRadius)*Math.PI*m;
		double D = Math.PI*topRadius*topRadius; 
		double G = Math.PI*baseRadius*baseRadius;
		return M+G+D;
	}

	/**
	 * Calculates the volume.
	 * Intersection with other object are not considered.The total volume will be calculated.
	 * V=(r1^2+r1*r2+r2^2)*(Pi*h)/3
	 * 
	 * @return volume
	 */
	@Override
	public double getVolume() {
		return (baseRadius*baseRadius+baseRadius*topRadius+topRadius*topRadius)*(Math.PI*length)/3f;
	}

}
