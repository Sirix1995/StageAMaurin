package de.grogra.animation.interpolation.linear;

import de.grogra.math.TVector3d;

public class TVector3dInterpolationRule implements InterpolationRule {

	private DoubleInterpolationRule ir = new DoubleInterpolationRule();
	
	public Object getInterpolatedValue(Object value1, Object value2, double ratio) {
		TVector3d result = new TVector3d();
		
		result.set(
				ir.getInterpolatedValue(((TVector3d) value1).x, ((TVector3d) value2).x, ratio),
				ir.getInterpolatedValue(((TVector3d) value1).y, ((TVector3d) value2).y, ratio),
				ir.getInterpolatedValue(((TVector3d) value1).z, ((TVector3d) value2).z, ratio)
			);
		
		return result;
	}
	
}
