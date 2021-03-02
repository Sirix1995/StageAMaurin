package de.grogra.ray2.tracing.modular;

import java.util.ArrayList;

import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.vecmath.geom.Line;

public class CombinedPathValues extends PathValues {
	
	public ArrayList<Line> rayListEB;
	public ArrayList<Spectrum> weightListEB;
//	public ArrayList<Float> probabilitiDensitiesEB;
	public ArrayList<Float> geometryFactorsEB;
	
	public Spectrum3d initialSpectrumEnd = new Spectrum3d();
	
	public int dividerIndex;
	
	boolean validFlag = true;
	
	Line dummyLine;
	Spectrum dummySpec;
	float dummyGeomFac;

	public int lightID;
	public int sensorID;
	
	public void initialize(int size){
		super.initialize(size);
		
		validFlag = true;
		rayListEB = new ArrayList<Line>(size-1);
		weightListEB = new ArrayList<Spectrum>(size);
		geometryFactorsEB = new ArrayList<Float>(size);
		
		dividerIndex = -1;
		
		
		dummyLine = new Line();
		dummyLine.directionDensity = 0.12345f;
		dummySpec = new Spectrum3d();
		dummyGeomFac = 0.98765f;
	}
	
	public void merge2Paths(PathValues firstPath, int firstIndex, PathValues secondPath, int secondIndex){
//		PathValues resultPath = new PathValues();
//		resultPath.initialize(thisIndex+secondIndex+2);
		this.pathLength = firstIndex+secondIndex +2;
		this.initialSpectrum.set((Spectrum)firstPath.initialSpectrum);
		this.initialSpectrumEnd.set((Spectrum)secondPath.initialSpectrum);
		
		this.dividerIndex = firstIndex;
		
		for(int i=0; i<(firstIndex+secondIndex +2); i++){
			if(i<= firstIndex){
				
				if(this.envList.size() > i) this.envList.set(i,firstPath.envList.get(i));
				else this.envList.add(i,firstPath.envList.get(i));
				
				if(this.shaderList.size() > i) this.shaderList.set(i,firstPath.shaderList.get(i)); 
				else this.shaderList.add(i,firstPath.shaderList.get(i));
				
				if(this.intersecList.size() > i) this.intersecList.set(i,firstPath.intersecList.get(i));
				else this.intersecList.add(i,firstPath.intersecList.get(i));

				if(i>0){
					if(this.rayListBE.size() > i-1) this.rayListBE.set(i-1,firstPath.rayListBE.get(i-1));
					else this.rayListBE.add(i-1,firstPath.rayListBE.get(i-1));
					
					if(this.weightListBE.size() > i-1)  this.weightListBE.set(i-1,firstPath.weightListBE.get(i-1));
					else this.weightListBE.add(i-1,firstPath.weightListBE.get(i-1));
					
					if(this.geometryFactorsBE.size() > i-1) this.geometryFactorsBE.set(i-1,firstPath.geometryFactorsBE.get(i-1));
					else this.geometryFactorsBE.add(i-1,firstPath.geometryFactorsBE.get(i-1));
				}
				
				if( i < firstPath.specReflectionList.length)this.specReflectionList[i] = firstPath.specReflectionList[i];
				if( i < firstPath.specRefractionList.length)this.specRefractionList[i] = firstPath.specRefractionList[i];
				
				if( i < firstPath.hitLights.length) this.hitLights[i] = firstPath.hitLights[i];
				
				//Just for initialisation...
				if(this.rayListEB.size() > i) this.rayListEB.set(i,dummyLine);
				else this.rayListEB.add(i,dummyLine);
				
				if(this.weightListEB.size() > i)  this.weightListEB.set(i,dummySpec);
				else this.weightListEB.add(i,dummySpec);
				
				if(this.geometryFactorsEB.size() > i) this.geometryFactorsEB.set(i,dummyGeomFac);
				else this.geometryFactorsEB.add(i,dummyGeomFac);
				
				
			}else{
				int complementIndex = secondIndex - (i -firstIndex-1) ;
				
				if(this.envList.size() > i) this.envList.set(i,secondPath.envList.get(complementIndex));
				else this.envList.add(i,secondPath.envList.get(complementIndex));
				
				if(this.shaderList.size() > i) this.shaderList.set(i,secondPath.shaderList.get(complementIndex)); 
				else this.shaderList.add(i,secondPath.shaderList.get(complementIndex));
				
				if(this.intersecList.size() > i) this.intersecList.set(i,secondPath.intersecList.get(complementIndex));
				else this.intersecList.add(i,secondPath.intersecList.get(complementIndex));

				if(complementIndex < secondIndex){
					if(this.rayListEB.size() > i-1) this.rayListEB.set(i-1,secondPath.rayListBE.get(complementIndex));
					else this.rayListEB.add(i-1,secondPath.rayListBE.get(complementIndex));
					
					if(this.weightListEB.size() > i-1)  this.weightListEB.set(i-1,secondPath.weightListBE.get(complementIndex));
					else this.weightListEB.add(i-1,secondPath.weightListBE.get(complementIndex));
					
					if(this.geometryFactorsEB.size() > i-1) this.geometryFactorsEB.set(i-1,secondPath.geometryFactorsBE.get(complementIndex));
					else this.geometryFactorsEB.add(i-1,secondPath.geometryFactorsBE.get(complementIndex));

				}
				if( complementIndex < secondPath.specReflectionList.length){
					this.specReflectionList[i] = secondPath.specReflectionList[complementIndex];
					this.specRefractionList[i] = secondPath.specRefractionList[complementIndex];
				}
				if( complementIndex < secondPath.hitLights.length)this.hitLights[i] = secondPath.hitLights[complementIndex];
			}
		}
		
		validFlag = true;
	}
	
	
	public void splitInto2Paths(int splitIndex, PathValues firstPath, PathValues secondPath){
		
		firstPath.pathLength = splitIndex+1;
		secondPath.pathLength = this.pathLength - firstPath.pathLength;
		
		firstPath.initialSpectrum.set((Spectrum)this.initialSpectrum);
		secondPath.initialSpectrum.set((Spectrum)this.initialSpectrumEnd);
		
		if((pathLength-1)<splitIndex) splitIndex = pathLength-1;
		
		for(int i=0; i<= splitIndex; i++){
			
			if(firstPath.envList.size()>i) firstPath.envList.set(i,this.envList.get(i));
			else firstPath.envList.add(i,this.envList.get(i));
			
			if(firstPath.intersecList.size()>i) firstPath.intersecList.set(i,this.intersecList.get(i));
			else firstPath.intersecList.add(i,this.intersecList.get(i));
		
			if(firstPath.shaderList.size()>i) firstPath.shaderList.set(i,this.shaderList.get(i));
			else firstPath.shaderList.add(i,this.shaderList.get(i));
			
			firstPath.hitLights[i] =this.hitLights[i];
			firstPath.specReflectionList[i] = this.specReflectionList[i];
			firstPath.specRefractionList[i] = this.specRefractionList[i];
			
			if(i==0) {
				if(firstPath.weightListBE.size()>(i)) firstPath.weightListBE.set(i,this.weightListBE.get(i));
				else firstPath.weightListBE.add(i,this.weightListBE.get(i));				
			}
			
			if(i>0){
				if(firstPath.weightListBE.size()>(i-1)) firstPath.weightListBE.set(i-1,this.weightListBE.get(i-1));
				else firstPath.weightListBE.add(i-1,this.weightListBE.get(i-1));
				
				if(firstPath.rayListBE.size()>(i-1)) firstPath.rayListBE.set(i-1,this.rayListBE.get(i-1));
				else firstPath.rayListBE.add(i-1,this.rayListBE.get(i-1));

				if(firstPath.geometryFactorsBE.size()>(i-1)) firstPath.geometryFactorsBE.set(i-1,this.geometryFactorsBE.get(i-1));
				else firstPath.geometryFactorsBE.add(i-1,this.geometryFactorsBE.get(i-1));
			}
			
		}
		
		for(int i=pathLength-1; i>splitIndex; i--){
			
			int complementIndex = pathLength-1-i;
			
			if(secondPath.envList.size()>complementIndex) secondPath.envList.set(complementIndex,this.envList.get(i));
			else secondPath.envList.add(complementIndex,this.envList.get(i));
			
			if(secondPath.intersecList.size()>complementIndex) secondPath.intersecList.set(complementIndex,this.intersecList.get(i));
			else secondPath.intersecList.add(complementIndex,this.intersecList.get(i));
		
			if(secondPath.shaderList.size()>complementIndex) secondPath.shaderList.set(complementIndex,this.shaderList.get(i));
			else secondPath.shaderList.add(complementIndex,this.shaderList.get(i));
			
			secondPath.hitLights[complementIndex] 
			                     =this.hitLights[i];
			secondPath.specRefractionList[complementIndex] = this.specRefractionList[i];
			secondPath.specReflectionList[complementIndex] = this.specReflectionList[i];

//			if(complementIndex==0){
//				if(secondPath.weightListBE.size()>(complementIndex)) secondPath.weightListBE.set(complementIndex,this.weightListEB.get(i));
//				else secondPath.weightListBE.add(complementIndex,this.weightListEB.get(i));
//			}
			
			if(complementIndex>0){
				if(secondPath.weightListBE.size()>(complementIndex-1)) secondPath.weightListBE.set(complementIndex-1,this.weightListEB.get(i));
				else secondPath.weightListBE.add(complementIndex-1,this.weightListEB.get(i));
				
				if(secondPath.rayListBE.size()>(complementIndex-1)) secondPath.rayListBE.set(complementIndex-1,this.rayListEB.get(i));
				else secondPath.rayListBE.add(complementIndex-1,this.rayListEB.get(i));

				if(secondPath.geometryFactorsBE.size()>(complementIndex-1)) secondPath.geometryFactorsBE.set(complementIndex-1,this.geometryFactorsEB.get(i));
				else secondPath.geometryFactorsBE.add(complementIndex-1,this.geometryFactorsEB.get(i));
			}
			
			
		}
	}

	public void setValidFlag(boolean newVal){
		validFlag = newVal;
	}
	
	public boolean isValid(){
		return validFlag;
	}
	
	public String toString(){
		
		String s="CombinedPathValues \n   lightSourceId=" +lightID + " sensorSourceId=" +sensorID +"  dividerIndex=" +dividerIndex+"\n";
		s += "   " +super.toString();
		s+= "   Valid=" +validFlag+"\n";
		s += "   initalSpectrumEnd=" +this.initialSpectrumEnd+"\n";
		for(int i=0; i<pathLength; i++){
			if((i<(pathLength-1))&&(i<rayListEB.size()-1)){
				s+= "   rayListEB[" +i +"].direction=" +rayListEB.get(i).direction +"   DirDens[" +i+"]=" +rayListEB.get(i).directionDensity +"  ---  weightEB[" +i +"]=" +weightListEB.get(i);
			}else{
				s+= "   rayListEB[" +i +"].direction=not set" +"  ---  weightEB[" +i +"]=not set";
			}
			s += "\n";
		}
		if(pathLength>2) s+= "    Crossing Canvas in Pixel(" +rayListEB.get(pathLength-2).x +"," +rayListEB.get(pathLength-2).y +")";
		return s;
	}

}
