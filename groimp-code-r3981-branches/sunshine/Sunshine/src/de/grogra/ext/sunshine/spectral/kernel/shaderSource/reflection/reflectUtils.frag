/*
 * Here you can choose the distribution you want to use with Cook-Torrance
 *
 * 0:	beckmann
 */
#define DISTRIBUTION 0

#define AIR_IOR 1

/*
 * This procedure swap two indices of refraction.
 */
void swapIOR(inout float ni, inout float nt)
{
	float nx= ni;

	ni		= nt;
	nt		= nx;
}

void ConcentricSampleDisk(float u1, float u2,
		inout float dx, inout float dy)
{
	float r, theta;
	// Map uniform random numbers to $[-1,1]^2$
	float sx = 2 * u1 - 1;
	float sy = 2 * u2 - 1;
	// Map square to $(r,\theta)$
	// Handle degeneracy at the origin
	if (sx == 0.0 && sy == 0.0) {
		dx = 0.0;
		dy = 0.0;
	} else {

		if (sx >= -sy) {
			if (sx > sy) {
				// Handle first region of disk
				r = sx;
				if (sy > 0.0)
					theta = sy/r;
				else
					theta = 8.0f + sy/r;
			}
			else {
				// Handle second region of disk
				r = sy;
				theta = 2.0f - sx/r;
			}
		}
		else {
			if (sx <= sy) {
				// Handle third region of disk
				r = -sx;
				theta = 4.0f - sy/r;
			}
			else {
				// Handle fourth region of disk
				r = -sy;
				theta = 6.0f + sx/r;
			}
		}
		theta *= PI / 4.f;

		dx = r*cos(theta);
		dy = r*sin(theta);
	}
}

vec3 CosineSampleHemisphere(float u1, float u2)
{
	float x = 0.0;
	float y = 0.0;
	float z = 0.0;

	ConcentricSampleDisk(u1, u2, x, y);

	z = sqrt(max(0.f, 1.f - x * x - y * y));

	return vec3(x, y, z);
}

vec3 SphericalDirection(float sintheta,
                              float costheta, float phi) {
	return vec3(sintheta * cos(phi),
	              sintheta * sin(phi),
				  costheta);
}

bool SameHemisphere(vec3 w, vec3 wp)
{
	return w.z * wp.z > 0.0;
}

float CosTheta(vec3 w)
{
	return w.z;
}

float SinTheta(vec3 w)
{
	return sqrt(max(0.f, 1.f - w.z*w.z));
}

float SinTheta2(vec3 w)
{
	return 1.f - CosTheta(w)*CosTheta(w);
}

float PowerHeuristic(int nf, float fPdf, int ng, float gPdf) {
	float f = nf * fPdf;
	float g = ng * gPdf;
	return (f*f) / (f*f + g*g);
}

/**
 * Geometric self-shadowing and masking term.
 */
float G(vec3 wo, vec3 wi, vec3 wh)
{
	float NdotWh 	= abs(CosTheta(wh));
	float NdotWo 	= abs(CosTheta(wo));
	float NdotWi 	= abs(CosTheta(wi));
	float WOdotWh 	= abs(dot(wo, wh));

	return min(1.f, min((2.f * NdotWh * NdotWo / WOdotWh), (2.f * NdotWh * NdotWi / WOdotWh)));
}
