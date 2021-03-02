package de.grogra.bwinReader;

import java.util.ArrayList;
import java.util.prefs.Preferences;
public class ParameterHandler {
	private ParameterValue[] liste= {
			new ParameterValue("BHD_mR_cm","dbh","float"),
			new ParameterValue("Hoehe_m","height","float"),
			new ParameterValue("Kronenansatz_m","crownBase","float"),
			new ParameterValue("MittlererKronenDurchmesser_m","crownDiameter","float"),
			new ParameterValue("Alter_Jahr","age","int"),
			
			new ParameterValue("BaumartcodeLokal","speciesCodeLocal","int"),
			new ParameterValue("Nr","id","int"),
			new ParameterValue("Kennung","marker","int"),
			new ParameterValue("BaumartcodeStd","speciesCodeStd","int"),
			new ParameterValue("SiteIndex_m", "siteIndex","float"),
			new ParameterValue("RelativeXKoordinate_m","Xpos","float"),
			new ParameterValue("RelativeYKoordinate_m","Ypos","float"),
			new ParameterValue("RelativeBodenhoehe_m","Zpos","float"),
			new ParameterValue("Lebend","alive","boolean"),
			new ParameterValue("Entnommen","taken","boolean"),
			new ParameterValue("AusscheideMonat","monthTaken","int"),
			new ParameterValue("AusscheideJahr","yearTaken","int"),
			new ParameterValue("ZBaum","futureTree","boolean"),
			new ParameterValue("ZBaumtemporaer","futureTreeTmp","boolean"),
			new ParameterValue("HabitatBaum","habitatTree","boolean"),
			new ParameterValue("KraftscheKlasse","KraftClass","int"),
			new ParameterValue("Schicht","layer","int"),
			new ParameterValue("Flaechenfaktor","areaFactor","float"),
			new ParameterValue("Volumen_cbm","volume","float"),
			new ParameterValue("VolumenTotholz_cbm","volumeDead","float")
	};
	private int pointerMode;
	private String coniferModel, deciduousModel;
	private ArrayList<Integer> selection;
	private ArrayList<FilterItem> filters;
	Preferences prefs;
	public ParameterHandler() {
	
		prefs = Preferences.userNodeForPackage(de.grogra.bwinReader.ParameterHandler.class);
		
		String treeParameter=prefs.get("treeParameter","0,1,2,3,4").toString();
		pointerMode=Integer.parseInt(prefs.get("treePointer_Mode","0").toString());
		deciduousModel =prefs.get("deciduousModel","==>Cylinder(crownBase+(height-crownBase)/2, dbh/200)Scale(crownDiameter/2,crownDiameter/2,(height-crownBase)/2)Sphere(1);");
		coniferModel = prefs.get("coniferModel","==>Cylinder(crownBase, dbh/200)Cone(height-crownBase,crownDiameter/2);").toString();
		String filterList=prefs.get("filterList", "13,true,0");
		
		filters=new ArrayList<FilterItem>();
		selection=new ArrayList<Integer>();
		String[] part=treeParameter.split(",");
		for(String p:part) {
			selection.add(Integer.parseInt(p));
		}
		if(filterList.length()>4) {
			String[] filterTmp=filterList.split(";");
			for(String f: filterTmp) {
				String[] fTmp =f.split(",");
				filters.add(new FilterItem(Integer.parseInt(fTmp[0]),liste[Integer.parseInt(fTmp[0])], fTmp[1], Integer.parseInt(fTmp[2])));
			}
		}
	}
	public ParameterValue[] getListe() {
			return liste;
	}
	public ParameterValue[] getSelection() {
		ParameterValue[] erg=new ParameterValue[selection.size()];
		int i=0;
		for(int s:selection) {
			erg[i]=liste[s];
			i++;
		}
		return erg;
	}
	public void dropSelection(int id) {	
		selection.remove(id);
	}
	public String[] getSelectionGerman() {
		String[] erg=new String[selection.size()];
		int i=0;
		for(int s:selection) {
			erg[i]=liste[s].getGerman();
			i++;
		}
		return erg;
	}
	public void addSelection(int[] add) {
		for(int a: add) {
			if(!selection.contains(a)) {
				selection.add(a);
			}
		}
	}
	public void saveData() {
		prefs.put("treeParameter", selectListString());
		prefs.put("treePointer_Mode", ""+getPointerMode());
		prefs.put("deciduousModel", getDeciduousModel());
		prefs.put("coniferModel", getConiferModel());
		String filterList="";
		for(FilterItem f:filters) {
			filterList+=f.toString();
		}
		prefs.put("filterList", filterList);	
	}
	private String selectListString() {
		String erg="";	
		for(int i:selection) {
				erg+=i+",";
		}
		erg=erg.substring(0,erg.length()-1);
		return erg;
	}
	public String selectVariableList() {
		String erg="(";
		for(int i:selection) {
			erg+=liste[i].getType()+" "+liste[i].getEnglish()+",";
		}
		erg=erg.substring(0,erg.length()-1)+")";
		return erg;
	}
	public int getPointerMode() {
		return pointerMode;
	}
	public void setPointerMode(int pointerMode) {
		this.pointerMode = pointerMode;
	}	
	public void reset(){
		this.setPointerMode(0);
		this.selection.removeAll(selection);
		this.selection.add(0);
		this.selection.add(1);
		this.selection.add(2);
		this.selection.add(3);
		this.selection.add(4);
		this.setDeciduousModel("==>Cylinder(crownBase+(height-crownBase)/2, dbh/200)Scale(crownDiameter/2,crownDiameter/2,(height-crownBase)/2)Sphere(1);");
		this.setConiferModel("==>Cylinder(crownBase, dbh/200)Cone(height-crownBase,crownDiameter/2);");
		this.filters=new ArrayList<FilterItem>();
		this.filters.add(new FilterItem(13,liste[13],"true",0));
	}
	public String getDeciduousModel() {
		return deciduousModel;
	}
	public void setDeciduousModel(String dicedureModel) {
		this.deciduousModel = dicedureModel;
	}
	public String getConiferModel() {
		return coniferModel;
	}
	public void setConiferModel(String coniferModel) {
		this.coniferModel = coniferModel;
	}
	public ArrayList<FilterItem> getFilters() {
		return filters;
	}
	public void setFilters(ArrayList<FilterItem> filters) {
		this.filters = filters;
	}
	public void addFilter(FilterItem fi) {
		this.filters.add(fi);
	}
	public void removeFilter(int id) {
		this.filters.remove(id);
	}
	public void removeAllFilter() {
		this.filters=new ArrayList<FilterItem>();
	}
}


