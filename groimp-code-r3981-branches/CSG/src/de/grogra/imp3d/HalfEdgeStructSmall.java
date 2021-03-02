package de.grogra.imp3d;

import javax.media.opengl.GL;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

import de.grogra.imp3d.shading.Shader;
import de.grogra.xl.util.FloatList;
import de.grogra.xl.util.DoubleList;
import de.grogra.xl.util.IntList;
import de.grogra.xl.util.ObjectList;

import de.grogra.imp3d.IntersectionTests;

public class HalfEdgeStructSmall
{
	
	/**
	 * the epsilon value used by the intersection testing routine
	 */
	
	final static double EPSILON_D = 0.00001d;
	final static double EPSILON_D_VERT = 0.000001d;
	
	final static float EPSILON_F = 0.00001f;
	
	/**
	 * List of shaders used in the csg object
	 * the number stands for the position in
	 * the shader Array
	 */
	
	IntList triangleToShader;
	/**
	 * List of all shaders used by this object
	 */
	
	ObjectList shader;
	/**
	 * [n][3] n Vertices (n-1)*3 = x coordinate of the n-th vertex (n-1)*3 + 1 =
	 * y coordinate of the n-th vertex (n-1)*3 + 2 = z coordinate of the n-th
	 * vertex
	 */
	DoubleList vertices;
	/**
	 * n-th element is the position of one HalfEdge the Vertex correspond to the
	 * correspondiong HalfEdge points to this Vertex
	 */
	
	IntList vertexToHalfEdge;
	/**
	 * An half edge points to its vertex position.
	 * 
	 * (m-1)*3 = pointer to the vertex of the m-th HalfEdge 
	 * (m-1)*3 + 1 = pointer to twin of the m-th HalfEdge
	 * 
	 * 
	 * previous halfedge is at e+2 if m%3==0 else at e-1 
	 * next halfedge is at e-2 if m%3==2 else at e+1
	 * 
	 * 
	 * 
	 * Faces (n-1)*6
	 */
	
	IntList halfEdges;
	/**
	 * (n-1)*3 = x coordinate of the n-th normal 
	 * (n-1)*3 + 1 = y coordinate of the n-th normal 
	 * (n-1)*3 + 2 = z coordinate of the n-th normal 
	 * 
	 */
	
	FloatList normals;
	/**
	 * (n-1)*2 = u coordinate (n-1)*2 + 1 = v coordinate
	 */
	
	FloatList uv;
	/**
	 * 
	 */
	
	public void addShader(Shader s){
		this.shader.add(s);
	}
	/**
	 * 
	 */
	
	public int getFacesCount() {
		return halfEdges.size / 6;
	}
	/**
	 * 
	 */
	
	public int getHalfEdgesCount() {
		return halfEdges.size / 2;
	}
	/**
	 * 
	 */
	
	public Tuple3d getHalfEdgeDestination(int halfEdgePos) {
		return vertexGetTuple3f(heGetVertex(halfEdgePos));
	}
	/**
	 * 
	 */
	
	public Tuple3d getHalfEdgeOrigin(int halfEdgePos) {
		return vertexGetTuple3f(heGetVertex(heGetTwin(halfEdgePos)));
	}
	/**
	 * 
	 */
	
	int vertexGetHalfEdge(int vertexPos) {
		return vertexToHalfEdge.get(vertexPos);
	}
	
	//TODO: replace with getVertex()
	Tuple3d vertexGetTuple3f(int vertexPos) {
		return new Point3d(vertices.get(vertexPos * 3),
				vertices.get(vertexPos * 3 + 1),
				vertices.get(vertexPos * 3 + 2));
	}
	/*
	 * Vertex Getter
	 */
	
	/**
	 * 
	 */
	
	int getVerticesCount() {
		return vertices.size() / 3;
	}
	/**
	 * returns the coordinates of a given vertex as a Tuple3f
	 */
	
	Tuple3d getVertex(int vertexPos) {
		return new Point3d(vertexGetVertexX(vertexPos),
				vertexGetVertexY(vertexPos), vertexGetVertexZ(vertexPos));
	}
	/**
	 * sets the values of a given Tuple3f as the values of a given Vertex in the vertices array
	 */
	
	void setVertex(int vertpos, Tuple3d vert) {
		vertices.set(vertpos * 3, vert.x);
		vertices.set(vertpos * 3 + 1, vert.y);
		vertices.set(vertpos * 3 + 2, vert.z);
	}
	/**
	 * 
	 */
	
	double vertexGetVertexX(int vertexPos) {
		return vertices.get(vertexPos * 3);
	}
	/**
	 * 
	 */
	
	double vertexGetVertexY(int vertexPos) {
		return vertices.get(vertexPos * 3 + 1);
	}
	/**
	 * 
	 */
	
	double vertexGetVertexZ(int vertexPos) {
		return vertices.get(vertexPos * 3 + 2);
	}
	/*
	 * Vertex Getter
	 */
	
	/**
	 * 
	 */
	
	int heGetPrev(int halfEdgePos) {
		return (halfEdgePos % 3 == 0) ? halfEdgePos + 2 : halfEdgePos - 1;
	}
	/**
	 * 
	 */
	
	int heGetNext(int halfEdgePos) {
		return (halfEdgePos % 3 == 2) ? halfEdgePos - 2 : halfEdgePos + 1;
	}
	int heGetTwin(int halfEdgePos) {
		return halfEdges.get(halfEdgePos * 2 + 1);
	}
	Tuple3d heGetVertexTuple3f(int halfEdgePos) {
		return new Point3d(heGetVertexX(halfEdgePos), heGetVertexY(halfEdgePos),
				heGetVertexZ(halfEdgePos));
	}
	/**
	 * 
	 */
	
	int heGetVertex(int halfEdgePos) {
		if(halfEdgePos<halfEdges.size()/2 ){
		return halfEdges.get(2 * halfEdgePos);}
		else{
			return -1;
		}
	}
	double heGetVertexX(int halfEdgePos) {
		return vertices.get(3 * (halfEdges.get(2 * halfEdgePos)));
	}
	/**
	 * 
	 */
	
	double heGetVertexY(int halfEdgePos) {
		return vertices.get(3 * (halfEdges.get(2 * halfEdgePos)) + 1);
	}
	/**
	 * 
	 */
	
