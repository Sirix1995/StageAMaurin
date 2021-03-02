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

import de.grogra.lignum.jadt.Mathsym;
import de.grogra.lignum.stlLignum.LGMAD;

/**
 * ForestDescriptor collects descriptive statistics and indices for the
 * forest in the voxel space. The natural place to insert data 
 * (original Lignum comment)
 * 
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */
public class ForestDescriptor implements Mathsym{

	public ForestDescriptor() {
		afb = 0.0;
		afc = 0.0;
		laib = 0.0;
		laic = 0.0;
		h = -1.0;
		cbase = R_HUGE;
	}
	
	private double afb;//leaf area, broad leaved
	private double afc;//needle area, conifers
	private double laib;//leaf area index for broad leaved
	private double laic;//leaf area index for conifers
	private double h;//height of the tallest tree 
	private double cbase;//lower limit  for tree crowns (defined  as the smallest
			      //z-coord of all segments that have foliage)
	
	//Original Comment:
	//Update LAI, A is the area of the voxel space in  meters
	//Call after  the last call to InsertVoxelObjects
	public void updateLAI(double A) {
		laic = afc/A;
		laib = afb/A;
	}
	
	//Original Comment:
	//Reset by VoxelSpace::reset()
	
	public void reset(){
	    afb=0.0;afc=0.0;laib=0.0;
	    laic=0.0;h=-1.0;cbase=R_HUGE;
	  }
	
	public double SetValue(LGMAD name, double value) {
		
		double val = this.GetValue(name);
		switch(name){
		case LGAAfb:
			this.afb = value;
			break;
		case LGAAfc:
			this.afc = value;
			break;
		//The lowest segment having foliage
		case LGAcbase:
			if (value < this.cbase)
			      this.cbase = value;
			break;
		case LGAH:
			if (value > this.h)
			      this.h = value;
			break;
		default:
			System.out.println( "ForestDescriptor SetValue Unknown name  ");
		}
		return val;
	}
	
	public double GetValue(LGMAD name) {
		
	
		switch(name){
		case LGAAfb:
			return this.afb;
		case LGAAfc:
			return this.afc;
		case LGAcbase:
			return this.cbase;
		case LGAH:
			return this.h;
		case LGALAIb:
			return this.laib;
		case LGALAIc:
			return this.laic;
		default:
			System.out.println( "ForestDescriptor GetValue Unknown name  ");
		}
		return 0.0;
	}
}
