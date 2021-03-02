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

package de.grogra.vecmath.geom;

import java.util.ArrayList;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector2d;
import javax.vecmath.Vector3d;

import de.grogra.vecmath.Math2;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

/**
 * This class represents a given {@link de.grogra.vecmath.geom.Mesh}
 * as a {@link de.grogra.vecmath.geom.Volume}. It uses an octree
 * in order to speed up the intersection computation; an octree
 * cell contains a list of those polygons of the mesh which lie within
 * the cell.
 * <p>
 * The algorithm for a single polygon-line intersection is an extension
 * to general polygonal meshes (with convex, planar polygons)
 * of the algorithm in the paper
 * "Ray Tracing Triangular Meshes" of John Amanatides and Kin Choi.
 * The algorithm uses Pl&uuml;cker coordinates to decide if a line
 * intersects a polygon.
 * <p>
 * The mesh coordinates are specified in their own coordinate system.
 * The transformation from world coordinates to mesh coordinates is
 * implemented by {@link #worldToMesh}. The advantage of this is that
 * it is possible to create a set of copies of a <code>MeshVolume</code>
 * using {@link #dup()} which can then be shifted to another location
 * using {@link #setTransformation(Matrix4d)}.
 * <p>
 * To set the data of a <code>MeshVolume</code>, both methods
 * {@link #setMesh} and {@link #setTransformation} have to be invoked
 * in this order.
 * 
 * @author Ole Kniemeyer
 */
public class MeshVolume extends VolumeBase implements Cloneable
{
	/**
	 * Minimum number of polygons which have to be present in
	 * an octree cell so that it is considered for subdivision.
	 */
	public static int MIN_CELL_OBJECTS = 15;

	/**
	 * The octree of the polygons.
	 */
	OctreeImpl octree;

	/**
	 * The minimum coordinates of this mesh, in global world coordinates.
	 */
	Point3d min;

	/**
	 * The maximum coordinates of this mesh, in global world coordinates.
	 */
	Point3d max;

	/**
	 * The affine transformation from global world coordinates to mesh
	 * coordinates.
	 * 
	 * @see #setTransformation(Matrix4d)
	 */
	Matrix4d worldToMesh = new Matrix4d ();

	/**
	 * The transpose of the upper 3x3 values of {@link #worldToMesh}.
	 * This is used to transform normal vectors from mesh coordinates
	 * to world coordinates.
	 */
	Matrix3d normalToWorld = new Matrix3d ();

	/**
	 * A rough estimate for the size of the mesh
	 * (the euclidian distance from minimum mesh coordinates to
	 * maximum mesh coordinates.
	 */
	double magnitude;

	static final int FLOATS_PER_VERTEX = 5;

	/**
	 * Contains the complete vertex data of the
	 * underlying {@link Mesh}. For each vertex of the mesh,
	 * this array holds five values in the following order:
	 * x, y, z of the vertex, u, v.
	 */
	float[] vertexCoordinates;

	/**
	 * Contains all edges of the mesh. Each edge is represented
	 * by two consecutive values which are indices
	 * into the array {@link #vertexCoordinates} (i.e., they are
	 * premultiplied by 5, the number of entries in
	 * {@link #vertexCoordinates} for a single vertex). The indices
	 * are sorted such that the first is less than the second.
	 */
	int[] edges;

	/**
	 * Contains the cross products of edge vertices of the mesh.
	 * Each edge is represented by three consecutive values,
	 * the order of edges is the same as for {@link #edges}.
	 * The values specify the cross product of the first vertex with the
	 * second vertex. These coordinates are related
	 * to the Pl&uuml;cker coordinates of the edge.
	 */
	float[] crossProducts;

	/**
	 * Contains the topology and normal vectors of the mesh.
	 * This array is composed
	 * of consecutive sequences of <code>int</code>-values.
	 * <p>
	 * A single sequence describes a single polygon of the mesh.
	 * Its first value is the index into {@link #polyTransformations}
	 * for the polygon (which is even), additionally bit 0 is set
	 * if the polygon has more than three edges.
	 * After this first value, the sequence contains two
	 * <code>int</code>-value for each edge of the polygon:
	 * The first is the index of the edge in {@link #edges} (which is even);
	 * if the edge is traversed in reverse order bit 0 is set.
	 * if the edge is the last of the polygon bit 31
	 * (the sign bit) is set.
	 * The second value represents the normalized normal vector
	 * of the first traversed vertex of the edge, encoded as three 10-bit
	 * values <em>c</em> with the conversion formula
	 * <em>f</em> = (2 <em>c</em> + 1) / 1023 for each component <em>f</em>.
	 * <p>
	 * Note that if only bit 31 is masked, an entry <code>e</code> in
	 * <code>polys</code> can be used as index into {@link #edges},
	 * too: The indexed value in {@link #edges} is the index
	 * of the starting vertex of the edge in the traversal order of
	 * the polygon, <code>vertexIndex = edges[e & 0x7fffffff];</code>.
	 */
	int[] polys;