class ParameterValue{
	private String german,english, type;
	
	public ParameterValue(String german, String english, String type) {
		this.setGerman(german);
		this.setEnglish(english);
		this.setType(type);
	}
	
	
	public String getGerman() {
		return german;
	}

	public void setGerman(String german) {
		this.german = german;
	}

	public String getEnglish() {
		return english;
	}

	public void setEnglish(String english) {
		this.english = english;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String toString() {
		return this.getEnglish()+"("+this.getType()+")";
	}
}

class FilterItem{
	String[] operators= {"==","<",">","!="};
	ParameterValue pv;
	int pvId;
	String val;
	int op;
	public FilterItem(int pvId,ParameterValue pv, String val, int op) {
		this.setPv(pv);
		this.setOp(op);
		this.setVal(val);
		this.pvId=pvId;
	}
	public ParameterValue getPv() {
		return pv;
	}
	public void setPv(ParameterValue pv) {
		this.pv = pv;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public int getOp() {
		return op;
	}
	public void setOp(int op) {
		this.op = op;
	}
	public String toString() {
		return this.pvId+","+this.getVal()+","+this.getOp()+";";
	}
	public String opAsString() {
		return operators[op];
	}
	public String printReal() {
		if(getPv().getType().equals("boolean")){
			return pv.getEnglish()+" is "+getVal(); 
		}else {
			return pv.getEnglish()+" "+opAsString()+" "+getVal();
		}
	}
}
