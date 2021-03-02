
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


vec4 epsilonEnvironment(vec4 point, vec3 direction)
{	
	return point + (EPSILON * vec4( normalize(direction), 0.0) );
} //epsilonEnvironment


UV getUVMapping(TraceData td)
{
	UV result;
	bool sm, pm, cm = false;
	sm = true;

	//spherical mapping
	if(sm && (td.type == BOX || td.type == CFC) )
		td.iPoint.z -= 0.5;
	
	
	//if(td.type == SPHERE) // is a sphere -> sphere mapping	
	if(sm)
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), ( acos( td.iPoint.z ) ) / (PI) );


		
	if( !sm && td.type == SPHERE)
		td.iPoint.z = (td.iPoint.z + 1.0) / 2.0;
	
	if( pm && td.type == SPHERE)
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
		
	//if(td.type == BOX) //is a box
	if(pm && td.type != SPHERE)
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
		
	
	if(td.type == PARA)
		result = UV( (td.iPoint.x/2.0)+0.5 , 1.0-td.iPoint.z );
	
	//if(td.type == CFC)
	if(cm)
	{
		CylFruCo cfc = getCFC(td.id);
		
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), td.iPoint.z);
		cfc.type=0.0;
		if(cfc.type == 0.0)
		if( td.iPoint.z > 0.999 || td.iPoint.z < 0.001)
			result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
		
		if(cfc.type == 1.0)
		{
			result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z) );
		
			if( td.iPoint.z < -0.999)
				result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 ); 
		}
		
		if(cfc.type == 2.0)
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

	if(td.type == PLANE)
		result = UV( mod(td.iPoint.x,1.0), 1.0-mod(td.iPoint.y,1.0) );
	
	return result;
}


// expects the iPoint in global space 
UV getUV(TraceData td)
{
	UV result = UV(0.0, 0.0);
	td.iPoint = getInverseMatrix(td.type == TRI ? meshPart : td.id) * td.iPoint;
	
	if(td.type == SPHERE)
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), ( acos( td.iPoint.z ) ) / (PI) );
	
	if(td.type == BOX)
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
	
	if(td.type == PLANE)
		result = UV( mod(td.iPoint.x,1.0), 1.0-mod(td.iPoint.y,1.0) );
	
	if(td.type == PARA)
		result = UV( (td.iPoint.x/2.0)+0.5 , 1.0-td.iPoint.z );
	
	if(td.type == CFC)
	{
		CylFruCo cfc = getCFC(td.id);
		
		result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), td.iPoint.z);
		
		if(cfc.type == 0.0)
		if( td.iPoint.z > 0.999 || td.iPoint.z < 0.001)
			result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 );
		
		if(cfc.type == 1.0)
		{
			result = UV( ( PI + atan(-td.iPoint.y, -td.iPoint.x ) ) / (2.0*PI), abs(td.iPoint.z) );
		
			if( td.iPoint.z < -0.999)
				result = UV( (td.iPoint.x/2.0)+0.5, (td.iPoint.y/2.0)+0.5 ); 
		}
		
		if(cfc.type == 2.0)
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

mat3 getOrthonormalBasis(vec3 vecIn)
{
	mat3 matOut;
	float f;
	
	matOut[2] = normalize(vecIn);
	
	// Due to some strange behavior on ATI's graphics card we have to ensure
	// that there is no comparison between two values close to zero.
	bool xIsZero = vecIn.x < EPSILON && vecIn.x > -EPSILON;
	bool yIsZero = vecIn.y < EPSILON && vecIn.y > -EPSILON;
	
//	if( abs(vecIn.x) > abs(vecIn.y) )
	if( abs(vecIn.x) > abs(vecIn.y) && ( (!xIsZero && !yIsZero) || yIsZero ))
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


bool hasIntersection(float id)
{
	return id != NO_INTERSECTION;
}

float calculateAngle(vec3 a, vec3 b)
{	
	return dot( normalize(a), normalize(b) );
} //calculateAngle


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


TraceData getTraceData(vec4 data, sampler2DRect texture)
{
	int id 		= int(abs(data.w));
	int type	= int(texture2DRect(texture, gl_TexCoord[0].xy).w);
	
	vec4 localiPoint	= convertGlobal2Local(id, type, data.xyz);
	TraceData td 		= TraceData(true, localiPoint, vec3(0.0), id, type, sign(data.w + EPSILON));
	
	td.normal = calculateNormal(td);
	td.iPoint = vec4(data.xyz, 1.0);
	
	return td;
} //getTraceData

vec2 getSphericalCoordinates(vec3 dir)
{
	float theta = atan(dir.y, dir.x);
	float phi	= acos(dir.z);
	
	return vec2(theta, phi);
}

