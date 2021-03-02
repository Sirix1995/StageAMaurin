package de.grogra.imp3d.glsl.light.shadow;

import javax.media.opengl.GL;
import javax.vecmath.Color3f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import org.sunflow.image.ChromaticitySpectrum;
import org.sunflow.image.ConstantSpectralCurve;
import org.sunflow.image.IrregularSpectralCurve;
import org.sunflow.image.RGBSpace;
import org.sunflow.image.RegularSpectralCurve;
import org.sunflow.image.SpectralCurve;
import org.sunflow.math.MathUtils;

import de.grogra.imp3d.glsl.GLSLDisplay;
import de.grogra.imp3d.glsl.light.LightPos;
import de.grogra.imp3d.glsl.light.SunSkyToDirectionalLightWrapper;
import de.grogra.imp3d.glsl.utility.GLSLShader;
import de.grogra.imp3d.glsl.utility.ShaderConfiguration;
import de.grogra.imp3d.objects.DirectionalLight;
import de.grogra.imp3d.shading.SunSkyLight;
import de.grogra.math.RGBColor;
import de.grogra.ray.physics.Spectrum3f;
import de.grogra.vecmath.Math2;
import de.grogra.vecmath.Matrix34d;

/**
 * Implementation of the directional light type.
 * 
 * @author Konni Hartmann
 */
public class GLSLSkyLightShadow extends GLSLDirectionalLightShadow {


	@Override
	public Class instanceFor() {
		return SunSkyLight.class;
	}
	

	SunSkyToDirectionalLightWrapper mock = new SunSkyToDirectionalLightWrapper();
	LightPos mockPos = new LightPos(mock);

	SunSkyMock m = new SunSkyMock(); 

	protected void setupDynamicUniforms(GL gl, GLSLDisplay disp, Object data, int shaderNo) {

		LightPos lPos = null;
		assert (data instanceof LightPos);
		lPos = (LightPos) data;
		
		assert (lPos.getLight() instanceof SunSkyLight);
		SunSkyLight light = (SunSkyLight) lPos.getLight();

		m.dir.normalize(light.getSun());
		m.turb = light.getTurbidity();
		
		mockPos.setLightTransform(lPos.getLightTransform());
		mockPos.setLightDir(lPos.getLightDir());
//		mock.setPowerDensity(282.649093259050079f);
//		mock.setPowerDensity(Math2.M_2PI);
//		mock.setPowerDensity(6*light.getRadianceFactor()*(0.5f*Math2.M_1_2PI)*disp.getCurrentGLState().getBgPowerDensity());
//		mock.setPowerDensity(light.getRadianceFactor()*disp.getCurrentGLState().getBgPowerDensity());

		
		if((m.getSunColor() == null) || (disp.getCurrentGLState().hasGraphChanged())) {
			m.initSunColor(light);
		}

		Spectrum3f spectrum = new Spectrum3f();
		spectrum.set((Tuple3f)m.getSunColor());
		spectrum.mul ((Tuple3f) m.invIrradiance);
		spectrum.scale (m.getSunsolidangle() / m.sunFraction);
		
//		System.err.println(m.invIrradiance);
//		System.err.println(m.getSunsolidangle());
//		System.err.println(m.sunFraction);
//		System.err.println(spectrum.integrate());	
		
		mock.setPowerDensity( (float) (32*spectrum.integrate()));
//		mock.setPowerDensity((float) (1));
		
		Vector3f color = new Vector3f(m.getSunColor());
		mock.setCurcol(color);

		super.setupDynamicUniforms(gl, disp, mockPos, shaderNo);	
	};
	
	@Override
	public GLSLShader getInstance() {
		return new GLSLSkyLightShadow();
	}

//	@Override
//	public GLSLLightShader getInstance(LightShaderConfiguration lsc) {
//		GLSLLightShader result = new GLSLSkyLight();
//		result.setLightShaderConfiguration(lsc);
//		return result;
//	}
}