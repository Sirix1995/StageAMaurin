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

package de.grogra.lignum.stlVoxelspace;

import static java.lang.Math.abs;
import static java.lang.Math.acos;
import static java.lang.Math.exp;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Distance;
import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.jadt.ParametricCurve;
import de.grogra.lignum.jadt.PositionVector;
import de.grogra.lignum.stlLignum.BeamShading;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class CfCylinder extends VoxelObject implements Mathsym {
	
	private final Point3d p0 = new Point3d();//Start point of the cylinder
	private final Point3d p2 = new Point3d();//p0+sp*l*Point(d), point that could be the start point of the light beam
	private final PositionVector d = new PositionVector();//direction of the cylinder
	private final double l;//Length of the cylinder
	private final double rw;//Wood radius
	private final double rf;//Foliage radius
	private final double af;//Foliage area
	private final double vf;//Foliage volume
	private final double sp;//start point [0:1] of the light beam (e.g. 0.5 is the mid
    //point of the segment)
	
	
	public CfCylinder(CfCylinder cfobj){
		super(cfobj.getTag());
		p0.set(cfobj.p0);
		p2.set(cfobj.p2);
		d.set(cfobj.d);
		l = cfobj.l;
		rw = cfobj.rw;
		rf = cfobj.rf;
		af =cfobj.af;
		vf = cfobj.vf;
		sp = cfobj.sp;		
	}
	
	public CfCylinder(Point3d p, PositionVector dir, double length, double rwood, double rfol, double folarea, double folvol,
			double beam_start, long tag){
		super(tag);
		p0.set(p);
		d.set(dir);
		l=length;
		rw = rwood;
		rf = rfol;
		af = folarea;
		vf = folvol;
		sp = beam_start;
		
		PositionVector tmp = new PositionVector(p0);
		PositionVector dummy = new PositionVector(d);
		dummy.mul(sp);
		dummy.mul(l);
		tmp.add(dummy);//tmp = (p0)+sp*l*d
		p2.set(tmp);
	}
	
	@Override
	public int getRoute(Point3d p, PositionVector dir, Distance length){
		
		hit_self = false;
		if(abs(p.distance(p2))< R_EPSILON){//Check if start point of the light beam
			hit_self = true;
			//Original Comment:
			//The light beam starts from the shading segment
		    //cout << p << dir << endl << p0 << d << endl << p2 << " " << sp << " " << l << endl<< endl; 
			return 0;
		}
		
		return BeamShading.CylinderBeamShading(p,dir,p0,d,rf,rw,l,length);
	}
	
	
	@Override
	public double getExtinction(Point3d p, PositionVector dir, ParametricCurve K) {
		Distance length = new Distance(0.0); //path length 
		double tau = 1.0; //clear sky
		//Original Comment:
		//Return the path length light beam travels in the shading segment
		int result = this.getRoute(p,dir,length);
		//check the two extreme cases
		if(result == 0){//Not hit
		      tau = 1.0;//clear sky so far
		      //Original Comment:
		      //Debug misses
		      //cout << tau << " " << p0.getX() << " "  << p0.getY() << " " << p0.getZ() << " "
		      //     << d.getX() << " " << d.getY() << " " << d.getZ() << " "
		      //     << p.getX() << " " << p.getY() << " " << p.getZ() << " " 
		      //     << dir.getX() << " "  << dir.getY() << " "  << dir.getZ() << " " 
		      //     << l << " " << rw << " " << rf << " " << rf-rw << endl;
		      return tau;
		}
		else if(result == -1){//Wood hit
		      tau = 0.0;//sector blocked
		      return tau;
		}
		
		//Original Comment:
		//The angle between the light beam and the segment
		double a_dot_b = d.dot(dir);
		double phi = 0.0;
		
		phi = PI_DIV_2 - acos(abs(a_dot_b));
		
		tau = exp(-K.eval(phi)*length.d*af/vf);
		
		return tau;
		
	}
	

}
