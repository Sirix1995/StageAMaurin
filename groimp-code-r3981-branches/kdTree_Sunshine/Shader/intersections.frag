

bool RayDiscIntersection(float radius, Ray ray, vec4 iPoint, vec4 origin)
{
	return dot(iPoint-origin, iPoint-origin) <= radius*radius;
}


TraceData RayParallelogramIntersection(Ray ray)
{
	vec4 iPoint = vec4(0.0);
	float t = -1.0;
		
	vec3 normal = vec3(0.0, 1.0, 0.0); //normal vector of the parallelogram
		
	t = -( dot(ray.origin.xyz, normal) ) / ( dot(ray.direction, normal) );

	iPoint = ray.origin + t*vec4(ray.direction, 0.0);
		
	if( ray.origin.y < iPoint.y ) normal *= -1.0;

	if( iPoint.x > 1.0 || iPoint.x < - 1.0 || iPoint.z < 0.0 || iPoint.z > 1.0)
		t = -1.0;
		
	return TraceData(t >= 0.0, iPoint, normal, 0, 0, ray.origin.y < iPoint.y);
}


TraceData RayPlaneIntersection(Ray ray)
{
	vec4 iPoint = vec4(0.0);
	float t = -1.0;
	
	vec3 normal = vec3(0.0, 0.0, 1.0); //normal vector of the plane
	
	t = -( dot(ray.origin.xyz, normal) ) / ( dot(ray.direction, normal) );

	iPoint = ray.origin + t*vec4(ray.direction, 0.0);
	
	if( ray.origin.z < iPoint.z ) normal *= -1.0;
	
	return TraceData(t >= 0.0, iPoint, normal, 0, 0, ray.origin.z < iPoint.z);
}


TraceData RayCFCIntersection(Ray ray, CylFruCo z)
{
	bool invertN = false;
	float zmax = z.max;
	
	vec4 iPoint = vec4(0.0);
	float a, b, c, d, t, t1, t2, t3, t4, z1, z2, zmin;
	
	
	if(z.typ == 0.0)
	{
		a = ray.direction.x*ray.direction.x + ray.direction.y*ray.direction.y;
		b = 2.0*(ray.origin.x*ray.direction.x + ray.origin.y*ray.direction.y);
		c = pow(ray.origin.x, 2.0) + pow(ray.origin.y, 2.0) - (1.0);
		zmin = 0.0;
	} else 
	{
		a = ray.direction.x*ray.direction.x + ray.direction.y*ray.direction.y - ray.direction.z*ray.direction.z;
		b = 2.0*( ray.origin.x*ray.direction.x + ray.origin.y*ray.direction.y - ray.origin.z*ray.direction.z );
		c = ray.origin.x*ray.origin.x + ray.origin.y*ray.origin.y - ray.origin.z*ray.origin.z;
		
		zmin = z.max <= 0.0 ? -1.0 : 1.0;
	}
	
	
	//Diskriminante d
	d = b*b - 4.0*a*c;
	t = -1.0;
	
	if(d >= 0.0)
	{
		d = sqrt(d);
		t1 = ( -b + d ) / (2.0*a);
		t2 = ( -b - d ) / (2.0*a);
	
		z1 = ray.origin.z + t1*ray.direction.z;
		z2 = ray.origin.z + t2*ray.direction.z;
		
		
		if(zmin < z1 && z1 < zmax && t1 > 0.0 )
			t = t1;
		
		if(zmin < z2 && z2 < zmax && t2 > 0.0 )
			t = t != -1.0 ? min(t, t2) : t2;
			
		if(t == t1) invertN = true;
	
		
		
		//closed top
		if(z.isTopOpen != 1)
		{
			t3 = (zmax - ray.origin.z) / ray.direction.z;
			if(ray.origin.z > zmax)
			{
				if(z1 <= zmax && z2 >= zmax && t3 > 0.0)
				{
					t = t != -1.0 ? min(t, t3) : t3;
					invertN = false; 
				}
			} else 
			{
				if(z1 >= zmax && z2 <= zmax && t3 > 0.0)
				{
					t = t != -1.0 ? min(t, t3) : t3;
					if(t == t3) invertN = true;
				}
			}
		}		
		
		//closed bottom
		if(z.isBaseOpen != 1)
		{
			t4 = (zmin - ray.origin.z) / ray.direction.z;
			if(ray.origin.z > zmin)
			{
				if(z1 <= zmin && z2 >= zmin && t4 > 0.0)
				{
					t = t != -1.0 ? min(t, t4) : t4;
					if(t == t4) invertN = true;
				}
			} else
			{
				if(z1 >= zmin && z2 <= zmin && t4 > 0.0)
				{
					t = t != -1.0 ? min(t, t4) : t4;
					invertN = false;
				}
			}	
		}
		
		iPoint = ray.origin + t*vec4(ray.direction, 0.0);
	} //if

	return TraceData(t >= 0.0, iPoint, vec3(1.0,0.0,0.0), 0, 0, invertN);
} //RayCylinderIntersection


