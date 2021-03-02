
vec4 treeLookUp(float pos)
{
	int treeSize = 500;
//	int offset = pos / treeSize;
//	pos = int( mod( float(pos), float(treeSize)) );
	
	int offset = int(pos / float(treeSize));
	pos = pos - float(treeSize * offset);	// This is better and more numerical stable.
	
	return texture2DRect(treeTexture, vec2(pos, offset));
}

vec4 treeLookUp(int pos)
{
	return treeLookUp(float(pos));
}

vec4 lookUp(float pos)
{
	vec4 result;

	if(!tree)
	{
//		int offset 	= pos / size;
//		pos 		= int( mod( float(pos), float(size)) );
		
		int offset = int(pos / float(size));
		pos 	= pos - float(size * offset);	// This is better and more numerical stable.
		result 	= texture2DRect(sceneTexture, vec2(pos, offset) );
	} else
	{
		result = treeLookUp(pos);
	}
	
	return result;
} //lookUp

vec4 lookUp(int pos)
{
	return lookUp(float(pos));
} //lookUp

Sphere getSphere(int pos)
{	
	return Sphere( vec4(0.0), 1.0);
} //getSphere

Box getBox()
{
	vec4 parameter[2];
	
	vec4 min = vec4(-0.5, -0.5, 0.0, 1.0);
	vec4 max = vec4(0.5, 0.5, 1.0, 1.0);
	
	parameter[0] = min;
	parameter[1] = max;
	
	return Box( parameter,
			distance(min.x, max.x) - EPSILON,
			distance(min.y, max.y) - EPSILON,
			distance(min.z, max.z) - EPSILON );
	
} //getBox

CylFruCo getCFC(int pos)
{
	vec4 values = lookUp(pos+7);
	return CylFruCo(int(values.x), int(values.y), values.z, values.w );
}

mat4 getMatrix(int pos)
{	
	mat4 m;
	
	vec4 tmp0 = lookUp(pos+0);
	vec4 tmp1 = lookUp(pos+1);
	vec4 tmp2 = lookUp(pos+2);
	

	m[0] = vec4(tmp0.xyz, 0.0);
	m[1] = vec4(tmp0.w, tmp1.xy, 0.0);
	m[2] = vec4(tmp1.zw, tmp2.x, 0.0);
	m[3] = vec4(tmp2.yzw, 1.0);
	
	return m;
} //getMatrix


mat4 getInverseMatrix(int pos)
{
	return getMatrix(pos+3);
} //getInverseMatrix


//the color is always on the 6th position
vec4 getObjectColor(int pos)
{
	vec4 color = lookUp(pos+6);
	
	float f = floor(color.w);
	if(f > 1.0)
		color.a = (color.w - f) * 10.0;
	
	return color;
} //getColor

float getIOR(int pos)
{
	vec4 color = lookUp(pos+6);
	
	return 10.0 / floor(color.w);
} //getIOR


Light getLight(int pos)
{
	mat4 m = getMatrix(lightPart + pos);
	vec4 p0 = lookUp(lightPart + pos + 7);
	vec4 p1 = lookUp(lightPart + pos + 8);
	
	return Light( m[3], getObjectColor(lightPart + pos), p1.x, p0.x, p1.y, p1.z, int(p0.w), int(p1.w), p0.y, p0.z );
} //getLight
