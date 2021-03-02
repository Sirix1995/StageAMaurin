package de.grogra.ray2.tracing;

import java.util.ArrayList;
import java.util.Random;

import javax.vecmath.Color4f;
import javax.vecmath.Tuple3d;

import de.grogra.ray.physics.Spectrum;
import de.grogra.ray2.Scene;
import de.grogra.ray2.metropolis.DirectLightingCalculator;
import de.grogra.ray2.metropolis.MetropolisPathMutator;
import de.grogra.ray2.metropolis.strategy.LensSubpathStrat;
import de.grogra.ray2.tracing.MetropolisRenderer.MetropolisResult;
import de.grogra.ray2.tracing.PixelwiseRenderer.Result;
import de.grogra.ray2.tracing.modular.CombinedPathValues;
import de.grogra.ray2.tracing.modular.MemoryHelper;
import de.grogra.vecmath.geom.Line;

public class MetropolisProcessor extends BiDirectionalProcessor {

	public static final String CORRECTION_FACTOR = "MetropolisPathTracer/correctionFactor";
	public static final String MUTATION_PP_COUNT = "MetropolisPathTracer/mutationPPCount";
//	public static final String DIRECTLIGHT = "MetropolisPathTracer/DirectLighting";
//	public static final String SWITCHFLAG = "MetropolisPathTracer/testFlag";

	public int minimalPathLength, maximalPathLength;
	
	public static boolean stopped;
	
	public int imageWidth;
	public int imageHeight;

//	private static float correctionFlag;
//	public static int mutationPPCount = 1;
//	private static int maxMutationLoop = 100;
	private static float rendererBrightness;
	
	private static int zeroAcceptanceCount =0;
	private static int notAcceptanceCount =0;
	private static int overallCount =0;
	static int count_LoopAbort;
	
	
	private int renderedPixels;
	private int mutatedPixelFinish;
	
//	private boolean testFlag;
	
	private int processorID;
	
	
	
//	protected int imageUpdateDistance = 50;
//	protected int nextImageUpdateI=50;
	protected int imageUpdateRate = 7000;
	protected long nextImageUpdateTime=7000;
	protected long timeDiff=0;
	
	private MetropolisResult result;
	
	private MemoryHelper memHelper;
	private MetropolisRenderer renderer;
	
	DirectLightingCalculator directLightCalc;
	
	boolean directLightingMode;
	boolean expectedValuesMode;
//	private Tuple3d maxWeight;

	
	private CombinedPathValues seedPath;
	
	private CombinedPathValues tmpCombPath1, tmpCombPath2;

	private MetropolisPathMutator mutator;
	
	private boolean schalter = false; 
	
	public MetropolisProcessor() {
		super();
	}
	
	
	
	public RayProcessor dup (Scene scene)
	{
		
		MetropolisProcessor p = (MetropolisProcessor) clone ();
		p.lightProcessor = lightProcessor.dup (scene);
		p.scene = scene;
		p.initLocals ();
		p.initialize(renderer, scene);
		return p;
	}
	

	
	
	public void initialize (PixelwiseRenderer renderer, Scene scene)
	{
		super.initialize(renderer, scene);
		
		this.renderer  = (MetropolisRenderer)renderer;
		 
//		correctionFlag = renderer.getNumericOption(CORRECTION_FACTOR, 2).floatValue();
//		assert correctionFlag >=0;
//		
//		testFlag = renderer.getBooleanOption(SWITCHFLAG, true);
		 
//		
//		mutationPPCount = renderer.getNumericOption(MUTATION_PP_COUNT, 10000).intValue();
//		assert mutationPPCount >=1;
		
		directLightingMode =  ((MetropolisRenderer)renderer).directLightning_Ref;
		expectedValuesMode = ((MetropolisRenderer)renderer).expectedValues_Ref;
		
//		System.err.println(" MetroPoc: initialize: mutationPPCount=" + mutationPPCount);
		
		isMetropolis = true;
		
		mutator = new MetropolisPathMutator(this);
		
		tmpCombPath1 = new CombinedPathValues();
		tmpCombPath1.initialize(eyePathDepth+lightPathDepth);
		
		tmpCombPath2 = new CombinedPathValues();
		tmpCombPath2.initialize(eyePathDepth+lightPathDepth);
		
		seedPath = new CombinedPathValues();
		seedPath.initialize(eyePathDepth+lightPathDepth);
		
		memHelper = new MemoryHelper(3*(eyePathDepth+lightPathDepth), scene);

		tracingMediator.setMemHelper(memHelper);
		
		directLightCalc = new DirectLightingCalculator();
		directLightCalc.setLightProccessor(getLightProcessor());
		
		imageWidth =0;
		imageHeight =0;
		
		renderedPixels =0;
		mutatedPixelFinish =0;
//		maxMutationLoop = 100;
		rendererBrightness =0;
		
		zeroAcceptanceCount =0;
		notAcceptanceCount =0;
		overallCount =0;
		count_LoopAbort =0;
		
		stopped = false;
		
		minimalPathLength = eyePathDepth+lightPathDepth;
		maximalPathLength =0;
	}
	
