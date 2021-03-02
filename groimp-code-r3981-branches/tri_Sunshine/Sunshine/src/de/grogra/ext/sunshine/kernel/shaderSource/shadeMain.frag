
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
	vec4 lightOrigin	= vec4(tmp3.xyz, 1.0);
	
	int id				= int(tmp0.w);
	int type			= int(tmp1.w);
	float shine			= tmpOI3.w;
	random 	 			= tmp2.w;

	refDeep				= tmp2.x;
	bool inShadow		= tmp3.w > 0.0 ? true : false;
			
	bool invertNormal	= false; // tmp3.x < 0 ? true : false;

	float variance		= 1.0;
	primRay  			= Ray(origin, direction, -1, spectrum);
	

	
	TraceData td = TraceData(	id >= 0, 
								vec4(iPoint, 1.0),
								normal,
								id,
								type,
								invertNormal
							);								
	if(td.hasIntersection)	
	{	
		vec3 color  = black;
		if(!inShadow)
		{
			color = shade(td, spectrum, lightOrigin);
			pixelColor +=  vec4((shine * color) / superSample, 1.0);	
		} 			
		
		secRay.origin 	= vec4(iPoint, 1.0);		
		variance 		= getSecondaryRay(primRay, normal, td);		
		direction		= normalize(secRay.direction);
		secRay.origin 	= vec4(epsilonEnvironment(secRay.origin.xyz, direction), 1.0);
	
	} 
	else	
	{
		secRay.spectrum = vec3(0.0);	
	}	

	
	
	gl_FragData[0]	= vec4(pixelColor						);
	gl_FragData[1]	= vec4(iPoint			, 	-1			);
	gl_FragData[2]	= vec4(direction		,	random		);
	gl_FragData[3]	= vec4(secRay.spectrum	,	variance	); 
}