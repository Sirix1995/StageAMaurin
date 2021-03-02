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
import javax.vecmath.Tuple3d;

import de.grogra.graph.Cache;
import de.grogra.graph.ContextDependent;
import de.grogra.graph.Graph;
import de.grogra.graph.GraphState;
import de.grogra.graph.impl.ContextDependentBase;
import de.grogra.graph.impl.Edge;
import de.grogra.graph.impl.Node;
import de.grogra.imp3d.PolygonArray;
import de.grogra.math.Transform3D;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

/**
 * A <code>GRSMesh</code> looks for {@link de.grogra.imp3d.objects.GRSVertex}
 * nodes in its object context (see {@link de.grogra.imp3d.objects.Polygons}),
 * interprets them as a <it>graph rotation system</code>, and computes
 * the corresponding polygonal mesh.
 * <p>
 * The precise semantics is as follows: The object context is represented by a
 * {@link de.grogra.imp3d.objects.MeshNode}. The <code>GRSMesh</code> looks
 * for all <code>GRSVertex</code> nodes which are directly connected by an edge
 * with the <code>MeshNode</code>, the latter being the source of the edge.
 * All found vertices are taken as vertices of the mesh, their location is
 * obtained by the translational component of their transformation
 * (see {@link de.grogra.imp3d.objects.Null#transform}). For each vertex,
 * the list {@link de.grogra.imp3d.objects.GRSVertex#neighbors} defines its
 * cyclic neighborhood, i.e., all vertices which are connected with the current
 * vertex by an edge of a mesh polygon, sorted in counter-clockwise order when
 * seen from above.
 * <p>
 * When a polygon of a <code>GRSMesh</code> has more than three edges, an
 * additional vertex will be inserted at the center of the polygon vertices.
 * For every polygon edge, a triangle is created using the edge and the
 * additional central vertex.
 * <p>
 * The implementation of <code>GRSMesh</code> cannot handle neighborhoods
 * of more than 63 vertices. In practice, this should not pose any problem.
 * 
 * @author Ole Kniemeyer
 */
public class GRSMesh extends ContextDependentBase implements Polygons
{
	private final Object polygonizeLock = new Object ();

	//enh:sco de.grogra.persistence.SCOType

