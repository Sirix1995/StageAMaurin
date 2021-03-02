bool isValid(float value)
{
	return value == value && value < INFINITY && value > -INFINITY;
}

bool isValid(vec3 value)
{
	return isValid(value.x) && isValid(value.y) && isValid(value.z);
}

void main(void)
{
	vec2 xy				= gl_TexCoord[0].xy;

	vec4 tmpOI0			= texture2DRect(outputImage0, xy);
	vec4 tmpOI1			= texture2DRect(outputImage1, xy);
	vec4 tmpOI2			= texture2DRect(outputImage2, xy);
	vec4 tmpOI3			= texture2DRect(outputImage3, xy);

	vec4 tmp0			= texture2DRect(a0, xy);
	vec4 tmp1			= texture2DRect(a1, xy);
	vec4 tmp2			= texture2DRect(a2, xy);
	vec4 tmp3			= texture2DRect(a3, xy);

	init();

	vec4 pixelColor		= tmpOI0;
	vec4 origin			= vec4(tmpOI1.xyz, 1.0);
	vec3 direction		= tmpOI2.xyz;
	vec3 spectrum		= tmpOI3.xyz;

	vec3 iPoint			= tmp0.xyz;
	vec3 normal			= tmp1.xyz;
	float area			= tmp2.y;
	vec4 lightOrigin	= vec4(tmp3.xyz, 1.0);

	int id				= int(tmp0.w);
	int type			= int(tmp1.w);
	float shine			= tmpOI3.w;
	random2D 			= tmp2.zw;

	float lightDecay	= tmp3.w;

	bool invertNormal	= false; // tmp3.x < 0 ? true : false;

	primRay  			= Ray(origin, direction, -1, spectrum);

	bool isBlack		= isBlack(spectrum);

	TraceData td = TraceData(	id >= 0,
								vec4(iPoint, 1.0),
								normal,
								id,
								type,
								invertNormal
							);

	if(id > -1) {

		vec3 color  = black;

		#ifdef SHADE_MODE
		
		Light light = getLight(  lightPass*sizes[LIGHT] );
		
		if(light.type == 2.0)
			light.origin = lightOrigin;
		
		if(td.type == PARA && ((light.isAlsoLight * sizes[PARA]) + paraPart) == td.id && recursionDepth == 0)
			pixelColor 	+=  vec4((light.color.xyz * light.power) / superSample, 1.0);

		if(lightDecay > EPSILON && isValid(lightDecay) && isValid(spectrum))
		{
			
			color 		= shade(td, spectrum, light, lightDecay, area);
			if(isValid(color))
			pixelColor 	+=  vec4((shine * color) / superSample, 1.0);
		}

		#endif

		#ifdef SPAWN_MODE

		secRay.origin 	= vec4(epsilonEnvironment(iPoint, normal), 1.0);
		
		getSecondaryRay(primRay, normal, td);

		direction 		= normalize(secRay.direction);
		spectrum		= secRay.spectrum;
		
		#endif

	} else {

		#ifdef USE_SUNSKY
		
		if(!isBlack)
		{
			vec3 color = vec3(1.0, 0.0, 0.0);
			
			getSkyRGB(direction, color);
	
			pixelColor	+= vec4((shine * color * spectrum) / superSample, 1.0);
		}
		
		#endif

		#ifdef SPAWN_MODE

		spectrum		= vec3(0.0);

		#endif

	}

	gl_FragData[0]	= vec4(pixelColor					);
	gl_FragData[1]	= vec4(iPoint		, 	random2D.x	);
	gl_FragData[2]	= vec4(direction	,	random2D.y	);
	gl_FragData[3]	= vec4(spectrum		,		1.0		);
}
