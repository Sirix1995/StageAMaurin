
#ifdef USE_SUNSKY

uniform float S0;
uniform float S1;
uniform float S2;

float perezFunction (vec4 lam, float lam4, float cosTheta,
			float gamma, float cosGamma, float lvz)
{
	float den = ((1.0 + lam[0] * exp (lam[1])) * (1.0 + lam[2]
		* exp (lam[3] * sunTheta) + lam4 * cosSunTheta
		* cosSunTheta));
	float num = ((1.0 + lam[0] * exp (lam[1] / cosTheta)) * (1.0
		+ lam[2] * exp (lam[3] * gamma) + lam4 * cosGamma
		* cosGamma));
	return lvz * num / den;
}

float getIntensity(float x, float y) {
	float M1 	= (-1.3515f - 1.7703f * x + 5.9114f * y) / (0.0241f + 0.2562f * x - 0.7341f * y);
	float M2 	= (0.03f - 31.4424f * x + 30.0717f * y) / (0.0241f + 0.2562f * x - 0.7341f * y);

	return S0 + M1*S1 + M2*S2;
}

void constrainSkyRGB (inout vec3 c)
{
	float m = -min(c.x, min(c.y, c.z));
	if (m > 0)
		c += m;

}

bool getSkyIntensity (vec3 dir, inout float intensity)
{
	if (dir.z < 0)
	{
		intensity = 0.0;
		return false;
	}
	if (dir.z < 0.001f)
		dir.z = 0.001f;

	dir  			= normalize(dir);

	float cosTheta 	= clamp(dir.z, -1, 1);
	float cosGamma 	= clamp(dot(dir, sunDir), -1, 1);
	float gamma 	= acos(cosGamma);

	float x_perez 	= perezFunction(perezx, perezx4, cosTheta, gamma, cosGamma, zenithx);
	float y_perez 	= perezFunction(perezy, perezy4, cosTheta, gamma, cosGamma, zenithy);
	float Y_perez 	= perezFunction(perezY, perezY4, cosTheta, gamma, cosGamma, zenithY);

	intensity 		= getIntensity(x_perez, y_perez) ;

	// 683.002: conversion from cd/m² to W/m²sr
	intensity 	   *= (Y_perez / 683.002f);

	return true;
}

#else

bool getSkyIntensity (vec3 dir, inout float intensity)
{
	return true;
}

#endif


