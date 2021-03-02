
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
import de.grogra.xl.util.DoubleList;

/**
 * A <code>MeshNode</code> is used to represent a polygonal mesh defined
 * by an instance of {@link de.grogra.imp3d.objects.Polygons} within the
 * graph.
 * 
 * @author Ole Kniemeyer
 */
public class MeshNode extends ShadedNull implements Pickable, Polygonizable, Renderable
{
	
	private class TriangleRayTest {

		private final Point3d origin;
		private final Vector3d direction;
		private PolygonMesh polygons;

		public TriangleRayTest(Point3d origin, Vector3d direction, Matrix4d transformation) {
			this.origin = new Point3d( origin );
			this.direction = new Vector3d( direction );
		}

		public void setPolygons( PolygonMesh polygons ) {

			this.polygons = polygons;
		}

		/**
		 * Implementation after Tomas Möller, Eric Haines: Real-Time Rendering, p.
		 * 305
		 *
		 * @return Returns distance to intersection point of intersected triangle
		 */
		public DoubleList intersectionTest() {

			DoubleList results = new DoubleList();

			// if (true) return 0;
			float epsilon = 0.00001f;
			float[] vertices = this.polygons.getVertexData( );
			int[] indices = this.polygons.getIndexData( );

			// iterating over triangles
			for (int i = 0; i <= indices.length - 3; i += 3) {// iterating over triangles
				// vertices of the triangle
				Point3d v0 = new Point3d( vertices[indices[i + 0] * 3] , vertices[indices[i + 0] * 3 + 1] , vertices[indices[i + 0] * 3 + 2] );
				Point3d v1 = new Point3d( vertices[indices[i + 1] * 3] , vertices[indices[i + 1] * 3 + 1] , vertices[indices[i + 1] * 3 + 2] );
				Point3d v2 = new Point3d( vertices[indices[i + 2] * 3] , vertices[indices[i + 2] * 3 + 1] , vertices[indices[i + 2] * 3 + 2] );

				// edge vectors
				Point3d e1 = new Point3d( v1.x - v0.x , v1.y - v0.y , v1.z - v0.z ); //for u
				Point3d e2 = new Point3d( v2.x - v0.x , v2.y - v0.y , v2.z - v0.z ); //for v

				// calculate angle between direction and triangle plane
				Vector3d p = new Vector3d( direction );
				p.cross( p , new Vector3d( e2.x , e2.y , e2.z ) );

				double a = e1.x * p.x + e1.y * p.y + e1.z * p.z;
				if (Math.abs(a) < epsilon)
					continue; // no intersection, test
				// next triangle
				double f = 1 / a;

				// vector from vertice 0 to origin
				Vector3d s = new Vector3d( origin.x - v0.x , origin.y - v0.y , origin.z - v0.z );
				double u = f * s.dot( p );
				if ((u < 0) || (u > 1))
					continue;
				Vector3d q = new Vector3d( s );
				q.cross( q , new Vector3d( e1.x , e1.y , e1.z ) );
				double v = f * direction.dot( q );
				if (v < 0 || (u + v) > 1)
					continue;

				/*
				 * Now we have (u,v,t) in barycentric coordinates we need a distance as
				 * return value
				 */
				double result = f*(new Vector3d(e2.x, e2.y, e2.z).dot( q ));
				if (result < 0)
					continue;
				results.add(result);
			}
			return results;
		}
	}
	
	/**
	 * This defines the geometry of this node, namely a polygonal mesh.  
	 */
	protected Polygons polygons;
	//enh:field attr=Attributes.POLYGONS getter setter
	
	protected int visibleSides = Attributes.VISIBLE_SIDES_BOTH;
	//enh:field attr=Attributes.VISIBLE_SIDES getter setter

	public MeshNode ()
	{
		this (null);
	}


	public MeshNode (Polygons polygons)
	{
		this.polygons = polygons;
		setLayer (1);
	}


	@Override
	public ContextDependent getPolygonizableSource (GraphState gs)
	{
		return polygons;
	}


	@Override
	public void pick (Object node, boolean asNode, Point3d origin, Vector3d direction,
					  Matrix4d transformation, de.grogra.imp.PickList list)
	{
	    if (!(this.polygons instanceof PolygonMesh))
	    {
	    	Sphere.pick (1, origin, direction, list);
	    	return;
	    }
		
		TriangleRayTest test = new TriangleRayTest( origin , direction , transformation );
		test.setPolygons( (PolygonMesh) this.polygons );

		DoubleList lengths = test.intersectionTest( );
		for (int i = 0; i < lengths.size; i++) {
			list.add(lengths.get(i));
		}
	}


