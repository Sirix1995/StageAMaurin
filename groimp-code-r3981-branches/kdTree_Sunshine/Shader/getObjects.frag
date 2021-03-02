
vec4 treeLookUp(int pos)
{
	int s = 100;
	int offset = pos / s;
	pos 	= int( mod( float(pos), float(s)) );
	
	return texture2DRect(kd_tree, vec2(pos, offset) );
}


vec4 lookUp(int pos)
{
	vec4 result;
	
	if(!tree)
	{
		int offset = pos / size;
		pos 	= int( mod( float(pos), float(size)) );
		result 	= texture2DRect(scene, vec2(pos, offset) );
	} else
	{
		result = treeLookUp(pos);
	}
	
	return result;
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
	return lookUp(pos+6);
} //getColor


Light getLight(int pos)
{
	mat4 m = getMatrix(lightPart + pos);
	vec4 p0 = lookUp(lightPart + pos + 7);
	vec4 p1 = lookUp(lightPart + pos + 8);
	
	return Light( m[3], getObjectColor(lightPart + pos), p1.x, p1.y, p1.z, int(p0.w), int(p1.w), p0.y, p0.z );
} //getLight


//transform the ray into the coord system of the object
Ray getTransformRay(int pos, Ray ray)
{
	mat4 m4x4 = getInverseMatrix(pos);
	mat3 m3x3;
	m3x3[0] = m4x4[0].xyz;
	m3x3[1] = m4x4[1].xyz;
	m3x3[2] = m4x4[2].xyz;
			
	return Ray( m4x4 * ray.origin, m3x3 * ray.direction, -1, ray.spectrum );
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
	
	if (orthonormal)
	{
		matOut[2][0] = vecIn.x * s;
		matOut[2][1] = vecIn.y * s;
		matOut[2][2] = vecIn.z * s;
	}
	else
	{
		matOut[2][0] = vecIn.x;
		matOut[2][1] = vecIn.y;
		matOut[2][2] = vecIn.z;
	}
	
	if( abs(vecIn.x) > abs(vecIn.y) )
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
	
	if (orthonormal)
	{
		matOut[1][0] = matOut[2][1] * matOut[0][2] - matOut[2][2] * matOut[0][1];
		matOut[1][1] = matOut[2][2] * matOut[0][0] - matOut[2][0] * matOut[0][2];
		matOut[1][2] = matOut[2][0] * matOut[0][1] - matOut[2][1] * matOut[0][0];
	} else
	{
		matOut[1][0] = s * (matOut[2][1] * matOut[0][2] - matOut[2][2] * matOut[0][1]);
		matOut[1][1] = s * (matOut[2][2] * matOut[0][0] - matOut[2][0] * matOut[0][2]);
		matOut[1][2] = s * (matOut[2][0] * matOut[0][1] - matOut[2][1] * matOut[0][0]);
	}
}
