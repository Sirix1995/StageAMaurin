package de.grogra.imp3d;

import javax.media.opengl.GL;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple2f;
import javax.vecmath.Vector3f;

import com.sun.opengl.util.BufferUtil;

import java.lang.Math;
import java.nio.ByteBuffer;

import de.grogra.graph.ContextDependent;
import de.grogra.graph.GraphState;
import de.grogra.imp3d.PolygonArray;
import de.grogra.imp3d.Polygonizable;
import de.grogra.imp3d.gl.Texture;
import de.grogra.imp3d.objects.Attributes;
import de.grogra.imp3d.objects.Frustum;
import de.grogra.imp3d.shading.Shader;
import javax.media.opengl.GL;

import de.grogra.xl.util.BooleanList;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.ByteList;
import de.grogra.xl.util.ObjectList;

import de.grogra.imp3d.shading.Shader;

import de.grogra.imp3d.IntersectionTests;

/**
 * @author semme
 * 
 */
public class HalfEdgeStructCSG extends HalfEdgeStructSmall {
	

	CSGLink link;
	
	/**
	 * flag for updates
	 * changes everytime triangles are added or removed
	 */
	
	boolean hasUpdated = false;
	
	/**
	 * [n] status of the n-th Vertex
	 * 
	 * 0 = UNDEFINED 1 = BORDER 2 = INSIDE 3 = OUTSIDE 4 = UPDATING
	 * 
	 */
	
	int clusters[];

	ByteList heStatus;
	ByteList triangleStatus;

	final static byte UNDEFINED = 0;
	final static byte BORDER = 1;
	final static byte INSIDE = 2;
	final static byte OUTSIDE = 3;
	final static byte UPDATING = 4;
	final static byte SAME = 5;
	final static byte OPPOSITE = 6;



	
	/**
	 * 
	 */
		
	public HalfEdgeStructCSG(HalfEdgeStructCSG hes) {
		vertices = new DoubleList();
		vertices.addAll(hes.vertices);
		halfEdges = new IntList();
		halfEdges.addAll(hes.halfEdges);
		normals = new FloatList();
		normals.addAll(hes.normals);
		uv = new FloatList();
		uv.addAll(hes.uv);
		vertexToHalfEdge = new IntList();
		vertexToHalfEdge.addAll(hes.vertexToHalfEdge);
//		vertexStatus = new ByteList();
//		vertexStatus.addAll(hes.vertexStatus);
		shader = new ObjectList();
		shader.addAll(hes.shader);
		triangleToShader = new IntList();
		triangleToShader.addAll(hes.triangleToShader);
		
	}

	/**
	 * 
	 */
		
	public void set(HalfEdgeStructCSG hes) {
		
		vertices = new DoubleList();
		vertices.addAll(hes.vertices);
		
		halfEdges = new IntList();
		halfEdges.addAll(hes.halfEdges);
		
		normals = new FloatList();
		normals.addAll(hes.normals);
		
		uv = new FloatList();
		uv.addAll(hes.uv);
		
		vertexToHalfEdge = new IntList();
		vertexToHalfEdge.addAll(hes.vertexToHalfEdge);

		shader = new ObjectList();
		shader.addAll(hes.shader);
		
		triangleToShader = new IntList();
		triangleToShader.addAll(hes.triangleToShader);
		
	}

	/**
	 * 
	 */
		
	public HalfEdgeStructCSG() {
		vertices = new DoubleList();
		halfEdges = new IntList();
		normals = new FloatList();
		uv = new FloatList();
		vertexToHalfEdge = new IntList();
//		vertexStatus = new ByteList();
		heStatus = new ByteList();
		triangleStatus = new ByteList();
		shader = new ObjectList();
		triangleToShader = new IntList();
	}

	/**
	 * prints the content of all arrays of this HalfEdgeObject
	 */
	
	public void printState() {
		
		
		
		System.out.println ("TTS "+this.triangleToShader);
		
		for (int i = 0 ; i < vertices.size()/3 ; i++){
			System.out.println(" ( "+i+" ) [ "+vertices.get (i*3)+" ; "+vertices.get (i*3 + 1)+" ; "+vertices.get (i*3 + 2)+" ]");
		}
		
		
		System.out.println("Half Edges");
		for (int i = 0 ; i < halfEdges.size()/2 ; i++){
			
			System.out.println(" ( "+i+" ) [ "+heGetVertex(i)+" ; "+i/3+" ; "+heGetTwin(i)+" ]");
			
		}
		


	}

	/**
	 * Switches an Half-Edge.
	 * No new Triangles are created.
	 * 
	 * @param halfEdgePos position of the Half-Edge which should be switched
	 */
	
	public void heSwitch(int halfEdgePos) {

		// get half edges involved in the switch
		int he1 = halfEdgePos;
		int he2 = heGetNext(halfEdgePos);
		int he3 = heGetPrev(halfEdgePos);

		int he4 = heGetTwin(halfEdgePos);
		int he5 = heGetNext(he4);
		int he6 = heGetPrev(he4);

		// Save old vertex data
		int v1 = heGetVertex(he1);
		int v2 = heGetVertex(he2);
		int v3 = heGetVertex(he3);
		int v4 = heGetVertex(he5);

		// Save old twin data
		int t2 = heGetTwin(he2);
		int t3 = heGetTwin(he3);
		int t5 = heGetTwin(he5);
		int t6 = heGetTwin(he6);

		// Set the new vertices

		heSetVertex(he1, v4);
		heSetVertex(he2, v1);
		heSetVertex(he3, v2);
		heSetVertex(he4, v2);
		heSetVertex(he5, v3);
		heSetVertex(he6, v4);

		// Set the new Twins

		heSetTwin(he2, t6);
		heSetTwin(t6, he2);

		heSetTwin(he3, t2);
		heSetTwin(t2, he3);

		heSetTwin(he5, t3);
		heSetTwin(t3, he5);

		heSetTwin(he6, t5);
		heSetTwin(t5, he6);

		if (vertexToHalfEdge.get(v1) == he4) {
			vertexToHalfEdge.set(v1, he3);
		}
		if (vertexToHalfEdge.get(v3) == he1) {
			vertexToHalfEdge.set(v3, he6);
		}

	}

