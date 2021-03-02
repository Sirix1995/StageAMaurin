

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

	int id				= !(loopStart > 0) ? -1 	: int(tmpTrace0.w);
	int type			= !(loopStart > 0) ? -1 	: int(tmpTrace1.w);
	shortestDistance	= !(loopStart > 0) ? -1.0 	: tmpTrace2.x;
	float area			= tmpTrace2.y;
	float lightDecay 	= tmpTrace3.w;

	// From texture set "outputImage"
	vec4 origin			= vec4(tmpOutputImg1.xyz, 1.0);
	vec3 direction		= tmpOutputImg2.xyz;
	float intensity		= tmpOutputImg3.x;
	int lightID			= int(tmpOutputImg3.y);
	bool specIsBlack	= !(intensity > 0.0);

	random2D			= tmpOutputImg3.zw;

	origin				= vec4(epsilonEnvironment(origin.xyz, direction), 1.0);

	Ray ray				= Ray(origin, direction, typeOfRay, vec3(intensity));

	TraceData td		= TraceData
							(
									id > -1,
									vec4(iPoint, 1.0),
									normal,
									id,
									type,
									false
							);
	if(!specIsBlack)
	{
		intersection( ray, td );

		iPoint				= td.iPoint.xyz;
		normal				= td.normal;

		id					= (td.type == TRI) ? int(meshPart) : td.id;
		type				= td.type;
		lightDecay			= 1.0;

		// Preparation for the upcoming shadow-test
		if(lastCycle > 0) // aka == 1
		{
			normal 			= normalLoc2Glo(normal, id);

			Light light 	= getLight(int(lightID * sizes[LIGHT]));
			lightOrigin 	= light.origin.xyz;

			if(light.type == AREA_LIGHT)
			{
				lightOrigin = randomOrigin( float(lightID), area ).xyz;
				light.origin = vec4(lightOrigin, 1.0);
			}

			if(id != (light.isAlsoLight * sizes[PARA]) + paraPart)
				lightDecay	= emittedPower(vec4(iPoint, 1.0), normal, lightID * sizes[LIGHT], light);


		}
	}
	else
		id = -1;

	gl_FragData[0]		= vec4(iPoint, 				id							);		// Global intersection point 		/ Object id
	gl_FragData[1]		= vec4(normal, 				type						);		// Global global normal vector 		/ Object type
	gl_FragData[2]		= vec4(shortestDistance,	area,			random2D	);		// Distance from origin to iPoint 	/ Actual used random number
	gl_FragData[3]		= vec4(lightOrigin,		 	lightDecay					);		// Optional for area lights	origin	/ Is iPoint lighted by actual light source

}
