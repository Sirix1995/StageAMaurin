package de.grogra.ray2.tracing.modular;

import de.grogra.ray2.antialiasing.Antialiasing;
import de.grogra.ray2.antialiasing.MetropolisAntiAliasing;
import de.grogra.ray2.tracing.PixelwiseRenderer;
import de.grogra.ray2.tracing.RayProcessorBase;

public class TracingMediator {

	PixelwiseRenderer renderer;
	RayProcessorBase processor;
	Antialiasing antialiser;
	
	LineTracer linetracer;
	ComplementTracer complementTracer;
	
	MemoryHelper memHelper;
	
	public Antialiasing getAntialiser() {
		return antialiser;
	}
	public void setAntialiser(Antialiasing antialiser) {
		this.antialiser = antialiser;
	}
	public ComplementTracer getComplementTracer() {
		return complementTracer;
	}
	public void setComplementTracer(ComplementTracer complementTracer) {
		this.complementTracer = complementTracer;
	}
	public LineTracer getLinetracer() {
		return linetracer;
	}
	public void setLinetracer(LineTracer linetracer) {
		this.linetracer = linetracer;
	}
	public RayProcessorBase getProcessor() {
		return processor;
	}
	public void setProcessor(RayProcessorBase processor) {
		this.processor = processor;
	}
	public PixelwiseRenderer getRenderer() {
		return renderer;
	}
	public void setRenderer(PixelwiseRenderer renderer) {
		this.renderer = renderer;
	}

	
	public MetropolisAntiAliasing getMetropolisAntialiser(){
		if(antialiser instanceof MetropolisAntiAliasing) return (MetropolisAntiAliasing)antialiser;
		return null;
		
	}
	public MemoryHelper getMemHelper() {
		return memHelper;
	}
	public void setMemHelper(MemoryHelper memHelper) {
		this.memHelper = memHelper;
	}
	
}
