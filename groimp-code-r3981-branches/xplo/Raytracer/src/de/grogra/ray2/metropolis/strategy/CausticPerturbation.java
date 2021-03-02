package de.grogra.ray2.metropolis.strategy;

import java.util.ArrayList;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3f;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray2.Scene;
import de.grogra.ray2.metropolis.strategy.LensPerturbationStrat.StratificationCondition;
import de.grogra.ray2.radiosity.Vector3d;
import de.grogra.ray2.tracing.MetropolisProcessor;
import de.grogra.ray2.tracing.MetropolisRenderer;
import de.grogra.ray2.tracing.modular.CombinedPathValues;
import de.grogra.ray2.tracing.modular.ConditionObject;
import de.grogra.ray2.tracing.modular.LineTracer;
import de.grogra.ray2.tracing.modular.PathValues;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.Line;

public class CausticPerturbation extends MutationStrategy {
	
	static final int NO_DIFFUSE_FOUND=101;
	
	static int countOfLensEdgeMutations;
	static int countOfFirstLightRayMutations;
	static int countOfMiddlePathMutations;
	static int countOfLensEdgeMutationsError =0;
	static int countOfFirstLightRayMutationsError = 0;
	static int countOfMiddlePathMutationsError = 0;
	static int count_NoError;
	
	static int count_notImplemented=0;
	static int count_notVisible=0;
	static int count_ZeroBSDF=0;
	static int count_noChanges=0;
	static int count_notValidPath=0;
	static int count_weakSpec=0;
	static int count_notCanvasHit=0;
	static int count_CalculationError=0;
	static int count_totalError=0;
	static int count_tracing =0;
	static int count_no_diffuse=0;
	
	static int count_CausticEvent=0;
	static int count_CausticEventError=0;
	static int count_CausticEventErrorAtNotVisible=0;
	static int count_CausticEventErrorAtNoDiffu=0;
	
	public int kd,ka,l,m,l_,m_,real_m_, real_l_;
	public float acceptance;
	
	
	float phi1,phi2,r1, r2;
	Vector3d perturbedRay=null;
	
	public CausticPerturbation(MetropolisProcessor metroProc) {
		super(metroProc);
		eyePath = new PathValues();
		eyePath.initialize(eyePathDepth+lightPathDepth);
		
		lightPath = new PathValues();
		lightPath.initialize(eyePathDepth+lightPathDepth);
		
		

		
		

	}

	public void initialize(double[] values) {
		// TODO Auto-generated method stub

	}
	
	public void resetAll(){
		countOfLensEdgeMutations=0;
		countOfFirstLightRayMutations=0;
		countOfMiddlePathMutations=0;
		
		count_NoError =0;
		countOfLensEdgeMutationsError =0;
		countOfFirstLightRayMutationsError = 0;
		countOfMiddlePathMutationsError = 0;
		count_notImplemented=0;
		count_notVisible=0;
		count_ZeroBSDF=0;
		count_noChanges=0;
		count_notValidPath=0;
		count_weakSpec=0;
		count_notCanvasHit=0;
		count_CalculationError=0;
		count_totalError=0;
		count_tracing=0;
		count_no_diffuse=0;
		
		count_CausticEvent=0;
		count_CausticEventError=0;
	}


