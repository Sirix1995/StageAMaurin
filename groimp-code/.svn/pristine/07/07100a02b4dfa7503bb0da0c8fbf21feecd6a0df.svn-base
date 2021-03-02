/*
 * Copyright (C) 2020 GroIMP Developer Team
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

/**
 * @author      elsaromm
 * @version     1.0                                      
 * @since       2022.10.30
 */
package de.grogra.nurbseditor3d;


import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import de.grogra.imp3d.objects.Box;
import de.grogra.imp3d.objects.Cone;
import de.grogra.imp3d.objects.Cylinder;
import de.grogra.imp3d.objects.Frustum;
import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.imp3d.objects.NURBSSurface;
import de.grogra.imp3d.objects.Sphere;
import de.grogra.math.BSplineCurve;
import de.grogra.math.BSplineOfVertices;
import de.grogra.math.BSplineSurfaceImpl;
import de.grogra.math.VertexListImpl;

public class ObjectGeometry3D {
	static final int MAX_COUNT = 50;
	
	private static NURBSCurve[] nurbscurve = new NURBSCurve[MAX_COUNT];
	private static BSplineSurfaceImpl[] nurbssurfaces = new BSplineSurfaceImpl[MAX_COUNT];
	private static Box[] quads = new Box[MAX_COUNT];
	private static Point3f[] quadCoordinates = new Point3f[MAX_COUNT];
	private static Cone[] cones = new Cone[MAX_COUNT];
	private static Point3f[] coneCoordinates = new Point3f[MAX_COUNT];
	private static Sphere[] spheres = new Sphere[MAX_COUNT];
	private static Point3f[] sphereCoordinates = new Point3f[MAX_COUNT];
	private static Cylinder[] cylinders = new Cylinder[MAX_COUNT];
	private static Point3f[] cylinderCoordinates = new Point3f[MAX_COUNT];
	private static Frustum[] frustum = new Frustum[MAX_COUNT];
	private static Point3f[] frustumCoordinates = new Point3f[MAX_COUNT];
	private static int CurveCounter = 0;
	private static int SurfaceCounter = 0;
	
	//initialize new NURBS-Curve
	public static void initCurve() {
		if(CurveCounter < MAX_COUNT) {
			nurbscurve[CurveCounter] = new NURBSCurve();
			CurveCounter++;
		}
	}
	
	//initialize new NURBS-Surface
	public static void initSurface() {
		if(SurfaceCounter < MAX_COUNT) {
			nurbssurfaces[SurfaceCounter] = new BSplineSurfaceImpl();
			nurbssurfaces[SurfaceCounter].setUCount(getUCount());
			SurfaceCounter++;
		}
	}
	
	public static void addCurve(NURBSCurve curve) {
		int index = getCurveCounter();
		if(index < MAX_COUNT) {
			nurbscurve[index] = curve;
			setCurveCounter(index+1);
		}
	}
	
	public static void addSurface(BSplineSurfaceImpl surface) {
		int index = getSurfaceCounter();
		if(index < MAX_COUNT) {
			nurbssurfaces[index] = surface;
			setSurfaceCounter(index+1);
		}
	}
	
	public static void deleteCurve() {
		int index = ObjectGeometry3D.getCurveCounter();
		if(index > 0) {
			setCurveData(nurbscurve[index-1], null);
			if(index > 1) {
				ObjectGeometry3D.setCurveCounter(index-1);
			}
		}
	}
	
	public static void deleteSurface() {
		int index = ObjectGeometry3D.getSurfaceCounter();
		if(index > 0) {
			setSurfaceData(nurbssurfaces[index-1], null); 
			if(index > 1) {
				ObjectGeometry3D.setSurfaceCounter(index-1);
			}
		}
	}
	
	public static void deleteQuad() {
		int index = getQCounter();
		if(index > 0) {
			quads[index-1] = null;
			setQCounter(index-1);
		}
	}
	public static void deleteCone() {
		int index = getCCounter();
		if(index > 0) {
			cones[index-1] = null;
			setCCounter(index-1);
		}
	}
	public static void deleteSphere() {
		int index = getSCounter();
		if(index > 0) {
			spheres[index-1] = null;
			setSCounter(index-1);
		}
	}
	public static void deleteCylinder() {
		int index = getCylCounter();
		if(index > 0) {
			cylinders[index-1] = null;
			setCylCounter(index-1);
		}
	}
	public static void deleteFrustum() {
		int index = getFCounter();
		if(index > 0) {
			frustum[index-1] = null;
			setFCounter(index-1);
		}
	}
	
