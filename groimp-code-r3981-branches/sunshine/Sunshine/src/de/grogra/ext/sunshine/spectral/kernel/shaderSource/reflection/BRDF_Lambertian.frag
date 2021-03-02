float computeBRDF_Lambertian(Material mat)
{
	return mat.v1 * _PI;
}

vec3 computeDir_Lambertian(vec3 wo, inout float pdf)
{
	vec2 uv			= rand2D();

	float u 		= uv.x;
	float v 		= uv.y;

	vec3 wi 		= CosineSampleHemisphere(u, v);

	if( wo.z < 0.0 )
		wi.z *= -1.0;

	//wi = normalize(wi);

	pdf				= SameHemisphere(wo, wi) ? abs(wi.z) * _PI : 0.0;

	return wi;
}

float computePDF_Lambertian(vec3 wo, vec3 wi)
{
	return SameHemisphere(wo, wi) ? abs(wi.z) * _PI : 0.0;
}