	public float mutatePath(CombinedPathValues actualPath, CombinedPathValues mutatedPath) {
		super.mutatePath(actualPath, mutatedPath);
		
		kd = ka =l = m = l_ = m_ =real_m_= real_l_=-1;
		
		int d = searchForDoubleDiffuse();
		
		if(d<0) {
//			System.err.println(" LensSubPathStrat: mutatePath: Found no diffuse Surface on path!");
			return registerError(NO_DIFFUSE_FOUND);
		}
		else{
//			System.err.println(" LensSubPathStrat: mutatePath: Found diffuse Surface on path at index= " +d);
		}
		
		l = d;
		m = srcPath.pathLength-1;
		l_ = m-l-1;
		m_ = 0;
		
		if(l_ == 1) return registerError(NO_DIFFUSE_FOUND);
		if(l<(m-2)) count_CausticEvent++;
		
		
		srcPath.splitInto2Paths(l, lightPath, eyePath);
		eyePath.pathResultList = srcPath.pathResultList;
		eyePath.creatorID = srcPath.sensorID;
		lightPath.creatorID = srcPath.lightID;
		eyePath.pathLength = 1;
		eyePath.pathResultList = 0.00001f;
		
		condition = new CausticPerturbationCondition(lightPath,tracer,srcPath);
		tracer.condition = condition;
		
//		System.err.println("XXXXXX    LesnSubStrat: mutatepath:  SourcePath=" +srcPath);
		
		int lightPathLengthBefore = lightPath.pathLength; 
		
		perturbedRay = perturbRay();
		traceLightSubPath(l, l_);
		real_m_ = 0;
		real_l_ = lightPath.pathLength - lightPathLengthBefore;  
		
		if(l_ != real_l_) return registerError(TRACINGGOAL_WASNT_REACHED);
		int j=0;
		for(int i= l; i< srcPath.pathLength-1; i++){
			if(srcPath.isSpecular(i)!= lightPath.isSpecular(i)) return registerError(TRACINGGOAL_WASNT_REACHED);
		}
		
//		System.err.println(" *******************************************************************");
//		System.err.println("LLLLLLLLLLL LesnSubStrat: specPath not valid!");
//		System.err.println("   srcpath=" +srcPath);
//		
//		System.err.println("\n  kd=" +kd +"  l=" +l +" m=" +m +" ka=" +ka +" l_=" +l_ +" m_="+m_+"\n");
//		System.err.println("\n  perturbed Direction=" +perturbedRay);
//	
//		System.err.println("LLLLLLLLLLL LesnSubStrat: mutatepath: after Tracing:: lightPath=" +lightPath);
//		System.err.println(" *******************************************************************");
		
//		if((eyePath.pathLength>1) && (eyePath.isSpecular(1))){
//			System.err.println("LLLLLLLLLLL LesnSubStrat: mutatepath: after Tracing:: lightPath=" +lightPath);
//			System.err.println("EEEEEEEEEEE LesnSubStrat: mutatepath: after e Tracing:: eyePath=" +eyePath);			
//		}
		
		Environment lastLightEnv = lightPath.envList.get(lightPath.pathLength-1);
		Environment lastEyeEnv = eyePath.envList.get(eyePath.pathLength-1);
		Intersection lastLightInt = lightPath.intersecList.get(lightPath.pathLength-1);
		
		if(eyePath.pathLength==1){
//			System.err.println("BidiMutStrat: mutatepath:  not Visible!");
			if(!isVisble(lastEyeEnv,lastLightEnv,null)){
//				System.err.println("BidiMutStrat: mutatepath:  not Visible!");
				return registerError(NOT_VISIBLE);
			}
		}else if(!isVisble(lastLightEnv, lastEyeEnv, lastLightInt)){
//			if((eyePath.pathLength>1) && (eyePath.isSpecular(1))) System.err.println("LLLLLLLLLLL LesnSubStrat: not visible!"); 
			return registerError(NOT_VISIBLE);		
		}
//		if((eyePath.pathLength>1) && (eyePath.isSpecular(1))) System.err.println("LLLLLLLLLLL LesnSubStrat: specPath was visible!");
		//3) combine the new paths
		complTracer.complement2Paths(lightPath, lightPath.pathLength-1, eyePath, eyePath.pathLength-1, false, this.mutatedPath);
		this.mutatedPath.pathResultList = eyePath.pathResultList;

		
				
		if (! hitsCanvas(mutatedPath)){
//			if((eyePath.pathLength>1) && (eyePath.isSpecular(1))) System.err.println("LLLLLLLLLLL LesnSubStrat: not hits canvas!");
			abbortCode = CANVAS_WASNT_HIT;
			return -1;
		}
//		if((eyePath.pathLength>1) && (eyePath.isSpecular(1))) System.err.println("LLLLLLLLLLL LesnSubStrat: specPath on canvas!");
		
		if(!mutatedPath.isValid()){
//			if((eyePath.pathLength>1) && (eyePath.isSpecular(1))) {
//				System.err.println(" *******************************************************************");
//				System.err.println("LLLLLLLLLLL LesnSubStrat: specPath not valid!");
//				System.err.println("   srcpath=" +srcPath);
//				
//				System.err.println("\n  kd=" +kd +"  l=" +l +" m=" +m +" ka=" +ka +" l_=" +l_ +" m_="+m_+"\n");
//			
//				System.err.println("   mutatedpath=" +mutatedPath);
//				System.err.println(" *******************************************************************");
//			}
			
			return this.registerError(NO_VALID_COMBINEDPATH);
		}
		
		
		//4) calculate acepptance-prob
		acceptance = calculateAcceptanceProbality(l,m, real_l_,real_m_);
//		System.err.println("LensSubPathStrat: mutatePath: mutationProb= " +a);
		if(abbortCode==SUCCESSFUL_MUTATIONRUN){
			count_NoError++;
			if(m== actualPath.pathLength-1)  countOfLensEdgeMutations++;
			if(l==0) countOfFirstLightRayMutations++;
			if((m!= actualPath.pathLength-1) &&(l!=0)) countOfMiddlePathMutations++;
		}

//		System.err.println(" *******************************************************************");
//		System.err.println("   srcpath=" +srcPath);
//		
//		System.err.println("\n  kd=" +kd +"  l=" +l +" m=" +m +" ka=" +ka +" l_=" +l_ +" m_="+m_+"\n");
//	
//		System.err.println("   mutatedpath=" +mutatedPath);
//		System.err.println(" *******************************************************************");
		
		
//		if((eyePath.pathLength>1) && (eyePath.isSpecular(1))) System.err.println("LLLLLLLLLLL LesnSubStrat: specPath  was accepted by  "  +acceptance +"!");
		return acceptance;
	}
	
