
vec4 treeLookUp(int pos)
{
	int s = 100;
	int offset = pos / s;
	pos = int( mod( float(pos), float(s)) );
	
	return texture2DRect(kd_tree, vec2(pos, offset) );
}

vec4 lookUp(float pos)
{
	vec4 result;
	
	//if(!tree)
	//{
		
		int offset = int(pos / float(size));
		pos 	= pos - float(size * offset);	// This is better and more numerical stable.
		
		//pos 	= int( mod( float(pos), float(size)) );
		
		result 	= texture2DRect(scene, vec2(pos, offset) );
	

	//testVec = vec3(pos, offset, 3.0);	
		
	//} else
	//{
	//	result = treeLookUp(pos);
	//}
	
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
vec4 getObjectColor(float pos)
{
	return lookUp(pos + float(6)); 
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
	
	return Light( m[3], getObjectColor(lightPart + pos), p1.x, p1.y, p1.z, int(p0.w), int(p1.w), p0.y, p0.z );
} //getLight

Light getLight(int pos)
{
	return getLight(float(pos));
} //getLight


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

UV getUVMapping(TraceData td)
{
	UV result;
	bool sm, pm, cm = false;
	pm = true;

	//spherical mapping
	if(sm && (td.typ == BOX || td.typ == CFC) )
		td.iPoint.z -= 0.5;
	
	
	//if(td.typ == SPHERE) // is a sphere -> sphere mapping	
	if(sm)
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), ( acos( td.iPoint.z ) ) / (PI) );


		
	if( !sm && td.typ == SPHERE)
		td.iPoint.z = (td.iPoint.z + 1.0) / 2.0;
	
	if( pm && td.typ == SPHERE)
	{		
		float u = atan(td.iPoint.y, td.iPoint.x );
		float v = acos( td.iPoint.z );
		float z = td.iPoint.z;
		
		if( u > (0.25*PI) && u < (0.75*PI) &&  z <= 0.9 && z >= 0.1 )
		{
			td.iPoint.x = -0.5;
			
			result = UV( td.iPoint.y + 0.5, 1.0-td.iPoint.z );
		}
		
		if( u+PI > (0.75*PI) && u+PI < (1.25*PI) && z <= 0.9 && z >= 0.1 )
		{
			td.iPoint.y = 0.5;
			
			result = UV( td.iPoint.x + 0.5, 1.0-td.iPoint.z );
			
		}

		if( td.iPoint.z > 0.9 )
		{
			td.iPoint.z = 1.0;
			result = UV( td.iPoint.x + 0.5, abs(td.iPoint.y-0.5) );
			
		}
		
		if( td.iPoint.z < 0.1 )
		{
			td.iPoint.z = 0.0;
			result = UV( td.iPoint.x + 0.5, td.iPoint.y+0.5 );
		}
		
	}
		
	//if(td.typ == BOX) //is a box
	if(pm && td.typ != SPHERE)
	{
	
		if( td.iPoint.z >= 0.999 ) //top
			result = UV( td.iPoint.x + 0.5, abs(td.iPoint.y-0.5) );
		
		if( td.iPoint.z <= 0.001 ) //bottom
			result = UV( td.iPoint.x + 0.5, td.iPoint.y+0.5 );
			
		if( td.iPoint.y <= -0.499 )
			result = UV( td.iPoint.x + 0.5, 1.0-td.iPoint.z );
		
		if( td.iPoint.y >= 0.499 )
			result = UV( abs(td.iPoint.x - 0.5), 1.0-td.iPoint.z );
		
		if( td.iPoint.x >= 0.499 )
			result = UV( td.iPoint.y + 0.5, 1.0-td.iPoint.z );
		
		if( td.iPoint.x <= -0.499 )
			result = UV( abs(td.iPoint.y - 0.5), 1.0-td.iPoint.z );
			
	
	} //if
		
	
	if(td.typ == PARA)
		result = UV( (td.iPoint.x/2.0)+0.5 , 1.0-td.iPoint.z );
	
	//if(td.typ == CFC)
	if(cm)
	{
		CylFruCo cfc = getCFC(td.id);
		
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), td.iPoint.z);
		cfc.typ=0.0;
		if(cfc.typ == 0.0)
		if( td.iPoint.z > 0.999 || td.iPoint.z < 0.001)
			result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
		
		if(cfc.typ == 1.0)
		{
			result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z) );
		
			if( td.iPoint.z < -0.999)
				result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 ); 
		}
		
		if(cfc.typ == 2.0)
		{
			float zmin = cfc.max <= 0.0 ? -1.0 : 1.0;
			if(cfc.max <= 0.0) //zmin = -1
			{
				result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z)*2.0 -1.0 );
				
				if( td.iPoint.z > cfc.max-0.001)
					result = UV( (td.iPoint.x/ (2.0*cfc.max))+0.5, 1.0-(td.iPoint.y/ (2.0*cfc.max) )+0.5 );
				
				if( td.iPoint.z < -0.999)
					result = UV( (td.iPoint.x/2.0)+0.5, 1.0-(td.iPoint.y/2.0)+0.5 );
				
			} else //zmin = 1
			{
				result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), (abs(td.iPoint.z)-1.0) / (cfc.max-1.0) );
				if( td.iPoint.z > cfc.max-0.001)
					result = UV( (td.iPoint.x/ (2.0*cfc.max))+0.5, 1.0-(td.iPoint.y/ (2.0*cfc.max) )+0.5 );
				
				if( td.iPoint.z < 1.001	)
					result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
			}
		}
	}

	if(td.typ == PLANE)
		result = UV( mod(td.iPoint.x,1.0), 1.0-mod(td.iPoint.y,1.0) );
	
	return result;
}