	double heGetVertexZ(int halfEdgePos) {
		return vertices.get(3 * (halfEdges.get(2 * halfEdgePos)) + 2);
	}
	/**
	 * 
	 */
	
	Tuple2f heGetUV(int halfEdgePos) {
		return new Point2f(uv.get(halfEdgePos * 2), uv.get(halfEdgePos * 2 + 1) );
	}
	/**
	 * 
	 */
	
	float heGetU(int halfEdgePos) {
		return uv.get(halfEdgePos * 2);
	}
	/**
	 * 
	 */
	
	float heGetV(int halfEdgePos) {
		return uv.get(halfEdgePos * 2 + 1);
	}
	/**
	 * 
	 */
	
	Tuple3f heGetNormal(int halfEdgePos) {
		return new Point3f(normals.get(halfEdgePos * 3), normals.get(halfEdgePos * 3 + 1),
				normals.get(halfEdgePos * 3 + 2));
	}
	/**
	 * 
	 */
	
	float heGetNormalX(int halfEdgePos) {
		return normals.get(halfEdgePos * 3);
	}
	/**
	 * 
	 */
	
	float heGetNormalY(int halfEdgePos) {
		return normals.get(halfEdgePos * 3 + 1);
	}
	/**
	 * 
	 */
	
	float heGetNormalZ(int halfEdgePos) {
		return normals.get(halfEdgePos * 3 + 2);
	}
	/*
	 * HalfEdge setter
	 */
	
	/**
	 * 
	 */
	
	int heSetVertex(int halfEdgePos, int vertexPos) {
		return halfEdges.set(2 * halfEdgePos, vertexPos);
	}
	/**
	 * 
	 */
	
	void heSetUV(int halfEdgePos, Tuple2f uv) {
		this.uv.set(halfEdgePos*2, uv.x);
		this.uv.set(halfEdgePos*2 + 1, uv.y);
	}
	/**
	 * 
	 */
	
	void heSetNormal(int halfEdgePos, Tuple3f normal) {
		this.normals.set(halfEdgePos*3, normal.x);
		this.normals.set(halfEdgePos*3 + 1, normal.y);
		this.normals.set(halfEdgePos*3 + 2, normal.z);
	}
	/**
	 * 
	 */
	
	int heSetTwin(int halfEdgePos, int twinPos) {
		return halfEdges.set(halfEdgePos * 2 + 1, twinPos);
	}
	// Circulators
	
	/**
	 * 
	 */
	
	int heGetNextCW(int hePos) {
		return heGetNext(heGetTwin(hePos));
	}
	/**
	 * 
	 */
	
	int heGetFace(int hePos) {
		return hePos/3;
	}
	/**
		 * Splits the triangle by an given point in 3 Triangles.
		 * 2 new Triangles are created in this step.
		 * The first one is altered.
		 * 
		 * 
		 * @param FacePos the position of the triangle which should be split
		 * @param point the point given as a Tuple3f
		 */
		
		//TODO: Version for baricentric coordinates
		
	//	public void faceSplitAtPoint(int FacePos, Tuple3f point) {
	//
	//		int newVertex = insertVertex(point);
	//		if (newVertex < 0) {
	//			status.set((newVertex + 1) * (-1), BORDER);
	//			return;
	//		}
	//		status.set(newVertex, BORDER);
	//		// interpolating normal and uv
	//		int v = heGetVertex(faceGetHalfEdge(FacePos));
	//		Tuple3f newNormal = new Point3f(vertexGetVertexX(v),
	//				vertexGetVertexY(v), vertexGetVertexZ(v));
	//		Tuple2f newUV = new Point2f(1, 1);
	//		int he1 = faceGetHalfEdge(FacePos);
	//		int he2 = heGetNext(he1);
	//		int he3 = heGetNext(he2);
	//		int vertex1 = heGetVertex(he1);
	//		int vertex2 = heGetVertex(he2);
	//		int vertex3 = heGetVertex(he3);
	//		Tuple2f tex1 = new Point2f(heGetU(he1), heGetV(he1));
	//		Tuple2f tex2 = new Point2f(heGetU(he2), heGetV(he2));
	//		Tuple2f tex3 = new Point2f(heGetU(he3), heGetV(he3));
	//		Tuple3f normal1 = new Point3f(heGetNormalX(he1), heGetNormalY(he1),
	//				heGetNormalZ(he1));
	//		Tuple3f normal2 = new Point3f(heGetNormalX(he2), heGetNormalY(he2),
	//				heGetNormalZ(he2));
	//		Tuple3f normal3 = new Point3f(heGetNormalX(he3), heGetNormalY(he3),
	//				heGetNormalZ(he3));
	//		int oldCount = halfEdges.size / 2;
	//		int twin1 = heGetTwin(he1);
	//		int twin2 = heGetTwin(he2);
	//		int twin3 = heGetTwin(he3);
	//
	//		// Add first Triangle from v1-v2-vn
	//
	//		addTriangle(vertex1, he2, tex1, normal1, vertex2, twin2, tex2, normal2,
	//				newVertex, oldCount + 3, newUV, newNormal);
	//
	//		// Add second Triangle from v2-v3-vn
	//
	//		addTriangle(vertex2, oldCount + 2, tex2, normal2, vertex3, twin3, tex3,
	//				normal3, newVertex, he3, newUV, newNormal);
	//
	//		// alter the data of the Triangle already in List to v3-v1-vn
	//
	//		heSetVertex(he2, newVertex);
	//		heSetTwin(twin3, oldCount + 4);
	//		heSetTwin(twin2, oldCount + 1);
	//		heSetTwin(he2, oldCount);
	//		heSetTwin(he3, oldCount + 5);
	//		uv.set((he2 + 1) * 2, newUV.x);
	//		uv.set((he2 + 1) * 2 + 1, newUV.y);
	//		normals.set((he2 + 1) * 3, newNormal.x);
	//		normals.set((he2 + 1) * 3 + 1, newNormal.y);
	//		normals.set((he2 + 1) * 3 + 2, newNormal.z);
	//
	//		// Add entry to HalfEdgeToVertex Array
	//
	//		vertexToHalfEdge.add(heGetNext(he2));
	//
	//		// Update entries if they have changed
	//		// TODO
	//		if (vertexToHalfEdge.get(vertex2) == he2) {
	//			vertexToHalfEdge.set(vertex2, oldCount + 1);
	//		}
	//		if (vertexToHalfEdge.get(vertex3) == he3) {
	//			vertexToHalfEdge.set(vertex3, oldCount + 4);
	//		}
	//
	//	}
		
		
		
