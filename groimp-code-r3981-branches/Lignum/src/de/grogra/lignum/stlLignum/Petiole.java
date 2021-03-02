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

package de.grogra.lignum.stlLignum;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.PositionVector;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class Petiole {

	//The functions of this class are declared as friends in cLignum. I just used normal methods.
	
	private final Point3d begin = new Point3d();
	private final Point3d end = new Point3d();
	
	public Petiole(Petiole p){
		begin.set(p.begin);
		end.set(p.begin);
	}
	
	public Petiole(Point3d b, Point3d e){
		begin.set(b);
		end.set(e);
	}
	
	public void setBegin(Point3d b) {
		begin.set(b);
	}
	
	public void setEnd(Point3d e) {
		end.set(e);
	}
	
	public Point3d getBegin() {
		return begin;
	}
	
	public Point3d getEnd() {
		return end;
	}
		
	public Point3d GetStartPoint() {
		return begin;
	}
	
	public Point3d GetEndPoint() {
		return end;
	}
	
	public void SetStartPoint(Point3d pnt) {
		begin.set(pnt);
	}
	
	public void SetEndPoint(Point3d pnt) {
		end.set(pnt);
	}
	
	public PositionVector GetDirection() {
		Point3d dp = new Point3d();
		dp.sub(end, begin);
		PositionVector d = new PositionVector(dp.x, dp.y, dp.z);//construct the vector
		d.normalize();//normalize it
		
		return d;
	}
	
}