	public void polygonize (ContextDependent source, GraphState gs,
			PolygonArray out, int flags, float flatness)
	{
		MeshNode mesh = (MeshNode) gs.getObjectContext ().getObject ();
		out.init (3);
		out.edgeCount = 3;
		out.planar = true;
		out.closed = true;

		Matrix4d mat = new Matrix4d ();

		ObjectList<GRSVertex> vertices = new ObjectList<GRSVertex> (32);
		// collect all vertices of graph rotation system
		for (Edge e = mesh.getFirstEdge (); e != null; e = e.getNext (mesh))
		{
			Node n = e.getTarget ();
			if (n instanceof GRSVertex)
			{
				// vertex found
				GRSVertex v = (GRSVertex) n;
				vertices.push (v);
				if ((flags & COMPUTE_UV) != 0)
				{
					out.uv.push (v.u, v.v);
				}

				// obtain translation of vertex
				Transform3D t = v.getTransform ();
				if (t instanceof Tuple3d)
				{
					Tuple3d p = (Tuple3d) t;
					out.vertices.push ((float) p.x, (float) p.y, (float) p.z);
				}
				else if (t != null)
				{
					mat.setIdentity ();
					t.transform (mat, mat);
					out.vertices.push ((float) mat.m03, (float) mat.m13,
						(float) mat.m23);
				}
				else
				{
					out.vertices.push (0, 0, 0);
				}
			}
		}

		int n = vertices.size;

		// Bit j of edgesToProcess[i] is set iff the edge from
		// vertex i to its j-th neighbor has not yet been processed.
		// The sign bit (bit 63) is set iff vertex i has an outgoing
		// mark edge. This technique allows for a maximum of 63 neighbors
		// of a single vertex.
		long[] edgesToProcess = new long[n];

		boolean normals = (flags & COMPUTE_NORMALS) != 0;
		float[] vertexNormals = normals ? new float[n * 3] : null;

		// This contains the vertex indices of the current polygon.
		IntList polyVertices = new IntList (20);

		synchronized (polygonizeLock)
		{
			for (int i = 0; i < n; i++)
			{
				GRSVertex v = vertices.get (i);

				// set vertex index
				v.meshIndex = i;

				// initialize bits in edgesToProcess
				edgesToProcess[i] = (1L << v.neighbors.size) - 1;
				if (v.findAdjacent (false, true, Graph.MARK_EDGE) != null)
				{
					// outgoing mark edge: set sign bit
					edgesToProcess[i] |= 1L << 63;
				}
			}

			for (int i = 0; i < n; i++)
			{
				GRSVertex v = vertices.get (i);

				// process all edges of vertex i
				for (int j = 0; (j < v.neighbors.size)
					&& ((edgesToProcess[i] << 1) != 0); j++)
				{
					// process edge to j-th neighbor
					if ((((int) (edgesToProcess[i] >> j)) & 1) != 0)
					{
						// this edge has not yet been processed
						// => start a new polygon
						int vertexCount = 0;
						polyVertices.clear ();

						float[] coords = out.vertices.elements;

						// initial size of out.polygons
						int polySize = out.polygons.size;

						// sum of (x,y,z) values of polygon vertices
						float sx = 0;
						float sy = 0;
						float sz = 0;

						// sum of (u,v) values of polygon vertices
						float su = 0;
						float sv = 0;

						// sum of cross products of consecutive vertices
						float nx = 0;
						float ny = 0;
						float nz = 0;

						// start vertex of edge
						GRSVertex start = v;
						int startIndex = i;

						// end vertex of edge
						GRSVertex end = v.neighbors.get (j);
						int neighborIndex = j;

						// index of additional central vertex of polygon
						// (if polygon has more than three edges)
						int center = -1;

						// if polygon is marked by mark edge, remove it 
						boolean remove = false;
						do
						{
							if ((edgesToProcess[startIndex] < 0)
								&& (start.findAdjacent (false, true,
									Graph.MARK_EDGE) == end))
							{
								// the current polygon edge from start
								// to end has been marked by a mark edge
								// => don't show the polygon
								remove = true;
							}

							// record new vertex
							polyVertices.add (startIndex);
							vertexCount++;

							// mark current edge as processed
							edgesToProcess[startIndex] &= ~(1L << neighborIndex);

							int endIndex = end.meshIndex;

							int k = startIndex * 3;
							sx += coords[k];
							sy += coords[k + 1];
							sz += coords[k + 2];

							su += start.u;
							sv += start.v;

							if (normals)
							{
								int k2 = endIndex * 3;
								// add cross product "end x start"
								nx += coords[k2 + 1] * coords[k + 2]
									- coords[k2 + 2] * coords[k + 1];
								ny += coords[k2 + 2] * coords[k] - coords[k2]
									* coords[k + 2];
								nz += coords[k2] * coords[k + 1]
									- coords[k2 + 1] * coords[k];
							}

							if (vertexCount == 3)
							{
								if (end == v)
								{
									// this polygon is a triangle
									out.polygons.push (endIndex, startIndex,
										polyVertices.get (1));
									break;
								}
								else
								{
									// this polygon will have more than three
									// edges => append central vertex
									center = out.vertices.size / 3;

									// first triangle using center
									out.polygons.push (center, polyVertices
										.get (1), polyVertices.get (0));

									// second triangle using center
									out.polygons.push (center, startIndex,
										polyVertices.get (1));
								}
							}
							if (vertexCount >= 3)
							{
								// add triangle using current edge and center
								out.polygons
									.push (center, endIndex, startIndex);
							}

							// find neighbor which is next to start in the
							// cyclic list of neighbors of end
							neighborIndex = end.neighbors.indexOf (start) + 1;
							if (neighborIndex <= 0)
							{
								// inconsistent GRS data
								System.err.println ("Inconsistent data: " + end
									+ " does not have " + start
									+ " as neighbor");
								remove = true;
								break;
							}
							if (vertexCount > n)
							{
								// inconsistent GRS data
								System.err.println ("Inconsistent data: " + v
									+ " leads to loop not containing itself");
								remove = true;
								break;
							}
							if (neighborIndex == end.neighbors.size)
							{
								neighborIndex = 0;
							}

							start = end;
							end = end.neighbors.get (neighborIndex);

							startIndex = endIndex;
						}
						while (start != v);

						if (remove)
						{
							// undo addition of polygon
							out.polygons.setSize (polySize);
						}
						else
						{
							if (center >= 0)
							{
								float f = 1f / vertexCount;
								out.vertices.push (sx * f, sy * f, sz * f);
								if ((flags & COMPUTE_UV) != 0)
								{
									out.uv.push (su * f, sv * f);
								}
								if (normals)
								{
									out.setNormal (center, nx, ny, nz);
								}
							}
							if (normals)
							{
								while (!polyVertices.isEmpty ())
								{
									int k = polyVertices.pop () * 3;
									float f = 1 / (float) Math.sqrt (nx * nx
										+ ny * ny + nz * nz);
									vertexNormals[k] += nx * f;
									vertexNormals[k + 1] += ny * f;
									vertexNormals[k + 2] += nz * f;
								}
							}
						}
					}
				}
			}
		}
		if (normals)
		{
			for (int i = 0; i < n; i++)
			{
				int k = i * 3;
				out.setNormal (i, vertexNormals[k], vertexNormals[k + 1],
					vertexNormals[k + 2]);
			}
		}
	}

	public boolean dependsOnContext ()
	{
		return true;
	}

	@Override
	public void writeStamp (Cache.Entry cache, GraphState gs)
	{
		super.writeStamp (cache, gs);
		MeshNode mesh = (MeshNode) gs.getObjectContext ().getObject ();
		for (Edge e = mesh.getFirstEdge (); e != null; e = e.getNext (mesh))
		{
			Node n = e.getTarget ();
			if (n instanceof GRSVertex)
			{
				GRSVertex v = (GRSVertex) n;
				cache.write (v.hashCode ());
				cache.write (v.getStamp ());
			}
		}
	}

	//enh:insert
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final Type $TYPE;


	public static class Type extends de.grogra.persistence.SCOType
	{
		public Type (Class c, de.grogra.persistence.SCOType supertype)
		{
			super (c, supertype);
		}

		public Type (GRSMesh representative, de.grogra.persistence.SCOType supertype)
		{
			super (representative, supertype);
		}

		Type (Class c)
		{
			super (c, de.grogra.persistence.SCOType.$TYPE);
		}


		static Field _addManagedField (Type t, String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			return t.addManagedField (name, modifiers, type, componentType, id);
		}

		@Override
		public Object newInstance ()
		{
			return new GRSMesh ();
		}

	}

	public de.grogra.persistence.ManageableType getManageableType ()
	{
		return $TYPE;
	}


	static
	{
		$TYPE = new Type (GRSMesh.class);
		$TYPE.validate ();
	}

//enh:end

}
