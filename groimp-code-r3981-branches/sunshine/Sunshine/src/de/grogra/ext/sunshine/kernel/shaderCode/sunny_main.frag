
bool checkConeOfLight(TraceData td, int pos, Light light, out float decay)
{
	bool result = true;
	decay = 1.0;
	
	if(light.type == SPOT_LIGHT)
	{
		vec4 pnt = getInverseMatrix(lightPart + pos) * td.iPoint;
		
		result = acos( calculateAngle(pnt.xyz, vec3(0.0, 0.0, 1.0) ) ) < light.innerAngle;
	}
	
	if(light.type == AREA_LIGHT)
	{
		mat4 m = getInverseMatrix(lightPart + pos);
		vec4 origin = m * light.origin;
		vec4 pnt = m * td.iPoint;
		pnt = pnt - origin;
		decay = calculateAngle( pnt.xyz, vec3(0.0, 1.0, 0.0) );
		result = decay > 0.0;// && pnt.x >= -1.0 && pnt.x <= 1.0;
	}
	
	return result;
}


vec4 randomOrigin(int pos)
{
	//left edge of parallelogram in <0,0>
	vec3 start = vec3(-1.0, 0.0, 0.0); 
	
	start.x += 2.0*rand();;
	start.z += rand();;
	
	return getMatrix(lightPart + pos*sizes[LIGHT]) * vec4(start, 1.0);
}


vec3 shade(TraceData td, vec3 normal, vec3 spectrum)
{	
	float angle;
	vec3 color 	= black;
	vec3 col 	= black;
	float phong;// = -1.0;
	Light light;
	bool shadow;

	for(int i = 0; i < lightCount; i++)
	{
		shadow = false;
		light = getLight( i*sizes[LIGHT] );

		if(light.type == AREA_LIGHT)
			light.origin = randomOrigin(i);
		
		float decay = 1.0;
		
		if( checkConeOfLight(td, i*sizes[LIGHT], light, decay) )
		{
			vec3 pointToLight = light.origin.xyz - td.iPoint.xyz;
			angle = calculateAngle( pointToLight, normal );
			
			if( !isShadowed(td.iPoint, pointToLight, light.shadowless, light.isAlsoLight) )
			{
				color += spectrum*calcDirectIllumination(light.color.rgb, 
						-normalize(primRay.direction), pointToLight, td, true, decay );	
			}
			
		} //if
		
	} //for
	
	return color;
} //shade


vec3 trace(Ray ray)
{	
	secRay = ray;
	vec3 color = black;

//	accelerate = false;
//	TraceData td = accelerate ? traverseTree(ray, 0) : intersection(ray);
	TraceData td = intersection(ray);
	
	
	if(td.hasIntersection)
	{
		vec3 normal = calculateNormal(td);
		
		td.iPoint = getMatrix(td.id) * td.iPoint;
		td.iPoint = epsilonEnvironment(td.iPoint, normal);
		secRay.origin = td.iPoint;
		
		color = shade(td, normal, ray.spectrum);
		
		getSecRayDirection(ray, normal, td);				
		
	} else
	{
		secRay.spectrum = vec3(0.0);
	}
	
	
	
	return color;
} //trace


void init(float seed)
{
	sizes[SPHERE] 	= 7;
	sizes[BOX] 		= 7;
	sizes[CFC] 		= 8;
	sizes[PLANE] 	= 7;
	sizes[PARA]		= 8;
	sizes[LIGHT]	= 9;
	sizes[CELL]		= 8;
	
	//init RNG
	random 		= seed;
	refDepth 	= -1.0;
	debug 		= 47.11;
} //init


void main(void)
{
	vec2 texCoord 		= gl_TexCoord[0].xy;//gl_FragCoord.xy;
	vec4 oldColor		= texture2DRect(a0, texCoord);
	vec4 origin 		= texture2DRect(a1, texCoord);
	vec4 dir 			= texture2DRect(a2, texCoord);
	vec4 shine			= texture2DRect(a3, texCoord);

	init(dir.w);
	primRay  = Ray(origin, dir.xyz, -1, shine.rgb);
	
	vec3 currentColor = black;
	
	currentColor = trace(primRay);	
	
	
	gl_FragData[0] = vec4(oldColor.xyz + shine.w*currentColor/superSample, debug);
	gl_FragData[1] = secRay.origin;
	gl_FragData[2] = vec4(secRay.direction, random);
	gl_FragData[3] = vec4(secRay.spectrum, 1.0);
} //main

