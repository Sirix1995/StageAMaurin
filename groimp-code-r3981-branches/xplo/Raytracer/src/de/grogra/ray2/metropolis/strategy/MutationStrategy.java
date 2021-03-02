package de.grogra.ray2.metropolis.strategy;

import java.util.ArrayList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import net.goui.util.MTRandom;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Scattering;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray.util.RayList;
import de.grogra.ray2.Scene;
import de.grogra.ray2.antialiasing.MetropolisAntiAliasing;
import de.grogra.ray2.metropolis.MetropolisStrategy;
import de.grogra.ray2.tracing.MetropolisProcessor;
import de.grogra.ray2.tracing.MetropolisRenderer;
import de.grogra.ray2.tracing.modular.CombinedPathValues;
import de.grogra.ray2.tracing.modular.ComplementTracer;
import de.grogra.ray2.tracing.modular.ConditionObject;
import de.grogra.ray2.tracing.modular.LineTracer;
import de.grogra.ray2.tracing.modular.PathValues;
import de.grogra.ray2.tracing.modular.TracingMediator;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;

public abstract class MutationStrategy implements MetropolisStrategy{
	
	protected static final float MIN_WEIGHT = 0.00001f;
	
	protected TracingMediator  tracingMediator;
	
	protected MetropolisAntiAliasing antialiser;
	protected LineTracer tracer;
	protected ComplementTracer complTracer;
	protected MTRandom rnd;
	protected CombinedPathValues srcPath;
	protected CombinedPathValues mutatedPath;
	
	public ConditionObject condition = null;
	
	static final double EPSILON = 1e-4d;
	public static final String EYEPATH_DEPTH = "BidirectionalPathTracer/eyeDepth";
	public static final String LIGHTPATH_DEPTH = "BidirectionalPathTracer/lightDepth";
	
	public int abbortCode;
	public static final int NOT_IMPLEMENTED_YET = -1;
	public static final int SUCCESSFUL_MUTATIONRUN = 0;
	public static final int NOT_VISIBLE = 1;
	public static final int NO_VALID_COMBINEDPATH = 2;
	public static final int BSDF_WAS_ZERO = 3;
	public static final int NO_CHANGES_HAPPENED = 4;
	public static final int PATH_SPECTRA_WERE_TOO_WEAK = 5;
	public static final int CANVAS_WASNT_HIT = 6;
	public static final int CALCULATION_ERROR = 99;
	public static final int TRACINGGOAL_WASNT_REACHED = 7;
	
	
	protected static int MAXVAL = 10;
	protected int sig = -1;
	protected PathValues eyePath;
	protected PathValues lightPath;
	
//	int kd,ka,l,m,l_, m_;
	
	protected Spectrum3d tmpSpec1, tmpSpec2,tmpSpec3;
	protected Vector3f tmpVec1, tmpVec2;
	protected Point3d tmpPoint1, tmpPoint2;
	protected Line tmpLine;
	
	protected IntersectionList ilist = new IntersectionList();
	protected RayList tmpRayList = new RayList();
	protected Environment tmpEnv; 
	
	protected int eyePathDepth, lightPathDepth;
	
	public MutationStrategy(MetropolisProcessor metroProc){
		ilist.setSize(1);
		
		tracingMediator = metroProc.tracingMediator;
		
		this.antialiser = tracingMediator.getMetropolisAntialiser();
		this.tracer = tracingMediator.getLinetracer();
		this.complTracer = tracingMediator.getComplementTracer();
		rnd = new MTRandom(tracingMediator.getRenderer().getSeed());
		
		
		//get the depth (maxium index of the path vertex) of the eye subpath
		eyePathDepth = tracingMediator.getRenderer().getNumericOption(EYEPATH_DEPTH, 10).intValue();
		assert eyePathDepth >1;
 
		//get the depth (maxium index of the path vertex) of the light subpath
		 lightPathDepth = tracingMediator.getRenderer().getNumericOption(LIGHTPATH_DEPTH, 10).intValue();
		 assert lightPathDepth >1;
		 
		 Scene scene = tracingMediator.getRenderer().getScene();
		 tmpEnv = new Environment(scene.getBoundingBox (), scene.createSpectrum(),Environment.PATH_TRACER);
		 
			
		tmpSpec1 = new Spectrum3d();
		tmpSpec2 = new Spectrum3d();
		tmpSpec3 = new Spectrum3d();
			
		tmpVec1 = new Vector3f();
		tmpVec2 = new Vector3f();
			
			 
		 tmpLine = new Line();
		 tmpPoint1 = new Point3d();
		 tmpPoint2 = new Point3d();
	}
	
