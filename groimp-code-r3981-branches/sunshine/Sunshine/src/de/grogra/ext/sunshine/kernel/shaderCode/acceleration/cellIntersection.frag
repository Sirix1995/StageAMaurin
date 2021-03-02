
//--------------------------cellIntersection.frag-----------------------------\\

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
				
		
			hit =  tmin < t1 && tmax > t0;			
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

//-------------------------end cellIntersection.frag--------------------------\\
