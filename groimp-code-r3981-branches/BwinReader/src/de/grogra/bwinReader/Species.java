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
		
			return pre+"\nmodule Tree_"+this.getCode()+ph.selectVariableList()+ph.getDeciduousModel();//"==>Cylinder(crownBase+crownDiameter/2, bhd/200)Sphere(crownDiameter/2);";
		}else{
			return pre+"\nmodule Tree_"+this.getCode()+ph.selectVariableList()+ph.getConiferModel();//"==>Cylinder(crownBase, bhd/200)Cone(height-crownBase,crownDiameter/2);";
		}
	}
	
//output
	public String toString(ParameterHandler ph){
		return "//" + this.getLatinName() +
				"\n" + this.getBaseModel(ph) +"\n\n";
		
	}
	
}
