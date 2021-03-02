Material getMaterial(TraceData td)
{
	float id				= td.id;
	Material mat 			= Material(BXDF_LAMBERTIAN, lambda, 1.0, 1.0, 1.0, 0.0, 1.0, 0.0);
	vec4 color				= getObjectColor(id);

	// Cause on CPU side we need to negate and add 1 to the mat ID we have to
	// subtracted 1 and take the absolute of matID in order to get the right ID
	float matID				= abs(color.x) - 1.0;
	int matType				= int(color.y);

	if(color.x < 0)
	{

		vec4 tmpFetch		= texture2DRect(tempMatTex0, gl_TexCoord[0].xy);

		mat.v1				= tmpFetch.x;

		if(matType == BXDF_COOKTORRANCE) {

			mat.bxdf_type	= BXDF_COOKTORRANCE;

			mat.v2			= tmpFetch.y;
			mat.urough		= tmpFetch.z * ROUGHNESS_SCALE;
			mat.ior			= tmpFetch.w;

			if(mat.urough < ROUGHNESS_MIN)
				mat.urough	= ROUGHNESS_MIN;

		} else if (matType == BXDF_GLASS) {

			mat.bxdf_type	= BXDF_GLASS;

			mat.v2			= tmpFetch.y;
			mat.ior			= tmpFetch.z;
			mat.cauchy		= tmpFetch.w;

		} else if (matType == BXDF_MICROFACET) {

			mat.bxdf_type	= BXDF_MICROFACET;

			mat.v2			= tmpFetch.y;
			mat.urough		= tmpFetch.z * ROUGHNESS_SCALE;
			mat.ior			= tmpFetch.w;
			mat.cauchy		= texture2DRect(tempMatTex1, gl_TexCoord[0].xy).x;

			if(mat.urough < ROUGHNESS_MIN)
				mat.urough	= ROUGHNESS_MIN;
		}

	} else {

		float intens		= getReflectanceIntensity(color.xyz);
		mat.v1 				= intens;
		mat.v2 				= intens;
	}

	return mat;
}
