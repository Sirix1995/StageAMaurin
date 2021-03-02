
void prepareRay(inout Ray ray, int pos)
{
	ray = getTransformRay(pos, ray);
	ray.direction = normalize(ray.direction);
}


bool RayDiscIntersection(float radius, Ray ray, vec4 iPoint, vec4 origin)
{
	return dot(iPoint-origin, iPoint-origin) <= radius*radius;
}


TraceData RayParallelogramIntersection(Ray ray, int pos)
{
	prepareRay(ray, pos);
	
	float invertN = 1.0;
	vec4 iPoint = vec4(0.0);
	float t = -1.0;
		
	vec3 normal = vec3(0.0, 1.0, 0.0); //normal vector of the parallelogram
		
	t = -( dot(ray.origin.xyz, normal) ) / ( dot(ray.direction, normal) );

	iPoint = ray.origin + t*vec4(ray.direction, 0.0);
		
	if( ray.origin.y < iPoint.y ) invertN = -1.0;

	if( iPoint.x > 1.0 || iPoint.x < - 1.0 || iPoint.z < 0.0 || iPoint.z > 1.0)
		t = -1.0;
		
	return TraceData(t >= 0.0, iPoint, normal*invertN, 0, 0, invertN);
}


TraceData RayPlaneIntersection(Ray ray, int pos)
{
	prepareRay(ray, pos);
	
	float invertN = 1.0;
	vec4 iPoint = vec4(0.0);
	float t = -1.0;
	
	vec3 normal = vec3(0.0, 0.0, 1.0); //normal vector of the plane
	
	t = -( dot(ray.origin.xyz, normal) ) / ( dot(ray.direction, normal) );

	iPoint = ray.origin + t*vec4(ray.direction, 0.0);
	
	if( ray.origin.z < iPoint.z ) invertN = -1.0;
	
	return TraceData(t >= 0.0, iPoint, normal*invertN, 0, 0, invertN);
}


TraceData RayCFCIntersection(Ray ray, int pos)
{
	prepareRay(ray, pos);
	CylFruCo z = getCFC(pos);
	
	float invertN = 1.0;
	float zmax = z.max;
	
	vec4 iPoint = vec4(0.0);
	float a, b, c, d, t, t1, t2, t3, t4, z1, z2, zmin;
	
	
	if(z.type == 0.0)
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
			
		if(t == t1) invertN = -1.0;
	
		
		
		//closed top
		if(z.isTopOpen != 1)
		{
			t3 = (zmax - ray.origin.z) / ray.direction.z;
			if(ray.origin.z > zmax)
			{
				if(z1 <= zmax && z2 >= zmax && t3 > 0.0)
				{
					t = t != -1.0 ? min(t, t3) : t3;
					invertN = 1.0; 
				}
			} else 
			{
				if(z1 >= zmax && z2 <= zmax && t3 > 0.0)
				{
					t = t != -1.0 ? min(t, t3) : t3;
					if(t == t3) invertN = -1.0;
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
					if(t == t4) invertN = -1.0;
				}
			} else
			{
				if(z1 >= zmin && z2 <= zmin && t4 > 0.0)
				{
					t = t != -1.0 ? min(t, t4) : t4;
					invertN = 1.0;
				}
			}	
		}
		
		iPoint = ray.origin + t*vec4(ray.direction, 0.0);
	} //if

	return TraceData(t >= 0.0, iPoint, vec3(1.0,0.0,0.0), 0, 0, invertN);
} //RayCylinderIntersection


//improved Smits method
TraceData RayBoxIntersection(Ray r, int pos)
{
	prepareRay(r, pos);

	vec4 iPoint = vec4(0.0);
	vec3 normal = vec3(0.0); 
	
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
		
	}
	else 
	{
		tmin = (max.x - r.origin.x) * divx;
		tmax = (min.x - r.origin.x) * divx;
		
	}
	
	if (divy >= 0.0) 
	{ 
		tymin = (min.y - r.origin.y) * divy;
		tymax = (max.y - r.origin.y) * divy;
	}
	else 
	{	
		tymin = (max.y - r.origin.y) * divy;
		tymax = (min.y - r.origin.y) * divy;
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
		}
			
			
		if (tymax < tmax)
		{
			tmax = tymax;
		} 
		
		if (divz >= 0.0) 
		{
			tzmin = (min.z - r.origin.z) * divz;
			tzmax = (max.z - r.origin.z) * divz;
		}
		else 
		{
			tzmin = (max.z - r.origin.z) * divz;
			tzmax = (min.z - r.origin.z) * divz;
		}
		
		if ( (tmin > tzmax) || (tzmin > tmax) )
		{
			result = false;
		} 
		else
		{
			if(tzmin > tmin)
			{
				tmin = tzmin;
			}
				
		
			if(tzmax < tmax)
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
	
	return TraceData(result, iPoint, normal, 0, BOX, 1.0);
}

