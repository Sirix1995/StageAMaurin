
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

import de.grogra.graph.ContextDependent;
import de.grogra.graph.GraphState;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.math.Pool;
import de.grogra.math.VertexList;
import de.grogra.vecmath.Math2;

public class Polygon extends ShadedNull implements Pickable, Polygonizable, Renderable
{
	VertexList vertices;
	//enh:field attr=Attributes.VERTEX_LIST getter setter
	
	int visibleSides = Attributes.VISIBLE_SIDES_BOTH;
	//enh:field attr=Attributes.VISIBLE_SIDES getter setter

//enh:insert $TYPE.addIdentityAccessor (Attributes.SHAPE);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field vertices$FIELD;
	public static final NType.Field visibleSides$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (Polygon.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 1:
					((Polygon) o).visibleSides = value;
					return;
			}
			super.setInt (o, value);
		}

		@Override
		public int getInt (Object o)
		{
			switch (id)
			{
				case 1:
					return ((Polygon) o).getVisibleSides ();
			}
			return super.getInt (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((Polygon) o).vertices = (VertexList) value;
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
					return ((Polygon) o).getVertices ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new Polygon ());
		$TYPE.addManagedField (vertices$FIELD = new _Field ("vertices", 0 | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (VertexList.class), null, 0));
		$TYPE.addManagedField (visibleSides$FIELD = new _Field ("visibleSides", 0 | _Field.SCO, de.grogra.reflect.Type.INT, null, 1));
		$TYPE.declareFieldAttribute (vertices$FIELD, Attributes.VERTEX_LIST);
		$TYPE.declareFieldAttribute (visibleSides$FIELD, Attributes.VISIBLE_SIDES);
		$TYPE.addIdentityAccessor (Attributes.SHAPE);
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
		return new Polygon ();
	}

	public int getVisibleSides ()
	{
		return visibleSides;
	}

	public void setVisibleSides (int value)
	{
		this.visibleSides = value;
	}

	public VertexList getVertices ()
	{
		return vertices;
	}

	public void setVertices (VertexList value)
	{
		vertices$FIELD.setObject (this, value);
	}

//enh:end


	public Polygon ()
	{
		this (null);
	}


	public Polygon (VertexList vertices)
	{
		this.vertices = vertices;
		setLayer (1);
	}


	@Override
	public ContextDependent getPolygonizableSource (GraphState gs)
	{
		return vertices;
	}


	@Override
	public void pick (Object node, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d transformation, de.grogra.imp.PickList list)
	{
		Sphere.pick (1, origin, direction, list);
	}


	@Override
	public Polygonization getPolygonization ()
	{
		final class Poly implements Polygonization
		{
			final int visibleSides = Polygon.this.visibleSides;

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
				return (p.visibleSides == visibleSides);
			}

			@Override
			public int hashCode ()
			{
				return visibleSides;
			}
		}

		return new Poly ();
	}

	void polygonizeImpl (ContextDependent source, GraphState gs, PolygonArray out,
						 int flags, float flatness)
	{
		out.init (3);
		int n;
		if ((vertices != null) && ((n = vertices.getSize (gs)) > 2))
		{
			boolean normals = (flags & Polygonization.COMPUTE_NORMALS) != 0;
			boolean uv = (flags & Polygonization.COMPUTE_UV) != 0;
			boolean rational = vertices.isRational (gs);
			out.edgeCount = 3;
			out.planar = true;
			out.closed = false;
			float[] tmp = Pool.get (gs).getFloatArray (0, 4);
			float[] tmp2 = Pool.get (gs).getFloatArray (1, 4);
			int d = vertices.getVertex (tmp, 0, gs);
		checkEndPoints:
			if (d == vertices.getVertex (tmp2, n - 1, gs))
			{
				if (rational)
				{
					if (Math.abs (tmp[--d] - tmp2[d]) > 1e-5f)
					{
						break checkEndPoints;
					}
				}
				float sum = 0, diff = 0;
				for (int i = 0; i < d; i++)
				{
					sum += Math.abs (tmp[i]) + Math.abs (tmp2[i]);
					diff += Math.abs (tmp[i] - tmp2[i]);
				}
				if (diff > sum * 1e-5f)
				{
					break checkEndPoints;
				}
				n--;
			}
			out.visibleSides = visibleSides;
			if (n == 3)
			{
				for (int i = 0; i < 3; i++)
				{
					d = vertices.getVertex (tmp, i, gs);
					if (rational)
					{
						float w = 1 / tmp[--d];
						for (int j = d - 1; j >= 0; j--)
						{
							tmp[j] *= w;
						}
					}
					out.vertices.push (tmp[0])
						.push ((d > 1) ? tmp[1] : 0)
						.push ((d > 2) ? tmp[2] : 0);
					out.polygons.push (i);
				}
				if (normals)
				{
					out.computeNormal (tmp, 0, 1, 2);
					out.setNormal (0, tmp[0], tmp[1], tmp[2]);
					out.copyNormal (0, 1);
					out.copyNormal (0, 2);
				}
				if (uv)
				{
					out.uv.push (0).push (0).push (1).push (0).push (0).push (1);
				}
			}
			else
			{
				float sx = 0, sy = 0, sz = 0;
				for (int i = 0; i < n; i++)
				{
					d = vertices.getVertex (tmp, i, gs);
					if (rational)
					{
						float w = 1 / tmp[--d];
						for (int j = d - 1; j >= 0; j--)
						{
							tmp[j] *= w;
						}
					}
					out.vertices.push (tmp[0])
						.push ((d > 1) ? tmp[1] : 0)
						.push ((d > 2) ? tmp[2] : 0);
					sx += tmp[0];
					if (d > 1)
					{
						sy += tmp[1];
						if (d > 2)
						{
							sz += tmp[2];
						}
					}
					out.polygons.push ((i == 0) ? n - 1 : i - 1)
						.push (i).push (n);
				}
				float f = 1f / n;
				out.vertices.push (sx * f).push (sy * f).push (sz * f);
				if (uv)
				{
					for (int i = 0; i < n; i++)
					{
						char p = (char) (i * 0x10000 / n);
						out.uv.push ((Math2.ccos (p) + 1) * 0.5f)
							.push ((Math2.csin (p) + 1) * 0.5f);
					}
					out.uv.push (0.5f).push (0.5f);
				}
				if (normals)
				{
					out.computeNormal (tmp, n - 1, 0, n);
					f = tmp[0] * tmp[0] + tmp[1] * tmp[1] + tmp[2] * tmp[2];
					if (f > 0)
					{
						f = 1 / (float) Math.sqrt (f);
					}
					float x, y, z, x0, y0, z0;
					sx = x = x0 = f * tmp[0];
					sy = y = y0 = f * tmp[1];
					sz = z = z0 = f * tmp[2];
					for (int i = 1; i < n; i++)
					{
						out.computeNormal (tmp, i - 1, i, n);
						f = tmp[0] * tmp[0] + tmp[1] * tmp[1] + tmp[2] * tmp[2];
						if (f > 0)
						{
							f = 1 / (float) Math.sqrt (f);
						}
						out.setNormal (i - 1, x + (x = f * tmp[0]),
									   y + (y = f * tmp[1]),
									   z + (z = f * tmp[2]));
						sx += x;
						sy += y;
						sz += z;
					}
					out.setNormal (n - 1, x + x0, y + y0, z + z0);
					out.setNormal (n, sx, sy, sz);
				}
			}
		}
	}

	@Override
	public void draw (Object object, boolean asNode, RenderState rs)
	{
		rs.drawPolygons (this, object, asNode, null, -1, isRenderAsWireframe(), null);
	}

}
