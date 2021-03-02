/**
 * This method creates a local coordinate system which is needed by
 * some BSDF stuff. The idea is adapted by pbrt: With the normal
 * vector (nn) of a surface, two other vectors (sn, tn) are produced.
 * Each of these vectors are perpenticular to there other vectors.
 */
ShaderCoor createShadeCoorSys(vec3 nn)
{
	float zz	= sqrt(1.0-nn.z*nn.z);

	vec3 sn		= vec3(0.0);
	vec3 tn		= vec3(0.0);

	if (abs(zz)<1e-6)
		sn		= vec3(1.0, 0.0, 0.0);
	else
		sn		= vec3(nn.y/zz, -nn.x/zz, 0.0);

	tn			= cross(nn, sn);

	return ShaderCoor(nn, sn, tn);
}

/**
 * Transforms a certain vector from global to local coordinate
 * system which is defined by there three base vector nn, sn, tn.
 */
vec3 globalToLocalVec(ShaderCoor sc, vec3 vec)
{
	return vec3(dot(vec, sc.sn), dot(vec, sc.tn), dot(vec, sc.nn));
}

/**
 * Transforms a certain vector from local to global coordinates.
 */
vec3 localToGlobalVec(ShaderCoor sc, vec3 vec)
{
	return vec3(	sc.sn.x * vec.x + sc.tn.x * vec.y + sc.nn.x * vec.z,
					sc.sn.y * vec.x + sc.tn.y * vec.y + sc.nn.y * vec.z,
					sc.sn.z * vec.x + sc.tn.z * vec.y + sc.nn.z * vec.z);
}

/**
  * Transform the local normal vector to global coordinates
  */
vec3 getNormal(TraceData td)
{
	mat3 m3x3;
	mat4 m4x4 	= getMatrix(td.id);
	m4x4 		= transpose(m4x4);

	m3x3[0] 	= m4x4[0].xyz;
	m3x3[1]	 	= m4x4[1].xyz;
	m3x3[2] 	= m4x4[2].xyz;

	return normalize(mulMat(m3x3, vec3(0.0,0.0,1.0)));
}