	public static void addCurvePoint(Point4f p) {
    	
    	int curveIndex = ObjectGeometry3D.getCurveCounter() - 1;
		VertexListImpl vertexlist = new VertexListImpl();
		int size;
		int degree = 3;
		float [] oldData= getCurveData(nurbscurve[curveIndex]);
		if(oldData == null) {
			size = 0;
		}
		else {
			size = oldData.length;
		}
		float [] data = new float[size + 4];
		for(int i = 0; i < data.length-4; i++) {
			data[i] = oldData[i];   
		}
		
		data[data.length-4] = (float) p.x;
		data[data.length-3] = (float) p.y;
		data[data.length-2] = (float) p.z;
		data[data.length-1] = (float) p.w;
		
		vertexlist.setData(data);
		vertexlist.setDimension(4);
		BSplineOfVertices bspline = new BSplineOfVertices(vertexlist, degree, false, false);
		bspline.setRational(true);
		nurbscurve[curveIndex].setCurve(bspline);
    }
	
	public static void addSurfacePoint(Point4f p) {
    	int surfaceIndex = ObjectGeometry3D.getSurfaceCounter() - 1;
    	int size;
		float [] oldData= getSurfaceData(nurbssurfaces[surfaceIndex]);
		if(oldData == null) {
			size = 0;
		}
		else {
			size = oldData.length;
		}
		float [] data = new float[size + 4];
		for(int i = 0; i < data.length-4; i++) {
			data[i] = oldData[i];   
		}
		
		data[data.length-4] = (float) p.x;
		data[data.length-3] = (float) p.y;
		data[data.length-2] = (float) p.z;
		data[data.length-1] = (float) p.w;
		
		nurbssurfaces[surfaceIndex].setData(data);	
	}
	
	private static int uCount = 1;
	public static int getUCount() {
		return uCount;
	}
	
	public static void setUCount(int spline, int c) {
		uCount = c;
		if(spline >= 0) {
			nurbssurfaces[spline].setUCount(c);
			nurbssurfaces[spline].setUDegree(c-1);
			int vDegree = nurbssurfaces[spline].getData().length/(c * 4) - 1;
			nurbssurfaces[spline].setVDegree(vDegree);
		}
	}
	
	public static NURBSSurface createNURBSSurface(BSplineSurfaceImpl bspline) {
		float[] data = bspline.getData();
		int dimension = 4;
		int uCount = bspline.getUCount();
		int uDegree = uCount -1;
		int vDegree = data.length/(uCount * dimension)-1;
		boolean uClamp = true;
		boolean vClamp = true;
		boolean uPeriodic = false;
		boolean vPeriodic = false;		
		BSplineSurfaceImpl s = new BSplineSurfaceImpl();
		s = s.create(data, uCount, dimension, uDegree, uClamp, uPeriodic, vDegree, vClamp, vPeriodic);
		s.setRational(true);
		NURBSSurface nurbs = new NURBSSurface(s);
		return nurbs;
	}
	
	
	public static int getCurveSize(NURBSCurve nurbs) {
    	int size = 0;
    	float data[] = getCurveData(nurbs);
		if(data != null) {
			size = data.length / 4;
		}
    	return size;
    }
	
	public static int getSurfaceSize(BSplineSurfaceImpl nurbs) {
    	int size = 0;
    	float data[] = getSurfaceData(nurbs);
		if(data != null) {
			size = data.length / 4;
		}
    	return size;
    }
	
	public static int getCurveCounter() {
		return CurveCounter;
	}
	
	public static void setCurveCounter(int c) {
		CurveCounter = c;
	}
	
	public static int getSurfaceCounter() {
		return SurfaceCounter;
	}
	
	public static void setSurfaceCounter(int c) {
		SurfaceCounter = c;
	}
	
	public static Point4f getCurvePoint(NURBSCurve nurbs, int i) {
    	Point4f point = new Point4f();
    	float[] data = getCurveData(nurbs);
		if(data != null) {
			point.x = data[4 * i];
			point.y = data[4 * i + 1];
			point.z = data[4 * i + 2];
			point.w = data[4 * i + 3];
		}
		return point;
    }
	
