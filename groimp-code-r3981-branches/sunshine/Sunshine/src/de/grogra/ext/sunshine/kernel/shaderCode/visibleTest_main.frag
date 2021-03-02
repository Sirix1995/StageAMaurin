
bool checkConeOfLight(vec3 iPoint, float pos, inout float decay)
{
	bool result = true;

	if(lightPathVertex == 0)
	{
		Light light = getLight(int(pos) - lightPart);
		
		if(light.type == SPOT_LIGHT)
		{
			vec4 pnt 	= getInverseMatrix(int(pos)) * vec4(iPoint, 1.0);
			vec3 normal = vec3(0.0, 0.0, 1.0);
			
			result = acos(calculateAngle(pnt.xyz, normal)) < light.innerAngle;
		}
		
		
		if(light.type == AREA_LIGHT)
		{
			mat4 m 		= getInverseMatrix(int(pos));
			vec4 origin = m * light.origin;
			vec4 pnt 	= m * vec4(iPoint, 1.0);
			pnt 		= pnt - origin;
			decay 		= calculateAngle( pnt.xyz, vec3(0.0, 1.0, 0.0) );
			result 		= decay > 0.0;// && pnt.x >= -1.0 && pnt.x <= 1.0;
		}
	} //if

	return result;
}


void testShadow(vec3 indexC, vec4 indexS, inout vec4 attenuation)
{
	float decay 	= 1.0;
	vec3 outRay		= indexS.xyz - indexC;
	
	if(checkConeOfLight(indexC, indexS.w, decay))
	{
		attenuation[3] = float(!isShadowed(vec4(indexC, 1.0), outRay, 0));
		attenuation[3] *= decay;
	}
	else
	{
		attenuation.w = 0.0;
	}


} //testShadow


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

	refDepth 		= -1.0;
} //init

void main(void)
{
	vec4 indexC 	= texture2DRect(index2_Texture, gl_TexCoord[0].xy);
	vec4 indexS 	= texture2DRect(indexI_Texture, gl_TexCoord[0].xy);
	vec4 state		= texture2DRect(stateTexture, gl_TexCoord[0].xy);

	init();

	if((hasIntersection(indexC.w) && hasIntersection(indexS.w) && state.w > 0.0)
			|| (eyePathVertex == 0 && hasIntersection(indexS.w)))
	{
		testShadow(indexC.xyz, indexS, state);
	}

	gl_FragData[0] = vec4(1.0, 1.0, 0.0, state.w);
} //main