	public abstract void initialize(double[] values);
	
	public float mutatePath(CombinedPathValues actualPath, CombinedPathValues mutatedPath){
		srcPath = actualPath;
		this.mutatedPath = mutatedPath;
		mutatedPath.pathLength=0;
		abbortCode = SUCCESSFUL_MUTATIONRUN;
		return -1;
	}

	
	
	
	public float getDeleteProbability(int l, int m){
		int k_d = m-l;
		
		float pd1,pd2;
		if(k_d==1) pd1= 0.25f;
		else if(k_d==2)pd1= 0.5f;
		else pd1= (float)Math.pow(2, -k_d);
		
		pd2=1f;
		return pd1*pd2;
	}

	public float getAddProbability(int l, int m, int l_ , int m_){
		int k_d = m-l;
		int k_a = m_+l_+1;
		
		float pa1, pa2;
		int j = k_d - k_a;
		if(j==0) pa1=0.5f;
		else if((j==-1) || (j==1)) pa1= 0.15f;
		else pa1= 0.2f*(float)Math.pow(2, -Math.abs(j));
		
		pa2 = 1f/(k_a);
		
		return pa1*pa2;
	}
	
	
	public int getDeletionPathLength(){
		float p = rnd.nextFloat();
		
		if(p>=0.5) return 2;
		if(p>=0.25) return 1;
		
		for(int i=3; i<srcPath.pathLength; i++){
			if(p>= Math.pow(2, -i)) return i;
		}
		return srcPath.pathLength-1;		
	}
	
	public int getAddiationPathLength(int kd){
		
		int maxValue = (eyePathDepth+lightPathDepth-1) - (srcPath.pathLength-kd-1);
		while(maxValue>0){
			float p = rnd.nextFloat();
			sig = -sig;
			
			if(p>=0.5) return kd;
			if(p>=0.15) return (((kd+sig)<=maxValue) ? kd+sig : maxValue);
			
			
			for(int i=2; (i+kd)<=maxValue; i++){
				if(p>= (0.2*Math.pow(2, -i))) {
					if(kd+sig*i>=0) return kd+sig*i;
				}
			}
		}
		return 0;		
	}	
	
	public int[] getDeleteVertice(int k){
		int l = rnd.nextInt(srcPath.pathLength-k);
		int m = l+k;
		
		int[] ret = {l,m};
		return ret;
	}
	
	public int[] getAddVertice(int k){
		int l_ = (k>0) ? rnd.nextInt(k) : 0; 
		int m_ = (k>0) ? k-1-l_ : 0;
		
		int[] ret = {l_,m_};
		return ret;
	}
	
	
	
	public void traceLightSubPath(int l, int l_){
//		if(l_>0){
			if(l<=0){
				Line startLine = generateNewStartLine(true);
				Spectrum3d startSpec = new Spectrum3d();
				startSpec.set(startLine.spectrum);
//				System.err.println("Tracing new light path...");
				tracer.traceLine(l_+1, lightPath, startLine, startSpec, lightPath.creatorID, true, rnd);
				
//				if(startSpec.sum()<68.0){
//					System.err.println("  MutationStrat: traceLightSubPath:  \n   lightPath=" +lightPath);
//					System.exit(0);
//				}
				
			}else {
//				System.err.println("Tracing light subpath starting at "+l +" to depth " +(l+l_) +" ...");
				if(l_>0) tracer.traceSubPath(lightPath,l, l_, true,null);
			}		
//		}
			
//		System.err.println(" MuatStrat: traceLightSP: lightSubPath was traced " +(tracer.recursionCounter-1) +" times and should has been traced " +l_+" times");
//		System.err.println(" MuatStrat: traceLightSP: Tracing abbort description:" +tracer.getTracingAbbortDescription());
		tracer.condition=null;
	}
	
	
	
