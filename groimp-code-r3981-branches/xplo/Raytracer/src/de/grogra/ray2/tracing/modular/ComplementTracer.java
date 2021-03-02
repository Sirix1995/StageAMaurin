/**
 * 
 */
package de.grogra.ray2.tracing.modular;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Scattering;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.Scene;
import de.grogra.ray2.tracing.PixelwiseRenderer;
import de.grogra.vecmath.geom.Line;

/**
 * @author Hagen Steidelmueller
 *
 */
public class ComplementTracer {
	
	static final double EPSILON = 1e-4d;
//	static float MAXVALUE = 0.0005f;

	private boolean hasCombEdgeFailure = false;
	
	
	static final int BEGIN2END =1;
	static final int END2BEGIN =2;
	static int DIRECTION;
	
	RayList rays = new RayList();
	
	Scene scene;
	Random random;
	
//	float[] complementedProbsArray;
//	float[] complementedGeomFacsArray;
//	boolean[] complementPathReflecList;
	PathValues startPathValues;
	PathValues complementPathValues;
	CombinedPathValues resultPath;
	int startIndex, complIndex;
	
	public ArrayList<Line> resultRayList;
	public ArrayList<Spectrum> resultWeightList;
	public ArrayList<Float> resultGeometryFactors;
	
	Vector3f tempVector1 = new Vector3f();
	Vector3f tempVector2 = new Vector3f();
	Vector3f tempVector3 = new Vector3f();
	
	Vector3f lastDirection = new Vector3f();
	
	
	int counter;
	int step;
	
	public ComplementTracer(Scene scene, Random random) {
		this.scene = scene;
		this.random = random;
		

	}
	
	
	
	
	public void complement2Paths(PathValues firstPath, int firstIndex, PathValues secondPath, int secondIndex, boolean adjoint, CombinedPathValues resultPath){
//		this.resultPath = resultPath;
//		this.resultPath.setValidFlag(true);
		
//		System.err.println("ComplementTracer_ver2: complement2Paths: s=" +firstIndex +" t=" +secondIndex);
		this.resultPath = resultPath;
		resultPath.merge2Paths(firstPath, firstIndex, secondPath, secondIndex);
		resultPath.pathResultList = secondPath.pathResultList;
		resultPath.lightID = firstPath.creatorID;
		resultPath.sensorID = secondPath.creatorID;

//		if((firstIndex<firstPath.rayListBE.size())&& !firstPath.rayListBE.get(firstIndex).valid) {
//			resultPath.setValidFlag(false);
//			return;
//		}
//		if((secondIndex < secondPath.rayListBE.size())&&!secondPath.rayListBE.get(secondIndex).valid) {
//			resultPath.setValidFlag(false);
//			return;
//		}
//		
//		if(firstPath.isSpecular(firstIndex)|| secondPath.isSpecular(secondIndex)){
//			resultPath.setValidFlag(false);
//			return;			
//		}
		
		
		
		resultPath.specReflectionList[firstIndex] = firstPath.specReflectionList[firstIndex];
		resultPath.specReflectionList[firstIndex+1] = secondPath.specReflectionList[secondIndex];
		resultPath.specRefractionList[firstIndex] = firstPath.specRefractionList[firstIndex];
		resultPath.specRefractionList[firstIndex+1] = secondPath.specRefractionList[secondIndex];
		
		
		if(PixelwiseRenderer.DEBUG_SUBPIXEL){
//		if(firstIndex>0 && firstIndex+secondIndex>2){
			System.err.println("ComplementTracer: compl2Paths:  Before s=" +firstIndex +" t=" +secondIndex);
			for(int i=0; i< (firstIndex); i++) System.err.println(" rLBE= " +resultPath.rayListBE.get(i).directionDensity);
			for(int i=0; i< (firstIndex); i++) System.err.println(" geomFacBE= " +resultPath.geometryFactorsBE.get(i));
			for(int i=0; i< (firstIndex+secondIndex+1); i++) System.err.println(" rLEB= " +resultPath.rayListEB.get(i).directionDensity);
			for(int i=0; i< (firstIndex+secondIndex+1); i++) System.err.println(" geomFacEB= " +resultPath.geometryFactorsEB.get(i));			
		}
		
//		System.err.println("ComplementTracer_ver2: complement2Paths: from light to eye");
		traceComplement(firstPath, firstIndex, secondPath, secondIndex, adjoint, BEGIN2END);
		
//		System.err.println("ComplementTracer_ver2: complement2Paths: from eye to light");
		if(resultPath.isValid()) traceComplement(secondPath, secondIndex, firstPath, firstIndex, !adjoint, END2BEGIN);

		if(PixelwiseRenderer.DEBUG_SUBPIXEL){
//		if(firstIndex>0 &&firstIndex+secondIndex>2){
			System.err.println("ComplementTracer: compl2Paths:  After  s=" +firstIndex +" t=" +secondIndex);
			for(int i=0; i< (firstIndex+secondIndex); i++) System.err.println(" rLBE= " +resultPath.rayListBE.get(i).directionDensity);
			for(int i=0; i< (firstIndex+secondIndex); i++) System.err.println(" geomFacBE= " +resultPath.geometryFactorsBE.get(i));
			for(int i=0; i< (firstIndex+secondIndex); i++) System.err.println(" rLEB= " +resultPath.rayListEB.get(i).directionDensity);
			for(int i=0; i< (firstIndex+secondIndex); i++) System.err.println(" geomFacEB= " +resultPath.geometryFactorsEB.get(i));			
		}
	}
	
	
	
