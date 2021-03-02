
/**
 * This method calculates the BRDF based on the Cook-Torrance Illumination
 * model. This is done by assuming of a microfacet distribution. Each of
 * these facets is ideal specular reflecting mirror. Only by appling the
 * roughness (parameter 'n' in method D) the overall reflection behavior
 * can be controlled.
 * The diffuse part is Lambertian
 */
float computeBSDF_Microfacet(vec3 wo, vec3 wi, Material mat, int which)
{
	float diffuse	= mat.v1;
	float specular	= mat.v2;
	float rms		= mat.urough;
	float ior		= mat.ior;
	float cb		= mat.cauchy;

	float ni		= AIR_IOR;
	float no		= ior;

	if(cb != 0.0)
	{
		// Handle dispersion using cauchy formula
		no += (cb * 1E6) / (mat.lambda * mat.lambda);
	}

	if(wo.z < 0.0)	// leaving the medium with higher ior
		swapIOR(ni, no);

	float f			= 0.0;
	float n			= ni / no;

	/* The reflection term */

	// Determine the h_r vector for both side of the surface
	if(which == T_BXDF_REFLECTION)
	{
		vec3 whr 		= normalize(wi + wo);

		float cosThetaHr= dot(wi, whr);

		float cosThetaO = abs(CosTheta(wo));
		float cosThetaI = abs(CosTheta(wi));

		float D			= beckmann_D(whr, rms);
		float G			= G(wo, wi, whr);
		float F 		= fresnel_slick(cosThetaHr, n);

		f 				= diffuse * D * G * F / (4.f * cosThetaI * cosThetaO) + 0.05;

	} else  {	/* The transmission term */
//	if(which == T_BXDF_TRANSMISSION)
	//{
		vec3 wht		= normalize( -(ni * wi + no * wo) );

		float dotWhI	= dot(wi, wht);
		float dotWhO	= dot(wo, wht);

		float dotWhIAbs	= abs(dotWhI);
		float dotWhOAbs	= abs(dotWhO);

		float dotIN		= abs(dot(wi, vec3(0,0,1)));
		float dotON		= abs(dot(wo, vec3(0,0,1)));

		float D			= beckmann_D(wht, rms);
		float G			= G(wo, wi, wht);
		float F 		= fresnel_slick(dotWhI, n);

		float factor	= ni * dotWhI + no * dotWhO;

		f				= ((dotWhIAbs * dotWhOAbs) / (dotIN * dotON)) * (((no * (1.0 - F) * G * D)) / ( factor * factor ));
	}
	return f;

} //computeBSDF

vec3 computeDir_Microfacet(vec3 wo, float rms, Material mat, inout float pdf)
{
	float ior		= mat.ior;
	float cb		= mat.cauchy;

	normalTest 		= vec4(random2D, 88, 99);
	vec2 uv			= rand2D();

	float u 		= uv.x;
	float v 		= uv.y;
	float z 		= rand();

	bool entering 	= CosTheta(wo) > 0.f;
	vec3 wi			= vec3(0.0);

	vec3 wh			= beckmann_getHalfwayVec(rms, wo);
	wh				= normalize(wh);

	float p			= beckmann_D(wh, rms) * abs(dot(wh, vec3(0,0,1)));

	float ni	= 1.0;
	float nt	= ior;

	if(cb != 0.0)
	{
		// Handle dispersion using cauchy formula
		nt += (cb * 1E6) / (mat.lambda * mat.lambda);
	}

	if(!entering)
		swapIOR(ni, nt);

	float n			= ni / nt;

	float F			= fresnel_slick(dot(wo, vec3(0,0,1)), ior);

	if(z > (abs(F) * abs(dot(wo, vec3(0,0,1)))))
	{
		wi			= 2.f * dot(wo, wh) * wh - wo;

	} else if(z > 0.0) {

		float c		= dot(wo, wh);
		float sign	= sign(dot(wo, vec3(0,0,1)));

		//wi			= ((n * c - sign * sqrt(1.0 + n * ((c * c) - 1.0))) * wh - n * (wo));

		wi = refract(-wo, wh, n);

	} else {
		wi 			= CosineSampleHemisphere(u, v);

		if( wo.z < 0.0 )
			wi.z *= -1.0;
	}

	wi				= normalize(wi);
	pdf = 0.0;
/*
	if(SameHemisphere(wo, wi))
	{
		pdf			= abs(wi.z) * _PI;

		//#if DISTRIBUTION == 0

		pdf		   += beckmann_pdf(wo, wi, rms);

		//#endif

	}
*/
	//vec3 wiW		= localToGlobalVec(normal, sn, tn, wi);

	pdf = (abs(dot(wo, wh)) * G(wo, wi, wh)) / (abs(dot(wo, vec3(0,0,1))) * abs(dot(wh, vec3(0,0,1))));


	return wi;
}

float computePDF_Microfacet(vec3 wo, vec3 wi, Material mat)
{
	float rms 	= mat.urough;
	float pdf	= 0.0;

	pdf		   	= abs(wi.z) * _PI;

//#if DISTRIBUTION == 0
	pdf		   += beckmann_pdf(wo, wi, rms);
//#endif

	pdf		   *= 0.5;

	return pdf;
}