	public void traceEyeSubPath(int m, int m_){
	//	if(m_>0){
			
			if(m<=0){
//				System.err.println("MutationStrat: traceEyeSubPath: Tracing new eye path...");
				Line startLine = generateNewStartLine(false);
				if(startLine==null) return;
				Spectrum3d startSpec = new Spectrum3d();
				startSpec.set(startLine.spectrum);
				
				tracer.traceLine(m_+1, eyePath, startLine, startSpec, eyePath.creatorID, false, rnd);
//				System.err.println("  MutationStrat: traceEyeSubPath:   startLine=" +startLine);
//				System.exit(0);
			}else{
//				System.err.println("Tracing eye subpath starting at "+m +" to depth " +(m+m_) +" ...");
				if(m_ >0) tracer.traceSubPath(eyePath,m, m_, false,null);
			}	
//		}
			
//		System.err.println(" MuatStrat: traceEyeSP: eyeSubPath was traced " +(tracer.recursionCounter-1) +" times and should has been traced " +m_+" times");
//		System.err.println(" MuatStrat: traceEyeSP: Tracing abbort description:" +tracer.getTracingAbbortDescription());
		tracer.condition=null;
	}

	public Line generateNewStartLine(boolean isLightRay){
		Line newLine=null;
		
		
		if(isLightRay){
			tmpRayList.clear();
			tmpRayList.setSize(1);
			
			int lId = lightPath.creatorID;
			
			Scene scene = tracingMediator.getProcessor().scene;
			
			tmpEnv.localToGlobal.set(scene.getLightTransformation(lId));
			tmpEnv.globalToLocal.set(scene.getInverseLightTransformation(lId));
			
			
			Light light = scene.getLights()[lightPath.creatorID];
			light.generateRandomOrigins(tmpEnv, tmpRayList, rnd);
			light.generateRandomRays(tmpEnv, null, tmpRayList.rays[0].spectrum, tmpRayList, false, rnd);
			
			newLine = tmpRayList.rays[0].convert2Line();
			
		}else{
//			System.err.println(" MutStrat: getNewStartLine: old Coordinates =" +srcPath.rayListEB.get(srcPath.pathLength-2).x +"," +srcPath.rayListEB.get(srcPath.pathLength-2).y);
			
			newLine = tracingMediator.getMetropolisAntialiser().getStratifiedLensEdge();
//			System.err.println(" MutStrat: getNewStartLine: new Coordinates =" +newLine.x +"," +newLine.y);
		}
		
		
//		System.err.println(" MutStrat: getNewStartLine: newLine=" +newLine);
		
		
		return newLine;
	}
	
//	void changeLineDirection(boolean isLightRay,  Line targetLine){
//		
//		
//		if(isLightRay){
//			tmpRayList.clear();
//			int lId = lightPath.creatorID;
//			
//			Scene scene = tracingMediator.getRenderer().getScene();
//			
//			tmpEnv.localToGlobal.set(scene.getLightTransformation(lId));
//			tmpEnv.globalToLocal.set(scene.getInverseLightTransformation(lId));
//						
//			Light light = scene.getLights()[lightPath.creatorID];
//			tmpSpec1.set(targetLine.spectrum);
//			light.generateRandomRays(tmpEnv, null, tmpSpec1, tmpRayList, false, rnd);
//			
//			targetLine.direction.set(tmpRayList.rays[0].direction);
//		}else{
//			float angle = (float)(rnd.nextDouble()*2f*Math.PI);
//			float r = (float)(rnd.nextDouble());
//			targetLine = tracingMediator.getMetropolisAntialiser().getPerturbedLensEdge(targetLine, r, angle);
//			
//		}
//		
//	}
	
	
	
