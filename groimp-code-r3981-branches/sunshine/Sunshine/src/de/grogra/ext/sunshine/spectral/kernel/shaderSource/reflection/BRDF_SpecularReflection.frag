float computeBRDF_SpecularReflection(Material mat)
{
	return 0.0f;
}

vec3 computeDir_SpecularReflection(Material mat, vec3 wo, inout float f, inout float pdf, float F_r)
{
	float specular	= mat.v1;
	float ior		= mat.ior;

	vec3 wi 		= wo;

	wi.x *= -1.0;
	wi.y *= -1.0;

	pdf				= 1;

//	float F_rs		= fresnel_dielectric(CosTheta(wo), 1.0, ior);

	f =  F_r * specular / abs(CosTheta(wi));

	return normalize(wi);
}

float computePDF_SpecularReflection(vec3 wo, vec3 wi)
{
	return 0.0;
}
