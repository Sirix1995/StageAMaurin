package de.grogra.ray2.tracing.modular;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Scattering;
import de.grogra.ray.physics.Sensor;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.ray.tracing.BidirectionalPathTracer;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.Renderer;
import de.grogra.ray2.Scene;
import de.grogra.ray2.tracing.BiDirectionalProcessor;
import de.grogra.ray2.tracing.MetropolisRenderer;
import de.grogra.ray2.tracing.PathTracer;
import de.grogra.ray2.tracing.PixelwiseRenderer;
import de.grogra.ray2.tracing.RayProcessor;
import de.grogra.ray2.tracing.RayProcessorBase;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;
import de.grogra.vecmath.geom.Volume;

public class LineTracer extends PathTracer
{
	static final double EPSILON = 1e-4d;
	private static final float MIN_WEIGHT = 0.01f;
//	private static float MAXVALUE = 0.0005f;

	
	public static final int SPECULAR_CONDITION=1;
	public int STOP_CONDITION=1;
	public ConditionObject condition = null;
	
	
	public int abbortCode;
	public static final int MAX_DEPTH_REACHED = 1;
	public static final int NO_OBJECT_HIT = 2;
	public static final int OUTGOING_RAY_TOO_WEAK = 3;
	public static final int RAY_WAS_INFINITE = 4;
	public static final int RAY_WAS_ABSORBED = 5;
	public static final int RAY_WAS_STOPPED_ON_CONDITION = 6;
	
	public boolean traceSubPath = false;
//	public boolean saveMemoryMode = true;
	MemoryHelper envHelper;
	
	public int recursionCounter;
	
	BiDirectionalProcessor processor;
	
	//general path objects and variables
//	public Scene scene;
//	Renderer renderer;
	Random randomCreation;
	int maxDepth;
	boolean isLightRay;
	String kind;
	
	//the record object to store all neccessary infos about the path
	PathValues pV= null;
	

	IntersectionList ilist = null;
	float outgoingDirectionDensity;
	float actualGeometryFactor;
	
	
	//Intersection values
	RayList rays= null;
	Vector3f view= null;
//	Environment actualEnv= null;
//	Environment lastEnvironment= null;
	Light hitLight= null;
	Sensor hitSensor= null;
	int lightID;
	int sensorID;
	boolean finite;
	Shader shader= null;

	Spectrum3d tmpSpec;
	RayList tmpRaylist;
	Environment tmpEnv;
	
//
//
//	private ArrayList<Volume> enteredSolids;
	
	
	public LineTracer(BiDirectionalProcessor processor){
		this.processor = processor;
		
	}
	
	public void initialize(PixelwiseRenderer renderer, Scene scene){
		super.initialize(renderer, scene);
		
		ilist = new IntersectionList();
		rays = new RayList(1);
		view = new Vector3f();
		
		tmpSpec = new Spectrum3d();
		tmpRaylist = new RayList();
		tmpEnv = new Environment(scene.getBoundingBox (), scene.createSpectrum(),Environment.PATH_TRACER);
	}
	
	
	public void setRandom(Random random){
		randomCreation = random;		
	}
	
	/**
	 * Defines the maximum depth of recursion, where maxDepth is the index of the last path vertex
	 * 
	 * @param maxDepth
	 */
	public void setMaxDepth(int maxDepth){
		this.maxDepth = maxDepth;
		enteredSolids = new ArrayList<Volume> (maxDepth);
		
//		pV.initialize(this.maxDepth);
	}
	
	
	/**
	 * Defines if this tracing is about light ray tracing or importance ray tracing
	 * 
	 * @param isLightRay
	 */
	public void set2LightPathTracing(boolean isLightRay){
		this.isLightRay = isLightRay;
		if(isLightRay) kind = "light";
		else kind = "eye";	
	}
	