	public void traceComplement(PathValues startPath, int startIndex, PathValues complementPath, int complIndex, boolean adjoint, int direct/*,
			float[] complementedProbs, float[] complementedGeomFacs, boolean[] complementPathReflecList*/){
		
		
		startPathValues = startPath;
		complementPathValues = complementPath; 		
		DIRECTION = direct;
		hasCombEdgeFailure = false;
		this.startIndex = startIndex;
		this.complIndex = complIndex;
		
		if(DIRECTION == BEGIN2END){
			resultRayList = resultPath.rayListBE;
			resultWeightList = resultPath.weightListBE;
			resultGeometryFactors = resultPath.geometryFactorsBE;	
			step=1;
			counter = startIndex-1;
		}else{
			resultRayList = resultPath.rayListEB;
			resultWeightList = resultPath.weightListEB;
			resultGeometryFactors = resultPath.geometryFactorsEB;
			step=-1;
			counter = complIndex+1;
		}
		
		
		Spectrum newWeight = scene.createSpectrum();		
		Scattering sh = startPathValues.shaderList.get(startIndex);
		Environment envS = startPathValues.envList.get(startIndex);
		Spectrum weight = (startIndex!=0)?startPathValues.weightListBE.get(startIndex-1): startPathValues.initialSpectrum;
		
		Environment envC = complementPathValues.envList.get(complIndex);
		tempVector1.sub(envC.point, envS.point);
		/*if(tempVector1.x+tempVector1.y+tempVector1.z>0)*/tempVector1.normalize();

		if(startIndex!=0) tempVector2.set(startPath.rayListBE.get(startIndex-1).direction);
		else tempVector2.set(0, 0,0);			
		tempVector2.negate();

		float dirDens = 1f;
		
		if (sh != null)
		{
try {
	//			sh.generateRandomRays (envS, tempVector1, weight, rays, adjoint, random);
				dirDens = sh.computeBSDF(envS, tempVector2, weight, tempVector1, adjoint, newWeight);
	//			newWeight.scale(1f/dirDens);
				

				if(startPathValues.isSpecular(startIndex))  newWeight.scale(1f/1e10f);
				
				if(dirDens<0.000001f) {
	//				System.err.println("ComplementTracer:traceComplement: Combining Edge produced NULL-BSDF !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
	//				System.err.println("ComplementTracer:traceComplement:  StartInind=" +startIndex +" ComplIndex=" +complIndex +( (DIRECTION == BEGIN2END)?" Begin2End" :" End2Begin") );
	//				System.err.println(" envS.point=" +envS.point + " tmpVec2=" +tempVector2 + "  weight=" +weight +"   tmpVec1=" +tempVector1 +"  newWeight=" +newWeight); 
	//				System.err.println("StartPath:" +startPath);
	//				System.err.println("ComplPath: "+ complementPath);
					hasCombEdgeFailure = true;
	//				System.exit(0);
	//				throw new ComplementException(" Error in Complementtracer: Zeror-Calculation of BSDF");
					
					
					//if the combining edge cut through the last light-path-object
					resultPath.setValidFlag(false);
					return; 
				}
} catch (UnsupportedOperationException e) {
	// TODO Auto-generated catch block
	newWeight.set (weight);
}
			
//			if(weight.sum()>1 && newWeight.sum()<0.01){
//				System.err.println("  ComplementTracer:traceComplement: The new Weight decreases very fast!");
//				System.err.println("  ComplPath: "+ complementPathValues);
//				System.err.println("  complementing: Vec1=" +tempVector1 +" <---- Env.point=" +envS.point   +" ----> Vec2="+tempVector2);
//				System.err.println("  Combination-Indices: start-I="+ startIndex +" compl-I="+complIndex);
////				System.exit(0);
//			}
			
		}
		else	newWeight.set (weight);

		
//		if((dirDens== 1e10f) && (startPathValues.isSpecular(startIndex))){
//			resultPath.setValidFlag(false);
//			return;
//		}
		
		Line combineEdge = new Line();
		combineEdge.origin.set(envS.point);
		combineEdge.direction.set(tempVector1);
		combineEdge.directionDensity = dirDens;
	
		float geoFac = calculateGeometryfactor(envC,complIndex,envS,0, tempVector1);
		
		counter += step;
		
		save2ResultPath(counter, combineEdge, newWeight, geoFac);
		
		Vector3f lD = new Vector3f();
		lD.set(tempVector1);
		lD.negate();
		if(complIndex >0) traceRecursively(complIndex-1, lD, newWeight, adjoint);
		
	}
	
