package de.grogra.ext.sunshine.spectral;

import org.sunflow.image.SpectralCurve;
import org.sunflow.image.XYZColor;

public class SunshineRegularSpectralCurve extends SpectralCurve {

	private final float[] spectrum;
	private final float lambdaMin, lambdaMax;
	private final float delta, invDelta;
	private boolean isNormalized = false;
	
	public SunshineRegularSpectralCurve(float[] spectrum, float lambdaMin, float lambdaMax) {
		this.lambdaMin = lambdaMin;
		this.lambdaMax = lambdaMax;
		this.spectrum = spectrum;
		delta = (lambdaMax - lambdaMin) / (spectrum.length - 1);
		invDelta = 1 / delta;
	}
	
	public float sample(float lambda) {
		// reject wavelengths outside the valid range
		if (lambda < lambdaMin || lambda > lambdaMax)
			return 0;
		if(spectrum.length == 1)
			return spectrum[0];
		
		// interpolate the two closest samples linearly
		float x = (lambda - lambdaMin) * invDelta;
		int b0 = (int) x;
		int b1 = Math.min(b0 + 1, spectrum.length - 1);
		float dx = x - b0;
		return (1 - dx) * spectrum[b0] + dx * spectrum[b1];
	}
	
	
	/**
	 * To display a spectral curve, we need to determine a standard illuminant. This is
	 * necessary to define the white point. By default this is defined in 
	 * {@link de.grogra.ext.sunhsine.spectral.SpectralColors.DISPLAY_WHITE_POINT_BBTEMP}
	 * 
	 * @return		Integrated spectrum according to a pre-defined standard illuminant.
	 */
	public XYZColor toXYZBB() 
	{
		float X = 0, Y = 0, Z = 0;
        for (int i = 0, w = SPDConversion.LAMBDA_MIN; i < SPDConversion.cie_xbar.length; i++, w ++ ) 
        {            
        	float s 	= sample(w);
        	float bb 	= SpectralColors.bb_spectrum(w);
            
            X 			+= s * bb * SPDConversion.getXMatch(w);
            Y 			+= s * bb * SPDConversion.getYMatch(w);
            Z 			+= s * bb * SPDConversion.getZMatch(w);
        }
        
        return new XYZColor(X, Y, Z);
	}
	
	/**
	 * Normalizes the spectrum with maximum value of all amplitudes.
	 */
	public void normalize()
	{
		if(isNormalized)
			return;
		
		float maxAmpl = 0;
		
		for (int i = 0; i < spectrum.length; i++ ) 
			maxAmpl = Math.max(spectrum[i], maxAmpl);
		
		maxAmpl = 1.0f / maxAmpl;
		
		for (int i = 0; i < spectrum.length; i++ ) 
			spectrum[i] *= maxAmpl;
		
		isNormalized = true;
	}
	
	
}