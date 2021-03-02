/*
 * Copyright (C) 2015 GroIMP Developer Team
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;

import de.grogra.graph.GraphState;
import de.grogra.imp.PickList;
import de.grogra.imp3d.Pickable;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.Renderable;
import de.grogra.imp3d.math.delaunay.Tetrahedralization;
import de.grogra.imp3d.math.delaunay.Tetrahedron;
import de.grogra.imp3d.ray.Raytraceable;
import de.grogra.imp3d.ray.RaytracerLeaf;
import de.grogra.imp3d.shading.RGBAShader;
import de.grogra.math.Pool;
import de.grogra.math.util.Geometry;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.IntList;

@SuppressWarnings("unchecked")
public class VoronoiCell extends Sphere implements Pickable, Renderable, Raytraceable {

	private static final long serialVersionUID = 307760750308121L;

	protected boolean showVoronoiNucleus = true;
	//enh:field attr=Attributes.VORONOI_NUCLEUS getter setter

	protected boolean showNeighbours = false;
	//enh:field attr=Attributes.VORONOI_NEIGHBOURS getter setter
	
	protected boolean showVoronoiDiagram = true;
	//enh:field attr=Attributes.VORONOI_DIAGRAM getter setter

	protected boolean showVoronoiFaces = false;
	//enh:field attr=Attributes.VORONOI_FACES getter setter

	protected boolean showVoronoiPoints = false;
	//enh:field attr=Attributes.VORONOI_POINTS getter setter

	protected boolean showDelaunayDiagram = false;
	//enh:field attr=Attributes.DELAUNAY_DIAGRAM getter setter

	protected boolean showDelaunayFaces = false;
	//enh:field attr=Attributes.DELAUNAY_FACES getter setter

	protected boolean showDelaunayPoints = false;
	//enh:field attr=Attributes.DELAUNAY_POINTS getter setter

	
	/**
	 * One of the tetrahedra adjacent to the vertex
	 */
	private Tetrahedron adjacent;

	/**
	 * Reference to the "master class" where this VoronoiCell is registered
	 */
	private Tetrahedralization tetrahedralization;
	
	/**
	 * The number of tetrahedra adjacent to the vertex
	 */
	private int order = 0;

	/**
	 * Minimal zero
	 */
	static final double EPSILON = Math.pow(10D, -20D);

	public VoronoiCell() {
		this(1f);
	}

	public VoronoiCell(float radius) {
		super(radius);
	}

	public VoronoiCell(float radius, Tuple3d p) {
		super();
		this.radius = radius;
		setTranslation(p.x, p.y, p.z);
	}

	public VoronoiCell(float radius, double x, double y, double z) {
		super();
		this.radius = radius;
		setTranslation(x, y, z);
	}

	public VoronoiCell(float radius, double i, double j, double k, double scale) {
		this(radius, i * scale, j * scale, k * scale);
	}
	
	public void setTetrahedralization(Tetrahedralization tetrahedralization) {
		this.tetrahedralization = tetrahedralization;
	}
	
	public double getX() {
		return getTranslation().x;
	}
	public double getY() {
		return getTranslation().y;
	}
	public double getZ() {
		return getTranslation().z;
	}
	
	public Point3f asPoint3f() {
		return new Point3f((float)getX(), (float)getY(), (float)getZ());
	}

	public Point3d asPoint3d() {
		return new Point3d(getX(), getY(), getZ());
	}

	/**
	 * Account for the deletion of an adjacent tetrahedron.
	 */
	public final void deleteAdjacent() {
		order--;
		assert order >= 0;
	}

	/**
	 * Answer one of the adjacent tetrahedron
	 * 
	 * @return
	 */
	public final Tetrahedron getAdjacent() {
		return adjacent;
	}

	/**
	 * Answer the number of tetrahedra adjacent to the receiver vertex in the
	 * tetrahedralization
	 * <p>
	 * 
	 * @return
	 */
	public final int getOrder() {
		return order;
	}

	
	/**
	 * returns a set of points that build the voronoi faces for this VoronoiCell
	 * 
	 * @return set of voronoi points
	 */
	public Set<Point3d> getVoronoiPointsSet() {
		Set<Point3d> pointSet = new HashSet<Point3d>(100);
		//System.out.println("\n\n "+getId()+"  ");
		for(Point3d[] face : tetrahedralization.getVoronoiRegion(this)) {
			//System.out.println("F("+face.length+")= "+Arrays.toString(face)+"\n");
			for(Point3d p:face) {
				//System.out.println("p = "+p+"  dis = "+p.distance(new Point3d())+"  "+(p.distance(new Point3d())<15));
				if(p.distance(new Point3d())<15) pointSet.add(p);
			}
			//System.out.println("S("+pointSet.size()+")=  "+pointSet+"\n\n");
		}
		return pointSet;
	}

	/**
	 * returns an array of points that build the voronoi faces for this VoronoiCell
	 * 
	 * @return array of voronoi points
	 */
	public Point3d[] getVoronoiPointsArray() {
		Set<Point3d> pointSet = getVoronoiPointsSet();
		return pointSet.toArray(new Point3d[pointSet.size()]);
	}
	
	/**
	 * returns a list of points that build the voronoi faces for this VoronoiCell
	 * 
	 * @return list of voronoi points
	 */
	public List<Point3d> getVoronoiPointsList() {
		List<Point3d> pointList = new ArrayList<Point3d>();
		pointList.addAll(getVoronoiPointsSet());
		return pointList;
	}
	
	/**
	 * Answer the collection of neighbouring VoronoiCells around this one.
	 * 
	 * @return collection of neighbouring VoronoiCells
	 */
	public Collection<VoronoiCell> getNeighboursAll() {
		return tetrahedralization.getNeighboursAll(this);
	}
	
	/**
	 * Answer the cleaned collection of neighbouring VoronoiCells around this one.
	 * (cleaned --> without VoronoiCell.getId()==-1)
	 * 
	 * @return cleaned collection of neighbouring VoronoiCells
	 */
	public Collection<VoronoiCell> getNeighbours() {
		return tetrahedralization.getNeighbours(this);
	}
	
	/**
	 * Answer the number of neighbouring VoronoiCells around this one.
	 * 
	 * @return number of neighbouring VoronoiCells
	 */
	public int getNumberOfNeighboursAll() {
		if(tetrahedralization==null || tetrahedralization.getNeighboursAll(this) == null) return 0;
		return tetrahedralization.getNeighboursAll(this).size();
	}
	
	/**
	 * Answer the number of neighbouring VoronoiCells around this one.
	 * 
	 * @return number of neighbouring VoronoiCells
	 */
	public int getNumberOfNeighbours() {
		if(tetrahedralization==null || tetrahedralization.getNeighbours(this) == null) return 0;
		return tetrahedralization.getNeighbours(this).size();
	}
	
	
	/**
	 * Return +1 if the receiver lies inside the sphere passing through a, b, c,
	 * and d; -1 if it lies outside; and 0 if the five points are cospherical.
	 * The vertices a, b, c, and d must be ordered so that they have a positive
	 * orientation (as defined by {@link #orientation(VoronoiCell, VoronoiCell, VoronoiCell)}),
	 * or the sign of the result will be reversed.
	 * <p>
	 * 
	 * @param a
	 *            , b, c, d - the points defining the sphere, in oriented order
	 * @return +1 if the receiver lies inside the sphere passing through a, b,
	 *         c, and d; -1 if it lies outside; and 0 if the five points are cospherical
	 */

	public final int inSphere(VoronoiCell a, VoronoiCell b, VoronoiCell c, VoronoiCell d) {
		double result = Geometry.inSphere(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ(), c.getX(),
				c.getY(), c.getZ(), d.getX(), d.getY(), d.getZ(), getX(), getY(), getZ());
		if (result > 0.0) {
			return 1;
		} else if (result < 0.0) {
			return -1;
		}
		return 0;

	}

	/**
	 * Answer +1 if the orientation of the receiver is positive with respect to
	 * the plane defined by {a, b, c}, -1 if negative, or 0 if the test point is
	 * coplanar
	 * <p>
	 * 
	 * @param a
	 *            , b, c - the points defining the plane
	 * @return +1 if the orientation of the query point is positive with respect
	 *         to the plane, -1 if negative and 0 if the test point is coplanar
	 */
	public final int orientation(VoronoiCell a, VoronoiCell b, VoronoiCell c) {
		double result = Geometry.leftOfPlane(a.getX(), a.getY(), a.getZ(), b.getX(), b.getY(), b.getZ(), c.getX(), c.getY(), c.getZ(), getX(), getY(), getZ());
		if (result > 0.0) {
			return 1;
		} else if (result < 0.0) {
			return -1;
		}
		return 0;
	}

	/**
	 * Reset the state associated with a tetrahedralization.
	 */
	public final void reset() {
		adjacent = null;
		order = 0;
	}

	/**
	 * Note one of the adjacent tetrahedron
	 * <p>
	 * 
	 * @param tetrahedron
	 */
	public final void setAdjacent(Tetrahedron tetrahedron) {
		order++;
		adjacent = tetrahedron;
	}

	@Override
	public String toString() {
		return "id="+getId()+"  neighbours="+getNumberOfNeighbours();
	}
	
	private static void initType() {
		$TYPE.addIdentityAccessor(Attributes.SHAPE);
	}

	public static class Pattern extends de.grogra.xl.impl.base.FieldListPattern {
		public Pattern() {
			super(VoronoiCell.$TYPE, radius$FIELD);
		}

		public static void signature(@In @Out VoronoiCell s, float r) {
		}
	}

	public static void pick(float radius, Point3d origin, Vector3d direction,
			PickList list) {
		double bx, by, bz, s, t;
		bx = -origin.x;
		by = -origin.y;
		bz = -origin.z;
		s = bx * direction.x + by * direction.y + bz * direction.z;
		t = direction.lengthSquared();
		bx = s * s + t * (radius * radius - bx * bx - by * by - bz * bz);
		if (bx < 0) {
			return;
		}
		bx = (bx <= 0d) ? 0d : Math.sqrt(bx);
		if (s >= bx) {
			list.add((s - bx) / t);
		} else if (s + bx >= 0d) {
			list.add((s + bx) / t);
		}
	}

	@Override
	public void pick(Object object, boolean asNode, Point3d origin,
			Vector3d direction, Matrix4d t, PickList list) {
		GraphState gs = list.getGraphState();
		if (object == this) {
			if (gs.getInstancingPathIndex() <= 0) {
				pick(radius, origin, direction, list);
			} else {
				pick(gs.checkFloat(this, true, Attributes.RADIUS, radius),
						origin, direction, list);
			}
		} else {
			pick(gs.getFloat(object, asNode, Attributes.RADIUS), origin,
					direction, list);
		}
	}

	@Override
	public void draw(Object object, boolean asNode, RenderState rs) {
		GraphState gs = rs.getRenderGraphState();
		boolean auxiliary = false; 
		if(tetrahedralization!=null) {
		for(Point3d[] face : tetrahedralization.getVoronoiRegion(this))
			if (isAuxiliary(face)) { auxiliary = true; break; }
		}
		if(showVoronoiNucleus && tetrahedralization!=null && !(!tetrahedralization.isShowAuxiliaryCells() && auxiliary)) {
				
			if (object == this) {
				if (gs.getInstancingPathIndex() <= 0) {
					rs.drawSphere(radius, null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
				} else {
					rs.drawSphere(gs.checkFloat(this, true, Attributes.RADIUS, radius),
							null, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
				}
			} else {
				rs.drawSphere(gs.getFloat(object, asNode, Attributes.RADIUS), null,
						RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
			}
		}
		
		if(tetrahedralization!=null) {
			Pool pool = rs.getPool ();
			Tuple3f p0 = pool.q3f0, p1 = pool.q3f1;
			Tuple3f nullP = pool.q3f2;
			nullP.set(asPoint3f());
			//Voronoi diagram
			if(showVoronoiDiagram || tetrahedralization.isShowVoronoiCells()) {
				if(!auxiliary || tetrahedralization.isShowAuxiliaryFaces()) {
					for(Point3d[] face : tetrahedralization.getVoronoiRegion(this)) {
						for(int i=0;i<face.length-1; i++) {
							p0.set(face[i]); p1.set(face[i+1]);
							p0.sub(nullP); p1.sub(nullP);
							rs.drawLine(p0, p1, new Point3f(0,0,1), RenderState.CURRENT_HIGHLIGHT, null);
							//points
							if(showVoronoiPoints) {
								rs.drawPoint(p0, 3, new Point3f(0,0,0), RenderState.CURRENT_HIGHLIGHT, null);
							}
						}
						//points
						if(showVoronoiPoints) {
							rs.drawPoint(p1, 3, new Point3f(0,0,0), RenderState.CURRENT_HIGHLIGHT, null);
						}
						p0.set(face[face.length-1]); p1.set(face[0]);
						p0.sub(nullP); p1.sub(nullP);
						rs.drawLine(p0, p1, new Point3f(0,0,1), RenderState.CURRENT_HIGHLIGHT, null);
						//faces
						if(showVoronoiFaces && !isAuxiliary(face)) {
							rs.drawPolygons(getMesh(cleanPoints(face)), null, false, RGBAShader.GREEN, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
						}
					}
				}
			}
			//Delaunay triangulation
			if(showDelaunayDiagram || tetrahedralization.isShowDelaunayFaces()) {
					for(Point3d[] face : tetrahedralization.getDelaunayRegion(this)) {
						for(int i=0;i<face.length-1; i++) {
							p0.set(face[i]); p1.set(face[i+1]);
							p0.sub(nullP); p1.sub(nullP);
							if(face[i].distance(face[i+1]) < 15) {
								rs.drawLine(p0, p1, new Point3f(1,0,0), RenderState.CURRENT_HIGHLIGHT, null);
							}
							//points
							if(showDelaunayPoints) {
								rs.drawPoint(p0, 3, new Point3f(0,0,0), RenderState.CURRENT_HIGHLIGHT, null);
							}
						}
						//points
						if(showDelaunayPoints) {
							rs.drawPoint(p1, 3, new Point3f(0,0,0), RenderState.CURRENT_HIGHLIGHT, null);
						}
						p0.set(face[face.length-1]); p1.set(face[0]);
						p0.sub(nullP); p1.sub(nullP);
						if(face[0].distance(face[face.length-1]) < 15) {
							rs.drawLine(p0, p1, new Point3f(1,0,0), RenderState.CURRENT_HIGHLIGHT, null);
						}
						//faces
						if(showDelaunayFaces && !isAuxiliary(face)) {
							rs.drawPolygons(getMesh(cleanPoints(face)), null, false, RGBAShader.GREEN, RenderState.CURRENT_HIGHLIGHT, isRenderAsWireframe(), null);
						}
					}
			}

			//neighbours
			if(showNeighbours) {
				p0.set(new Point3f());
				for(VoronoiCell c : tetrahedralization.getNeighbours(this)) {
					if(c.getId()!=-1) {
						p1.set(c.asPoint3f()); p1.sub(nullP);
						rs.drawLine(p0, p1, new Point3f(1,1,0), RenderState.CURRENT_HIGHLIGHT, null);
					}
				}
			}
		}
	}
	
	protected boolean isAuxiliary(Point3d[] face) {
		if (face.length < 3) return true;
		for (Point3d p : face) if(p.distance(new Point3d())>15) return true;
		return false;
	}

	public boolean isAuxiliaryCell() {
		if(tetrahedralization==null) return false;
		for(Point3d[] face : tetrahedralization.getVoronoiRegion(this))
			if(isAuxiliary(face)) return true;
		return false;
	}

	public boolean isInternalCell() {
		return !isAuxiliaryCell();
	}

	public boolean isBorderCell() {
		if(tetrahedralization==null) return false;
		return tetrahedralization.getVirtualNeighbours(this).size()!=0;
	}

	private float[] cleanPoints(Point3d[] data) {
		FloatList pointList = new FloatList();
		Vector3d nullPos = getTranslation();
		for(Point3d p:data) {
			pointList.add((float)(p.x-nullPos.x));
			pointList.add((float)(p.y-nullPos.y));
			pointList.add((float)(p.z-nullPos.z));
		}
		return pointList.toArray();
	}
	
	private static MeshNode getMesh (float[] pointlist) {
		// triangulate data
		FloatList vertexData = new FloatList();
		for(int i=1; i<=pointlist.length/3-2; i++) {
			vertexData.add(pointlist[0]);   vertexData.add(pointlist[1]);   vertexData.add(pointlist[2]);
			vertexData.add(pointlist[i*3+0]); vertexData.add(pointlist[i*3+1]); vertexData.add(pointlist[i*3+2]);
			vertexData.add(pointlist[i*3+3]); vertexData.add(pointlist[i*3+4]); vertexData.add(pointlist[i*3+5]);
		}
		
		//construct a data type, which can handle a list of triangulated mash
		PolygonMesh polygonMesh = new PolygonMesh();
		int[] tmp = new int[vertexData.size()/3];
		for(int i = 0; i<tmp.length; i++) tmp[i]=i;
		// set a list of the indices of the used list of vertices
		// normally = {0,1,2,3,...,n}, where n is the number of used vertices minus one 
		polygonMesh.setIndexData(new IntList(tmp));
		// set the list of vertices
		polygonMesh.setVertexData(vertexData);

		// put the data type (our polygon mesh) into a drawable object
		MeshNode mesh = new MeshNode();
		mesh.setPolygons(polygonMesh);
		mesh.setShader(RGBAShader.GREEN);
		return mesh;
	}
	
	@Override
	public RaytracerLeaf createRaytracerLeaf(Object object, boolean asNode,
			long pathId, GraphState gs) {
		float cur_radius = 0.0f;

		if (object == this) {
			if (gs.getInstancingPathIndex() <= 0) {
				cur_radius = radius;
			} else {
				cur_radius = gs.checkFloat(this, true, Attributes.RADIUS,
						radius);
			}
		} else {
			cur_radius = gs.getFloat(object, asNode, Attributes.RADIUS);
		}

		return new de.grogra.imp3d.ray.RTSphere(object, asNode, pathId,
				cur_radius);
	}

	// enh:insert initType ();
//enh:begin
// NOTE: The following lines up to enh:end were generated automatically

	public static final NType $TYPE;

	public static final NType.Field showVoronoiNucleus$FIELD;
	public static final NType.Field showNeighbours$FIELD;
	public static final NType.Field showVoronoiDiagram$FIELD;
	public static final NType.Field showVoronoiFaces$FIELD;
	public static final NType.Field showVoronoiPoints$FIELD;
	public static final NType.Field showDelaunayDiagram$FIELD;
	public static final NType.Field showDelaunayFaces$FIELD;
	public static final NType.Field showDelaunayPoints$FIELD;

	private static final class _Field extends NType.Field
	{
		private final int id;

		_Field (String name, int modifiers, de.grogra.reflect.Type type, de.grogra.reflect.Type componentType, int id)
		{
			super (VoronoiCell.$TYPE, name, modifiers, type, componentType);
			this.id = id;
		}

		@Override
		public void setBoolean (Object o, boolean value)
		{
			switch (id)
			{
				case 0:
					((VoronoiCell) o).showVoronoiNucleus = value;
					return;
				case 1:
					((VoronoiCell) o).showNeighbours = value;
					return;
				case 2:
					((VoronoiCell) o).showVoronoiDiagram = value;
					return;
				case 3:
					((VoronoiCell) o).showVoronoiFaces = value;
					return;
				case 4:
					((VoronoiCell) o).showVoronoiPoints = value;
					return;
				case 5:
					((VoronoiCell) o).showDelaunayDiagram = value;
					return;
				case 6:
					((VoronoiCell) o).showDelaunayFaces = value;
					return;
				case 7:
					((VoronoiCell) o).showDelaunayPoints = value;
					return;
			}
			super.setBoolean (o, value);
		}

		@Override
		public boolean getBoolean (Object o)
		{
			switch (id)
			{
				case 0:
					return ((VoronoiCell) o).isShowVoronoiNucleus ();
				case 1:
					return ((VoronoiCell) o).isShowNeighbours ();
				case 2:
					return ((VoronoiCell) o).isShowVoronoiDiagram ();
				case 3:
					return ((VoronoiCell) o).isShowVoronoiFaces ();
				case 4:
					return ((VoronoiCell) o).isShowVoronoiPoints ();
				case 5:
					return ((VoronoiCell) o).isShowDelaunayDiagram ();
				case 6:
					return ((VoronoiCell) o).isShowDelaunayFaces ();
				case 7:
					return ((VoronoiCell) o).isShowDelaunayPoints ();
			}
			return super.getBoolean (o);
		}
	}

	static
	{
		$TYPE = new NType (new VoronoiCell ());
		$TYPE.addManagedField (showVoronoiNucleus$FIELD = new _Field ("showVoronoiNucleus", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 0));
		$TYPE.addManagedField (showNeighbours$FIELD = new _Field ("showNeighbours", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 1));
		$TYPE.addManagedField (showVoronoiDiagram$FIELD = new _Field ("showVoronoiDiagram", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 2));
		$TYPE.addManagedField (showVoronoiFaces$FIELD = new _Field ("showVoronoiFaces", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 3));
		$TYPE.addManagedField (showVoronoiPoints$FIELD = new _Field ("showVoronoiPoints", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 4));
		$TYPE.addManagedField (showDelaunayDiagram$FIELD = new _Field ("showDelaunayDiagram", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 5));
		$TYPE.addManagedField (showDelaunayFaces$FIELD = new _Field ("showDelaunayFaces", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 6));
		$TYPE.addManagedField (showDelaunayPoints$FIELD = new _Field ("showDelaunayPoints", _Field.PROTECTED  | _Field.SCO, de.grogra.reflect.Type.BOOLEAN, null, 7));
		$TYPE.declareFieldAttribute (showVoronoiNucleus$FIELD, Attributes.VORONOI_NUCLEUS);
		$TYPE.declareFieldAttribute (showNeighbours$FIELD, Attributes.VORONOI_NEIGHBOURS);
		$TYPE.declareFieldAttribute (showVoronoiDiagram$FIELD, Attributes.VORONOI_DIAGRAM);
		$TYPE.declareFieldAttribute (showVoronoiFaces$FIELD, Attributes.VORONOI_FACES);
		$TYPE.declareFieldAttribute (showVoronoiPoints$FIELD, Attributes.VORONOI_POINTS);
		$TYPE.declareFieldAttribute (showDelaunayDiagram$FIELD, Attributes.DELAUNAY_DIAGRAM);
		$TYPE.declareFieldAttribute (showDelaunayFaces$FIELD, Attributes.DELAUNAY_FACES);
		$TYPE.declareFieldAttribute (showDelaunayPoints$FIELD, Attributes.DELAUNAY_POINTS);
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
		return new VoronoiCell ();
	}

	public boolean isShowVoronoiNucleus ()
	{
		return showVoronoiNucleus;
	}

	public void setShowVoronoiNucleus (boolean value)
	{
		this.showVoronoiNucleus = value;
	}

	public boolean isShowNeighbours ()
	{
		return showNeighbours;
	}

	public void setShowNeighbours (boolean value)
	{
		this.showNeighbours = value;
	}

	public boolean isShowVoronoiDiagram ()
	{
		return showVoronoiDiagram;
	}

	public void setShowVoronoiDiagram (boolean value)
	{
		this.showVoronoiDiagram = value;
	}

	public boolean isShowVoronoiFaces ()
	{
		return showVoronoiFaces;
	}

	public void setShowVoronoiFaces (boolean value)
	{
		this.showVoronoiFaces = value;
	}

	public boolean isShowVoronoiPoints ()
	{
		return showVoronoiPoints;
	}

	public void setShowVoronoiPoints (boolean value)
	{
		this.showVoronoiPoints = value;
	}

	public boolean isShowDelaunayDiagram ()
	{
		return showDelaunayDiagram;
	}

	public void setShowDelaunayDiagram (boolean value)
	{
		this.showDelaunayDiagram = value;
	}

	public boolean isShowDelaunayFaces ()
	{
		return showDelaunayFaces;
	}

	public void setShowDelaunayFaces (boolean value)
	{
		this.showDelaunayFaces = value;
	}

	public boolean isShowDelaunayPoints ()
	{
		return showDelaunayPoints;
	}

	public void setShowDelaunayPoints (boolean value)
	{
		this.showDelaunayPoints = value;
	}

//enh:end

	/**
	 * Calculates the area of an object. Intersection with other object are not
	 * considered.The total area will be calculated.
	 * 
	 * @return volume
	 */
	@Override
	public double getSurfaceArea() {
		return -1;
	}

	/**
	 * Calculates the volume. Intersection with other object are not
	 * considered.The total volume will be calculated.
	 * 
	 * @return volume
	 */
	@Override
	public double getVolume() {
		return -1;
	}

}