	public static void setCurvePoint(NURBSCurve nurbs, Point4f p, int index){
    	float [] data = getCurveData(nurbs);
    	if(data != null) {
    		data[4*index] = p.x;
    		data[4*index + 1] = p.y;
    		data[4*index + 2] = p.z;
    		data[4*index + 3] = p.w;
    		setCurveData(nurbs, data);
    	}
    }
	
	public static Point4f getSurfacePoint(BSplineSurfaceImpl nurbs, int i) {
    	Point4f point = new Point4f();
    	float[] data = getSurfaceData(nurbs);
		if(data != null) {
			point.x = data[4 * i];
			point.y = data[4 * i + 1];
			point.z = data[4 * i + 2];
			point.w = data[4 * i + 3];
		}
		return point;
    }
	
	public static void setSurfacePoint(BSplineSurfaceImpl nurbs, Point4f p, int index){
    	
    	float [] data = getSurfaceData(nurbs);
    	if(data != null) {
    		data[4*index] = p.x;
    		data[4*index + 1] = p.y;
    		data[4*index + 2] = p.z;
    		data[4 * index + 3] = p.w;
    		setSurfaceData(nurbs, data);
    	}
    }
	
	public static NURBSCurve getNURBSCurve(int index) {
		return nurbscurve[index];
	}
	
	public static BSplineSurfaceImpl getNURBSSurface(int index) {
		return nurbssurfaces[index];
	}
		
	public static Point4f getCurveCenter(NURBSCurve nurbs) {
    	Point4f center = new Point4f();
    	
    	//Schwerpunkt der Kurve
			float sumX = 0;
			float sumY = 0;
			float sumZ = 0;
			float sumW = 0;
			for(int i = 0; i < getCurveSize(nurbs); i++) {
				sumX += getCurvePoint(nurbs, i).x;
				sumY += getCurvePoint(nurbs, i).y;
				sumZ += getCurvePoint(nurbs, i).z;
				sumW += getCurvePoint(nurbs, i).w;
			}
			float mittelX = 1.0f /(float) getCurveSize(nurbs) * sumX;
			float mittelY = 1.0f /(float) getCurveSize(nurbs) * sumY;
			float mittelZ = 1.0f /(float) getCurveSize(nurbs) * sumZ;
			float mittelW = 1.0f /(float) getCurveSize(nurbs) * sumW;
			center.set(mittelX, mittelY, mittelZ, mittelW);
    	return center;
    }
	
	public static Point4f getSurfaceCenter(BSplineSurfaceImpl nurbs) {
    	Point4f center = new Point4f();
    	
    	//Schwerpunkt der Kurve
			float sumX = 0;
			float sumY = 0;
			float sumZ = 0;
			float sumW = 0;
			for(int i = 0; i < getSurfaceSize(nurbs); i++) {
				sumX += getSurfacePoint(nurbs, i).x;
				sumY += getSurfacePoint(nurbs, i).y;
				sumZ += getSurfacePoint(nurbs, i).z;
				sumW += getSurfacePoint(nurbs, i).w;
			}
			float mittelX = 1.0f /(float) getSurfaceSize(nurbs) * sumX;
			float mittelY = 1.0f /(float) getSurfaceSize(nurbs) * sumY;
			float mittelZ = 1.0f /(float) getSurfaceSize(nurbs) * sumZ;
			float mittelW = 1.0f /(float) getSurfaceSize(nurbs) * sumW;
			center.set(mittelX, mittelY, mittelZ, mittelW);
    	return center;
    }
	
	
	public static VertexListImpl createVertexList(Point4f[] p) {
		VertexListImpl vertexlist = new VertexListImpl();
		float[] data = new float[CurveCounter*4];
		
		for(int i = 0; i < CurveCounter; i++) {
			data[4*i] = (float) p[i].x;
			data[4*i+1] = (float) p[i].y;
			data[4*i+2] = (float) p[i].z;
			data[4*i + 3] = (float) p[i].w;
		}
		vertexlist.setData(data);
		vertexlist.setDimension(4);
		return vertexlist;
	}
	  
    public static float[] getCurveData(NURBSCurve nurbs){
    	float[] data = null;
    	BSplineCurve curve = nurbs.getCurve();
		if(curve != null) {
			if(curve instanceof BSplineOfVertices){ 
				if(((BSplineOfVertices) curve).getVertices() instanceof VertexListImpl) {
					data = ((VertexListImpl)(((BSplineOfVertices) curve)).getVertices()).getData();
				}
			}
		}
		return data;	
    }
    