		public Tuple3d faceCalculateNormal(int facePos){
	
			Tuple3d v1 = heGetVertexTuple3f( faceGetHalfEdge(facePos) );
			Tuple3d v2 = heGetVertexTuple3f( heGetNext(faceGetHalfEdge(facePos) ) );
			Tuple3d v3 = heGetVertexTuple3f( heGetPrev(faceGetHalfEdge(facePos) ) );
			
			//get smallest and biggest component
			Tuple3d dv1v2 = new Point3d();
			Tuple3d dv2v3 = new Point3d();
			Tuple3d dv3v1 = new Point3d();
			
			dv1v2.sub (v1,v2);
			dv2v3.sub (v2,v3);
			dv3v1.sub (v3,v1);
			
			double fdv1v2 = IntersectionTests.DOT (dv1v2, dv1v2);
			double fdv2v3 = IntersectionTests.DOT (dv2v3, dv2v3);
			double fdv3v1 = IntersectionTests.DOT (dv3v1, dv3v1);
			
			Tuple3d c1 = new Point3d();
			Tuple3d c2 = new Point3d();
			
			c1.sub (v2,v1);
			c2.sub (v3,v1);
			
			
//			if(fdv1v2 < fdv2v3){
//				if(fdv1v2 < fdv3v1){
//					// fdv1v2 is the smallest
//					c1.sub (v2,v1);
//					c2.sub (v3,v1);
//				}
//				else {
//					// fdv3v1 is the smallest
//					c1.sub (v1,v3);
//					c2.sub (v2,v3);
//				}
//			}
//			else {
//				if(fdv2v3 < fdv3v1){
//					// fdv2v3 is the smallest
//					c1.sub (v3,v2);
//					c2.sub (v1,v2);
//				}
//				else {
//					// fdv3v1 is the smallest
//					c1.sub (v1,v3);
//					c2.sub (v2,v3);
//				}
//			}
			
			v1 = IntersectionTests.CROSS(c1,c2);
			
			double length = IntersectionTests.DOT (v1, v1);
			length = Math.sqrt ( length );
			
			v1.scale (1.0f/length);
			
			return v1;
			
		}
	/**
	 * @param facePos
	 * @param uv
	 * @return
	 */
	public int faceGetHe(int facePos, int hePos){
	
		return ( facePos*3 ) + ( hePos%3 );
		
	}
	//	public byte vertexGetStatus(int vertexPos){
	//		return vertexStatus.get(vertexPos);
	//	}
		
		public int faceGetVertexWPos (int facePos){
			return heGetVertex( faceGetHe(facePos , 0) );
		}
	public int faceGetVertexUPos (int facePos){
		return heGetVertex( faceGetHe(facePos , 1) );
	}
	public int faceGetVertexVPos (int facePos){
		return heGetVertex( faceGetHe(facePos , 2) );
	}
	public Tuple3d faceGetVertexW(int facePos){
	
		return heGetVertexTuple3f ( faceGetHe(facePos , 0) );	
	}
	public Tuple3d faceGetVertexU(int facePos){
		return heGetVertexTuple3f ( faceGetHe(facePos , 1) );	
	}
	public Tuple3d faceGetVertexV(int facePos){
	
		return heGetVertexTuple3f ( faceGetHe(facePos , 2) );	
	}
	/**
	 * @param facePos
	 * @param uv
	 * @return
	 */
	public Tuple3d faceGetVertexUV(int facePos, Tuple2d uv){
	
		Tuple3d v1 = heGetVertexTuple3f( faceGetHalfEdge(facePos) );
		Tuple3d v2 = heGetVertexTuple3f( heGetNext(faceGetHalfEdge(facePos) ) );
		Tuple3d v3 = heGetVertexTuple3f( heGetPrev(faceGetHalfEdge(facePos) ) );
		
		v2.sub(v1);
		v3.sub(v1);
		
		v2.scale(uv.x);
		v3.scale(uv.y);
		
		v1.add(v2);
		v1.add(v3);
		
		return v1;
		
	}
	/**
	 * returns shader x
	 */
	
	public Shader getShader(int facePos) {
		return (Shader) shader.get(triangleToShader.get(facePos));
	}
	/**
	 * 
	 */
	
	public void setShader(Shader s) {
		this.shader.add(s);
	}
	/**
	 * 
	 */
	
	public void setShaderPrimitive(Shader s) {
		this.shader.add(s);
		for(int i=0; i< this.getFacesCount(); i++)
		this.triangleToShader.add(0);
	}
	/**
	 * 
	 */
	
	public int getShaderCount(){
		return shader.size;
	}
	/**
	 * 
	 */
	
	public Shader getShaderPos(int shaderpos) {
		return (Shader) shader.get(shaderpos);
	}
	/**
	 * draws only the parts which uses the given shader
	 */
	
	public void draw(GL gl, int shader) {
		
		for (int i = 0; i < halfEdges.size() / 6; i++) {
			if(triangleToShader.get(i)==shader){
			gl.glBegin(GL.GL_TRIANGLES);
			
			gl.glNormal3f(normals.get(9 * i), normals.get(9 * i + 1),
					normals.get(9 * i + 2));
			gl.glTexCoord2f(uv.get(i * 6), uv.get(i * 6 + 1));
			gl.glVertex3d(heGetVertexX(i*3), heGetVertexY(i*3), heGetVertexZ(i*3));
			
			gl.glNormal3f(normals.get(9 * i+3), normals.get(9 * i + 4),
					normals.get(9 * i + 5));
			gl.glTexCoord2f(uv.get(i * 6+2), uv.get(i * 6 + 3));
			gl.glVertex3d(heGetVertexX((i*3)+1), heGetVertexY((i*3)+1), heGetVertexZ((i*3)+1));
			
			gl.glNormal3f(normals.get(9 * i+6), normals.get(9 * i + 7),
					normals.get(9 * i + 8));
			gl.glTexCoord2f(uv.get(i * 6+4), uv.get(i * 6 + 5));
			gl.glVertex3d(heGetVertexX((i*3)+2), heGetVertexY((i*3)+2), heGetVertexZ((i*3)+2));
			}
		}
		gl.glEnd();
	}
	/**
		 * for debugging purpose
		 */
		
