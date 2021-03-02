package de.grogra.imp3d.glsl.material.channel;

import javax.vecmath.Matrix3f;

import de.grogra.imp3d.glsl.material.MaterialConfiguration;
import de.grogra.imp3d.shading.AffineUVTransformation;
import de.grogra.math.Channel;
import de.grogra.math.ChannelMap;

/**
 * Implementation of AffineUVTransformation. Will return a vec2.
 * 
 * @author Konni Hartmann
 */
public class GLSLAffineUVTransformation extends GLSLChannelMapNode {

	/**
	 * Generates the 3x3 Matrix holding the affine Transformation of the represented AffineUVTransformation Object.
	 * @param aUV The represented Object
	 * @return The Matrix representing the UV-Transformation
	 */
	Matrix3f generateTransMatrix(AffineUVTransformation aUV) {
		Matrix3f m = new Matrix3f(), i = new Matrix3f();
		m.setIdentity();
		i.setIdentity();

		float angle = aUV.getAngle();
		float shear = aUV.getShear();
		float scaleU = aUV.getScaleU();
		float scaleV = aUV.getScaleV();
		float offsetU = aUV.getOffsetU();
		float offsetV = aUV.getOffsetV();

		{
			m.setIdentity();
			i.setIdentity();
			m.m02 = -offsetU;
			m.m12 = -offsetV;
			i.rotZ(-angle);
			i.mul(m);

			m.m00 = scaleU;
			m.m11 = scaleV;
			m.m01 = -shear * scaleV;
			m.m02 = 0;
			m.m12 = 0;
			m.mul(i);
			// de.grogra.vecmath.Math2.invertAffine (m, i);
		}
		return m;
	}

	@Override
	public Result generate(ChannelMap inp, MaterialConfiguration cs,
			GLSLChannelMap inpChan, int channel){
		assert (inp instanceof AffineUVTransformation);

		AffineUVTransformation aUV = (AffineUVTransformation) inp;

		Matrix3f m = generateTransMatrix(aUV);

		String resS = generateResultWithChannelDefault(aUV.getInput(), cs,
				inpChan, Channel.U).convert(Result.ET_VEC2);

		String U = "(" + resS + ").s";
		String V = "(" + resS + ").t";

		return new Result("vec2(" + m.m00 + " * " + U + " + " + m.m01 + " * "
				+ V + " + " + m.m02 + ", " + m.m10 + " * " + U + " + " + m.m11
				+ " * " + V + " + " + m.m12 + ")", Result.ET_VEC2);
	}

	@Override
	public Class<AffineUVTransformation> instanceFor() {
		return AffineUVTransformation.class;
	}

}
