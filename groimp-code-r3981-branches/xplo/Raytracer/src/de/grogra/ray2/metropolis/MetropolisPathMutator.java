package de.grogra.ray2.metropolis;

import java.util.ArrayList;

import de.grogra.ray2.antialiasing.MetropolisAntiAliasing;
import de.grogra.ray2.metropolis.strategy.BidirectionalMutationStrat;
import de.grogra.ray2.metropolis.strategy.CausticPerturbation;
import de.grogra.ray2.metropolis.strategy.LensSubpathStrat;
import de.grogra.ray2.metropolis.strategy.MutationStrategy;
import de.grogra.ray2.metropolis.strategy.LensPerturbationStrat;
import de.grogra.ray2.tracing.MetropolisProcessor;
import de.grogra.ray2.tracing.PixelwiseRenderer;
import de.grogra.ray2.tracing.modular.CombinedPathValues;
import de.grogra.ray2.tracing.modular.ComplementTracer;
import de.grogra.ray2.tracing.modular.LineTracer;
import de.grogra.ray2.tracing.modular.PathValues;
import net.goui.util.MTRandom;

public class MetropolisPathMutator {
	
	public static final String STRAT1 = "MetropolisPathTracer/Strategies/BidirectionalStrat";
	public static final String STRAT2 = "MetropolisPathTracer/Strategies/LensSubpathStrat";
	public static final String STRAT3 = "MetropolisPathTracer/Strategies/LensPerturbStrat";
	public static final String STRAT4 = "MetropolisPathTracer/Strategies/CausticPerturbStrat";
	MTRandom rnd; 
	
//	MutationStrategy strat1;
	static int strat1Count;
	
//	MutationStrategy strat2;
	static int strat2Count;
	
//	MutationStrategy strat3;
	static int strat3Count;
	
	static int strat4Count;
	
//	public  int strategyCount = 3;
	int lastStrategyNumber;
	
	ArrayList<MetropolisStrategy> stratList;
	
	ArrayList<String> mutatorStatistic;
	
