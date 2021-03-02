package de.grogra.ext.sunshine.spectral;

import javax.vecmath.Color3f;

import org.sunflow.image.IrregularSpectralCurve;
import org.sunflow.image.RegularSpectralCurve;

import com.sun.opengl.util.StreamUtil;

import de.grogra.xl.util.FloatList;

public class SpectralColors {	
	
	/**
	 * This is the temperature in Kelvin to determine the white of a
	 * certain spectral curve.
	 */
	public static float DISPLAY_WHITE_POINT_BBTEMP 			= 6500;           
	
	
	/**
	 * Approximation of the black body curve.
	 * 
	 * @param wavelength		In nanometer
	 * @return					Irridiance of an ideal black body radiator.
	 */
	public static float bb_spectrum(float wavelength, float bbTemp)
	{
		float wlm = wavelength * 1e-9f;   /* Wavelength in meters */
		
		return (float) ((3.74183e-16f * Math.pow(wlm, -5.f)) /
					(Math.exp(1.4388e-2f / (wlm * bbTemp)) - 1.f));
	}
	
	public static float bb_spectrum(float wavelength)
	{
		return bb_spectrum(wavelength, DISPLAY_WHITE_POINT_BBTEMP);
	}

	/**
	 * For integrating over a given spectral curve (SPD) we need the scaling factor k.
	 * The factor is calculate in this way: To transform a given SPD into XYZ color space
	 * we have to determine a standard illuminant. This can be done by assign the temperature
	 * (in Kelvin) of the light source. Out of temperature according to the Planck's 
	 * law of black body radiation, a spectral curve will be constructed. This happens by
	 * invoking {@link: #bb_spectrum(float, float)}.
	 * The luminance (Y) of that illuminant curve is integrated in manner of Riemann integration 
	 * and with the use of the y-component from the CIE XYZ color matching functions. 
	 * With the result of that integration we can normalize an integrated reflective spectral curve
	 * by multiplying it with the inverted value of the Y.
	 * 
	 * @param temp			Temperature of the standard illuminant.
	 * @return				k - Inverted result of integration from luminance Y
	 */
	public static float getK(float temp)
	{
		float Y = 0;
		
		for (int i = 0, w = SPDConversion.LAMBDA_MIN; i < SPDConversion.cie_xbar.length; i+=3, w ++ ) 
		{            
			float bb = SpectralColors.bb_spectrum(w, temp);
			
			Y += bb * SPDConversion.getYMatch(w);
		}
		
		return 1.f / Y;
	}
	
	/**
	 * Read a file which holds an SPD for a certain color. It is supposed that all
	 * SPDs are stored in this way:
	 * 				* First row is a comment (useless)
	 * 				* Second row has the value of lambda minimum
	 * 				* All rows below has the format [lambda] [intensity]
	 * 				* [lambda] values are stored in ascending order
	 * 				* The smallest interval is 1nm
	 * 				* Every row has one intensity for one lambda
	 * 
	 * @param str	Name of a certain SPD
	 * 
	 * @return		Irregular spectral curve of SPD
	 */
	public static SunshineRegularSpectralCurve parseString(String str)
	{
		float lambda					= 0f;
		float intens					= 0f;		
		
		// This holds for lambda min till lambda max all intensities in 1nm interval
		FloatList lambdas 				= new FloatList();
		FloatList intensities 			= new FloatList();	
		
		String[] lines, values;

		try {
			
			String source = "";
			
			// Reading the file content from disk
			try {
				
				source = new String(StreamUtil.readAll(SpectralColors.class.getResourceAsStream("colors/" + str + ".sd")));
				
			} catch (Exception e)
			{
				System.err.println(e.getMessage());			
			}		
			
			// Every line becomes a field in temp
			lines			= source.split("\n");
			
			
			// Skip the first line of the file..			
			// .. and obtain the lambda minimum of this SPD (file)
			
			// For all fields in temp..
			for(int i = 2; i < lines.length; i++)
			{	
				// obtain the intensity for a certain lambda
				values		= lines[i].split(" ");
				
				lambda		= Float.parseFloat(values[0]);
				intens 		= Float.parseFloat(values[1]);	
				
				lambdas.add(lambda);
				intensities.add(intens);
			}

		} catch (Exception e) {
			// Something went wrong
			System.err.println("Error2: " + e);
		}
		
		// Creating float arrays with the exact number of elements
		float[] lambdasArray			= lambdas.toArray();
		float[] intensitiesArray		= intensities.toArray();
		
		// With thes two arrays we construct an irregular spectral curve
		IrregularSpectralCurve ir_curve	= new IrregularSpectralCurve(lambdasArray, intensitiesArray);

		// The range information are needed to construct a regular curve
		float lambdaMin					= lambdasArray[0];
		float lambdaMax					= lambda;
	
		// Construct a regular curve and return
		return irreg2RegSpecCurve(ir_curve, lambdaMin, lambdaMax);
	}
	
