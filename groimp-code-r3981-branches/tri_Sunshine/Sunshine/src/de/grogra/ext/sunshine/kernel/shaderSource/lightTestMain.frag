bool testShadow(TraceData td, vec4 lightOrigin, float angle, int shadowless, int isAlsoLight)
{	
	bool result = false;
	
	
	if(angle > 0.0 && shadowless != 1)
	{
		Ray shadowFeeler = Ray(td.iPoint, lightOrigin.xyz - td.iPoint.xyz, isAlsoLight, vec3(1.0) );
		
		/*
		//shadowTraceData 
		TraceData std = intersection( shadowFeeler );
		
		
		if(std.hasIntersection)
		{	
			vec4 iPoint = getMatrix(std.id) * std.iPoint;
			
			result = distance(td.iPoint, lightOrigin) > distance(td.iPoint, iPoint);
		} //if	*/
	} //if
	

	return result;
} //testShadow

bool checkConeOfLight(vec4 iPoint, int pos, Light light, out float decay)
{
	bool result = true;
	decay = 1.0;
	
	if(light.typ == 1.0)
	{
		vec4 pnt = getInverseMatrix(lightPart + pos) * iPoint;
		result = pnt.x > light.innerAngle;
		//result = acos( calculateAngle(pnt.xyz, vec3(0.0, 0.0, 1.0) ) ) < light.innerAngle;
	}

	if(light.typ == 2.0)
	{
		vec4 origin = getInverseMatrix(lightPart + pos) * light.origin;
		vec4 pnt = getInverseMatrix(lightPart + pos) * iPoint;
		pnt = pnt - origin;
		decay = calculateAngle( pnt.xyz, vec3(0.0, 1.0, 0.0) );
		result = decay > 0.0;// && pnt.x >= -1.0 && pnt.x <= 1.0;
	}
	
	return result;
}


vec4 randomOrigin(int pos)
{
	vec4 result;
	vec3 start = vec3(-1.0, 0.0, 0.0); //left edge of parallelogram in <0,0>
	
	float x = LCG(random);
	float z = LCG(random);
	
	start.x += 2.0*x;
	start.z += z;
	result = getMatrix(lightPart + pos*sizes[LIGHT]) * vec4(start, 1.0);
	return result;
}

vec3 shade(Ray ray, inout float var, inout TraceData td)
{	
	vec3 color = black;	
	vec3 normal;
	
	//if(td.hasIntersection)
	//{		
		normal = td.normal;
		
		if(td.typ == TRI)
			td.id 		= int(meshPart);
		
		// Intersection point with a small distance to the original iPoint						
		td.iPoint 		= epsilonEnvironment(td.iPoint, normal);
		
		secRay.origin 	= td.iPoint;			
		
		float angle;
		float AREA_LIGHT = 2.0;


		float phong;// = -1.0;
		Light light;
		vec4 lightOrigin;
		
		//check used shading
		vec4 c = vec4(0.5019608, 0.5019608, 0.5019608, 1.0);//getObjectColor(td.id);
	
		phong = c.x == -1.0 ? c.y : -1.0;
		
		
		for(int i = 0; i < lightCount; i++)
		{
			light = getLight( i*sizes[LIGHT] );
			//light.origin = vec4(0.0, -1.0, -0.1, 1.0);
	
			if(light.typ == AREA_LIGHT)
				light.origin = randomOrigin( i );
			
			float decay = 1.0;
			
			if( checkConeOfLight(td.iPoint, i*sizes[LIGHT], light, decay) )
			{										
				angle = 10.0; //calculateAngle( light.origin.xyz - td.iPoint.xyz, normal );
				
				if( !testShadow(td, light.origin, angle, light.shadowless, light.isAlsoLight) )
				{
					color += ray.spectrum*calcDirectIllumination(light, td, max(angle, 0.0), normal, phong, 0 );	
					color = light.typ == AREA_LIGHT ? color * decay : color;
				}
				
			} //if
			
		} //for

		//color = td.typ == TRI ? vec3(1.0) : vec3(0.0);
		//var = getSecondaryRay(ray, normal, td);
		//color = td.iPoint.xyz;
		
	//} else
	//{
	//	secRay.spectrum = vec3(0.0);
	//}
	
	return color;
} //trace

void main(void)
{

	vec2 texCoord 		= gl_TexCoord[0].xy;
	
	// This is the texture with normals
	vec4 tmp0			= texture2DRect(a0, texCoord);
	vec4 tmp1			= texture2DRect(a1, texCoord);
	vec4 tmp2			= texture2DRect(a2, texCoord);
	vec4 tmp3			= texture2DRect(a3, texCoord);
	
	init();
	
	vec3 pixelColor		= tmp0.xyz;
	vec4 origin			= vec4(tmp0.w, tmp1.x, tmp1.y, 1.0);
	vec3 iPoint			= vec3(tmp1.zw, tmp2.x);
	vec3 spectrum		= tmp2.yzw;
	int id				= int(tmp3.x); //tmp3.x < 0 ? -1*int(tmp3.x) : int(tmp3.x);
	int typ				= getType(float(id)); //int(tmp3.y);
	bool invertNormal	= false; // tmp3.x < 0 ? true : false;
	random 	 			= tmp3.w;

		
		
	gl_FragData[0]		= vec4(pixelColor + currentColor / superSample, origin.x);
	/*gl_FragData[1]		= vec4(secRay.origin.yz, td.iPoint.xy);
	gl_FragData[2]		= vec4(td.iPoint.z, secRay.spectrum);
	gl_FragData[3]		= vec4(-1, -1, 9999, random);*/
}