float fresnel_dielectric_avg(float cosi, float cost, float etai, float etat) {
        float Rparl = ((etat * cosi) - (etai * cost)) /
                         ((etat * cosi) + (etai * cost));
        float Rperp = ((etai * cosi) - (etat * cost)) /
                         ((etai * cosi) + (etat * cost));

	return (Rparl*Rparl + Rperp*Rperp) / 2.f;
}

float fresnel_dielectric(float cosi, float eta_i, float eta_t, float cb) {
	// Compute Fresnel reflectance for dielectric
	cosi = clamp(cosi, -1.f, 1.f);

	// Compute indices of refraction for dielectric
	bool entering 	= cosi > 0.;
	float ei 		= eta_i;
	float et 		= eta_t;
	
	if(cb != 0.0)
	{
		// Handle dispersion using cauchy formula
		et += (cb * 1E6) / (lambda * lambda);
	}

	if (!entering)
		swapIOR(ei, et);
	// Compute _sint_ using Snell's law
	float sint = ei/et * sqrt(max(0.f, 1.f - cosi*cosi));

	// Handle total internal reflection
	float result = 1f;

	if (sint < 1.)
	{
		float cost 	= sqrt(max(0.f, 1.f - sint*sint));
		result 		= fresnel_dielectric_avg(abs(cosi), cost, ei, et);
	}

	return result;
}

float fresnel_dielectric(float cosi, float eta_i, float eta_t) 
{
	return fresnel_dielectric(cosi, eta_i, eta_t, 0.0f);
}

float fresnel_dielectric_complement(float cosi, float eta_i, float eta_t)
{
	return 1.f - fresnel_dielectric(cosi, eta_i, eta_t);
}

