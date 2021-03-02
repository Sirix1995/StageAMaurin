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
package de.grogra.nurbseditor2d;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;

import de.grogra.imp3d.objects.NURBSCurve;
import de.grogra.imp3d.objects.Polygon;
import de.grogra.math.BSplineCurve;
import de.grogra.math.BSplineOfVertices;
import de.grogra.math.VertexListImpl;

public class ObjectGeometry2D {
	private static final int MAX_COUNT = 50;
	private static NURBSCurve[] nurbscurve = new NURBSCurve[MAX_COUNT];
	private static Polygon [] rectangles = new Polygon[MAX_COUNT];
	private static Polygon [] triangles = new Polygon[MAX_COUNT];
	private static Point2f[][] circles = new Point2f[MAX_COUNT][2];
	private static int CurveCounter = 0;
	
	public static Polygon[] getRectangles(){
		return rectangles;
	}
	public static Polygon[] getTriangles(){
		return triangles;
	}
	public static Point2f[][] getCircles(){
		return circles;
	}
	
	public static int getCurveCounter() {
		return CurveCounter;
	}
	
	public static void setCurveCounter(int c) {
		CurveCounter = c;
	}
	
	public static void deleteCurve() {
		int index = getCurveCounter();
		if(index > 0) {
			setCurveData(nurbscurve[index-1], null);
			if(index > 1) {	   
				setCurveCounter(index-1);
			}
		}
	}
	
	public static void deleteRectangle() {
		int index = getRCounter();
		if(index > 0) {
			rectangles[index-1] = null;
			setRCounter(index-1);
		}
	}
	public static void deleteTriangle() {
		int index = getTCounter();
		if(index > 0) {
			triangles[index-1] = null;
			setTCounter(index-1);
		}
	}
	public static void deleteCircle() {
		int index = getCCounter();
		if(index > 0) {
			circles[index-1][0] = null;
			circles[index-1][1] = null;
			setCCounter(index-1);
		}
	}
	
	
	public static void addCurve(NURBSCurve curve) {
		int index = getCurveCounter();
		nurbscurve[index] = curve;
		setCurveCounter(index+1);
	}
	
	public static void initCurve() {
		nurbscurve[CurveCounter] = new NURBSCurve();
		CurveCounter++;
	}
		
	public static NURBSCurve getNURBSCurve(int index) {
		return nurbscurve[index];
	}
	
	public static void setWeight(NURBSCurve curve, int index, int weight) {
		Point3f p = getPoint(curve, index);
		if(weight < 0) {
			if(p.z > 0.1f) {
				p.z += (float)weight/10.0f;
			}
		}else {
			p.z += (float)weight/10.0;
		}
		setPoint(curve, p, index);	
	}
	
