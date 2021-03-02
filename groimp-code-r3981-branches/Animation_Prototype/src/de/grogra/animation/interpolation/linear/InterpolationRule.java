package de.grogra.animation.interpolation.linear;

import java.io.Serializable;

public interface InterpolationRule extends Serializable {
	
	public Object getInterpolatedValue(Object value1, Object value2, double ratio);

}
