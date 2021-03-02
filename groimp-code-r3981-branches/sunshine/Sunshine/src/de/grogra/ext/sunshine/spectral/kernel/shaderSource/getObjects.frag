float bb_spectrum(int wavelength, float bbTemp)
{
    float wlm = wavelength * 1e-9;   /* Wavelength in meters */

    return (3.74183e-16 * (1.0 / pow(wlm, 5.0))) /
           (exp(1.4388e-2 / (wlm * bbTemp)) - 1.0);
}

LLight getLLight(float pos)
{
	mat4 m 			= getMatrix(lightPart + pos);
	vec4 p0			= lookUp(lightPart + pos + float(7));
	vec4 p1			= lookUp(lightPart + pos + float(8));

	vec3 color		= getObjectColor(lightPart + pos).xyz;

	float inten		= 0.0;

	if(color.x <= -1.0)
	{
	
		float matID	= abs(color.x) - 1.0;

		int line 	= int(matID / float(matCols));
		int posS		= int(matID - int(matCols * line));

		line	   += matRowOffset;

		inten		= texture2DRect(materialTex, vec2(posS, line)).x;

	} else {

		//vec3 col	= vec3(40.0, 32.0, 20.0);
		inten 		= getIlluminationIntensity(color);
	}

	return LLight( m[3], inten, p1.x, p0.x, p1.y, p1.z, int(p0.w), int(p1.w), p0.y, p0.z  );

} //getLight

