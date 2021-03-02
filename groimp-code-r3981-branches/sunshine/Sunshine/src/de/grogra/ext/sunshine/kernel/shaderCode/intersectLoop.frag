
float calculateDeep(TraceData td, Ray ray)
{
	mat4 m = getMatrix(td.id);
	vec4 iPoint = m * td.iPoint;
	
	return distance(iPoint, ray.origin);
} //calculateDeep


TraceData calcCover(TraceData td, TraceData ref, Ray ray, int id, int type)
{	
	td.id = id;
	td.type = type;
	
	float newDeep = calculateDeep(td, ray);

	
	if(ref.hasIntersection)
	{				
		if(newDeep < refDepth)
		{
			ref = td;
			refDepth = newDeep;
		} //if
	}
	else
	{
		ref = td;
		refDepth = newDeep;
	}
	
	return ref;
} //check


TraceData intersection(Ray ray)
{
	int pos = 0;
	TraceData td 		= TraceData(false, vec4(0.0), vec3(0.0),-1,-1, 1.0);
	TraceData result 	= TraceData(false, vec4(0.0), vec3(0.0),-1,-1, 1.0);
	
	int start = 0;
	for(int i = 0; i < sphereCount; i++)
	{
		pos = start + i*sizes[SPHERE];
		td = RaySphereIntersection(ray, pos);
		if(td.hasIntersection)
		{	
			result = calcCover(td, result, ray, pos, SPHERE);
		} //if
	} //for

	start = sphereCount*sizes[SPHERE];
	
	for(int i = 0; i < boxCount; i++)
	{
		pos = start + i*sizes[BOX];
		td = RayBoxIntersection(ray, pos);
		if(td.hasIntersection)
		{	
			result = calcCover(td, result, ray, pos, BOX);
		} //if
	} //for
	

	start += boxCount*sizes[BOX];
	
	for(int i = 0; i < cylinderCount; i++)
	{
		td = RayCFCIntersection( getTransformRay(start + i*sizes[CFC], ray), getCFC( start + i*sizes[CFC]) );
		if( td.hasIntersection )
		{	
			result = calcCover(td, result, ray, start + i*sizes[CFC], CFC);
		} //if
	} //for
	
	start += cylinderCount*sizes[CFC];
	
	for(int i = 0; i < planeCount; i++)
	{
		pos = start + i*sizes[PLANE];
		td = RayPlaneIntersection( getTransformRay(pos, ray) );
		if( td.hasIntersection )
		{
			result = calcCover(td, result, ray, pos, PLANE );
		} //if
	} //RayPlaneIntersection
	
	start += planeCount*sizes[PLANE];
	
	for(int i = 0; i < paraCount; i++)
	{
		if( ray.type == i ) continue;
		td = RayParallelogramIntersection(ray, start + i*sizes[PARA]);
		if(td.hasIntersection)
		{
			vec4 c = getObjectColor( start + i*sizes[PARA]);
			if(c.x == -1.0)//phong
			{
				TraceData tmp = td;
				tmp.id = start + i*sizes[PARA];
				tmp.iPoint = getMatrix(tmp.id) * td.iPoint;
				tmp.type = PARA;
				Phong p = getPhong(c.y, tmp);
				
				if(p.dcm.a == 0.0) continue;
			}
			
			result = calcCover(td, result, ray, start + i*sizes[PARA], PARA );
		} //if
	} //RayParallelogramIntersection
	
	
	return result;
} //intersection


void intersection(Ray ray, int id, inout TraceData result)
{	
	TraceData td = TraceData(false, vec4(0.0), vec3(0.0),-1,-1, 1.0);

	
	td = RaySphereIntersection(ray, id);
	if(td.hasIntersection)
	{	
		result = calcCover(td, result, ray, id, SPHERE);
	} //if
}