//improved Smits method
TraceData RayBoxIntersection(Ray r)
{	
	r.direction = normalize(r.direction);
	vec4 iPoint = vec4(0.0);
	vec3 normal, topNormal, frontNormal, leftNormal;
	
	float tmin, tmax, tymin, tymax, tzmin, tzmax, divx, divy, divz;
	float t0 = -100.0;
	float t1 = 100.0;	
	bool result = false;
	
	vec3 min = vec3(-0.5, -0.5, 0.0);
	vec3 max = vec3(0.5, 0.5, 1.0);
	
	divx = 1.0 / r.direction.x;
	divy = 1.0 / r.direction.y;
	divz = 1.0 / r.direction.z;
	
	if(divx >= 0.0) 
	{
		tmin = (min.x - r.origin.x) * divx;
		tmax = (max.x - r.origin.x) * divx;
		
		frontNormal = vec3(-1.0, 0.0, 0.0);
	//	frontNormal = vec3(0.5, 0.0, 0.0);
	}
	else 
	{
		tmin = (max.x - r.origin.x) * divx;
		tmax = (min.x - r.origin.x) * divx;
		
		frontNormal = vec3(1.0, 0.0, 0.0);
	}
	
	if (divy >= 0.0) 
	{ 
		tymin = (min.y - r.origin.y) * divy;
		tymax = (max.y - r.origin.y) * divy;
		leftNormal = vec3(0.0, -1.0, 0.0);
	//	normal = vec3(0.0, 0.5, 0.0);
	}
	else 
	{	
		tymin = (max.y - r.origin.y) * divy;
		tymax = (min.y - r.origin.y) * divy;
		leftNormal = vec3(0.0, 1.0, 0.0);
	}

	if ( (tmin > tymax) || (tymin > tmax) )
	{
		result = false;
	}
	else
	{	
		if (tymin > tmin) // left
		{
			tmin = tymin;
			normal = leftNormal;
		}
		else //front
			normal = frontNormal;
			
			
		if (tymax < tmax)
		{
			tmax = tymax;
		} 
		
		if (divz >= 0.0) 
		{
			tzmin = (min.z - r.origin.z) * divz;
			tzmax = (max.z - r.origin.z) * divz;
			topNormal = vec3(0.0, 0.0, -1.0);
		//	normal = vec3(0.0, 0.0, 0.5);
		}
		else 
		{
			tzmin = (max.z - r.origin.z) * divz;
			tzmax = (min.z - r.origin.z) * divz;
			topNormal = vec3(0.0, 0.0, 1.0);
		}
		
		if ( (tmin > tzmax) || (tzmin > tmax) )
		{
			result = false;
		} 
		else
		{
			if (tzmin > tmin)
			{
				tmin = tzmin;
				normal = topNormal;
			}
				
		
			if (tzmax < tmax)
			{
				tmax = tzmax;
			}
				
		
			result = ( (tmin < t1 && tmin > 0.0) && (tmax > t0) );					
		}	
	}
	
	if(result)
	{
		iPoint = r.origin + vec4(tmin*r.direction, 0.0);
	}
	
	return TraceData(result, iPoint, normal, 0, 0, false);
}


