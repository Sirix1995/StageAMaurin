

void main(void)
{
	vec2 xy				= gl_TexCoord[0].xy;

	vec4 tmpTrace0		= texture2DRect(a0, xy);
	vec4 tmpTrace1		= texture2DRect(a1, xy);
	vec4 tmpTrace2		= texture2DRect(a2, xy);
	vec4 tmpTrace3		= texture2DRect(a3, xy);

	vec4 tmpOutputImg1	= texture2DRect(outputImage1, xy);
	vec4 tmpOutputImg2	= texture2DRect(outputImage2, xy);
	vec4 tmpOutputImg3	= texture2DRect(outputImage3, xy);

	init();

	// From texture set "a"
	vec3 iPoint			= vec3(tmpTrace0.xyz);
	vec3 normal			= vec3(tmpTrace1.xyz);
	vec3 lightOrigin	= vec3(tmpTrace3.xyz);

	int id				= int(tmpTrace0.w);
	int type			= int(tmpTrace1.w);

	shortestDistance	= !(loopStart > 0) ? -1.0 : tmpTrace2.x;

	float lightPDF		= tmpTrace2.y;
	float lightDecay 	= tmpTrace3.w;

	// From texture set "outputImage"
	vec4 origin			= vec4(tmpOutputImg1.xyz, 1.0);
	vec3 direction		= tmpOutputImg2.xyz;

	random2D			= tmpTrace2.zw;

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
	gl_FragData[2]		= vec4(shortestDistance,	lightPDF,		random2D	);		// Distance from origin to iPoint 	/ Actual used random number
	gl_FragData[3]		= vec4(lightOrigin,		 	lightDecay					);		// Optional for area lights	origin	/ Is iPoint lighted by actual light source

}