		public void drawNormals(GL gl) {
			gl.glLineWidth(1.5f);
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glDisable(GL.GL_LIGHTING);
			gl.glBegin(GL.GL_LINES);
	
			for (int i = 0; i < halfEdges.size() / 2; i += 3) {
				
				gl.glColor3f(1f, 1f, 0f);
				
				Tuple3d v1= heGetVertexTuple3f(i);
				Tuple3d v2 = heGetVertexTuple3f(i+1);
				Tuple3d v3 = heGetVertexTuple3f(i+2);
				
				Tuple3f v1f=new Point3f();
				v1f.set (v1);
				Tuple3f v2f=new Point3f();
				v2f.set (v1);
				Tuple3f v3f=new Point3f();
				v3f.set (v3);
				
				
				Tuple3f n1 = heGetNormal(i);
				Tuple3f n2 = heGetNormal(i+1);
				Tuple3f n3 = heGetNormal(i+2);
				
				n1.scale(0.1f);
				n2.scale(0.1f);
				n3.scale(0.1f);
				
				n1.add(v1f);
				n2.add(v2f);
				n3.add(v3f);
				
	//			switch (vertexStatus.get(heGetVertex(i)))
	//			{
	//			case UNDEFINED:
	//				gl.glColor3f(1f, 0f, 0f);
	//				break;
	//			case BORDER:
	//				gl.glColor3f(0f, 1f, 0f);
	//				break;
	//			case INSIDE:
	//				gl.glColor3f(0f, 1f, 1f);
	//				break;
	//			case OUTSIDE:
	//				gl.glColor3f(1f, 1f, 1f);
	//				break;
	//			
	//			}
				
				gl.glVertex3f((float)v1.x,(float)v1.y,(float)v1.z);
				gl.glVertex3f(n1.x,n1.y,n1.z);
				
	//			switch (vertexStatus.get(heGetVertex(i+1)))
	//			{
	//			case UNDEFINED:
	//				gl.glColor3f(1f, 0f, 0f);
	//				break;
	//			case BORDER:
	//				gl.glColor3f(0f, 1f, 0f);
	//				break;
	//			case INSIDE:
	//				gl.glColor3f(0f, 1f, 1f);
	//				break;
	//			case OUTSIDE:
	//				gl.glColor3f(1f, 1f, 1f);
	//				break;
	//			}
				
				gl.glVertex3f((float)v2.x,(float)v2.y,(float)v2.z);
				gl.glVertex3f(n2.x,n2.y,n2.z);
				
	//			switch (vertexStatus.get(heGetVertex(i+2)))
	//			{
	//			case UNDEFINED:
	//				gl.glColor3f(1f, 0f, 0f);
	//				break;
	//			case BORDER:
	//				gl.glColor3f(0f, 1f, 0f);
	//				break;
	//			case INSIDE:
	//				gl.glColor3f(0f, 1f, 1f);
	//				break;
	//			case OUTSIDE:
	//				gl.glColor3f(1f, 1f, 1f);
	//				break;
	//			}
				
				gl.glVertex3f((float)v3.x,(float)v3.y,(float)v3.z);
				gl.glVertex3f(n3.x,n3.y,n3.z);
	
			}
			gl.glEnd();
			gl.glLineWidth(1.0f);
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glEnable(GL.GL_LIGHTING);
		}
		
		public void drawFaceNormals(GL gl) {
			gl.glLineWidth(1.5f);
			gl.glDisable(GL.GL_DEPTH_TEST);
			gl.glDisable(GL.GL_LIGHTING);
			gl.glBegin(GL.GL_LINES);
	
			for (int i = 0; i < getFacesCount (); i ++) {
				
				gl.glColor3f(1f, 1f, 1f);
				
				
				
				Tuple3d normal = faceCalculateNormal(i);
				Tuple3d bary = faceCalculateBarycentre2(i);
				
				normal.add (bary);
				
				gl.glVertex3d(normal.x,normal.y,normal.z);
				gl.glVertex3d(bary.x, bary.y, bary.z);
				
	
	
			}
			gl.glEnd();
			gl.glLineWidth(1.0f);
			gl.glEnable(GL.GL_DEPTH_TEST);
			gl.glEnable(GL.GL_LIGHTING);
		}
	/**
	 * usefull for debugging
	 */
	
	public void drawTwins(GL gl) {
		System.out.println ("Draw the Twins");
		
		gl.glLineWidth(1.5f);
		gl.glDisable(GL.GL_LIGHTING);
		gl.glDisable(GL.GL_DEPTH_TEST);
		for (int i = 0; i < halfEdges.size() / 2; i++) {
			gl.glBegin(GL.GL_LINES);
			gl.glColor3f(0f, 0f, 1f);
			if (heGetTwin(i) > i) {
				gl.glLineWidth(2.0f);
				gl.glColor3f(0f, 1f, 0f);
			}
			if (heGetTwin(i) < i) {
				gl.glLineWidth(2.0f);
				gl.glColor3f(0f, 0f, 1f);
			}
			if (heGetTwin(i) > i) {
				gl.glVertex3d(heGetVertexX(i), heGetVertexY(i), heGetVertexZ(i));
				gl.glVertex3d(heGetVertexX(heGetTwin(i)),
						heGetVertexY(heGetTwin(i)), heGetVertexZ(heGetTwin(i)));
			}
			gl.glColor3f(0f, 0f, 0f);
			gl.glEnd();
		}
		gl.glLineWidth(1.0f);
	
		gl.glEnable(GL.GL_LIGHTING);
		gl.glEnable(GL.GL_DEPTH_TEST);
	
	}
	/**
	 * 
	 */
	
	public int getVertexPos(Tuple3d vertex){
		for (int i = 0 ; i<this.vertices.size()/3; i++)
		{
			if (vertex.epsilonEquals(vertexGetTuple3f(i), EPSILON_D_VERT)){
				return i;
			}
		}
		return -1;
	}
	/**
	 * 
	 */
	
