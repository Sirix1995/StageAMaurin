
bool isShadowed(vec4 iPoint, vec3 pointToLight, int shadowless)
{	
	bool result = false;
	
	if(shadowless != 1)
	{
		vec4 origin 		= epsilonEnvironment(iPoint, pointToLight);
		Ray shadowFeeler 	= Ray(origin, pointToLight, 0, vec3(1.0), 1.0);
		
		
		//shadowTraceData
		TraceData td 	= nilTD;
		TraceData std 	= intersection(shadowFeeler, td);
		
		
		if(std.hasIntersection)
		{	
			vec4 hitPoint = convertLocal2Global(std.id, std.type, std.iPoint.xyz);
			
			result = length(pointToLight) - EPSILON > distance(iPoint.xyz, hitPoint.xyz);
		} //if	
	} //if
	

	return result;
} //testShadow