    public static void setCurveData(NURBSCurve nurbs, float[] data) {
	   	BSplineCurve curve = nurbs.getCurve();
		if(curve != null) {
			if(curve instanceof BSplineOfVertices){ 
				if(((BSplineOfVertices) curve).getVertices() instanceof VertexListImpl) {
					((VertexListImpl)(((BSplineOfVertices) curve)).getVertices()).setData(data);	
				}
			}
		}
	}
    
    
    public static float[] getSurfaceData(BSplineSurfaceImpl nurbs){
    	return nurbs.getData();
    }
    
    public static void setSurfaceData(BSplineSurfaceImpl nurbs, float[] data) {
		nurbs.setData(data);
	}
    
    
    public static void setCurveWeight(NURBSCurve curve, int index, int weight) {
		Point4f p = getCurvePoint(curve, index);
		if(weight < 0) {
			if(p.w > 0.1f) {
				p.w += (float)weight/10.0f;
			}
		}else {
			p.w += (float)weight/10.0;
		}
		setCurvePoint(curve, p, index);
		
	}
	
	public static float getCurveWeight(NURBSCurve curve, int index) {
		float w;
			float [] data  = getCurveData(curve);
			w = data[4*index + 4];
		return w;
	}
	
	public static void setSurfaceWeight(BSplineSurfaceImpl surface, int index, int weight) {
		Point4f p = getSurfacePoint(surface, index);
		if(weight < 0) {
			if(p.w > 0.1f) {
				p.w += (float)weight/10.0f;
			}
		}else {
			p.w += (float)weight/10.0;
		}
		setSurfacePoint(surface, p, index);
		
	}
	
	public static float getSurfaceWeight(BSplineSurfaceImpl surface, int index) {
		float w;
			float [] data  = getSurfaceData(surface);
			w = data[4*index + 4];
		return w;
	}
	
	
	