	/**
	 * This function traces a Line(=ray) throug the scene starting at the first path vertex (index =0) 
	 * 
	 * @param maxDepth - the maximum path depth (=the index of the last path vertex)
	 * @param pathValues - In this pathvalue-instance the results of the tracing will be stored 
	 * @param startLine - the inital line, which has to be traced further
	 * @param initialWeight - the weight of the initial line
	 * @param sourceID - the id of the line source (light or camera)
	 * @param isLightRay - the boolean for checking, wether this is line is a light ray or importance ray
	 * @param random - the randomizer
	 * @return the path result
	 */
	public PathValues traceLine(int maxDepth, PathValues pathValues, Line startLine, Spectrum initialWeight, int sourceID, boolean isLightRay, Random random){
//		System.err.println("LineTracer: traceLine: maxDepth" +maxDepth);
		clearVariables();
		
		pV = pathValues;
		
		setRandom(random);
		setMaxDepth(maxDepth);
		set2LightPathTracing(isLightRay);
		
		//initialize the first Pathvertex
		initializeFirstPathVertex(startLine, sourceID, initialWeight);
		
		recursionCounter =0;
		//start tracing
		float result = traceRecursively(1,startLine,null,initialWeight);
		pV.saveResult(result);
		return pV;
	}
	
	/**
	 * This function initializes the nessecary values of the first path vertex at position 0. 
	 * This means, especially for the first light path vertex there have to be initialized the transformations and the light shader 
	 * 
	 * @param startLine - the first ray
	 * @param sourceID - the id of the source (light or camera)
	 * @param initialWeight - the weigth/spectrum of the first ray
	 */
	private void initializeFirstPathVertex( Line startLine, int sourceID, Spectrum initialWeight){
		
		// get first Environment
		Environment env0;
		//if pVList already has elements take them
		if(pV.envList.size()>0)	env0 = pV.envList.get(0);
		//else create a new Environment
		else env0 = new Environment(scene.getBoundingBox (), scene.createSpectrum(), Environment.PATH_TRACER);
		env0.iorRatio = 1;
		Scattering sh0 = null;
		if(isLightRay){
			//if is light ray tracing set light environment
			env0.localToGlobal.set(scene.getLightTransformation(sourceID));
			env0.globalToLocal.set(scene.getInverseLightTransformation(sourceID));
			sh0= scene.getLights()[sourceID];
		}else{
			sh0= renderer.getCamera();
			env0.localToGlobal.set (renderer.getCameraTransformation ());
			env0.globalToLocal.m33 = 1;
			Math2.invertAffine (env0.localToGlobal, env0.globalToLocal);
//			sh0= scene.getSensors()[0];
			//else: is eye ray tracing: set correct directiondensity
//			sh0= scene.getSensors()[0];
//			if(sh0==null){
//				System.err.println(" LineTRacer: initFirstVErtex:  Eye Sensor Shader == null!!!!!!!!!");
//				System.exit(0);
//			}
			startLine.directionDensity = Math2.M_1_2PI / 2;
		}
		env0.point.set(startLine.origin);
		
		Float p0 = new Float(startLine.directionDensity);
		Intersection ins0 = null;
		
		// save first values to the result path
		pV.saveValues(0, startLine, ins0, env0, initialWeight, sh0, false, false);
		pV.initialSpectrum.set(initialWeight);
		pV.saveProbabilityDensity(0,p0);
//		pV.saveGeometryFactor(0,geoF0);
		pV.savePathLenght(1);
	}
	
	
	/**
	 * trace recursively the Line through the scene
	 * 
	 * 
	 * @param depth - the actual path depth (conform to Veach's path vertices)
	 * @param incomingRay - the ray before
	 * @param lastIntersection - the intersection before
	 * @param incomingWeight - the weight of the ray before
	 * @return the path result
	 */
	private float traceRecursively(int depth, Line incomingRay, Intersection lastIntersection, Spectrum incomingWeight){
		
		abbortCode = -1;
		recursionCounter++;
		
//		System.err.println("LineTracer:traceRec: Trace "+kind +" ray in depth:" +depth);

		//initialize temporary trace variables
		float result =0;
		Line outgoingRay = null;
		Spectrum3f outgoingWeight = null; 
		Environment lastEnv = pV.envList.get(depth-1);
		
		//process next intersection
		int ilistSize = ilist.size;
		Intersection actualIntersection = getNextIntersection(incomingRay,lastIntersection);
		
		//if there was an intersection
		if(actualIntersection != null){

			//initilaizes the actual intersection values 
			Environment actualEnv = initializeEnvironment(depth,actualIntersection,incomingWeight, actualIntersection.line.direction);
			
			// callculate the Geometry factor for the last ray
			actualGeometryFactor = calculateGeometryFactor(depth,actualEnv, lastEnv);
			
			//if there was an intersection in finite space and the trace isn't deep enough
			if((finite) && (depth < maxDepth)){
								
				//get next ray for further tracing
				outgoingRay = getNextRayAndSpec(depth, actualEnv,actualIntersection,incomingWeight, incomingRay.direction);
				
				// the weight=spectrum of the outgoing ray
				outgoingWeight = new Spectrum3f(outgoingRay.spectrum.x,outgoingRay.spectrum.y,outgoingRay.spectrum.z);

				//is there an specular reflection at the actual environment?
				boolean specReflected = ( Math.abs(actualEnv.normal.dot (view) - actualEnv.normal.dot (new Vector3f(outgoingRay.direction))) <= EPSILON );
				boolean specRefracted = ((actualEnv.iorRatio!=1)||(actualIntersection.type == Intersection.LEAVING) ) ? true : false;
				//save actual variables to the result path
				saveActualValues(depth,outgoingRay,actualIntersection,actualEnv,outgoingWeight, specReflected,specRefracted);
				if((condition== null)|| (!condition.stopOnCondition())){
					if(outgoingRay.valid) {
						//if weigth of ray high enough
						if(outgoingWeight.integrate() > MIN_WEIGHT){
		
							//for transmission: save actual intersection object (th object in which the ray may be refracted)
							int i = record(actualIntersection, outgoingRay.reflected);
		
							// trace deeper
							result = traceRecursively(depth+1,outgoingRay,actualIntersection,outgoingWeight);
							
		
							//after recursion: pop the actual intersection object from the transmission list
							unrecord(actualIntersection, i);
							
							ilist.setSize(ilistSize);
							
						}else{
							abbortCode = OUTGOING_RAY_TOO_WEAK;
							result=0;
						}
					}else{
						abbortCode = RAY_WAS_ABSORBED;
					}
				}else{
					abbortCode = RAY_WAS_STOPPED_ON_CONDITION;
				}
				if(outgoingRay.reflected) result =0;				
			}else if(!finite){
				abbortCode = RAY_WAS_INFINITE;
			}else {
				abbortCode = MAX_DEPTH_REACHED;
				
			}
			ilist.setSize(ilistSize);
		}
		else {
			abbortCode = NO_OBJECT_HIT;
			result = (float)incomingWeight.integrate();
		}

		return result;
	}

	
	/**
	 * Calculates the next Intersection of the ray
	 * 
	 * @param incomingRay - the actual ray
	 * @param lastIntersection - the former intersection, needed for excluding
	 * @return the next intersection on this path
	 */
	Intersection getNextIntersection(Line incomingRay, Intersection lastIntersection){
		
		int size = ilist.size;
		
//		if(traceSubPath) System.err.println("LineTracer: getNxtInt:  recDepth=" +recursionCounter +"   last Intersection=" +((lastIntersection!=null)?lastIntersection.getPoint():"null"));
		
		// compute next intersection
		scene.computeIntersections (incomingRay, Intersection.CLOSEST,ilist, lastIntersection, null);
		
		if(ilist.size > size) {
//			if(traceSubPath) System.err.println("LineTracer: getNxtInt:  recDepth=" +recursionCounter +"   next Intersection=" +ilist.elements[size].getPoint() +"  Kontrolle: last Intersection=" +((lastIntersection!=null)?lastIntersection.getPoint():"null")  +"  iList.Size=" +ilist.size);
			return ilist.elements[size].deepCopy();
		}
		return null;
	}

	
	/**
	 * Initializes the Environment at the actual path position (indices are conform to Veach)
	 * 
	 * @param depth
	 * @param actualIntersection
	 * @param incomingWeight
	 * @return the Environment at the path vertex
	 */	
	Environment initializeEnvironment(int depth, Intersection actualIntersection,Spectrum incomingWeight, Vector3d incommingDirection){
		
		finite = actualIntersection.parameter < Double.POSITIVE_INFINITY;
		
		view.set (incommingDirection);
		view.negate ();
		assert Math.abs (view.lengthSquared () - 1) < 1e-4f;

		shader = scene.getShader (actualIntersection.volume);
		
		lightID = scene.getLight (actualIntersection.volume);
		hitLight = (lightID >= 0) ? scene.getLights ()[lightID] : null;

		sensorID = scene.getSensor(actualIntersection.volume);
		hitSensor = (sensorID >= 0) ? scene.getSensors ()[sensorID] : null;
		
		Environment actualEnv;
		if((envHelper==null) && (pV.envList.size()>depth)) actualEnv = pV.envList.get(depth);	
		else if(envHelper!=null){
			actualEnv = envHelper.getFreeEnvironment();
		}else actualEnv = new Environment (scene.getBoundingBox (), scene.createSpectrum(), Environment.PATH_TRACER);
		
		actualEnv.set (actualIntersection, ((shader != null) ? shader.getFlags () : 0) | ((hitLight != null) ? hitLight.getFlags () : 0), scene);
		actualEnv.iorRatio = (float)getIOR(actualIntersection, incomingWeight); //(float) record (actualIntersection, true, incomingWeight);
		actualEnv.point.set((Point3d)actualIntersection.getPoint().clone());
		actualEnv.normal.set((Vector3d)actualIntersection.getNormal().clone());

//		if(isLightRay && (depth>1)) System.err.println(" LineTracer: initEnv: dept=" +depth +"  IOR=" +actualEnv.iorRatio);
		
		return actualEnv;
	}
	
	
	/**
	 * Calculates the geometray term values (see Veach p. 303)
	 * 
	 * @param index - actual vertex index
	 * @param actEnv - actual environment
	 * @param lastEnv - former environment (index -1)
	 * @return the geometry term value
	 */
	float calculateGeometryFactor(int index, Environment actEnv, Environment lastEnv){
		Vector3f out = new Vector3f();
		out.sub(actEnv.point,lastEnv.point );
		out.normalize();
		if(lastEnv.normal.length()!=0)lastEnv.normal.normalize();
		if(actEnv.normal.length()!=0) actEnv.normal.normalize();
		
		double cosOut = ((lastEnv.normal.length()!=0)? lastEnv.normal.dot(out): 1f);
		
		out.negate();
		double cosIn = ((actEnv.normal.length()!=0)?actEnv.normal.dot(out):1f);
		
		double dist_sq = Math.max(1f,actEnv.point.distanceSquared(lastEnv.point));
//		double dist_sq = Math.min(1f,actEnv.point.distanceSquared(lastEnv.point));
//		double dist_sq = actEnv.point.distanceSquared(lastEnv.point);
		
		float geomFac=(float)(Math.abs(cosOut*cosIn)/(dist_sq));
//		float geomFac=(float)(Math.abs(cosIn)/ dist_sq);
//		float geomFac=(float)(Math.abs(cosOut)/(dist_sq));
//		float geomFac= (float)(1f/ dist_sq);
		
//		if(geomFac > 1)	System.err.println("LineTracer:calcGeoFac: cosIn= " +cosIn +"  cosOut=" +cosOut +"  dist=" +dist_sq);
		
		return geomFac;// * MAXVALUE;
	}
	
	