	/**
	 * Specifies the transformation from mesh coordinates to
	 * polygon coordinates. This array is indexed by the first
	 * entry of a polygon sequence in {@link #polys}. Starting at
	 * the indexed element, the first six elements
	 * <code>a0</code> ... <code>a5</code> define a matrix
	 * which map global 3d vertex coordinates (x, y, z) to
	 * 2d polygon coordinates (s, t):
	 * <pre>
	 *   s = a0 * (x - vx) + a1 * (y - vy) + a2 * (z - vz)
	 *   t = a3 * (x - vx) + a4 * (y - vy) + a5 * (z - vz)
	 * </pre>
	 * (vx, vy, vz) are the coordinates of the first vertex. The matrix
	 * is chosen such that the first vertex is mapped to (0, 0),
	 * the second vertex to (1, 0), and the third vertex to (1, 1).
	 * <p>
	 * If the polygon has more than three edges, two further elements
	 * are present: Let the point D specify the 2d polygon coordinates
	 * of the fourth vertex, then the first additional element
	 * is the reciprocal of the t-value of the intersection point
	 * of the line through (0, 0) and D with the line s = 1.
	 * The second additional element is the s-value of the intersection
	 * point of the line through (1, 1) and D with the line t = 0. 
	 */
	float[] polyTransformations;

	/**
	 * The number of polygons in {@link #polys}.
	 */
	int polyCount;

	boolean closed;

	float[] boxPolyData;

	int[] boxPolyIndex;

	public int getPolygonCount ()
	{
		return polyCount;
	}

	public Octree getOctree ()
	{
		return octree;
	}

	/**
	 * This octree state implements the common operations of
	 * <code>State</code>. In addition, it implements <code>Volume</code>
	 * and as such represents the single polygon at index
	 * {@link #currentPolygon} in {@link MeshVolume#polys}.
	 * 
	 * @author Ole Kniemeyer
	 */
	private final class StateImpl extends Octree.State implements Volume
	{
		/**
		 * Index of polygon in {@link MeshVolume#polys} which this
		 * state shall represent.
		 */
		int currentPolygon;

		int excludeStartFace;
		int excludeEndFace;

		/**
		 * Stores the marks for the polygon volumes. Each <code>int</code>
		 * value holds 32 marks. 
		 * 
		 * @see #mark(int)
		 * @see #clear(int)
		 */
		private final int[] markedPolys;

		/**
		 * Caches the signs of the Pl&uuml;cker inner products of the current
		 * line with the edges. For an edge having index <code>e</code> in
		 * {@link MeshVolume#edges} (note that <code>e</code> is even), the
		 * <code>e</code>-th bit of this array indicates the sign of the
		 * inner product, but only if the <code>(e+1)</code>-th bit is set.
		 * If this bit is not set, the sign has not yet been computed.
		 * <p>
		 * The indices of elements which are non-zero have to be recorded
		 * in {@link #nonzeroEdgeSigns}.   
		 */
		private final int[] edgeSigns;

		/**
		 * Records the indices of elements of {@link #edgeSigns}
		 * which are non-zero. This is used to efficiently clear
		 * the array {@link #edgeSigns}.
		 */
		private final IntList nonzeroEdgeSigns = new IntList (16);

		/**
		 * x-component of line direction.
		 */
		private float dx;

		/**
		 * y-component of line direction.
		 */
		private float dy;

		/**
		 * z-component of line direction.
		 */
		private float dz;

		/**
		 * x-component of cross product of origin and direction of line
		 */
		private float cx;

		/**
		 * y-component of cross product of origin and direction of line
		 */
		private float cy;

		/**
		 * z-component of cross product of origin and direction of line
		 */
		private float cz;

		StateImpl ()
		{
			edgeSigns = new int[(edges.length + 31) >> 5];
			markedPolys = new int[(polys.length + 31) >> 5];
		}

		/**
		 * Sets the current line for polygon intersection.
		 * 
		 * @param line current line
		 * @param exclStartFace
		 * @param exclEndFace
		 */
		void setLine (Line line, int exclStartFace, int exclEndFace)
		{
			Vector3d v = tmpVector0;
			v.set (line.origin);
			v.cross (v, line.direction);

			dx = (float) line.direction.x;
			dy = (float) line.direction.y;
			dz = (float) line.direction.z;

			cx = (float) v.x;
			cy = (float) v.y;
			cz = (float) v.z;

			int[] a = nonzeroEdgeSigns.elements;
			for (int i = nonzeroEdgeSigns.size - 1; i >= 0; i--)
			{
				edgeSigns[a[i]] = 0;
			}
			nonzeroEdgeSigns.size = 0;

			excludeStartFace = exclStartFace;
			excludeEndFace = exclEndFace;
		}

