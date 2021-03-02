/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus*/
package de.grogra.ext.limelight;

import javax.vecmath.*;


import de.grogra.vecmath.geom.Line;

public class Ray {
	
	public Point3f o;
	public Vector3f d;
	
	public Ray(){
		o=new Point3f();
		d=new Vector3f();
		
	}
	
	public Ray(Point3f origin, Vector3f direction)
	{
		o=origin;
		d=direction;
	}
	
	Line ToLine(){
		Point3d od=new Point3d((double)o.x,(double)o.y,(double)o.z);
		Vector3d dd=new Vector3d((double)d.x,(double)d.y,(double)d.z);
		return new Line((Tuple3d)od,dd,0.0,Double.POSITIVE_INFINITY);
	}
	
	public void set(Ray r){
		o.set(r.o);
		d.set(r.d);
	}
	
}
