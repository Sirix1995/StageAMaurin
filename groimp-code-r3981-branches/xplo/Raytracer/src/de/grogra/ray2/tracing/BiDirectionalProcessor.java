/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
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

package de.grogra.ray2.tracing;

import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Scattering;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.Scene;
import de.grogra.ray2.tracing.modular.CausticMap;
import de.grogra.ray2.tracing.modular.CombinedPathValues;
import de.grogra.ray2.tracing.modular.ComplementException;
import de.grogra.ray2.tracing.modular.ComplementTracer;
import de.grogra.ray2.tracing.modular.LineTracer;
import de.grogra.ray2.tracing.modular.PathValues;
import de.grogra.ray2.tracing.modular.TracingMediator;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.Line;

/**
 *  
 * @author Hagen Steidelmüller
 */
public class BiDirectionalProcessor extends PathTracer
{

	public static final String EYEPATH_DEPTH = "BidirectionalPathTracer/eyeDepth";
	public static final String LIGHTPATH_DEPTH = "BidirectionalPathTracer/lightDepth";
	public static final String HEURISTIC_FACTOR = "BidirectionalPathTracer/heuristicFactor";
//	public static final String CORRECTION_FACTOR = "BidirectionalPathTracer/correctionFactor";
	
	
	public TracingMediator tracingMediator;
	public CausticMap loacalCausticMap=null;
	
	public boolean isMetropolis=false;
	
	static final double EPSILON = 1e-4d;
	static final int ERROR = -1;
	
	static final float  DELTA_FACTOR = 1e10f;
	
	static final float maxVal=Float.MAX_VALUE/10f;
	static final float minVal=Float.MIN_VALUE*10f;
	
	boolean debugOutput = false;
	boolean lastDebugOutput = false;
	boolean wasWrong = false;
	
	RayList tmpRayList;
	Environment tmpEnv;
	
	int heuristicExponent = 0;
	int eyePathDepth =0;
	int lightPathDepth =0;
//	private float correctionFlag=1;
	
	int totalLightCount;
	int lightIndex;
	
	public LineTracer lineTracer;
	ComplementTracer complementTracer;
	
	double[] probDensArray;
	
	Random random;
	Random rand; 
	
	//all necessary values for one eye path
	PathValues eyePathValues;
	
	//all necessary values for the paths of one light source
	PathValues[] lightPathValues;	
	
	CombinedPathValues combinedPath;
	

	
	float combinedProbDensEL =0;
	float combinedProbDensLE =0;
	Color3f sumColor = new Color3f();
	
	
	Color3f tmpColor1 = new Color3f();
	Color3f tmpColor2 = new Color3f();
	
	Vector3f tmpVector1 = new Vector3f();
	Vector3f tmpVector2 = new Vector3f();
	Vector3f tmpVector3 = new Vector3f();
	Vector3f tmpVector4 = new Vector3f();
	Line tmpLine = new Line();
	Vector3d tmpVectorD1= new Vector3d(); 
	Point3d tmpPoint1 = new Point3d();
	
	RayList tmpRays = new RayList();
	int loopIndex1,loopIndex2;
	
	private float transparency;
	private int pathLength;
	private int indexS, indexT;
	private Point3d combinedBSDFSpectrum = new Point3d();
	private Spectrum bsdfL_Spec;
	private Spectrum bsdfE_Spec;
	private float combinedBSDFSpec_Distsq;
	private int tmpSize;
	private double p_pre;
	private double next;
	private float prob;
	private float geomFac;
	
	
	public int bestS, bestT, bestLight;
	private Tuple3d maxWeight;
	
	public BiDirectionalProcessor ()
	{
		super();

	}

	public RayProcessor dup (Scene scene)
	{
		
		BiDirectionalProcessor p = (BiDirectionalProcessor) clone ();
		p.lightProcessor = lightProcessor.dup (scene);
		p.scene = scene;
		p.initLocals ();
		p.initialize(renderer, scene);
		return p;
	}
	
	
	
