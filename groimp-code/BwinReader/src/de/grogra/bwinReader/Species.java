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


package de.grogra.bwinReader;

public class Species {
	private int code;
	private String latinName;

//getter and setters
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getLatinName() {
		return latinName;
	}
	public void setLatinName(String latinName) {
		this.latinName = latinName;
	}
	//every code less than 500 is supposed to be a leaf Tree
	public boolean isLeaf(){
		return (this.code < 500);
	}

//Basic Models
	//the basic Models are divided into leaf trees and needle trees
	public String getBaseModel(ParameterHandler ph){
		String pre="";
		if(ph.getPointerMode()==3) {
			pre="module Tree_"+this.code+"_pointer(int code, int age);";
		}
		if(this.isLeaf()){
		
			return pre+"module Tree_"+this.getCode()+ph.selectVariableList()+ph.getDeciduousModel();//"==>Cylinder(crownBase+crownDiameter/2, bhd/200)Sphere(crownDiameter/2);";
		}else{
			return pre+"module Tree_"+this.getCode()+ph.selectVariableList()+ph.getConiferModel();//"==>Cylinder(crownBase, bhd/200)Cone(height-crownBase,crownDiameter/2);";
		}
	}
	
//output
	public String toString(ParameterHandler ph){
		return "//" + this.getLatinName() +
				"\n" + this.getBaseModel(ph) +"\n\n";
		
	}
	
}
