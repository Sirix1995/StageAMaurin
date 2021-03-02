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

import java.util.ArrayList;
import java.util.HashMap;

public class Tree {
	HashMap<String,String> data;
	public Tree(){
		data=new HashMap<String,String>();
	}

	public HashMap<String, String> getData() {
		return data;
	}
	
// output functions
	

	public String getTranslate() {
		return "Translate(" + this.data.get("RelativeXKoordinate_m") + "," + this.data.get("RelativeYKoordinate_m") + ","
				+ this.data.get("RelativeBodenhoehe_m") + ")";
	}

	public String getFunctionPart(String[] para){
			String erg="(";
			for(String pa : para){
				erg+=this.data.get(pa)+",";
			}
			erg=erg.substring(0,erg.length()-1);
			return erg+")";
		
	}
	/*public String getFunctionPart() {	
		return "(" + this.data.get("Hoehe_m") + "," + this.data.get("BHD_mR_cm") + ","
				+ this.data.get("Kronenansatz_m") + "," + this.data.get("MittlererKronenDurchmesser_m") + ","
				+ this.data.get("Alter_Jahr") + ","+this.data.get("Lebend")+")";
	}
*/
	public String toString(String[] parameter) {
		return this.getTranslate() + " Tree_" + this.data.get("BaumartcodeLokal")+ this.getFunctionPart(parameter);
	}
	
	public String toString_basicPointer(String[] parameter) {
		String erg=("TreePointer("+this.data.get("BaumartcodeLokal")+","+this.data.get("Alter_Jahr")+") "+this.toString(parameter));
		return erg;
	}
	public String toString_simpelPointer(String[] parameter){
		String erg=(Integer.parseInt(this.data.get("BaumartcodeLokal"))<500 ? "Deciduous_pointer":"Conifer_pointer");
		erg+="("+this.data.get("BaumartcodeLokal")+","+this.data.get("Alter_Jahr")+")"+this.toString(parameter);	
		return erg;
	}
	public String toString_advancedPointer(String[] parameter){
		String erg="Tree_"+this.data.get("BaumartcodeLokal")+"_pointer("+this.data.get("BaumartcodeLokal")+","+this.data.get("Alter_Jahr")+")";		
		erg+=this.toString(parameter);
		return erg;
	}

	public String printTee(ParameterHandler ph){
		
		switch(ph.getPointerMode()){
			case(1): return this.toString_basicPointer(ph.getSelectionGerman());//basic TreePointer
			case(2): return this.toString_simpelPointer(ph.getSelectionGerman());//simple TreePointer
			case(3): return this.toString_advancedPointer(ph.getSelectionGerman());//advanced TreePointer
			default: return this.toString(ph.getSelectionGerman());//no TreePointer
		}
		
	}

	public boolean filterAll(ArrayList<FilterItem> flist) {
		for(FilterItem f:flist) {
			if(!filterPart(f)) {
				return false;
			}
		}
		return true;
	}

	public boolean filterPart(FilterItem f) {
		if(f.getPv().getType().equals("boolean")) {
			return this.getData().get(f.getPv().getGerman().toString()).equals(f.getVal());
		}else if(f.getPv().getType().equals("int")){
			if(f.getOp()==0) {
				return Integer.parseInt(this.getData().get(f.getPv().getGerman()))==Integer.parseInt(f.getVal());
			}else if(f.getOp()==1) {
				return Integer.parseInt(this.getData().get(f.getPv().getGerman()))< Integer.parseInt(f.getVal());
			}else if(f.getOp()==2) {
				return Integer.parseInt(this.getData().get(f.getPv().getGerman()))> Integer.parseInt(f.getVal());
			}else {
				return Integer.parseInt(this.getData().get(f.getPv().getGerman()))!= Integer.parseInt(f.getVal());
			}
		}else {
			if(f.getOp()==0) {
				return Float.parseFloat(this.getData().get(f.getPv().getGerman()))==Float.parseFloat(f.getVal());
			}else if(f.getOp()==1) {
				return Float.parseFloat(this.getData().get(f.getPv().getGerman()))<Float.parseFloat(f.getVal());
			}else if(f.getOp()==2) {
				return Float.parseFloat(this.getData().get(f.getPv().getGerman()))>Float.parseFloat(f.getVal());
			}else {
				return Float.parseFloat(this.getData().get(f.getPv().getGerman()))!=Float.parseFloat(f.getVal());
			}
		}
	}

}
