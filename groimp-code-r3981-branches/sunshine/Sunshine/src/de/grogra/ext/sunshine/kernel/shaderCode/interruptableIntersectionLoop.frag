
float calculateDeep(TraceData td, Ray ray)
{
	vec4 iPoint = convertLocal2Global(td.id, td.type, td.iPoint.xyz);
	
	return distance(iPoint.xyz, ray.origin.xyz);
} //calculateDeep


TraceData calcCover(TraceData td, TraceData ref, Ray ray, int id, int type)
{	
	td.id 	= id;
	td.type = type;
	
	
	float newDeep 	= calculateDeep(td, ray);
	bool refHasInt 	= ref.hasIntersection;
		
	if(!refHasInt || (refHasInt && newDeep < refDepth))
	{
		ref 	= td;
		refDepth = newDeep;		
	}
	
	return ref;
} //check


void setLoopParam(int maximum, inout int start, inout int stop)
{
	int tmpStart	= loopStart - stop; 
	int tmpStop		= loopStop - stop;

//	tmpStart		= tmpStart < 0 ? 0 : tmpStart;
	tmpStart		= int( max(float(tmpStart), 0.0) );

	start			= tmpStart	< maximum ? tmpStart : maximum;
	stop			= tmpStop	< maximum ? tmpStop  : maximum;
//	stop			= int( min(tmpStop, maximum) );
}


TraceData intersection(Ray ray, TraceData lastStepData)
{
	TraceData td 		= nilTD;
	TraceData result	= lastStepData;
	ray.direction		= normalize(ray.direction);
	
	int start	= 0;
	int stop	= 0;
	int pos 	= 0;
	int offset 	= 0;
	
	if(sphereCount > 0)
	{
		setLoopParam(sphereCount, start, stop);

		for(int i = start; i < stop; i++)
		{
			pos = offset + i*sizes[SPHERE];
			td = RaySphereIntersection(ray, pos);
			if(td.hasIntersection)
			{	
				result = calcCover(td, result, ray, pos, SPHERE);				
			} //if
		} //for	
		
		offset = sphereCount*sizes[SPHERE];	
	}
	/*---------------------------------------------------------------------*/	
	if(boxCount > 0)
	{
		setLoopParam(boxCount, start, stop);
		
		for(int i = start; i < stop; i++)
		{
			pos = offset + i*sizes[BOX];
			td = RayBoxIntersection(ray, pos);
			if(td.hasIntersection)
			{	
				result = calcCover(td, result, ray, pos, BOX);
			} //if
		} //for
		
		offset += boxCount*sizes[BOX];
	}
	/*---------------------------------------------------------------------*/
	if(cylinderCount > 0)
	{
		setLoopParam(cylinderCount, start, stop);
		
		for(int i = start; i < stop; i++)
		{
			pos = offset + i*sizes[CFC];
			td = RayCFCIntersection(ray, pos);
			if(td.hasIntersection)
			{	
				result = calcCover(td, result, ray, pos, CFC);
			} //if
		} //for
	
		offset += cylinderCount*sizes[CFC];
	}
	/*---------------------------------------------------------------------*/
	if(meshCount > 0)
	{
		setLoopParam(triangleCount, start, stop);
		
		//TODO: Multiple meshes
		//int meshID += int(floor( float(start) / float(triCount[meshID]) ));
		//int triTempCount = start;
		
		for(int i = start; i < stop; i++)
		{
			pos = offset + i*sizes[TRI] + sizes[MESH];
			td = RayTriIntersection(pos, ray, offset);
					
			if(td.hasIntersection)
			{
				result = calcCover(td, result, ray, pos, TRI);
			} //if
			
						
			//meshID += int(floor( float(i) / float(triCount[meshID]) ));
			
		} //RayTriIntersection			
	
		offset += triangleCount*sizes[TRI] + meshCount*sizes[MESH];
	}
	/*---------------------------------------------------------------------*/
	if(paraCount > 0)
	{
		setLoopParam(paraCount, start, stop);
		
		for(int i = start; i < stop; i++)
		{
			pos = offset + i*sizes[PARA];
			td = RayParallelogramIntersection(ray, pos);
			if(td.hasIntersection)
			{
				vec4 c = getObjectColor(pos);
				if(c.x == -1.0)//phong
				{
					TraceData tmp = td;
					tmp.id = pos;
					tmp.iPoint = getMatrix(tmp.id) * td.iPoint;
					tmp.type = PARA;
					Phong p = getPhong(c.y, tmp);
					
					if(p.dcm.a == 0.0) continue;
				}
				
				result = calcCover(td, result, ray, pos, PARA);
			} //if
		} //for
		
		offset += paraCount*sizes[PARA];
	} //if
	/*---------------------------------------------------------------------*/	
	if(planeCount > 0)
	{
		setLoopParam(planeCount, start, stop);
		
		for(int i = start; i < stop; i++)
		{
			pos = offset + i*sizes[PLANE];
			td = RayPlaneIntersection(ray, pos);
			if(td.hasIntersection)
			{
				result = calcCover(td, result, ray, pos, PLANE);
			} //if
		} //RayPlaneIntersection
	} //if
	
	return result;
} //intersection


void intersection(Ray ray, int id, inout TraceData result)
{	
	TraceData td = nilTD;
	
	int TYPE = SPHERE;
	
	int	offset = sphereCount*sizes[SPHERE];	

	if(sphereCount > 0 && id < offset)
	{
		td = RaySphereIntersection(ray, id);
	}
	/*---------------------------------------------------------------------*/
	else if(boxCount > 0 && id < (offset += boxCount*sizes[BOX])) 
	{		
		td = RayBoxIntersection(ray, id);
		TYPE = BOX;
	}
	/*---------------------------------------------------------------------*/
	else if(cylinderCount > 0 && id < (offset += cylinderCount*sizes[CFC]))
	{
		td = RayCFCIntersection(ray, id);
		TYPE = CFC;
	}
	/*---------------------------------------------------------------------*/
	else if(meshCount > 0 && id < (offset += triangleCount*sizes[TRI] + meshCount*sizes[MESH]))
	{	
//		id *= sizes[TRI];
		id = sizes[MESH] + id*sizes[TRI];
		td = RayTriIntersection(id, ray, meshPart);
		TYPE = TRI;
	}
	/*---------------------------------------------------------------------*/
	else if(paraCount > 0 && id < (offset += paraCount*sizes[PARA]))
	{
		td = RayParallelogramIntersection(ray, id);
		TYPE = PARA;
	}
	
	
	
	if(td.hasIntersection)
	{	
//		if(TYPE == PARA)
//		{
//			vec4 c = getObjectColor(id);
//			if(c[0] < 0.0)//phong
//			{
//				TraceData tmp = td;
//				tmp.id = id;
//				tmp.iPoint = getMatrix(tmp.id) * td.iPoint;
//				tmp.type = PARA;
//				Phong p = getPhong(c.y, tmp);
//				
//				if(p.dcm.a < EPSILON) td.hasIntersection = false;
//			}
//		}
		
		
		result = calcCover(td, result, ray, id, TYPE);
	} //if
}