	@Override
	public Polygonization getPolygonization ()
	{
		final class Poly implements Polygonization
		{
			final int visibleSides = MeshNode.this.visibleSides;

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
		if (polygons == null)
		{
			out.init (3);
		}
		else
		{
			polygons.polygonize (source, gs, out, flags, flatness);
			out.visibleSides = visibleSides;
		}
	}

	@Override
	public void draw (Object object, boolean asNode, RenderState rs)
	{
		rs.drawPolygons (this, object, asNode, null, -1, isRenderAsWireframe(), null);
	}

//enh:insert $TYPE.addIdentityAccessor (Attributes.SHAPE);
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field polygons$FIELD;
	public static final NType.Field visibleSides$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (MeshNode.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setInt (Object o, int value)
		{
			switch (id)
			{
				case 1:
					((MeshNode) o).visibleSides = value;
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
					return ((MeshNode) o).getVisibleSides ();
			}
			return super.getInt (o);
		}

		@Override
		protected void setObjectImpl (Object o, Object value)
		{
			switch (id)
			{
				case 0:
					((MeshNode) o).polygons = (Polygons) value;
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
					return ((MeshNode) o).getPolygons ();
			}
			return super.getObject (o);
		}
	}

	static
	{
		$TYPE = new NType (new MeshNode ());
		$TYPE.addManagedField (polygons$FIELD = new _Field ("polygons", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.ClassAdapter.wrap (Polygons.class), null, 0));
		$TYPE.addManagedField (visibleSides$FIELD = new _Field ("visibleSides", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.INT, null, 1));
		$TYPE.declareFieldAttribute (polygons$FIELD, Attributes.POLYGONS);
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
		return new MeshNode ();
	}

	public int getVisibleSides ()
	{
		return visibleSides;
	}

	public void setVisibleSides (int value)
	{
		this.visibleSides = value;
	}

	public Polygons getPolygons ()
	{
		return polygons;
	}

	public void setPolygons (Polygons value)
	{
		polygons$FIELD.setObject (this, value);
	}

//enh:end

	public static double getAreaOfTriangulation (float[] pointlist) {
		// check input data
		if(pointlist.length<9) {
			System.out.println("Error in getAreaOfTriangulation: Number of points must be at least three. I switched to the default leaf.");
			return 0;
		}
		if(pointlist.length%3!=0) {
			System.out.println("Error in getAreaOfTriangulation: Number of points must be a multiple of three. I switched to the default leaf.");
			return 0;
		}

		double area = 0;
		// triangulate data
		for(int i=0; i<pointlist.length/9; i++) {
			double a = Math.sqrt(
				Math.pow((pointlist[i*9+0]-pointlist[i*9+6]), 2) + 
				Math.pow((pointlist[i*9+1]-pointlist[i*9+7]), 2) + 
				Math.pow((pointlist[i*9+2]-pointlist[i*9+8]), 2));
			double b = Math.sqrt(
				Math.pow((pointlist[i*9+6]-pointlist[i*9+3]), 2) + 
				Math.pow((pointlist[i*9+7]-pointlist[i*9+4]), 2) + 
				Math.pow((pointlist[i*9+8]-pointlist[i*9+5]), 2));
			double c = Math.sqrt(
				Math.pow((pointlist[i*9+0]-pointlist[i*9+3]), 2) + 
				Math.pow((pointlist[i*9+1]-pointlist[i*9+4]), 2) + 
				Math.pow((pointlist[i*9+2]-pointlist[i*9+5]), 2));
			//heron's formula
			double s = (a + b + c)/2f;
			area += Math.sqrt(s*(s - a)*(s - b)*(s - c));
		}
		return area;
	}
	
	/**
	 * Calculates the area of an object.
	 * Intersection with other object are not considered.The total area will be calculated.
	 * 
	 * @return area
	 */
	@Override
	public double getSurfaceArea() {
		if(polygons==null) return -1;
		if(polygons instanceof PolygonMesh)
			return getAreaOfTriangulation(((PolygonMesh)polygons).getVertexData());
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
