package de.grogra.gpuflux.scene.volume;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Vector;

import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point2d;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import de.grogra.gpuflux.FluxSettings;
import de.grogra.graph.ContextDependent;
import de.grogra.graph.GraphState;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.Polygonization;
import de.grogra.imp3d.PolygonizationCache;
import de.grogra.imp3d.RenderState;
import de.grogra.imp3d.VolumeBuilderBase;
import de.grogra.imp3d.shading.Shader;
import de.grogra.math.Pool;
import de.grogra.pf.boot.Main;
import de.grogra.pf.ui.Workbench;
import de.grogra.ray2.radiosity.Vector3d;
import de.grogra.vecmath.geom.Cone;
import de.grogra.vecmath.geom.Cube;
import de.grogra.vecmath.geom.Cylinder;
import de.grogra.vecmath.geom.FrustumBase;
import de.grogra.vecmath.geom.HalfSpace;
import de.grogra.vecmath.geom.Sphere;

/**
 * 
 * @author Dietger van Antwerpen
 *
 */

public abstract class FluxVolumeBuilder extends VolumeBuilderBase implements RenderState
{
	// parameter for polygonization
	private static final float EPSILON = 0.0001f;
	
	// list of all vertices
	private Vector<FluxVertex> vertices = new Vector<FluxVertex>();
	
	// current graph state
	GraphState state;
	private float flatness;
	//private PolygonizationCache polyCach;

	private Vector<MeshVolume> meshVolumes = new Vector<MeshVolume>();
		
	public FluxVolumeBuilder(GraphState state, float flatness ) {
		super( null , EPSILON );
		this.state = state;
		
		this.flatness = flatness; 
		
		polyCache = new PolygonizationCache (state, Polygonization.COMPUTE_NORMALS
				| Polygonization.COMPUTE_UV, flatness, true);
	}

	public GraphState getRenderGraphState() {
		return null;
	}

	public Pool getPool() {
		return null;
	}

	public FontMetrics getFontMetrics(Font font) {
		return null;
	}

	public int getCurrentHighlight() {
		return 0;
	}

	public float estimateScaleAt(Tuple3f point) {
		return 0;
	}

	public Shader getCurrentShader() {
		return null;
	}

	public void drawPoint(Tuple3f location, int pixelSize, Tuple3f color,
			int highlight, Matrix4d t) {
	}

	public void drawPointCloud(float[] locations, float pointSize,
			Tuple3f color, int highlight, Matrix4d t) {
		Main.getLogger ().warning("GPUFlux does not support pointclouds, object ignored");
		Workbench.current ().logGUIInfo("GPUFlux does not support pointclouds, object ignored");
	}

	public void drawLine(Tuple3f start, Tuple3f end, Tuple3f color,
			int highlight, Matrix4d t) {
	}

	public void drawParallelogram(float length, Vector3f faxis,
			float scaleU, float scaleV, Shader s, int highlight, Matrix4d t) {
		t = getTransformation( t );
		
		// compute normal transformation matrix
		Matrix4f world2obj = new Matrix4f();
		Matrix4d n = new Matrix4d();
		n.invert(t);
		world2obj.set(n);
		n.transpose(n);
		
		Vector3d axis = new Vector3d( faxis.x , faxis.y , faxis.z );
		
		// calculate normal vector for surface
		Vector3d normal = new Vector3d (0, 0, length);
		normal.cross (axis, normal);
		normal.normalize();
		
		int offset = getVertices().size();
		
		FluxVertex v0 = new FluxVertex(
				new Point3d(-axis.x, -axis.y, -axis.z),
				(Vector3d)normal.clone(),
				new Point2d (0, 0) );
		FluxVertex v1 = new FluxVertex(
				new Point3d (axis.x, axis.y, axis.z),
				(Vector3d)normal.clone(),
				new Point2d (1, 0) );
		FluxVertex v2 = new FluxVertex(
				new Point3d (-axis.x, -axis.y, length - axis.z),
				(Vector3d)normal.clone(),
				new Point2d (0, 1) );
		
		// transform vertex to world space
		v0.transform( t , n );
		v1.transform( t , n );
		v2.transform( t , n );
		
		getVertices().add( v0 );
		getVertices().add( v1 );
		getVertices().add( v2 );
		
		FluxPolygon fluxPolygon = new FluxPolygon( offset , offset + 1 , offset + 2 , getVertices() , true , world2obj );
		fluxPolygon.finish();
		
		addPrimitive( fluxPolygon );
	}

