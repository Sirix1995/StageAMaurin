package de.grogra.animation.interpolation;

import java.io.Serializable;
import java.util.Set;
import de.grogra.reflect.Type;

public interface Interpolation extends Serializable {

	public void setInterpolationType(Type<?> type);
	
	public boolean putValue(int time, Object value);
	
	public void changeValue(int oldTime, int newTime, Object value);
	
	public Object getValue(double time);
	
	public void getTimes(Set<Integer> times);
	
}