	/**
	 * @param facePos
	 * @param point
	 */
	public boolean faceSplitAtPoint(int facePos, Tuple2d point) {
		
		Tuple3d newV = faceGetVertexUV(facePos, point);
		
		int newVertex = insertVertex(newV);
		int v = heGetVertex(faceGetHalfEdge(facePos));
		

		int he1 = faceGetHalfEdge(facePos);
		int he2 = heGetNext(he1);
		int he3 = heGetNext(he2);
		int vertex1 = heGetVertex(he1);
		int vertex2 = heGetVertex(he2);
		int vertex3 = heGetVertex(he3);		
		
		// interpolate normal and uv
		
		Tuple2f pointF = new Point2f();
		pointF.set (point);
		
		Tuple3f newNormal = InterpolateNormal(facePos, pointF);
		
		Tuple2f newUV = InterpolateTexData(facePos, pointF);
		
		Tuple2f tex1 = heGetUV(he1);
		Tuple2f tex2 = heGetUV(he2);
		Tuple2f tex3 = heGetUV(he3);
		Tuple3f normal1 = new Point3f(heGetNormalX(he1), heGetNormalY(he1),
				heGetNormalZ(he1));
		Tuple3f normal2 = new Point3f(heGetNormalX(he2), heGetNormalY(he2),
				heGetNormalZ(he2));
		Tuple3f normal3 = new Point3f(heGetNormalX(he3), heGetNormalY(he3),
				heGetNormalZ(he3));
		int oldCount = halfEdges.size / 2;
		int twin1 = heGetTwin(he1);
		int twin2 = heGetTwin(he2);
		int twin3 = heGetTwin(he3);

		// Add first Triangle from v1-v2-vn

		addTriangle(vertex1, he2, tex1, normal1, vertex2, twin2, tex2, normal2,
				newVertex, oldCount + 3, newUV, newNormal);
		
		triangleToShader.add(triangleToShader.get(facePos));
		
		// Add second Triangle from v2-v3-vn

		addTriangle(vertex2, oldCount + 2, tex2, normal2, vertex3, twin3, tex3,
				normal3, newVertex, he3, newUV, newNormal);

		triangleToShader.add(triangleToShader.get(facePos));		
		
		// alter the data of the Triangle already in List to v3-v1-vn

		heSetVertex(he2, newVertex);
		heSetTwin(twin3, oldCount + 4);
		heSetTwin(twin2, oldCount + 1);
		heSetTwin(he2, oldCount);
		heSetTwin(he3, oldCount + 5);
		heSetUV(he2, newUV);
				
		normals.set((he2 + 1) * 3, newNormal.x);
		normals.set((he2 + 1) * 3 + 1, newNormal.y);
		normals.set((he2 + 1) * 3 + 2, newNormal.z);

		// Add entry to HalfEdgeToVertex Array

		vertexToHalfEdge.add(heGetNext(he2));

		// Update entries if they have changed
		if (vertexToHalfEdge.get(vertex2) == he2) {
			vertexToHalfEdge.set(vertex2, oldCount + 1);
		}
		if (vertexToHalfEdge.get(vertex3) == he3) {
			vertexToHalfEdge.set(vertex3, oldCount + 4);
		}
		
		return true;

	}


	/**
	 * @param halfEdgePos
	 * @param point
	 * @param backfacing
	 */
	
	public boolean heSplitAtPoint(int halfEdgePos, float t,
			boolean backfacing) {
		
		Tuple3d point = InterpolateVertex(halfEdgePos, t);
		Tuple3d pointD = new Point3d();
		pointD.set (point);
		int vnew = insertVertex( pointD );

		// Get all involved Half Edges
		int he1 = halfEdgePos;
		int he2 = heGetNext(he1);
		int he3 = heGetPrev(he1);
		int he4 = heGetTwin(he1);
		int he5 = heGetNext(he4);
		int he6 = heGetPrev(he4);
		// Get all involved Vertices
		int v1 = heGetVertex(he1);
		int v2 = heGetVertex(he4);
		int v3 = heGetVertex(he2);
		int v4 = heGetVertex(he5);

		// Interpolate Normals and Tex
		Tuple3f normalnew1 = InterpolateNormal(he1, t);
		Tuple3f normalnew2 = InterpolateNormal(he4, 1.0f - t);
		Tuple2f newUV1 = InterpolateTex(he1, t);
		Tuple2f newUV2 = InterpolateTex(he4, 1.0f - t);
		
		// insert 2 new Triangles
		int old = halfEdges.size / 2;
		addTriangle(v3, he3, heGetUV(he2), heGetNormal(he2), v2,
				heGetTwin(he3), heGetUV(he3), heGetNormal(he3), vnew, he4,
				newUV1, normalnew1);
		triangleToShader.add(triangleToShader.get(heGetFace(he1)));		
		
		
		addTriangle(v4, he6, heGetUV(he5), heGetNormal(he5), v1,
				heGetTwin(he6), heGetUV(he6), heGetNormal(he6), vnew, he1,
				newUV2, normalnew2);
		triangleToShader.add(triangleToShader.get(heGetFace(he4)));		
		
		// Alter The Data of the 2 Triangles already in List

		// Triangle 1
		heSetVertex(he6, vnew);
		heSetTwin(heGetTwin(he6), old + 4);
		heSetUV(he6, newUV2);
		heSetNormal(he6, normalnew2);
		heSetTwin(he6, old + 3);
		heSetTwin(he4, old + 2);

		// Triangle 2
		heSetVertex(he3, vnew);
		heSetUV(he3, newUV1);
		heSetNormal(he3, normalnew1);
		heSetTwin(heGetTwin(he3), old + 1);
		heSetTwin(he3, old);
		heSetTwin(he1, old + 5);

		// Add Entry to HalfEdgesToVertex Array

		vertexToHalfEdge.add(he4);
		// Update entries if they have changed

		if (vertexToHalfEdge.get(v2) == he1) {
			vertexToHalfEdge.set(v2, old + 2);
		}
		if (vertexToHalfEdge.get(v1) == he4) {
			vertexToHalfEdge.set(v1, old + 5);
		}
		return true;

	}
	
	/**
	 * @param halfEdgePos
	 * @param point
	 * @param backfacing
	 */
	