	public float calculateAcceptanceProbality(int l, int m, int l_, int m_){

		if((mutatedPath.rayListBE.size()< mutatedPath.pathLength-1) || (mutatedPath.rayListEB.size()< mutatedPath.pathLength-1)) 
			return registerError(CALCULATION_ERROR);
		
		if((mutatedPath.weightListBE.get(mutatedPath.pathLength-2).integrate()< MIN_WEIGHT) ||
			(mutatedPath.weightListEB.get(0).integrate()< MIN_WEIGHT)){
				return registerError(PATH_SPECTRA_WERE_TOO_WEAK);

			}
		
		
//		boolean before=false;
//		for(int i= l+l_; i < mutatedPath.pathLength-1;i++){
//			
//			if(mutatedPath.isSpecular(i)||before){
//				mutatedPath.weightListBE.get(i).scale(1f/1e10f);
//				before = true;
//			}
//
//		}
//		before=false;
//		for(int i= m-m_; i > 0;i--){
//			
//			if(mutatedPath.isSpecular(i)||before){
//				mutatedPath.weightListEB.get(i-1).scale(1f/1e10f);
//				before = true;
//			}
//
//		}
		
		
		Spectrum3d C_bd_l_ = new Spectrum3d();
//		if(!mutatedPath.isSpecular(l+l_) && !mutatedPath.isSpecular(l+l_+1)){
			C_bd_l_ = getCombinedBSDF(l+l_,mutatedPath);
//		}		
		float transparency = mutatedPath.pathResultList;		
		mutatedPath.color.set((float)C_bd_l_.x,(float)C_bd_l_.y, (float)C_bd_l_.z, (transparency < 1) ? 1 - transparency : 0);
		float c_bd_l_ = luminance(C_bd_l_);
		
		
		if(c_bd_l_ ==0){
			return registerError( BSDF_WAS_ZERO);
		}
				
//		if(mutatedPath.isSpecular(mutatedPath.pathLength-2)) mutatedPath.color.scale(10f);
		
		int kd= m-l;
		
			int ka = mutatedPath.pathLength - (srcPath.pathLength-kd);
		
			float I_0_mut = getI_0(mutatedPath);
			float Q_xy = calculateQ(l,m,l_,m_, srcPath, mutatedPath,true)*I_0_mut;
			if(Q_xy >0) {
				
				float I_0_src = getI_0(srcPath);				
				float Q_yx = calculateQ(l,l+ka,0,kd-1,mutatedPath,srcPath, false)*I_0_src;
				if(Q_yx > 0){

					if(Q_yx==Q_xy) {
						return registerError(NO_CHANGES_HAPPENED);
					}
					
//					if(mutatedPath.isSpecular(mutatedPath.pathLength-2)) {
//						System.err.println(" MutStrat: calcQ:      specular path   color=" +mutatedPath.color +"    acceptance = " +Math.min(1f, Q_yx/Q_xy));
//					}
					
					float acceptance = Math.min(1f, Q_yx/Q_xy); 
					if((acceptance<=0)|| Float.isInfinite(acceptance)||Float.isNaN(acceptance)){
						return registerError(CALCULATION_ERROR); 
					}
					if(luminance(mutatedPath.initialSpectrum)*10 < luminance(new Spectrum3d(mutatedPath.color.x,mutatedPath.color.y,mutatedPath.color.z))){
						System.err.println(" MutStrat: calcQ:      l=" +l +" m=" +m +" l_=" +l_ +" m_=" +m_ +"\n    mutatedPath=" +mutatedPath 
								+"\n    srcPath=" +srcPath +"\n    acceptance = " +Math.min(1f, Q_yx/Q_xy));
						
					}
					return acceptance;
				}
			}	
			return registerError(CALCULATION_ERROR);
			

//		}else {
//			abbortCode = NOT_VISIBLE;
//			return -1f;
//		}
	}
	
	
	public float getI_0(CombinedPathValues path){
		MetropolisRenderer render = (MetropolisRenderer) tracingMediator.getRenderer(); 
		if(render.twoStage_Ref && (render.actRenderingStage == render.SECOND_STAGE)){
			int x =(int)path.rayListEB.get(path.pathLength-2).x;
			int y =(int)path.rayListEB.get(path.pathLength-2).y;
			float ret = render.getFirstStageImageValue(x, y);
			
			if(ret==0) System.err.println("  MutationStrat: getI0:     return=" +ret);
			
			return ret;
		}else return 1f;
	}
	
	
	