	public synchronized void setMinMaxPathLength(int testLength){
		maximalPathLength = Math.max(testLength, maximalPathLength);
		minimalPathLength = Math.min(testLength,minimalPathLength);
		
		
	}

	public void getColorFromRay (Line ray, Spectrum resp, Color4f color, Random random)
	{
		
		renderedPixels =0;
//		
//		caluclateMutationLooopCounter();
		
//		System.err.println(" Metro-Proces: getColorFromrRay MutaionloopCount=" +mutatedPixelFinish);
//		System.err.println(" Metro-Proces: getColorFromrRay: initiale Scene.local2Global-Matrix: \n " + scene.getLightTransformation(0));

		
		//1) trace eye path
		//2) for all light sources: trace light path
		//3) choose best path combination for seed path of the metropolis algorithm

		lineTracer.setSafeMemoryMode(null);
		super.getColorFromRay(ray, resp, color, random);
		lineTracer.setSafeMemoryMode(memHelper);
		
		if(bestS<0) return;
		
		
//		System.err.println("Metro-Proces: getColorFromrRay  BestS=" +bestS +" bestT=" +bestT );
//		System.err.println(" Light:" +lightPathValues[bestLight]);		
//		System.err.println(" Eye: "+ eyePathValues);

		
		//4) rebuild the seed path from the pathvalues
		CombinedPathValues seedPath = getNextPath2Mutate();
		complementTracer.complement2Paths(lightPathValues[bestLight],bestS,eyePathValues , bestT, false,seedPath);

		if(!seedPath.isValid()) return;
		
//		System.err.println(" CombinedPath: " +seedPath);
	
		
		CombinedPathValues mutatedPath = getNextPath2Mutate();
		
		CombinedPathValues actualPath = seedPath;
		//5) Mutationloop
		int loopCount=0;
		float acceptProb =0;
		do{

				//6) mutate the actual path
			acceptProb = mutator.mutatePath(actualPath,mutatedPath);
			
//			System.err.println("MetroProc: getColor:    "+processorID +"  rendPix=" +renderedPixels +"  targetCount=" +mutatedPixelFinish);
			
			
			if((mutatedPath.pathLength>2) && directLightingMode) {
				if( mutator.getLastStrategy() instanceof LensSubpathStrat){
								//							System.err.println("MetroProc: getColor: DirectLighting contributed!");
															Tuple3d col = directLightCalc.calculateDirectLight(mutatedPath, 
																		mutatedPath.pathLength-2, random);
															/* TODO Remove this */
								//							col.x = 0;
								//							col.y = 100; // Test um Fehler im DL deutlich zu machen
								//							col.z = 0;
															/* TODO Remove this */
															recordColor(mutatedPath, col);
							}
			}
				
			
			
			if(expectedValuesMode)recordSample(mutatedPath, ((acceptProb>0)?acceptProb:0), actualPath);
			
//			System.err.println("MetroProc: getColor: acceptance = " +acceptProb   +( (acceptProb<0)? ("    error=" +mutator.getLastStrategy().getAcceptanceAbbortDescription()) : ""));
			
			
			memHelper.freeUnusedPathElements(actualPath, mutatedPath);	
				
			//7) test acceptance

			if(isAccepted(acceptProb)){
	
//				System.err.println("MetroProc: getColor: acceptance = " +acceptProb   +"   Strat=" +mutator.getLastStrategy() +"  Descr=" +mutator.getLastStrategy().getDescription());
				
				//8) if accepted than replace old with new path
				setMinMaxPathLength(mutatedPath.pathLength);

				actualPath = mutatedPath;
				mutatedPath = getNextPath2Mutate();
				
			}else{
//				System.err.println("Metro-Proces-loop: Inner mutatedPixelFinish had to be abborted!");
//				count_LoopAbort++;
			}

			if(!expectedValuesMode)recordSample(actualPath);
			
//				System.err.println("Metro-Proces-loop: Strategy " +mutator.getlastStrategy() +" wasn't accepted");

			loopCount++;
//			if((loopCount % 3000) == 0){
//				System.err.println(" Metro-Proces-loop: " +loopCount +" of " +mutatedPixelFinish +" act. acceptance prob= " +acceptProb);
//				System.err.println(" 				last Strat=" +mutator.getLastStrategy());
//			}
			
		}while((renderedPixels < mutatedPixelFinish) && ! stopped );
		
		mergeWithRenderer();
		
	}
	
	


	
	