	public boolean heSplitAtPoint(int halfEdgePos, float t) {
		
		Tuple3d point = InterpolateVertex(halfEdgePos, t);
		Tuple3d pointD = new Point3d();
		int vnew = insertVertex(pointD);

		// Get all involved Half Edges
		int he1 = halfEdgePos;
		int he2 = heGetNext(he1);
		int he3 = heGetPrev(he1);
		int he4 = heGetTwin(he1);
		int he5 = heGetNext(he4);
		int he6 = heGetPrev(he4);
		// Get all involved Vertices
		int v1 = heGetVertex(he1);
		int v2 = heGetVertex(he4);
		int v3 = heGetVertex(he2);
		int v4 = heGetVertex(he5);

		// Interpolate Normals and Tex
		Tuple3f normalnew1 = InterpolateNormal(he1, t);
		Tuple3f normalnew2 = InterpolateNormal(he4, 1.0f - t);
		Tuple2f newUV1 = InterpolateTex(he1, t);
		Tuple2f newUV2 = InterpolateTex(he4, 1.0f - t);
		

		// insert 2 new Triangles
		int old = halfEdges.size / 2;
		addTriangle(v3, he3, heGetUV(he2), heGetNormal(he2), v2,
				heGetTwin(he3), heGetUV(he3), heGetNormal(he3), vnew, he4,
				newUV1, normalnew1);
		triangleToShader.add(triangleToShader.get(heGetFace(he1)));		
		
		
		addTriangle(v4, he6, heGetUV(he5), heGetNormal(he5), v1,
				heGetTwin(he6), heGetUV(he6), heGetNormal(he6), vnew, he1,
				newUV2, normalnew2);
		triangleToShader.add(triangleToShader.get(heGetFace(he4)));		
		
		// Alter The Data of the 2 Triangles already in List

		// Triangle 1
		heSetVertex(he6, vnew);
		heSetTwin(heGetTwin(he6), old + 4);
		heSetUV(he6, newUV2);
		heSetNormal(he6, normalnew2);
		heSetTwin(he6, old + 3);
		heSetTwin(he4, old + 2);

		// Triangle 2
		heSetVertex(he3, vnew);
		heSetUV(he3, newUV1);
		heSetNormal(he3, normalnew1);
		heSetTwin(heGetTwin(he3), old + 1);
		heSetTwin(he3, old);
		heSetTwin(he1, old + 5);

		// Add Entry to HalfEdgesToVertex Array

		vertexToHalfEdge.add(he4);
		// Update entries if they have changed

		if (vertexToHalfEdge.get(v2) == he1) {
			vertexToHalfEdge.set(v2, old + 2);
		}
		if (vertexToHalfEdge.get(v1) == he4) {
			vertexToHalfEdge.set(v1, old + 5);
		}
		return true;

	}
	
	/**
	 * draw method
	 */
	
	public void draw(GL gl) {
		gl.glBegin(GL.GL_TRIANGLES);

		for (int i = 0; i < halfEdges.size() / 2; i++) {
			gl.glNormal3f(normals.get(3 * i), normals.get(3 * i + 1),
					normals.get(3 * i + 2));
			gl.glTexCoord2f(uv.get(i * 2), uv.get(i * 2 + 1));
			gl.glVertex3d(heGetVertexX(i), heGetVertexY(i), heGetVertexZ(i));
		}
		gl.glEnd();
	}
	

	/**
	 * usefull for debugging
	 */
	
	public void drawTwins(GL gl, float c1, float c2, float c3) {
		gl.glLineWidth(3.0f);
		gl.glDisable(GL.GL_DEPTH_TEST);
		for (int i = 0; i < halfEdges.size() / 2; i++) {
			gl.glBegin(GL.GL_LINES);
			gl.glColor3f(c1, c2, c3);

			gl.glVertex3d(heGetVertexX(i), heGetVertexY(i), heGetVertexZ(i));
			gl.glVertex3d(heGetVertexX(heGetTwin(i)),
					heGetVertexY(heGetTwin(i)), heGetVertexZ(heGetTwin(i)));
			
			gl.glEnd();
		}
		gl.glLineWidth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);

	}
	

	/**
	 * Linearly interpolates between the 
	 * 
	 * @param facePos
	 * @param uv
	 * @return
	 */
	public Tuple2f InterpolateTexData(int facePos, Tuple2f uv){
		
		
		Tuple2f tex1 = heGetUV(faceGetHe(facePos,0));
		Tuple2f tex2 = heGetUV(faceGetHe(facePos,1));
		Tuple2f tex3 = heGetUV(faceGetHe(facePos,2));
				
		tex2.sub(tex1);
		tex3.sub(tex1);
		
		float w = 1.0f - (uv.x +uv.y);
		
		if(w<0.0f){
			w=0.0f;
		}
		
		tex2.scale(uv.x);
		tex3.scale(uv.y);
		
		tex1.add(tex2);
		tex1.add(tex3);
		
		return tex1;
		
	}
	
	public Tuple3f InterpolateNormal(int halfEdgePos, float t){
		Tuple3f origin = heGetNormal(heGetTwin(halfEdgePos));
		Tuple3f destination = heGetNormal(halfEdgePos);
		
		destination.sub(origin);
		destination.scale(1.0f-t);
		destination.add(origin);
		
		return destination;
		
	}
	
	public Tuple2f InterpolateTex(int halfEdgePos, float t){
		Tuple2f origin = heGetUV(heGetPrev(halfEdgePos));
		Tuple2f destination = heGetUV(halfEdgePos);
		
		destination.sub(origin);
		destination.scale(t);
		destination.add(origin);
		
		return destination;
		
	}
	
	public Tuple3d InterpolateVertex(int halfEdgePos, float t){
		Tuple3d origin = heGetVertexTuple3f(heGetPrev(halfEdgePos));
		Tuple3d destination = heGetVertexTuple3f(halfEdgePos);
		
		destination.sub(origin);
		destination.scale(t);
		destination.add(origin);
		
		return destination;
	}
	
	/**
	 * @param facePos
	 * @param uv
	 * @return
	 */
	public Tuple3f InterpolateNormal(int facePos, Tuple2f uv){
		Tuple3f n1 = heGetNormal(faceGetHalfEdge(facePos));
		Tuple3f n2 = heGetNormal(heGetNext(faceGetHalfEdge(facePos)));
		Tuple3f n3 = heGetNormal(heGetPrev(faceGetHalfEdge(facePos)));
		
		n2.sub(n1);
		n3.sub(n1);
		
		n2.scale(uv.x);
		n3.scale(uv.y);
		
		Tuple3f out = new Point3f();
		out.add(n1, n2);
		out.add(n3);
		return out;
	}
	
	
	/*
	 * return the intersection point of triangle and a ray, given by 
	 * origin and destination, in baricentric coordinates*/

	/**
	 * Intersection routine for barycentric coordinates
	 */
		
	public Tuple3d triangleIntersectionUV(int facePos, Tuple3d origin,
			Tuple3d destination, boolean[] out) {
		
		
		Tuple3d dest = destination;
		Tuple3d orig = origin;
		orig.sub(dest);

		Tuple3d v1 = vertexGetTuple3f(heGetVertex(faceGetHalfEdge(facePos)));
		Tuple3d v2 = vertexGetTuple3f(heGetVertex(heGetNext(faceGetHalfEdge(facePos))));
		Tuple3d v3 = vertexGetTuple3f(heGetVertex(heGetPrev(faceGetHalfEdge(facePos))));

		Tuple3d edge1 = new Point3d();
		Tuple3d edge2 = new Point3d();

		edge1.sub(v2, v1);
		edge2.sub(v3, v1);

		Tuple3d pvec = new Point3d();
		pvec = IntersectionTests.CROSS(orig, edge2);

		double det;

		det = IntersectionTests.DOT(edge1, pvec);

		if (det > -EPSILON_D && det < EPSILON_D)// segment lies in plane
			return null;
		if (det < EPSILON_D)
			out[0] = true;
		else
			out[0] = false;
		double inv_det = 1.0d / det;

		// calculate distance from v1 to ray origin

		Tuple3d tvec = new Point3d();

		tvec.sub(dest, v1);

		// calculate U parameter and test bounds

		double u;

		u = (tvec.x * pvec.x + tvec.y * pvec.y + tvec.z * pvec.z) * inv_det;

		if (u < -EPSILON_D || u  > 1.0f+EPSILON_D)
			return null;

		Tuple3d qvec = new Point3d();

		qvec = IntersectionTests.CROSS(tvec, edge1);

		double v = IntersectionTests.DOT(orig, qvec) * inv_det;
		if (v < -EPSILON_D || (u + v) > 1.0f+EPSILON_D)
			return null;

		// calculate t, ray intersects triangle

		double t =1.0f - IntersectionTests.DOT(edge2, qvec) * inv_det;
		if ((t < -EPSILON_D) || (t > 1.0f+EPSILON_D)) {
			return null;
		}
		return new Point3d(u,v,t);
	}
	
	
	public int triangleTriangleIntersection(int trianglePos, Tuple3d tuple3d, Tuple3d tuple3d2, Tuple3d tuple3d3, Tuple3d isectpt1, Tuple3d isectpt2){
		return IntersectionTests.triangleTriangleIntersection( heGetVertexTuple3f( faceGetHe( trianglePos , 0 ) ) , 
				heGetVertexTuple3f( faceGetHe( trianglePos , 1 ) ) , 
				heGetVertexTuple3f( faceGetHe( trianglePos , 2 ) ), 
				tuple3d , tuple3d2 ,tuple3d3 , isectpt1, isectpt2);
	}
	
