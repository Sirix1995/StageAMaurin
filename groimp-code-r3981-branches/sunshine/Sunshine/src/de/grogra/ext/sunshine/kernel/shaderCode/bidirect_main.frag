
vec2 trace(Ray ray, inout State state, inout VertexInfos infos)
{	
	secRay 			= ray;
	ray.origin 		= epsilonEnvironment(ray.origin, ray.direction);
	vec2 result 	= vec2(state.id, state.type);
	bool lastCycle 	= loopStop >= objectCount;
	
	TraceData lastStateData = nilTD;
	TraceData td 			= nilTD;
	
	// recover the last state
	if(state.depth > 0.0)
	{
		result = vec2(state.id, state.type);
		refDepth = state.depth;
		float id = abs(state.id);
		float invertNormal = state.id >= 0.0 ? 1.0 : -1.0;
		vec4 iPoint = vec4(state.iPoint, 1.0);
		
		lastStateData = TraceData(true, iPoint, vec3(0.0), int(id), 
				int(state.type), invertNormal); 
	} //if
	
	td = intersection(ray, lastStateData);
	
	if(td.hasIntersection)
	{
		result 			= vec2( float(td.id)*td.invertNormal, float(td.type) );
		
		state.depth		= refDepth;
		state.iPoint 	= td.iPoint.xyz;
		
		
		if(lastCycle)
		{			
			vec3 normal = calculateNormal(td);
			
			secRay.origin = convertLocal2Global(td.id, td.type, td.iPoint.xyz);			
			setSecRayDirection(ray, normal, td);
			
			td.normal 	= normal;
			setVertexInfos(ray.direction, td, infos);
			
			state.depth = -1.0;
		} //if
	} //if
	
	if(lastCycle && !td.hasIntersection)
		result = vec2(NO_INTERSECTION);
	
	
	return result;
} //trace


vec3 getDirection(vec2 angles)
{
	float theta = angles[0];
	float phi	= angles[1];
	
	vec3 dir;
	
	dir.x = cos(theta)*sin(phi);
	dir.y = sin(theta)*sin(phi);
	dir.z = cos(phi);
	
	return dir;
}


void main(void)
{
	vec4 attachment0 	= texture2DRect(a0, gl_FragCoord.xy);
	vec4 attachment1	= texture2DRect(a1, gl_FragCoord.xy);
	vec4 attachment2	= texture2DRect(a2, gl_FragCoord.xy);
	vec4 state			= texture2DRect(stateTexture, gl_FragCoord.xy);
	
	
	vec3 origin 		= attachment0.xyz;
	vec2 angles 		= attachment1.xy;
	vec3 spectrum 		= attachment2.rgb;
	float id			= attachment0.w;
	float dirDens	 	= attachment1.z;
	float type			= attachment2.w;
	
	
	init(attachment1.w);

	VertexInfos infos = VertexInfos(dirDens, 1.0, 1.0);
	
	vec3 direction = getDirection(angles);
	
	
	// it is important to normalize the ray direction
	primRay = Ray(vec4(origin, 1.0), direction, -1, spectrum, dirDens);
	
	
	//state = vec3 iPoint | float id | float type | float depth;
	State lastState = State(state.xyz, id, type, state.w);

	
	if(loopStart == 0) lastState = State(vec3(0.0), NO_INTERSECTION, NO_INTERSECTION, -1.0);
	
	
	vec2 tdata = trace(primRay, lastState, infos);
	
	
	gl_FragData[0] = vec4(secRay.origin.xyz, tdata.x);
	gl_FragData[1] = vec4(getSphericalCoordinates(secRay.direction), secRay.density, random);
	gl_FragData[2] = vec4(secRay.spectrum, tdata.y);
	
	if(loopStop < objectCount)
	{
		gl_FragData[3] = vec4(lastState.iPoint, lastState.depth);

	}
	else
	{
		gl_FragData[3] = vec4(dirDens, infos.dirDensOut, infos.geomFac, 
				lastState.depth);
	}
	
} //main