    public static String createRGGText() {
    	String output = "";

    	int dim = 4;
		int degree = 3;
		boolean periodic = false;
		boolean bezier = false;
		boolean rational = true;
		
		boolean first = true;
				
		int curves = getCurveCounter();
		int surfaces = getSurfaceCounter();
		int boxes = getQCounter();
		int cones = getCCounter();
		int spheres = getSCounter();
		int cylinders = getCylCounter();
		int frustums = getFCounter();
		
		//body of rgg file should only contain text if any objects were created
    	if( curves > 0 || surfaces > 0 || boxes > 0 || cones > 0 || spheres > 0 || cylinders > 0 || frustums > 0) {
			output += "module A(float len) extends Sphere(0.1);\n\nprotected void init ()[\n\t" + 
							"Axiom ==> A(1);\n]\n\npublic void start ()[\n{";
			
			for(int i = 0; i < curves; i++) {
				float[] data = getCurveData(nurbscurve[i]);
				if(data != null) {
					float[] d = new float[data.length];
					for(int j = 0; j < data.length/4; j++) {
						d[4 * j] = data[j * 4 + 2];
						d[4 * j +1]	= data[j*4]; 
						d[4 * j +2]	= data[j*4 + 1];
						d[4 * j +3]	= data[j*4 + 3];
					}
					
					output += "\n\tBSplineOfVertices curve" + i + " = new BSplineOfVertices("
						+ " new VertexListImpl(new float[] {" +data[0] ;
							
					for(int j = 1; j < data.length; j++) {
						output += ", " + data[j];
					}				
					
					output += "}, " + dim + "), " + degree + ", " + periodic + ", " + bezier + "); \n\t";
					output += "curve" + i + ".setRational("+rational+");\n";		
				}
			}
			
			
			for(int i = 0; i < surfaces; i++) {
				float[] data = getSurfaceData(nurbssurfaces[i]);
				int uCount = nurbssurfaces[i].getUCount();
				int uDegree = nurbssurfaces[i].getUDegree();
				boolean uClamp = true;
				boolean uPeriodic = nurbssurfaces[i].isUPeriodic();
				int vDegree = nurbssurfaces[i].getVDegree();
				boolean vClamp = true;
				boolean vPeriodic = nurbssurfaces[i].isVPeriodic();
				
				
				if(data != null) {
					float[] d = new float[data.length];
					for(int j = 0; j < data.length/4; j++) {
						d[4 * j] = data[j * 4];
						d[4 * j +1]	= data[j*4 + 2]; 
						d[4 * j +2]	= data[j*4 + 1];
						d[4 * j +3]	= data[j*4 + 3];
					}
					
					output += "\n\tBSplineSurfaceImpl surface" + i + " = new BSplineSurfaceImpl(); \n\tsurface" + i + " = surface" + i + ".create("
						+ "new float[] {" +data[0] ;
							
					for(int j = 1; j < data.length; j++) {
						output += ", " + data[j];
					}				
					
					output += "}, " +uCount + ", " + dim + ", " +uDegree + ", " + uClamp + ", " + uPeriodic + ", " 
																+vDegree + ", " + vClamp + ", " + vPeriodic + "); \n\t";
					output += "surface" + i + ".setRational("+rational+");\n";		
				}
			}
			
			
			for(int k = 0; k < boxes; k++) {
				Box object = getQuads()[k];
				output += "\n\tBox b"+k+" = new Box(" + object.getLength() + ", " + object.getWidth() + ", " + object.getHeight() + "); ";
			}
			for(int k = 0; k < cones; k++) {
				Cone object = getCones()[k];
				output += "\n\tCone c"+k+" = new Cone(" +object.getLength() + ", " + object.getRadius() + "); ";
			}
			for(int k = 0; k < spheres; k++) {
				Sphere object = getSpheres()[k];
				output += "\n\tSphere s"+k+" = new Sphere(" + object.getRadius() + "); ";
			}
			for(int k = 0; k < cylinders; k++) {
				Cylinder object = getCylinders()[k];
				output += "\n\tCylinder cyl"+k+" = new Cylinder(" + object.getLength() + ", " + object.getRadius() + "); ";
			}
			for(int k = 0; k < frustums; k++) {
				Frustum object = getFrustums()[k];
				output += "\n\tFrustum f"+k+" = new Frustum(" + object.getLength() + ", " + object.getBaseRadius() + ", " + object.getTopRadius() +  "); ";
			}
			
			output+= "}\n\tA(x) ==> ";
			
			for(int i = 0; i < curves; i++) {
				if(getCurveData(nurbscurve[i]) != null) {
					if( i == 0 && first) {
						output += "\n\tNURBSCurve(curve" + i + ")";
						first = false;
					}else {
						output += "\n\t^NURBSCurve(curve" + i + ")";
					}
				}
			}
			for(int i = 0; i < surfaces; i++) {
				if(getSurfaceData(nurbssurfaces[i]) != null) {
					if( i == 0 && first) {
						output += "\n\tNURBSSurface(surface" + i + ")";
						first = false;
					}else {
						output += "\n\t^NURBSSurface(surface" + i + ")";
					}
				}
			}
			for(int k = 0; k < boxes; k++) {
				Point3f coordinates = getQuadCoordinates()[k];
				if(k == 0 && first) {
					output += "\n\tTranslate(" + coordinates.x+ ", " + coordinates.y + ", " + coordinates.z + ")";
					first = false;
				}else {
					output += "\n\t^Translate(" + coordinates.x+ ", " + coordinates.y + ", " + coordinates.z + ")";
				}
				output += "\tb"+k;
			}
			for(int k = 0; k < cones; k++) {
				Point3f coordinates = getConeCoordinates()[k];
				if(k == 0 && first) {
					output += "\n\tTranslate(" + coordinates.x+ ", " + coordinates.y + ", " + coordinates.z + ")";
					first = false;
				}else {
					output += "\n\t^Translate(" + coordinates.x+ ", " + coordinates.y + ", " + coordinates.z + ")";
				}
				output += "\tc"+k;
			}
			for(int k = 0; k < spheres; k++) {
				Point3f coordinates = getSphereCoordinates()[k];
				if(k == 0 && first) {
					output += "\n\tTranslate(" + coordinates.x+ ", " + coordinates.y + ", " + coordinates.z + ")";
					first = false;
				}else {
					output += "\n\t^Translate(" + coordinates.x+ ", " + coordinates.y + ", " + coordinates.z + ")";
				}
				output += "\ts"+k;	
			}
			for(int k = 0; k < cylinders; k++) {
				Point3f coordinates = getCylinderCoordinates()[k];
				if(k == 0 && first) {
					output += "\n\tTranslate(" + coordinates.x+ ", " + coordinates.y + ", " + coordinates.z + ")";
					first = false;
				}else{
					output += "\n\t^Translate(" + coordinates.x+ ", " + coordinates.y + ", " + coordinates.z + ")";
				}
				output += "\tcyl"+k;
			}
			for(int k = 0; k < frustums; k++) {
				Point3f coordinates = getFrustumCoordinates()[k];
				if(k == 0 && first) {
					output += "\n\tTranslate(" + coordinates.x+ ", " + coordinates.y + ", " + coordinates.z + ")";
					first = false;
				}else {
					output += "\n\t^Translate(" + coordinates.x+ ", " + coordinates.y + ", " + coordinates.z + ")";
				}
				output += "\tf"+k;	
			}	
			
			output += ";\n] ";	
		}
		return output;
    }
    