public void getObject(HalfEdgeStructCSG obj){
		
		Tuple3d vertice1;
		Tuple3d vertice2;
		Tuple3d vertice3;
		
		Tuple3f normal1;
		Tuple3f normal2;
		Tuple3f normal3;
		
		Tuple2f tex1;
		Tuple2f tex2;
		Tuple2f tex3;
		
		for (int i = 0 ; i < obj.halfEdges.size()/2; i+=3)
		{
			
			
			tex1 = new Point2f (obj.uv.get(i*2), obj.uv.get(i*2+1));
			normal1 = new Point3f (obj.normals.get(3*i),obj.normals.get(3*i+1),obj.normals.get(3*i+2));
			vertice1= new Point3d (obj.heGetVertexX(i),obj.heGetVertexY(i),obj.heGetVertexZ(i));
			
			tex2 = new Point2f (obj.uv.get((i+1)*2), obj.uv.get((i+1)*2+1));
			normal2 = new Point3f (obj.normals.get(3*(i+1)),obj.normals.get(3*(i+1)+1),obj.normals.get(3*i+2));
			vertice2= new Point3d (obj.heGetVertexX(i+1),obj.heGetVertexY(i+1),obj.heGetVertexZ(i+1));
			
			tex3 = new Point2f (obj.uv.get((i+2)*2), obj.uv.get((i+2)*2+1));
			normal3 = new Point3f (obj.normals.get(3*(i+2)),obj.normals.get(3*(i+2)+1),obj.normals.get(3*(i+2)+2));
			vertice3= new Point3d (obj.heGetVertexX(i+2),obj.heGetVertexY(i+2),obj.heGetVertexZ(i+2));
			
			this.addTriangle(vertice1, vertice2, vertice3, normal1, normal2, normal3, tex1, tex2, tex3, obj.getShader(obj.heGetFace(i)));
			
		}
		
	}

	/**
	 * clears all Arrays
	 */
	
	public void clear() {
		vertices = new DoubleList();
		halfEdges = new IntList();
		normals = new FloatList();
		uv = new FloatList();
		vertexToHalfEdge = new IntList();
		triangleToShader = new IntList();
		shader = new ObjectList();
		triangleStatus = new ByteList();
		heStatus = new ByteList();
	}

	/**
	 * 
	 */
	
	public byte getClassification(Tuple3d vert, Tuple3d normal, int hePos){
		//TODO
		Tuple3d b1 = getVertex(heGetVertex(heGetNext(hePos)));
		Tuple3d b2 = getVertex(heGetVertex(heGetNext(heGetTwin(hePos))));
		
		Tuple3d e1 = getVertex(heGetVertex(hePos));
		Tuple3d e2 = getVertex(heGetVertex(heGetTwin(hePos)));
		
		boolean debug=false;
		
		if(debug){
		System.out.println("vert ="+vert);
		
		System.out.println("e1   ="+e1);
		System.out.println("e2   ="+e2);
		
		System.out.println("b1   ="+b1);
		System.out.println("b2   ="+b2);
		}
		
		// Plane equation
		Tuple3d v11, v21, normal1;
		v11 = new Point3d();
		v21 = new Point3d();
		
//		Get Plane equation 1
		
		v11.sub(b1, e1);
		v21.sub(e2, e1);
		normal1=IntersectionTests.CROSS( v21,v11 );
		
		Tuple3d  v12, normal2;
		

//		Get Plane equation 2
		

		v12 = new Point3d();
		
		v12.sub(b2, e1);
		
		
		normal2=IntersectionTests.CROSS(v12, v21);
		
		Tuple3d x = new Point3d();
		x.set(b2);
		x.sub(e1);
		
		vert.sub(e1);
		
		//test vert lies on one of the two faces
		
		double face1 = IntersectionTests.DOT(vert,normal1);
		
		if(face1>-EPSILON_D && face1<EPSILON_D){
			//vert lies on face 1
			//compare the normals of face 1 and the face of the vert
			double compare = IntersectionTests.DOT(normal,normal1);
			if(compare < 0.0d){
				return OPPOSITE;
			} else{
				return SAME;
			}
		}
		
		double face2 = IntersectionTests.DOT(vert,normal2);
		
		if(face2>-EPSILON_D && face2<EPSILON_D){
			//vert lies on face 2
			//compare the normals of face 2 and the face of the vert
			double compare = IntersectionTests.DOT(normal,normal2);
			if(compare < 0.0d){
				return OPPOSITE;
			} else{
				return SAME;
			}
		}
		
		double out = IntersectionTests.DOT(x,normal1);
		
		
		
		double b1S = IntersectionTests.DOT(vert,normal2);
		
		x.set(b1);
		x.sub(e1);
		
		double b2S = IntersectionTests.DOT(vert,normal1);
		
		double test = IntersectionTests.DOT(normal1 , normal2);
		
//		Test the classification
		
		if (out < EPSILON_D){
			//convex
			if(debug){
			System.out.println("convex");
			System.out.println(b2S+" "+b1S);
			}
			if(b2S>=0 && b1S>=0) return INSIDE;
			else return OUTSIDE;
			
		}
		else {
			//not convex
			if(debug){
			System.out.println("not convex");
			}
			if(b2S>0 && b1S>0) return OUTSIDE;
			else return INSIDE;	
		}
		
		// TODO: if -EPSILON<out<EPSILON faces are coplanar means they have the same
		// plane equation
		// at the moment the not convex case handles these faces
		
		//convex?
		//if b2 lies on the negative side of plane1 they are convex
		
	}

	public void createLink(){
		link = new CSGLink(vertices);
	}
	
	public void addEdgeToLink(Tuple3d from, Tuple3d to){
		link.addEdge (from, to);
	}
	

	
	public void addExteriorToLink(){


		int face=0;
		IntList edges = new IntList();
		FloatList normalData = new FloatList();
		FloatList texData = new FloatList();
		clusters = new int[getFacesCount()];

		int vertexStatus[] = new int[getVerticesCount()];
		for (int i=0; i<clusters.length; i++){
			clusters[i]=-1;
		}
		for (int i=0; i<vertexStatus.length; i++){
			vertexStatus[i]=-1;
		}
		for (int i=0; i<getFacesCount(); i++)
		{
			if (clusters[i]==-1){
				Tuple3d v1 = faceGetVertexU(i);
				Tuple3d v2 = faceGetVertexV(i);
				Tuple3d v3 = faceGetVertexW(i);
				
				// TODO: switch to calculate normal
				
				//calculate Plane equation
				v2.sub (v1);
				v3.sub (v1);
				
				Tuple3d n = IntersectionTests.CROSS(v2,v3);
				n= faceCalculateNormal(i);
				
				clusters[i]=face;
				link.addClusterNormal (n.x, n.y, n.z);
				link.addShader(triangleToShader.get (i));
				
				int currentShader=triangleToShader.get (i);
				
				//classify all vertices
				for(int j=0; j<getVerticesCount(); j++){
					double d;
					Tuple3d v = getVertex(j);
					v.sub (v1);
					d=IntersectionTests.DOT(v,n);
					if(d<EPSILON_D && d>-EPSILON_D){
						vertexStatus[j]=i;
					}
				}
				// get all triangles with 3 Vertices on this plane
				for (int j=0; j<getFacesCount(); j++){
					if( vertexStatus[heGetVertex(faceGetHe(j,0))]==i 
							&& vertexStatus[heGetVertex(faceGetHe(j,1))]==i 
							&& vertexStatus[heGetVertex(faceGetHe(j,2))]==i 
							&& currentShader==triangleToShader.get (j))
					{
						if(IntersectionTests.DOT (n, faceCalculateNormal (j)) > 0.0f)
						{
						clusters[j] = face;
						for (int m = 0; m < 3; m++)
						{
							//add exterior half-edges to the edges list
							if (!heIsInterior (faceGetHe (j, m)))
							{
								int exteriorHe = faceGetHe (j, m);
								
								edges.add (heGetVertex (heGetTwin (exteriorHe)));
								edges.add (heGetVertex (exteriorHe));
								edges.add (face);
								normalData.push (heGetNormalX (exteriorHe), heGetNormalY (exteriorHe), heGetNormalZ (exteriorHe));
								texData.push ( heGetU (exteriorHe) , heGetV (exteriorHe) );
								
							}
						}
						} else{
						}
						
					}
				}
				face++;
			}

		}
		//add Edges to the Link
		link.addEdges (edges , normalData , texData);
	}
	
	//Returns the number of the cluster the triangle is located in
	
	public int getFaceCluster(int facePos){
		return clusters[facePos];
	}
	
	public void drawLink(GL gl){

		link.draw(gl);
	}
	

	public boolean addIntersectionLine (DoubleList intersectionLine,
			IntList intersectionClusters , FloatList normalData , FloatList uvData ){
		
		boolean debug = false;
		
//		int old = link.halfEdges.size ();
		link.addIntersectionLine (intersectionLine, intersectionClusters,normalData, uvData);
		// TODO Auto-generated method stub
//		if(old==link.halfEdges.size()){
//			if (debug)
//			{
//				System.out.println ("ABORTED");
//			}
//			return false;}
		int stop = link.clusterNormal.size()/3;
		
//		for (int i=0 ; i<stop ; i++){
//			link.createNewFace (i);
//		}
//		
//		stop = link.clusterNormal.size()/3;
		
		for (int i=0 ; i<stop ; i++){
			link.monotonateCluster (i);
		}
		stop = link.clusterNormal.size()/3;
		for (int i=0 ; i<stop ; i++){
			link.triangulate (i);
		}
		return true;
	}
	
	public void insertLink(){
		insertLink(this.link);
	}
	
	private void insertLink(CSGLink link){
		
		//remove the old halfedges
		// TODO: only Triangles from Intersection Clusters have to be removed
		halfEdges.clear ();
		triangleToShader.clear ();

		link.orderTriangles ();
		
		normals.clear ();
		normals.addAll (link.normals);
		normals.trimToSize ();
		
		uv.clear ();
		uv.addAll (link.uv);
		uv.trimToSize ();
		
		
		
		for (int i=0 ; i<link.heGetCount() ; i++){
			if(i%3==0){
//				System.out.println("Half edge "+i+" adding shader of Face "+link.heGetFace (i));
				triangleToShader.add(link.triangleToShader.get( link.heGetFace (i )));
			}
				halfEdges.push(link.heGetVertex (i));
				halfEdges.push(link.heGetTwin (i));
		}

//		System.out.println("WTF 2"+triangleToShader);
		
		halfEdges.trimToSize ();
		triangleToShader.trimToSize ();
//		for (int i=vertices.size ()/3 ; i<link.vertices.size ()/3 ; i++){
//			vertices.push (link.vertices.get (i*3));
//			vertices.push (link.vertices.get (i*3+1));
//			vertices.push (link.vertices.get (i*3+2));
//		}
//		vertices.clear ();
//		vertices.addAll(link.vertices);
		
		vertices.trimToSize ();
		
		vertexToHalfEdge.clear ();
		vertexToHalfEdge.addAll (link.vertexToHalfEdge);
		
		heStatus.clear ();
		heStatus.addAll (link.heStatus);
		
//		for(int i=vertexStatus.size (); i<vertices.size()/3 ; i++){
//			vertexStatus.push (BORDER);
//		}
		
		triangleStatus.setSize (getFacesCount ());
		
	}
	
	public int[] getNeigboringTriangles(int facePos){
		int[] out = new int[3];
		out[0] = heGetFace(heGetTwin(faceGetHe(facePos , 0)));
		out[1] = heGetFace(heGetTwin(faceGetHe(facePos , 1)));
		out[2] = heGetFace(heGetTwin(faceGetHe(facePos , 2)));
		return out;
	}
	
	public byte getNeigborhoodClassification(int facePos){
		
		boolean debug = false;
		
		triangleStatus.set (facePos, UPDATING);
		
		if(debug){System.out.println ("Face "+facePos);}
		
		int[] neigbors = getNeigboringTriangles(facePos);
		if(debug){
			System.out.println("Neigbors "+neigbors[0]+" "+neigbors[1]+" "+neigbors[2]);
		}
		
		if( heGetStatus( faceGetHe(facePos , 0) ) != BORDER){
			if(triangleStatus.get(neigbors[0]) == INSIDE || triangleStatus.get(neigbors[0]) == OUTSIDE){
				
				if(debug){
					System.out.println("N0");
					System.out.println ("receving Status from neigbor "+neigbors[0]+" "+triangleStatus.get(neigbors[0]));
				}
				triangleStatus.set (facePos, triangleStatus.get(neigbors[0]));
				return triangleStatus.get(neigbors[0]);
			}	
		}
		if( heGetStatus( faceGetHe(facePos , 1) ) != BORDER){
			if(triangleStatus.get(neigbors[1]) == INSIDE || triangleStatus.get(neigbors[1]) == OUTSIDE){
				if(debug){
					System.out.println("N1");
					System.out.println ("receving Status from neigbor "+neigbors[1]+" "+triangleStatus.get(neigbors[1]));
				}
				triangleStatus.set (facePos, triangleStatus.get(neigbors[1]));
				return triangleStatus.get(neigbors[1]);
			}	
		}
		if( heGetStatus( faceGetHe(facePos , 2) ) != BORDER){
			if(triangleStatus.get(neigbors[2]) == INSIDE || triangleStatus.get(neigbors[2]) == OUTSIDE){
				if(debug){
					System.out.println("N2");
					System.out.println ("receving Status from neigbor "+neigbors[2]+" "+triangleStatus.get(neigbors[2]));
				}
				triangleStatus.set (facePos, triangleStatus.get(neigbors[2]));
				return triangleStatus.get(neigbors[2]);
			}	
		}
			byte outStatus=UNDEFINED;
			
			if( heGetStatus( faceGetHe(facePos , 0) ) != BORDER){
				if(triangleStatus.get(neigbors[0]) != UPDATING){
					
					if(debug){System.out.println ("Following "+neigbors[0]);}
					outStatus=getNeigborhoodClassification(neigbors[0]);
					if(outStatus==INSIDE || outStatus==OUTSIDE){
						triangleStatus.set (facePos, outStatus);
						return outStatus;
					}
				}	
			}
			if( heGetStatus( faceGetHe(facePos , 1) ) != BORDER){
				if(triangleStatus.get(neigbors[1]) != UPDATING){
					
					if(debug){System.out.println ("Following "+neigbors[1]);}
					outStatus=getNeigborhoodClassification(neigbors[1]);
					if(outStatus==INSIDE || outStatus==OUTSIDE){
						triangleStatus.set (facePos, outStatus);
						return outStatus;
					}
				}	
			}
			if( heGetStatus( faceGetHe(facePos , 2) ) != BORDER){
				if(triangleStatus.get(neigbors[2]) != UPDATING){
					
					if(debug){System.out.println ("Following "+neigbors[2]);}
					outStatus=getNeigborhoodClassification(neigbors[2]);
					if(outStatus==INSIDE || outStatus==OUTSIDE){
						triangleStatus.set (facePos, outStatus);
						return outStatus;
					}
				}	
			}
			if(outStatus==UPDATING){outStatus=UNDEFINED;
			triangleStatus.set (facePos, UPDATING);
			}
			
			return outStatus;
			
	}
	
	public void getInsideTriangles (HalfEdgeStructCSG object)
	{
		for (int i=0 ; i<object.getHalfEdgesCount () ; i+=3){
			if( object.triangleStatus.get ( object.heGetFace(i) ) == INSIDE ){
				Tuple3d vertex1=object.heGetVertexTuple3f (i);
				Tuple3d vertex2=object.heGetVertexTuple3f (i+1);
				Tuple3d vertex3=object.heGetVertexTuple3f (i+2);
				
				Tuple3f normal1=object.heGetNormal(i);
				Tuple3f normal2=object.heGetNormal(i+1);
				Tuple3f normal3=object.heGetNormal(i+2);
				
				Tuple2f tex1=object.heGetUV (i);
				Tuple2f tex2=object.heGetUV (i+1);
				Tuple2f tex3=object.heGetUV (i+2);
				
				Shader s=object.getShader (object.heGetFace(i));
				
				this.addTriangle (vertex1, vertex2, vertex3, normal1, normal2, normal3, tex1, tex2, tex3, s);
				
				
			}
		}
		
	}

	public void getInsideReversedTriangles (HalfEdgeStructCSG object)
	{
		for (int i=0 ; i<object.getHalfEdgesCount () ; i+=3){
			if( object.triangleStatus.get ( object.heGetFace(i) ) == INSIDE ){
				
				Tuple3d vertex3=object.heGetVertexTuple3f (i);
				Tuple3d vertex2=object.heGetVertexTuple3f (i+1);
				Tuple3d vertex1=object.heGetVertexTuple3f (i+2);
				
				Tuple3f normal3=object.heGetNormal(i);
				normal3.negate ();
				Tuple3f normal2=object.heGetNormal(i+1);
				normal2.negate ();
				Tuple3f normal1=object.heGetNormal(i+2);
				normal1.negate ();
				
				Tuple2f tex3=object.heGetUV (i);
				Tuple2f tex2=object.heGetUV (i+1);
				Tuple2f tex1=object.heGetUV (i+2);
				
				Shader s=object.getShader (object.heGetFace(i));
				
				this.addTriangle (vertex1, vertex2, vertex3, normal1, normal2, normal3, tex1, tex2, tex3, s);
				
				
			}
			
		}
		
	}

	public void getOutsideTriangles (HalfEdgeStructCSG object)
	{
		for (int i=0 ; i<object.getHalfEdgesCount () ; i+=3){
			if( object.triangleStatus.get ( object.heGetFace(i) ) == OUTSIDE ){
				
				Tuple3d vertex1=object.heGetVertexTuple3f (i);
				Tuple3d vertex2=object.heGetVertexTuple3f (i+1);
				Tuple3d vertex3=object.heGetVertexTuple3f (i+2);
				
				Tuple3f normal1=object.heGetNormal(i);
				Tuple3f normal2=object.heGetNormal(i+1);
				Tuple3f normal3=object.heGetNormal(i+2);
				
				Tuple2f tex1=object.heGetUV (i);
				Tuple2f tex2=object.heGetUV (i+1);
				Tuple2f tex3=object.heGetUV (i+2);
				
				Shader s=object.getShader (object.heGetFace(i));
				
				this.addTriangle (vertex1, vertex2, vertex3, normal1, normal2, normal3, tex1, tex2, tex3, s);
				
				
			}
		}
		
	}
	
	public void switchHePos(int he1Pos , int he2Pos){
		//update references
		
	}
	
	public void updatePosition(int heOldPos , int heNewPos){
		//update the Twin
		heSetTwin(heGetTwin(heOldPos) , heNewPos);
		//update the prev
	}
	
	public byte heGetStatus(int hePos){
		return heStatus.get (hePos);
	}
	
	public void classifyFacesByRayCasting(HalfEdgeStructCSG object2){
		for (int i = 0 ; i < getFacesCount() ; i++){
			if( triangleStatus.get (i) == UNDEFINED || triangleStatus.get (i) == UPDATING){
				triangleStatus.set( i , classifyFaceByRayCasting( i , object2 ) );
			}
		}
		
	}
	
	public byte classifyFaceByRayCasting(int facePosObj1 , HalfEdgeStructCSG object2){
		
		boolean debug = false;
		
		Tuple3d barycentre = faceCalculateBarycentre(facePosObj1);
		Tuple3d normal = faceCalculateNormal(facePosObj1);
		
		double distance = Double.MAX_VALUE;
		double currentDistance;
		
		boolean backfacing=false;
		
		if(debug){
			System.out.println ("CLASSIFYING face "+facePosObj1+ " faces count "+object2.getFacesCount ());
			System.out.println(faceGetVertexW(facePosObj1));
			System.out.println(faceGetVertexU(facePosObj1));
			System.out.println(faceGetVertexV(facePosObj1));
			System.out.println("Barycentre at "+barycentre);
		}
		
		for (int i = 0; i < object2.getFacesCount (); i++)
		{
			if(debug){System.out.println("with Face "+i);}
			
			Tuple3d isectpt=new Point3d();
			
			int intersect = rayTriangleIntersect (barycentre, normal,
				i , object2, isectpt);

			//			currentDistance = rayTriangleIntersection(barycentre , normal , i , object2 , backfacing, intersect );

			//			if(debug){System.out.println("min distance "+distance+" new distance = "+currentDistance+" backfacing = "+backfacing[0]+ " did intersect "+intersect[0]);
			//			System.out.println ("face of object 2 "+i);}

			if (intersect == 1)
			{
				
				if(debug){
					System.out.println("intersection occured");
					System.out.println("at "+isectpt);
				}
				
//				currentDistance = IntersectionTests.distanceBetween2Points (
//					barycentre, object2.faceCalculateBarycentre (i));
				
				
				currentDistance = IntersectionTests.distanceBetween2Points (
					barycentre, isectpt);
				
				if (currentDistance < distance)
				{
					if(debug)System.out.println(" distance smaller"+currentDistance);
					
					
					distance = currentDistance;
					
					backfacing = ( IntersectionTests.DOT(normal , object2.faceCalculateNormal ( i )) > 0 );
					
					if (distance < EPSILON_D)
					{
						if (backfacing)
						{
							return SAME;
						}
						else
						{
							return OPPOSITE;
						}
					}

				} else {
					if (debug){System.out.println ("distance larger "+currentDistance);}
				}
			}

		}
		
		
		if(backfacing){
			return INSIDE;
		}
		else{
			return OUTSIDE;
		}
		
	}
	
	private int rayTriangleIntersect (Tuple3d origin, Tuple3d direction,
			int facePos, HalfEdgeStructCSG object, Tuple3d out)
	{
		return IntersectionTests.rayTriangleIntersect (origin, direction, object.faceGetVertexW (facePos), object.faceGetVertexU (facePos), object.faceGetVertexV (facePos), out);
	}

	private Tuple3d faceCalculateNormalNormalized (int facePos)
	{
		Tuple3d normal = faceCalculateNormal(facePos);
		
		double length = Math.sqrt ( ( normal.x*normal.x+normal.y*normal.y+normal.z*normal.z ) );
		
		normal.scale (1.0f/length);
		
		return normal;
	}

	private double rayTriangleIntersection(Tuple3d origin , Tuple3d dest ,int facePosObj2 , 
			HalfEdgeStructCSG object2, boolean[] backfacing, boolean[] intersect){
		
		return IntersectionTests.rayTriangleIntersection(origin,
			dest, object2.faceGetVertexW(facePosObj2) , object2.faceGetVertexU(facePosObj2), 
			object2.faceGetVertexV(facePosObj2),  backfacing, intersect);
	}
	
	public CSGLink getLink(){
		return link;
	}
	
	public Tuple2f computeUV(int facePos, Tuple3d point){
		
//		System.out.println("computing u and v of "+facePos+" and point "+point);
		
		Tuple3d a = faceGetVertexW (facePos);
		Tuple3d b = faceGetVertexU (facePos);
		Tuple3d c = faceGetVertexV (facePos);
		
//		Tuple3f d = IntersectionTests.SUB (b, a);
//		Tuple3f e = IntersectionTests.SUB (c, a);
//		Tuple3f z = IntersectionTests.SUB (point, a);
//		
//		float v = (z.y-z.x*d.x) / ( d.x*e.y - e.x*d.y );
//		float u = (z.x- v*e.x) / d.x; 
		
		Tuple3d v0 = new Point3d();
		v0.sub (faceGetVertexV (facePos) , faceGetVertexW (facePos));
		Tuple3d v1 = new Point3d();
		v1.sub (faceGetVertexU (facePos) , faceGetVertexW (facePos));
		Tuple3d v2 = new Point3d();
		v2.sub (point , faceGetVertexW (facePos));
		
//		System.out.println ("vertex W="+faceGetVertexW (facePos));
//		System.out.println ("vertex U="+faceGetVertexU (facePos));
//		System.out.println ("vertex V="+faceGetVertexV (facePos));
		
		double dot00 = IntersectionTests.DOT(v0 , v0);
		double dot01 = IntersectionTests.DOT(v1 , v0);
		double dot02 = IntersectionTests.DOT(v2 , v0);
		double dot11 = IntersectionTests.DOT(v1 , v1);
		double dot12 = IntersectionTests.DOT(v2 , v1);
		
		double invDenom = 1 / (dot00*dot11 - dot01*dot01);
		double v = (dot11 * dot02 - dot01 * dot12)*invDenom;
		double u = (dot00 * dot12 - dot01 * dot02)*invDenom;
		
//		System.out.println("U ="+u+" V="+v);
//		
//		if( (u>=1) || (v>=1) || (u+v>=1) ){
//			System.out.println("UV komisch "+u+" "+v);
//		}
		if(u<0.0f){u=0.0d;}
		if(v<0.0f){v=0.0d;}
		
//		System.out.println ("Reverse Calculation");
//		
//		float w = 1.0f - u - v;
//		
//		a.scale (w);
//		b.scale (u);
//		c.scale (v);
//		
//		a.add(b);
//		a.add(c);
//		System.out.println(a);
//		a.sub(point);
//		System.out.println("Error ="+a);
		
		Tuple2d out = new Point2d();
		out.set (u, v);
		
		Tuple2f out_f = new Point2f();
		out_f.set (out);
		
		return out_f;
	}
	
	public boolean addIntersectionLine2 (DoubleList intersectionLine,
			IntList intersectionClusters , FloatList normalData , FloatList uvData ){
		
		
		boolean debug = false;
		
		int old = link.halfEdges.size ();
		link.addIntersectionLine (intersectionLine, intersectionClusters,normalData, uvData);
		// TODO Auto-generated method stub
//		if(old==link.halfEdges.size()){
//			if (debug)
//			{
//				System.out.println ("ABORTED");
//			}
//			return false;}

		int stop = link.clusterNormal.size()/3;
		
//		for (int i=0 ; i<stop ; i++){
//			link.createNewFace (i);
//		}
//		
		stop = link.clusterNormal.size()/3;
		
		System.out.println ("Stop = "+stop);
		
//		link.monotonateCluster (0);
		
//		System.out.println ("Monotonate Cluster debug 0");
//		link.monotonate2 (0);
//		System.out.println ("Done: Monotonate Cluster debug 0");
		
//		for (int i=0 ; i<stop ; i++){
//			System.out.println ("Monotonate Cluster debug "+i);
//			link.monotonate2 (i);	
//		}
		
//		System.out.println(link);
		
//		int oldStop=stop;
		stop = link.clusterNormal.size()/3;
////		
//		System.out.println ("trinagulating cluster 7");
//		link.triangulate (7);
//		
//		for (int i=0 ; i<stop ; i++){
//			System.out.println ("Triangulate Cluster debug "+i);
//			link.triangulate (i);
//		}
		return true;
	}
	
	public boolean liesOnExteriorEdge(int facePos , Tuple2f uv){
		
		if(uv.x-EPSILON_D<0.0f && uv.x+EPSILON_D>0.0f){
			// u=0 vertex lies on the Edge between W and V
			if (!heIsInterior ( faceGetHe (facePos, 0) ) ){
				return true;
			}
			
		} else if(uv.y-EPSILON_D<0.0f && uv.y+EPSILON_D>0.0f){
			// v=0 vertex lies on the Edge between W and U
			if (!heIsInterior ( faceGetHe (facePos, 1) ) ){
				return true;
			}
			
		} else if(uv.x+uv.y-EPSILON_D<1.0f && uv.x+uv.y+EPSILON_D>1.0f){
			//w=0 vertex lies on the Edge between U and V
			if (!heIsInterior ( faceGetHe (facePos, 2) ) ){
				return true;
			}
			
		}
		
		return false;
	}

	public void getOppositeTriangles (HalfEdgeStructCSG object)
	{
		for (int i=0 ; i<object.getHalfEdgesCount () ; i+=3){
			if( object.triangleStatus.get ( object.heGetFace(i) ) == OPPOSITE ){
				Tuple3d vertex1=object.heGetVertexTuple3f (i);
				Tuple3d vertex2=object.heGetVertexTuple3f (i+1);
				Tuple3d vertex3=object.heGetVertexTuple3f (i+2);
				
				Tuple3f normal1=object.heGetNormal(i);
				Tuple3f normal2=object.heGetNormal(i+1);
				Tuple3f normal3=object.heGetNormal(i+2);
				
				Tuple2f tex1=object.heGetUV (i);
				Tuple2f tex2=object.heGetUV (i+1);
				Tuple2f tex3=object.heGetUV (i+2);
				
				Shader s=object.getShader (object.heGetFace(i));
				
				this.addTriangle (vertex1, vertex2, vertex3, normal1, normal2, normal3, tex1, tex2, tex3, s);
				
				
			}
		}
		
	}
	
	public void getSameTriangles (HalfEdgeStructCSG object)
	{
		for (int i=0 ; i<object.getHalfEdgesCount () ; i+=3){
			if( object.triangleStatus.get ( object.heGetFace(i) ) == SAME ){
				Tuple3d vertex1=object.heGetVertexTuple3f (i);
				Tuple3d vertex2=object.heGetVertexTuple3f (i+1);
				Tuple3d vertex3=object.heGetVertexTuple3f (i+2);
				
				Tuple3f normal1=object.heGetNormal(i);
				Tuple3f normal2=object.heGetNormal(i+1);
				Tuple3f normal3=object.heGetNormal(i+2);
				
				Tuple2f tex1=object.heGetUV (i);
				Tuple2f tex2=object.heGetUV (i+1);
				Tuple2f tex3=object.heGetUV (i+2);
				
				Shader s=object.getShader (object.heGetFace(i));
				
				this.addTriangle (vertex1, vertex2, vertex3, normal1, normal2, normal3, tex1, tex2, tex3, s);
				
				
			}
		}
		
	}
	
}