
//transform the ray into the coord system of the object
Ray getTransformRay(int pos, Ray ray)
{
	mat4 m4x4 = getInverseMatrix(pos);
	mat3 m3x3;
	m3x3[0] = m4x4[0].xyz;
	m3x3[1] = m4x4[1].xyz;
	m3x3[2] = m4x4[2].xyz;
			
	return Ray(m4x4 * ray.origin, m3x3 * ray.direction, -1, ray.spectrum, ray.density);
} //getTransformRay


vec4 convertGlobal2Local(int id, int type, vec3 data)
{
	id = type == TRI ? meshPart : id;
	
	return getInverseMatrix(id) * vec4(data, 1.0);
}

vec4 convertLocal2Global(int id, int type, vec3 data)
{
	id = type == TRI ? meshPart : id;
	
	return getMatrix(id) * vec4(data, 1.0);
}
