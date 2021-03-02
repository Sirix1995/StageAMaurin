package de.grogra.ray2.tracing.modular;

import java.util.ArrayList;
import java.util.HashMap;

import javax.vecmath.Color4f;

import de.grogra.ray.physics.Emitter;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Scattering;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray.util.Ray;
import de.grogra.ray.util.RayList;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;

public class PathValues {
	

	public ArrayList<Intersection> intersecList;
	public ArrayList<Environment> envList;
	public ArrayList<Scattering> shaderList;
	public boolean[] specReflectionList;
	public boolean[] specRefractionList;
	public Light[] hitLights;
	
	public ArrayList<Line> rayListBE;
	public ArrayList<Spectrum> weightListBE;
//	public ArrayList<Float> probabilitiDensitiesBE;
	public ArrayList<Float> geometryFactorsBE;

	public float pathResultList;

	public Spectrum3d initialSpectrum = new Spectrum3d();
	
	public int pathLength;
	
	public Color4f color;
	
	public int creatorID;
	
	public PathValues(){
		
		color = new Color4f();
	}

	
	public void initialize(int size){
		
		
		
		shaderList = new ArrayList<Scattering>(size);
		intersecList = new ArrayList<Intersection>(size);
		envList = new ArrayList<Environment>(size);
		
		rayListBE = new ArrayList<Line>(size-1);
		weightListBE = new ArrayList<Spectrum>(size);
		geometryFactorsBE = new ArrayList<Float>(size);
		
		pathResultList = 0;
//		probabilitiDensitiesBE = new ArrayList<Float>(size);
		
		hitLights = new Light[size];

		specReflectionList = new boolean[size];
		specRefractionList= new boolean[size];
		
//		for(int i=0; i < rayListBE.size(); i++) rayListBE.add(i, new Line());
//		for(int i=0; i < shaderList.size(); i++) shaderList.add(i, new Shaderr);
//		for(int i=0; i < envList.size(); i++) envList.add(i, null);
//		for(int i=0; i < weightListBE.size(); i++) weightListBE.add(i, null);
//		for(int i=0; i < geometryFactorsBE.size(); i++) geometryFactorsBE.add(i, null);
	}

	
	public void saveValues(int i, Line r, Intersection desc, Environment env, Spectrum spec, Scattering sh, boolean isSpec, boolean isRefrac){
//		System.err.println(" PathValues:saveValues: save Values in depth " +i);
//		System.err.println(" PV" +toString());
		if(rayListBE.size()>i) rayListBE.set(i,r); 
		else rayListBE.add(i,r);
		

		if(intersecList.size()>i) intersecList.set(i, desc);
		else intersecList.add(i,desc);
		if(envList.size()>i) envList.set(i, env);
		else envList.add(i,env);
		if(weightListBE.size()>i) weightListBE.set(i,spec);
		else weightListBE.add(i,spec);
		if(shaderList.size()>i) shaderList.set(i,sh);
		else shaderList.add(i,sh);

		specReflectionList[i] = isSpec;
		specRefractionList[i]=  isRefrac;
//		System.err.println(" PV" +toString());
	}

	public void saveProbabilityDensity(int i, float probDens){
//		probabilitiDensitiesBE.add(i,new Float(probDens));
	}
	public void saveGeometryFactor(int i, float geoFac){
		
		if(geometryFactorsBE.size()>i) geometryFactorsBE.set(i,new Float(geoFac));
		else geometryFactorsBE.add(i,new Float(geoFac));
	}	
	
	public void saveResult( float value){
		pathResultList = value;
	}
	
	public void saveHitLight(int index, Light hitLight){
		hitLights[index]= hitLight;
	}
	
	public String toString(){
		
		String s= super.toString();
		s+= "   SourceId=" +creatorID +"  Pathlength=" +pathLength +"\n";
		s+= "   initialSpectrum=" +initialSpectrum+"\n";
		s+= "   color=" +color+"\n";
		for(int i=0; i<pathLength; i++){
			s += "   envList["+i+"]=" +envList.get(i).point +" --- " + "SpecularReflection["+i+"]" +specReflectionList[i] + " SpecularRefraction["+i+"]" +specRefractionList[i]  +"\n";

		}		
//		for(int i=0; i<pathLength; i++){
//			if(intersecList.get(i)!=null)s += "   intersectList["+i+"]=" +intersecList.get(i).getPoint() +"    intsection.line.direction= " +intersecList.get(i).line.direction +"\n";
//			else s += "   intersectList["+i+"]= no intersection saved \n";
//		}
		for(int i=0; i<pathLength; i++){
			if((i<(pathLength-1))&& (i<rayListBE.size()-1) ){
				s+= "   rayListBE[" +i +"].direction=" +rayListBE.get(i).direction +"   DirDens[" +i+"]=" +rayListBE.get(i).directionDensity
				+"  ---  weightBE[" +i +"]=" +weightListBE.get(i)+"\n";
			}else{
				if(pathLength==1)s+= "   rayListBE[" +i +"].direction=not set" +"  ---  weightBE[" +i +"]=" +weightListBE.get(0)+"\n";			
				else s+= "   rayListBE[" +i +"].direction=not set" +"  ---  weightBE[" +i +"]=not set"+"\n";
			}
		}
		return s;
	}
	
	public void savePathLenght(int depth){
		pathLength = depth;
	}

	
	
	
	public boolean isSpecular(int index){
		if((index<0)||(index > pathLength-1)) return false;
		return(this.specReflectionList[index]||this.specRefractionList[index]);
	}
	

}
