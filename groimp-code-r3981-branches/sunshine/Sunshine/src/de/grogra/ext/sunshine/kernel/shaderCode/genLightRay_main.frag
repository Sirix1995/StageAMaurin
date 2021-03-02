
void init(float seed)
{
	sizes[LIGHT]	= 9;
	
	// init RNG
	random 	 		= seed;
} //init

void main(void)
{
	init(texture2DRect(a1, gl_FragCoord.xy).w);
	
	int pos			= 0;
	Light light 	= retrieveLight(pos);

	//--------------------------------------------------\\
	
	pos 			= lightPart + pos*sizes[LIGHT];
	Direction ray 	= generateRandomRays(light, pos);
	vec3 spectrum 	= float(lightCount)*light.color.rgb;
	float originDensity	= getOriginDensity(light, pos);

	
	gl_FragData[0] = vec4(light.origin.xyz, float(pos));
	gl_FragData[1] = vec4(getSphericalCoordinates(ray.dir), ray.density, random);
	gl_FragData[2] = vec4(spectrum, NO_INTERSECTION);
	gl_FragData[3] = vec4(originDensity, -1.0, 1.0, -1.0);
}