	public int getNeighbourCount(int vertexpos){
		int i = 1;
		int start = vertexToHalfEdge.get(vertexpos);
		int j=start;
		j= heGetNext( heGetTwin(j) );
		while (j!=start && i<512){
			i++;
			j= heGetNext( heGetTwin(j) );
		}
		return i;
	}
	/**
		 * 
		 */
		
		public int getHeFT(int fromVert, int toVert)
		{
			if(fromVert==-1 || toVert==-1) return -1;
			
	//		int heStart = vertexToHalfEdge.get(fromVert);
			
			
			//TODO: replace with other method
//			int heStart = vertexGetOHe(fromVert);
//			int n = getNeighbourCount(fromVert);
			int outgoingHalfEdges [] = vertexGetOHeArray (fromVert);
			int n = outgoingHalfEdges.length;
			
			for (int i = 0 ; i<n; i++){
				int heStart = outgoingHalfEdges[i];
				
				if(heGetVertex(heStart)==toVert){
					return heStart;
				}
				else {
//					heStart= getNextOHeCW(heStart);
	//				heStart= heGetNext(heGetTwin(heStart));
				}
			}
			
			return -1;
		}
//	public int vertexGetIHe(int vertexPos){
//		return vertexToHalfEdge.get (vertexPos);
//	}
//	public int vertexGetOHe(int vertexPos){
//		return heGetTwin(vertexGetIHe(vertexPos));
//	}
		
	public int [] vertexGetOHeArray(int vertexPos){
		IntList temp = new IntList();
		
		for (int i=0 ; i<getHalfEdgesCount () ; i++){
			if(heGetVertex(i)==vertexPos){
				temp.push (heGetTwin(i));
			}
		}
		temp.trimToSize ();
		return temp.toArray ();
	}
	
	public int [] vertexGetIHeArray(int vertexPos){
		IntList temp = new IntList();
		
		for (int i=0 ; i<getHalfEdgesCount () ; i++){
			if(heGetVertex(i)==vertexPos){
				temp.push (i);
			}
		}
		temp.trimToSize ();
		return temp.toArray ();
	}
		
	public int getNextIHeCW(int hePos){
		return heGetTwin(heGetNext(hePos));
	}
	public int getNextOHeCW(int hePos){
		return heGetNext(heGetTwin(hePos));
	}
	/*
		 * 
		 * */
		
		
		
		
		
		/**
		 * Intersection routine which returns a Tuple3f
		 */
			
	//	public Tuple3f TriangleIntersection(int facePos, Tuple3f origin,
	//			Tuple3f destination, boolean[] out) {
	//
	//		Tuple3f orig = origin;
	//		Tuple3f dir = destination;
	//		dir.sub(orig);
	//
	//		Tuple3f v1 = vertexGetTuple3f(heGetVertex(faceGetHalfEdge(facePos)));
	//		Tuple3f v2 = vertexGetTuple3f(heGetVertex(heGetNext(faceGetHalfEdge(facePos))));
	//		Tuple3f v3 = vertexGetTuple3f(heGetVertex(heGetPrev(faceGetHalfEdge(facePos))));
	//
	//		Tuple3f edge1 = new Point3f();
	//		Tuple3f edge2 = new Point3f();
	//
	//		edge1.sub(v2, v1);
	//		edge2.sub(v3, v1);
	//
	//		Tuple3f pvec = new Point3f();
	//		pvec = CROSS(dir, edge2);
	//
	//		float det;
	//
	//		det = DOT(edge1, pvec);
	//
	//		if (det > -EPSILON && det < EPSILON)// segment lies in plane
	//			return null;
	//		if (det < EPSILON)
	//			out[0] = true;
	//		else
	//			out[0] = false;
	//		float inv_det = 1.0f / det;
	//
	//		// calculate distance from v1 to ray origin
	//
	//		Tuple3f tvec = new Point3f();
	//
	//		tvec.sub(orig, v1);
	//
	//		// calculate U parameter and test bounds
	//
	//		float u;
	//
	//		u = (tvec.x * pvec.x + tvec.y * pvec.y + tvec.z * pvec.z) * inv_det;
	//
	//		if (u < 0.0f || u > 1.0f)
	//			return null;
	//
	//		Tuple3f qvec = new Point3f();
	//
	//		qvec = CROSS(tvec, edge1);
	//
	//		float v = DOT(dir, qvec) * inv_det;
	//		if (v < 0.0f || u + v > 1.0f)
	//			return null;
	//		
	//		//TODO vielleicht die Punkte der Dreiecke abfangen, kann aber auch in der FaceSplit und HeSplit gemacht werden
	//		if( (u==0 && v==0) || (u==1 && v==0) || (u==0 && v==1))
	//			return null;
	//		
	//		// calculae t, ray intersects triangle
	//
	//		float t = DOT(edge2, qvec) * inv_det;
	//		if ((t < 0.0f) || (t > 1.0f)) {
	//			return null;
	//		}
	//		return new Point3f(orig.x + dir.x * t, orig.y + dir.y * t, orig.z
	//				+ dir.z * t);
	//	}
	