	public void initialize (PixelwiseRenderer renderer, Scene scene)
	{
		super.initialize (renderer, scene);

		rand = new Random(100);
		maxWeight = new Spectrum3d();
		
		//initialze the line tracer, which traces the line through the scene
		lineTracer = new LineTracer(this);
		lineTracer.initialize(renderer, scene);
		
		//initialize the complement tracer, which calculates the combined path from light to eye (see Veach p. 306)
		complementTracer = new ComplementTracer(scene, random);

		Light[] lights = scene.getLights();
		totalLightCount = lights.length;

		// the pathValue list for all light sources
		lightPathValues = new PathValues[totalLightCount] ;	
		
		//get the depth (maxium index of the path vertex) of the eye subpath
		eyePathDepth = renderer.getNumericOption(EYEPATH_DEPTH, 10).intValue();
		assert eyePathDepth >1;
 
		//get the depth (maxium index of the path vertex) of the light subpath
		 lightPathDepth = renderer.getNumericOption(LIGHTPATH_DEPTH, 10).intValue();
		 assert lightPathDepth >1;
		 
		 //get the heuristic exponent (see Veach p. 306)
		 heuristicExponent = renderer.getNumericOption(HEURISTIC_FACTOR, 2).intValue();
		 assert heuristicExponent >=0;
		 
		 System.err.println(" BdiPoc: initialize: heuristicExponent=" + heuristicExponent);
		 
//		 correctionFlag = renderer.getNumericOption(CORRECTION_FACTOR, 2).floatValue();
//		 assert correctionFlag >=0;


		 //initialize the PathValues-instance for the eye path wich store all neccessary information about the tracing
		 eyePathValues = new PathValues();
		 eyePathValues.initialize(eyePathDepth);
		 eyePathValues.creatorID =-1;
		 
		 // for all lights...
		 for (int i =0; i<totalLightCount ; i++)
		{
			 //initialize the PathValues-instance for the light path wich store all neccessary information about the tracing
			 lightPathValues[i] = new PathValues();
			 lightPathValues[i].initialize(lightPathDepth);
			 lightPathValues[i].creatorID = i;
		}
		 
		 //initialize the PathValues-instance which holds the information about the combined path (conform to Veach p. 306) 
		combinedPath = new CombinedPathValues();
		combinedPath.initialize(eyePathDepth + lightPathDepth); 
		
		//initialize the probability density quotient array (see Veach 10.9 on p. 306)
		probDensArray = new double[eyePathDepth + lightPathDepth +1];
		
		
//		lineTracer.setMaxGeometrieFactorValue(correctionFlag);
//		complementTracer.setMaxGeometrieFactorValue(correctionFlag);
		
		
		tracingMediator = new TracingMediator();
		tracingMediator.setRenderer(renderer);
		tracingMediator.setAntialiser(renderer.antialiasing);
		tracingMediator.setComplementTracer(complementTracer);
		tracingMediator.setLinetracer(lineTracer);
		tracingMediator.setProcessor(this);
		
		//some temporary variables
		bsdfL_Spec = scene.createSpectrum();
		bsdfE_Spec = scene.createSpectrum();
		tmpRayList = new RayList();
		tmpEnv = new Environment(scene.getBoundingBox (), scene.createSpectrum(), getEnvironmentType ());
	}
	
	
	public void initializeBeforeTracing(Random random){
	
		lineTracer.setRandom(random);
		this.random = random;
		lineTracer.setSafeMemoryMode(null);
		
		Light[] lights = scene.getLights();
		totalLightCount = lights.length;
	}
	
	
	
