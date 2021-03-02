package de.grogra.ray2.metropolis.strategy;

import java.util.ArrayList;

import javax.vecmath.Vector3f;

import net.goui.util.MTRandom;
import de.grogra.ray.physics.Environment;
import de.grogra.ray.physics.Light;
import de.grogra.ray.physics.Scattering;
import de.grogra.ray.physics.Shader;
import de.grogra.ray.physics.Spectrum;
import de.grogra.ray.physics.Spectrum3d;
import de.grogra.ray2.Scene;
import de.grogra.ray2.antialiasing.MetropolisAntiAliasing;
import de.grogra.ray2.tracing.MetropolisProcessor;
import de.grogra.ray2.tracing.MetropolisRenderer;
import de.grogra.ray2.tracing.PixelwiseRenderer;
import de.grogra.ray2.tracing.modular.CombinedPathValues;
import de.grogra.ray2.tracing.modular.ComplementException;
import de.grogra.ray2.tracing.modular.ComplementTracer;
import de.grogra.ray2.tracing.modular.LineTracer;
import de.grogra.ray2.tracing.modular.PathValues;
import de.grogra.ray2.tracing.modular.TracingConstants;
import de.grogra.vecmath.geom.Intersection;
import de.grogra.vecmath.geom.IntersectionList;
import de.grogra.vecmath.geom.Line;

public class BidirectionalMutationStrat extends MutationStrategy {

	
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
	
	public int kd,ka,l,m,l_,m_,real_m_, real_l_;
	public float acceptance;
	
	
	public BidirectionalMutationStrat(MetropolisProcessor metroProc) {
		super(metroProc);
		
		eyePath = new PathValues();
		eyePath.initialize(eyePathDepth+lightPathDepth);
		
		lightPath = new PathValues();
		lightPath.initialize(eyePathDepth+lightPathDepth);		
	}

	public void initialize(double[] values) {

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
	}
	
	
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

	@Override
	public float mutatePath(CombinedPathValues actualPath, CombinedPathValues mutatedPath) {
		super.mutatePath(actualPath, mutatedPath);
		
		MetropolisRenderer renderer = (MetropolisRenderer)tracingMediator.getRenderer();
		
		int outerloop =0;
		do{
			int innerloop =0;
			do{
				//1) delete subpath
				//1.1) choose length of delete-subpath --> kd
				kd = getDeletionPathLength();
	
				//2) add new subpath
				//2.1) choose length of add-subpath --> ka
				ka = getAddiationPathLength(kd);
				innerloop++;
			}while((kd==1) && (ka==1) && (innerloop<TracingConstants.LOOP_ABORT));
			if(innerloop>=TracingConstants.LOOP_ABORT) System.err.println(" BidiMutStrat: mutatePath:  Innerloop aborded!");
			
			//1.2) choose vertice of delete-subpath --> l,m
			int[] vert = getDeleteVertice(kd);
			l = vert[0];
			m = vert[1];
			
			//2.2) choose vertice of add-subpath --> l',m'
			vert = getAddVertice(ka);
			l_ = vert[0];
			m_ = vert[1];
			
			outerloop++;
			
		}while(((l==0) && (m==srcPath.pathLength-2) &&(ka==1) && renderer.directLightning_Ref) &&(outerloop<TracingConstants.LOOP_ABORT));
		if(outerloop>=TracingConstants.LOOP_ABORT) System.err.println(" BidiMutStrat: mutatePath:  Outerloop aborded!");		
		real_l_ =0;
		real_m_ =0;
		/*
		 * BEGIN: TEST
		 * TODO Remove this
		 */

		/*
		 * END: TEST
		 * 
		 */	
		
//		System.err.println("BidiMutStrat: mutatepath: BEFORE Splitting !");
//		System.err.println(" \n*******************************************************************");
//		System.err.println(" *******************************************************************");
//		System.err.println("  kd=" +kd +"  l=" +l +" m=" +m +" ka=" +ka +" l_=" +l_ +" m_="+m_);
//		System.err.println(" *******************************************************************");
//		System.err.println(" *******************************************************************\n");
//		System.err.println("  BidiMutStrat: mutatepath:  SourcePath=" +srcPath);
		
		srcPath.splitInto2Paths(l, lightPath, eyePath);
		eyePath.pathResultList = srcPath.pathResultList;
		eyePath.creatorID = srcPath.sensorID;
		lightPath.creatorID = srcPath.lightID;
		eyePath.pathLength = srcPath.pathLength -m;
		
//		System.err.println(" AFTER Splitting BEFORE Tracing:");
//		System.err.println(" Light:" +lightPath);
//		System.err.println(" Eye: "+ eyePath);
//		
		
//		System.err.println("XXXXXX    BidiMutStrat: mutatepath: before l+e Tracing:: SourcePath=" +srcPath);
//		System.err.println("YYYYYYYYYY    BidiMutStrat: mutatepath: before l+e Tracing::  tracer.iList=" +tracer.printIList());
		
		if(ka>1){
			//2.3) trace new light subpath
			traceLightSubPath(l, l_);
//			System.err.println("LLLLLLLLLL BidiMutStrat: mutatepath: after Tracing:: lightPath=" +lightPath);
//			System.err.println("EEEEEEEEEE BidiMutStrat: mutatepath: after Light Tracing before Eye Tracing:: eyePath=" +eyePath);
//			System.err.println("XXXXXX    BidiMutStrat: mutatepath: after Light Tracing before Eye Tracing:: SourcePath=" +srcPath);
//			System.err.println("YYYYYYYYYY    BidiMutStrat: mutatepath: after Light Tracing before Eye::  tracer.iList=" +tracer.printIList());

//			for(int i= 0; i < lightPath.pathLength-2;i++){
//				if(lightPath.rayListBE.get(i).directionDensity >= 1e10f) {
//					
//					lightPath.weightListBE.get(i).scale(1f/lightPath.rayListBE.get(i).directionDensity);
//					lightPath.rayListBE.get(i).directionDensity = 1;
//				}
//			}
			
//			if((lightPath.pathLength-2>=0)&& (lightPath.weightListBE.get(lightPath.pathLength-2).getMax()>10000)){
//				System.err.println("  kd=" +kd +"  l=" +l +" m=" +m +" ka=" +ka +" l_=" +l_ +" m_="+m_);
//				System.err.println("BidiMutStrat: mutatepath: before Tracing:: lightPath.length=" +lightPath.pathLength + "   eyePath.length=" +eyePath.pathLength);
//				System.err.println("LLLLLLLLLLL BidiMutStrat: mutatepath: after Tracing:: lightPath=" +lightPath);	
//			}
			
			//2.4) trace new eye subpath
			
			traceEyeSubPath(eyePath.pathLength-1, m_);
			
//			if((eyePath.pathLength-2>=0) && (eyePath.weightListBE.get(eyePath.pathLength-2).getMax()>10000)){
//				System.err.println("EEEEEEEEEEE BidiMutStrat: mutatepath: after e Tracing:: eyePath=" +eyePath);	
//				System.exit(0);
//			}
			
			
//			System.err.println("XXXXXX    BidiMutStrat: mutatepath: after l+e Tracing:: SourcePath=" +srcPath);
//			System.err.println("YYYYYYYYYY    BidiMutStrat: mutatepath: after Tracing::  tracer.iList=" +tracer.printIList());
			
			//the real values for the additionally traced vertices
			real_l_ = lightPath.pathLength -l -1;
			real_m_ = eyePath.pathLength -(srcPath.pathLength-m-1)  -1;   
			
//			for(int i= 0; i < eyePath.pathLength-2;i++){
//				if(eyePath.rayListBE.get(i).directionDensity >= 1e10f) {
//					
//					eyePath.weightListBE.get(i).scale(1f/eyePath.rayListBE.get(i).directionDensity);
//					eyePath.rayListBE.get(i).directionDensity = 1;
//				}
//			}

		}

		if((real_l_ != l_)||(real_m_!=m_)) return registerError(TRACINGGOAL_WASNT_REACHED);
		
//		int index = lightPath.pathLength-2;
//		if (index<0) index=0; 
//		if(lightPath.weightListBE.get(0).sum()<68){
//			System.err.println("LLLLLLLLLLL BidiMutStrat: mutatepath: after Tracing:: lightPath=" +lightPath);
//			System.exit(0);
//		}
//		index = eyePath.pathLength-2;
//		if (index<0) index=0;
//		if(eyePath.weightListBE.get(0).sum()!=3){
//			System.err.println("EEEEEEEEEEEEE BidiMutStrat: mutatepath: after Tracing:: eyePath=" +eyePath);
//			System.exit(0);
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
			return registerError(NOT_VISIBLE);		
		}
		