	private boolean isAccepted(float acceptProb){
		
		overallCount++;
		
		if(acceptProb<=0){
//			System.err.println("Metro-Proc: isAccepted: Strategy was DENIED because of 0-Acceptance!: " +mutator.getlastStrategy().getAcceptanceAbbortDescription());
			notAcceptanceCount++;
			zeroAcceptanceCount++;
			return false;
		}
		
		float a = rand.nextFloat();
		if(a<acceptProb) {
//			System.err.println("Metro-Proc: isAccepted: Strategy was ACCEPTED with " +acceptProb +" by " +a);
			return true;
		}
		
		notAcceptanceCount++;
//		System.err.println("Metro-Proc: isAccepted: Strategy was DENIED with " +acceptProb +" by " +a);
		return false;
	}
	
	private void recordColor(CombinedPathValues path, Tuple3d color){
		renderedPixels++;
		
		float count = 1f; //renderer.mutationPPCount/2f;
		Line lensEdge = path.rayListEB.get(path.pathLength-2);		

		result.data.add( result.lastIndex*4,(float)color.x /count);
		result.data.add( (result.lastIndex*4)+1,(float)color.y /count);
		result.data.add( (result.lastIndex*4)+2,(float)color.z /count);
		result.data.add( (result.lastIndex*4)+3,1f);//path.color.w);
		result.lines.add(result.lastIndex,(int) lensEdge.y);
		result.cols.add(result.lastIndex, (int)lensEdge.x);
		
//		System.err.println("  MetroProc: recordColor: mult = " +mult +"color=" + color +" w=" +path.color.w);
		
		result.lastIndex++;
	}
	
	/**
	 * records the color of the actual path sample
	 * (method without "Expected Values"-Refinement) 
	 * 
	 * @param path - the actual path
	 */
	private void recordSample(CombinedPathValues path){
		
		
//		System.err.println("MetroProc:  recordSample(single):  one sample recorded!");
		
		renderedPixels++;
		
		tracingMediator.getMetropolisAntialiser().registerUsedLensEdge(path.rayListEB.get(path.pathLength-2).x, 
																		path.rayListEB.get(path.pathLength-2).y);
		
//		if(acceptance < 0.1) acceptance = 0.1f;
		
		Line lensEdge = path.rayListEB.get(path.pathLength-2);		
		float I_0 = getI_0(path);
		
//		if(I_0 < 0.01) I_0 = 0.01f;
		
		result.data.add( result.lastIndex*4,path.color.x *I_0);
		result.data.add( (result.lastIndex*4)+1,path.color.y *I_0);
		result.data.add( (result.lastIndex*4)+2,path.color.z *I_0);
		result.data.add( (result.lastIndex*4)+3,path.color.w /** acceptance*/);
		result.lines.add(result.lastIndex, (int)lensEdge.y);
		result.cols.add(result.lastIndex, (int)lensEdge.x);

		// progress changed
		if ((result.lastIndex >= 0.30f*(imageHeight*imageWidth)) || (System.currentTimeMillis () >= nextImageUpdateTime))
		{
			
			((MetropolisRenderer)renderer).merge(result);			
//			System.err.println(" MetropolisProcessor: synchronized after " +result.lastIndex +" mutated Pixels of " +(imageHeight*imageWidth) +" overall! TimeDiff = " +timeDiff );
			result.lines.setSize (0);
			result.data.setSize (0);
			((MetropolisResult)result).cols.setSize(0);
			((MetropolisResult)result).lastIndex =0;

			timeDiff = nextImageUpdateTime;
			nextImageUpdateTime = System.currentTimeMillis()+ imageUpdateRate;
			timeDiff = nextImageUpdateTime -timeDiff;
			
			
		}else 	result.lastIndex++;
	}	
	
