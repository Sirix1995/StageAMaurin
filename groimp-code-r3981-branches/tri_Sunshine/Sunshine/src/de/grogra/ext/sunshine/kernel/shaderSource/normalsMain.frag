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
	
	sizes[TRI]		= 3;

	refDeep = -1.0;
} //init


void mainOld(void)
{
	vec2 texCoord = gl_TexCoord[0].xy;//gl_FragCoord.xy;
	vec4 oldColor		= texture2DRect(a0, texCoord);
	vec4 origin 		= texture2DRect(a1, texCoord);
	vec4 dir 			= texture2DRect(a2, texCoord);
	vec4 shine			= texture2DRect(a3, texCoord);

	init();
	primRay  = Ray(origin, dir.xyz, -1, shine.xyz);
	random 	 = dir.w;
	
	float variance	= 1.0;
	vec3 currentColor = black;
	
	//currentColor = trace(primRay, variance, TraceData());	
	
	
	gl_FragData[0] = vec4(oldColor.xyz + shine.w*currentColor/superSample, debug);
	gl_FragData[1] = secRay.origin;
	gl_FragData[2] = vec4(secRay.direction, random);
	gl_FragData[3] = vec4(secRay.spectrum, variance);
} //main


void main(void)
{
	vec2 texCoord 		= gl_TexCoord[0].xy;//gl_FragCoord.xy;
	
	vec4 tmp0			= texture2DRect(a0, texCoord);
	vec4 tmp1			= texture2DRect(a1, texCoord);
	vec4 tmp2			= texture2DRect(a2, texCoord);
	vec4 tmp3			= texture2DRect(a3, texCoord);
	
	init();

	vec4 pixelColor		= tmp0;
	vec4 origin			= vec4(tmp1.xyz, 1.0);
	vec3 iPoint			= tmp2.xyz;
	vec3 spectrum		= tmp3.xyz;
	
	int id				= int(tmp1.w); //tmp3.x < 0 ? -1*int(tmp3.x) : int(tmp3.x);
	int typ				= getType(float(id)); //int(tmp3.y);
	bool invertNormal	= false; // tmp3.x < 0 ? true : false;
	random 	 			= tmp3.w;
	
	primRay  			= Ray(origin, normalize(iPoint.xyz - origin.xyz), -1, spectrum.xyz);
	
	float variance		= 1.0;
	vec3 currentColor 	= black;
	
	TraceData td = TraceData(	id >= 0, 
								vec4(iPoint, 1.0),
								vec3(0.0),
								id,
								typ,
								invertNormal
							);
		

	if(td.hasIntersection)
	{
		td.iPoint		= getInverseMatrix(td.typ != TRI ? float(td.id) : meshPart) * td.iPoint;
		
		td.normal		= calculateNormal(td) ;			
			
	}
	
	//pixelColor			= td.hasIntersection ? vec3(1.0) : vec3(0.0);
	
	// Intersection point with a small distance to the original iPoint						
	iPoint 				= epsilonEnvironment(vec4(iPoint, 1.0), td.normal).xyz;
	
	//gl_FragData[0]		= vec4(pixelColor.xyz, 1.0);
	//gl_FragData[0]		= vec4(pixelColor.xyz, 		td.id);	
		
	gl_FragData[0]		= vec4(td.normal, 		td.id);
	gl_FragData[1]		= vec4(iPoint, 			td.typ);
	gl_FragData[2]		= vec4(0.0);
	gl_FragData[3]		= vec4(0.0);	
		
	/*	
	gl_FragData[0]		= vec4(pixelColor, origin.x);
	gl_FragData[1]		= vec4(origin.yz, iPoint.xy);
	gl_FragData[2]		= vec4(iPoint.z, spectrum);
	gl_FragData[3]		= vec4(id, normStore, random);
	*/
	
} //main

