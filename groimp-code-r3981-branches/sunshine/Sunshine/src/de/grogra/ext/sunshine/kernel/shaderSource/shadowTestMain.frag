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

void main(void)
{
	vec2 xy				= gl_TexCoord[0].xy;

	vec4 tmpTrace0		= texture2DRect(a0, xy);
	vec4 tmpTrace1		= texture2DRect(a1, xy);
	vec4 tmpTrace2		= texture2DRect(a2, xy);
	vec4 tmpTrace3		= texture2DRect(a3, xy);

	vec4 tmpOutputImg1	= texture2DRect(outputImage1, xy);
	vec4 tmpOutputImg2	= texture2DRect(outputImage2, xy);

	init();

	// From texture set "a"
	vec3 iPoint			= vec3(tmpTrace0.xyz);
	vec3 normal			= vec3(tmpTrace1.xyz);
	vec3 lightOrigin	= vec3(tmpTrace3.xyz);

	int id				= int(tmpTrace0.w);
	int type			= int(tmpTrace1.w);

	shortestDistance	= !(loopStart > 0) ? -1.0 : tmpTrace2.x;

	float area			= tmpTrace2.y;
	float lightDecay 	= tmpTrace3.w;

	// From texture set "outputImage"
	vec4 origin			= vec4(tmpOutputImg1.xyz, 1.0);
	vec3 direction		= tmpOutputImg2.xyz;

	random2D			= tmpTrace2.zw;;

	vec4 rayOri			= vec4(iPoint, 1.0);
	vec3 rayDir			= normalize(lightOrigin - iPoint);

	rayOri				= vec4(epsilonEnvironment(rayOri.xyz, rayDir), 1.0);

	Ray ray				= Ray(rayOri, rayDir, typeOfRay, vec3(0.0));

	if(id > -1 && lightDecay > 0.0)
	{
		TraceData td	= TraceData(lightDecay < EPSILON, vec4(0.0), vec3(0.0), -1, -1, false);

		intersection( ray, td );

		float dist1 	= distanceSQR(rayOri.xyz, lightOrigin);
		float dist2 	= distanceSQR(rayOri.xyz, td.iPoint.xyz);

		lightDecay 		= td.hasIntersection && dist1 > dist2 ? 0.0 : lightDecay;
	}

	gl_FragData[0]		= vec4(iPoint, 				id							);		// Global intersection point 		/ Object id
	gl_FragData[1]		= vec4(normal, 				type						);		// Global global normal vector 		/ Object type
	gl_FragData[2]		= vec4(shortestDistance,	area,			random2D	);		// Distance from origin to iPoint 	/ Actual used random number
	gl_FragData[3]		= vec4(lightOrigin,		 	lightDecay					);		// Optional for area lights	origin	/ Is iPoint lighted by actual light source

}