	public void getColorFromRay (Line ray, Spectrum resp, Color4f color, Random random)
	{
		bestS=-1;
		bestT=-1;
		bestLight=-1;
		maxWeight.set(-1, -2, -3);
		
		lineTracer.setSafeMemoryMode(null);
		
		lastDebugOutput = this.debugOutput;
		this.debugOutput = PixelwiseRenderer.DEBUG_SUBPIXEL;
		ilist.clear();

		sumColor.set(0,0, 0);

		//1) Trace eye path
		traceEyePath(ray, resp);
		
		//for all lights...
		Light[] lights = scene.getLights();
		if(eyePathValues.pathLength>=1){
			
//			System.err.println("BidiProc:  getColorFromRay:     eyePath=" +eyePathValues);
			
			for (lightIndex =0; lightIndex<lights.length ; lightIndex++){

				
				
				tmpRayList.clear ();
				tmpRayList.setSize(1);
				tmpEnv.localToGlobal.set(scene.getLightTransformation(lightIndex));
				tmpEnv.globalToLocal.set(scene.getInverseLightTransformation(lightIndex));
				Light light = lights[lightIndex];		
				
				if (light.getLightType() == Light.NO_LIGHT) continue;
				
//				System.err.println(" Bidi-Proces: getColorFromrRay: initiale Scene.loval2Global-Matrix (lightIndex=="+lightIndex +"): /n " + scene.getLightTransformation(lightIndex));
				
				//...2) generate new rays
				light.generateRandomOrigins(tmpEnv, tmpRayList, random);
				light.generateRandomRays(tmpEnv, null, tmpRayList.rays[0].spectrum, tmpRayList, false, random);
				Line newRay = tmpRayList.rays[0].convert2Line();
				Spectrum lightResp = tmpRayList.rays[0].spectrum;
							
//				lightResp.scale(1f/lightResp.getMax());
				traceLightPath(newRay,lightIndex, lightResp);
				
//				System.err.println(" Bidi-Proces: getColorFromrRay:   strating Spec=" +lightResp);
				
				// if there are more than only the first (automatically initilized) Vertices on both paths, do...
				if(lightPathValues[lightIndex].pathLength>=1) {
					
					//...6) combine eye and light path
					sumColor.add( combineEyeAndLightPath());
				}
			}			
		}
		
		sumColor.scale (getBrightness ());
		color.x = (float) sumColor.x;
		color.y = (float) sumColor.y;
		color.z = (float) sumColor.z;
		
		transparency = eyePathValues.pathResultList;
		color.w = (transparency < 1) ? 1 - transparency : 0;	
		
		if(loacalCausticMap != null){	
			int x = (int)eyePathValues.rayListBE.get(0).x;
			int y = (int)eyePathValues.rayListBE.get(0).y;
			loacalCausticMap.saveColor(null, x, y);
		}
	}

	
	
