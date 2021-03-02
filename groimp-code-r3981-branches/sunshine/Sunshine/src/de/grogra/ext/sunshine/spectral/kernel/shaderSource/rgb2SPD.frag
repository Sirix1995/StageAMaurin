/**
 * Converts a given RGB triple to intensity depending on the
 * actual lambda (globally defined). For more information
 * read Smits paper "An RGB to Spectra Conversion for Reflectance".
 */
float getIntensity(vec3 rgb, sampler2DRect baseSpectra)
{
	float intenity		= 0.0;

	/* 32; 380; 720; */

	vec4 wcmy			= texture2DRect(baseSpectra, vec2(0, colorRGB2SPDRow));
	vec4 rgb0			= texture2DRect(baseSpectra, vec2(1, colorRGB2SPDRow));

	float whiteSpec		= wcmy.x;
	float cyanSpec		= wcmy.y;
	float magentaSpec	= wcmy.z;
	float yellowSpec	= wcmy.w;
	float redSpec		= rgb0.x;
	float greenSpec		= rgb0.y;
	float blueSpec		= rgb0.z;

	float r				= rgb.x;
	float g				= rgb.y;
	float b				= rgb.z;

	if (r <= g && r <= b)
	{
		intenity = r * whiteSpec;

		if (g <= b)
		{
			intenity += (g - r) * cyanSpec;
			intenity += (b - g) * blueSpec;
		}
		else
		{
			intenity += (b - r) * cyanSpec;
			intenity += (g - b) * greenSpec;
		}
	}
	else if (g <= r && g <= b)
	{
		intenity = g * whiteSpec;

		if (r <= b)
		{
			intenity += (r - g) * magentaSpec;
			intenity += (b - r) * blueSpec;
		}
		else
		{
			intenity += (b - g) * magentaSpec;
			intenity += (r - b) * redSpec;
		}
	}
	else // blue <= red && blue <= green
	{
		intenity = b * whiteSpec;

		if (r <= g)
		{
			intenity += (r - b) * yellowSpec;
			intenity += (g - r) * greenSpec;
		}
		else
		{
			intenity += (g - b) * yellowSpec;
			intenity += (r - g) * redSpec;
		}
	}

	return intenity;
}

/**
 * This function delegates the conversion of an RGB triple to
 * a certain intensity for reflective colors.
 */
float getReflectanceIntensity(vec3 rgb)
{
	return getIntensity(rgb, colorRGB2SPDRefTex);
}

/**
 * This function delegates the conversion of an RGB triple to
 * a certain intensity for emissive colors.
 */
float getIlluminationIntensity(vec3 rgb)
{
	return getIntensity(rgb, colorRGB2SPDIlluTex);
}
