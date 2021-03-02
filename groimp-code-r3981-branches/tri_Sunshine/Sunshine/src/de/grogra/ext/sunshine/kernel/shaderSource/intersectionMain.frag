

void main(void)
{
	vec2 xy				= gl_TexCoord[0].xy;
	
	vec4 tmpTrace0		= texture2DRect(a0, xy);
	vec4 tmpTrace1		= texture2DRect(a1, xy);
	vec4 tmpTrace2		= texture2DRect(a2, xy);
	vec4 tmpTrace3		= texture2DRect(a3, xy);
	
	vec4 tmpOutputImg1	= texture2DRect(outputImage1, xy);	
	vec4 tmpOutputImg2	= texture2DRect(outputImage2, xy);	
	vec4 tmpOutputImg3	= texture2DRect(outputImage3, xy);		
	
	init();
	
	// From texture set "a"
	vec3 iPoint			= vec3(tmpTrace0.xyz);
	vec3 normal			= vec3(tmpTrace1.xyz);
	vec3 lightOrigin	= vec3(tmpTrace3.xyz);
	
	int id				= isShadowTest < 1 && !(loopStart > 0) ? -1 		: int(tmpTrace0.w);
	int type			= isShadowTest < 1 && !(loopStart > 0) ? -1 		: int(tmpTrace1.w);
	refDeep				= /*isShadowTest < 1 &&*/ !(loopStart > 0) ? -1.0 	: tmpTrace2.x;
	bool inShadow 		= tmpTrace3.w > 0.0 /*&& isShadowTest > 0.0*/;
	
	// From texture set "outputImage"
	vec4 origin			= vec4(tmpOutputImg1.xyz, 1.0);
	vec3 direction		= tmpOutputImg2.xyz;
	vec3 spectrum		= tmpOutputImg3.xyz;	
	
	random				= isShadowTest < 1 ? tmpOutputImg2.w : tmpTrace2.w;
		
	vec4 rayOri			= origin;
	vec3 rayDir			= direction;	
		
	if(isShadowTest > 0 && id > -1)
	{
		Light light 	= getLight(lightPass * sizes[LIGHT]);
				
		if(light.typ == 2.0)  // is area-light?
		{	
			lightOrigin = !(loopStart > 0) ? randomOrigin( float(lightPass * sizes[LIGHT]) ).xyz : lightOrigin;		
			light.origin = vec4(lightOrigin, 1.0);
		}
		
		rayDir 			= normalize(light.origin.xyz - iPoint);
		rayOri			= vec4(epsilonEnvironment(iPoint, rayDir), 1.0);
		
	} 
	
	Ray ray				= Ray(rayOri, rayDir, typeOfRay, spectrum);
	setTriSet();

	TraceData td		= TraceData
							(
								id > -1,
								vec4(iPoint, 1.0),
								normal,
								id,
								type,
								false
							);
							
	if(isShadowTest > 0 && id > -1)						
			td			= TraceData(inShadow, vec4(0.0), vec3(0.0), -1, -1, false);
			

	if(isShadowTest < 1 || (isShadowTest > 0 && id > -1 && !inShadow))	
		intersection( ray, td );	
	
			
	if(isShadowTest < 1)
	{
		iPoint				= td.iPoint.xyz;
		normal				= td.normal;	
				
		id					= (td.typ == TRI) ? int(meshPart) : td.id;
		type				= td.typ;
		inShadow			= false;
			
		if(lastCycle > 0) // aka == 1	
			normal 			= normalLoc2Glo(normal, id);		
			
	} else if(!inShadow && id > -1){	
		
		float dist1 		= distance(rayOri.xyz, lightOrigin);
		float dist2 		= distance(rayOri.xyz, td.iPoint.xyz);
			
		inShadow 			= td.hasIntersection && (dist1 > dist2 && dist1 > EPSILON && dist2 > EPSILON);					
	} 
		
	gl_FragData[0]		= vec4(iPoint, 				id						);		// Global intersection point 		/ Object id	
	gl_FragData[1]		= vec4(normal, 				type					);		// Global global normal vector 		/ Object type
	gl_FragData[2]		= vec4(refDeep, vec2(0.0),	random					);		// Distance from origin to iPoint 	/ Actual used random number
	gl_FragData[3]		= vec4(lightOrigin.xyz, 	inShadow ? 1.0 : -1.0	);		// Optional for area lights	origin	/ Is iPoint lighted by actual light source		

}