	//	/**
	//	 * Intersection routine which doesn't check the hole triangle.
	//	 * needs to be replaced. 
	//	 */
	//	
	//	public Tuple3f TriangleIntersectionNOE(int facePos, Tuple3f origin,
	//			Tuple3f destination, boolean[] out) {
	//
	//		Tuple3f orig = origin;
	//		Tuple3f dir = destination;
	//		dir.sub(orig);
	//
	//		Tuple3f v1 = vertexGetTuple3f(heGetVertex(faceGetHalfEdge(facePos)));
	//		Tuple3f v2 = vertexGetTuple3f(heGetVertex(heGetNext(faceGetHalfEdge(facePos))));
	//		Tuple3f v3 = vertexGetTuple3f(heGetVertex(heGetPrev(faceGetHalfEdge(facePos))));
	//
	//		Tuple3f edge1 = new Point3f();
	//		Tuple3f edge2 = new Point3f();
	//
	//		edge1.sub(v2, v1);
	//		edge2.sub(v3, v1);
	//
	//		Tuple3f pvec = new Point3f();
	//		pvec = CROSS(dir, edge2);
	//
	//		float det;
	//
	//		det = DOT(edge1, pvec);
	//
	//		if (det > -EPSILON && det < EPSILON)// segment lies in plane
	//			return null;
	//		if (det < EPSILON)
	//			out[0] = true;
	//		else
	//			out[0] = false;
	//		float inv_det = 1.0f / det;
	//
	//		// calculate distance from v1 to ray origin
	//
	//		Tuple3f tvec = new Point3f();
	//
	//		tvec.sub(orig, v1);
	//
	//		// calculate U parameter and test bounds
	//
	//		float u;
	//
	//		u = (tvec.x * pvec.x + tvec.y * pvec.y + tvec.z * pvec.z) * inv_det;
	//
	//		if (u < 0 || u > 1.0f)
	//			return null;
	//
	//		Tuple3f qvec = new Point3f();
	//
	//		qvec = CROSS(tvec, edge1);
	//
	//		float v = DOT(dir, qvec) * inv_det;
	//		if (v < 0 || u + v > 1.0f)
	//			return null;
	//
	//		// calculae t, ray intersects triangle
	//
	//		float t = DOT(edge2, qvec) * inv_det;
	//		if ((t < EPSILON) || (t + EPSILON > 1.0f)) {
	//			return null;
	//		}
	//		return new Point3f(orig.x + dir.x * t, orig.y + dir.y * t, orig.z
	//				+ dir.z * t);
	//	}
		
		/**
		 * transforms all vertices by the given transformation matrix
		 * @param transformation
		 */
		public void transform(Matrix4d transformation) {
	
			for (int i = 0; i < getVerticesCount(); i++) {
				Tuple3d vert=getVertex(i);
				transformation.transform( (Point3d) vert  );
				setVertex(i, vert);
			}
	
		}
		/**
		 * Adds a new Triangle to the Half-Edge object.
		 * This method also updates the twin relation between neighboring Half-Edges.
		 * 
		 * @param vertex1 coordinates of the first vertex as a Tuple3f
		 * @param vertex2 coordinates of the second vertex as a Tuple3f
		 * @param vertex3 coordinates of the third vertex as a Tuple3f
		 * @param normal1 normal at the first vertex as a Tuple3f
		 * @param normal2 normal at the second vertex as a Tuple3f
		 * @param normal3 normal at the third vertex as a Tuple3f
		 * @param tex1 texture coordinate at the first vertex as a Tuple2f
		 * @param tex2 texture coordinate at the second vertex as a Tuple2f
		 * @param tex3 texture coordinate at the third vertex as a Tuple2f
		 */
		public void addTriangle(Tuple3d vertex1, Tuple3d vertex2,
				Tuple3d vertex3, Tuple3f normal1, Tuple3f normal2,
				Tuple3f normal3, Tuple2f tex1, Tuple2f tex2, Tuple2f tex3) {
		
			normals.push(normal1.x, normal1.y, normal1.z);
			normals.push(normal2.x, normal2.y, normal2.z);
			normals.push(normal3.x, normal3.y, normal3.z);
		
			uv.push(tex1.x, tex1.y);
			uv.push(tex2.x, tex2.y);
			uv.push(tex3.x, tex3.y);
		
			// 1.Insert the vertices save positions and which ones are already in
			// the list
			// 1.1.1 Insert vertex1
			int vertpos1 = insertVertex(vertex1);
		
			boolean vert1InList = false;
			if (vertpos1 < 0) {
				vertpos1 = (vertpos1 + 1) * (-1);
				vert1InList = true;
			}
			// 1.1.2 Insert vertex2
			int vertpos2 = insertVertex(vertex2);
		
			boolean vert2InList = false;
			if (vertpos2 < 0) {
				vertpos2 = (vertpos2 + 1) * (-1);
				vert2InList = true;
			}
			// 1.1.3 Insert vertex3
			int vertpos3 = insertVertex(vertex3);
		
			boolean vert3InList = false;
			if (vertpos3 < 0) {
				vertpos3 = (vertpos3 + 1) * (-1);
				vert3InList = true;
			}
			// 1.2.1 Add first Halfedge
		
			this.insertEdge2(vertpos3, vertpos1, vert3InList, vert1InList);
		
			// 1.2.2 Add second Halfedge
		
			this.insertEdge2(vertpos1, vertpos2, vert1InList, vert2InList);
		
			// 1.2.3 Add third Halfedge
		
			this.insertEdge2(vertpos2, vertpos3, vert2InList, vert3InList);
		}
		/**
		 * Adds a Triangle to the Half-Edge object with an additional shader.
		 * The method just calls addTriangle
		 * 
		 * @param vertex1 coordinates of the first vertex as a Tuple3f
		 * @param vertex2 coordinates of the second vertex as a Tuple3f
		 * @param vertex3 coordinates of the third vertex as a Tuple3f
		 * @param normal1 normal at the first vertex as a Tuple3f
		 * @param normal2 normal at the second vertex as a Tuple3f
		 * @param normal3 normal at the third vertex as a Tuple3f
		 * @param tex1 texture coordinate at the first vertex as a Tuple2f
		 * @param tex2 texture coordinate at the second vertex as a Tuple2f
		 * @param tex3 texture coordinate at the third vertex as a Tuple2f
		 * @param s 
		 */
		public void addTriangle(Tuple3d vertex1, Tuple3d vertex2,
				Tuple3d vertex3, Tuple3f normal1, Tuple3f normal2,
				Tuple3f normal3, Tuple2f tex1, Tuple2f tex2, Tuple2f tex3, Shader s) {
			addTriangle(vertex1, vertex2, vertex3, normal1, normal2, normal3, tex1, tex2, tex3);
			//try to get the index of the shader
			
			
			int sIndex = shader.indexOf(s);
			if(sIndex<0){
				// shader is not in list add it
				shader.add(s);
				sIndex=shader.size-1;
			}
			triangleToShader.add(sIndex);
		}
		/**
		 * Adds a Triangle to the Half-Edge object. This method requires the positions of the 3 vertices inside the Vertices Array 
		 * and the the positions of the Half-Edge Twins from the HalfEdge Array. 
		 * This method is normally used to alter the data of an existing Half-Edge mesh.
		 * The new Triangle is added to the end of the HalfEdge Array.
		 * 
		 * @param vertex1 position inside the Vertices array of the first vertex
		 * @param twin1 position inside the Vertices array of the twin of the first vertex
		 * @param uv1 texture coordinate of the first vertex as Tuple2f
		 * @param normal1 normal at the first vertex as Tuple3f
		 * @param vertex2 position inside the Vertices array of the second vertex
		 * @param twin2 position inside the Vertices array of the twin of the second vertex
		 * @param uv2 texture coordinate of the second vertex as Tuple2f
		 * @param normal2 normal at the second vertex as Tuple3f
		 * @param vertex3 position inside the Vertices array of the third vertex
		 * @param twin3 position inside the Vertices array of the twin of the third vertex
		 * @param uv3 texture coordinate of the third vertex as Tuple2f
		 * @param normal3 normal at the third vertex as Tuple3f
		 */
		