	/**
	 * Calculates the next Line, which is reflected or refracted at the actual environment
	 * 
	 * @param depth - the actual path depth (== index of the actual path vertex)
	 * @param actEnv - the actual environment
	 * @param actualIntersection = the actual intersection (at the same position like the actEnv)
	 * @param incomingWeight - the incomming weigth of the former reflection
	 * @param lastRay - the former ray 
	 * @return the next ray on the way
	 */
	Line getNextRayAndSpec(int depth, Environment actEnv, Intersection actualIntersection, Spectrum incomingWeight, Vector3d lastRayDirection){

		
		Line outgoingRay;
//		rays.clear();
		rays.setSize (1);
		
		//if there is a shader calculate the next rays
		if (shader != null)
		{
//			System.err.println("LineTracer:getNextRay&Spec: Spectrum is calculated by a shader");
			shader.generateRandomRays (actEnv, view, incomingWeight, rays, isLightRay, randomCreation);
		}
		// else follow the same direction of the last ray
		else
		{
//			System.err.println(" No Shader!!!");
			rays.rays[0].direction.set (lastRayDirection);//actualIntersection.line.direction);
			rays.rays[0].spectrum.set (incomingWeight);
		}
		
//		if(rays.rays[0].directionDensity>=1e10f) rays.rays[0].valid = false; 
		
		//convert from class Ray to class Line
		outgoingRay = rays.rays[0].convert2Line();
		outgoingRay.setLineAttributes(0, Double.POSITIVE_INFINITY);
		outgoingRay.origin.set(actEnv.point);

		
		
		outgoingDirectionDensity = rays.rays[0].directionDensity;
		Color3f col = new Color3f();
		incomingWeight.get(col);
		
//		if(isLightRay && (!lastRay.reflected) && outgoingRay.reflected )System.err.println("LineTracer:getNextRay&Spec: Ray left Object at " +(depth-1));
		
//		if(rays.rays[0].spectrum.getMax()>1000) 
//			System.err.println(" LineTracer: getNextRay:   outgoingingRay= " +outgoingRay +"\n      lastray="+lastRayDirection);
		
		return outgoingRay;
	}
	
	
	/**
	 * Save all nesseccary information for later calculation to the instance of pathValues
	 * 
	 * @param depth
	 * @param outgoingRay
	 * @param actualIntersection
	 * @param actEnv
	 * @param incomingWeight
	 * @param isReflected
	 */
	void saveActualValues(int depth,Line outgoingRay, Intersection actualIntersection, Environment actEnv, Spectrum incomingWeight, boolean isReflected, boolean isRefrac){
		pV.saveValues(depth,outgoingRay, actualIntersection, actEnv, incomingWeight, shader, isReflected, isRefrac);
		pV.saveProbabilityDensity(depth,outgoingDirectionDensity);
		pV.saveGeometryFactor(depth-1,actualGeometryFactor);
		pV.saveHitLight(depth, hitLight);
		pV.savePathLenght(depth+1);
	}
	
	
	void clearVariables(){
		//the record object to store all neccessary infos about the path
		pV= null;
		traceSubPath = false;
		// path values to be stored in PathValues-object pV
//		incomingRay = null;
//		outgoingRay = null;
////		incomingWeight = null;
//		outgoingWeight = null;
		
		ilist.clear();
		ilist.elements = new Intersection[1];
		ilist.setSize(10);
		ilist.setSize(0);
		
		outgoingDirectionDensity = 0;
		actualGeometryFactor = 0;
		enteredSolids.clear();
		
		//Intersection values
		rays.clear();
		view= new Vector3f();
		hitLight= null;
		hitSensor= null;
		lightID = 0;
		sensorID = 0;
		finite = false;
		shader= null;
	}
	

	
	public PathValues getPathValues(){
		return pV;
	}
	
	
	