    public static boolean isValidRotation(int yRotation, int zRotation) {
    	boolean rotation = false;
    	
    	if((yRotation == 0 || Math.abs(yRotation) == 90 || Math.abs(yRotation) == 180) && (zRotation == 0 || Math.abs(zRotation) == 90 || Math.abs(zRotation) == 180)) {
    		rotation =true;
    	}
    	return rotation;
    }
    
    /*
     * determines if two Points a and b are close by comparing two of the three coordinates depending on the Rotation
     */
    public static boolean comparePoints(Point3f a, Point3f b, int yRotation, int zRotation) {
    	boolean isNear = false;
    	float range = 0.2f;
    	
    	if(a != null && b != null) {
    		if((yRotation == 0 || Math.abs(yRotation) == 180) && (zRotation == 0 || Math.abs(zRotation) == 180)) {
    			if((a.y > (b.y - range) && a.y < (b.y + range)) && (a.z > (b.z - range) && a.z < (b.z + range))){
    				isNear = true;
    			}
    		}
    		if(Math.abs(yRotation) == 90) {
    			if((a.x > (b.x - range) && a.x < (b.x + range)) && (a.y > (b.y - range) && a.y < (b.y + range))){
    				isNear = true;
    			}
    		}
    		if((yRotation == 0 || Math.abs(yRotation) == 180) && Math.abs(zRotation) == 90) {
    			if((a.x > (b.x - range) && a.x < (b.x + range)) && (a.z > (b.z - range) && a.z < (b.z + range))){
    				isNear = true;
    			}
    		}
    		
    	}
    	return isNear;
    }
    
