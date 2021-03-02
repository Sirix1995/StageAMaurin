package de.grogra.ray2.metropolis.strategy;

import java.util.ArrayList;

import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray2.Scene;
import de.grogra.ray2.antialiasing.MetropolisAntiAliasing;
import de.grogra.ray2.metropolis.strategy.LensSubpathStrat.StratificationCondition;
import de.grogra.ray2.tracing.MetropolisProcessor;
import de.grogra.ray2.tracing.modular.CombinedPathValues;
import de.grogra.ray2.tracing.modular.ComplementTracer;
import de.grogra.ray2.tracing.modular.ConditionObject;
import de.grogra.ray2.tracing.modular.LineTracer;
import de.grogra.ray2.tracing.modular.PathValues;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.Line;

public class LensPerturbationStrat extends MutationStrategy {
	
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
	
	public int kd,ka,l,m,l_,m_,real_m_, real_l_;
	public float acceptance;
	
	public LensPerturbationStrat(MetropolisProcessor metroProc) {
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
		l_ = 0;
		m_ = m-l-1;
		
//		if(m_ == 1) return registerError(NO_DIFFUSE_FOUND);
		
		srcPath.splitInto2Paths(l, lightPath, eyePath);
		eyePath.pathResultList = srcPath.pathResultList;
		eyePath.creatorID = srcPath.sensorID;
		lightPath.creatorID = srcPath.lightID;
		eyePath.pathLength = 1;
		
		condition = new StratificationCondition(eyePath,tracer);
		tracer.condition = condition;
		
//		System.err.println("XXXXXX    LesnSubStrat: mutatepath:  SourcePath=" +srcPath);
		
		int eyePathLengthBefore = eyePath.pathLength; 
		traceEyeSubPath(0, m_);
		real_l_ = 0;
		real_m_ = eyePath.pathLength - eyePathLengthBefore;  
		
		if(m_ != real_m_) return registerError(TRACINGGOAL_WASNT_REACHED);
		int j=0;
		for(int i= srcPath.pathLength-1; i > l; i--){
			if(srcPath.isSpecular(i)!= eyePath.isSpecular(j)) return registerError(TRACINGGOAL_WASNT_REACHED);
			j++;
		}
		
//		System.err.println("LLLLLLLLLLL LesnSubStrat: mutatepath: after Tracing:: lightPath=" +lightPath);
//		System.err.println("EEEEEEEEEEE LesnSubStrat: mutatepath: after e Tracing:: eyePath=" +eyePath);
		
//		for(int i= 0; i < eyePath.pathLength-2;i++){
//			if(eyePath.rayListBE.get(i).directionDensity >= 1e10f) {
//				
//				eyePath.weightListBE.get(i).scale(1f/eyePath.rayListBE.get(i).directionDensity);
//				eyePath.rayListBE.get(i).directionDensity = 1;
//			}
//		}
		
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
		
//		for(int i= 0; i < mutatedPath.pathLength-1;i++){
//			
//			if(mutatedPath.isSpecular(i)) mutatedPath.weightListBE.get(i).scale(1f/1e10f); 
//
//		}
		
		
//		System.err.println("   before  mutatedpath=" +mutatedPath);
		
		//4) calculate acepptance-prob
		acceptance = calculateAcceptanceProbality(l,m, real_l_,real_m_);
//		System.err.println("LensSubPathStrat: mutatePath: mutationProb= " +a);
		if(abbortCode==SUCCESSFUL_MUTATIONRUN){
			count_NoError++;
			if(m== actualPath.pathLength-1)  countOfLensEdgeMutations++;
			if(l==0) countOfFirstLightRayMutations++;
			if((m!= actualPath.pathLength-1) &&(l!=0)) countOfMiddlePathMutations++;
		}
//		System.err.println("   after calculating Acceptance  mutatedpath=" +mutatedPath);
//		if((eyePath.pathLength>1) && (eyePath.isSpecular(1))) System.err.println("LLLLLLLLLLL LesnSubStrat: specPath  was accepted by  "  +acceptance +"!");
		return acceptance;
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
			
			newLine = tracingMediator.getMetropolisAntialiser().getPerturbedLensEdge(eyePath.rayListBE.get(0));
//			System.err.println(" MutStrat: getNewStartLine: new Coordinates =" +newLine.x +"," +newLine.y);
		}
		
		
//		System.err.println(" MutStrat: getNewStartLine: newLine=" +newLine);
		
		
		return newLine;
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
		stat.add(" * successfull path centered mutation runs:" +countOfMiddlePathMutations +"(" +((float)countOfMiddlePathMutations/count_NoError) +")\n\n");
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
		stat.add(" * count_CalculationError: " +count_CalculationError +" ("+((float)count_CalculationError /count_totalError) +")\n\n");
		stat.add(" * count_tracingError: " +count_tracing +" ("+((float)count_tracing /count_totalError) +")\n\n");
		return stat;
	}
	
	
	
	
	
	
	int searchForDoubleDiffuse(){
		boolean lastWasDiffuse = false;
//		if(!srcPath.isSpecular(srcPath.pathLength-2)
		for(int i=srcPath.pathLength-2; i>=0; i--){
			
			if(!srcPath.isSpecular(i) && lastWasDiffuse) return i; 
			if(!srcPath.isSpecular(i)) lastWasDiffuse = true;
		}
		
		return -1;
	}
	
	
	class StratificationCondition extends ConditionObject{

		public StratificationCondition(PathValues path, LineTracer lineTracer) {
			super(path, lineTracer);
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