	public static float getWeight(NURBSCurve curve, int index) {
		float w;
			float [] data  = getCurveData(curve);
			w = data[3*index + 3];
		return w;
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
    
    public static int getSize(NURBSCurve nurbs) {
    	int size = 0;
    	float data[] = getCurveData(nurbs);
		if(data != null) {
			size = data.length / 3;
		}
    	return size;
    }
    
    
    public static void addPoint(Point3f p) {
    	int curveIndex = ObjectGeometry2D.getCurveCounter() - 1;
		VertexListImpl vertexlist = new VertexListImpl();
		int size;
		float [] oldData= getCurveData(nurbscurve[curveIndex]);
		if(oldData == null) {
			size = 0;
		}
		else {
			size = oldData.length;
		}
		float [] data = new float[size + 3];
		for(int i = 0; i < data.length-3; i++) {
			data[i] = oldData[i];   
		}
		
		data[data.length-3] = (float) p.x;
		data[data.length-2] = (float) p.y;
		data[data.length-1] = (float) p.z;
		
		vertexlist.setData(data);
		vertexlist.setDimension(3);
		BSplineOfVertices bspline = new BSplineOfVertices(vertexlist, 3, false, false);
		bspline.setRational(true);
		nurbscurve[curveIndex].setCurve(bspline);
    }
    
    
    public static void setPoint(NURBSCurve nurbs, Point3f p, int index){
    	float [] data = getCurveData(nurbs);
    	if(data != null) {
    		data[3*index] = p.x;
    		data[3*index + 1] = p.y;
    		data[3*index + 2] = p.z;
    		setCurveData(nurbs, data);
    	}
    }
    
    public static Point3f getPoint(NURBSCurve nurbs, int i) {
    	Point3f point = new Point3f();
    	float[] data = getCurveData(nurbs);
		if(data != null) {
			point.x = data[3 * i];
			point.y = data[3 * i + 1];
			point.z = data[3 * i + 2];
		}
		return point;
    }
    
    
    public static Point3f getCenter(NURBSCurve nurbs) {
    	Point3f center = new Point3f();
    	
    	//calculate geometric center of curve
			float sumX = 0;
			float sumY = 0;
			float sumZ = 0;
			for(int i = 0; i < getSize(nurbs); i++) {
				sumX += getPoint(nurbs, i).x;
				sumY += getPoint(nurbs, i).y;
				sumZ += getPoint(nurbs, i).z;
			}
			float mittelX = 1.0f /(float) getSize(nurbs) * sumX;
			float mittelY = 1.0f /(float) getSize(nurbs) * sumY;
			float mittelZ = 1.0f /(float) getSize(nurbs) * sumZ;
			center.set(mittelX, mittelY, mittelZ);
    	return center;
    }
    
    private static float calculateDistance(Point2f a, Point2f b) {
    	float distance = (float) Math.sqrt(Math.pow(a.x - b.x,2) + Math.pow(a.y - b.y, 2));
    	return distance;
    }
    
    private static boolean mousePressed;
	public static void isMousePressed(boolean b) {
		mousePressed = b;
	}

    
    public static void mouseWheelAction(Point2f source, int rotation) {
    	Point3f comp = new Point3f();
    	float s = 0.2f;
    	
    	//compare mouseInput with every control point of every curve
        for(int j = 0; j < getCurveCounter(); j++) {
			NURBSCurve curve = getNURBSCurve(j);
			
			//set weight if mouse is near control point
	        for(int i = 0; i < getSize(curve); i++) {
				comp = getPoint(curve, i);
	
	        	if(source.x > (comp.x - s) && source.x < (comp.x + s)){
	        		if(source.y > (comp.y - s) && source.y < (comp.y + s)){
	        		//change weight 
	        		setWeight(curve, i, rotation);
	        		}
	        	}
	        }
		
	        
			comp = getCenter(curve);
			//check if mouseInput was near center of curve
			if(source.x > (comp.x - s) && source.x < (comp.x + s)){
		    	if(source.y > (comp.y - s) && source.y < (comp.y + s)){
		    		
		    		//scale curve if mouse is pressed
		    		if(mousePressed == true) {
		    			//scaling factor
		    			float scale = 1 + (float) rotation/10.0f; 
		    			//scale points
		    			for(int i = 0; i < getSize(curve); i++) {
		    				Point3f tmp = getPoint(curve, i);
		    				tmp.x = comp.x + ((tmp.x - comp.x) * scale); 
		    				tmp.y = comp.y + ((tmp.y - comp.y) * scale);
		    				setPoint(curve, new Point3f(tmp.x, tmp.y, tmp.z), i);
		    			}		
		    		}else {
		    			//rotate curve points around center 
			    		float angle = (float) rotation/10.0f;
	
						for(int i = 0; i < getSize(curve); i++) {
							Point2f newP = new Point2f();
							Point3f oldP = getPoint(curve, i);
			
							oldP.x = oldP.x - comp.x;
							oldP.y = oldP.y - comp.y;
							
							newP.x = (float) (oldP.x * Math.cos(angle) - oldP.y* Math.sin(angle));
							newP.y = (float) (oldP.y * Math.cos(angle) + oldP.x * Math.sin(angle));
							newP.x = newP.x + comp.x;
							newP.y = newP.y + comp.y;
							setPoint(curve, new Point3f(newP.x, newP.y, (getPoint(curve, i)).z), i);
						}
		    		}
		    	}
			}
		}
    }
    
    
    public static String createRGGText() {
    	int count = ObjectGeometry2D.getCurveCounter();
    	String output = "";
    	int dim = 3;
		int degree = 3;
		boolean periodic = false;
		boolean bezier = false;
		boolean first = true;
	
		int curves = getCurveCounter();
		int quad = getRCounter();
		int triangles = getTCounter();
		int circles = getCCounter();

    	if( curves > 0 || quad > 0 || triangles > 0 || circles > 0) {
		
			output += "module A(float len) extends Sphere(0.1);\n\nprotected void init ()[\n\t" + 
							"Axiom ==> A(1);\n]\n\npublic void start ()[\n{";
			
			for(int i = 0; i < count; i++) {
				float[] data = getCurveData(nurbscurve[i]);
				if(data != null) {
					output += "\n\tBSplineOfVertices spline" + i + " = new BSplineOfVertices("
							+ " new VertexListImpl(new float[] {" + data[0] ;
							
					for(int j = 1; j < data.length; j++) {
						output += ", " + data[j];
					}
					output += "}, " + dim + "), " + degree + ", " + periodic + ", " + bezier + "); \n\t";
					output += "spline" + i + ".setRational(true);\n\tNURBSCurve curve" + i + " = new NURBSCurve(spline" + i + ");";		
				}
			}
			for(int k = 0; k < ObjectGeometry2D.getRCounter(); k++) {
				Polygon p = getRectangles()[k];
				VertexListImpl vertices = (VertexListImpl) p.getVertices();
				float[] data = vertices.getData();
				output += "\n\tPolygon rectangle"+k+" = new Polygon( new VertexListImpl(new float[] {" + data[0];
				for(int i = 1; i < data.length; i ++) {
					output += ", " + data[i];
				}			
				output += "}, 2)); ";
			}
			for(int k = 0; k < ObjectGeometry2D.getTCounter(); k++) {
				Polygon p = getTriangles()[k];
				VertexListImpl vertices = (VertexListImpl) p.getVertices();
				float[] data = vertices.getData();
				output += "\n\tPolygon triangle"+k+" = new Polygon(new VertexListImpl(new float[] {" + data[0];
				for(int i = 1; i < data.length; i ++) {
					output += ", " + data[i];
				}			
				output += "}, 2)); ";
			}
			for(int k = 0; k < ObjectGeometry2D.getCCounter(); k++) {
				Point2f [] p = getCircles()[k];
				output += "\n\tCircle circle"+k+" = new Circle(" + calculateDistance(p[0], p[1]) + "); ";
			}
			
			output+= "\n}\n\tA(x) ==> ";
			
			for(int i = 0; i < count; i++) {
				if(i == 0 && first) {
				output += "\n\tcurve" + i;
				first = false;
				}else {
					output += "\n\t^curve" + i;
				}
			}
			for(int k = 0; k < ObjectGeometry2D.getRCounter(); k++) {
				if(k == 0 && first) {
					output += "\n\trectangle"+k;
					first = false;
				}else {
					output += "\n\t^rectangle"+k;
				}
			}
			for(int k = 0; k < ObjectGeometry2D.getTCounter(); k++) {
				if(k == 0 && first) {
					output += "\n\ttriangle"+k;
					first = false;
				}
				else {
					output += "\n\t^triangle"+k;
				}
			}
			for(int k = 0; k < ObjectGeometry2D.getCCounter(); k++) {
				Point2f [] params = getCircles()[k];
				if(k == 0 && first) {
				
				output += "\n\tTranslate(" + (float)params[0].x+ ", " + (float)params[0].y + ", 0)";
				output += "\tcircle"+k;	
				first = false;
				}
				else {
					output += "\n\t^ Translate(" + (float)params[0].x+ ", " + (float)params[0].y + ", 0)";
					output += "\tcircle"+k;	
					}
			}			
			output += ";\n] ";	
		}
		return output;
    }	
    
    public static int getRCounter() {
    	return rIndex;
    }
    public static void setRCounter(int c) {
    	rIndex = c;
    }
    public static int getTCounter() {
    	return tIndex;
    }
    public static void setTCounter(int c) {
    	tIndex = c;
    }
    public static int getCCounter() {
    	return cIndex;
    }
    public static void setCCounter(int c) {
    	cIndex = c;
    }
    
    static int r = 0;
    static int t = 0;
    static int c = 0;
    private static int rIndex = 0;
    private static int tIndex = 0;
    private static int cIndex = 0;
    private static float rectangleData[] = new float[8];
    private static float triangleData[] = new float[6];
    
    /*
     * Initializes new primitive objects depending on RadioButton 
     * object will only be initialized when all corner points are marked
     */
    public static void initObject(String type, Point2f p){
    	if(type =="Rectangle") {
    		t = 0;
    		c = 0;
    		rectangleData[2*r] = p.x;
    		rectangleData[2*r+1] = p.y;
    		r++;
    		if(r % 4 == 0) {
    			r = 0;
    		    VertexListImpl vertices = new VertexListImpl(rectangleData, 2);
    			rectangles[rIndex] = new Polygon(vertices);
    			rIndex++;
    			rectangleData = new float[8];
    		}
    	}else if(type =="Triangle") {
    		r = 0;
    		c = 0;
        	triangleData[2* t] = p.x;
    		triangleData[2*t+1] = p.y;
        	t++;
        	if(t % 3 == 0) {
        		t = 0;
        		VertexListImpl vertices = new VertexListImpl(triangleData, 2);
        		triangles[tIndex] = new Polygon(vertices);
        		tIndex++;
        		triangleData = new float[6];
        	}    		
    	}else if(type =="Circle") {
    		r = 0;
    		t = 0;
    		circles[cIndex][c] = p;   
    		c++;
    		if(c % 2 == 0) {
    			c = 0;
    			cIndex++;
    		}
    	}
	}
}