		/**
		 * Returns the sign of the Pl&uuml;cker inner product of the
		 * line with a polygon <code>edge</code>. <code>edge</code>
		 * is the value of an element of {@link MeshVolume#polys} as is.
		 * The returned sign respects the original direction of the
		 * polygon edge (which may be the reverse of the direction of the
		 * edge stored in {@link MeshVolume#edges} and is encoded in
		 * bit 0 of <code>edge</code>).
		 * 
		 * @param edge edge including direction information in bit 0
		 * @return sign of Pl&uuml;cker inner product in bit 0, the other
		 * bits are unspecified
		 */
		private int getSign (int edge)
		{
			int e = edge & 0x7ffffffe;
			int r = edgeSigns[e >> 5];
			// take advantage of << using only the lowest 5 bits of second operand 
			if ((r & (2 << e)) != 0)
			{
				// sign has been computed and cached before
				return edge + (r >> e);
			}
			if (r == 0)
			{
				// record non-zero entry in edgeSigns
				nonzeroEdgeSigns.push (e >> 5);
			}
			float[] cp = crossProducts;
			float[] vc = vertexCoordinates;
			int i = (e * 3) >> 1;
			int s = edges[e];
			int t = edges[e + 1];
			// compute Pluecker product
			if (dx * cp[i] + dy * cp[i + 1] + dz * cp[i + 2] + cx
				* (vc[t] - vc[s]) + cy * (vc[t + 1] - vc[s + 1]) + cz
				* (vc[t + 2] - vc[s + 2]) >= 0)
			{
				edgeSigns[e >> 5] = r | (3 << e);
				return edge + 1;
			}
			else
			{
				edgeSigns[e >> 5] = r | (2 << e);
				return edge;
			}
		}

		@Override
		public boolean mark (int id)
		{
			int v = markedPolys[id >> 5];
			// take advantage of << using only the lowest 5 bits of second operand 
			if ((v & (1 << id)) != 0)
			{
				return false;
			}
			markedPolys[id >> 5] = v | (1 << id);
			return true;
		}

		@Override
		public void clear (int id)
		{
			markedPolys[id >> 5] &= ~(1 << id);
		}

		public int getId ()
		{
			return currentPolygon;
		}

		public boolean computeIntersections (Line line, int which,
				IntersectionList list, Intersection excludeStart,
				Intersection excludeEnd)
		{
			// get sign of first edge
			int side = getSign (polys[currentPolygon + 1]);

			// loop over remaining edges
			for (int i = currentPolygon + 3;; i += 2)
			{
				int edge = polys[i];
				if (((side + getSign (edge)) & 1) != 0)
				{
					// different signs of Pluecker product, no intersection
					return false;
				}

				if (edge < 0)
				{
					// last edge has been reached, all signs are equal
					// => line intersects polygon
					if (currentPolygon == excludeStartFace)
					{
						excludeStartFace = -1;
						return false;
					}
					if (currentPolygon == excludeEndFace)
					{
						excludeEndFace = -1;
						return false;
					}

					// compute intersection point of line with
					// plane of polygon
					Vector3d v = list.tmpVector1;
					Vector3d w = list.tmpVector2;
					getNormal (currentPolygon, v);
					int j = edges[edge & 0x7ffffffe];
					w.x = vertexCoordinates[j];
					w.y = vertexCoordinates[j + 1];
					w.z = vertexCoordinates[j + 2];
					w.sub (line.origin);
					double lambda = w.dot (v) / line.direction.dot (v);
					if ((lambda >= line.start) && (lambda <= line.end))
					{
						list.add (MeshVolume.this, line, lambda,
							closed ? ((side & 1) == 0) ? Intersection.ENTERING : Intersection.LEAVING : Intersection.PASSING,
							currentPolygon).volumeVector.z = -1;
					}
					return false;
				}
			}
		}

		public boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
				double radius, Variables temp)
		{
			return boxContainsPolygon (box.min, box.max, temp, currentPolygon);
		}

		public boolean contains (Tuple3d point, boolean open)
		{
			return false;
		}

		public void computeNormal (Intersection is, Vector3d normal)
		{
			throw new UnsupportedOperationException ();
		}

		public void computeUV (Intersection is, Vector2d uv)
		{
			throw new UnsupportedOperationException ();
		}

		public void computeTangents (Intersection is, Vector3d dpdu,
				Vector3d dpdv)
		{
			throw new UnsupportedOperationException ();
		}

		public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
		{
			throw new UnsupportedOperationException ();
		}

		public void setId (int id)
		{
			throw new UnsupportedOperationException ();
		}

		public Volume operator$com ()
		{
			throw new UnsupportedOperationException ();
		}

		public Volume operator$or (Volume v)
		{
			throw new UnsupportedOperationException ();
		}

		public Volume operator$and (Volume v)
		{
			throw new UnsupportedOperationException ();
		}

