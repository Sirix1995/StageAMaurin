/*
 * Here you can choose the distribution you want to use with Cook-Torrance
 *
 * 0:	beckmann
 */
#define DISTRIBUTION 0

/**
 * This method calculates the BRDF based on the Cook-Torrance Illumination
 * model. This is done by assuming of a microfacet distribution. Each of
 * these facets is ideal specular reflecting mirror. Only by appling the
 * roughness (parameter 'n' in method D) the overall reflection behavior
 * can be controlled.
 * The diffuse part is Lambertian
 */
float computeBRDF_CookTorrance(vec3 wo, vec3 wi, Material mat)
{
	float diffuse	= mat.v1;
	float specular	= mat.v2;
	float exponent	= 1.0 / mat.urough;
	float n			= mat.ior;

	float ret 		= 0.0f; //diffuse * _PI;

	float cosThetaO	= abs(CosTheta(wo));
	float cosThetaI	= abs(CosTheta(wi));

	vec3 wh 		= normalize(wi + wo);

	float cosThetaH = dot(wi, wh);
	float cG 		= G(wo, wi, wh);

	float d			= 0.0;

	//#if DISTRIBUTION == 0

	d				= blinn_D(wh, exponent);

	//#endif
	float F_r		= fresnel_dielectric(cosThetaH, 1.0, n);

	ret 		   += specular * d * cG * F_r / (PI * cosThetaI * cosThetaO);

	return ret;

} //computeBSDF

float computeDir_CookTorrance(vec3 wo, inout vec3 wi, Material mat, inout float pdf)
{

	float diffuse	= mat.v1;
	float specular	= mat.v2;
	float exponent	= 1.0 / mat.urough;
	float n			= mat.ior;

	vec2 uv			= rand2D();

	float u 		= uv.x;
	float v 		= uv.y;
	float z 		= 1.0; //rand();

	vec3 wh			= vec3(0.0);
	/*if(z >= rms)
	{
	*/	wh			= blinn_getHalfwayVec(exponent, wo);

		wi			= -wo + 2.f * dot(wo, wh) * wh;
		//wi.z *= -1;
	//	wi			= normalize(wi);

	/*} else {
		wi 			= CosineSampleHemisphere(u, v);

		if( wo.z < 0.0 )
			wi.z *= -1.0;
	}*/

	pdf = 0.0;

	if(SameHemisphere(wo, wi))
	{
		vec3 H = normalize(wo + wi);
		float costheta = abs(H.z);
		// Compute PDF for \wi from Blinn distribution

		pdf = ((exponent + 1.f) *
			                   pow(costheta, exponent)) /
				(2.f * PI * 4.f * dot(wo, H));

	}

	float ret 		= 0.0f; //diffuse * _PI;

	wh 				= normalize(wi + wo);

	float cosThetaO	= abs(CosTheta(wo));
	float cosThetaI	= abs(CosTheta(wi));

	float cosThetaH = dot(wi, wh);
	float cG 		= G(wo, wi, wh);

	//#if DISTRIBUTION == 0

	float d			= blinn_D(wh, exponent);

	float F_r		= 1.0;
	F_r				= fresnel_dielectric(cosThetaH, 1.0, n);

	ret 		   	= specular * d * cG  * F_r  / (4 * cosThetaI * cosThetaO);

	return ret;
}

float computePDF_CookTorrance(vec3 wo, vec3 wi, Material mat)
{
	float pdf	= 1.0;

	if(mat.urough > 0)
	{
		//pdf		   	= abs(wi.z) * _PI;

	//#if DISTRIBUTION == 0
		pdf		   = blinn_pdf(wo, wi, 1.0 / mat.urough);
	//#endif

		//pdf		   *= 0.5;
	}

	return pdf;
}