	private void traceRecursively(int index, Vector3f lastDirection, Spectrum weight, boolean adjoint){
		
		Spectrum newWeight = scene.createSpectrum();
		
		Scattering sh = complementPathValues.shaderList.get(index+1);
		Environment env = complementPathValues.envList.get(index+1);
		tempVector1.set(complementPathValues.rayListBE.get(index).direction);
		tempVector1.negate();
		tempVector1.normalize();

		tempVector2.set(lastDirection);//complementPathValues.rayListBE.get(index+1).direction);

//		System.err.println("  complementing: Vec1=" +tempVector1 +" <---- Env.point=" +env.point   +" ----> Vec2="+tempVector2);
		
		float dirDens = 1f;
		
		rays.clear();
		rays.setSize(1);
		if (sh != null)
		{

			try {
				dirDens = sh.computeBSDF(env, tempVector2, weight, tempVector1, adjoint, newWeight);			

				if(complementPathValues.isSpecular(index+1))  newWeight.scale(1f/1e10f);
				
				if(dirDens<0.000001f) {
					
					//if the combining edge cut through the last eye-path-object 
					if(index+1 == complIndex){
//					System.err.println("ComplementTracer:traceRecursively: Another edge behind Combining produced NULL-BSDF ?????????????????????????????????????????????????????????????");
						resultPath.setValidFlag(false);
//					return; 
					}
					
					//else ERROR!!!
					if(PixelwiseRenderer.DEBUG_SUBPIXEL){
						System.err.println("ComplementTracer:traceRecursively: Another edge behind Combining produced NULL-BSDF ?????????????????????????????????????????????????????????????");
						System.err.println("  ComplTracer:traceRecursively:  DirectionDensity == ZERO  at " +index + " predeccor-Weight=  " +weight +"    And combining Edge has Failure=" +hasCombEdgeFailure);
//				System.err.println("  StartPath:" +startPathValues);
						System.err.println("  ComplPath: "+ complementPathValues);
						System.err.println("  complementing: Vec1=" +tempVector1 +" <---- Env.point=" +env.point   +" ----> Vec2="+tempVector2);
						System.err.println("  Combination-Indices: start-I="+ startIndex +" compl-I="+complIndex);

					}
					resultPath.setValidFlag(false);
					return;
//				System.err.println("ComplementTracer:traceComplement:  StartInind=" +startIndex +" ComplIndex=" +complIndex +( (DIRECTION == BEGIN2END)?" Begin2End" :" End2Begin") );
//				System.err.println("StartPath:" +startPath.creatorID);
//				for(int i=0; i< startPath.pathLength; i++) {
//					System.err.println("     env[" +i+ "].point=" +startPath.envList.get(i).point +"   ----   weight[" +i+"]=" +((i<startPath.pathLength-1)?startPath.weightListBE.get(i):"null"));
//				}
//				System.err.println("ComplPath: "+ complementPath.creatorID);
//				for(int i=0; i< complementPath.pathLength; i++) {
//					System.err.println("     env[" +i+ "].point=" +complementPath.envList.get(i).point +"   ----   weight[" +i+"]=" +((i<complementPath.pathLength-1)?complementPath.weightListBE.get(i):"null"));
//				}
//				throw new ComplementException(" Error in Complementtracer: Zeror-Calculation of BSDF");
				}
				
				
//			if(weight.sum()>1 && newWeight.sum()<0.01){
//				System.err.println("  ComplementTracer:traceRecursively: The new Weight decreases very fast!");
//				System.err.println("  ComplPath: "+ complementPathValues);
//				System.err.println("  complementing: Vec1=" +tempVector1 +" <---- Env.point=" +env.point   +" ----> Vec2="+tempVector2);
//				System.err.println("  Combination-Indices: start-I="+ startIndex +" compl-I="+complIndex);
////				System.exit(0);
//			}
			} catch (UnsupportedOperationException e) {
				// TODO Auto-generated catch block
				newWeight.set (weight);
			}
			
			
		}
		else newWeight.set (weight);

//		if((dirDens== 1e10f) && (complementPathValues.isSpecular(index+1))){
//			resultPath.setValidFlag(false);
//			return;
//		}
		
		Line newLine = new Line();
		newLine.origin.set(env.point);
		newLine.direction.set(tempVector1);
		newLine.directionDensity = dirDens;	
		
		float geoFac = getGeometryFactor(index, complementPathValues);

		counter+= step;
		
		save2ResultPath(counter, newLine, newWeight, geoFac);
		
		Vector3f lD = new Vector3f();
		lD.set(tempVector1);
		lD.negate();
		if(index>0) traceRecursively(index-1,lD,newWeight, adjoint);	
	}
	
	
	