    //calculate distance between Points a and b on a plane parallel to view
    public static Vector3f calculateDistance(Point3f a, Point3f b, int yRotation, int zRotation) {
    	Vector3f distance = null;
	   	if(a != null && b != null) {
    		if((yRotation == 0 || Math.abs(yRotation) == 180) && (zRotation == 0 || Math.abs(zRotation) == 180)) {
    			distance = new Vector3f(0, a.y  - b.y, a.z - b.z);
    		}
    		if(Math.abs(yRotation) == 90) {
    			distance = new Vector3f(a.x - b.x, a.y - b.y, 0);
    		}
    		if((yRotation == 0 || Math.abs(yRotation) == 180) && Math.abs(zRotation) == 90) {
    			distance = new Vector3f(a.x - b.x, 0, a.z - b.z);
    		}
    		
    	}
    	return distance;
	}    	
 
    
    private static boolean mousePressed;
	public static void isMousePressed(boolean b) {
		mousePressed = b;
	}

    
    public static void mouseWheelAction(Point3f source, int rotation, int yRotation, int zRotation) {
    	Point4f comp = new Point4f();
    	
    	
   	   	//compare mouseInput with every control point of every curve
    	for(int j = 0; j < getCurveCounter(); j++) {
 			NURBSCurve curve = getNURBSCurve(j);
 			//set weight if mouse is near control point
 			for(int i = 0; i < getCurveSize(curve); i++) {
 				comp = getCurvePoint(curve, i);
 	        	if(comparePoints(source, new Point3f(comp.x, comp.y, comp.z), yRotation, zRotation)) {
 	        		//set weight for current controlpoint 
 					int w = rotation;
 					setCurveWeight(curve, i, w);
         		}
 	        }
 			//check if mouseInput was near center of curve
 			comp = getCurveCenter(curve);
 			if(comparePoints(source, new Point3f(comp.x, comp.y, comp.z), yRotation, zRotation)){
 				//scale curve if mouse is pressed
 				if(mousePressed == true) {
	 				//Scaling factor
	     			float scale = 1 + (float) rotation/10.0f; 
	     			//scale every point of the surface
	     			for(int i = 0; i < getCurveSize(curve); i++) {
	     				Point4f tmp = getCurvePoint(curve, i);
	     				tmp.x = comp.x + ((tmp.x - comp.x) * scale); 
	     				tmp.y = comp.y + ((tmp.y - comp.y) * scale);
	     				tmp.z = comp.z + ((tmp.z - comp.z) * scale);
	     				setCurvePoint(curve, new Point4f(tmp.x, tmp.y, tmp.z, tmp.w), i);
	     			}	
 				}else {
 					//rotate curve around center
 					float angle = (float) rotation/10.0f;
 					for(int i = 0; i < getCurveSize(curve); i++) {
 						Point3f newP = new Point3f();
 						Point3f phi = new Point3f();
 						Point4f oldP = getCurvePoint(curve, i);
 						
 						
 						if((yRotation == 0 || Math.abs(yRotation) == 180) && (zRotation == 0 || Math.abs(zRotation) == 180)) {
 							phi.y = oldP.y - comp.y;
 							phi.z = oldP.z - comp.z;
 							
 							newP.y = (float) (phi.y * Math.cos(angle) + phi.z * Math.sin(angle));
 							newP.z = (float) (phi.z * Math.cos(angle) - phi.y* Math.sin(angle));
 							newP.x = oldP.x;
 							newP.y = newP.y + comp.y;
 							newP.z = newP.z + comp.z;
 						}else if(Math.abs(yRotation) == 90) {
 							phi.x = oldP.x - comp.x;
 							phi.y = oldP.y - comp.y;
 							
 							newP.x = (float) (phi.x * Math.cos(angle) - phi.y* Math.sin(angle));
 							newP.y = (float) (phi.y * Math.cos(angle) + phi.x * Math.sin(angle));
 							newP.x = newP.x + comp.x;
 							newP.y = newP.y + comp.y;
 							newP.z = oldP.z;
 						}else if((yRotation == 0 || Math.abs(yRotation) == 180) && Math.abs(zRotation) == 90) {
							phi.x = oldP.x - comp.x;
							phi.z = oldP.z - comp.z;
							
							newP.x = (float) (phi.x * Math.cos(angle) - phi.z* Math.sin(angle));
							newP.z = (float) (phi.z * Math.cos(angle) + phi.x * Math.sin(angle));
							newP.x = newP.x + comp.x;
							newP.z = newP.z + comp.z;
							newP.y = oldP.y;
						}
 						setCurvePoint(curve, new Point4f(newP.x, newP.y, newP.z, oldP.w), i);
 					}
 				}
 	    	}
 		}
    	//check if mouseInput was near control point of surface
    	for(int k = 0; k < getSurfaceCounter(); k++) {
 			BSplineSurfaceImpl surface = getNURBSSurface(k);
        
 			for(int i = 0; i < getSurfaceSize(surface); i++) {
 				comp = getSurfacePoint(surface, i);
 	        	if(comparePoints(source, new Point3f(comp.x, comp.y, comp.z), yRotation, zRotation)) {
 	        		//set weight for current controlpoint 
 					int w = rotation;
 					setSurfaceWeight(surface, i, w);
         		}
 	        }
 			
 			//check if mouseInput was near center of surface
 			comp = getSurfaceCenter(surface);
         	if(comparePoints(source, new Point3f(comp.x, comp.y, comp.z), yRotation, zRotation)) {
     			//scale surface if mousePressed
         		if(mousePressed == true) {
	         		//Scaliung factor
	     			float scale = 1 + (float) rotation/10.0f; 
	     			//scale every point of the surface
	     			for(int i = 0; i < getSurfaceSize(surface); i++) {
	     				Point4f tmp = getSurfacePoint(surface, i);
	     				tmp.x = comp.x + ((tmp.x - comp.x) * scale); 
	     				tmp.y = comp.y + ((tmp.y - comp.y) * scale);
	     				tmp.z = comp.z + ((tmp.z - comp.z) * scale);
	     				setSurfacePoint(surface, new Point4f(tmp.x, tmp.y, tmp.z, tmp.w), i);
	     			}	
	     			
     			}else {
     				//rotate surface around center
     				float angle = (float) rotation / 10.0f;
				
					//rotate every point around center
					for(int i = 0; i < getSurfaceSize(surface); i++) {
						Point3f newP = new Point3f();
						Point3f phi = new Point3f();
						Point4f oldP = getSurfacePoint(surface, i);
						
 						if((yRotation == 0 || Math.abs(yRotation) == 180) && (zRotation == 0 || Math.abs(zRotation) == 180)) {
							phi.y = oldP.y - comp.y;
							phi.z = oldP.z - comp.z;
							
							newP.y = (float) (phi.y * Math.cos(angle) + phi.z * Math.sin(angle));
							newP.z = (float) (phi.z * Math.cos(angle) - phi.y* Math.sin(angle));
							newP.x = oldP.x;
							newP.y = newP.y + comp.y;
							newP.z = newP.z + comp.z;
						}else if(Math.abs(yRotation) == 90) {
							phi.x = oldP.x - comp.x;
							phi.y = oldP.y - comp.y;
							
							newP.x = (float) (phi.x * Math.cos(angle) - phi.y* Math.sin(angle));
							newP.y = (float) (phi.y * Math.cos(angle) + phi.x * Math.sin(angle));
							newP.x = newP.x + comp.x;
							newP.y = newP.y + comp.y;
							newP.z = oldP.z;
						}else if((yRotation == 0 || Math.abs(yRotation) == 180) && Math.abs(zRotation) == 90) {
							phi.x = oldP.x - comp.x;
							phi.z = oldP.z - comp.z;
							
							newP.x = (float) (phi.x * Math.cos(angle) - phi.z* Math.sin(angle));
							newP.z = (float) (phi.z * Math.cos(angle) + phi.x * Math.sin(angle));
							newP.x = newP.x + comp.x;
							newP.z = newP.z + comp.z;
							newP.y = oldP.y;
						}
						setSurfacePoint(surface, new Point4f(newP.x, newP.y, newP.z, oldP.w), i);
					}	
     			}
			}
		}
    }
    
    
    public static Box[] getQuads(){
		return quads;
	}
	public static Cone[] getCones(){
		return cones;
	}
	public static Sphere[] getSpheres(){
		return spheres;
	}
	public static Cylinder[] getCylinders(){
		return cylinders;
	}
	public static Frustum[] getFrustums(){
		return frustum;
	}
	