	/** 
	 * This function traces the eyepath only
	 * 
	 */
	PathValues traceEyePath(Line startRay, Spectrum initialEyeWeight){
		pathLength = determinePathLengthRandomly(eyePathDepth);
		return lineTracer.traceLine(pathLength, eyePathValues ,startRay,initialEyeWeight,0,false, random);
	}
	

	
	/**
	 * This function traces the light path only
	 * 
	 */
	PathValues traceLightPath(Line startRay, int lightId, Spectrum initialLightWeight){
		pathLength = determinePathLengthRandomly(lightPathDepth);
		return lineTracer.traceLine(pathLength, lightPathValues[lightId],startRay,initialLightWeight,lightId,true, random);
	}

	
	
	
	/**
	 * This method calculates the resulting sampling color for all light and eye subbpath combinations coformable to Veach chapter 10
	 */
	Color3f combineEyeAndLightPath(){
		
		tmpColor1.set(0, 0, 0);
		
		
		//for all subpathlengths (=index -1) of the light path do...
		for(indexS =/*0*/-1; indexS < lightPathValues[lightIndex].pathLength; indexS++){
			Environment lightEnv = null;
			Intersection lightIntsObject = null;
			
			//restore the environment at the actual light path vertex
			if(indexS>=0) {
				lightEnv  = lightPathValues[lightIndex].envList.get(indexS);
				lightIntsObject  = lightPathValues[lightIndex].intersecList.get(indexS);
			}
			
//			indexT = eyePathValues.pathLength-2-indexS;
	
			//for all subpathlengths (=index -1) of the light path except for the first edge between camera and scene do...
			for(indexT=0; indexT < (eyePathValues.pathLength/*-indexS*/); indexT++){
		
				//restore the environment and the intersection at the actual eye path vertex
				Environment eyeEnv = eyePathValues.envList.get(indexT);
				Intersection eyeInt = eyePathValues.intersecList.get(indexT);
				
				//if the eye path crossed a light source at this index (only for area lights)
				if(indexS<0){
					Light hitLight = eyePathValues.hitLights[indexT];
					if((hitLight!=null) && !hitLight.isIgnoredWhenHit() && (hitLight.getLightType() != Light.NO_LIGHT)){
						Point3d hitLightColor = lightSourceWasDirectlyHit(indexT, hitLight);
						double w_st = 1f/(indexT+1);
						hitLightColor.scale(w_st);
						tmpColor1.x += hitLightColor.x;
						tmpColor1.y += hitLightColor.y;
						tmpColor1.z += hitLightColor.z;
					}			
				}
				
//				if (PixelwiseRenderer.DEBUG_SUBPIXEL && (indexS>=0))
//				{
//					System.err.println(" BiDiProcessor: combineEyeAndLightPath:  My s=" +indexS +"  t=" +indexT +"    Eye-Env="+eyeEnv.point +"    Light-Env=" +lightEnv.point  );
//					System.err.println(" BiDiProcessor: combineEyeAndLightPath:  Visibility=" +isVisible(eyeEnv,eyeInt, lightEnv,lightIntsObject,indexS,indexT));
//				}
				
				//make a visibility test between s and t
				if((indexS>=0)&& (isVisible(eyeEnv,eyeInt, lightEnv,lightIntsObject,indexS,indexT) || 
						((indexT==0)&&(isVisible(lightEnv,null, eyeEnv,null,indexT,indexS))) )) {
					
					if(debugOutput ) System.err.println("BiDiProcessor: combineEyeAndLightPath: Visble at s: " +indexS + " -- t:" +indexT   +"  total = s+t+2=" +(indexS+indexT+2));
					

					//...4) calculate the complementary probability densities, line weights and geometry factors for the eye and light path (needed in Veach 10.9)
					complementTracer.complement2Paths(lightPathValues[lightIndex],indexS, eyePathValues,indexT, false, combinedPath);

					if(!combinedPath.isValid()) continue;
					
					if(debugOutput ) {
						System.err.println("BiDiProcessor: combineE&LPath: after Complementing!");
						System.err.println("  lightPath:" +lightPathValues[lightIndex]);
						System.err.println("*****************************************************");
						System.err.println("  eyePath:" +eyePathValues);
						System.err.println("*****************************************************");
						System.err.println("  combinedPath:" +combinedPath);
					}
					
										
					//combine both eye and light path at index s(light) and t(eye) (Veachs 10.8)
					Tuple3d unWeightedSpec = combine2Paths(indexS,indexT);

					//if the resulting spectrum is not ZERO
					if((unWeightedSpec.x + unWeightedSpec.y +unWeightedSpec.z)> 0.0001f){
											
						if(debugOutput ) System.err.println("BiDiProcessor: combineE&LPath: unWeightedSpec=" +unWeightedSpec);
						
						//for later metropolis calculation (not minded in bidirectional calculations)
						if(isMetropolis){
							chooseBestPathCombination(indexS,indexT,lightIndex, unWeightedSpec);
						}
						else{
							
							//calculate weightening factor (see Veach p. 306)
							float w_st = calcWeighteningFactor(indexS,indexT);
							
							if(debugOutput ) System.err.println("BiDiProcessor: combineE&LPath: w_st=" +w_st + "    w_st_reciprocal=" +(1/w_st));
		
							//scale the unweighted contribution with the weightening factor
							unWeightedSpec.scale(w_st);
							
							if((indexT==0)&& (loacalCausticMap!=null) && !combinedPath.isSpecular(indexS)){
								Color4f causticCol = new Color4f();
								
								causticCol.x = (float) unWeightedSpec.x;
								causticCol.y = (float) unWeightedSpec.y;
								causticCol.z = (float) unWeightedSpec.z;
								causticCol.scale (getBrightness ());
								
								float transparency = 0.0001f;//eyePathValues.pathResultList;
								causticCol.w = (transparency < 1) ? 1 - transparency : 0;
								
								float[] pix = ((BidirectionalRenderer)renderer).getPixelsForLine2Vertex(eyePathValues.envList.get(0), 
										new Point3d(lightPathValues[lightIndex].envList.get(indexS).point));
								if((pix[0]>=0) && (pix[1]>=0)) loacalCausticMap.saveColor(causticCol, (int)pix[0], (int)pix[1]);
								
							}else{
								//add to return color
								tmpColor1.x += unWeightedSpec.x;
								tmpColor1.y += unWeightedSpec.y;
								tmpColor1.z += unWeightedSpec.z;
								
							}
						}
					}else{
						if(debugOutput) System.err.println("BiDiProcessor: combineE&LPath: Combine Color is ZERO!!!!!!! s="+indexS +"   t="+indexT);
					}
				}
			}
		}

		// return sampling color
		return tmpColor1;
	}
	
	
	
	
	
