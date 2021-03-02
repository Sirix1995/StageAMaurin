float blinn_D(vec3 wh, float exponent)
{
	float costhetah = abs(CosTheta(wh));

	return (exponent+2) *
			       _PI * 0.5 *
			pow(max(0.f, costhetah), exponent);
}


float blinn_pdf(vec3 wo, vec3 wi, float exponent)
{
	vec3 H = normalize(wo + wi);
	float costheta = abs(H.z);
	// Compute PDF for \wi from Blinn distribution
	float blinn_pdf = ((exponent + 1.f) *
		                   pow(costheta, exponent)) /
							   (2.f * PI * 4.f * dot(wo, H));

	return blinn_pdf;
}


/**
 * Computes a Halfway vector according to a roughness value r and
 * checks, whether the Halfway vector is on the same side as
 * the incoming ray.
 */
vec3 blinn_getHalfwayVec(float exponent, vec3 wo)
{
	vec2 rnd = rand2D();
	// Compute sampled half-angle vector $\wh$ for Blinn distribution
	float costheta = pow(rnd.x, 1.f / (exponent+1));
	float sintheta = sqrt(max(0.f, 1.f - costheta*costheta));
	float phi = rnd.y * 2.f * PI;

	vec3 H = SphericalDirection(sintheta, costheta, phi);

	if (dot(wo, H) < 0.f) H = -H;

	return H;
}