bool RayCellIntersection(Ray r, int cell, out float tmax, out vec3 iPoint)
{
	tree = true;
	r = getTransformRay(cell, r);
	tree = false;
	
	r.direction = normalize(r.direction);
	vec3 normal, topNormal, frontNormal, leftNormal;
	
	float tmin, tymin, tymax, tzmin, tzmax, divx, divy, divz;
	float t0 = -100.0;
	float t1 = 100.0;	
	bool result = false;
	
	vec3 min = vec3(-0.5, -0.5, 0.0);
	vec3 max = vec3(0.5, 0.5, 1.0);
	
	divx = 1.0 / r.direction.x;
	divy = 1.0 / r.direction.y;
	divz = 1.0 / r.direction.z;
	
	if(divx >= 0.0) 
	{
		tmin = (min.x - r.origin.x) * divx;
		tmax = (max.x - r.origin.x) * divx;
		
		frontNormal = vec3(-1.0, 0.0, 0.0);
	//	frontNormal = vec3(0.5, 0.0, 0.0);
	}
	else 
	{
		tmin = (max.x - r.origin.x) * divx;
		tmax = (min.x - r.origin.x) * divx;
		
		frontNormal = vec3(1.0, 0.0, 0.0);
	}
	
	if (divy >= 0.0) 
	{ 
		tymin = (min.y - r.origin.y) * divy;
		tymax = (max.y - r.origin.y) * divy;
		leftNormal = vec3(0.0, -1.0, 0.0);
	//	normal = vec3(0.0, 0.5, 0.0);
	}
	else 
	{	
		tymin = (max.y - r.origin.y) * divy;
		tymax = (min.y - r.origin.y) * divy;
		leftNormal = vec3(0.0, 1.0, 0.0);
	}

	if ( (tmin > tymax) || (tymin > tmax) )
	{
		result = false;
	}
	else
	{	
		if (tymin > tmin) // left
		{
			tmin = tymin;
			normal = leftNormal;
		}
		else //front
			normal = frontNormal;
			
			
		if (tymax < tmax)
		{
			tmax = tymax;
		} 
		
		if (divz >= 0.0) 
		{
			tzmin = (min.z - r.origin.z) * divz;
			tzmax = (max.z - r.origin.z) * divz;
			topNormal = vec3(0.0, 0.0, -1.0);
		//	normal = vec3(0.0, 0.0, 0.5);
		}
		else 
		{
			tzmin = (max.z - r.origin.z) * divz;
			tzmax = (min.z - r.origin.z) * divz;
			topNormal = vec3(0.0, 0.0, 1.0);
		}
		
		if ( (tmin > tzmax) || (tzmin > tmax) )
		{
			result = false;
		} 
		else
		{
			if (tzmin > tmin)
			{
				tmin = tzmin;
				normal = topNormal;
			}
				
		
			if (tzmax < tmax)
			{
				tmax = tzmax;
			}
				
		
			result = ( (tmin < t1 && tmin > 0.0) && (tmax > t0) );					
		}	
	}
	
	if(result)
	{
		iPoint = r.origin.xyz + tmin*r.direction;
	}
	
	return result;
}


TraceData RaySphereIntersection(Ray r)
{	
	r.direction = normalize(r.direction);
	
	float t1;
	float t2;
	float len = -1.0;
	vec4 iPoint = vec4(0.0);
	
	Sphere sp = Sphere(vec4(0.0), 1.0);
			
	float p = 2.0*( r.direction.x*(r.origin.x - sp.origin.x) + r.direction.y*(r.origin.y - sp.origin.y) + r.direction.z*(r.origin.z - sp.origin.z) );
	float q = pow(r.origin.x - sp.origin.x, 2.0) + pow(r.origin.y - sp.origin.y, 2.0) + pow(r.origin.z - sp.origin.z, 2.0) - 1.0;
		
	float disc = p*p - 4.0*q;

	if(disc >= 0.0)
	{
		disc = sqrt(disc);
		
		t1 = (- p - disc ) / 2.0;
		if(t1 >= 0.0)			
			len = t1;
		else
			len = (- p + disc ) / 2.0;
		
		
		iPoint = r.origin + vec4(len*r.direction, 0.0);
	} //if
	
	
	return TraceData(len >= 0.0, iPoint, iPoint.xyz, 0, 0, false);
}