		//3) combine the new paths
		complTracer.complement2Paths(lightPath, lightPath.pathLength-1, eyePath, eyePath.pathLength-1, false, this.mutatedPath);
		this.mutatedPath.pathResultList = eyePath.pathResultList;

		if (! hitsCanvas(mutatedPath)){
			abbortCode = CANVAS_WASNT_HIT;
			return -1;
		}
		
		
		if(!mutatedPath.isValid()){
			return registerError( NO_VALID_COMBINEDPATH);
		}
		
//		System.err.println("BidiMutStrat: mutatepath: after Tracing and Recombining:: mutatedPath=" +mutatedPath);
		
//		if(mutatedPath.weightListBE.get(mutatedPath.pathLength-2).sum()>10000) {
//			System.err.println("BidiMutStrat: mutatepath: Calculation Error!!!");
//			System.err.println("  kd=" +kd +"  l=" +l +" m=" +m +" ka=" +ka +" l_=" +l_ +" m_="+m_);
//			System.err.println("BidiMutStrat: mutatepath: before Tracing:: lightPath.length=" +lightPath.pathLength + "   eyePath.length=" +eyePath.pathLength);
//			System.err.println("LLLLLLLLLLL BidiMutStrat: mutatepath: after Tracing:: lightPath=" +lightPath);	
//			System.err.println("EEEEEEEEEEE BidiMutStrat: mutatepath: after e Tracing:: eyePath=" +eyePath);	
//			System.err.println("MMMMMMMMMMM BidiMutStrat: mutatepath: after complementing=" +mutatedPath);	
//			System.exit(0);
//		}

		//4) calculate acepptance-prob
		acceptance = calculateAcceptanceProbality(l,m, real_l_,real_m_);
		
		if(abbortCode==SUCCESSFUL_MUTATIONRUN){
			count_NoError++;
			if(m== actualPath.pathLength-1)  countOfLensEdgeMutations++;
			if(l==0) countOfFirstLightRayMutations++;
			if((m!= actualPath.pathLength-1) &&(l!=0)) countOfMiddlePathMutations++;
		}
		
		
		return acceptance;
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
	

	
}

