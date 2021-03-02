
vec4 epsilonEnvironment(vec4 point, vec3 direction)
{
	//float epsilon = 1.0/160.0;
	const float epsilon = 0.001;
	
	return point + (epsilon * vec4( normalize(direction), 0.0) );
} //epsilonEnvironment


vec3 trace(Ray ray, inout float var)
{	
	vec3 normal = vec3(-1.0);

	accelerate = false;
	TraceData td = accelerate ? traverseTree(ray, 0) : intersection(ray);
	
	
	if(td.hasIntersection)
	{
		secRay.origin.rgb = red;
		normal = calculateNormal(td);
		
		//local to global
		td.iPoint = getMatrix(td.id) * td.iPoint;
		td.iPoint = epsilonEnvironment(td.iPoint, normal);
		secRay.origin = td.iPoint;
			
		
		var = getSecondaryRay(ray, normal, td);
		secRay.origin.w = float(td.id);
	} 
	else
	{
		secRay.origin.w = -1.0;
	}
	
	return normal;
} //trace

void init(void)
{
	sizes[SPHERE] 	= 7;
	sizes[BOX] 		= 7;
	sizes[CFC] 		= 8;
	sizes[PLANE] 	= 7;
	sizes[PARA]		= 8;
	sizes[LIGHT]	= 9;
	sizes[CELL]		= 8;
	
	refDeep = -1.0;
} //init


void main(void)
{
	vec2 texCoord = gl_TexCoord[0].xy;//gl_FragCoord.xy;
	
	vec4 origin = vec4(texture2DRect(a1, texCoord).xyz, 1.0);
	vec4 dir 	= texture2DRect(a2, texCoord);
	vec4 shine	= texture2DRect(a3, texCoord);

	init();
	primRay  = Ray(origin, dir.xyz, -1, shine.xyz);
	random 	 = dir.w;
	
	float variance	= 1.0;
	
	vec3 normal = trace(primRay, variance);
	
	
	gl_FragData[0] = vec4(normal, 1.0);
	gl_FragData[1] = secRay.origin;
	gl_FragData[2] = vec4(secRay.direction, random);
	gl_FragData[3] = vec4(secRay.spectrum, variance);
} //main

