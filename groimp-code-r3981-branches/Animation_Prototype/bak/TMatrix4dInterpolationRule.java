package de.grogra.animation.interpolation.linear;

import javax.vecmath.Quat4d;
import de.grogra.math.TMatrix4d;

public class TMatrix4dInterpolationRule implements InterpolationRule {

	private static DoubleInterpolationRule ir = new DoubleInterpolationRule();
	private TMatrix4d result = new TMatrix4d();
	
	public TMatrix4d getInterpolatedValue(Object value1, Object value2,
			double ratio) {
		
		result.setIdentity();
		TMatrix4d m1 = (TMatrix4d) value1;
		TMatrix4d m2 = (TMatrix4d) value2;
		
		// translation
		result.setColumn(3,
				ir.getInterpolatedValue(m1.m03, m2.m03, ratio),
				ir.getInterpolatedValue(m1.m13, m2.m13, ratio),
				ir.getInterpolatedValue(m1.m23, m2.m23, ratio),
				1
			);
		
		// rotation
		Quat4d r1 = new Quat4d();
		Quat4d r2 = new Quat4d();
		m1.get(r1);
		m2.get(r2);
		r1.interpolate(r2, ratio);
		result.setRotation(r1);
		
		// scale
		result.setScale(ir.getInterpolatedValue(m1.getScale(), m2.getScale(), ratio));
		
		return result;
	}

}
