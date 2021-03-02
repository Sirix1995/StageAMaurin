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

import java.util.Vector;

/**
 * Translated from orignal C++ Lignum code.
 *
 * @author Alexander Brinkmann
 */ 
public class VOBookKeeper {

	private int tag; //A tag  for each voxel object. If  two objects denote
	//the same segment, they  have the same tag. Note that
	//we may  have a lot of objects. 
	
	private Vector<Boolean> v = new Vector<Boolean>(); //Vector to mark voxel object computed
	
	
	public VOBookKeeper() {
		tag = 0;
	}
	
	//Return a (unique) tag for object
	public long newTag() {
		int tmp = tag;
		tag++;
		return tmp;
	}
	
	//Return true if light beam hit (v[tag] == true)
	public boolean rayHit(int tag) {
		return v.get(tag) == true;
	}
	
	//Set v[tag] = true if light beam hit
	public void setRayHit(int tag) {
		v.set(tag, true);
	}
	
	//Call before each light beam (v[i] must be false)
	public void resetVector() {
		for(int i=0;i<v.size(); i++)
		v.set(i, false);
	}
	
	//Call  before InsertVoxelObjects  (v.size() ==  0 and tag = 0)
	public void reset() {
		v.clear();
		tag = 0;
	}
	
	//Call after InsertVoxelObjects
	public void init() {
		initVector();
	}
	
	//Call after InsertVoxelObjects (v.size() == tag and v[i] == false
	public void initVector() {
		v.setSize(tag);
		int i =v.size()-1;
		while (v.get(i)==null){
			v.set(i,false);
			i--;
		}
	}
}
