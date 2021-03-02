float computeBRDF_SpecularTransmission(Material mat)
{
	return 0.0f;
}

vec3 computeDir_SpecularTransmission(Material mat, vec3 wo, inout float f, inout float pdf)
{
	float specular	= mat.v2;
	float ior		= mat.ior;
	float cb		= mat.cauchy;

	vec3 wi 		= vec3(0.0);

	bool entering = CosTheta(wo) > 0.f;
	float ei = 1.0;
	float et = ior;

	if(cb != 0.0)
	{
		// Handle dispersion using cauchy formula
		et += (cb * 1E6) / (mat.lambda * mat.lambda);
	}

	if (!entering)
		swapIOR(ei, et);

	// Compute transmitted ray direction
	float sini2 = SinTheta2(wo);
	float eta = ei / et;
	float eta2 = eta * eta;
	float sint2 = eta2 * sini2;

	// Handle total internal reflection for transmission
	if (sint2 > 1.0f)
	{
		f = 1.0f; 
		return vec3(-wo.x, -wo.y, wo.z);
	}

	float cost = sqrt(max(0.f, 1.f - sint2));
	if (entering)
		cost = -cost;

	float sintOverSini = eta;

	wi = vec3(-eta * wo.x, -eta * wo.y, cost);
	
	pdf = 1.f;
	
	float F_r 	= fresnel_dielectric(cost, 1.0, ior, cb);

	//f = (ei*ei)/(et*et) * (1.0 - F_r) * specular / abs(CosTheta(wi));
	//f = (1.0 - F_r) * specular / (abs(cost) * eta2);
	f = (1.0 - F_r) * specular * ( eta2 / abs(cost));

	return wi;
}

float computePDF_SpecularTransmission(vec3 wo, vec3 wi)
{
	return 0.0;
}