		public void addTriangle(int vertex1, int twin1, Tuple2f uv1,
				Tuple3f normal1, int vertex2, int twin2, Tuple2f uv2,
				Tuple3f normal2, int vertex3, int twin3, Tuple2f uv3,
				Tuple3f normal3) {
			halfEdges.push(vertex1, twin1);
			halfEdges.push(vertex2, twin2);
			halfEdges.push(vertex3, twin3);
			uv.push(uv1.x, uv1.y);
			uv.push(uv2.x, uv2.y);
			uv.push(uv3.x, uv3.y);
			normals.push(normal1.x, normal1.y, normal1.z);
			normals.push(normal2.x, normal2.y, normal2.z);
			normals.push(normal3.x, normal3.y, normal3.z);
		}
		/**
		 * Adds a Triangle to the Half-Edge object with an additional Shader. 
		 * This method requires the positions of the 3 vertices inside the Vertices Array 
		 * and the the positions of the Half-Edge Twins from the HalfEdge Array. 
		 * This method is normally used to alter the data of an existing Half-Edge mesh.
		 * The new Triangle is added to the end of the HalfEdge Array.
		 * 
		 * @param vertex1 position inside the Vertices array of the first vertex
		 * @param twin1 position inside the Vertices array of the twin of the first vertex
		 * @param uv1 texture coordinate of the first vertex as Tuple2f
		 * @param normal1 normal at the first vertex as Tuple3f
		 * @param vertex2 position inside the Vertices array of the second vertex
		 * @param twin2 position inside the Vertices array of the twin of the second vertex
		 * @param uv2 texture coordinate of the second vertex as Tuple2f
		 * @param normal2 normal at the second vertex as Tuple3f
		 * @param vertex3 position inside the Vertices array of the third vertex
		 * @param twin3 position inside the Vertices array of the twin of the third vertex
		 * @param uv3 texture coordinate of the third vertex as Tuple2f
		 * @param normal3 normal at the third vertex as Tuple3f
		 * @param s Shader used to render this Triangle
		 */
		
		public void addTriangle(int vertex1, int twin1, Tuple2f uv1,
				Tuple3f normal1, int vertex2, int twin2, Tuple2f uv2,
				Tuple3f normal2, int vertex3, int twin3, Tuple2f uv3,
				Tuple3f normal3, Shader s) {
			halfEdges.push(vertex1, twin1);
			halfEdges.push(vertex2, twin2);
			halfEdges.push(vertex3, twin3);
			uv.push(uv1.x, uv1.y);
			uv.push(uv2.x, uv2.y);
			uv.push(uv3.x, uv3.y);
			normals.push(normal1.x, normal1.y, normal1.z);
			normals.push(normal2.x, normal2.y, normal2.z);
			normals.push(normal3.x, normal3.y, normal3.z);
			
			int sIndex = shader.indexOf(s);
			if(sIndex<0){
				shader.add(s);
				sIndex=shader.size-1;
			}
			triangleToShader.add(sIndex);
			
		}
		/**
		 * returns the first half edge of a given face
		 */
		
		int faceGetHalfEdge(int faceNo) {
			return 3 * faceNo;
		}
		/**
		 * 
		 */
		
