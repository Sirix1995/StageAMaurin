
vec4 treeLookUp(int pos)
{
	int s = 100;
	int offset = pos / s;
	pos = int( mod( float(pos), float(s)) );

	return texture2DRect(kd_tree, vec2(pos, offset) );
}
bool unlock = false;
vec4 lookUp(float pos)
{
	vec4 result;

	//if(!tree)
	//{

		int offset 	= int(pos / float(size));
		pos			= pos - int(size * offset);	// This is better and more numerical stable.




		//pos 	= int( mod( float(pos), float(size)) );
		result 	= texture2DRect(scene, vec2(pos, offset) );

		if(unlock)
		{
			unlock = false;
			pos = 21.0;
			//normalTest = texture2DRect(scene, vec2(pos, offset) ); //vec4(pos, offset, size, 9999);
		}




	//testVec = vec3(pos, offset, 3.0);

	//} else
	//{
	//	result = treeLookUp(pos);
	//}

	return result;
} //lookUp

vec4 lookUp(int pos)
{
	int offset 	= int(pos / float(size));
	pos			= pos - int(size * offset);	// This is better and more numerical stable.




			//pos 	= int( mod( float(pos), float(size)) );
	return texture2DRect(scene, vec2(pos, offset) );
} //lookUp




Sphere getSphere(int pos)
{
	return Sphere( vec4(0.0), 1.0);
} //getSphere


Box getBox(int pos)
{
	vec4 parameter[2];

	vec4 min = vec4(-0.5, -0.5, 0.0, 1.0);
	vec4 max = vec4(0.5, 0.5, 1.0, 1.0);

	parameter[0] = min;
	parameter[1] = max;

	return Box( parameter,
			distance(min.x, max.x) - 0.001,
			distance(min.y, max.y) - 0.001,
			distance(min.z, max.z) - 0.001 );

} //getBox


CylFruCo getCFC(int pos)
{
	vec4 values = lookUp(pos+7);
	return CylFruCo(int(values.x), int(values.y), values.z, values.w );
}

mat4 getMatrix(float pos)
{
	mat4 m;

	vec4 tmp0 = lookUp(pos + float(0));
	vec4 tmp1 = lookUp(pos + float(1));
	vec4 tmp2 = lookUp(pos + float(2));

	m[0] = vec4(tmp0.xyz, 0.0);
	m[1] = vec4(tmp0.w, tmp1.xy, 0.0);
	m[2] = vec4(tmp1.zw, tmp2.x, 0.0);
	m[3] = vec4(tmp2.yzw, 1.0);

	return m;
} //getMatrix

mat4 getMatrix(int pos)
{
	return getMatrix(float(pos));
}

mat4 getInverseMatrix(float pos)
{
	return getMatrix(pos + float(3));
} //getInverseMatrix

mat4 getInverseMatrix(int pos)
{
	return getInverseMatrix(float(pos));
} //getInverseMatrix

//the color is always on the 6th position
vec4 getObjectColor(float pos, inout float ior)
{
	vec4 color = lookUp(pos+6);

	float f = floor(color.w);
	if(f > 1.0)
		color.a = (color.w - f) * 10.0;

	ior			= 10.0 / f;

	return color;
} //getColor

//the color is always on the 6th position
vec4 getObjectColor(float pos)
{
	float ior = 0.0;

	return getObjectColor(pos, ior);
} //getColor

vec4 getObjectColor(int pos)
{
	return getObjectColor(float(pos));
} //getColor

Light getLight(float pos)
{
	mat4 m 	= getMatrix(lightPart + pos);
	vec4 p0	= lookUp(lightPart + pos + float(7));
	vec4 p1	= lookUp(lightPart + pos + float(8));

	return Light( m[3], getObjectColor(lightPart + pos), p1.x, p0.x, p1.y, p1.z, int(p0.w), int(p1.w), p0.y, p0.z );
} //getLight

Light getLight(int pos)
{
	mat4 m 	= getMatrix(lightPart + pos);
	vec4 p0	= lookUp(lightPart + pos + 7);
	vec4 p1	= lookUp(lightPart + pos + 8);

	return Light( m[3], getObjectColor(lightPart + pos), p1.x, p0.x, p1.y, p1.z, int(p0.w), int(p1.w), p0.y, p0.z );
} //getLight
/*
Light getLight(int pos)
{
	return getLight(float(pos));
} //getLight
*/

//transform the ray into the coord system of the object
Ray getTransformRay(int pos, Ray ray)
{
	mat4 m4x4 = getInverseMatrix(pos);
	mat3 m3x3;
	m3x3[0] = m4x4[0].xyz;
	m3x3[1] = m4x4[1].xyz;
	m3x3[2] = m4x4[2].xyz;

	return Ray( mulMat(m4x4, ray.origin), mulMat(m3x3, ray.direction), -1, ray.spectrum );
} //getTransformRay

mat3 getOrthonormalBasis(vec3 vecIn)
{
	mat3 matOut;
	float f;

	matOut[2] = normalize(vecIn);

	//bool xIsZero = vecIn.x < EPSILON && vecIn.x > -EPSILON;
	//bool yIsZero = vecIn.y < EPSILON && vecIn.y > -EPSILON;

	bool biggerX = abs(vecIn.x) - abs(vecIn.y) > EPSILON;

	//if( abs(vecIn.x) > abs(vecIn.y) )
	//if( abs(vecIn.x) > abs(vecIn.y) && ( (!xIsZero && !yIsZero) || yIsZero ))
	//if( ( (!xIsZero && !yIsZero) || yIsZero ))
	if(biggerX)
	{
		f = 1.0 / sqrt(vecIn.x * vecIn.x + vecIn.z * vecIn.z);
		matOut[0][0] = vecIn.z * f;
		matOut[0][1] = 0.0;
		matOut[0][2] = -vecIn.x * f;
	}
	else
	{
		f = 1.0 / sqrt(vecIn.y * vecIn.y + vecIn.z * vecIn.z);
		matOut[0][0] = 0.0;
		matOut[0][1] = vecIn.z * f;
		matOut[0][2] = -vecIn.y * f;
	}


	matOut[1][0] = matOut[2][1] * matOut[0][2] - matOut[2][2] * matOut[0][1];
	matOut[1][1] = matOut[2][2] * matOut[0][0] - matOut[2][0] * matOut[0][2];
	matOut[1][2] = matOut[2][0] * matOut[0][1] - matOut[2][1] * matOut[0][0];

	return matOut;
} //getOrthonormalBasis