	public float calculateQ(int l, int m, int l_, int m_, CombinedPathValues srcPath, CombinedPathValues mutatedPath, boolean x2y){


		
		int kd = m-l;
		int real_ka = mutatedPath.pathLength - (srcPath.pathLength-kd);  // == l_ + m_+1
		
		float[] C_bd = new float[real_ka];
		
//		C_bd[l_] = c_bd_l_;
//		
//		float C_pre;
//		
//		for(int i=l_+1; (i+1)<= real_ka; i++){
//			C_pre = C_bd[i-1];
//			C_bd[i] = getAddProbability(l, m, i,real_ka-i-1)*C_pre/getAddProbability(l, m,i-1,real_ka-i );
//		}
//		
//		for(int i=l_; i>=1; i--){
//			C_pre = C_bd[i];
//			C_bd[i-1] = getAddProbability(l, m, i-1,real_ka-i)*C_pre/getAddProbability(l, m,i,real_ka-i-1 );
//		}

		
//		System.err.println(" MutStrat: calcQ: mutatedPath= \n" +"  " +mutatedPath);
//		System.err.println(" MutStrat: calcQ: l=" +l +" m=" +m +"  real_l_=" +l_ +" real_m_=" +m_ +"   --> kd=" +kd +"   real_ka="+real_ka);
		
		float sum=1;
		for(int i=1; i<=real_ka; i++){
//			if(mutatedPath.isSpecular(l+i-1)||mutatedPath.isSpecular(l+i)){
//				C_bd[i-1] = 1;
//			}else{
				Spectrum3d C = getCombinedBSDF(l+i-1, mutatedPath);
				C_bd[i-1] = luminance(C);
//			}
			
			sum += getAddProbability(l, m,i-1,real_ka-i)/C_bd[i-1];
		}
		
//		System.err.println(" MutStrat: calcQ: pd[" +l +"," +m +"]=" +getDeleteProbability(l, m) +"  Sum[" +l_ +"," +m_ +"]=" +sum);
		
		if(Double.isInfinite(sum) || Double.isNaN(sum)||(sum <=0)){
			
//			System.err.println(" MutStrat: calcQ: FEHLER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			System.err.println(" MutStrat: calcQ:  sum ="+sum);
//			System.err.println(" MutStrat: calcQ:  direction ="+x2y);
//			System.err.println(" MutStrat: calcQ: mutatedPath= \n" +"  " +mutatedPath);
//			System.err.println(" MutStrat: calcQ: l=" +l +" m=" +m +"  real_l_=" +l_ +" real_m_=" +m_ +"   --> kd=" +kd +"   real_ka="+real_ka );
//			for(int i=0; i< C_bd.length; i++) System.err.println("     C_bd[" +i +"]=" +C_bd[i]);
			
//			throw new RuntimeException();
			return registerError(CALCULATION_ERROR);
//			System.exit(0);
		}
		
//		System.err.println(" MutStrat: calcQ: l=" +l +" m=" +m +"  real_l_=" +l_ +" real_m_=" +m_ +"   --> kd=" +kd +"   real_ka="+real_ka  +"    C_bd_l=" +C_bd_l_ +"      C_bd[]:");
//		for(int i=0; i< C_bd.length; i++) System.err.println("     C_bd[" +i +"]=" +C_bd[i]);
		
		
		
		return getDeleteProbability(l, m)*sum;
	}
	

	public float luminance(Spectrum3d lumSpec){
		
		return (float)(0.299f*lumSpec.x + 0.587f*lumSpec.y + 0.114f*lumSpec.z);
	}
	
	
	public Spectrum3d getCombinedBSDF(int startIndex, CombinedPathValues mutatedPath){
		
		int lI = startIndex; 
		int eI = lI+1;
		
		Scattering shL = (lI<0)? null :mutatedPath.shaderList.get(lI);
		Scattering shE = (eI == mutatedPath.pathLength) ? null :mutatedPath.shaderList.get(eI);
		
		Environment envL = mutatedPath.envList.get(lI);
		Environment envE = mutatedPath.envList.get(eI);
		
		Spectrum specInL;
		if(lI>0){
			tmpVec1.set(mutatedPath.rayListBE.get(lI-1).direction);
			specInL = mutatedPath.weightListBE.get(lI-1);
		}else{
			tmpVec1.set(0,0,0);
			specInL = mutatedPath.initialSpectrum;
		}
		tmpVec1.negate();
		tmpVec1.normalize();
		
		tmpVec2.sub(envE.point, envL.point);
		tmpVec2.normalize();
		
				
		float densL = shL.computeBSDF(envL, tmpVec1, specInL, tmpVec2, false, tmpSpec1);
//		if(mutatedPath.isSpecular(lI)) tmpSpec1.scale(dens); 
		
		Spectrum specInE;
		if(eI < mutatedPath.pathLength-1){
			tmpVec1.set(mutatedPath.rayListEB.get(eI).direction);
			specInE = mutatedPath.weightListEB.get(eI);
		}else{
			tmpVec1.set(0,0, 0);
			specInE = mutatedPath.initialSpectrumEnd;
		}
		tmpVec1.negate();
		tmpVec1.normalize();
	
		tmpVec2.negate();
		
		try {
			float densE=1;
			if((shE!=null) && (eI != mutatedPath.pathLength-1))	densE = shE.computeBSDF(envE, tmpVec1, specInE, tmpVec2, true, tmpSpec2);
			else tmpSpec2.set(specInE);
		} catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			tmpSpec2.set(specInE);
		}
//		if(mutatedPath.isSpecular(eI)) tmpSpec2.scale(dens); 
		
