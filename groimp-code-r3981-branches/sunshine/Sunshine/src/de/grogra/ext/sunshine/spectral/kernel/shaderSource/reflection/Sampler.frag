bool isValid(float value)
{
	return value == value && value < INFINITY && value > -INFINITY;
}

bool isValid(vec3 value)
{
	return isValid(value.x) && isValid(value.y) && isValid(value.z);
}

bool isTransmitted = false;
float sample_BxDFs(LRay ray, LLight light, TraceData td, Material mat, float area, int lightID)
{
	if(light.intensity < EPSILON)
		return 0.0f;

	float Ld 			= 0.0;
	vec3 normal			= td.normal;
	vec4 iPoint			= td.iPoint;

	ShaderCoor sc		= createShadeCoorSys(normal);

	vec3 woW			= -ray.direction;
	vec3 wiW			= normalize(light.origin.xyz - iPoint.xyz);

	vec3 wo				= globalToLocalVec(sc, woW);	// Incomming ray
	vec3 wi				= globalToLocalVec(sc, wiW);	// Light ray

	float Li			= light.intensity;

	float[] f			= float[](0.f, 0.f);
	float[] bsdfPDF		= float[](1.f, 1.f);

	float weight		= 1.f;
	float[] lightPDF	= float[2](1.f, 1.f);

	bool isSpecular		= false;

	lightPDF[0]			= areaLightPDF(light.origin, iPoint, wiW, area, lightID);

	if(lightPDF[0] < 0.1)
		return 0.f;

	if (mat.bxdf_type == BXDF_LAMBERTIAN)
	{
		f[0]			= computeBRDF_Lambertian(mat);
		bsdfPDF[0]		= computePDF_Lambertian(wo, wi);

		wi				= normalize(computeDir_Lambertian(wo, bsdfPDF[1]));

	/*	f[1]			= computeBRDF_Lambertian(mat);
		bsdfPDF[1]		= computePDF_Lambertian(wo, wi);

		lightPDF[1]		= areaLightPDF(light.origin, iPoint, localToGlobalVec(sc, wi), area, lightID);;*/

	} else if(mat.bxdf_type == BXDF_GLASS) {

		/*int which		= dot(wiW, normal) * dot(woW, normal) > 0.0 ? T_BXDF_REFLECTION : T_BXDF_TRANSMISSION;

		f_lightSample				= computeBSDF_Microfacet(wo, wi, mat, which);

		bsdfPDF			= computePDF_Microfacet(wo, wi, mat);*/

		f[0]				= 0;
		bsdfPDF[0]			= 0;

		isSpecular		= true;


	} else if(mat.bxdf_type == BXDF_MICROFACET) {

		/*int which		= dot(wiW, normal) * dot(woW, normal) > 0.0 ? T_BXDF_REFLECTION : T_BXDF_TRANSMISSION;

		f_lightSample				= computeBSDF_Microfacet(wo, wi, mat, which);

		bsdfPDF			= computePDF_Microfacet(wo, wi, mat);*/

		isSpecular		= true;


	} else if(mat.bxdf_type == BXDF_COOKTORRANCE){

		f[0]				= 0.0; //computeBRDF_CookTorrance(wo, wi, mat);
		bsdfPDF[0]			= 0.f; //computePDF_CookTorrance(wo, wi, mat);


		isSpecular		= true;
	}

	if(lightPDF[0] < EPSILON)
		return 0.f;

	// Sampling BSDF at intersection point
	if(light.type > SPOT_LIGHT)
	{
		//Ld	   		   	= Li * f[0] * abs(dot(wiW, normal)) / area; /// lightPDF[0];



		// Light Sampling
		weight 			= PowerHeuristic(1, lightPDF[0], 1, bsdfPDF[0]);
		Ld	   		   	= Li * f[0] * abs(dot(wiW, normal)) * weight / lightPDF[0];

	//	weight 			= PowerHeuristic(1, bsdfPDF[1], 1, lightPDF[1]);

	//	Ld	   			+= Li * f[1] * abs(dot(localToGlobalVec(sc, wi), normal)) * weight / bsdfPDF[1];

		//TODO: BSDF-Sampling

	} else {

		Ld	   			= Li * f[0] * abs(dot(wiW, normal)); // / lightPDF[0];
	}

	return Ld * lightCount;
}

