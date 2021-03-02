/**
 * Beckman distribution of the microfacets.
 */
float beckmann_Dold(float m, float beta)
{
	float left 	= 1.0 / (m*m * pow(abs(cos(beta)),4.0));

	float exp 	= tan(beta) / m;
	float right	= pow(EULER, -1.0*(exp*exp));

	return left * right;
}

float beckmann_D(vec3 wh, float rms)
{
	float costhetah = CosTheta(wh);
	float theta 	= 0.0;

	// Make sure that costheta is lower then 1.0 to avoid an undefined
	// result during acos() calculation.
	if(abs(costhetah) <= 1.0 - EPSILON)
		theta 		= acos(costhetah);

	float tanthetah = tan(theta);

	float dfac 		= tanthetah / rms;

	float result	= exp(-(dfac * dfac)) / (rms * rms * pow(abs(costhetah), 4.0));

	return result;
}


float beckmann_pdf(vec3 wo, vec3 wi, float rms)
{
	vec3 wh 				= normalize(wo + wi);
	float conversion_factor = 1.0 / 4.0 * dot(wo, wh);
	float beckmann_pdf 		= conversion_factor * beckmann_D(wh, rms);

	return beckmann_pdf;
}

float beckmann_pdf(vec3 wh, float cos, float rms)
{
	float conversion_factor = 1.0 / 4.0 * cos;
	float beckmann_pdf 		= conversion_factor * beckmann_D(wh, rms);

	return beckmann_pdf;
}

/**
 * Computes a Halfway vector according to a roughness value r and
 * checks, whether the Halfway vector is on the same side as
 * the incoming ray.
 */
vec3 beckmann_getHalfwayVec(float r, vec3 wo)
{
	vec2 u			= rand2D();

	float u1 		= u.x;
	float u2 		= u.y;

	float theta 	= atan (sqrt (-(r * r) * log(1.0 - u1)));
	float costheta 	= cos (theta);
	float sintheta 	= sqrt(max(0.f, 1.f - costheta*costheta));
	float phi 		= u2 * 2.f * PI;

	//sintheta, costheta, phi
	vec3 H 			= vec3(	sintheta * cos(phi),
							sintheta * sin(phi),
							costheta
							);

	if (!SameHemisphere(wo, H))
	    H.z *= -1.f;

	return H;
}