	private Vector3d perturbRay(){
		
		int width = ((MetropolisProcessor)tracingMediator.getProcessor()).imageWidth;
		int height = ((MetropolisProcessor)tracingMediator.getProcessor()).imageHeight;
		
		float pixelSize = 2.0f / width;
		
		r1= 0.1f*pixelSize;
		r2 = (float)Math.sqrt(0.05f * width*height/Math.PI);
		
		
		float le = srcPath.envList.get(srcPath.pathLength-1).point.distance(srcPath.envList.get(srcPath.pathLength-2).point);
		phi1 = (float)Math.atan(r1/le);
		phi2 = (float)Math.atan(r2/le);
		
		float sum = 0;
		for(int i=l+1; i< srcPath.pathLength-1; i++)
			sum += srcPath.envList.get(i).point.distance(srcPath.envList.get(i-1).point);
		
		phi1 = phi1*le/sum;
		phi2 = phi2*le/sum;
		
		float U = rnd.nextFloat();
		
		float phi = phi2 * (float)Math.exp( - Math.log(phi2/phi1)*U);
		float angle = rnd.nextFloat()*(float)Math.PI*2f;
		
//		System.err.println("  Caustic Perturbation:  perturbRay:   r1=" +r1 +"  r2=" +r2 +"  le=" +le  +" sum=" +sum);
//		System.err.println("  Caustic Perturbation:  perturbRay:   phi1=" +phi1 +"  phi2=" +phi2 +"  resPhi=" +phi  +" angle=" +angle);
		
		Vector3d direction = new Vector3d(srcPath.rayListBE.get(l).direction);
		Matrix3d rot1  = new Matrix3d();
		rot1.m00 = Math.cos(phi);	rot1.m10 = -Math.sin(phi); rot1.m20 = 0;
		rot1.m01 = Math.sin(phi);	rot1.m11 = Math.cos(phi); rot1.m21 = 0;
		rot1.m02 = 0;	rot1.m12 = 0; rot1.m22 = 1;
		
		Vector3d newDirection = new Vector3d();
		rot1.transform(direction,newDirection);

		direction.normalize();
		double x = direction.x;
		double y = direction.y;
		double z = direction.z;
		
		Matrix3d rot2  = new Matrix3d();
		rot2.m00 = Math.cos(angle)+x*x*(1-Math.cos(angle));	
		rot2.m10 = x*y*(1-Math.cos(angle)) -z*Math.sin(angle); 
		rot2.m20 = x*z*(1-Math.cos(angle)) +y*Math.sin(angle);
		
		rot2.m01 = x*y*(1-Math.cos(angle)) +z*Math.sin(angle);
		rot2.m11 = Math.cos(angle)+y*y*(1-Math.cos(angle));
		rot2.m21 = z*y*(1-Math.cos(angle)) -x*Math.sin(angle);
		
		rot2.m02 = x*z*(1-Math.cos(angle)) -y*Math.sin(angle);
		rot2.m12 = z*y*(1-Math.cos(angle)) +x*Math.sin(angle);
		rot2.m22 =  Math.cos(angle)+z*z*(1-Math.cos(angle));
		
		rot2.transform(newDirection);
		
                                     
		
		if(Double.isNaN(newDirection.x)){
			
			System.err.println("  Caustic Perturbation:  perturbRay:   r1=" +r1 +"  r2=" +r2 +"  le=" +le  +" sum=" +sum);
			System.err.println("  Caustic Perturbation:  perturbRay:   phi1=" +phi1 +"  phi2=" +phi2 +"  resPhi=" +phi  +" angle=" +angle);
			System.err.println("  Caustic Perturbation:  perturbRay:   oldDirection= \n" +srcPath.rayListBE.get(l).direction);
			System.err.println(" 	 newDirection= \n" +newDirection);
		}
		return newDirection;
	}
	
