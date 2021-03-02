/*
bool testShadow(TraceData td, vec4 lightOrigin, float angle, int shadowless, int isAlsoLight)
{	
	bool result = false;
	
	
	if(angle > 0.0 && shadowless != 1)
	{
		Ray shadowFeeler = Ray(td.iPoint, lightOrigin.xyz - td.iPoint.xyz, isAlsoLight, vec3(1.0) );
		
		
		//shadowTraceData
		TraceData std = intersection( shadowFeeler );
		
		
		if(std.hasIntersection)
		{	
			vec4 iPoint = getMatrix(std.id) * std.iPoint;
			
			result = distance(td.iPoint, lightOrigin) > distance(td.iPoint, iPoint);
		} //if	
	} //if
	

	return result;
} //testShadow
*/

float calculateAngle(vec3 a, vec3 b)
{	
	return dot( normalize(a), normalize(b) );
} //calculateAngle


vec3 getNormal(vec3 iPoint, int id)
{
	mat4 m4x4 =	getInverseMatrix(id);
	vec3 normal = (m4x4 * vec4(iPoint, 1.0)).xyz;
	m4x4 = transpose(m4x4);
	
	mat3 m3x3;
	m3x3[0] = m4x4[0].xyz;
	m3x3[1] = m4x4[1].xyz;
	m3x3[2] = m4x4[2].xyz;
	

	return normalize(m3x3 * normal);
}

vec3 shade(TraceData td, vec3 normal)
{
	vec3 color = black;
	
	float angle;
		
	Light light = getLight( 0 );
		
//	if( !testShadow(td, light.origin, angle, light.shadowless, light.isAlsoLight) )
//	{
		angle = calculateAngle( light.origin.xyz - td.iPoint.xyz, normal );
		color =	getObjectColor(td.id).rgb * light.color.xyz * max(angle, 0.0);	
//	}
	
	return color;
} //shade


void main(void)
{
	vec2 texCoord = gl_TexCoord[0].xy;//gl_FragCoord.xy;
	
	vec4 normal			= texture2DRect(normals, texCoord);
	vec4 eyeVertices 	= texture2DRect(eyePath, texCoord);
	vec4 lightVertices	= texture2DRect(lightPath, texCoord);
	vec3 oldColor		= texture2DRect(color, texCoord).rgb;

	
	vec3 currentColor = oldColor;
	currentColor = black;
	
	if(eyeVertices.w >= 0.0)
	{
		int id 			= int(eyeVertices.w);
		vec3 normal 	= getNormal( eyeVertices.xyz, int(eyeVertices.w) );
		TraceData td 	= TraceData(id >= 0, vec4(eyeVertices.xyz, 1.0), normal, 
						id, SPHERE, false);
		
		currentColor += shade(td, normal);
	}
	
	
	gl_FragData[0] = vec4(currentColor.rgb, 1.0);
} //main