	public void setCondition(ConditionObject cond){
		condition = cond;
	}
	
	private boolean testOnSTop(){
		
		return false;
	}
	
//	public void setMaxGeometrieFactorValue(float newMax){
//		MAXVALUE = newMax;
//	}
	
	
	
	public void traceSubPath(PathValues srcPath,int startVertex, int newVertexCount, boolean isLightRay, Vector3d direction){
		clearVariables();
		pV = srcPath;
		traceSubPath = true;
		
		setMaxDepth(startVertex+newVertexCount+1);
		set2LightPathTracing(isLightRay);
		
		Environment actEnv;
		Line lastLine;
		
		Intersection actualIntersec = pV.intersecList.get(startVertex);
		Spectrum incomingWeight = pV.weightListBE.get(startVertex-1);

		lastLine = pV.rayListBE.get(startVertex-1);
		if(!lastLine.valid) {
			abbortCode = RAY_WAS_ABSORBED;
			return;
		}

		

		
		if(actualIntersec != null) 	actEnv= initializeEnvironment(startVertex, actualIntersec, incomingWeight, lastLine.direction);
		else actEnv=pV.envList.get(startVertex);


//		
//		tmpSpec.set(lastLine.spectrum.x,lastLine.spectrum.y,lastLine.spectrum.z);
		Line startLine = getNextRayAndSpec(startVertex, actEnv, actualIntersec, incomingWeight,  lastLine.direction);
		
		if(direction!=null) startLine.direction.set(direction);
		
		Spectrum3d newSpec = new Spectrum3d();
		newSpec.set(startLine.spectrum.x,startLine.spectrum.y,startLine.spectrum.z);
		
		if(srcPath.rayListBE.size()>startVertex) srcPath.rayListBE.set(startVertex, startLine);
		else srcPath.rayListBE.add(startVertex, startLine);
		
		if(srcPath.weightListBE.size()>startVertex) srcPath.weightListBE.set(startVertex, newSpec);
		else srcPath.weightListBE.add(startVertex, newSpec);		
		
//		System.err.println(" LineTRacer: traceSubpath:  \n  at actEnv=" +actEnv.point +"    actInt=" +actualIntersec.getPoint() +"    (actInt.line="+ actualIntersec.line.direction +")"); 
//		System.err.println("  incomingLine=" +lastLine.direction +" (view=" +view +")  incommingWeight=" +incomingWeight +"  \n   outgoingLine=" +startLine.direction +"  outgoingWeight=" +newSpec);
		
//		if(startLine.direction.x==1.0 && startLine.direction.y==0 && startLine.direction.z==0) System.exit(0);
		
		if(!startLine.valid) {
			abbortCode = RAY_WAS_ABSORBED;
			return;
		}
		
		//start tracing
		if(maxDepth > startVertex){
			recursionCounter=0;
			float result = traceRecursively(startVertex+1,startLine,actualIntersec,newSpec);
			pV.saveResult(result);
		}else{
			abbortCode = MAX_DEPTH_REACHED;
		}
		
	}

	
	public void setSafeMemoryMode(/*boolean newVal*/ MemoryHelper helper){
//		saveMemoryMode = newVal;
		envHelper = helper;
	}
	
	
	public int getAbbortCode(){
		return abbortCode;
	}
	