		public boolean heIsInterior(int halfEdgePos) {
			
			int prevHalfEdge=heGetPrev(halfEdgePos);
			
			int twin = heGetTwin(halfEdgePos);
			int prevTwin = heGetPrev(twin);
			
			//check if Shaders, are the same
			
			int faceHe=heGetFace(halfEdgePos);
			int faceTwin=heGetFace(twin);
			
			if(! (getShader(faceHe)==getShader(faceTwin) ) ){
				return false;
			}
			
			//check if UV-coordinates are the same
			
			Tuple2f twinUV = heGetUV(twin);
			Tuple2f heUV = heGetUV(prevHalfEdge);
			
			if(!twinUV.epsilonEquals (heUV, EPSILON_F)){
				return false;
			}
			
			twinUV = heGetUV(prevTwin);
			heUV = heGetUV(halfEdgePos);
			
			if(!twinUV.epsilonEquals (heUV, EPSILON_F)){
				return false;
			}
			
			//check if Normals are the Same
			
			Tuple3f twinNormal = heGetNormal(twin);
			Tuple3f heNormal = heGetNormal(prevHalfEdge);
			
			if(!twinNormal.epsilonEquals (heNormal, EPSILON_F)){
				return false;
			}
			
			twinNormal = heGetNormal(prevTwin);
			heNormal = heGetNormal(halfEdgePos);
			
			if(!twinNormal.epsilonEquals (heNormal, EPSILON_F)){
				return false;
			}
			
			//check if faces are co-planar
			
			Tuple3d v1edge;
			Tuple3d v2edge;
			Tuple3d sedge;
			
			sedge = vertexGetTuple3f(heGetVertex(halfEdgePos));
			sedge.sub(vertexGetTuple3f(heGetVertex(heGetTwin(halfEdgePos))));
		
			v1edge = vertexGetTuple3f(heGetVertex(heGetNext(halfEdgePos)));
			v1edge.sub(vertexGetTuple3f(heGetVertex(heGetTwin(heGetNext(halfEdgePos)))));
		
			v2edge = vertexGetTuple3f(heGetVertex(heGetNext(heGetTwin(halfEdgePos))));
			v2edge.sub(vertexGetTuple3f(heGetVertex(heGetTwin(heGetNext(heGetTwin(halfEdgePos))))));
		
			Tuple3d pvec = IntersectionTests.CROSS(v1edge, v2edge);
		
			double det = IntersectionTests.DOT(pvec, sedge);
		
			if (det > -EPSILON_D && det < EPSILON_D){
				return true;
			}
			return false;
		
		}
		/**
		 * This method should be used to insert an Half-Edge in the Half-Edge Array 
		 * and adds the Twin automatically, 
		 * without comparing every Half-Edge already in the Half-Edge Array,
		 * by checking local relationships.
		 * 
		 * This method is not used but should be worth implementing.
		 * 
		 * @param fromVertex
		 * @param toVertex
		 * @param fromVertexInList
		 * @param toVertexInList
		 */
		private void insertEdge(int fromVertex, int toVertex,
				boolean fromVertexInList, boolean toVertexInList) {
		
			halfEdges.push(toVertex);
		
			if (toVertexInList == true) {
				// check if edge exists
				if (fromVertexInList == true) {
					// suche HalfEdge die auf vertex1 zeigt und dessen vorgänger auf
					// vertex2 zeigt
					int start = vertexGetHalfEdge(fromVertex);
					// if(halfEdgeVertex(start)!=vertpos2)
					while (start >= 0 && heGetVertex(heGetPrev(start)) != toVertex) {
						start = (heGetTwin(heGetNext(start)));
					}
					// if(start<0){
					// start = vertexHE(fromVertex);
					// while( start>=0 && halfEdgeVertex(prevHE(start))!=toVertex
					// ){//Counterclockwise
					// start=( twinHE(start) ) ;
					// if(start>=0){start=prevHE(start);}
					// }
					// }
					halfEdges.push(start);
					if (start >= 0)
						halfEdges.set(3 * start + 1, halfEdges.size() / 3);
				} else
					halfEdges.push(-1);
			} else { // If vertex wasn't in the List add next HalfEdge to the vertex
				halfEdges.push(-1);
				vertexToHalfEdge.push(heGetNext(halfEdges.size() / 3 - 1));
			}
		}
		/**
		 * This method is used to add an Half-Edge to the Half-Edge Array.
		 * It also searches for the twin Half-Edges and adds it if it exists,
		 * else it assignees the value -1.
		 * Searching the twin probably requires o(n) because every Half-Edge 
		 * inside the Half-Edges array is checked until the twin Half-Edge is being found.
		 * 
		 * @param fromVertex
		 * @param toVertex
		 * @param fromVertexInList
		 * @param toVertexInList
		 */
		private void insertEdge2(int fromVertex, int toVertex,
				boolean fromVertexInList, boolean toVertexInList) {
			
			halfEdges.push(toVertex);
		
			if (fromVertexInList == true) {
				// check if edge exists
				if (toVertexInList == true) {
					// booth vertices are already in the list
					// try to find the Twin if any
					int start = -1;
					for (int i = 0; i < halfEdges.size() / 2; i++) {
						//TODO: could be replaced by getHeFT() it even would return -1 if that half-edge doesnt exist
						if (heGetVertex(i) == fromVertex
								&& heGetVertex(heGetPrev(i)) == toVertex) {
							start = i;
							break;
						}
					}
					
					//Add The Twin to the HalfEdges Array. If any.
					halfEdges.push(start);
					if (start >= 0 && heGetTwin(start) == -1){
						//If Twin exists, alter its twin to this half edge
						heSetTwin(start, halfEdges.size() / 2 - 1);}
				} else	{
					halfEdges.push(-1);}
			} else { 
				// If vertex wasn't in the List add next HalfEdge to the vertex
				// we also know there is no twin yet
				halfEdges.push(-1);
				vertexToHalfEdge.push(heGetNext(halfEdges.size() / 2 - 1));
			}
		}
		/**
			 * This method adds the given vertex to the vertices Array if the vertex is not already present in the Array.
			 * It returns the position of the vertex inside the vertices Array.
			 * This method checks every vertex inside the Array for equality by comparing it within the given epsilon frame.
			 * 
			 * If the Vertex is already present in the array this method returns its position as a negative value minus 1.
			 * 
			 * Otherwise
			 * 
			 * @param vertex vertex given as a Tuple3f
			 * @return position of the vertex inside the array
			 */
			int insertVertex(Tuple3d vertex) {
		
				for (int i = 0; i < vertices.size/3; i++) {
					if (vertex.epsilonEquals(vertexGetTuple3f(i), EPSILON_D_VERT))
						return ((i) * (-1)) - 1;
				}
		
				vertices.push(vertex.x, vertex.y, vertex.z);
		//		vertexStatus.add(UNDEFINED);
				return (vertices.size() - 1) / 3;
		
			}
		public Tuple3d faceCalculateBarycentre(int facePos){
			Tuple3d out = faceGetVertexW(facePos);
			out.add (faceGetVertexU(facePos));
			out.add (faceGetVertexV(facePos));
			out.scale (1.0d/3.0d);
			
			
			return out;
		}
		
		public Tuple3d faceCalculateBarycentre2(int facePos){
			Tuple3d v0 = faceGetVertexW(facePos);
			Tuple3d v1 = faceGetVertexU(facePos);
			Tuple3d v2 = faceGetVertexV(facePos);
			
			v0.scale (0.7f);
			v1.scale(0.15f);
			v2.scale(0.15f);
			
			Tuple3d out = v0; 
			out.add (v1);
			out.add (v2);
			
			return out;
		}
		
		public boolean validateStatus(){
			for (int i = 0 ; i<this.getHalfEdgesCount () ; i++){
				if(heGetTwin(i)<0){
					return false;
				}
			}
			return true;
		}
		
		public String toString(){
			
			String out = new String();
			
			for (int i = 0 ; i < vertices.size()/3 ; i++){
				out+=" ( "+i+" ) [ "+vertices.get (i*3)+" ; "+vertices.get (i*3 + 1)+" ; "+vertices.get (i*3 + 2)+" ]"+'\n';
			}
			
			
			System.out.println("Half Edges");
			for (int i = 0 ; i < halfEdges.size()/2 ; i++){
				
				out+=" ( "+i+" ) [ "+heGetVertex(i)+" ; "+i/3+" ; "+heGetTwin(i)+" ]"+'\n';
				
			}
			return out;
		}
		
}
