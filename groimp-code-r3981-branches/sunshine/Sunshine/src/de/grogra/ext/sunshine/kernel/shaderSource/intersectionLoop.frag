
float calculateDistance(TraceData td, Ray ray, inout vec4 tmpIPoint)
{
	// Local to global transformation
	mat4 m 		= getMatrix(td.type != TRI ? float(td.id) : meshPart);
	td.iPoint.w	= 1.0;	// Thanks to Thomas: The last component in this vec4 should allways be 1.0

	vec4 iPoint	= mulMat(m, td.iPoint);

	iPoint.w	= 1.0;
	ray.origin.w= 1.0;

	tmpIPoint	= iPoint;

	return distanceSQR(iPoint, ray.origin);
} //calculateDistance


TraceData calcCover(TraceData td, TraceData ref, Ray ray, int id, int type)
{
	td.id 					= id;
	td.type					= type;

	vec4 tmpIPoint 			= vec4(0.0);

	float newDistance 		= calculateDistance(td, ray, tmpIPoint);
	bool refHasInt 			= ref.hasIntersection;

	if(!refHasInt || (refHasInt && newDistance < shortestDistance))
	{
		ref 				= td;
		shortestDistance 	= newDistance;
		ref.iPoint 			= tmpIPoint;
	}

	return ref;
} //check

void setLoopParam(int max, inout int start, inout int stop)
{
	int tempStart	= loopStart - stop;
	int tempStop	= loopStop - stop;

	tempStart		= tempStart < 0 ? 0 : tempStart;

	start			= tempStart	< max ? tempStart 	: max;
	stop			= tempStop	< max ? tempStop 	: max;
}

bool intersection(Ray ray, inout TraceData result)
{
	TraceData td 		= TraceData(false, vec4(0.0), vec3(0.0),-1,-1, false);
	//TraceData result	= TraceData(false, vec4(0.0), vec3(0.0),-1,-1, false);

	int start			= 0;
	int stop			= 0;

	int offset 			= 0;

	if (sphereCount > 0)
	{
		setLoopParam(sphereCount, start, stop);

		for(int i = start; i < stop; i++)
		{
			td = RaySphereIntersection( getTransformRay((i*sizes[SPHERE]), ray) );
			if( td.hasIntersection )
			{
				result = calcCover(td, result, ray, i*sizes[SPHERE], SPHERE);
			} //if
		} //for

		offset = sphereCount*sizes[SPHERE];

	}


	if(boxCount > 0)
	{
		setLoopParam(boxCount, start, stop);

		for(int i = start; i < stop; i++)
		{
			td = RayBoxIntersection( getTransformRay( offset + i*sizes[BOX], ray) );
			if( td.hasIntersection )
			{
				result = calcCover(td, result, ray, offset + i*sizes[BOX], BOX);

			} //if
		} //for

		offset += boxCount*sizes[BOX];
	}

	if(cylinderCount > 0)
	{
		setLoopParam(cylinderCount, start, stop);

		for(int i = start; i < stop; i++)
		{
			td = RayCFCIntersection( getTransformRay(offset + i*sizes[CFC], ray), getCFC( offset + i*sizes[CFC]) );
			if( td.hasIntersection )
			{
				result = calcCover(td, result, ray, offset + i*sizes[CFC], CFC);

			} //if
		} //for

		offset += cylinderCount*sizes[CFC];
	}

	if(meshCount > 0)
	{
		setLoopParam(allTris, start, stop);

		//TODO: Multiple meshes
		//int meshID += int(floor( float(start) / float(triCount[meshID]) ));
		//int triTempCount = start;

		//Ray transRay = getTransformRay(offset /*+ meshID*sizes[MESH]*/, ray);

		for(int i = start; i < stop; i++)
		{
			td = RayTriIntersection( offset + i*sizes[TRI] + sizes[MESH], getTransformRay(offset /*+ meshID*sizes[MESH]*/, ray));

			if( td.hasIntersection )
			{
				result = calcCover(td, result, ray, offset + i*sizes[TRI] + sizes[MESH], TRI );


			} //if


			//meshID += int(floor( float(i) / float(triCount[meshID]) ));

		} //RayTriIntersection

		offset += allTris*sizes[TRI] + meshCount*sizes[MESH];
	}

	if(paraCount > 0)
	{
		setLoopParam(paraCount, start, stop);

		for(int i = start; i < stop; i++)
		{
			//if( ray.type== i ) continue;
			td = RayParallelogramIntersection( getTransformRay(offset + i*sizes[PARA], ray) );
			if( td.hasIntersection  )
			{
				/*vec4 c = getObjectColor( offset + i*sizes[PARA]);
				if( c.x == -1.0 )//phong
				{
					TraceData tmp = td;
					tmp.id = start + i*sizes[PARA];
					tmp.iPoint = getMatrix(tmp.id) * td.iPoint;
					tmp.type= PARA;
				//	Phong p = getPhong(c.y, tmp);

				//	if(p.dcm.w == 0.0) continue;
				}
					*/

				result = calcCover(td, result, ray, offset + i*sizes[PARA], PARA );

			} //if
		} //RayParallelogramIntersection

		offset += paraCount*sizes[PARA];
	}

	if(planeCount > 0)
	{
		setLoopParam(planeCount, start, stop);

		for(int i = start; i < stop; i++)
		{
			td = RayPlaneIntersection( getTransformRay(offset + i*sizes[PLANE], ray) );
			if( td.hasIntersection )
			{
				result = calcCover(td, result, ray, offset + i*sizes[PLANE], PLANE );

			} //if
		} //RayPlaneIntersection
	}


	return false;

} //intersection

