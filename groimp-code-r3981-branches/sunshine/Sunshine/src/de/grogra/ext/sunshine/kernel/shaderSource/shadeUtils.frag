vec3 shade(TraceData td, vec3 spectrum, Light light, float lightDecay, float area)
{
	vec3 color 			= black;
	vec3 normal 		= td.normal;

	float AREA_LIGHT 	= 2.0;

	float phong;// 		= -1.0;

	//check used shading
	vec4 c 				= getObjectColor(td.id);

	phong 				= c.x == -1.0 ? c.y : -1.0;

	vec3 skyIntensity 	= vec3(0.0);

	//getSkyRGB (-normalize(lightOrigin.xyz - td.iPoint.xyz), skyIntensity);

	//Light light 		= Light(lightOrigin, vec4(skyIntensity, 1.0), 0.0f, 0.0f, 0.0f, 0.0f, 0, -1, 1.0f, 1.0f);

	vec4 pointToLight 	= light.origin - td.iPoint;
	float angle 		= calculateAngle( pointToLight.xyz, normal );

	angle 				= abs(angle);

	float lightPDF		= areaLightPDF(light.origin, td.iPoint, normalize(light.origin.xyz - td.iPoint.xyz), area, lightPass*sizes[LIGHT]);

	if(lightPDF > 0.1)
	{
		color 			= spectrum*calcDirectIllumination(light, pointToLight.xyz, td,
									angle, normal, phong, 0, 1.0 );

		color		   *= lightDecay  * angle;		
		
		color 		   /= (lightPDF );
	}
	return color;
} //shade