UV getUV(TraceData td)
{
	UV result = UV(0.0, 0.0);
	
	if(td.typ == SPHERE) // is a sphere -> sphere mapping
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), ( acos( td.iPoint.z ) ) / (PI) );
	
	if(td.typ == BOX) //is a box
	{
		if( td.iPoint.z >= 0.999 ) //top
			result = UV( td.iPoint.x + 0.5, abs(td.iPoint.y-0.5) );
		
		if( td.iPoint.z <= 0.001 ) //bottom
			result = UV( td.iPoint.x + 0.5, td.iPoint.y+0.5 );
		
		if( td.iPoint.y <= -0.499 )
			result = UV( td.iPoint.x + 0.5, 1.0-td.iPoint.z );
		
		if( td.iPoint.y >= 0.499 )
			result = UV( abs(td.iPoint.x - 0.5), 1.0-td.iPoint.z );
		
		if( td.iPoint.x >= 0.499 )
			result = UV( td.iPoint.y + 0.5, 1.0-td.iPoint.z );
		
		if( td.iPoint.x <= -0.499 )
			result = UV( abs(td.iPoint.y - 0.5), 1.0-td.iPoint.z );
	} //if
	
	if(td.typ == PLANE)
		result = UV( mod(td.iPoint.x,1.0), 1.0-mod(td.iPoint.y,1.0) );
	
	if(td.typ == PARA)
		result = UV( (td.iPoint.x/2.0)+0.5 , 1.0-td.iPoint.z );
	
	if(td.typ == CFC)
	{
		CylFruCo cfc = getCFC(td.id);
		
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), td.iPoint.z);
		
		if(cfc.typ == 0.0)
		if( td.iPoint.z > 0.999 || td.iPoint.z < 0.001)
			result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
		
		if(cfc.typ == 1.0)
		{
			result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z) );
		
			if( td.iPoint.z < -0.999)
				result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 ); 
		}
		
		if(cfc.typ == 2.0)
		{
			float zmin = cfc.max <= 0.0 ? -1.0 : 1.0;
			if(cfc.max <= 0.0) //zmin = -1
			{
				result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z)*2.0 -1.0 );
				
				if( td.iPoint.z > cfc.max-0.001)
					result = UV( (td.iPoint.x/ (2.0*cfc.max))+0.5, 1.0-(td.iPoint.y/ (2.0*cfc.max) )+0.5 );
				
				if( td.iPoint.z < -0.999)
					result = UV( (td.iPoint.x/2.0)+0.5, 1.0-(td.iPoint.y/2.0)+0.5 );
				
			} else //zmin = 1
			{
				result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), (abs(td.iPoint.z)-1.0) / (cfc.max-1.0) );
				if( td.iPoint.z > cfc.max-0.001)
					result = UV( (td.iPoint.x/ (2.0*cfc.max))+0.5, 1.0-(td.iPoint.y/ (2.0*cfc.max) )+0.5 );
				
				if( td.iPoint.z < 1.001	)
					result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
			}
		}
	}
		
	return result;
}


