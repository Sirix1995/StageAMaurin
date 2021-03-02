#ifdef USE_SUNSKY
// Conversion matrix from XYZ in SRGB
vec3 r_con = vec3(3.241, -0.9692, 0.0556);
vec3 g_con = vec3(-1.5374, 1.8759, -0.204);
vec3 b_con = vec3(-0.4986, 0.0416, 1.0571);

float perezFunction (vec4 lam, float lam4, float cosTheta,
			float gamma, float cosGamma, float lvz)
{
	float den = ((1.0 + lam.x * exp (lam.y)) * (1.0 + lam.z
		* exp (lam.w * sunTheta) + lam4 * cosSunTheta
		* cosSunTheta));
	float num = ((1.0 + lam.x * exp (lam.y / cosTheta)) * (1.0
		+ lam.z * exp (lam.w * gamma) + lam4 * cosGamma
		* cosGamma));
	return lvz * num / den;
}

void getChrom(float x, float y, inout vec3 color) {
	float M1 = (-1.3515f - 1.7703f * x + 5.9114f * y) / (0.0241f + 0.2562f * x - 0.7341f * y);
	float M2 = (0.03f - 31.4424f * x + 30.0717f * y) / (0.0241f + 0.2562f * x - 0.7341f * y);
	color.x = S0xyz.x + M1 * S1xyz.x + M2 * S2xyz.x;
	color.y = S0xyz.y + M1 * S1xyz.y + M2 * S2xyz.y;
	color.z = S0xyz.z + M1 * S1xyz.z + M2 * S2xyz.z;
}

void convertXYZtoRGB(float X, float Y, float Z, inout vec3 color) {
	float r = (r_con.x * X) + (g_con.x * Y) + (b_con.x * Z);
	float g = (r_con.y * X) + (g_con.y * Y) + (b_con.y * Z);
	float b = (r_con.z * X) + (g_con.z * Y) + (b_con.z * Z);
	color = vec3(r, g, b);
}

void constrainSkyRGB (inout vec3 c)
{
	float m = -min(c.x, min(c.y, c.z));
	if (m > 0)
		c += m;

}

bool getSkyRGB (vec3 dir, inout vec3 color)
{
	if (dir.z < 0)
	{
		color = vec3(0, 0, 0);
		return false;
	}
	if (dir.z < 0.001f)
		dir.z = 0.001f;

	dir  = normalize(dir);

	float cosTheta = clamp(dir.z, -1, 1);
	float cosGamma = clamp(dot(dir, sunDir), -1, 1);
	float gamma = acos(cosGamma);

	float x = perezFunction(perezx, perezx4, cosTheta, gamma, cosGamma, zenithx);
	float y = perezFunction(perezy, perezy4, cosTheta, gamma, cosGamma, zenithy);
	float Y = perezFunction(perezY, perezY4, cosTheta, gamma, cosGamma, zenithY);
	getChrom(x, y, color);

	// 683.002: conversion from cd/m² to W/m²sr
	color *=  Y / (color.y * 683.002f);

	convertXYZtoRGB (color.x, color.y, color.z, color);
	constrainSkyRGB(color);

	//color *= 0.015;
	//return cosGamma >= cosHalfSunAngle;
	//color = vec3(1.0, 0.0, 0.0);

	return true;
}

#endif
