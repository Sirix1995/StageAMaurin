package de.grogra.ext.sunshine.spectral.shader;

import de.grogra.ext.sunshine.spectral.MaterialHandler.BxDFTypes;

public interface MaterialCollector {

	public BxDFTypes getBxDFType();
	
	public SunshineChannel[] collectData(); 
	
}
