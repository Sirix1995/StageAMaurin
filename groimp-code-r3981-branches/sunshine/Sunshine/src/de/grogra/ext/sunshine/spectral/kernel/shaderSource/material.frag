
float getValueFromTex(TraceData td, float matID, int rowOffset, int matCols )
{
	int line 		= int(matID / float(matCols));
	int pos			= int(matID - int(matCols * line));

	line		   += rowOffset;

	float value		= texture2DRect(materialTex, vec2(pos, line)).x;

	#ifdef HAS_IMG_TEX

	if(value <= -1.0 )
	{
		UV uv		= getUV(td);

		value		= abs(value) - 1;

		int y		= int(value / 8.0);
		int x		= int(value - (y * 8));

		vec3 rgb	= texture2DRect(texTexture, vec2( (x + uv.u) * 512, (y + uv.v) * 512) ).xyz;
		value		= getReflectanceIntensity(rgb);
	}

	#endif

	return value;
}

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

		mat.v1				= getValueFromTex(td, matID + 0.0f, matRowOffset, matCols);

		if(matType == BXDF_COOKTORRANCE) {

			mat.bxdf_type	= BXDF_COOKTORRANCE;

			mat.v2			= getValueFromTex(td, matID + 1.0f, matRowOffset, matCols);
			mat.urough		= getValueFromTex(td, matID + 2.0f, matRowOffset, matCols) * ROUGHNESS_SCALE;
			mat.ior			= getValueFromTex(td, matID + 3.0f, matRowOffset, matCols);

			if(mat.urough < ROUGHNESS_MIN)
				mat.urough	= ROUGHNESS_MIN;

		} else if (matType == BXDF_GLASS) {

			mat.bxdf_type	= BXDF_GLASS;

			mat.v2			= getValueFromTex(td, matID + 1.0f, matRowOffset, matCols);
			mat.ior			= getValueFromTex(td, matID + 2.0f, matRowOffset, matCols);
			mat.cauchy		= getValueFromTex(td, matID + 3.0f, matRowOffset, matCols);

		} else if (matType == BXDF_MICROFACET) {

			mat.bxdf_type	= BXDF_MICROFACET;

			mat.v2			= getValueFromTex(td, matID + 1.0f, matRowOffset, matCols);
			mat.urough		= getValueFromTex(td, matID + 2.0f, matRowOffset, matCols) * ROUGHNESS_SCALE;
			mat.ior			= getValueFromTex(td, matID + 3.0f, matRowOffset, matCols);
			mat.cauchy		= getValueFromTex(td, matID + 4.0f, matRowOffset, matCols);

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