	/**
	 * records the color of the actual  mutatted path sample and the origin path weightend 
	 * by their acceptance value 
	 * (method for "Expected Values"-Refinement) 
	 * 
	 * @param path - the actual path
	 */
	private void recordSample(CombinedPathValues recordPath,float acceptance, CombinedPathValues oldPath){
		
//		System.err.println("MetroProc:  recordSample(twice):  Two sample recorded!");
		
		Line lensEdge = null;		
		float I_0_new = 0;
		float I_0_old = 0;
		float I_0 = I_0_new ;
		
		if(acceptance>0){
			
			if(recordPath.pathLength>1){
				tracingMediator.getMetropolisAntialiser().registerUsedLensEdge(recordPath.rayListEB.get(recordPath.pathLength-2).x, 
						recordPath.rayListEB.get(recordPath.pathLength-2).y);
				
		//		if(acceptance < 0.1) acceptance = 0.1f;
				
				lensEdge = recordPath.rayListEB.get(recordPath.pathLength-2);		
				I_0_new = getI_0(recordPath);
				I_0 = I_0_new ;		
				
				result.data.add( result.lastIndex*4,recordPath.color.x * acceptance*I_0);
				result.data.add( (result.lastIndex*4)+1,recordPath.color.y * acceptance*I_0 );
				result.data.add( (result.lastIndex*4)+2,recordPath.color.z * acceptance*I_0 );
				result.data.add( (result.lastIndex*4)+3,recordPath.color.w /** acceptance*/);
				result.lines.add(result.lastIndex,(int) lensEdge.y);
				result.cols.add(result.lastIndex, (int)lensEdge.x);
				
				if((result.data.get(result.lastIndex*4) < 0) ||
					(result.data.get(result.lastIndex*4 +1) < 0) ||
					(result.data.get(result.lastIndex*4 +2)< 0) ||
					(result.data.get(result.lastIndex*4 +3) <0)){
					
					System.err.println(" MetroProc: recordSampl:     Negative Value!!!!  newColor=" 
							+recordPath.color +"   acceptance=" +acceptance +"   I_0=" +I_0);
					System.err.println("     lastStrat=" +mutator.getLastStrategy());
					System.exit(0);
				}
				
				renderedPixels++;
				result.lastIndex++;
			}
		}
		if(acceptance < 1) {
			
//			System.err.println("MetroProc:  recordSample(twice):  Two sample recorded!");
			
			tracingMediator.getMetropolisAntialiser().registerUsedLensEdge(oldPath.rayListEB.get(oldPath.pathLength-2).x, 
					oldPath.rayListEB.get(oldPath.pathLength-2).y);
			

			lensEdge = oldPath.rayListEB.get(oldPath.pathLength-2);	
			I_0_old = getI_0(oldPath);
			I_0 = I_0_old ;
			
			result.data.add( result.lastIndex*4,oldPath.color.x *(1f- acceptance)*I_0);
			result.data.add( (result.lastIndex*4)+1,oldPath.color.y *(1f- acceptance)*I_0);
			result.data.add( (result.lastIndex*4)+2,oldPath.color.z * (1f- acceptance)*I_0);
			result.data.add( (result.lastIndex*4)+3,oldPath.color.w /**(1- acceptance)*/);
			result.lines.add(result.lastIndex, (int)lensEdge.y);
			result.cols.add(result.lastIndex,(int) lensEdge.x);		

			
			if((result.data.get(result.lastIndex*4) < 0) ||
					(result.data.get(result.lastIndex*4 +1) < 0) ||
					(result.data.get(result.lastIndex*4 +2)< 0) ||
					(result.data.get(result.lastIndex*4 +3) <0)){
					
					System.err.println(" MetroProc: recordSampl:     Negative Value!!!!  oldColor=" 
							+oldPath.color +"   acceptance=" +acceptance +"   I_0=" +I_0);
					System.err.println("     lastStrat=" +mutator.getLastStrategy());
					System.exit(0);
			}
			
			renderedPixels++;
			result.lastIndex++;
			
//		}else{
//			System.err.println("MetroProc:  recordSample(twice):  one sample recorded!");
		}
		// progress changed
		if ((result.lastIndex >= 0.40f*(imageHeight*imageWidth)) || (System.currentTimeMillis () >= nextImageUpdateTime))
		{
			
//			System.err.println("MetroProc:  recordSample(twice):  merging!");
			
			mergeWithRenderer();

			timeDiff = nextImageUpdateTime;
			nextImageUpdateTime = System.currentTimeMillis()+ imageUpdateRate;
			timeDiff = nextImageUpdateTime -timeDiff;

		}	
	}
	