LRay sample_Directions(LRay ray, TraceData td, Material mat, LLight light, float lightPDF)
{
	float m					= mat.urough;
	vec3 normal				= td.normal;
	vec4 iPoint				= td.iPoint;

	ShaderCoor sc			= createShadeCoorSys(normal);

	vec3 woW				= -ray.direction; 								// (global)
	vec3 wiLW				= normalize(light.origin.xyz - iPoint.xyz);	// (global)
	vec3 wiW				= vec3(0.0);									// (global)

	vec3 wo					= globalToLocalVec(sc, woW);					// Incomming ray (local)
	vec3 wiL				= globalToLocalVec(sc, wiLW);					// Light ray (local)
	vec3 wi					= vec3(0.0); 									// New outgoing ray (local)

	int which			= T_BXDF_REFLECTION;

	if(dot(wiW, normal) * dot(woW, normal) < 0.0)
		which			= T_BXDF_TRANSMISSION;

	if(mat.bxdf_type == BXDF_LAMBERTIAN) {

		float pdf			= 0.0;

		wi					= computeDir_Lambertian(wo, pdf);
		wi					= normalize(wi);
		wiW					= localToGlobalVec(sc, wi);

		ray.intensity		*= computeBRDF_Lambertian(mat);
		ray.intensity  		*= abs(dot(wiW, normal)) / pdf;

		ray.direction		= wiW;

	} else if(mat.bxdf_type == BXDF_GLASS) {

		float pdf			= 0.0;
		float f				= 0.0;

		float F_r			= fresnel_dielectric(CosTheta(wo), 1.0, mat.ior);

		float u				= rand();

		if(u >= (1.f - F_r))
		//if(u >= 0.5)
		{
			wi				= computeDir_SpecularReflection(mat, wo, f, pdf, F_r);
			pdf				= (F_r);
		}
		else
		{
			wi				= computeDir_SpecularTransmission(mat, wo, f, pdf);
			pdf				= (1.f - F_r);
			isTransmitted   = true;
		}

		wi					= normalize(wi);
		wiW					= localToGlobalVec(sc, wi);

		f 				   /= pdf ;
		//f 				   *= 2 ;

		ray.intensity	   *= f * abs(dot(wiW, normal));
		ray.direction		= wiW;

	} /*else if(mat.bxdf_type == BXDF_MICROFACET) {

			float pdf			= 0.0;
			float f				= 0.0;

			wi					= computeDir_Microfacet(wo, m, mat, pdf);

			wi					= normalize(wi);
			wiW					= localToGlobalVec(sc, wi);

			//float weight		= 1.0;
			//if(pdf > 0.0)
			//	weight			= abs(dot(direction, normal)) / pdf;


			// TODO: Right weighting of BSDFs
		//	ray.intensity		*= computeBSDF_Microfacet(wi, wiL, mat, which);

			//if(pdf > EPSILON)
				ray.intensity	*= f * abs(dot(wiW, normal)) / pdf;
				//ray.intensity	*= abs(dot(wiW, normal)) / lightPDF;
			//else
			//	ray.intensity = 0.0;

			ray.direction		= wiW;

	} */else if(mat.bxdf_type == BXDF_COOKTORRANCE) {

		float pdf			= 0.0;

		float f				= computeDir_CookTorrance(wo, wi, mat, pdf);
		wi					= normalize(wi);

		wiW					= localToGlobalVec(sc, wi);

		ray.intensity 	   *= f * abs(dot(wiW, normal)) / pdf;

		ray.direction		= wiW;
	}

	//if(ray.intensity > 1)
				//ray.intensity = 1;
	ray.origin				= td.iPoint;

	return ray;
}