	public String getTracingAbbortDescription(){
		switch (abbortCode) {
		case MAX_DEPTH_REACHED:
			
			return (" Maximum depth of " +maxDepth +" was reached!");

		case OUTGOING_RAY_TOO_WEAK:
			
			return (" The weight of the outgoing ray was too weak for continuing tracing!");
		case NO_OBJECT_HIT:
			
			return (" The ray didn't intersect another object!");
		case RAY_WAS_INFINITE:
			
			return (" The ray reached infinity!");
		case RAY_WAS_ABSORBED:
			
			return (" The ray was absorbed by the object surface!");
		case RAY_WAS_STOPPED_ON_CONDITION:
			
			return (" The ray was stopped by condition!");
		default:
			return (" Something wrong abborted the Line tracer");
		}
		
		
	}
	
	
	
	public String printIList(){
		String s = "ilist.Size=" +ilist.size +" ilist.elements.Size=" +ilist.elements.length +"\n";
		
		for(int i=0; i< ilist.elements.length; i++){
			if((ilist.elements[i]!=null)&& (ilist.elements[i].line!=null)) s+= "   element[" +i +"]=" 
				+ilist.elements[i].getPoint() +"\n";
			else s+= "   element[" +i+ "]= null \n";
		}
		
		return s;
	}
	
	
}
