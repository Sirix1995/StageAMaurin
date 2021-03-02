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

import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

import java.util.Vector;

import javax.vecmath.GMatrix;
import javax.vecmath.Point3d;

import de.grogra.ray2.radiosity.Vector3d;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class PositionVector extends Vector3d implements Mathsym {

	//public PositionVector(PositionVector v){
		//super(v);
	//}
	
	public PositionVector(){
		super();
	}
	
	public PositionVector(Vector<Double> v){
		super(v.get(0),v.get(1),v.get(2));
		if(v.size()>3) System.out.println("Error creating PositionVector from Vector. Vector too long");
		else if(v.size()<3) if(v.size()>3) System.out.println("Error creating PositionVector from Vector. Vector too short");
	}
	
	public PositionVector(PositionVector v){
		super(v.x,v.y,v.z);
	}
	
	public PositionVector(Point3d v){
		super(v.x,v.y,v.z);
	}
	
	public PositionVector(double x, double y, double z){
		super(x,y,z);
	}
	
	public void mul(double c){
		this.x = this.x *c;
		this.y = this.y *c;
		this.z = this.z *c;
	}
	
	public void mul(double c, PositionVector v){
		this.x = v.x *c;
		this.y = v.y *c;
		this.z = v.z *c;
	}
	
	public static PositionVector mul(PositionVector v, double c){
		PositionVector w = new PositionVector(v.x*c,v.y*c,v.z*c);
		return w;
	}
	
	public void subtract (PositionVector v, PositionVector w){
		this.x = v.x - w.x;
		this.y = v.y -w.y;
		this.z = v.z -w.z;
	}
	
	public void subtract (PositionVector w){
		this.x = x - w.x;
		this.y = y -w.y;
		this.z = z -w.z;
	}
	
	public void subtract (Point3d w){
		this.x = x - w.x;
		this.y = y -w.y;
		this.z = z -w.z;
	}
	
	//Original Comment:
	//Rotation about an arbitrary axis in space according to
	//Rogers&Adams: mathematical Elements for Computer Graphics p. 121-128
	// dir does not need to be a unit vector. It is nomalized here. If it is
	//not normalized, the distance of the rotated point (end point of vector)
	// from the axis of ration = Length_of(dir) * distance_before
	public void rotate(Point3d p0, PositionVector dir, double angle) {
		
		//Original Comment:
		// TM = transformation matrix,
		// M = multiplication matrix 
		
		GMatrix TM = new GMatrix(4,4);
		//Original Comment:
		// 1. Make p0 origin of the coordinate system = move
		
		GMatrix T = new GMatrix(4,4);
		T.setIdentity();
		T.setElement(3, 0, -p0.x);
		T.setElement(3, 1, -p0.y);
		T.setElement(3, 2, -p0.z);
		//System.out.println("TM1" + TM);
		//System.out.println("T" + T);
		TM.set(T);
		
		//Original Comment:
		// 2. Perform appropriate rotations to make the axis of rotation coincident with
		// the z-axis
		// Normalize the direction vector

		GMatrix Rx = new GMatrix(4,4);
		GMatrix Ry = new GMatrix(4,4);
		
		double cx,cy,cz;  //direction cosines of the axis =(x,y,z) of a unit vector
		PositionVector cc = new PositionVector(dir.x, dir.y, dir.z);
		cc.normalize();
		cx = cc.x;
		cy = cc.y;
		cz = cc.z;
		
		double lxy; //projection of direction (unit) vector on xy plane
		
		Rx.setIdentity();
		
		lxy = sqrt(pow(cy,2.0) + pow(cz,2.0));
		
		if(lxy > R_EPSILON){
			Rx.setElement(1, 1, cz/lxy);
			Rx.setElement(1, 2, cy/lxy);
			Rx.setElement(2, 1, -cy/lxy);
			Rx.setElement(2, 2, cz/lxy);
			
			TM.mul(Rx); 		// rotation about x-axis
			
		}
		
		Ry.setIdentity();
		Ry.setElement(0, 0, lxy);
		Ry.setElement(0, 2, cx);
		Ry.setElement(2, 0, -cx);
		Ry.setElement(2, 2, lxy);
		//System.out.println("TM2" + TM);
		TM.mul(Ry); //rotation about y-axis
		
		//OriginalComment:
		// 3. Finally, the rotation about the arbitrary axis is given be z-axis rotation
		//    matrix
			
		
		GMatrix Ra = new GMatrix(4,4);
		double cosa, sina;
		Ra.setIdentity();
		cosa= cos(angle);
		sina = sin(angle);
		Ra.setElement(0, 0, cosa);
		Ra.setElement(0, 1, sina);
		Ra.setElement(1, 0, -sina);
		Ra.setElement(1, 1, cosa);
		//System.out.println("TM3" + TM);
		//System.out.println("Ra" + Ra);
		TM.mul(Ra);
		
		//OriginalComment:
		// 4. Now, trasformations Ry, Rx and T inversed to get back to
		//    original position
		//System.out.println("TM4" + TM);
		Ry.transpose();
		TM.mul(Ry);
		
		if(lxy > R_EPSILON) {
			Rx.transpose();
			TM.mul(Rx);
		}
		
		// Move back
		T.setIdentity();
		T.setElement(3, 0, p0.x);
		T.setElement(3, 1, p0.y);
		T.setElement(3, 2, p0.z);
		//System.out.println("TM5" + TM);
		TM.mul(T);
		//System.out.println("T" + T);
		 // Apply transformation to Positionvector
		//Since GMatrix does not provide a method for multiplication with a 
		//vector matrix multiplication is used instead. 
		GMatrix dummy = new GMatrix(4,4);
		dummy.setZero();
		dummy.setElement(0, 0, this.x);
		dummy.setElement(0, 1, this.y);
		dummy.setElement(0, 2, this.z);
		dummy.setElement(0, 3, 1.0);
		//System.out.println("dummy" + dummy);
		dummy.mul(TM);
		//System.out.println("dummy" + dummy);
		//System.out.println("TM6" + TM);
		this.x = dummy.getElement(0, 0);
		this.y = dummy.getElement(0, 1);
		this.z = dummy.getElement(0, 2);
				
		}
	}
	

