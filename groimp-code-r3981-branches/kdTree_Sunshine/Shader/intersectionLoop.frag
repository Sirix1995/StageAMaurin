
float calculateDeep(TraceData td, Ray ray)
{
	mat4 m = getMatrix(td.id);
	vec4 iPoint = m * td.iPoint;
	
	return distance(iPoint, ray.origin);
} //calculateDeep


TraceData calcCover(TraceData td, TraceData ref, Ray ray, int id, int typ)
{	
	td.id = id;
	td.typ = typ;
	
	float newDeep = calculateDeep(td, ray);

	
	if(ref.hasIntersection)
	{				
		if( newDeep < refDeep)
		{
			ref = td;
			refDeep = newDeep;
		} //if
	}
	else
	{
		ref = td;
		refDeep = newDeep;
	}
	
	return ref;
} //check


TraceData intersection(Ray ray)
{	
	TraceData td 		= TraceData(false, vec4(0.0), vec3(0.0),-1,-1, false);
	TraceData result 	= TraceData(false, vec4(0.0), vec3(0.0),-1,-1, false);
	
	int start = 0;
	for(int i = 0; i < sphereCount; i++)
	{
		td = RaySphereIntersection( getTransformRay((i*sizes[SPHERE]), ray) );
		if( td.hasIntersection )
		{	
			result = calcCover(td, result, ray, i*sizes[SPHERE], SPHERE);
		} //if
	} //for

	start = sphereCount*sizes[SPHERE];
	
	for(int i = 0; i < boxCount; i++)
	{
		td = RayBoxIntersection( getTransformRay( start + i*sizes[BOX], ray) );
		if( td.hasIntersection )
		{	
			result = calcCover(td, result, ray, start + i*sizes[BOX], BOX);
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
		td = RayPlaneIntersection( getTransformRay(start + i*sizes[PLANE], ray) );
		if( td.hasIntersection )
		{
			result = calcCover(td, result, ray, start + i*sizes[PLANE], PLANE );
		} //if
	} //RayPlaneIntersection
	
	start += planeCount*sizes[PLANE];
	
	for(int i = 0; i < paraCount; i++)
	{
		if( ray.typ == i ) continue;
		td = RayParallelogramIntersection( getTransformRay(start + i*sizes[PARA], ray) );
		if( td.hasIntersection )
		{
			vec4 c = getObjectColor( start + i*sizes[PARA]);
			if( c.x == -1.0 )//phong
			{
				TraceData tmp = td;
				tmp.id = start + i*sizes[PARA];
				tmp.iPoint = getMatrix(tmp.id) * td.iPoint;
				tmp.typ = PARA;
				Phong p = getPhong(c.y, tmp);
				
				if(p.dcm.w == 0.0) continue;
			}
			
			result = calcCover(td, result, ray, start + i*sizes[PARA], PARA );
		} //if
	} //RayParallelogramIntersection
	
	
	return result;
} //intersection


void intersection(Ray ray, int id, inout TraceData result)
{	
	TraceData td = TraceData(false, vec4(0.0), vec3(0.0),-1,-1, false);

	
	td = RaySphereIntersection( getTransformRay(id, ray) );
	if( td.hasIntersection )
	{	
		result = calcCover(td, result, ray, id, SPHERE);
	} //if
	

}
