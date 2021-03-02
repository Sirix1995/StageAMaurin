package de.grogra.ray2.metropolis;

import java.util.ArrayList;

import de.grogra.ray2.tracing.modular.CombinedPathValues;

public interface MetropolisStrategy {

	public void resetAll();
	
	public ArrayList<String> getStatistics();
	
	public float mutatePath(CombinedPathValues actualPath, CombinedPathValues mutatedPath);
	
	public void pathChanged();
	public String getAcceptanceAbbortDescription();
	public String getDescription();
}