	/**
	 * This function construct a regular spectral curve {@link #org.sunflow.image.RegularSpectralCurve}
	 * out of an irregular {@link #org.sunflow.image.IrregularSpectralCurve} one. It is supposed that
	 * all lambdas and their corresponding intensities are stored in ascending order.
	 * The regular curve is provided in 1nm interval.
	 * 
	 * @param ir_curve		Irregular curve
	 * @param lambdaMin		Lower limit of the irregular and regular curve
	 * @param lambdaMax		Upper limit of the irregular and regular curve
	 * 
	 * @return				A regular spectral curve with an 1nm-step interval.
	 */
	public static SunshineRegularSpectralCurve irreg2RegSpecCurve(IrregularSpectralCurve ir_curve, float lambdaMin, float lambdaMax)
	{
		int range			= (int) (lambdaMax - lambdaMin);
		
		if(range == 0)
			range++;
		
		int lambda			= (int) lambdaMin;
		float[] spectrum 	= new float[range];
		
		for(int i = 0; i < range; i++)
		{
			spectrum[i]		= ir_curve.sample(lambda++);
		}
		
		return new SunshineRegularSpectralCurve(spectrum, lambdaMin, lambdaMax);
	}
	
	/**
	 * This function converts a given color (RGB) in Spectral Power Distribution namely RegularSpectralCurve.
	 * The algorithm based on Smits paper "An RGB to Spectra Conversion for Reflectance".
	 * 
	 * @param color				The RGB-color which has to be converted
	 * @param lambdaMin			Lower limit of wavelength
	 * @param lambdaMax			Upper limit of wavelength
	 * @param isReflectance		To decide which type of base spectra is used for converion
	 * 
	 * @return					A regular spectral curve with an 1nm-step interval.
	 */
	public static SunshineRegularSpectralCurve fromRGB(Color3f color, int lambdaMin, int lambdaMax, boolean isReflectance)
	{
		float[] spectrum 	= new float[(int) (lambdaMax - lambdaMin)];
		
		float[] whiteSpec, cyanSpec, magentaSpec, yellowSpec, redSpec, greenSpec, blueSpec;
		
		if(isReflectance)
		{
			whiteSpec		= RGB2Spectrum_Ref.WHITE;
			cyanSpec		= RGB2Spectrum_Ref.CYAN;
			magentaSpec		= RGB2Spectrum_Ref.MAGENTA;
			yellowSpec		= RGB2Spectrum_Ref.YELLOW;
			redSpec			= RGB2Spectrum_Ref.RED;
			greenSpec		= RGB2Spectrum_Ref.GREEN;
			blueSpec		= RGB2Spectrum_Ref.BLUE;
			
		} else {
			
			whiteSpec		= RGB2Spectrum_Illum.WHITE;
			cyanSpec		= RGB2Spectrum_Illum.CYAN;
			magentaSpec		= RGB2Spectrum_Illum.MAGENTA;
			yellowSpec		= RGB2Spectrum_Illum.YELLOW;
			redSpec			= RGB2Spectrum_Illum.RED;
			greenSpec		= RGB2Spectrum_Illum.GREEN;
			blueSpec		= RGB2Spectrum_Illum.BLUE;
		}		
		
		float r				= color.x;
		float g				= color.y;
		float b				= color.z;
		
		int i				= 0;
		
		for(int l = lambdaMin; l < lambdaMax; l++)
		{
			if (r <= g && r <= b)
			{
				spectrum[i] = r * getInterploateBin(whiteSpec, l, isReflectance);

				if (g <= b)
				{
					spectrum[i] += (g - r) * getInterploateBin(cyanSpec, l, isReflectance);
					spectrum[i] += (b - g) * getInterploateBin(blueSpec, l, isReflectance);
				}
				else
				{
					spectrum[i] += (b - r) * getInterploateBin(cyanSpec, l, isReflectance);
					spectrum[i] += (g - b) * getInterploateBin(greenSpec, l, isReflectance);
				}
			}
			else if (g <= r && g <= b)
			{
				spectrum[i] = g * getInterploateBin(whiteSpec, l, isReflectance);

				if (r <= b)
				{
					spectrum[i] += (r - g) * getInterploateBin(magentaSpec, l, isReflectance);
					spectrum[i] += (b - r) * getInterploateBin(blueSpec, l, isReflectance);
				}
				else
				{
					spectrum[i] += (b - g) * getInterploateBin(magentaSpec, l, isReflectance);
					spectrum[i] += (r - b) * getInterploateBin(redSpec, l, isReflectance);
				}
			}
			else // blue <= red && blue <= green
			{
				spectrum[i] = b * getInterploateBin(whiteSpec, l, isReflectance);

				if (r <= g)
				{
					spectrum[i] += (r - b) * getInterploateBin(yellowSpec, l, isReflectance);
					spectrum[i] += (g - r) * getInterploateBin(greenSpec, l, isReflectance);
				}
				else
				{
					spectrum[i] += (g - b) * getInterploateBin(yellowSpec, l, isReflectance);
					spectrum[i] += (r - g) * getInterploateBin(redSpec, l, isReflectance);
				}
			}
			
			i++;
		}
		
		return new SunshineRegularSpectralCurve(spectrum, lambdaMin, lambdaMax);
	}
	
	/**
	 * Helper function to retrieve the right value for converting from the base-spectra.
	 * 
	 * @param array				Base spectra.
	 * @param sample			Wavelength
	 * @param isReflectance		Which type of base-spectra
	 * 
	 * @return					Value for converting.
	 */
	private static float getInterploateBin(float[] array, int sample, boolean isReflectance)
	{
		if(isReflectance)
			return RGB2Spectrum_Ref.interpolate(array, sample);
		else
			return RGB2Spectrum_Illum.interpolate(array, sample);
	}
}
