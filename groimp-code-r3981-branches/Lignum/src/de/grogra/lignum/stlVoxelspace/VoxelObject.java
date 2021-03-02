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

import javax.vecmath.Point3d;

import de.grogra.lignum.jadt.Distance;
import de.grogra.lignum.jadt.ParametricCurve;
import de.grogra.lignum.jadt.PositionVector;

/**
 * Wrapper class for different photosynthesising elements a voxel box
 * 
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */
public abstract class VoxelObject {

	private final long tag; //A  tag for object.  If two  objects denote  the same
	//segment, they have the same tag
	public boolean hit_self; //debug: true if segment compares itself 
	
	protected VoxelObject(VoxelObject vo){
		tag = vo.tag;
	}
	
	protected VoxelObject(long t){
		hit_self = false;
		tag = t;
	}
	
	public abstract int getRoute(Point3d p, PositionVector dir, Distance length);
	public abstract double getExtinction(Point3d p, PositionVector dir, ParametricCurve K);
	public long getTag(){
		return tag;
		}
	
	
	
}