	public void traceLightSubPath(int l, int l_){
		
		
//		if(l_>0){
		if(l<=0){
			Line startLine = generateNewStartLine(true);
			startLine.direction.set(perturbedRay);
			Spectrum3d startSpec = new Spectrum3d();
			startSpec.set(startLine.spectrum);
//			System.err.println("Tracing new light path...");
			tracer.traceLine(l_+1, lightPath, startLine, startSpec, lightPath.creatorID, true, rnd);
			
//			if(startSpec.sum()<68.0){
//				System.err.println("  MutationStrat: traceLightSubPath:  \n   lightPath=" +lightPath);
//				System.exit(0);
//			}
			
		}else {
//			System.err.println("Tracing light subpath starting at "+l +" to depth " +(l+l_) +" ...");
			if(l_>0) tracer.traceSubPath(lightPath,l, l_, true,perturbedRay);
		}		
	
		
//		System.err.println(" MuatStrat: traceLightSP: lightSubPath was traced " +(tracer.recursionCounter-1) +" times and should has been traced " +l_+" times");
//		System.err.println(" MuatStrat: traceLightSP: Tracing abbort description:" +tracer.getTracingAbbortDescription());
		tracer.condition=null;
	}
	
	
	
	
	@Override
	public int registerError(int error){
		switch (error) {
			case NOT_IMPLEMENTED_YET:{
				count_notImplemented++;
				break ;
			}
		case NOT_VISIBLE:{
				count_notVisible++;
				break ;
			}
		case SUCCESSFUL_MUTATIONRUN:{
				count_NoError++;
				break ;
			}
		case BSDF_WAS_ZERO:{
				count_ZeroBSDF++;
				break ;
			}		
		case NO_CHANGES_HAPPENED:{
				count_noChanges++;
				break ;
			}	
		case NO_VALID_COMBINEDPATH:{
				count_notValidPath++;
				break ;
			}
		case PATH_SPECTRA_WERE_TOO_WEAK:{
				count_weakSpec++;
				break ;
			}
		case CANVAS_WASNT_HIT:{
				count_notCanvasHit++;
				break ;
			}
		case TRACINGGOAL_WASNT_REACHED:{
			count_tracing++;
			break ;
		}
		case NO_DIFFUSE_FOUND:{
			count_no_diffuse++;
			break ;
		}
		default:{
				count_CalculationError++;
				break ;
			}
		}
		if(error!= SUCCESSFUL_MUTATIONRUN){
			count_totalError++;
			
			if(l<(m-2)) count_CausticEventError++;
			
			if(m>= srcPath.pathLength-1)  countOfLensEdgeMutationsError++;
			if(l<=0) countOfFirstLightRayMutationsError++;
			if((m< srcPath.pathLength-1) &&(l>0)) countOfMiddlePathMutationsError++;
		}
		return super.registerError(error);
	}

//	@Override
//	public Line generateNewStartLine(boolean isLightRay){
//		Line newLine=null;
//		
//		
//		if(isLightRay){
//			tmpRayList.clear();
//			tmpRayList.setSize(1);
//			
//			int lId = lightPath.creatorID;
//			
//			Scene scene = tracingMediator.getProcessor().scene;
//			
//			tmpEnv.localToGlobal.set(scene.getLightTransformation(lId));
//			tmpEnv.globalToLocal.set(scene.getInverseLightTransformation(lId));
//			
//			
//			Light light = scene.getLights()[lightPath.creatorID];
//			light.generateRandomOrigins(tmpEnv, tmpRayList, rnd);
//			light.generateRandomRays(tmpEnv, null, tmpRayList.rays[0].spectrum, tmpRayList, false, rnd);
//			
//			newLine = tmpRayList.rays[0].convert2Line();
//			
//		}else{
////			System.err.println(" MutStrat: getNewStartLine: old Coordinates =" +srcPath.rayListEB.get(srcPath.pathLength-2).x +"," +srcPath.rayListEB.get(srcPath.pathLength-2).y);
//			
//			newLine = tracingMediator.getMetropolisAntialiser().getStratifiedLensEdge();//getNewLensEdge();
////			System.err.println(" MutStrat: getNewStartLine: new Coordinates =" +newLine.x +"," +newLine.y);
//		}
//		
//		
////		System.err.println(" MutStrat: getNewStartLine: newLine=" +newLine);
//		
//		
//		return newLine;
//	}
	
	
	public ArrayList<String> getStatistics(){
		
		ArrayList<String> stat = new ArrayList<String>();
		stat.add("total Count of successfull strategy runs: " +(count_NoError) +"\n");
		stat.add(" * successfull lens edge mutation runs:" +countOfLensEdgeMutations +"(" +((float)countOfLensEdgeMutations/count_NoError) +")\n");
		stat.add(" * successfull first light ray mutation runs:" +countOfFirstLightRayMutations +"(" +((float)countOfFirstLightRayMutations/count_NoError) +")\n");
		stat.add(" * successfull path centered mutation runs:" +countOfMiddlePathMutations +"(" +((float)countOfMiddlePathMutations/count_NoError) +")\n");
		stat.add(" * successfull caustic event mutation runs:" +(count_CausticEvent-count_CausticEventError) +"(" +((float)(count_CausticEvent-count_CausticEventError)/count_CausticEvent) +")\n\n");
		stat.add("total count of failure: " +(count_totalError) +"\n");
		stat.add(" * error on lens edge mutation:" +countOfLensEdgeMutationsError +"(" +((float)countOfLensEdgeMutationsError/count_totalError) +")\n");
		stat.add(" * error on first light ray mutation:" +countOfFirstLightRayMutationsError +"(" +((float)countOfFirstLightRayMutationsError/count_totalError) +")\n");
		stat.add(" * error on path centered mutation:" +countOfMiddlePathMutationsError +"(" +((float)countOfMiddlePathMutationsError/count_totalError) +")\n\n");
		stat.add(" * count_no_diffuse: " +count_no_diffuse +" (" +((float)count_no_diffuse /count_totalError) +")\n");
		stat.add(" * count_notImplemented: " +count_notImplemented +" (" +((float)count_notImplemented /count_totalError) +")\n");
		stat.add(" * count_notVisible: " +count_notVisible +" (" +((float)count_notVisible /count_totalError) +")\n");
		stat.add(" * count_ZeroBSDF: " +count_ZeroBSDF +" ("+((float) count_ZeroBSDF/count_totalError) +")\n");
		stat.add(" * count_noChanges: " +count_noChanges  +" ("+((float)count_noChanges /count_totalError) +")\n");
		stat.add(" * count_notValidPath: " +count_notValidPath +" ("+((float)count_notValidPath /count_totalError) +")\n");
		stat.add(" * count_weakSpec: " +count_weakSpec +" ("+((float)count_weakSpec /count_totalError) +"\n");
		stat.add(" * count_notCanvasHit: " +count_notCanvasHit +" ("+((float)count_notCanvasHit /count_totalError) +")\n");
		stat.add(" * count_CalculationError: " +count_CalculationError +" ("+((float)count_CalculationError /count_totalError) +")\n");
		stat.add(" * count_tracingError: " +count_tracing +" ("+((float)count_tracing /count_totalError) +")\n");
		stat.add(" * count_CausticEventError: " +count_CausticEventError +" ("+((float)count_CausticEventError /count_CausticEvent) +")\n\n");
		return stat;
	}
	
	
	
	
	
	
	int searchForDoubleDiffuse(){
		if(srcPath.pathLength<2) return -1;
		
		if(!srcPath.isSpecular(srcPath.pathLength-2)){
			for(int i=srcPath.pathLength-3; i>=0; i--){
				if(!srcPath.isSpecular(i)) return i; 
			}
		}
//		if(srcPath.pathLength>3) System.err.println("  Error on finding Caustic-Paths:  srcPath=" +srcPath);
		return -1;
	}
	
	
	class CausticPerturbationCondition extends ConditionObject{

		CombinedPathValues srcPath;
		
		public CausticPerturbationCondition(PathValues path, LineTracer lineTracer, CombinedPathValues srcPath) {
			super(path, lineTracer);
			this.srcPath = srcPath; 
		}
		
		
		public boolean stopOnCondition(){
			int last = path.pathLength-1;
			
			if(last==0)return false;
			
//			if(path.isSpecular(last)) System.err.println(" LensSubpathStrat: stoOnCondition!   index=" +last  +"   cond=" +path.isSpecular(last));
//			if(last!=1 ) System.err.println(" LensSubpathStrat: after passing trough stoOnCondition!   index=" +last  +"   cond=" +path.isSpecular(last));
			return (!path.isSpecular(last));

		}
		
	}


}