	/**
	 * This function tests on visibility between the give vertexes
	 * 
	 * 
	 */
	boolean isVisible(Environment eyeEnv, Intersection eyeIntObject, Environment lightEnv, Intersection lightIntObject, int indexLightEnv, int indexEyeEnv){
		
//		if(indexEyeEnv==2) return true;
		
//		if(eyeIntObject.type == Intersection.LEAVING) System.err.println("BiDirectionalProcessor.isVisible()  EyeRay is in Object:" +eyeIntObject.solid); 
//		if((lightIntObject!=null) && (lightIntObject.type == Intersection.LEAVING)) System.err.println("BiDirectionalProcessor.isVisible()  LightRay is in Object:" +eyeIntObject.solid);		
		
//		Vector3f direction = new Vector3f();
		tmpVector1.sub(eyeEnv.point,lightEnv.point);
//		Line connectingEdge = new Line();
		
		
		tmpLine.origin.set(lightEnv.point);

		tmpLine.setLineAttributes(0, Double.POSITIVE_INFINITY);
		tmpVector1.normalize();
		tmpLine.direction.set(tmpVector1);
		
		tmpSize = ilist.size;
		if(( indexLightEnv!=0) && (tmpVector1.dot(lightEnv.normal)==0)) {
//			if(PixelwiseRenderer.DEBUG_SUBPIXEL ) System.err.println("BiDirectionalProcessor.isVisible()  on the same Surface!!!");
			return false;
		}
		Intersection exclude = null;
//		if((lightIntObject!=null) && (lightIntObject.solid == eyeIntObject.solid)) */ exclude =null;
		scene.computeIntersections (tmpLine, Intersection.CLOSEST,
			ilist, exclude, null);
		

		if(ilist.size > tmpSize) { 
			Intersection calcInts =  ilist.elements[tmpSize];
			tmpPoint1.set(eyeEnv.point);						
			double dist1 = calcInts.getPoint().distance((tmpPoint1));
//			double dist2 = (correctionFlag==0) ? 2*EPSILON  :  calcInts.getPoint().distance((eyeIntObject.getPoint()));
			
			//if both points are nearly the same, then return true
			if ((dist1 <= EPSILON)){//||(dist2 <= EPSILON)) {
//				if(PixelwiseRenderer.DEBUG_SUBPIXEL )System.err.println("BiDirectionalProcessor.isVisible()  EyeRay is in Object:" +eyeIntObject.solid +"  at t=" +indexEyeEnv +"and is visble!!!!");
//				System.err.println("BiDirectionalProcessor.isVisible()  Correct Object:" +calcInts.solid +" was hit by a ray from Object:" +((lightIntObject!=null)?lightIntObject.solid:"LightSource"));
				return true;
			}
		}//else	if(PixelwiseRenderer.DEBUG_SUBPIXEL ) System.err.println("BiDirectionalProcessor.isVisible()  No Object was hit by a ray from Object:" +((lightIntObject!=null)?lightIntObject.solid:"LightSource") +" to Object:" +eyeIntObject.solid);
		
		return false;
	}
	
	
	
	
	
	
	/**
	 * combines eye and light path accordng to Veach (10.8)
	 */
	Tuple3d combine2Paths(int lightPathVertex, int eyePathVertex){
		
		combinedBSDFSpectrum.set(0, 0, 0);
		
		Scattering shaderE = eyePathValues.shaderList.get(eyePathVertex);
		Scattering shaderL = lightPathValues[lightIndex].shaderList.get(lightPathVertex);
		Environment envE = eyePathValues.envList.get(eyePathVertex);
		Environment envL = lightPathValues[lightIndex].envList.get(lightPathVertex);
		
		Spectrum lightPathWeight = (lightPathVertex>0) ? lightPathValues[lightIndex].weightListBE.get(lightPathVertex-1) :  lightPathValues[lightIndex].initialSpectrum;
		Spectrum eyePathWeight = (eyePathVertex>0) ? eyePathValues.weightListBE.get(eyePathVertex-1) : eyePathValues.initialSpectrum;
		
		// calculate all neccessary vectors
		
		//in vector on eye path
		if(eyePathVertex>0)tmpVector3.set(eyePathValues.rayListBE.get(eyePathVertex-1).direction);
		else tmpVector3.set(0,0,0);
		tmpVector3.negate();
		
		//in vector on light path
		if (lightPathVertex>0)tmpVector4.set(lightPathValues[lightIndex].rayListBE.get(lightPathVertex-1).direction);
		else tmpVector4.set(0,0,0);
		tmpVector4.negate();

		//out vector on eye path
		tmpVector1.sub(envL.point,envE.point);
		tmpVector1.normalize();

		//out vector on light path
		tmpVector2.sub(envE.point,envL.point);
		tmpVector2.normalize();

		//compute BSDF from light direction
		combinedProbDensLE = shaderL.computeBSDF(envL,  tmpVector4, lightPathWeight,tmpVector2, false, bsdfL_Spec);
		
		//calculate the distance between both vertices
		combinedBSDFSpec_Distsq = Math.max(1,envE.point.distanceSquared(envL.point));
//		combinedBSDFSpec_Distsq = Math.min(1f,envE.point.distanceSquared(envL.point));
//		combinedBSDFSpec_Distsq = envE.point.distanceSquared(envL.point);
		
		try {
			//calculate BSDF form eye direction
			combinedProbDensEL = shaderE.computeBSDF(envE, tmpVector3,eyePathWeight , tmpVector1, true, bsdfE_Spec);
		} catch (UnsupportedOperationException e) {
			bsdfE_Spec.set(eyePathWeight);
		}
		
		//multiply both BSDFs
		bsdfE_Spec.dot(bsdfL_Spec, combinedBSDFSpectrum);
		
		//************************************************************************************

//		if (PixelwiseRenderer.DEBUG_SUBPIXEL)
//		{
//			System.err.println("BiDiPT: comb2Paths: My Eye-Env="+envE.point +"    Light-Env=" +envL.point );
//			System.err.println("BiDiPT: comb2Paths: My tmpVector3(view)="+tmpVector3 +"     tmpVector1(out)=" +tmpVector1);
//			System.err.println("BiDiPT: comb2Paths: Resulting Eye-BSDF=" +bsdfE_Spec);
//		}
		
		//scale the resulting spectrum with the distance
		combinedBSDFSpectrum.scale(1f/*Math2.M_1_PI*//(combinedBSDFSpec_Distsq));
		
		if(eyePathValues.isSpecular(eyePathVertex)) combinedBSDFSpectrum.scale(1f/1e10f);
		if(lightPathValues[lightIndex].isSpecular(lightPathVertex)) combinedBSDFSpectrum.scale(1f/1e10f);
 
//		if((PixelwiseRenderer.DEBUG_SUBPIXEL)&&(combinedBSDFSpectrum.x==0 && combinedBSDFSpectrum.y==0 && combinedBSDFSpectrum.z==0)) {
//			System.err.println(" BiDiPT: comb2Paths t=" +eyePathVertex +"  Type=" +( (eyePathValues.intersecList.get(eyePathVertex)!=null) ? eyePathValues.intersecList.get(eyePathVertex).type : "null") );
////			if((eyePathVertex==2) && (eyePathValues.intersecList.get(2).type == Intersection.LEAVING)){
//				System.err.println(" BiDiPT: comb2Paths E-Weight=" +eyePathWeight +"   L-Weight=" +lightPathWeight);
//				System.err.println(" BiDiPT: comb2Paths Pd E-->L=" +bsdfE_Spec +"  Pd L-->E=" +bsdfL_Spec +"    -->    comb. Bsdf=" +combinedBSDFSpectrum );
//				System.err.println(" BiDiPT: comb2Paths Shader=" +shaderE );
////			}
//		}
		return combinedBSDFSpectrum;
	}

	
	
	
	
	
	

	
	
	
	/**
	 * calculates the weightening factor w<sub>s,t</sub> (see Veach p.306)
	 */
	float calcWeighteningFactor(int s, int t){
		
		p_pre = 1f;
		probDensArray[s+1] = p_pre;

		//calculation of the probability density quotients conformable to Veach 10.9 in eye path direction
		for(loopIndex1=s+1; loopIndex1<= (s+t+1); loopIndex1++){
			p_pre = probDensArray[loopIndex1];
			probDensArray[loopIndex1+1] = getEnumerator(loopIndex1,s,t)*p_pre / getDenomirator(loopIndex1, s, t);
//			probDensArray[loopIndex1] = getDenomirator(loopIndex1, s, t)*p_pre /getEnumerator(loopIndex1,s,t) ;
//			loopIndex1f(loopIndex1==0){
//				loopIndex1f(debugOutput) System.err.prloopIndex1ntln("BloopIndex1DloopIndex1Processor:calcWeloopIndex1ghtFac: !!!!!!!!!!!!!!!!!!!!!   "+getEnumerator(loopIndex1,s,t)+" * " +p_pre +" / " +getDenomloopIndex1rator(loopIndex1, s, t));
//			}
		}

		//calculation of the probability density quotients conformable to Veach 10.9 in light path direction
		for(loopIndex1=s; loopIndex1>=0; loopIndex1--){
			p_pre = probDensArray[loopIndex1+1];
			probDensArray[loopIndex1] = getDenomirator(loopIndex1, s, t)*p_pre /getEnumerator(loopIndex1,s,t) ;
//			probDensArray[loopIndex1+1] = getEnumerator(loopIndex1,s,t)*p_pre / getDenomirator(loopIndex1, s, t);
		}
			
//		if(t==0){
//			boolean test = false;
//			int specIndex = -1;
//			for(int i=0; i < combinedPath.pathLength; i++){
//				if(combinedPath.isSpecular(i)){
//					test = true;
//					specIndex = i;
//					break;
//				}
//			}
//			if(test){
//				System.err.println("BidiProc:   calcWeightning for specular path at index "+ specIndex +": ");
//				for(int i=0; i< s+t+2; i++){
//					System.err.println("   probDens[" +i+"]=" +probDensArray[i]);
//				}
//			}
//		}
		
//		correctSpecularReflection(s, t);
		
		//calculation of the sum of all quotients
		float w_st = 0f;
		for(loopIndex1=/*0*/1; loopIndex1< (s+t+2/*3*/); loopIndex1++){

			if(debugOutput) System.err.println("BiDiProcessor:calcWeightFac:    pD[" +loopIndex1 +"]=" +probDensArray[loopIndex1] +"   heuristicFac=" +heuristicExponent);
			next = Math.pow((probDensArray[loopIndex1]), heuristicExponent);
			w_st += next; 
//			w_st += (next<=maxVal) ? ((next>=minVal) ? next : minVal) : maxVal;
//			if(w_st>=maxVal) w_st = maxVal;
//			if(w_st<=minVal) w_st = minVal;
		}

		if(Float.isInfinite(w_st)|| Float.isNaN(w_st)){
//			System.err.println("Fehler!");
		}
		return 1f/w_st;
	}