		float dist_sq = Math.max(1,envE.point.distanceSquared(envL.point));
//		float dist_sq = envE.point.distanceSquared(envL.point);
		
		tmpSpec1.dot((Spectrum)tmpSpec2,tmpSpec3);
		tmpSpec3.scale(1f/dist_sq);
		
		if(mutatedPath.isSpecular(eI)) tmpSpec3.scale(1f/1e10f);
		if(mutatedPath.isSpecular(lI)) tmpSpec3.scale(1f/1e10f);
		
//		System.err.println("  MutationStrat: getCombinedBSDF:   C_bd was computed between: [" +lI +"]=" +envL.point +" and [" +eI +"]=" +envE.point);
		
		return tmpSpec3;
	}
	
	public boolean isVisble(Environment lightEnv,Environment eyeEnv,Intersection lightInt ){
		
		tmpPoint1.set(lightEnv.point);
		tmpPoint2.set(eyeEnv.point);
		
		tmpLine.origin.set(lightEnv.point);
		tmpLine.direction.sub(tmpPoint2, tmpPoint1);
		tmpLine.direction.normalize();
		
		int sizeBefore = ilist.size;
		
		tracer.scene.computeIntersections(tmpLine, Intersection.CLOSEST, ilist, lightInt, null);
		
		if(sizeBefore<ilist.size){
			Intersection calcInt = ilist.elements[sizeBefore];
			
			double dist = calcInt.getPoint().distance(tmpPoint2);
			
			ilist.setSize(sizeBefore);
			if(dist <= EPSILON) return true;
		}
		
		
		return false;
	}
	
	
	public boolean hitsCanvas(CombinedPathValues testPath){
		
		Environment env = testPath.envList.get(testPath.pathLength-1);
		Point3d vertex = new Point3d(testPath.envList.get(testPath.pathLength-2).point);
		float[] res = ((MetropolisRenderer )tracingMediator.getRenderer()).getPixelsForLine2Vertex(env, vertex);
		if((res[0]>-1) && (res[1]>-1)){
			Line lensEdge = testPath.rayListEB.get(testPath.pathLength-2);
			lensEdge.x = res[0];
			lensEdge.y = res[1];
			return true;
		}
		
		testPath.setValidFlag(false);
		return false;
	}
	
	
	
	public String getAcceptanceAbbortDescription(){
		switch (abbortCode) {
		case NOT_IMPLEMENTED_YET:
			
			return "This strategy isn't implemented yet!";
		case NOT_VISIBLE:
			
			return "Visibilitytest on combining new eye and light subpath failed!";
		case SUCCESSFUL_MUTATIONRUN:
			
			return "Acceptance probalility was successfully calculated!";
		case BSDF_WAS_ZERO:
			
			return "Combining BSDF-Spectrum was Zero!";		
		case NO_CHANGES_HAPPENED:
			
			return "No changes happend to the source path!";	
		case NO_VALID_COMBINEDPATH:
			
			return "Recombination auf new traced light and eye path couldn't be acomplished!";
		case PATH_SPECTRA_WERE_TOO_WEAK:
			
			return "The complementing of the paths produced too weak spectra";
			
		case CANVAS_WASNT_HIT:
			return "The resulting lesnEdge, doesn't hit the canvas!";
		default:
			return "Something wrong abborted the strategy";
		}
	}
	

	public int registerError(int error){
		abbortCode = error;
		return -1;
	}
	
	
	public void pathChanged(){
		
	}
	
	public String getDescription(){return "";}
	
	abstract public void resetAll();
	
	abstract public ArrayList<String> getStatistics();
	
}
