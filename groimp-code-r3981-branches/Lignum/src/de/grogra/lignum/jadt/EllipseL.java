/*
 * Copyright (C) 2016 GroIMP Developer Team
 *
 * Department Ecoinformatics, Biometrics and Forest Growth,
 * University of Göttingen, Germany
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */ 

package de.grogra.lignum.jadt;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.Vector;

import javax.vecmath.Point3d;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class EllipseL implements Shape,Mathsym {

	private Point3d center = new Point3d();
	private PositionVector normal = new PositionVector(0,0,0);
	private PositionVector xdir = new PositionVector(0,0,0);
	private PositionVector ydir = new PositionVector(0,0,0);
	double semimajoraxis;
	double semiminoraxis;
	
	public EllipseL(Point3d center0, PositionVector normal0, double semimajoraxis0, 
			       double semiminoraxis0){
		
		center = center0;
		normal = normal0;
		semimajoraxis = semimajoraxis0;
		semiminoraxis = semiminoraxis0;
	}
	
	public EllipseL(Point3d petiole_end, PositionVector petiole_dir, 
			       PositionVector normal0, double semimajoraxis0,
			       double semiminoraxis0) {
		
		normal.set(normal0);
		semimajoraxis = semimajoraxis0;
		semiminoraxis = semiminoraxis0;
		
		PositionVector v = new PositionVector();
		v.cross(normal0, petiole_dir);
		PositionVector x1 = new PositionVector(normal0);
		Point3d p = new Point3d(0,0,0);
		x1.rotate(p,v,PI/2.0);
		x1.normalize();
		
		PositionVector y1 = new PositionVector();
		y1.cross(normal, x1);
		y1.normalize();
		PositionVector dummyx1 = new PositionVector(x1);
		dummyx1.mul(semimajoraxis0);
		PositionVector petiolecenter = new PositionVector(dummyx1);
		PositionVector center0 = new PositionVector(petiole_end);
		center0.add(petiolecenter);
		center.set(center0);
		
		xdir = x1;
		ydir = y1;
		
	}
	
	public EllipseL(Point3d petiole_end, PositionVector normal0, PositionVector xdir0,
			PositionVector ydir0, double semimajoraxis0, double semiminoraxis0){
		
		normal.set(normal0);
		xdir.set(xdir0);
		ydir.set(ydir0);
		semimajoraxis = semimajoraxis0;
		semiminoraxis = semiminoraxis0;
		
		PositionVector petiolecenter = new PositionVector();
		petiolecenter.mul(semimajoraxis,xdir);
		PositionVector center0 = new PositionVector(petiole_end);
		center0.add(petiolecenter);
		center.set(center0);
		
	}
	
	public EllipseL(PositionVector p, PositionVector n, PositionVector x1,
			double semimajoraxis0, double semiminoraxis0){
		
		normal.set(n);
		semimajoraxis = semimajoraxis0;
		semiminoraxis = semiminoraxis0;
		
		xdir.set(x1);
		xdir.normalize();
		ydir.cross(xdir, normal);
		ydir.normalize();
		PositionVector petiolecenter = new PositionVector();
		petiolecenter.mul(semimajoraxis,xdir);
		PositionVector center0 = new PositionVector(p);
		center0.add(petiolecenter);
		center.set(center0);
	}
	//In cLignum there is some kind of call to Shape...
	public EllipseL(EllipseL e){
		center.set(e.center);
		normal.set(e.normal);
		xdir.set(e.xdir);
		ydir.set(e.ydir);
		semimajoraxis= e.semimajoraxis;
		semiminoraxis = e.semiminoraxis;
	}
	
	//Set method used instead of = :
	public void set(EllipseL e) {
		center.set(e.center);
		normal.set(e.normal);
		xdir.set(e.xdir);
		ydir.set(e.ydir);
		semimajoraxis= e.semimajoraxis;
		semiminoraxis = e.semiminoraxis;
	}
	
	//Original Comment:
	//unit vector x-axis in the ellipse plane
	public PositionVector x1u() {
		
		return xdir;
	}
	
	//Original Comment:
	//unit vector y-axis in the ellipse plane
	public PositionVector y1u() {
		
		return ydir;
	}
	
	public Point3d getSemimajorAxisPoint() {
		Point3d p = new Point3d();
		x1u().mul(semimajoraxis);
		p.set(x1u());
		p.add(center);
		return p;
				
	}
	
	public Point3d getSemiminorAxisPoint() {
		Point3d p = new Point3d();
		y1u().mul(semimajoraxis);
		p.set(y1u());
		p.add(center);
		return p;
				
	}
	
	public Point3d getCenterPoint() {
		return center;
	}
	
	public PositionVector getNormal() {
		return normal;
	}
	
	public double getSemimajorAxis() {
		return semimajoraxis;
	}
	
	public double getSemiminorAxis() {
		return semiminoraxis;
	}
	
	public void setCenterPoint(Point3d center0) {
		center.set(center0);
	}
	
	public void setSemimajorAxis(double semimajoraxis0) {
		semimajoraxis = semimajoraxis0;
	}
	
	public void setSemiminorAxis(double semiminoraxis0) {
		semiminoraxis = semiminoraxis0;
	}
	
	//Original Comment:
	//the ellipse area calculation
	public double getArea() {
		return semimajoraxis*semiminoraxis*PI;
	}
	
	//Original Comment:
	//Rotate ellipse around major axis by angle angle
	public void roll(double angle) {
		
		Point3d p= new Point3d(0.0,0.0,0.0);
		normal.rotate(p, xdir, angle);
		normal.normalize();
		ydir.rotate(p, xdir, angle);
		ydir.normalize();
	}
	
	//Original Comment:
	//Rotate ellipse around minor axis by angle angle
    //Axis of rotation is at end 
	//of petiole (at intersection of perimeter and major axis)
	public void pitch(double angle) {
		
		//Original Comment:
		//rotation around axis that goes through point where major axis
	    //intersects with perimeter ( -semimajoraxis*xdir = end of
	    //petiole, presumably) and is parallel with semiminoraxis =>
	    //center of ellipse is changed.
	    // Normal, xdir are just rotated
	 
	    //Position of point of rotation at perimeter
		Point3d start = new Point3d();
		xdir.mul(semimajoraxis);
		start.set(xdir);
		start.negate();
		start.add(center);
		
		//Original Comment:
		//vector pointing from axis of rotation to center
		PositionVector cc = new PositionVector(start);
		cc.negate();
		cc.add(center);
		
		Point3d p = new Point3d(0.0,0.0,0.0);
		
		normal.rotate(p, ydir, angle);
		xdir.rotate(p, ydir, angle);
		normal.normalize();
		xdir.normalize();
		cc.rotate(p, ydir, angle);
		
		center.set(start);
		center.add(cc);
		
	}
	//Original Comment:
	 //Rotate ellipse around normal by angle angle
    //Axis of rotation is at end of petiole (at intersection 
	//of perimeter and major axis)
	public void turn(double angle) {
		//Original Comment:
		 //rotation around axis that goes through point where major axis
	    //intersects with perimeter ( -semimajoraxis*xdir = end of
	    //petiole, presumably) and is parallel with normal =>
	    //center of ellipse is changed
	    // xdir, ydir are just rotated
		
		//Position of point of rotation at perimeter
		Point3d start = new Point3d();
		xdir.mul(semimajoraxis);
		start.set(xdir);
		//start.mul(semimajoraxis, xdir);
		start.negate();
		start.add(center);
		
		//Original Comment:
		//vector pointing from axis of rotation to center
		PositionVector cc = new PositionVector(start);
		cc.negate();
		cc.add(center);
		
		Point3d p = new Point3d(0.0,0.0,0.0);
		
		xdir.rotate(p, normal, angle);
		ydir.rotate(p, normal, angle);
		xdir.normalize();
		ydir.normalize();
		cc.rotate(p, normal, angle);
		
		center.set(start);
		center.add(cc);
	}
	
	//getting the ellipse points using the ellipse equation
	//X=Center + a*cos(t)*X1u +b*sin(t)*Y1u
	//where t:[0,2pi] and the step=[2*pi/n];
	//The npoints parameter is defaulted as 50.
	
	public Vector<Point3d> getVertexVector(Vector<Point3d> points) {
		return this.getVertexVector(points, 50);
	}
	
	public Vector<Point3d> getVertexVector(Vector<Point3d> points, int npoints) {
		
		int i;
		double t;
		Point3d x = new Point3d();
		Point3d px = new Point3d();
		Point3d py = new Point3d();
		
		double step = 2.0 * PI/(npoints);
		
		for(i=0;i<npoints;i++){
			t = i*step;
			px.set(0,0,0);
			py.set(0,0,0);
			x.set(getCenterPoint());
			
			x1u().mul(getSemimajorAxis()*cos(t));
			px.set(x1u());
			y1u().mul(getSemiminorAxis()*sin(t));
			py.set(y1u());

			//py.mul((getSemiminorAxis()*sin(t)), y1u()); //alt
			x.add(px);
			x.add(py);
			//System.out.println(x + "Test");
			points.add(new Point3d(x));
					}
		return points;
		
	}
	
	//Original Comment:
	//Creation  a new ellipse from the old one 
	//using the new ellipse area
	//Scaling equation is X'=S*X
	//where X' - a new point
	//X  - a old point
	//S - the 4x4 matrix for scaling 
	//(see a book for the computer graphic)
	//The scaling coefficient scalcoef in our case
	//is calculated as
	//scalcoef=sqrt(areanew/areaold);
	
	//This case  with the base point as the scaling center 
	
	public double setArea(double area, Point3d base) {
		double areaold, areanew,scalcoef;
		double adbasex, adbasey, adbasez;
		int i;
		//Set the scaling around the center point
		setCenterPoint(base);
		
		areaold = getArea();
		areanew = area;
		
		scalcoef = sqrt(areanew/areaold);
		 
		adbasex= base.x*(1-scalcoef);
	    adbasey= base.y*(1-scalcoef);
	    adbasez= base.z*(1-scalcoef);
	   
	    Vector<Point3d> points = new Vector<Point3d>();
	    points.add(getSemimajorAxisPoint());
	    points.add(getSemiminorAxisPoint());
	    Point3d x = new Point3d();
	    Point3d p = new Point3d();
	    
	    for(i=0; i<points.size();i++){
	    	p.set(0,0,0);
	    	p.add(points.get(i));
	    	x.x = p.x *scalcoef +adbasex;
	    	x.y = p.y *scalcoef +adbasey;
	    	x.z = p.z *scalcoef +adbasez;
	    	
	    	switch(i){
	    	case 0:
	    		setSemimajorAxis(x.distance(getCenterPoint()));
	    		break;
	    	case 1:
	    		setSemiminorAxis(x.distance(getCenterPoint()));
	    	}	
	    }
	    
	    //Original Comment:
	    //Move  the scale  center (i.e.  the ellipse)  to the  new ellipse
	    //center
	    
	    PositionVector from_base_to_center = new PositionVector();
	    from_base_to_center.mul(getSemimajorAxis(), x1u());
	    center.set(base);
	    center.add(from_base_to_center);
	    return getArea();
	    
	}
	
	//This case  with the ellipse center as the scaling center 
	
	public double setArea(double area) {
		double areaold, areanew,scalcoef;
		double adbasex, adbasey, adbasez;
		int i;
		//Set the scaling around the center point
		
		
		areaold = getArea();
		areanew = area;
		
		scalcoef = sqrt(areanew/areaold);
		
		adbasex= getCenterPoint().x*(1-scalcoef);
	    adbasey= getCenterPoint().y*(1-scalcoef);
	    adbasez= getCenterPoint().z*(1-scalcoef);
	    
	    Vector<Point3d> points = new Vector<Point3d>();
	    points.add(getSemimajorAxisPoint());
	    points.add(getSemiminorAxisPoint());
	    Point3d x = new Point3d();
	    Point3d p = new Point3d();
	    
	    for(i=0; i<points.size();i++){
	    	p.set(0,0,0);
	    	p.add(points.get(i));
	    	x.x = p.x *scalcoef +adbasex;
	    	x.y = p.y *scalcoef +adbasey;
	    	x.z = p.z *scalcoef +adbasez;
	    	
	    	switch(i){
	    	case 0:
	    		System.out.println(x.distance(getCenterPoint()) + "Test");
	    		setSemimajorAxis(x.distance(getCenterPoint()));
	    		break;
	    	case 1:
	    		setSemiminorAxis(x.distance(getCenterPoint()));
	    		break;
	    	}	
	    }
	    
	    return getArea();
	}
	
	//Original Comment:
	//This method checks crossing a ellipse 
	//with a straight line in space, 
	//given by the O point(O - the first parameter the method) 
	//and direction B(B - the second parameter the method).
	
	public boolean intersectShape(Point3d O, PositionVector B0) {
		//Original Comment:
		//If the beam (starting from Point p0 with direction v) hits
	    //the ellipsis?
	    //It has its center at Point ps
	    //other properties are defined by BroadLeaf leaf, PosVector petiole
	
	    boolean NO_HIT = false;
	    boolean HIT_THE_FOLIAGE = true;
	    
	    //  Point pc =  GetCenterPoint(leaf);
	    Point3d pc = new Point3d(center);
		Point3d p0 = new Point3d(O);
		
		PositionVector v = new PositionVector(B0);
		
		//Original Comment:
		//1.      Rough testing, if point p0 is higher (z-axis=height) than
	    //pc + max width of the ellipsis, the beam
	    //cannot hit the ellipsis
	    //double a;
	    //a = (double)GetShape(leaf).getSemimajorAxis();
		
		double a = semimajoraxis;
		if(p0.z > pc.z +2.0 * a) {
			return NO_HIT;
		}
		
		//  PositionVector n  = GetShape(leaf).getNormal();
		
		PositionVector n = new PositionVector(normal);
		
		//2. Where does line starting at p0 and having direction b intersect the plane
	    //that contains the ellipsis leaf
		
		double vn = v.dot(n);
		
		if(max(vn,-vn)<R_EPSILON){
			return NO_HIT;
		}// if v * n = 0 => beam parallel with the plane; cannot
	    // we forget here the possibility that the beam is in the plane
		
		// 3. Calculate the point where beam hits the plane (=vector pointing to this point)

	    // 3.2 Calculate the hit point
		
		double u;
		PositionVector pc1 = new PositionVector(pc);
		PositionVector p01 = new PositionVector(p0);
		p01.negate();
		pc1.add(p01);
		u = n.dot(pc1)/vn;
		
		//If u < 0 the hit point is in the opposite dirction to that where the sky
	    //sector is (it is pointed by v)
		
		if(u <= 0.0) return NO_HIT;
		
		PositionVector hit = new PositionVector(0,0,0);
		hit.set(p0);
		hit.mul(u, v);
		
		// 4. Test if the hit point is inside the ellipse
		
		double coord_a, coord_b;
	    PositionVector diff = new PositionVector(0,0,0);
	    
	    //  double b = (double)GetShape(leaf).getSemiminorAxis();
	    double b = semiminoraxis;
	    diff.set(hit);
	    PositionVector pcc = new PositionVector(pc);
	    pcc.negate();
	    diff.add(pcc);
	   
	    //  Point pend = GetEndPoint(GetPetiole(leaf));
	    //  coord_b = Dot(PositionVector(pend - pc), diff);
	    
	    coord_b = diff.dot(ydir);			 //len of ydir == 1
	    // coord_b /= b;            
	    //  if(maximum(coord_b, -coord_b) > b) {
	    
	    if(abs(coord_b) > b) {
	        return NO_HIT;
	      }
	    
	    coord_a = sqrt( diff.dot(diff) - coord_b * coord_b );
	    
	    // if(maximum(coord_a, -coord_a) > a) {
	    
	    if(abs(coord_a) > a) {
	        return NO_HIT;
	      }
	    
	    if(pow(coord_a/a, 2.0) +pow(coord_b/b, 2.0) > 1.0)
	    	{ return NO_HIT;}
	    else
	    	 {return HIT_THE_FOLIAGE;}
	    
	}
	
	
}