	public MetropolisPathMutator(MetropolisProcessor metroProc) {
		
		
//		strat1 = new BidirectionalMutationStrat(metroProc);
//		
//		strat2 = new LensSubpathStrat(metroProc);	
//		strat3 = new LensPerturbationStrat(metroProc);
		mutatorStatistic = new ArrayList<String>();
		
		stratList = new ArrayList<MetropolisStrategy>();
		
		if( metroProc.tracingMediator.getRenderer().getBooleanOption(STRAT1, true)){
			stratList.add(new BidirectionalMutationStrat(metroProc));
		}
		if( metroProc.tracingMediator.getRenderer().getBooleanOption(STRAT2, true)){
			stratList.add(new LensSubpathStrat(metroProc));
		}
		if( metroProc.tracingMediator.getRenderer().getBooleanOption(STRAT3, true)){
			stratList.add(new LensPerturbationStrat(metroProc));
		}
		if( metroProc.tracingMediator.getRenderer().getBooleanOption(STRAT4, true)){
			stratList.add(new CausticPerturbation(metroProc));
		}
		rnd = new MTRandom(metroProc.tracingMediator.getRenderer().getSeed());
	}
	
	
	public void resetAll(){

		strat1Count =0;
		strat2Count=0;
		strat3Count=0;
		strat4Count=0;
		for(MetropolisStrategy strat: stratList) strat.resetAll();
	}
	
	
	public float mutatePath(CombinedPathValues actualPath, CombinedPathValues mutatedPath){
		
		MetropolisStrategy strategy = determineStrategy();
		if(strategy== null) return -1;
		return strategy.mutatePath(actualPath, mutatedPath); 
	}

	
	private MetropolisStrategy determineStrategy(){
		
		if(stratList.size()<1) {
			System.err.println("MetroPathMutat: determinStrat:   NO Strategy to choose!");
			return null;
		}
		
		int i;
//		do{
			i = rnd.nextInt(stratList.size());
		
//		}while((i==lastStrategyNumber)&&(stratList.size()>1));

		
//		i=1;
			
		lastStrategyNumber =i;
		
		MetropolisStrategy ret = stratList.get(i);
		
		if (ret instanceof BidirectionalMutationStrat) {
//		System.err.println("Metro-PathMutator: determStrat Strat 1:" +strat1 +" was chosen!");
			strat1Count++;	
			
		}else if(ret instanceof LensSubpathStrat) {
//		System.err.println("Metro-PathMutator: determStrat Strat 2:" +strat2 +" was chosen!");
			strat2Count++;		
			
		}else if (ret instanceof LensPerturbationStrat) {
//		System.err.println("Metro-PathMutator: determStrat: Error Unknown Strategy chosen!!!");
			strat3Count++;					
		}if (ret instanceof CausticPerturbation) {
//		System.err.println("Metro-PathMutator: determStrat: Error Unknown Strategy chosen!!!");
			strat4Count++;					
		}
		
		
		return ret;
	}
	
	
	public MetropolisStrategy getLastStrategy(){
		return stratList.get(lastStrategyNumber);
	}
	
//	public MutationStrategy getStrategy(int no){
//
////		System.err.println("Metro-PathMutator: determStrat Number " +no +" ist requested!");
//		
//		switch (no) {
//		case 0:	
////			System.err.println("Metro-PathMutator: determStrat Strat 1:" +strat1 +" was chosen!");
//			strat1Count++;
//			return strat1;
//		case 1:	
////			System.err.println("Metro-PathMutator: determStrat Strat 2:" +strat2 +" was chosen!");
//			strat2Count++;
//			return strat2;
//		case 2:	
////			System.err.println("Metro-PathMutator: determStrat Strat 3:" +strat3 +" was chosen!");
//			strat3Count++;
//			return strat3;			
//		default:
////			System.err.println("Metro-PathMutator: determStrat: Error Unknown Strategy chosen!!!");
//			strat1Count++;
//			return strat1;
//		}		
//	}
	
	public int getMutationStrategyCount(){
		return stratList.size(); 
	}
	
//	public MutationStrategy getlastStrategy(){
//		return getStrategy(lastStrategyNumber-1);
//	}
	
	public ArrayList<String> getStatistics(){
		mutatorStatistic.clear();
		
//		ArrayList<String> bidiStat = strat1.getStatistics();
//		if(bidiStat!=null) for(String stat: bidiStat) mutatorStatistic.add("   " +stat);
//		
//
//		ArrayList<String> lensStat = strat2.getStatistics();
//		if(lensStat!=null) for(String stat: lensStat) mutatorStatistic.add("   " +stat);
//		
//
//		ArrayList<String> perturbStat = strat3.getStatistics();
//		if(perturbStat!=null) for(String stat: perturbStat) mutatorStatistic.add("   " +stat);
		
		for(MetropolisStrategy strat: stratList){
			if (strat instanceof BidirectionalMutationStrat) {
				mutatorStatistic.add("Count of Bidirection Strategy:" +strat1Count +"\n");
					
			}else if(strat instanceof LensSubpathStrat) {
				mutatorStatistic.add("Count of Lens Sub Path Strategy:" +strat2Count +"\n");	
					
			}else if (strat instanceof LensPerturbationStrat) {
				mutatorStatistic.add("Count of Lens Perturbation Strategy:" +strat3Count +"\n");					
			}
			else if (strat instanceof CausticPerturbation) {
					mutatorStatistic.add("Count of Caustic Perturbation Strategy:" +strat4Count +"\n");					
				}
			if(strat.getStatistics()!=null) for(String stat: strat.getStatistics()) mutatorStatistic.add("   " +stat);
		}
		
		return mutatorStatistic;
	}

	
	public void pathChanged(){
		for(MetropolisStrategy strat: stratList) strat.pathChanged();
	}
	
}
