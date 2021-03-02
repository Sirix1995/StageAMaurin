LLight getSampledLight(int lightID, vec4 lightOrigin, vec4 iPoint)
{
	LLight light;

	/*if(lightID == lightCount - 1 && hasSunSky)
	{
		//float skyIntensity 	= 0.0;

		//getSkyIntensity(-normalize(lightOrigin - iPoint).xyz, skyIntensity);

		//light 		= LLight(lightOrigin, skyIntensity, SKY_LIGHT, 0.f, 0.0f, 0, -1, 1.0f, 1.0f);

		light 		= LLight(vec4(0,0,-20,1.0), 1.0, POINT_LIGHT, 0.f, 0.0f, 0, -1, 1.0f, 1.0f);

	} else {
*/
		light	= getLLight(int(lightID * sizes[LIGHT]));

//	}

	if(light.type >= AREA_LIGHT)
		light.origin = lightOrigin;

	return light;
}

int lambdaDelta = lambdaMax - lambdaMin;


void main()
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

	vec3 CIE_XYZ		= vec3(CIE_X, CIE_Y, CIE_Z);

	vec4 pixelColor		= tmpOI0;
	vec4 origin			= vec4(tmpOI1.xyz, 1.0);
	vec3 direction		= tmpOI2.xyz;
	float rayIntens		= tmpOI3.x;
	int lightID			= int(tmpOI3.y);

	vec3 iPoint			= tmp0.xyz;
	vec3 normal			= tmp1.xyz;
	vec4 lightOrigin	= vec4(tmp3.xyz, 1.0);

	int id				= int(tmp0.w);
	int type			= int(tmp1.w);
	float shine			= tmpOI3.w;
	random2D			= tmp2.zw;

	float area			= tmp2.y;
	float lightDecay	= tmp3.w;

	float specBounce	= tmpOI1.w;

	//normalTest			= vec4(7777);

	init();

	// PBRT notation for a eye-ray
	LRay wo				= LRay(origin, direction, -1, rayIntens);

	// PBRT notation for the generated ray
	LRay wi				= wo;

	TraceData td = TraceData(	id >= 0,
									vec4(iPoint, 1.0),
									normal,
									id,
									type,
									false
								);



	float intensity 	= 0.0;
	float skyIntensity 	= 0.0;

	if(id >= 0)
	{
		Material mat 	= getMaterial(td);

		LLight light	= getSampledLight(lightID, lightOrigin, vec4(iPoint, 1.0));

		#ifdef SHADE_MODE_BxDF

		if(lightDecay > EPSILON && isValid(lightDecay) || specBounce > 0.0)
		{
			if(td.type == PARA && ((light.isAlsoLight * sizes[PARA]) + paraPart) == td.id)
			{
				if(recursionDepth == 0 && dot(normal, -direction) > 0f)
					intensity = light.power * light.intensity;
				else if(specBounce > 0.0 && recursionDepth <= 3)
					intensity += rayIntens * specBounce;

				specBounce = -2.0;

				mat.v1 = 0.0;
			}

			//Sampling the BxDF at hidden point
			intensity	+= rayIntens * sample_BxDFs(wo, light, td, mat, area, lightID) * lightDecay;

			//Delete specular bounce value
			if(specBounce > -1.9)
				specBounce = 0.0;

			//Store received light intensity, when the last BxDF was a specular one.

			if((specBounce > -1.9 || recursionDepth <= 0) && (mat.bxdf_type == BXDF_GLASS || mat.bxdf_type == BXDF_COOKTORRANCE || mat.bxdf_type == BXDF_MICROFACET))
				specBounce = light.intensity * lightDecay;

			else
				specBounce = -2.0;

			//Validated computed value...
			if(isValid(intensity))
			{
				// Integration of the result spectra, just one step
				vec3 xyzCOL	= intensity * CIE_XYZ;

				pixelColor += (vec4(xyzCOL, 1.0) * invLambdaSamples) / superSample;
			}
		}

		#endif

		#ifdef SHADE_MODE_SPAWN

		if(td.type == PARA && ((light.isAlsoLight * sizes[PARA]) + paraPart) == td.id)
		{
			mat.v1 = 0.0;
		}

		wi				= sample_Directions(wo, td, mat, light, area);

		if(!isValid(wi.intensity) || !isValid(wi.direction) || !isValid(wi.origin.xyz))
		{
			wi.origin		= vec4(0.0);
			wi.intensity	= 0.0;
			wi.direction	= vec3(1.0);
		}

		#endif

	} else {

		#ifdef SHADE_MODE_BxDF

		if(rayIntens > 0.0)
		{
			getSkyIntensity(direction, skyIntensity);
			vec3 xyzCOL		= rayIntens * skyIntensity * CIE_XYZ / 683.03;

			pixelColor	   += (vec4(xyzCOL, 1.0) * invLambdaSamples) / superSample;

			wi.intensity	= 0.0;
		}
		#endif

		#ifdef SHADE_MODE_SPAWN

		wi.intensity	= 0.0;

		#endif
	}

	// Choosing for the nex ray the next light
	#ifdef SHADE_MODE_SPAWN
	lightID			= int(min(float(int(rand() * lightCount)), float(lightCount - 1)));
	#endif

	gl_FragData[0] 	= pixelColor;
	gl_FragData[1]	= vec4(wi.origin.xyz,		specBounce						);
	gl_FragData[2]	= vec4(wi.direction, 			-1							);
	gl_FragData[3] 	= vec4(wi.intensity, 		lightID, 	random2D			);
}