	public void drawPlane(Shader s, int highlight, Matrix4d t) {
		t = getTransformation (t);
		HalfSpace plane = buildPlane( t );
		if( plane != null )
			addInfinitePrimitive( new FluxPlane( plane ) );
	}

	public void drawSphere(float radius, Shader s, int highlight, Matrix4d t) {
		t = getTransformation (t);
		Sphere sphere = buildSphere( radius, t );
		if( sphere != null )
			addPrimitive( new FluxSphere( sphere ) );
	}

	public void drawSupershape(float a, float b, float m1, float n11,
			float n12, float n13, float m2, float n21, float n22, float n23,
			Shader s, int highlight, Matrix4d t) {
		Main.getLogger ().warning("GPUFlux does not support supershape, object ignored");
		Workbench.current ().logGUIInfo("GPUFlux does not support supershape, object ignored");
	}

	public void drawBox(float halfWidth, float halfLength, float height,
			Shader s, int highlight, Matrix4d t) {
		t = getTransformation (t);
		Cube box = buildBox(halfWidth, halfLength, height, t );
		if( box != null )
			addPrimitive( new FluxBox( box ));
	}

	public void drawFrustum(float height, float baseRadius, float topRadius,
			boolean baseClosed, boolean topClosed, float scaleV, Shader s,
			int highlight, Matrix4d t) {
		t = getTransformation (t);
		
		FrustumBase frustum = buildBaseFrustum(height, baseRadius, topRadius,	baseClosed, topClosed, scaleV, t);
		
		if( frustum != null )
		{
			if( frustum instanceof Cone )
			{ 
				addPrimitive( new FluxFrustum( (Cone) frustum ) );
			}
			else if( frustum instanceof Cylinder )
			{
				addPrimitive( new FluxCylinder( (Cylinder) frustum ) );
			}
		}
	}
	
	class MeshVolume
	{
		public MeshVolume(int vertexStart, int vertexCount,
				Vector<FluxPrimitive> primitives, Matrix4d m) {
			this.vertexStart = vertexStart;
			this.vertexCount = vertexCount;
			this.primitives = primitives;
			this.transform = m;
		}

		public int vertexStart, vertexCount;
		public Vector<FluxPrimitive> primitives;
		public Matrix4d transform;
	};
	