	/**
	 * This Function calculates the enumerator of the probability density quotient (see Veach 10.9)
	 * 
	 * @param index
	 * @param s
	 * @param t
	 * @return
	 */
	float getEnumerator(int index,int s, int t){
	
		if(index==0){
			prob = DELTA_FACTOR;//combinedPath.rayListBE.get(0).originDensity;
//			prob = 1f;
			geomFac = 1f;
		}else{
			prob = combinedPath.rayListBE.get(index-1).directionDensity;
			geomFac = combinedPath.geometryFactorsBE.get(index-1);				
		}
	
		if(debugOutput) System.err.println("BiDiPoc_ver2:getEnumerator() index = " +index +" ProbDens=" +prob +"  GeomFac=" +geomFac);
		
		if((prob*geomFac )==0){
//			System.err.println("Fehler!");
		}	
		return prob*geomFac;
	}
	
	
	/**
	 * This Function calculates the denomirator of the probability density quotient (see Veach 10.9)
	 * 
	 * @param index
	 * @param s
	 * @param t
	 * @return
	 */	
	float getDenomirator(int index, int s, int t){

		if(index == (s+t+1)){
			prob = DELTA_FACTOR;//combinedPath.rayListEB.get(0).originDensity;
//			prob = 1f;
			geomFac = 1f;
		}else{
			prob = combinedPath.rayListEB.get(index).directionDensity;
			geomFac = combinedPath.geometryFactorsEB.get(index);				
		}
		
		
		if(debugOutput) System.err.println("BiDiPoc_ver2:getDenomirator() index = " +index +" ProbDens=" +prob +"  GeomFac=" +geomFac); 

		return prob*geomFac;
	}
	
	
	/**
	 * This function calculates the resulting spctrum, when the eye path directly hits a light source
	 * 
	 * 
	 * @param t
	 * @param hitLight
	 * @return
	 */
	Point3d lightSourceWasDirectlyHit(int t,Light hitLight){
		

		Environment actualEnv = eyePathValues.envList.get(t);
		// ray hits light source

		Spectrum weight = eyePathValues.weightListBE.get(t-1);
		
		int lightIndex=-1;
		for(int i=0; i<scene.getLights().length; i++){
			if(hitLight == scene.getLights()[i]) lightIndex =i;
		}
		
		actualEnv.localToGlobal.set (scene.getLightTransformation (lightIndex));
		actualEnv.globalToLocal.set (scene.getInverseLightTransformation (lightIndex));
		hitLight.computeExitance (actualEnv,bsdfE_Spec);


		tmpVector1.set(eyePathValues.rayListBE.get(t-1).direction);
		tmpVector1.negate();
		
		hitLight.computeBSDF (actualEnv, null, bsdfE_Spec, tmpVector1, true, bsdfL_Spec);

		tmpPoint1.set(0,0,0);
		bsdfL_Spec.dot (weight, tmpPoint1);	
		
		return tmpPoint1;
	}
	
	
	
	
	int determinePathLengthRandomly(int seed){
		return seed;		
	}
	
