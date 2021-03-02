/**
  * This method computes a uniform randomly distribution of points over a squares surface. 
  * Afterwards the point is transformed into global coordinates. The matrix for transformation
  * is determined by the id for an objects matrix.
  */
vec4 randomOrigin(float pos)
{
	vec4 result;
	vec3 start 	= vec3(-1.0, EPSILON, 0.0); //left edge of parallelogram in <0,0>
	
	float x 	= LCG(random);
	float z 	= LCG(random);
	
	start.x 	+= 2.0*x;
	start.z 	+= z;
	
	result 		= mulMat(getMatrix(lightPart + pos * float(sizes[LIGHT])), vec4(start, 1.0));
	
	return result;
}

/**
  * Shifts a point along in a certain direction. The distance
  * of shifting is given by EPSILON.
  */
vec3 epsilonEnvironment(vec3 point, vec3 direction)
{	
	return point + (EPSILON * normalize(direction) );
} //epsilonEnvironment

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

