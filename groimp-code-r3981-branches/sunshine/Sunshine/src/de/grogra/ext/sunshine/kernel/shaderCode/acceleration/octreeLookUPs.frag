
// ---------------------------octreeLookUps.frag------------------------------\\

vec4 treeLookUp(float pos)
{
	int treeSize = 500;
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

//------------------------end octreeLookUps.frag------------------------------\\