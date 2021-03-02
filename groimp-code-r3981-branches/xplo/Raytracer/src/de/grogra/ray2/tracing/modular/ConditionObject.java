package de.grogra.ray2.tracing.modular;

public abstract class ConditionObject {
	
	public PathValues path;
	public LineTracer lineTracer;
	
	public ConditionObject(PathValues path, LineTracer lineTracer) {
		this.path = path;
		this.lineTracer = lineTracer;
	}

	public boolean stopOnCondition(){
		return false;
	}
	
}
