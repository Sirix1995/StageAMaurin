package de.grogra.ray2.tracing.modular;

import java.util.ArrayList;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray2.Scene;
import de.grogra.vecmath.geom.Line;

public class MemoryHelper {

	ArrayList<Environment> freeEnvList;
	ArrayList<Environment> usedEnvList;
	ArrayList<Environment> markedEnvs2Remove;
	
	ArrayList<Line> freeLineList;
	ArrayList<Line> usedLineList;
	ArrayList<Line> markedLines2Remove;

	ArrayList<Spectrum> freeSpecList;
	ArrayList<Spectrum> usedSpecList;
	ArrayList<Spectrum> markedSpecs2Remove;
	
	
	int funcCallEnv, funcCallLine,funcCallSpec;
	
	public MemoryHelper( int length, Scene scene) {
		
		freeEnvList = new ArrayList<Environment>(length);
		usedEnvList = new ArrayList<Environment>(0);
		markedEnvs2Remove = new ArrayList<Environment>(length);		
		
		freeLineList = new ArrayList<Line>(length);
		usedLineList = new ArrayList<Line>(0);
		markedLines2Remove = new ArrayList<Line>(length);
		
		freeSpecList = new ArrayList<Spectrum>(length);
		usedSpecList = new ArrayList<Spectrum>(0);
		markedSpecs2Remove = new ArrayList<Spectrum>(length);
		
		for(int i=0; i< length; i++){
			freeEnvList.add(new Environment (scene.getBoundingBox (), scene.createSpectrum(), Environment.PATH_TRACER));
		}

		
	}
	
	public Environment getFreeEnvironment(){
		funcCallEnv++;
		Environment next = freeEnvList.remove(0);
		usedEnvList.add(next);
//		if(funcCall == 10000){
//			funcCall = 0;
//			System.err.println("EnvHelper: getFreeEnv: Only " +freeEnvList.size() +" Envs left to remove!");
//		}
		return next;
	}
	
	public Line getFreeLine(){
		funcCallLine++;
		Line next = freeLineList.remove(0);
		usedLineList.add(next);
//		if(funcCall == 10000){
//			funcCall = 0;
//			System.err.println("EnvHelper: getFreeEnv: Only " +freeEnvList.size() +" Envs left to remove!");
//		}
		return next;
	}
	
	
	public Spectrum getFreeSpectrum(){
		funcCallSpec++;
		Spectrum next = freeSpecList.remove(0);
		usedSpecList.add(next);
//		if(funcCall == 10000){
//			funcCall = 0;
//			System.err.println("EnvHelper: getFreeEnv: Only " +freeEnvList.size() +" Envs left to remove!");
//		}
		return next;
	}
	
	
	
	
	
	public void freeUnusedPathElements(CombinedPathValues activePath, CombinedPathValues inactivePath){

		for(Environment test: usedEnvList){
			
			if(! activePath.envList.contains(test) && !inactivePath.envList.contains(test)) {
				markedEnvs2Remove.add(test);
			}
		}
		for(Environment rem: markedEnvs2Remove){
			usedEnvList.remove(rem);
			freeEnvList.add(rem);
		}
		usedEnvList.trimToSize();
		freeEnvList.trimToSize();
		markedEnvs2Remove.clear();

		//--------------------------------------------------------
		for(Line test: usedLineList){
			
			if(! activePath.rayListBE.contains(test) && 
					! activePath.rayListEB.contains(test)&& 
					!inactivePath.rayListBE.contains(test)&&
					!inactivePath.rayListEB.contains(test)) {
				markedLines2Remove.add(test);
			}
		}
		for(Line rem: markedLines2Remove){
			usedLineList.remove(rem);
			freeLineList.add(rem);
		}
		usedLineList.trimToSize();
		freeLineList.trimToSize();
		markedLines2Remove.clear();
		//---------------------------------------------------------
		
		for(Spectrum test: usedSpecList){
			
			if(! activePath.weightListBE.contains(test) && 
					! activePath.weightListEB.contains(test)&& 
					!inactivePath.weightListBE.contains(test)&&
					!inactivePath.weightListEB.contains(test)) {
				markedSpecs2Remove.add(test);
			}
		}
		for(Spectrum rem: markedSpecs2Remove){
			usedSpecList.remove(rem);
			freeSpecList.add(rem);
		}
		usedSpecList.trimToSize();
		freeSpecList.trimToSize();
		markedSpecs2Remove.clear();
		
		
	}
	
	
}