Cell RayCellIntersection(Ray ray, int index)
{
	tree = true;
	Ray transRay = getTransformRay(index, ray);
	tree = false;
	
	transRay.direction = normalize(transRay.direction);
	
	float tmin, tmax, tymin, tymax, tzmin, tzmax, divx, divy, divz;
	float t0 = -100.0;
	float t1 = 100.0;	
	bool hit = false;
	
	vec3 min = vec3(-0.5, -0.5, 0.0);
	vec3 max = vec3(0.5, 0.5, 1.0);
	
	divx = 1.0 / transRay.direction.x;
	divy = 1.0 / transRay.direction.y;
	divz = 1.0 / transRay.direction.z;
	
	if(divx >= 0.0)
	{
		tmin = (min.x - transRay.origin.x) * divx;
		tmax = (max.x - transRay.origin.x) * divx;
	}
	else 
	{
		tmin = (max.x - transRay.origin.x) * divx;
		tmax = (min.x - transRay.origin.x) * divx;
	}
	
	if (divy >= 0.0) 
	{ 
		tymin = (min.y - transRay.origin.y) * divy;
		tymax = (max.y - transRay.origin.y) * divy;
	}
	else 
	{	
		tymin = (max.y - transRay.origin.y) * divy;
		tymax = (min.y - transRay.origin.y) * divy;
	}

	if(tmin > tymax || tymin > tmax)
	{
		hit = false;
	}
	else
	{	
		if(tymin > tmin) // left
		{
			tmin = tymin;
		}
			
			
		if(tymax < tmax)
		{
			tmax = tymax;
		} 
		
		if(divz >= 0.0) 
		{
			tzmin = (min.z - transRay.origin.z) * divz;
			tzmax = (max.z - transRay.origin.z) * divz;
		}
		else 
		{
			tzmin = (max.z - transRay.origin.z) * divz;
			tzmax = (min.z - transRay.origin.z) * divz;
		}
		
		if(tmin > tzmax || tzmin > tmax)
		{
			hit = false;
		} 
		else
		{
			if(tzmin > tmin)
			{
				tmin = tzmin;
			}
				
		
			if(tzmax < tmax)
			{
				tmax = tzmax;
			}
				
		
			hit = ( (tmin < t1 && tmin > 0.0) && (tmax > t0) );			
		}	
	}
	
	Cell cell = nilCell;
	if(hit)
	{
		vec3 P_entry 	= transRay.origin.xyz + tmin*transRay.direction;
		vec3 P_exit 	= transRay.origin.xyz + tmax*transRay.direction;
		
		tree = true;
		vec4 hitPoint 	= convertLocal2Global(index, CELL, P_entry);
		vec4 exitPoint 	= convertLocal2Global(index, CELL, P_exit);
		tree = false;

		cell.index 			= index;
		cell.p_entry		= P_entry;
		cell.p_exit			= P_exit;
		cell.delta_entry 	= distance(ray.origin.xyz, hitPoint.xyz);
		cell.delta_exit 	= distance(ray.origin.xyz, exitPoint.xyz);
		
	} //if
	
	return cell;
}


/**
  * Moeller and Trumbore: "Fast Minimum Storage RayTriangle Intersection"
  */
TraceData RayTriIntersection(int triID, Ray ray, int pos)
{
	prepareRay(ray, pos);
	
	bool result = false;
	vec4 iPoint = vec4(0.0);
	vec3 normal = vec3(0.0);
	
	vec4 temp0 	= lookUp(triID + 0);
	vec4 temp1 	= lookUp(triID + 1);
	vec4 temp2 	= lookUp(triID + 2);
	vec4 temp3 	= lookUp(triID + 3);
	vec4 temp4 	= lookUp(triID + 4);
	
	vec3 vert0	= temp0.xyz;
	vec3 vert1	= vec3(temp1.zw, temp2.x);
	vec3 vert2	= temp3.xyz;
	
	vec3 vertN0	= vec3(temp0.w, temp1.xy);
	vec3 vertN1	= temp2.yzw;
	vec3 vertN2	= vec3(temp3.w, temp4.xy);
	
	float t = 0.0;
	float u = 0.0;
	float v = 0.0;
	
	float det;
	float inv_det;
	
	vec3 edge1;
	vec3 edge2;
	vec3 tvec;
	vec3 pvec;
	vec3 qvec;
	
	edge1 = vert1 - vert0;
	edge2 = vert2 - vert0;

	pvec = cross(ray.direction, edge2);
	
	det = dot(edge1, pvec);

	if(det < -EPSILON || det > EPSILON)
	{
		inv_det = 1.0 / det;
		tvec = ray.origin.xyz - vert0;
		u = dot(tvec, pvec) * inv_det;
			
		if(u > 0.0 && u < 1.0)
		{
			qvec = cross(tvec, edge1);
			v = dot(ray.direction, qvec) * inv_det;
				
			if(v > 0.0 && (u + v) < 1.0)
			{
				t = dot(edge2, qvec) * inv_det;
					
				result = t > EPSILON;
			} //if
		} //if
	} //if
			
	float w = 1.0 - (u + v);		
	
	if(result)
	{
		iPoint = ray.origin + vec4(t * ray.direction, 0.0);
		normal = (w * vertN0) + (u * vertN1) + (v * vertN2);
	}
	
	return TraceData(result, iPoint, normal, 0, TRI, 1.0);
} //RayTriIntersection


TraceData RaySphereIntersection(Ray r, int pos)
{	
	prepareRay(r, pos);
	
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
	
	
	return TraceData(len >= 0.0, iPoint, iPoint.xyz, 0, SPHERE, 1.0);
}