	public void mergeWithRenderer(){
		((MetropolisResult)result).lastIndex --;
//		System.err.println(" MetropolisProcessor: synchronized after " +result.lastIndex +" mutated Pixels of " +(imageHeight*imageWidth) +" overall! TimeDiff = " +timeDiff 
//				+"   resulta.data.size=" +result.data.size);
		((MetropolisRenderer)renderer).merge(result);			
		
		result.lines.setSize (0);
		result.data.setSize (0);
		((MetropolisResult)result).cols.setSize(0);
		((MetropolisResult)result).lastIndex =0;
	}
	
	
	
	float getI_0(CombinedPathValues path){
		MetropolisRenderer render = (MetropolisRenderer) tracingMediator.getRenderer(); 
		if(render.twoStage_Ref && (render.actRenderingStage == render.SECOND_STAGE)){
			int x = (int)path.rayListEB.get(path.pathLength-2).x;
			int y = (int)path.rayListEB.get(path.pathLength-2).y;
			float ret = render.getFirstStageImageValue(x, y);
			if(ret==0) System.err.println("  MetroProc: getI0:     return=" +ret);
			return ret;
		}else return 1f;
	}
	
	
	CombinedPathValues getNextPath2Mutate() {
		schalter = !schalter;
		
		if(schalter) return tmpCombPath1;
		else return tmpCombPath2;
		
	}
	
	public void resetAll(){
		minimalPathLength =eyePathDepth+lightPathDepth;
		maximalPathLength =0;
		overallCount =0;
		notAcceptanceCount =0;
		zeroAcceptanceCount =0;
		count_LoopAbort =0;
		stopped = false;
		mutator.resetAll();
	}
	
	
	public void initializeLoop(float brightness, int imageWidth,int imageHeight, int mutatedPixPerProc, MetropolisResult result, int procId){
		
		rendererBrightness = brightness;
		this.imageHeight = imageHeight; 
		this.imageWidth = imageWidth;
		mutatedPixelFinish = mutatedPixPerProc;
		processorID = procId;
		
		this.result  = result;
		
	}
	
	
	public Result getResult(){
		return result;
	}
	
	public int getMutatedPixels(){
		return renderedPixels;
	}
	
//	
//	public void setResult(MetropolisResult result){
//		this.result = result;
//	}
	
	@Override
	protected void appendStatisticsImpl(StringBuffer stats) {
		
		stats.append("Metropolis Light Transport Statistics: \n");
//		stats.append("    average mutation per Pixel=" +((float)renderedPixels/(imageWidth*imageHeight))  + "\n" );
		stats.append("    Max. length of accepted paths=" +maximalPathLength  + "\n" );
		stats.append("    Min. length of accepted paths=" +minimalPathLength  + "\n" );
		stats.append("    mutation Loop=" +mutatedPixelFinish  + "\n \n");
		stats.append("    total count of mutation  : " +overallCount + "\n");
		stats.append("    count of accepted mutation  : " +(overallCount-notAcceptanceCount) + "\n");
		stats.append("    count of rejected mutations: " +notAcceptanceCount +" (" +((float)notAcceptanceCount/overallCount) +")" +"\n");
		stats.append("    count of rejected mutations with mutation errors: " +zeroAcceptanceCount +" (" +((float)zeroAcceptanceCount/notAcceptanceCount) +")" +"\n \n");
		stats.append("    count of forced mutation loop abborts: " +count_LoopAbort +"\n \n");
		stats.append("    Strategy Statictics: \n");
		ArrayList<String> mutStat = mutator.getStatistics();
		for(String stat : mutStat) stats.append("     " +stat);
//		stats.append("\n \n");
//		stats.append("    Inital Path: " +seedPath +"\n \n");
//		getNextPath2Mutate();
//		stats.append("    Last Path: " +getNextPath2Mutate() +"\n \n");
	}
	
	public void pathChanged(){
		mutator.pathChanged();
	}
}
