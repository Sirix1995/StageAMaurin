/**
  * Transform the local normal vector to global coordinates
  */
vec3 normalLoc2Glo(vec3 normal, int id)
{
	mat3 m3x3;
	mat4 m4x4 	= getInverseMatrix(id);
	m4x4 		= transpose(m4x4);

	m3x3[0] 	= m4x4[0].xyz;
	m3x3[1]	 	= m4x4[1].xyz;
	m3x3[2] 	= m4x4[2].xyz;

	return normalize(mulMat(m3x3, normal));
}

float calculateAngle(vec3 a, vec3 b)
{
	return dot( normalize(a), normalize(b) );
} //calculateAngle


float checkConeOfLight(vec4 iPoint, float pos)
{
	float decay = 1.0;
	Light light = getLight(int(pos));
	
	if(light.type == SPOT_LIGHT)
	{
		vec4 pnt 	= getInverseMatrix(lightPart + int(pos)) * iPoint;		
		decay 		= acos( calculateAngle(pnt.xyz, vec3(0.0, 0.0, 1.0) ) ) < light.innerAngle ? 1.0 : 0.0;
	}
	
	if(light.type == AREA_LIGHT)
	{
		mat4 m = getInverseMatrix(lightPart + int(pos));
		vec4 origin = m * light.origin;
		vec4 pnt = m * iPoint;
		pnt = pnt - origin;
		decay = calculateAngle( pnt.xyz, vec3(0.0, 1.0, 0.0) ) * light.power;
	}
	
	return decay;
}

float emittedPower(vec4 iPoint, vec3 normal, int pos, Light light)
{
	float Le 			= light.power;

	if(light.type == POINT_LIGHT)
	{
		Le		   		= 4. * PI / distanceSQR(light.origin, iPoint);

	} else if(light.type == SPOT_LIGHT) {

		mat4 m			= getInverseMatrix(lightPart + float(pos));
		vec4 pnt 		= mulMat(m, iPoint);

		Le 				= acos( calculateAngle(pnt.xyz, vec3(0.0, 0.0, 1.0) ) ) < light.innerAngle ? Le : 0.0;

		Le			   *= 4. * PI / distance(light.origin, iPoint);

	} else if(light.type == AREA_LIGHT){

		vec4 lightP		= light.origin;
		vec4 point 		= iPoint;
		vec3 wi			= normalize(point - lightP).xyz;

		vec3 normalL	= normalLoc2Glo(vec3(0.0, 1.0, 0.0), int(lightPart + float(pos)));

		if(dot(normalL, wi ) < EPSILON || dot(normal, -wi ) < EPSILON)
			Le	 		= 0;
	}

	return Le;
}

float areaLightPDF(vec4 lightO, vec4 iPoint, vec3 wi, float area, int lightID)
{
	vec3 normalL	= normalLoc2Glo(vec3(0.0, 1.0, 0.0), int(lightPart + lightID));

	float cos		= abs(dot(normalL, -wi));
	
	if (cos > EPSILON)
		return distanceSQR(lightO.xyz, iPoint.xyz) / (cos * area);
	else		
		return INFINITY;
}

/**
  * This method computes a uniform randomly distribution of points over a squares surface.
  * Afterwards the point is transformed into global coordinates. The matrix for transformation
  * is determined by the id for an objects matrix.
  */
vec4 randomOrigin(float pos, inout float area)
{
	vec4 result;
	vec3 start 		= vec3(-1.0, 0.01, 0.01); //left edge of parallelogram in <0,0>

	vec3 topLeft 	= vec3(-1.0, 0.0, 0.0);
	vec3 topRight 	= vec3(1.0, 0.0, 0.0);
	vec3 botLeft 	= vec3(-1.0, 0.0, 1.0);

	#if 1

	// New Version: Jitter

	float areas 		= sqrt(superSample);
	float areas_size	= 1.0 / areas;
	float tail			= 0.0;

	if(superSample != 1.0)
		tail			= mod(areas, 2);

	int vPos			= int((sample + tail)/ areas);
	int hPos			= int(sample - vPos * areas);

	vec2 xyRnd			= rand2D();

	start.x				+= 2.0 * areas_size * (hPos + xyRnd.x);
	start.z				+= areas_size * (vPos + xyRnd.y);

	#else

	// Old version of distribution over the area.

	vec2 xCoor	= rand2D();

	float x 	= random2D.x;
	float z 	= random2D.y;

	start.x 	+= 2.0*x;
	start.z 	+= z;

	#endif

	float lightID = lightPart + pos * float(sizes[LIGHT]);
	mat4 mat	= getMatrix(lightID);

	result 		= mulMat(mat, vec4(start, 1.0));

	topLeft		= mulMat(mat, vec4(topLeft, 1.0)).xyz;
	topRight	= mulMat(mat, vec4(topRight, 1.0)).xyz;
	botLeft		= mulMat(mat, vec4(botLeft, 1.0)).xyz;

	float a		= distance(topLeft, topRight);
	float b		= distance(topLeft, botLeft);

	area		= a * b;

	return result;
}



/**
  * Shifts a point along in a certain direction. The distance
  * of shifting is given by EPSILON.
  */
vec3 epsilonEnvironment(vec3 point, vec3 direction)
{
	return point + (EPSILON_RAY * normalize(direction) );
} //epsilonEnvironment



