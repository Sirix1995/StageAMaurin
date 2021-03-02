float calculateAngle(vec3 a, vec3 b)
{	
	return dot( normalize(a), normalize(b) );
} //calculateAngle


//float decay = 1.0;

bool checkConeOfLight(vec4 iPoint, int pos, Light light, out float decay)
{
	bool result = true;
	decay = 1.0;
	
	if(light.typ == 1.0)
	{
		mat4 m		= getInverseMatrix(lightPart + float(pos));
		vec4 pnt 	= mulMat(m, iPoint);
		//result 		= pnt.x > light.innerAngle;
		
		result 		= acos( calculateAngle(pnt.xyz, vec3(0.0, 0.0, 1.0) ) ) < light.innerAngle;
	}

	if(light.typ == 2.0)
	{
		mat4 m		= getInverseMatrix(lightPart + float(pos));
		vec4 origin = mulMat(m, light.origin);
		vec4 pnt 	= mulMat(m, iPoint);
		pnt 		= pnt - origin;
		decay 		= calculateAngle( pnt.xyz, vec3(0.0, 1.0, 0.0) );
		result 		= decay > EPSILON;// && pnt.x >= -1.0 && pnt.x <= 1.0;
	}
	
	return result;
}

vec3 shade(TraceData td, vec3 spectrum, vec4 lightOrigin)
{	
	vec3 color 			= black;	
	vec3 normal 		= td.normal;
	
	float AREA_LIGHT 	= 2.0;

	float phong;// 		= -1.0;
		
	//check used shading
	vec4 c 				= getObjectColor(td.id);
	
	phong 				= c.x == -1.0 ? c.y : -1.0;
		
	Light light 		= getLight(  lightPass*sizes[LIGHT] );		
			
	if(light.typ == AREA_LIGHT)
		light.origin 	= lightOrigin;
		
	float decay 		= 1.0;
			
	if( checkConeOfLight(td.iPoint, lightPass*sizes[LIGHT], light, decay) )
	{										
		float angle = calculateAngle( light.origin.xyz - td.iPoint.xyz, normal );
		
		angle = max(angle, 0.0);
		
		color = spectrum*calcDirectIllumination(light, td, angle, normal, phong, 0 );	
		color *= light.typ == AREA_LIGHT ? decay : 1.0;
				
	} //if
			
	
	return color;
} //shade