	public void drawPolygons(Polygonizable polygons, Object obj,
			boolean asNode, Shader s, int highlight, Matrix4d t) {
		
		PolygonArray mesh = polyCache.get (obj, asNode, polygons);
		
		// compute normal transformation matrix
		t = getTransformation(t);
		
		Matrix4f world2obj = new Matrix4f();
		Matrix4d m = new Matrix4d();
		Matrix4d n = new Matrix4d();
		m.invert(t);
		world2obj.set(m);
		n.transpose(m);
		
		if( !mesh.wasCleared() )
		{
			// clone an old mesh
			
			MeshVolume v = (MeshVolume)mesh.userObject;
			
			if( v == null )
				return;
			
			int offset = getVertices().size();
			
			// clone all vertices
			for( int i = v.vertexStart ; i < v.vertexStart + v.vertexCount ; i++ )
			{
				getVertices().add( (FluxVertex)(getVertices().get(i).clone()) );
			}
			
			Vector<FluxPrimitive> primitives = new Vector<FluxPrimitive>();
			
			// clone all polygons
			for( FluxPrimitive polygon : v.primitives )
			{
				FluxPolygon polygon2 = (FluxPolygon)((FluxPolygon)polygon).clone();
				
				// set new transformation matrix
				polygon2.setWorld2Obj(world2obj);
				primitives.add(polygon2);
			}
			
			// add all new primitives
			addPrimitives( primitives );
			
			MeshVolume vc = new MeshVolume(offset, v.vertexCount, primitives, (Matrix4d)t.clone());
			
			// store the mesh volume
			meshVolumes.add(vc);
			
		}
		else
		{
			// Construct a new mesh
			
			mesh.userObject = null;
			
			Vector3d normal = new Vector3d();
			Point3d point = new Point3d();
			Point2d uv = new Point2d();
			
			int offset = getVertices().size();
			
			if( mesh.getVertexCount() == 0 || mesh.getMaxEdgeCount() == 0 )
				return;
			
			// add all vertices
			for( int i = 0 ; i < mesh.getVertexCount() ; i++ )
			{
				mesh.getVertex(i, point);
				mesh.getNormal(i, normal);
				mesh.getUV(i, uv);
	
				normal.normalize();
				
				FluxVertex vertex = new FluxVertex( point , normal , uv );
				
				getVertices().add( vertex );
			}
			
			int polyCount = mesh.getPolygonCount();
			
			if( polyCount == 0 )
				return;
			
			int [] indicesOut = new int[mesh.getMaxEdgeCount()];
			int [] normalsOut = new int[mesh.getMaxEdgeCount()];
			
			Vector<FluxPrimitive> primitives = new Vector<FluxPrimitive>();
			
			if( mesh.edgeCount > 0 )
			{	
				// triangulate polygons and add as primitives
				for( int i = 0 ; i < polyCount ; i++ )
				{
					mesh.getPolygon(i, indicesOut, normalsOut);
		
					for( int tri = 0 ; tri < indicesOut.length - 2 ; tri++ )
					{
						int idx1 = indicesOut[0];
						int idx2 = indicesOut[tri + 1];
						int idx3 = indicesOut[tri + 2];
						
						primitives.add( new FluxPolygon( idx1 , idx2 , idx3 , getVertices() , false , world2obj ) );
					}
				}
			}
			
			MeshVolume v = new MeshVolume( offset , mesh.getVertexCount() , primitives , (Matrix4d)t.clone() );
			mesh.userObject = v;
			
			addPrimitives( primitives );
			
			// store the mesh volume
			meshVolumes.add(v);
		}
	}

	public void finish()
	{
		// reduce memory footprint by releasing the cache
		polyCache.clear();
		
		for( MeshVolume v : meshVolumes )
		{
			Matrix4d m = new Matrix4d();
			Matrix4d n = new Matrix4d();
			m.invert(v.transform);
			n.transpose(m);
			
			// transform all vertices
			for( int i = v.vertexStart ; i < v.vertexStart + v.vertexCount ; i++ )
				getVertices().get(i).transform(v.transform, n);
			
			// finnish all polygons
			for( FluxPrimitive polygon : v.primitives )
			{
				// transform vertex indices to global locations
				((FluxPolygon)polygon).shiftIndex(v.vertexStart);
				// finish the polygon
				((FluxPolygon)polygon).finish();
			}
		}
	}
	
	protected abstract void addPrimitives(Vector<FluxPrimitive> primitives);

	public boolean getWindowPos(Tuple3f location, Tuple2f out) {
		return false;
	}

	public void drawRectangle(int x, int y, int w, int h, Tuple3f color) {
	}

	public void fillRectangle(int x, int y, int w, int h, Tuple3f color) {
	}

	public void drawString(int x, int y, String text, Font font, Tuple3f color) {
	}
	
	protected abstract void addPrimitive(FluxPrimitive prim );

	protected abstract void addInfinitePrimitive(FluxPrimitive prim);

	public void setVertices(Vector<FluxVertex> vertices) {
		this.vertices = vertices;
	}

	public Vector<FluxVertex> getVertices() {
		return vertices;
	}
	
	public void drawFrustumIrregular(float height, int sectorCount, float[] baseRadii, float[] topRadii, 
			boolean baseClosed, boolean topclosed, 
			float scaleV, Shader s, int highlight, Matrix4d t)
	{
	}
	
	public void drawPrismRectangular(float y, float xPos, float xNeg, float zPos, float zNeg, int highlight, Matrix4d t)
	{
		
	}
}