float convertShininess(float x)
{
	float result;
	
	x = x * (2.0 - x);
	if (x <= 0.0)
	{
		result = 0.0;
	}
	else if( x >= 1.0)
	{
		result = MAX_SHININESS;
	}
	else
	{
		result = min(-2.0 / log(x), MAX_SHININESS);
	}
	
	return result;
}


vec3 calcRGB(vec3 color, Light light, vec3 normal, float angle)
{
	return color * light.color.xyz * angle;
}


vec3 calcPhong(Phong p, Light light, vec3 normal, TraceData td, int counter)
{
	vec4 cpri;
	
	vec3 vp = normalize(light.origin.xyz - td.iPoint.xyz);
	vec3 vc = normalize(primRay.origin.xyz - td.iPoint.xyz);
	
	// distance to light source
	float d = length(vp);
	
	float fade_distance = light.distance;
	float fade_power = light.exponent;
	
//	vec4 acs = vec4(0.2, 0.2, 0.2, 1.0); //ambient color of scene
//	vec4 acl = vec4(0.0);				// ambient color of light
//	float att = 1.0 / ( 1.0 + 0.0*length(vp) + 0.0*length(vp)*length(vp) );

	vec4 dcl = light.color; 			//diffuse color of light
	float att = light.origin.w != 0.0 ? 2.0 / (1.0 + pow(d / fade_distance, fade_power)) : 1.0;
	float spot = 1.0;
	
	float f = max(dot(normal, vp), 0.0) != 0.0 ? 1.0 : 0.0;
	
	vec3 h = (vp + vc) / length(vp + vc); //Blinn-Phong-Model: Halfway vector
	vec4 scl = vec4(1.0); //specular intensity of light
	
	if(counter == 0)
		cpri = p.ecm;
	
	cpri += att * spot * ( p.acm
		+ max( dot(normal, vp ), 0.0) * p.dcm * dcl
//		+ f * pow(max(dot(normal, normalize(h) ), 0.0), p.srm.x*128.0) * p.scm * scl );
		+ f * pow(max(dot(normal, normalize(h) ), 0.0), pow(2.0, (p.srm.x-4.0)+(5.0*p.srm.x) ) *128.0) * p.scm * scl );
	
	return cpri.xyz;
}


void getOrthogonalBasis(vec3 vecIn, inout mat3 matOut, bool orthonormal)
{
	float s = 1.0 / sqrt(vecIn.x * vecIn.x + vecIn.y * vecIn.y + vecIn.z* vecIn.z);
	float f;
	
	matOut[0]	= vec3(0.0);
	matOut[1]	= vec3(0.0);
	matOut[2]	= vecIn;	

	if (orthonormal)	
		matOut[2] *= s;
	
	// Due to some strange behavior on ATI's graphics card we have to ensure
	// that there is no comparison between two values close to zero.
	bool xIsZero = vecIn.x < EPSILON && vecIn.x > -EPSILON;
	bool yIsZero = vecIn.y < EPSILON && vecIn.y > -EPSILON;
	
	if( abs(vecIn.x) > abs(vecIn.y) && ( (!xIsZero && !yIsZero) || yIsZero ))
	{
		f = 1.0 / sqrt(vecIn.x * vecIn.x + vecIn.z * vecIn.z);
		matOut[0].x = vecIn.z * f;
		matOut[0].y = 0.0;
		matOut[0].z = -vecIn.x * f;
	}
	else
	{
		f = 1.0 / sqrt(vecIn.y * vecIn.y + vecIn.z * vecIn.z);
		matOut[0].x = 0.0;
		matOut[0].y = vecIn.z * f;
		matOut[0].z = -vecIn.y * f;
	}	
		
	//normalTest = vec4(matOut[0], 1.0);	
		
	matOut[1].x = matOut[2].y * matOut[0].z - matOut[2].z * matOut[0].y;
	matOut[1].y = matOut[2].z * matOut[0].x - matOut[2].x * matOut[0].z;
	matOut[1].z = matOut[2].x * matOut[0].y - matOut[2].y * matOut[0].x;
	
	if (!orthonormal)
		matOut[1] *= s;	
	
}