	/**
	 * This function deals with the special handling of specular surfaces according to Veach on page 314
	 * 
	 * @param s
	 * @param t
	 */
	void correctSpecularReflection(int s, int t){
		
		boolean isSpec = false;
		for(int i=0; i< s+t+2; i++){
			isSpec = false;

			isSpec = combinedPath.isSpecular(i);
			
			if(isSpec){
				probDensArray[i] =0;
				probDensArray[i+1] =0;
			}
		}
		
	}
	

	public void chooseBestPathCombination(int s, int t,int lightId, Tuple3d combWeight){
		
		
		double sumMax = maxWeight.x+maxWeight.y+maxWeight.z;
		double sumCW = combWeight.x+combWeight.y+combWeight.z;
		if(sumMax < sumCW) {
			bestS = s;
			bestT = t;
			bestLight = lightId;
			maxWeight.set(combWeight);
		}
		
//		System.err.println(" BiDi-Proces: chooseBPComb: tests " +combWeight +" at s= " +s +" t= " +t +"  -->  with result " +maxWeight +" [" +bestLight +"]:"+bestS+" - " +bestT);
	}
	
	
	
	@Override
	protected void appendStatisticsImpl(StringBuffer stats) {
		stats.append("Bidirectional Path Tracing Statistics:\n");

	}
	
	
}


