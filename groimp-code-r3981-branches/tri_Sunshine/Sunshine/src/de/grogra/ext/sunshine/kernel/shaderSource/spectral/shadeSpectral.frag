uniform int lamda;
int tileX = 0;


uniform float X;
uniform float Y;
uniform float Z;

int lamdaDelta = lamdaMax - lamdaMin;

float shadePerLamda(int lamda)
{
	return 1.0;
}


void main()
{
	// Doing here the Riemann-Sum stuff:
	// LAMDA_STEP * (FOR EACH L: INTENSITY(L) * COLOR_MATCH(L))

	vec2 texCoor	= gl_TexCoord[0].xy;
	vec3 xyz		= texture2DRect(outputImage3, texCoor).xyz;

	float intensity = shadePerLamda(lamda);

	xyz 			= intensity * vec3(X, Y, Z);


	float xPos		= float(tileX * tileWidth) + texCoor.x;
	float relPos	= xPos / float(imageWidth);

	int mPos		= int( int(relPos  * (float(lamdaDelta))) + lamdaMin);

	bool draw		= lamda == mPos;

	gl_FragData[0] = texture2DRect(outputImage0, texCoor) + vec4( draw ? xyz : vec3(0.0), 1.0);
	gl_FragData[3] = vec4(xyz, 1.0);
}
