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

import java.util.Vector;

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.EllipseL;
import de.grogra.lignum.jadt.PositionVector;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class BroadLeaf {

	private final BroadLeafAttributes bla;
	//cLignum uses typdef LGMdouble instead of double :
	
	public BroadLeaf(double sf, double tauL, double dof, int number_of_sectors, Petiole petiole, EllipseL shape){
		bla = new BroadLeafAttributes(sf,tauL,dof,petiole,shape,number_of_sectors);
	}
	
	public BroadLeaf(EllipseL shape, Petiole petiole){
		bla = new BroadLeafAttributes(1.0,1.0,1.0,petiole,shape,10);
	}
	
	public BroadLeaf(EllipseL shape, Petiole petiole, int sky_sectors){
		bla = new BroadLeafAttributes(1.0,1.0,1.0,petiole,shape,sky_sectors);
	}
	
	public BroadLeaf(BroadLeaf b1){
		bla = new BroadLeafAttributes(b1.bla);
	}
	
	public void photosynthesis(double p0){
		bla.P = p0 * bla.Qabs;
	}
	
	public void move(Point3d mov){
		Point3d tmp = new Point3d(bla.petiole.getBegin());
		tmp.add(mov);
		bla.petiole.setBegin(tmp);
		tmp.set(bla.petiole.getEnd());
		tmp.add(mov);
		bla.petiole.setEnd(tmp);
		//bla.e.move(mov);
	}
	
	public double GetValue(LGMAD name){
		switch (name){
		case LGAA:
			return bla.e.getArea() * GetValue(LGMAD.LGAdof);
			//break;
		case LGAdof:
			return bla.degree_of_filling;
		//	break;
		case LGAtauL:
			return bla.tauL;
		//	break;
		case LGAP:
			return bla.P;
		//	break;
		case LGAM:
			return bla.M;
		//	break;
		case LGAQabs:
			return bla.Qabs;
		//	break;
		case LGAQin:
			return bla.Qin;
		//	break;
		case LGAsf:
			return bla.sf;
		//	break;
		case LGAWf:
			//Wf = A/SLA (i.e., kgC = m2/(m2/kgC) ) 
			return GetValue(LGMAD.LGAA)/GetValue(LGMAD.LGAsf);
		//	break;
		default:
			System.out.println("BroadLeaf GetValue unknown attribute: "+name + " returning 0.0" );
			return 0.0;
		//	break;
		}
		
	}
	
	public void SetValue(LGMAD name, double value){
		
		double old_value = GetValue(name);
		
		switch(name){
		case LGAA:
			//Given the true area of the leaf, set the shape area, the scaling
		    //center is the petiole end
			bla.e.setArea(value/GetValue(LGMAD.LGAdof), GetPetiole().GetEndPoint());
			break;
		case LGAdof:
			bla.degree_of_filling = value;
			break;
		case LGAtauL:
			bla.tauL = value;
			break;
		case LGAP:
			bla.P = value;
			break;
		case LGAM:
			bla.M = value;
			break;
		case LGAQabs:
			bla.Qabs = value;
			break;
		case LGAQin:
			bla.Qin = value;
			break;
		case LGAsf:
			bla.sf = value;
			break;
		case LGAWf:
			//Original Comment:
			//Given the Wf, set the area of leaf to correspond to:
		    //LeafA = SLA*Wf, i.e m2 = (m2/kgC)*kgC
		    //where LeafA is _actual_ leaf area (effect of dof is taken into
		   // consideration in GetValue(*,LGAA), SetValue(*,LGAA, value)
		    //bl.bla.shape.setArea(GetValue(bl,LGAsf)*value/GetValue(bl,LGAdof));
			SetValue(LGMAD.LGAA,GetValue(LGMAD.LGAsf)*value);
			break;
		default:
			System.out.println("BroadLeaf SetValue unknown attribute: "+ name + "returning:" + old_value);
			//return old_value;
		}
	}
	
	public Point3d GetCenterPoint() {
		return bla.e.getCenterPoint();
	}
	
	
	public PositionVector GetLeafNormal() {
		return bla.e.getNormal();
	}
	
	public Petiole GetPetiole() {
		return bla.petiole;
	}
	
	public EllipseL getShape(){
		return bla.e;
	}
	
	public void SetCenterPoint(Point3d p) {
		bla.e.setCenterPoint(p);
	}
	
	public void SetRadiationVector(Vector<Double> v) {
		for(int i = 0; i<bla.sv.size();i++){
			bla.sv.set(i, v.get(i));
		}
	}
	
	public Vector<Double> getRadiationVector() {
		return bla.sv;
	}
	
	//Original Comment:
	//Translates BroadLeaf as specified by vector t, orientation not changed
	//(= vector t added to all positions, including Petiole)
	
	public void TranslateLeaf(PositionVector t) {
		
		Point3d t_point = new Point3d(t);
		Point3d pnt = new Point3d(GetCenterPoint());
		
		pnt.add(t_point); // pnt + t_point
		SetCenterPoint(pnt);
		
		pnt.set(bla.petiole.GetStartPoint());
		pnt.add(t_point); // pnt + t_point
		bla.petiole.SetStartPoint(pnt);
		
		pnt.set(bla.petiole.GetEndPoint());
		pnt.add(t_point); // pnt + t_point
		bla.petiole.SetEndPoint(pnt);
	}
	
	
	public void Roll(double angle) {
		bla.e.roll(angle);
	}
	
	public void Pitch(double angle) {
		bla.e.pitch(angle);
	}
	
	public void Turn(double angle) {
		bla.e.turn(angle);		
	}
	
	public void SetLeafPosition(Point3d p) {
		PositionVector transvct = new PositionVector(p);
		transvct.subtract(bla.petiole.GetStartPoint());
		
		TranslateLeaf(transvct);
	}
	
}