	public static Point3f[] getQuadCoordinates(){
		return quadCoordinates;
	}
	public static Point3f[] getConeCoordinates(){
		return coneCoordinates;
	}
	public static Point3f[] getSphereCoordinates(){
		return sphereCoordinates;
	}
	public static Point3f[] getCylinderCoordinates(){
		return cylinderCoordinates;
	}
	public static Point3f[] getFrustumCoordinates(){
		return frustumCoordinates;
	}
	
    
    public static int getQCounter() {
    	return qIndex;
    }
    public static void setQCounter(int c) {
    	qIndex = c;
    }
    public static int getCCounter() {
    	return cIndex;
    }
    public static void setCCounter(int c) {
    	cIndex = c;
    }
    public static int getSCounter() {
    	return sIndex;
    }
    public static void setSCounter(int c) {
    	sIndex = c;
    }
    public static int getCylCounter() {
    	return cylIndex;
    }
    public static void setCylCounter(int c) {
    	cylIndex = c;
    }
    public static int getFCounter() {
    	return fIndex;
    }
    public static void setFCounter(int c) {
    	fIndex = c;
    }
    
    private static int qIndex = 0;
    private static int cIndex = 0;
    private static int sIndex = 0;
    private static int cylIndex = 0;
    private static int fIndex = 0;
    
    //Initialize primitive object depending on selection 
    public static void initObject(String type, Point3f p){
    	if(type =="Box") {
    		if(qIndex < MAX_COUNT) {
    		quads[qIndex] = new Box(1, 1, 1);   
    		quadCoordinates[qIndex] = p;
   			qIndex++;
    		}
    	}else if(type =="Cone") {
    		if(qIndex < MAX_COUNT) {
    			cones[cIndex] = new Cone(1, 0.5f);
       			coneCoordinates[cIndex] = p;   
   				cIndex++;
    		}
    	}else if(type =="Sphere") {
    		if(qIndex < MAX_COUNT) {
    			spheres[sIndex] = new Sphere(0.5f);
    			sphereCoordinates[sIndex] = p; 
    			sIndex++;
    		}
    	}else if(type =="Cylinder") {
    		if(qIndex < MAX_COUNT) {
	    		cylinders[cylIndex] = new Cylinder(1, 0.5f);
	    		cylinderCoordinates[cylIndex] = p;   
	   			cylIndex++;
	    	}
    	}else if(type =="Frustrum") {
    		if(qIndex < MAX_COUNT) {
	    		frustum[fIndex] = new Frustum(2, 1, 0.5f);
				frustumCoordinates[fIndex] = p;   
	   			fIndex++;
    		}
    	}	
	}    
}
