
bool checkConeOfLight(vec4 iPoint, float pos, inout float decay)
{
	bool result = true;
	Light light = getLight(int(pos));
	
	if(light.type == SPOT_LIGHT)
	{
		vec4 pnt 	= getInverseMatrix(lightPart + int(pos)) * iPoint;		
		result 		= acos( calculateAngle(pnt.xyz, vec3(0.0, 0.0, 1.0) ) ) < light.innerAngle;
	}
	
	if(light.type == AREA_LIGHT)
	{
		mat4 m = getInverseMatrix(lightPart + int(pos));
		vec4 origin = m * light.origin;
		vec4 pnt = m * iPoint;
		pnt = pnt - origin;
		decay = calculateAngle( pnt.xyz, vec3(0.0, 1.0, 0.0) );
		result = decay > 0.0;// && pnt.x >= -1.0 && pnt.x <= 1.0;
	}
	
	return result;
} //checkConeOfLight


void testShadow(vec2 pos, vec3 iPoint, inout vec3 attenuation)
{
	bool shadow			= false;
	vec2 coord 			= gl_FragCoord.xy;
	
	coord += pos;
	
	vec4 lightOrigin 	= texture2DRect(lightPathTexture, coord);
	float isAlsoLight	= texture2DRect(irradianceTexture, coord).w;
	vec3 pointToLight 	= lightOrigin.xyz - iPoint;
	
	// !lightPath > 0 geht hier auch 
//	if(checkConeOfLight(vec4(iPoint, 1.0), lightOrigin.w, attenuation))
//	{
		shadow = isShadowed(vec4(iPoint, 1.0), pointToLight, 0, int(isAlsoLight));
//	}
	
	if(shadow)
	{
		attenuation = vec3(0.0);
	}
	
} //calcAttenuation


void init(void)
{
	sizes[SPHERE] 	= 7;
	sizes[BOX] 		= 7;
	sizes[CFC] 		= 8;
	sizes[PLANE] 	= 7;
	sizes[MESH]		= 7;
	sizes[PARA]		= 8;
	sizes[LIGHT]	= 9;
	sizes[CELL]		= 8;
	sizes[TRI]		= 5;
	
	refDepth = -1.0;
} //init




void main(void)
{
	vec2 texCoord 		= gl_TexCoord[0].xy;
	vec4 iPoint 		= texture2DRect(eyePathTexture, texCoord);
	float type			= texture2DRect(typeTexture, texCoord).w;
	vec4 normal 		= texture2DRect(stateTexture, texCoord);
	vec4 lightOrigin 	= texture2DRect(lightPathTexture, texCoord);
	vec4 attenuation	= texture2DRect(attenuationTexture, texCoord);
	
	init();

	
	float att = attenuation[0];

	
	// if an intersection point exists calculate the normal vector
	if(hasIntersection(iPoint.w) && att > 0.0)
	{
		int id = int( abs(iPoint.w) );
		float invertNormal = sign(iPoint.w + EPSILON);// >= 0.0 ? 1.0 : -1.0;
		iPoint.w = 1.0;
		
		vec4 localiPoint = getInverseMatrix(type == TRI ? meshPart : id) * iPoint;
		TraceData td = TraceData(true, localiPoint, normal.xyz, id, int(type), 
				invertNormal);
		
		normal.xyz = calculateNormal(td);
		
		testShadow(vec2(0.0), iPoint.xyz, normal.xyz);
		
	} //if
	
	gl_FragData[0] = normal;
	gl_FragData[1] = attenuation;
}