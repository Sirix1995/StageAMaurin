

vec4 randomOrigin(int pos)
{
	//left edge of parallelogram in <0,0>
	vec4 start = vec4(-1.0, 0.0, 0.0, 1.0); 
	

	#if 0

	// New Version: Jitter

	float areas 		= sqrt(superSample);
	float areas_size	= 1.0 / areas;
	float tail			= 0.0;

	if(superSample != 1.0)
		tail			= mod(areas, 2.0);

	int vPos			= int((currentSample + tail)/ areas);
	int hPos			= int(currentSample - vPos * areas);

	start.x				+= 2.0 * areas_size * (hPos + rand());
	start.z				+= areas_size * (vPos + rand());

	#else

	start.x += 2.0*rand();
	start.z += rand();
	
	#endif
	
	return getMatrix(lightPart + pos) * start;
} //randomOrigin


Direction getRayForSpotLight(float innerAngle, float outerAngle, int pos)
{
	Direction direction = Direction(vec3(1.0), 1.0);
	
	float outer = cos(outerAngle);
	float inner = cos(innerAngle);
	
	float j 	= rand();
	float cost 	= 0.0;
	float d		= 1.0 - 0.5 * (inner + outer);
	
	if(2.0 * j > inner - outer)
	{
		cost = j + 0.5 * (inner + outer);
		direction.density = 1.0 / (2.0*PI * d);
	}
	else
	{
		j *= 2.0 / (inner - outer);

		// solution of 2 t^3 - t^4 = x
		float t = pow(j > 0.3 ? j : 0.5 * j, 1.0 / 3.0);
		while(true)
		{
			float t2 = t * t;
			float delta = j - t2 * (2.0 * t - t2);
			if((-EPSILON < delta) && (delta < EPSILON))
			{
				cost = (inner - outer) * t + outer;
				direction.density = (3.0 - 2.0 * t) * t2 / (2.0 * PI * d);
				break;
			}
			t += delta / (6.0 * t2 - 4.0 * t * t2); 
		}
	}
	float u = rand();
	float v = rand(); 
		
	float theta = 2.0*PI*u;
	float phi 	= acos(2.0*v - 1.0);
	float sint 	= sqrt(1.0 - cost * cost);
	
	mat4 m4x4 = getMatrix(pos);
	mat3 m3x3;
	m3x3[0] = m4x4[0].xyz;
	m3x3[1] = m4x4[1].xyz;
	m3x3[2] = m4x4[2].xyz;
	
	direction.dir = m3x3 * vec3( cos(theta)*sint, sin(theta)*sint, cost );
	
	return direction;
}

Direction getRayForAreaLight(int pos, float exponent)
{
	Direction direction = Direction(vec3(1.0), 1.0);
	
	mat4 m4x4 = getMatrix(pos);
	mat3 m3x3;
	m3x3[0] = m4x4[0].xyz;
	m3x3[1] = m4x4[1].xyz;
	m3x3[2] = m4x4[2].xyz;
	
	//length in global coordinate system
	vec3 length = m3x3 * vec3(0.0, 0.0, 1.0);
	//axis in global coordinate system
	vec3 axis = m3x3 * vec3(1.0, 0.0, 0.0);
	
	mat3 basis = getOrthonormalBasis(cross(length, axis));
	
	float j = rand();
	
	direction.density = (exponent + 2.0)
	* pow(j, (exponent + 1.0) / (exponent + 2.0))
	* (1.0 / (2.0 * PI));
	
	float cost = pow(j, 1.0/(exponent+2.0));
	
	float u = rand();
	float v = rand(); 
		
	float theta = 2.0*PI*u;
	float phi 	= acos(2.0*v - 1.0);
	float sint 	= sqrt(1.0 - cost * cost);
	
	direction.dir = basis * vec3(cos(theta)*sint, sin(theta)*sint, cost);
	
	return direction;
}

Direction generateRandomRays(Light light, int pos)
{
	Direction direction = Direction(vec3(1.0), 1.0);
	
	if(light.type == PONT_LIGHT)
	{
		float u = rand();
		float v = rand();
		
		float theta = 2.0*PI*u;
		float phi = acos(2.0*v - 1.0);
		float sint = sin(phi);
		
		direction.dir 		= vec3(cos(theta)*sint, sin(theta)*sint, cos(phi));
		direction.density 	= _PI_ / 2.0; 
	}
	
	if(light.type == SPOT_LIGHT)
	{
		direction = getRayForSpotLight(light.innerAngle, light.outerAngle, pos);
	}
	
	if(light.type == AREA_LIGHT)
	{
		direction = getRayForAreaLight(pos, light.exponent);
	}
	
	direction.dir = normalize(direction.dir);
	return direction;
}


float getOriginDensity(Light light, int pos)
{
	float density = DELTA_FACTOR;
	
	if(light.type == AREA_LIGHT)
	{
		mat4 m4x4 = getMatrix(pos);
		mat3 m3x3;
		m3x3[0] = m4x4[0].xyz;
		m3x3[1] = m4x4[1].xyz;
		m3x3[2] = m4x4[2].xyz;
		
		//length in global coordinate system
		vec3 v = m3x3 * vec3(0.0, 0.0, 1.0);
		//axis in global coordinate system
		vec3 w = m3x3 * vec3(1.0, 0.0, 0.0);
		
		vec3 q = cross(v, w);
		float area = 2.0 * length(q);
		
		density = 1.0 / area;
	}
	
	return density;
}