		public Volume operator$sub (Volume v)
		{
			throw new UnsupportedOperationException ();
		}
	}

	/**
	 * This class implements <code>Octree.Cell</code>. The
	 * list of volumes contained in a cell is represented by a list
	 * of indices into the polygon array {@link MeshVolume#polys}.
	 * 
	 * @author Ole Kniemeyer
	 */
	private static final class CellImpl extends Octree.Cell
	{
		/**
		 * Specifies start indices of those polygons in
		 * {@link MeshVolume#polys} which are contained in this cell.
		 * If <code>polys</code> is <code>null</code>, no polygons are
		 * contained. Otherwise, the {@link #volumeCount} elements
		 * of the <code>int</code>-array <code>polys</code>
		 * starting at {@link #polysStart} specify the indices. 
		 */
		int[] polys;

		int polysStart = 0;
		int volumeCount = 0;

		CellImpl (int position, int capacity)
		{
			super (position);
			polys = new int[capacity];
		}

		@Override
		public int getVolumeCount ()
		{
			return volumeCount;
		}

		@Override
		public void clearVolumes ()
		{
			polys = null;
			volumeCount = 0;
		}

		@Override
		public Volume getVolume (int index, Octree.State state)
		{
			((StateImpl) state).currentPolygon = polys[polysStart + index];
			// now state represents the index-th polygon of this cell 
			return (StateImpl) state;
		}

		@Override
		public void addVolume (Volume v)
		{
			// v has been returned previously by invocation of getVolume
			// on parent cell, so the polygon index can be obtained
			// from v safely in this way
			polys[polysStart + volumeCount++] = ((StateImpl) v).currentPolygon;
		}

		@Override
		protected Octree.Cell createChild (int position)
		{
			return new CellImpl (position, getVolumeCount ());
		}

		@Override
		void finish (Octree tree)
		{
			super.finish (tree);
			if (polys != null)
			{
				polysStart = ((OctreeImpl) tree).addCellPolys (polys,
					volumeCount);
				polys = ((OctreeImpl) tree).cellPolys;
			}
		}
	}

	private static final class OctreeImpl extends Octree implements Cloneable
	{
		private final CellIterator iterator;

		MeshVolume mesh;

		int[] cellPolys;
		int cellPolysIndex;

		OctreeImpl (MeshVolume mesh, CellIterator it)
		{
			this.mesh = mesh;
			iterator = it;
		}

		@Override
		public State createState ()
		{
			StateImpl state = mesh.new StateImpl ();
			state.cellIterator = iterator.dup ();
			return state;
		}

		OctreeImpl dup (MeshVolume mesh)
		{
			try
			{
				OctreeImpl t = (OctreeImpl) clone ();
				t.mesh = mesh;
				return t;
			}
			catch (CloneNotSupportedException e)
			{
				throw new AssertionError (e);
			}
		}

		@Override
		protected ArrayList getInfiniteVolumes ()
		{
			return null;
		}

		@Override
		protected void finishCells ()
		{
			cellPolys = new int[countVolumes (getRoot ())];
			cellPolysIndex = 0;
			super.finishCells ();
		}

		int addCellPolys (int[] polys, int size)
		{
			int cpi = cellPolysIndex;
			System.arraycopy (polys, 0, cellPolys, cpi, size);
			cellPolysIndex = cpi + size;
			return cpi;
		}
	}

	static int countVolumes (Octree.Cell c)
	{
		int v = c.getVolumeCount ();
		if (c.children != null)
		{
			for (int i = 0; i < 8; i++)
			{
				v += countVolumes (c.children[i]);
			}
		}
		return v;
	}

	static void push (FloatList f, Tuple3d t)
	{
		f.push ((float) t.x).push ((float) t.y).push ((float) t.z);
	}

	/**
	 * Creates a duplicate of this mesh. The complete polygon data is
	 * simply referenced from the new duplicate, so no new data arrays
	 * are allocated. The duplicate can be shifted to another location
	 * using {@link #setTransformation(Matrix4d)}.
	 * 
	 * @return a duplicate of this volume
	 */
	public MeshVolume dup ()
	{
		try
		{
			MeshVolume v = (MeshVolume) clone ();
			v.octree = octree.dup (v);
			return v;
		}
		catch (CloneNotSupportedException e)
		{
			throw new AssertionError (e);
		}
	}

	private static int d2i (double d)
	{
		return ((d > 0) ? (int) (511.5f * d) : (int) (511.5f * d - 1)) & 1023;
	}

	/**
	 * Sets the mesh of this volume to the specified <code>mesh</code>.
	 * All data is copied, no persistent reference
	 * to <code>mesh</code> is made. The method
	 * {@link #setTransformation} has to be invoked afterwards to specify
	 * the global coordinate transformation.
	 * 
	 * @param mesh a mesh
	 */
	public void setMesh (Mesh mesh)
	{
		int pc = mesh.getPolygonCount ();
		int[] indices = new int[mesh.getMaxEdgeCount ()];
		int[] normalIndices = new int[mesh.getMaxEdgeCount ()];
		Point3d meshMin = new Point3d ();
		Point3d meshMax = new Point3d ();
		meshMin.x = meshMin.y = meshMin.z = Double.POSITIVE_INFINITY;
		meshMax.x = meshMax.y = meshMax.z = Double.NEGATIVE_INFINITY;
		Vector3d a = new Vector3d ();
		Vector3d b = new Vector3d ();
		Vector3d p = new Vector3d ();
		Vector3d q = new Vector3d ();
		Vector3d r = new Vector3d ();
		Vector3d tmp = new Vector3d ();
		Vector2d uv = new Vector2d ();

		vertexCoordinates = new float[mesh.getVertexCount () * FLOATS_PER_VERTEX];
		// copy vertex and uv coordinates
		for (int i = mesh.getVertexCount () - 1; i >= 0; i--)
		{
			mesh.getVertex (i, p);
			Math2.min (meshMin, p);
			Math2.max (meshMax, p);
			int j = i * FLOATS_PER_VERTEX;
			vertexCoordinates[j] = (float) p.x;
			vertexCoordinates[j + 1] = (float) p.y;
			vertexCoordinates[j + 2] = (float) p.z;
//			mesh.getNormal (i, p);
//			p.normalize ();
//			vertexCoordinates[j + 3] = (float) p.x;
//			vertexCoordinates[j + 4] = (float) p.y;
//			vertexCoordinates[j + 5] = (float) p.z;
			mesh.getUV (i, uv);
			vertexCoordinates[j + 3] = (float) uv.x;
			vertexCoordinates[j + 4] = (float) uv.y;
		}

		magnitude = meshMax.distance (meshMin);

		IntList edgeMap = new IntList (6 * pc).push (0);
		IntList edgeList = new IntList (4 * pc);
		IntList polyList = new IntList (4 * pc);
		IntList boxPolyIndices = new IntList (4 * pc);
		FloatList boxPolyDataList = new FloatList (11 * pc);
		int[] mapStart = new int[mesh.getVertexCount ()];
		FloatList trafoList = new FloatList (pc * 8);
		double epsSquared = meshMax.distanceSquared (meshMin) * 1e-8;
		IntList polyIndices = new IntList (pc);
		Point3d pmin = new Point3d ();
		Point3d pmax = new Point3d ();
		Vector3d normal = new Vector3d ();
		Matrix3d m3 = new Matrix3d ();

		for (int i = 0; i < pc; i++)
		{
			boolean planar = mesh.isPolygonPlanar (i);
			int length = mesh.getPolygon (i, indices, normalIndices);
			int n1 = planar ? length - 1 : 2;

			// non-planar polygons are converted to triangles
			for (int triangle = n1; triangle < length; triangle++)
			{
				if (!planar)
				{
					// this implements the conversion to triangles:
					// the polygon is converted to a triangle fan
					// having the first polygon vertex as common vertex
					indices[1] = indices[triangle - 1];
					indices[2] = indices[triangle];
					normalIndices[1] = normalIndices[triangle - 1];
					normalIndices[2] = normalIndices[triangle];
				}
				int begin = polyList.size;
				polyList.push (trafoList.size);

				int edgeCount = 0;
				int start = indices[0];
				mesh.getVertex (start, a);
				pmin.set (a);
				pmax.set (a);
				normal.x = normal.y = normal.z = 0;
				double pmagn = 0;
				for (int j = 0; j <= n1; j++)
				{
					int end = indices[(j == n1) ? 0 : (j + 1)];
					mesh.getVertex (end, b);
					Math2.min (pmin, b);
					Math2.max (pmax, b);
					tmp.sub (b, a);
					double ls = tmp.lengthSquared ();
					if (ls < epsSquared)
					{
						// collapse vertices which are very close
						continue;
					}
					pmagn += Math.sqrt (ls);

					tmp.cross (b, a);
					normal.add (tmp);

					edgeCount++;
					switch (edgeCount)
					{
						case 1:
							p.set (a);
							break;
						case 2:
							q.sub (a, p);
							r.sub (b, a);
							break;
						case 3:
							// first three edges of polygon available,
							// compute matrix of polygon transformation
							m3.setColumn (0, q);
							m3.setColumn (1, r);
							q.cross (q, r);
							m3.setColumn (2, q);
							m3.invert ();
							trafoList.push ((float) m3.m00).push (
								(float) m3.m01).push ((float) m3.m02).push (
								(float) m3.m10).push ((float) m3.m11).push (
								(float) m3.m12);
							break;
						case 4:
							// polygon has more than three edges,
							// compute two additional values for polygon
							// transformation
							q.sub (a, p);
							m3.transform (q);
							trafoList.push ((float) (q.x / q.y)).push (
								(Math.abs (q.y - 1) < 1e-6) ? 1e6f
										: (float) ((q.y - q.x) / (q.y - 1)));
							polyList.elements[begin] |= 1;
							break;
					}

					boolean flip;
					int s;
					int e;
					if (start > end)
					{
						flip = true;
						s = end;
						e = start;
					}
					else
					{
						flip = false;
						s = start;
						e = end;
					}
					int edge;
					findEdge:
					{
						int k = mapStart[s];
						if (k == 0)
						{
							mapStart[s] = edgeMap.size;
						}
						else
						{
							while (true)
							{
								if (edgeMap.elements[k] == e)
								{
									edge = edgeMap.elements[k + 1];
									break findEdge;
								}
								int m = edgeMap.elements[k + 2];
								if (m == 0)
								{
									edgeMap.elements[k + 2] = edgeMap.size;
									break;
								}
								k = m;
							}
						}
						edge = edgeList.size;
						edgeList.push (s * FLOATS_PER_VERTEX).push (e * FLOATS_PER_VERTEX);
						edgeMap.push (e).push (edge).push (0);
					}
					polyList.push (flip ? edge | 1 : edge);
					mesh.getNormal (normalIndices[j], tmp);
					tmp.normalize ();
					polyList.push (d2i (tmp.x) + (d2i (tmp.y) << 10) + (d2i (tmp.z) << 20));
					start = end;
					Vector3d t = a;
					a = b;
					b = t;
				}
				if (edgeCount < 3)
				{
					// ignore polygon
					polyList.setSize (begin);
				}
				else
				{
					polyIndices.push (begin);
					// mark last edge as terminal edge
					polyList.elements[polyList.size - 2] |= 0x80000000;
					boxPolyIndices.setSize (begin + 1);
					boxPolyIndices.elements[begin] = boxPolyDataList.size;
					normal.normalize ();
					push (boxPolyDataList, pmin);
					push (boxPolyDataList, pmax);
					push (boxPolyDataList, normal);
					boxPolyDataList.push ((float) normal.dot (a)).push (
						1e-4f * (float) pmagn);
				}
			}
		}

		polyCount = polyIndices.size;
		CellImpl root = new CellImpl (Octree.Cell.ROOT_POSITION, 0);
		root.volumeCount = polyCount;
		root.polys = polyIndices.toArray ();

		boxPolyIndex = boxPolyIndices.elements;
		boxPolyIndices = null;
		boxPolyData = boxPolyDataList.elements;
		boxPolyDataList = null;

		polyTransformations = trafoList.toArray ();
		trafoList = null;
		edges = edgeList.toArray ();
		edgeList = null;
		polys = polyList.toArray ();
		polyList = null;

		crossProducts = new float[(edges.length * 3) >> 1];
		// compute cross products
		for (int i = 0, e = 0; i < edges.length; i += 2, e += 3)
		{
			mesh.getVertex (edges[i] / FLOATS_PER_VERTEX, p);
			mesh.getVertex (edges[i + 1] / FLOATS_PER_VERTEX, q);
			q.cross (p, q);
			crossProducts[e] = (float) q.x;
			crossProducts[e + 1] = (float) q.y;
			crossProducts[e + 2] = (float) q.z;
		}
		
		closed = mesh.isClosed ();

		CellIterator it = new DefaultCellIterator ();
		octree = new OctreeImpl (this, it);
		octree.initialize (Octree.suggestDepth (polyCount), MIN_CELL_OBJECTS, meshMin,
			meshMax, root);
		it.initialize (octree);

		boxPolyData = null;
		boxPolyIndex = null;
	}

	/**
	 * Sets the transformation from mesh coordinates
	 * to global world coordinates.
	 * 
	 * @param meshToWorld transformation matrix
	 */
	public void setTransformation (Matrix4d meshToWorld)
	{
		worldToMesh = new Matrix4d ();
		worldToMesh.m33 = 1;
		Math2.invertAffine (meshToWorld, worldToMesh);
		normalToWorld = new Matrix3d ();
		worldToMesh.getRotationScale (normalToWorld);
		normalToWorld.transpose ();

		min = new Point3d ();
		max = new Point3d ();
		min.x = min.y = min.z = Double.POSITIVE_INFINITY;
		max.x = max.y = max.z = Double.NEGATIVE_INFINITY;

		Point3d v = new Point3d ();

		for (int i = 0; i < vertexCoordinates.length; i += FLOATS_PER_VERTEX)
		{
			v.x = vertexCoordinates[i];
			v.y = vertexCoordinates[i + 1];
			v.z = vertexCoordinates[i + 2];
			meshToWorld.transform (v);
			Math2.min (min, v);
			Math2.max (max, v);
		}
	}

	private void getNormal (int polyStart, Vector3d normal)
	{
		float nx = 0;
		float ny = 0;
		float nz = 0;
		float[] cp = crossProducts;
		// compute normal vector as the sum of the cross products
		// of the edge coordinates
		for (int j = polyStart + 1;; j += 2)
		{
			int edge = polys[j];
			int e = ((edge & 0x7ffffffe) * 3) >> 1;
			if ((edge & 1) == 0)
			{
				nx += cp[e];
				ny += cp[e + 1];
				nz += cp[e + 2];
			}
			else
			{
				nx -= cp[e];
				ny -= cp[e + 1];
				nz -= cp[e + 2];
			}
			if (edge < 0)
			{
				normal.x = nx;
				normal.y = ny;
				normal.z = nz;
				return;
			}
		}
	}
	
	/**
	 * Compute the face normal of the triangle that was hit (is.face).
	 * The normal vector is calculated as the cross product between the edge
	 * vectors of the triangle.
	 * @param is intersection between this volume and a ray
	 * @param normal output memory for computed normal vector
	 */
	public void computeFaceNormal(Intersection is, Vector3d normal) {
		getNormal(is.face, normal);
		normalToWorld.transform(normal);
		normal.normalize();
	}

	private float getMagnitude (int polyStart)
	{
		float[] vc = vertexCoordinates;
		float sum = 0;
		int s = edges[polys[polyStart + 1]];
		for (int j = polyStart + 3;; j += 2)
		{
			int t = edges[polys[j] & 0x7fffffff];
			sum += Math.abs (vc[t] - vc[s]) + Math.abs (vc[t + 1] - vc[s + 1])
				+ Math.abs (vc[t + 2] - vc[s + 2]);
			if (polys[j] < 0)
			{
				return sum;
			}
			s = t;
		}
	}

	public boolean computeIntersections (Line line, int which,
			IntersectionList list, Intersection excludeStart,
			Intersection excludeEnd)
	{
		int w = which;
		double ls = line.start;
		double le = line.end;
		boolean checkExclude = false;
		int exclStartFace;

		Tuple3d origOrigin = line.origin;
		line.origin = list.tmpPoint0;
		Math2.transformPoint (worldToMesh, origOrigin, line.origin);

		Vector3d origDirection = line.direction;
		line.direction = list.tmpVector0;
		worldToMesh.transform (origDirection, line.direction);

		double eps = 1e-4 / line.direction.length ();

		if ((excludeStart != null) && (excludeStart.volume == this))
		{
			exclStartFace = excludeStart.face;
			line.start -= eps * magnitude;
			w = Intersection.ALL;
			checkExclude = true;
		}
		else
		{
			excludeStart = null;
			exclStartFace = -1;
		}
		int exclEndFace;
		if ((excludeEnd != null) && (excludeEnd.volume == this))
		{
			exclEndFace = excludeEnd.face;
			if (exclStartFace == exclEndFace)
			{
				line.origin = origOrigin;
				line.direction = origDirection;
				return false;
			}
			line.end += eps * magnitude;
			w = Intersection.ALL;
			checkExclude = true;
		}
		else
		{
			excludeEnd = null;
			exclEndFace = -1;
		}
		StateImpl s = (StateImpl) list.cache.get (this);
		if (s == null)
		{
			s = (StateImpl) octree.createState ();
			list.cache.put (this, s);
		}
		s.setLine (line, exclStartFace, exclEndFace);
		int begin = list.size;
		boolean b = octree.computeIntersections (line, w, list, excludeStart,
			excludeEnd, s);
		if (checkExclude)
		{
			line.start = ls;
			line.end = le;
			if (list.size > begin)
			{
				if (s.excludeStartFace >= 0)
				{
					int i = list.findClosestIntersection (ls, eps
						* getMagnitude (s.excludeStartFace), begin, list.size);
					if (i >= 0)
					{
						list.remove (i, i + 1);
					}
				}
				if (s.excludeEndFace >= 0)
				{
					int i = list.findClosestIntersection (le, eps
						* getMagnitude (s.excludeEndFace), begin, list.size);
					if (i >= 0)
					{
						list.remove (i, i + 1);
					}
				}
				removeBeforeStart:
				{
					for (int i = begin; i < list.size; i++)
					{
						if (list.elements[i].parameter >= ls)
						{
							list.remove (begin, i);
							break removeBeforeStart;
						}
					}
					list.size = begin;
				}
				removeAfterEnd:
				{
					for (int i = list.size - 1; i >= begin; i--)
					{
						if (list.elements[i].parameter <= le)
						{
							list.remove (i + 1, list.size);
							break removeAfterEnd;
						}
					}
					list.size = begin;
				}
				if ((which != Intersection.ALL) && (list.size > begin))
				{
					list.size = begin + 1;
				}
			}
		}
		line.origin = origOrigin;
		line.direction = origDirection;
		return b;
	}

	boolean boxContainsPolygon (Point3d min, Point3d max, Variables temp,
			int polyStart)
	{
		Vector3d n = temp.tmpVector0;
		Vector3d vertex = temp.tmpVector1;
		double dist;
		double eps;
		if (boxPolyData != null)
		{
			int i = boxPolyIndex[polyStart];
			float[] a = boxPolyData;
			if ((a[i] > max.x) || (a[i + 1] > max.y) || (a[i + 2] > max.z))
			{
				return false;
			}
			if ((a[i + 3] < min.x) || (a[i + 4] < min.y) || (a[i + 5] < min.z))
			{
				return false;
			}
			n.x = a[i + 6];
			n.y = a[i + 7];
			n.z = a[i + 8];
			dist = a[i + 9];
			eps = a[i + 10];
		}
		else
		{
			Point3d pmin = temp.tmpPoint0;
			Point3d pmax = temp.tmpPoint1;
			for (int j = polyStart + 1;; j += 2)
			{
				int edge = polys[j];
				int e = edges[edge & 0x7fffffff];
				vertex.x = vertexCoordinates[e];
				vertex.y = vertexCoordinates[e + 1];
				vertex.z = vertexCoordinates[e + 2];
				if (j == polyStart + 1)
				{
					pmin.set (vertex);
					pmax.set (vertex);
				}
				else
				{
					Math2.min (pmin, vertex);
					Math2.max (pmax, vertex);
				}
				if (edge < 0)
				{
					if (!Math2.lessThanOrEqual (min, pmax)
						|| !Math2.lessThanOrEqual (pmin, max))
					{
						return false;
					}
					break;
				}
			}

			getNormal (polyStart, n);
			n.normalize ();
			dist = n.dot (vertex);
			eps = 1e-4 * getMagnitude (polyStart);
		}

		boolean above = false;
		boolean below = false;
		// loop over all 8 corners of box
		for (int x = 0; x <= 1; x++)
		{
			double nv1 = n.x * ((x == 0) ? min : max).x - dist;
			for (int y = 0; y <= 1; y++)
			{
				double nv2 = nv1 + n.y * ((y == 0) ? min : max).y;
				for (int z = 0; z <= 1; z++)
				{
					double d = nv2 + n.z * ((z == 0) ? min : max).z;
					if (d > eps)
					{
						if (below)
						{
							return true;
						}
						above = true;
					}
					else if (d < -eps)
					{
						if (above)
						{
							return true;
						}
						below = true;
					}
					else
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public boolean contains (Tuple3d point, boolean open)
	{
		return false;
	}

	public void getExtent (Tuple3d min, Tuple3d max, Variables temp)
	{
		min.set (this.min);
		max.set (this.max);
	}

	public boolean boxContainsBoundary (BoundingBox box, Tuple3d center,
			double radius, Variables temp)
	{
		if (!Math2.lessThanOrEqual (box.min, max)
			|| !Math2.lessThanOrEqual (min, box.max))
		{
			// bounding boxes have an empty intersection
			return false;
		}
		return true;
	}

	private boolean getUVOfPoly (Intersection is, Tuple3d tmp)
	{
		if (is.volumeVector.z < 0)
		{
			is.volumeVector.z = 1;
			Math2.transformPoint (worldToMesh, is.getPoint (), tmp);
			int i = polys[is.face] & ~1;
			int vertex = edges[polys[is.face + 1]];
			double px = tmp.x - vertexCoordinates[vertex];
			double py = tmp.y - vertexCoordinates[vertex + 1];
			double pz = tmp.z - vertexCoordinates[vertex + 2];
			double x = polyTransformations[i] * px + polyTransformations[i + 1]
				* py + polyTransformations[i + 2] * pz;
			double y = polyTransformations[i + 3] * px
				+ polyTransformations[i + 4] * py + polyTransformations[i + 5]
				* pz;
			if ((polys[is.face] & 1) != 0)
			{
				double q = polyTransformations[i + 6];
				is.volumeVector.x = 1 + (1 - x) / (y * q - 1);
				q = polyTransformations[i + 7];
				is.volumeVector.y = y * (1 - q) / (x - q);
				return true;
			}
			else
			{
				is.volumeVector.x = x;
				is.volumeVector.y = (x < 1e-6) ? 0 : y / x;
				return false;
			}
		}
		else
		{
			return (polys[is.face] & 1) != 0;
		}
	}

	private void interpolate (int ofs, boolean three, Intersection is,
			Tuple3d out)
	{
		boolean quad = getUVOfPoly (is, out);
		int j = is.face + 1;
		double pu = is.volumeVector.x;
		double pv = is.volumeVector.y;
		double t = pu * (1 - pv);
		if (ofs < 0)
		{
			int n = polys[j + 3];
			out.x = t * ((n << 22) | (1 << 21));
			out.y = t * ((n << 12) | (1 << 21));
			out.z = t * ((n << 2) | (1 << 21));
		}
		else
		{
			int vertex = edges[polys[j + 2]] + ofs;
			out.x = t * vertexCoordinates[vertex];
			out.y = t * vertexCoordinates[vertex + 1];
			if (three)
			{
				out.z = t * vertexCoordinates[vertex + 2];
			}
		}
		t = pu * pv;
		if (ofs < 0)
		{
			int n = polys[j + 5];
			out.x += t * ((n << 22) | (1 << 21));
			out.y += t * ((n << 12) | (1 << 21));
			out.z += t * ((n << 2) | (1 << 21));
		}
		else
		{
			int vertex = edges[polys[j + 4] & 0x7fffffff] + ofs;
			out.x += t * vertexCoordinates[vertex];
			out.y += t * vertexCoordinates[vertex + 1];
			if (three)
			{
				out.z += t * vertexCoordinates[vertex + 2];
			}
		}
		if (quad)
		{
			t = (1 - pu) * (1 - pv);
			if (ofs < 0)
			{
				int n = polys[j + 1];
				out.x += t * ((n << 22) | (1 << 21));
				out.y += t * ((n << 12) | (1 << 21));
				out.z += t * ((n << 2) | (1 << 21));
			}
			else
			{
				int vertex = edges[polys[j]] + ofs;
				out.x += t * vertexCoordinates[vertex];
				out.y += t * vertexCoordinates[vertex + 1];
				if (three)
				{
					out.z += t * vertexCoordinates[vertex + 2];
				}
			}
			t = (1 - pu) * pv;
			if (ofs < 0)
			{
				int n = polys[j + 7];
				out.x += t * ((n << 22) | (1 << 21));
				out.y += t * ((n << 12) | (1 << 21));
				out.z += t * ((n << 2) | (1 << 21));
			}
			else
			{
				int vertex = edges[polys[j + 6] & 0x7fffffff] + ofs;
				out.x += t * vertexCoordinates[vertex];
				out.y += t * vertexCoordinates[vertex + 1];
				if (three)
				{
					out.z += t * vertexCoordinates[vertex + 2];
				}
			}
		}
		else
		{
			t = 1 - pu;
			if (ofs < 0)
			{
				int n = polys[j + 1];
				out.x += t * ((n << 22) | (1 << 21));
				out.y += t * ((n << 12) | (1 << 21));
				out.z += t * ((n << 2) | (1 << 21));
			}
			else
			{
				int vertex = edges[polys[j]] + ofs;
				out.x += t * vertexCoordinates[vertex];
				out.y += t * vertexCoordinates[vertex + 1];
				if (three)
				{
					out.z += t * vertexCoordinates[vertex + 2];
				}
			}
		}
	}

	public void computeNormal (Intersection is, Vector3d normal)
	{
		interpolate (-1, true, is, normal);
		normalToWorld.transform (normal);
		normal.normalize ();
	}

	public void computeUV (Intersection is, Vector2d uv)
	{
		interpolate (3, false, is, is.tmpVector0);
		uv.x = is.tmpVector0.x;
		uv.y = is.tmpVector0.y;
	}

	public void computeTangents (Intersection is, Vector3d dpdu, Vector3d dpdv)
	{
	}

}
