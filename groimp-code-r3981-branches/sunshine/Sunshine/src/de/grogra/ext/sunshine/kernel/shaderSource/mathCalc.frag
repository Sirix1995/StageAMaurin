float distanceSQR(vec4 v1, vec4 v2)
{
	v1 -= v2;
	v1 *= v1;
	
	return (v1.x + v1.y + v1.z + v1.w);
}

float distanceSQR(vec3 v1, vec3 v2)
{
	v1 -= v2;
	v1 *= v1;
	
	return (v1.x + v1.y + v1.z);
}


vec4 mulMat(mat4 m, vec4 v)
{
	m = transpose(m);
	
	vec4 result;
	
	result.x = m[0].x * v.x + m[0].y * v.y + m[0].z * v.z + m[0].w * v.w;
	result.y = m[1].x * v.x + m[1].y * v.y + m[1].z * v.z + m[1].w * v.w;
	result.z = m[2].x * v.x + m[2].y * v.y + m[2].z * v.z + m[2].w * v.w;
	result.w = m[3].x * v.x + m[3].y * v.y + m[3].z * v.z + m[3].w * v.w;
	
	return result;
}

vec3 mulMat(mat3 m, vec3 v)
{
	m = transpose(m);
	
	vec3 result;
	
	result.x = m[0].x * v.x + m[0].y * v.y + m[0].z * v.z;
	result.y = m[1].x * v.x + m[1].y * v.y + m[1].z * v.z;
	result.z = m[2].x * v.x + m[2].y * v.y + m[2].z * v.z;
	
	return result;
}