	float getGeometryFactor(int index, PathValues complementPath){
		Environment env = complementPath.envList.get(index);
		Environment env_prev = complementPath.envList.get(index+1);

		tempVector3.sub(env.point, env_prev.point);
		tempVector3.normalize();

		
		return calculateGeometryfactor(env,index, env_prev, index+1,tempVector3);
	}
	
	
	public float calculateGeometryfactor(Environment env,int indexEnv , Environment env_prev, int indexEnvPrev, Vector3f vec){

		Vector3f oi = new Vector3f(vec);
		oi.normalize();
		if(env.normal.length()!=0)env.normal.normalize();
		if(env_prev.normal.length()!=0)env_prev.normal.normalize();

		float cosOut = ((env_prev.normal.length()!=0)? env_prev.normal.dot(oi): 1f);
		oi.negate();
		float cosIn = ((env.normal.length()!=0)? oi.dot(env.normal): 1f);
				
		float dist_sq = Math.max(1f,env.point.distanceSquared(env_prev.point));
//		float dist_sq = Math.min(1f,env.point.distanceSquared(env_prev.point));
//		float dist_sq = env.point.distanceSquared(env_prev.point);
		
//		if(PixelwiseRenderer.DEBUG_SUBPIXEL)System.err.println("ComplemtTracer:calculGeoFac: cosOut=" +cosOut +" cosIn=" +cosIn +" dist="+dist_sq);
		
		//TODO spielt hier irgendwo Math2.M_1_PI  mit rein? 
//		float geomFac= Math.abs(cosIn)/ (dist_sq);
//		float geomFac=  Math.abs(cosOut)/ dist_sq;
		float geomFac=  Math.abs(cosIn *cosOut)/ dist_sq;
//		float geomFac= 1f/ dist_sq;
		
		if(PixelwiseRenderer.DEBUG_SUBPIXEL)System.err.println("ComplemtTracer:calculGeoFac: cosOut=" +cosOut +" cosIn=" +cosIn +" dist="+dist_sq  +" --> geomFac=" +geomFac);
		
//		if(geomFac > 1)	System.err.println("CompleTracer:calcGeoFac: cosIn= " +cosIn +"  cosOut=" +cosOut +"  dist=" +dist_sq);
		
		return geomFac; //* MAXVALUE;
	}
	

	
	boolean isSpecularlyReflected(int index, Environment prevEnv){
		boolean ret = false;
		index = index-1;
		
		if(index!=0){
			Environment actEnv = complementPathValues.envList.get(index);
			tempVector1.sub(prevEnv.point, actEnv.point);
			tempVector1.normalize();
			
			tempVector2.set(complementPathValues.rayListBE.get(index-1).direction);
			tempVector2.negate();
			ret = (Math.abs(actEnv.normal.dot (tempVector1) - actEnv.normal.dot (tempVector2)) <= EPSILON );
		}
		return ret;
	}
	
	
	void save2ResultPath(int index, Line newLine, Spectrum newWeight, float newGeoFac/*, boolean newIsSpec*/){
		if(resultRayList.size()>index) resultRayList.set(index, newLine);
		else resultRayList.add(index, newLine);
		
		if(resultWeightList.size()>index) resultWeightList.set(index, newWeight);
		else resultWeightList.add(index, newWeight);	
		
		if(resultGeometryFactors.size()>index) resultGeometryFactors.set(index, new Float(newGeoFac));
		else resultGeometryFactors.add(index, new Float(newGeoFac));
		
		
	}
	
	
//	public void setMaxGeometrieFactorValue(float newMax){
//		MAXVALUE = newMax;
//	}
	
}
