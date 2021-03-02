void main(void)
{
	vec4 origin 	= texture2DRect(a0, gl_TexCoord[0].xy);
	vec4 dir 		= texture2DRect(a1, gl_TexCoord[0].xy);
	vec4 spectrum	= texture2DRect(a2, gl_TexCoord[0].xy);
	
	init(dir.w);
	
	secRay = Ray(vec4(origin.xyz, 1.0), dir.xyz, -1, spectrum.rgb);

	if(hasIntersection(origin.w))
	{
		Ray inRay = Ray(vec4(origin.xyz, 1.0), dir.xyz, -1, spectrum.rgb);
		
		TraceData td = getTraceData(origin, a1);
		
		setSecRayDirection(inRay, td.normal, td);
	}
	
	gl_FragData[0] = origin;
	gl_FragData[1] = vec4(secRay.direction, random);
	gl_FragData[2] = vec4(secRay.spectrum, spectrum.w);
	gl_FragData[3] = vec4(-1.0);
} //main